/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke - initial JPA Employee example using XML (bug 217884)
 *      mbraeuer - annotated version
 ******************************************************************************/
package org.osbp.jpa.historized.tests.entities;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;

import org.eclipse.persistence.annotations.Cache;

@Entity
@Cache(refreshOnlyIfNewer = true)
//@AttributeOverrides({ @AttributeOverride(name = "id.id", column = @Column(name = "ADDR_ID")),
//		@AttributeOverride(name = "id.validFrom", column = @Column(name = "ADDR_VALIDFROM")) })
public class Address extends BaseUUIDHistorized {

	@Basic
	private String city;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String country;

	@Basic
	private String province;

	@Basic
	@Column(name = "P_CODE")
	private String postalCode;

	@Basic
	private String street;

	public Address() {
	}

	public Address(String city, String country, String province, String postalCode, String street) {
		super();
		this.city = city;
		this.country = country;
		this.province = province;
		this.postalCode = postalCode;
		this.street = street;

	}

	// @PrePersist
	// @PreUpdate
	// void postPersist() {
	// validFrom = new Date();
	// }

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String pCode) {
		this.postalCode = pCode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Address newVersion() {
		Address newVersion = new Address();
		newVersion.setId(getId().copy());
		newVersion.setValidFrom(new Date().getTime());
		newVersion.setHistCurrent(true);
		newVersion.city = city;
		newVersion.country = country;
		newVersion.postalCode = postalCode;
		newVersion.province = province;
		newVersion.street = street;
		return newVersion;
	}

	@Override
	public String toString() {
		return "Address [getId()=" + getId() + ", getValidUntil()=" + getValidUntil() + ", getVersion()=" + getVersion()
				+ ", isCustomVersion()=" + isCustomVersion() + ", isHistCurrent()=" + isHistCurrent() + ", city=" + city
				+ ", country=" + country + ", postalCode=" + postalCode + ", province=" + province + ", street="
				+ street + "]";
	}

	// @PreUpdate
	// @PrePersist
	// public void preUpdate() {
	// validFrom = new Date().getTime();
	// }

	// public void newVersionInternal() {
	// this.validFrom = new Date();
	// }

	

}
