package com.group10.service;

import com.group10.request.AddressRequest;
import com.group10.dto.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO detail(long id);

    void add(AddressRequest addressRequest);

    int del(int addressId);

    List<AddressDTO> listUserAllAddress();
}
