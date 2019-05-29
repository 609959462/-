package com.ppf.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.ppf.alipay.Main;
import com.ppf.common.*;
import com.ppf.dao.*;
import com.ppf.pojo.*;
import com.ppf.service.ICart;
import com.ppf.service.IOderService;
import com.ppf.service.IProductService;
import com.ppf.utils.BigDecimalUtils;
import com.ppf.utils.DateUtils;
import com.ppf.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderService implements IOderService {
    @Autowired
    ICart cart;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    IProductService productService;
    @Autowired
    ShippingMapper shippingMapper;
    @Value("${online.imageHost}")
    private  String imageHost;
    @Autowired
    PayInfoMapper payInfoMapper;
    @Override
    public ServerResponse creatOrder(Integer userId, Integer shippingId) {
        //非空校验
        if (shippingId==null) {
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"收货地址不能为空");
        }
        //拿到购物车中已勾选的List<Cart>
       ServerResponse<List<Cart>> serverResponse=cart.cartListChecked(userId);
        List<Cart> cartList=serverResponse.getData();
        if(serverResponse.getData()==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"购物车为空或未选中商品");
        }
        //将已勾选的List<Cart>---->List<OrderItem>
        ServerResponse<List<OrderItem>> serverResponse1=getCartOrderItem(userId,cartList);
        if(!serverResponse1.isSucess()){
            return ServerResponse.createServerResponseByFail(serverResponse1.getStatus(),serverResponse1.getMsg());
        }
        List<OrderItem> orderItemList=serverResponse1.getData();
        //创建Order
        ServerResponse orderS=creatOr(userId,shippingId,orderItemList);
        if(!orderS.isSucess()){
            return ServerResponse.createServerResponseByFail(orderS.getStatus(),orderS.getMsg());
        }
        //保存订单明细
        Order order=(Order)orderS.getData();
        ServerResponse serverResponse2=saveOrderItems(orderItemList,order);
        if(!serverResponse2.isSucess()){
            return serverResponse2;
        }
       //扣库存
        reduceStock(orderItemList);
        //清空购物车--批量删除
        ServerResponse serverResponse3=cart.clearCart(cartList);
        if(!serverResponse3.isSucess()){
          return   serverResponse3;
        }
        //返回前端需要的OrderVO
        OrderVO orderVO=assembleOrderVO(order,orderItemList,shippingId);
        return ServerResponse.createServerResponseBySucess("成功",orderVO);
    }



    //转换OrderItem的方法
    private  ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){

        if(cartList==null||cartList.size()==0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"购物车空");
        }
        List<OrderItem> orderItemList= Lists.newArrayList();

        for(Cart cart:cartList){

            OrderItem orderItem=new OrderItem();
            orderItem.setUserId(userId);

            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
            if(product==null){
                return  ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"id为"+cart.getProductId()+"的商品不存在");
            }
            if(product.getStatus()!= ProductStatusEnum.PRODUCT_ONLINE.getStatus()){//商品下架
                return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"id为"+product.getId()+"的商品已经下架");
            }
            if(product.getStock()<cart.getQuantity()){//库存不足
                return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"id为"+product.getId()+"的商品库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));

            orderItemList.add(orderItem);
        }
        return  ServerResponse.createServerResponseBySucess(null,orderItemList);
    }
    //生成OrderItemVO
    private OrderItemVO assembleOrderItemVO (OrderItem orderItem){
        OrderItemVO orderItemVO=new OrderItemVO();
        if(orderItem!=null){
            orderItemVO.setQuantity(orderItem.getQuantity());
            orderItemVO.setCreateTime(DateUtils.dateToStr(orderItem.getCreateTime()));
            orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVO.setOrderNo(orderItem.getOrderNo());
            orderItemVO.setProductId(orderItem.getProductId());
            orderItemVO.setProductImage(orderItem.getProductImage());
            orderItemVO.setProductName(orderItem.getProductName());
            orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        }
        return orderItemVO;
    }
    //保存List<orderItemList>到数据库
    private ServerResponse saveOrderItems(List<OrderItem> orderItemList,Order order){
        //orderList需要编号
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //插入集合到数据库--批量插入
        int result=orderItemMapper.insertBatch(orderItemList);
        if(result!=orderItemList.size()){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"有些数据没有插入成功");
        }
        return ServerResponse.createServerResponseBySucess();
    }
    //生成订单Order（内部的属性）同时保存到DB
        private ServerResponse creatOr(Integer userId,Integer shipping,List<OrderItem> orderItemList){
            Order order=new Order();
            order.setOrderNo(generateOrderNO());
            order.setUserId(userId);
            order.setShippingId(shipping);
            order.setPayment(getOrderTotalPrice(orderItemList));
            order.setPostage(0);
            order.setPaymentType(PaymentEnum.ONLINE.getCode());
            order.setStatus(OrderStatusEnum.ORDER_UN_PAY.getStatus());
            int result=orderMapper.insert(order);
            if(result<=0){
                return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"订单保存失败");
            }
            return ServerResponse.createServerResponseBySucess("成功",order);
        }
        /**
         * 生成订单编号
         * */
        private Long generateOrderNO(){
            return System.currentTimeMillis()+new Random().nextInt(100);
        }
        /**
         * 订单总价格
         * */
        private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
           BigDecimal orderTotalPrice=new BigDecimal("0");
           for(OrderItem orderItem:orderItemList){
               orderTotalPrice=BigDecimalUtils.add(orderItem.getTotalPrice().doubleValue(),orderTotalPrice.doubleValue());
           }
           return orderTotalPrice;
        }
        //扣库存
    private ServerResponse reduceStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem:orderItemList){
           Integer productId=orderItem.getProductId();
           ServerResponse<ProductDetailVo> serverResponse=productService.detail(productId);
            ProductDetailVo product=serverResponse.getData();
           int stock=product.getStock()-orderItem.getQuantity();
           ServerResponse<Product> serverResponse1=productService.reduceStock(productId,stock);
           if(!serverResponse1.isSucess()){
               return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"扣除库存失败");
           }
        }

        return ServerResponse.createServerResponseBySucess();
    }
    //转化OrderVO
    private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItemList, Integer shippingId){
        OrderVO orderVO=new OrderVO();

        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO= assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVoList(orderItemVOList);
        orderVO.setImageHost(imageHost);
        Shipping shipping= shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping!=null){
            orderVO.setShippingId(shippingId);
            ShippingVO shippingVO= assmbleShippingVO(shipping);
            orderVO.setShippingVo(shippingVO);
            orderVO.setReceiverName(shipping.getReceiverName());
        }

        orderVO.setStatus(order.getStatus());
        //根据状态码取描述codeOf
        OrderStatusEnum orderStatusEnum=OrderStatusEnum.codeOf(order.getStatus());
        System.out.println(order.getStatus());
        if(orderStatusEnum!=null){
            orderVO.setStatusDesc(orderStatusEnum.getDesc());
        }

        orderVO.setPostage(0);
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        PaymentEnum paymentEnum=PaymentEnum.codeOf(order.getPaymentType());
        if(paymentEnum!=null){
            orderVO.setPaymentTypeDesc(paymentEnum.getDesc());
        }
        orderVO.setOrderNo(order.getOrderNo());

        return orderVO;
    }

    private ShippingVO assmbleShippingVO(Shipping shipping){
        ShippingVO shippingVO=new ShippingVO();

        if(shipping!=null){
            shippingVO.setReceiverAddress(shipping.getReceiverAddress());
            shippingVO.setReceiverCity(shipping.getReceiverCity());
            shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVO.setReceiverMobile(shipping.getReceiverMobile());
            shippingVO.setReceiverName(shipping.getReceiverName());
            shippingVO.setReceiverPhone(shipping.getReceiverPhone());
            shippingVO.setReceiverProvince(shipping.getReceiverProvince());
            shippingVO.setReceiverZip(shipping.getReceiverZip());
        }
        return shippingVO;
    }
    /**
     *
     * */
    @Override
    public ServerResponse pay(Integer userId, Long ordeerNo) {
        if(ordeerNo==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"orderNo必传");
        }
        Order order=orderMapper.findOrderByOrderNo(ordeerNo);
        if(order==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"订单不存在");
        }

        return   pay(order);
    }

    @Override
    public String callback(Map<String, String> requestParams) {
       //根据支付宝回调的参数来进行剩下的业务判断
           // 订单号
       String orderNo=requestParams.get("out_trade_no");
            //支付宝订单号
       String trade_no=requestParams.get("trade_no");
            //支付状态
       String trade_status=requestParams.get("trade_status");
            //支付时间
       String pay_time=requestParams.get("gmt_create");
       //进行业务判断
        Order order=orderMapper.findOrderByOrderNo(Long.parseLong(orderNo));
        if(order==null){
            return "fail";
        }
        if(trade_status.equals("TRADE_SUCCESS")){

            //保存到数据库
            order.setOrderNo(Long.parseLong(orderNo));
            order.setStatus(OrderStatusEnum.ORDER_PAYED.getStatus());
            order.setPaymentTime(DateUtils.strToDate(pay_time));
            int result=orderMapper.updateOrder(order);
            if(result<=0){
                return "fail";
            }
        }
        //保存支付信息
        PayInfo payInfo=new PayInfo();
        payInfo.setOrderNo(Long.parseLong(orderNo));
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(PaymentEnum.ONLINE.getCode());
        payInfo.setPlatformNumber(trade_no);
        payInfo.setPlatformStatus(trade_status);

        int result=payInfoMapper.insert(payInfo);
        if(result<=0){
            return "fail";
        }

        return "success";
    }

    //查询订单状态
    @Override
    public ServerResponse query(Long orderNo) {
        Order result=orderMapper.selectAll(orderNo);
        if(result==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"该用户并没有该订单,查询无效");
        }
        if(result.getStatus()==OrderStatusEnum.ORDER_PAYED.getStatus()){
            return ServerResponse.createServerResponseBySucess(null,true);
        }
        return ServerResponse.createServerResponseByFail(ResponseCode.ERROR);
    }

    private static Log log = LogFactory.getLog(Main.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService   tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
    // 测试当面付2.0生成支付二维码
    public ServerResponse pay(Order order ) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(order.getOrderNo());
        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "【睿乐购】当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = order.getPayment().toString();

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品共"+order.getPayment();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";
        //根据订单编号查询orderItem
            List<OrderItem> orderItemList=orderItemMapper.findOrderItemByOrderNo(order.getOrderNo());
            if(orderItemList==null||orderItemList.size()==0){
                return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"没有可购买的商品");
            }
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
            for(OrderItem orderItem:orderItemList){
                GoodsDetail goods=GoodsDetail.newInstance(String.valueOf(orderItem.getProductId()),orderItem.getProductName(),orderItem.getCurrentUnitPrice().intValue(),
                        orderItem.getQuantity());
                goodsDetailList.add(goods);
            }
        // 商品明细列表，需填写购买商品详细信息，
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://a3xqaa.natappfree.cc/order/callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
                String filePath = String.format("d:/upload/qr-%s.png",
                        response.getOutTradeNo());
                log.info("filePath:" + filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                PayVO payVO=new PayVO(order.getOrderNo(),imageHost+"qr-"+ response.getOutTradeNo()+".png");
                return ServerResponse.createServerResponseBySucess(null,payVO);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }  return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"下单失败");
    }

}
