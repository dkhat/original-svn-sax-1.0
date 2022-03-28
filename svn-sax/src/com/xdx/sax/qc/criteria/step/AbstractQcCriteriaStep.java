/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.qc.criteria.step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.QcCriteriaStepBO;
import com.xdx.sax.bo.QcCriteriaStepResultBO;
import com.xdx.sax.dao.QcCriteriaStepResultDAO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.qc.criteria.QcTemplateImpl;
import com.xdx.sax.util.StatsLibrary;
import com.xdx.sax.util.StringLibrary;

/**
 * @author scchavis
 * @author smeier
 *
 */
public abstract class AbstractQcCriteriaStep extends BusinessObject implements QcCriteriaStep {
	
	//
	private static final long serialVersionUID = 1L;

	// Log object
	private final Log log = LogFactory.getLog(getClass());
	
	// Class members - all of them are accessible to implementors
	protected SaxDataSet dataSet = null;
	protected QcCriteriaStepBO step = null;
	protected QcTemplateImpl template = null;
	private QcCriteriaStepResultBO result = null;
	
	/**
	 * 
	 * @param plateSet
	 * @param plateSection
	 * @param step
	 */
	public void initialize(SaxDataSet dataSet, QcCriteriaStepBO step) {
		
		this.dataSet = dataSet;
		this.step = step;
		this.template = new QcTemplateImpl(step.getQccriteriasteptemplate());
	}
	
    /****************************************************************************
     * Return the template string with the appropriate fields filled in         *
     * This function takes the template string associated with the template     *
     * object, i.e., {Y} <= {ASSAY1} - {ASSAY2} <= {Z} and substitutes the      *
     * specific values associated with this criteria and returns the new string *
     *                                                                          *
     * So the template string: {Y} <= {ASSAY1} - {ASSAY2} <= {Z}                *
     * might result in a return string like: 5 <= 18S - LTP <= 23               *
     ****************************************************************************/
     protected String parseTemplateString() {
    	 log.debug(buildString("Parsing template string"));

    	 // check initialization
    	 assertInitialized();

    	 // null template string?
    	 if (this.template.getTemplateString() == null)
    		 return "";


        String  templateStr = this.template.getTemplateString() ;
        String  escapers    = "{}" ;
        String  operator    = null ;
        String  tokenMatch1 = null ;
        String  tokenMatch2 = null ;
        String  assay1 = step.getAssay1sub();
        Double assay1val;
        String  assay2 = step.getAssay2sub();
        Double assay2val;

        // replace token {ASSAY1} with the weighted smooth mean value of the assay
		if(assay1 != null) {
			if (dataSet.getAssay(assay1) != null) {
				assay1val = getAssayValue(assay1);
				if (assay1val == null) return null;
		        templateStr = templateStr.replaceAll(StringLibrary.escapeChars(QcTemplateImpl.ASSAY1_KEY, escapers), assay1val.toString());
			} else {
				log.debug(buildString("No value for assay", assay1));
				return null;
			}
		}

        // replace token {ASSAY2} with the weighted smooth mean value of the assay
		if(assay2 != null) {
			if (dataSet.getAssay(assay2) != null) {
				assay2val = getAssayValue(assay2);
				if (assay2val == null) return null;
		        templateStr = templateStr.replaceAll(StringLibrary.escapeChars(QcTemplateImpl.ASSAY1_KEY, escapers), assay2val.toString());
			} else {
				log.debug(buildString("No value for assay", assay2));
				return null;
			}
		}

		// replace comparison constants
        if (step.getYvalsub() != null)       { templateStr = templateStr.replaceAll(StringLibrary.escapeChars(QcTemplateImpl.YVAL_KEY, escapers), step.getYvalsub()) ; }
        if (step.getZvalsub() != null)       { templateStr = templateStr.replaceAll(StringLibrary.escapeChars(QcTemplateImpl.ZVAL_KEY, escapers), step.getZvalsub()) ; }

        if (step.getProcessorStatus() != null)     { templateStr = templateStr.replaceAll(StringLibrary.escapeChars(QcTemplateImpl.PROCESSOR_KEY, escapers), step.getProcessorStatus()) ; }
        if (step.getNormalizationClass() != null)    { templateStr = templateStr.replaceAll(StringLibrary.escapeChars(QcTemplateImpl.NORM_KEY, escapers), step.getNormalizationClass()) ; }


        // Since Assay 2 is optional, it may not have been replaced; try to swap out " - {ASSAY2}" and if that
        // doesn't work, just swap the Assay-2 template substitution string with the constant 0
        templateStr = templateStr.replaceAll(StringLibrary.escapeChars(" - "+QcTemplateImpl.ASSAY2_KEY, escapers), "") ;
        templateStr = templateStr.replaceAll(StringLibrary.escapeChars(QcTemplateImpl.ASSAY2_KEY,       escapers), "0") ;


        // Since users can supply a Y value, a Z value, or both, we need to pull out any left over {Y} type strings
        // But we need to pull out the comparison operator associated with the Y and/or Z value; currently all criteria use either
        // the "<=" operator or the ">=" operator, so we just need to check which one we should be using before we begin
        if (((step.getYvalsub() != null) && (step.getZvalsub() == null)) || ((step.getYvalsub() == null) && (step.getZvalsub() != null))) {

            if (templateStr.indexOf("<=") != -1)        { operator = "<=" ; }
            else if (templateStr.indexOf(">=") != -1)   { operator = ">=" ; }

            if (step.getYvalsub() == null) {

                tokenMatch1 = QcTemplateImpl.YVAL_KEY+" "+operator+" " ;
                tokenMatch2 = " "+operator+" "+QcTemplateImpl.YVAL_KEY ;
            }

            else if (step.getZvalsub() == null) {

                tokenMatch1 = QcTemplateImpl.ZVAL_KEY+" "+operator+" " ;
                tokenMatch2 = " "+operator+" "+QcTemplateImpl.ZVAL_KEY ;
            }

             templateStr = templateStr.replaceAll(StringLibrary.escapeChars(tokenMatch1, escapers), "") ;
             templateStr = templateStr.replaceAll(StringLibrary.escapeChars(tokenMatch2, escapers), "") ;
        }

        return templateStr ;
     }

