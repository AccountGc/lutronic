package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "ecrm", type = ECRMRequest.class),

		roleB = @GeneratedRole(name = "cr", type = EChangeRequest.class)

)
public class EcrmToCrLink extends _EcrmToCrLink {
	static final long serialVersionUID = 1;

	public static EcrmToCrLink newEcrmToCrLink(ECRMRequest ecrm, EChangeRequest cr) throws WTException {
		EcrmToCrLink instance = new EcrmToCrLink();
		instance.initialize(ecrm, cr);
		return instance;
	}
}
