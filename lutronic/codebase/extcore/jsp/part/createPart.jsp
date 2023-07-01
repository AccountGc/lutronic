<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script>

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	numberCodeList('partType1', '');
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
		/* if($.trim($("#seq").val()) == "" ) {
			alert("SEQ${f:getMessage('을(를) 입력하세요.')}");
			return false;
		}else  */if($.trim($("#seq").val()) != "" ) {
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
	*                      등록타입 변경시
	----------------------------------------------------------%>
	$("input[name=seqType]").change(function() {
		if(this.value == "0") {
			$("#seqv").hide();
			$("#etcv").hide();
			$("#seq").attr("disabled", "true");
			$("#etc").attr("disabled", "true");
			$("#seqList").hide();
			$("#manualNum").hide();
		}else {
			$("#seqv").show();
			$("#seq").removeAttr("disabled");
			$("#etcv").show();
			$("#etc").removeAttr("disabled");
			$("#etc").val("");
			$("#seqList").show();
			$("#manualNum").show();
		}
	})
	<%----------------------------------------------------------
	*                      등록버튼
	----------------------------------------------------------%>
	$("#createBtn").click(function() {
		
		if($.trim($("#fid").val()) == "") {
			alert("${f:getMessage('품목분류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
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
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("part", "createPartAction", "createPartForm", "listPart");
		}
	})
	<%----------------------------------------------------------
	*                      목록버튼
	----------------------------------------------------------%>
	$("#listBtn").click(function() {
		location.href = getURLString("part", "listPart", "do");
	})
	
	$("#seqList").click(function() {
		var url = getURLString("part", "searchSeqList", "do") + "?partNumber="+$("#partType1").val()+$("#partType2").val()+$("#partType3").val()+$("#seq").val();
		openOtherName(url,"searchSeqList","900","450","status=no,scrollbars=yes,resizable=yes");
	})
	
	$('#resetBtn').click(function() {
		resetFunction();
	})
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode1) {
	var type = "";
	if(id == 'partType1' || id == 'partType2' || id =='partType3') {
		type = "PARTTYPE";
	}else {
		type = id.toUpperCase();
	}
	
	var data = common_numberCodeList(type, parentCode1, false);
	
	addSelectList(id, eval(data.responseText));
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id,data){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			$("#"+ id).append("<option value='" + data[i].code + "' title='" + data[i].oid + "' > [" + data[i].code + "] " + data[i].name + "</option>");
		}
	}
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
</script>

<style>
.hover{ 
 	  cursor: default;
      background:#dedede;
}
</style>

<body>

<form name="createPartForm" id="createPartForm" method="post" style="padding:0px;margin:0px" enctype="multipart/form-data">

<input type="hidden" name="wtPartType"		id="wtPartType"		 	value="separable"     />
<input type="hidden" name="source"			id="source"	      		value="make"            />
<input type="hidden" name="lifecycle"   	id="lifecycle"			value="LC_PART"  />
<input type="hidden" name="view"			id="view"        		value="Design" />
<input type="hidden" name="fid" 			id="fid"				value="" >
<input type="hidden" name="location" 		id="location" 				value="/Default/PART_Drawing">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto;">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							${f:getMessage('부품')}
							${f:getMessage('관리')} 
							> 
							${f:getMessage('제품')}/${f:getMessage('품목')}
							${f:getMessage('등록')}	
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
					<td class="tdblueM" >${f:getMessage('품목분류')} <span style="color:red;">*</span>
					<td class="tdwhiteL">
						<b>
							<span id="locationName">
								/Default/PART_Drawing
							</span>
						</b>
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
									<input id="partNameCustom" name="partName4" class='partName' type="text" style="width: 95%; text-transform: uppercase;">
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
							<option value="">
								${f:getMessage('선택')}
							</option>
						</select>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						${f:getMessage('중분류')} <span style="color:red;">*</span>
					</td>
					
					<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						<select id="partType3" name="partType3" style="width: 95%">
							<option value="">
								${f:getMessage('선택')}
							</option>
						</select>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" align="center" height="25" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						${f:getMessage('SEQ')} <span id="seqv" style="color:red; display: none;" >*</span>
						<br>
						<b><font color="red">(3자리)</font></b>
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
						<b><font color="red">(2자리)</font></b>
					</td>
					
					<td class="tdwhiteL25" align="center" style="font-size:12px; font-family:Dotum; color:#425E6E;">
						<input type="text" id="etc" name="etc" style="width: 30%" maxlength="2"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 속정 정보 -->
<jsp:include page="/eSolution/common/include_createAttributes.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${f:getMessage('속성 정보')}" name="title"/>
	<jsp:param value="" name="oid"/>
</jsp:include>
		
<!-- 주 도면 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>  
	              
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('주 도면')}
				&nbsp;&nbsp;&nbsp;
				<font color="red">
				(${f:getMessage('메카')}
				:
				CAD${f:getMessage('파일')})
				,
				(${f:getMessage('광학')}/${f:getMessage('제어')}/${f:getMessage('파워')}/${f:getMessage('인증')}
				:
				PDF${f:getMessage('파일')})</font>
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
			
              	<tr bgcolor="ffffff" height="35">
               		<td class="tdblueM">
						${f:getMessage('주 도면')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="createPartForm"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="type" value="PRIMARY"/>
							<jsp:param name="btnId" value="createBtn" />
						</jsp:include>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
</table>

<!-- 첨부 파일 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>     
	           
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('첨부파일')}
				&nbsp;&nbsp;
				<font color="red">
				(${f:getMessage('제어')}/${f:getMessage('파워')}
				:
				${f:getMessage('배포파일')})
				</font>
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
			
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					<td class="tdwhiteL" colspan="3">
					
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="createPartForm"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="btnId" value="createBtn" />
						</jsp:include>
					
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 관련 문서 -->
<jsp:include page="/eSolution/doc/include_DocumentSelect.do">
	<jsp:param value="${f:getMessage('관련 문서')}" name="title"/>
	<jsp:param value="docOid" name="paramName"/>
</jsp:include>

<!-- 관련 rohs -->
<jsp:include page="/eSolution/rohs/include_RohsSelect.do">
	<jsp:param value="${f:getMessage('관련')} RoHS" name="title"/>
	<jsp:param name="paramName" value="rohsOid"/>
	<jsp:param value="part" name="module"/>
</jsp:include>

<!-- 버튼 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr>
		<td align="center" colspan="2">
			<table border="0" cellpadding="0" cellspacing="4" align="center">
				<tr>
					<td>
						<button type="button" name="" value="" class="btnCRUD" title="${f:getMessage('등록')}" id="createBtn" name="createBtn">
		                  	<span></span>
		                  	${f:getMessage('등록')}
	                  	</button>
					
					</td>
					<td>
						<button title="${f:getMessage('초기화')}" class="btnCustom" type="reset" name="resetBtn" id="resetBtn">
							<span></span>
							${f:getMessage('초기화')}
						</button>
					</td>
					<td>
						<button title="${f:getMessage('목록')}" class="btnCustom" type="button" name="listBtn" id="listBtn">
							<span></span>
							${f:getMessage('목록')}
						</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>