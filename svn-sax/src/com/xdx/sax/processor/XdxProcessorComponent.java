/**
 * 
 */
package com.xdx.sax.processor;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.domain.ConfigurationModel;

/**
 * @author smeier
 *
 */
public abstract class XdxProcessorComponent {

	private static final Log log= LogFactory.getLog(XdxProcessorComponent.class);

	private static final String category= "XDxCTProcessor";

	//protected static final boolean DELETE_MC_FILE = false;
	protected static final boolean DELETE_MC_FILE = ConfigurationModel.lookup("HTx", category,"DELETE_MC_FILE") == "FALSE" ? false : true;

	/**
	 * The data file is 'clipped file' from ABI
	 */
	private static final int CLIPPED_FILE= new Integer(ConfigurationModel.lookup("HTx", category,"CLIPPED_FILE_INTEGER_KEY"));
	protected static int getClippedFile() { return CLIPPED_FILE; }

	/**
	 * The data file is 'SDS file' from ABI
	 */
	private static final int SDS_FILE= new Integer(ConfigurationModel.lookup("HTx", category,"SDS_FILE_INTEGER_KEY"));
	protected static int getSDSFile() { return SDS_FILE; }

	/**
	 * The total number of cycles of PCR. The default value is 40.
	 */
	public static int TOTCYCLES= new Integer(ConfigurationModel.lookup("HTx", category,"TOTCYCLES_INTEGER_KEY"));

	/**
	 * The number of wells in each plate. The default value is 384.
	 */
	public static int MAXWELLS= new Integer(ConfigurationModel.lookup("HTx", category,"MAXWELLS_INTEGER_KEY"));

	/**
	 * The annealing temperature
	 */
	public static double ANNEAL_TEMPERATURE= new Double(ConfigurationModel.lookup("HTx", category,"ANNEAL_TEMPERATURE_INTEGER_KEY"));

	/**
	 * The maximum deviation from ANNEAL_TEMPERATURE.
	 */
	public static double ANNEAL_TEMP_DEVIATION= new Double(ConfigurationModel.lookup("HTx", category,"ANNEAL_TEMP_DEVIATION_DOUBLE_KEY"));

	/**
	 * The threshold cycle (CT) depends on the selection of the threshold.
	 * The default threshold is 0.2
	 */
	public static double THRESHOLD= new Double(ConfigurationModel.lookup("HTx", category,"THRESHOLD_DOUBLE_KEY"));

	/**
	 * If the calculated CT is below MIN_THRESHOLD_CYCLE, it's treated as error.
	 * The default minimum CT is 8.
	 */
	public static double MIN_THRESHOLD_CYCLE= new Double(ConfigurationModel.lookup("HTx", category,"MIN_THRESHOLD_CYCLE_INTEGER_KEY"));

	/**
	 * The algorithm determines the baseline starting from cycle #2
	 */
	public static int MIN_BASELINE_CYCLE_START= new Integer(ConfigurationModel.lookup("HTx", category,"MIN_BASELINE_CYCLE_START_INTEGER_KEY"));

	/**
	 * The algorithm uses a window of 3 cycles to find the portion of the curve
	 * with the steepest rise.
	 */
	public static int CYCLE_RISE_RANGE= new Integer(ConfigurationModel.lookup("HTx", category,"CYCLE_RISE_RANGE_INTEGER_KEY"));

	/**
	 * This is one parameter in the algorithm derived from trial and error.
	 * The default value is 0.5 .
	 */
	public static double EXPONENT= new Double(ConfigurationModel.lookup("HTx", category,"EXPONENT_DOUBLE_KEY"));

	/**
	 * This is one parameter in the algorithm derived from trial and error
	 * The default value is 0.75 .
	 */
	public static double PERCENT_RISE_RATIO= new Double(ConfigurationModel.lookup("HTx", category,"PERCENT_RISE_RATIO_DOUBLE_KEY"));

