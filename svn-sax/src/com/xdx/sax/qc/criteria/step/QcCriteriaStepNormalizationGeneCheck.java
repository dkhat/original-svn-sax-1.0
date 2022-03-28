/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.domain.ConfigurationModel;

/**
 * @author smeier
 *
 */
public class QcCriteriaStepNormalizationGeneCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());

	private final String EXPECTED_CT = "EXPECTED_CT";

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
		boolean pass=true;
		boolean isBorderline=false;
		String errorMessage = "";
		
		Double expectedCT = null;
		Double diff = null;
		Double controlVal = null;
		int valuesAbove = 0;
		int valuesBelow = 0;
		int valuesOut = 0;
		
    	for ( String controlMarker: (Set<String>) dataSet.getControls().getMarkerNames()) {   		
			// get expected CT from configuration
			expectedCT= Double.valueOf(ConfigurationModel.lookup(
					dataSet.getPlateSet().getTestcode().getTestcodename(),
					this.EXPECTED_CT, controlMarker));
			
			controlVal = dataSet.getControls().getNormalizedControlValue(controlMarker);
			
			if (controlVal == null) {
				valuesOut++;
				continue;
			}
				
			diff = expectedCT - controlVal;
			
   	    	if (diff > 1) {
   	    		valuesBelow++;
   	    		valuesOut++;
   	    		log.debug(buildString("WsmNormCt: ", controlVal));
   	    		log.debug(buildString("Expected:  ", expectedCT));
   	    		log.debug(buildString("Difference to expected Ct for control marker ",
   	    				controlMarker, " out of bounds: ", diff));
   	    	}
   	    	
   	    	if (diff < -1) {
   	    		valuesAbove++;
   	    		valuesOut++;
   	    		log.debug(buildString("WsmNormCt: ", controlVal));
   	    		log.debug(buildString("Expected:  ", expectedCT));
   	    		log.debug(buildString("Difference to expected Ct for control marker ",
   	    				controlMarker, " out of bounds: ", diff));
   	    	}
    	}

    	// Check condition ... only one diff value may be above and one may be below
    	if(valuesAbove > 1 || valuesBelow > 1 || valuesOut > 2) {
    		log.debug(buildString("FAILURE"));
    		pass = false;
    		errorMessage = buildString(step.getFailreturnresult(), " - values above: ", valuesAbove, ", values below: ", valuesBelow, ", ", valuesOut, " genes missing");
    	} else if (valuesOut > 2) {
    		log.debug(buildString("FAILURE - ", valuesOut, " genes missing"));
    		pass = false;
    		errorMessage = buildString(step.getFailreturnresult(), " - ", valuesOut, " genes missing");
    	} else {
    		log.debug(buildString("PASS"));
    	}
		
		// Now check for borderline case if check passed
		if (pass && valuesAbove>0 && valuesBelow>0) {
			isBorderline = true;
			errorMessage = "Some values outside threshold";
		}
		
    	// set the main check result
		setResult(pass, isBorderline, errorMessage);
	}

}
