
package com.e3ps.workspace;

import java.io.IOException;
import java.io.ObjectInput;

import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.TableProperties;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, versions = { 2538346186404157511L },

		roleA = @GeneratedRole(name = "persistable", type = Persistable.class),

		roleB = @GeneratedRole(name = "approval", type = AsmApproval.class, cardinality = Cardinality.ONE),

		tableProperties = @TableProperties(tableName = "AppPerLink")

)
public class AppPerLink extends _AppPerLink {

	static final long serialVersionUID = 1;

	public static AppPerLink newAppPerLink(Persistable persistable, AsmApproval approval) throws WTException {

		AppPerLink instance = new AppPerLink();
		instance.initialize(persistable, approval);
		return instance;
	}

	boolean readVersion2538346186404157511L(ObjectInput input, long readSerialVersionUID, boolean superDone)
			throws IOException, ClassNotFoundException {

		if (!superDone) // if not doing backward compatibility
			super.readExternal(input); // handle super class

		return true;
	}

}
