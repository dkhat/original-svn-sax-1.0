/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.domain;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.ConfigBO;
import com.xdx.sax.bo.TestcodeBO;
import com.xdx.sax.dao.ConfigDAO;
import com.xdx.sax.dao.TestcodeDAO;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 *
 */
public final class ConfigurationModel extends BusinessObject {

	private static final Log log= LogFactory.getLog(ConfigurationModel.class);

	private static Hashtable<String, Hashtable<String, Hashtable<String, String>>> configData = new Hashtable<String, Hashtable<String, Hashtable<String, String>>>();
	
	/**
	 * Looks up a configuration value for a given category/name pair
	 * 
	 * @param category
	 * @param name
	 * @return configuration value
	 * @throws SaxException if configuration value not present
	 */
	public static String lookup (String testcode, 
				  					 String category, 
				  					 String name) throws SaxException {
		Hashtable <String, String> categoryData;

		// create new config for this test code if necessary
		if (! configData.containsKey(testcode)) {
			log.debug(buildString("Adding testcode ", testcode));
			configData.put(testcode, new Hashtable<String, Hashtable<String, String>>());
		}
		Hashtable<String, Hashtable<String, String>> testCodeData = 
			(Hashtable<String, Hashtable<String, String>>) configData.get(testcode);
		
		// load category if necessary
		if (! testCodeData.containsKey(category)) {
			log.debug(buildString("Loading category ", category));
			categoryData =  new Hashtable<String, String>();
			
			ConfigBO template = new ConfigBO();
			template.setConfigtype(category);
			template.setTestcode(getTestcode(testcode));
			
			for (ConfigBO entry : new ConfigDAO().findByExample(template))
				categoryData.put(entry.getName(), entry.getValue());

			testCodeData.put(category, categoryData);
		}

		categoryData = testCodeData.get(category);
		
		// get configuration value
		if (! categoryData.containsKey(name))
			throw new SaxException(buildString("Category ", category,
					" does not contain a configuration for ", name));

		return categoryData.get(name);
	}
	
	private static TestcodeBO getTestcode(String testcode) {
		TestcodeBO template = new TestcodeBO();
		template.setTestcodename(testcode);
		
		return (TestcodeBO)new TestcodeDAO().findByExample(template).toArray()[0];
	}
}
