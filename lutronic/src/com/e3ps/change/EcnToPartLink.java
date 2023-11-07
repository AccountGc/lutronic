package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.part.WTPart;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "ecn", type = EChangeNotice.class),

		roleB = @GeneratedRole(name = "part", type = WTPart.class)

)

public class EcnToPartLink extends _EcnToPartLink {

	static final long serialVersionUID = 1;

	public static EcnToPartLink newEcnToPartLink(EChangeNotice ecn, WTPart part) throws WTException {
		EcnToPartLink instance = new EcnToPartLink();
		instance.initialize(ecn, part);
		return instance;
	}
}
