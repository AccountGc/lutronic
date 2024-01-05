<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="com.e3ps.change.cr.dto.CrDTO"%>
<%@page import="java.util.Map"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
CrDTO dto = (CrDTO) request.getAttribute("dto");
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
				CR 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if (dto.is_modify()) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<%
			}
			%>
			<%
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
				<th class="lb">CR 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>CR 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getWriter() != null ? dto.getWriter() : ""%></td>
				<th>작성부서</th>
				<td class="indent5"><%=dto.getCreateDepart_name()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getWriteDate()%></td>
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td class="indent5"><%=dto.getChangeSection()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_text()%></td>
				<th>승인일</th>
				<td class="indent5"><%=dto.getApproveDate()%></td>
			</tr>
			<tr>
				<th class="lb">제품명</th>
				<td colspan="3" class="indent5"><%=dto.getModel()%></td>
				<th>보존년한</th>
				<td class="indent5"><%=dto.getPeriod_name()%></td>
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
				<th class="lb">변경사유</th>
				<td colspan="5" class="indent5">
					<div class="textarea-auto">
						<textarea name="eoCommentA" id="eoCommentA" readonly="readonly"><%=dto.getEoCommentA()%></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td colspan="5" class="indent5">
					<div class="textarea-auto">
						<textarea name="eoCommentB" id="eoCommentB" readonly="readonly"><%=dto.getEoCommentB()%></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">참고사항</th>
				<td colspan="5" class="indent5">
					<div class="textarea-auto">
						<textarea name="eoCommentC" id="eoCommentC" readonly="readonly"><%=dto.getEoCommentC()%></textarea>
					</div>
				</td>
			</tr>
			<%
			}
			%>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td colspan="5" class="indent5">
					<%
					Map<String, Object> contentMap = dto.getContentMap();
					if (contentMap != null) {
					%>
					<div>
						<a href="<%=contentMap.get("url")%>">
							<span style="position: relative; bottom: 2px;"><%=contentMap.get("name")%></span>
							<img src="<%=contentMap.get("fileIcon")%>" style="position: relative; top: 1px;">
						</a>
					</div>
					<%
					} else {
					%>
					<font color="red">
						<b>등록된 주 첨부파일이 없습니다.</b>
					</font>
					<%
					}
					%>
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
		<jsp:include page="/extcore/jsp/change/cr/include/cr-record-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/cr/modify?oid=" + oid);
		document.location.href = url;
	}

	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/cr/delete?oid=" + oid);
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
		autoTextarea();
		createAUIGrid90(columns90);
		createAUIGrid101(columns101);
		createAUIGrid105(columns105);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID105);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID105);
		AUIGrid.resize(myGridID51);
		AUIGrid.resize(myGridID10000);
		AUIGrid.resize(myGridID10001);
	});
</script>
