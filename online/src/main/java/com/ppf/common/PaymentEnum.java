package com.ppf.common;

public enum  PaymentEnum {
    ONLINE(1,"在线支付"),
    OFFLINE(2,"货到付款")
    ;
    private Integer code;
    private String desc;

    PaymentEnum() {
    }

    PaymentEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public static PaymentEnum codeOf(Integer code){
        for(PaymentEnum paymentEnum:values()){
            if(paymentEnum.getCode()==code){
                return paymentEnum;
            }
        }
        return null;
    }
}
