package com.e3ps.system;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class })

public class SystemErrorLog extends _SystemErrorLog {

	static final long serialVersionUID = 1;

	public static SystemErrorLog newSystemErrorLog() throws WTException {
		SystemErrorLog instance = new SystemErrorLog();
		instance.initialize();
		return instance;
	}

}
