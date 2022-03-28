package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.OldPlateMapBO;

/**
 * Home object for domain model class OldPlateMapBO.
 * @see com.xdx.sax.bo.OldPlateMapBO
 * @author Hibernate Tools
 */
public class OldPlateMapDAO extends HibernateDao<Long, OldPlateMapBO> {

	@SuppressWarnings("unchecked")
	public List<OldPlateMapBO> findByName(String name) {
		log.debug("finding OldPlateMapBO instances by name");
		try {
			List<OldPlateMapBO> results = session.createCriteria("com.xdx.sax.bo.OldPlateMapBO")
					.add(Restrictions.eq("name", name))
					.addOrder(Order.asc("section"))
					.addOrder(Order.asc("wellName"))
					.list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

}
