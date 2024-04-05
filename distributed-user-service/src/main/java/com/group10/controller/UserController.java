package com.group10.controller;


import com.group10.dto.UserDTO;
import com.group10.enums.BizCodeEnum;
import com.group10.request.UserLoginRequest;
import com.group10.request.UserRegisterRequest;
import com.group10.service.FileService;
import com.group10.service.UserService;
import com.group10.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zzf
 * @since 2023-08-29
 */

@Api(tags = "user module")
@RestController
@RequestMapping("/api/user/v1")
public class UserController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @ApiOperation("user registration")
    @PostMapping("register")
    public JsonData register(@ApiParam("userRegisterRequest") @RequestBody UserRegisterRequest userRegisterRequest){
        return userService.register(userRegisterRequest);
    }


    @ApiOperation("user login")
    @PostMapping("login")
    public JsonData login(@ApiParam("User login object") @RequestBody UserLoginRequest userLoginRequest){
        return userService.login(userLoginRequest);
    }

    @ApiOperation("user login")
    @PostMapping("logout")
    public JsonData logout(){
        return userService.logout();
    }

    @ApiOperation("get customer information")
    @GetMapping("detail")
    public JsonData detail(){

        UserDTO userDTO = userService.getUserDetail();

        return JsonData.buildSuccess(userDTO);
    }

}

