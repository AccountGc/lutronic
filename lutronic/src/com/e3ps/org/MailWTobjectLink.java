package com.e3ps.org;

import java.io.IOException;
import java.io.ObjectInput;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.ObjectToObjectLink;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, versions = { 2538346186404157511L },

		roleA = @GeneratedRole(name = "object", type = WTObject.class),

		roleB = @GeneratedRole(name = "user", type = MailUser.class, cardinality = Cardinality.ONE),

		tableProperties = @TableProperties(tableName = "MailWTobjectLink")

)
public class MailWTobjectLink extends _MailWTobjectLink {

	static final long serialVersionUID = 1;

	public static MailWTobjectLink newMailWTobjectLink(WTObject object, MailUser user) throws WTException {
		MailWTobjectLink instance = new MailWTobjectLink();
		instance.initialize(object, user);
		return instance;
	}

	boolean readVersion2538346186404157511L(ObjectInput input, long readSerialVersionUID, boolean superDone)
			throws IOException, ClassNotFoundException {
		if (!superDone) // if not doing backward compatibility
			super.readExternal(input); // handle super class

		return true;
	}
}
