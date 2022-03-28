/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.calc;

import com.xdx.sax.domain.SaxDataSet;

/**
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
public interface Aggregator extends Calculator {

	/**
	 * This method performs a one-off aggregation on the
	 * given dataset.
	 * 
	 * @param dataSet the data set to operate on
	 * @return result data set
	 */
	public SaxDataSet aggregate(SaxDataSet dataSet);
	
}
