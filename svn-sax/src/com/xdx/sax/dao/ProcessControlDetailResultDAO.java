package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.ProcessControlDetailResultBO;
import com.xdx.sax.exceptions.SaxException;

/**
 * Home object for domain model class QcCriteriaStepResultBO.
 * @see com.xdx.sax.bo.QcCriteriaStepResultBO
 * @author Hibernate Tools
 */
public class ProcessControlDetailResultDAO extends HibernateDao<Long, ProcessControlDetailResultBO> {

	@SuppressWarnings("unchecked")
	public ProcessControlDetailResultBO findByPcSetSection(long processControlDetailId, long plateSetId, long plateSectionId) {
		log.debug("getting ProcessControlDetailResultBO instance with id: "+ processControlDetailId+ " set "+ plateSetId+ " section "+ plateSectionId);
		try {
			Criteria crit = session.createCriteria("com.xdx.sax.bo.ProcessControlDetailResultBO");
			crit.createCriteria("plateSet").add(Restrictions.idEq(plateSetId));
			crit.createCriteria("plateSection").add(Restrictions.idEq(plateSectionId));
			crit.createCriteria("processControlDetail").add(Restrictions.idEq(processControlDetailId));
			List<ProcessControlDetailResultBO> results = crit.list();
			ProcessControlDetailResultBO instance = null;
			
			if (results.size() == 0) {
				log.debug("get successful, no instance found");
			} else if (results.size() == 1) {
				log.debug("get successful, instance found");
				instance = (ProcessControlDetailResultBO) crit.list().toArray()[0];
			} else {
				log.error("more than one criteria step found");
				throw new SaxException("More than one result for plate " + plateSetId + " section " + plateSectionId + " step " + processControlDetailId);
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProcessControlDetailResultBO> findBySetSection(
			long plateSetId, long plateSectionId) {
		log.debug("getting ProcessControlDetailResultBO instances with set "+ plateSetId+ " section "+ plateSectionId);
		try {
			Criteria crit = session.createCriteria("com.xdx.sax.bo.ProcessControlDetailResultBO");
			crit.createCriteria("plateSet").add(Restrictions.idEq(plateSetId));
			crit.createCriteria("plateSection").add(Restrictions.idEq(plateSectionId));
			List<ProcessControlDetailResultBO> results = crit.list();
			
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
