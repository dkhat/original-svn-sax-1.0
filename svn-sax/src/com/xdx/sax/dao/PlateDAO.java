package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.PlateBO;

/**
 * Home object for domain model class PlateBO.
 * @see com.xdx.sax.bo.PlateBO
 * @author Hibernate Tools
 */
public class PlateDAO extends HibernateDao<Long, PlateBO> {

	public PlateBO findByBarcode(String plateBarcode) {
		log.debug("finding PlateBO instance by barcode " + plateBarcode);
		try {
			PlateBO instance = (PlateBO) session.createCriteria("com.xdx.sax.bo.PlateBO")
			.add(Restrictions.eq("platebarcode", plateBarcode))
			.uniqueResult();
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("find by barcode failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<PlateBO> findNew() {
		log.debug("Finding new plates, highest ID first");
		
		try {
			List<PlateBO> results = session.createCriteria("com.xdx.sax.bo.PlateBO")
				.add(Restrictions.isNull("processorError"))
				.addOrder(Order.desc("id"))
				.list();
			log.debug("find new plates successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.debug("find new plates failed", re);
		}
		return null;
	}
}
