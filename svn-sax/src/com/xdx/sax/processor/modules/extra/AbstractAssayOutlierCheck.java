/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.processor.modules.extra;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.processor.XdxProcessorComponent;
import com.xdx.sax.workflow.OrchestratorStep;

/**
 * @author smeier
 *
 */
public abstract class AbstractAssayOutlierCheck extends XdxProcessorComponent implements OrchestratorStep, AssayOutlierCheck {

	protected SaxDataSet dataSet = null;
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.workflow.OrchestratorStep#execute()
	 */
	public void execute() {
		if (this.dataSet == null)
			throw new SaxException("Dataset has not been initialized");
		
    	// No aggregation if processor error
    	for (PlateBO plate: dataSet.getPlateSet().getPlates())
    		if (plate.isProcessorError())
    			return;
    	
		runCheck();
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.workflow.OrchestratorStep#initialize(com.xdx.sax.domain.SaxDataSet)
	 */
	public void initialize(SaxDataSet dataSet) {
		this.dataSet = dataSet;
	}

}
