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
public class ProcessControlItem implements Serializable {

	//
	private static final long serialVersionUID = 1L;

	private String markerName;
	private Double expectedValue;
	private Double standardDeviation;
	
	public ProcessControlItem() {}
	
	/**
	 * @param lotName
	 * @param assayName
	 * @param expectedValue
	 */
	public ProcessControlItem(String assayName, Double expectedValue, Double standardDeviation) {
		this.markerName = assayName;
		this.expectedValue = expectedValue;
		this.standardDeviation = standardDeviation;
	}

	/**
	 * @return the assayName
	 */
	public String getMarkerName() {
		return markerName;
	}

	/**
	 * @param assayName the assayName to set
	 */
	public void setMarkerName(String assayName) {
		this.markerName = assayName;
	}

	/**
	 * @return the expectedValue
	 */
	public Double getExpectedValue() {
		return expectedValue;
	}

	/**
	 * @param expectedValue the expectedValue to set
	 */
	public void setExpectedValue(Double expectedValue) {
		this.expectedValue = expectedValue;
	}

	/**
	 * @return the standardDeviation
	 */
	public Double getStandardDeviation() {
		return standardDeviation;
	}

	/**
	 * @param standardDeviation the standardDeviation to set
	 */
	public void setStandardDeviation(Double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	
	
}
