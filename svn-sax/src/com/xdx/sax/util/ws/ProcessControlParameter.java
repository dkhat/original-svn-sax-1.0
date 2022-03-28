/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.util.ws;

import java.io.Serializable;

/**
 * This class wraps parameters required to query for specific
 * samples (plateset/section combination) in the SAX database
 * It is used in the webservices interfaces to allow for arrays
 * of sample parameters to be passed in for queries.
 * 
 * @author smeier
 *
 */
public class ProcessControlParameter implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private String plateSetId;
	private String plateSectionName;
	private String processControlLotName;
	
	public ProcessControlParameter() { }
	
	/**
	 * @return the plateSetExternalId
	 */
	public String getPlateSetId() {
		return plateSetId;
	}
	/**
	 * @param plateSetExternalId the plateSetExternalId to set
	 */
	public void setPlateSetId(String plateSetId) {
		this.plateSetId = plateSetId;
	}
	/**
	 * @return the plateSectionName
	 */
	public String getPlateSectionName() {
		return plateSectionName;
	}
	/**
	 * @param plateSectionName the plateSectionName to set
	 */
	public void setPlateSectionName(String plateSectionName) {
		this.plateSectionName = plateSectionName;
	}

	/**
	 * @return the processControlLotName
	 */
	public String getProcessControlLotName() {
		return processControlLotName;
	}

	/**
	 * @param processControlLotName the processControlLotName to set
	 */
	public void setProcessControlLotName(String processControlLotName) {
		this.processControlLotName = processControlLotName;
	}	
}
