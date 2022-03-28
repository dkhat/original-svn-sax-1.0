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
public class QcCriteriaStepTemplateAmountCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;

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
		
		Assay assay = null;
		
		// check LTP value
		assay = dataSet.getAssay(MARKERNAME_2228);
		log.debug(buildString("Checking Ct of 2228"));
		if (assertNotNull(assay)){
			Double ct = assay.getResult().getWsmxdxct();
			if (ct != null && ct <= 35.5) {
				assay = dataSet.getAssay(MARKERNAME_18s);
				log.debug(buildString("Checking Ct of 18s"));
				
				if (ct > 33.5) isBorderline = true;
				
				if (assertNotNull(assay)) {
					ct = assay.getResult().getWsmxdxct();
					if (ct != null && ct <= 15 && ct >= 9) {
						log.debug("PASS");
						pass = true;
						errorMessage = "";				

						if (ct > 13.5) isBorderline = true;
					}
				}
			}
		}
		
		
		setResult(pass, isBorderline, errorMessage);
	}

}
