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
String oid = (String) request.getAttribute("oid");
String module = (String) request.getAttribute("module");
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
	<form name="documentModifyForm" id="documentModifyForm" method="post" enctype="multipart/form-data">
	<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
	<input type="hidden" name="module" id="module" value="<%= module %>" />
	<% if("금형문서".equals(docData.getDocumentType())){  %>
		<input type="hidden" name="location" id="location" value="/Default/금형문서">
		<input type="hidden" name="documentName" id="documentName"  value="documentName">
	<% } %>
	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button" value="개정" title="개정" id="reviseBtn" >
				<input type="button"  value="수정"  title="수정"  class="btnCRUD blue"  id="updateBtn" name="updateBtn" >
				<input type="button" value="이전페이지" title="이전페이지" class="red" id="approveBtn" onclick="javascript:history.back();">
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
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="*">
		</colgroup>
		<tr>
			<th class="req lb">결재방식</th>
			<td>&nbsp;
				<div class="pretty p-switch">
					<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default" checked="checked">
					<div class="state p-success">
						<label> <b>기본결재</b>
						</label>
					</div>
				</div>&nbsp;
				<div class="pretty p-switch">
					<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default_NonWF">
					<div class="state p-success">
						<label> <b>일괄결재</b>
						</label>
					</div>
				</div>
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
function revise(){
	if(confirm("개정하시겠습니까?")){
		const oid = document.getElementById("oid").value;
		const module = document.getElementById("module").value;
		const lifecycle = document.getElementById("lifecycle").value;
		const url = getCallUrl("/doc/document-revise");
		const params = new Object();
		params.oid = oid;
		params.module = module;
		params.lifecycle = lifecycle;
		call(url, params, function(data) {
			if (data.result) {
				alert(data.msg + "개정 성공하였습니다.");
				if(module == 'rohs') {
					opener.location.href = getCallUrl("/rohs/view?oid="+ oid);
				}else {
					opener.location.href = getCallUrl("/doc/view?oid="+ oid);
				}
				self.close();
			} else {
				alert("개정에 실패하였습니다.  \n" + data.msg);
			}
		});
	}
}

$(function() {
	<%----------------------------------------------------------
	*                      수정버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function() {
// 		if($.trim($("input[name='location']").val()) == '/Default/Document') {
// 			alert("문서분류을(를) 선택하세요.");
// 			return;
// 		}
		
// 		if($.trim($("#documentName").val()) == '') {
// 			alert("문서종류을(를) 입력하세요.");
// 			$("#documentName").focus();
// 			return;
// 		}
		
// 		if($("#documentType").val() == '') {
// 			alert("문서분류'을(를) 선택하세요.");
// 			$("#documentType").focus();
// 			return;
// 		}
		
// 		if(!textAreaLengthCheckId('description','2000','설명')){
// 			return;
// 		}
		
// 		if($("#preseration").val() == '') {
// 			alert("보존기간을(를) 선택하세요.");
// 			$("#preseration").focus();
// 			return;
// 		}
		
// 		if($("#moldtype").val() == '') {
// 			alert("금형타입을(를) 선택하세요.");
// 			$("#moldtype").focus();
// 			return;
// 		}
		
// 		if($("#PRIMARY").length == 0 && $("#PRIMARY_delocIds").length == 0) {
// 			alert("주 첨부파일을(를) 선택하세요.");
// 			return;
// 		}
		
		if (confirm("수정하시겠습니까?")){
			
			const oid = document.getElementById("oid").value;
// 			const location = document.getElementById("location").value;
			const docName = document.getElementById("docName");
			const lifecycle = document.getElementById("lifecycle").value;
			const description = document.getElementById("description").value;
			const iterationNote = document.getElementById("iterationNote").value;
			const primarys = toArray("primarys");
			
			const params = new Object();
			const url = getCallUrl("/doc/updateDocumentAction");
			params.oid = oid;
// 			params.location = location;
			params.docName = docName.value;
			params.lifecycle = lifecycle;
			params.description = description;
			params.iterationNote = iterationNote;
			
			params.primarys = primarys;
			
			call(url, params, function(data) {
				if (data.result) {
					alert("수정 성공하였습니다.");
					location.href = getCallUrl("/doc/view?oid=" + data.oid);
				} else {
					alert("수정 실패하였습니다. \n" + data.msg);
				}
			});
		}
	})
	$("input[name=documentName]").keyup(function (event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		if((charCode == 38 || charCode == 40) ) {
			if(!$( "#"+this.id+"Search" ).is( ":hidden" )){
				var isAdd = false;
				if(charCode == 38){
					isAdd = true;
				}
				moveDocumentNameFocus(this.id, isAdd);
			}
		} else if(charCode == 13 || charCode == 27){
			$("#" + this.id + "Search").hide();
		} else {
			autoSearchDocumentName(this.id, this.value);
		}
		
	})
	
	$("input[name=documentName]").focusout(function () {
		$("#" + this.id + "Search").hide();
	})
})

<%----------------------------------------------------------
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.moveDocumentNameFocus = function(id,isAdd) {
	var removeCount = 0;
	var addCount = 0;
	var l = $("#" + id + "UL li").length;
	for(var i=0; i<l; i++){
		var cls = $("#" + id + "UL li").eq(i).attr('class');
		if(cls == 'hover') {
			$("#" + id + "UL li").eq(i).removeClass("hover");
			removeCount = i;
			if(isAdd){
				addCount = (i-1);
			}else if(!isAdd) {
				addCount = (i+1);
			}
			break;
		}
	}
	if(addCount == l) {
		addCount = 0;
	}
	$("#" + id + "UL li").eq(addCount).addClass("hover");
	$("#" + id).val($("#" + id + "UL li").eq(addCount).text());
}

<%----------------------------------------------------------
*                      품목명 입력시 이름 검색
----------------------------------------------------------%>
window.autoSearchDocumentName = function(id, value) {
	var codeType = id.toUpperCase();
	var data = common_autoSearchName(codeType, value);
	addSearchList(id, eval(data.responseText), false);
}

<%----------------------------------------------------------
*                      품목명 입력시 데이터 리스트 보여주기
----------------------------------------------------------%>
window.addSearchList = function(id, data, isRemove) {
	$("#" + id + "UL li").remove();
	if(isRemove) {
		$("#" + this.id + "Search").hide();
	}else {
		if(data.length > 0) {
			$("#" + id + "Search").show();
			for(var i=0; i<data.length; i++) {
				$("#" + id + "UL").append("<li title='" + id + "' class=''>" + data[i].name);
			}
		}else {
			$("#" + id + "Search").hide();
		}
	}
}

<%----------------------------------------------------------
*                      품목명 데이터 마우스 올렸을때
----------------------------------------------------------%>
$(document).on("mouseover", 'div > ul > li', function() {
	var partName = $(this).attr("title");
	$(this).addClass("hover");
	$("#" + partName).val($(this).text());
})

<%----------------------------------------------------------
*                      품목명 데이터 마우스 뺄때
----------------------------------------------------------%>
$(document).on("mouseout", 'div > ul > li', function() {
	$(this).removeClass("hover");
})
</script>
	</form>
</body>
</html>