package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.LockMode;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.hibernate.HibernateUtil;

/**
 * Home object for domain model class PlateSetBO.
 * @see com.xdx.sax.bo.PlateSetBO
 * @author Hibernate Tools
 */
public class PlateSetDAO extends HibernateDao<Long, PlateSetBO>{

	private static final Log log = LogFactory.getLog(PlateSetDAO.class);

	@SuppressWarnings("unchecked")
	public List<PlateSetBO> findAll() {
		log.debug("finding all PlateSetBO instances");
		try {
			List<PlateSetBO> results = session.createCriteria(
					"com.xdx.sax.bo.PlateSetBO")
					.addOrder(Order.desc("id"))
					.list();
			log.debug(buildString("findAll successful, result size: ", results.size()));

			return results;
		} catch (RuntimeException re) {
			log.error("findAll failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<PlateSetBO> findNew() {
		log.debug("finding new PlateSetBO instances");
		try {
			List<PlateSetBO> results = session.createCriteria(
					"com.xdx.sax.bo.PlateSetBO")
					.add(Restrictions.isNull("processedTimestamp"))
					.addOrder(Order.desc("registeredTimestamp"))
					.addOrder(Order.desc("id"))
					.list();
			log.debug(buildString("findAll successful, result size: ", results.size()));

			return results;
		} catch (RuntimeException re) {
			log.error("findAll failed", re);
			throw re;
		}
	}

	public PlateSetBO findByExternalId(String externalId) {
		log.debug("getting PlateSetBO instance with external id: " + externalId);
		try {
			PlateSetBO instance = (PlateSetBO) session
			.createCriteria("com.xdx.sax.bo.PlateSetBO")
			.add(Restrictions.eq("externalId", externalId))
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

}
