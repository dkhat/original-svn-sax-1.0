/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.domain.Assay;

/**
 * @author smeier
 *
 * This class implements the well-to-well variance check. For each triplicate assay
 * we look at the standard deviation. The test is passed if in an ordered list of standard
 * deviation values the value at the index below the 90% threshold is 0.7 or less.
 * 
 */
public class QcCriteriaStepWellWellVarianceCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());

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
		boolean isBorderline=false;
		String errorMessage = step.getFailreturnresult();
		
		// check all assays for standard deviation
		log.debug("Checking assays for well-to-well deviation");

		List<Double> devs = new ArrayList<Double>();
		
		// exclude "Blank" and "GUSB-P" from the check
		for (Assay assay : dataSet.getAssays())
			if (assertNotNull(assay) && 
				assertNotNull(assay.getResult().getPlatestddev()) && 
				(! assay.getAggregate().getName().equals("")) &&
				(! assay.getAggregate().getName().equals("Blank")) &&
				(! assay.getAggregate().getName().equals("GUSB-P")))
				devs.add(assay.getResult().getPlatestddev());
		
		Collections.sort(devs);

		if (devs.size()==0) {
			errorMessage = buildString(errorMessage, " - non-null Assay data!");			
		} else {			
			int index = (int) Math.floor(devs.size()*0.9)-1;

			if (index<0) index = 0;
			
			if (devs.get(index) <= 0.7) {
				log.debug(buildString("PASS, well ", index+1, "has stddev ", devs.get(index)));
				pass = true;
				errorMessage = "";
				
				// borderline pass?
				if (devs.get(index) > 0.5)
					isBorderline=true;
			} else {
				errorMessage = buildString(errorMessage, " - too many wells have standard deviation > 0.7");
			}
		}
		
		// TODO: implement borderline check
		setResult(pass, isBorderline, errorMessage);
	}

}
