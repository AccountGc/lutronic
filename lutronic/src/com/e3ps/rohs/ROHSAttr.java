package com.e3ps.rohs;

import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

	@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {
			@GeneratedProperty(name="publicationDate", type=String.class),
		},

		foreignKeys = {
				@GeneratedForeignKey(name="HolderAppliationLink", myRoleIsRoleA=false,
				      foreignKeyRole=@ForeignKeyRole(name="app", type=wt.content.ApplicationData.class,
				         constraints=@PropertyConstraints(required=true)),
				      myRole=@MyRole(name="holder")),
		}

	)
	public class ROHSAttr extends _ROHSAttr {
		static final long serialVersionUID = 1;
		
		public static ROHSAttr newROHSAttr() throws WTException {
			ROHSAttr instance = new ROHSAttr();
			instance.initialize();
			return instance;
		}
	
	}