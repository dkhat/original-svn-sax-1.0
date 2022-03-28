/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor.modules.aggregator;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.AggMarkerResultBO;
import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.domain.Assay;
import com.xdx.sax.domain.ConfigurationModel;
import com.xdx.sax.util.StatsLibrary;

public class HTxAggregatorImpl extends AbstractAggregator {

	private final Log log = LogFactory.getLog(getClass());
	
	private final Double LOW_CT_THRESHOLD = new Double(ConfigurationModel.lookup("HTx", "HtxAggregator", "LOW_CT_THRESHOLD"));
	private final Double HIGH_CT_THRESHOLD= new Double(ConfigurationModel.lookup("HTx", "HtxAggregator", "HIGH_CT_THRESHOLD"));

	public void aggregate() {
		log.debug(buildString("Executing aggregation ..."));

		// process all assays
		for (Assay assay : this.getDataSet().getAssays()) {
			log.debug(buildString("Aggregating assay: Aggregate ", assay.getAggregate().getId(),
					" and result ", assay.getResult().getId()));

			AggMarkerResultBO result = assay.getResult();
			result.setWsmxdxct(calcWsmRawCt(assay));
			result.setMedianxdxct(calcMedianRawCt(assay));
			result.setWsmnormct(null);
			result.setPlatestddev(calcStdDev(assay));
		}
	}

	private Double calcWsmRawCt (Assay assay) {
		ArrayList<Double> ctVals = getXdxCts(assay);
		
		if (ctVals.size() > 1)
			return StatsLibrary.wtSmoothMean(ctVals);
		else
			return null;		
		
	}

	private Double calcMedianRawCt (Assay assay) {
		ArrayList<Double> ctVals = getXdxCts(assay);
		
		if (ctVals.size() > 1)
			return StatsLibrary.median(ctVals);
		else
			return null;		
		
	}

	private Double calcStdDev (Assay assay) {
		ArrayList<Double> ctVals = getXdxCts(assay);
		
		if (ctVals.size() > 1)
			return StatsLibrary.stdDev(ctVals);
		else
			return null;		
		
	}

	private ArrayList<Double> getXdxCts(Assay assay) {
		ArrayList<Double> ctVals = new ArrayList<Double>();
		Double xdxCt;
		int errornum;
		
		//check raw data for thresholds and errors
		for (RawWellDataBO rawValue : assay.getRawData()) {
			xdxCt = rawValue.getXdxct();
			errornum = rawValue.getErrornum();
			
			if ((xdxCt != null) && 
				(xdxCt > 0) && 
				(xdxCt >= LOW_CT_THRESHOLD) &&
				(xdxCt <= HIGH_CT_THRESHOLD) &&
				(errornum == 0 || errornum == 2 || errornum == 32))
			{
				log.debug(buildString("Valid well: ", rawValue.getId()));
				ctVals.add(xdxCt);
			} else {
				log.debug(buildString("Invalid well: ", rawValue.getId()));
			}
		}
		
		return ctVals;
	}

}
