package com.ppf.service.impl;

import com.ppf.common.ResponseCode;
import com.ppf.common.ServerResponse;
import com.ppf.dao.ShippingMapper;
import com.ppf.pojo.Shipping;
import com.ppf.service.IShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingImpl implements IShipping {
    @Autowired
    ShippingMapper shippingMapper;
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int result=shippingMapper.insert(shipping);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"添加失败");
        }
        Integer id=shipping.getId();
        return ServerResponse.createServerResponseBySucess("新建地址成功",id);
    }
    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {

        //step1:参数非空校验
        if(shippingId==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数错误");
        }
        //setp2:删除
        int result=shippingMapper.deleteByUserIdAndShippingId(userId,shippingId);
        //step3:返回结果
        if(result>0){
            return ServerResponse.createServerResponseBySucess();
        }

        return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"删除失败");
    }
}
