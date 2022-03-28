/**
 * 
 */
package com.xdx.sax.workflow;

import java.util.List;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.ProcessBO;
import com.xdx.sax.bo.ProcessStepBO;
import com.xdx.sax.bo.TestcodeBO;
import com.xdx.sax.dao.ProcessDAO;
import com.xdx.sax.dao.ProcessStepDAO;
import com.xdx.sax.dao.TestcodeDAO;
import com.xdx.sax.domain.SaxDataSet;

/**
 * @author smeier
 *
 */
public abstract class AbstractOrchestrator extends BusinessObject  implements Orchestrator {

	// test code property
	private TestcodeBO testcode=null;
	private List<ProcessStepBO> steps = null;
	
	public TestcodeBO getTestcode() { return this.testcode; }
	 
	protected void setTestcode(TestcodeBO testcode){
		this.testcode = testcode;
	}

	public List<ProcessStepBO> getSteps() {
		return this.steps;
	}
	
	protected void setSteps(List<ProcessStepBO> steps) {
		this.steps = steps;
	}
	
	protected void initialize (String testcodeName) {
		// set testcode
		setTestcode(new TestcodeDAO().findByName(testcodeName));
		ProcessBO proc = getTestcode().getProcess();
		
		// set steps
		this.steps = new ProcessStepDAO().findByProcess(proc);
	}
	
	public abstract void execute(SaxDataSet dataSet);
}
