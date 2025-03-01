package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.ObjectToObjectLink;
import wt.part.WTPartMaster;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "rightPart", type = Boolean.class),

				@GeneratedProperty(name = "leftPart", type = Boolean.class),

				@GeneratedProperty(name = "past", type = Boolean.class, javaDoc = "고도화 이전 여부"),

				@GeneratedProperty(name = "version", type = String.class, constraints = @PropertyConstraints(upperLimit = 10)),

				@GeneratedProperty(name = "visible", type = boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "revise", type = boolean.class, initialValue = "false"),

				@GeneratedProperty(name = "baseline", type = boolean.class),

				@GeneratedProperty(name = "delivery", type = String.class),

				@GeneratedProperty(name = "complete", type = String.class),

				@GeneratedProperty(name = "inner", type = String.class),

				@GeneratedProperty(name = "orders", type = String.class),

				@GeneratedProperty(name = "partStateCode", type = String.class),

				@GeneratedProperty(name = "preOrder", type = Boolean.class),

				@GeneratedProperty(name = "weight", type = Double.class),

				@GeneratedProperty(name = "sendType", type = String.class)

		},

		roleA = @GeneratedRole(name = "part", type = WTPartMaster.class),

		roleB = @GeneratedRole(name = "eco", type = EChangeOrder.class),

		tableProperties = @TableProperties(tableName = "EcoPartLink")

)
public class EcoPartLink extends _EcoPartLink {

	static final long serialVersionUID = 1;

	public static EcoPartLink newEcoPartLink(WTPartMaster part, EChangeOrder eco) throws WTException {
		EcoPartLink instance = new EcoPartLink();
		instance.initialize(part, eco);
		return instance;
	}
}
