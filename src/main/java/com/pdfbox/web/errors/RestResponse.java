package com.pdfbox.web.errors;

import java.io.Serializable;

public class RestResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2643010244973482720L;
	
	private int code;
	private String message;
	private Object data;
	public static final int OK = 0;
	public static final int GENERIC_ERROR = 500;
	public static final int BAD_REQUEST_ERROR = 400;
	public static final String BAD_REQUEST_MSG = "Requête incorrecte";
	
	public RestResponse(){
		this.code = 0;
		this.message = "";
	}
	
	public RestResponse(int code, String message, Object data){
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	public RestResponse(int code, String message){
		this.code = code;
		this.message = message;
	}
	
	public RestResponse(RestResponse origine){
		this.code = origine.getCode();
		this.message = origine.getMessage();
		this.data = origine.getData();
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RestResponse [code=" + code + ", message=" + message + ", data=" + data + "]";
	}

}
