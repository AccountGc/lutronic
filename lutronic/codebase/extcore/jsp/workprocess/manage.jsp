<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) request.getAttribute("list");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
	<form>


		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						관리자 메뉴
					</div>
				</td>
			</tr>
		</table>

		<div id="layer">
			<table class="search-table">
				<colgroup>
					<col width="174">
					<col width="*">
				</colgroup>
				<tr>
					<th>OID</th>
					<td class="indent5">
						<input type="text" name="value" id="value" class="width-300">
					</td>
				</tr>
			</table>
			<table class="button-table">
				<tr>
					<td class="center">
						<input type="button" value="제출" class="red" onclick="info();">
					</td>
				</tr>
			</table>
		</div>



		<div id="msg">
			<table class="search-table">
				<colgroup>
					<col width="130">
					<col width="*">
				</colgroup>
				<tr>
					<th>OID</th>
					<td class="indent5">
						<input type="button" value="삭제" class="red">
					</td>
				</tr>
				<tr>
					<th>라이프사이클 템플릿</th>
					<td class="indent5">
						새 라이프 사이클 템플릿
						<input type="text" name="lifecycle" id="lifecycle" class="width-300">
						<input type="button" value="재지정" title="재지정" class="blue">
					</td>
				</tr>
				<tr>
					<th>상태</th>
					<td>
						TERMINATE :&nbsp;
						<div class="pretty p-switch">
							<input type="radio" name="terminate" value="true" checked="checked">
							<div class="state p-success">
								<label>
									<b>TRUE</b>
								</label>
							</div>
						</div>
						&nbsp;
						<div class="pretty p-switch">
							<input type="radio" name="terminate" value="false">
							<div class="state p-success">
								<label>
									<b>FALSE</b>
								</label>
							</div>
						</div>
						<input type="button" value="상태변경" title="상태변경" class="blue">
					</td>
				</tr>
			</table>
		</div>

		<br>

		<!-- 속성값 변경 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						속성값 변경
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>OID</th>
				<td class="indent5">
					<input type="text" name="oid" id="oid" class="width-300">
				</td>
			</tr>
			<tr>
				<th>속성명</th>
				<td class="indent5">
					<select name="attrName" id="attrName" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> map : list) {
						%>
						<option value="<%=map.get("key")%>&<%=map.get("type")%>"><%=map.get("name")%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>속성값</th>
				<td class="indent5">
					<input type="text" name="attrValue" id="attrValue" class="width-300">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="변경" title="변경" class="blue" onclick="modify();">
					<input type="button" value="초기화" title="초기화" onclick="_reset();">
				</td>
			</tr>
		</table>
		<br>

		<!-- 도면 재변환 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						도면 재변환
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>등록일자</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제">
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="변환요청" title="변환요청" class="blue" onclick="publish();">
				</td>
			</tr>
		</table>

		<!-- 기안자 변경 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						기안자 변경
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>OID</th>
				<td class="indent5">
					<input type="text" name="target" id="target" class="width-300">
				</td>
				<th>변경 기안자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" data-multi="false" class="width-200">
					<input type="hidden" name="creatorOid" id="creatorOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="기안자 변경" title="기안자 변경" class="blue" onclick="apply();">
				</td>
			</tr>
		</table>



		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				selectbox("attrName");
				twindate("created");
				finderUser("creator");
				document.getElementById("msg").style.display = "none";
			})

			// 기안자 변경
			function apply() {
				const target = document.getElementById("target");
				const creator = document.getElementById("creator");
				const creatorOid = document.getElementById("creatorOid");
				if (target.value === "") {
					alert("기안자 변경할 객체의 OID를 입력하세요.");
					target.focus();
					return false;
				}
				if (creator.value === "") {
					alert("변경할 기안자를 선택하세요.");
					creator.focus();
					return false;
				}
				const url = getCallUrl("/groupware/apply");
				const params = {
					oid : target.value,
					creatorOid : creatorOid.value
				}
				if (!confirm("변경 하시겠습니까?")) {
					return false;
				}

				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {

					} else {

					}
					parent.closeLayer();
				})
			}

			function info() {
				const oid = document.getElementById("value");
				if (oid.value === "") {
					alert("OID 값을 입력하세요.");
					value.focus();
					return false;
				}

				if (oid.value.indexOf("WTPart") <= -1 && oid.value.indexOf("EPMDocument") <= -1 && oid.value.indexOf("WTDocument") <= -1) {
					alert("부품 & 도면 & 문서의 OID 값만 입력이 가능합니다.\n예시) wt.epm.EPMDocument:1111");
					oid.value = "";
					oid.focus();
					return false;
				}

				const url = getCallUrl("/groupware/info");
				const params = {
					oid : oid.value
				}
				parent.openLayer();
				call(url, params, function(data) {
					if (data.result) {
						console.log(data);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				})
			}

			function _reset() {
				const oid = document.getElementById("oid");
				const attrName = document.getElementById("attrName");
				const attrValue = document.getElementById("attrValue");
				oid.value = "";
				attrValue.value = "";
				$("#attrName").bindSelectSetValue("");
			}

			function modify() {
				const oid = document.getElementById("oid");
				const attrName = document.getElementById("attrName").value;
				const attrValue = document.getElementById("attrValue");

				if (oid.value === "") {
					alert("OID 값을 입력하세요.");
					oid.focus();
					return false;
				}

				if (oid.value.indexOf("WTPart") <= -1 && oid.value.indexOf("EPMDocument") <= -1 && oid.value.indexOf("WTDocument") <= -1) {
					alert("부품 & 도면 & 문서의 OID 값만 입력이 가능합니다.\n예시) wt.epm.EPMDocument:1111");
					oid.value = "";
					oid.focus();
					return false;
				}

				if (attrName === "") {
					alert("속성명을 선택하세요.");
					return false;
				}

				if (attrValue === "") {
					alert("속성값을 입력하세요.");
					attrValue.focus();
					return false;
				}

				const url = getCallUrl("/groupware/modify");
				const params = {
					oid : oid.value,
					value : attrValue.value,
					attrName : attrName
				};
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.reload();
					} else {
						parent.closeLayer();
					}
				})
			}

			function publish() {
				const createdFrom = document.getElementById("createdFrom").value;
				const createdTo = document.getElementById("createdTo").value;

				if (createdFrom === "") {
					alert("등록일(시작0)을 입력(선택) 하세요.");
					return false;
				}

				if (createdTo === "") {
					alert("등록일(끝)을 입력(선택) 하세요.");
					return false;
				}

				const params = {
					predate : createdFrom,
					postdate : createdTo
				};

				if (!confirm(createdFrom + " ~ " + createdTo + " 기간동안의 도면파일을 재변환 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/groupware/publish");
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.reload();
					}
					parent.closeLayer();
				})
			}
		</script>
	</form>
</body>
</html>