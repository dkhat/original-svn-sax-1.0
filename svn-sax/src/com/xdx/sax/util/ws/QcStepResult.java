package com.xdx.sax.util.ws;

import java.io.Serializable;

/**
 * This is a helper class that provides QC results for one sample
 * (= plateset/section combination) in a form that can be made accessible
 * via web services (only standard data types / arrays)
 * 
 * @author smeier
 *
 */
public class QcStepResult implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private String qcStepName;
	private boolean qcStepPassed;
	private String qcStepError;

	public QcStepResult () {}
	
	public QcStepResult (String qcStepName, boolean qcStepPassed, String qcStepError) {
		this.qcStepName = qcStepName;
		this.qcStepError = qcStepError;
		this.qcStepPassed = qcStepPassed;
	}
	
	/**
	 * @return the qcStepName
	 */
	public String getQcStepName() {
		return qcStepName;
	}
	/**
	 * @param qcStepName the qcStepName to set
	 */
	public void setQcStepName(String qcStepName) {
		this.qcStepName = qcStepName;
	}
	/**
	 * @return the qcStepPassed
	 */
	public boolean isQcStepPassed() {
		return qcStepPassed;
	}
	/**
	 * @param qcStepPassed the qcStepPassed to set
	 */
	public void setQcStepPassed(boolean qcStepPassed) {
		this.qcStepPassed = qcStepPassed;
	}
	/**
	 * @return the qcStepError
	 */
	public String getQcStepError() {
		return qcStepError;
	}
	/**
	 * @param qcStepError the qcStepError to set
	 */
	public void setQcStepError(String qcStepError) {
		this.qcStepError = qcStepError;
	}
}
