package com.e3ps.system;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class })
public class SystemFassoLog extends _SystemFassoLog {

	static final long serialVersionUID = 1;

	public static SystemFassoLog newSystemFassoLog() throws WTException {
		SystemFassoLog instance = new SystemFassoLog();
		instance.initialize();
		return instance;
	}

}
