<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript" src="/Windchill/jsp/js/eo.js" ></script>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript">
<%----------------------------------------------------------
*                      AUI 그리드 
----------------------------------------------------------%>
var myGridID;

var defaultGridHeight = 61;
var rowHeight = 26;
var headerHeight = 24;
var footerHeight = 30;

//칼럼 레이아웃 작성
var columnLayout = [ 
	
	
	{
        dataField : "number",
        //headerText : 'EO ${f:getMessage('번호')}',
        headerText : 'ECO ${f:getMessage('번호')}',
        width: "8%"
	}, 
	{
        dataField : "name",
        //headerText : 'EO ${f:getMessage('제목')}',
        headerText : 'ECO ${f:getMessage('제목')}',
        style: "AUI_left",
        width: "*",
        renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			//var rowItems = AUIGrid.getSelectedItems(myGridID);
			var temp = "<a href=javascript:openDistributeView('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
	}, 
	{
        dataField : "licensingDisplay",
        headerText : '${f:getMessage('인허가 변경')}',
        style: "AUI_center_H",
        width: "8%",
        renderer : { // HTML 템플릿 렌더러 사용
 			type : "TemplateRenderer"
 		},
 		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
 			//var rowItems = AUIGrid.getSelectedItems(myGridID);
 		    
 			var temp = "<b><font color='red' style='line-height:26px;'>" + value + "</font></b>"
 			if("선택안됨" == value){
 				
 			}else{
 				//temp = value;
 				temp = "<font style='line-height:26px;'>" + value + "</font>"
 			}
 			//console.log(temp);
 			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
 		}
	},
	{
        dataField : "riskTypeName",
        headerText : '${f:getMessage('위험통제')}',
        width: "8%",
        style: "AUI_center_H",
        renderer : { // HTML 템플릿 렌더러 사용
 			type : "TemplateRenderer"
 		},
 		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
 			//var rowItems = AUIGrid.getSelectedItems(myGridID);
 			var temp = "<b><font color='red' style='line-height:26px;'>" + value + "</font><b>"
 			if("선택안됨" == value){
 				
 			}else{
 				//temp = value;
 				temp = "<font style='line-height:26px;'>" + value + "</font>"
 			}
 			
 			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
 		}
	},
	{
        dataField : "state",
        headerText : '${f:getMessage('상태')}',
        width: "8%"
	},
	{
        dataField : "creator",
        headerText : '${f:getMessage('등록자')}',
        width: "8%"
	},
	{
        dataField : "eoApproveDate",
        headerText : '${f:getMessage('승인일')}',
        width: "8%"
	},
	{
        dataField : "attachFile",
        headerText : '산출물',
        width: "7%",
        renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			//console.log("value =" + value); '
			var temp ="<span style='line-height:26px;'><button type='button' class='btnCustom' onclick=javascript:batchEODownLoad('"+item.oid+"','attach') >산출물</buttom></span>";
			
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}      
        
	},
	{
        dataField : "dwg",
        headerText : '도면',
        width: "7%",
        renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			//console.log("value =" + value); '
		
			var temp = "<span style='line-height:26px;'><button type='button' class='btnCustom' onclick=javascript:batchEODownLoad('"+item.oid+"','drawing') >도면</buttom></span>";
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}      
        
	},
	{
        dataField : "changePart",
        headerText : '변경부품',
        width: "7%",
        renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			//console.log("value =" + value); '
		
			var temp = "<span style='line-height:26px;'><button type='button' class='btnCustom' onclick=javascript:changePartView('"+item.number+"') >보기</buttom></span>";
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}      
        
	}
	

];

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
	//Aui 그리드 설정
	var auiGridProps = {
		selectionMode : "singleRow",
		showRowNumColumn : false,
		softRemoveRowMode : false,
		processValidData : true,
		headerHeight : headerHeight,
		rowHeight : rowHeight,
		height : 400,
		enableSorting : false,
		
	};
	
	// 실제로 #grid_wrap 에 그리드 생성
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	
	gfn_InitCalendar("preApproveDate", "preApproveDateBtn");
	gfn_InitCalendar("postApproveDate", "postApproveDateBtn");
	
	numberCodeList('LICENSING', '');
	lfn_getStateList('LC_ECO');
	//lfn_Search();
	getGridData(1, 14);
})

<%----------------------------------------------------------
*                     NumberCode List
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode1) {
	var type = id.toUpperCase();
	
	var data = common_numberCodeList(type, parentCode1, true);
	
	addSelectList(id, eval(data.responseText));
}
<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
function addSelectList(type,data){
	var id = "licensing";
	
	$("#"+ id).append("<input type='radio' name="+id+" id="+id+" value='NONE'>선택안됨");
	for(var i=0; i<data.length; i++) {
		
		$("#"+ id).append("<input type='radio' name="+id+" id="+id+" value='" + data[i].code + "'>"+ data[i].name);
	}
	$("#"+ id).append("<input type='radio' name="+id+" id="+id+" value=''>전체");
}

