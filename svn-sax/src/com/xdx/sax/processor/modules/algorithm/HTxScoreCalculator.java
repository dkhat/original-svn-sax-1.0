/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.processor.modules.algorithm;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.AggMarkerResultBO;
import com.xdx.sax.bo.AggSectionResultBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.dao.AggSectionResultDAO;
import com.xdx.sax.domain.Assay;
import com.xdx.sax.domain.ConfigurationModel;
import com.xdx.sax.domain.SaxDataSet;

/**
 * @author smeier
 *
 */
public class HTxScoreCalculator extends AbstractScoreCalculator {

	private final Log log = LogFactory.getLog(getClass().getName());
	private final String MARKERTYPE_ALGORITHM_GENE = "Algorithm Gene";
	
	// results hashtable
	private Hashtable<String, AggMarkerResultBO> resultsByAggregate = null;
	
	public void initialize(SaxDataSet dataSet) {
		log.debug(buildString("Initializing ..."));
		
		// initialize superclass
		super.initialize(dataSet);
		
		// create hashtable of results keyed by aggregate name (= marker name)
		this.resultsByAggregate = new Hashtable<String, AggMarkerResultBO>();
		for (Assay assay : this.dataSet.getAssays()) {
			log.debug(buildString("Processing assay ", assay.getAggregate().getName()));
			MarkerBO mk = (MarkerBO)assay.getMarkers().toArray()[0]; 
			if (mk.getMarkertype().getName().equals(MARKERTYPE_ALGORITHM_GENE))
				this.resultsByAggregate.put(assay.getAggregate().getName(), assay.getResult());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.processor.modules.algorithm.ScoreCalculator#calculate()
	 */
	public void calculate() {
		log.debug(buildString("Calculating score ..."));

		double ldaConstant = new Double(ConfigurationModel.lookup("HTx", "HTxScoreCalculator", "LDA_CONSTANT")).doubleValue();
		double mapConstantA = new Double(ConfigurationModel.lookup("HTx", "HTxScoreCalculator", "MAP_CONSTANT_A")).doubleValue();
		double mapConstantB = new Double(ConfigurationModel.lookup("HTx", "HTxScoreCalculator", "MAP_CONSTANT_B")).doubleValue();
		double mapStdErr = new Double(ConfigurationModel.lookup("HTx", "HTxScoreCalculator", "MAP_STD_ERR")).doubleValue();
		Double algoScore;
		
		algoScore = computeScore(ldaConstant);
		
		if (algoScore != null) {
			// find section result record for this assay
			AggSectionResultBO sectionResult = 
				(AggSectionResultBO) new AggSectionResultDAO().findBySetSection(
						dataSet.getPlateSet().getId(),
						dataSet.getPlateSection().getId());
			
			// raw score
			sectionResult.setAlgoscore(algoScore);
			// mapped score
			sectionResult.setMappedscore(computeMappedScore(algoScore, mapConstantA, mapConstantB));
			// mapped high score
			sectionResult.setMappedhighscore(computeMappedScore(algoScore+mapStdErr, mapConstantA, mapConstantB));
			// mapped low score
			sectionResult.setMappedlowscore(computeMappedScore(algoScore-mapStdErr, mapConstantA, mapConstantB));
		}
		log.debug(buildString("=== done ==="));
	}

	/**
	 * Perform essential statistical computations that are part of an AlloMapComputations test.
	 *
	 * @param assays
	 * @param ldaConstant
	 * @return the name of an instantiated <code>double</code>
	 */
	private Double computeScore(double ldaConstant) {
		log.debug(buildString("Calculating algorithm score for plate set", dataSet.getPlateSet().getId(), " and section ", dataSet.getPlateSection().getId()));

		Double score= null;
		double geneCoeff= 0;

		score= 0.0;
		log.debug(buildString("Processing genes ", resultsByAggregate.keySet()));
		
		for (String marker : resultsByAggregate.keySet()) {
			log.debug(buildString("Processing marker ", marker));
			
			// TODO was: getMedianNormalizedCT !!!!!
			if (resultsByAggregate.get(marker).getWsmnormct() == null) {
				log.debug(buildString("No normalized wsm CT for gene ", marker));
				return null;
			}
			else {
				geneCoeff= new Double(ConfigurationModel.lookup("HTx", "HTxGeneCoefficient", marker)).doubleValue();

				score += resultsByAggregate.get(marker).getWsmnormct() * geneCoeff;
				log.debug(buildString("temp score: ", score));
			}
		}

		score += ldaConstant;

		log.debug(buildString("Final score: ", score));
		return score;
	}

	/**
	 * Perform essential statistical mapped computations that are part of an AlloMapComputations test.
	 *
	 * @param score
	 * @param mapConstA
	 * @param mapConstB
	 * @return the name of an instantiated <code>double</code>
	 *
	 * @see com.xdx.analyzer.domain.SAXDataSetImpl#calculateScores()
	 */
	private Double computeMappedScore(Double score, double mapConstA, double mapConstB) {

		Double mapScore= null;

		if (score == null)
			return null;
		
		if ( ! (Double.isNaN(score))) {

			double x= mapConstA + (mapConstB * score);
			mapScore= 40 * (Math.exp(x) / (1 + Math.exp(x)));
		}

		if (mapScore < 0) {
			mapScore= new Double(0);
		}

		if (mapScore > 40) {
			mapScore= new Double(40);
		}

		return mapScore;
	}

}
