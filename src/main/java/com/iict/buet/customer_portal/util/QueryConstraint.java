package com.iict.buet.customer_portal.util;

public final class QueryConstraint {
    private QueryConstraint() {
    }

    public static class CustomerBillManagement {
        public static final String REAL_TIME_SURCHARGE_METERED_FUNC = "SELECT meter_realtime_surcharge (:customerCode, :billYear, :billMonth, :chequePayDate) FROM DUAL";
        public static final String REAL_TIME_SURCHARGE_NON_METERED_FUNC = "SELECT NM_realtime_surcharge (:customerCode, :payDate) FROM DUAL";

        public static final String CERTIFICATE_DETERMINER =
                "SELECT COUNT(*) FROM CUSTOMER_BILL cb " +
                        "JOIN METER_UNOIN_DOM_CUST_VIEW mv ON mv.code = cb.CUST_CODE " +
                        "WHERE cb.status <> 'Paid' " +
                        "AND cb.CUST_CODE = :inCustCode " +
                        "AND CONCAT(cb.BILL_YEAR, TRIM(TO_CHAR(cb.BILL_MONTH, '09'))) <= CONCAT(:inBillYear, TRIM(TO_CHAR(:inBillMonth, '09')))";

        public static final String CERTIFICATE_DETERMINER_NM =
                "SELECT COUNT(*) " +
                        "FROM VALID_FOR_CERTIFICATE  VFC " +
                        "WHERE VFC.CUST_CODE = :inCustCode";

        public static final String CERTIFICATE_DETERMINER_ALL =
                "SELECT COUNT(*) " +
                        "FROM CUSTOMER_BILL cb " +
                        "WHERE cb.cust_code = :inCustCode " +
                        "AND cb.status <> 'Paid' " +
                        "AND cb.bill_month <= :inBillMonth  " +
                        "AND cb.bill_year <= :inBillYear ";


    }

}
