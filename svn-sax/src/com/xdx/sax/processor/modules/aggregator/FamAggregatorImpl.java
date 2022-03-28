/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor.modules.aggregator;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.AggMarkerResultBO;
import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.domain.Assay;

/**
 * 
 * @author smeier
 *
 * The FAM aggregation class is an auxilliary component used ONLY for manufacturing data.
 * Some of the manufacturing tools use aggregated FAM data, and in the legacy system this
 * data was aggregated using the weighted smooth mean algorithm devised for CT values.
 * 
 * This way of calculation is not useful for FAM data, but for backward compatibility purposes
 * we must do the FAM value aggregation exactly the way it was done in the legacy system.
 * 
 * This component wraps the original calculation of the aggregates for FAM values.
 * 
 * This is how the aggregation works:
 * - for a given plateset/section/assay, get the well data
 * - consider all wells that have an XDx CT
 * - determine 'median' (if 3 values, take true median. if 2 values, take the lower one)
 * - determine weights (for FAM data the weight for non-median values is almost always 0)
 * - agg val is sum of (weight*value)
 * 
 * Conclusion: in almost all cases the 'aggregate' value is the median (if 3 CT and FAM values)
 *             or the lower value (if 2 CTs/FAM values)
 * 
 */
public class FamAggregatorImpl extends AbstractAggregator {

	private final Log log = LogFactory.getLog(getClass());
	
	public void aggregate() {
		log.debug(buildString("Executing FAM aggregation ..."));

		// process all assays
		for (Assay assay : this.getDataSet().getAssays()) {
			log.debug(buildString("Aggregating assay: Aggregate ", assay.getAggregate().getId(),
					" and result ", assay.getResult().getId()));

			AggMarkerResultBO result = assay.getResult();
			result.setWsmBaselineFam(aggregateBaselineFam(assay));
			result.setWsmSaturationFam(aggregateSaturationFam(assay));
		}
	}

	private Double aggregateBaselineFam (Assay assay) {
		Double[] vals = getBaselineFamArray(assay);
		
		Double val;
		
		if (vals.length > 1)
			val = aggregateFamValues(vals);
		else
			val = null;
		
		log.debug(buildString("wsm baseline fam for assay ", assay.getAggregate().getName(), " is ", val));
		
		return val;
	}
	
	private Double aggregateSaturationFam (Assay assay) {
		Double[] vals = getSaturationFamArray(assay);
		
		Double val;
		
		if (vals.length > 1)
			val = aggregateFamValues(vals);
		else
			val = null;		
		
		log.debug(buildString("wsm saturation fam for assay ", assay.getAggregate().getName(), " is ", val));
		
		return val;
	}
	
	private Double[] getBaselineFamArray(Assay assay) {
		HashSet<Double> baselineFamArray = new HashSet<Double>();

		//int idx=0;
		for (RawWellDataBO rawValue : assay.getRawData())
			// In the original system, only wells that have a valid CT are even
			// considered for aggregation !!
			if (rawValue.getXdxct() != null)
				baselineFamArray.add(rawValue.getBaselinefam());
			
		return baselineFamArray.toArray(new Double[baselineFamArray.size()]);
	}
	
	private Double[] getSaturationFamArray(Assay assay) {
		HashSet<Double> saturationFamArray = new HashSet<Double>();

		//int idx=0;
		for (RawWellDataBO rawValue : assay.getRawData())
			// In the original system, only wells that have a valid CT are even
			// considered for aggregation !!
			if (rawValue.getXdxct() != null)
				saturationFamArray.add(rawValue.getSaturationfam());
			
		return saturationFamArray.toArray(new Double[saturationFamArray.size()]);
	}
	
	/**
	 * This is the original algorithm used to calculate weighted smooth mean values for CTs.
	 * It's calibrated for values that are usually not more than 1 apart - FAM values almost always
	 * are apart by much more than that.
	 * 
	 * @param data array of individual FAM values
	 * @return aggregated FAM value
	 */
	private Double aggregateFamValues (Double[] data) {

		final double outlierThreshold1 = 0.3;
		final double outlierThreshold2 = 0.5;
		final int n = data.length;
		
		if (n < 2) {
			return Double.NaN;
		}

		Arrays.sort(data);
		int index = (n - 1) / 2;
		Double referenceVal = data[index];

		Double sumOfWeightedCts = 0.0;
		Double sumOfWeights = 0.0;
		int numberOfCts = n;

		for ( int ctIndex = 0; ctIndex < numberOfCts; ctIndex++ ) {

			Double Ct = data[ ctIndex ];
			Double absDelta = Math.abs( Ct - referenceVal );
			Double weight = 0.0;

			if ( absDelta < outlierThreshold1 ) {
				weight = 1.0;
			}
			else if ( absDelta < outlierThreshold2 ) {
				weight = ( absDelta - outlierThreshold2 ) / ( outlierThreshold1 - outlierThreshold2 );
         	}

			sumOfWeightedCts += weight * Ct;
			sumOfWeights += weight;
		}
		if ( sumOfWeights <= 0.0 ) sumOfWeights = 1.0;

		return sumOfWeightedCts / sumOfWeights;
	}

}
