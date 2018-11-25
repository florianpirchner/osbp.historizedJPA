package org.osbp.jpa.historized.tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Assert;
import org.junit.Test;
import org.osbp.jpa.historized.tests.entities.Address;
import org.osbp.jpa.historized.tests.entities.EmployeeOneToManyNonCascade;


public class OneToManyCascadeTest extends PersistenceTesting {

	private EntityManager em;

	@Test
	public void test() {

		EntityManagerFactory emf = createEMF(true);
		em = emf.createEntityManager();

		clear(em);

		Address a_v1 = new Address("Heidelberg", "DE", "Somewhere", "69115", "Hebelstrasse");
		persist(em, a_v1);

		Address pers_Av1 = AddressService.getHistCurrent(em, a_v1);
		Address.ID key_Av1 = pers_Av1.getHistKey();

		Address a_v2 = pers_Av1.newVersion();
		a_v2.setCountry("AT");
		persist(em, a_v2);

		Address pers_Av2 = AddressService.getHistCurrent(em, a_v2);
		Address.ID key_Av2 = pers_Av2.getHistKey();

		pers_Av1 = getAddress(em, key_Av1);

		EmployeeOneToManyNonCascade e1 = new EmployeeOneToManyNonCascade();
		e1.setFirstName("Foo");
		e1.setLastName("Bar");
		e1.getAddresses().add(pers_Av1);
		e1.getAddresses().add(pers_Av2);
		persist(em, e1);
		
		EmployeeOneToManyNonCascade pers_E1 = em.find(EmployeeOneToManyNonCascade.class, e1.getId());
		Assert.assertEquals(2, pers_E1.getAddresses().size());
		
		
	}
}