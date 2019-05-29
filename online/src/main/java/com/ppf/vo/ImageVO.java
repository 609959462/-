package com.ppf.vo;

public class ImageVO {
    private String uri;//图片名字
    private String url;//访问图片完整地址

    public ImageVO(String uri, String url) {
        this.uri = uri;
        this.url = url;
    }
    public ImageVO() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
