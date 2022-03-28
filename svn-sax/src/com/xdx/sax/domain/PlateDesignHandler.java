/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.domain;

import com.xdx.sax.util.ws.PlateDesignParam;

/**
 * @author smeier
 *
 */
public interface PlateDesignHandler {

	/**
	 * @param plateDesignId		plate design id
	 * @param plateDesignName	name of the plate design
	 * @param masterMixVol		mastermix volume used for this plate design
	 * @param availableWellVol	total available volume for a well
	 */
	public void updatePlateDesign(
			long plateDesignId, String plateDesignName, Double masterMixVol, Double availableWellVol);

	/**
	 * 
	 * @param plateDesignName	name of the plate design
	 * @param numPlates			number of plates
	 * @param numRowsPerPlate	number of rows per plate
	 * @param numColsPerPlate	number of cloumns per plate
	 * @param masterMixVol		mastermix volume used for this plate design
	 * @param availableWellVol	total available volume for a well
	 * @param details			details of the plate design
	 */
	public void registerPlateDesign(
			String testcode,
			String plateDesignName, int numPlates, 
			int numRowsPerPlate, int numColsPerPlate, 
			Double masterMixVol, Double availableWellVol, PlateDesignParam[] details);
	
}
