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
public class PlateRegParameterSimple implements Serializable {
	//
	private static final long serialVersionUID = 1L;
	
	private String externalPlateSetId;
	private int plateNum;
	private String testcode; 
	private String plateBarcode;
	private int plateDesignId;
	private String[] emptySections;
	private boolean processControlsMandatory;
	
	public PlateRegParameterSimple() {
		
	}
	
	/**
	 * @param plateSetId
	 * @param plateNum
	 * @param testcode
	 * @param plateBarcode
	 * @param plateDesignId
	 */
	public PlateRegParameterSimple(String externalPlateSetId, int plateNum, String testcode,
			String plateBarcode, int plateDesignId,
			String[] emptySections) {
		this.externalPlateSetId = externalPlateSetId;
		this.plateNum = plateNum;
		this.testcode = testcode;
		this.plateBarcode = plateBarcode;
		this.plateDesignId = plateDesignId;
		this.emptySections = emptySections;
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
	public void setExternalPlateSetId(String externalPlateSetId) {
		this.externalPlateSetId = externalPlateSetId;
	}
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
	 * @return the testcode
	 */
	public String getTestcode() {
		return testcode;
	}
	/**
	 * @param testcode the testcode to set
	 */
	public void setTestcode(String testcode) {
		this.testcode = testcode;
	}
	/**
	 * @return the plateBarcode
	 */
	public String getPlateBarcode() {
		return plateBarcode;
	}
	/**
	 * @param plateBarcode the plateBarcode to set
	 */
	public void setPlateBarcode(String plateBarcode) {
		this.plateBarcode = plateBarcode;
	}
	/**
	 * @return the plateDesignId
	 */
	public int getPlateDesignId() {
		return plateDesignId;
	}
	/**
	 * @param plateDesignId the plateDesignId to set
	 */
	public void setPlateDesignId(int plateDesignId) {
		this.plateDesignId = plateDesignId;
	}

	/**
	 * @return the emptySections
	 */
	public String[] getEmptySections() {
		return emptySections;
	}

	/**
	 * @param emptySections the emptySections to set
	 */
	public void setEmptySections(String[] emptySections) {
		this.emptySections = emptySections;
	}

	/**
	 * @return the processControlsMandatory
	 */
	public boolean isProcessControlsMandatory() {
		return processControlsMandatory;
	}

	/**
	 * @param processControlsMandatory the processControlsMandatory to set
	 */
	public void setProcessControlsMandatory(boolean processControlsMandatory) {
		this.processControlsMandatory = processControlsMandatory;
	}

}
