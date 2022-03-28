package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.PlateDesignBO;

/**
 * Home object for domain model class PlateDesignBO.
 * @see com.xdx.sax.bo.PlateDesignBO
 * @author Hibernate Tools
 */
public class PlateDesignDAO extends HibernateDao<Long, PlateDesignBO> {

	public PlateDesignBO findByName(String plateDesignName) {
		log.debug("getting PlateDesignBO instance with id: " + plateDesignName);
		try {
			PlateDesignBO instance = (PlateDesignBO) session.createCriteria("com.xdx.sax.bo.PlateDesignBO")
					.add(Restrictions.eq("name", plateDesignName))
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
	public List<PlateDesignBO> findAll() {
		log.debug("finding all PlateDesignBO instances");
		try {
			List<PlateDesignBO> results = session.createCriteria("com.xdx.sax.bo.PlateDesignBO").list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<PlateDesignBO> findByTestcode(String testcode) {
		log.debug("getting PlateDesignBO instances with testcode: " + testcode);
		try {
			List<PlateDesignBO> results = session.createCriteria("com.xdx.sax.bo.PlateDesignBO")
					.createCriteria("testcodes")
					.add(Restrictions.eq("testcodename", testcode))
					.list();
			return results;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
