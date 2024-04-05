package com.group10.enums;


public enum AddressStatusEnum {


    DEFAULT_STATUS(1),

    COMMON_STATUS(0);

    private int status;

    private AddressStatusEnum(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
