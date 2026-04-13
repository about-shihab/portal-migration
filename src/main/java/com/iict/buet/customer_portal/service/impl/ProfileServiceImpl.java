package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.dto.ApplianceInfoDto;
import com.iict.buet.customer_portal.dto.MeterInfoDto;
import com.iict.buet.customer_portal.dto.ProfileDto;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.model.*;
import com.iict.buet.customer_portal.repository.*;
import com.iict.buet.customer_portal.service.ProfileService;
import com.iict.buet.customer_portal.service.UtilService;
import com.iict.buet.customer_portal.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("profileService")
public class ProfileServiceImpl implements ProfileService {
    private final CustomerRepository customerRepository;
    private final CustomerDomesticRepository customerDomesticRepository;
    private final CustomerDomesticAddressRepository customerDomesticAddressRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final NonMeteredCustomerApplianceRepository nonMeteredCustomerApplianceRepository;
    private final InstalledMeterInfoRepository installedMeterInfoRepository;
    private final ApplianceInfoRepository applianceInfoRepository;
    private final UtilService utilService;

    public ProfileServiceImpl(InstalledMeterInfoRepository installedMeterInfoRepository, CustomerAddressRepository customerAddressRepository, ApplianceInfoRepository applianceInfoRepository, NonMeteredCustomerApplianceRepository nonMeteredCustomerApplianceRepository, CustomerDomesticAddressRepository customerDomesticAddressRepository, UtilService utilService, CustomerRepository customerRepository, CustomerDomesticRepository customerDomesticRepository) {
        this.customerRepository = customerRepository;
        this.customerDomesticRepository = customerDomesticRepository;
        this.utilService = utilService;
        this.customerDomesticAddressRepository = customerDomesticAddressRepository;
        this.nonMeteredCustomerApplianceRepository = nonMeteredCustomerApplianceRepository;
        this.applianceInfoRepository = applianceInfoRepository;
        this.customerAddressRepository = customerAddressRepository;
        this.installedMeterInfoRepository = installedMeterInfoRepository;
    }

