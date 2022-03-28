package com.xdx.sax.domain;

import java.util.Set;

import com.xdx.sax.bo.RawWellDataBO;

public interface ControlSet {

	/**
	 * return the control value for a given marker
	 * In the case of AlloMap this is the control CT values
	 * 
	 * @param markerName name of the marker to look for
	 * @return marker CT value
	 */
	public abstract Double getControlValue(String markerName);

	/**
	 * return the normalized control value for a given marker
	 * In the case of AlloMap this is the control CT values
	 * 
	 * @param markerName name of the marker to look for
	 * @return marker CT value
	 */
	public abstract Double getNormalizedControlValue(String markerName);

	/**
	 * 
	 * @return Set of names
	 */
	public abstract Set<String> getMarkerNames();

	public abstract Set<RawWellDataBO> getRawData();
		
	public abstract long getPlateSetId();

	public abstract long getPlateSectionId();
}