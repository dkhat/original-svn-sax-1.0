package com.xdx.sax.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.assembler.TOAssembler;
import com.xdx.sax.bo.AggregateBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.OldPlateMapBO;
import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.bo.PlateDesignBO;
import com.xdx.sax.bo.PlateDesignDetailBO;
import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.bo.QcCriteriaBO;
import com.xdx.sax.bo.QcCriteriaStepBO;
import com.xdx.sax.bo.QcCriteriaStepResultBO;
import com.xdx.sax.bo.QcCriteriaStepTemplateBO;
import com.xdx.sax.bo.QcResultFinalBO;
import com.xdx.sax.bo.QcSchemaBO;
import com.xdx.sax.bo.TestcodeBO;
import com.xdx.sax.bo.WellTypeBO;
import com.xdx.sax.dao.AggregateDAO;
import com.xdx.sax.dao.MarkerDAO;
import com.xdx.sax.dao.OldPlateMapDAO;
import com.xdx.sax.dao.PlateDAO;
import com.xdx.sax.dao.PlateDesignDAO;
import com.xdx.sax.dao.PlateDesignDetailDAO;
import com.xdx.sax.dao.PlateSectionDAO;
import com.xdx.sax.dao.PlateSetDAO;
import com.xdx.sax.dao.QcCriteriaStepResultDAO;
import com.xdx.sax.dao.QcResultFinalDAO;
import com.xdx.sax.dao.QcSchemaDAO;
import com.xdx.sax.dao.TestcodeDAO;
import com.xdx.sax.dao.WellTypeDAO;
import com.xdx.sax.domain.HTxSaxDataSetImpl;
import com.xdx.sax.domain.PlateDesignHandlerImpl;
import com.xdx.sax.domain.PlateHandlerImpl;
import com.xdx.sax.domain.ProcessControlHandlerImpl;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.processor.FileProcessor;
import com.xdx.sax.processor.PlateProcessorImpl;
import com.xdx.sax.processor.SdsFileProcessorImpl;
import com.xdx.sax.qc.criteria.step.QcCriteriaStep;
import com.xdx.sax.schedulable.SdsDatabaseScannerImpl;
import com.xdx.sax.schedulable.SdsFileScannerImpl;
import com.xdx.sax.to.PlateDesignTO;
import com.xdx.sax.to.TestcodeTO;
import com.xdx.sax.util.ws.PlateDesignData;
import com.xdx.sax.util.ws.PlateDesignParam;
import com.xdx.sax.util.ws.PlateQcResult;
import com.xdx.sax.util.ws.PlateRegParameter;
import com.xdx.sax.util.ws.ProcessControlItem;
import com.xdx.sax.util.ws.ProcessControlParameter;
import com.xdx.sax.util.ws.QcResult;
import com.xdx.sax.util.ws.QcStepResult;
import com.xdx.sax.util.ws.Result;
import com.xdx.sax.util.ws.SampleParameter;
import com.xdx.sax.util.ws.SampleResult;
import com.xdx.sax.util.ws.WsSampleId;

/**
 * 
 * @author smeier
 * 
 */
@Stateless(name="SaxApi")
@WebService(name="SaxApi")
@SOAPBinding(style=Style.RPC)
public class SaxApiServiceBeanImpl implements SaxApiServiceBean {

