/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.util.ws;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.xdx.abi.ws.AbiService;
import com.xdx.abi.ws.util.AbiRuntimeData;
import com.xdx.sax.bo.AggSectionResultBO;
import com.xdx.sax.bo.AssayOutlierResultBO;
import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.bo.ProcessControlRawScoreResultBO;
import com.xdx.sax.bo.QcResultFinalBO;
import com.xdx.sax.dao.AggSectionResultDAO;
import com.xdx.sax.dao.AssayOutlierResultDAO;
import com.xdx.sax.dao.PlateSectionDAO;
import com.xdx.sax.dao.PlateSetDAO;
import com.xdx.sax.dao.ProcessControlRawScoreResultDAO;
import com.xdx.sax.dao.QcResultFinalDAO;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.schedulable.SdsDatabaseScanner;

/**
 * @author smeier
 *
 * This class wraps results for one sample. It processes the different aspects of a sample result:
 * 
 *  - scores
 *  - process control results, if any
 *  - auxiliary checks, if any
 *  - additional informational data
 */
public class SampleResult implements Serializable {
	//
	private static final long serialVersionUID = 1L;
	
	private boolean processingFinished;
	private Integer score;
	private Double rawScore;
	private Double lowScore;
	private Double highScore;
	private String qcStatus;
	private String qcFailReason;
	private String processControlResult;
	private String abiName;
	private Date abiDateTime;
	private ExtraFlagResult[] extraFlagResults; 
	
	public SampleResult() {}
	
	public SampleResult(String plateSetId, String plateSectionName) {
		PlateSetBO set = new PlateSetDAO().findByExternalId(plateSetId);
		if (set == null)
			throw new SaxException(buildString("Set ", plateSetId, " does not exist"));
		
		PlateSectionBO section = new PlateSectionDAO().findByDesignSectionname(set.getPlatedesign().getId(), plateSectionName);
		if (section == null)
			throw new SaxException(buildString("Section ", plateSectionName, " for set ", plateSetId, " does not exist"));
		
		AggSectionResultBO result = new AggSectionResultDAO().findBySetSection(set.getId(), section.getId());
		QcResultFinalBO finalResult = new QcResultFinalDAO().findByPlateSetSection(set.getId(), section.getId());

		if (result == null || finalResult == null)
			throw new SaxException(buildString("Results missing for set ", plateSetId, " and section ", plateSectionName));
		
		if (set.getProcessedTimestamp() != null &&
				finalResult.getUserqcresult() != null) {
			processingFinished = true;
			
			// set scores
			if (result.getMappedscore() != null)
				score = (int) Math.floor(result.getMappedscore());
			else
				score = null;
			rawScore = result.getAlgoscore();
			lowScore = result.getMappedlowscore();
			highScore = result.getMappedhighscore();

			// Process user assigned QC result
			if (finalResult.getUserqcresult() == null)
				qcStatus = "";
			else
				qcStatus = finalResult.getUserqcresult();
			
			// Process QC failure resason
			if (finalResult.getQcFailReason() == null)
				qcFailReason = "";
			else
				qcFailReason = finalResult.getQcFailReason();

			// Check proces control result (only raw score result is erlevant)
			ProcessControlRawScoreResultBO rlpcResult = new ProcessControlRawScoreResultDAO().findBySetSection(set.getId(), section.getId());
			
			if (rlpcResult != null) {
				processControlResult = rlpcResult.getPass()?"PASS":"FAIL";
			} else {
				processControlResult="";
			}
		} else {
			processingFinished = false;
			score = null;
			rawScore = null;
			lowScore = null;
			highScore = null;
			qcStatus = "";
			qcFailReason = "";
			processControlResult = "";
		}
		
		getAbiRuntimeData(set);

		this.extraFlagResults = new ExtraFlagResult[1];
		this.extraFlagResults[0] = getAssayOutlierResult(set.getId(), section.getId());
	}

