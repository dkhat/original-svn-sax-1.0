/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import com.xdx.sax.bo.QcCriteriaStepBO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.bo.QcCriteriaStepResultBO;

/**
 * @author scchavis
 * @author smeier
 *
 */
public interface QcCriteriaStep {
	
	public void evaluate();
	
	public void initialize(SaxDataSet dataSet, QcCriteriaStepBO step);
	
	public QcCriteriaStepResultBO getResult();
}
