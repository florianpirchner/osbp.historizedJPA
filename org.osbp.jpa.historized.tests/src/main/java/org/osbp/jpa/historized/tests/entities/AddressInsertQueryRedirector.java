package org.osbp.jpa.historized.tests.entities;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.EmptyRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.queries.WriteObjectQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class AddressInsertQueryRedirector implements QueryRedirector {

	@SuppressWarnings("unchecked")
	@Override
	public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {
		WriteObjectQuery insertObjectQuery = (WriteObjectQuery) query;

//		List<DatabaseField> pk = query.getDescriptor().getPrimaryKeyFields();
//		DatabaseMapping mapping = query.getDescriptor().getMappingForAttributeName("id");
//		mapping.getField();
//		// DatabaseField validFromDBField =
//		// query.getDescriptor().getMappingForAttributeName("validFrom").getField();
//		DatabaseField validUntilDBField = query.getDescriptor().getMappingForAttributeName("validUntil").getField();
//		DatabaseField versionDBField = query.getDescriptor().getMappingForAttributeName("version").getField();
//		DatabaseField histCurrentDBField = query.getDescriptor().getMappingForAttributeName("histCurrent").getField();

		Address addr = (Address) insertObjectQuery.getObject();

		if (addr.getVersion() > 1) {
			throw new IllegalArgumentException(
					"Version must be 0. Changing historized records is not allowed: " + addr.toString());
		}

		Address current = getCurrent(addr, (AbstractSession) session);

		if (current == null) {
			addr.setValidFrom(createNow(addr));
			addr.setValidUntil(getMaxDate());
			addr.setHistCurrent(true);

			insertObjectQuery.setDoNotRedirect(true);
			Object newAdr = insertObjectQuery.execute((AbstractSession) session, (AbstractRecord) arguments);
			return newAdr;
		} else {

			// sleep 1ms for min time intervall
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}

			// update the address to be historized
			long now = createNow(addr);

			// DataModifyQuery updateQuery = (DataModifyQuery) new
			// DataModifyQuery();
			// SQLUpdateStatement statement = new SQLUpdateStatement();
			// statement.setTable(query.getDescriptor().getDefaultTable());
			//
			// ExpressionBuilder eb = new ExpressionBuilder(Address.class);
			// eb.setQueryClassAndDescriptor(Address.class,
			// query.getDescriptor());
			// eb.setSession((AbstractSession) session);
			// Expression whereClause =
			// eb.get("id").equal(addr.getId()).and(eb.get("histCurrent").equal(true));
			// statement.setWhereClause(whereClause);
			//
			// AbstractRecord modifyRow = new DatabaseRecord();
			// modifyRow.put(validUntilDBField, now);
			// modifyRow.put(histCurrentDBField, false);
			// statement.setModifyRow(modifyRow);
			// updateQuery.setModifyRow(modifyRow);
			// updateQuery.setHasModifyRow(true);
			// updateQuery.setSQLStatement(statement);
			// updateQuery.setDoNotRedirect(true);
			//// updateQuery.dontMaintainCache();

			// ReadObjectQuery
			// Object updated = ((AbstractSession)
			// session).executeQuery(updateQuery, modifyRow);
			//

			Address currentManaged = getCurrentManaged(current.getId(), (AbstractSession) session);
			if (query.getDescriptor().getObjectBuilder().compareObjects(addr, currentManaged,
					(AbstractSession) session)) {
				return addr;
			}

			currentManaged.setHistCurrent(false);
			currentManaged.setValidUntil(now);
			UpdateObjectQuery updateCurrent = new UpdateObjectQuery(currentManaged);
			updateCurrent.setDoNotRedirect(true);
			updateCurrent.setIsUserDefined(true);
			updateCurrent.execute((AbstractSession) session, (AbstractRecord) arguments);
			session.getIdentityMapAccessor().invalidateObject(currentManaged.getId(), Address.class);

			addr.setValidFrom(now);
			addr.setValidUntil(getMaxDate());
			addr.setHistCurrent(true);
			addr.setVersion(0);

			InsertObjectQuery insertObjectQ = new InsertObjectQuery(addr);
			insertObjectQ.setIsUserDefined(true);
			insertObjectQ.setDoNotRedirect(true);

			Object newAdr = insertObjectQ.execute((AbstractSession) session, (AbstractRecord) arguments);
			return newAdr;
		}

	}

	private long createNow(Address addr) {
		if (addr.isCustomVersion()) {
			return addr.getId().validFrom;
		}
		return new Date().getTime();
	}

	private Address getCurrent(Address addr, AbstractSession session) {

		UnitOfWorkImpl uow = session.acquireNonSynchronizedUnitOfWork();

		ReadObjectQuery rq = new ReadObjectQuery(Address.class);

		ExpressionBuilder eb = rq.getExpressionBuilder();
		Expression exp = eb.get("id").get("id").equal(addr.getId().id).and(eb.get("histCurrent").equal(true));
		exp.toString();
		rq.setSelectionCriteria(exp);
		rq.dontCheckCache();
		rq.dontMaintainCache();

		System.out.println(exp);
		Address current = (Address) rq.executeInUnitOfWork(uow, EmptyRecord.getEmptyRecord());

		uow.clearForClose(true);
		return current;
	}

	private Address getCurrentManaged(UUIDHistId addrId, AbstractSession session) {

		ReadObjectQuery rq = new ReadObjectQuery(Address.class);

		ExpressionBuilder eb = rq.getExpressionBuilder();
		Expression exp = eb.get("id").equal(addrId);
		rq.setSelectionCriteria(exp);

		Address current = (Address) rq.execute(session, EmptyRecord.getEmptyRecord());

		return current;
	}

	private long getMaxDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2099, Calendar.DECEMBER, 31, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date maxDate = cal.getTime();
		return maxDate.getTime();
	}

}
