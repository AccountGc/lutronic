package com.e3ps.admin.form;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ColumnType;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = { @GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "number", type = String.class, columnProperties = @ColumnProperties(columnName = "FORMNUMBER")),

				@GeneratedProperty(name = "description", type = String.class, columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

				@GeneratedProperty(name = "formType", type = String.class),

				@GeneratedProperty(name = "version", type = Integer.class)

		}

)

public class FormTemplate extends _FormTemplate {

	static final long serialVersionUID = 1;

	public static FormTemplate newFormTemplate() throws WTException {
		FormTemplate instance = new FormTemplate();
		instance.initialize();
		return instance;
	}
}
