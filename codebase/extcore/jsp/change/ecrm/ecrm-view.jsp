<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.ecrm.dto.EcrmDTO"%>
<%@page import="com.e3ps.change.ecpr.dto.EcprDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
ArrayList<NumberCode> sectionList = (ArrayList<NumberCode>) request.getAttribute("sectionList");
EcrmDTO dto = (EcrmDTO) request.getAttribute("dto");
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
				ECRM 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="일괄다운로드" title="일괄다운로드" onclick="download();">
			<%
			if (dto.is_withdraw()) {
			%>
			<input type="button" value="회수(결재선 유지)" title="회수(결재선 유지)" class="gray" onclick="withdraw('false');">
			<input type="button" value="회수(결재선 삭제)" title="회수(결재선 삭제)" class="blue" onclick="withdraw('true');">
			<%
			}
			%>
			<%
			if (isAdmin) {
			%>
			<select name="state" id="state" class="width-100" onchange="lcm(this);">
				<option value="">선택</option>
				<option value="LINE_REGISTER">결재선 지정</option>
				<option value="INWORK">작업 중</option>
				<option value="APPROVED">승인됨</option>
			</select>
			<%
			}
			%>
			<%
			if (dto.is_print()) {
			%>
			<input type="button" value="인쇄" title="인쇄" class="gray" onclick="print();">
			<%
			}
			%>
			<%
			if (dto.is_modify()) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<%
			}
			if (dto.is_delete()) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
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
				<th class="lb" colspan="6">
					<%=dto.getName()%>
				</th>
			</tr>
			<tr>
				<th class="lb">ECRM 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>보존년한</th>
				<td class="indent5"><%=dto.getPeriod_name()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getState()%></td>
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
				<th class="lb">제품명</th>
				<td class="indent5"><%=dto.getModel()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_text()%></td>
				<th>승인일</th>
				<td class="indent5"><%=dto.getApproveDate()%></td>
			</tr>
			<tr>
				<th class="lb">변경구분</th>
				<td colspan="5">
					&nbsp;
					<%
					for (NumberCode section : sectionList) {
						int isInclude = -1;
						if (dto.getChangeSection() != null) {
							isInclude = dto.getChangeSection().indexOf(section.getCode());
						}
					%>
					<div class="pretty p-switch">
						<input type="checkbox" name="changeSection" disabled="disabled" value="<%=section.getCode()%>" <%if (isInclude >= 0) {%> checked <%}%>>
						<div class="state p-success">
							<label>
								<b><%=section.getName()%></b>
							</label>
						</div>
					</div>
					&nbsp;
					<%
					}
					%>
				</td>
			</tr>
			<%
			if (dto.is_isNew()) {
			%>
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
			<%
			} else {
			%>
			<tr>
				<th class="lb">내용</th>
				<td colspan="5" class="indent5">
					<div class="textarea-auto">
						<textarea rows="5" readonly="readonly" rows="5"><%=dto.getEoCommentA() != null ? dto.getEoCommentA() : ""%></textarea>
					</div>
				</td>
			</tr>
			<%
			}
			%>
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

		<!-- 	관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 	관련 ECO -->
		<jsp:include page="/extcore/jsp/change/eco/include/eco-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 	관련 CR -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="true" name="header" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 이력관리 -->
		<jsp:include page="/extcore/jsp/change/ecrm/include/ecrm-record-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecrm/modify?oid=" + oid);
		document.location.href = url;
	}

	function _delete() {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecrm/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		}, "DELETE");
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
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
		createAUIGrid90(columns90);
		createAUIGrid101(columns101);
		createAUIGrid105(columns105);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID105);
		selectbox("state");
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID105)
		AUIGrid.resize(myGridID51);
		AUIGrid.resize(myGridID10000);
		AUIGrid.resize(myGridID10001);
	});

	function print() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecrm/print?oid=" + oid);
		const isPrint = savePrintHistory(oid);
		if(isPrint) {
			const p = _popup(url, "", "", "f");
		}
	}
	
	function download() {

		if (!confirm("일괄 다운로드 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecrm/download?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			if (data.result) {
				const n = data.name;
				document.location.href = '/Windchill/extcore/jsp/common/content/FileDownload2.jsp?fileName=' + n + '&originFileName=' + n;
			}
			closeLayer();
		}, "GET");
	}
</script>
