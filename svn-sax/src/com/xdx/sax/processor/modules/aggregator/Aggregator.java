/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor.modules.aggregator;

import com.xdx.sax.exceptions.SaxException;

/**
 * This is the interface for all aggregation methods
 * that aggregate data by plate section
 * 
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
public interface Aggregator {

	/**
	 * This method performs a one-off normalization on the
	 * given dataset. 
	 */
	public void aggregate() throws SaxException;
	
}
