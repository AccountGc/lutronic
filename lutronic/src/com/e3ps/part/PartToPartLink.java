package com.e3ps.part;

import com.e3ps.change.EChangeOrder;
import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.ObjectToObjectLink;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "prev", type = WTPart.class, javaDoc = "현재 부품"),

		roleB = @GeneratedRole(name = "after", type = WTPart.class, javaDoc = "개정 부품"),

		foreignKeys = {

				@GeneratedForeignKey(name = "EcoPartHistoryLink",

						foreignKeyRole = @ForeignKeyRole(name = "eco", type = EChangeOrder.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "history", cardinality = Cardinality.ONE))

		}

)
public class PartToPartLink extends _PartToPartLink {

	static final long serialVersionUID = 1;

	public static PartToPartLink newPartToPartLink(WTPart prev, WTPart after) throws WTException {
		PartToPartLink instance = new PartToPartLink();
		instance.initialize(prev, after);
		return instance;
	}

}
