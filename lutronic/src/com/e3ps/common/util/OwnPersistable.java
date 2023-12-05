package com.e3ps.common.util;

import wt.org.WTPrincipalReference;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

@GenAsPersistable(

		properties = {

				@GeneratedProperty(name = "owner", type = WTPrincipalReference.class, constraints = @PropertyConstraints(required = true))

		}

)
public interface OwnPersistable extends _OwnPersistable {

}
