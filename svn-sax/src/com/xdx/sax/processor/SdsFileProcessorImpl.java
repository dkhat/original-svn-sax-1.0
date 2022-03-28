/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.peabd.sds.io.SDS2MainComponent;
import com.peabd.sds.model.MulticompPlateDM;
import com.peabd.sds.model.PlateDataModel;
import com.xdx.abi.ws.AbiService;
import com.xdx.abi.ws.util.AbiRuntimeData;
import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.bo.WellRnBO;
import com.xdx.sax.dao.PlateDAO;
import com.xdx.sax.dao.RawWellDataDAO;
import com.xdx.sax.dao.WellRnDAO;
import com.xdx.sax.domain.ConfigurationModel;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 *
 */
public final class SdsFileProcessorImpl extends XdxProcessorComponent implements FileProcessor {

	private static final Log log= LogFactory.getLog(SdsFileProcessorImpl.class);

	private static final String ABI_JNDI_NAME = "AbiService/local";
	
	// name of the file that we are processing
	private String dataFile;
  	
  	// plate object that the given file maps to
  	private PlateBO plate;
  	
 	/**
 	 * Constructor
 	 * 
 	 * @param plateBarcode
 	 * @param dataFile
 	 */
 	public SdsFileProcessorImpl () {
	}

 	/**
 	 * Standard method from interface FileProcessor
 	 * 
 	 * @param dataFileName
 	 * @throws SaxException
 	 */
	public String process(String dataFile) throws SaxException {
		boolean isOk = true;
		String sep = ConfigurationModel.lookup("HTx", "SYSTEM", "DIR_SEPARATOR");

		int index= dataFile.lastIndexOf('.');
		String barcode = (index < 0) ? dataFile : dataFile.substring(0, index);
		index = barcode.lastIndexOf(sep);
		barcode = (index < 0) ? barcode : barcode.substring(index+1, barcode.length());

		this.plate = new PlateDAO().findByBarcode(barcode);		
		this.dataFile = dataFile;
		
		if (this.plate == null) {
			//log.debug(buildString("Not processing unregistered file ", dataFile));
			return null;
		}
		
		if (this.plate.getProcessorError() != null) {
			//log.debug(buildString("Skipping already processed plate ", dataFile));
			return null;
		}

		// Now set ABI machine and runtime
		try {
			AbiService abiService = (AbiService) getLocalBeanInterface(ABI_JNDI_NAME);
			AbiRuntimeData abiData = abiService.getAbiRuntimeData(barcode);
			plate.setInstrumentSerialNumber(abiData.getAbiIdentifier());
			plate.setInstrumentRunTime(abiData.getAbiRunDate());
		} catch (Exception e) {
			log.info(buildString("No ABI runtime data available for plate ", barcode));
		}
		
		try {
			log.info(buildString("Processing file ", dataFile));
			analyzeSDSFile();
			log.info(buildString("=== done ==="));
		} catch (Exception e) {
			// something was wrong with the SDS data structure
			isOk=false;
			this.plate.setProcessorError(new Long(1));
			this.plate.setProcessedTimestamp(new Date());
			log.error(e.getMessage());
		}
		
		if (isOk) {		
			this.plate.setProcessorError(new Long(0));
			this.plate.setProcessedTimestamp(new Date());
		}
		
		return barcode;
	}

	/**
	 * Start the CT calculations for the SDS file.
	 *
	 * @throws Exception
	 *
	 * @see ProcessorPlateImpl#analyze()
	 */
	private void analyzeSDSFile() {

		log.debug("ENTER AbstractProcessorPlate.analyzeSDSFile()");

		String mcfile= addExtension(this.dataFile, ".mc");

		execSDS(this.dataFile, mcfile);

		parseMultiComponentFile(mcfile);

		log.debug("EXIT AbstractProcessorPlate.analyzeSDSFile()");
	}

