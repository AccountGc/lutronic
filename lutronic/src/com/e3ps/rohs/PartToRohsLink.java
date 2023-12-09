
package com.e3ps.rohs;

import com.e3ps.rohs.ROHSMaterial;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import wt.part.WTPart;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionToVersionLink;
import com.ptc.windchill.annotations.metadata.*;

@GenAsBinaryLink(superClass = VersionToVersionLink.class,

		serializable = Serialization.EXTERNALIZABLE_BASIC,

		roleA = @GeneratedRole(name = "part", type = WTPart.class),

		roleB = @GeneratedRole(name = "rohs", type = ROHSMaterial.class),

		tableProperties = @TableProperties(tableName = "PartToRohsLink"

		)

)
public class PartToRohsLink extends _PartToRohsLink {

	static final long serialVersionUID = 1;

	public static PartToRohsLink newPartToRohsLink(WTPart part, ROHSMaterial rohs) throws WTException {
		PartToRohsLink instance = new PartToRohsLink();
		instance.initialize(part, rohs);
		return instance;
	}
}