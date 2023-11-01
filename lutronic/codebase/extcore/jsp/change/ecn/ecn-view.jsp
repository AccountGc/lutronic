<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.ecn.dto.EcnDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcnDTO dto = (EcnDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>

<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECN 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="Erp전송" title="Erp전송" class="blue" onclick="modify();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
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
			<a href="#tabs-2">설변 활동</a>
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
			</colgroup>
			<tr>
				<th class="lb">ECN 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>ECN 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"></td>
				<th>등록자</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">구분</th>
				<td class="indent5"></td>
				<th>등록일</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">수정일</th>
				<td class="indent5"></td>
				<th>승인일</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly" id="eoCommentA" rows="5"><%=dto.getEoCommentA()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly" id="eoCommentB" rows="5"><%=dto.getEoCommentB()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">특기사항</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly" id="eoCommentC" rows="5"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly" id="eoCommentD" rows="5"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">계변경 부품 내역파일</th>
				<td class="indent5" colspan="3">
					<font color="red">
						<b>등록된 계변경 부품 내역파일이 없습니다.</b>
					</font>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	
	<div id="tabs-2">
		<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="250" name="height" />
		</jsp:include>
	</div>
</div>

<script type="text/javascript">
	const oid = document.getElementById("oid").value;

	function autoHeight() {
		const eoCommentC = document.getElementById("eoCommentC");
		eoCommentC.style.height = "auto";
		eoCommentC.style.height = "500px";
		// 		const style = window.getComputedStyle(eoCommentC);
		// 		console.log(style);

	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					break;
				case "tabs-2":
					const isCreated200 = AUIGrid.isCreated(myGridID200); // 설변 활동
					if (isCreated200) {
						AUIGrid.resize(myGridID200);
					} else {
						createAUIGrid200(columns200);
					}
					break;
				}
			}
		});

		autoHeight();
	});
	
	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecn/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				clsoeLayer();
			}
		}, "DELETE");
	}
</script>