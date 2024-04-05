package com.group10.interceptor;

import com.group10.enums.BizCodeEnum;
import com.group10.model.LoginUser;
import com.group10.util.CommonUtil;
import com.group10.util.JWTUtil;
import com.group10.util.JsonData;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = request.getHeader("token");
        if(accessToken == null) {
            accessToken = request.getParameter("token");
        }

        if (StringUtils.isNotBlank(accessToken)){
            Claims claims = JWTUtil.checkJWT(accessToken);
            if(claims == null) {
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }
            long userId = Long.valueOf(claims.get("id").toString());
            String avatar = (String) claims.get("avatar");
            String name = (String) claims.get("name");
            String email = (String) claims.get("email");

            LoginUser loginUser = LoginUser.builder().id(userId).avatar(avatar).name(name).email(email).build();

            threadLocal.set(loginUser);
            return true;
        }
        CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
