package com.e3ps.change;

import com.e3ps.common.util.OwnPersistable;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;

import wt.content.ContentHolder;
import wt.inf.container.WTContained;
import wt.org.WTUser;
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

				foreignKeyRole = @ForeignKeyRole(name = "worker", type = WTUser.class),

				myRole = @MyRole(name = "ecr"))

		}

)
public class ECPRRequest extends _ECPRRequest {

	static final long serialVersionUID = 1;

	public static ECPRRequest newECPRRequest() throws WTException {

		ECPRRequest instance = new ECPRRequest();
		instance.initialize();
		return instance;
	}
}
