package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.dto.ProfileDto;
import com.iict.buet.customer_portal.dto.Response;

public interface ProfileService {
    Response getProfileInfo();
    ProfileDto getProfile();
}
