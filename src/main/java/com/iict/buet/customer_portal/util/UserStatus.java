package com.iict.buet.customer_portal.util;

public enum UserStatus {
	ACTIVE ("Active", "User is created and activated"),
	DEACTIVATED ("Deactivated", "User is deactivated in system by admin"),
	INACTIVE ("Awaiting activation","User is created but activation link is not used"),
	BLOCKED("Blocked", "Blocked"),
	LOCKED ("Locked out", "User is locked out due to invalid password attempts"),
	DELETED ("Deleted", "Admins delete the user");

	private final String description;
	private final String summary;

	UserStatus(String description, String summary) {
		this.description = description;
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}
}
