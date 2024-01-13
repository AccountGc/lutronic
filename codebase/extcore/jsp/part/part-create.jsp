<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.part.QuantityUnit"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> matList = (ArrayList<NumberCode>) request.getAttribute("matList");
ArrayList<NumberCode> productmethodList = (ArrayList<NumberCode>) request.getAttribute("productmethodList");
ArrayList<NumberCode> finishList = (ArrayList<NumberCode>) request.getAttribute("finishList");
QuantityUnit[] unitList = (QuantityUnit[]) request.getAttribute("unitList");
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
		<input type="hidden" name="wtPartType" id="wtPartType" value="separable">
		<input type="hidden" name="source" id="source" value="make">
		<input type="hidden" name="lifecycle" id="lifecycle" value="LC_PART">
		<input type="hidden" name="view" id="view" value="Design">

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						채번 정보
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
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">품목분류</th>
				<td class="indent5">
					<input type="hidden" name="location" id="location" value="/Default/PART_Drawing">
					<span id="locationText"><%=PartHelper.PART_ROOT%></span>
					<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">
				</td>
				<th rowspan="4">품목명</th>
				<th class="lb">대제목</th>
				<td class="indent5">
					<input id="partName1" name="partName1" class='partName width-200' type="text">
					<div id="partName1Search" style="width: 250px; display: none; border: 1px solid black; position: absolute; background-color: white;">
						<ul id="partName1UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">품목구분</th>
				<td class="indent5">
					<select id="partType1" name="partType1" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th class="lb">중제목</th>
				<td class="indent5">
					<input id="partName2" name="partName2" class='partName width-200' type="text">
					<div id="partName2Search" style="width: 250px; display: none; border: 1px solid black; position: absolute; background-color: white;">
						<ul id="partName2UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">대분류</th>
				<td class="indent5">
					<select id="partType2" name="partType2" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th class="lb">소제목</th>
				<td class="indent5">
					<input id="partName3" name="partName3" class='partName width-200' type="text">
					<div id="partName3Search" style="width: 250px; display: none; border: 1px solid black; position: absolute; background-color: white;">
						<ul id="partName3UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">중분류</th>
				<td class="indent5">
					<select id="partType3" name="partType3" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th class="lb">사용자 KEY IN</th>
				<td class="indent5">
					<input type="text" class="partName width-200" name="partName4" id="partName4">
				</td>
			</tr>
			<tr>
				<th class="lb">
					SEQ
					<span style="color: red;">(3자리)</span>
				</th>
				<td class="indent5">
					<input type="text" name="seq" id="seq" class="width-200" maxlength="3">
					<input type="button" class="btnSearch" value="SEQ 현황보기" title="SEQ 현황보기" onclick="seqList();">
				</td>
				<td colspan="3" rowspan="3">
					<div id="partTypeNum" style="font-size: 24px; padding-left: 45%; font-weight: bold; vertical-align: middle; float: left;"></div>
					<div id="manualNum">
						<div id="seqNum" style="font-size: 24px; font-weight: bold; vertical-align: middle; float: left;"></div>
						<div id="etcNum" style="font-size: 24px; font-weight: bold; vertical-align: middle; float: left;"></div>
					</div>
					<div style="clear: both;"></div>
					<div style="text-align: center;">
						<span style="font-size: 24px; font-weight: bold; vertical-align: middle;" id="displayName"></span>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">
					기타
					<span style="color: red;">(2자리)</span>
				</th>
				<td class="indent5" colspan="4">
					<input type="text" name="etc" id="etc" class="width-200" maxlength="2">
				</td>
			</tr>
		</table>
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

		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">프로젝트코드</th>
				<td class="indent5">
					<input type="text" name="model" id="model" class="width-300">
				</td>
				<th class="req">제작방법</th>
				<td class="indent5">
					<select name="productmethod" id="productmethod" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode productmethod : productmethodList) {
						%>
						<option value="<%=productmethod.getCode()%>"><%=productmethod.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="req lb">부서</th>
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
				<th class="req">단위</th>
				<td class="indent5">
					<select name="unit" id="unit" class="width-200">
						<option value="">선택</option>
						<%
						for (QuantityUnit unit : unitList) {
							String value = unit.toString();
						%>
						<option value="<%=value%>"><%=unit.getDisplay()%> /
							<%=value%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb">무게(g)</th>
				<td class="indent5">
					<input type="text" name="weight" id="weight" class="width-200">
				</td>
				<th>MANUFACTURER</th>
				<td class="indent5">
					<input type="text" name="manufacture" id="manufacture" class="width-200">
				</td>
			</tr>
			<tr>
				<th class="lb">재질</th>
				<td class="indent5">
					<select name="mat" id="mat" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode mat : matList) {
						%>
						<option value="<%=mat.getCode()%>"><%=mat.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>후처리</th>
				<td class="indent5">
					<select name="finish" id="finish" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode finish : finishList) {
						%>
						<option value="<%=finish.getCode()%>"><%=finish.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb">OEM Info.</th>
				<td class="indent5">
					<input type="text" name="remarks" id="remarks" class="width-200">
				</td>
				<th>사양</th>
				<td class="indent5">
					<input type="text" name="specification" id="specification" class="width-200">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						주 도면&nbsp;&nbsp;
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
				<td class="indent5">
					<jsp:include page="/extcore/jsp/common/attach-primary-drawing.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						첨부파일&nbsp;&nbsp;
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

		<!-- 관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="insert90" name="method" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />

		</jsp:include>

		<!-- 관련 RoHS -->
		<jsp:include page="/extcore/jsp/rohs/include/rohs-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="insert106" name="method" />
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

			function create(temp) {
				const location = document.getElementById("location").value;
				const partName1 = document.getElementById("partName1").value;
				const partType1 = document.getElementById("partType1").value;
				const partName2 = document.getElementById("partName2").value;
				const partType2 = document.getElementById("partType2").value;
				const partName3 = document.getElementById("partName3").value;
				const partType3 = document.getElementById("partType3").value;
				const partName4 = document.getElementById("partName4").value;
				const modelTag = document.getElementById("modelcode");
				let model;
				if(modelTag != null) {
					model = modelTag.value;
				}
				const manufactureTag = document.getElementById("manufacturecode");
				let manufacture;
				if(manufactureTag != null) {
					manufacture = manufactureTag.value;
				}		
				const productmethod = document.getElementById("productmethod").value;
				const deptcode = document.getElementById("deptcode").value;
				const unit =  document.getElementById("unit").value;
				const temprary = JSON.parse(temp);
				
				const primary = document.querySelector("input[name=primary]") == null ? "" : document.querySelector("input[name=primary]").value;
				const secondary = toArray("secondarys");
				
				// 관련문서
				const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
	            
				// RoHs
				const rows106 = AUIGrid.getGridDataWithState(myGridID106, "gridState");
				
				if(location === "/Default/PART_Drawing") {
					alert("품목분류를 선택하세요.");
					folder();
					return false;
				}
				
	            if(isEmpty(partType1)){
					alert("품목구분을 입력하세요.");
					return;					
				}
	            if(isEmpty(partType2)){
					alert("대분류를 입력하세요.");
					return;					
				}
	            if(isEmpty(partType3)){
					alert("중분류를 입력하세요.");
					return;					
				}
				if(isEmpty(partName1)&&isEmpty(partName2)&&isEmpty(partName3)&&isEmpty(partName4)){
					alert("품목명을 입력하세요.");
					return;					
				}
				
				if(isEmpty(model)){
					alert("프로젝트 코드를 선택하세요.");
					return;					
				}
				if(isEmpty(productmethod)){
					alert("제작방법을 선택하세요.");
					return;					
				}
				if(isEmpty(deptcode)){
					alert("부서를 선택하세요.");
					return;					
				}
				if(isEmpty(unit)){
					alert("단위를 선택하세요.");
					return;					
				}
				
				if (!confirm("등록하시겠습니까?")) {
					return false;
				}
				
				const params ={
						partName1 : partName1,
						partName2 : partName2,
						partName3 : partName3,
						partName4 : partName4,
						partType1 : partType1,
						partType2 : partType2,
						partType3 : partType3,
						model : model,
						productmethod : productmethod,
						deptcode : deptcode,
						unit : unit,
						temprary : temprary,
						primary : primary,
						secondary : secondary,
						seq : toId("seq"),
						etc : toId("etc"),
						weight : toId("weight"),
						manufacture : manufacture,
						mat : toId("mat"),
						finish : toId("finish"),
						remarks : toId("remarks"),
						specification : toId("specification"),
						wtPartType : toId("wtPartType"),
						source : toId("source"),
						lifecycle : toId("lifecycle"),
						view :toId("view"),
						location : location,
						// 링크 데이터
						rows90 : rows90,
						rows106 : rows106,
				};
				const url = getCallUrl("/part/create");
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
	 					document.location.href = getCallUrl("/part/list");
					}
					parent.closeLayer();
				});
			};

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid90(columns90);
				createAUIGrid106(columns106);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID106);
				numberCodeList('partType1', '');
				selectbox("partType2");
				selectbox("partType3");
				selectbox("state");
