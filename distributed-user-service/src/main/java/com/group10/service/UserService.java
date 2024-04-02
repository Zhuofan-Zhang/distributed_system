package com.group10.service;

import com.group10.request.UserLoginRequest;
import com.group10.request.UserRegisterRequest;
import com.group10.dto.UserDTO;
import com.group10.util.JsonData;

public interface UserService {

    JsonData register(UserRegisterRequest userRegisterRequest);
    JsonData login(UserLoginRequest userLoginRequest);

    UserDTO getUserDetail();

    JsonData logout();
}
