package com.xdx.sax.service;

import java.util.List;

import javax.ejb.Local;

import com.xdx.sax.to.TestcodeTO;
import com.xdx.sax.util.ws.PlateDesignData;
import com.xdx.sax.util.ws.PlateDesignParam;
import com.xdx.sax.util.ws.PlateQcResult;
import com.xdx.sax.util.ws.ProcessControlItem;
import com.xdx.sax.util.ws.ProcessControlParameter;
import com.xdx.sax.util.ws.QcResult;
import com.xdx.sax.util.ws.QcStepResult;
import com.xdx.sax.util.ws.Result;
import com.xdx.sax.util.ws.SampleParameter;
import com.xdx.sax.util.ws.SampleResult;
import com.xdx.sax.util.ws.WsSampleId;

@Local
public interface SaxApiServiceBean {

	public static final String JNDI_NAME = "sax/SaxApi/local";
	
	/**
	 * 
	 * @param plateSetId
	 * @param plateNum
	 * @param testcode
	 * @param plateBarcode
	 * @param plateDesignId
	 * @param processcontrols
	 * @param emptySections
	 * @return
	 */
	public abstract Result registerPlate(
			String plateSetId, int plateNum, String testcode,
			String plateBarcode, int plateDesignId,
			boolean processControlsMandatory,
			ProcessControlParameter[] processcontrols,
			String[] emptySections);

	/**
	 * 
	 * @return
	 */
	public abstract String processFiles();

	/**
	 * 
	 * @param fileName
	 */
	public abstract String processFile(String fileName);

	/**
	 * 
	 * @param plateBarcode
	 * @return
	 */
	public abstract String processPlate(String plateBarcode);

	/**
	 * 
	 * @param plateMapName
	 * @return
	 */
	public abstract String convertPlatemap(String plateMapName);

	/**
	 * 
	 * @return
	 */
	public abstract TestcodeTO[] getAvailableTestcodes();

	/**
	 * 
	 * @return
	 */
	public abstract PlateDesignData[] getAvailablePlateDesigns(String testcode);

	/**
	 * 
	 * @param platesetId
	 * @param schemaId
	 * @return
	 */
	public abstract String executeQc(long platesetId, long schemaId);

	/**
	 * 
	 * @String testcode
	 * @String lotName
	 * @param processControlData
	 * @return result of the operation
	 */
	public abstract Result registerProcessControl(
			String testcode,
			String lotName, 
			Double expectedRawScore,
			Double rawScoreSstandardDeviation,
			ProcessControlItem[] processControlData);

	/**
	 * 
	 * @param parameters
	 * @return
	 */
	public abstract QcResult[] getSampleQcResults(SampleParameter[] parameters);

	/**
	 * 
	 * @param param
	 * @return
	 */
	public abstract QcStepResult[] getQcStepResults(SampleParameter param);

	/**
	 * 
	 * @return
	 */
	public abstract WsSampleId[] getSampleList();

	/**
	 * 
	 * @param plateSetId
	 * @param plateSectionId
	 * @param pass
	 * @param failureReason
	 */
	public abstract void passSample(int plateSetId, int plateSectionId,
			boolean pass, String failureReason, String user);

	/**
	 * 
	 * @param barcode
	 * @param pass
	 * @param failureReason
	 * @param comment
	 */
	public abstract void passPlate(String barcode,
			boolean pass, String failureReason, String comment, String user);

	/**
	 * 
	 * @param plateSetId
	 * @param plateSectionName
	 * @return
	 */
	public SampleResult getResult(String plateSetId, 
			String plateSectionName);

	/**
	 * 
	 * @param barcode
	 * @return
	 */
	public PlateQcResult getPlateQcResult (String barcode);
	
	/**
	 * 
	 */
	public void processNextPlate();

	/**
	 * @param plateDesignId		plate design id
	 * @param plateDesignName	name of the plate design
	 * @param masterMixVol		mastermix volume used for this plate design
	 * @param availableWellVol	total available volume for a well
	 *  @return result object
	 */
	public Result updatePlateDesign(
			long plateDesignId, String plateDesignName, Double masterMixVol, Double availableWellVol);

	/**
	 * 
	 * @param plateDesignName	name of the plate design
	 * @param masterMixVol		mastermix volume used for this plate design
	 * @param availableWellVol	total available volume for a well
	 * @param details			details of the plate design
	 * @return result object
	 */
	public Result registerPlateDesign(
			String plateDesignName, Double masterMixVol, Double availableWellVol, PlateDesignParam[] details);

	/**
	 * 
	 * @return list of new barcodes to process
	 */
	public List<String> getNewBarcodes();
}