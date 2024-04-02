package com.group10.service;

import com.group10.enums.SendCodeEnum;
import com.group10.util.JsonData;

public interface NotifyService {
    JsonData sendCode(SendCodeEnum sendCodeEnum, String to);

    boolean verifyCode(SendCodeEnum sendCodeEnum, String to, String code);
}
