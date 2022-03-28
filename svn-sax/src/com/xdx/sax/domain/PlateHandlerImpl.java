/**
 * 
 */
package com.xdx.sax.domain;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.AggMarkerResultBO;
import com.xdx.sax.bo.AggSectionResultBO;
import com.xdx.sax.bo.AggregateBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.bo.PlateDesignBO;
import com.xdx.sax.bo.PlateDesignDetailBO;
import com.xdx.sax.bo.PlateProcessControlBO;
import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.PlateSectionDetailBO;
import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.bo.ProcessControlBO;
import com.xdx.sax.bo.QcResultFinalBO;
import com.xdx.sax.bo.RawWellDataBO;
import com.xdx.sax.bo.TestcodeBO;
import com.xdx.sax.dao.AggMarkerResultDAO;
import com.xdx.sax.dao.AggSectionResultDAO;
import com.xdx.sax.dao.MarkerDAO;
import com.xdx.sax.dao.PlateDAO;
import com.xdx.sax.dao.PlateDesignDAO;
import com.xdx.sax.dao.PlateProcessControlDAO;
import com.xdx.sax.dao.PlateSectionDetailDAO;
import com.xdx.sax.dao.PlateSetDAO;
import com.xdx.sax.dao.ProcessControlDAO;
import com.xdx.sax.dao.QcResultFinalDAO;
import com.xdx.sax.dao.RawWellDataDAO;
import com.xdx.sax.dao.TestcodeDAO;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.util.ws.PlateRegParameter;
import com.xdx.sax.util.ws.ProcessControlParameter;
import com.xdx.sax.util.ws.Result;

/**
 * @author smeier
 *
 */
public class PlateHandlerImpl implements PlateHandler {

	private final Log log = LogFactory.getLog(PlateHandlerImpl.class);
	
	// Array of plate design detail info for reference
	private PlateDesignDetailBO[][][] designDetails;
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.PlateHandler#registerNewPlate(long, long, java.lang.String, long)
	 */
	public void registerNewPlate(
			String plateSetId, 
			long plateNum, 
			String testcode, 
			String plateBarcode, 
			long plateDesignId,
			boolean processControlsMandatory,
			ProcessControlParameter[] processControls,
			String[] emptySections)
	{
		// Log input parameters
		log.info(buildString("Registering plate with barcode ", plateBarcode,
				", external id ", plateSetId, ", plate number ", plateNum,
				" for plate design id ", plateDesignId));
		log.info(buildString(processControls.length, " processcontrols:"));
		for (ProcessControlParameter ppm: processControls)
			log.info(buildString("Plate ", ppm.getPlateSetId(), " section ", ppm.getPlateSectionName(), " process control '", ppm.getProcessControlLotName(), "' ..."));
		log.info(buildString(emptySections.length, " empty sections:"));
		for (String s: emptySections)
			log.info(buildString("Section ", s));
		
		// Does the plate design exist?
		PlateDesignBO plateDesign = new PlateDesignDAO().findById(plateDesignId);
		if (plateDesign == null) {
			throw new SaxException(buildString("Invalid plate design", plateDesignId));
		}	
		
		// set design details
		setDetails(plateDesign);
		
		// Does the plate exist?
		PlateBO plate = new PlateDAO().findByBarcode(plateBarcode);
		if (plate != null) {
			plate.getPlateset().setRegisteredTimestamp(new Date());
			log.error("plate with barcode" + plateBarcode +
			          " already exists. Cannot re-register");
			throw new SaxException(buildString("Plate ", plateBarcode, " already exists. Cannot re-register."));
		}

		// check existence of process controls (if mandatory)
		if (processControlsMandatory) {
			for (ProcessControlParameter ppm : processControls) {
				log.debug(buildString("Checking process control for section ", ppm.getPlateSectionName(), " and lot ", ppm.getProcessControlLotName()));
				String processControlName = ppm.getProcessControlLotName();
				ProcessControlBO pd = new ProcessControlDAO().findByExternalId(processControlName);
				if (pd == null) {
					log.error(buildString("Unknown process control ", processControlName));
					throw new SaxException(buildString("Process control ", processControlName, " has not been registered with SAX."));
				} else {
					log.debug(buildString(" ... found, id=", pd.getId()));
				}
			}
		} else {
			log.debug("Skipping process control check");
		}
		
		// Get plate Set
		PlateSetBO plateSet = createPlateSet(plateSetId, plateDesign, testcode);

		// Get plate
		plate = createPlate(plateSet, plateNum, plateBarcode);
		
		// Generate section result record
		generatePlateSectionResult(plateSet, plateDesign);

		// Generate section details
		generatePlateSectionDetails(plateSet, plateDesign, processControls, emptySections);

		// Generate final section result record
		generateFinalSectionResult(plateSet, plateDesign);

		// Generate section marker result records
		generateSectionMarkerResults(plateSet, plateDesign);

		// Generate raw result records
		generateRawResults(plate, plateDesign);

		log.info(buildString("=== done ==="));
	}

