/**
 * Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.util;

/**
 *
 *  Description: This class encapsulates the linear regression calculations
 *
 * @author Dinh Diep
 * @author gtrester
 *
 */
public interface LinearCurve {

	public double getAvgResidualN();
	public double getAvgResidual();

	public double calculateX(double y);
	public double calculateY(double x);

	public double getCoefficient();
	public double getIntercept();

	public void fitCurve(double[] data, int start, int end);
	public void fitCurveN(double[] data, int start, int end, int N);
}