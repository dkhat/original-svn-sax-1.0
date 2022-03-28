/**
 * Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.util;

import java.util.Enumeration;
import java.util.Vector;

/**
 *
*  Description: This class encapsulates the linear regression calculations
 *
 * @author Dinh Diep
 * @author gtrester
 */
public abstract class AbstractLinearCurve implements LinearCurve {

	/**
	 * Calculate the intercept and coefficient of the linear function y= a + bx.
	 *
	 * @param points A vector of PlotPoints (x,y).
	 * @param N A specific number used to calculate avgResidualN.
	 */
	protected void regress(Vector<PlotPoint> points, int N) {

		regress(points);
		avgResidualN = sumResidual / (points.size() - N);
	}

	/**
	 * Calculate the intercept and coefficient of the linear function y= a + bx.
	 *
	 * In addition, it calculates the sum of residuals (sumResidual), the average residual
	 * (avgResidual), and the maximum residual (maxResidual).
	 *
	 * @param points A vector of PlotPoints (x, y).
	 */
	protected void regress(Vector<PlotPoint> points) {

		double sumx= 0;
		double sumy= 0;
		double sumxx= 0;
		double sumyy= 0;
		double sumxy= 0;

		double maxResidual= 0D;
		PlotPoint p, q;

	    for (Enumeration<PlotPoint> e = points.elements(); e.hasMoreElements(); ) {
	        p = (PlotPoint) e.nextElement();
	        sumx += p.x;
	        sumy += p.y;
	        sumxx += p.x * p.x;
	        sumyy += p.y * p.y;
	        sumxy += p.x * p.y;
		}

		int n= points.size();

		double Sxx= sumxx - sumx * sumx / n;
		double Sxy= sumxy - sumx * sumy / n;

		coefficient= Sxy / Sxx;
		intercept= (sumy - coefficient * sumx) / n;
		sumResidual= 0;
		maxResidual= -9999999;

		for (Enumeration<PlotPoint> e = points.elements(); e.hasMoreElements(); ) {
	        p = (PlotPoint) e.nextElement();
			double residual= Math.abs(p.y - (intercept + (coefficient * p.x)));
			sumResidual += residual;
			maxResidual= Math.max(maxResidual, residual);
		}

		avgResidual= sumResidual / points.size();
	}

	/**
	 * Intercept of the linear curve
	 */
	private double intercept;
	public double getIntercept() { return intercept; }

	/**
	 * Coefficient of the linear curve
	 */
	private double coefficient;
	public double getCoefficient() { return coefficient; }

	/**
	 * Average residual= (Sum of residuals) / (number of plot points)
	 */
	private double avgResidual;
	public double getAvgResidual() { return avgResidual; }

	/**
	 * Average residual= (Sum of residuals) / (number of plot points - n).<br>
	 * n is a specific number provided by the user.
	 */
	private double avgResidualN;
	public double getAvgResidualN() { return avgResidualN; }
	public void setAvgResidualN(double value) { avgResidualN= value; }

	/**
	 * Sum of residuals
	 */
	private double sumResidual;
	public double getSumResidual() { return sumResidual; }
}