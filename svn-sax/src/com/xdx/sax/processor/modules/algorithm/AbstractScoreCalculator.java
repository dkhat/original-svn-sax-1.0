/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.processor.modules.algorithm;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.workflow.OrchestratorStep;

/**
 * @author smeier
 *
 */
public abstract class AbstractScoreCalculator implements OrchestratorStep, ScoreCalculator {

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
    	
		calculate();
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.workflow.OrchestratorStep#initialize(com.xdx.sax.domain.SaxDataSet)
	 */
	public void initialize(SaxDataSet dataSet) {
		this.dataSet = dataSet;
	}

    /**
	 * Utility method tests for abscence of a nullity state
	 * in a <i>Null Object Pattern</i> design pattern manner.
	 */
	protected final boolean assertNotNull(Object object) {
	 	return object != null;
	}

	/**
	 * Utility method tests for presence of a nullity state
	 * in a <i>Null Object Pattern</i> design pattern manner.
	 */
	protected final boolean assertNull(Object object) {
	 	return  !  assertNotNull(object);
	}

	/**
	 * Utility method builds Strings; avoids string concatenation.
	 *
	 * @param values
	 * @return the concatenated string
	 */
	protected final String buildString(Object... values) {

		StringBuilder sb= new StringBuilder();

		for (Object object : values) {
			sb.append((assertNull(object))  ?  "" :  object.toString());
		}

		return sb.toString();
	}

}
