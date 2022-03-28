/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.domain.ConfigurationModel;

/**
 * @author smeier
 *
 */
public class QcCriteriaStepGenomicDnaContaminationCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());

	private final String MARKERNAME_GUSBP = "GUSB-P";
	private final String MARKERNAME_BGUS2 = "b-GUS-2";
	
	private final Double LOW_CT_THRESHOLD = new Double(ConfigurationModel.lookup("HTx", "HtxAggregator", "LOW_CT_THRESHOLD"));
	private final Double HIGH_CT_THRESHOLD= new Double(ConfigurationModel.lookup("HTx", "HtxAggregator", "HIGH_CT_THRESHOLD"));

	/* (non-Javadoc)
	 * @see com.xdx.sax.qc.criteria.step.AbstractQcCriteriaStep#evaluate()
	 */
	@Override
	public void evaluate() {
		log.debug(buildString("Evaluating QC criteria"));
		
		// check initialization
		assertInitialized();
		
    	// No aggregation if processor error
    	for (PlateBO plate: dataSet.getPlateSet().getPlates())
    		if (plate.isProcessorError()) {
    			setResult(false, false, "Cannot execute check");
    			return;
    		}
    	
		// initialize with failure defaults and reset if check passes
		boolean pass=false;
		String errorMessage = step.getFailreturnresult();
		
		// get GUSB-P value
		Double gusbpCt = null;
		Set<RawWellDataBO> rawData = new HashSet<RawWellDataBO>();
		try {
			gusbpCt = dataSet.getAssay(MARKERNAME_GUSBP).getResult().getWsmxdxct();
		} catch (NullPointerException e) {}
		
		try {
//			rawData = dataSet.getAssay(MARKERNAME_GUSBP).getRawData();
			rawData = dataSet.getRawData(MARKERNAME_GUSBP);
		} catch (NullPointerException e) {
			log.error("Cannot get raw data for assay GUSB-P");
		}
		
		// get b-GUS-2 value 
		Double bgus2Ct = null;
		try{
			bgus2Ct = dataSet.getAssay(MARKERNAME_BGUS2).getResult().getWsmxdxct();
		} catch (NullPointerException e) {}
		
		log.debug(buildString("Checking presence of b-GUS-2 and GUSB-P"));
		if (assertNull(bgus2Ct)) {
			log.debug("No b-GUS-2 value");
			log.debug("PASS");
			pass = true;
			errorMessage = "";
		} else if (assertNull(gusbpCt)) {
			// Now check all the individual XDx CT values
			List<Double> cts = new ArrayList<Double>();
			log.debug(buildString("Processing wellData", rawData));
			for (RawWellDataBO well : rawData) {
				if ((well.getXdxct() != null) && 
					(well.getXdxct() <= HIGH_CT_THRESHOLD) &&
					(well.getXdxct() >= LOW_CT_THRESHOLD))
					cts.add(well.getXdxct());
			}
			log.debug("Created list");
			Collections.sort(cts);
			log.debug("Sorted Collection");
			
			if ((cts.size()==0) || ((cts.get(0) - bgus2Ct) >= 3)) {
				log.debug("Difference between minimum CT and b-GUS-2 >= 3");
				log.debug("PASS");
				pass = true;
				errorMessage = "";						
			}
		} else {
			log.debug(buildString("Checking difference with b-GUS-2"));
			
			try {
				if ((gusbpCt - bgus2Ct) >= 3) {
					log.debug("PASS");
					pass = true;
					errorMessage = "";				
				} else {
					log.debug(buildString("Difference of GUSB-P: ", gusbpCt,
								" and b-GUS-2: ", bgus2Ct, " is not high enough"));
				}
			} catch (NullPointerException e) {
				// no assay result - FAIL
				log.debug(buildString("No assay result for b-GUS-2"));
			}
		}
		
		
		// TODO: implement borderline check
		setResult(pass, false, errorMessage);
	}

}
