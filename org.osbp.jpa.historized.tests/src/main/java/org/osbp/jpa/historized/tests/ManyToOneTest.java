package org.osbp.jpa.historized.tests;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Assert;
import org.junit.Test;
import org.osbp.jpa.historized.tests.entities.Address;
import org.osbp.jpa.historized.tests.entities.EmployeeManyToOne;
import org.osbp.jpa.historized.tests.entities.UUIDHistId;

public class ManyToOneTest extends PersistenceTesting {

	private EntityManager em;

	@Test
	public void test() {

		EntityManagerFactory emf = createEMF(true);
		em = emf.createEntityManager();

		clear(em);

		Address a_v1 = new Address("Heidelberg", "DE", "Somewhere", "69115", "Hebelstrasse");
		persist(em, a_v1);

		Address pers_Av1 = AddressService.getHistCurrent(em, a_v1);

		EmployeeManyToOne e1 = new EmployeeManyToOne();
		e1.setFirstName("Foo");
		e1.setLastName("Bar");
		e1.setAddressRef(pers_Av1);
		persist(em, e1);

		EmployeeManyToOne e_pers1 = em.find(EmployeeManyToOne.class, e1.getId());
		Assert.assertEquals("DE", e_pers1.getAddressRef().getCountry());

		Address a_v2 = pers_Av1.newVersion();
		a_v2.setCountry("AT");
		merge(em, a_v2);

		e_pers1.setAddressRef(a_v2);
		e_pers1 = (EmployeeManyToOne) merge(em, e_pers1);
		EmployeeManyToOne e_pers2 = em.find(EmployeeManyToOne.class, e1.getId());
		Assert.assertEquals("AT", e_pers2.getAddressRef().getCountry());

		e_pers1.setAddressRef(a_v1);
		e_pers1 = (EmployeeManyToOne) merge(em, e_pers1);
		e_pers1 = (EmployeeManyToOne) merge(em, e_pers1);
		e_pers1 = (EmployeeManyToOne) merge(em, e_pers1);
	}

	@Test
	public void testCustomVersion() {

		EntityManagerFactory emf = createEMF(true);
		em = emf.createEntityManager();

		clear(em);

		Address a_v1 = new Address("Heidelberg", "DE", "Somewhere", "69115", "Hebelstrasse");
		a_v1.setCustomVersion(true);
		a_v1.setValidFrom(new Date().getTime());
		UUIDHistId keyA_v1 = a_v1.getHistKey();

		persist(em, a_v1);

		Address pers_Av1 = em.find(Address.class, keyA_v1);
		em.detach(pers_Av1);

		EmployeeManyToOne e1 = new EmployeeManyToOne();
		e1.setFirstName("Foo");
		e1.setLastName("Bar");
		e1.setAddressRef(pers_Av1);
		persist(em, e1);

		EmployeeManyToOne e_pers1 = em.find(EmployeeManyToOne.class, e1.getId());
		Assert.assertEquals("DE", e_pers1.getAddressRef().getCountry());
		Assert.assertEquals("DE", pers_Av1.getCountry());

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}

		Address a_v2 = pers_Av1;
		a_v2.setCustomVersion(true);
		a_v2.setValidFrom(new Date().getTime());
		a_v2.setCountry("AT");
		a_v2.setVersion(0);
		UUIDHistId keyA_v2 = a_v2.getHistKey();
		merge(em, a_v2);

		e_pers1.setAddressRef(a_v2);
		e_pers1 = (EmployeeManyToOne) merge(em, e_pers1);
		EmployeeManyToOne e_pers2 = em.find(EmployeeManyToOne.class, e1.getId());
		Address pers_Av2 = em.find(Address.class, keyA_v2);
		pers_Av1 = em.find(Address.class, keyA_v1);
		Assert.assertEquals("AT", e_pers2.getAddressRef().getCountry());
		Assert.assertEquals("AT", pers_Av2.getCountry());

	}

}
