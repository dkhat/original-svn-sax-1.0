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
public class QcCriteriaStepPcrReactionCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());

	private final String MARKERNAME_LTP = "LTP";
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
		boolean isBorderline=false;
		
		String errorMessage = step.getFailreturnresult();
		
		Assay assayLtp  = dataSet.getAssay(MARKERNAME_LTP);
		Assay assay18s  = dataSet.getAssay(MARKERNAME_18s);
		Assay assay2228 = dataSet.getAssay(MARKERNAME_2228);

		if (assertNotNull(assayLtp) && assertNotNull(assay18s) && assertNotNull(assay2228)) {
			Double valueLtp  = assayLtp.getResult().getWsmxdxct();
			Double value18s  = assay18s.getResult().getWsmxdxct();
			Double value2228 = assay2228.getResult().getWsmxdxct();

			if ((valueLtp != null && valueLtp >= 20 && valueLtp <= 24) ||
				(value18s != null && value2228 != null && value18s < 15 && value2228 < 35.5)) {
				log.debug("PASS");
				pass = true;
				errorMessage = "";
				
			} else {
				log.debug(buildString("Check failed due to assay values: LTP ", valueLtp, ", 18s", value18s, ", 2228: ", value2228));
			}
		} else {
			log.debug(buildString("One or more assays are null: LTP", assayLtp, ", 18s: ", assay18s, ", 2228: ", assay2228));
		}
		
		setResult(pass, isBorderline, errorMessage);
	}

}
