/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.PlateDesignDetailBO;
import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.dao.PlateSectionDAO;
import com.xdx.sax.dao.RawWellDataDAO;
import com.xdx.sax.exceptions.SaxException;

/**
 * This class implements a set of controls for a given plate set.
 * The controls are defined in the plate design, the actual values
 * are tied to the plates that make up the specific plate set.
 * 
 * This implementation for the AlloMap algorithm
 * provides a hashtable of the CT values keyed by control marker names.
 * 
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
public class HTxNormalizationGeneSetImpl extends BusinessObject implements ControlSet {

	// this is used to identify the right marker type in the database
	// matches MARKERTYPE.NAME in the DB.
	private final static String WELLTYPE_NORMALIZATION = "Normalization Gene";

	private HashMap<String, ArrayList<RawWellDataBO>> markerData;

	private Set<RawWellDataBO> wells;

	private SaxDataSet dataSet;
	
	private long plateSetId;
	private long plateSectionId;
	
	private final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Builds the set of controls from the database
	 * 
	 * @param plateSetId
	 * @param plateSectionId
	 */
	public HTxNormalizationGeneSetImpl (long plateSetId, long plateSectionId, SaxDataSet dataSet) {
		this.plateSetId = plateSetId;
		this.plateSectionId = plateSectionId;
		this.dataSet = dataSet;
		
		buildControlSet();

	}

	/**
	 * Private method that builds the control genes data set
	 * from the database. This implementation is specific to
	 * AlloMap and might not be transferable to other uses.
	 * 
	 * @param plateSetId
	 */
	private void buildControlSet() {
		// initialize HashMap for control gene data
		this.markerData = new HashMap<String, ArrayList<RawWellDataBO>>();
		this.wells = new HashSet<RawWellDataBO>();
		
		PlateSectionBO plateSection = new PlateSectionDAO().findById(plateSectionId);

		MarkerBO marker;
		RawWellDataBO well;
		
		for (PlateDesignDetailBO detail : plateSection.getPlatedesigndetails()) {
			well = new RawWellDataDAO().findByPlatesetRowCol(plateSetId, detail.getNumberinset(), detail.getPlaterow(), detail.getPlatecolumn());
			if (well.getWellType().getName().indexOf(WELLTYPE_NORMALIZATION) >= 0) {
				log.debug(buildString("Type of well ", well.getId(), " is ", well.getWellType().getName()));
				// add well to overall set
				this.wells.add(well);
				
				// this is HTx, so by definition there is only 1 marker!
				marker = (MarkerBO)well.getMarkers().toArray()[0];
				
				// new key?
				if (! markerData.containsKey(marker.getName()))
					markerData.put(marker.getName(), new ArrayList<RawWellDataBO>());

				// add well to HashMap keyed by marker name
				markerData.get(marker.getName()).add(well);
			}
		}

		log.debug(buildString("===> Normalization genes: ", this.markerData));
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.ControlSet#getControlValue(java.lang.String)
	 */
	public Double getControlValue(String markerName) {
		if (! this.markerData.containsKey(markerName))
			throw new SaxException(buildString("Control gene ", markerName, " is missing. Set is: ", this.markerData));

		return this.dataSet.getAssay(markerName).getResult().getWsmxdxct();
		// have to dynamically calculate values at runtime as they might
		// change over time
//		ArrayList<Double> rawValues = new ArrayList<Double>();
//		for (RawWellDataBO well : markerData.get(markerName)) {
//			if (well.getXdxct() != null)
//				rawValues.add(well.getXdxct());
//			else
//				log.debug(buildString("null XDx CT for well", well.getId()));
//		}
		
//		log.debug(buildString("Number raw values for ", markerName, ": ", rawValues.size()));
//		if (rawValues.size() < 2)
//			return null;
		
//		return StatsLibrary.wtSmoothMean(rawValues);
//		return StatsLibrary.median(rawValues);
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.ControlSet#getControlValue(java.lang.String)
	 */
	public Double getNormalizedControlValue(String markerName) {
		if (! this.markerData.containsKey(markerName))
			throw new SaxException(buildString("Control gene ", markerName, " is missing. Set is: ", this.markerData));

		return this.dataSet.getAssay(markerName).getResult().getWsmnormct();
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.ControlSet#getMarkers()
	 */
	public Set<String> getMarkerNames(){
		return this.markerData.keySet();
	}

	public Set<RawWellDataBO> getRawData() {
		return this.wells;		
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.ControlSet#getPlateSetId()
	 */
	public long getPlateSetId() {
		return this.plateSetId;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.ControlSet#getPlateSectionId()
	 */
	public long getPlateSectionId() {
		return this.plateSectionId;
	}
}
