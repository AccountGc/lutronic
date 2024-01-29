<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.eco.service.EcoHelper"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="com.e3ps.common.iba.IBAUtils"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="com.e3ps.change.EOCompletePartLink"%>
<%@page import="com.e3ps.org.service.OrgHelper"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.part.QuantityUnit"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EChangeOrder e = (EChangeOrder) CommonUtil.getObject("com.e3ps.change.EChangeOrder:208998227");

ArrayList<EOCompletePartLink> completeParts = EcoHelper.manager.completeParts(e);
ArrayList<EcoPartLink> ecoParts = EcoHelper.manager.ecoParts(e);


System.out.println("ecoParts="+ecoParts.size());

Timestamp s = DateUtil.convertDate("2023-01-25");
String today = s.toString().substring(0, 10);
for (EcoPartLink link : ecoParts) {
	EChangeOrder eco = link.getEco();
	WTPartMaster m = link.getPart();
	String v = link.getVersion();
	WTPart part = PartHelper.manager.getPart(m.getNumber(), v);
	if (part != null) {
		boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
		if (!isApproved) {
	LifeCycleHelper.service.setLifeCycleState(part, State.toState("APPROVED"));
	part = (WTPart) PersistenceHelper.manager.refresh(part);
		}
		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		if (epm != null) {

	isApproved = epm.getLifeCycleState().toString().equals("APPROVED");
	if (!isApproved) {
		LifeCycleHelper.service.setLifeCycleState(epm, State.toState("APPROVED"));
	}

	EPMDocument epm2d = PartHelper.manager.getEPMDocument2D(epm);
	if (epm2d != null) {
		epm2d = DrawingHelper.manager.latest((EPMDocumentMaster)epm2d.getMaster());
		isApproved = epm2d.getLifeCycleState().toString().equals("APPROVED");
		System.out.println("변경되는 2D = " + epm2d.getNumber() + ", 상태 = " +epm2d.getLifeCycleState().getDisplay());
	
		if (!isApproved) {
			LifeCycleHelper.service.setLifeCycleState(epm2d, State.toState("APPROVED"));
			IBAUtils.appendIBA(epm2d, "CHANGENO", eco.getEoNumber(), "s");
			IBAUtils.appendIBA(epm2d, "CHANGEDATE", today, "s");
		}
	}
		}
	}

	// 메카가 아닐경우에만 멀 하는데..???
	//	if(!ChangeUtil.isMeca(location)){
	//		IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_APR, approveName , "string");
	//		IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_CHK, checkerName , "string");
	//	}

	// EO,ECO시 누적으로 등록
	//	String changeNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_CHANGENO));
	//	String changeDate = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_CHANGEDATE));
}

// 완제품
System.out.println("completeParts="+completeParts.size());
for (EOCompletePartLink link : completeParts) {
	EChangeOrder eco = link.getEco();
	WTPartMaster m = link.getCompletePart();
	String v = link.getVersion();
	WTPart part = PartHelper.manager.getPart(m.getNumber(), v);
	if (part != null) {
		boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
		if (!isApproved) {
	LifeCycleHelper.service.setLifeCycleState(part, State.toState("APPROVED"));
	part = (WTPart) PersistenceHelper.manager.refresh(part);
		}

		// 최종승인일..
		// 		IBAUtils.appendIBA(part, "CHANGENO", eco.getEoNumber(), "s");
		// 		IBAUtils.appendIBA(part, "CHANGEDATE", today, "s");

		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		if (epm != null) {
	// 	IBAUtils.appendIBA(epm, "CHANGENO", eco.getEoNumber(), "s");
	// 	IBAUtils.appendIBA(epm, "CHANGEDATE", today, "s");

	EPMDocument epm2d = PartHelper.manager.getEPMDocument2D(epm);
	System.out.println("epm2d="+epm2d);
	if (epm2d != null) {
		isApproved = epm2d.getLifeCycleState().toString().equals("APPROVED");
		System.out.println("변경되는 2D = " + epm2d.getNumber() + ", 상태 = " +epm2d.getLifeCycleState().getDisplay());
		if (!isApproved) {
			LifeCycleHelper.service.setLifeCycleState(epm2d, State.toState("APPROVED"));
			IBAUtils.appendIBA(epm2d, "CHANGENO", eco.getEoNumber(), "s");
			IBAUtils.appendIBA(epm2d, "CHANGEDATE", today, "s");
		}
	}

		}
	}
}

out.println("종료!");
%>