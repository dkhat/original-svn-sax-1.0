/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor.modules.aggregator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.workflow.OrchestratorStep;

/**
 * This is the abstract base class for all implementations that aggregate 
 * data on a plate section basis.
 *
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
public abstract class AbstractAggregator implements Aggregator, OrchestratorStep {

	private final Log log = LogFactory.getLog(getClass());

	// Data set that this calculation step works on
	private SaxDataSet dataSet = null;

	protected SaxDataSet getDataSet() {
		if (this.dataSet == null)
			throw new SaxException("Dataset not initialized");
		
		return this.dataSet;
	}
	
	/**
	 * Execute calculation for this orchestrator step
	 */
    public void execute () {
    	// check data set
    	if (this.dataSet == null)
    		throw new SaxException("Dataset not initialized");
    	
    	// No aggregation if processor error
    	for (PlateBO plate: dataSet.getPlateSet().getPlates())
    		if (plate.isProcessorError())
    			return;
    	
    	aggregate();
    }
    
    public abstract void aggregate();
    
    /**
     * Initialize data structures from given SaxDataSet
     * 
     * @param dataSet dataset that this calculation step works on
     */
	public void initialize(SaxDataSet dataSet) {
		log.debug(buildString("Initializing aggregator ..."));
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