$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		resetSearch();
	})
	<%----------------------------------------------------------
	*                      초기화 버튼
	----------------------------------------------------------%>
	$("#btnReset").click(function() {
		$("#creator").val("");
		deleteAllCode();
		deleteAllCompleteParts();
	})
	<%----------------------------------------------------------
	*                      제품  추가 버튼
	----------------------------------------------------------%>
	$("#addNumberCode").click(function() {
		
		var url = getURLString("common", "popup_numberCodeList", "do")+ "?codeType=MODEL";
		openOtherName(url,"selectCodePopup","800","600","status=no,scrollbars=yes,resizable=yes");
	})
	<%----------------------------------------------------------
	*                      제품  삭제 버튼
	----------------------------------------------------------%>
	$("#delNumberCode").click(function() {
		delteNumberCode();
		$("#modelCodeCheck").prop("checked", "");
	})
	$(document).keypress(function(event) {
		 if(event.which == 13 && $( '#userNameSearch' ).is( ':hidden' )) {
			 resetSearch();
		 }
	})
	$('#searchCompletePart').click(function() {
		var url = getURLString("part", "selectPartPopup", "do") + "?mode=mutil";
		openOtherName(url,"window","1180","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	$('#deleteCompletePart').click(function() {
		$('input[name=deleteCompletePartCheck]').each(function() {
			if(this.checked) {
				$('#' + this.value).remove();
			}
		})
		$("#completePartCheck").prop("checked", "");
	})
	
	$('#modelCodeCheck').click(function() {
		if(this.checked) {
			$("input[name='modelcode']").prop("checked", "checked");
		}else {
			$("input[name='modelcode']").prop("checked", "");
		}
	})
	
	$('#completePartCheck').click(function() {
		if(this.checked) {
			$("input[name='deleteCompletePartCheck']").prop("checked", "checked");
		}else {
			$("input[name='deleteCompletePartCheck']").prop("checked", "");
		}
	})
})

var sortValue = "";
var sortCheck = true;
function createPagingNavigator(goPage, totalPage) {
	var pageButtonCount = 10;		// 페이지 네비게이션에서 보여줄 페이지의 수
	
	var retStr = "";
	var prevPage = parseInt((goPage - 1)/pageButtonCount) * pageButtonCount;
	var nextPage = ((parseInt((goPage - 1)/pageButtonCount)) * pageButtonCount) + pageButtonCount + 1;

	prevPage = Math.max(0, prevPage);
	nextPage = Math.min(nextPage, totalPage);
	
	// 처음
	retStr += "<a href='javascript:moveToPage(1)'><span class='aui-grid-paging-number aui-grid-paging-first'>first</span></a>";

	// 이전
	retStr += "<a href='javascript:moveToPage(" + Math.max(1, prevPage) + ")'><span class='aui-grid-paging-number aui-grid-paging-prev'>prev</span></a>";

	for (var i=(prevPage+1), len=(pageButtonCount+prevPage); i<=len; i++) {
		if (goPage == i) {
			retStr += "<span class='aui-grid-paging-number aui-grid-paging-number-selected'>" + i + "</span>";
		} else {
			retStr += "<a href='javascript:moveToPage(" + i + ")'><span class='aui-grid-paging-number'>";
			retStr += i;
			retStr += "</span></a>";
		}
		
		if (i >= totalPage) {
			break;
		}

	}

	// 다음
	retStr += "<a href='javascript:moveToPage(" + nextPage + ")'><span class='aui-grid-paging-number aui-grid-paging-next'>next</span></a>";

	// 마지막
	retStr += "<a href='javascript:moveToPage(" + totalPage + ")'><span class='aui-grid-paging-number aui-grid-paging-last'>last</span></a>";

	document.getElementById("grid_paging").innerHTML = retStr;
}

function moveToPage(goPage) {
	
	var rowCount = $("#rows").val();
	
	// rowCount 만큼 데이터 요청
	$("#page").val(goPage);
	
	getGridData(goPage, rowCount, sortValue);
}