	public Result registerPlates(PlateRegParameter[] parameter) {
		log.debug(buildString("Registering ", parameter.length, " plates"));

		// initialize result data structure
		Result result = new Result();
		result.setOk(true);
		result.setErrorMessage("");

		// Cycle through parameter list and initialize plates
		for (int i=0; i<parameter.length; i++) {
			log.debug(buildString("Registering plate ", parameter[i].getPlateBarcode()));

			// TODO: fix PlateRegParameter !!!
			try {
				registerNewPlate(
						parameter[i].getExternalPlateSetId(), 
						parameter[i].getPlateNum(), 
						parameter[i].getTestcode(), 
						parameter[i].getPlateBarcode(), 
						parameter[i].getPlateDesignId(),
						parameter[i].isProcessControlsMandatory(),
						parameter[i].getProcessControls(),
						parameter[i].getEmptySections());
			} catch (Exception e) {
				// register exceptions if any
				result.setOk(false);
				result.setErrorMessage(buildString(result.getErrorMessage(),"\n\n", e.getMessage()));
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param plate
	 * @param plateDesign
	 */
	private void generateRawResults(PlateBO plate, PlateDesignBO plateDesign) {
		log.debug("Generating raw results");
		
		PlateDesignDetailBO designDetail;
		
		for (int i = 0; i < plateDesign.getNumberrowsperplate(); i++) {
			for (int j = 0; j < plateDesign.getNumbercolumnsperplate(); j++) {
				designDetail = designDetails[(int)plate.getNumberinset()-1][i][j]; 
				log.debug(buildString("Detailid for well ", i, ", ", j, " is ", designDetail.getId()));
				
				RawWellDataBO wellData = new RawWellDataBO();
				wellData.setRownumber(i);
				wellData.setColumnnumber(j);
				wellData.setPlate(plate);
				wellData.setAggregate(designDetail.getAggregate());
				for (MarkerBO marker : designDetail.getMarkers()) {
					log.debug(buildString("Setting well ", i, ", ", j, " to marker ", marker.getId()));
					wellData.getMarkers().add(new MarkerDAO().findById(marker.getId()));
				}
				wellData.setWellType(designDetail.getWellType());
				
				// is well empty?
				if (wellData.getMarkers().size() == 0)
					wellData.setIsEmpty(true);
				else
					wellData.setIsEmpty(false);
				
				// persist well data
				new RawWellDataDAO().persist(wellData);
			}	
		}		   		
		log.debug("... done");
	}

	/**
	 * 
	 * @param plateSet
	 * @param plateDesign
	 */
	private void generateSectionMarkerResults(PlateSetBO plateSet,
			PlateDesignBO plateDesign) {
		log.debug("Generating results per section/marker");
		
		// A set used to track which aggregates have been processed
		HashSet<Long> aggIds = new HashSet<Long>();
		AggregateBO aggregate;
		AggMarkerResultBO markerResult;
		
		// find marker id's for section/detail
		for (PlateSectionBO section : plateDesign.getPlatesections()) {
			// Generate Section/Marker specific result records
			for (PlateDesignDetailBO designDetail : section.getPlatedesigndetails()) {
				aggregate = designDetail.getAggregate();
				
				if (assertNotNull(aggregate) && (! aggIds.contains(aggregate.getId()))) {
					// Create and populate new record
					markerResult = new AggMarkerResultBO();
					markerResult.setPlateset(plateSet);
					markerResult.setPlatesection(section);
					for (MarkerBO marker : designDetail.getMarkers())
						markerResult.getMarkers().add(new MarkerDAO().findById(marker.getId()));
					markerResult.setAggregate(aggregate);

					new AggMarkerResultDAO().persist(markerResult);
					
					// Add ID to set of processed aggregates
					aggIds.add(aggregate.getId());
				}
			}
		}

		log.debug(buildString("... done, Result size ", aggIds.size()));		
	}

	/**
	 * 
	 * @param plateSet
	 * @param plateDesign
	 */
	private void generatePlateSectionResult(PlateSetBO plateSet,
			                                PlateDesignBO plateDesign) {
		log.debug("Generate plate section results");
		for (PlateSectionBO section : plateDesign.getPlatesections()) {
			// Generate section result record
			AggSectionResultBO sectionResult = new AggSectionResultBO();
			sectionResult.setPlateset(plateSet);
			sectionResult.setPlatesection(section);
			new AggSectionResultDAO().persist(sectionResult);
		}
		log.debug("... done");
	}

	/**
	 * 
	 * @param plateSet
	 * @param plateDesign
	 * @param processControlsMandatory 
	 */
	private void generatePlateSectionDetails(
			PlateSetBO plateSet,
            PlateDesignBO plateDesign,
            ProcessControlParameter[] processControls,
            String[] emptySections) {
		log.debug(buildString("Generate plate section details"));

		Set<String> esSet = new HashSet<String>(Arrays.asList(emptySections));
		Hashtable<String, String> esCtl = new Hashtable<String, String>();
		
		// build hashtable of process control parameters
		// skip null values (mandatory process control check was already performed)
		for (ProcessControlParameter ppm : processControls) {
			try {
				esCtl.put(ppm.getPlateSectionName(),ppm.getProcessControlLotName());
			} catch (NullPointerException e) {
				log.warn(buildString("NULL section or processcontrol name"));				
			}
		}
		
		for (PlateSectionBO section : plateDesign.getPlatesections()) {
			PlateSectionDetailBO det = new PlateSectionDetailBO();
			det.setPlateset(plateSet);
			det.setPlatesection(section);

			if (esSet.contains(section.getName()))
				det.setIsEmpty(true);
			else
				det.setIsEmpty(false);

			// Add process controls if any
			try {
				if (esCtl.containsKey(section.getName())) {
					// add processcontrol info to detail object
					String processControlName = esCtl.get(section.getName());
					ProcessControlBO pd = new ProcessControlDAO().findByExternalId(processControlName);
					
					det.setProcesscontrol(pd);
					
					if (assertNotNull(pd)) {
						// Create processcontrol result record
						PlateProcessControlBO ppc = new PlateProcessControlBO();
						ppc.setPlateset(plateSet);
						ppc.setPlatesection(section);
						ppc.setProcesscontrol(pd);
						new PlateProcessControlDAO().persist(ppc);
					}				
				}
			} catch (Exception e) {
				log.warn(buildString("Found non-existing process control ", esCtl.get(section.getName())));
			}
			
			new PlateSectionDetailDAO().persist(det);
		}
		
		log.debug(buildString("... done"));
	}
	
	/**
	 * 
	 * @param plateSet
	 * @param plateDesign
	 */
	private void generateFinalSectionResult(PlateSetBO plateSet,
			                                PlateDesignBO plateDesign) {
		log.debug("Generate final section results");
		for (PlateSectionBO section : plateDesign.getPlatesections()) {
			// Generate section result record
			QcResultFinalBO finalResult = new QcResultFinalBO();
			finalResult.setPlateset(plateSet);
			finalResult.setPlatesection(section);
			new QcResultFinalDAO().persist(finalResult);
		}
		log.debug("... done");
	}

	/**
	 * 
	 * @param plateSet
	 * @param plateNum
	 * @param plateBarcode
	 * @return
	 */
	private PlateBO createPlate(PlateSetBO plateSet, long plateNum,
			                    String plateBarcode) {
		log.debug("Creating plate");
		
		PlateBO plate = new PlateBO();
		
		plate.setNumberinset(plateNum);
		plate.setPlatebarcode(plateBarcode);
		plate.setPlateset(plateSet);
		plate.setRegisteredTimestamp(new Date());
		
		// persist plate
		new PlateDAO().persist(plate);
		
		log.debug("... done");
		
		return plate;
	}

	/**
	 * 
	 * @param plateSetId
	 * @param plateDesign
	 * @param testcode
	 * @return
	 */
	private PlateSetBO createPlateSet(String plateSetId, PlateDesignBO plateDesign, String testcode) {
		log.debug("Creating plate set");
		
		PlateSetDAO dao = new PlateSetDAO();
		// Create plate set only if it does not exist
		PlateSetBO plateSet = dao.findByExternalId(plateSetId);

		if (plateSet == null) {
			TestcodeBO template = new TestcodeBO();
			template.setTestcodename(testcode);
			
			plateSet = new PlateSetBO();
			plateSet.setExternalId(plateSetId);
			plateSet.setPlatedesign(plateDesign);
			plateSet.setTestcode((TestcodeBO) new TestcodeDAO().findByExample(template).toArray()[0]);
			plateSet.setRegisteredTimestamp(new Date());
		}
		
		log.debug(buildString("... done"));

		// persist plate set
		dao.persist(plateSet);
		
		return plateSet;
	}

	/**
	 * Set design details for further reference
	 * 
	 * @param design
	 */
	private void setDetails(PlateDesignBO design) {
		log.debug("Setting details");
		designDetails = new PlateDesignDetailBO[(int)design.getNumberofplates()][(int)design.getNumberrowsperplate()][(int)design.getNumbercolumnsperplate()];
		for (PlateDesignDetailBO detail : design.getPlatedesigndetails()) {
			designDetails[(int)detail.getNumberinset()-1][(int)detail.getPlaterow()][(int)detail.getPlatecolumn()] = detail;
		}
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
