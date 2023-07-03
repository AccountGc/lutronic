package com.e3ps.org.beans;

import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;

import lombok.Getter;
import lombok.Setter;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;

@Getter
@Setter
public class UserData {

	private String poid;
	private String woid;
	private String name;
	private String id;

	public UserData() throws Exception {

	}

	public UserData(WTUser user) throws Exception {
		setWoid(user.getPersistInfo().getObjectIdentifier().getStringValue());
		QueryResult qr = PersistenceHelper.manager.navigate(user, "people", WTUserPeopleLink.class);
		if (qr.hasMoreElements()) {
			People people = (People) qr.nextElement();
			setPoid(people.getPersistInfo().getObjectIdentifier().getStringValue());
			setName(people.getName());
			setId(people.getUser().getName());
		}
	}

	public UserData(People people) throws Exception {
		setPoid(people.getPersistInfo().getObjectIdentifier().getStringValue());
		setWoid(people.getUser().getPersistInfo().getObjectIdentifier().getStringValue());
		setName(people.getName());
		setId(people.getUser().getName());
	}
}