function getGridData(goPage, rowCount, sortValue){
	
	var form = $("form[name=changeRequestForm]").serialize();
	
	var url	= getURLString("distribute", "listPagingECOAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			// 그리드 데이터
			var gridData = data.list;
			
			// 그리드에 데이터 세팅
			AUIGrid.setGridData(myGridID, gridData);
			
			AUIGrid.setAllCheckedRows(myGridID, false);
			
			var totalPage = data.totalPage;
			var startPage = data.startPage;
			var endPage = data.endPage;
			var listCount = data.listCount;
			var totalCount = data.totalCount;
			var currentPage = data.currentPage;
			var param = data.param;
			var rowCount = data.rowCount;
			$("#sessionId").val(data.sessionId);
			$("#page").val(currentPage);
			
			createPagingNavigator(currentPage, totalPage);
			
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	});
}

<%----------------------------------------------------------
*                      선택 품목 데이터 추가
----------------------------------------------------------%>
function addPart(obj,isHref) {
	for (var i = 0; i < obj.length; i++) {
		if(!$('#' + obj[i][1]).is('#' + obj[i][1])){
			var html = '';
			html += '<div id="' + obj[i][1] + '" style="float: left;">';
			
			html += '	<input type="checkbox" name="deleteCompletePartCheck" value="' + obj[i][1] + '"/>';
			html += '	<input type="hidden" name="completeParts" value="' + obj[i][0] + '" />';
			html += '	<span class="partSpans">';
			html +=			obj[i][1];
			html += '	</span>';
			html += '</div>';
			
			$('#completepartDiv').append(html);
		}
	}
}

window.resetSearch = function() {
	
	$("#sortValue").val("");
	$("#sortCheck").val("");
	$("#sessionId").val("");
	$("#page").val(1);
	//lfn_Search();
	getGridData(1, 14);
}

<%----------------------------------------------------------
*                      Lifecycle 상태 가져오기
----------------------------------------------------------%>
function lfn_getStateList(lifecycle) {
	var form = $("form[name=changeRequestForm]").serialize();
	var url	= getURLString("WFItem", "lifecycleList", "do") + "?lifecycle="+lifecycle;
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$("#state").find("option").remove();
			$("#state").append("<option value=''>${f:getMessage('선택')}</option>");
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					$("#state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
				}
			}
			
		}
	});
}



<%----------------------------------------------------------
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=changeRequestForm]").serialize();
	var url	= getURLString("distribute", "listECOAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			console.log(data);
			// 그리드 데이터
			var gridData = data;
			
			// 그리드에 데이터 세팅
			AUIGrid.setGridData(myGridID, gridData);
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	});
}


<%----------------------------------------------------------
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	console.log(" EO redrawGrid width =" + width);
	AUIGrid.resize(myGridID);
}

function onExcelDown() {
	$("#changeRequestForm").attr("method", "post");
	$("#changeRequestForm").attr("action", getURLString("excelDown", "ECOExcelDown", "do")).submit();
}

</script>

<body>
<form name="changeRequestForm" id="changeRequestForm" method=post style="padding:0px;margin:0px">
<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="14"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<!-- 폴더 선택시 검색 function을 호출하기 위한 변수 -->
<input type="hidden" name="search" id="search" value="true" />

<!-- 정렬을 사용할때 사용하는 변수 -->
<input type="hidden" name="sortValue" id="sortValue" />
<input type="hidden" name="sortCheck" id="sortCheck" />

<input type="hidden" name="lifecycle" id="lifecycle" value="LC_ECO">

