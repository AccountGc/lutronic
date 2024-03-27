<%@page import="com.e3ps.sap.service.SAPHelper"%>
<%@page import="com.e3ps.change.util.EChangeUtils"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="wt.epm.EPMDocumentHelper"%>
<%@page import="wt.fc.IdentityHelper"%>
<%@page import="wt.epm.EPMDocumentMasterIdentity"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="com.ptc.wpcfg.deliverables.library.EPMDocumentMaker"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="com.e3ps.common.util.ZipUtil"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="java.io.InputStream"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
//http://pdm.lutronic.com/Windchill/plm/eco/view?oid=com.e3ps.change.EChangeOrder:239335886
String oid = "com.e3ps.change.EChangeOrder:239335886";
EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
System.out.println("정전개 대상 몇번인가 = " + qr.size());
while (qr.hasMoreElements()) {
	EcoPartLink link = (EcoPartLink) qr.nextElement();
	WTPartMaster master = link.getPart();
	String version = link.getVersion();
	WTPart target = PartHelper.manager.getPart(master.getNumber(), version);
	boolean isPast = link.getPast();

	WTPart next_part = null;
	WTPart pre_part = null;

	out.println(isPast);
	if (!isPast) { // 과거 아닐경우 과거 데이터는 어떻게 할지..???
		boolean isRight = link.getRightPart();
		boolean isLeft = link.getLeftPart();
		// 오른쪽이면 다음 버전 품목을 전송해야한다.. 이게 맞는듯
		if (isLeft) {
	// 왼쪽이면 승인됨 데이터..그니깐 개정후 데이터를 보낸다 근데 변경점이 없지만 PDM상에서 버전은 올라간 상태
	next_part = (WTPart) EChangeUtils.manager.getNext(target);
	pre_part = target;
		} else if (isRight) {
	// 오른쪽 데이터면 애시당초 바귄 대상 품번 그대로 넣어준다..
	next_part = target;
	pre_part = SAPHelper.manager.getPre(target, eco);
		}
	}

	out.println("next_part = " + next_part + ", pre_part = " + pre_part + "<br>");

}
%>