/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 *
 */
public class QcCriteriaStepSdsFileCheck extends AbstractQcCriteriaStep {

	//
	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.qc.criteria.step.AbstractQcCriteriaStep#evaluate()
	 */
	@Override
	public void evaluate() {
		log.debug(buildString("Evaluating QC criteria"));
		
		// check initialization
		assertInitialized();
		
		boolean pass=true;
		long expectedErrorCode;
		String errorMessage = "";
		
		try {
			expectedErrorCode = new Integer(parseTemplateString()).longValue();
		} catch (Exception e) {
			log.error("Cannot correctly parse template string");
			throw new SaxException(e);
		}
		
		for (PlateBO plate : dataSet.getPlateSet().getPlates()) {
			if (plate.getProcessorError() != expectedErrorCode) {
				log.debug(buildString("QC criteria failed"));
				pass = false;
				errorMessage = step.getFailreturnresult();
				break;
			}
		}

		// TODO: implement borderline check
		setResult(pass, false, errorMessage);
	}

}
