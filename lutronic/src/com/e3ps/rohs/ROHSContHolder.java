
package com.e3ps.rohs;

import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import wt.content.ApplicationData;
import wt.fc.InvalidAttributeException;
import wt.util.WTException;

@GenAsPersistable(serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "fileType", type = String.class),

				@GeneratedProperty(name = "publicationDate", type = String.class),

				@GeneratedProperty(name = "fileName", type = String.class)

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "HolderAppliationLink", myRoleIsRoleA = false,

						foreignKeyRole = @ForeignKeyRole(name = "app", type = ApplicationData.class,

								constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "holder")),

				@GeneratedForeignKey(name = "RohsHolderLink", myRoleIsRoleA = false,

						foreignKeyRole = @ForeignKeyRole(name = "rohs", type = ROHSMaterial.class,

								constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "holder"))

		}

)
public class ROHSContHolder extends _ROHSContHolder {

	static final long serialVersionUID = 1;

	public static ROHSContHolder newROHSContHolder() throws WTException {

		ROHSContHolder instance = new ROHSContHolder();
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
