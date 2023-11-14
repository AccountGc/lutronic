
package com.e3ps.change;

import com.e3ps.common.util.OwnPersistable;
//import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;

//import java.sql.SQLException;
import wt.content.ContentHolder;
//import wt.fc.ObjectReference;
import wt.inf.container.WTContained;
import wt.org.WTUser;
//import wt.org.WTUser;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;

@GenAsPersistable(superClass = ECOChange.class, interfaces = { OwnPersistable.class, WTContained.class,
		ContentHolder.class },

		properties = {

				@GeneratedProperty(name = "createDate", type = String.class),

				@GeneratedProperty(name = "approveDate", type = String.class),

				@GeneratedProperty(name = "createDepart", type = String.class),

				@GeneratedProperty(name = "writer", type = String.class),

				@GeneratedProperty(name = "proposer", type = String.class),

				@GeneratedProperty(name = "changeSection", type = String.class)

		},

		foreignKeys = { @GeneratedForeignKey(myRoleIsRoleA = false,

				foreignKeyRole = @ForeignKeyRole(name = "worker",

						type = WTUser.class), myRole = @MyRole(name = "ecr"))

		}

)

public class EChangeRequest extends _EChangeRequest {

	static final long serialVersionUID = 1;

	public static EChangeRequest newEChangeRequest() throws WTException {
		EChangeRequest instance = new EChangeRequest();
		instance.initialize();
		return instance;
	}
}
