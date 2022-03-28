package com.xdx.sax.schedulable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.varia.scheduler.Schedulable;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.dao.PlateDAO;
import com.xdx.sax.domain.ConfigurationModel;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.service.SaxApiServiceBean;

@Stateless(name="SdsFileScanner")
public class SdsFileScannerImpl implements Schedulable, SdsFileScanner {

	private static final Log log = LogFactory.getLog(SdsFileScannerImpl.class);

	public void perform(Date arg0, long arg1) {
		SdsFileScanner scan;
		try {
			scan = (SdsFileScanner) new InitialContext().lookup(JNDI_NAME);
		} catch(NamingException e) {
			throw new SaxException(e);
		}
//		scan.scanDirectory();
		scan.processNextSdsFile();
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.schedulable.SdsFileScanner#scanDirectory()
	 */
	public void scanDirectory() {
		File dir;
		String sep = ConfigurationModel.lookup("HTx", "SYSTEM", "DIR_SEPARATOR");
		String result;
		
		try {
			// get URL from configuration
			URL dirUrl = new URL(ConfigurationModel.lookup("HTx", "SYSTEM", "SDSURL"));
			
			log.debug(buildString("Checking directory ", dirUrl));
			
			// transform into file object and check if it's a directory
			dir = new File(dirUrl.getPath());
			if (! dir.isDirectory())
				throw new MalformedURLException(buildString(dirUrl.getPath(), "is not a valid path"));
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
			throw new SaxException(e);
		}
		
		// Need to use local interface to the session bean at this point
		// to ensure that we are getting a new transaction context for each call
		// to the processFile() method.
		SaxApiServiceBean bean;
		
		try {
			bean = (SaxApiServiceBean) new InitialContext().lookup(SaxApiServiceBean.JNDI_NAME);
		} catch (NamingException e) {
			throw new SaxException(e);
		}
			
		// list directory (filter from configuration)
		String[] files = dir.list();
		for (String file : files) {
			if (file.matches(".*\\.sds")) {
				log.debug("=============================");
				log.debug(buildString("Analyzing file ", buildString(dir.getAbsolutePath(), sep, file)));
				result = bean.processFile(buildString(dir.getAbsolutePath(), sep, file));					
				log.debug("============== DONE processing SDS file ===============");
				
				// Check if we had success
				if (result != null)
					break;
			}
		}

		bean.processNextPlate();
		log.debug("============== DONE processing plate ===============");
	}

	/* (non-Javadoc)
	 * @see com.xdx.sax.schedulable.SdsFileScanner#scanDirectory()
	 */
	public void processNextSdsFile() {
		String sep = ConfigurationModel.lookup("HTx", "SYSTEM", "DIR_SEPARATOR");
		String dir;
		String fileName;

		try {
			dir = new URL(ConfigurationModel.lookup("HTx", "SYSTEM", "SDSURL")).getPath();
		} catch (MalformedURLException e) {
			throw new SaxException(e);
		}

		// Need to use local interface to the session been at this point
		// to ensure that we are getting a new transaction context for each call
		// to the processFile() method.
		SaxApiServiceBean bean;
		
		try {
			bean = (SaxApiServiceBean) new InitialContext().lookup(SaxApiServiceBean.JNDI_NAME);
		} catch (NamingException e) {
			throw new SaxException(e);
		}
			
		List<PlateBO> newPlates = new PlateDAO().findNew();

		if (newPlates != null) {
			for (PlateBO plate: newPlates) {
				fileName = buildString(dir, sep, plate.getPlatebarcode(), ".sds");

				if (! new File(fileName).exists())
					continue;
				
				log.debug("=============================");
				log.info(buildString("Analyzing file ", fileName));
				bean.processFile(fileName);					
				log.debug("============== DONE processing SDS file ===============");
					
				break;
			}
		}
		
		bean.processNextPlate();
		log.debug("============== DONE processing plate ===============");
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
