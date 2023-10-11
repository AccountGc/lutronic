<%@page import="net.sf.json.JSONObject"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@page import="wt.doc.DocumentType"%>
<%@page import="com.e3ps.admin.form.FormTemplate"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> preserationList = (ArrayList<NumberCode>) request.getAttribute("preserationList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<FormTemplate> form = (ArrayList<FormTemplate>) request.getAttribute("form");
JSONArray docTypeList = (JSONArray) request.getAttribute("docTypeList");
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
<script type="text/javascript" src="/Windchill/extcore/smarteditor2/js/HuskyEZCreator.js"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서 정보
					</div>
				</td>
				<td class="right">
					<input type="button" value="기안" title="기안" class="red" onclick="create('false');">
					<input type="button" value="결재선 지정" title="결재선 지정" class="blue" onclick="">
					<input type="button" value="임시저장" title="임시저장" class="" onclick="create('true');">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">문서분류</th>
				<td class="indent5" colspan="3">
					<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT%>">
					<span id="locationText"> /Default/문서 </span>
					<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">
				</td>
				<th class="req">문서 템플릿</th>
				<td class="indent5">
					<select name="formType" id="formType" class="width-200" onchange="loadForm();">
						<option value="">선택</option>
						<%
						for (FormTemplate formType : form) {
						%>
						<option value="<%=formType.getPersistInfo().getObjectIdentifier().getStringValue()%>"><%=formType.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb">문서명</th>
				<td class="indent5">
					<input type="text" name="docName" id="docName" class="width-300">
				</td>
				<th class="req">문서종류</th>
				<td class="indent5">
					<input type="text" name="documentName" id="documentName" class="width-300">
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
				<th class="req lb">문서유형</th>
				<td class="indent5">
					<select name="documentType" id="documentType" class="width-200">
						<option value="">선택</option>
						<%
						for (int i = 0; i < docTypeList.size(); i++) {
							JSONObject obj = (JSONObject) docTypeList.get(i);
							String key = (String) obj.get("key");
							String value = (String) obj.get("value");
						%>
						<option value="<%=key%>"><%=value%></option>
						<%
						}
						%>
					</select>
				</td>
				<th class="req">보존기간</th>
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
				<th>프로젝트코드</th>
				<td class="indent5">
					<select name="model" id="model" class="width-200">
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
			</tr>
			<tr>
				<th class="lb">부서</th>
				<td class="indent5">
					<select name="deptcode" id="deptcode" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode deptcode : deptcodeList) {
						%>
						<option value="<%=deptcode.getCode()%>"><%=deptcode.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>내부 문서번호</th>
				<td class="indent5">
					<input type="text" name="interalnumber" id="interalnumber" class="width-200">
				</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="writer" id="writer" class="width-200">
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td colspan="5" class="indent5">
					<textarea name="content" id="content" rows="30"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">문서설명</th>
				<td colspan="5" class="indent5">
					<textarea name="description" id="description" rows="5"></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">주 첨부파일</th>
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
			<jsp:param value="250" name="height" />
		</jsp:include>

		<!-- 	관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>

		<!-- 	관련 EO -->
		<jsp:include page="/extcore/jsp/change/eo/include/eo-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>

		<!-- 	관련 CR -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>

		<!-- 	관련 ECPR -->

		<!-- 	관련 ECO -->
		<jsp:include page="/extcore/jsp/change/eco/include/eco-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>

		<script type="text/javascript">
			const oEditors = [];
			nhn.husky.EZCreator.createInIFrame({
				oAppRef : oEditors,
				elPlaceHolder : "content", //textarea ID 입력
				sSkinURI : "/Windchill/extcore/smarteditor2/SmartEditor2Skin.html", //martEditor2Skin.html 경로 입력
				fCreator : "createSEditor2",
				htParams : {
					// 툴바 사용 여부 (true:사용/ false:사용하지 않음) 
					bUseToolbar : true,
					// 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음) 
					bUseVerticalResizer : false,
					// 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음) 
					bUseModeChanger : false
				},
				fOnAppLoad : function() {
					//기존 저장된 내용의 text 내용을 에디터상에 뿌려주고자 할때 사용
					// 					oEditors.getById["description"].exec("PASTE_HTML", [ "기존 DB에 저장된 내용을 에디터에 적용할 문구" ]);
				},
			});

			function folder() {
				const location = decodeURIComponent("/Default/문서");
				const url = getCallUrl("/folder/popup?location=" + location);
				_popup(url, 500, 600, "n");
			}

			function loadForm() {
				const oid = document.getElementById("formType").value;
				if (oid === "") {
					return false;
				}
				const url = getCallUrl("/form/html?oid=" + oid);
				parent.openLayer();
				call(url, null, function(data) {
					if (data.result) {
						oEditors.getById["content"].exec("PASTE_HTML", [ data.html ]);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				}, "GET");
			}

			// 문서 등록
			function create(temp) {
				// temp 임시저장 여부 처리
				const location = document.getElementById("location");
				const formType = document.getElementById("formType");
				const name = document.getElementById("docName");
				const documentType = document.getElementById("documentType");
				oEditors.getById["content"].exec("UPDATE_CONTENTS_FIELD", []);
				const content = document.getElementById("content");
				const description = document.getElementById("description");
				const lifecycle = document.querySelector("input[name=lifecycle]:checked").value;
				const secondarys = toArray("secondarys");
				const primary = document.querySelector("input[name=primary]");
				const model = document.getElementById("model").value;
				const writer = document.getElementById("writer").value;
				const interalnumber = document.getElementById("interalnumber").value;
				const deptcode = document.getElementById("deptcode").value;
				const preseration = document.getElementById("preseration").value;
				const documentName = document.getElementById("documentName");
				const temprary = JSON.parse(temp);

				const url = getCallUrl("/doc/create");

				// 관련문서
				const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
				// 관련품목
				const rows91 = AUIGrid.getGridDataWithState(myGridID91, "gridState");
				// 관련EO
				const rows100 = AUIGrid.getGridDataWithState(myGridID100, "gridState");
				// 관련CR
				const rows101 = AUIGrid.getGridDataWithState(myGridID101, "gridState");
				// 관련ECO
				const rows105 = AUIGrid.getGridDataWithState(myGridID105, "gridState");

				if (isNull(documentName.value)) {
					alert("문서종류를 입력해주세요.");
					documentName.focus();
					return false;
				}

				if (isNull(documentType.value)) {
					alert("문서유형을 선택해주세요.");
					return false;
				}

				if (primary == null) {
					alert("주 첨부파일을 첨부해주세요.");
					return false;
				}

				if (temprary) {
					if (!confirm("임시저장하시겠습니까??")) {
						return false;
					}
				} else {
					if (!confirm("등록하시겠습니까?")) {
						return false;
					}
				}

				const params = {
					name : name.value,
					lifecycle : lifecycle,
					documentType_code : documentType.value,
					description : description.value,
					content : content.value,
					secondarys : secondarys,
					primary : primary.value,
					location : location.value,
					model_code : model,
					deptcode_code : deptcode,
					interalnumber : interalnumber,
					writer : writer,
					preseration_code : preseration,
					documentName : documentName.value,
					// 링크 데이터
					rows90 : rows90,
					rows91 : rows91,
					rows100 : rows100,
					rows101 : rows101,
					rows105 : rows105,
					temprary : temprary
				};
				parent.openLayer();
				
				console.log(params);
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/doc/list");
					}
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				selectbox("formType");
				selectbox("preseration");
				selectbox("documentType");
				selectbox("model");
				selectbox("deptcode");
				$("#preseration").bindSelectSetValue("PR001");
				createAUIGrid90(columns90);
				createAUIGrid91(columns91);
				createAUIGrid100(columns100);
				createAUIGrid101(columns101);
				createAUIGrid105(columns105);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID91);
				AUIGrid.resize(myGridID100);
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID105);

				// 문서명 규칙
				$("#documentName").bindSelector({
					reserveKeys : {
						options : "list",
						optionValue : "value",
						optionText : "name"
					},
					optionPrintLength : "all",
					onsearch : function(id, obj, callBack) {
						const value = document.getElementById(id).value;
						const url = getCallUrl("/doc/finder");
						const params = {
							value : value,
						};
						logger(params);
						call(url, params, function(data) {
							callBack({
								options : data.list
							})
						})
					},
					onchange : function() {
						const id = this.targetID;
						if (this.selectedOption != null) {
							const value = this.selectedOption.value;
							document.getElementById(id).value = value;
						}
					},
				});
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID91);
				AUIGrid.resize(myGridID100);
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID105);
			});
		</script>
	</form>
</body>
</html>