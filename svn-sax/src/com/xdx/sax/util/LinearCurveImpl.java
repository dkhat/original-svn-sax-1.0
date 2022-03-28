/**
 * Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.util;

import java.util.Vector;

/**
 *
 *  Description: This class encapsulates the linear regression calculations
 *
 * @author Dinh Diep
 * @author gtrester
 *
 */
public class LinearCurveImpl extends AbstractLinearCurve {

	public LinearCurveImpl() {
		super();
	}

	/**
	 * Return the predicted value y.
	 *
	 * @param x The independent variable x
	 * @return the predicted value (Y)
	 */
	public double calculateY(double x) {
		return getIntercept() + (getCoefficient() * x);
	}

	/**
	 * Return the independent variable x.
	 *
	 * @param y The predicted value y
	 * @return the independent variable (X)
	 */
	public double calculateX(double y) {
		if (getCoefficient() == 0)
			return 0;
		else
			return ((y - getIntercept()) / getCoefficient());
	}

	/**
	 * Fit a linear curve with x values from 'start' to 'end', and y values
	 * are the corresponding elements with the index x in the data array 'data'.
	 *
	 * @param data An array of double's
	 * @param start Starting value of x
	 * @param end Ending value of x
	 * @throws Exception
	 */
	public void fitCurve(double[] data, int start, int end) {
		fitCurveN(data, start, end, 0);
	}

	/**
	 * Same as the function fitCurve.
	 * <br>In addition to the normal average residual, which is the ratio
	 * of sumResidual over the number of data points, a new average residual (the variable avgResidualN)
	 * is calculated using the denominator as the number of data points minus 'n':
	 * <br>avgResidual= sumResidual / number of (x,y) pairs
	 * <br>avgResidualN= sumResidual / (number of (x,y) pairs - n).
	 *
	 * @param data An array of double's
	 * @param start Starting value of x
	 * @param end Ending value of x
	 * @param N
	 * @throws Exception
	 */
	public void fitCurveN(double[] data, int start, int end, int N) {

		Vector<PlotPoint> points= new Vector<PlotPoint>();

		for (int i= start; i <= end; i++) {

			points.add(new PlotPoint(i, data[i]));
		}

		regress(points, N);
	}
}
