
package com.e3ps.change;

//import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.Serialization;
import com.ptc.windchill.annotations.metadata.TableProperties;

//import java.sql.SQLException;
import wt.fc.ObjectToObjectLink;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "ecoType", type = String.class) },

		roleA = @GeneratedRole(name = "eco", type = EChangeOrder.class),

		roleB = @GeneratedRole(name = "ecr", type = EChangeRequest.class),

		tableProperties = @TableProperties(tableName = "RequestOrderLink")

)

public class RequestOrderLink extends _RequestOrderLink {

	static final long serialVersionUID = 1;

	public static RequestOrderLink newRequestOrderLink(EChangeOrder eco, EChangeRequest ecr) throws WTException {
		RequestOrderLink instance = new RequestOrderLink();
		instance.initialize(eco, ecr);
		return instance;
	}
}
