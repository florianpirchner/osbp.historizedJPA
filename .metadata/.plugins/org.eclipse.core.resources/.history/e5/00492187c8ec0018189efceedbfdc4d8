package eclipselink.example.jpa.employee.model;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class AddressUpdateQueryRedirector implements QueryRedirector {

	@Override
	public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {

		Date maxDate = getMaxDate();

		// we need to insert the changed address
		//
		UpdateObjectQuery updateObjectQuery = (UpdateObjectQuery) query;
		Address addr = (Address) updateObjectQuery.getObject();

		if(addr.getVersion() > 1) {
			throw new IllegalArgumentException("Version is greater then 1");
		}

		// update the validFrom to now
		addr.setValidFrom(new Date());
		addr.setValidUntil(maxDate);
		addr.setHistCurrent(true);

		InsertObjectQuery insertObjectQuery = new InsertObjectQuery(addr);

		// call the insertQuery and let do the AddressInsertQueryRedirector the
		// rest for us
		//
		Object result = insertObjectQuery.execute((AbstractSession) session, (AbstractRecord) arguments);
		return result;
	}

	private Date getMaxDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2099, Calendar.DECEMBER, 31, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date maxDate = cal.getTime();
		return maxDate;
	}

}
