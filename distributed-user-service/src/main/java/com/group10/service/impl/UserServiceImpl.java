package com.group10.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.group10.constants.CacheKey;
import com.group10.model.LoginUser;
import com.group10.request.NewUserCouponRequest;
import com.group10.request.UserLoginRequest;
import com.group10.request.UserRegisterRequest;
import com.group10.service.NotifyService;
import com.group10.service.UserService;
import com.group10.dto.UserDTO;
import com.group10.util.JWTUtil;
import com.group10.util.JsonData;
import com.group10.enums.BizCodeEnum;
import com.group10.enums.SendCodeEnum;
import com.group10.feign.CouponFeignService;
import com.group10.interceptor.LoginInterceptor;
import com.group10.mapper.UserMapper;
import com.group10.model.LoginUser;
import com.group10.model.UserDO;
import com.group10.util.CommonUtil;
import com.group10.util.JWTUtil;
import com.group10.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.group10.enums.SendCodeEnum.USER_LOGIN;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData register(UserRegisterRequest userRegisterRequest) {
        boolean codeVerification = false;
        if (StringUtils.isNotBlank(userRegisterRequest.getCode())) {
            codeVerification = notifyService.verifyCode(SendCodeEnum.USER_REGISTER, userRegisterRequest.getEmail(), userRegisterRequest.getCode());
        }
        if (!codeVerification) {
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }
        UserDO userDo = new UserDO();
        BeanUtils.copyProperties(userRegisterRequest, userDo);
        userDo.setCreatedAt(new Date());
        userDo.setSlogan(userRegisterRequest.getSlogan());
        //设置密码 生成秘钥 盐
        userDo.setSecret("$1$" + CommonUtil.getStringNumRandom(8));

        //密码+盐处理
        String cryptPwd = Md5Crypt.md5Crypt(userRegisterRequest.getPwd().getBytes(), userDo.getSecret());
        userDo.setPwd(cryptPwd);
        //account unique check TODO

        if (isUnique(userDo.getEmail())) {
            int rows = userMapper.insert(userDo);
            log.info("rows {},successfully registered", rows);
            userRegisterInitTask(userDo);
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
        }
    }

    @Override
    public JsonData login(UserLoginRequest userLoginRequest) {
        String email = userLoginRequest.getEmail();
        String lockKey = "lock:login:" + email;
        RLock rLock = redissonClient.getLock(lockKey);

        rLock.lock();


        log.info("Login locked:{}", Thread.currentThread().getId());
        try {
            String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, USER_LOGIN, email);

            String cacheValue = redisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.isNotBlank(cacheValue) && Integer.parseInt(cacheValue) >= 4) {
                return JsonData.buildError("Exceed maximum login devices");
            } else {
                List<UserDO> userDOList = userMapper.selectList(new QueryWrapper<UserDO>().eq("email", userLoginRequest.getEmail()));
                if (StringUtils.isBlank(cacheValue)) {
                    redisTemplate.opsForValue().set(cacheKey, String.valueOf(1));
                } else {
                    String loggedInNumber = String.valueOf(Integer.parseInt(cacheValue) + 1);
                    redisTemplate.opsForValue().set(cacheKey, loggedInNumber);
                }
                if (userDOList != null && userDOList.size() == 1) {
                    UserDO userDO = userDOList.get(0);
                    String cryptPwd = Md5Crypt.md5Crypt(userLoginRequest.getPwd().getBytes(), userDO.getSecret());
                    if (cryptPwd.equals(userDO.getPwd())) {
                        LoginUser loginUser = LoginUser.builder().build();
                        BeanUtils.copyProperties(userDO, loginUser);

                        String accessToken = JWTUtil.geneJsonWebToken(loginUser);

                        return JsonData.buildSuccess(accessToken);

                    } else {
                        return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
                    }
                } else {
                    return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
                }
            }
        } finally {
            log.info("login unlocked");
            rLock.unlock();
        }

    }

    @Override
    public UserDTO getUserDetail() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("id", loginUser.getId()));
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userDO, userDTO);
        return userDTO;
    }

    @Override
    public JsonData logout() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String email = loginUser.getEmail();
        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, USER_LOGIN, email);
        String lockKey = "lock:logout:" + email;
        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock();
        log.info("Login locked:{}", Thread.currentThread().getId());
        try {
            String cacheValue = redisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.isNotBlank(cacheValue)) {
                String loggedInNumber = String.valueOf(Integer.parseInt(cacheValue) - 1);
                if ((Integer.parseInt(cacheValue) - 1) == 0) {
                    redisTemplate.delete(cacheKey);
                } else {
                    redisTemplate.opsForValue().set(cacheKey, loggedInNumber);
                }
                return JsonData.buildCodeAndMsg(0, "Logout Successfully");
            } else {
                return JsonData.buildError("No Logged In User");
            }
        } finally {
            rLock.unlock();
            log.info("lock released");
        }
    }

    /**
     * 初始化福利信息
     *
     * @param email
     */

    private boolean isUnique(String email) {
        QueryWrapper queryWrapper = new QueryWrapper<UserDO>().eq("email", email);

        List<UserDO> list = userMapper.selectList(queryWrapper);

        return list.size() > 0 ? false : true;
    }

    private void userRegisterInitTask(UserDO userDO) {
        NewUserCouponRequest request = new NewUserCouponRequest();
        request.setName(userDO.getName());
        request.setUserId(userDO.getId());
        JsonData jsonData = couponFeignService.addNewUserCoupon(request);
        log.info("发放新用户注册优惠券：{},结果:{}", request.toString(), jsonData.toString());

    }
}
