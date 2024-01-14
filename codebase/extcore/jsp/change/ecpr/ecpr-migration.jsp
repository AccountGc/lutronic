<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="wt.folder.FolderEntry"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.lifecycle.LifeCycleTemplate"%>
<%@page import="com.e3ps.change.ECPRRequest"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="com.e3ps.change.EChangeRequest"%>
<%@page import="wt.query.QuerySpec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
delete();

QuerySpec query = new QuerySpec();
int idx = query.appendClassList(EChangeRequest.class, true);
SearchCondition sc = new SearchCondition(EChangeRequest.class, EChangeRequest.EO_NUMBER, "LIKE", "ECPR%");
query.appendWhere(sc, new int[] { idx });
QueryResult qr = PersistenceHelper.manager.find(query);
while (qr.hasMoreElements()) {
	Object[] obj = (Object[]) qr.nextElement();
	EChangeRequest e = (EChangeRequest) obj[0];
	out.println(e.getEoNumber() + "<br>");

	ECPRRequest ecpr = ECPRRequest.newECPRRequest();
	ecpr.setEoNumber(e.getEoNumber());
	ecpr.setEoName(e.getEoName());
	ecpr.setCreateDepart(e.getCreateDepart());
	ecpr.setCreateDate(e.getCreateDate());
	ecpr.setWriter(e.getWriter());
	ecpr.setChangeSection(e.getChangeSection());
	ecpr.setModel(e.getModel());
	ecpr.setContents(e.getContents());
	ecpr.setEoCommentA(e.getEoCommentA());
	ecpr.setEoCommentB(e.getEoCommentB());
	ecpr.setEoCommentC(e.getEoCommentC());
	ecpr.setEoCommentD(e.getEoCommentD());
	ecpr.setEoCommentE(e.getEoCommentE());
	ecpr.setEoApproveDate(e.getApproveDate());
	ecpr.setEoType(e.getEoType());
	ecpr.setOwnership(e.getOwnership());
	ecpr.setPeriod("PR004");
	ecpr.setIsNew(false);
	String location = "/Default/설계변경/ECPR";
	String lifecycle = "LC_Default";

	Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
	FolderHelper.assignLocation((FolderEntry) ecpr, folder);
	// 문서 lifeCycle 설정
	LifeCycleHelper.setLifeCycle(ecpr,
	LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
	ecpr = (ECPRRequest) PersistenceHelper.manager.save(ecpr);

	PersistenceHelper.manager.save(ecpr);
	ecpr = (ECPRRequest) PersistenceHelper.manager.refresh(ecpr);

	LifeCycleHelper.service.setLifeCycleState(ecpr, State.toState(e.getLifeCycleState().toString()));

	// 첨부 파일 
	ContentHelper.service.copyContent(e, ecpr);
}
%>

<%!private void delete() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ECPRRequest.class, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			ECPRRequest req = (ECPRRequest) obj[0];
			PersistenceHelper.manager.delete(req);
			System.out.println("삭제 = " + req.getEoNumber());
		}
	}%>