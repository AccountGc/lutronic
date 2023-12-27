package com.e3ps.org.service;

import javax.servlet.http.HttpServletRequest;

import com.e3ps.org.Department;
import com.e3ps.org.People;

import wt.org.WTUser;
import wt.query.QueryException;
import wt.query.QuerySpec;

public interface PeopleService {

	void eventListener(Object _obj, String _event);

	void syncStore(WTUser _user);

	void syncModify(WTUser _user);

	void syncDelete(WTUser _user);

	void syncWTUser();

	People getPeople(String name);

	void savePassword(HttpServletRequest req);

	WTUser getChiefUser(Department depart);

	QuerySpec getUserSearch(String name) throws QueryException;

}
