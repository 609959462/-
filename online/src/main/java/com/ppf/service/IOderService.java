package com.ppf.service;

import com.ppf.common.ServerResponse;

import java.util.Map;

public interface IOderService {
    public ServerResponse creatOrder(Integer userId,Integer shippingId);
    public ServerResponse pay(Integer userId,Long ordeerNo);
    /**
     * 支付回调接口
     * */
    public String callback(Map<String,String>requestParams);
    public ServerResponse query(Long orderNo);
}
