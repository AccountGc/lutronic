package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(type = ECPRRequest.class, name = "ecpr"),

		roleB = @GeneratedRole(type = WTDocument.class, name = "doc")

)
public class EcprToDocumentLink extends _EcprToDocumentLink {
	static final long serialVersionUID = 1;

	public static EcprToDocumentLink newEcprToDocumentLink(ECPRRequest ecpr, WTDocument doc) throws WTException {
		EcprToDocumentLink instance = new EcprToDocumentLink();
		instance.initialize(ecpr, doc);
		return instance;
	}
}
