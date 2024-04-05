package com.group10.feign;

import com.group10.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "distributed-user-service")
public interface UserFeignService {


    /**
     * Query the user's address, the interface itself has level prevention privileges
     * @param addressId
     * @return
     */
    @GetMapping("/api/address/v1//find/{address_id}")
    JsonData detail(@PathVariable("address_id")long addressId);

}
