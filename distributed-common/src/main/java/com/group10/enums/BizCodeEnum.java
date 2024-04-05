package com.group10.enums;

import lombok.Getter;

public enum BizCodeEnum {




    CART_FAIL(220001,"Add to cart failed"),
    CODE_TO_ERROR(240001,"Invalid phone number."),
    CODE_LIMITED(240002,"Request for CAPTCHA sent too soon"),
    CODE_ERROR(240003,"Wrong email verification code"),
    CODE_CAPTCHA_ERROR(240101,"Wrong CAPTCHA"),




    ACCOUNT_REPEAT(250001,"Account already exists"),
    ACCOUNT_UNREGISTER(250002,"Account doesn't exist"),
    ACCOUNT_PWD_ERROR(250003,"Wrong email or password"),
    ACCOUNT_UNLOGIN(250004,"Account not logged in"),


    COUPON_CONDITION_ERROR(270001,"Unmet coupon usage condition"),
    COUPON_UNAVAILABLE(270002,"No usable coupons"),
    COUPON_NO_EXITS(270003,"Coupon doesn't exist"),
    COUPON_NO_STOCK(270005,"Coupon out of stock"),
    COUPON_OUT_OF_LIMIT(270006,"Exceed coupon number limits"),
    COUPON_OUT_OF_TIME(270407,"Coupon expired "),
    COUPON_GET_FAIL(270407,"Issuing coupon failed"),
    COUPON_RECORD_LOCK_FAIL(270409,"Lock coupon failed"),


    ORDER_CONFIRM_COUPON_FAIL(280001,"Submit order failed - Unmet coupon usage price condition"),
    ORDER_CONFIRM_PRICE_FAIL(280002,"Submit order failed - Price verification failed"),
    ORDER_CONFIRM_LOCK_PRODUCT_FAIL(280003,"Submit order failed - product out of stock"),
    ORDER_CONFIRM_ADD_STOCK_TASK_FAIL(280004,"Submit order failed - insert product task failed"),
    ORDER_CONFIRM_TOKEN_NOT_EXIST(280008,"No order token"),
    ORDER_CONFIRM_TOKEN_EQUAL_FAIL(280009,"Wrong order token"),
    ORDER_CONFIRM_NOT_EXIST(280010,"Order not exist"),
    ORDER_CONFIRM_CART_ITEM_NOT_EXIST(280011,"Cart items not exist"),


    ADDRESS_ADD_FAIL(290001,"Add address failed"),
    ADDRESS_DEL_FAIL(290002,"Delete address failed"),
    ADDRESS_NO_EXITS(290003,"Address not exist"),


    PAY_ORDER_FAIL(300001,"Failed to create payment order"),
    PAY_ORDER_CALLBACK_SIGN_FAIL(300002,"PAY_ORDER_CALLBACK_SIGN_FAIL"),
    PAY_ORDER_CALLBACK_NOT_SUCCESS(300003,"PAY_ORDER_CALLBACK_NOT_SUCCESS"),
    PAY_ORDER_NOT_EXIST(300005,"PAY_ORDER_NOT_EXIST"),
    PAY_ORDER_STATE_ERROR(300006,"PAY_ORDER_STATE_ERROR"),
    PAY_ORDER_PAY_TIMEOUT(300007,"PAY_ORDER_PAY_TIMEOUT"),

    FILE_UPLOAD_USER_IMG_FAIL(600101,"FILE_UPLOAD_USER_IMG_FAIL");

    @Getter
    private String message;

    @Getter
    private int code;

    private BizCodeEnum(int code, String message){
        this.code = code;
        this.message = message;
    }
}