	/**
	 * Extract data from the SDS file, and generate the Multicomponent file and the CT Result file.<br>
	 * The CTs in the result file are ABI CT.
	 *
	 * @param sdsfile SDS file name
	 * @param mcfile Create the multicomponent file using this file name
	 * @throws Exception
	 *
	 * @see #analyzeSDSFile()
	 */
	private void execSDS(String sdsfile, String mcfile) {

		log.debug("ENTER AbstractProcessorPlate.execSDS(String sdsfile, String mcfile)");

		SDS2MainComponent sdsDoc= null;
		PlateDataModel pdm= null;

		File sdsFile= new File(sdsfile);
		File mcFile= new File(mcfile);

		sdsDoc= new SDS2MainComponent();

		try {
			sdsDoc.loadFile(sdsFile);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new SaxException(e);
		}

		pdm= PlateDataModel.createPlateDataModel(sdsDoc);
		sdsDoc.freeMem();

		if (assertNull(pdm)) {
			throw new SaxException("Internal error: can't create PlateDataModel.");
		}

		if (pdm.hasAcquisitionData()) {

			if (pdm instanceof MulticompPlateDM) {

				PipedOutputStream pos= new PipedOutputStream();
				PrintStream ps= new PrintStream(pos);
				PrintStream err= System.err;
				System.setErr(ps);

				MulticompPlateDM mpdm= (MulticompPlateDM) pdm;

				try {

					mpdm.doAnalyze();

					try {
						mpdm.doExportMulticomp(mcFile, false, false, null);
					}
					catch (IOException e) {
						throw new SaxException(buildString("Error exporting multicomp data: ", e.getMessage()));
					}
				}
				catch (Exception e) {
					System.setErr(err);
					throw new SaxException(e);
				}

				System.setErr(err);

				ps.close();
				
				try {
					pos.close();
				} catch (IOException e) {
					log.error(e.getMessage());
					throw new SaxException(e);
				}
			}
			else {
				throw new SaxException("This document does not support AD export.");
			}
		}
		else {
			throw new SaxException(buildString(sdsFile.getName(), " does not contain acquisition data."));
		}

		log.debug("EXIT AbstractProcessorPlate.execSDS(String sdsfile, String mcfile)");
	}

