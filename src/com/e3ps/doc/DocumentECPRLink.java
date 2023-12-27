package com.e3ps.doc;

import com.e3ps.change.ECPRRequest;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

roleA = @GeneratedRole(type = WTDocument.class, name = "document"),

roleB = @GeneratedRole(type = ECPRRequest.class, name = "ecpr")

)
public class DocumentECPRLink extends _DocumentECPRLink {
	static final long serialVersionUID = 1;

	public static DocumentECPRLink newDocumentECPRLink(WTDocument document, ECPRRequest ecpr) throws WTException {
		DocumentECPRLink instance = new DocumentECPRLink();
		instance.initialize(document, ecpr);
		return instance;
	}
}
