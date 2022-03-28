package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.ProcessBO;
import com.xdx.sax.bo.ProcessStepBO;

/**
 * Home object for domain model class ProcessStepBO.
 * @see com.xdx.sax.bo.ProcessStepBO
 * @author Hibernate Tools
 */
public class ProcessStepDAO extends HibernateDao<Long, ProcessStepBO> {

	@SuppressWarnings("unchecked")
	public List<ProcessStepBO> findByProcess(ProcessBO process) {
		log.debug("finding ProcessStepBO instances by process");
		try {
			List<ProcessStepBO> results = session.createCriteria("com.xdx.sax.bo.ProcessStepBO")
					.add(Restrictions.eq("process", process))
					.addOrder(Order.asc("steporder"))
					.list();
			log.debug("find by process successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by process failed", re);
			throw re;
		}
	}

}
