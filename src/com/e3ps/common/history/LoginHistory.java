
package com.e3ps.common.history;

import wt.fc.InvalidAttributeException;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.Serialization;

@GenAsPersistable(serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "id", type = String.class),

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "conTime", type = String.class),

				@GeneratedProperty(name = "ip", type = String.class)

		}

)
public class LoginHistory extends _LoginHistory {

	static final long serialVersionUID = 1;

	public static LoginHistory newLoginHistory() throws WTException {

		LoginHistory instance = new LoginHistory();
		instance.initialize();
		return instance;
	}

	protected void initialize() throws WTException {

	}

	public String getIdentity() {

		return null;
	}

	public String getType() {

		return null;
	}

	@Override
	public void checkAttributes() throws InvalidAttributeException {

	}

}
