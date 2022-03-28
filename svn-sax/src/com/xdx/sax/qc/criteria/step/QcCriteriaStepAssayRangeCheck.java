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
public class QcCriteriaStepAssayRangeCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private static String marker_18s = "18s";
	
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
    	
		boolean pass=true;
		String errorMessage = this.step.getPassreturnresult();
		
		String parsedTemplate = parseTemplateString();
		
		if (parsedTemplate != null)
			pass = doTheMath(parsedTemplate);
		else
			pass=false;
		
		if (! pass) {
			errorMessage = step.getFailreturnresult();
			log.debug(buildString("FAILURE"));

			// Check for low template as well
			Assay assay = dataSet.getAssay(marker_18s);

			try {
				if (assertNotNull(assay) && 
					assertNotNull(assay.getResult().getWsmxdxct()) &&
					assay.getResult().getWsmxdxct() > 13)
					errorMessage = buildString("Low template with ", errorMessage);
			} catch (NullPointerException e) {
				log.error(getStackTrace(e));
			}
		}
		
		// TODO: implement borderline check
		setResult(pass, false, errorMessage);
	}

}
