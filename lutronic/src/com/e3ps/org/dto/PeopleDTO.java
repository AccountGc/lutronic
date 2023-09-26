/**
 * @(#)	PeopleData.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @since jdk 1.4.2
 * @author Cho Sung Ok, jerred@e3ps.com
 */

package com.e3ps.org.dto;

import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;

import lombok.Getter;
import lombok.Setter;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Getter
@Setter
public class PeopleDTO {

	private String poid;
	private String woid;
	private String name;
	private String id;
	private String department_name;
	private String department_oid;
	private String email;
	private String auth;
	private String duty;

	public PeopleDTO() throws Exception {
		this((WTUser) SessionHelper.manager.getPrincipal());
	}

	public PeopleDTO(WTUser user) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(user, "people", WTUserPeopleLink.class);
		People people = null;
		if (result.hasMoreElements()) {
			people = (People) result.nextElement();
		}
		setPoid(people.getPersistInfo().getObjectIdentifier().getStringValue());
		setWoid(people.getUser().getPersistInfo().getObjectIdentifier().getStringValue());
		setName(people.getName());
		setId(people.getId());
		if (people.getDepartment() != null) {
			setDepartment_name(people.getDepartment().getName());
			setDepartment_oid(people.getDepartment().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setEmail(people.getEmail());
		setAuth(people.getAuth());
		setDuty(people.getDuty() != null ? people.getDuty() : "지정안됨");
	}

	public PeopleDTO(People people) throws Exception {
		setPoid(people.getPersistInfo().getObjectIdentifier().getStringValue());
		setWoid(people.getUser().getPersistInfo().getObjectIdentifier().getStringValue());
		setName(people.getName());
		setId(people.getId());
		if (people.getDepartment() != null) {
			setDepartment_name(people.getDepartment().getName());
			setDepartment_oid(people.getDepartment().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setEmail(people.getEmail());
		setAuth(people.getAuth());
		setDuty(people.getDuty() != null ? people.getDuty() : "지정안됨");
	}
}