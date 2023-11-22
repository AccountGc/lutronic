package com.e3ps.workspace;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "reads", type = Boolean.class, javaDoc = "결재 확인 여부", initialValue = "false"),
				
				@GeneratedProperty(name = "process", type = Boolean.class, javaDoc = "결재선 지정 완료 여부", initialValue = "false"),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "PerWorkDataLink",

						foreignKeyRole = @ForeignKeyRole(name = "per", type = Persistable.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "workData", cardinality = Cardinality.ONE))

		}

)
public class WorkData extends _WorkData {

	static final long serialVersionUID = 1;

	public static WorkData newWorkData() throws WTException {
		WorkData instance = new WorkData();
		instance.initialize();
		return instance;
	}
}
