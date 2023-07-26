<%@page import="com.e3ps.drawing.beans.EpmData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
EpmData dto = (EpmData) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if(dto.isNameSyschronization){
			%>
				<input type="button" value="Name 동기화" title="Name 동기화" class="" id="updateName">
			<%	
			}
			%>
			<%
			if(dto.isUpdate){
			%>
				<input type="button" value="수정" title="수정" class="blue" id="updateBtn">
				<input type="button" value="삭제" title="삭제" class="red" id="deleteBtn">
			<%	
			}
			%>
			<%
			if(dto.isLatest){
			%>
				<input type="button" value="최신버전" title="최신버전" class="" id="latestBtn">
			<%	
			}
			%>
			<input type="button" value="버전이력" title="버전이력" class="" id="versionBtn">
			<input type="button" value="다운로드이력" title="다운로드이력" class="" id="downloadBtn">
			<input type="button" value="결재이력" title="결재이력" class="" id="approveBtn">
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
		<th>물질명</th>
		<td colspan="3"><%=dto.getName()%></td>
	</tr>
	<tr>
		<th>물질 번호</th>
		<td><%=dto.getNumber()%></td>
		<th>협력업체</th>
		<td><%=dto.getManufactureDisplay()%></td>
	</tr>
	<tr>
		<th>상태</th>
		<td><%=dto.getState()%></td>
		<th>Rev.</th>
		<td></td>
	</tr>
	<tr>
		<th>등록자</th>
		<td><%=dto.getCreator()%></td>
		<th>수정자</th>
		<td><%=dto.getModifier()%></td>
	</tr>
	<tr>
		<th>등록일</th>
		<td><%=dto.getCreateDate()%></td>
		<th>수정일</th>
		<td><%=dto.getModifyDate()%></td>
	</tr>
	<tr>
		<th>결재방식</th>
		<td><%=dto.getApprovalType()%></td>
		<th>설명</th>
		<td><%=dto.getDescription()%></td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td colspan="3"></td>
	</tr>
</table>
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
			
			//개정
			$("#reviseBtn").click(function () {
				var url	= getURLString("doc", "reviseDocumentPopup", "do") + "?oid="+$("#oid").val()+"&module=rohs";
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
			
			//결재 이력
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
			
			//copy
			$('#copyRohs').click(function() {
				var url = getURLString("rohs", "copyRohs", "do") + '?oid='+$('#oid').val();
				openOtherName(url,"copyRohs","830","300","status=no,scrollbars=yes,resizable=yes");
			})
			
			//결재 회수
			$("#withDrawBtn").click(function() {
				var url	= getURLString("common", "withDrawPopup", "do") + "?oid="+$("#oid").val();
				openOtherName(url,"withDrawBtn","400","220","status=no,scrollbars=yes,resizable=yes");
			})
			
			//일괄 다운로드
			$("#batchSecondaryDown").click(function() {
				var form = $("form[name=rohsViewForm]").serialize();
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
			

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID7);
				AUIGrid.resize(myGridID);
				AUIGrid.resize(myGridID100);
			});
		</script>
