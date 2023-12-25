package com.e3ps.system;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "aennr8", type = String.class, javaDoc = "EO/ECO 번호"),

				@GeneratedProperty(name = "matnr", type = String.class, javaDoc = "자재번호"),

				@GeneratedProperty(name = "maktx", type = String.class, javaDoc = "자재명"),

				@GeneratedProperty(name = "meins", type = String.class, javaDoc = "단위"),

				@GeneratedProperty(name = "zspec", type = String.class, javaDoc = "사양"),

				@GeneratedProperty(name = "zmodel", type = String.class, javaDoc = "프로젝트 코드"),

				@GeneratedProperty(name = "zprodm", type = String.class, javaDoc = "가공방법"),

				@GeneratedProperty(name = "zdept", type = String.class, javaDoc = "부서"),

				@GeneratedProperty(name = "zdwgno", type = String.class, javaDoc = "도면번호"),

				@GeneratedProperty(name = "zeivr", type = String.class, javaDoc = "버전"),

				@GeneratedProperty(name = "zprepo", type = String.class, javaDoc = "선구매여부"),

				@GeneratedProperty(name = "brgew", type = String.class, javaDoc = "중량"),

				@GeneratedProperty(name = "gewei", type = String.class, javaDoc = "??"),

				@GeneratedProperty(name = "zmatlt", type = String.class, javaDoc = "재질"),

				@GeneratedProperty(name = "zpostp", type = String.class, javaDoc = "후처리"),

				@GeneratedProperty(name = "zdevnd", type = String.class, javaDoc = "개발공급업체"),

				@GeneratedProperty(name = "sendResult", type = Boolean.class, javaDoc = "성공여부")

		}

)
public class SAPInterfacePartLogger extends _SAPInterfacePartLogger {
	static final long serialVersionUID = 1;

	public static SAPInterfacePartLogger newSAPInterfacePartLogger() throws WTException {
		SAPInterfacePartLogger instance = new SAPInterfacePartLogger();
		instance.initialize();
		return instance;
	}
}
