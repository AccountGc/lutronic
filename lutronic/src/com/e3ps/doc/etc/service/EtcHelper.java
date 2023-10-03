package com.e3ps.doc.etc.service;

import wt.services.ServiceFactory;

public class EtcHelper {

	public static final EtcService service = ServiceFactory.getService(EtcService.class);
	public static final EtcHelper manager = new EtcHelper();

	public static final String RA = "/Default/RA팀 문서관리/RA팀";
	public static final String PRODUCTION = "/Default/생산본부 문서관리/생산본부";
	public static final String PATHOLOGICAL = "/Default/병리연구 문서관리/병리연구";
	public static final String CLINICAL = "/Default/임상개발 문서관리/임상개발";
	public static final String COSMETIC = "/Default/화장품 문서관리/화장품";

	/**
	 * 기타 문서 타입별 위치
	 */
	public String toLocation(String type) throws Exception {
		if ("ra".equalsIgnoreCase(type)) {
			return RA;
		} else if ("production".equalsIgnoreCase(type)) {
			return PRODUCTION;
		} else if ("pathological".equalsIgnoreCase(type)) {
			return PATHOLOGICAL;
		} else if ("clinical".equalsIgnoreCase(type)) {
			return CLINICAL;
		} else if ("cosmetic".equalsIgnoreCase(type)) {
			return COSMETIC;
		}
		return null;
	}

}
