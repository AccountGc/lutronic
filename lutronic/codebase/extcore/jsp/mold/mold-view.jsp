<%@page import="com.e3ps.mold.dto.MoldDTO"%>
<%@page import="com.e3ps.rohs.dto.RohsData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
MoldDTO dto = (MoldDTO) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				금형 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if(dto.getState().equals("APPROVED")){
			%>
				<input type="button" value="개정" title="개정" class="" id="reviseBtn">
			<%	
			}
			%>
			<!-- 회수 권한 승인중 && 소유자 || 관리자 -->
			<%
			if(dto.isWithDraw()){
			%>
				<input type="button" value="결재회수" title="결재회수" class="" id="withDrawBtn">
			<%	
			}
			%>
			<%
			if(dto.getState().equals("INWORK") || dto.getState().equals("BATCHAPPROVAL") || dto.getState().equals("REWORK")){
			%>
				<input type="button" value="수정" title="수정" class="blue" id="updateBtn">
				<input type="button" value="삭제" title="삭제" class="red" id="deleteBtn">
			<%	
			}
			%>
			<input type="button" value="Rev.이력" title="Rev.이력" class="" id="versionBtn">
			<input type="button" value="다운로드이력" title="다운로드이력" class="" id="downloadBtn">
			<input type="button" value="결재이력" title="결재이력" class="" id="approveBtn">
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
			<a href="#tabs-2">속성</a>
		</li>
		<li>
			<a href="#tabs-3">관련 품목</a>
		</li>
		<li>
			<a href="#tabs-4">관련 문서</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="450">
				<col width="130">
				<col width="450">
				<col width="130">
				<col width="450">
			</colgroup>
			<tr>
				<th class="lb">금형번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>금형분류</th>
				<td class="indent5"><%=dto.getLocation()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getStateDisplay()%></td>
			</tr>
			<tr>
				<th class="lb">REV</th>
				<td class="indent5">
					<%=dto.getVersion()%>.<%=dto.getIteration()%>
					<%
					if (!dto.isLatest()) {
					%>
					&nbsp;
					<b>
						<a href="javascript:latest();">(최신버전으로)</a>
					</b>
					<%
					}
					%>
				</td>
				<th>등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5"><%=dto.getCreateDate()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifyDate()%></td>
				<th>문서유형</th>
				<td class="indent5"><%=dto.getDocumentTypeDisplay()%></td>
			</tr>
			<tr>
				<th class="lb">결재방식</th>
				<td class="indent5"><%=dto.getApprovaltype_name() == null ? "" : dto.getApprovaltype_name()%></td>
				<th>내부문서번호</th>
				<td class="indent5"><%=dto.getInteralnumber() == null ? "" : dto.getInteralnumber()%></td>
				<th>부서</th>
				<td class="indent5"><%=dto.getDeptcode_name() == null ? "" : dto.getDeptcode_name()%></td>
			</tr>
			<tr>
				<th class="lb">금형번호</th>
				<td class="indent5"><%=dto.getMoldnumber() == null ? "" : dto.getMoldnumber()%></td>
				<th>금형타입</th>
				<td class="indent5"><%=dto.getMoldtype_name() == null ? "" : dto.getMoldtype_name()%></td>
				<th>금형개발비</th>
				<td class="indent5"><%=dto.getMoldcost() == null ? "" : dto.getMoldcost()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="5" class="indent5">
					<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getDescription() == null ? "" : dto.getDescription()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<!-- 속성 -->
<%-- 		<jsp:include page="/extcore/jsp/part/include_viewPart.jsp" flush="false" > --%>
<%-- 			<jsp:param value="<%=dto.getOid() %>" name="oid" /> --%>
<%-- 			<jsp:param value="관련 품목" name="title" /> --%>
<%-- 			<jsp:param value="doc" name="moduleType"/> --%>
<%-- 		</jsp:include> --%>
	</div>
	<div id="tabs-3">
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/part/include_viewPart.jsp" flush="false" >
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
			<jsp:param value="관련 품목" name="title" />
			<jsp:param value="doc" name="moduleType"/>
		</jsp:include>
	</div>
	<div id="tabs-4">
		<!-- 관련 문서 -->
<%-- 		<jsp:include page="/extcore/jsp/part/include_viewPart.jsp" flush="false" > --%>
<%-- 			<jsp:param value="<%=dto.getOid() %>" name="oid" /> --%>
<%-- 			<jsp:param value="관련 품목" name="title" /> --%>
<%-- 			<jsp:param value="doc" name="moduleType"/> --%>
<%-- 		</jsp:include> --%>
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
				self.close();
			}
		}, "GET");
	})
			
	//개정
	$("#reviseBtn").click(function () {
		const oid = $("#oid").val();
		const url = getCallUrl("/mold/revise?oid=" + oid);
		document.location.href = url;
	})
	
	//버전이력
	$("#versionBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/common/versionHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	//다운로드 이력
	$("#downloadBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/common/downloadHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	//결재이력
	$("#approveBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/groupware/workHistory?oid=" + oid);
		popup(url, 830, 600);
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
	
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-2":
// 					const isCreated1 = AUIGrid.isCreated(partGridID);
// 					if (isCreated1) {
// 						AUIGrid.resize(partGridID);
// 					} else {
// 						createAUIGrid1(columnPart);
// 					}
					break;
				case "tabs-3":
					const isCreated2 = AUIGrid.isCreated(partGridID);
					if (isCreated2) {
						AUIGrid.resize(partGridID);
					} else {
						createAUIGrid1(columnPart);
					}
					break;
				case "tabs-4":
// 					const isCreated3 = AUIGrid.isCreated(rohs2GridID);
// 					if (isCreated3) {
// 						AUIGrid.resize(rohs2GridID);
// 					} else {
// 						createAUIGridRohs2(columnRohs2);
// 					}
					break;
				}
			}
		});
		createAUIGrid1(columnPart);
		AUIGrid.resize(partGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(partGridID);
	});
</script>
