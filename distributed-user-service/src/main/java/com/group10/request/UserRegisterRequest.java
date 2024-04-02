package com.group10.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserRegisterRequest", description = "UserRegisterRequest")
public class UserRegisterRequest {

    @ApiModelProperty(value = "name",example = "Anna")
    private String name;

    @ApiModelProperty(value = "password",example = "12345")
    private String pwd;

    @ApiModelProperty(value = "avatar",example = "https://xdclass-zzf.oss-rg-china-mainland.aliyuncs.com/test/2023/08/31/144740f59c754113a0f7507918acea9d.jpg")
    @JsonProperty("avatar")
    private String avatar;

    @ApiModelProperty(value = "slogan",example = "yes yes yes")
    private String slogan;

    @ApiModelProperty(value = "gender",example = "female")
    private String gender;

    @ApiModelProperty(value = "points",example = "10 ")
    private String points;

    @ApiModelProperty(value = "email",example = "xxx@xxx.com")
    private String email;

    @ApiModelProperty(value = "secret",example = "202330 ")
    private String code;
}
