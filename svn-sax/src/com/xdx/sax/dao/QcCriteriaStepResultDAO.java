package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.QcCriteriaStepResultBO;
import com.xdx.sax.exceptions.SaxException;

/**
 * Home object for domain model class QcCriteriaStepResultBO.
 * @see com.xdx.sax.bo.QcCriteriaStepResultBO
 * @author Hibernate Tools
 */
public class QcCriteriaStepResultDAO extends HibernateDao<Long, QcCriteriaStepResultBO>{

	private static final Log log = LogFactory
			.getLog(QcCriteriaStepResultDAO.class);

	@SuppressWarnings("unchecked")
	public QcCriteriaStepResultBO findByStepSetSection(long stepId, long plateSetId, long plateSectionId) {
		log.debug("getting QcCriteriaStepResultBO instance with id: "+ stepId+ " set "+ plateSetId+ " section "+ plateSectionId);
		try {
			Criteria crit = session.createCriteria("com.xdx.sax.bo.QcCriteriaStepResultBO");
			crit.createCriteria("plateset").add(Restrictions.idEq(plateSetId));
			crit.createCriteria("platesection").add(Restrictions.idEq(plateSectionId));
			crit.createCriteria("qccriteriastep").add(Restrictions.idEq(stepId));
			List<QcCriteriaStepResultBO> results = crit.list();
			QcCriteriaStepResultBO instance = null;
			
			if (results.size() == 0) {
				log.debug("get successful, no instance found");
			} else if (results.size() == 1) {
				log.debug("get successful, instance found");
				instance = (QcCriteriaStepResultBO) crit.list().toArray()[0];
			} else {
				log.error("more than one criteria step found");
				throw new SaxException("More than one criteria step for plate " + plateSetId + " section " + plateSectionId + " step " + stepId);
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<QcCriteriaStepResultBO> findBySetSection(
			long plateSetId, long plateSectionId) {
		log.debug("getting QcCriteriaStepResultBO instances with set "+ plateSetId+ " section "+ plateSectionId);
		try {
			Criteria crit = session.createCriteria("com.xdx.sax.bo.QcCriteriaStepResultBO");
			crit.createCriteria("plateset").add(Restrictions.idEq(plateSetId));
			crit.createCriteria("platesection").add(Restrictions.idEq(plateSectionId));
			crit.createCriteria("qccriteriastep").addOrder(Order.asc("ranking"));
			List<QcCriteriaStepResultBO> results = crit.list();
			
			if (results.size() == 0) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, " + results.size() + " instances found");
			}
			return results;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
