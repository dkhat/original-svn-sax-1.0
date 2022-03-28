/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.processor;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.bo.PlateSectionBO;
import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.dao.PlateDAO;
import com.xdx.sax.dao.PlateSetDAO;
import com.xdx.sax.domain.HTxSaxDataSetImpl;
import com.xdx.sax.domain.SaxDataSet;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.workflow.Orchestrator;
import com.xdx.sax.workflow.OrchestratorImpl;

/**
 * @author smeier
 *
 */
public class PlateProcessorImpl extends XdxProcessorComponent implements PlateProcessor {

	private final Log log = LogFactory.getLog(getClass());

	public void process(String plateBarcode) throws SaxException {
		log.info(buildString("Processing plate with barcode: ", plateBarcode));

		PlateBO plate = new PlateDAO().findByBarcode(plateBarcode);
		processPlateSet(plate.getPlateset());
		
		log.info(buildString("=== done ==="));
	}

	public void processNextPlate() {
		// For all new plate sets check if we have
		// SDS data for all the plates of the set already
		for (PlateSetBO plateSet : new PlateSetDAO().findNew()) {
			boolean process=true;
			for (PlateBO p: plateSet.getPlates())
				if(p.getProcessorError() == null)
					process = false;
			
			// all good? then process it ...
			if (process) {
				processPlateSet(plateSet);
				break;
			}
		}		
	}

	private void processPlateSet (PlateSetBO plateSet) {
		log.info(buildString("Processing plate set ", plateSet.getId(), " (", plateSet.getExternalId(), ")"));
		
		for (PlateSectionBO section : plateSet.getPlatedesign().getPlatesections())
			processPlateSection(plateSet, section);

		plateSet.setProcessedTimestamp(new Date());

		log.info(buildString("=== done ==="));
	}
	
	private void processPlateSection (PlateSetBO plateSet, PlateSectionBO plateSection) {
		SaxDataSet dataset = null;

		log.info(buildString("Processing section ", plateSection.getName()));
		dataset = new HTxSaxDataSetImpl(plateSet.getId(), plateSection.getId());						

		Orchestrator orch = new OrchestratorImpl(plateSet.getTestcode().getTestcodename());
		orch.execute(dataset);
		
		log.info(buildString("=== done ==="));
	}

}
