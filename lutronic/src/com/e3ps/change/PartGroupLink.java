package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.part.WTPart;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "part", type = WTPart.class),

		roleB = @GeneratedRole(name = "ecr", type = EChangeRequest.class)

)
public class PartGroupLink extends _PartGroupLink {
	static final long serialVersionUID = 1;

	public static PartGroupLink newPartGroupLink(WTPart part, EChangeRequest ecr) throws WTException {
		PartGroupLink instance = new PartGroupLink();
		instance.initialize(part, ecr);
		return instance;
	}
}
