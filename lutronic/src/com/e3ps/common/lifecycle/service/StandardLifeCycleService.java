package com.e3ps.common.lifecycle.service;

import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class StandardLifeCycleService extends StandardManager implements LifeCycleService {

	public static StandardLifeCycleService newStandardLifeCycleService() throws Exception {
		final StandardLifeCycleService instance = new StandardLifeCycleService();
		instance.initialize();
		return instance;
	}
	
	/**
     * ���� ����
     * @param lcm
     * @param state
     * @return
     * @throws WTException
     * @author : Choi Seunghwan
     * @since : 2006.03
     */
	@Override
    public LifeCycleManaged setLifeCycleState(LifeCycleManaged lcm, String state) throws WTException {
        if (lcm == null || state == null) { return lcm; }
        
        boolean isCheckOut = false;
        if(lcm instanceof Workable) {
            isCheckOut = WorkInProgressHelper.isCheckedOut((Workable) lcm);
        }
        
        if (!isCheckOut && !lcm.getLifeCycleState().equals(State.toState(state)))
            lcm = wt.lifecycle.LifeCycleHelper.service.setLifeCycleState(lcm, State.toState(state), false);
        return lcm;
    }
}
