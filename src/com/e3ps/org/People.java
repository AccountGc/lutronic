
package com.e3ps.org;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = {

		ContentHolder.class }, serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "duty", type = String.class, constraints = @PropertyConstraints(upperLimit = 30)),

				@GeneratedProperty(name = "dutyCode", type = String.class, constraints = @PropertyConstraints(upperLimit = 30)),

				@GeneratedProperty(name = "officeTel", type = String.class, constraints = @PropertyConstraints(upperLimit = 50)),

				@GeneratedProperty(name = "homeTel", type = String.class, constraints = @PropertyConstraints(upperLimit = 50)),

				@GeneratedProperty(name = "cellTel", type = String.class, constraints = @PropertyConstraints(upperLimit = 50)),

				@GeneratedProperty(name = "address", type = String.class, constraints = @PropertyConstraints(upperLimit = 100)),

				@GeneratedProperty(name = "priority", type = int.class, constraints = @PropertyConstraints(upperLimit = 100)),

				@GeneratedProperty(name = "password", type = String.class, constraints = @PropertyConstraints(upperLimit = 30)),

				@GeneratedProperty(name = "isDisable", type = boolean.class, initialValue = "false"),

				@GeneratedProperty(name = "sortNum", type = int.class, javaDoc = "0"),

				@GeneratedProperty(name = "email", type = String.class),

				@GeneratedProperty(name = "id", type = String.class),

				@GeneratedProperty(name = "pwChangeDate", type = Timestamp.class),

				@GeneratedProperty(name = "name", type = String.class, constraints = @PropertyConstraints(upperLimit = 100)),

				@GeneratedProperty(name = "title", type = String.class),

				@GeneratedProperty(name = "nameEn", type = String.class),

				@GeneratedProperty(name = "auth", type = String.class),

				@GeneratedProperty(name = "chief", type = Boolean.class, initialValue = "false"),

				@GeneratedProperty(name = "pdfAuth", type = Boolean.class)

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "WTUserPeopleLink",

						foreignKeyRole = @ForeignKeyRole(name = "user", type = WTUser.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "people", cardinality = Cardinality.ZERO_TO_ONE)),

				@GeneratedForeignKey(name = "DepartmentPeopleLink",

						foreignKeyRole = @ForeignKeyRole(name = "department", type = Department.class),

						myRole = @MyRole(name = "people"))

		}

)
public class People extends _People {

	static final long serialVersionUID = 1;

	public static People newPeople() throws WTException {

		People instance = new People();
		instance.initialize();
		return instance;
	}

}
