package com.e3ps.common.code;

import wt.fc.InvalidAttributeException;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.Changeable;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

@GenAsPersistable(serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
		@GeneratedProperty(name = "name", type = String.class), @GeneratedProperty(name = "code", type = String.class),
		@GeneratedProperty(name = "description", type = String.class),
		@GeneratedProperty(name = "disabled", type = boolean.class),
		@GeneratedProperty(name = "engName", type = String.class),
		@GeneratedProperty(name = "sort", type = String.class),
		@GeneratedProperty(name = "codeType", type = NumberCodeType.class, constraints = @PropertyConstraints(changeable = Changeable.VIA_OTHER_MEANS, required = true)) }, foreignKeys = {
				@GeneratedForeignKey(name = "NCodeNCodeLink", myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "parent", type = com.e3ps.common.code.NumberCode.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "child")) })
public class NumberCode extends _NumberCode {

	static final long serialVersionUID = 1;

	public static NumberCode newNumberCode() throws WTException {

		NumberCode instance = new NumberCode();
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