	/**
	 * The minimum number of cycles for the baseline.
	 * The default value is 4
	 */
	public static int MIN_BASELINE_CYCLES= new Integer(ConfigurationModel.lookup("HTx", category,"MIN_BASELINE_CYCLES_INTEGER_KEY"));

	/**
	 * The starting cycle for calculating the baseline FAM. The default value is 3.
	 */
	public static int FAM_BASELINE_START_CYCLE= new Integer(ConfigurationModel.lookup("HTx", category,"FAM_BASELINE_START_CYCLE_INTEGER_KEY"));

	/**
	 * The end cycle for calculating the baseline FAM. The default value is 5.
	 */
	public static int FAM_BASELINE_END_CYCLE= new Integer(ConfigurationModel.lookup("HTx", category,"FAM_BASELINE_END_CYCLE_INTEGER_KEY"));

	/**
	 * If the FAM value falls below MIN_FAM, it's considered an empty well.
	 * The default value is 500.
	 */
	public static int MIN_FAM= new Integer(ConfigurationModel.lookup("HTx", category,"MIN_FAM_INTEGER_KEY"));

	/**
	 * The starting reading in the Multicomponenet file for calculating the background FAM.
	 * The default value is 1.
	 */
	public static int BKG_FAM_START_READING= new Integer(ConfigurationModel.lookup("HTx", category,"BKG_FAM_START_READING_INTEGER_KEY"));

	/**
	 * The ending reading in the Multicomponent file for calculating the background FAM.
	 * The default value is 42.
	 */
	public static int BKG_FAM_END_READING= new Integer(ConfigurationModel.lookup("HTx", category,"BKG_FAM_END_READING_INTEGER_KEY"));

	/**
	 * If the slope of the delta RN curve fall below MIN_DELTARN_SLOPE, the well CT is undetermined.
	 * The default value is -0.002
	 */
	public static double MIN_DELTARN_SLOPE= new Double(ConfigurationModel.lookup("HTx", category,"MIN_DELTARN_SLOPE_DOUBLE_KEY"));

	/**
	 * If the left end of the Log Linear curve (LLLE) is equal or higher than MAX_LLLE, the well is undetermined.
	 * The default value is 38.
	 */
	public static int MAX_LLLE= new Integer(ConfigurationModel.lookup("HTx", category,"MAX_LLLE_INTEGER_KEY"));
	public static int MAX_LLRE= new Integer(ConfigurationModel.lookup("HTx", category,"MAX_LLRE_INTEGER_KEY"));

	/**
	 * If the amount of rise between cycles i and i+3 is less than this value,
	 * The curve is determined to be flat.
	 */
	public static double MIN_PERCENT_RISE= new Double(ConfigurationModel.lookup("HTx", category,"MIN_PERCENT_RISE_DOUBLE_KEY"));

	/**
	 * Definition of a bad well : CT= 0
	 *
	 * If the number of wells with zero CT is greater than MAX_BAD_WELLS,
	 * then the variable AnalyzerStatus in the class ProcessorPlate will be set to an error message.
	 */
 	public static int MAX_BAD_WELLS= new Integer(ConfigurationModel.lookup("HTx", category,"MAX_BAD_WELLS_INTEGER_KEY"));

