<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.beans.DocumentData"%>
<%-- <%@page import="e3ps.project.dto.ProjectDTO"%> --%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
DocumentData data = (DocumentData) request.getAttribute("docData");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<% if(data.getDocumentType().equals("금형문서")){ %>
					금형 상세보기
				<% }else{ %>
					문서 상세보기
				<% } %>
			</div>
		</td>
		<td class="right">
			<%
			if(data.isLatest()){
			%>
				<%
				if(data.getState().equals("APPROVED")){
				%>
					<input type="button" value="개정" title="개정" id="reviseBtn">
				<%	
				}
				%>
				<!-- 회수 권한 승인중 && 소유자 || 관리자 -->
				<%
				if(data.isWithDraw()){
				%>
					<input type="button" value="결재회수" title="결재회수" id="withDrawBtn">
				<%	
				}
				%>
				<%
				if(data.getState().equals("INWORK") || data.getState().equals("BATCHAPPROVAL") || data.getState().equals("REWORK")){
				%>
					<input type="button" value="수정" title="수정" class="blue" id="updateBtn">
					<input type="button" value="삭제" title="삭제" class="red" id="deleteBtn">
				<%	
				}
				%>
			<%
			}else{
			%>
				<input type="button" value="최신Rev." title="최신Rev." id="lastestBtn">
			<%	
			}
			%>
			<input type="button" value="Rev.이력" title="Rev.이력" id="versionBtn">
			<input type="button" value="다운로드이력" title="다운로드이력" id="downloadBtn">
			<input type="button" value="결재이력" title="결재이력" id="approveBtn">
			<input type="button" value="닫기" title="닫기" class="gray" id="closeBtn" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="view-table">
	<colgroup>
		<col width="150">
		<col width="350">
		<col width="150">
		<col width="350">
	</colgroup>
	<tr>
		<% if(data.getDocumentType().equals("금형문서")){ %>
		<th class="lb">금형번호</th>
		<% }else{ %>
		<th class="lb">문서번호</th>
		<% } %>
		<td class="indent5"><%=data.getNumber()%></td>
		<% if(data.getDocumentType().equals("금형문서")){ %>
		<th class="lb">금형분류</th>
		<% }else{ %>
		<th class="lb">문서분류</th>
		<% } %>
		<td class="indent5"><%=data.getLocation()%></td>
	</tr>
	<tr>
		<th class="lb">상태</th>
		<td class="indent5"><%=data.getState()%></td>
		<th>Rev.</th>
		<td class="indent5">
		</td>
	</tr>
	<tr>
		<th class="lb">등록자</th>
		<td class="indent5"><%=data.getCreator()%></td>
		<th class="lb">수정자</th>
		<td class="indent5"><%=data.getModifier()%></td>
	</tr>
	<tr>
		<th class="lb">등록일</th>
		<td class="indent5"><%=data.getCreateDate()%></td>
		<th class="lb">수정일</th>
		<td class="indent5"><%=data.getModifyDate()%></td>
	</tr>
	<tr>
		<th class="lb">문서유형</th>
		<td class="indent5"><%=data.getDocumentType()%></td>
		<th class="lb">결재방식</th>
		<td class="indent5"><%=data.getApprovalType()%></td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td colspan="3" class="indent5">
			<textarea rows="5" readonly="readonly"><%=data.getDescription() != null ? data.getDescription() : ""%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">주 첨부파일</th>
		<td colspan="3" class="indent5">
<%-- 			<jsp:include page="/eSolution/content/includeAttachFileView"> --%>
<%-- 				<jsp:param value="p" name="type"/> --%>
<%-- 				<jsp:param value="<%= data.getOid() %>" name="oid"/> --%>
<%-- 			</jsp:include> --%>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일
			<br>
			<input type="button" value="일괄 다운" title="일괄 다운"  onclick="">
		</th>
		<td colspan="3" class="indent5">
<%-- 			<jsp:include page="/eSolution/content/includeAttachFileView"> --%>
<%-- 				<jsp:param value="<%= data.getOid() %>" name="oid"/> --%>
<%-- 			</jsp:include> --%>
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 속성
			</div>
		</td>
	</tr>
</table>
<%if(data.getDocumentType().equals("금형문서")){%>
	<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
		<jsp:param value="<%=data.getOid()%>" name="oid" />
		<jsp:param value="<%=data%>" name="docData" />
		<jsp:param value="mold" name="module"/>
	</jsp:include>
<%}else{ %>
	<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
		<jsp:param value="<%=data.getOid()%>" name="oid" />
		<jsp:param value="<%=data%>" name="docData" />
		<jsp:param value="doc" name="module"/>
	</jsp:include>
<%} %>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">관련 품목</a>
		</li>
		<li>
			<a href="#tabs-2">관련 개발업무</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/part/include_viewPart.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
			<jsp:param value="관련 품목" name="title" />
		</jsp:include>
	</div>
	<div id="tabs-2">
		<!-- 관련 개발업무 -->
		<jsp:include page="/extcore/jsp/development/include_viewDevelopment.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
		</jsp:include>
	</div>
</div>
	
<script type="text/javascript">
	//수정
	$("#updateBtn").click(function () {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/update?oid=" + oid + "&mode=" + mode);
		openLayer();
		document.location.href = url;
	})

	//삭제
	$("#deleteBtn").click(function () {
	
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
	
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/delete?oid=" + oid);
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
	//				opener.loadGridData();
				self.close();
			}
		}, "GET");
	})
	
	//개정
	$("#reviseBtn").click(function () {
		var url	= getURLString("doc", "reviseDocumentPopup", "do") + "?oid="+$("#oid").val();
		openOtherName(url,"reviseDocumentPopup","350","200","status=no,scrollbars=yes,resizable=yes");
	})
	
	//버전이력
	$("#versionBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "versionHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	//다운로드 이력
	$("#downloadBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "downloadHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	//결재이력
	$("#approveBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("groupware", "historyWork", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	//최신버전
	$("#lastestBtn").click(function() {
		var oid = this.value;
		openView(oid);
	})
	
	//결재 회수
	$("#withDrawBtn").click(function() {
		
		var url	= getURLString("common", "withDrawPopup", "do") + "?oid="+$("#oid").val();
		openOtherName(url,"withDrawBtn","400","220","status=no,scrollbars=yes,resizable=yes");
	})
	
	//일괄 다운로드
	$("#batchSecondaryDown").click(function() {
		var form = $("form[name=documentViewForm]").serialize();
		var url	= getURLString("common", "batchSecondaryDown", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			
			error:function(data){
				var msg = "데이터 검색오류";
				alert(msg);
			},
			
			success:function(data){
				console.log(data.message);
				if(data.result) {
					location.href = '/Windchill/jsp/common/content/FileDownload.jsp?fileName='+data.message+'&originFileName='+data.message;
				}else {
					alert(data.message);
				}
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
	})
	
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated1 = AUIGrid.isCreated(partGridID);
					if (isCreated1) {
						AUIGrid.resize(partGridID);
					} else {
						createAUIGrid1(columnPart);
					}
					break;
				case "tabs-2":
					const isCreated2 = AUIGrid.isCreated(devGridID);
					if (isCreated2) {
						AUIGrid.resize(devGridID);
					} else {
						createAUIGrid4(columnDev);
					}
					break;	
				}
			}
		});
		createAUIGrid1(columnPart);
		AUIGrid.resize(partGridID);
		createAUIGrid4(columnDev);
		AUIGrid.resize(devGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(partGridID);
		AUIGrid.resize(devGridID);
	});
	
</script>