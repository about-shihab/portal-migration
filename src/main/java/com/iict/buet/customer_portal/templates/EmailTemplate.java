package com.iict.buet.customer_portal.templates;

import static com.iict.buet.customer_portal.util.SystemConstants.OTP_EXPIRY_MINUTES;

/**
 * Created by IICT BUET on 2/22/2020.
 */
public final class EmailTemplate {

    private EmailTemplate() {
    }

    public static String afterRegistrationHtmlEmailMessage(String customerName) {
        String body = "<h4 style='margin: 2px;'>Dear Customer,</h4>\n\r<h4 style='color:#3399ff; margin: 2px;'>Greetings from KGDCL Customer Portal!</h4>\n\rDear, " + customerName + "\n\rYour registration is successful. You can get updated customer and bill related information from the following link by Sign in/ Sign Up.\n\r\n\r\n\r<label style='border:1px solid lightgrey; padding: 5px; font-weight:bold; margin-bottom: 20px; border-radius: 5px; background: cornsilk; font-size: 16px;'><b>Download Link:</b> https://billing.kgdcl.gov.bd/</label><br>";
        body = body.replaceAll("(\r\n|\n)", "<br />");
        return body;
    }

    public static String afterForgotPasswordOtpExecution(String customerName, String otp) {
        String body = "Dear, \n\r " + customerName + "\n\r\n\r Your OTP for forget password of KGDCL Portal is:  " + otp + ".\n\r" +
                "\n\rThis OTP will be expired after " + OTP_EXPIRY_MINUTES + " mins\n\r " +
                "Thank You\n";
        return body;
    }

    public static String afterRegisterOtpExecution(String customerName, String otp) {
        String body = "Dear, \n\r " + customerName + "\n\r\n\r Your OTP for KGDCL customer Portal activation is:  " + otp + ".\n\r" +
                "\n\rThis OTP will be expired after 5 mins\n\r " +
                "Thank You\n";
        return body;
    }
}
