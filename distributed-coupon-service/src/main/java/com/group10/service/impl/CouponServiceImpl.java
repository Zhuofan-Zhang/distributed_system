package com.group10.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group10.dto.CouponDTO;
import com.group10.enums.BizCodeEnum;
import com.group10.enums.CouponCategoryEnum;
import com.group10.enums.CouponPublishEnum;
import com.group10.enums.CouponStateEnum;
import com.group10.exception.BizException;
import com.group10.interceptor.LoginInterceptor;
import com.group10.mapper.CouponMapper;
import com.group10.mapper.CouponRecordMapper;
import com.group10.model.CouponDO;
import com.group10.model.CouponRecordDO;
import com.group10.model.LoginUser;
import com.group10.request.NewUserCouponRequest;
import com.group10.service.CouponService;
import com.group10.util.CommonUtil;
import com.group10.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Map<String, Object> getCouponListWithPagination(int page, int size) {

        Page<CouponDO> pageInfo = new Page<>(page, size);

        IPage<CouponDO> couponDOIPage = couponMapper.selectPage(pageInfo, new QueryWrapper<CouponDO>()
                .eq("publish", CouponPublishEnum.PUBLISH)
                .eq("category", CouponCategoryEnum.PROMOTION)
                .orderByDesc("created_at"));
        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", couponDOIPage.getTotal());
        pageMap.put("total_page", couponDOIPage.getPages());
        pageMap.put("current_data", couponDOIPage.getRecords().stream().map(obj -> processObj(obj)).collect(Collectors.toList()));


        return pageMap;
    }

    @Transactional(rollbackFor=Exception.class,propagation= Propagation.REQUIRED)
    public JsonData addCoupon(long couponId, CouponCategoryEnum category) {

        String lockKey = "lock:coupon:" + couponId;
        RLock rLock = redissonClient.getLock(lockKey);

        rLock.lock();


        log.info("领劵接口加锁成功:{}", Thread.currentThread().getId());
        try {
            validateAndUpdateCouponRecord(couponId, category);
        } finally {
            rLock.unlock();
            log.info("lock released");
        }

        return JsonData.buildCodeAndMsg(0, "add coupon successfully");

    }


    @Transactional(rollbackFor=Exception.class,propagation=Propagation.REQUIRED)
    @Override
    public JsonData issueNewUserCoupon(NewUserCouponRequest newUserCouponRequest) {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(newUserCouponRequest.getUserId());
        loginUser.setName(newUserCouponRequest.getName());
        LoginInterceptor.threadLocal.set(loginUser);

        List<CouponDO> couponDOList = couponMapper.selectList(new QueryWrapper<CouponDO>()
                .eq("category",CouponCategoryEnum.NEW_USER.name()));

        for(CouponDO couponDO : couponDOList){
            addCoupon(couponDO.getId(),CouponCategoryEnum.NEW_USER);

        }

        return JsonData.buildSuccess();
    }

    private void validateAndUpdateCouponRecord(long couponId, CouponCategoryEnum category) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();


        CouponDO couponDO = couponMapper.selectOne(new QueryWrapper<CouponDO>()
                .eq("id", couponId)
                .eq("category", category.name()));

        this.validateCoupon(couponDO, loginUser.getId());

        CouponRecordDO couponRecordDO = new CouponRecordDO();
        BeanUtils.copyProperties(couponDO, couponRecordDO);
        couponRecordDO.setCreatedAt(new Date());
        couponRecordDO.setUsageState(CouponStateEnum.NEW.name());
        couponRecordDO.setUserId(loginUser.getId());
        couponRecordDO.setUserName(loginUser.getName());
        couponRecordDO.setCouponId(couponId);
        couponRecordDO.setId(null);

        int rows = couponMapper.reduceStock(couponId);

        if (rows == 1) {

            couponRecordMapper.insert(couponRecordDO);

        } else {
            log.warn("Issue coupon failed:{},USER:{}", couponDO, loginUser);

            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }
    }

    private void validateCoupon(CouponDO couponDO, Long userId) {

        if (couponDO == null) {
            throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
        }

        //库存是否足够
        if (couponDO.getRemaining() <= 0) {
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }

        //判断是否是否发布状态
        if (!couponDO.getPublish().equals(CouponPublishEnum.PUBLISH.name())) {
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }

        //是否在领取时间范围
        long time = CommonUtil.getCurrentTimestamp();
        long start = couponDO.getValidFrom().getTime();
        long end = couponDO.getValidUntil().getTime();
        if (time < start || time > end) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }

        //用户是否超过限制
        int recordNum = couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
                .eq("coupon_id", couponDO.getId())
                .eq("user_id", userId));

        if (recordNum >= couponDO.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }


    }

    private CouponDTO processObj(CouponDO couponDO) {
        CouponDTO couponDTO = new CouponDTO();
        BeanUtils.copyProperties(couponDO, couponDTO);
        return couponDTO;
    }
}
