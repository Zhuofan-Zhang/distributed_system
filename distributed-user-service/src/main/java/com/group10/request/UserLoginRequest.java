package com.group10.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "Login object",description = "User login request object")
public class UserLoginRequest {



    @ApiModelProperty(value = "email", example = "794666918@qq.com")
    private String email;

    @ApiModelProperty(value = "password", example = "123456")
    private String pwd;


}