	/**
	 * Get the ABI runtime data from the ABI interface service
	 * 
	 * @param set
	 */
	private void getAbiRuntimeData(PlateSetBO set) {
		this.abiName = "N/A";
		this.abiDateTime = null;		

		AbiService res;
		try {
			res = (AbiService) new InitialContext().lookup(SdsDatabaseScanner.JNDI_NAME);
		} catch(NamingException e) {
			throw new SaxException(e);
		}
		
		AbiRuntimeData data = res.getAbiRuntimeData(set.getExternalId()); 
		if (data.getAbiIdentifier() != null) {
			this.abiName = data.getAbiIdentifier();
			this.abiDateTime = data.getAbiRunDate();					
		}
	}

	/**
	 * 
	 * @param plateSetId
	 * @param plateSectionId
	 * @return
	 */
	private ExtraFlagResult getAssayOutlierResult(long plateSetId, long plateSectionId) {
		ExtraFlagResult result = new ExtraFlagResult();
		result.setFlagType("AssayOutlier");
		result.setPass(null);
		
		List<AssayOutlierResultBO> r = new AssayOutlierResultDAO().findByPlatesetSection(plateSetId, plateSectionId);
		if (r == null || r.size() == 0)
			return result;
		
		result.setPass(true);
		for (AssayOutlierResultBO obj : r) {
			if (! obj.getPass())
				result.setPass(false);
		}
		
		return result;
	}

	/**
	 * @param processingFinished the processingFinished to set
	 */
	public void setProcessingFinished(boolean processingFinished) {
		this.processingFinished = processingFinished;
	}

	/**
	 * @return the processingFinished
	 */
	public boolean isProcessingFinished() {
		return processingFinished;
	}

	/**
	 * @return the algoScore
	 */
	public Integer getScore() {
		return score;
	}

	/**
	 * @param algoScore the algoScore to set
	 */
	public void setScore(Integer algoScore) {
		this.score = algoScore;
	}

	/**
	 * @return the lowScore
	 */
	public Double getLowScore() {
		return lowScore;
	}

	/**
	 * @param lowScore the lowScore to set
	 */
	public void setLowScore(Double lowScore) {
		this.lowScore = lowScore;
	}

	/**
	 * @return the highScore
	 */
	public Double getHighScore() {
		return highScore;
	}

	/**
	 * @param highScore the highScore to set
	 */
	public void setHighScore(Double highScore) {
		this.highScore = highScore;
	}

	/**
	 * @return the qcStatus
	 */
	public String getQcStatus() {
		return qcStatus;
	}

	/**
	 * @param qcStatus the qcStatus to set
	 */
	public void setQcStatus(String qcStatus) {
		this.qcStatus = qcStatus;
	}

	/**
	 * @return the rawScore
	 */
	public Double getRawScore() {
		return rawScore;
	}

	/**
	 * @param rawScore the rawScore to set
	 */
	public void setRawScore(Double rawScore) {
		this.rawScore = rawScore;
	}

	/**
	 * @return the qcFailReason
	 */
	public String getQcFailReason() {
		return qcFailReason;
	}

	/**
	 * @param qcFailReason the qcFailReason to set
	 */
	public void setQcFailReason(String qcFailReason) {
		this.qcFailReason = qcFailReason;
	}

	/**
	 * @return the processControlResult
	 */
	public String getProcessControlResult() {
		return processControlResult;
	}

	/**
	 * @param processControlResult the processControlResult to set
	 */
	public void setProcessControlResult(String processControlResult) {
		this.processControlResult = processControlResult;
	}

	/**
	 * @param abiName the abiName to set
	 */
	public void setAbiName(String abiName) {
		this.abiName = abiName;
	}

	/**
	 * @return the abiName
	 */
	public String getAbiName() {
		return abiName;
	}

	/**
	 * @param abiDateTime the abiDateTime to set
	 */
	public void setAbiDateTime(Date abiDateTime) {
		this.abiDateTime = abiDateTime;
	}

	/**
	 * @return the abiDateTime
	 */
	public Date getAbiDateTime() {
		return abiDateTime;
	}

	/**
	 * @return the extraFlagResults
	 */
	public ExtraFlagResult[] getExtraFlagResults() {
		return extraFlagResults;
	}

	/**
	 * @param extraFlagResults the extraFlagResults to set
	 */
	public void setExtraFlagResults(ExtraFlagResult[] extraFlagResults) {
		this.extraFlagResults = extraFlagResults;
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
