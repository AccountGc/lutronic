package com.e3ps.doc.etc.service;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentEOLink;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPartDescribeLink;
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
	
	/**
	 * 문서 링크 관계 확인 ( true : 연결, false : 미연결 )
	 */
	public boolean isConnect(String oid, Class<?> target) throws Exception {
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		return isConnect(doc, target);
	}

	/**
	 * 문서 링크 관계 확인 ( true : 연결, false : 미연결 )
	 */
	public boolean isConnect(WTDocument doc, Class<?> target) throws Exception {
		boolean isConnect = false;

		if (target.equals(DocumentEOLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "eo", DocumentEOLink.class);
			isConnect = qr.size() > 0;
		} else if (target.equals(DocumentECOLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "eco", DocumentECOLink.class);
			isConnect = qr.size() > 0;
//		} else if (target.equals(DocumentECPRLink.class)) {
//			QueryResult qr = PersistenceHelper.manager.navigate(doc, "ecpr", DocumentECPRLink.class);
//			isConnect = qr.size() > 0;
		} else if (target.equals(DocumentCRLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "cr", DocumentCRLink.class);
			isConnect = qr.size() > 0;
		} else if (target.equals(WTPartDescribeLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "describes", WTPartDescribeLink.class);
			isConnect = qr.size() > 0;
		}
		return isConnect;
	}

}
