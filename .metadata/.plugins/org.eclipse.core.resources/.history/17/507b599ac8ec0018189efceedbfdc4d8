package eclipselink.example.jpa.employee.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.persistence.internal.expressions.SQLInsertStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.StatementQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.EmptyRecord;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.JPQLCall;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.changesets.ChangeRecord;

public class AddressInsertQueryRedirector2 implements QueryRedirector {

	@Override
	public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {
		InsertObjectQuery insertObjectQuery = (InsertObjectQuery) query;
		List<ChangeRecord> changes = insertObjectQuery.getObjectChangeSet().getChanges();
		Address addr = (Address) insertObjectQuery.getObject();
		if(changes.isEmpty() && !insertObjectQuery.getObjectChangeSet().isNew()) {
			return addr;
		}

		if (addr.getVersion() > 1) {
			throw new IllegalArgumentException("Version is greater then 1");
		}

		Date maxDate = getMaxDate();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		Date now = new Date();

		Call call = new JPQLCall(
				"select a from Address a where a.id = :id and a.validUntil = :maxDate and a.histCurrent = :histCurrent");
		ReadObjectQuery readQuery = new ReadObjectQuery(Address.class, call);
		readQuery.dontCheckCache();
		readQuery.addArgument("id", String.class);
		readQuery.addArgument("maxDate", Date.class);
		readQuery.addArgument("histCurrent", Boolean.class);
		readQuery.addArgumentValue(addr.getId());
		readQuery.addArgumentValue(maxDate);
		readQuery.addArgumentValue(true);
		Address oldAddress = (Address) readQuery.execute((AbstractSession) session, EmptyRecord.getEmptyRecord());
		if (oldAddress == addr) {
			// update the address
			addr.setValidUntil(now);
			addr.setHistCurrent(false);
			UpdateObjectQuery updateObjectQuery = new UpdateObjectQuery(addr);
			updateObjectQuery.setDoNotRedirect(true);
			updateObjectQuery.execute((AbstractSession) session, EmptyRecord.getEmptyRecord());

			//
			InsertObjectQuery _insertObjectQuery = (InsertObjectQuery) insertObjectQuery.clone();
			_insertObjectQuery.setSession((AbstractSession) session);
			StatementQueryMechanism insertMechanism = new StatementQueryMechanism(_insertObjectQuery);
			//
			SQLInsertStatement insertStatement = new SQLInsertStatement();
			insertStatement.setTable(query.getDescriptor().getDefaultTable());
			insertMechanism.getSQLStatements().add(insertStatement);
			// InsertObjectQuery _insertQ = new InsertObjectQuery(addr);

			AbstractRecord insertRow = query.getDescriptor().getObjectBuilder().buildRow(addr,
					(AbstractSession) session, WriteType.INSERT);

			DatabaseField validFromDBField = query.getDescriptor().getMappingForAttributeName("validFrom").getField();
			DatabaseField validUntilDBField = query.getDescriptor().getMappingForAttributeName("validUntil").getField();
			DatabaseField versionDBField = query.getDescriptor().getMappingForAttributeName("version").getField();
			DatabaseField histCurrentDBField = query.getDescriptor().getMappingForAttributeName("histCurrent")
					.getField();
			insertRow.put(validFromDBField, now);
			insertRow.put(validUntilDBField, maxDate);
			insertRow.put(histCurrentDBField, true);
			insertRow.put(versionDBField, 0);

			_insertObjectQuery.setTranslationRow(insertRow);
			_insertObjectQuery.setModifyRow(insertRow);
			_insertObjectQuery.setDoNotRedirect(true);
			insertMechanism.insertObject();
			insertMechanism.getQuery().getProperty("output");

			call = new JPQLCall(
					"select a from Address a where a.id = :id and a.validUntil = :maxDate and a.histCurrent = :histCurrent");
			readQuery = new ReadObjectQuery(Address.class, call);
			readQuery.dontCheckCache();
			readQuery.addArgument("id", String.class);
			readQuery.addArgument("maxDate", Date.class);
			readQuery.addArgument("histCurrent", Boolean.class);
			readQuery.addArgumentValue(addr.getId());
			readQuery.addArgumentValue(maxDate);
			readQuery.addArgumentValue(true);

			Address result = (Address) readQuery.execute((AbstractSession) session, EmptyRecord.getEmptyRecord());
			
			return result;
			// return _insertQ.execute((AbstractSession) session, insertRow);

		} else if (oldAddress != null) {
			System.out.println("");
		}

		addr.setValidUntil(maxDate);
		addr.setHistCurrent(true);

		insertObjectQuery.setDoNotRedirect(true);
		return insertObjectQuery.execute((AbstractSession) session, (AbstractRecord) arguments);
	}

	private Date getMaxDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2099, Calendar.DECEMBER, 31, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date maxDate = cal.getTime();
		return maxDate;
	}

}
