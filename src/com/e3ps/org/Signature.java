package com.e3ps.org;

import com.e3ps.common.impl.OwnPersistable;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, OwnPersistable.class },

		properties = {

				@GeneratedProperty(name = "id", type = String.class) }

)
public class Signature extends _Signature {
	static final long serialVersionUID = 1;

	public static Signature newSignature() throws WTException {
		Signature instance = new Signature();
		instance.initialize();
		return instance;
	}
}
