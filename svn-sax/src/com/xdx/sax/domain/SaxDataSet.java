/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.domain;

import java.util.Set;

import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.bo.RawWellDataBO;

/**
 * A SaxDataSet comprises all the necessary data related to
 * one particular 'test' in the lab. It holds the aggregated data
 * for each assay as well as the calculated summary data.
 * 
 * The SaxDataSet does NOT include the underlying raw data.
 * 
 * A SaxDataSet can only be constructed from already existing data
 * in the database. The constructor requires valid ids for the 
 * plate set and plate section that identify this data set.
 * 
 * A SaxDataSet can persist its data after modification. The
 * persist() method must be explicitly called to trigger
 * updates to the database. 
 * 
  * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 *
 */
public interface SaxDataSet {

	/**
	 * @return the assays
	 */
	public Set<Assay> getAssays();

	/**
	 * 
	 * @param markerName name of the marker
	 * @return the assay (or null if not available)
	 */
	public Assay getAssay(String markerName);
	
	/**
	 * @return the plateSetId
	 */
	public PlateSetBO getPlateSet();

	/**
	 * @return the plateSetId
	 */
	public PlateSectionBO getPlateSection();

	/**
	 * @return the score
	 */
	public double getScore();

	/**
	 * @param score the score to set
	 */
	public void setScore(double score);

	/**
	 * 
	 * @return control genes
	 */
	public ControlSet getControls();
	
	/**
	 * 
	 * @return
	 */
	public Set<RawWellDataBO> getRawData();
	
	/**
	 * 
	 * @return
	 */
	public Set<RawWellDataBO> getRawData(String marker);
}