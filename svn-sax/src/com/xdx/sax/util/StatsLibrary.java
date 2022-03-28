/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.util;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.exceptions.SaxException;

/**
  * Math and statistical functions that operate on
  * a set of double values
  *
  * @author Dinh Diep
  * @author gtrester
  * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
  *
  *
  */
public final class StatsLibrary {

	private static final Log log= LogFactory.getLog(StatsLibrary.class);


	private final static double outlierThreshold1= 0.3;
	private final static double outlierThreshold2= 0.5;

	/**
	 *
	 * @param value
	 */
	public StatsLibrary() {
	}

	/**
	 * Convert an array of Double's to an array of doubles.
	 *
	 * @param data
	 * @return the collection of type <code>double</code>, converted from an <code>ArrayList</code>
	 */
	public static double[] toDouble(ArrayList<Double> data) {

		double[] arrDouble= new double[data.size()];

		for (int i= 0; i < data.size(); i++) {
			try {
				arrDouble[i]= data.get(i);
			} catch (NullPointerException e) {
				log.error(buildString("Data error in StatsLibrary.toDouble, value ", i, " is null: ", data.toString()));
				throw new SaxException(e);
			}
		}

		return arrDouble;
	}

	/**
	 * Return the median of an ArrayList of doubles.
	 *
	 * @param data
	 * @return the median in a list of values
	 */
	public static Double median(ArrayList<Double> data) {
		return median(toDouble(data));
	}

  	/**
    * Return the median of an array of doubles.
    *
    * @param data
    * @return the median in a list of values
    */
  	public static Double median(double[] data) {

  		Double median;

  		log.debug(buildString("Calculating median for ", data));
  		
		if (data.length < 2) {
			 return null;
		}

  		Arrays.sort(data);

		int mid= data.length / 2;

		if ( (data.length % 2) == 0) {
			 median= (data[mid - 1] + data[mid]) / 2.0;
		}
		else {
			 median= data[mid];
		}

		log.debug(buildString("Result: ", median));
		
		return median;
	}

 	/**
 	 * Return the mean of an array of doubles.
 	 *
 	 * @param data
 	 * @return the mean in a list of values
 	 */
 	public static Double mean(double[] data) {

		double mean= 0;

		if (data.length == 0) {
			 return null;
		}

		for (int i= 0; i < data.length; i++) {
			 mean += data[i];
		}

		return mean / data.length;
 	}

 	/**
 	 * Return the rms of an array of doubles.
 	 *
 	 * @param values
 	 * @param start_x
 	 * @param end_x
 	 * @return the RMs in a list of values
 	 */
 	static public double RMS(double values[], int start_x, int end_x) {

		double tot_val, mean_val, sum_val, dif;
		int i, n;
		tot_val= 0;

		for (i= start_x; i <= end_x; i++) {
			tot_val= tot_val + values[i];
		}

		n= end_x - start_x + 1;
		mean_val= tot_val / n;
		sum_val= 0;

		for (i= start_x; i <= end_x; i++) {

			dif= values[i] - mean_val;
				sum_val= sum_val + (dif * dif);
		}

		return Math.sqrt(sum_val / n);
	}

 	/**
 	 * Return the standard deviation of an ArrayList of doubles.
 	 *
 	 * @param data
 	 * @return the standard deviation in a list of values
 	 */
 	public static Double stdDev(ArrayList<Double> data) {
		return stdDev(toDouble(data));
 	}

  	/**
  	 * Return the standard deviation of an array of doubles.
  	 *
  	 * @param data
  	 * @return the standard deviation in a list of values
  	 */
  	public static Double stdDev(double[] data) {

		double mean= mean(data);
		final int n= data.length;

		if (n < 1) {
			return null;
		}

		if (n == 1) {
			return 0.0;
		}

		double sum= 0;

		for (int i= 0; i < n; i++) {
			final double v= data[i] - mean;
			sum += v * v;
		}

		return Math.sqrt(sum / (n - 1));
	}

	/**
	 * Return the Log base 10.
	 *
	 * @param d
	 * @return the Log10 in a list of values
	 */
	static public double Log10(double d) {
		return (d <= 0) ?  0  : (Math.log(d) / Math.log(10));
	}

	/**
	 *
	 * @param n
	 * @return the factorial of a value
	 */
	public static int fac(int n) {
		return (n == 0) ? 1 : (n * fac(n-1));
	}

	/**
	 *
	 * @param data
	 * @return the weighted smooth mean of a list of values
	 */
	public static Double wtSmoothMean (ArrayList<Double> data) {
		return wtSmoothMean(toDouble(data));
	}
	
