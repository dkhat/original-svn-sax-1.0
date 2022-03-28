/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor.modules.normalizer;

/**
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
public interface AggregateNormalizer {

	/**
	 * This method performs a one-off normalization on the
	 * given dataset.
	 * 
	 * @param dataSet the data set to operate on
	 * @return result data set
	 */
	public void normalize();
	
}
