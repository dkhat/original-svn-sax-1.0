/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.util.ws;

import java.io.Serializable;

import com.xdx.sax.to.PlateDesignTO;

/**
 * @author smeier
 *
 */
public class PlateDesignData implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private PlateDesignTO plateDesign;
	private String[] plateSectionNames;
	
	public PlateDesignData () {}
	
	public PlateDesignData(PlateDesignTO plateDesign, String[] plateSectionNames) {
		this.plateDesign = plateDesign;
		this.plateSectionNames = plateSectionNames;
	}

	/**
	 * @return the plateDesign
	 */
	public PlateDesignTO getPlateDesign() {
		return plateDesign;
	}

	/**
	 * @param plateDesign the plateDesign to set
	 */
	public void setPlateDesign(PlateDesignTO plateDesign) {
		this.plateDesign = plateDesign;
	}

	/**
	 * @return the plateSectionNames
	 */
	public String[] getPlateSectionNames() {
		return plateSectionNames;
	}

	/**
	 * @param plateSectionNames the plateSectionNames to set
	 */
	public void setPlateSectionNames(String[] plateSectionNames) {
		this.plateSectionNames = plateSectionNames;
	}
}
