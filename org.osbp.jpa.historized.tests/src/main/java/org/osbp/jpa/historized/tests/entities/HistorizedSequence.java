package org.osbp.jpa.historized.tests.entities;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sessions.Session;

public class HistorizedSequence extends DefaultSequence implements SessionCustomizer {

	public HistorizedSequence() {
		super();
	}

	public HistorizedSequence(String name, int size, int initialValue) {
		super(name, size, initialValue);
	}

	public HistorizedSequence(String name, int size) {
		super(name, size);
	}

	public HistorizedSequence(String name) {
		super(name);
	}

	@Override
	public boolean shouldAlwaysOverrideExistingValue() {
		return false;
	}

	public void customize(Session session) throws Exception {
		HistorizedSequence sequence = new HistorizedSequence("hist-id");

		session.getLogin().addSequence(sequence);
	}

}
