/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.AggSectionResultBO;
import com.xdx.sax.bo.QcCriteriaBO;
import com.xdx.sax.bo.QcCriteriaStepBO;
import com.xdx.sax.bo.QcCriteriaStepResultBO;
import com.xdx.sax.bo.QcCriteriaStepTemplateBO;
import com.xdx.sax.bo.QcResultFinalBO;
import com.xdx.sax.dao.AggSectionResultDAO;
import com.xdx.sax.dao.QcCriteriaDAO;
import com.xdx.sax.dao.QcResultFinalDAO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.qc.criteria.step.QcCriteriaStep;

/**
 * @author smeier
 *
 */
public class QcProcessorImpl extends AbstractQcProcessor {

	private final Log log = LogFactory.getLog(getClass().getName());

	/**
	 * @see com.xdx.sax.qc.AbstractQcProcessor#executeQc()
	 */
	public void executeQc() {
		log.info(buildString("Executing QC schema ", getQcSchema().getId()));
		
		QcCriteriaStepResultBO res = null;
		boolean pass = true;
		String msg = "";
		
		List<QcCriteriaBO> criteriaSet = new QcCriteriaDAO().findBySchemaSorted(getQcSchema().getId());
		for (QcCriteriaBO criteria : criteriaSet) {
			log.debug(buildString("Evaluating QC criteria ", criteria.getCriterianame()));
			for (QcCriteriaStepBO step : criteria.getQccriteriasteps()) {
				res = executeCheck(getDataSet(), step);
				
				if ((!res.getPass()) && pass) {
					pass = false;
					msg = res.getResultvalue();
				}
				log.debug(buildString("msg now: ", msg));
			}
		}

		setSectionResult(pass, msg);

		setFinalResult(pass, msg);

		log.info(buildString("=== done ==="));
	}

	/**
	 * Record the final verdict from SAX
	 * 
	 * @param pass did the sample pass?
	 * @param msg 
	 */
	private void setFinalResult(boolean pass, String msg) {
		QcResultFinalBO finalResult = 
			new QcResultFinalDAO().findByPlateSetSection(
					getDataSet().getPlateSet().getId(), 
					getDataSet().getPlateSection().getId());
		if (pass) {
			finalResult.setSaxqcresult("PASS");
			finalResult.setQcFailReason(null);
		} else {
			finalResult.setSaxqcresult("FAIL");
			finalResult.setUserqcresult("FAIL");
			finalResult.setQcFailReason(msg);
			
			// now reset scores that might have been calculated to null
			// because QC failed
			
			// find section result record for this assay
			AggSectionResultBO sectionResult = 
				(AggSectionResultBO) new AggSectionResultDAO().findBySetSection(
						getDataSet().getPlateSet().getId(),
						getDataSet().getPlateSection().getId());
			
			// raw score
			sectionResult.setAlgoscore(null);
			// mapped score
			sectionResult.setMappedscore(null);
			// mapped high score
			sectionResult.setMappedhighscore(null);
			// mapped low score
			sectionResult.setMappedlowscore(null);
		}
	}

	/**
	 * Record the final verdict of SAX for the sample. Records reason for
	 * failure if sample did not pass
	 * 
	 * @param pass Did the sample pass?
	 * @param msg Reason for failure
	 */
	private void setSectionResult(boolean pass, String msg) {
		AggSectionResultBO sectionResult = 
			new AggSectionResultDAO().findBySetSection(
					getDataSet().getPlateSet().getId(), 
					getDataSet().getPlateSection().getId());
		if (pass) {
			sectionResult.setQcstatus("PASS");
			sectionResult.setQcfailreason(null);
		} else {
			sectionResult.setQcstatus("FAIL");
			sectionResult.setQcfailreason(msg);
		}
	}

	@SuppressWarnings("unchecked")
	private QcCriteriaStepResultBO executeCheck (SaxDataSet dataSet, QcCriteriaStepBO step) {
		QcCriteriaStepTemplateBO template = step.getQccriteriasteptemplate();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			Class<QcCriteriaStep> clazz = (Class<QcCriteriaStep>) cl.loadClass(template.getClassname());
				
			log.debug("Instantiating");
			QcCriteriaStep check = (QcCriteriaStep) clazz.newInstance();
			
			log.debug("Initializing with data set");
			check.initialize(dataSet, step);
			
			check.evaluate();
			   
			return check.getResult();		
			
		} catch (ClassNotFoundException e) {
			log.error(buildString("Cannot load class ", template.getClassname()));
			throw new SaxException(e);
		} catch (IllegalAccessException e) {
			log.error(buildString("Illegal access exception for class ", template.getClassname()));
			throw new SaxException(e);
		} catch (InstantiationException e) {
			log.error(buildString("Illegal instantiation exception for class ", template.getClassname()));
			throw new SaxException(e);
		}
	}
	   
}
