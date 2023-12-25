package com.e3ps.event;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.eco.service.EcoHelper;
import com.e3ps.change.eo.service.EoHelper;

import wt.events.KeyedEvent;
import wt.fc.PersistenceManagerEvent;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.org.WTUser;
import wt.services.ServiceEventListenerAdapter;

public class EventListener extends ServiceEventListenerAdapter {

	private static final String POST_STORE = PersistenceManagerEvent.POST_STORE;
	private static final String POST_MODIFY = PersistenceManagerEvent.POST_MODIFY;
	private static final String STATE_CHANGE = LifeCycleServiceEvent.STATE_CHANGE;

	public EventListener(String s) {
		super(s);
	}

	public void notifyVetoableEvent(Object obj) throws Exception {
		if (!(obj instanceof KeyedEvent)) {
			return;
		}

		KeyedEvent keyedEvent = (KeyedEvent) obj;
		Object target = keyedEvent.getEventTarget();
		String type = keyedEvent.getEventType();

		// 사용자 싱크
		if (target instanceof WTUser) {
			WTUser wtUser = (WTUser) target;
			if (type.equals(POST_STORE)) {
				EventHelper.service.create(wtUser);
			} else if (type.equals(POST_MODIFY)) {
				EventHelper.service.modify(wtUser);
			}
		}
	}
}
