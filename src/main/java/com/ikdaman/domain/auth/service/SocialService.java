package com.ikdaman.domain.auth.service;

import com.ikdaman.domain.auth.model.AuthReq;
import com.ikdaman.domain.auth.model.AuthRes;

public interface SocialService {
    AuthRes login(AuthReq dto, String socialToken);
} 