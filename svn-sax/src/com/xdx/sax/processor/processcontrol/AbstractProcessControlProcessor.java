/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.processor.processcontrol;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.processor.XdxProcessorComponent;
import com.xdx.sax.workflow.OrchestratorStep;

/**
 * @author smeier
 *
 */
public abstract class AbstractProcessControlProcessor extends XdxProcessorComponent implements OrchestratorStep, ProcessControlProcessor {

	// The data set that we are working on
	protected SaxDataSet dataSet = null;
	
	/**
	 * Convenience method for the implementing class
	 *
	 * @return
	 */
	protected SaxDataSet getDataSet() {
		if (this.dataSet == null)
			throw new SaxException("Dataset not initialized");
		
		return this.dataSet;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.processor.processcontrol.ProcessControlProcessor#execute()
	 */
	public void execute() {


    	// No aggregation if processor error
    	for (PlateBO plate: dataSet.getPlateSet().getPlates())
    		if (plate.isProcessorError())
    			return;
    	
		executeQc();
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.processor.processcontrol.ProcessControlProcessor#initialize(com.xdx.sax.domain.SaxDataSet)
	 */
	public void initialize(SaxDataSet dataSet) {
		if (dataSet == null)
			throw new SaxException("Null dataset");

		this.dataSet = dataSet;
	}

}