// 				selectbox("model");
				selectbox("productmethod");
				selectbox("deptcode");
				selectbox("unit");
				selectbox("mat");
				selectbox("finish");
// 				selectbox("manufacture");
				finderUser("creator");
				twindate("created");
				twindate("modified");
				$("#unit").bindSelectSetValue("ea");
				finderCode("model", "MODEL", "code");
				finderCode("manufacture", "MANUFACTURE", "code");
			});
			
			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID106);
			});
			
			// 품목구분(partType1) 변경 메서드
			$("#partType1").change(function() {
				numberCodeList('partType2', $("#partType1 option:selected").attr("title"));
				$("#partTypeNum").html(this.value);
			})
			
			// 대분류(partType2) 변경 메서드
			$("#partType2").change(function() {
				numberCodeList('partType3', $("#codeNum").html() + $("#partType2 option:selected").attr("title"));
				$("#partTypeNum").html($("#partType1").val() + this.value);
			})
			
			// 중분류(partType3) 변경 메서드
			$("#partType3").change(function() {
				$("#partTypeNum").html($("#partType1").val() + $("#partType2").val() + this.value);
			})
			
			// SEQ 입력 중 발생 메서드
			$("#seq").keypress(function (event) {
				if($("#partType3").val() == "" ) {
					alert("제품분류을(를) 선택하세요.");
					return false;
				}else {
					return common_isNumber(event, this);
				}
		    })
		    
		    // SEQ 입력 시 발생 메서드
			$("#seq").keyup(function() {
				var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
				$("#seq").val(result);
			})
			
			// SEQ 입력 후 포커스 아웃 시 발생 메서드
			$("#seq").focusout(function() {
				if($("#partType3").val() != "" && $.trim(this.value) ) {
					$("#seqNum").html(this.value);
				}
			})
			
			// SEQ 현황보기 클릭 시 메서드
			function seqList(){
				const partType1 = document.getElementById("partType1").value;
				const partType2 = document.getElementById("partType2").value;
				const partType3 = document.getElementById("partType3").value;
				const seq = document.getElementById("seq").value;
				const partNumber = partType1+partType2+partType3+seq;
// 				if(partNumber.length !== 8) {
// 					alert("SEQ 현황을 보기 위해서 모든 채번 정보를 선택 및 입력하세요.");
// 					return false;
// 				}
				const url = getCallUrl("/part/seq?partNumber=" + partNumber);
				_popup(url, 1100, 550, "n");
			}
		    
			// 기타 입력 중 발생 메서드
			$("#etc").keypress(function (event) {
				if($.trim($("#seq").val()) == "" ) {
					alert("SEQ을(를) 입력하세요.");
					return false;
				}else{
					return common_isNumber(event, this);
				}
		    })
		    
			// 기타 입력 시 발생 메서드
			$("#etc").keyup(function() {
				var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
				$("#etc").val(result);
			})
			
			// 기타 입력 후 포커스 아웃 시 발생 메서드
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
			
			// 품목명에서 ↑,↓ 입력 시 발생 메서드
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
			
			// 품목명 입력 시 품목명 리스트 출력 메서드
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
			
			// 품목명 가져오기 메서드
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
			
			// 품목명 입력시 데이터 리스트 보여주기
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
			
			// 품목명 데이터 마우스 올렸을때
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
			
			//품목명 데이터 마우스 뺄때
			$(document).on("mouseout", 'div > ul > li', function() {
				$(this).removeClass("hover") ;
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
			
			function folder() {
				const location = decodeURIComponent("/Default/PART_Drawing");
				const url = getCallUrl("/folder/popup?location=" + location);
				_popup(url, 500, 600, "n");
			}
			
			// 무게 입력 메서드
			$(function() {
				// 무게 입력 중
				$('#weight').keypress(function(event) {
					var charCode = (event.which) ? event.which : event.keyCode;
					return (charCode == 46) || common_isNumber(event, this);
				})
				// 무게 입력 중
				$("#weight").keyup(function() {
					var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
					$("#weight").val(result);
				})
			})
		</script>
	</form>
</body>
</html>