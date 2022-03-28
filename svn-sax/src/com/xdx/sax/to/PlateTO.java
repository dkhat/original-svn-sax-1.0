package com.xdx.sax.to;

import java.io.Serializable;
import java.util.Date;

/**
 * PlateBO generated by hbm2java
 */
public class PlateTO implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private long id;	
	private PlateSetTO plateset;
	private String platebarcode;
	private long numberinset;
	private Date processedTimestamp;
	private Date registeredTimestamp;
	private Long processorError;
	private String qcResult;
	private String qcFailReason;
	private String qcComment;
	private String qcDoneBy;
	private Date qcDate;
	private String instrumentSerialNumber;
	private Date instrumentRunTime;

	public PlateTO() {
	}

	public PlateTO(long id, PlateSetTO plateset, String platebarcode,
			long numberinset) {
		this.id = id;
		this.plateset = plateset;
		this.platebarcode = platebarcode;
		this.numberinset = numberinset;
	}

	public PlateTO(long id, PlateSetTO plateset, String platebarcode,
			long numberinset, Date processedTimestamp, 
			Date registeredTimestamp, Long processorError) {
		this.id = id;
		this.plateset = plateset;
		this.platebarcode = platebarcode;
		this.numberinset = numberinset;
		this.processedTimestamp = processedTimestamp;
		this.registeredTimestamp = registeredTimestamp;
		this.processorError = processorError;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PlateSetTO getPlateset() {
		return this.plateset;
	}

	public void setPlateset(PlateSetTO plateset) {
		this.plateset = plateset;
	}

	public String getPlatebarcode() {
		return this.platebarcode;
	}

	public void setPlatebarcode(String platebarcode) {
		this.platebarcode = platebarcode;
	}

	public long getNumberinset() {
		return this.numberinset;
	}

	public void setNumberinset(long numberinset) {
		this.numberinset = numberinset;
	}

	/**
	 * @return the processedTimestamp
	 */
	public Date getProcessedTimestamp() {
		return processedTimestamp;
	}

	/**
	 * @return the registeredTimestamp
	 */
	public Date getRegisteredTimestamp() {
		return registeredTimestamp;
	}

	/**
	 * @param registeredTimestamp the registeredTimestamp to set
	 */
	public void setRegisteredTimestamp(Date registeredTimestamp) {
		this.registeredTimestamp = registeredTimestamp;
	}

	/**
	 * @param processedTimestamp the processedTimestamp to set
	 */
	public void setProcessedTimestamp(Date processedTimestamp) {
		this.processedTimestamp = processedTimestamp;
	}

	public Long getProcessorError() {
		return this.processorError;
	}

	public void setProcessorError(Long processorError) {
		this.processorError = processorError;
	}

	/**
	 * @return the qcResult
	 */
	public String getQcResult() {
		return qcResult;
	}

	/**
	 * @param qcResult the qcResult to set
	 */
	public void setQcResult(String qcResult) {
		this.qcResult = qcResult;
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
	 * @return the qcComment
	 */
	public String getQcComment() {
		return qcComment;
	}

	/**
	 * @param qcComment the qcComment to set
	 */
	public void setQcComment(String qcComment) {
		this.qcComment = qcComment;
	}

	/**
	 * @return the qcDoneBy
	 */
	public String getQcDoneBy() {
		return qcDoneBy;
	}

	/**
	 * @param qcDoneBy the qcDoneBy to set
	 */
	public void setQcDoneBy(String qcDoneBy) {
		this.qcDoneBy = qcDoneBy;
	}

	/**
	 * @return the qcDate
	 */
	public Date getQcDate() {
		return qcDate;
	}

	/**
	 * @param qcDate the qcDate to set
	 */
	public void setQcDate(Date qcDate) {
		this.qcDate = qcDate;
	}

	/**
	 * @return the instrumentSerialNumber
	 */
	public String getInstrumentSerialNumber() {
		return instrumentSerialNumber;
	}

	/**
	 * @param instrumentSerialNumber the instrumentSerialNumber to set
	 */
	public void setInstrumentSerialNumber(String instrumentSerialNumber) {
		this.instrumentSerialNumber = instrumentSerialNumber;
	}

	/**
	 * @return the instrumentRunTime
	 */
	public Date getInstrumentRunTime() {
		return instrumentRunTime;
	}

	/**
	 * @param instrumentRunTime the instrumentRunTime to set
	 */
	public void setInstrumentRunTime(Date instrumentRunTime) {
		this.instrumentRunTime = instrumentRunTime;
	}

}
