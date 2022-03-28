/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.domain;

import java.util.HashSet;
import java.util.Set;

import com.xdx.sax.bo.AggregateBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.PlateDesignBO;
import com.xdx.sax.bo.PlateDesignDetailBO;
import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.TestcodeBO;
import com.xdx.sax.bo.WellTypeBO;
import com.xdx.sax.dao.AggregateDAO;
import com.xdx.sax.dao.MarkerDAO;
import com.xdx.sax.dao.PlateDesignDAO;
import com.xdx.sax.dao.PlateDesignDetailDAO;
import com.xdx.sax.dao.PlateSectionDAO;
import com.xdx.sax.dao.TestcodeDAO;
import com.xdx.sax.dao.WellTypeDAO;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.util.ws.PlateDesignParam;

/**
 * @author smeier
 *
 */
public class PlateDesignHandlerImpl implements PlateDesignHandler {

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.PlateDesignHandler#registerPlateDesign(java.lang.String, java.lang.Double, java.lang.Double, com.xdx.sax.util.ws.PlateDesignParam[])
	 */
	public void registerPlateDesign(
			String testcodeName, 
			String plateDesignName,int numPlates, 
			int numRowsPerPlate, int numColsPerPlate, 
			Double masterMixVol, Double availableWellVol,
			PlateDesignParam[] details) {

		Set<Long> wells = new HashSet<Long>();
		
		PlateDesignDAO dao = new PlateDesignDAO();
		
		TestcodeBO testcode = new TestcodeDAO().findByName(testcodeName);
		if (testcode == null)
			throw new SaxException(buildString("Testcode ", testcodeName, " does not exist"));
			
		// check if design exists
		PlateDesignBO des = dao.findByName(plateDesignName);
		if (des != null)
			throw new SaxException(buildString("Plate design ", plateDesignName, " alsready exists."));

		// check that we have the right number of detail records
		long numWellsPerPlate = numRowsPerPlate * numColsPerPlate;
		long numWells = numPlates * numWellsPerPlate;
		
		if (details.length != numWells)
			throw new SaxException(buildString("Expecting ", numWells, " detail record, got ", details.length));

		Long wellNum;
		for (PlateDesignParam p: details) {
			wellNum = p.getPlateNum() * numWellsPerPlate + (p.getRowNum() * numColsPerPlate + p.getColNum());
			
			// check uniqueness of wells
			if (wells.contains(wellNum))
				throw new SaxException(buildString("Duplicate well: plate ", p.getPlateNum(), ", row ",
						p.getRowNum(), ", col ", p.getColNum()));
			wells.add(wellNum);
			
			// check that we know the marker
			MarkerBO mk = new MarkerDAO().findByName(p.getMarkerName());
			if (mk == null)
				throw new SaxException(buildString("Cannot find marker ", p.getMarkerName()));
			
			// TODO: need to test and add wellType !!!
			if (p.getWellType() == null || p.getWellType().equals(""))
				throw new SaxException(buildString("Well type is mandatory"));
			
			WellTypeBO wt = new WellTypeDAO().findByName(p.getWellType());
			if (wt == null)
				throw new SaxException(buildString("Well type ", p.getWellType(), " does not exist."));
		}
		
		des = new PlateDesignBO();

		Set<TestcodeBO> testcodes = des.getTestcodes();
		if (testcodes==null)
			testcodes = new HashSet<TestcodeBO>();
		testcodes.add(testcode);

		des.setName(plateDesignName);
		des.setNumberofplates(numPlates);
		des.setNumberrowsperplate(numRowsPerPlate);
		des.setNumbercolumnsperplate(numColsPerPlate);
		des.setMastermixvolume(masterMixVol);
		des.setAvailablewellvolume(availableWellVol);
		des.setTestcodes(testcodes);
		dao.persist(des);
		
		for (PlateDesignParam p: details) {
			// create set of marker
			Set<MarkerBO> s = new HashSet<MarkerBO>();
			s.add(new MarkerDAO().findByName(p.getMarkerName()));
			// get well type
			WellTypeBO wt = new WellTypeDAO().findByName(p.getWellType());

			PlateSectionDAO secDao = new PlateSectionDAO();
			
			//get section
			PlateSectionBO sec = secDao.findByDesignSectionname(des.getId(), p.getSectionName());
			if (sec == null) {
				sec = new PlateSectionBO();
				sec.setPlatedesign(des);
				sec.setName(p.getSectionName());
				secDao.persist(sec);
			}
			
			// Check Aggregate
			AggregateDAO aggDao = new AggregateDAO();
			AggregateBO agg = aggDao.findByNameSection(sec.getId(), p.getMarkerName());
			if (agg == null) {
				agg = new AggregateBO();
				agg.setPlateSection(sec);
				agg.setName(p.getMarkerName());
				aggDao.persist(agg);
			}
			
			PlateDesignDetailBO det = new PlateDesignDetailBO();
			det.setPlatedesign(des);
			det.setMarkers(s);
			det.setAggregate(agg);
			det.setNumberinset(p.getPlateNum());
			det.setPlaterow(p.getRowNum());
			det.setPlatecolumn(p.getColNum());
			det.setPlatesection(sec);
			det.setWellType(wt);
			new PlateDesignDetailDAO().persist(det);
		}
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.PlateDesignHandler#updatePlateDesign(long, java.lang.String, java.lang.Double, java.lang.Double)
	 */
	public void updatePlateDesign(long plateDesignId,
			String plateDesignName, Double masterMixVol, Double availableWellVol) throws SaxException {
		PlateDesignBO des = new PlateDesignDAO().findById(plateDesignId);

		if(des == null)
			throw new SaxException(buildString("Cannot find plate design with id ", plateDesignId));
		
		if (plateDesignName != null && ! plateDesignName.equals(""))
			des.setName(plateDesignName);
		
		if (masterMixVol != null)
			des.setMastermixvolume(masterMixVol);
		
		if (availableWellVol != null)
			des.setAvailablewellvolume(availableWellVol);
	}

	/**
     * Utility method tests for absence of a nullity state
     * in a <i>Null Object Pattern</i> design pattern manner.
     */
    protected final boolean assertNotNull(Object object) {
    	return object != null;
	}

    /**
     * Utility method tests for presence of a nullity state
     * in a <i>Null Object Pattern</i> design pattern manner.
     */
    protected final boolean assertNull(Object object) {
    	return  !  assertNotNull(object);
	}

    /**
     * Utility method builds Strings; avoids string concatenation.
     */
    protected final String buildString(Object... values) {
    	
    	StringBuilder sb= new StringBuilder();

    	for (Object object : values) {
    		sb.append((assertNull(object))  ?  "" :  object.toString());
		}

    	return sb.toString();
	}

}
