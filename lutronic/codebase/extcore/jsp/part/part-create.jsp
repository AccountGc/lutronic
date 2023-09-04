<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<NumberCode> partTypeList = (ArrayList<NumberCode>) request.getAttribute("partTypeList");
	ArrayList<NumberCode> partName1List = (ArrayList<NumberCode>) request.getAttribute("partName1List");
	ArrayList<NumberCode> partName2List = (ArrayList<NumberCode>) request.getAttribute("partName2List");
	ArrayList<NumberCode> partName3List = (ArrayList<NumberCode>) request.getAttribute("partName3List");
%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
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
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="lastNum" id="lastNum">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid">
		<input type="hidden" name="wtPartType"		id="wtPartType"		 	value="separable"     />
		<input type="hidden" name="source"			id="source"	      		value="make"            />
		<input type="hidden" name="lifecycle"   	id="lifecycle"			value="LC_PART"  />
		<input type="hidden" name="view"			id="view"        		value="Design" />
		<input type="hidden" name="fid" 			id="fid"				value="" >
		<input type="hidden" name="location" 		id="location" 				value="/Default/PART_Drawing">
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 채번 정보
					</div>
				</td>
			</tr>
		</table>
		
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="130">
				<col width="130">
				<col width="130">
				<col width="130">
				<col width="130">
			</colgroup>
			<tr>
				<th>품목분류 <span style="color:red;">*</span></th>
				<td class="indent5" colspan="2">
					<span id="locationName">
						/Default/PART_Drawing
					</span>
				</td>
				<th rowspan="4">품목명 <span style="color:red;">*</span></th>
				<th>대제목</th>
				<td class="indent5"><select name="partName1"  id="partName1" class="width-300">
						<option value="">선택</option>
						<%
						for (NumberCode partName1 : partName1List) {
						%>
						<option value="<%=partName1.getCode() %>"><%=partName1.getName()%></option>
						<%
						}
						%>
				</select></td>
			</tr>
			<tr>
				<th>품목구분 <span style="color:red;">*</span></th>
				<td class="indent5" colspan="2">
					<select id="partType1" name="partType1" style="width: 95%">
						<option value="1">
							선택
						</option>
					</select>
				</td>
				<th>중제목</th>
				<td class="indent5"><select name="partName2"  id="partName2" class="width-300">
						<option value="">선택</option>
						<%
						for (NumberCode partName2 : partName2List) {
						%>
						<option value="<%=partName2.getCode() %>"><%=partName2.getName()%></option>
						<%
						}
						%>
				</select></td>
			</tr>
			<tr>
				<th>대분류 <span style="color:red;">*</span></th>
				<td class="indent5" colspan="2">
					<select id="partType2" name="partType2" style="width: 95%">
						<option value="01">
							선택
						</option>
					</select>
				</td>
				<th>소제목</th>
				<td class="indent5"><select name="partName3"  id="partName3" class="width-300">
						<option value="">선택</option>
						<%
						for (NumberCode partName3 : partName3List) {
						%>
						<option value="<%=partName3.getCode() %>"><%=partName3.getName()%></option>
						<%
						}
						%>
				</select></td>
			</tr>
			<tr>
				<th>중분류 <span style="color:red;">*</span></th>
				<td class="indent5" colspan="2">
					<select id="partType3" name="partType3" style="width: 95%">
						<option value="01">
							선택
						</option>
					</select>
				</td>
				<th>사용자 Key in</th>
				<td class="indent5">
					<input type="text" name="partName4" id="partName4" class="width-300">
				</td>
			</tr>
			<tr>
				<th>SEQ <br><span style="color:red;">(3자리)</span></th>
				<td class="indent5" colspan="2">
					<input type="text" name="seq" id="seq" class="width-200">
					<input type="button" id="seqList" class="btnSearch" value="SEQ 현황보기" title="SEQ 현황보기" onclick="loadGridData();">
				</td>
				<td class="indent5" colspan="3">
					<div id="partTypeNum" style="padding-left: 45%;font-weight:bold; vertical-align:middle; float: left;"></div>
					<div id="manualNum">
						<div id="seqNum" style="font-weight:bold; vertical-align:middle; float: left;"></div>
						<div id="etcNum" style="font-weight:bold; vertical-align:middle; float: left;"></div>
					</div>
					<br>
					<div>
						<span style="font-weight: bold; vertical-align: middle;" id="displayName"></span>
					</div>
				</td>
			</tr>
			<tr>
				<th>기타 <br><span style="color:red;">(2자리)</span></th>
				<td class="indent5" colspan="5">
					<input type="text" name="etc" id="etc" class="width-200">
				</td>
			</tr>
		</table>
		
		<br>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 품목 속성
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
				<th>프로젝트코드 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="model" id="model" class="width-500">
				</td>
				<th>제작방법 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="productmethod" id="productmethod" class="width-500">
				</td>
			</tr>
			<tr>
				<th>부서 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="deptcode" id="deptcode" class="width-500">
				</td>
				<th>단위 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="unit" id="unit" class="width-500">
				</td>
			</tr>
			<tr>
				<th>무게(g)</th>
				<td class="indent5">
					<input type="text" name="weight" id="weight" class="width-500">
				</td>
				<th>MANUFACTURER</th>
				<td class="indent5">
					<input type="text" name="manufacture" id="manufacture" class="width-500">
				</td>
			</tr>
			<tr>
				<th>재질</th>
				<td class="indent5">
					<input type="text" name="mat" id="mat" class="width-500">
				</td>
				<th>후처리</th>
				<td class="indent5">
					<input type="text" name="finish" id="finish" class="width-500">
				</td>
			</tr>
			<tr>
				<th>OEM Info.</th>
				<td class="indent5">
					<input type="text" name="remarks" id="remarks" class="width-500">
				</td>
				<th>사양</th>
				<td class="indent5">
					<input type="text" name="specification" id="specification" class="width-500">
				</td>
			</tr>
		</table>
		<br>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 주 도면&nbsp;&nbsp;
						<span class="red">(메카 : CAD파일), (광학/제어/파워/인증 : PDF파일)</span>
					</div>
				</td>
			</tr>
		</table>
		
		<table class="search-table">
			<colgroup>
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th>주 도면</th>
				<td class="indent5" colspan="3">
				</td>
			</tr>
		</table>
		<br>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 첨부파일&nbsp;&nbsp;
						<span class="red">(제어/파워 : 배포파일)</span>
					</div>
				</td>
			</tr>
		</table>
		
		<table class="search-table">
			<colgroup>
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<br>
		
		<!-- 관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp">
			<jsp:param value="관련 문서" name="title"/>
			<jsp:param value="docOid" name="paramName"/>
		</jsp:include>
		<br>
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 관련 RoHS
					</div>
				</td>
			</tr>
		</table>
		
		<table class="search-table">
			<colgroup>
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th>관련 RoHS</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/rohs/include_selectRohs.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" id="createBtn" class="blue" onclick="create('false');" />
					<input type="button" value="초기화" title="초기화" id="resetBtn" />
					<input type="button" value="목록" title="목록" id="listBtn" />
				</td>
			</tr>
		</table>

		<script type="text/javascript">

			function create(isSelf) {
				const partName1 = document.getElementById("partName1").value;
				const partType1 = document.getElementById("partType1").value;
				const partName2 = document.getElementById("partName2").value;
				const partType2 = document.getElementById("partType2").value;
				const partName3 = document.getElementById("partName3").value;
				const partType3 = document.getElementById("partType3").value;
				const partName4 = document.getElementById("partName4").value;
				const seq = document.getElementById("seq").value;
				const etc = document.getElementById("etc").value;
				const model = document.getElementById("model").value;
				const productmethod = document.getElementById("productmethod").value;
				const deptcode = document.getElementById("deptcode").value;
				const weight = document.getElementById("weight").value;
				const manufacture = document.getElementById("manufacture").value;
				const mat = document.getElementById("mat").value;
				const finish = document.getElementById("finish").value;
				const remarks = document.getElementById("remarks").value;
				const specification = document.getElementById("specification").value;
				const unit = "EA";
//	 			const addRows7 = AUIGrid.getAddedRowItems(myGridID7);
//	 			const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
//	 			const addRows11 = AUIGrid.getAddedRowItems(myGridID11);
				const primarys = toArray("primarys");
				const wtPartType = document.getElementById("wtPartType").value;
				const source = document.getElementById("source").value;
				const lifecycle = document.getElementById("lifecycle").value;
				const view = document.getElementById("view").value;
				const fid = document.getElementById("fid").value;
				const location = document.getElementById("location").value;

				if(isEmpty($("#partName1").val()) || isEmpty($("#partName2").val()) || isEmpty($("#partName3").val()) || isEmpty($("#partName4").val())){
					alert("품목명을 입력하세요.");
					return;					
				}
				if(isEmpty($("#partType1").val())){
					alert("품목구분을 입력하세요.");
					return;					
				}
				if(isEmpty($("#partType2").val())){
					alert("대분류를 입력하세요.");
					return;					
				}
				if(isEmpty($("#partType3").val())){
					alert("중분류를 입력하세요.");
					return;					
				}
				if(isEmpty($("#model").val())){
					alert("프로젝트 코드를 입력하세요.");
					return;					
				}
				if(isEmpty($("#productmethod").val())){
					alert("제작방법을 입력하세요.");
					return;					
				}
				if(isEmpty($("#deptcode").val())){
					alert("부서를 입력하세요.");
					return;					
				}
				if(isEmpty($("#unit").val())){
					alert("단위를 입력하세요.");
					return;					
				}
				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}
				
				const params = new Object();
				const url = getCallUrl("/part/create");
				params.partName1 = "MODULE";
				params.partType1 = partType1;
				params.partName2 = "BOARD";
				params.partType2 = partType2;
				params.partName3 = "LD DRIVER";
				params.partType3 = partType3;
				params.partName4 = "";
				params.seq = seq;
				params.etc = etc;
				params.model = model;
				params.productmethod = productmethod;
				params.deptcode = deptcode;
				params.weight = weight;
				params.manufacture = manufacture;
				params.mat = mat;
				params.finish = finish;
				params.remarks = remarks;
				params.specification = specification;
				params.unit = "ea";
