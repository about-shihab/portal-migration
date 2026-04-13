package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.dto.*;
import com.iict.buet.customer_portal.enums.RoleName;
import com.iict.buet.customer_portal.enums.SmsEmailNotificationEnum;
import com.iict.buet.customer_portal.model.*;
import com.iict.buet.customer_portal.repository.*;
import com.iict.buet.customer_portal.service.ProfileService;
import com.iict.buet.customer_portal.service.UserService;
import com.iict.buet.customer_portal.service.UtilService;
import com.iict.buet.customer_portal.templates.EmailTemplate;
import com.iict.buet.customer_portal.templates.SmsTemplate;
import com.iict.buet.customer_portal.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.iict.buet.customer_portal.util.SystemConstants.*;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class.getName());

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final CustomerDomesticRepository customerDomesticRepository;
    private final CustomerRepository customerRepository;
    private final UtilService utilService;
    private final MailService mailService;
    private final SmsSender smsSender;
    private final SmsEmailNotificationRepository smsEmailNotificationRepository;
    private final ProfileService profileService;
    private final MobileNumberChangeLogRepository mobileNumberChangeLogRepository;
    private final AuthUtils authUtils;
    @PersistenceContext
    private EntityManager entityManager;

    private static final String REG_STEP_COOKIE = "registrationSteps";
    private static final String CUST_CODE_SESSION = "custCode";
    private static final String OTP_VERIFICATION_STEP = "2";
    private static final String PWD_SETUP_STEP = "3";


    private BaseCustomer checkInvalidCustomer(String customerCode, String mobileNo) {
        BaseCustomer customer = getRegisteredCustomer(customerCode, mobileNo);
        if (customer == null) {
            throw new IllegalArgumentException("Invalid Customer Code Or Mobile no.");
        }
        if (!customer.getIsRegistered()) {
            throw new IllegalArgumentException("You are an unregistered customer. Please complete online registration at KGDCL revenue department.");
        }
        int alreadyRegisteredCount = userRepository.countByCustomerCodeAndIsActiveTrue(customerCode);
        if (alreadyRegisteredCount > 0) {
            throw new IllegalArgumentException("You are already registered!");
        }
        return customer;
    }

    private BaseCustomer getRegisteredCustomer(String customerCode, String mobileNo) {
        BaseCustomer customer;
        if (customerCode.contains("NM-")) {
            customer = customerDomesticRepository.findByCodeAndMobileNoAndIsRegisteredTrue(customerCode, mobileNo);
        } else {
            customer = customerRepository.findByCodeAndMobileNoAndIsRegisteredTrue(customerCode, mobileNo);
        }
        return customer;
    }

    private BaseCustomer getRegisteredCustomerByCode(String customerCode) {
        BaseCustomer customer;
        if (customerCode.contains("NM-")) {
            customer = customerDomesticRepository.findByCodeAndIsRegisteredTrue(customerCode);
        } else {
            customer = customerRepository.findByCodeAndIsRegisteredTrue(customerCode);
        }
        return customer;
    }

    @Override
    public Response validateCustomers(UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        BaseCustomer customer;
        try {
            customer = checkInvalidCustomer(userDto.getCustomerCode(), userDto.getPhoneNo());
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        int inactiveCustomerCount = userRepository.countByCustomerCodeAndIsActiveFalse(userDto.getCustomerCode());
        User user = null;
        if (inactiveCustomerCount > 0) {
            User firstUser = userRepository.findFirstByCustomerCodeAndIsActiveFalseOrderByLastOtpSendDateDesc(userDto.getCustomerCode());
            if (firstUser != null && isOtpRequestLimitExceeded(firstUser)) {
                return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "You can't make more than " + DAILY_OTP_LIMIT + " requests per day.");
            }
            user = mapDtoAndSaveUser(userDto, firstUser.getId(), firstUser.getNoOfSendOtp() + 1L);
        } else {
            user = mapDtoAndSaveUser(userDto, null, 1L);
        }
        VerificationToken verificationToken = saveVerificationToken(user.getCustomerCode());
        if (verificationToken != null) {
            sendOtpAndSetCookies(request, response, customer, verificationToken, true);
            return ResponseBuilder.getSuccessResponse(HttpStatus.CREATED, null, "Please collect the OTP from your online registered mobile number.");
        } else {
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "You cannot make more than one request in " + OTP_EXPIRY_MINUTES + " minutes. Please try again after " + OTP_EXPIRY_MINUTES + " minutes.");
        }

    }

    private User mapDtoAndSaveUser(UserDto userDto, Long id, long noOfOtp) {
        User user = modelMapper.map(userDto, User.class);
        user.setId(id);
        user.setIsActive(false);
        user.setNoOfSendOtp(noOfOtp);
        setRole(user);
        user = userRepository.save(user);
        return user;
    }

    private void sendOtpAndSetCookies(HttpServletRequest request, HttpServletResponse response, BaseCustomer customer, VerificationToken verificationToken, boolean isSignup) {
        String customerName;
        String mobileNo;
        String email = customer.getEmail();

        if (customer instanceof CustomerDomestic) {
            customerName = ((CustomerDomestic) customer).getCustomerName();
            mobileNo = ((CustomerDomestic) customer).getMobileNo();
        } else {
            customerName = ((Customer) customer).getCustomerName();
            mobileNo = ((Customer) customer).getMobileNo();
        }
        if (isSignup) {
            sendSmsAsync(mobileNo, SmsTemplate.afterRegistrationOtpExecution(customerName, verificationToken.getToken()));
            sendEmailNotificationAsync(email, "KGDCL Customer Portal Registration", EmailTemplate.afterRegisterOtpExecution(customerName, verificationToken.getToken()));
        } else {
            sendSmsAsync(mobileNo, SmsTemplate.afterForgotPasswordOtpExecution(customerName, verificationToken.getToken()));
            sendEmailNotificationAsync(email, "KGDCL Customer Portal Registration", EmailTemplate.afterForgotPasswordOtpExecution(customerName, verificationToken.getToken()));
        }
        HttpSession session = request.getSession();
        session.setAttribute(CUST_CODE_SESSION, customer.getCode());
        CookieUtils.setCookie(response, REG_STEP_COOKIE, OTP_VERIFICATION_STEP, SystemConstants.STEP_COOKIE_EXPIRY_MINUTES, false, isHttps(request));
    }

    @Override
    public Response create(UserDto userDto) {
        CustomerDomestic customerDomestic = null;
        Customer customer = null;
        String email = null;
        String customerName = null;
        if (userDto.getCustomerCode().contains("NM-")) {
            customerDomestic = customerDomesticRepository.findByCodeAndMobileNoAndIsRegisteredTrue(userDto.getCustomerCode(), userDto.getPhoneNo());
            if (customerDomestic != null) {
                email = customerDomestic.getEmail();
                customerName = customerDomestic.getCustomerName();
            }
        } else {
            customer = customerRepository.findByCodeAndMobileNoAndIsRegisteredTrue(userDto.getCustomerCode(), userDto.getPhoneNo());
            if (customer != null) {
                email = customer.getEmail();
                customerName = customer.getCustomerName();
            }
        }
        if (customer == null && customerDomestic == null) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Invalid Customer Code Or Phone no.");
        }
        int alreadyRegisteredCount = userRepository.countByCustomerCodeAndIsActiveTrue(userDto.getCustomerCode());
        if (alreadyRegisteredCount > 0) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "You are already registered");
        }
        alreadyRegisteredCount = userRepository.countByCustomerCodeAndIsActiveFalse(userDto.getCustomerCode());
        Long id = null;
        Long noOfOtp = Long.valueOf("1");
        if (alreadyRegisteredCount > 0) {
            List<User> userList = userRepository.findAllByCustomerCodeAndIsActiveFalse(userDto.getCustomerCode());
            if (DateUtils.getStringDate(userList.get(0).getLastOtpSendDate(), "dd-MM-yyyy").equals(DateUtils.getStringDate(new Date(), "dd-MM-yyyy"))) {
                if (userList.get(0).getNoOfSendOtp() >= 3) {
                    return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "You can't make more than " + DAILY_OTP_LIMIT + " request per day.");
                }
            }
            id = userList.get(0).getId();
            noOfOtp = userList.get(0).getNoOfSendOtp() + Long.valueOf("1");
        }
        User user = modelMapper.map(userDto, User.class);
        user.setId(id);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setNoOfSendOtp(noOfOtp);
        setRole(user);
        user = userRepository.save(user);
        if (user != null) {
            VerificationToken verificationToken = saveVerificationToken(user.getCustomerCode());
            sendSmsAsync(userDto.getPhoneNo(), SmsTemplate.afterRegistrationOtpExecution(customerName, verificationToken.getToken()));
            sendEmailNotificationAsync(email, "KGDCL Customer Portal Registration", EmailTemplate.afterRegisterOtpExecution(customerName, verificationToken.getToken()));
            return ResponseBuilder.getSuccessResponse(HttpStatus.CREATED, null, "KGDCL Customer Portal activation SMS sent.");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs.");
    }

    @Override
    public Response activateUser(ActivateUserDto activateUserDto) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndCustomerCodeAndIsActiveTrue(activateUserDto.getToken(), activateUserDto.getCustomerCode());
        if (verificationToken != null) {
            List<User> userList = userRepository.findAllByCustomerCodeAndIsActiveFalse(verificationToken.getCustomerCode());
            if (userList != null && userList.size() == 1) {
                User user = userList.get(0);
                user.setIsActive(true);
                user = userRepository.save(user);
                if (user != null) {
                    return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, "KGDCL Customer Portal activation successful.");
                }
                return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs.");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Invalid OTP request");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "Invalid OTP or Expired");
    }

    @Override
    public Response verifyOtp(String otp, HttpServletRequest request, HttpServletResponse response) {
        String customerCode = getCustomerCodeFromSession(request);
        String registrationSteps = CookieUtils.getCookie(request, REG_STEP_COOKIE);

        if (customerCode == null || !OTP_VERIFICATION_STEP.equals(registrationSteps)) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Valid customer code required");
        }
        if (otp.isEmpty()) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Valid OTP required");
        }
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndCustomerCodeAndIsActiveTrue(otp, customerCode);
        if (verificationToken != null) {
            if (userRepository.existsByCustomerCode(verificationToken.getCustomerCode())) {
                verificationTokenRepository.delete(verificationToken);
                CookieUtils.setCookie(response, REG_STEP_COOKIE, PWD_SETUP_STEP, SystemConstants.STEP_COOKIE_EXPIRY_MINUTES, false, isHttps(request));
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, "OTP verification successful");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "OTP Invalid or Expired");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "OTP Invalid or Expired");
    }

    @Override
    public String activateUser(UserDto userDto, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {
        String customerCode = getCustomerCodeFromSession(request);
        String registrationStep = CookieUtils.getCookie(request, REG_STEP_COOKIE);

        if (shouldRedirectToLogin(bindingResult, customerCode, registrationStep)) {
            return "redirect:/login";
        }

        userDto.setCustomerCode(customerCode);
        List<User> userList = userRepository.findAllByCustomerCodeAndIsActiveFalse(customerCode);
        if (userList != null && !userList.isEmpty()) {
            User user = userList.get(0);
            if (updateAndAuthenticateUser(user, userDto, response, request)) {
                return "redirect:/profile";
            }
        }
        return "redirect:/login";
    }

    private boolean shouldRedirectToLogin(BindingResult bindingResult, String customerCode, String registrationStep) {
        return bindingResult.hasErrors() || customerCode == null || !PWD_SETUP_STEP.equals(registrationStep);
    }

    private boolean updateAndAuthenticateUser(User user, UserDto userDto, HttpServletResponse response, HttpServletRequest request) {
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIsActive(true);
        userRepository.save(user);

        deleteCookies(response, request);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDto.getCustomerCode(), userDto.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (AuthenticationException exception) {
            return false;
        }
    }

    private static void deleteCookies(HttpServletResponse response, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(CUST_CODE_SESSION);
        CookieUtils.deleteCookie(response, REG_STEP_COOKIE, false, isHttps(request));
    }

    private void setRole(User user) {
        Role role = roleRepository.findByName(RoleName.ROLE_PORTAL_USER.name());
        if (role == null) {
            role = new Role();
            role.setName(RoleName.ROLE_PORTAL_USER.name());
            role = roleRepository.save(role);
        }
        user.setRoles(Collections.singletonList(role));
    }

    public String getToken() {
        String token = StringUtils.getRandomNumber(OTP_SIZE);
        VerificationToken verificationOtp = verificationTokenRepository.findByToken(token);
        if (verificationOtp == null) {
            return token;
        }
        return getToken();
    }

    @Override
    public void deleteAllExpiredToken() {
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAllByExpiryDateBeforeAndIsActiveTrue(new Date());
        List<VerificationToken> deletedVerificationTokenList = new ArrayList<>();
        verificationTokenList.forEach(verificationToken -> {
            verificationToken.setIsActive(false);
            deletedVerificationTokenList.add(verificationToken);
        });
        verificationTokenRepository.saveAll(deletedVerificationTokenList);
    }

    @Override
    public void updateAllUserPassword() {
        List<User> userList = userRepository.findAllByUpdatedByIsNull();
        userList.forEach(user -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUpdatedAt(new Date());
            user.setUpdatedBy(Long.valueOf("0"));
            setRole(user);
            user = userRepository.save(user);
        });
    }

    private VerificationToken saveVerificationToken(String customerCode) {
        List<VerificationToken> verificationTokens = verificationTokenRepository.findAllByCustomerCode(customerCode);

        if (verificationTokens != null && !verificationTokens.isEmpty()) {
            verificationTokens.sort(Comparator.comparing(VerificationToken::getExpiryDate).reversed());
            VerificationToken mostRecentToken = verificationTokens.get(0);
            if (mostRecentToken.getExpiryDate().after(new Date())) {
                return null;
            }
            verificationTokenRepository.deleteAll(verificationTokens);
        }
        VerificationToken verificationToken = new VerificationToken();
        String token = getToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(DateUtils.calculateExpiryDate(OTP_EXPIRY_MINUTES));
        verificationToken.setCustomerCode(customerCode);
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public Response createForgotPasswordRequest(ForgotPasswordRequestDto forgotPasswordRequestDto, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findUserByCustomerCodeAndIsActiveTrue(forgotPasswordRequestDto.getUsername());
        if (user != null) {
            if (isOtpRequestLimitExceeded(user)) {
                return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "You can't make more than " + DAILY_OTP_LIMIT + " requests per day.");
            }
            VerificationToken verificationToken = saveVerificationToken(user.getCustomerCode());
            if (verificationToken != null) {
                BaseCustomer customer = getRegisteredCustomerByCode(user.getCustomerCode());

                if (customer == null) {
                    return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Invalid username");
                }
                user.setLastOtpSendDate(new Date());
                user.setNoOfSendOtp(user.getNoOfSendOtp() == null ? 1L : user.getNoOfSendOtp() + 1L);
                userRepository.save(user);
                sendOtpAndSetCookies(request, response, customer, verificationToken, false);
                return ResponseBuilder.getSuccessResponse(HttpStatus.CREATED, null, "Please collect the OTP from your online registered mobile number.");
            } else {
                return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "You cannot make more than one request in " + OTP_EXPIRY_MINUTES + " minutes. Please try again after " + OTP_EXPIRY_MINUTES + " minutes.");
            }
        }
        return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Requested user not found");
    }

    @Override
    public Response changeForgottenPassword(ForgotPasswordDto forgotPasswordDto, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {
        String customerCode = getCustomerCodeFromSession(request);
        String registrationStep = CookieUtils.getCookie(request, REG_STEP_COOKIE);

        if (shouldRedirectToLogin(bindingResult, customerCode, registrationStep)) {
            return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Unauthorized request");
        }
        User user = userRepository.findUserByCustomerCodeAndIsActiveTrue(customerCode);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(forgotPasswordDto.getNewPassword()));
            user = userRepository.save(user);
            if (user != null) {
                deleteCookies(response, request);
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, "Password Changed successfully");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Unauthorized request");
    }

    private String getCustomerCodeFromSession(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(CUST_CODE_SESSION);
    }

    private boolean isOtpRequestLimitExceeded(User user) {
        if (!DateUtils.isToday(user.getLastOtpSendDate())) {
            user.setNoOfSendOtp(0L); // Reset the count for the new day
        }
        return user.getNoOfSendOtp() != null && user.getNoOfSendOtp() >= DAILY_OTP_LIMIT;
    }

    private CompletableFuture<Void> sendEmailNotificationAsync(String email, String subject, String message) {
        return CompletableFuture.runAsync(() -> {
            if (email != null && !email.equals("")) {
                String[] to = {email};
                mailService.sendHtmlMail(to, subject, message);
                SmsEmailNotification smsEmailNotification = new SmsEmailNotification();
                smsEmailNotification.setEmail(email);
                smsEmailNotification.setMessage(message);
                smsEmailNotification.setStatus(SmsEmailNotificationEnum.SEND.name());
                smsEmailNotification.setType("Email");
                smsEmailNotification.setEmail(email);
                smsEmailNotificationRepository.save(smsEmailNotification);
            }
        });
    }

    private CompletableFuture<Boolean> sendSmsAsync(String mobileNo, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Boolean isSendSms = smsSender.sendSms(mobileNo, message);
                if (isSendSms) {
                    SmsEmailNotification smsEmailNotification = new SmsEmailNotification();
                    smsEmailNotification.setMobile(mobileNo);
                    smsEmailNotification.setMessage(message);
                    smsEmailNotification.setStatus(SmsEmailNotificationEnum.SEND.name());
                    smsEmailNotification.setType("SMS");
                    smsEmailNotificationRepository.save(smsEmailNotification);
                }
                return isSendSms;
            } catch (IOException e) {
                logger.info("Invalid Mobile No: " + mobileNo + " Message: " + e.getMessage());
                return false;
            }
        });
    }

    @Override
    public Response changeForgottenPassword(ForgotPasswordDto forgotPasswordDto) {

        if (!forgotPasswordDto.getNewPassword().equals(forgotPasswordDto.getConfirmPassword())) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "new password and confirm password not matched");
        }
        String token = forgotPasswordDto.getToken();
        User user = userRepository.findUserByCustomerCodeAndIsActiveTrue(forgotPasswordDto.getUsername());
        if (user == null) {
            return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Unauthorized request");
        }
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndCustomerCodeAndIsActiveTrue(token, user.getCustomerCode());
        if (verificationToken != null) {
            user.setPassword(passwordEncoder.encode(forgotPasswordDto.getNewPassword()));
            user = userRepository.save(user);
            if (user != null) {
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, "Password Changed successfully");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "OTP expired. Please try again.");
    }

    @Override
    public Response resetPassword(ResetPasswordDto resetPasswordDto) {
        String username = utilService.getLoggedInUsername();
        if (username == null) {
            return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Unauthorized request");
        }
        User user = userRepository.findUserByCustomerCodeAndIsActiveTrue(username);
        if (user == null) {
            return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Unauthorized request");
        }
        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "New Password and Confirm Password not matched");
        }
        if (!passwordEncoder.matches(resetPasswordDto.getPassword(), user.getPassword())) {
            return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Old Password not matched");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        user = userRepository.save(user);
        if (user != null) {
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, "Password Changed successfully");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    @Override
    public Response testPassword(String password) {
//        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        String passwordPattern = SystemConstants.PASSWORD_REGEX;
        Map<String, Boolean> isAcceptedPasswordMap = new HashMap<>();
        isAcceptedPasswordMap.put("isAcceptedPassword", MatchPattern.isMatchPattern(password, passwordPattern));
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, isAcceptedPasswordMap, "Password Acceptance Result");
    }

    @Override
    public Response matchPassword(String password) {
        Map<String, Boolean> isMatchedPasswordMap = new HashMap<>();
        String customerCode = utilService.getLoggedInUserCustomerCode();
        User user = getUserByEmailOrUserName(customerCode);
        isMatchedPasswordMap.put("isMatchedPassword", passwordEncoder.matches(password, user.getPassword()));
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, isMatchedPasswordMap, "Password Match Result");
    }

    @Override
    public ResponseEntity<Response> getAllMobileNumberLogByCustomerCode() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        return ResponseBuilder.getSuccessResponseInEntity(HttpStatus.OK, mobileNumberChangeLogRepository.findByCustomerCode(customerCode), "Success");
    }

    @Override
    public ResponseEntity<Response> processOtpRequest(HttpServletRequest request, HttpServletResponse response) {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        User user = userRepository.findUserByCustomerCodeAndIsActiveTrue(customerCode);
        BaseCustomer baseCustomer = getRegisteredCustomerByCode(user.getCustomerCode());

        if (isOtpRequestLimitExceeded(user)) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.NOT_ACCEPTABLE, "You can't make more than " + DAILY_OTP_LIMIT + " requests per day.");
        }
        VerificationToken verificationToken = saveVerificationToken(user.getCustomerCode());
        if (verificationToken == null) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.NOT_ACCEPTABLE, "You cannot make more than one request in " + OTP_EXPIRY_MINUTES + " minutes. Please try again after " + OTP_EXPIRY_MINUTES + " minutes.");
        }
        user.setLastOtpSendDate(new Date());
        user.setNoOfSendOtp(user.getNoOfSendOtp() == null ? 1L : user.getNoOfSendOtp() + 1L);
        userRepository.save(user);
        sendOtpAndSetCookies(request, response, baseCustomer, verificationToken, false);
        return ResponseBuilder.getSuccessResponseInEntity(HttpStatus.CREATED, null, "Please collect the OTP from your online registered mobile number.");
    }


    @Override
    public ResponseEntity<Response> createChangeMobileNumberRequest(InitialChangeMobileNumberDto changeMobileNumberDto, HttpServletRequest request, HttpServletResponse response) {
        if (authUtils.canUserImpersonateCustomer()) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.UNAUTHORIZED, "Admin users are not allowed to change mobile number");
        }
        String customerCode = utilService.getLoggedInUserCustomerCode();

        User user = getUserAndValidate();
        if (user == null) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.BAD_REQUEST, "Requested user not found");
        }

        if (customerCode.contains("NM-")) {
            CustomerDomestic customerDomestic = customerDomesticRepository.findByCodeAndIsRegisteredTrue(customerCode);
            if (!customerDomestic.getMobileNo().equals(changeMobileNumberDto.getMobileNumber())) {
                return ResponseBuilder.getFailureResponseInEntity(HttpStatus.BAD_REQUEST, "Current Mobile Number Incorrect");
            }
        } else {
            Customer customer = customerRepository.findByCodeAndIsRegisteredTrue(customerCode);
            if (!customer.getMobileNo().equals(changeMobileNumberDto.getMobileNumber())) {
                return ResponseBuilder.getFailureResponseInEntity(HttpStatus.BAD_REQUEST, "Current Mobile Number Incorrect");
            }
        }
        if (!isChangeAllowed(customerCode)) {
            return ResponseBuilder.getFailureResponseInEntity(
                    HttpStatus.BAD_REQUEST,
                    "Mobile number change limit exceeded. Please note that you can update your mobile number only once every 90 days."
            );
        }
        return processOtpRequest(request, response);
    }

    @Override
    public User getUserAndValidate() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        User user = userRepository.findUserByCustomerCodeAndIsActiveTrue(customerCode);
        BaseCustomer baseCustomer = getRegisteredCustomerByCode(customerCode);

        if (user == null || baseCustomer == null) {
            return null;
        }
        return user;
    }

    @Override
    public boolean isActiveOTPExist(String otp) {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndCustomerCodeAndIsActiveTrue(otp, customerCode);
        return verificationToken != null;
    }

    @Override
    public boolean verifyAndExpireOtp(String otp) {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndCustomerCodeAndIsActiveTrue(otp, customerCode);
        if (verificationToken != null) {
            verificationToken.setIsActive(false);
            verificationToken.setExpiryDate(new Date());
            verificationTokenRepository.save(verificationToken);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseEntity<Response> changeMobileNumber(ChangeMobileNumberDto changeMobileNumberDto) {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        if (customerCode == null) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.UNAUTHORIZED, "Unauthorized request");
        }
        User user = userRepository.findUserByCustomerCodeAndIsActiveTrue(customerCode);
        if (user == null) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.UNAUTHORIZED, "Unauthorized request");
        }

        if (!verifyAndExpireOtp(changeMobileNumberDto.getOtp())) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.BAD_REQUEST, "OTP expired. Please try again.");
        }

        if (!isChangeAllowed(customerCode)) {
            return ResponseBuilder.getFailureResponseInEntity(
                    HttpStatus.BAD_REQUEST,
                    "Mobile number change limit exceeded. Please note that you can update your mobile number only once every 90 days."
            );
        }
        String oldMobileNumber;
        String newMobileNumber = changeMobileNumberDto.getNewMobileNumber();

        if (customerCode.contains("NM-")) {
            CustomerDomestic customerDomestic = customerDomesticRepository.findByCodeAndIsRegisteredTrue(customerCode);
            if (!customerDomestic.getMobileNo().equals(changeMobileNumberDto.getMobileNumber())) {
                return ResponseBuilder.getFailureResponseInEntity(HttpStatus.BAD_REQUEST, "Current Mobile Number Incorrect");
            }
            oldMobileNumber = customerDomestic.getMobileNo();
            customerDomestic.setMobileNo(newMobileNumber);
            customerDomestic = customerDomesticRepository.save(customerDomestic);
            if (customerDomestic == null) {
                return ResponseBuilder.getFailureResponseInEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
        } else {
            Customer customer = customerRepository.findByCodeAndIsRegisteredTrue(customerCode);
            if (!customer.getMobileNo().equals(changeMobileNumberDto.getMobileNumber())) {
                return ResponseBuilder.getFailureResponseInEntity(HttpStatus.BAD_REQUEST, "Current Mobile Number Incorrect");
            }
            oldMobileNumber = customer.getMobileNo();
            customer.setMobileNo(newMobileNumber);
            customer = customerRepository.save(customer);
            if (customer == null) {
                return ResponseBuilder.getFailureResponseInEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
        }
        MobileNumberChangeLog log = new MobileNumberChangeLog();
        log.setCustomerCode(customerCode);
        log.setOldMobileNo(oldMobileNumber);
        log.setNewMobileNo(newMobileNumber);
        log.setChangedDateTime(LocalDateTime.now());
        log.setChangedBy(user.getCustomerCode());
        log.setRemarks("Mobile number changed from portal");
        mobileNumberChangeLogRepository.save(log);
        return ResponseBuilder.getSuccessResponseInEntity(HttpStatus.OK, null, "Mobile number changed successfully");
    }

    @Override
    public User getUserByEmailOrUserName(String customerCode) {
        return userRepository.findUserByCustomerCodeAndIsActiveTrue(customerCode);
    }

    private static boolean isHttps(HttpServletRequest request) {
        return request.getScheme().equalsIgnoreCase("https");
    }

    private boolean isChangeAllowed(String customerCode) {
        LocalDateTime now = LocalDateTime.now();

        List<MobileNumberChangeLog> changeLogs = mobileNumberChangeLogRepository.findByCustomerCode(customerCode);
        if (changeLogs.isEmpty()) {
            return true;
        }
        LocalDateTime lastChangeDate = changeLogs.get(changeLogs.size() - 1).getChangedDateTime();
        return lastChangeDate.plusDays(90).isBefore(now);
    }


}

