package com.xdx.sax.workflow;

import com.xdx.sax.domain.SaxDataSet;

public interface Orchestrator {

	/**
	 * Run SAX for a given testcode
	 * 
	 * @param testcode
	 */
	public void execute (SaxDataSet dataSet);
}
