/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.bo.PlateProcessControlBO;
import com.xdx.sax.bo.ProcessControlDetailBO;
import com.xdx.sax.dao.PlateProcessControlDAO;
import com.xdx.sax.domain.Assay;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 * 
 * This check is a check that is applied only to process controls.
 * It compares the raw score to an expected value.
 *
 */
public class QcCriteriaStepRawScoreCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());
	
	private static final double MAX_RANGE=2.0;
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.qc.criteria.step.AbstractQcCriteriaStep#evaluate()
	 */
	@Override
	public void evaluate() {
		log.debug(buildString("Evaluating raw score"));
		
		// check initialization
		assertInitialized();
		
    	// No aggregation if processor error
    	for (PlateBO plate: dataSet.getPlateSet().getPlates())
    		if (plate.isProcessorError()) {
    			setResult(false, false, "Cannot execute check");
    			return;
    		}
    	
		boolean pass=true;
		String errorMessage = "";
		ArrayList<String> failedMarkers = new ArrayList<String>();
		
		PlateProcessControlBO pc = 
			new PlateProcessControlDAO().findByPlateSetSection(
					dataSet.getPlateSet().getId(), 
					dataSet.getPlateSection().getId());
		
		// Is plateset/section a process control at all?
		if (pc == null) {
			log.debug(buildString("Section is not a process control"));
			return;
		}

		// put control details in hashmap keyed by gene name
		HashMap<String, ProcessControlDetailBO> controls = new HashMap<String, ProcessControlDetailBO>();
		for (ProcessControlDetailBO det : pc.getProcesscontrol().getProcesscontroldetails()) {
			controls.put(det.getMarker().getName(), det);
		}

		// check control against result for each gene
		for (Assay as : this.dataSet.getAssays()) {
			String markerName = as.getAggregate().getName();
			
			// is control value present?
			if (! controls.containsKey(markerName))
				throw new SaxException(buildString("Missing control value for gene ", markerName));
			
			ProcessControlDetailBO det = controls.get(markerName);
			double upperBounds = det.getExpectedValue() + MAX_RANGE * det.getBound();
			double lowerBounds = det.getExpectedValue() - MAX_RANGE * det.getBound();
			
			if (as.getResult().getWsmnormct() > upperBounds ||
				as.getResult().getWsmnormct() < lowerBounds) {
				pass = false;
				failedMarkers.add(markerName);
			}
		}
		
		if (! pass)
			errorMessage=buildString("Failed markers: ", failedMarkers.toString());
		
		setResult(pass, false, errorMessage);
	}

}