    //
	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(getClass());

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
	@WebMethod
	@WebResult(name="plateRegistrationResult")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Result registerPlate(
			@WebParam(name="plateSetId") String plateSetId,
			@WebParam(name="plateNum") int plateNum,
			@WebParam(name="testcode") String testcode, 
			@WebParam(name="plateBarcode") String plateBarcode,
			@WebParam(name="plateDesignId") int plateDesignId,
			@WebParam(name="processControlsMandatory") boolean processControlsMandatory,
			@WebParam(name="processControls") ProcessControlParameter[] processcontrols,
			@WebParam(name="emptySections") String[] emptySections)
    {		   
    	Result result = new Result();
    	result.setOk(true);
    	result.setErrorMessage("");
    	
    	try {
    		new PlateHandlerImpl()
    		.registerNewPlate(
    				plateSetId, (long)plateNum, 
    				testcode, plateBarcode,
    				(long)plateDesignId,
    				processControlsMandatory,
    				processcontrols,
    				emptySections);
    	} catch (Exception ex) {
    		result.setOk(false);
    		result.setErrorMessage(ex.getMessage());
    		log.error(getStackTrace(ex));
    	}

    	return result;
    }

	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#registerPlates(com.xdx.sax.util.PlateRegParameter[])
	 */
	@WebMethod
	@WebResult(name="plateRegistrationResult")
	public Result registerPlates(
			@WebParam(name="parameterList") PlateRegParameter[] parameter) {

    	try {
	        InitialContext ctx = new InitialContext();
	        Queue queue = (Queue) ctx.lookup(SaxCoreServiceBean.QUEUE_NAME);
	        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
	        QueueConnection cnn = factory.createQueueConnection();
	        QueueSession session = cnn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
	
	        //Message msg2 = session.createMessage();
	        ObjectMessage msg2 = session.createObjectMessage(parameter);
	        msg2.setStringProperty(SaxCoreServiceBean.METHOD_PARAM, SaxCoreServiceBean.REGISTER_PLATE_METHOD);
	        log.debug(buildString("Setting paramters in message"));
	        //msg2.setObjectProperty(SaxCoreServiceBean.REGISTER_PLATE_METHOD, (Object) parameter);
	        
	        QueueSender sender = session.createSender(queue);
	        log.debug(buildString("Sending message"));

	        sender.send(msg2);
	        session.close();
	        cnn.close();
    	} catch (NamingException e) {
    		throw new SaxException(e);    		
    	} catch (JMSException e) {
    		throw new SaxException(e);
    	}

    	return new Result(true, "");
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#processFiles()
	 */
	@WebMethod
	@WebResult(name="processFilesResult")
    public String processFiles() {
    	SdsFileScannerImpl scanner = new SdsFileScannerImpl();
    	scanner.perform(new Date(), (long)0);

    	return "OK";
    }

    /* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#test()
	 */
	@WebMethod
	@WebResult(name="processSdsDatabaseResult")
    public String processSdsDatabase () {
    	SdsDatabaseScannerImpl scanner = new SdsDatabaseScannerImpl();
    	scanner.perform(new Date(), (long)0);

    	return "OK";
    }

	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#processPlate(java.lang.String)
	 */
	@WebMethod
	@WebResult(name="processPlateResult")
    public String processPlate(
    		@WebParam(name="plateBarcode") String plateBarcode) {

		// reset plate record
		PlateBO plate = new PlateDAO().findByBarcode(plateBarcode);
		if (plate != null) plate.setProcessorError(null);
		
		// reset plate set record
		PlateSetBO plateset = new PlateSetDAO().findByExternalId(plateBarcode);
		if (plateset != null) plateset.setProcessedTimestamp(null);
		
		if (false) {
	    	try {
		        InitialContext ctx = new InitialContext();
		        Queue queue = (Queue) ctx.lookup("queue/sax/core");
		        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
		        QueueConnection cnn = factory.createQueueConnection();
		        QueueSession session = cnn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		
		        Message msg2 = session.createMessage();
		        msg2.setStringProperty(SaxCoreServiceBean.METHOD_PARAM, SaxCoreServiceBean.PROCESS_PLATE_METHOD);
		        msg2.setStringProperty(SaxCoreServiceBean.PROCESS_PLATE_BARCODE_PARAM, plateBarcode);
		        
		        QueueSender sender = session.createSender(queue);
		        sender.send(msg2);
		        session.close();
		        cnn.close();
	    	} catch (NamingException e) {
	    		throw new SaxException(e);    		
	    	} catch (JMSException e) {
	    		throw new SaxException(e);
	    	}
		}
		
    	return "OK";
    }


	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#convertPlatemap(java.lang.String)
	 */
	@WebMethod
	@WebResult(name="convertPlatemapResult")
    public String convertPlatemap(
    		@WebParam(name="plateMapName") String plateMapName) {
    	log.info(buildString("Converting plate map", plateMapName));
    	int rows=16;
    	int cols=24;
    	PlateSectionBO section=null;
    	PlateDesignDetailBO detail=null;
    	Hashtable<String, AggregateBO> aggregates = null;
    	
    	List<OldPlateMapBO> recs = new OldPlateMapDAO().findByName(plateMapName);
    	if (recs.size() == 0) {
    		log.error(buildString("Cannot find plate map ", plateMapName));
    		return "NOTOK";
    	}
		   
    	PlateDesignDAO desDao = new PlateDesignDAO();
    	
    	PlateDesignBO design = desDao.findByName(plateMapName);
    	if (design != null) {
    		log.error(buildString("Plate design ", plateMapName, " exists already."));
    		return "NOTOK";
    	}
		   
    	int row;
    	int col;
    	HashMap<Integer, Set<Integer>> sectionData = new HashMap<Integer, Set<Integer>>(); 
    	String[][] markerData = new String[rows][cols]; 

    	// process data from oldplatemap table
    	for (OldPlateMapBO rec : recs) {
    		log.debug(buildString("Processing old plate record ", rec.getId()));
    		
    		row=(int)(rec.getWellName()-1) / 24;
    		col=(int)(rec.getWellName()-1) % 24;
    		
    		log.debug(buildString("Looking for key ", rec.getSection()));
    		if (! sectionData.containsKey((int)rec.getSection())) {
    			log.debug(buildString("Adding key ", rec.getSection()));
    			sectionData.put((int)rec.getSection(), new HashSet<Integer>());
    		}
    		try {
    			sectionData.get((int)rec.getSection()).add((int)rec.getWellName());
    		} catch (NullPointerException e) {
    			log.error(buildString("Section " , rec.getSection(), " well ", rec.getWellName()));
    		}
    		markerData[row][col]=rec.getMarkerName();
    	}
		   
    	// now create the new objects
    	log.debug(buildString("Creating new plate design record"));
    	design = new PlateDesignBO();
    	design.setAvailablewellvolume(new Double(3));
    	design.setMastermixvolume(new Double(3));
    	design.setName(plateMapName);
    	design.setNumberofplates(1);
    	design.setNumberrowsperplate(rows);
    	design.setNumbercolumnsperplate(cols);
    	design.setTestcodes(null);

    	log.debug(buildString("Setting test code"));
    	TestcodeBO testcode = new TestcodeDAO().findById(new Long(1));
    	Set<TestcodeBO> tcs = new HashSet<TestcodeBO>();
    	tcs.add(testcode);
    	design.setTestcodes(tcs);

    	desDao.persist(design);
    	
    	for (int sectionNum : sectionData.keySet()) {
    		log.debug(buildString("Creating new plate section ", sectionNum));
    		section = new PlateSectionBO();
    		section.setName(new Integer(sectionNum).toString());
    		section.setPlatedesign(design);
    		new PlateSectionDAO().persist(section);
    		aggregates = new Hashtable<String, AggregateBO>();
			   
    		for (int wellNum : sectionData.get(sectionNum)) {
    			row=(wellNum-1) / 24;
    			col=(wellNum-1) % 24;
				   
    			if (row >= rows || col >= cols) {
    				log.error(buildString("plate map record ", wellNum, " in platemap ", design.getName()));
    				throw new SaxException("Wrong platemap record");
    			}
				   
    			log.debug(buildString("Creating new detail record for row ", row, " and col ", col));
    			detail = new PlateDesignDetailBO();
    			detail.setNumberinset(1);
    			detail.setPlaterow(row);
    			detail.setPlatecolumn(col);
    			detail.setPlatedesign(design);
    			detail.setPlatesection(section);
    			
    			String markerName = markerData[row][col];
    			String wellTypeName = markerName;
    			
    			log.debug(buildString("Adding marker ", markerName));
				MarkerBO marker = new MarkerDAO().findByName(markerName);
				if (marker == null) {
					log.error(buildString("Cannot find marker ", markerName));
					throw new SaxException(buildString("Unknown marker ", markerName));
				}
				
				Set<MarkerBO> markerSet = new HashSet<MarkerBO>();
				markerSet.add(marker);
				detail.setMarkers(markerSet);
				wellTypeName = marker.getMarkertype().getName();
			   
				if(! aggregates.containsKey(markerName)) {
					log.debug(buildString("Creating aggregate ", markerName));
					AggregateBO agg = new AggregateBO();
					agg.setName(markerName);
					agg.setPlateSection(section);
					new AggregateDAO().persist(agg);
					aggregates.put(markerName, agg);
				}
				log.debug(buildString("Adding aggregate ", markerName));
				detail.setAggregate(aggregates.get(markerName));
				   
    			log.debug(buildString("Setting welltype"));
    			WellTypeBO wellType = new WellTypeDAO().findByName(wellTypeName);
    			detail.setWellType(wellType);
				   
    			new PlateDesignDetailDAO().persist(detail);
    		}
    	}
    	log.info(buildString("=== done ==="));
    	return "OK";
    }
	   
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#getAvailablePlateDesigns()
	 */
	@WebMethod
	@WebResult(name="testCodes")
    public TestcodeTO[] getAvailableTestcodes() {
		
		log.info(buildString("Building list of test codes"));
		
    	List<TestcodeBO> testCodes = new TestcodeDAO().findAll();
    	TestcodeTO[] results = new TestcodeTO[testCodes.size()];
    	try {
	    	int i=0;
	    	for (TestcodeBO testCode : testCodes) {
	    		log.info(buildString("Adding testcode ", testCode.getId()));
	    		
	    		results[i] = (TestcodeTO)TOAssembler.createTO(TestcodeTO.class, testCode);
	    		
	    		i++;
	    	}
    	} catch (Exception ex) {
    		log.error(getStackTrace(ex));
    	}
    	log.info(buildString("Returning list with ", results.length, " elements."));
    	
    	return results;
    }
	   
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#getAvailablePlateDesigns()
	 */
	@WebMethod
	@WebResult(name="plateDesigns")
    public PlateDesignData[] getAvailablePlateDesigns(
    		@WebParam(name="testcode") String testcode) {
		
		log.info(buildString("Building list of plate designs"));
		
    	List<PlateDesignBO> designs = new PlateDesignDAO().findByTestcode(testcode);
    	PlateDesignData[] results = new PlateDesignData[designs.size()];
    	try {
	    	int i=0;
	    	for (PlateDesignBO design : designs) {
	    		log.debug(buildString("Adding design ", design.getId()));
	    		
	    		String[] sectionNames = new String[design.getPlatesections().size()];
	    		int j=0;
	    		for (PlateSectionBO section: design.getPlatesections())
	    			sectionNames[j++] = section.getName();
	    		
	    		results[i] = new PlateDesignData();
	    		results[i].setPlateDesign((PlateDesignTO)TOAssembler.createTO(PlateDesignTO.class, design));
	    		results[i].setPlateSectionNames(sectionNames);
	    		
	    		i++;
	    	}
    	} catch (Exception ex) {
    		log.error(getStackTrace(ex));
    	}
    	log.info(buildString("Returning list with ", results.length, " elements."));
    	
    	return results;
    }
	   
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#executeQc(long, long)
	 */
	@WebMethod
	@WebResult(name="executeQcResult")
    public String executeQc(
    		@WebParam(name="platesetId") long platesetId, 
    		@WebParam(name="schemaId") long schemaId){
    	PlateSetBO plateSet = new PlateSetDAO().findById(platesetId);
    	for (PlateSectionBO section : plateSet.getPlatedesign().getPlatesections()) {
    		SaxDataSet dataSet = new HTxSaxDataSetImpl(platesetId, section.getId());
			   
    		QcSchemaBO schema = new QcSchemaDAO().findById(schemaId);
    		for (QcCriteriaBO criteria : schema.getQccriterias()) {
    			log.debug(buildString("Evaluating QC criteria ", criteria.getCriterianame()));
    			for (QcCriteriaStepBO step : criteria.getQccriteriasteps()) {
    				executeCheck(dataSet, step);
    			}
    		}
    	}
    	return "OK";
    }
	
	@SuppressWarnings("unchecked")
	@WebMethod
	@WebResult(name="executeCheckResult")
    private void executeCheck (SaxDataSet dataSet, QcCriteriaStepBO step) {
    	QcCriteriaStepTemplateBO template = step.getQccriteriasteptemplate();
    	ClassLoader cl = Thread.currentThread().getContextClassLoader();
    	try {
    		Class clazz = cl.loadClass(template.getClassname());
			
    		log.debug("Instantiating");
    		QcCriteriaStep check = (QcCriteriaStep) clazz.newInstance();

    		log.debug("Initializing with data set");
    		check.initialize(dataSet, step);
			
    		check.evaluate();
    	} catch (ClassNotFoundException e) {
    		log.error(buildString("Cannot load class ", template.getClassname()));
    		throw new SaxException(e);
    	} catch (IllegalAccessException e) {
    		log.error(buildString("Illegal access exception for class ", template.getClassname()));
    		throw new SaxException(e);
    	} catch (InstantiationException e) {
    		log.error(buildString("Illegal instantiation exception for class ", template.getClassname()));
    		throw new SaxException(e);
    	}
		   
    }
	   
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#registerProcessControl(int, int, int, int, java.lang.String)
	 */
	@WebMethod
	@WebResult(name="registerProcessControlResult")
    public Result registerProcessControl (
    		@WebParam(name="testcode") String testcode,
    		@WebParam(name="lotName") String lotName,
			@WebParam(name="expectedRawScore") Double expectedRawScore,
			@WebParam(name="standardDeviation") Double rawScoreSstandardDeviation,
    		@WebParam(name="processControlData") ProcessControlItem[] processControlData)
    {		   
    	Result result = new Result();
    	result.setOk(true);

    	try {
    		new ProcessControlHandlerImpl().registerNewLot(
    				testcode, 
    				lotName, 
    				expectedRawScore, 
    				rawScoreSstandardDeviation, 
    				processControlData);
    	} catch (Exception ex) {
    		log.error(getStackTrace(ex));
    		result.setOk(false);
    		result.setErrorMessage(ex.getMessage());
    	}

    	log.debug(buildString("Result: ", result.getOk()));
    	log.debug(buildString("Error message: ", result.getErrorMessage()));
    	
    	return result;
    }

	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#getSampleQcResults(com.xdx.sax.util.ws.SampleParameter[])
	 */
	@WebMethod
	@WebResult(name="sampleQcResults")
    public QcResult[] getSampleQcResults (SampleParameter[] parameters) {
    	if (parameters.length==0)
    		return null;
		   
    	QcResult[] results = new QcResult[parameters.length];
    	int i=0;
		   
    	try {
    		for (SampleParameter param : parameters)
    			results[i++] = new QcResult(param.getPlateSectionName(), param.getPlateSetName());
			   
			return results;
    	} catch (SaxException ex) {
    		log.error(getStackTrace(ex));
    		return null;
    	}
    }
	   
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#getQcStepResults(com.xdx.sax.util.ws.SampleParameter)
	 */
	@WebMethod
	@WebResult(name="qcStepResults")
    public QcStepResult[] getQcStepResults (SampleParameter param) {
    	PlateSetBO plateSet = new PlateSetDAO().findByExternalId(param.getPlateSetName());
    	PlateSectionBO plateSection = new PlateSectionDAO().findByDesignSectionname(plateSet.getPlatedesign().getId(), param.getPlateSectionName());
			   
    	// get individual step results
    	List<QcCriteriaStepResultBO> res = new QcCriteriaStepResultDAO().findBySetSection(plateSet.getId(), plateSection.getId());
    	QcStepResult[] stepResults = new QcStepResult[res.size()];
    	int i=0;

    	// add step results to result array
    	for (QcCriteriaStepResultBO stepResult : res) {
    		String name = stepResult.getQccriteriastep().getQccriteriasteptemplate().getName();
    		stepResults[i++] = new QcStepResult(name, stepResult.getPass(), stepResult.getResultvalue());
		}

		return stepResults;
		   
    }
	   
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#getSampleList()
	 */
	@WebMethod
	@WebResult(name="sampleList")
    public WsSampleId[] getSampleList () {
    	List<WsSampleId> result = new ArrayList<WsSampleId>();
		   
    	// get plate sets
    	List<PlateSetBO> plateSets = new PlateSetDAO().findAll();
		   
    	for (PlateSetBO plateSet : plateSets) {
    		// for each set, get number of plates and sections
    		// and iterate over all of them
    		long numPlates = plateSet.getPlatedesign().getNumberofplates();
    		Set<PlateSectionBO> sections = plateSet.getPlatedesign().getPlatesections();
			   
    		for (long j=1; j<=numPlates; j++) {
    			for (PlateSectionBO section : sections) {
    				result.add(new WsSampleId(plateSet.getExternalId(),
    						j, section.getName())); 
    			}
    		}
    	}
		   
    	WsSampleId[] resultArray = new WsSampleId[result.size()];
    	int i=0;
		   
    	for (WsSampleId obj : result)
    		resultArray[i++] = obj;
		   
    	return resultArray;
    }
	   
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#passSample(int, int, boolean, java.lang.String)
	 */
	@WebMethod
    public void passSample (
    		@WebParam(name="plateSetId") int plateSetId, 
    		@WebParam(name="plateSectionId") int plateSectionId, 
    		@WebParam(name="pass") boolean pass,
    		@WebParam(name="failureReason") String failureReason,
    		@WebParam(name="user") String user) {
    	QcResultFinalBO result = new QcResultFinalDAO().findByPlateSetSection(plateSetId, plateSectionId);
		   
    	if (result == null)
    		throw new SaxException(buildString("Cannot find result for plate set ", plateSetId, " and plate section ", plateSectionId));
		   
    	if (pass)
    		result.setUserqcresult("PASS");
    	else {
    		result.setUserqcresult("FAIL");
    	}
    	
    	result.setCreatedby(user);
    	result.setCreateddate(new Date());
    }

	/**
	 * 
	 */
	@WebMethod
	@WebResult(name="processFileResult")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String processFile(
			@WebParam(name="fileName") String fileName) {
		FileProcessor processor = new SdsFileProcessorImpl();
		return processor.process(fileName);		
	}

	/**
	 * 
	 * @param plateSetId
	 * @param plateSectionName
	 * @return
	 */
	@WebMethod
	@WebResult(name="sampleResult")
	public SampleResult getResult(
			@WebParam(name="plateSetId") String plateSetId, 
			@WebParam(name="plateSectionName") String plateSectionName) {
		
		SampleResult res;
		
		try {
			res = new SampleResult(plateSetId, plateSectionName);
		} catch (SaxException e) {
			log.error(getStackTrace(e));
			res = new SampleResult();
		}
		
		return res;
	}
	
	/**
	 * 
	 */
	@WebMethod
	@WebResult(name="plateQcResult")
	public PlateQcResult getPlateQcResult(
			@WebParam(name="barcode") String barcode) {
		return new PlateQcResult(barcode);
	}

	/**
	 * 
	 */
	@WebMethod
	public void passPlate(
			@WebParam(name="barcode") String barcode, 
			@WebParam(name="isPass") boolean pass, 
			@WebParam(name="failureReason") String failureReason, 
			@WebParam(name="comment") String comment,
			@WebParam(name="user") String user) {
		PlateBO plate = new PlateDAO().findByBarcode(barcode);
		
		if (plate == null)
			throw new SaxException(buildString("Unknown plate ", barcode));
		
		if (pass) {
			log.debug(buildString("Passing plate: ", barcode));
			plate.setQcResult("PASS");
			plate.setQcFailReason("");
		} else {
			log.debug(buildString("Failing plate: ", barcode));
			plate.setQcResult("FAIL");
			plate.setQcFailReason(failureReason);
		}

		plate.setQcComment(comment);
		plate.setQcDoneBy(user);
		plate.setQcDate(new Date());
	}


	/**
	 * 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processNextPlate() {
    	new PlateProcessorImpl().processNextPlate();
	}

	
    /* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#updatePlateDesign(long, java.lang.String, java.lang.Double, java.lang.Double)
	 */
	@WebMethod
	@WebResult(name="result")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Result updatePlateDesign(
			@WebParam(name="plateDesignId") long plateDesignId,
			@WebParam(name="plateDesignName") String plateDesignName,
			@WebParam(name="masterMixVol") Double masterMixVol,
			@WebParam(name="availableWellVol") Double availableWellVol) {
		
		Result res = new Result();
		res.setOk(true);
		res.setErrorMessage("");
				
		try {
			new PlateDesignHandlerImpl().updatePlateDesign(
					plateDesignId, plateDesignName, masterMixVol, availableWellVol);
		} catch(Exception e) {
			log.error(getStackTrace(e));
			res.setOk(false);
			res.setErrorMessage(e.getMessage());
		}
		
		return res;
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#registerPlateDesign(java.lang.String, java.lang.Double, java.lang.Double, com.xdx.sax.util.ws.PlateDesignParam[])
	 */
	@WebMethod
	@WebResult(name="result")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Result registerPlateDesign(
			@WebParam(name="plateDesignName") String plateDesignName,
			@WebParam(name="masterMixVol") Double masterMixVol,
			@WebParam(name="availableWellVol") Double availableWellVol,
			@WebParam(name="details") PlateDesignParam[] details) {
		// TODO Auto-generated method stub
		Result result = new Result();
		result.setOk(true);
		
		PlateDesignBO design = new PlateDesignDAO().findByName(plateDesignName);
		if (design != null) {
			result.setErrorMessage("plate design exists");
			result.setOk(false);
			return result;
		}
		
		result.setErrorMessage("Feature not implemented!");
		result.setOk(false);
		
		return result;
	}

	
	/* (non-Javadoc)
	 * @see com.xdx.sax.service.SaxApiServiceBean#getNewBarcodes()
	 */
	public List<String> getNewBarcodes() {
		List<PlateBO> newPlates = new PlateDAO().findNew();
		List<String> result = new ArrayList<String>();
		
		for (PlateBO plate : newPlates) {
			result.add(plate.getPlatebarcode());
		}
		
		return result;
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

    /**
     * get stack trace in a printable form
     * 
     * @param aThrowable exception
     * @return printable stack trace
     */
	protected String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

}
