/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - Employee Demo 2.4.2
 ******************************************************************************/
package org.osbp.jpa.historized.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.osbp.jpa.historized.tests.entities.Address;

/**
 * Persistence testing helper which creates an EMF providing testing overrides
 * to use direct JDBC instead of a data source
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class PersistenceTesting {

	public static EntityManagerFactory createEMF(boolean replaceTables) {
		Map<String, Object> props = new HashMap<String, Object>();

		// Ensure the persistence.xml provided data source are ignored for Java
		// SE testing
		props.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, "");
		props.put(PersistenceUnitProperties.JTA_DATASOURCE, "");
		props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		// props.put(PersistenceUnitProperties.SESSION_CUSTOMIZER,
		// "eclipselink.example.jpa.employee.model.HistorizedSequence");

		// Configure the use of embedded derby for the tests allowing system
		// properties of the same name to override
		// setProperty(props, PersistenceUnitProperties.JDBC_DRIVER,
		// "org.apache.derby.jdbc.EmbeddedDriver");
		// setProperty(props, PersistenceUnitProperties.JDBC_URL,
		// "jdbc:derby:target/derby/mysports;create=true");
		// setProperty(props, PersistenceUnitProperties.JDBC_USER, "app");
		// setProperty(props, PersistenceUnitProperties.JDBC_PASSWORD, "app");
		//
		setProperty(props, PersistenceUnitProperties.JDBC_DRIVER, "com.mysql.jdbc.Driver");
		setProperty(props, PersistenceUnitProperties.JDBC_URL,
				"jdbc:mysql://localhost/mysports?allowPublicKeyRetrieval=TRUE&useSSL=FALSE&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
		setProperty(props, PersistenceUnitProperties.JDBC_USER, "root");
		setProperty(props, PersistenceUnitProperties.JDBC_PASSWORD, "DpJf?T.iZ4j(");

		// Ensure weaving is used
		props.put(PersistenceUnitProperties.WEAVING, "false");

		if (replaceTables) {
			props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
			props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
		}

		return Persistence.createEntityManagerFactory("employee", props);
	}

	/**
	 * Add the system property value if it exists, otherwise use the default
	 * value.
	 */
	protected static void setProperty(Map<String, Object> props, String key, String defaultValue) {
		String value = defaultValue;
		if (System.getProperties().containsKey(key)) {
			value = System.getProperty(key);
		}
		props.put(key, value);
	}

	protected Object merge(EntityManager em, Object entity) {
		EntityTransaction txn = em.getTransaction();

		txn.begin();
		Object result = em.merge(entity);
		txn.commit();
		em.clear();
		((JpaEntityManager) em.getDelegate()).getServerSession().getIdentityMapAccessor()
				.invalidateClass(Address.class);
		return result;
	}

	protected void persist(EntityManager em, Object entity) {
		EntityTransaction txn = em.getTransaction();

		txn.begin();
		em.persist(entity);
		txn.commit();
		em.refresh(entity);
		em.clear();
		((JpaEntityManager) em.getDelegate()).getServerSession().getIdentityMapAccessor()
				.invalidateClass(Address.class);
	}

	protected void refresh(EntityManager em, Object entity) {
		EntityTransaction txn = em.getTransaction();

		txn.begin();
		em.refresh(entity);
		txn.commit();
		em.clear();
	}

	protected void clear(EntityManager em) {

	}
	

	protected Date getFromDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	protected int getYear(Date valFrom_v1) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(valFrom_v1);
		return cal.get(Calendar.YEAR);
	}


	protected int getMonth(Date valFrom_v1) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(valFrom_v1);
		return cal.get(Calendar.MONTH);
	}


	protected int getDay(Date valFrom_v1) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(valFrom_v1);
		return cal.get(Calendar.DAY_OF_MONTH);
	}


}