     /****************************************************************************
      * Take the template string, with the gene CT values and the Y/Z values     *
      * fully substituted, and perform the math implied by the string            *
      ****************************************************************************/
      protected boolean doTheMath(String mathExpression) {

         if (mathExpression == null) { return false ; }

         String[]    tokens          = null ;
         String      compOperator    = null ;
         String      leftStr         = null ;
         String      rightStr        = null ;
         String      centerStr       = null ;
         Double      leftVal         = null ;
         Double      rightVal        = null ;
         Double      centerVal       = null ;
         boolean     leftResult      = false ;
         boolean     rightResult     = false ;

         // Check for comparison operator
         tokens = mathExpression.split("<=") ;

         if (tokens.length > 1)  { compOperator = "<=" ; }
         else                    { compOperator = ">=" ; }


         // Currently, the most complicated template is: {Y} <= {ASSAY1} - {ASSAY2} <= {Z}
         // so we may have three tokens: left, X, and right

         tokens = mathExpression.split(compOperator) ;
         if (tokens.length == 3) {
        	 leftStr = tokens[0].trim();
        	 centerStr = tokens[1].trim();
        	 rightStr = tokens[2].trim();
         } else if (tokens.length == 2) {
        	 leftStr = tokens[0].trim();
        	 centerStr = null;
        	 rightStr = tokens[1].trim();
         } else {
        	 log.error(buildString(" [!!!] The constant value '", mathExpression, "' resolves to TRUE"));
        	 return true;
         }

         // Any of the center, left, or right pieces may required
         // further splitting (i.e., there's a substraction involved)
         centerVal   = parseSubtraction(centerStr) ;
         leftVal     = parseSubtraction(leftStr) ;
         rightVal    = parseSubtraction(rightStr) ;


         // If we have a left, middle, and right value i.e., Y <= FISH <= Z,
         // then we resolve the individual comparisons (Y <= FISH) and
         // (FISH <= Z) and return the cummulative results

         if (centerVal != null) {

             if (compOperator.equals("<=")) {
                 leftResult  = (leftVal.doubleValue() <= centerVal.doubleValue()) ;
                 rightResult = (centerVal.doubleValue() <= rightVal.doubleValue()) ;
             }

             else {
                 leftResult  = (leftVal.doubleValue() >= centerVal.doubleValue()) ;
                 rightResult = (centerVal.doubleValue() >= rightVal.doubleValue()) ;
             }
         }

         // We have a one-sided comparison (Y <= FISH) *or* (FISH <= Z)
         // so we can perform the comparison directly and send the result back
         else if ((leftVal != null) && (rightVal != null)) {

             if (compOperator.equals("<="))  { leftResult  = (leftVal.doubleValue() <= rightVal.doubleValue()) ; }
             else                            { leftResult  = (leftVal.doubleValue() >= rightVal.doubleValue()) ; }

             rightResult = true ;
         }

         // Blarg
         return (leftResult && rightResult) ;
     }


