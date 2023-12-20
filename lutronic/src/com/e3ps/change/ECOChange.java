
package com.e3ps.change;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import wt.content.ContentHolder;
import wt.enterprise.Managed;
import wt.inf.container.WTContained;
import wt.util.WTException;

@GenAsPersistable(superClass = Managed.class, interfaces = { WTContained.class,
		ContentHolder.class }, serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {

				@GeneratedProperty(name = "eoNumber", type = String.class, javaDoc = "EO/ECO 번호"),

				@GeneratedProperty(name = "eoName", type = String.class, javaDoc = "EO/ECO 명"),

				@GeneratedProperty(name = "model", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "eoCommentA", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "eoCommentB", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "eoCommentC", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "eoCommentD", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "eoType", type = String.class),

				@GeneratedProperty(name = "eoCommentE", type = String.class),

				@GeneratedProperty(name = "eoApproveDate", type = String.class),

				@GeneratedProperty(name = "isNew", type = Boolean.class)

}

)
public class ECOChange extends _ECOChange {

	static final long serialVersionUID = 1;

	public static ECOChange newECOChange() throws WTException {
		ECOChange instance = new ECOChange();
		instance.initialize();
		return instance;
	}
}
