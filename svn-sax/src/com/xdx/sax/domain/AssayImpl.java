/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.domain;

import java.util.Set;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.AggMarkerResultBO;
import com.xdx.sax.bo.AggregateBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.RawWellDataBO;

/**
 * A representation of an <b>Assay</b>.
 *
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 * @see com.xdx.sax.domain.Assay
 * 
 */
public class AssayImpl extends BusinessObject implements Assay {

	private Set<RawWellDataBO> rawData = null;
	private AggMarkerResultBO result = null;

	/**
	 * @param rawData	- raw data for this assay
	 * @param result	- aggregated results for this assay
	 */
	public AssayImpl(Set<RawWellDataBO> rawData, AggMarkerResultBO result) {
		this.rawData = rawData;
		this.result = result;
	}
	
	// GETTERS and SETTERS

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.Assay#getMarkers()
	 */
	public Set<MarkerBO> getMarkers() {
		return this.result.getMarkers();
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.Assay#getAggregate()
	 */
	public AggregateBO getAggregate() {
		return this.result.getAggregate();
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.Assay#getRawData()
	 */
	public Set<RawWellDataBO> getRawData() {
		return this.rawData;
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.Assay#getResult()
	 */
	public AggMarkerResultBO getResult() {
		return this.result;
	}

}
