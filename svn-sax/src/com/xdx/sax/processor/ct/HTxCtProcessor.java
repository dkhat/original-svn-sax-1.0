/**
 * 
 */
package com.xdx.sax.processor.ct;

import com.xdx.sax.bo.RawWellDataBO;

/**
 * @author smeier
 *
 */
public class HTxCtProcessor extends AbstractCtProcessor {

	public void calculateCT(RawWellDataBO well) {
		HTxSingleCtProcessor proc = new HTxSingleCtProcessor (well);
		proc.calculateCT();
	}

}
