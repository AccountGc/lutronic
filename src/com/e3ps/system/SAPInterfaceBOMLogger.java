package com.e3ps.system;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "oldParentNumber", type = String.class),

				@GeneratedProperty(name = "oldChildNumber", type = String.class),

				@GeneratedProperty(name = "oldParentVersion", type = String.class),

				@GeneratedProperty(name = "oldChildVersion", type = String.class),

				@GeneratedProperty(name = "newParentNumber", type = String.class),

				@GeneratedProperty(name = "newChildNumber", type = String.class),

				@GeneratedProperty(name = "newParentVersion", type = String.class),

				@GeneratedProperty(name = "newChildVersion", type = String.class),

				@GeneratedProperty(name = "qty", type = String.class),

				@GeneratedProperty(name = "sendType", type = String.class),

		}

)
public class SAPInterfaceBOMLogger extends _SAPInterfaceBOMLogger {
	static final long serialVersionUID = 1;

	public static SAPInterfaceBOMLogger newSAPInterfaceBOMLogger() throws WTException {
		SAPInterfaceBOMLogger instance = new SAPInterfaceBOMLogger();
		instance.initialize();
		return instance;
	}

}
