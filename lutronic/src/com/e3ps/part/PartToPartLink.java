package com.e3ps.part;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.part.WTPart;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "current", type = WTPart.class, javaDoc = "현재 부품"),

		roleB = @GeneratedRole(name = "after", type = WTPart.class, javaDoc = "개정 부품")

)
public class PartToPartLink extends _PartToPartLink {

	static final long serialVersionUID = 1;

	public static PartToPartLink newPartToPartLink(WTPart current, WTPart after) throws WTException {
		PartToPartLink instance = new PartToPartLink();
		instance.initialize(current, after);
		return instance;
	}

}
