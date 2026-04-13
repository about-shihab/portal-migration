package com.iict.buet.customer_portal.util;

import java.util.HashSet;
import java.util.Set;

public final class SystemConstants {
    public static final String AUTHOR = "KGDCL";
    public static final String CREATOR = "billing.kgdcl.gov.bd";
    public static final String CERT_TITLE = "Customer Certificate";
    public static final String DUES_CERT_PATH = "dues_certificate.jasper";

    //public static final String DUES_CERT_PATH ="E:\\New folder (3)\\kgdcl_portal\\kgdcl_portal\\src\\main\\resources\\reports\\dues_certificate.jasper";

    public static final String NO_DUES_CERT_PATH = "no_dues_certificate.jasper";
    //public static final String NO_DUES_CERT_PATH = "E:\\New folder (3)\\kgdcl_portal\\kgdcl_portal\\src\\main\\resources\\reports\\no_dues_certificate.jasper";

    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$";
    public static final String MOBILE_NUMBER_REGEX = "^01\\d{9}$";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final int CUST_COOKIE_EXPIRY_MINUTES = 10;
    public static final int OTP_EXPIRY_MINUTES = 3;
    public static final int DAILY_OTP_LIMIT = 5;
    public static final int STEP_COOKIE_EXPIRY_MINUTES = OTP_EXPIRY_MINUTES;

    public static final int OTP_SIZE = 5;

    public static final Set<String> ELIGIBLE_FOR_RECONNECTION = new HashSet<String>() {{
        add("10402");
        add("10396");
        add("10397");
        add("10398");
        add("10399");
        add("10400");
        add("12677");
        add("12680");
        add("40001");
        add("10101");
        add("10404");
    }};

}
