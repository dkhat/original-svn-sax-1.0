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
public class SampleParameter implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private String plateSetName;
	private String plateSectionName;
	private String processControlLotName;
	
	/**
	 * @return the plateSetExternalId
	 */
	public String getPlateSetName() {
		return plateSetName;
	}
	/**
	 * @param plateSetExternalId the plateSetExternalId to set
	 */
	public void setPlateSetName(String plateSetExternalId) {
		this.plateSetName = plateSetExternalId;
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
	 * @param processControlLotName the processControlLotName to set
	 */
	public void setProcessControlLotName(String processControlLotName) {
		this.processControlLotName = processControlLotName;
	}
	/**
	 * @return the processControlLotName
	 */
	public String getProcessControlLotName() {
		return processControlLotName;
	}	
}
