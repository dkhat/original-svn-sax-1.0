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
public class PlateRegParameter implements Serializable {
	//
	private static final long serialVersionUID = 1L;
	
	private String externalPlateSetId;
	private int plateNum;
	private String testcode; 
	private String plateBarcode;
	private int plateDesignId;
	private String[] emptySections;
	private boolean processControlsMandatory;
	private ProcessControlParameter[] processControls;
	
	public PlateRegParameter() {
		
	}
	
	/**
	 * @param plateSetId
	 * @param plateNum
	 * @param testcode
	 * @param plateBarcode
	 * @param plateDesignId
	 */
	public PlateRegParameter(String externalPlateSetId, int plateNum, String testcode,
			String plateBarcode, int plateDesignId,
			String[] emptySections, ProcessControlParameter[] processControls) {
		this.externalPlateSetId = externalPlateSetId;
		this.plateNum = plateNum;
		this.testcode = testcode;
		this.plateBarcode = plateBarcode;
		this.plateDesignId = plateDesignId;
		this.emptySections = emptySections;
		this.processControls = processControls;
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

	/**
	 * @return the processControls
	 */
	public ProcessControlParameter[] getProcessControls() {
		return processControls;
	}

	/**
	 * @param processControls the processControls to set
	 */
	public void setProcessControls(ProcessControlParameter[] processControls) {
		this.processControls = processControls;
	}

}
