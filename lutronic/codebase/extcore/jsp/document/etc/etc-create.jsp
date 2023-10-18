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
String type = request.getParameter("type");
ArrayList<NumberCode> preserationList = (ArrayList<NumberCode>) request.getAttribute("preserationList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<FormTemplate> form = (ArrayList<FormTemplate>) request.getAttribute("form");
JSONArray docTypeList = (JSONArray) request.getAttribute("docTypeList");
DocumentDTO dto = (DocumentDTO) request.getAttribute("dto");
String location = (String)request.getAttribute("location");
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
					<input type="hidden" name="location" id="location" value="<%=location%>">
					<span id="locationText"><%=location %></span>
					<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">
				</td>
				<th class="req">문서 템플릿</th>
				<td class="indent5">
					<select name="formType" id="formType" class="width-200" onchange="loadForm();">
						<option value=-"">선택</option>
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
				<th class="req lb">문서명</th>
				<td class="indent5">
					<input type="text" name="docName" id="docName" class="width-300">
				</td>
				<th class="req">문서종류</th>
				<td class="indent5">
					<input type="text" name="documentName" id="documentName" class="width-300">
					<div id="documentNameSearch" style="display: none; border: 1px solid black; position: absolute; background-color: white; z-index: 1;">
						<ul id="documentNameUL" style="list-style-type: none; padding-left: 5px; text-align: left;">
						</ul>
					</div>
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
			<tr>
				<th class="lb">결재</th>
				<td colspan="5">
					<jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/part/include/part-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="insert91" name="method" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>

		<!-- 	관련 기타 문서 -->
		<jsp:include page="/extcore/jsp/document/etc/include/etc-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="insert90" name="method" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
			<jsp:param value="<%= location %>" name="location" />
		</jsp:include>

		<!-- 	관련 EO -->
		<jsp:include page="/extcore/jsp/change/include_selectEO.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
		</jsp:include>

		<!-- 	관련 CR -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						관련 CR
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">관련 CR</th>
				<td class="indent5 pt5">
					<jsp:include page="/extcore/jsp/change/include_selectCr.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<!-- 	관련 ECPR -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						관련 ECPR
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">관련 ECPR</th>
				<td class="indent5 pt5">
					<jsp:include page="/extcore/jsp/change/include_selectEcpr.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<!-- 	관련 ECO -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						관련 ECO
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">관련 ECO</th>
				<td class="indent5 pt5">
					<jsp:include page="/extcore/jsp/change/eco_include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="기안" title="기안" class="red" onclick="create('false');">
					<input type="button" value="임시저장" title="임시저장" class="" onclick="create('true');">
				</td>
			</tr>
		</table>

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
				const location = decodeURIComponent("<%=location%>");
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
				const name = document.getElementById("docName");
				const documentType = document.getElementById("documentType");
				oEditors.getById["content"].exec("UPDATE_CONTENTS_FIELD", []);
				const content = document.getElementById("content");
				const description = document.getElementById("description");
				const lifecycle = document.querySelector("input[name=lifecycle]:checked").value;
				const secondarys = toArray("secondarys");
				const primary = document.querySelector("input[name=primary]").value;
				const model = document.getElementById("model").value;
				const writer = document.getElementById("writer").value;
				const interalnumber = document.getElementById("interalnumber").value;
				const deptcode = document.getElementById("deptcode").value;
				const preseration = document.getElementById("preseration").value;
				const documentName = document.getElementById("documentName").value;
				
				const temprary = JSON.parse(temp);
				const addRows8 = AUIGrid.getAddedRowItems(myGridID8);

				if (temprary) {
					if (!confirm("임시저장하시겠습니까??")) {
						return false;
					}
					
					if(addRows8){
						alert("결재선 지정을 해지해주세요.")
						return false;
					}
					
				} else {
					if (!confirm("등록하시겠습니까?")) {
						return false;
					}
				}
				
				const url = getCallUrl("/etc/create");

				// 관련문서
				const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
				// 관련품목
				const rows91 = AUIGrid.getGridDataWithState(myGridID91, "gridState");
				const params = {
					name : name.value,
					lifecycle : lifecycle,
					documentType_code : documentType.value,
					description : description.value,
					content : content.value,
					secondarys : secondarys,
					primary : primary,
					location : location.value,
					model_code : model,
					deptcode_code : deptcode,
					interalnumber : interalnumber,
					writer : writer,
					preseration_code : preseration,
					documentName : documentName,
					// 링크 데이터
					rows90 : rows90,
					rows91 : rows91
				};
				toRegister(params, addRows8); // 결재선 세팅
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/etc/list?type=<%= type %>");
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
				createAUIGrid8(columns8);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID91);
				AUIGrid.resize(myGridID8);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID91);
				AUIGrid.resize(myGridID8);
			});
		</script>
	</form>
</body>
</html>