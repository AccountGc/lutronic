package com.e3ps.doc;

import com.ptc.windchill.annotations.metadata.GenAsEnumeratedType;

@GenAsEnumeratedType
public class DocumentClassType extends _DocumentClassType {
	public static final DocumentClassType DEV = toDocumentClassType("DEV"); // 개발문서
	public static final DocumentClassType CHANGE = toDocumentClassType("CHANGE"); // 설계변경
	public static final DocumentClassType INSTRUCTION = toDocumentClassType("INSTRUCTION"); // 지침서
	public static final DocumentClassType REPORT = toDocumentClassType("REPORT"); // 보고서
	public static final DocumentClassType VAILDATION = toDocumentClassType("VAILDATION"); // 벨리데이션문서
	public static final DocumentClassType MEETING = toDocumentClassType("MEETING"); // 회의록
	public static final DocumentClassType RND = toDocumentClassType("RND"); // 연구소문서
	public static final DocumentClassType ELEC = toDocumentClassType("ELEC"); // 전자매체
	public static final DocumentClassType SOURCE = toDocumentClassType("SOURCE"); // 개발소스
	public static final DocumentClassType APPROVAL = toDocumentClassType("APPROVAL"); // 승인원
	public static final DocumentClassType ETC = toDocumentClassType("ETC"); // 기타
}
