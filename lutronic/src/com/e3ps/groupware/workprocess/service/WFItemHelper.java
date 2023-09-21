package com.e3ps.groupware.workprocess.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.common.message.Message;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;

import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.PhaseTemplate;
import wt.lifecycle.State;
import wt.services.ServiceFactory;

public class WFItemHelper {
	public static final WFItemService service = ServiceFactory.getService(WFItemService.class);
	public static final WFItemHelper manager = new WFItemHelper();
	
	public List<Map<String,String>> lifecycleList(String paramLifecycle, String paramState){
		List<Map<String,String>> list = null;
		String lifecycle = paramLifecycle;
		String state = StringUtil.checkReplaceStr(paramState,"");
		
		try {
			if(!StringUtil.checkString(lifecycle)) {
				lifecycle = "LC_Default";
			}
			list = getLifeCycleState(lifecycle, state);
		}catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<Map<String,String>>();
		}
		return list;
	}
	
	public List<Map<String,String>> getLifeCycleState(String lifeCycle, String state) throws Exception {
    	List<Map<String,String>> stateVec = new ArrayList<Map<String,String>>();
    	
		LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(lifeCycle, WCUtil.getWTContainerRef());
		Vector states = LifeCycleHelper.service.getPhaseTemplates(lct);
		PhaseTemplate pt = null;
		State lcState = null;
		for (int i = 0; i < states.size(); i++) {
			pt = (PhaseTemplate) states.get(i);
			lcState = pt.getPhaseState();
			// stateVec.add(lcState);
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", lcState.getDisplay(Message.getLocale()));
			map.put("code", lcState.toString());
			if(StringUtil.checkString(state)) {
				for(String a : state.split(",")) {
					if(a.equals(lcState.toString())) {
						stateVec.add(map);
					}
				}
			}else {
				stateVec.add(map);
			}
		}
    	
    	return stateVec;
    	
    }
}
