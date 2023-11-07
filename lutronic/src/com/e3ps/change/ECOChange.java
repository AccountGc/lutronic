
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
				@GeneratedProperty(name = "eoNumber", type = String.class, javaDoc = "EO ??"),
				@GeneratedProperty(name = "eoName", type = String.class, javaDoc = "EO Name"),
				@GeneratedProperty(name = "model", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),
				@GeneratedProperty(name = "eoCommentA", type = String.class, javaDoc = "ECR : ECR ????ECO : ECO ????", constraints = @PropertyConstraints(upperLimit = 4000)),
				@GeneratedProperty(name = "eoCommentB", type = String.class, javaDoc = "ECR : ?? ????ECO : ?? ????", constraints = @PropertyConstraints(upperLimit = 4000)),
				@GeneratedProperty(name = "eoCommentC", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),
				@GeneratedProperty(name = "eoCommentD", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),
				@GeneratedProperty(name = "eoType", type = String.class),
				@GeneratedProperty(name = "eoCommentE", type = String.class),
				@GeneratedProperty(name = "eoApproveDate", type = String.class) })
public class ECOChange extends _ECOChange {

	static final long serialVersionUID = 1;

	public static ECOChange newECOChange() throws WTException {
		ECOChange instance = new ECOChange();
		instance.initialize();
		return instance;
	}
}
