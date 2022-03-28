/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.bo.PlateProcessControlBO;
import com.xdx.sax.bo.ProcessControlDetailBO;
import com.xdx.sax.dao.PlateProcessControlDAO;

/**
 * @author smeier
 *
 */
public class QcCriteriaStepProcessControlCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());
	
	private static final double MAX_RANGE=1.5;
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.qc.criteria.step.AbstractQcCriteriaStep#evaluate()
	 */
	@Override
	public void evaluate() {
		log.debug(buildString("Evaluating process controls"));
		
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
		
		for (ProcessControlDetailBO detail: pc.getProcesscontrol().getProcesscontroldetails()) {
			String marker = detail.getMarker().getName();
			double diff = Math.abs(this.dataSet.getAssay(marker).getResult().getWsmnormct() - detail.getExpectedValue());
			if(diff > MAX_RANGE) {
				pass=false;
				failedMarkers.add(marker);
			}
		}
		
		if (! pass)
			errorMessage=buildString("Failed markers: ", failedMarkers.toString());
		
		setResult(pass, false, errorMessage);
	}

}
