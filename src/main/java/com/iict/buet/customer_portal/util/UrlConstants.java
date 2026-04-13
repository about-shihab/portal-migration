package com.iict.buet.customer_portal.util;

public final class UrlConstants {
    private UrlConstants() {

    }

    private static final String API = "/api";
    private static final String VERSION = "/v1";

    public static class UserManagement {
        public static final String ROOT = API + VERSION + "/users";
        public static final String GET_INFO = "/info";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String GET = "/{id}";
        public static final String GET_ALL = "/all";
        public static final String LOGOUT = "/logout";
        public static final String MATCH_PASSWORD = "/matchPassword";
        public static final String RESET_PASSWORD = "/resetPassword";
        public static final String CHANGE_PASSWORD = "/changePassword";
        public static final String UPDATE_USERNAME = "/updateUsername";
        public static final String CHANGE_MOBILE_NUMBER = "/changeMobileNumber";
        public static final String CHANGE_MOBILE_NUMBER_OTP_REQUEST = "/changeMobileNumberOtpRequest";
        public static final String CHANGE_MOBILE_NUMBER_LOG = "/changeMobileNumberLog";
    }

    public static class ComplainInfoManagement {
        public static final String ROOT = API + VERSION + "/complain-infos";
        public static final String CREATE = "/create";
        public static final String COMPLAIN_CAUSE_LIST = "/cause-list";
        public static final String COMPLAIN_TICKET_STATUS_LIST = "/ticket-status-list";
        public static final String COMPLAIN_REVIEW_STATUS_LIST = "/ticket-review-list";
        public static final String REVIEW = "/review";
    }

    public static class TransactionManagement {
        public static final String ROOT = API + VERSION + "/transactions";
        public static final String GET_INFO = "/info";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String GET = "/{id}";
        public static final String GET_ALL = "/all";
        public static final String LOGOUT = "/logout";
        public static final String MATCH_PASSWORD = "/matchPassword";
        public static final String RESET_PASSWORD = "/resetPassword";
    }

    public static class BillInfoManagement {
        public static final String ROOT = API + VERSION + "/billInfo";
        public static final String DOWNLOAD_FILE = "/download";
        public static final String NON_METERED_BILL = "/nonMeteredBill";
        public static final String METERED_BILL = "/meteredBill";
        public static final String METERED_PARTIAL_BILL = "/meteredPartialBill";
        public static final String METERED_PARTIAL_DETAILS = "/meteredPartialDetails";
        public static final String YEAR_MAP = "/yearMap";
        public static final String BILL_LIST = "/billList";
        public static final String BILL_HISTORY = "/billHistory";
        public static final String BILL_COLLECTION_REPORT = "/billCollectionReport";
        public static final String BILL_LIST_UNPAID = "/unpaidList";
        public static final String METERED_BILL_ALL_DUES = "/meteredAllDues";
        public static final String GET_DBBL_PAYMENT_RESULT = "/getDbblPaymentResult";
        public static final String GET_DBBL_PAYMENT_TRANSACTION_ID = "/getDbblPaymentTrId";
        public static final String GATEWAY_FEE = "/calculateGatewayFee";
        public static final String NON_METERED_PAYMENT_INFO = "/nonMeteredPaymentInfo";
    }

    public static class ActionLogManagement {
        public static final String ROOT = API + VERSION + "/actionLogs";
        public static final String GET_ALL = "/all";
    }

    public static class RoleManagement {
        public static final String ROOT = API + VERSION + "/roles";
        public static final String CREATE = "/create";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String GET = "/{id}";
        public static final String GET_ALL = "/all";
        public static final String ASSIGN_URL = "/assignUrl/{id}";
        public static final String ASSIGN_USERS = "/assignUsers/{id}";
    }

    public static class MenuManagement {
        public static final String ROOT = API + VERSION + "/menus";

        public static final String CREATE = "/create";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String GET = "/{id}";
        public static final String GET_ALL = "/all";
    }

    public static class AuthManagement {
        public static final String ROOT = API + "/auth";
        public static final String LOGIN = "/login";
        public static final String SIGNUP = "/signup";
        public static final String VALIDATE_CUSTOMER = "/validate";
        public static final String ADMIN_LOGIN = "/admin/login";
        public static final String TEST_PASSWORD = "/testPassword";
        public static final String FORGOT_PASSWORD = "/forgotPassword";
        public static final String SET_NEW_PASSWORD_FOR_FORGOT = "/setForgottenPassword";
        public static final String RESET = "/reset";

        public static final String OTP_VERIFY = "/otp";
        public static final String CREATE = "/create";
        public static final String BANK_LIST = "/bankList";
        public static final String C_LIST = "/cList";

        public static final String ACTIVATE_USER = "/activate";
    }

    public static class CustomerCertificateManagement {
        public static final String ROOT = API + VERSION + "/certificate";
        public static final String DOWNLOAD = "/download";
    }

    public static class RegistrationCardManagement {
        public static final String ROOT = API + VERSION + "/registration-card";
        public static final String ISSUE_REQUEST = "/issue-request";
        public static final String VALIDATE_OTP = "/validate-otp";
        public static final String DOWNLOAD = "/download";
    }

    public static class ReconnectionManagement {
        public static final String ROOT = "/reconnection";
        public static final String REQUEST = "/request";
    }


    public static String getVersion() {
        return VERSION;
    }
}