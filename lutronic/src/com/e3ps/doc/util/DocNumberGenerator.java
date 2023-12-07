package com.e3ps.doc.util;

/**
 * 루트로닉 문서 자동 채번을 위한 클래스
 */
public class DocNumberGenerator {

	private final String REPORT = "REPORT";

	// 처음 분류 정하는 함수 호출
	public String auto(String number) throws Exception {
		String auto = "";

		// 1. 웹에서 받아온 번호로 분류 선정
		String docTyep = getDocType(number);

		return auto;
	}

	private String getDocType(String number) throws Exception {
		return null;
	}
}
