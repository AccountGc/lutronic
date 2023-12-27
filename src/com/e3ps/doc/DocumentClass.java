package com.e3ps.doc;

import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "clazz", type = String.class),

				@GeneratedProperty(name = "description", type = String.class),

				@GeneratedProperty(name = "enabled", type = Boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "sort", type = String.class),

				@GeneratedProperty(name = "classType", type = DocumentClassType.class),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "DClassDClassLink",

						foreignKeyRole = @ForeignKeyRole(name = "parent", type = DocumentClass.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "child"))

		}

)

public class DocumentClass extends _DocumentClass {

	static final long serialVersionUID = 1;

	public static DocumentClass newDocumentClass() throws WTException {
		DocumentClass instance = new DocumentClass();
		instance.initialize();
		return instance;
	}

}
