
package com.e3ps.org;

import com.e3ps.common.util.Tree;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import wt.fc.InvalidAttributeException;
import wt.util.WTException;

@GenAsPersistable(interfaces = { Tree.class }, serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "name", type = String.class, constraints = @PropertyConstraints(upperLimit = 90)),

				@GeneratedProperty(name = "code", type = String.class, constraints = @PropertyConstraints(upperLimit = 90)),

				@GeneratedProperty(name = "sort", type = int.class)

		}

)
public class Department extends _Department {

	static final long serialVersionUID = 1;

	public static Department newDepartment() throws WTException {
		Department instance = new Department();
		instance.initialize();
		return instance;
	}

	protected void initialize() throws WTException {

	}

	public String getIdentity() {

		return null;
	}

	public String getType() {

		return null;
	}

	@Override
	public void checkAttributes() throws InvalidAttributeException {

	}

}
