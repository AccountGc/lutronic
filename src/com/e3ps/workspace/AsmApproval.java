package com.e3ps.workspace;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.StringCase;

import wt.enterprise.Managed;
import wt.util.WTException;

@GenAsPersistable(superClass = Managed.class,

		properties = {

				@GeneratedProperty(name = "name", type = String.class, columnProperties = @ColumnProperties(persistent = true)),

				@GeneratedProperty(name = "number", type = String.class, constraints = @PropertyConstraints(stringCase = StringCase.UPPER_CASE, upperLimit = 20), columnProperties = @ColumnProperties(columnName = "asmNumber")),

				@GeneratedProperty(name = "description", type = String.class)

		}

)
public class AsmApproval extends _AsmApproval {

	static final long serialVersionUID = 1;

	public static AsmApproval newAsmApproval() throws WTException {
		AsmApproval instance = new AsmApproval();
		instance.initialize();
		return instance;
	}
}
