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
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<NumberCode> documentTemplateTypeList = (ArrayList<NumberCode>) request.getAttribute("documentTemplateTypeList");
%>
<!-- AUIGrid -->
<%-- <%@include file="/extcore/jsp/common/aui/auigrid.jsp"%> --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
<script type="text/javascript" src="/Windchill/extcore/smarteditor2/js/HuskyEZCreator.js" charset="utf-8"></script>
</head>
<body>
<form id="form">
<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT %>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 문서 템플릿 등록
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
		<th class="req lb">문서양식 번호</th>
		<td class="indent5"><input type="text" name="number" id="number" class="width-200"></td>
		<th class="lb">문서양식 제목</th>
		<td class="indent5"><input type="text" name="name" id="name" class="width-200"></td>
	</tr>
	<tr>
		<th class="req lb">문서양식 유형</th>
		<td class="indent5" colspan="3">
			<select name="documentTemplateType" id="documentTemplateType" class="width-200" tabindex="11">
				<option value="">선택</option>
				<%
				for (NumberCode documentTemplateType : documentTemplateTypeList) {
				%>
				<option value="<%=documentTemplateType.getPersistInfo().getObjectIdentifier().getStringValue() %>"><%=documentTemplateType.getName()%></option>
<%-- 				<option value="<%=documentTemplateType.getName() %>"><%=documentTemplateType.getName()%></option> --%>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td colspan="3" class="indent5">
			<textarea name="description" id="description" rows="15">
			</textarea>
		</td>
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="center">
			<input type="button"  value="등록"  title="등록"  class="btnCRUD blue"  id="createBtn" name="createBtn" onclick="create('false');">
			<input type="button" value="초기화" title="초기화"  class="btnCRUD"  id="resetBtn" name="resetBtn">
			<input type="button" value="목록" title="목록"  class="btnCRUD"  id="listBtn" name="listBtn">
		</td>
	</tr>
</table>
</form>
<script type="text/javascript">
	// 텍스트 편집기
	var oEditors = [];
	nhn.husky.EZCreator.createInIFrame({
	    oAppRef: oEditors,
	    elPlaceHolder: "description",  //textarea ID 입력
	    sSkinURI: "/Windchill/extcore/smarteditor2/SmartEditor2Skin.html",  //martEditor2Skin.html 경로 입력
	    fCreator: "createSEditor2",
	    htParams : { 
	    	// 툴바 사용 여부 (true:사용/ false:사용하지 않음) 
	        bUseToolbar : true, 
			// 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음) 
			bUseVerticalResizer : false, 
			// 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음) 
			bUseModeChanger : false 
	    }
	});
	
	document.addEventListener("DOMContentLoaded", function() {
		selectbox("dcumentTemplateType");
	});
	
	function create(isSelf) {
		const number = document.getElementById("number").value;
		const name = document.getElementById("name").value;
		const documentTemplateType = document.getElementById("documentTemplateType").value;
		oEditors.getById["description"].exec("UPDATE_CONTENTS_FIELD", []);
	    const description = document.getElementById("description").value;
	    
		if(isEmpty(number)){
			alert("문서양식 번호를 입력하세요.");
			return;					
		}
		if(isEmpty(name)){
			alert("문서양식 제목을 입력하세요.");
			return;					
		}
		if(isEmpty(documentTemplateType)){
			alert("문서양식 유형을 입력하세요.");
			return;					
		}
		 if(description == '<p>&nbsp;</p>') { //비어있는 경우
	        alert("내용을 입력해주세요.")
	        oEditors.getById["editorTxt"].exec("FOCUS")
	        return;
	    }
		
		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		const params = new Object();
		const url = getCallUrl("/doc/template-create");
// 		params.files = fileObject[0];
		params.number = number;
		params.name = name;
		params.documentTemplateType = documentTemplateType;
		params.description = description;
        
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				location.href=getCallUrl("/doc/template-list");
			} else {
			}
		});
	};
</script>
</body>
</html>