/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.processor.processcontrol;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.AggSectionResultBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.PlateProcessControlBO;
import com.xdx.sax.bo.ProcessControlDetailBO;
import com.xdx.sax.bo.ProcessControlDetailResultBO;
import com.xdx.sax.bo.ProcessControlRawScoreBO;
import com.xdx.sax.bo.ProcessControlRawScoreResultBO;
import com.xdx.sax.bo.QcResultFinalBO;
import com.xdx.sax.dao.AggSectionResultDAO;
import com.xdx.sax.dao.PlateProcessControlDAO;
import com.xdx.sax.dao.ProcessControlDetailDAO;
import com.xdx.sax.dao.ProcessControlDetailResultDAO;
import com.xdx.sax.dao.ProcessControlRawScoreDAO;
import com.xdx.sax.dao.ProcessControlRawScoreResultDAO;
import com.xdx.sax.dao.QcResultFinalDAO;
import com.xdx.sax.domain.Assay;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 *
 */
public class ProcessControlProcessorImpl extends AbstractProcessControlProcessor {

	private static Log log = LogFactory.getLog(ProcessControlProcessorImpl.class);
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.processor.processcontrol.ProcessControlProcessor#executeQc()
	 */
	public void executeQc() {
		if (this.dataSet == null) {
			log.error(buildString("Dataset not initialized"));
			throw new SaxException("Dataset not initialized");
		}

		long plateSetId = dataSet.getPlateSet().getId();
		long plateSectionId = dataSet.getPlateSection().getId();
		PlateProcessControlBO pc =
			new PlateProcessControlDAO().findByPlateSetSection(
					plateSetId, plateSectionId);

		// Do we have a process control here?
		if (pc == null) {
			log.debug(buildString("Plateset ", plateSetId, " section ", plateSectionId,
					" is not a process control."));
			return;
		}
		
		pc.setIsOk(true);
		pc.setFailureReason(null);

		// get result for this set/section
		AggSectionResultBO res = new AggSectionResultDAO().findBySetSection(
				dataSet.getPlateSet().getId(), 
				dataSet.getPlateSection().getId());
		if (res == null)
			throw new SaxException(buildString("No results for set", dataSet.getPlateSet().getExternalId(), " section ", dataSet.getPlateSection().getName()));
		
		processRawScore(pc, res);
		
		processAssays(pc);
	}
	
	/**
	 * 
	 * @param pc Process control object
	 * @param res 
	 */
	private void processRawScore(PlateProcessControlBO pc, AggSectionResultBO res) {
		ProcessControlRawScoreBO pcScore = new ProcessControlRawScoreDAO().findByProcessControl(pc.getProcesscontrol());
		// do we have a raw score for this process control?
		if (pcScore == null) {
			pc.setIsOk(false);
			pc.setFailureReason(buildString("No raw score value for process control ", pc.getProcesscontrol().getExternalid()));
			return;
		}
		
		// check if existing or new result
		ProcessControlRawScoreResultDAO dao = new ProcessControlRawScoreResultDAO();
		ProcessControlRawScoreResultBO result = dao.findBySetSection(
				dataSet.getPlateSet().getId(), 
				dataSet.getPlateSection().getId());
		if (result == null) {
			result = new ProcessControlRawScoreResultBO();
			result.setPlateSet(dataSet.getPlateSet());
			result.setPlateSection(dataSet.getPlateSection());
			result.setProcessControlRawScore(pcScore);
			dao.persist(result);
		}
		result.setPass(true);
		result.setFailureReason("");
		
		// check expected against effective
		try {
			if (Math.abs(res.getAlgoscore() - pcScore.getExpectedRawScore()) > pcScore.getBound()) {
				result.setPass(false);
				result.setFailureReason(buildString("AlgoScore ", res.getAlgoscore(), "fails check against expected ", pcScore.getExpectedRawScore()));
			}
		} catch (NullPointerException e) {
			result.setPass(false);
			result.setFailureReason(buildString("AlgoScore ", res.getAlgoscore(), "fails check against expected ", pcScore.getExpectedRawScore()));			
		}
	}

	/**
	 * 
	 * @param pc process control object
	 * @param res 
	 */
	private void processAssays(PlateProcessControlBO pc) {
		List<String> failedMarkers = new ArrayList<String>();
		
		// Check the process control for each marker
		for (Assay assay : dataSet.getAssays()) {
			
			// ATTENTION: process controls are only defined for one marker per assay
			if (assay.getMarkers().size() > 1)
				throw new SaxException(buildString("Cannot check processcontrol for assays with more (", assay.getMarkers().size(), ") than 1 marker."));
			
			MarkerBO marker = (MarkerBO) assay.getMarkers().toArray()[0];
			log.debug(buildString("Checking marker ", marker.getName()));
			ProcessControlDetailBO pcDetail =
				new ProcessControlDetailDAO().findByProcessControlMarker(
						pc.getProcesscontrol().getId(), marker.getId());
			
			if (pcDetail == null) {
				log.debug(buildString("No expected value for marker ", marker.getName(), " - skipping"));
				continue;
			}
			
			ProcessControlDetailResultDAO pcdDao = new ProcessControlDetailResultDAO();
			
			ProcessControlDetailResultBO result = pcdDao.findByPcSetSection(
					pcDetail.getId(),
					this.dataSet.getPlateSet().getId(),
					this.dataSet.getPlateSection().getId());
			
			if (result == null) {
				result = new ProcessControlDetailResultBO();
				result.setProcessControlDetail(pcDetail);
				result.setPlateSet(this.dataSet.getPlateSet());
				result.setPlateSection(this.dataSet.getPlateSection());
				pcdDao.persist(result);
			} else {
				log.debug(buildString("Using existing result record"));
			}
			
			log.debug(buildString("Checking XDx CT against expected val"));

			if (assay.getResult().getWsmxdxct() == null) {
				pc.setIsOk(false);
				result.setPass(false);
				result.setFailureReason(buildString("No result for marker ", marker.getName()));
			} else if (Math.abs(pcDetail.getExpectedValue() - assay.getResult().getWsmxdxct()) > (pcDetail.getBound())) {
				// compare the assay CT with the process control (check against bound)
				String failureReason = buildString("Process control for marker ", marker.getName(), " failed.");
				
				pc.setIsOk(false);
				failedMarkers.add(marker.getName());
				result.setPass(false);
				result.setFailureReason(failureReason);
			} else {
				result.setPass(true);
				result.setFailureReason(null);
			}
		}
		
		log.debug(buildString("Res: ", pc.getIsOk(), " failed markers: ", failedMarkers.toString()));
		if (!pc.getIsOk())
			pc.setFailureReason(buildString("Process control failed for markers ", failedMarkers.toString()));

	}
}
