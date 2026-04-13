package com.iict.buet.customer_portal.util;

public enum GatewayConstraint {
    DBBL_NEXUS("DBBL_NEXUS", "DBBL Nexus Payment Gateway");
    private String name;
    private String description;

    GatewayConstraint(String name, String description) {
        this.name = name;
        this.description = description;
    }
}