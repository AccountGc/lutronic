package com.e3ps.org.service;

import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class PeopleHelper {
	public static final PeopleService service = ServiceFactory.getService(PeopleService.class);
	public static final PeopleHelper manager = new PeopleHelper();
	
	public PeopleDTO getPeople(WTUser user) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(People.class, true);
		query.appendWhere(new SearchCondition(People.class, "userReference.key.id", "=", user.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx });
		QueryResult qr = PersistenceHelper.manager.find(query);
		PeopleDTO data = null;
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			data = new PeopleDTO((People) obj[0]);
		}
		return data;
	}
}