    @Override
    public Response getProfileInfo() {
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, getProfile(), "User Profile Information Retrieve Successfully");
    }

    @Override
    public ProfileDto getProfile() {
        ProfileDto profileDto = new ProfileDto();
        String customerCode = utilService.getLoggedInUserCustomerCode();
        profileDto.setCustomerCode(customerCode);
        String name = null;
        String mobileNo = null;
        String email = null;
        Boolean isNonMetered = false;
        String username = utilService.getLoggedInUsername();
        profileDto.setUsername(username);
        String customerType = null;
        String connectionStatus = null;
        String connectionStatusId = null;
        String zone = null;
        String address = null;
        String oldCode;
        if (customerCode.contains("NM-")) {
            isNonMetered = true;
            CustomerDomestic customerDomestic = customerDomesticRepository.findByCodeAndIsRegisteredTrue(customerCode);
            oldCode = customerDomestic.getOldCode();
            name = customerDomestic.getCustomerName();
            mobileNo = customerDomestic.getMobileNo();
            email = customerDomestic.getEmail();
            customerType = "Non-Metered Domestic";
            connectionStatus = customerDomestic.getConnectionStatusType().getName();
            connectionStatusId = customerDomestic.getConnectionStatusType().getId().toString();
            zone = customerDomestic.getZone();
            CustomerDomesticAddress domesticAddress = customerDomesticAddressRepository.findByCustomerIdAndTitleAndIsActiveTrue(customerDomestic.getId(), "Billing Address");
            if (domesticAddress != null) {
                address = getAddress(domesticAddress.getHoldingNo(), domesticAddress.getRoadNo(), domesticAddress.getMoholla(), domesticAddress.getPoliceStation(), domesticAddress.getDistrict());

            }
            List<NonMeteredCustomerAppliance> nonMeteredCustomerAppliances = nonMeteredCustomerApplianceRepository.findAllByCustomerId(customerDomestic.getId());
            if (nonMeteredCustomerAppliances != null && nonMeteredCustomerAppliances.size() > 0) {
                List<ApplianceInfoDto> applianceInfoDtos = new ArrayList<>();
                nonMeteredCustomerAppliances.forEach(nonMeteredCustomerAppliance -> {
                    ApplianceInfoDto applianceInfoDto = new ApplianceInfoDto();
                    applianceInfoDto.setQuantityOfAppliance(nonMeteredCustomerAppliance.getQuantity());
                    ApplianceInfo applianceInfo = applianceInfoRepository.findById(nonMeteredCustomerAppliance.getApplianceInfoId()).get();
                    applianceInfoDto.setApplianceName(applianceInfo.getApplianceName());
                    applianceInfoDtos.add(applianceInfoDto);
                });
                profileDto.setApplianceInfoList(applianceInfoDtos);
            }
        } else {
            Customer customer = customerRepository.findByCodeAndIsRegisteredTrue(customerCode);
            oldCode = customer.getOldCode();
            name = customer.getCustomerName();
            mobileNo = customer.getMobileNo();
            email = customer.getEmail();
            customerType = customer.getCustomerType().getTitle();
            connectionStatusId = customer.getConnectionStatusType().getId().toString();
            connectionStatus = customer.getConnectionStatusType().getName();
            zone = customer.getZone();
            CustomerAddress customerAddress = customerAddressRepository.findByCustomerIdAndTitleAndIsActiveTrue(customer.getId(), "Billing Address");
            if (customerAddress != null) {
                address = getAddress(customerAddress.getHoldingNo(), customerAddress.getRoadNo(), customerAddress.getMoholla(), customerAddress.getPoliceStation(), customerAddress.getDistrict());

            }
            List<InstalledMeterInfo> installedMeterInfos = installedMeterInfoRepository.findAllByCustomerCodeAndIsActiveTrue(customer.getCode());
            if (installedMeterInfos != null && installedMeterInfos.size() > 0) {
                List<MeterInfoDto> meterInfoDtos = new ArrayList<>();
                installedMeterInfos.forEach(installedMeterInfo -> {
                    MeterInfoDto meterInfoDto = new MeterInfoDto();
                    meterInfoDto.setMeterCode(installedMeterInfo.getMeterCode());
                    meterInfoDto.setMeterGroup(installedMeterInfo.getMeterGroup());
                    meterInfoDto.setMeterNo(installedMeterInfo.getMeterNo());
                    meterInfoDto.setMeterStatus(installedMeterInfo.getMeterStatus());
                    meterInfoDto.setMeterType(installedMeterInfo.getMeterType());
                    meterInfoDtos.add(meterInfoDto);
                });
                profileDto.setMeterInfoList(meterInfoDtos);
            }
        }
        profileDto.setOldCode(oldCode);
        profileDto.setName(name);
        profileDto.setMobileNo(mobileNo);
        profileDto.setEmail(email);
        profileDto.setCustomerType(customerType);
        profileDto.setConnectionId(connectionStatusId);
        profileDto.setConnectionStatus(connectionStatus);
        profileDto.setZone(zone);
        profileDto.setAddress(address);
        profileDto.setIsNonMetered(isNonMetered);
        return profileDto;
    }

    private String getAddress(String holdingNo, String roadNo, String moholla, String policeStation, String district) {
        StringBuilder builder = new StringBuilder("");
        if (holdingNo != null && !holdingNo.trim().equals("")) {
            builder.append("Holding No: " + holdingNo + ", ");
        }
        if (roadNo != null && !roadNo.trim().equals("")) {
            builder.append("Road No: " + roadNo + ", ");
        }
        if (moholla != null && !moholla.trim().equals("")) {
            builder.append("Moholla: " + moholla + ", ");
        }
        if (policeStation != null && !policeStation.trim().equals("")) {
            builder.append("PS: " + policeStation + ", ");
        }
        if (district != null && !district.trim().equals("")) {
            builder.append("District: " + district);
        }
        return builder.toString();
    }

}
