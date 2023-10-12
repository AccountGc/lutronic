package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

	roleA = @GeneratedRole(type = EChangeRequest.class, name = "cr"),
	
	roleB = @GeneratedRole(type = ECPRRequest.class, name = "ecpr")

)

public class CrToEcprLink extends _CrToEcprLink {
	static final long serialVersionUID = 1;

	public static CrToEcprLink newCrToEcprLink(EChangeRequest cr, ECPRRequest ecpr) throws WTException {
		CrToEcprLink instance = new CrToEcprLink();
		instance.initialize(cr, ecpr);
		return instance;
	}
}