//	 			params.addRows7 = addRows7;
//	 			params.addRows11 = addRows11;
				params.primarys = primarys;
				params.wtPartType = wtPartType;
				params.source = source;
				params.lifecycle = lifecycle;
				params.view = view;
				params.fid = fid;
				params.location = location;
				
//	 			toRegister(params, addRows8);
//	 			openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
//	 					opener.loadGridData();
//	 					self.close();
					} else {
//	 					closeLayer();
					}
				});
			};

			function exportExcel() {
// 				const exceptColumnFields = [ "primary" ];
// 				const sessionName = document.getElementById("sessionName").value;
// 				exportToExcel("문서 리스트", "문서", "문서 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
// 					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})
			
			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid4(columnsDoc);
				AUIGrid.resize(docGridID);
				createAUIGrid6(columnsRohs);
				AUIGrid.resize(rohsGridID);
				document.getElementById("partName1").focus();
			});
			
			window.addEventListener("resize", function() {
				AUIGrid.resize(docGridID);
				AUIGrid.resize(rohsGridID);
			});
			
			<%----------------------------------------------------------
			*                      NumberCode 리스트 가져오기
			----------------------------------------------------------%>
// 			const numberCodeList = (id, parentCode1) => {
// 				var type = "";
// 				if(id == 'partType1' || id == 'partType2' || id =='partType3') {
// 					type = "PARTTYPE";
// 				}else {
// 					type = id.toUpperCase();
// 				}
				
// 				let data = common_numberCodeList(type, parentCode1, false);
				
// 				addSelectList(id, eval(data.responseText));
// 			}

			<%----------------------------------------------------------
			*                      selectBodx에 옵션 추가
			----------------------------------------------------------%>
// 			const addSelectList = (id,data) => {
// 				const removeId = "#"+ id + " option";
// 				document.querySelector(removeId).remove();
// 				const selectId = "#"+ id;
// 				document.querySelector(selectId).innerHTML = "<option value='' title='' > 선택 </option>";
// 				if(data.length > 0) {
// 					for(var i=0; i<data.length; i++) {
// 						let value = "<option value='" + data[i].code + "' title='" + data[i].oid + "' > [" + data[i].code + "] " + data[i].name + "</option>";
// 						document.querySelector(selectId).innerHTML += value;
// 					}
// 				}
// 			}
		</script>
	</form>
</body>
</html>