/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor.ct;

import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.processor.XdxProcessorComponent;
import com.xdx.sax.workflow.OrchestratorStep;

/**
 * @author smeier
 *
 */
public abstract class AbstractCtProcessor extends XdxProcessorComponent implements OrchestratorStep {

	private final Log log = LogFactory.getLog(getClass());
	
	private SaxDataSet dataSet;
	
	/**
	 * This method calculates the CT for one single well
	 * Specific to the implementation of the CT processor
	 * 
	 * @param well well object containing the raw data
	 */
	public abstract void calculateCT (RawWellDataBO well);
	
	/**
	 * For each well calculates the CT value
	 */
	public void execute() {
		if (dataSet == null)
			throw new SaxException("CT processor not initialized");
		
		// Calculating raw XDx CTs for the normalization genes
		for (RawWellDataBO well : (Set<RawWellDataBO>) dataSet.getControls().getRawData()) {
			try {
				calculateCT(well);
			} catch (SaxException e) {
				log.error(buildString("Well ", well.getId(), ": ", e.getMessage()));
			}
		}				

		// Calculating raw XDx CTs for the algorithm genes
		for (RawWellDataBO well : (Set<RawWellDataBO>) dataSet.getRawData()) {
			try {
				calculateCT(well);
			} catch (SaxException e) {
				log.error(buildString("Well ", well.getId(), ": ", e.getMessage()));
			}
		}				
	}

	public void initialize(SaxDataSet dataSet) {
		log.debug(buildString("Initializing CT processor ", getClass().getName()));
		
		this.dataSet = dataSet;		
	}
}
