/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */

package com.xdx.sax.processor.modules.normalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.domain.Assay;
import com.xdx.sax.domain.ConfigurationModel;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;

/**
 * Operates on normalization genes and normalizes all wells for a given plateset.
 *
 * @author gtrester
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 *
 */
public final class HTxAggregateNormalizerImpl extends AbstractAggregateNormalizer {

	private final Log log= LogFactory.getLog(getClass());

	private Double normalizationFactor;

	private final String EXPECTED_CT = "EXPECTED_CT";

	/**
	 * Overwrites initialization method of the superclass. In addition to the standard
	 * initialization we need to create the normalization factor that we want to use
	 * 
	 * @param dataSet the data set we're going to work on
	 */
	public void initialize (SaxDataSet dataSet) {
		log.debug(buildString("Initializing ", getClass().getName()));
		super.initialize(dataSet);
		createNormalizationFactor();
	}

	/**
	 * Creates the normalized cts and load them into the well data objects.
	 *
	 * @throws Exception
	 *
	 */
	public void createNormalizedCT() throws Exception {

		log.debug("ENTER NormalizationSchemeImpl.createNormalizedCT()");
		normalize();
		log.debug("EXIT NormalizationSchemeImpl.createNormalizedCT()");
	}


	/**
	 * Creates the normalizationFactor from the CT values read in from a flat file in this
	 * class' constructor.  The CT values  are used in normalization calculations.
	 *
	 * @param assays
	 * @return a <code>double</code> value representing the normalization factor
	 *
	 * @see com.xdx.analyzer.instances.HTxAnalyzer#createNormalizationFactor(TreeSet assays)
	 * @see com.xdx.analyzer.instances.LTxAnalyzer#createNormalizationFactor(TreeSet assays)
	 * @see com.xdx.analyzer.instances.OtherAnalyzer#createNormalizationFactor(TreeSet assays)
	 */
 	private void createNormalizationFactor() {

   		if (getControls() == null) {
   	    	log.error(buildString("Data set ", this.getDataSet().getPlateSection().getName(), " has no controls, no normalization can happen"));
			this.normalizationFactor = null;
			return;
		}

    	// This set holds the CT delta's for each of the normalization genes.
   		// (Diff between expected CT value for that particular control gene and the
   		//  actual CT value on our plate set)
    	List<Double> deltaControlSet= new ArrayList<Double>();

    	double normDelta= 0.0;
    	Double expectedCT= 0.0;
    	double sumOfDiff= 0.0;
    	double valsUsed= 0.0;

    	int numVals= 0;
    	int index= -1;
    	int start= -1;
    	int stop= -1;

    	// First generate a list of the differences between the actual
    	// CT values and the expected CT value according to the scheme
    	for ( String controlMarker: (Set<String>) getControls().getMarkerNames()) {   		
			// get expected CT from configuration
			expectedCT= Double.valueOf(ConfigurationModel.lookup(
					this.getDataSet().getPlateSet().getTestcode().getTestcodename(),
					this.EXPECTED_CT, controlMarker));

			Double controlVal = getControls().getControlValue(controlMarker);
			
			if (controlVal == null) {
				log.error(buildString("Data set ", this.getDataSet().getPlateSection().getName(), " is missing control ", controlMarker, "=> no normalization"));
				this.normalizationFactor=null;
				return;
			}
			
   	    	normDelta= expectedCT - controlVal;
   	    	log.debug(buildString("Delta for marker ", controlMarker, ": ", normDelta));
   	    	
   	    	deltaControlSet.add(normDelta);
    	}

    	// Now order the list low to high. We will throw out the highest and the lowest value, assuming
    	// we have 4 or more values (Olympic scoring); otherwise we will just use whatever we've got.
    	Collections.sort(deltaControlSet);

    	numVals= deltaControlSet.size();

    	if (numVals == 0) {
			return;
		} else if (numVals <= 4) {

			start= 0;
			stop= numVals;
			valsUsed= (double) numVals;
		} else {
			start= 1;
			stop= (numVals - 1);
			valsUsed= ((double) numVals - 2.0);
    	}

    	for (index= start; index < stop; index++) {
			normDelta= deltaControlSet.get(index);
			sumOfDiff= sumOfDiff + normDelta;
    	}

    	// Original norm factor seems to be rounded !!!!!!
    	if (valsUsed >=2)
    		this.normalizationFactor = (sumOfDiff/valsUsed);
    	else
    		this.normalizationFactor = null;
    	
    	log.debug(buildString("Data set ", this.getDataSet().getPlateSection().getName(), " has normalization factor ", this.normalizationFactor));
 	}

	/**
	 * Returns the a collection of <code>com.xdx.analyzer.domain.Assay</code> objects that have normalized values.
	 *
	 * @param assays
	 * @return a collection of normalized wells
	 * @throws SaxException
	 *
	 */
	public void normalize() {

		log.debug("ENTER HtxAggregateNormalizerImpl.normalizeWells()");

		Double result;

		for (Assay assay : getDataSet().getAssays()) {
			result = null;
			
			if (assay != null && assay.getResult().getWsmxdxct() != null) {
				log.debug(buildString("Normalizing gene ", assay.getAggregate().getName(), ": wsmxdxct=", assay.getResult().getWsmxdxct()));
				if (normalizationFactor != null)
					result = assay.getResult().getWsmxdxct() + normalizationFactor;				
			} else
				log.debug(buildString("No assay data for ", assay.getAggregate().getName(), ": setting wsmnormct to ", result));

			assay.getResult().setWsmnormct(result);
		}
		
		log.debug("EXIT HtxAggregateNormalizerImpl.normalizeWells()");
	}

}