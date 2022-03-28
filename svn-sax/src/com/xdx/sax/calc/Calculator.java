/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.calc;

import com.xdx.sax.domain.SaxDataSet;

/**
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
public interface Calculator {

	/**
	 * @return SaxDataSet
	 */
	public SaxDataSet getDataSet();

	/**
	 * @param dataSet the data set the operation is to be performed on
	 */
	public void setDataSet(SaxDataSet dataSet);

	/**
	 * This method performs the actual calculation on the given
	 * SaxDataSet. The type of calculation is specified by the
	 * implementing class.
	 */
	public void calculate();
}
