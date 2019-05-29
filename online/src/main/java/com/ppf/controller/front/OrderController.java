package com.ppf.controller.front;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.ppf.common.ServerResponse;
import com.ppf.pojo.User;
import com.ppf.service.IOderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/order/")
public class OrderController {
   @Autowired
    HttpSession session;
   @Autowired
   IOderService iOderService;
    @RequestMapping("create.do")
    public ServerResponse creatOrder(Integer shippingId){
        User user=(User)session.getAttribute("user");
        Integer userId =user.getId();
        return iOderService.creatOrder(userId,shippingId);
    }
    @RequestMapping("pay.do")
    public ServerResponse pay(Long orderNo){
        User user=(User)session.getAttribute("user");
        Integer userId =user.getId();
        return iOderService.pay(userId,orderNo);
    }
    //支付宝回调，传回来是否成功等信息
    @RequestMapping("callback.do")
    public String ali_callback(HttpServletRequest request){
        Map<String,String[]> callbackParams=request.getParameterMap();
        //为了下面的rsaCheckV2方法--参数需要，要把数组转换成string
        Map<String,String> signParam= Maps.newHashMap();
        Iterator<String> iterator=callbackParams.keySet().iterator();
        while (iterator.hasNext()){
            String key=iterator.next();
            String[] values=callbackParams.get(key);
            System.out.println("key="+key+"value="+values);
            StringBuffer stringBuffer=new StringBuffer();
            if(values!=null&&values.length>0){
                for(int i=0;i<values.length;i++){
                    stringBuffer.append(values[i]);
                    if(i!=values.length-1){
                        stringBuffer.append(",");
                    }
                }

            }
            signParam.put(key,stringBuffer.toString());
        }


        //为保证只有支付宝可以调用此接口--验签
        try {
            signParam.remove("sign_type");//需要删掉这个
           boolean result= AlipaySignature.rsaCheckV2(signParam,Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

           //如果为真那么验证通过
           if(result){
               iOderService.callback(signParam);
           }else {
               return "fail";
           }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return "未知错误";
    }
    //查询订单状态
    @RequestMapping("query_order_pay_status.do")
    public ServerResponse queryOrder(Long orderNo){

        return iOderService.query(orderNo);
    }
}
