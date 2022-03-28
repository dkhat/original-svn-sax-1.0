/**
 * 
 */
package com.xdx.sax.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.PlateSetBO;
import com.xdx.sax.hibernate.HibernateUtil;

/**
 * @author smeier
 *
 */
public abstract class HibernateDao<K, E> implements Dao<K, E> {
	protected Class<E> entityClass;
	protected Session session;
	protected Log log;
	
	@SuppressWarnings("unchecked")
	public HibernateDao() {
		this.session = HibernateUtil.getCurrentSaxSession();
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
		this.log = LogFactory.getLog(this.entityClass);
	}
 
	public void persist(E entity) {
		session.persist(entity);
	}
 
	public void delete(E entity) {
		session.delete(entity);
	}
 
	@SuppressWarnings("unchecked")
	public E findById(K id) {
		return (E) session.createCriteria(entityClass.getName())
							.add(Restrictions.idEq(id))
							.uniqueResult();
	}

	public void attachDirty(E entity) {
		session.saveOrUpdate(entity);
	}

	public void attachClean(E entity) {
		session.lock(entity, LockMode.NONE);
	}

	@SuppressWarnings("unchecked")
	public E merge(E detachedInstance) {
		E result = (E) session.merge(detachedInstance);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<E> findByExample(E instance) {
		List<E> results = (List<E>) session.createCriteria(instance.getClass()).add(Example.create(instance)).list();
		return results;
	}

	/**
	 * Utility method builds Strings; avoids string concatenation.
	 *
	 * @param values
	 * @return the concatenated string
	 */
	protected final static String buildString(Object... values) {

		StringBuilder sb= new StringBuilder();

		for (Object object : values) {
			sb.append((object == null)  ?  "" :  object.toString());
		}

		return sb.toString();
	}

}
