package com.e3ps.org.service;

import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;
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

	/**
	 * WTUser 객체로 People 객체 찾아오기
	 */
	public People getPeople(WTUser user) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(user, "people", WTUserPeopleLink.class);
		if (result.hasMoreElements()) {
			People p = (People) result.nextElement();
			return p;
		}
		return null;
	}
}