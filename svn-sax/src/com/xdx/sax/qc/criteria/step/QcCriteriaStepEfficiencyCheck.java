/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.domain.Assay;

/**
 * @author smeier
 *
 */
public class QcCriteriaStepEfficiencyCheck extends AbstractQcCriteriaStep {

	private final Log log = LogFactory.getLog(getClass());

	private final String MARKERNAME_18s = "18s";
	private final String MARKERNAME_2228 = "2228";
	
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
		boolean isBorderline = false;
		String errorMessage = step.getFailreturnresult();
		
		Assay assay2228 = dataSet.getAssay(MARKERNAME_2228);
		Assay assay18s  = dataSet.getAssay(MARKERNAME_18s);
		
		// check LTP value
		if (assertNotNull(assay2228) && assertNotNull(assay18s)) {
			log.debug(buildString("Checking median Ct of 2228"));
			Double ct2228 = assay2228.getResult().getWsmxdxct();
			Double ct18s  = assay18s.getResult().getWsmxdxct();
			
			if (ct2228 == null || ct18s == null) {
				setResult(pass, isBorderline, errorMessage);
				return;
			}
			
			log.debug(buildString("Checking diff of 2228 and 18s median XDx Cts"));			
			double diff = ct2228 - ct18s;
			
			if (diff >= 18.5 && diff <= 22.5) {
				log.debug("PASS");
				pass = true;
				errorMessage = "";
				
				// borderline pass?
				if ((diff < 19) || (diff > 21.5))
					isBorderline = true;
			} else {
				log.debug(buildString("FAILURE: medianct 2228=", ct2228,
						", 18s=", ct18s));
				errorMessage = buildString(errorMessage,
						" - difference between 2228 and 18s is ", diff);
			}			
		}
		
		setResult(pass, isBorderline, errorMessage);
	}

}