<input type=hidden name=eoType value='CHANGE'>
<input name="state" type="hidden"  value="APPROVED" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="1" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp; ECO ${f:getMessage("관리")} > ECO ${f:getMessage("검색")}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr  align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
				<tr><td height=1 width=100%></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			<col width='10%'><col width='40%'><col width='10%'><col width='40%'>
				<tr>
					<td class="tdblueM">
						ECO${f:getMessage("제목")}
					</td>
					
					<td class="tdwhiteL">
						<input name="name" id="name" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
					
					<td class="tdblueM">
						ECO${f:getMessage("번호")}
					</td>
					
					<td class="tdwhiteL">
						<input name="number" id="number" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
				</tr>
				
				<tr>
					<td class="tdblueM">
						${f:getMessage("등록자")}
					</td>
					
	                <td class="tdwhiteL" colspan="3">
	                
	                	<%-- 
	                   	<input type="hidden" id="creator" name="creator" value="" />
						<input type="text" class="txt_field" id="tempcreator" name="tempcreator" size="30" style="width:30%" value="" readOnly/>
						
						<a href="JavaScript:searchUser('changeRequestForm','single','creator','tempcreator','people')">
							<img src="/Windchill/jsp/portal/images/s_search.gif" border="0" />
						</a>
						
						<a href="JavaScript:clearText('creator');clearText('creatorName');">
							<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
						</a>
						--%>
						
						<jsp:include page="/eSolution/common/userSearchForm.do">
							<jsp:param value="single" name="searchMode"/>
							<jsp:param value="creator" name="hiddenParam"/>
							<jsp:param value="creatorName" name="textParam"/>
							<jsp:param value="people" name="userType"/>
							<jsp:param value="" name="returnFunction"/>
						</jsp:include>
						
					</td>
					<!--  
					<td class="tdblueM" >
						${f:getMessage("상태")}
					</td>
					
                    <td class="tdwhiteL" >
	                    <select name="state" id="state" >
 							<option value=''>${f:getMessage("선택")}</option>
						</select>
                    </td>
                    -->
				</tr>
				<tr>
                    <td class="tdblueM">
						${f:getMessage("등록일")}
					</td>
					
					<td class="tdwhiteL" >
						<input name="predate" id="predate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="predateBtn" id="predateBtn" >
						</a>
						<a href="JavaScript:clearText('predate')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
						~
						<input name="postdate" id="postdate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postdateBtn" id="postdateBtn" >
						</a>
						<a href="JavaScript:clearText('postdate')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
					</td>
					<td class="tdblueM">
						${f:getMessage("승인일")}
					</td>
					
					<td class="tdwhiteL" >
						<input name="preApproveDate" id="preApproveDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="preApproveDateBtn" id="preApproveDateBtn" >
						</a>
						<a href="JavaScript:clearText('preApproveDate')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
						~
						<input name="postApproveDate" id="postApproveDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postApproveDateBtn" id="postApproveDateBtn" >
						</a>
						<a href="JavaScript:clearText('postApproveDate')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
					</td>
				</tr>
				<tr>	
					<td class="tdblueM" >
						${f:getMessage("인허가변경")}
					</td>
					
                    <td class="tdwhiteL" >
	                    <div id='licensing'></div>	
                    </td>
                    <td class="tdblueM" >
						${f:getMessage("위험 통제")}
					</td>
					
                    <td class="tdwhiteL" >
	                    <input type='radio' name="riskType" id="riskType" value='NONE'> 선택안됨	
						<input type='radio' name="riskType" id="riskType" value='0'> 불필요
						<input type='radio' name="riskType" id="riskType" value='1'> 필요 	
						<input type='radio' name="riskType" id="riskType" value=''> 전체
                    </td>
                </tr>
                
                <tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage("제품명")}
						<input type="checkbox" id='modelCodeCheck'>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<table border="0" cellpadding="0" cellspacing="2" width="100%">
							<tr align="left">
								<td>
									<button type="button" name="addNumberCode" id="addNumberCode" class="btnCustom">
										<span></span>
										${f:getMessage('추가')}
									</button>
									
									<button type="button" name="delNumberCode" id="delNumberCode" class="btnCustom">
										<span></span>
										${f:getMessage('삭제')}
									</button>
								</td>
							</tr>
							
							<tr>
								<td>
									<div id='modeltable'></div>	
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage("완제품 품목")}
						<input type="checkbox" id='completePartCheck'>
					</td>
					
					<td class="tdwhiteL" colspan="3">
					
						<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('검색')}" id="searchCompletePart" name="searchCompletePart">
			            	<span></span>
			            	${f:getMessage('추가')}
			           	</button>
			           	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('검색')}" id="deleteCompletePart" name="deleteCompletePart">
			            	<span></span>
			            	${f:getMessage('삭제')}
			           	</button>
			           	
			           	<BR>
			           	
						<div id='completepartDiv'>
						</div>
					</td>
				</tr>
				
			</table>
		
			<table border="0" cellpadding="0" cellspacing="4" align="right">
                <tr>
                    <!-- <td><script>setButtonTag3D("검색","60","doSubmit();","");</script></td>
                    <td><script>setButtonTag3D("초기화","60","document.changeRequestForm.reset(), clearText2();","");</script></td> -->
				    <td>
	                  	<button type="button" class="btnSearch" title="${f:getMessage('검색')}" id="searchBtn" name="searchBtn">
		                  	<span></span>
		                  	${f:getMessage("검색")}
	                  	</button>   
	                </td>
	                  
	                <td>
	                 	 <button title="Reset" class="btnCustom" type="reset" id="btnReset">
	                 	 	<span></span>
	                 	 	${f:getMessage("초기화")}
	                 	 </button>
	                </td>
	                <td>
	                	 <a href="javascript:exportAUIExcel('ECO');">
					  		<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="middle">
				    	</a>
				    </td>
                </tr>
            </table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="grid_wrap" style="width: 100%; height:400px; margin-top: 10px; border-top: 3px solid #244b9c;">
			</div>
			<div id="grid_paging"
				class="aui-grid-paging-panel my-grid-paging-panel"></div>
		</td>
  	</tr>
</table>
		
</form>
</body>
</html>