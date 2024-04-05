package com.group10.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    /**
     * name
     */
    private String name;


    /**
     * photo
     */
    private String avatar;

    /**
     * User signature
     */
    private String slogan;

    /**
     * 0 = female，1= man
     */
    private Integer gender;

    /**
     * points
     */
    private Integer points;


    /**
     * emaiil
     */
    private String email;

}