	// Error codes
	public static final int FLAT_LINE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"FLAT_LINE_ERR"));
	public static final int LLRE_TOO_HIGH_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"LLRE_TOO_HIGH_ERR"));
	public static final int LLLE_TOO_HIGH_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"LLLE_TOO_HIGH_ERR"));
	public static final int LEFT_END_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"LEFT_END_ERR"));
	public static final int RIGHT_END_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"RIGHT_END_ERR"));
	public static final int CYCLE_RANGE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CYCLE_RANGE_ERR"));
	public static final int DIVIDE_BY_ZERO_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"DIVIDE_BY_ZERO_ERR"));
	public static final int SLOPE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"SLOPE_ERR"));
	public static final int THRESHOLD_CYCLE_TOO_HIGH_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"THRESHOLD_CYCLE_TOO_HIGH_ERR"));
	public static final int THRESHOLD_CYCLE_TOO_LOW_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"THRESHOLD_CYCLE_TOO_LOW_ERR"));
	public static final int CALC_LOG_PHASE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CALC_LOG_PHASE_ERR"));
	public static final int CALC_BASELINE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CALC_BASELINE_ERR"));
	public static final int GET_START_CYCLE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"GET_START_CYCLE_ERR"));
	public static final int CALC_CYCLE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CALC_CYCLE_ERR"));
	public static final int CALC_CALIB_STD_DEV_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CALC_CALIB_STD_DEV_ERR"));
	public static final int CONNECTSTRING_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CONNECTSTRING_ERR"));
	public static final int NO_LOGPHASE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"NO_LOGPHASE_ERR"));
	public static final int THRESHOLD_CYCLE_OUT_OF_RANGE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"THRESHOLD_CYCLE_OUT_OF_RANGE_ERR"));
	public static final int DELTARN_LOWER_THAN_THRESHOLD_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"DELTARN_LOWER_THAN_THRESHOLD_ERR"));
	public static final int CALC_BASELINE_ENDS_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CALC_BASELINE_ENDS_ERR"));
	public static final int UNDETERMINED_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"UNDETERMINED_ERR"));
	public static final int FILE_FORMAT_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"FILE_FORMAT_ERR"));
	public static final int UNKNOWN_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"UNKNOWN_ERR"));
	public static final int CALIB_WELLNUM_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CALIB_WELLNUM_ERR"));
	public static final int EMPTY_FILE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"EMPTY_FILE_ERR"));
	public static final int TOO_MANY_BAD_WELLS_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"TOO_MANY_BAD_WELLS_ERR"));
	public static final int ROX_ALL_NEGATIVE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"ROX_ALL_NEGATIVE_ERR"));
	public static final int ROX_PARTIAL_NEGATIVE_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"ROX_PARTIAL_NEGATIVE_ERR"));
	public static final int SDS_EXECUTION_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"SDS_EXECUTION_ERR"));
	public static final int ZERO_THRESHOLD_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"ZERO_THRESHOLD_ERR"));
	public static final int LLRE_TOO_HIGH_AND_NULL_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"LLRE_TOO_HIGH_AND_NULL_ERR"));
	public static final int CYCLES_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"CYCLES_ERR"));
	public static final int DETECTOR_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"DETECTOR_ERR"));
	public static final int EMPTY_WELL_ERR= new Integer(ConfigurationModel.lookup("HTx", category,"EMPTY_WELL_ERR"));

	public static final String[] ERROR_MESSAGE= {
		 "",
		 ConfigurationModel.lookup("HTx", category,"FLAT_LINE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"LLRE_TOO_HIGH_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"LLLE_TOO_HIGH_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"LEFT_END_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"RIGHT_END_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CYCLE_RANGE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"DIVIDE_BY_ZERO_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"SLOPE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"THRESHOLD_CYCLE_TOO_HIGH_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"THRESHOLD_CYCLE_TOO_LOW_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CALC_LOG_PHASE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CALC_BASELINE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"GET_START_CYCLE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CALC_CYCLE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CALC_CALIB_STD_DEV_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CONNECTSTRING_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"NO_LOGPHASE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"THRESHOLD_CYCLE_OUT_OF_RANGE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"DELTARN_LOWER_THAN_THRESHOLD_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CALC_BASELINE_ENDS_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"UNDETERMINED_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"FILE_FORMAT_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"UNKNOWN_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CALIB_WELLNUM_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"EMPTY_FILE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"TOO_MANY_BAD_WELLS_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"ROX_ALL_NEGATIVE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"ROX_PARTIAL_NEGATIVE_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"SDS_EXECUTION_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"ZERO_THRESHOLD_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"LLRE_TOO_HIGH_AND_NULL_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"CYCLES_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"DETECTOR_ERR_MSG"),
		 ConfigurationModel.lookup("HTx", category,"EMPTY_WELL_ERR_MSG")
 	};

	/**
	 * Return the error message associated with the error number caused by the CT calculation
     *
	 * @param errorNumber The error number
	 */
	protected String getErrorMessage(int errorNumber) {

		if (errorNumber  >  0   &&   errorNumber  <=  ERROR_MESSAGE.length) {
			return ERROR_MESSAGE[errorNumber];
		}

		return "";
	}

	/**
	 * Return file extension.
	 *
	 * @param fileHandle File name
	 */
 	protected String getFileExtension(String fileHandle) {

		int index= fileHandle.lastIndexOf('.');

		return (index < 0)  ?  "" : fileHandle.substring(index + 1, fileHandle.length());
 	}

 	/**
	 * Return file base name.
	 *
	 * @param fileHandle File name
	 */
 	protected String getFileBasename(String fileHandle) {

		String fname= getFileName(fileHandle);

		int index= fname.lastIndexOf('.');

		return (index < 0) ? fileHandle : fname.substring(0, index);

 	}

  /**
	 * Create a new file name using fileHandle's path and basename plus the 'extension'.
     *
	 * @param fileHandle  Use the path and basename of fileHandle.
	 * @param extension The extension to be added to the new file name
	 * @return a new file name using fileHandle's path and basename plus the 'extension'
	 */
 	protected String addExtension(String fileHandle, String extension) {

		String path= "";
		String basename= "";

		try {

			path= new File(fileHandle).getParent();

			if (path == null) {
				path= ".";
			}

			basename= getFileBasename(fileHandle);
		}
		catch(Throwable e) {
			log.error(buildString("\n ", e));
		}

 		return buildString(path, File.separator, basename, extension);
 	}

	/**
 	 * Convert well number to row(A,B,C, ...,P) / 24-column (1,2,3, ...,24) format.
 	 * This seems to be a convenience method (for debugging).
 	 *
 	 * @param wellnum com.xdx.analyzer.domain.Assay number
 	 */
	protected String well2Letter(int wellnum) {

  		int i= (int) ( (wellnum - 1) / 24);
		int j= wellnum % 24;

		if (j == 0) {
		 	j= 24;
		}

		return (char)(i + 65) + String.valueOf(j);
 	}

	/**
	 * This method assumes 24-column layout, and well numbers starting with 1
	 * 
	 * @param wellnum
	 * @return
	 */
	protected int well2Row(int wellnum) {
  		int i= (int) ( (wellnum - 1) / 24);
		return i;
	}
	
	/**
	 * This method assume 24-column layout, and well numbers starting with 1
	 *
	 * @param wellnum
	 * @return
	 */
	protected int well2Column(int wellnum) {
		int j= (wellnum - 1) % 24;
		return j;
	}
	
	/**
	 * Instantiate a <code>File</code> and return its name.
	 *
	 * @param fileHandle
	 * @return the name of an instantiated <code>File</code>
	 */
 	private final String getFileName(String fileHandle) {
		return new File(fileHandle).getName();
	}

	/**
	 * Utility method tests for abscence of a nullity state
	 * in a <i>Null Object Pattern</i> design pattern manner.
	 */
	protected final static boolean assertNotNull(Object object) {
	 	return object != null;
	}

	/**
	 * Utility method tests for presence of a nullity state
	 * in a <i>Null Object Pattern</i> design pattern manner.
	 */
	protected final static boolean assertNull(Object object) {
	 	return  !  assertNotNull(object);
	}

	/**
	 * Utility method builds Strings; avoids string concatenation.
	 *
	 * @param values
	 * @return the concatenated string
	 */
	protected final static String buildString(Object... values) {

		StringBuilder sb= new StringBuilder();

		for (Object object : values) {
			sb.append((assertNull(object))  ?  "" :  object.toString());
		}

		return sb.toString();
	}

}
