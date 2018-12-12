package org.osbp.jpa.historized.tests;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.osbp.jpa.historized.tests.entities.Address;

public class AddressService {

	protected static Address getHistCurrent(EntityManager em, Address temp) {
		TypedQuery<Address> query = em
				.createQuery("SELECT e FROM Address e where e.id.id = :adrId and e.histCurrent = true", Address.class);
		query.setParameter("adrId", temp.getId().id);
		List<Address> results = query.getResultList();

		Address addr = results.get(0);
		em.detach(addr);
		return addr;
	}

	protected static Address getCurrentValid(EntityManager em, String uuid) {
		return getValidByDate(em, uuid, new Date());
	}

	protected static Address getValidByDate(EntityManager em,  String uuid, Date date) {
		TypedQuery<Address> query = em.createQuery(
				"SELECT e FROM Address e where e.id = :id and e.validFrom >= :date and e.validUntil > :date", Address.class);
		query.setParameter("id", uuid);
		query.setParameter("date", date.getTime());
		List<Address> results = query.getResultList();

		if (results.isEmpty()) {
			return null;
		}
		Address addr = results.get(0);
		em.detach(addr);
		return addr;
	}

}
