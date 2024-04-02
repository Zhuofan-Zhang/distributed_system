package com.group10.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.group10.request.AddressRequest;
import com.group10.service.AddressService;
import com.group10.dto.AddressDTO;
import com.group10.enums.AddressStatusEnum;
import com.group10.interceptor.LoginInterceptor;
import com.group10.mapper.AddressMapper;
import com.group10.model.AddressDO;
import com.group10.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public AddressDTO detail(long id) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        AddressDO addressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id", id).eq("user_id", loginUser.getId()));

        if (addressDO == null) {
            return null;
        }
        AddressDTO addressDTO = new AddressDTO();
        BeanUtils.copyProperties(addressDO, addressDTO);

        return addressDTO;
    }

    @Override
    public void add(AddressRequest addressRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        AddressDO addressDO = new AddressDO();
        addressDO.setCreatedAt(new Date());
        addressDO.setUserId(loginUser.getId());

        BeanUtils.copyProperties(addressRequest, addressDO);


        //是否有默认收货地址
        if (addressDO.getDefaultStatus() == AddressStatusEnum.DEFAULT_STATUS.getStatus()) {
            //查找数据库是否有默认地址
            AddressDO defaultAddressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>()
                    .eq("user_id", loginUser.getId())
                    .eq("default_status", AddressStatusEnum.DEFAULT_STATUS.getStatus()));

            if (defaultAddressDO != null) {
                //修改为非默认收货地址
                defaultAddressDO.setDefaultStatus(AddressStatusEnum.COMMON_STATUS.getStatus());
                addressMapper.update(defaultAddressDO, new QueryWrapper<AddressDO>().eq("id", defaultAddressDO.getId()));
            }
        }

        int rows = addressMapper.insert(addressDO);

        log.info("created new address:rows={},data={}", rows, addressDO);
    }

    @Override
    public int del(int addressId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        return addressMapper.delete(new QueryWrapper<AddressDO>().eq("id", addressId).eq("user_id", loginUser.getId()));
    }

    @Override
    public List<AddressDTO> listUserAllAddress() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        List<AddressDO> list = addressMapper.selectList(new QueryWrapper<AddressDO>().eq("user_id", loginUser.getId()));

        return list.stream().map(obj -> {
            AddressDTO addressDTO = new AddressDTO();
            BeanUtils.copyProperties(obj, addressDTO);
            return addressDTO;
        }).collect(Collectors.toList());
    }
}
