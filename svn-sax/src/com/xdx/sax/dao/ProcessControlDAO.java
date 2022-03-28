/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.dao;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.ProcessControlBO;

/**
 * @author smeier
 *
 */
public class ProcessControlDAO extends HibernateDao<Long, ProcessControlBO> {

	@SuppressWarnings("unchecked")
	public List<ProcessControlBO> findAll() {
		log.debug("finding all ProcessControlBO instances");
		try {
			List<ProcessControlBO> results = session.createCriteria("com.xdx.sax.bo.ProcessControlBO")
					.addOrder(Order.desc("id"))
					.list();
			log.debug(buildString("findAll successful, result size: ", results.size()));

			return results;
		} catch (RuntimeException re) {
			log.error("findAll failed", re);
			throw re;
		}
	}

	public ProcessControlBO findByExternalId(String externalId) {
		log.debug("getting ProcessControlBO instance with external id: " + externalId);
		try {
			ProcessControlBO instance = (ProcessControlBO) session.createCriteria("com.xdx.sax.bo.ProcessControlBO")
			.add(Restrictions.eq("externalid", externalId))
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
