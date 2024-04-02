package com.group10.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.group10.config.RabbitMQConfig;
import com.group10.dto.CouponRecordDTO;
import com.group10.enums.BizCodeEnum;
import com.group10.enums.CouponStateEnum;
import com.group10.enums.ProductOrderStateEnum;
import com.group10.enums.StockTaskStateEnum;
import com.group10.exception.BizException;
import com.group10.feign.ProductOrderFeignSerivce;
import com.group10.interceptor.LoginInterceptor;
import com.group10.mapper.CouponRecordMapper;
import com.group10.mapper.CouponTaskMapper;
import com.group10.model.CouponRecordDO;
import com.group10.model.CouponRecordMessage;
import com.group10.model.CouponTaskDO;
import com.group10.model.LoginUser;
import com.group10.request.LockCouponRecordRequest;
import com.group10.service.CouponRecordService;
import com.group10.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zzf
 * @since 2023-09-09
 */
@Service
@Slf4j
public class CouponRecordServiceImpl implements CouponRecordService {

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private CouponTaskMapper couponTaskMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private ProductOrderFeignSerivce orderFeignSerivce;

    @Override
    public Map<String, Object> page(int page, int size) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        //封装分页信息
        Page<CouponRecordDO> pageInfo = new Page<>(page,size);

        IPage<CouponRecordDO> recordDOIPage =  couponRecordMapper.selectPage(pageInfo,new QueryWrapper<CouponRecordDO>()
                .eq("user_id",loginUser.getId()).orderByDesc("created_at"));

        Map<String,Object> pageMap = new HashMap<>(3);

        pageMap.put("total_record",recordDOIPage.getTotal());
        pageMap.put("total_page",recordDOIPage.getPages());
        pageMap.put("current_data",recordDOIPage.getRecords().stream().map(obj-> mapCouponRecord(obj)).collect(Collectors.toList()));

        return pageMap;
    }

    @Override
    public CouponRecordDTO findById(long recordId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        CouponRecordDO couponRecordDO = couponRecordMapper.selectOne(new QueryWrapper<CouponRecordDO>()
                .eq("id",recordId).eq("user_id",loginUser.getId()));

        if(couponRecordDO == null ){return null;}

        return mapCouponRecord(couponRecordDO);
    }

    @Override
    public JsonData lockCouponRecords(LockCouponRecordRequest recordRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String orderOutTradeNo = recordRequest.getOrderOutTradeNo();
        List<Long> lockCouponRecordIds = recordRequest.getLockCouponRecordIds();


        int updateRows = couponRecordMapper.lockUseStateBatch(loginUser.getId(), CouponStateEnum.USED.name(),lockCouponRecordIds);

        List<CouponTaskDO> couponTaskDOList =  lockCouponRecordIds.stream().map(obj->{
            CouponTaskDO couponTaskDO = new CouponTaskDO();
            couponTaskDO.setCreateTime(new Date());
            couponTaskDO.setOutTradeNo(orderOutTradeNo);
            couponTaskDO.setCouponRecordId(obj);
            couponTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
            return couponTaskDO;
        }).collect(Collectors.toList());

        int insertRows = couponTaskMapper.insertBatch(couponTaskDOList);

        log.info("优惠券记录锁定updateRows={}",updateRows);
        log.info("新增优惠券记录task insertRows={}",insertRows);


        if(lockCouponRecordIds.size() == insertRows && insertRows==updateRows){
            //发送延迟消息

            for(CouponTaskDO couponTaskDO : couponTaskDOList){
                CouponRecordMessage couponRecordMessage = new CouponRecordMessage();
                couponRecordMessage.setOutTradeNo(orderOutTradeNo);
                couponRecordMessage.setTaskId(couponTaskDO.getId());

                rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),rabbitMQConfig.getCouponReleaseDelayRoutingKey(),couponRecordMessage);
                log.info("优惠券锁定消息发送成功:{}",couponRecordMessage.toString());
            }


            return JsonData.buildSuccess();
        }else {

            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    @Override
    public boolean releaseCouponRecord(CouponRecordMessage recordMessage) {

        //查询下task是否存
        CouponTaskDO taskDO = couponTaskMapper.selectOne(new QueryWrapper<CouponTaskDO>().eq("id",recordMessage.getTaskId()));

        if(taskDO==null){
            log.warn("工作单不存，消息:{}",recordMessage);
            return true;
        }

        //lock状态才处理
        if(taskDO.getLockState().equalsIgnoreCase(StockTaskStateEnum.LOCK.name())){

            //查询订单状态
            JsonData jsonData = orderFeignSerivce.queryProductOrderState(recordMessage.getOutTradeNo());
            if(jsonData.getCode()==0){
                //正常响应，判断订单状态
                String state = jsonData.getData().toString();
                if(ProductOrderStateEnum.NEW.name().equalsIgnoreCase(state)){
                    //状态是NEW新建状态，则返回给消息队，列重新投递
                    log.warn("订单状态是NEW,返回给消息队列，重新投递:{}",recordMessage);
                    return false;
                }

                //如果是已经支付
                if(ProductOrderStateEnum.PAY.name().equalsIgnoreCase(state)){
                    //如果已经支付，修改task状态为finish
                    taskDO.setLockState(StockTaskStateEnum.FINISH.name());
                    couponTaskMapper.update(taskDO,new QueryWrapper<CouponTaskDO>().eq("id",recordMessage.getTaskId()));
                    log.info("订单已经支付，修改库存锁定工作单FINISH状态:{}",recordMessage);
                    return true;
                }
            }

            //订单不存在，或者订单被取消，确认消息,修改task状态为CANCEL,恢复优惠券使用记录为NEW
            log.warn("订单不存在，或者订单被取消，确认消息,修改task状态为CANCEL,恢复优惠券使用记录为NEW,message:{}",recordMessage);
            taskDO.setLockState(StockTaskStateEnum.CANCEL.name());

            couponTaskMapper.update(taskDO,new QueryWrapper<CouponTaskDO>().eq("id",recordMessage.getTaskId()));
            //恢复优惠券记录是NEW状态
            couponRecordMapper.updateState(taskDO.getCouponRecordId(),CouponStateEnum.NEW.name());

            return true;
        }else {
            log.warn("工作单状态不是LOCK,state={},消息体={}",taskDO.getLockState(),recordMessage);
            return true;
        }
    }

    private CouponRecordDTO mapCouponRecord(CouponRecordDO couponRecordDO) {


        CouponRecordDTO CouponRecordDTO = new CouponRecordDTO();
        BeanUtils.copyProperties(couponRecordDO,CouponRecordDTO);
        return CouponRecordDTO;
    }
}
