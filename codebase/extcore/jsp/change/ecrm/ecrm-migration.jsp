<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.folder.IteratedFolderMemberLink"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.folder.Folder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String loc = "/Default/문서/03. 2023년(이전문서)/01.공유폴더/06.설계변경 위험관리";
Folder f = FolderTaskLogic.getFolder(loc, WCUtil.getWTContainerRef());

QuerySpec query = new QuerySpec();
int idx = query.appendClassList(WTDocument.class, true);
int idx_m = query.appendClassList(WTDocumentMaster.class, false);

query.setAdvancedQueryEnabled(true);
query.setDescendantQuery(false);

QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
		WTAttributeNameIfc.ID_NAME, idx, idx_m);

int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
SearchCondition fsc = new SearchCondition(fca, "=",
		new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
fsc.setOuterJoin(0);
query.appendWhere(fsc, new int[] { f_idx, idx });
query.appendAnd();
long fid = f.getPersistInfo().getObjectIdentifier().getId();
query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
		new int[] { f_idx });
query.appendAnd();

QuerySpecUtils.toLatest(query, idx, WTDocument.class);
QueryResult result = PersistenceHelper.manager.find(query);
out.println("="+result.size());
// while (result.hasMoreElements()) {
	
// }
%>