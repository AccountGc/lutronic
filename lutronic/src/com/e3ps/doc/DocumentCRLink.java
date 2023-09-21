package com.e3ps.doc;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(type = WTDocument.class, name = "document"),

		roleB = @GeneratedRole(type = EChangeRequest.class, name = "cr")

)
public class DocumentCRLink extends _DocumentCRLink {
	static final long serialVersionUID = 1;

	public static DocumentCRLink newDocumentCRLink(WTDocument document, EChangeRequest cr) throws WTException {
		DocumentCRLink instance = new DocumentCRLink();
		instance.initialize(document, cr);
		return instance;
	}

}
