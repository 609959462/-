package com.ppf.controller.front;

import com.ppf.common.ServerResponse;
import com.ppf.pojo.Shipping;
import com.ppf.pojo.User;
import com.ppf.service.IShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/shipping/")
public class shippingController {
    @Autowired
    HttpSession session;
   @Autowired
   IShipping iShipping;
    @RequestMapping("add.do")
    public ServerResponse addShipping(Shipping shipping){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return  iShipping.add(userId,shipping);
}
//@RequestMapping("del.do")
//    public ServerResponse deleteShipping(Integer shippingId){
//
//}
}
