/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.domain;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.AggMarkerResultBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.PlateDesignDetailBO;
import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.dao.AggMarkerResultDAO;
import com.xdx.sax.dao.PlateSectionDAO;
import com.xdx.sax.dao.PlateSetDAO;
import com.xdx.sax.dao.RawWellDataDAO;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 *
 */
public class HTxSaxDataSetImpl extends BusinessObject implements SaxDataSet {

	//
	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());

	// The assays belonging to this data set
	private Set<Assay> assays = null;
	private Hashtable<String, Assay> assayHT = null;
	
	// Set of raw data elements
	private Set<RawWellDataBO> rawWellData = null;
	
	// raw data by aggregate
	private Hashtable<String, Set<RawWellDataBO>> aggregateHT = null;
	
	// Set of control genes
	private ControlSet controlSet = null;
	
	// plateset and section uniquely define a test
	private PlateSetBO plateSet;
	private PlateSectionBO plateSection;
	
	// the test score
	private double score;

	/**
	 * The SaxDataSet constructor requires valid plate set
	 * and section ids to build the data set
	 * 
	 * @param plateSetId valid plate set id
	 * @param plateSectionId plate section id valid for the given plate set
	 */
	public HTxSaxDataSetImpl(long plateSetId, long plateSectionId) {
		this.plateSet = new PlateSetDAO().findById(plateSetId);
		this.plateSection = new PlateSectionDAO().findById(plateSectionId);
		
		// check ids for validity
		if ((this.plateSet == null) || (this.plateSection == null)) {
			throw new SaxException(buildString("Plateset ", plateSetId,
					" and plate section ", plateSectionId, " are invalid"));
		}
		
		buildSaxDataset ();
		
	}

	private void buildSaxDataset() {
		/**
		 *  This method builds the data set from the
		 *  underlying business objects.
		 */
		
		this.controlSet = new HTxNormalizationGeneSetImpl(this.plateSet.getId(), this.plateSection.getId(), this);
		
		buildRawDataSet();
		
		buildAssaySet();
		
	}
	
	private void buildRawDataSet() {
		log.debug(buildString("Building raw data set"));
		
		RawWellDataBO well;

		this.rawWellData = new HashSet<RawWellDataBO>();
		this.aggregateHT = new Hashtable<String, Set<RawWellDataBO>>();
		
		for (PlateDesignDetailBO detail : this.plateSection.getPlatedesigndetails()) {
			well = new RawWellDataDAO().findByPlatesetRowCol(this.plateSet.getId(), 
					detail.getNumberinset(), detail.getPlaterow(), detail.getPlatecolumn());
			// add well to raw data set
//			if (well.getWellType().getName().indexOf(WELLTYPE_BLANK) == -1)
			this.rawWellData.add(well);
			
			// add well to hashtable indexed by aggregate id
			if (well.getAggregate() != null) {
				String agName = well.getAggregate().getName();
				if (! this.aggregateHT.containsKey(agName))
					this.aggregateHT.put(agName, new HashSet<RawWellDataBO>());
				this.aggregateHT.get(agName).add(well);
			}

		}
		log.debug(buildString("Dataset has ", this.aggregateHT.keySet().size(), "aggregate elements"));
		log.debug(buildString("Dataset has ", this.rawWellData.size(), " raw data elements"));
	}
	
	private void buildAssaySet() {
		log.debug(buildString("Building assay list"));
		
		this.assays = new HashSet<Assay>();
		this.assayHT = new Hashtable<String, Assay>();
		
		List<AggMarkerResultBO> markerResults =
			new AggMarkerResultDAO().findByPlatesetSection(
					this.plateSet.getId(),
					this.plateSection.getId());

		// check validity of returned data
		if (markerResults == null) {
			throw new SaxException(buildString("No data for plate set ",
					this.plateSet.getId(), 
					" and section ", this.plateSection.getId()));
		}
		
		// fill assay data structure
		for(AggMarkerResultBO markerResult : markerResults) {
			
			try {
				String agName = markerResult.getAggregate().getName();
				if (this.aggregateHT.containsKey(agName)) {
					Assay assay = new AssayImpl(this.aggregateHT.get(agName), markerResult);
					this.assays.add(assay);
					this.assayHT.put(agName, assay);
				} else
					log.error(buildString("No raw data for aggregate ", agName));
			} catch (Exception e) {
				log.error(buildString("Error adding assay to assaylist, plateset",
						this.plateSet.getId(), " section ", this.plateSection.getId(),
						" markerresult ", markerResult));
			}			
		}
		log.debug(buildString("Dataset has ", this.assays.size(), " assay elements."));
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.SaxDataSet#getAssays()
	 */
	public Set<Assay> getAssays() {
		return assays;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.SaxDataSet#getAssays()
	 */
	public Assay getAssay(String markerName) {
		if (this.assayHT.containsKey(markerName))
			return this.assayHT.get(markerName);
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.SaxDataSet#getPlateSetId()
	 */
	public PlateSetBO getPlateSet() {
		return this.plateSet;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.SaxDataSet#getPlateSectionId()
	 */
	public PlateSectionBO getPlateSection() {
		return this.plateSection;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.SaxDataSet#getScore()
	 */
	public double getScore() {
		return score;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.SaxDataSet#setScore(double)
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.SaxDataSet#getControls(double)
	 */
	public ControlSet getControls() {
		if (this.controlSet == null)
			throw new SaxException("control set has not been initialized");
		
		return this.controlSet;
	}

	public Set<RawWellDataBO> getRawData() {
		if (this.rawWellData == null)
			throw new SaxException ("raw well data object not initialized");
		
		return this.rawWellData;
	}

	public Set<RawWellDataBO> getRawData(String marker) {
		if (this.rawWellData == null)
			throw new SaxException ("raw well data object not initialized");
		
		Set<RawWellDataBO> result = new HashSet<RawWellDataBO>();
		for (RawWellDataBO well: this.rawWellData) {
			for (MarkerBO mk : well.getMarkers()) {
				if (mk.getName().equals(marker))
					result.add(well);
			}
		}
		
		return result;
	}
}
