package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(type = EChangeRequest.class, name = "cr"),

		roleB = @GeneratedRole(type = WTDocument.class, name = "doc")

)

public class CrToDocumentLink extends _CrToDocumentLink {
	static final long serialVersionUID = 1;

	public static CrToDocumentLink newCrToDocumentLink(EChangeRequest cr, WTDocument doc) throws WTException {
		CrToDocumentLink instance = new CrToDocumentLink();
		instance.initialize(cr, doc);
		return instance;
	}
}
