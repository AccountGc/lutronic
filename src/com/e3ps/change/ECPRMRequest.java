package com.e3ps.change;

import com.e3ps.common.util.OwnPersistable;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;

import wt.content.ContentHolder;
import wt.inf.container.WTContained;
import wt.util.WTException;

@GenAsPersistable(superClass = ECOChange.class, interfaces = { OwnPersistable.class, WTContained.class,
		ContentHolder.class }

)
public class ECPRMRequest extends _ECPRMRequest {
	static final long serialVersionUID = 1;

	public static ECPRMRequest newECPRMRequest() throws WTException {
		ECPRMRequest instance = new ECPRMRequest();
		instance.initialize();
		return instance;
	}
}
