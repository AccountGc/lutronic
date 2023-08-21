<%@page import="wt.doc.DocumentType"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="com.e3ps.common.util.SequenceDao"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="com.e3ps.rohs.ROHSMaterial"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.folder.FolderEntry"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.inf.container.WTContainerHelper"%>
<%@page import="wt.inf.container.WTContainerRef"%>
<%@page import="wt.org.OrganizationServicesHelper"%>
<%@page import="wt.org.WTOrganization"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.doc.beans.DocumentData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%-- <%@include file="/extcore/jsp/common/aui/auigrid.jsp"%> --%>
<%
DocumentData docData = (DocumentData) request.getAttribute("docData");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
	<form id="form">
	<% if("금형문서".equals(docData.getDocumentType())){  %>
		<input type="hidden" name="location" id="location" value="/Default/금형문서">
		<input type="hidden" name="location" id="location"  value="documentName">
	<% } %>
	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button"  value="수정"  title="수정"  class="btnCRUD blue"  id="updateBtn" name="updateBtn" onclick="create('false');">
				<input type="button" value="초기화" title="초기화"  class="btnCRUD"  id="approveBtn" name="approveBtn" onclick="javascript:history.back();">
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 문서 정보
				</div>
			</td>
		</tr>
	</table>
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="*">
			<col width="150">
			<col width="*">
		</colgroup>
		<tr>
			<td class="lb" colspan="4">
				<%= docData.getNumber() %>[<%= docData.getName() %>]
			</td>
		</tr>
		
		<% if("금형문서".equals(docData.getDocumentType())){  %>
			<tr>
				<th class="req lb">문서분류</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/eSolution/folder/include_FolderSelect.do">
						<jsp:param value="/Default/Document" name="root"/>
						<jsp:param value="/Default<%= docData.getLocation() %>}" name="folder"/>
					</jsp:include>
				</td>
			</tr>
		<% } %>
		
		<tr>
			<% if("금형문서".equals(docData.getDocumentType())){  %>
				<th class="req lb">문서종류</th>
				<td>
					<input type="text"  name="documentName" id="documentName" class="width-200"  value="<%-- docData.getDocumentName(1) --%>"/>
					<div id="documentNameSearch" style="display: none; border: 1px solid black ; position: absolute; background-color: white; z-index: 1;">
						<ul id="documentNameUL" style="list-style-type: none; padding-left: 5px; text-align: left; ">
						</ul>
					</div>
				</td>
				<th class="lb">문서명</th>
				<td class="indent5"><input type="text" name="docName" id="docName" class="width-200" value="<%-- docData.getDocumentName(2) --%>"></td>
			<% } else{ %>
				<th class="req lb">문서명</th>
				<td class="indent5"><input type="text" name="docName" id="docName" class="width-200"  value="<%= docData.getName() %>"></td>
			<% } %>
		</tr>
		<tr>
			<th class="lb">문서설명</th>
			<td colspan="3" class="indent5"><textarea name="description" id="description" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="lb">수정사유</th>
			<td colspan="3" class="indent5"><textarea name="iterationNote" id="iterationNote" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="req lb">주 첨부파일</th>
			<td class="indent5" colspan="3">
				<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
					<jsp:param value="" name="oid" />
				</jsp:include>
			</td>
		</tr>
		<tr>
			<th class="lb">첨부파일</th>
			<td class="indent5" colspan="3">
				<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
					<jsp:param value="" name="oid" />
				</jsp:include>
			</td>
		</tr>
	</table>
	
	<!-- 속정 정보 -->
<%-- 	<jsp:include page="/eSolution/common/include_createAttributes.do"> --%>
<%-- 		<jsp:param value="doc" name="module"/> --%>
<%-- 		<jsp:param value="속성 정보" name="title"/> --%>
<%-- 		<jsp:param value="<%= docData.getOid() %>" name="oid"/> --%>
<%-- 	</jsp:include> --%>
	
	<!-- 	관련 품목 -->
	<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
		<jsp:param value="" name="oid" />
		<jsp:param value="create" name="mode" />
	</jsp:include>
	
	<!-- 	관련 문서 -->
	<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp">
		<jsp:param value="관련 문서" name="title"/>
		<jsp:param value="" name="oid" />
	</jsp:include>
	<script type="text/javascript">
	</script>
	</form>
</body>
</html>