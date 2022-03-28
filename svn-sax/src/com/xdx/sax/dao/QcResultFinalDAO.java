package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.QcResultFinalBO;

/**
 * Home object for domain model class QcResultFinalBO.
 * @see com.xdx.sax.bo.QcResultFinalBO
 * @author Hibernate Tools
 */
public class QcResultFinalDAO extends HibernateDao<Long, QcResultFinalBO> {

	public QcResultFinalBO findByPlateSetSection(long plateSetId, long plateSectionId) {
		log.debug("getting QcResultFinalBO instance with plateset: " + plateSetId + " plate section " + plateSectionId);
		try {
			Criteria crit = session.createCriteria("com.xdx.sax.bo.QcResultFinalBO");
			crit.createCriteria("plateset").add(Restrictions.idEq(plateSetId));
			crit.createCriteria("platesection").add(Restrictions.idEq(plateSectionId));
			QcResultFinalBO instance = (QcResultFinalBO) crit.uniqueResult();
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
