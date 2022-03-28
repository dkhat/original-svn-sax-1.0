package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.ProcessControlRawScoreResultBO;

/**
 * Home object for domain model class QcCriteriaStepResultBO.
 * @see com.xdx.sax.bo.QcCriteriaStepResultBO
 * @author Hibernate Tools
 */
public class ProcessControlRawScoreResultDAO extends HibernateDao<Long, ProcessControlRawScoreResultBO> {

	public ProcessControlRawScoreResultBO findBySetSection(
			long plateSetId, long plateSectionId) {
		log.debug("getting ProcessControlRawScoreResultBO instances with set "+ plateSetId+ " section "+ plateSectionId);
		try {
			Criteria crit = session.createCriteria("com.xdx.sax.bo.ProcessControlRawScoreResultBO");
			crit.createCriteria("plateSet").add(Restrictions.idEq(plateSetId));
			crit.createCriteria("plateSection").add(Restrictions.idEq(plateSectionId));
			ProcessControlRawScoreResultBO result = (ProcessControlRawScoreResultBO) crit.uniqueResult();
			
			if (result == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return result;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
