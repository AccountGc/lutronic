package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "ecrm", type = ECRMRequest.class),

		roleB = @GeneratedRole(name = "doc", type = WTDocument.class)

)
public class EcrmToDocumentLink extends _EcrmToDocumentLink {
	static final long serialVersionUID = 1;

	public static EcrmToDocumentLink newEcrmToDocumentLink(ECRMRequest ecrm, WTDocument doc) throws WTException {
		EcrmToDocumentLink instance = new EcrmToDocumentLink();
		instance.initialize(ecrm, doc);
		return instance;
	}
}
