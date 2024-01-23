package com.e3ps.event;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;
import com.e3ps.org.service.DepartmentHelper;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceManagerEvent;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEventService extends StandardManager implements EventService {

	private static final String POST_STORE = PersistenceManagerEvent.POST_STORE;
	private static final String POST_MODIFY = PersistenceManagerEvent.POST_MODIFY;
	private static final String STATE_CHANGE = LifeCycleServiceEvent.STATE_CHANGE;

	public static StandardEventService newStandardEventService() throws WTException {
		StandardEventService instance = new StandardEventService();
		instance.initialize();
		return instance;
	}

	protected synchronized void performStartupProcess() throws ManagerException {
		super.performStartupProcess();
		EventListener listener = new EventListener(StandardEventService.class.getName());
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(POST_STORE));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(POST_MODIFY));
		getManagerService().addEventListener(listener, LifeCycleServiceEvent.generateEventKey(STATE_CHANGE));
	}

	@Override
	public void create(WTUser wtUser) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Department department = DepartmentHelper.manager.getRoot();

			People people = People.newPeople();
			people.setDepartment(department);
			people.setUser(wtUser);
			people.setPdfAuth(false);
			people.setName(wtUser.getFullName());
			people.setId(wtUser.getName());
			people.setEmail(wtUser.getEMail() != null ? wtUser.getEMail() : "");
			people = (People) PersistenceHelper.manager.save(people);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void modify(WTUser wtUser) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			QueryResult result = PersistenceHelper.manager.navigate(wtUser, "people", WTUserPeopleLink.class);
			if (result.hasMoreElements()) {
				People people = (People) result.nextElement();
				people.setUser(wtUser);
				people.setPdfAuth(false);
				people.setName(wtUser.getFullName());
				people.setId(wtUser.getName());
				people.setEmail(wtUser.getEMail() != null ? wtUser.getEMail() : "");
				people = (People) PersistenceHelper.manager.modify(people);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
