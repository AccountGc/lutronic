package com.e3ps.change;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.part.WTPart;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "nation", type = String.class),

				@GeneratedProperty(name = "sendDate", type = Timestamp.class),

				@GeneratedProperty(name = "isSend", type = Boolean.class)

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "EcnSendLink",

						foreignKeyRole = @ForeignKeyRole(name = "ecn", type = EChangeNotice.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "send", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "PartSendLink",

						foreignKeyRole = @ForeignKeyRole(name = "part", type = WTPart.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "send", cardinality = Cardinality.ONE)),

		}

)
public class PartToSendLink extends _PartToSendLink {

	static final long serialVersionUID = 1;

	public static PartToSendLink newPartToSendLink() throws WTException {
		PartToSendLink instance = new PartToSendLink();
		instance.initialize();
		return instance;
	}

}
