/**
 *  $Id: AbstractNormalizationScheme.java,v 1.0 - 11 June, 2008 5:55:55 PM gtrester Exp $
 *  Diagnostic Expression Inc. Software Development Group
 *
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor.modules.normalizer;

import java.util.Set;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.domain.ControlSet;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.workflow.OrchestratorStep;

/**
 * Operates on normalization genes.
 *
 * @author gtrester
 *
 */
public abstract class AbstractAggregateNormalizer implements AggregateNormalizer, OrchestratorStep {

    private SaxDataSet dataSet= null;

    /**
     * 
     * @return
     */
	protected SaxDataSet getDataSet() {
    	assertInitialized();
		
		return this.dataSet;
	}
	
    /**
     * 
     * @return
     */
    protected ControlSet getControls() {     	
    	assertInitialized();
		
    	return this.dataSet.getControls();
    }

    /**
     * 
     */
    public void initialize(SaxDataSet dataSet) {
    	this.dataSet = dataSet;    	
    }
    
    /**
     * 
     * @return
     */
    protected Set<RawWellDataBO> getWellData() {
    	assertInitialized();

    	return this.dataSet.getRawData();
    }

    /**
     *     
     */
    public void execute() {
    	assertInitialized();
    	
    	// No aggregation if processor error
    	for (PlateBO plate: dataSet.getPlateSet().getPlates())
    		if (plate.isProcessorError())
    			return;
    	
    	normalize();
    }
    
    public abstract void normalize();
    
    /**
     * 
     */
    protected final void assertInitialized() {
    	if (this.dataSet == null)
    		throw new SaxException("Data set not initialized");    	
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