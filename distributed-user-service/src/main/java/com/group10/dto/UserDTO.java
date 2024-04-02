package com.group10.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    /**
     * 昵称
     */
    private String name;


    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户签名
     */
    private String slogan;

    /**
     * 0表示女，1表示男
     */
    private Integer gender;

    /**
     * 积分
     */
    private Integer points;


    /**
     * 邮箱
     */
    private String email;

}
