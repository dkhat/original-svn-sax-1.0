/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.util.ws;

import java.io.Serializable;

import com.xdx.sax.bo.AssayOutlierResultBO;

/**
 * @author smeier
 *
 */
public class ExtraFlagResult implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private String flagType;
	private Boolean pass;

	public ExtraFlagResult() {
	}
	
	/**
	 * @param flagType
	 * @param pass
	 */
	public ExtraFlagResult(String flagType, Boolean pass) {
		this.flagType = flagType;
		this.pass = pass;
	}

	/**
	 * @return the flagType
	 */
	public String getFlagType() {
		return flagType;
	}
	/**
	 * @param flagType the flagType to set
	 */
	public void setFlagType(String flagType) {
		this.flagType = flagType;
	}
	/**
	 * @return the pass
	 */
	public Boolean isPass() {
		return pass;
	}
	/**
	 * @param pass the pass to set
	 */
	public void setPass(Boolean pass) {
		this.pass = pass;
	}
	
}
