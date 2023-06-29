package com.e3ps.listener;

import wt.events.KeyedEvent;
import wt.services.ServiceEventListenerAdapter;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.service.EventEOHelper;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.groupware.workprocess.service.AsmApprovalHelper;
import com.e3ps.groupware.workprocess.service.EventAsmHelper;
//import com.e3ps.change.beans.EventEOManager;
//import com.e3ps.common.util.EventVersionManager;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
//import com.e3ps.org.beans.PeopleHelper;
import com.e3ps.org.service.PeopleHelper;

public class EventListener extends ServiceEventListenerAdapter {
	private static String LAF_HOME = "";

	public EventListener(String s) {
		super(s);
	}

	public void notifyVetoableEvent(Object obj) throws Exception {
		KeyedEvent keyedevent = (KeyedEvent) obj;
		Object eventObj = keyedevent.getEventTarget();
		String eventType = keyedevent.getEventType();
		//System.out.println( "[EventListener][keyedevent]"+keyedevent.getEventType()+"\tobj="+eventObj);
		if (obj instanceof EChangeActivity){
			//System.out.println( "[EventListener][keyedevent]"+keyedevent.getEventType());
		}
	    
		//Last 버전 iba 변경
		//EventVersionManager.manager.eventListener(eventObj, keyedevent.getEventType());
		
		//워크플로 상태변경 로봇 이벤트 실행시 STATE_CHANGE
		WFItemHelper.service.eventListener(eventObj, keyedevent.getEventType());
		
		//설계변경 
		EventEOHelper.service.eventListener(eventObj, keyedevent.getEventType());
		
		//일괄결재 상태 변경
		EventAsmHelper.service.eventListener(eventObj, keyedevent.getEventType());
		
		
		// 유저 생성시 People 싱크
		PeopleHelper.service.eventListener(eventObj, keyedevent.getEventType());
		
		
		
	}
}