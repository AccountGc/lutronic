<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%-- <%@include file="/extcore/jsp/common/aui/auigrid.jsp"%> --%>
<%-- <input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT %>"> --%>
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
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 문서 정보
				</div>
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
			<th class="req lb">EO 제목</th>
			<td class="indent5" ><input type="text" name="name" id="name" class="width-200"></td>
			<th class="req lb">EO 구분</th>
			<td>&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="eoType"  id="eoType" value="DEV" checked="checked">
						<div class="state p-success">
							<label> <b>개발</b>
							</label>
						</div>
					</div> &nbsp;
					<div class="pretty p-switch">
						<input type="radio"  name="eoType"  id="eoType"  value="PRODUCT">
						<div class="state p-success">
							<label> <b>양산</b>
							</label>
						</div>
					</div>
				</td>
		</tr>
		<tr>
			<th class="req lb">제품명</th>
			<td colspan="3">
				<input type="button" value="추가" title="추가" class="blue"  id="addNumberCode" name="addNumberCode"  onclick="addNumberCode();">
				<input type="button" value="삭제" title="삭제" class="red"   id="delNumberCode" name="delNumberCode"  onclick="delNumberCode();">
			</td>
		</tr>
		<tr>
			<th class="req lb">완제품 품목</th>
			<td colspan="3">
				<jsp:include page="/extcore/jsp/change/include_ecrCompletePart.jsp">
					<jsp:param value="" name="oid" />
					<jsp:param value="create" name="mode" />
				</jsp:include>
			</td>
		</tr>
		<tr>
			<th class="lb">제품 설계 개요</th>
			<td class="indent5" colspan="3"><textarea name="eoCommentA" id="eoCommentA" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="lb">특기사항</th>
			<td class="indent5" colspan="3"><textarea name="eoCommentB" id="eoCommentB" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="lb">기타사항</th>
			<td class="indent5" colspan="3"><textarea name="eoCommentC" id="eoCommentC" rows="6"></textarea></td>
		</tr>
		<tr>
			<th class="lb">첨부파일</th>
			<td class="indent5" colspan="3">
				<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
					<jsp:param value="" name="oid" />
				</jsp:include>
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button"  value="등록"  title="등록"  class="btnCRUD"  id="createBtn" name="createBtn" onclick="create()">
				<input type="button" value="초기화" title="초기화"  class="btnCRUD"  id="resetBtn" name="resetBtn">
				<input type="button" value="목록" title="목록"  class="btnCRUD"  id="listBtn" name="listBtn">
			</td>
		</tr>
	</table>
	<script type="text/javascript">
		function folder() {
			const location = decodeURIComponent("/Default/문서");
			const url = getCallUrl("/folder?location=" + location + "&container=product&method=setNumber&multi=false");
			popup(url, 500, 600);
		}

		function setNumber(item) {
			const url = getCallUrl("/doc/setNumber");
			const params = new Object();
			params.loc = item.location;
			call(url, params, function(data) {
				document.getElementById("loc").innerHTML = item.location;
				document.getElementById("location").value = item.location;
				document.getElementById("number").value = data.number;
			})
		}
		
		document.addEventListener("DOMContentLoaded", function() {
			selectbox("model");
			selectbox("preseration");
			selectbox("documentType");
			selectbox("deptcode");
		});

		function create() {
			const name = document.getElementById("name").value;
			const eoType = document.getElementById("eoType").value;
			/* const models = document.getElementById("models").value;
			const completeOids = document.getElementById("completeOids").value; */
			const eoCommentA = document.getElementById("eoCommentA").value;
			const eoCommentB = document.getElementById("eoCommentB").value;
			const eoCommentC = document.getElementById("eoCommentC").value;
			/* const secondarys = document.getElementById("secondarys").value; */
			

			/* if (location === "/Default/문서") {
				alert("문서 저장위치를 선택하세요.");
				folder();
				return false;
			}

			if (isNull(name.value)) {
				alert("문서제목을 입력하세요.");
				name.focus();
				return false;
			} */

			// 		if(addRows11.length === 0) {
			// 			alert("도번을 추가하세요.");
			// 			insert11();
			// 			return false;
			// 		}

			/* if (primarys.length === 0) {
				alert("첨부파일을 선택하세요.");
				return false;
			} */

			if (!confirm("등록 하시겠습니까?")) {
				return false;
			}

			const params = new Object();
			const url = getCallUrl("/changeECO/createEOAction");
			params.name = name.value;
			params.eoType = eoType.value;
			/* params.models = models.value;
			params.completeOids = completeOids.value; */
			params.eoCommentA = eoCommentA.value;
			params.eoCommentB = eoCommentB.value;
			params.eoCommentC = eoCommentC.value;
			/* params.secondarys = secondarys.value; */
			/* params.self = JSON.parse(isSelf); */
			/* toRegister(params, addRows8);
			openLayer(); */
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					opener.loadGridData();
					/* self.close(); */
				} else {
					/* closeLayer(); */
				}
			});
		};
		
		function addNumberCode(){
			const url = getCallUrl("/common/popup_numberCodes?codeType=MODEL&disable=true");
			popup(url, 1500, 700);
		};
		
		function delNumberCode(){
			
		}

		// jquery 삭제를 해가는 쪽으로 한다..
		document.addEventListener("DOMContentLoaded", function() {
			// DOM이 로드된 후 실행할 코드 작성
			createAUIGrid2(columnsPart);
			AUIGrid.resize(partGridID);
			/* createAUIGrid7(columns7);
			createAUIGrid11(columns11);
			createAUIGrid8(columns8);
			AUIGrid.resize(myGridID7);
			AUIGrid.resize(myGridID11);
			AUIGrid.resize(myGridID8); */
			document.getElementById("name").focus();
		});

		window.addEventListener("resize", function() {
			AUIGrid.resize(partGridID);
// 			AUIGrid.resize(myGridID7);
// 			AUIGrid.resize(myGridID11);
// 			AUIGrid.resize(myGridID8);
		});
	</script>
</body>
</html>