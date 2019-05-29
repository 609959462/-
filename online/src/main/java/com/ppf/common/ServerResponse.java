package com.ppf.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;

/**
 *
 * @JsonS是为了不返回有null的值
 * @JsonI则是不显示syccess:true---那个返回的是boolean
 * */


@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> {

	private int status;
	private String msg;
	private T data;
	
	
	 private  ServerResponse() {}
	 
	 private  ServerResponse(int status) {
		this.status=status;
	}
	
	 private  ServerResponse(int status,String msg) {
		
		this.status=status;
		this.msg=msg;
	}
	
	 private  ServerResponse(int status,T data) {
		this.status=status;
		this.data=data;
    	
	}
	
	 private  ServerResponse(int status,String msg,T data) {
		this.status=status;
		this.data=data;
		this.msg=msg;
		
	}
  
	 

	 /**
	  * 判断接口是否返回成功
	  * */
	 @JsonIgnore
	 public   boolean isSucess() {
		 
		 return this.status==ResponseCode.SUCESS;
	 }
	 
	 

	 
	 public  static ServerResponse createServerResponseBySucess() {
		 
		 return new ServerResponse(ResponseCode.SUCESS);
	 }
	 

	 
     public  static ServerResponse createServerResponseBySucess(String msg) {
		 
		 return new ServerResponse(ResponseCode.SUCESS,msg);
	 }
	 

	 
     public  static <T> ServerResponse<T> createServerResponseBySucess(String msg,T data) {
		 
		 return new ServerResponse<T>(ResponseCode.SUCESS,msg,data);
	 }
	 
	 

     public  static ServerResponse createServerResponseByFail(int status) {
		 
		 return new ServerResponse(ResponseCode.ERROR);
	 }
     /**
	  * {status: ,msg:""}
	  * */
     public static ServerResponse createServerResponseByFail(int status,String msg) {
		 
		 return new ServerResponse(status,msg);
	 }

	public  String  obj2str() {
		Gson gson=new Gson();
		String responseText=gson.toJson(this);
		return responseText;
	}

     
     
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
