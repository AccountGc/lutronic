<%@page import="com.e3ps.change.ecpr.dto.EcprDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
EcprDTO dto = (EcprDTO) request.getAttribute("dto");
%>
<style type="text/css">
iframe {
	margin-top: 3px;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECPR 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if (isAdmin || dto.isModify()) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="update();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="deleteBtn();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본 정보</a>
		</li>
		<li>
			<a href="#tabs-2">관련 객체</a>
		</li>
		<li>
			<a href="#tabs-3">이력 관리</a>
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
				<th class="lb">ECPR 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>ECPR 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>등록일</th>
				<td class="indent5"><%=dto.getCreatedDate()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_text()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getWriter()%></td>
				<th>작성부서</th>
				<td class="indent5"><%=dto.getCreateDepart()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getWriteDate()%></td>
			</tr>
			<tr>
				<th class="lb">승인일</th>
				<td class="indent5"><%=dto.getApproveDate()%></td>
				<th>제품명</th>
				<td class="indent5"><%=dto.getModel()%></td>
				<th>변경구분</th>
				<td class="indent5"><%=dto.getChangeCode()%></td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td colspan="5" class="indent7 pb8">
					<textarea name="contents" id="contents" rows="7" style="display: none;"><%=dto.getContents() != null ? dto.getContents() : ""%></textarea>
					<script type="text/javascript">
						// 에디터를 view 모드로 설정합니다.
						DEXT5.config.Mode = "view";
						new Dext5editor("content");
						const content = document.getElementById("contents").value;
						DEXT5.setBodyValue(content, "content");
					</script>
				</td>
			</tr>
			<!-- 			<tr> -->
			<!-- 				<th class="lb">변경사유</th> -->
			<%-- 				<td colspan="3" class="indent5"><%=dto.getEoCommentA()%></td> --%>
			<!-- 			</tr> -->
			<!-- 			<tr> -->
			<!-- 				<th class="lb">변경사항</th> -->
			<%-- 				<td colspan="3" class="indent5"><%=dto.getEoCommentB()%></td> --%>
			<!-- 			</tr> -->
			<!-- 			<tr> -->
			<!-- 				<th class="lb">참고사항</th> -->
			<%-- 				<td colspan="3" class="indent5"><%=dto.getEoCommentC()%></td> --%>
			<!-- 			</tr> -->
			<tr>
				<th class="lb">주 첨부파일</th>
				<td colspan="5" class="indent5">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td colspan="5" class="indent5">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<!-- 관련 객체 -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-reference-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 이력관리 -->
		<jsp:include page="/extcore/jsp/change/ecpr/include/ecpr-record-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function update() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecpr/update?oid=" + oid);
		document.location.href = url;
	}

	function deleteBtn() {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecpr/delete");
		let params = new Object();
		params.oid = oid;
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
		});
	}

	//결재 회수
	$("#withDrawBtn").click(function() {
		const oid = $("#oid").val();
		const url = getCallUrl("/common/withDrawPopup?oid=" + oid);
		_popup(url, 1500, 550, "n");
	})

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-2":
					const isCreated101 = AUIGrid.isCreated(myGridID101); // CR
					if (isCreated101) {
						AUIGrid.resize(myGridID101);
					} else {
						createAUIGrid101(columns101);
					}
					break;
				case "tabs-3":
					const isCreated51 = AUIGrid.isCreated(myGridID51); // 다운로드이력
					if (isCreated51) {
						AUIGrid.resize(myGridID51);
					} else {
						createAUIGrid51(columns51);
					}
					const isCreated10000 = AUIGrid.isCreated(myGridID10000); // 결재이력
					if (isCreated10000) {
						AUIGrid.resize(myGridID10000);
					} else {
						createAUIGrid10000(columns10000);
					}
					const isCreated10001 = AUIGrid.isCreated(myGridID10001); // 외부 메일
					if (isCreated10001) {
						AUIGrid.resize(myGridID10001);
					} else {
						createAUIGrid10001(columns10001);
					}
					break;
				}
			}
		});
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID300);
		AUIGrid.resize(myGridID101);
	});
</script>
