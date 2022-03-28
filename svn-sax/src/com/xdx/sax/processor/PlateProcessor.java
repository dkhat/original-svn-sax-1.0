/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor;

import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 *
 */
public interface PlateProcessor {

	/**
	 * This method processes a data file
	 * 
	 * @param dataFileName
	 * @return plate barcode associated with the file
	 * @throws SaxException
	 */
	public void process(String plateBarcode) throws SaxException;
}
