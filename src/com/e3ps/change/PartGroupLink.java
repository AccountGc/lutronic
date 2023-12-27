package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.ObjectToObjectLink;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "part", type = WTPart.class),

		roleB = @GeneratedRole(name = "ecr", type = EChangeRequest.class),

		foreignKeys = { @GeneratedForeignKey(name = "EcoGroupLink",

				foreignKeyRole = @ForeignKeyRole(name = "eco", type = EChangeOrder.class,

						constraints = @PropertyConstraints(required = true)),

				myRole = @MyRole(name = "group", cardinality = Cardinality.ONE))

		}

)
public class PartGroupLink extends _PartGroupLink {
	static final long serialVersionUID = 1;

	public static PartGroupLink newPartGroupLink(WTPart part, EChangeRequest ecr) throws WTException {
		PartGroupLink instance = new PartGroupLink();
		instance.initialize(part, ecr);
		return instance;
	}
}
