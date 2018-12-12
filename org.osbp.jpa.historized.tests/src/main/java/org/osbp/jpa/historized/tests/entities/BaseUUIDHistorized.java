package org.osbp.jpa.historized.tests.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreRemove;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.QueryRedirectors;

@MappedSuperclass
@SuppressWarnings("all")
@QueryRedirectors(insert = AddressInsertQueryRedirector.class, update = AddressInsertQueryRedirector.class)
public abstract class BaseUUIDHistorized {
	@Transient
	private boolean disposed;

	@EmbeddedId
	private UUIDHistId id = new UUIDHistId(UUID.randomUUID().toString(), new Date().getTime());

	@Version
	@Column(name = "VERSION")
	private int version;

	@Column(name = "VALIDUNTIL", length = 40)
	@HistValidUntil
	private long validUntil;

	@Basic
	@HistIsCurrent
	private boolean histCurrent;

	@Basic
	@HistCustomVersion
	private boolean customVersion;

	public BaseUUIDHistorized() {
	}

	/**
	 * @return true, if the object is disposed. Disposed means, that it is
	 *         prepared for garbage collection and may not be used anymore.
	 *         Accessing objects that are already disposed will cause runtime
	 *         exceptions.
	 * 
	 */
	public boolean isDisposed() {
		return this.disposed;
	}

	/**
	 * Checks whether the object is disposed.
	 * 
	 * @throws RuntimeException
	 *             if the object is disposed.
	 */
	private void checkDisposed() {
		if (isDisposed()) {
			throw new RuntimeException("Object already disposed: " + this);
		}
	}

	/**
	 * Calling dispose will destroy that instance. The internal state will be
	 * set to 'disposed' and methods of that object must not be used anymore.
	 * Each call will result in runtime exceptions.<br>
	 * If this object keeps composition containments, these will be disposed
	 * too. So the whole composition containment tree will be disposed on
	 * calling this method.
	 */
	public void dispose() {
		if (isDisposed()) {
			return;
		}
		disposed = true;
	}

	/**
	 * @return Returns the id property or <code>null</code> if not present.
	 */
	public UUIDHistId getId() {
		checkDisposed();
		return this.id;
	}

	/**
	 * Sets the id property to this instance.
	 */
	public void setId(final UUIDHistId id) {
		checkDisposed();
		this.id = id;
	}

	public void setValidFrom(long time) {
		if (id == null) {
			id = new UUIDHistId(UUID.randomUUID().toString(), time);
		} else {
			id.validFrom = time;
		}
	}

	/**
	 * @return Returns the version property or <code>null</code> if not present.
	 */
	public int getVersion() {
		checkDisposed();
		return this.version;
	}

	/**
	 * Sets the version property to this instance.
	 */
	public void setVersion(final int version) {
		checkDisposed();
		this.version = version;
	}

	public long getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(long validUntil) {
		this.validUntil = validUntil;
	}

	public boolean isHistCurrent() {
		return histCurrent;
	}

	public void setHistCurrent(boolean histCurrent) {
		this.histCurrent = histCurrent;
	}

	public boolean isCustomVersion() {
		return customVersion;
	}

	public void setCustomVersion(boolean customVersion) {
		this.customVersion = customVersion;
	}

	public boolean equalVersions(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseUUIDHistorized other = (BaseUUIDHistorized) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (other.version != this.version)
			return false;
		return true;
	}

	public abstract <T> T newVersion();

	@Override
	public boolean equals(final Object obj) {
		return equalVersions(obj);
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * Iterates all cross references and removes them from the parent to avoid
	 * ConstraintViolationException
	 */
	@PreRemove
	protected void preRemove() {

	}

}
