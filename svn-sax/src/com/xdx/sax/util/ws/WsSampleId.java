/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.util.ws;

import java.io.Serializable;

/**
 * @author smeier
 *
 */
public class WsSampleId implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private String externalPlateSetId;
	private long plateNum;
	private String plateSection;
	
	public WsSampleId () {}
	
	public WsSampleId (String externalPlateSetId, long plateNum, String plateSection) {
		this.externalPlateSetId = externalPlateSetId;
		this.plateNum = plateNum;
		this.plateSection = plateSection;
	}
	
	/**
	 * @return the plateSetId
	 */
	public String getExternalPlateSetId() {
		return externalPlateSetId;
	}
	/**
	 * @param plateSetId the plateSetId to set
	 */
	public void setExternalPlateSetId(String plateSetId) {
		this.externalPlateSetId = plateSetId;
	}
	/**
	 * @return the plateSection
	 */
	public String getPlateSection() {
		return plateSection;
	}
	/**
	 * @param plateSection the plateSection to set
	 */
	public void setPlateSection(String plateSection) {
		this.plateSection = plateSection;
	}
	/**
	 * @return the plateNum
	 */
	public long getPlateNum() {
		return plateNum;
	}
	/**
	 * @param plateNum the plateNum to set
	 */
	public void setPlateNum(long plateNum) {
		this.plateNum = plateNum;
	}
	
}
