package com.xdx.sax.domain;

import com.xdx.sax.util.ws.PlateRegParameter;
import com.xdx.sax.util.ws.ProcessControlParameter;
import com.xdx.sax.util.ws.Result;

public interface PlateHandler {

	/**
	 * Registers a plate 
	 * @param plateSetId
	 * @param plateNum
	 * @param plateBarcode
	 * @param plateDesignId
	 * 
	 */
	public abstract void registerNewPlate(
			String plateSetId, 
			long plateNum, 
			String testcode,
			String plateBarcode, 
			long plateDesignId,
			boolean processControlsMandatory,
			ProcessControlParameter[] processControls,
			String[] emptySections);

	public Result registerPlates(PlateRegParameter[] parameter);
}