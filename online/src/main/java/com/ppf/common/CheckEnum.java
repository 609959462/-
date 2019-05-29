package com.ppf.common;

public enum CheckEnum {
    CHECK_CHECKE("选中",1),
    CHECK_UNCHECK("未选中",0)
    ;
    private String desc;
    private Integer status;

    CheckEnum() {
    }

    CheckEnum(String desc, Integer status) {
        this.desc = desc;
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
