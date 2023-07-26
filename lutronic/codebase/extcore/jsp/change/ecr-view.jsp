<%@page import="com.e3ps.change.beans.ECRData"%>
<%@page import="com.e3ps.change.beans.ECOData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
ECRData dto = (ECRData) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CR/ECPR 상세보기
			</div>
		</td>
		<td class="right">
			<!-- 회수 권한 승인중 && 소유자 || 관리자 -->
			<%
			if(dto.isWithDraw()){
			%>
				<input type="button" value="결재회수" title="결재회수" class="" id="withDrawBtn">
			<%	
			}
			%>
			<input type="button" value="수정" title="수정" class="blue" id="updateBtn">
			<input type="button" value="삭제" title="삭제" class="red" id="deleteBtn">
			<input type="button" value="결재이력" title="결재이력" class="" id="approveBtn">
			<input type="button" value="다운로드이력" title="다운로드이력" class="" id="downloadBtn">
			<input type="button" value="닫기" title="닫기" class="gray" id="closeBtn" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="view-table">
	<colgroup>
		<col width="13%">
		<col width="37%">
		<col width="13%">
		<col width="37%">
	</colgroup>
	<tr>
		<th>CR/ECPR 제목</th>
		<td colspan="3"><%=dto.getEoName()%></td>
	</tr>
	<tr>
		<th>CR/ECPR 번호</th>
		<td><%=dto.getEoNumber()%></td>
		<th>상태</th>
		<td><%=dto.getState()%></td>
	</tr>
	<tr>
		<th>등록자</th>
		<td colspan="3"><%=dto.getCreator()%></td>
	</tr>
	<tr>
		<th>등록일</th>
		<td><%=dto.getCreateDate()%></td>
		<th>수정일</th>
		<td><%=dto.getModifyDate()%></td>
	</tr>
	<tr>
		<th>작성자</th>
		<td><%=dto.getWriter()%></td>
		<th>작성부서</th>
		<td><%=dto.getCreateDepart()%></td>
	</tr>
	<tr>
		<th>작성일</th>
		<td><%=dto.getWriteDate()%></td>
		<th>승인일</th>
		<td><%=dto.getApproveDate()%></td>
	</tr>
	<tr>
		<th>제안자</th>
		<td><%=dto.getProposer()%></td>
		<th>변경부분</th>
		<td><%=dto.getChangeDisplay()%></td>
	</tr>
	<tr>
		<th>제품명</th>
		<td colspan="3"><%=dto.getModelDisplay()%></td>
	</tr>
	<tr>
		<th>변경사유</th>
		<td colspan="3"><%=dto.getEoCommentA()%></td>
	</tr>
	<tr>
		<th>변경사항</th>
		<td colspan="3"><%=dto.getEoCommentB()%></td>
	</tr>
	<tr>
		<th>참고사항</th>
		<td colspan="3"><%=dto.getEoCommentC()%></td>
	</tr>
	<tr>
		<th>주 첨부파일</th>
		<td colspan="3"></td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td colspan="3"></td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">관련 ECO</a>
		</li>
		<li>
			<a href="#tabs-2">관련 ECR</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 완제품 품목 -->
		<jsp:include page="/extcore/jsp/change/include_view_ecr_eco.jsp" flush="false">
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-2">
		<!-- 관련 ECR -->
		<jsp:include page="/extcore/jsp/change/include_view_ecr_ecr.jsp" flush="false">
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	$("#updateBtn").click(function () {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/update?oid=" + oid + "&mode=" + mode);
		openLayer();
		document.location.href = url;
	})

	$("#deleteBtn").click(function () {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/delete?oid=" + oid);
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
//		 				opener.loadGridData();
				self.close();
			}
		}, "GET");
	})
	
	$("#viewECA").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("changeECA" , "viewECA", "do") + "?oid="+oid;
		openWindow(url, "eca", 1000, 600);
	})
	
	$("#approveBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("groupware", "historyWork", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	$("#downloadBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "downloadHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	$("#erpSend").click(function() {
		if (!confirm("ERP 전송 하시게습니까?")){
			return;
		}
		
		var form = $("form[name=viewECOForm]").serialize();
		var url	= getURLString("erp", "erpSendAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("ERP 전송 오류");
			},
			success:function(data){
				alert(data.message);
				location.reload();
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
	})
	
	$('#excelDown').click(function() {
		var url = getURLString("changeECO", "excelDown", "do");
		console.log(this.value);
		console.log(url);
		$.ajax({
			type:"POST",
			url: url,
			data:{
				oid : $('#oid').val(),
				eoType : this.value
			},
			dataType:"json",
			async: false,
			cache: false,
			success:function(data){
				console.log(data);
				if(data.result) {
					location.href = '/Windchill/jsp/common/content/FileDownload.jsp?fileName='+data.message+'&originFileName='+data.message;
				}else {
					alert(data.message);
				}
			}
		});
	})
	
	$("#batchSecondaryDown").click(function() {
		var form = $("form[name=viewECOForm]").serialize();
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
					const isCreated1 = AUIGrid.isCreated(ecoGridID);
					if (isCreated1) {
						AUIGrid.resize(ecoGridID);
					} else {
						createAUIGrid1(columnEco);
					}
					break;
				case "tabs-2":
					const isCreated2 = AUIGrid.isCreated(ecrGridID);
					if (isCreated2) {
						AUIGrid.resize(ecrGridID);
					} else {
						createAUIGrid2(columnEcr);
					}
					break;
				}
			}
		});
		createAUIGrid1(columnEco);
		AUIGrid.resize(ecoGridID);
		createAUIGrid2(columnEcr);
		AUIGrid.resize(ecrGridID);
// 		createAUIGrid2(columnComplePart);
// 		AUIGrid.resize(complePartGridID);
// 		createAUIGrid3(columnChangePart);
// 		AUIGrid.resize(changePartGridID);
// 		createAUIGrid4(columnEcr);
// 		AUIGrid.resize(ecrGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(ecoGridID);
		AUIGrid.resize(ecrGridID);
	});
</script>
