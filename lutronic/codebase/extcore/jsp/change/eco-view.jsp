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
ECOData dto = (ECOData) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%
				if(dto.getEoType().equals("CHANGE")){
				%>
					ECO
				<%	
				}else{
				%>
					EO
				<%	
				}
				%>
				상세보기
			</div>
		</td>
		<td class="right">
			<%
			if(dto.getState().equals("APPROVED")){
				if(dto.getEoType().equals("CHANGE")){
				%>
				<input type="button" value="Excel 다운" title="Excel 다운" class="" id="excelDown">
				<%		
				}else{
				%>
					<input type="button" value="Excel 다운" title="Excel 다운" class="" id="excelDown">
				<%	
				}
				%>
			<%	
			}
			%>
			<input type="button" value="수정" title="수정" class="blue" id="updateBtn">
			<input type="button" value="삭제" title="삭제" class="red" id="deleteBtn">
			<input type="button" value="산출물" title="산출물" class="" id="viewECA">
			<input type="button" value="결재이력" title="결재이력" class="" id="approveBtn">
			<input type="button" value="다운로드이력" title="다운로드이력" class="" id="downloadBtn">
			<input type="button" value="닫기" title="닫기" class="gray" id="closeBtn" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본 정보</a>
		</li>
		<li>
			<a href="#tabs-2">활동 현황</a>
		</li>
		<li>
			<a href="#tabs-3">완제품 품목</a>
		</li>
		<%
		if(dto.getEoType().equals("CHANGE")){
		%>
			<li>
				<a href="#tabs-4">대상 품목</a>
			</li>
			<li>
				<a href="#tabs-5">관련 ECR</a>
			</li>
		<%	
		}
		%>	
	</ul>
	<div id="tabs-1">
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="13%">
				<col width="37%">
				<col width="13%">
				<col width="37%">
			</colgroup>
			<tr>
				<th>
					<%
					if(dto.getEoType().equals("CHANGE")){
					%>
						ECO
					<%	
					}else{
					%>
						EO
					<%	
					}
					%>
					제목
				</th>
				<td colspan="3"><%=dto.getEoName()%></td>
			</tr>
			<tr>
				<th>
					<%
					if(dto.getEoType().equals("CHANGE")){
					%>
						ECO
					<%	
					}else{
					%>
						EO
					<%	
					}
					%>
					번호
				</th>
				<td><%=dto.getEoNumber()%></td>
				<th>상태</th>
				<td><%=dto.getState()%></td>
			</tr>
			<tr>
				<th>등록자</th>
				<td><%=dto.getCreator()%></td>
				<th>구분</th>
				<td><%=dto.getEoTypeDisplay()%></td>
			</tr>
			<tr>
				<th>인허가 변경</th>
				<td></td>
				<th>위험 통제</th>
				<td><%=dto.getRiskTypeName()%></td>
			</tr>
			<tr>
				<th>등록일</th>
				<td><%=dto.getCreateDate()%></td>
				<th>수정일</th>
				<td><%=dto.getModifyDate()%></td>
			</tr>
			<tr>
				<th>승인일</th>
				<td colspan="3"><%=dto.getEoApproveDate()%></td>
			</tr>
			<tr>
				<th>제품명</th>
				<td colspan="3"></td>
			</tr>
			<tr>
				<th>
					<%
					if(dto.getEoType().equals("CHANGE")){
					%>
						변경사유
					<%	
					}else{
					%>
						제품 설계 개요
					<%	
					}
					%>
				</th>
				<td colspan="3"><%=dto.getEoCommentA()%></td>
			</tr>
			<tr>
				<th>
					<%
					if(dto.getEoType().equals("CHANGE")){
					%>
						변경사항
					<%	
					}else{
					%>
						특기사항
					<%	
					}
					%>
				</th>
				<td colspan="3"><%=dto.getEoCommentB()%></td>
			</tr>
			<%
			if(dto.getEoType().equals("CHANGE")){
			%>
				<tr>
					<th>특기사항</th>
					<td colspan="3"><%=dto.getEoCommentC()%></td>
				</tr>
			<%	
			}
			%>	
			<tr>
				<th>기타사항</th>
				<td colspan="3">
					<%
					if(dto.getEoType().equals("CHANGE")){
						dto.getEoCommentD();
					}else{
						dto.getEoCommentC();
					}
					%>
				</td>
			</tr>
			<%
			if(dto.getEoType().equals("CHANGE")){
			%>
				<tr>
					<th>설계변경 부품 내역파일</th>
					<td colspan="3"></td>
				</tr>
			<%	
			}
			%>	
			<tr>
				<th>첨부파일</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/common/content/include_secondaryFileView.jsp">
						<jsp:param value="<%= dto.getOid() %>" name="oid"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<!-- 활동 현황 -->
		<jsp:include page="/extcore/jsp/change/include_viewECA.jsp" flush="false">
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 완제품 품목 -->
		<jsp:include page="/extcore/jsp/change/include_viewCompletePart.jsp" flush="false">
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	<%
	if(dto.getEoType().equals("CHANGE")){
	%>
		<div id="tabs-4">
			<!-- 대상 품목 -->
			<jsp:include page="/extcore/jsp/change/include_viewChangePart.jsp" flush="false">
				<jsp:param value="<%=dto.getOid() %>" name="oid" />
			</jsp:include>
		</div>	
		<div id="tabs-5">
			<!-- 관련 ECR -->
			<jsp:include page="/extcore/jsp/change/include_viewECR.jsp" flush="false">
				<jsp:param value="<%=dto.getOid() %>" name="oid" />
			</jsp:include>
		</div>
	<%	
	}
	%>
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
//		 				opener.loadGridData();
				self.close();
			}
		}, "GET");
	})
			
	//산출물
	$("#viewECA").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/changeECA/viewECA?oid=" + oid);
		popup(url, 1000, 600);
	})
	
	//다운로드 이력
	$("#downloadBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/common/downloadHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	//결재 이력
	$("#approveBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/groupware/workHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	//결재 회수
	$("#withDrawBtn").click(function() {
		var url	= getURLString("common", "withDrawPopup", "do") + "?oid="+$("#oid").val();
		openOtherName(url,"withDrawBtn","400","220","status=no,scrollbars=yes,resizable=yes");
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
				case "tabs-2":
					const isCreated1 = AUIGrid.isCreated(ecaGridID);
					if (isCreated1) {
						AUIGrid.resize(ecaGridID);
					} else {
						createAUIGrid1(columnEca);
					}
					break;
				case "tabs-3":
					const isCreated2 = AUIGrid.isCreated(complePartGridID);
					if (isCreated2) {
						AUIGrid.resize(complePartGridID);
					} else {
						createAUIGrid2(columnComplePart);
					}
					break;
				case "tabs-4":
					const isCreated3 = AUIGrid.isCreated(changePartGridID);
					if (isCreated3) {
						AUIGrid.resize(changePartGridID);
					} else {
						createAUIGridChangePart(columnChangePart);
					}
					break;
				case "tabs-5":
					const isCreated4 = AUIGrid.isCreated(ecrGridID);
					if (isCreated4) {
						AUIGrid.resize(ecrGridID);
					} else {
						createAUIGrid4(columnEcr);
					}
					break;	
				}
			}
		});
		createAUIGrid1(columnEca);
		AUIGrid.resize(ecaGridID);
		createAUIGrid2(columnComplePart);
		AUIGrid.resize(complePartGridID);
		createAUIGridChangePart(columnChangePart);
		AUIGrid.resize(changePartGridID);
		createAUIGrid4(columnEcr);
		AUIGrid.resize(ecrGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(ecaGridID);
		AUIGrid.resize(complePartGridID);
		AUIGrid.resize(changePartGridID);
		AUIGrid.resize(ecrGridID);
	});
</script>
