/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */

package com.xdx.sax.qc;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.QcSchemaBO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.workflow.OrchestratorStep;

/**
 * 
 * @author smeier
 *
 * This is the abstract definition of a QC processor.
 */
public abstract class AbstractQcProcessor extends BusinessObject implements QcProcessor, OrchestratorStep {

	// The data set that we are working on
	private SaxDataSet dataSet = null;
	private QcSchemaBO schema = null;
	
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

	/**
	 * Convenience method for the implementing class
	 *
	 * @return
	 */
	protected QcSchemaBO getQcSchema() {
		if (this.schema == null)
			throw new SaxException("QC schema not initialized");
		
		return this.schema;
	}

	/**
	 * Implementing the OrchestratorStep interface  
	 */
	public void execute() {
		if (this.dataSet == null)
			throw new SaxException("Dataset not initialized");

		executeQc();
	}

	/**
	 * Implementing the OrchestratorStep interface 
	 */
	public void initialize(SaxDataSet dataSet) {
		if (dataSet == null)
			throw new SaxException("Null dataset");

		this.dataSet = dataSet;
		this.schema = dataSet.getPlateSet().getTestcode().getQcSchema();
		
		if (this.schema == null)
			throw new SaxException(buildString("QC schema for plateset ", 
					dataSet.getPlateSet().getId(), " is null"));
	}

	/**
	 * Implementing the OrchestratorStep interface 
	 */
	public void initialize(SaxDataSet dataSet, QcSchemaBO schema) {
		if (dataSet == null)
			throw new SaxException("Null dataset");

		this.dataSet = dataSet;
		this.schema = schema;
		
		if (this.schema == null)
			throw new SaxException(buildString("QC schema is null"));
	}

	/**
	 * Worker method that actually does the QC processing
	 */
	public abstract void executeQc();

}
