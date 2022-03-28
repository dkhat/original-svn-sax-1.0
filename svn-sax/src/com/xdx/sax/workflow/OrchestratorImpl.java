/**
 * 
 */
package com.xdx.sax.workflow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.AggSectionResultBO;
import com.xdx.sax.bo.ProcessStepBO;
import com.xdx.sax.dao.AggSectionResultDAO;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;


/**
 * @author smeier
 *
 */
public class OrchestratorImpl extends AbstractOrchestrator {


	//
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(OrchestratorImpl.class);
	
	public OrchestratorImpl(String testcodeName) {
		log.debug(buildString("Initializing orchestrator for testcode ", testcodeName));
		initialize(testcodeName); 		
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.Orchestrator#execute(java.lang.String)
	 */
	public void execute(SaxDataSet dataSet) {
		log.info(buildString("Executing orchestrator ..."));
		OrchestratorStep  component;
		for (ProcessStepBO step : getSteps()) {
			try {
				log.debug(buildString("Loading class ",
						step.getProcesselement().getClassname()));
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				Class<?> clazz = cl.loadClass(step.getProcesselement().getClassname());
				
				log.debug("Instantiating");
				component = (OrchestratorStep) clazz.newInstance();
				
				log.debug("Initializing with data set");
				component.initialize(dataSet);
				
				log.info(buildString("Executing orchestrator step ", component.getClass().getName()));
				component.execute();
				log.info(buildString("=== done ==="));
			} catch (ClassNotFoundException e) {
				throw new SaxException(e);
			} catch (IllegalAccessException e) {
				throw new SaxException(e);
			} catch (InstantiationException e) {
				throw new SaxException(e);
			} catch (Exception e) {
				AggSectionResultBO res = new AggSectionResultDAO().findBySetSection(dataSet.getPlateSet().getId(), dataSet.getPlateSection().getId());
				res.setQcstatus("FAIL");
				res.setQcfailreason(e.getMessage());
				log.error(e.getMessage());
				log.error(getStackTrace(e));
			}
		}
		log.info(buildString("=== done ==="));
	}

}
