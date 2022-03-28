/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor.modules.normalizer;

import com.xdx.sax.exceptions.SaxException;

/**
 * This is the interface for all normalization methods
 * that normalize the raw plate data
 * 
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
public interface RawDataNormalizer {

	/**
	 * This method performs a one-off normalization on the
	 * given dataset. 
	 */
	public void normalize() throws SaxException;
	
}
