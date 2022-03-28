package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.QcCriteriaBO;

/**
 * Home object for domain model class QcCriteriaBO.
 * @see com.xdx.sax.bo.QcCriteriaBO
 * @author Hibernate Tools
 */
public class QcCriteriaDAO extends HibernateDao<Long, QcCriteriaBO> {

	@SuppressWarnings("unchecked")
	public List<QcCriteriaBO> findBySchemaSorted(long qcSchemaId) {
		log.debug("finding QcCriteriaBO instances (sorted by ranking) for schema " + qcSchemaId);
		try {
			List<QcCriteriaBO> results = session.createCriteria("com.xdx.sax.bo.QcCriteriaBO")
			.addOrder(Order.asc("ranking"))
			.createCriteria("qcschema")
			.add(Restrictions.idEq(qcSchemaId))
			.list();
			log.debug("find by schema sorted successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by schema sorted failed", re);
			throw re;
		}
	}
}
