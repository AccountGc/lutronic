package com.e3ps.doc;

import com.ptc.windchill.annotations.metadata.GenAsEnumeratedType;

@GenAsEnumeratedType
public class DocumentClassType extends _DocumentClassType {

	public static final DocumentClassType DEV = toDocumentClassType("DEV"); // 개발문서
	public static final DocumentClassType CHANGE = toDocumentClassType("CHANGE"); // 설계변경
	public static final DocumentClassType INSTRUCTION = toDocumentClassType("INSTRUCTION"); // 지침서
	public static final DocumentClassType VAILDATION = toDocumentClassType("VAILDATION"); // 벨리데이션문서
	}
}
