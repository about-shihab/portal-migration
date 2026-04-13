package com.iict.buet.customer_portal.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LoginRequestDto {
	public interface UpdateValidation {
	}

	public interface CreateValidation {
	}
	public interface ResetValidation {
	}
	public interface LogoutValidation {
	}

	@NotNull(groups = {LogoutValidation.class})
	private Long id;

	@NotEmpty(groups = {CreateValidation.class,ResetValidation.class  })
	@Email(groups = {CreateValidation.class,ResetValidation.class })
	private String username;
	@NotEmpty(groups = {CreateValidation.class,UpdateValidation.class })
	private String password;
	@NotEmpty(groups = {UpdateValidation.class })
	private String newPassword;
	
	/*@NotEmpty(groups = {UpdateValidation.class })
	@ValidateString(acceptedValues = { "SUPER_ADMIN", "ADMIN", "VIEW_ONLY", "VIEW_EDIT_ONLY",
			"SCRIBE" }, message = "Invalid userType")*/
	private String userType;
	private String uiScreen = "";
	private String ipAddress = "";

	private List<RoleDto> roles;

	public String getPageName() {
		String breadCrumb[] = this.uiScreen.split(">");
		return breadCrumb[breadCrumb.length - 1];
	}
}
