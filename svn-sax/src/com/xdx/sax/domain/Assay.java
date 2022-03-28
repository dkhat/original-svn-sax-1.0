package com.xdx.sax.domain;

import java.util.Set;

import com.xdx.sax.bo.AggMarkerResultBO;
import com.xdx.sax.bo.AggregateBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.RawWellDataBO;

/**
 * The Assay interface specifies a container that holds all relevant data
 * pertaining to one particular 'analysis unit'.
 * For AlloMap HTx, that analysis unit is a gene.
 * 
 * The Assay interface wraps the following data:
 * markers		- a list of genes (for AlloMap HTx exactly 1 gene)
 * rawData		- the raw individual CT scores as computed by the analyzer
 * aggregate	- the aggregated data for the assay
 * results		- the aggregated results for the assay (wsm CT, normalized wsm CT etc.)
 * 
 * @author smeier
 *
 */
public interface Assay {

	/**
	 * @return the markers
	 */
	public abstract Set<MarkerBO> getMarkers();

	/**
	 * @return the raw data
	 */
	public abstract Set<RawWellDataBO> getRawData();

	/**
	 * @return the aggregate
	 */
	public abstract AggregateBO getAggregate();

	/**
	 * @return the medianXdxCt
	 */
	public abstract AggMarkerResultBO getResult();

}