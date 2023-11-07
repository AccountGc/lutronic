
package com.e3ps.change;

import java.util.Vector;

import com.e3ps.common.util.OwnPersistable;
import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;

import wt.content.ContentHolder;
import wt.inf.container.WTContained;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = ECOChange.class,

		interfaces = { WTContained.class, OwnPersistable.class, ContentHolder.class },

		properties = {

				@GeneratedProperty(name = "licensingChange", type = String.class),

				@GeneratedProperty(name = "ecoType", type = String.class),

				@GeneratedProperty(name = "eul", type = Vector.class, columnProperties = @ColumnProperties(persistent = false)),

				@GeneratedProperty(name = "riskType", type = String.class) },

		foreignKeys = {

				@GeneratedForeignKey(myRoleIsRoleA = false,

						foreignKeyRole = @ForeignKeyRole(name = "worker", type = WTUser.class),

						myRole = @MyRole(name = "eco", cardinality = Cardinality.ZERO_TO_ONE))

		}

)
public class EChangeOrder extends _EChangeOrder {

	static final long serialVersionUID = 1;

	public static EChangeOrder newEChangeOrder() throws WTException {

		EChangeOrder instance = new EChangeOrder();
		instance.initialize();
		return instance;
	}

}
