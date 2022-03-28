package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.PlateDesignBO;
import com.xdx.sax.bo.TestcodeBO;
import com.xdx.sax.exceptions.SaxException;

/**
 * Home object for domain model class TestcodeBO.
 * @see com.xdx.sax.bo.TestcodeBO
 * @author Hibernate Tools
 */
public class TestcodeDAO extends HibernateDao<Long, TestcodeBO> {

	public TestcodeBO findByName(String name) {
		log.debug("getting TestcodeBO instance with name: " + name);
		try {
			TestcodeBO instance = (TestcodeBO) session.createCriteria("com.xdx.sax.bo.TestcodeBO")
			.add(Restrictions.eq("testcodename", name))
			.uniqueResult();
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<TestcodeBO> findAll() {
		log.debug("getting all TestcodBO instances");
		
		try {
			return (List<TestcodeBO>) session.createCriteria("com.xdx.sax.bo.TestcodeBO").list();
		} catch (RuntimeException e) {
			log.error("Exception when getting TestcodeBO objects");
			throw new SaxException (e);
		}
	}

}