	/**
	 *
	 * @param data
	 * @return the weighted smooth mean of a list of values
	 */
	public static Double wtSmoothMean (double[] data) {

		final int n= data.length;

		if (data.length < 2) {
			return null;
		}

//		double median= getLowMedian(data);
		double median= median(data);
		double sumOfWeightedCts= 0.0;
		double sumOfWeights= 0.0;
		int numberOfCts= n;

		for ( int ctIndex= 0; ctIndex < numberOfCts; ctIndex++ ) {

			double Ct= data[ ctIndex ];
			double absDelta= Math.abs( Ct - median );
			double weight= 0.0;

			if ( absDelta < outlierThreshold1 ) {
				weight= 1.0;
			}
			else if ( absDelta < outlierThreshold2 ) {
				weight= ( absDelta - outlierThreshold2 ) / ( outlierThreshold1 - outlierThreshold2 );
      		}

			sumOfWeightedCts += weight * Ct;
			sumOfWeights += weight;
		}

//		if (sumOfWeights <= 0.0) {
//			sumOfWeights= 1.0;
//		}

//		return sumOfWeightedCts / sumOfWeights;

		Double result;
		if (sumOfWeights == 0.0)
			result = null;
		else
			result = sumOfWeightedCts / sumOfWeights;
		
		return result;
	}

	/**
	 *
	 * @param value Value to be rounded
	 * @param decimalPlace number of decimal places
	 * @return the rounded value
	 */
	public static Double round(final Double value, final int decimalPlace) {

		if ((value == null)  ||  value.isNaN())  {
			return null;
		}

		double inputMask= value;  // assignment avoid stackoverflow
		double resultMask= round(inputMask, decimalPlace);

		return resultMask;
	}

	/**
	 *
	 * @param value Value to be rounded
	 * @param decimalPlace number of decimal places
	 * @return the rounded value
	 */
	public static double round(final double value, int decimalPlace) {

		log.debug(buildString("rounding ", value, " to ", decimalPlace, " decimal places"));

		double orderOfMagnitude= 1;

		while (decimalPlace-- > 0) {
			orderOfMagnitude *= 10.0;
		}

		double result = Math.round(value * orderOfMagnitude) / orderOfMagnitude;
		
		log.debug(buildString("Result: ", result));

		return result;
	}

	/**
	 *
	 * @param stringMask
	 * @return the expandEPower value
	 */
	public static String expandEPower(final String stringMask) {

		log.debug(buildString("expandEPower (String): ", stringMask));
		
		if (stringMask == null) {
			return null;
		}

		String[] tokens= null;
		String powerVal= null;
		String theNumber= null;
		String zeroString= null;
		String leftHalf= null;
		String rightHalf= null;
		int thePower= 0;
		int numZeros= 0;
		int index= 0;
		int tailDigits= 0;

		if (stringMask.indexOf("E") == -1) {
			return stringMask;
		}
		else {
			tokens= stringMask.split("E");
		}

		theNumber= tokens[0];
		powerVal= tokens[1];
		tailDigits= (theNumber.length() - 2);
		thePower= new Integer(powerVal);
		zeroString= "";

		if (thePower < 0) {
			numZeros= (thePower * -1) - 1;
		}
		else {
			numZeros= (thePower * 1)  - tailDigits;
		}

		for (index= 0; index < numZeros; index++) {
			zeroString += "0";
		}

		// If the power was negative, we move the decimal to the left and should
		// subtract one because we're going to pull out the decimal which is 1
		if (thePower < 0) {
				theNumber= theNumber.replaceAll("\\.", "");
				theNumber= buildString("0.", zeroString, theNumber);
		}

		// If the power was smaller than the number of trailing decimal values
		// we need to shift the decimal place over rather than pad with zeros
		else if (thePower < tailDigits) {

			tokens= theNumber.split("\\.");
			leftHalf= tokens[0];
			rightHalf= tokens[1];
			theNumber= buildString(leftHalf, rightHalf.substring(0,(thePower-1)), ".", rightHalf.substring(thePower));
		}

		// If the power is equal to the number of trailing decimal values
		// we just need to pull out the decimal point and return the number
		else if (thePower < tailDigits) {
			theNumber= theNumber.replaceAll("\\.", "");
		}

		// If the power is greater than the number of trailing decimal values
		// we need to pull out the decimal point and pad with extra zeros
		else {

			theNumber= theNumber.replaceAll("\\.", "");
			theNumber= buildString(theNumber, zeroString);
		}

		log.debug(buildString("Result: ", theNumber));
		
		return theNumber;
	}

	/**
	 * Utility method builds Strings; avoids string concatenation.
	 *
	 * @param values
	 * @return the concatenated string
	 */
	private final static String buildString(Object... values) {

		StringBuilder sb= new StringBuilder();

		for (Object object : values) {
			sb.append((object == null)  ?  "" :  object.toString());
		}

		return sb.toString();
	}
}