package com.group10.controller;


import com.google.code.kaptcha.Producer;
import com.group10.service.NotifyService;
import com.group10.enums.BizCodeEnum;
import com.group10.enums.SendCodeEnum;
import com.group10.util.CommonUtil;
import com.group10.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Api(tags = "notification")
@RestController
@RequestMapping("/api/user/v1")
@Slf4j
public class NotificationController {

    private static final long CAPTCHA_CODE_EXPIRED = 60 * 1000 * 10;

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private NotifyService notifyService;

    @GetMapping("captcha")
    @ApiOperation("get image verification code")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {

        String cacheKey = getCaptchaKey(request);

        String capText = captchaProducer.createText();

        redisTemplate.opsForValue().set(cacheKey, capText, CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);

        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = null;
        try {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "create_date-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.flush();
            out.close();

        } catch (IOException e) {
            log.error("获取验证码失败:{}", e);
        }
    }

    @ApiOperation("send email verification code")
    @PostMapping("send_code")
    public JsonData sendRegisterCode(@RequestParam(value = "to") String to, @RequestParam(value = "captcha") String captcha,
            HttpServletRequest request) {
        String key = getCaptchaKey(request);
        String cacheCaptcha = redisTemplate.opsForValue().get(key);
        if(captcha != null && captcha.equalsIgnoreCase(cacheCaptcha)){
            redisTemplate.delete(key);
            JsonData jsonData = notifyService.sendCode(SendCodeEnum.USER_REGISTER, to);
            return jsonData;
        }else {
            return JsonData.buildResult(BizCodeEnum.CODE_CAPTCHA_ERROR);
        }
    }

    ;

    private String getCaptchaKey(HttpServletRequest request) {
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        log.info("ip:{}", ip);
        log.info("userAgent:{}", userAgent);

        return "user-service:captcha:" + CommonUtil.MD5(ip + userAgent);
    }
}
