/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.domain.Assay;

/**
 * @author smeier
 *
 */
public class QcCriteriaStepDataPresentCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());

	private static String[] markers = {"4647", "4685-5", "PDCD1", "6489-4", "ITGAM", "2709", "873", "ITGA4",
		"FLT3-3L", "PF4", "G6b-1", "b-GUS-2"};

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
    	
		// initialize with failure defaults and reset if check passes
		boolean pass=true;
		String errorMessage = "";

		Assay assay = null;

		// check presence of markers
		log.debug(buildString("Checking presence of markers"));
		for (String marker : markers) {
			assay = dataSet.getAssay(marker);
			if (assertNull(assay) || assertNull(assay.getResult().getWsmxdxct())) {
				pass = false;
				errorMessage = buildString(errorMessage, ",", marker);
			}
		}
		
		if (!pass) {
			errorMessage = buildString(step.getFailreturnresult(), " - missing data for gene(s) ",
					errorMessage.substring(1));
			
			// Check for low template as well
			assay = dataSet.getAssay(marker_18s);

			try {
				if (assertNotNull(assay) && 
					assertNotNull(assay.getResult().getWsmxdxct()) && 
					assay.getResult().getWsmxdxct() > 13)
					errorMessage = buildString("Low template with ", errorMessage);
			} catch (Exception e) {
				log.error(getStackTrace(e));
			}
		}
		
		if (pass) {
			// check for normalization factor first
			boolean nCtPresent = false;
			for (Assay _assay : dataSet.getAssays()) {
				Double nCt = _assay.getResult().getWsmnormct();
				if (nCt != null) {				
					nCtPresent=true;
					break;
				}
			}
	
			if (! nCtPresent) {				
				pass=false;
				errorMessage = buildString(step.getFailreturnresult(), " - No normalization factor can be calculated - missing genes: ", getMissingNormGenes());
			}
		}
		
		if (pass) log.debug("PASS");
		
		// TODO: implement borderline check
		setResult(pass, false, errorMessage);
	}

	private Set<String> getMissingNormGenes () {
		HashSet<String> missingNormGenes = new HashSet<String>();

		Double controlVal;
		
    	for ( String controlMarker: (Set<String>) dataSet.getControls().getMarkerNames()) {   		
			controlVal = dataSet.getControls().getNormalizedControlValue(controlMarker);
			
			if (controlVal == null) {
				missingNormGenes.add(controlMarker);
				continue;
			}
    	}		
		return missingNormGenes;
	}
}
