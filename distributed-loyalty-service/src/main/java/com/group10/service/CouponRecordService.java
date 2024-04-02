package com.group10.service;

import com.group10.dto.CouponRecordDTO;
import com.group10.model.CouponRecordMessage;
import com.group10.request.LockCouponRecordRequest;
import com.group10.util.JsonData;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzf
 * @since 2023-09-09
 */
public interface CouponRecordService{

    Map<String, Object> page(int page, int size);

    CouponRecordDTO findById(long recordId);

    JsonData lockCouponRecords(LockCouponRecordRequest recordRequest);

    boolean releaseCouponRecord(CouponRecordMessage recordMessage);
}
