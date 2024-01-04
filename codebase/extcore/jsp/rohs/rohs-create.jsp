<%@page import="java.util.List"%>
<%@page import="com.e3ps.common.service.CommonHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.iba.AttributeKey"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="com.e3ps.common.util.SequenceDao"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.doc.DocumentType"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.rohs.ROHSMaterial"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
List<Map<String, String>> typeList = (List<Map<String, String>>) request.getAttribute("typeList");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body>
	<form>
		<input type="hidden" name="docType" id="docType" value="$$ROHS" />
		<input type="hidden" name="location" id="location" value="/Default/ROHS" />
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						RoHS 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="등록" title="등록" class="red" onclick="create('false');">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">물질번호</th>
				<td class="indent5">
					<input type="text" name="rohsNumber" id="rohsNumber" class="width-400">
					&nbsp;
					<input type="button" class="gray" value="번호 중복" title="번호 중복" onclick="check();">
				</td>
				<th class="req">물질명</th>
				<td class="indent5">
					<input type="text" name="rohsName" id="rohsName" class="width-400">
					&nbsp;
					<input type="button" class="gray" value="물질명 중복" title="물질명 중복" onclick="NameCheck();">
					<input type="hidden" id="duplicationChk" value="F">
				</td>
			</tr>
			<tr>
				<th class="req lb">결재방식</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="lifecycle" value="LC_Default" checked="checked">
						<div class="state p-success">
							<label>
								<b>기본결재</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="lifecycle" value="LC_Default_NonWF">
						<div class="state p-success">
							<label>
								<b>일괄결재</b>
							</label>
						</div>
					</div>
				</td>
				<th class="req">협력업체</th>
				<td class="indent5">
					<select name="manufacture" id="manufacture" class="width-300">
						<option value="">선택</option>
						<%
						for (NumberCode manufacture : manufactureList) {
						%>
						<option value="<%=manufacture.getCode()%>"><%=manufacture.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">파일구분</th>
				<td class="indent5">
					<select name="fileType" id="fileType" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> type : typeList) {
						%>
						<option value="<%=type.get("code")%>"><%=type.get("name")%></option>
						<%
						}
						%>
					</select>
				</td>
				<th class="req">발행일</th>
				<td class="indent5">
					<input type="text" name="publicationDate" id="publicationDate" class="width-100">
				</td>
			</tr>
		</table>


		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/part/include/part-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 관련 rohs -->
		<jsp:include page="/extcore/jsp/rohs/include/rohs-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" class="red" onclick="create('false');">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			var nameChk;

			function create(temp) {
				const temprary = JSON.parse(temp);

				let params = new Object();
				params.rohsNumber = $("#rohsNumber").val();
				params.lifecycle = $('input[name=lifecycle]:checked').val();
				params.rohsName = $("#rohsName").val();
				params.manufacture = $("#manufacture").val();
				params.description = $("#description").val();
				params.docType = $("#docType").val();
				params.location = $("#location").val();
				const secondarys = toArray("secondarys");
				params.secondary = secondarys;
				params.fileType = $("#fileType").val();
				params.publicationDate = $("#publicationDate").val();
				params.rohsList = AUIGrid.getGridDataWithState(myGridID106, "gridState");
				params.partList = AUIGrid.getGridDataWithState(myGridID91, "gridState");

				if (temprary) {
					if (!confirm("임시저장하시겠습니까??")) {
						return false;
					}

				} else {
					if (isEmpty($("#rohsName").val())) {
						alert("물질명을 입력하세요.");
						return;
					}

					if ($("#duplicationChk").val() == "F" || nameChk != $("#rohsName").val()) {
						alert("물질명 중복체크 해주세요.");
						return;
					}

					if (isEmpty($("#manufacture").val())) {
						alert("협력업체를 선택하세요.");
						return;
					}

					const secondarys = toArray("secondarys");
					if (secondarys.length > 0) {
						if (isEmpty($("#fileType").val())) {
							alert("파일구분을 선택하세요.");
							return;
						}
						if (isEmpty($("#publicationDate").val())) {
							alert("발행일을 입력하세요.");
							return;
						}
					}

					if (!confirm("등록하시겠습니까?")) {
						return false;
					}
				}
				params.temprary = temprary;
				parent.openLayer();
				var url = getCallUrl("/rohs/create");
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						location.href = getCallUrl("/rohs/list");
					}
					parent.closeLayer();
				});
			};

			// 물질명 중복체크
			function NameCheck() {
				var params = new Object();
				if (isEmpty($("#rohsName").val())) {
					alert("입력된 물질명이 없습니다.");
					return;
				}
				params.rohsName = $("#rohsName").val();
				var url = getCallUrl("/rohs/rohsCheck");
				call(url, params, function(data) {
					if (data.result) {
						if (data.count == 0) {
							alert("등록 가능한 물질명입니다.");
							$("#duplicationChk").val("S");
							nameChk = $("#rohsName").val();
						} else {
							alert("이미 등록된 물질명입니다.");
							$("#rohsName").val("");
						}
					} else {
						alert(data.msg);
					}
				});
			}

			// 번호 중복체크
			function check() {
				var params = new Object();
				if (isEmpty($("#rohsNumber").val())) {
					alert("입력된 물질번호가 없습니다.");
					return;
				}
				params.rohsNumber = $("#rohsNumber").val();
				var url = getCallUrl("/rohs/rohsCheck");
				call(url, params, function(data) {
					if (data.result) {
						if (data.count == 0) {
							alert("등록 가능한 물질번호 입니다.");
						} else {
							alert("이미 등록된 물질번호 입니다.");
							$("#rohsNumber").val("");
						}
					} else {
						alert(data.msg);
					}
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("rohsNumber");
				selectbox("manufacture");
				selectbox("fileType");
				date("publicationDate");
				createAUIGrid91(columns91);
				AUIGrid.resize(myGridID91);
				createAUIGrid106(columns106);
				AUIGrid.resize(myGridID106);
			});

			window.addEventListener("resize", function() {
				createAUIGrid106(columns106);
				createAUIGrid106(columns91);
			});
		</script>
	</form>
</body>
</html>