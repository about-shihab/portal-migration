package com.iict.buet.customer_portal.templates;

import static com.iict.buet.customer_portal.util.SystemConstants.OTP_EXPIRY_MINUTES;

/**
 * Created by IICT BUET on 2/22/2020.
 */
public class SmsTemplate {

    public static String afterForgotPasswordOtpExecution(String customerName, String otp) {
        String body = "Dear, " + customerName + ",\n\n"
                + "Your OTP for resetting the password on the KGDCL Portal: " + otp
                + "\n\rThis OTP will be expired after " + OTP_EXPIRY_MINUTES + " minutes.";
        return body;
    }


    public static String afterRegistrationOtpExecution(String customerName, String otp) {
        String body = "Dear, " + customerName + "\n\n"
                + "Your OTP for KGDCL Portal activation: " + otp
                + "\n\rThis OTP will be expired after " + OTP_EXPIRY_MINUTES + " minutes.";
        return body;
    }

    public static String afterRegistrationSendMessage(String customerName) {
        String body = "Dear, \n\r " + customerName + "\n\r\n\r Your KGDCL Portal registration successful.";
        return body;
    }
}
