package com.ppf.service;

import com.ppf.common.ServerResponse;
import com.ppf.pojo.Shipping;

public interface IShipping {
    public ServerResponse add(Integer userId,Shipping shipping);
    public ServerResponse del(Integer userId, Integer shippingId);
}
