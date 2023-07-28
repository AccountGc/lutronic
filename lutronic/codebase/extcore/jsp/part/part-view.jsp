<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.part.beans.PartData"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%@page import="net.sf.json.JSONArray"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
PartData data = (PartData) request.getAttribute("data");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				품목 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="속성 Clearing" title="속성 Clearing" class="blue" onclick="">
			<input type="button" value="일괄 수정" title="일괄 수정" onclick="">
			<input type="button" value="수정" title="수정" onclick="update('modify');">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="채번" title="채번"  onclick="">
			<input type="button" value="채번(새버전)" title="채번(새버전)"  onclick="">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
<!-- 	<ul> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-1">개발업무 상세보기</a> -->
<!-- 		</li> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-2">구성원</a> -->
<!-- 		</li> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-3">TASK</a> -->
<!-- 		</li> -->
<!-- 	</ul> -->
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="500">
				<col width="150">
				<col width="500">
			</colgroup>
			<tr>
				<th class="lb" colspan="5"><%= data.getName() %></th>
			</tr>
			<tr>
				<th class="lb">품목번호</th>
				<td class="indent5"><%=data.getNumber()%></td>
				<th>품목분류</th>
				<td class="indent5">
<%-- 					<%=data.getLocation()%> --%>
				</td>
				<td class="tdwhiteL" align="center" rowspan="7">
<%--                    	<jsp:include page="/eSolution/drawing/thumbview.do" flush="true"> --%>
<%-- 						<jsp:param name="oid" value="<%=data.getEpmOid() %>" /> --%>
<%-- 					</jsp:include> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5">
<%-- 					<%=data.getLifecycle()%> --%>
				</td>
				<th>Rev.</th>
				<td class="indent5">
<%-- 					<%=data.getVersion()%>.<%=data.getIteration %>(<%=data.getViewName %>) --%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=data.getCreator()%></td>
				<th>수정자</th>
				<td class="indent5">
<%-- 					<%=data.getModifier()%> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5" ><%=data.getCreateDate()%></td>
				<th class="lb">수정일</th>
				<td class="indent5" ><%=data.getModifyDate()%></td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
<%-- 					<jsp:include page="/eSolution/content/includeAttachFileView"> --%>
<%-- 						<jsp:param value="<%=data.getOid() %>" name="oid"/> --%>
<%-- 					</jsp:include> --%>
				</td>
			</tr>
		</table>
	</div>
	
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png">
					품목 속성
				</div>
			</td>
		</tr>
	</table>
	<div id="tabs-2">
		<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
			<jsp:param value="<%=data.getOid()%>" name="oid" />
			<jsp:param value="part" name="module"/>
		</jsp:include>
	</div>
	
	<!-- 	주도면 -->
	<div id="tabs-3">
		<jsp:include page="/extcore/jsp/drawing/drawingView_include.jsp">
			<jsp:param value="part" name="moduleType"/>
			<jsp:param value="main" name="epmType" />
			<jsp:param value="<%=data.getOid() %>" name="oid"/>
			<jsp:param value="주도면" name="title"/>
			<jsp:param value="epmOid" name="paramName"/>
		</jsp:include>
	</div>	
	
	<!-- 참조 항목 -->
	<div id="tabs-4">
		<jsp:include page="/extcore/jsp/drawing/include_viewReferenceBy.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	
		
</div>
<script type="text/javascript">
	function update(mode) {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/update?oid=" + oid + "&mode=" + mode);
		openLayer();
		document.location.href = url;
	}

	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
// 				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		}, "GET");
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-3":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
// 				case "tabs-3":
// 					const isCreated2 = AUIGrid.isCreated(myGridID2);
// 					if (isCreated2) {
// 						AUIGrid.resize(myGridID2);
// 					} else {
// 						createAUIGrid2(columns2);
// 					}
// 					break;
				}
			},
		});
		createAUIGrid(columns);
// 		createAUIGrid2(columns2);
		AUIGrid.resize(myGridID);
// 		AUIGrid.resize(myGridID2);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
// 		AUIGrid.resize(myGridID2);
	});
</script>