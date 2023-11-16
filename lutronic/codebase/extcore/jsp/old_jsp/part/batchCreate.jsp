<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
	setAttribute();
})


$(function() {
	<%----------------------------------------------------------
	*                      제품구분 변경시
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
			alert("${f:getMessage('제품분류')}${f:getMessage('을(를) 선택하세요.')}");
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
			//alert("SEQ${f:getMessage('을(를) 입력하세요.')}");
			//return false;
		}else {
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
	
	$('#partName4').focusout(function() {
		console.log("partName4 focusout ");
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
	
	$("#seqList").click(function() {
		var url = getURLString("part", "searchSeqList", "do") + "?partNumber="+$("#partType1").val()+$("#partType2").val()+$("#partType3").val()+$("#seq").val();
		openOtherName(url,"searchSeqList","900","450","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      Weight 입력 중
	----------------------------------------------------------%>
	$('#weight').keypress(function(event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		return (charCode == 46) || common_isNumber(event, this);
	})
	<%----------------------------------------------------------
	*                      Weight 입력시
	----------------------------------------------------------%>
	$("#weight").keyup(function() {
		var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
		$("#weight").val(result);
	})
	
	<%----------------------------------------------------------
	*                    addCount 입력 중
	----------------------------------------------------------%>
	$('#addCount').keypress(function(event) {
		return common_isNumber(event, this);
	})
	
	<%----------------------------------------------------------
	*                추가
	----------------------------------------------------------%>
	$('#addRow').click(function() {
		var dataMap = auiGridSetting();
		if($('#addCount').val() =="" || $('#addCount').val()==0){
			alert("추가 수량을 입력해 주세요")
			$('#addCount').focus();
			return;
		}
		dataMap["addCount"] = $('#addCount').val();
		opener.addRow(dataMap);
		//self.close();
	})
	
	<%----------------------------------------------------------
	*                    수정
	----------------------------------------------------------%>
	$('#modifyRow').click(function() {
		var dataMap = auiGridSetting();
		dataMap["auiId"] = $('#auiId').val();
		opener.modifyRow(dataMap);
		self.close();
	})
	
	
	
})

	<%----------------------------------------------------------
	*                   부포창의 Grid에 Setting
	----------------------------------------------------------%>
function auiGridSetting(){
	
	if(!validation()){
		return;
	}
	
	var dataMap = new Object(); 
	
	dataMap["partType1"] = $('#partType1').val();
	dataMap["partType1Name"] = checkSelect($("#partType1 option:selected").text());
	dataMap["partType2"] = $('#partType2').val();
	dataMap["partType2Name"] = checkSelect($("#partType2 option:selected").text());
	dataMap["partType3"] = $('#partType3').val();
	dataMap["partType3Name"] = checkSelect($("#partType3 option:selected").text());
	
	dataMap["seq"] = $('#seq').val();
	dataMap["etc"] = $('#etc').val();
	
	dataMap["partName1"] = $('#partName1').val();
	dataMap["partName2"] = $('#partName2').val();
	dataMap["partName3"] = $('#partName3').val();
	dataMap["partName4"] = $('#partName4').val();
	
	dataMap["displayName"] = $('#displayName').html();
	console.log("displayName =" + $('#displayName').html());
	
	dataMap["model"] = $('#model').val();
	dataMap["modelName"] =checkSelect($("#model option:selected").text());
	dataMap["productmethod"] = $('#productmethod').val();
	dataMap["productmethodName"] = checkSelect($("#productmethod option:selected").text());
	dataMap["deptcode"] = $('#deptcode').val();
	dataMap["deptcodeName"] = checkSelect($("#deptcode option:selected").text());
	dataMap["unit"] = $('#unit').val();
	dataMap["manufacture"] = $('#manufacture').val();
	dataMap["manufactureName"] =  checkSelect($("#manufacture option:selected").text());
	dataMap["mat"] = $('#mat').val();
	dataMap["matName"] = checkSelect($("#mat option:selected").text());
	dataMap["finish"] = $('#finish').val();
	dataMap["finishName"] = checkSelect($("#finish option:selected").text());
	dataMap["remarks"] = $('#remarks').val();
	dataMap["weight"] = $('#weight').val();
	dataMap["specification"] = $('#specification').val();
	
	return dataMap;
}

function checkSelect(value){
	if(value == "선택"){
		value = "-"
	}
	return value;
}

function validation(){
	/*
	if($("#partType1").val() == "" ) {
		alert("${f:getMessage('품목 분류')}${f:getMessage('을(를) 선택하세요.')}");
		$("#partType1").focus();
		return;
	}
	
	if($("#partType2").val() == "") {
		alert("${f:getMessage('대분류')}${f:getMessage('을(를) 선택하세요.')}");
		$("#partType2").focus();
		return;
	}
	
	if($("#partType3").val() == "") {
		alert("${f:getMessage('중분류')}${f:getMessage('을(를) 선택하세요.')}");
		$("#partType3").focus();
		return;
	}
	
	if($.trim($("#partName1").val()) == ""
	   && $.trim($("#partName2").val()) == ""
	   && $.trim($("#partName3").val()) == ""
	   && $.trim($("#partNameCustom").val()) == "" ) {
		alert("${f:getMessage('품목명')}${f:getMessage('을(를) 입력하세요.')}");
		$("#partName1").focus();
		return;
	}else if($("#displayName").text().length > 40) {
		alert("${f:getMessage('품목명')}${f:getMessage('은(는) 40자 이내로 입력하세요.')}");
		return;
	}
	
	if($("input[name=seqType]:checked").val() == "1") {
		if($.trim($("#seq").val()) == "") {
			alert("SEQ${f:getMessage('을(를) 입력하세요.')}");
			$("#seq").focus();
			return;
		}
		if($.trim($("#etc").val()) == "") {
			alert("${f:getMessage('기타')}${f:getMessage('을(를) 입력하세요.')}");
			$("#etc").focus();
			return;
		}
	}
	
	if($("#model").val() == "") {
		alert("${f:getMessage('프로젝트코드')}${f:getMessage('을(를) 선택하세요.')}");
		$("#model").focus();
		return;
	}
	
	if($("#productmethod").val() == "") {
		alert("${f:getMessage('제작방법')}${f:getMessage('을(를) 선택하세요.')}");
		$("#productmethod").focus();
		return;
	}
	
	if($("#deptcode").val() == "") {
		alert("${f:getMessage('부서')}${f:getMessage('을(를) 선택하세요.')}");
		$("#deptcode").focus();
		return;
	}
	
	if($("#unit").val() == "") {
		alert("${f:getMessage('단위')}${f:getMessage('을(를) 선택하세요.')}");
		$("#unit").focus();
		return;
	}
	*/
	return true;
}

<%----------------------------------------------------------
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.movePartNameFocus = function(id,isAdd) {
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

<%----------------------------------------------------------
*                      품목명 입력시 이름 검색
----------------------------------------------------------%>
window.autoSearchPartName = function(id, value) {
	if($.trim(value) == "") {
		addSearchList(id, '', true);
	} else {
		var codeType = id.toUpperCase();
		var data = common_autoSearchName(codeType, value);
		addSearchList(id, eval(data.responseText), false);
	}
}

<%----------------------------------------------------------
*                      품목명 입력시 데이터 리스트 보여주기
----------------------------------------------------------%>
window.addSearchList = function(id, data, isRemove) {
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

<%----------------------------------------------------------
*                      개벌 ID 수정 Setting
----------------------------------------------------------%>
function setAttribute(){
	if( $("#auiId").val() !=""){
		
		var rowItems = opener.getIDAttribute($("#auiId").val());
		
		var partType1 = rowItems[0].partType1;
		var partType2 = rowItems[0].partType2;
		var partType3 = rowItems[0].partType3;
		var seq = rowItems[0].seq;
		var etc = rowItems[0].etc;
		var partName1 = rowItems[0].partName1;
		var partName2 = rowItems[0].partName2;
		var partName3 = rowItems[0].partName3;
		var partName4 = rowItems[0].partName4;
		
		var model = rowItems[0].model;
		var productmethod = rowItems[0].productmethod;
		var deptcode = rowItems[0].deptcode;
		var unit = rowItems[0].unit;
		var manufacture = rowItems[0].manufacture;
		var mat = rowItems[0].mat;
		var finish = rowItems[0].finish;
		var weight = rowItems[0].weight;
		var remarks = rowItems[0].remarks;
		var specification = rowItems[0].specification;
		
		numberCodeList('partType1','', partType1);
		numberCodeList('partType2', $("#partType1 option:selected").attr("title"),partType2);
		numberCodeList('partType3', $("#codeNum").html() + $("#partType2 option:selected").attr("title"),partType3);
		
		var partTypeNum =partType1+partType2+partType3;
		var displayName =partName1+"_"+partName2+"_"+partName3
		$("#seq").val(seq);
		$("#etc").val(etc);
		$("#partTypeNum").html(partTypeNum);
		$("#seqNum").html(seq);
		$("#etcNum").html(etc);
		$("#partName1").val(partName1);
		$("#partName2").val(partName2);
		$("#partName3").val(partName3);
		$("#partName4").val(partName4);
		$("#displayName").html(displayName);
		
		numberCodeList('model', '', model);
		numberCodeList('productmethod', '', productmethod);
		numberCodeList('deptcode', '', deptcode);
		getQuantityUnit(unit);
		numberCodeList('manufacture', '', manufacture);
		numberCodeList('mat', '', mat);
		numberCodeList('finish', '', finish);
		
		$("#weight").val(weight);
		$("#remarks").val(remarks);
		$("#specification").val(specification);
		
	
	}else{
		numberCodeList('partType1','', '');
		numberCodeList('model', '', '');
		numberCodeList('productmethod', '', '');
		numberCodeList('deptcode', '', '');
		getQuantityUnit('');
		numberCodeList('manufacture', '', '');
		numberCodeList('mat', '', '');
		numberCodeList('finish', '', '');
	}
	
	//console.log("setAttribute seq=" + seq);
	//console.log("setAttribute etc=" + etc);
	
	
	
}

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode, value) {
	var type = "";
	if(id == 'partType1' || id == 'partType2' || id =='partType3') {
		type = "PARTTYPE";
	}else {
		type = id.toUpperCase();
	}
	
	var data = common_numberCodeList(type, parentCode, false);
	
	addSelectList(id, eval(data.responseText), value);
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id, data, value){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' >${f:getMessage('선택')}</option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "' title='" + data[i].oid + "'";
			
			if(data[i].code == value) {
				html += " selected";
			}
			
			html += " > [" + data[i].code + "] " + data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}

<%----------------------------------------------------------
*                      단위 리스트 가져오기
----------------------------------------------------------%>
window.getQuantityUnit = function(value) {
	var url	= getURLString("part", "getQuantityUnit", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('단위 목록 오류')}";
			alert(msg);
		},

		success:function(data){
			//$("#unit").append("<option value=''> ${f:getMessage('선택')} </option>");
			for(var i=0; i<data.length; i++) {
				var html = "<option value='" + data[i] + "'";
				
				if(data[i] == value) {
					html += " selected";
				}
				
				html += " > " + data[i] + "</option>";
				$("#unit").append(html);
			}
		}
	});
}
</script>

<body>

<form name="PartTreeForm"  method="post" >
<input type="hidden" name="auiId" 		id="auiId"		value="<c:out value="${auiId}"/>">

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align="center">
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color="white"><c:out value="${title }" /></font></B></td>
		   		</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
				<tr height="30">
					<td align="right">
					<c:choose>
						<c:when test="${mode eq 'single' }">
		  					
							<button type="button" name="modifyRow" id="modifyRow" class="btnClose" >
							<span></span>
							${f:getMessage('수정')}
						</button>
						</c:when>
						<c:otherwise>
						<b>*추가 수량 :</b> <input type="text" name="addCount" id="addCount" maxlength="2" size="5">
		  				<button type="button" name="addRow" id="addRow" class="btnClose" >
							<span></span>
							${f:getMessage('적용')}
						</button>
						</c:otherwise>
					</c:choose>
		  				<button type="button" name="" id="" class="btnClose" onclick="self.close()">
							<span></span>
							${f:getMessage('닫기')}
						</button>
		  			</td>
		  			
		  			
		    	</tr>
		    </table>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->

	<!-- 채번 정보 -->
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('채번')}
				${f:getMessage('정보')}
			</b>
		</td>
	</tr>
	
    <tr>
        <td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
			    <tr>
			        <td height="1" width="100%"></td>
			    </tr>
			</table>
		
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				
				<tr>
					<td width="150"></td>
					<td width="350"></td>
					<td width="150"></td>
					<td width="350"></td>
				</tr>
               
               	<!--  채번 타입
            	<tr bgcolor="ffffff" height="35">
	            	<td class="tdblueM" >
	            		${f:getMessage('채번')}${f:getMessage('타입')}
	            	</td>
	            	<td class="tdwhiteL" colspan="4">&nbsp;
	            		<input type="radio" name="seqType" value="0" checked="checked">
	            		<span></span>
	            		${f:getMessage('자동')}
	            		<input type="radio" name="seqType" value="1">
	            		<span></span>
	            		${f:getMessage('수동')}
	            	</td>
            	</tr> 
                -->
	                       
	            <!--  품목분류, 품목명 -->
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" ></span>
					<td class="tdwhiteL">
					</td>
				    
					<td class="tdblueM" rowspan="5">
							${f:getMessage('품목명')} <span style="color:red;">*</span>
					</td>					
					
					<td class="tdwhiteL25" rowspan="5">
					
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
							<tr bgcolor="ffffff" height="35">
							
								<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									${f:getMessage('대제목')}
								</td>
								
								<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									<input id="partName1" name="partName1" class='partName' type="text" style="width: 95%;">
									<div id="partName1Search" style="width: 250px; display: none; border: 1px solid black ; position: absolute; background-color: white;">
										<ul id="partName1UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
										</ul>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
							
								<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									${f:getMessage('중제목')}
								</td>
								
								<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									<input id="partName2" name="partName2" class='partName' type="text" style="width: 95%">
									<div id="partName2Search" style="width: 250px; display: none; border: 1px solid black ; position: absolute; background-color: white;">
										<ul id="partName2UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
										</ul>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									${f:getMessage('소제목')}
								</td>
								
								<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									<input id="partName3" name="partName3" class='partName' type="text" style="width: 95%">
									<div id="partName3Search" style="width: 250px; display: none; border: 1px solid black ; position: absolute; background-color: white;">
										<ul id="partName3UL" style="list-style-type: none; padding-left: 5px; text-align: left;">
										</ul>
									</div>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									${f:getMessage('사용자')} Key in
								</td>
								
								<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
									<input id="partName4" name="partName4" class='partName' type="text" style="width: 95%; text-transform: uppercase;">
								</td>
							</tr>
						
						</table>
					</td>
				</tr>
				
				<tr>
					<td width="100"></td>
				</tr>
			
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						${f:getMessage('품목구분')} <span style="color:red;">*</span>
					</td>
					
					<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						<select id="partType1" name="partType1" style="width: 95%">
							<option value="">
								${f:getMessage('선택')}
							</option>
						</select>
					</td>
								
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						${f:getMessage('대분류')} <span style="color:red;">*</span>
					</td>
					
					<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						<select id="partType2" name="partType2" style="width: 95%">
							<option value="">${f:getMessage('선택')}</option>
								
							
						</select>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						${f:getMessage('중분류')} <span style="color:red;">*</span>
					</td>
					
					<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						<select id="partType3" name="partType3" style="width: 95%">
							<option value="">${f:getMessage('선택')}</option>
								
							
						</select>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						${f:getMessage('SEQ')} <span id="seqv" style="color:red; display: none;" >*</span>
						<br>
						<b>(3자리)</b>
					</td>
					
					<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						<input type="text" id="seq" name="seq" style="width: 30%" maxlength="3"/>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<button id="seqList" class="btnSearch" type="button">
							<span></span>
							SEQ
							${f:getMessage('현황보기')}
						</button>
					</td>
					
					<td class="tdblueM" id="auto" colspan="2" rowspan="2" >
					
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
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						${f:getMessage('기타')} <span id="etcv" style="color:red; display: none;">*</span>
						<br>
						<b>(2자리)</b>
					</td>
					
					<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						<input type="text" id="etc" name="etc" style="width: 30%" maxlength="2"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 품목 속성 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>  
	              
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('품목')}
				${f:getMessage('속성')}
			</b>
		</td>
	</tr>
	
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			
				<tr>
					<td width="150"></td>
					<td width="350"></td>
					<td width="150"></td>
					<td width="350"></td>
				</tr>
				<!-- MODEL, PRODUCTMETHOD -->			
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('프로젝트코드')} <span style="color:red;">*</span>
					</td>
					
					<td class="tdwhiteM">
						<select id='model' name='model' style="width: 95%">
						</select>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('제작방법')} <span style="color:red;">*</span>
					</td>
					
					<td class="tdwhiteM">
						<select id='productmethod' name='productmethod' style="width: 95%">
						</select>
					</td>
				</tr>
				
				<!-- DEPTCODE, UNIT -->
              	<tr bgcolor="ffffff" height="35">
              		<td class="tdblueM">
						${f:getMessage('부서')} <span style="color:red;">*</span>
					</td>					
					
					<td class="tdwhiteM">
						<select id="deptcode" name="deptcode" style="width: 95%">
						</select>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('단위')} <span style="color:red;">*</span>
					</td>					
					
					<td class="tdwhiteM">
						<select id='unit' name='unit' style="width: 95%">
						</select>
					</td>
				</tr>
                
				<!-- WEIGHT, MANUFATURER -->
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('무게')}(g)
					</td>
					
					<td class="tdwhiteM">
						<input id="weight" name="weight" type="text" value="<c:out value='${weight }'/>" style="width: 95%;"/>
					</td>
					
					<td class="tdblueM">
						MANUFACTURER
					</td>
					
					<td class="tdwhiteM">
						<select id="manufacture" name="manufacture" style="width: 95%">
						</select>
					</td>
				</tr>
				
				<!-- MAT, FINISH -->
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('재질')}
					</td>
					
					<td class="tdwhiteM">
						<select id="mat" name="mat" style="width: 95%">
						</select>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('후처리')}
					</td>
					
					<td class="tdwhiteM">
						<select id="finish" name="finish" style="width: 95%">
						</select>
					</td>
				</tr>
				
				<!-- REMARKS, SPECIFICATION -->
				<tr bgcolor="ffffff" height="35">
               		<td class="tdblueM">
						${f:getMessage('비고')}
					</td>
					
					<td class="tdwhiteM">
						<input id="remarks" name="remarks" type="text" value="<c:out value='${remarks }'/>" style="width: 95%;"/>
					</td>
					
               		<td class="tdblueM">
						${f:getMessage('사양')}
					</td>
					
					<td class="tdwhiteM">
						<input id="specification" name="specification" type="text" value="<c:out value='${specification }'/>" style="width: 95%;"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>