package org.osbp.jpa.historized.tests.entities;

import java.io.Serializable;

public  class UUIDHistId implements Serializable {
	private static final long serialVersionUID = 1L;

	public String id;
	public long validFrom;

	public UUIDHistId() {
	}

	public UUIDHistId(String empId, long validFrom) {
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
		UUIDHistId other = (UUIDHistId) obj;
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
