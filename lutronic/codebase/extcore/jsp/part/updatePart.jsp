<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.e3ps.part.beans.PartData"%>
<%
String oid = (String) request.getAttribute("oid");
PartData data = (PartData) request.getAttribute("data");
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
<form name="updateDevelopmentForm" id="updateDevelopmentForm" method="post" >
<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
<table width="100%" border="0" cellpadding="0" cellspacing="0" > 
	<tr height="5">
		<td>
			<table class="button-table">
				<tr>
					<td class="left">
						<div class="header">
							<img src="/Windchill/extcore/images/header.png">&nbsp;개발업무 수정
						</div>
					</td>
					<td class="right">
						<input type="button" value="수정" name="updateDevBtn" id="updateDevBtn" >
						<input type="button" value="이전페이지" name="backBtn" id="backBtn" onclick="javascript:history.back();">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
			<table class="search-table">
				<colgroup>
					<col width="180">
					<col width="180">
					<col width="*">
				</colgroup>
				<tr>
					<th>품목번호</th>
					<td class="indent5" colspan="3">
						<%= data != null ? data.getNumber() : "" %>
					</td>
				</tr>
				<tr>
					<th>품목분류 <span style="color:red;">*</span></th>
					<td class="indent5" colspan="3">
						<span id="locationName">
							/Default/PART_Drawing
						</span>
					</td>
				</tr>
				<tr>
					<th rowspan="4">품목명 <span style="color:red;">*</span></th>
					<th>대제목</th>
					<td class="indent5">
						<input type="text" name="partName1" id="partName1" class="width-300" value="">
					</td>
				</tr>
				<tr>
					<th>중제목</th>
					<td class="indent5">
						<input type="text" name="partName2" id="partName2" class="width-300">
					</td>
				</tr>
				<tr>
					<th>소제목</th>
					<td class="indent5">
						<input type="text" name="partName3" id="partName3" class="width-300">
					</td>
				</tr>
				<tr>
					<th>사용자 Key in</th>
					<td class="indent5">
						<input type="text" name="partName4" id="partName4" class="width-300">
					</td>
				</tr>
				<tr>
					<td class="tdblueM" id="auto" colspan="3" >
						<div>
							<span style="font-weight: bold; vertical-align: middle;" id="displayName">
								<c:out value="<%= data != null ? data.getName() : "" %>" />
							</span>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
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
		<!-- 	주도면 -->
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
		
		<!-- 관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp">
			<jsp:param value="part" name="moduleType"/>
			<jsp:param value="<%= data.getOid() %>" name="oid"/>
			<jsp:param value="관련 문서" name="title"/>
			<jsp:param value="docOid" name="paramName"/>
		</jsp:include>
		<br>
		
		
		<!-- 관련 rohs -->
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
						<jsp:param value="관련 RoHS" name="title"/>
						<jsp:param name="paramName" value="rohsOid"/>
						<jsp:param value="<%= data.getOid() %>" name="oid"/>
						<jsp:param value="part" name="module"/>
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<!-- 첨부파일 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 첨부파일
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
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" id="createBtn" class="blue" onclick="create('false');" />
					<input type="button" value="초기화" title="초기화" id="resetBtn" />
					<input type="button" value="목록" title="목록" id="listBtn" />
				</td>
			</tr>
		</table>
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
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
	
	if(!$.trim($('#partNameCustom').val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partNameCustom').val();
	}
	
	$('#displayName').html(name);
	
	
})

$(function () {
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
		} else {
			autoSearchPartName(this.id, this.value);
		}
	})
	
	$('#partNameCustom').focusout(function() {
		$('#partNameCustom').val(this.value.toUpperCase());
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
		
		if(!$.trim($('#partNameCustom').val()) == '') {
			if(!$.trim(name) == '') {
				name += '_';
			}
			name += $('#partNameCustom').val();
		}
		
		$('#displayName').html(name);
	})
	
	<%----------------------------------------------------------
	*                      수정버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function() {
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
//			const addRows7 = AUIGrid.getAddedRowItems(myGridID7);
//			const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
//			const addRows11 = AUIGrid.getAddedRowItems(myGridID11);
		const primarys = toArray("primarys");
		const wtPartType = document.getElementById("wtPartType").value;
		const source = document.getElementById("source").value;
		const lifecycle = document.getElementById("lifecycle").value;
		const view = document.getElementById("view").value;
		const fid = document.getElementById("fid").value;
		const location = document.getElementById("location").value;
		
		const oid = document.getElementById("oid").value;
//			const location = document.getElementById("location").value;
		const docName = document.getElementById("docName");
		const lifecycle = document.getElementById("lifecycle").value;
		const description = document.getElementById("description").value;
		const iterationNote = document.getElementById("iterationNote").value;
		const primarys = toArray("primarys");

// 		if($.trim($("#partName1").val()) == ""
// 			   && $.trim($("#partName2").val()) == ""
// 			   && $.trim($("#partName3").val()) == ""
// 			   && $.trim($("#partNameCustom").val()) == "" ) {
// 				alert("품목명을(를) 입력하세요.");
// 				$("#partName1").focus();
// 				return;
// 			}else if($("#displayName").text().length > 40) {
// 				alert("품목명은(는) 40자 이내로 입력하세요.");
// 				return;
// 			}
// 			if($("#model").val() == "") {
// 				alert("프로젝트코드을(를) 선택하세요.");
// 				$("#model").focus();
// 				return;
// 			}
			
// 			if($("#productmethod").val() == "") {
// 				alert("제작방법을(를) 선택하세요.");
// 				$("#productmethod").focus();
// 				return;
// 			}
			
// 			if($("#deptcode").val() == "") {
// 				alert("부서을(를) 선택하세요.");
// 				$("#deptcode").focus();
// 				return;
// 			}
			
// 			if($("#unit").val() == "") {
// 				alert("단위을(를) 선택하세요.");
// 				$("#unit").focus();
// 				return;
// 			}
			
		if (confirm("수정하시겠습니까?")){
			
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
// 			params.addRows7 = addRows7;
// 			params.addRows11 = addRows11;
			params.primarys = primarys;
			params.wtPartType = wtPartType;
			params.source = source;
			params.lifecycle = lifecycle;
			params.view = view;
			params.fid = fid;
			params.location = location;
			
			call(url, params, function(data) {
				if (data.result) {
					alert("수정 성공하였습니다.");
					location.href = getCallUrl("/doc/view?oid=" + data.oid);
				} else {
					alert("수정 실패하였습니다. \n" + data.msg);
				}
			});
		}
	})
})
</script>
</form>
</body>
</html>