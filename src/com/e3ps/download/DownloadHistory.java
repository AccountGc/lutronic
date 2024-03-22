
package com.e3ps.download;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "target", type = String.class, javaDoc = "객체 OID"),

				@GeneratedProperty(name = "cnt", type = int.class, javaDoc = "횟수"),

				@GeneratedProperty(name = "describe", type = String.class),

				@GeneratedProperty(name = "name", type = String.class)

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "HistoryUserLink", myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "user", type = WTUser.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "history", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "PersistHistoryLink", myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "persist", type = Persistable.class, constraints = @PropertyConstraints(required = false)), myRole = @MyRole(name = "history", cardinality = Cardinality.ONE)), }

)
public class DownloadHistory extends _DownloadHistory {

	static final long serialVersionUID = 1;

	public static DownloadHistory newDownloadHistory() throws WTException {
		DownloadHistory instance = new DownloadHistory();
		instance.initialize();
		return instance;
	}
}