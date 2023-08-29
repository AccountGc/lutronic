package com.e3ps.doc.template;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.OwnPersistable;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ColumnType;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.InvalidAttributeException;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		interfaces = { OwnPersistable.class },

		properties = { @GeneratedProperty(name = "number", type = String.class

				, columnProperties = @ColumnProperties(columnName = "docTemplateNumber")),

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "templateType", type = NumberCode.class),

				@GeneratedProperty(name = "description", type = String.class,

						columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

		}

)

public class DocumentTemplate extends _DocumentTemplate {
	static final long serialVersionUID = 1;

	public static DocumentTemplate newDocumentTemplate() throws WTException {
		DocumentTemplate instance = new DocumentTemplate();
		instance.initialize();
		return instance;
	}
}