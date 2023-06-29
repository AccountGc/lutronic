package com.e3ps.common.lifecycle.service;

import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface LifeCycleService {

	LifeCycleManaged setLifeCycleState(LifeCycleManaged lcm, String state) throws WTException;

}
