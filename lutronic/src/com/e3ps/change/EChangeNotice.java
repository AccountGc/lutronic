
package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.org.WTUser;
//import com.e3ps.change.EChangeOrder;
//import java.io.Externalizable;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectOutput;
//import java.lang.ClassNotFoundException;
//import java.lang.Object;
//import java.lang.String;
//import java.sql.SQLException;
//import wt.fc.ObjectReference;
//import wt.pds.PersistentRetrieveIfc;
//import wt.pds.PersistentStoreIfc;
//import wt.pom.DatastoreException;
import wt.util.WTException;

@GenAsPersistable(superClass = ECOChange.class,

		foreignKeys = {

				@GeneratedForeignKey(

						foreignKeyRole = @ForeignKeyRole(name = "eco", type = EChangeOrder.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "ecn", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "EcnWorkerLink",

						foreignKeyRole = @ForeignKeyRole(name = "worker", type = WTUser.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "ecn", cardinality = Cardinality.ONE))

		}

)
public class EChangeNotice extends _EChangeNotice {

	static final long serialVersionUID = 1;

	public static EChangeNotice newEChangeNotice() throws WTException {
		EChangeNotice instance = new EChangeNotice();
		instance.initialize();
		return instance;
	}
}
