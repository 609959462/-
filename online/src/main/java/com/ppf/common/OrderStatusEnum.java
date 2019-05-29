package com.ppf.common;

public enum OrderStatusEnum {
    ORDER_CANCELED(0,"已取消"),
    ORDER_UN_PAY(10,"未付款"),
    ORDER_PAYED(20,"已付款"),
    ORDER_SEND(40,"已发货"),
    ORDER_SUCCESS(50,"交易成功"),
    ORDER_CLOSED(60,"交易关闭")
    ;
    private Integer status;
    private String desc;

    OrderStatusEnum() {
    }

    OrderStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    //遍历这个枚举的状态码
    public static OrderStatusEnum codeOf(Integer status){
        for(OrderStatusEnum orderStatusEnum:values()){
            if(orderStatusEnum.getStatus()==status){
                return orderStatusEnum;
            }
        }
        return null;
    }
}