     /****************************************************************************
      * This takes a string of the form A - B and returns the mathematic result  *
      * as a Double object. In the case where the string provided is just a      *
      * number, A, the number itself is returned. Numbers can be positive or     *
      * negative; the only restriction is that the subtraction character be      *
      * surrounded by spaces, i.e., " - " to differentiate it from the negative  *
      * sign.                                                                    *
      ****************************************************************************/
      private Double parseSubtraction(String mathText) {

         String[]    tokens          = null ;
         String      leftString      = null ;
         String      rightString     = null ;
         Double      resolvedValue   = null ;
         Double      tempVal1        = null ;
         Double      tempVal2        = null ;
         double      mathResult      = 0 ;


         // We have a subtraction statement to run
         if ((mathText != null) && (mathText.indexOf(" - ") != -1)) {

             tokens = mathText.split(" - ") ;

             if (tokens.length == 2) {

                 // If the number value exceeds the given space and has been converted to scientific notation
                 // i.e., 5.09573E-4, we need to expand the number to it's full form (0.000509573) before casting
                 // or performing the math... for some reason Java kind of chokes on strings in scientific notation
                 // when you try to cast them to Double and/or do math with it.
                 if (tokens[0] == null)  { leftString = null ; }
                 else                    { leftString = StatsLibrary.expandEPower(tokens[0].replaceAll(" ", "")) ; }

                 if (tokens[1] == null)  { rightString = null ; }
                 else                    { rightString = StatsLibrary.expandEPower(tokens[1].replaceAll(" ", "")) ; }

                 // Make sure we're working with good information
                 if ((leftString == null) || (rightString == null))  { return resolvedValue ; }
                 if (!StringLibrary.isFloat(leftString))               { return resolvedValue ; }
                 if (!StringLibrary.isFloat(rightString))              { return resolvedValue ; }

                 // Do do the math
                 tempVal1    = new Double(leftString) ;
                 tempVal2    = new Double(rightString) ;
                 mathResult  = (tempVal1.doubleValue() - tempVal2.doubleValue()) ;
             }

             // We have an incomplete string of the form "A - " or " - A" so just return the number
             else if (tokens.length == 1) {

                 // See comment above about scientific notation
                 if (tokens[0] == null)  { leftString = null ; }
                 else                    { leftString = StatsLibrary.expandEPower(tokens[0].replaceAll(" ", "")) ; }

                 if (leftString == null)                 { return resolvedValue ; }
                 if (!StringLibrary.isFloat(leftString))   { return resolvedValue ; }

                 tempVal1    = new Double(leftString) ;
                 mathResult  = tempVal1.doubleValue() ;
             }

             else { return resolvedValue ; }

             resolvedValue = new Double(mathResult) ;
         }

         // We were just given an individual number
         else if (mathText != null) {

             leftString = StatsLibrary.expandEPower(mathText.replaceAll(" ", "")) ;

             if (StringLibrary.isFloat(leftString))    { resolvedValue = new Double(leftString) ; }
             else                                    { resolvedValue = null ; }
         }

         return resolvedValue ;
     }

      /**
       * 
       * @param pass
       * @param message
       */
      protected void setResult (boolean pass, boolean isBorderline, String message) {
    	  log.debug(buildString("Setting result for step ", step.getId(),
    			  " to ", pass, " with message: ", message));

    	  assertInitialized();
    	  
    	  QcCriteriaStepResultBO result;
    	  QcCriteriaStepResultDAO dao = new QcCriteriaStepResultDAO();
    	  result = dao.findByStepSetSection(step.getId(), 
    			  dataSet.getPlateSet().getId(), dataSet.getPlateSection().getId());
    	  
    	  // initialize result record, either an existing one or a new one
    	  if (result != null) {
    		  log.debug(buildString("Adding to existing result, step ", result.getQccriteriastep().getId()));
    	  } else {
    		  log.debug(buildString("Creating new result"));
    		  result = new QcCriteriaStepResultBO();
    		  result.setPlateset(dataSet.getPlateSet());
    		  result.setPlatesection(dataSet.getPlateSection());
    		  result.setQccriteriastep(step);
    	  }    	  
    	  result.setPass(new Boolean(pass));
    	  result.setIsBorderline(isBorderline);
    	  result.setResultvalue(message);
    	  dao.persist(result);
    	  
    	  this.result = result;
      }

      /**
       * 
       * @param pass
       * @param message
       */
      public QcCriteriaStepResultBO getResult() {
    	  if (this.result == null)
    		  throw new SaxException("QC check has not been executed");
    	  
    	  return this.result;
      }
      
      /**
       * This method returns the value to be used for comparisions for a given
       * assay. By default it uses the weighted smooth mean of the normalized Ct.
       * Implementation classes can override this method to allow for different
       * values to be used.
       * 
       * @param assayName name of the assay
       * @return value
       */
	protected Double getAssayValue(String assayName) {
		Double result;
		
		try {
			result = dataSet.getAssay(assayName).getResult().getWsmnormct();
		} catch(Exception e) {
			log.debug(buildString("No assay value for assay ", assayName));
			result = null;
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.qc.criteria.step.QcCriteriaStep#evaluate()
	 */
	public abstract void evaluate();

	/**
	 * Assure that all class variables are properly initialized
	 */
	protected void assertInitialized() {
  	  if (dataSet == null || this.template == null) {
  		log.error(buildString("Criteria step not properly initialized"));
  		throw new SaxException("Uninitialized criteria step");
  	  }
		
	}
	
}
