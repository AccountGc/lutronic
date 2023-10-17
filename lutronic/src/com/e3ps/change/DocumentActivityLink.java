
package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.doc.WTDocumentMaster;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "branchIdentifier", type = long.class),

				@GeneratedProperty(name = "docClassName", type = String.class, constraints = @PropertyConstraints(upperLimit = 50))

		},

		roleA = @GeneratedRole(name = "doc", type = WTDocumentMaster.class, cardinality = Cardinality.ONE),

		roleB = @GeneratedRole(name = "activity", type = EChangeActivity.class), tableProperties = @TableProperties(tableName = "DocumentActivityLink")

)
public class DocumentActivityLink extends _DocumentActivityLink {

	static final long serialVersionUID = 1;

	public static DocumentActivityLink newDocumentActivityLink(WTDocumentMaster doc, EChangeActivity activity)
			throws WTException {
		DocumentActivityLink instance = new DocumentActivityLink();
		instance.initialize(doc, activity);
		return instance;
	}
}