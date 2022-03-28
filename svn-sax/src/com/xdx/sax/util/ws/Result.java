package com.xdx.sax.util.ws;

import java.io.Serializable;

public class Result implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private boolean ok;
	private String errorMessage;
	
	public Result() {
		this.ok = false;
		this.errorMessage="";
	}

	public Result(boolean isOk, String errorMessage) {
		this.ok = isOk;
		this.errorMessage=errorMessage;
	}

	public boolean getOk() {
		return ok;
	}

	public void setOk(boolean isOk) {
		this.ok = isOk;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
