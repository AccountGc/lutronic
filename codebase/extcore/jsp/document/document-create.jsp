<%@page import="net.sf.json.JSONObject"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@page import="wt.doc.DocumentType"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> preserationList = (ArrayList<NumberCode>) request.getAttribute("preserationList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<Map<String, String>> classTypes1 = (ArrayList<Map<String, String>>) request.getAttribute("classTypes1");
DocumentDTO dto = (DocumentDTO) request.getAttribute("dto");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<style type="text/css">
iframe {
	margin-top: 3px;
}
</style>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>

<!-- 채번스크립트 -->
<script type="text/javascript" src="/Windchill/extcore/jsp/document/js/genNumber.js?v=551"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="등록" title="등록" class="red" onclick="create('false');">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="120">
				<col width="*">
				<col width="120">
				<col width="*">
				<col width="120">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">문서분류</th>
				<td class="indent5">
					<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT%>">
					<span id="locationText"> /Default/문서 </span>
					<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">
					<!-- 					<input type="button" value="문서채번확인" title="문서채번확인" onclick="numberView();" class="red"> -->
				</td>
				<th class="req" id="reqNum">문서번호</th>
				<td class="indent5">
					<input type="text" name="interalnumber" id="interalnumber" class="width-300" readonly="readonly">
				</td>
				<th class="req">보존년한</th>
				<td class="indent5">
					<select name="preseration" id="preseration" class="width-200">
						<%
						for (NumberCode preseration : preserationList) {
							// 코드로 처리
						%>
						<option value="<%=preseration.getCode()%>"><%=preseration.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb req">대분류</th>
				<td class="indent5">
					<select name="classType1" id="classType1" class="width-200" onchange="first(this);">
						<option value="">선택</option>
						<%
						for (Map<String, String> map : classTypes1) {
							String value = map.get("value");
							String name = map.get("name");
							String clazz = map.get("clazz");
						%>
						<option value="<%=value%>" data-clazz="<%=clazz%>"><%=name%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>중분류</th>
				<td class="indent5">
					<input type="text" name="classType2" id="classType2" class="width-400" readonly="readonly">
					<img src="/Windchill/extcore/images/delete.png" id="clearSecond" class="delete" title="삭제" onclick="clearSecondValue();">
				</td>
				<th>소분류</th>
				<td class="indent5">
					<select name="classType3" id="classType3" class="width-300" onchange="last();">
						<option value="">선택</option>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb" id="modelreq">프로젝트코드</th>
				<td class="indent5">
					<select name="model" id="model" class="width-200" onchange="preNumberCheck(this);">
						<option value="">선택</option>
						<%
						for (NumberCode model : modelList) {
						%>
						<option value="<%=model.getCode()%>"><%=model.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th id="preq">제품명</th>
				<td class="indent5">
					<input type="text" name="product" id="product" style="width: 95%;" readonly="readonly">
				</td>
				<th>문서번호(구)</th>
				<td class="indent5">
					<input type="text" name="oldNumber" id="oldNumber" class="width-200">
				</td>
			</tr>
			<tr>
				<th class="lb req">문서명</th>
				<td class="indent5" colspan="3">
					<input type="text" name="preFix" id="preFix" class="width-500" readonly="readonly">
					&nbsp;&nbsp;
					<input type="text" name="suffix" id="suffix" class="width-400" readonly="readonly">
				</td>
				<th class="req">결재방식</th>
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
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td colspan="5" class="indent7 pb8">
					<script type="text/javascript">
						new Dext5editor("content");
					</script>
				</td>
			</tr>
			<tr>
				<th class="lb">문서설명</th>
				<td colspan="5" class="indent5">
					<div class="textarea-auto">
						<textarea name="description" id="description" rows="5"></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb" id="required">주 첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
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

		<!-- 	관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 	관련 EO -->
		<jsp:include page="/extcore/jsp/change/eo/include/eo-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 	관련 CR -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 	관련 ECPR -->
		<jsp:include page="/extcore/jsp/change/ecpr/include/ecpr-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 	관련 ECO -->
		<jsp:include page="/extcore/jsp/change/eco/include/eco-include.jsp">
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
			function folder() {
				const location = decodeURIComponent("/Default/문서");
				const url = getCallUrl("/folder/popup?location=" + location);
				_popup(url, 500, 600, "n");
			}

			// 문서 등록
			function create() {
				const location = document.getElementById("location");
				const preFix = document.getElementById("preFix").value;
				const suffix = document.getElementById("suffix");
				const description = document.getElementById("description");
				const lifecycle = document.querySelector("input[name=lifecycle]:checked").value;
				const secondarys = toArray("secondarys");
				const primary = document.querySelector("input[name=primary]");
				const model = document.getElementById("model").value;
				const interalnumber = document.getElementById("interalnumber");
				const preseration = document.getElementById("preseration").value;

				// 클래스타입
				const clazz2 = document.getElementById("clazz");
				let classType2_oid;
				if (clazz2 !== null) {
					classType2_oid = clazz2.getAttribute("data-oid");
				}
				const classType1_code = document.getElementById("classType1").value;
				const classType2 = document.getElementById(classType2Id);

				const classType3_oid = document.getElementById("classType3").value;

				const url = getCallUrl("/doc/create");

				// 관련문서
				const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
				// 관련품목
				const rows91 = AUIGrid.getGridDataWithState(myGridID91, "gridState");
				// 관련EO
				const rows100 = AUIGrid.getGridDataWithState(myGridID100, "gridState");
				// 관련CR
				const rows101 = AUIGrid.getGridDataWithState(myGridID101, "gridState");
				// 관련ECPR
				const rows103 = AUIGrid.getGridDataWithState(myGridID103, "gridState");
				// 관련ECO
				const rows105 = AUIGrid.getGridDataWithState(myGridID105, "gridState");
				// 내용
				const content = DEXT5.getBodyValue("content");
				// 구 문서번호
				const oldNumber = document.getElementById("oldNumber").value;
				
				
				if (location.value === "/Default/문서") {
					alert("문서분류를 선택하세요.");
					folder();
					return false;
				}

				let name;
				let checker = true;
				// 개발문서일 경우 체크를 안한다..
				if ("DEV" === classType1_code) {
					checker = false;
				}

				// 회의록에 일반 희의록이 아닐 경우만
				if ("MEETING" === classType1_code) {
					if ("회의록" !== clazz2.value) {
						checker = false;
					}
				}

				if (checker) {
					if (suffix.value === "") {
						alert("문서명을 입력하세요,");
						suffix.focus();
						return false;
					}
				}

				if (preFix !== "") {
					name = preFix + suffix.value;
				} else {
					name = suffix.value;
				}

				if ("DEV" === classType1_code || "INSTRUCTION" === classType1_code) {
					if (model === "") {
						alert("프로젝트 코드를 선택하세요.");
						return false;
					}
				}

				if ("SOURCE" !== classType1_code) {
					if (interalnumber.value === "") {
						interalnumber.focus();
						alert("문서번호를 입력해주세요.");
						return false;
					}
				}

				const product = document.getElementById("product").value;
				if ("DEV" === classType1_code) {
					if ("DMR" === clazz2.value) {
						if (product === "") {
							alert("제품명을 입력하세요.");
							product.focus();
							return false;
						}
					}
				}

				if ("DEV" === classType1_code || "INSTRUCTION" === classType1_code) {
					if (primary == null) {
						alert("주 첨부파일을 첨부해주세요.");
						return false;
					}

					const ext = primary.value.split('.').pop().toLowerCase();
					if (ext !== "doc" && ext !== "docx") {
						alert("개발문서와 지침서는 주 첨부파일에 워드형식의 파일만 첨부 가능합니다.");
						return false;
					}
				}

				if (!confirm("저장하시겠습니까?")) {
					return false;
				}

				const params = {
					name : name,
					lifecycle : lifecycle,
					description : description.value,
					content : content,
					secondarys : secondarys,
					primary : primary != null ? primary.value : "",
					location : location.value,
					model_code : model,
					interalnumber : interalnumber.value,
					preseration_code : preseration,
					// 링크 데이터
					rows90 : rows90,
					rows91 : rows91,
					rows100 : rows100,
					rows101 : rows101,
					rows103 : rows103,
					rows105 : rows105,
					// 클래스타입
					classType1_code : classType1_code,
					classType2_oid : classType2_oid,
					classType3_oid : classType3_oid,
					product : product,
					oldNumber : oldNumber
				};
				logger(params);
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						parent.updateHeader();
						document.location.href = getCallUrl("/doc/list");
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				selectbox("preseration");
				selectbox("model");
				$("#preseration").bindSelectSetValue("PR001");
				createAUIGrid90(columns90);
				createAUIGrid91(columns91);
				createAUIGrid100(columns100);
				createAUIGrid101(columns101);
				createAUIGrid103(columns103);
				createAUIGrid105(columns105);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID91);
				AUIGrid.resize(myGridID100);
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID103);
				AUIGrid.resize(myGridID105);
				toUI();
				autoTextarea();
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID91);
				AUIGrid.resize(myGridID100);
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID103);
				AUIGrid.resize(myGridID105);
			});
		</script>
	</form>
</body>
</html>