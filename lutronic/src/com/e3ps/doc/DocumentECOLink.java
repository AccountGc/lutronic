package com.e3ps.doc;

import com.e3ps.change.EChangeOrder;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(type = WTDocument.class, name = "document"),

		roleB = @GeneratedRole(type = EChangeOrder.class, name = "eco")

)
public class DocumentECOLink extends _DocumentECOLink {
	static final long serialVersionUID = 1;

	public static DocumentECOLink newDocumentECOLink(WTDocument document, EChangeOrder eco) throws WTException {
		DocumentECOLink instance = new DocumentECOLink();
		instance.initialize(document, eco);
		return instance;
	}
}
