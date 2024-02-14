package com.e3ps.system;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "ip", type = String.class),

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "departmentName", type = String.class),

				@GeneratedProperty(name = "targetName", type = String.class),

		}

)
public class PrintHistory extends _PrintHistory {
	static final long serialVersionUID = 1;

	public static PrintHistory newPrintHistory() throws WTException {
		PrintHistory instance = new PrintHistory();
		instance.initialize();
		return instance;
	}
}
