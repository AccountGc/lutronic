package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(type = EChangeOrder.class, name = "eco"),

		roleB = @GeneratedRole(type = ECPRRequest.class, name = "ecpr")

)

public class EcoToEcprLink extends _EcoToEcprLink {
	static final long serialVersionUID = 1;

	public static EcoToEcprLink newEcoToEcprLink(EChangeOrder cro, ECPRRequest ecpr) throws WTException {
		EcoToEcprLink instance = new EcoToEcprLink();
		instance.initialize(cr, ecpr);
		return instance;
	}
}
