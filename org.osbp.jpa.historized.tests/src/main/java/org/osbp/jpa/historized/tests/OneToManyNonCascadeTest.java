package org.osbp.jpa.historized.tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Assert;
import org.junit.Test;
import org.osbp.jpa.historized.tests.entities.Address;
import org.osbp.jpa.historized.tests.entities.EmployeeOneToManyNonCascade;
import org.osbp.jpa.historized.tests.entities.UUIDHistId;

public class OneToManyNonCascadeTest extends PersistenceTesting {

	private EntityManager em;

	@Test
	public void test() {

		EntityManagerFactory emf = createEMF(true);
		em = emf.createEntityManager();

		clear(em);

		// create address 1 and get key
		//
		Address a_v1 = new Address("Heidelberg", "DE", "Somewhere", "69115", "Hebelstrasse");
		persist(em, a_v1);
		Address pers_Av1 = AddressService.getHistCurrent(em, a_v1);
		UUIDHistId key_Av1 = pers_Av1.getId();

		// create address 2 and get key
		//
		Address a_v2 = pers_Av1.newVersion();
		a_v2.setCountry("AT");
		persist(em, a_v2);
		Address pers_Av2 = AddressService.getHistCurrent(em, a_v2);
		UUIDHistId key_Av2 = pers_Av2.getId();

		// create address 3 and get key
		//
		Address a_v3 = pers_Av2.newVersion();
		a_v3.setCountry("PL");
		persist(em, a_v3);
		Address pers_Av3 = AddressService.getHistCurrent(em, a_v3);
		UUIDHistId key_Av3 = pers_Av3.getId();

		// read all 3 versions
		//
		pers_Av1 = getAddress(em, key_Av1);
		pers_Av2 = getAddress(em, key_Av2);
		pers_Av3 = getAddress(em, key_Av3);

		
		// add AT and DE to employee
		//
		EmployeeOneToManyNonCascade e1 = new EmployeeOneToManyNonCascade();
		e1.setFirstName("Foo");
		e1.setLastName("Bar");
		e1.getAddresses().add(pers_Av1);
		e1.getAddresses().add(pers_Av2);
		persist(em, e1);

		EmployeeOneToManyNonCascade pers_E1 = em.find(EmployeeOneToManyNonCascade.class, e1.getId());
		Assert.assertEquals(2, pers_E1.getAddresses().size());
		Assert.assertEquals("DE", pers_E1.getAddresses().get(0).getCountry());
		Assert.assertEquals("AT", pers_E1.getAddresses().get(1).getCountry());
		

		// replace DE by PL
		//
		pers_E1.getAddresses().remove(0);
		pers_E1.getAddresses().add(pers_Av3);
		merge(em, pers_E1);
		
		pers_E1 = em.find(EmployeeOneToManyNonCascade.class, e1.getId());
		Assert.assertEquals(2, pers_E1.getAddresses().size());
		Assert.assertEquals("AT", pers_E1.getAddresses().get(0).getCountry());
		Assert.assertEquals("PL", pers_E1.getAddresses().get(1).getCountry());
		
		// add DE again to the end of the list
		//
		pers_E1.getAddresses().add(pers_Av1);
		merge(em, pers_E1);
		Assert.assertEquals(3, pers_E1.getAddresses().size());
		Assert.assertEquals("AT", pers_E1.getAddresses().get(0).getCountry());
		Assert.assertEquals("PL", pers_E1.getAddresses().get(1).getCountry());
		Assert.assertEquals("DE", pers_E1.getAddresses().get(2).getCountry());
		
		
		
		
	}
}