	/**
	 * Parse the Multicomponent file to generate the RN values; a Multicomponent file is an ABI construct.
	 *
	 * @param mcfile Multicomponent file Drive:\path\filename
	 * @throws Exception
	 *
	 * @see #analyzeSDSFile()
	 */
	private void parseMultiComponentFile(String mcfile) {

		log.debug(buildString("ENTER AbstractProcessorPlate.parseMultiComponentFile(", mcfile, ")"));

	  	// Number of wells with the error of number of cycles less than the expected (40 cycles).
	 	int errorCycles=0;

		String line ="";

		Pattern p1= Pattern.compile("^Well\\tTime\\tTemp\\t.+");
		Pattern p2= Pattern.compile("^\\d+\\t.+");

		int saturationStartCycle= TOTCYCLES - 1;
		int saturationEndCycle= TOTCYCLES;
		int averageCycles= 3;
		int wellNumber= 0;

		WellRnBO[] wellrns;
		
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new FileReader(mcfile));
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			throw new SaxException(e);
		}

		try {
			while (assertNotNull((line= reader.readLine()))) {

				Matcher m1= p1.matcher(line);

				if (m1.matches()) {

					wellNumber++;
					log.debug(buildString("Processing well ", wellNumber));

					RawWellDataDAO rwdDao = new RawWellDataDAO();
					
					// find well object
					RawWellDataBO well= rwdDao.findByPlatesetRowCol(
							plate.getPlateset().getId(), plate.getNumberinset(), 
							well2Row(wellNumber), well2Column(wellNumber));

					if (assertNull(well))
						throw new SaxException(buildString("Well number ", wellNumber,
								" invalid for plate barcode ", this.plate.getPlatebarcode()));

					log.debug(buildString("Found well object, ID=", well.getId()));
					
					// Reset error code in case of reprocessing
					well.setErrornum(0);
					
					// generate RN array if necessary
					if (well.getRn() == null || well.getRn().length == 0) {
						log.debug(buildString("Generating new array of ", TOTCYCLES+1, " RN elements"));
						wellrns = new WellRnBO[TOTCYCLES + 1];
						for (int j=0; j < wellrns.length; j++) {
							wellrns[j] = new WellRnBO();
							wellrns[j].setRn(new Double(0.0));
							new WellRnDAO().persist(wellrns[j]);
						}
						well.setRn(wellrns);
					} else {
						wellrns = well.getRn();
					}

					double totalBackgroundFam= 0;
					double totalBackgroundROX= 0;
					int backgroundFamReadings= 0;
					int datalines= 1;

					double[][] famval= new double[TOTCYCLES + 1][20];
					double[][] roxval= new double[TOTCYCLES + 1][20];
					double[][] tamraval= new double[TOTCYCLES + 1][20];

					int[] readings= new int[TOTCYCLES + 1];

					// cycle through all the lines for this well
					line= reader.readLine();

					Matcher m2= p2.matcher(line);

					while (m2.matches()) { // Start of processorWell

						// end of well data reached?
						if ( ! m2.matches()) {
							break;
						}

						//log.debug(buildString("Processing line ", line));

						String f[]= line.split("\\t");

						double temp= Double.parseDouble(f[2]);
						int cycle= Integer.parseInt(f[3]);
						int step= Integer.parseInt(f[4]);
						int repeat= Integer.parseInt(f[5]);
						double fam= Double.parseDouble(f[6]);
						double tamra= Double.parseDouble(f[7]);
						double rox= Double.parseDouble(f[8]);

						if (datalines >= BKG_FAM_START_READING && datalines <= BKG_FAM_END_READING) {

							totalBackgroundFam += fam;
							totalBackgroundROX += rox;
							backgroundFamReadings++;
						}

						double temp_deviation= Math.abs(temp - ANNEAL_TEMPERATURE);

						if (cycle == 1 && step == 1 && temp_deviation <= ANNEAL_TEMP_DEVIATION) {

							readings[repeat]++;
							famval[repeat][readings[repeat]]= fam;
							roxval[repeat][readings[repeat]]= rox;
							tamraval[repeat][readings[repeat]]= tamra;
						}

						line= reader.readLine();
						datalines++;
						m2= p2.matcher(line);
					} // End of processorWell

					log.debug(buildString("Finished reading data for well ", wellNumber));

					double totalBaselineFam= 0;
					double totalBaselineROX= 0;
					double totalBaselineTamra= 0;
					int baselineFamReadings= 0;
					double totalSaturationFam= 0;
					int saturationFamReadings= 0;

					log.debug(buildString("AverageCycles: ", averageCycles));
					
					for (int i= 1;  i <= TOTCYCLES;  i++) {

						int n= readings[i];

						if (n == 0) {
							log.debug(buildString("0 cycles, errornum: ", CYCLES_ERR));
							well.setErrornum(CYCLES_ERR);
						}
						else {
							double sumRatio= 0;

							for (int j= readings[i]; j > readings[i] - averageCycles; j--) {

								if (roxval[i][j] < 0) {

									sumRatio += famval[i][j];
								}
								else {

									sumRatio += famval[i][j] / roxval[i][j];
								}
							}

							double rn= sumRatio / averageCycles;

							if (rn < 0) {
								log.debug(buildString("Well ", well.getId(), " is empty, errornum: ", EMPTY_WELL_ERR));
								well.setErrornum(EMPTY_WELL_ERR);
								well.setIsEmpty(true);
							}

							wellrns[i].setRn(rn);
							
							log.debug(buildString("Setting RN ", i, "to ", rn));
							log.debug(buildString("readings[",i,"]: ", readings[i]));
							log.debug(buildString("SumRatio: ", sumRatio));
						}

						if (i >= FAM_BASELINE_START_CYCLE &&
								i <= FAM_BASELINE_END_CYCLE) {
							for (int j= 1; j <= readings[i]; j++) {

								totalBaselineFam += famval[i][j];
								totalBaselineROX += roxval[i][j];
								totalBaselineTamra += tamraval[i][j];
								baselineFamReadings++;
							}
						}

						if (i >= saturationStartCycle && i <= saturationEndCycle) {

							for (int j= 1; j <= readings[i]; j++) {

								totalSaturationFam += famval[i][j];
								saturationFamReadings++;
							}
						}
					}

					if (well.getErrornum() == CYCLES_ERR) {
						errorCycles++;
					}

					// CHANGED TYPE! SMEIER 02/02/2009
					well.setBaselinefam(totalBaselineFam / baselineFamReadings);

					if (well.getBaselinefam() < MIN_FAM) {
						log.debug(buildString("Well ", well.getId(), " is empty, errornum: ", EMPTY_WELL_ERR));
						well.setErrornum(EMPTY_WELL_ERR);
						well.setIsEmpty(true);
					}

					if (saturationFamReadings > 0) {
						well.setSaturationfam(totalSaturationFam / saturationFamReadings);
					}

					// CHANGED TYPE! SMEIER 02/02/2009
					log.debug(buildString("total Bkgfam sum: ", totalBackgroundFam));
					well.setBkgfam(totalBackgroundFam / (BKG_FAM_END_READING - BKG_FAM_START_READING + 1));

					// persist data
					rwdDao.persist(well);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new SaxException(e);
		}

		try {
			reader.close();
			if (DELETE_MC_FILE)
				new File(mcfile).delete();
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new SaxException(e);
		}

		// final check: error cycles below threshold?
		if (errorCycles >= 380){
			throw new SaxException("Error: IC");
		}
		
		log.debug("EXIT AbstractProcessorPlate.parseMultiComponentFile(String mcfile)");
	}

	/**
	 * 
	 * @param jndiName
	 * @return
	 */
	private Object getLocalBeanInterface(String jndiName) {
		log.debug(buildString("Obtaining local bean interface ", jndiName));
		
		Object res;
		try {
			res = new InitialContext().lookup(jndiName);
		} catch(NamingException e) {
			log.error(buildString("Error looking up local interface ", jndiName));
			throw new SaxException(e);
		}
		return res;
	}
	
}
