package com.xdx.sax.util.ws;

import java.io.Serializable;

import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.bo.QcCriteriaStepResultBO;
import com.xdx.sax.bo.QcResultFinalBO;
import com.xdx.sax.dao.PlateSectionDAO;
import com.xdx.sax.dao.PlateSetDAO;
import com.xdx.sax.dao.QcResultFinalDAO;
import com.xdx.sax.exceptions.SaxException;

/**
 * This is a helper class that provides QC results for one sample
 * (= plateset/section combination) in a form that can be made accessible
 * via web services (only standard data types / arrays)
 * 
 * @author smeier
 *
 */
public class QcResult implements Serializable {
	
	//
	private static final long serialVersionUID = 1L;
	
	private long plateSetId;
	private String plateSectionName;
	private String externalId;
	private String qcResult;
	private QcStepResult[] stepResults;
	
	public QcResult () {
		plateSetId=0;
		plateSectionName="";
		externalId="";
		qcResult="";
		stepResults = null;
	}
	
	public QcResult(String plateSectionName, String externalId) {
		// get plate set
		PlateSetBO plateSet = new PlateSetDAO().findByExternalId(externalId);
		if (plateSet == null)
			throw new SaxException(buildString("Invalid plate set external id ", externalId));
		
		// get plate section
		PlateSectionBO plateSection = new PlateSectionDAO().findByDesignSectionname(plateSet.getPlatedesign().getId(), plateSectionName);
		if (plateSection == null)
			throw new SaxException(buildString("Invalid plate section name ", plateSectionName, " for plate set (externalid) ", externalId));
		
		this.plateSetId = plateSet.getId();
		this.plateSectionName = plateSectionName;
		this.externalId = externalId;
		
		// get final summary result - if not available QC has not been run yet
		QcResultFinalBO finalResult = new QcResultFinalDAO().findByPlateSetSection(plateSetId, plateSection.getId());
		if(finalResult == null)
			return;
		this.qcResult = finalResult.getUserqcresult();
		
		// step results
		this.stepResults = new QcStepResult[plateSet.getQccriteriastepresults().size()];
		int i=0;
		for (QcCriteriaStepResultBO stepResult: plateSet.getQccriteriastepresults()) {
			this.stepResults[i++] = new QcStepResult(stepResult.getQccriteriastep().getQccriteria().getCriterianame(),
					stepResult.getPass(), stepResult.getResultvalue());
		}
	}
	
	/**
	 * @return the plateSetId
	 */
	public long getPlateSetId() {
		return plateSetId;
	}

	/**
	 * @return the plateSectionId
	 */
	public String getPlateSectionName() {
		return plateSectionName;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @return the qcResult
	 */
	public String getQcResult() {
		return qcResult;
	}

	/**
	 * @param plateSetId the plateSetId to set
	 */
	public void setPlateSetId(long plateSetId) {
		this.plateSetId = plateSetId;
	}

	/**
	 * @param plateSectionId the plateSectionId to set
	 */
	public void setPlateSectionName(String plateSectionName) {
		this.plateSectionName = plateSectionName;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @param qcResult the qcResult to set
	 */
	public void setQcResult(String qcResult) {
		this.qcResult = qcResult;
	}

	/**
	 * @return the stepResults
	 */
	public QcStepResult[] getStepResults() {
		return stepResults;
	}

	/**
	 * @param stepResults the stepResults to set
	 */
	public void setStepResults(QcStepResult[] stepResults) {
		this.stepResults = stepResults;
	}

	/**
     * Utility method tests for absence of a nullity state
     * in a <i>Null Object Pattern</i> design pattern manner.
     */
    protected final boolean assertNotNull(Object object) {
    	return object != null;
	}

    /**
     * Utility method tests for presence of a nullity state
     * in a <i>Null Object Pattern</i> design pattern manner.
     */
    protected final boolean assertNull(Object object) {
    	return  !  assertNotNull(object);
	}

    /**
     * Utility method builds Strings; avoids string concatenation.
     */
    protected final String buildString(Object... values) {
    	
    	StringBuilder sb= new StringBuilder();

    	for (Object object : values) {
    		sb.append((assertNull(object))  ?  "" :  object.toString());
		}

    	return sb.toString();
	}

}
