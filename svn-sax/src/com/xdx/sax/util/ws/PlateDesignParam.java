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
public class PlateDesignParam implements Serializable {

	//
	private static final long serialVersionUID = 1L;

	private int plateNum;
	private int rowNum;
	private int colNum;
	private String markerName;
	private String sectionName;
	private String wellType;
	
	/**
	 * @return the plateNum
	 */
	public int getPlateNum() {
		return plateNum;
	}
	/**
	 * @param plateNum the plateNum to set
	 */
	public void setPlateNum(int plateNum) {
		this.plateNum = plateNum;
	}
	/**
	 * @return the rowNum
	 */
	public int getRowNum() {
		return rowNum;
	}
	/**
	 * @param rowNum the rowNum to set
	 */
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	/**
	 * @return the colNum
	 */
	public int getColNum() {
		return colNum;
	}
	/**
	 * @param colNum the colNum to set
	 */
	public void setColNum(int colNum) {
		this.colNum = colNum;
	}
	/**
	 * @return the markerName
	 */
	public String getMarkerName() {
		return markerName;
	}
	/**
	 * @param markerName the markerName to set
	 */
	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}
	/**
	 * @return the sectionName
	 */
	public String getSectionName() {
		return sectionName;
	}
	/**
	 * @param sectionName the sectionName to set
	 */
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	/**
	 * @return the wellType
	 */
	public String getWellType() {
		return wellType;
	}
	/**
	 * @param wellType the wellType to set
	 */
	public void setWellType(String wellType) {
		this.wellType = wellType;
	}
	
}
