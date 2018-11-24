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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.QueryRedirectors;

@Entity
@Cache(refreshOnlyIfNewer = true)
@IdClass(Address.ID.class)
@QueryRedirectors(insert = AddressInsertQueryRedirector.class, update = AddressInsertQueryRedirector.class, readAll = AddressTimedependetQueryRedirector.class)
public class Address {

	@Id
	@Column(name = "ADR_ID")
	private String id = java.util.UUID.randomUUID().toString();

	@Version
	@Column(name = "ADR_VERSION")
	private int version;

	@Id
	@Column(name = "ADR_VALIDFROM", updatable = false, length = 40)
	private long validFrom;

	@Column(name = "ADR_VALIDUNTIL", length = 40)
	private long validUntil;

	@Basic
	private boolean histCurrent;

	@Basic
	private boolean customVersion;

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
		this.validFrom = new Date().getTime();
	}

	// @PrePersist
	// @PreUpdate
	// void postPersist() {
	// validFrom = new Date();
	// }

	public long getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(long validFrom) {
		this.validFrom = validFrom;
	}

	public long getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(long validUntil) {
		this.validUntil = validUntil;
	}

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

	public boolean isHistCurrent() {
		return histCurrent;
	}

	public void setHistCurrent(boolean histCurrent) {
		this.histCurrent = histCurrent;
	}

	// @PreUpdate
	// @PrePersist
	// public void preUpdate() {
	// validFrom = new Date().getTime();
	// }

	public boolean isCustomVersion() {
		return customVersion;
	}

	public void setCustomVersion(boolean customVersion) {
		this.customVersion = customVersion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Address newVersion() {
		Address newVersion = new Address();
		newVersion.setId(getId());
		newVersion.validFrom = new Date().getTime();
		newVersion.histCurrent = true;
		newVersion.city = city;
		newVersion.country = country;
		newVersion.postalCode = postalCode;
		newVersion.province = province;
		newVersion.street = street;
		return newVersion;
	}

	public ID getHistKey() {
		return new ID(getId(), getValidFrom());
	}

	// public void newVersionInternal() {
	// this.validFrom = new Date();
	// }

	@Override
	public String toString() {
		return "Address [validFrom=" + validFrom + ", validUntil=" + validUntil + ", histCurrent=" + histCurrent
				+ ", getId()=" + getId() + ", getVersion()=" + getVersion() + "]";
	}

	public static class ID implements Serializable {
		private static final long serialVersionUID = 1L;

		public String id;
		public long validFrom;

		public ID() {
		}

		public ID(String empId, long validFrom) {
			this.id = empId;
			this.validFrom = validFrom;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + (int) (validFrom ^ (validFrom >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ID other = (ID) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (validFrom != other.validFrom)
				return false;
			return true;
		}

	}
}
