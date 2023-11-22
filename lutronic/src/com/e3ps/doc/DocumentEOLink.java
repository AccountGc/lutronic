package com.e3ps.doc;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;
import wt.workflow.work.WorkflowHelper;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(type = WTDocument.class, name = "document"),

		roleB = @GeneratedRole(type = EChangeOrder.class, name = "eo")

)
public class DocumentEOLink extends _DocumentEOLink {
	static final long serialVersionUID = 1;

	public static DocumentEOLink newDocumentEOLink(WTDocument document, EChangeOrder eo) throws WTException {
		WorkflowHelper.service.getWorkItems(eo)
		DocumentEOLink instance = new DocumentEOLink();
		instance.initialize(document, eo);
		return instance;
	}

}
