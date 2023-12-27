
package com.e3ps.workspace;

import java.io.IOException;
import java.io.ObjectInput;

import wt.enterprise.Managed;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.StringCase;

@GenAsPersistable(superClass = Managed.class, versions = { 1963349263721597420L },

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

	boolean readVersion1963349263721597420L(ObjectInput input, long readSerialVersionUID, boolean superDone)
			throws IOException, ClassNotFoundException {

		if (!superDone) // if not doing backward compatibility
			super.readExternal(input); // handle super class

		description = (String) input.readObject();
		name = (String) input.readObject();
		number = (String) input.readObject();

		return true;
	}
}
