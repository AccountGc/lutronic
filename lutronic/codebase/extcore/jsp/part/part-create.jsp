<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> matList = (ArrayList<NumberCode>) request.getAttribute("matList");
ArrayList<NumberCode> productmethodList = (ArrayList<NumberCode>) request.getAttribute("productmethodList");
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
ArrayList<NumberCode> finishList = (ArrayList<NumberCode>) request.getAttribute("finishList");
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
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th>품목분류 <span style="color:red;">*</span></th>
				<td class="indent5">
					<span id="locationName">
						/Default/PART_Drawing
					</span>
				</td>
				<th rowspan="4">품목명 <span style="color:red;">*</span></th>
				<th>대제목</th>
				<td class="indent5">
					<input id="partName1" name="partName1" class='partName' type="text" style="width: 95%;">
					<div id="partName1Search" style="width: 250px; display: none; border: 1px solid black ; position: absolute; background-color: white;">
						<ul id="partName1UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<th>품목구분 <span style="color:red;">*</span></th>
				<td class="indent5">
					<select id="partType1" name="partType1" style="width: 95%">
						<option value="">
							선택
						</option>
					</select>
				</td>
				<th>중제목</th>
				<td class="indent5">
					<input id="partName2" name="partName2" class='partName' type="text" style="width: 95%">
					<div id="partName2Search" style="width: 250px; display: none; border: 1px solid black ; position: absolute; background-color: white;">
						<ul id="partName2UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<th>대분류 <span style="color:red;">*</span></th>
				<td class="indent5">
					<select id="partType2" name="partType2" style="width: 95%">
						<option value="">
							선택
						</option>
					</select>
				</td>
				<th>소제목</th>
				<td class="indent5">
					<input id="partName3" name="partName3" class='partName' type="text" style="width: 95%">
					<div id="partName3Search" style="width: 250px; display: none; border: 1px solid black ; position: absolute; background-color: white;">
						<ul id="partName3UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<th>중분류 <span style="color:red;">*</span></th>
				<td class="indent5">
					<select id="partType3" name="partType3" style="width: 95%">
						<option value="">
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
				<td class="indent5">
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
				<td class="indent5" colspan="4">
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
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th>프로젝트코드 <span style="color:red;">*</span></th>
				<td class="indent5">
					<select name="model" id="model" class="width-500">
						<option value="">선택</option>
						<%
						for (NumberCode model : modelList) {
						%>
						<option value="<%=model.getCode() %>"><%=model.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>제작방법 <span style="color:red;">*</span></th>
				<td class="indent5">
					<select name="productmethod" id="productmethod" class="width-500">
						<option value="">선택</option>
						<%
						for (NumberCode productmethod : productmethodList) {
						%>
						<option value="<%=productmethod.getCode() %>"><%=productmethod.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>부서 <span style="color:red;">*</span></th>
				<td class="indent5">
					<select name="deptcode" id="deptcode" class="width-500">
						<option value="">선택</option>
						<%
						for (NumberCode deptcode : deptcodeList) {
						%>
						<option value="<%=deptcode.getCode() %>"><%=deptcode.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>단위 <span style="color:red;">*</span></th>
				<td class="indent5">
					<select name="unit" id="unit" class="width-500">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>무게(g)</th>
				<td class="indent5">
					<input type="text" name="weight" id="weight" class="width-500">
				</td>
				<th>MANUFACTURER</th>
				<td class="indent5">
					<select name="manufacture" id="manufacture" class="width-500">
						<option value="">선택</option>
						<%
						for (NumberCode manufacture : manufactureList) {
						%>
						<option value="<%=manufacture.getCode() %>"><%=manufacture.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>재질</th>
				<td class="indent5">
					<select name="mat" id="mat" class="width-500">
						<option value="">선택</option>
						<%
						for (NumberCode mat : matList) {
						%>
						<option value="<%=mat.getCode() %>"><%=mat.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>후처리</th>
				<td class="indent5">
					<select name="finish" id="finish" class="width-500">
						<option value="">선택</option>
						<%
						for (NumberCode finish : finishList) {
						%>
						<option value="<%=finish.getCode() %>"><%=finish.getName()%></option>
						<%
						}
						%>
					</select>
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
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th>주 도면</th>
				<td class="indent5" >
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
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5">
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
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th>관련 RoHS</th>
				<td class="indent5" >
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
				if(isEmpty($("#partName1").val()) || isEmpty($("#partName2").val()) || isEmpty($("#partName3").val()) || isEmpty($("#partName4").val())){
					alert("품목명을 입력하세요.");
					return;					
				}
				if(isEmpty($("#seq").val())){
					alert("SEQ를 입력하세요.");
					return;					
				}
				if(isEmpty($("#etc").val())){
					alert("기타를 입력하세요.");
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
				params.partName1 = partName1;
				params.partType1 = partType1;
				params.partName2 = partName2;
				params.partType2 = partType2;
				params.partName3 = partName3;
				params.partType3 = partType3;
				params.partName4 = partName4;
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
				createAUIGrid90(columns90);
				AUIGrid.resize(myGridID90);
				createAUIGrid6(columnsRohs);
				AUIGrid.resize(rohsGridID);
				document.getElementById("partName1").focus();
				numberCodeList('partType1', '');
				selectbox("partType2");
				selectbox("partType3");
				selectbox("state");
				selectbox("model");
				selectbox("productmethod");
				selectbox("deptcode");
				selectbox("unit");
				selectbox("mat");
				selectbox("finish");
				selectbox("manufacture");
				finderUser("creator");
				twindate("created");
				twindate("modified");
				selectbox("_psize");
			});
			
			window.addEventListener("resize", function() {
				AUIGrid.resize(docGridID);
				AUIGrid.resize(rohsGridID);
			});
			
			<%----------------------------------------------------------
			*                      품목구분 변경시
			----------------------------------------------------------%>
			$("#partType1").change(function() {
				numberCodeList('partType2', $("#partType1 option:selected").attr("title"));
				$("#partTypeNum").html(this.value);
			})
			<%----------------------------------------------------------
			*                      대분류 변경시
			----------------------------------------------------------%>
			$("#partType2").change(function() {
				numberCodeList('partType3', $("#codeNum").html() + $("#partType2 option:selected").attr("title"));
				$("#partTypeNum").html($("#partType1").val() + this.value);
			})
			<%----------------------------------------------------------
			*                      중분류 변경시
			----------------------------------------------------------%>
			$("#partType3").change(function() {
				$("#partTypeNum").html($("#partType1").val() + $("#partType2").val() + this.value);
			})
			
			<%----------------------------------------------------------
			*                      SEQ 입력 중
			----------------------------------------------------------%>
			$("#seq").keypress(function (event) {
				if($("#partType3").val() == "" ) {
					alert("제품분류을(를) 선택하세요.");
					return false;
				}else {
					return common_isNumber(event, this);
				}
		    })
		    
		    <%----------------------------------------------------------
			*                      SEQ 입력시
			----------------------------------------------------------%>
			$("#seq").keyup(function() {
				var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
				$("#seq").val(result);
			})
			
			<%----------------------------------------------------------
			*                      SEQ 입력 후 focus 이동시
			----------------------------------------------------------%>
			$("#seq").focusout(function() {
				if($("#partType3").val() != "" && $.trim(this.value) ) {
					$("#seqNum").html(this.value);
				}
			})
		    
			<%----------------------------------------------------------
			*                      기타 입력 중
			----------------------------------------------------------%>
			$("#etc").keypress(function (event) {
				if($.trim($("#seq").val()) == "" ) {
					alert("SEQ을(를) 입력하세요.");
					return false;
				}else{
					return common_isNumber(event, this);
				}
		    })
		    
			<%----------------------------------------------------------
			*                      기타 입력시
			----------------------------------------------------------%>
			$("#etc").keyup(function() {
				var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
				$("#etc").val(result);
			})
			
			<%----------------------------------------------------------
			*                      기타 입력 후 focus 이동시
			----------------------------------------------------------%>
			$("#etc").focusout(function() {
				if($.trim($("#seq").val()) != "" && $.trim(this.value) ) {
					$("#etcNum").html(this.value);
				}
			})
	
			// NumberCode 리스트 가져오기
			const numberCodeList = (id, parentCode1) => {
				var type = "";
				if(id == 'partType1' || id == 'partType2' || id =='partType3') {
					type = "PARTTYPE";
				}else {
					type = id.toUpperCase();
				}
				
				let data = [];
				
				part_numberCodeList(type, parentCode1)
				    .then(result => {
				    	addSelectList(id, result);
				    	selectbox(id);
				    })
				    .catch(error => {
				        console.error(error);
				    });
				    
			}

			// PartType Select태그 option 추가
			const addSelectList = (id,data) => {
				const removeId = "#"+ id + " option";
				document.querySelector(removeId).remove();
				const selectId = "#"+ id;
				document.querySelector(selectId).innerHTML = "<option value='' title='' > 선택 </option>";
				if(data.length > 0) {
					for(let i=0; i<data.length; i++) {
						let value = "<option value='" + data[i].code + "' title='" + data[i].oid + "' > [" + data[i].code + "] " + data[i].name + "</option>";
						document.querySelector(selectId).innerHTML += value;
					}
				}
			}
			
			// PartType 리스트 가져오기
			function part_numberCodeList(type, parentOid) {
			    const url = getCallUrl("/common/numberCodeList");
			    const params = {
			        codeType: type,
			        parentOid: parentOid
			    };

			    return new Promise((resolve, reject) => {
			        call(url, params, function(dataList) {
			            const result = dataList.map(data => data); 
			            resolve(result); 
			        });
			    });
			}

			// 품목명 입력 시 
			$(".partName").keyup(function (event) {
				var charCode = (event.which) ? event.which : event.keyCode;
				if((charCode == 38 || charCode == 40) ) {
					if(!$( "#"+this.id+"Search" ).is( ":hidden" )){
						var isAdd = false;
						if(charCode == 38){
							isAdd = true;
						}
						movePartNameFocus(this.id, isAdd);
					}
				} else if(charCode == 13 || charCode == 27){
					$("#" + this.id + "Search").hide();
				} else if(charCode == 8) {
					if($.trim($(this).val()) == '') {
						$("#" + this.id + "Search").hide();
					} else {
						autoSearchPartName(this.id, this.value);
					}
				} else {
					autoSearchPartName(this.id, this.value);
				}
			})
			
			<%----------------------------------------------------------
			*                      ↑,↓ 입력시
			----------------------------------------------------------%>
			const movePartNameFocus = function(id,isAdd) {
				var removeCount = 0;
				var addCount = 0;
				var l = $("#" + id + "UL li").length;
				for(var i=0; i<l; i++){
					var cls = $("#" + id + "UL li").eq(i).attr('class');
					if(cls == 'hover') {
						$("#" + id + "UL li").eq(i).removeClass("hover");
						removeCount = i;
						if(isAdd){
							addCount = (i-1);
						}else if(!isAdd) {
							addCount = (i+1);
						}
						break;
					}
				}
				if(addCount == l) {
					addCount = 0;
				}
				$("#" + id + "UL li").eq(addCount).addClass("hover");
				$("#" + id).val($("#" + id + "UL li").eq(addCount).text());
			}
			
			// partName 입력 시 partName 리스트 출력 메서드
			const autoSearchPartName = function(id, value) {
				if($.trim(value) == "") {
					addSearchList(id, '', true);
				} else {
					var codeType = id.toUpperCase();
					
					autoSearchName(codeType, value)
					.then(result => {
						addSearchList(id, result, false);
					})
					.catch(error => {
						console.error(error);
					})
				}
			}
			
			// partName 가져오기 메서드
			const autoSearchName = function(codeType, value) {
				
				const url = getCallUrl("/common/autoSearchName");
				 const params = {
					 codeType: codeType,	
					 value: value
			    };
				 
				 return new Promise((resolve, reject) => {
			        call(url, params, function(dataList) {
			            const result = dataList.map(data => data); 
			            resolve(result); 
			        });
			    });
			}
			
			<%----------------------------------------------------------
			*                      품목명 입력시 데이터 리스트 보여주기
			----------------------------------------------------------%>
			const addSearchList = function(id, data, isRemove) {
				$("#" + id + "UL li").remove();
				if(isRemove) {
					$("#" + this.id + "Search").hide();
				} else{
					if(data.length > 0) {
						$("#" + id + "Search").show();
						for(var i=0; i<data.length; i++) {
							$("#" + id + "UL").append("<li title='" + id + "'>" + data[i].name);
						}
					} else {
						$("#" + id + "Search").hide();
					}
				}
			}
			
			<%----------------------------------------------------------
			*                      품목명 데이터 마우스 올렸을때
			----------------------------------------------------------%>
			$(document).on("mouseover", 'div > ul > li', function() {
				var partName = $(this).attr("title");
				
				$("#" + partName + "UL li").each(function() {
					var cls = $(this).attr('class');
					if(cls == 'hover') {
						$(this).removeClass('hover');
					}
				})
				
				$(this).addClass("hover") ;
				$("#" + partName).val($(this).text());
			})
			
			<%----------------------------------------------------------
			*                      품목명 데이터 마우스 뺄때
			----------------------------------------------------------%>
			$(document).on("mouseout", 'div > ul > li', function() {
				$(this).removeClass("hover") ;
			})
			
			$('#partName4').focusout(function() {
				$('#partName4').val(this.value.toUpperCase());
			})
			
			$(".partName").focusout(function () {
				$("#" + this.id + "Search").hide();
				
				var name = '';
				
				if(!$.trim($('#partName1').val()) == '') {
					name += $('#partName1').val();
				}
				
				if(!$.trim($('#partName2').val()) == '') {
					if(!$.trim(name) == '') {
						name += '_';
					}
					name += $('#partName2').val();
				}
				
				if(!$.trim($('#partName3').val()) == '') {
					if(!$.trim(name) == '') {
						name += '_';
					}
					name += $('#partName3').val();
				}
				
				if(!$.trim($('#partName4').val()) == '') {
					if(!$.trim(name) == '') {
						name += '_';
					}
					name += $('#partName4').val();
				}
				
				$('#displayName').html(name);
			})
		</script>
	</form>
</body>
</html>