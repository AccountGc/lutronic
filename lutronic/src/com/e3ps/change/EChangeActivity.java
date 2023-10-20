
package com.e3ps.change;

//import java.sql.SQLException;
import java.sql.Timestamp;

//import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import wt.content.ContentHolder;
import wt.enterprise.Managed;
import wt.inf.container.WTContained;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = Managed.class, interfaces = { WTContained.class,
		ContentHolder.class }, serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "name", type = String.class, columnProperties = @ColumnProperties(persistent = true)),

				@GeneratedProperty(name = "step", type = String.class),

				@GeneratedProperty(name = "activeState", type = String.class),

				@GeneratedProperty(name = "finishDate", type = Timestamp.class),

				@GeneratedProperty(name = "sortNumber", type = int.class),

				@GeneratedProperty(name = "activeType", type = String.class, javaDoc = "CHIEF , WORKING"),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "comments", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "EOActivityLink", myRoleIsRoleA = false,

						foreignKeyRole = @ForeignKeyRole(name = "eo", type = ECOChange.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "activity")),

				@GeneratedForeignKey(myRoleIsRoleA = false,

						foreignKeyRole = @ForeignKeyRole(name = "activeUser", type = WTUser.class),

						myRole = @MyRole(name = "activity"))

		}

)
public class EChangeActivity extends _EChangeActivity {

	static final long serialVersionUID = 1;

	public static EChangeActivity newEChangeActivity() throws WTException {
		EChangeActivity instance = new EChangeActivity();
		instance.initialize();
		return instance;
	}
}
