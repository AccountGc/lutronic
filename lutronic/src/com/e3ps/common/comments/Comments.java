package com.e3ps.common.comments;

import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {
				@GeneratedProperty(name = "comments", type = String.class, constraints = @PropertyConstraints(upperLimit = 1000)),

				@GeneratedProperty(name = "depth", type = Integer.class),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "PersistCommontsLink",

						foreignKeyRole = @ForeignKeyRole(name = "persist", type = Persistable.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "comments"))

		}

)
public class Comments extends _Comments {
	static final long serialVersionUID = 1;

	public static Comments newComments() throws WTException {
		Comments instance = new Comments();
		instance.initialize();
		return instance;
	}
}