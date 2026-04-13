package com.iict.buet.customer_portal.enums;

public enum SmsEmailNotificationEnum {
    SEND("SEND", "Send Notification"),
    PENDING("PENDING", "Ready to send Notification"),
    FAILED("FAILED", "Failed to send Notification");

    SmsEmailNotificationEnum(String name, String description) {
    }
}
