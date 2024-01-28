package com.e3ps;

import java.math.BigDecimal;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;

public class Test2 {

	public static void main(String[] args) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, false);

		query.appendSelect(new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { idx }, false);

		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			BigDecimal bd = (BigDecimal) obj[0];
			System.out.println(bd.longValue());
		}

	}
}