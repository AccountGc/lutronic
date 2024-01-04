package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "ecrm", type = ECRMRequest.class),

		roleB = @GeneratedRole(name = "eco", type = EChangeOrder.class)

)
public class EcrmToEcoLink extends _EcrmToEcoLink {
	static final long serialVersionUID = 1;

	public static EcrmToEcoLink newEcrmToEcoLink(ECRMRequest ecrm, EChangeOrder eco) throws WTException {
		EcrmToEcoLink instance = new EcrmToEcoLink();
		instance.initialize(ecrm, eco);
		return instance;
	}
}
