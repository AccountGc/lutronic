package com.e3ps.system;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "파일명"),

				@GeneratedProperty(name = "persistType", type = String.class, javaDoc = "파일 업로드 대상 타입"),

				@GeneratedProperty(name = "ip", type = String.class, javaDoc = "접속 아이피")

		}

)

public class SystemFasooLog extends _SystemFasooLog {

	static final long serialVersionUID = 1;

	public static SystemFasooLog newSystemFasooLog() throws WTException {
		SystemFasooLog instance = new SystemFasooLog();
		instance.initialize();
		return instance;
	}

}
