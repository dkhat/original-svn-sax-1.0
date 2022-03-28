package com.xdx.sax.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
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
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.assembler.TOAssembler;
import com.xdx.sax.bo.PlateDesignBO;
import com.xdx.sax.bo.QcCriteriaStepTemplateBO;
import com.xdx.sax.dao.PlateDesignDAO;
import com.xdx.sax.dao.QcCriteriaStepTemplateDAO;
import com.xdx.sax.domain.HTxSaxDataSetImpl;
import com.xdx.sax.domain.PlateDesignHandlerImpl;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.to.QcCriteriaStepTemplateTO;
import com.xdx.sax.util.ws.PlateDesignParam;
import com.xdx.sax.util.ws.PlateRegParameter;
import com.xdx.sax.util.ws.PlateRegParameterSimple;
import com.xdx.sax.util.ws.ProcessControlParameter;
import com.xdx.sax.util.ws.Result;

/**
 * 
 * @author smeier
 * 
 */
@Stateless(name="SaxWsTestService")
@WebService(name="SaxWsTestService")
@SOAPBinding(style=SOAPBinding.Style.RPC)
public class SaxWsTestBean {

    //
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(SaxWsTestBean.class);

	/**
	 * This method is used to upload SDS files directly
	 * Caller needs to know the location where to put the file
	 * 
	 * @param fileName fully qualified path
	 * @param data binary SDS data
	 */
	@WebMethod
	public void postSdsFile(
			@WebParam(name="fileName") String fileName,
			@WebParam(name="data") byte[] data)
	{
		try {
			FileOutputStream fos = new FileOutputStream(buildString("/tmp/", fileName));
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			throw new SaxException(e);
		}
	}
	
	@WebMethod
	@WebResult(name="plateRegistrationResult")
	public Result registerPlatesSimple(
			@WebParam(name="parameterList") PlateRegParameterSimple[] parameter) {

		PlateRegParameter[] plateRegParam = new PlateRegParameter[parameter.length];
		for (int i=0; i<parameter.length; i++) {
			plateRegParam[i] = new PlateRegParameter();
			plateRegParam[i].setEmptySections(new String[0]);
			plateRegParam[i].setExternalPlateSetId(parameter[i].getExternalPlateSetId());
			plateRegParam[i].setPlateBarcode(parameter[i].getPlateBarcode());
			plateRegParam[i].setPlateDesignId(parameter[i].getPlateDesignId());
			plateRegParam[i].setPlateNum(parameter[i].getPlateNum());
			plateRegParam[i].setProcessControls(new ProcessControlParameter[0]);
			plateRegParam[i].setTestcode(parameter[i].getTestcode());
			plateRegParam[i].setProcessControlsMandatory(false);
		}
		
		log.info(buildString("Registering ", plateRegParam.length, " plates in the queue"));
		
    	try {
	        InitialContext ctx = new InitialContext();
	        Queue queue = (Queue) ctx.lookup(SaxCoreServiceBean.QUEUE_NAME);
	        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
	        QueueConnection cnn = factory.createQueueConnection();
	        QueueSession session = cnn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
	
	        //Message msg2 = session.createMessage();
	        ObjectMessage msg2 = session.createObjectMessage(plateRegParam);
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
	
	/**
	 * This method is used to upload SDS files directly
	 * Caller needs to know the location where to put the file
	 * 
	 * @param fileName fully qualified path
	 * @param data binary SDS data
	 */
	@WebMethod
	@WebResult(name="assayInfo")
	public String testAssay(
			@WebParam(name="platesetId") int platesetId,
			@WebParam(name="platesectionId") int platesectionId)
	{
		HTxSaxDataSetImpl dataSet = new HTxSaxDataSetImpl(platesetId, platesectionId);
		if (dataSet == null)
			return "NULL dataset";
		if (dataSet.getAssay("18s") == null)
			return "18s assay is null";
		if (dataSet.getAssay("18s").getResult() == null)
			return "18s result is null";
		if (dataSet.getAssay("18s").getResult().getWsmnormct() == null)
			return ("18s normalized CT is null");
		return "18s normalized ct is " + dataSet.getAssay("18s").getResult().getWsmnormct();
	}
	
	@WebMethod
	@WebResult(name="result")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Result registerPlateDesign(
			@WebParam(name="testcodeName") String testcodeName,
			@WebParam(name="plateDesignName") String plateDesignName,
			@WebParam(name="masterMixVol") Double masterMixVol,
			@WebParam(name="availableWellVol") Double availableWellVol,
			@WebParam(name="numberOfPlates") Integer numberOfPlates,
			@WebParam(name="rowsPerPlate") Integer rowsPerPlate,
			@WebParam(name="columnsPerPlate") Integer columnsPerPlate,
			@WebParam(name="details") PlateDesignParam[] details) {

		Result result = new Result();
		result.setOk(true);
		
		PlateDesignBO design = new PlateDesignDAO().findByName(plateDesignName);
		if (design != null) {
			result.setErrorMessage("plate design exists");
			result.setOk(false);
			return result;
		}

		try {
			new PlateDesignHandlerImpl().registerPlateDesign(testcodeName, plateDesignName, numberOfPlates, rowsPerPlate, columnsPerPlate, masterMixVol, availableWellVol, details);
		} catch (Exception e) {
			result.setErrorMessage(e.getMessage());
			result.setOk(false);
			log.error(getStackTrace(e));
		}
		
		return result;
	}

	@WebMethod
	@WebResult(name="result")
	public Result createOrUpdateQcTemplate (
			@WebParam(name="qcTemplateRecord") QcCriteriaStepTemplateTO rec) {
		Result res = new Result();
		
		try {
			QcCriteriaStepTemplateBO boRec = (QcCriteriaStepTemplateBO) TOAssembler.createBO(QcCriteriaStepTemplateBO.class, rec);
		
			new QcCriteriaStepTemplateDAO().attachDirty(boRec);
		} catch (Exception e) {
			res.setOk(false);
			res.setErrorMessage(e.getMessage());
			log.error(getStackTrace(e));
		}
		
		res.setOk(true);
		return res;
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
