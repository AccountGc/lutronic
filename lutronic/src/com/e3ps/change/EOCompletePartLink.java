package com.e3ps.change;

//import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.Serialization;
import com.ptc.windchill.annotations.metadata.TableProperties;

//import java.sql.SQLException;
import wt.fc.ObjectToObjectLink;
import wt.part.WTPartMaster;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, serializable = Serialization.EXTERNALIZABLE_BASIC,

		properties = {

				@GeneratedProperty(name = "version", type = String.class)

		},

		roleA = @GeneratedRole(name = "completePart", type = WTPartMaster.class),

		roleB = @GeneratedRole(name = "eco", type = EChangeOrder.class),

		tableProperties = @TableProperties(tableName = "EOCompletePartLink")

)
public class EOCompletePartLink extends _EOCompletePartLink {

	static final long serialVersionUID = 1;

	public static EOCompletePartLink newEOCompletePartLink(WTPartMaster completePart, EChangeOrder eco)
			throws WTException {
		EOCompletePartLink instance = new EOCompletePartLink();
		instance.initialize(completePart, eco);
		return instance;
	}
}