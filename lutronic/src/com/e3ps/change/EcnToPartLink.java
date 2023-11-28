package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.ObjectToObjectLink;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "ecn", type = EChangeNotice.class),

		roleB = @GeneratedRole(name = "part", type = WTPart.class),

		properties = {

				@GeneratedProperty(name = "workEnd", type = Boolean.class),

				@GeneratedProperty(name = "rate", type = Double.class)

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "EcrEcnLink",

						foreignKeyRole = @ForeignKeyRole(name = "ecr", type = EChangeRequest.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "ecn", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "PartEcnLink",

						foreignKeyRole = @ForeignKeyRole(name = "completePart", type = WTPartMaster.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "ecn", cardinality = Cardinality.ONE)),

		}

)

public class EcnToPartLink extends _EcnToPartLink {

	static final long serialVersionUID = 1;

	public static EcnToPartLink newEcnToPartLink(EChangeNotice ecn, WTPart part) throws WTException {
		EcnToPartLink instance = new EcnToPartLink();
		instance.initialize(ecn, part);
		return instance;
	}
}
