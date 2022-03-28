/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.QcCriteriaStepTemplateBO;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 *
 */
public abstract class AbstractQcTemplate extends BusinessObject {

    // Class Constants - Template Keys
    public static final String  ASSAY1_KEY              = "{ASSAY1}" ;
    public static final String  ASSAY2_KEY              = "{ASSAY2}" ;
    public static final String  YVAL_KEY                = "{Y}" ;
    public static final String  ZVAL_KEY                = "{Z}" ;
    public static final String  NORM_KEY                = "{NORM_SCHEME}" ;
    public static final String  PROCESSOR_KEY           = "{PROCESSOR_STATUS}" ;

    // Low level template object
    private QcCriteriaStepTemplateBO template = null;
    
    /**
     * 
     * @param template
     */
    public AbstractQcTemplate (QcCriteriaStepTemplateBO template) {
    	if (template == null)
    		throw new SaxException("Cannot instantiate null template");
    	
    	this.template = template;
    }
    
    /**
     * 
     * @return
     */
    public String getTemplateString() {
    	return template.getTemplatestring();
    }
    
    /**
     * 
     * @return
     */
    public String getName() {
    	return template.getName();
    }

}
