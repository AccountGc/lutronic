<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script>
<%----------------------------------------------------------
*                      AUI 그리드 
----------------------------------------------------------%>
var myGridID;

var defaultGridHeight = 61;
var rowHeight = 26;
var headerHeight = 24;
var footerHeight = 30;
var production = "${production}";
//칼럼 레이아웃 작성
var columnLayout = [ 
	
	{
	    dataField : "icon",
	    headerText : '부품',
	    width: "5%",
    	renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			return value; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
		
	},
	
	{
        dataField : "number",
        headerText : '${f:getMessage('품번')}',
        width: "8%"
	}, 
	{
        dataField : "name",
        headerText : '${f:getMessage('품번명')}',
        style: "AUI_left",
        width: "*",
        
        renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			//var rowItems = AUIGrid.getSelectedItems(myGridID);
			
			var isProduct = item.isProduct;
			
			//console.log(item.number +" isProduct =" + isProduct +",production=" + production);
			var temp = "<a href=javascript:openDistributeView('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
			if(production == "true"){
				temp = "<a href=javascript:openProductView('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
				//console.log(temp);
			}
			
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
		
	}, 
	{
        dataField : "location",
        headerText : '${f:getMessage('품번분류')}',
        style: "AUI_left",
        width: "20%"
	}, 
	{
        dataField : "rev",
        headerText : 'Rev.',
        width: "5%"
	},
	{
        dataField : "remarks",
        headerText : 'OEM Info.',
        width: "10%"
	},
	{
        dataField : "state",
        headerText : '${f:getMessage('상태')}',
        width: "6%"
	},
	{
        dataField : "creator",
        headerText : '${f:getMessage('등록자')}',
        width: "6%"
	},
	{
        dataField : "createDate",
        headerText : '${f:getMessage('등록일')}',
        width: "7%"
	},
	{
        dataField : "modifyDate",
        headerText : '${f:getMessage('수정일')}',
        width: "7%"
	},
	{
        dataField : "bom",
        headerText : 'BOM',
        width: "5%",
        renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			//console.log("value =" + value); '
			var temp ="<span ><button type='button' class='btnCustom' onclick=javascript:viewBaselineBom('" + item.oid+ "') >BOM</buttom></span>";
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
	},

];

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	
	//Aui 그리드 설정
	var auiGridProps = {
		selectionMode : "multipleCells",
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
	
	partTitleSet();
	
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	gfn_InitCalendar("predate_modify", "predate_modifyBtn");
	gfn_InitCalendar("postdate_modify", "postdate_modifyBtn");
	gfn_InitCalendar("ecoPostdate", "ecoPostdateBtn");
	gfn_InitCalendar("ecoPredate", "ecoPredateBtn");
	lfn_getStateList();
	getQuantityUnit();
	numberCodeList('mat');
	numberCodeList('deptcode');
	numberCodeList('model');
	numberCodeList('manufacture');
	numberCodeList('finish');
	numberCodeList('productmethod');
	
	//lfn_Search();
	getGridData(1, 14);
});



<%----------------------------------------------------------
*                      Title Setting 
----------------------------------------------------------%>
window.partTitleSet = function() {
	var Production = "${production}";
	//alert("${production}");
		if(Production =="false"){
		
		$("#partTitle").html("품목 검색")
	}else{
		$("#partTitle").html("완제품 검색")
	}
	
}
<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id) {
	var type = "";
	if(id == 'partType1' || id == 'partType2' || id =='partType3') {
		type = "PARTTYPE";
	}else {
		type = id.toUpperCase();
	}
	
	var data = common_numberCodeList(type, '', true);
	
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



$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		resetSearch();
	})
	<%----------------------------------------------------------
	*                      상세검색 검색 버튼
	----------------------------------------------------------%>
	$("#detailBtn").click(function () {
		if($('#SearchDetailProject').css('display') == 'none'){ 
			$("#detailText").html("${f:getMessage('상세검색')} -");
		   	$('#SearchDetailProject').show(); 
		} else { 
			$("#detailText").html("${f:getMessage('상세검색')} +");
		   	$('#SearchDetailProject').hide(); 
		}
	})
	<%----------------------------------------------------------
	*                      초기화 버튼
	----------------------------------------------------------%>
	$("#btnReset").click(function() {
		$("#creator").val("");
		$("#fid").val("");
		$("#location").val("/Default/PART_Drawing");
		$("#locationName").html("/Default/PART_Drawing");
	})
	$(document).keypress(function(event) {
		 if(event.which == 13 && $( '#userNameSearch' ).is( ':hidden' )) {
			 resetSearch();
		 }
	})
})

window.resetSearch = function() {
	//lfn_DhtmlxGridInit();
	$("#sortValue").val("");
	$("#sortCheck").val("");
	$("#sessionId").val("");
	$("#page").val(1);
	//lfn_Search();
	getGridData(1, 14);
}


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
	
	var form = $("form[name=PartDrawingForm]").serialize();
	
	var url	= getURLString("distribute", "listPagingPartAction", "do");
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
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=PartDrawingForm]").serialize();
	var url	= getURLString("distribute", "listPartAction", "do");
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
*                      Lifecycle 상태 가져오기
----------------------------------------------------------%>
function lfn_getStateList() {
	var form = $("form[name=PartDrawingForm]").serialize();
	var url	= getURLString("WFItem", "lifecycleList", "do");
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
*                      단위 리스트 가져오기
----------------------------------------------------------%>
function getQuantityUnit() {
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
			$("#unit").append("<option value=''> ${f:getMessage('선택')} </option>");
			for(var i=0; i<data.length; i++) {
				$("#unit").append("<option value='" + data[i] + "'>" + data[i] + "</option>");
			}
		}
	});
}

<%----------------------------------------------------------
*                      유저 데이터 설정
----------------------------------------------------------%>
function addUser(obj) {
	$("#creator").val(obj[0][0]);
	$("#creatorName").val(obj[0][1]);
}


<%----------------------------------------------------------
*                      BOM 보기
----------------------------------------------------------%>
function viewBom(oid) {
	var str = getURLString("part", "PartTree", "do") + "?oid="+oid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBOM", opts+rest);
    newwin.focus();
}
<%----------------------------------------------------------
*                      엑셀 다운로드
----------------------------------------------------------%>
function onExcelDown() {
	$("#PartDrawingForm").attr("method", "post");
	$("#PartDrawingForm").attr("action", getURLString("excelDown", "partExcelDown", "do")).submit();
}

<%----------------------------------------------------------
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	
	AUIGrid.resize(myGridID);
}
</script>

<body>

<form name="PartDrawingForm" id="PartDrawingForm">

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

<input type="hidden" name="lifecycle" 		id="lifecycle"		value="LC_PART">
<input type="hidden" name="fid"				id="fid" 			value="">

<input name="islastversion" type="hidden"  value="false" />
<input name="state" type="hidden"  value="APPROVED" />
<input name="production" type="hidden"  value="${production}" />


<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="1" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp; ${f:getMessage('품목')} ${f:getMessage('관리')} >
							 <span id=partTitle></span>
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
    <tr align="center">
        <td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
			    <tr>
			        <td height="1" width=100%></td>
			    </tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			    <col width="13%"><col width="37%"><col width="13%"><col width="37%">
			    
			    <tr>
			        <td class="tdblueM">
			        	${f:getMessage('품목분류')}
			        </td>
			        
			        <td class="tdwhiteL">
			        	<b>
			        		<span id="locationName">
			        			/Default/PART_Drawing
			        		</span>
			        	</b>
			        </td>
			        
			        <td class="tdblueM">
						${f:getMessage('등록자')}
					</td>
					
	                <td class="tdwhiteL">
	                
	                   	<%-- 
	                   	<input type="hidden" id="creator" name="creator" value="" />
						<input type="text" id="creatorName" name="creatorName" size="30" class="txt_field" style="width:30%" value="" readOnly/>
						
						<a href="JavaScript:searchUser('documentForm','single','creator','creatorName','people')">
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
			    </tr>
			    
			    <tr>
			        <td class="tdblueM">
			        	${f:getMessage('품목번호')}
			        </td>
			        
			        <td class="tdwhiteL">
			            <input name="partNumber" class="txt_field" size="30" style="width:90%" onblur="this.value=this.value.toUpperCase();" />
			        </td>
			        
			        <td class="tdblueM">
			        	${f:getMessage('품목명')}
			        </td>
			        
			        <td class="tdwhiteL">
			            <input name="partName" class="txt_field" size="30" style="width:90%" />
			        </td>
			    </tr>

				<tr>
					<td class="tdblueM">
						${f:getMessage('등록일')}
					</td>
					
					<td class="tdwhiteL">
					    <input name="predate" id="predate" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="predateBtn" id="predateBtn" />
						</a>
						<a href="javascript:void(0);" onclick="clearText('predate');">
							<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
						</a>
						~
						<input name="postdate" id="postdate" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="postdateBtn" id="postdateBtn" />
						</a>
						<a href="javascript:void(0);" onclick="clearText('postdate');">
							<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
						</a>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('수정일')}
					</td>
					
					<td class="tdwhiteL">
					    <input name="predate_modify" id="predate_modify" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="predate_modifyBtn" id="predate_modifyBtn" />
						</a>
						<a href="javascript:void(0);" onclick="clearText('predate_modify');">
							<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
						</a>
						~
						<input name="postdate_modify" id="postdate_modify" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="postdate_modifyBtn" id="postdate_modifyBtn"/>
						</a>
						<a href="javascript:void(0);" onclick="clearText('postdate_modify');">
							<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
						</a>
					</td>
				</tr>
				<tr>
					<td class="tdblueM">
						ECO Date
					</td>
					
					<td class="tdwhiteL" colspan="3">
					    <input name="ecoPredate" id="ecoPredate" class="txt_field" size="12"  maxlength="15" readonly="readonly" value='${ecoPredate}'/>
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="ecoPredateBtn" id="ecoPredateBtn" />
						</a>
						<a href="javascript:void(0);" onclick="clearText('ecoPredate');">
							<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
						</a>
						~
						<input name="ecoPostdate" id="ecoPostdate" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="ecoPostdateBtn" id="ecoPostdateBtn" />
						</a>
						<a href="javascript:void(0);" onclick="clearText('ecoPostdate');">
							<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
						</a>
					</td>
				</tr>
				
			</table>
         	
         	<div id="SearchDetailProject" style="display: none;" >
	         	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
	         	<col width="13%"><col width="37%"><col width="13%"><col width="37%">
	         	
		         	<!-- MODEL, PRODUCTMETHOD -->			
					<tr bgcolor="ffffff" height="35">
						<td class="tdblueM">
							${f:getMessage('프로젝트코드')}
						</td>
						
						<td class="tdwhiteL">
							<select id='model' name='model' style="width: 95%">
							</select>
						</td>
						
						<td class="tdblueM">
							${f:getMessage('제작방법')}
						</td>
						
						<td class="tdwhiteL">
							<select id='productmethod' name='productmethod' style="width: 95%">
							</select>
						</td>
					</tr>
					
					<!-- DEPTCODE, UNIT -->
	              	<tr bgcolor="ffffff" height="35">
	              		<td class="tdblueM">
							${f:getMessage('부서')}
						</td>					
						
						<td class="tdwhiteL">
							<select id="deptcode" name="deptcode" style="width: 95%">
							</select>
						</td>
						
						<td class="tdblueM">
							${f:getMessage('단위')}
						</td>					
						
						<td class="tdwhiteL">
							<select id='unit' name='unit' style="width: 95%">
							</select>
						</td>
					</tr>
	                
					<!-- WEIGHT, MANUFACTURE -->
					<tr bgcolor="ffffff" height="35">
						<td class="tdblueM">
							${f:getMessage('무게')}
						</td>
						
						<td class="tdwhiteL">
							<input id="weight" name="weight" type="text" style="width: 95%;"/>
						</td>
						
						<td class="tdblueM">
							MANUFACTURER
						</td>
						
						<td class="tdwhiteL">
							<select id="manufacture" name="manufacture" style="width: 95%">
							</select>
						</td>
					</tr>
					
					<!-- MAT, FINISH -->
					<tr bgcolor="ffffff" height="35">
						<td class="tdblueM">
							${f:getMessage('재질')}
						</td>
						
						<td class="tdwhiteL">
							<select id="mat" name="mat" style="width: 95%">
							</select>
						</td>
						
						<td class="tdblueM">
							${f:getMessage('후처리')}
						</td>
						
						<td class="tdwhiteL">
							<select id="finish" name="finish" style="width: 95%">
							</select>
						</td>
					</tr>
					
					<!-- REMARKS, SPECIFICATION -->
					<tr bgcolor="ffffff" height="35">
	               		<td class="tdblueM">
							${f:getMessage('OEM Info.')}
						</td>
						
						<td class="tdwhiteL">
							<input id="remarks" name="remarks" type="text" style="width: 95%;"/>
						</td>
						
	               		<td class="tdblueM">
							${f:getMessage('사양')}
						</td>
						
						<td class="tdwhiteL">
							<input id="specification" name="specification" type="text" style="width: 95%;"/>
						</td>
					</tr>
		         	<tr bgcolor="ffffff" height="35">
		               		<td class="tdblueM">
								${f:getMessage('ECO No.')}
							</td>
							
							<td class="tdwhiteL" >
								<input id="ecoNo" name="ecoNo" type="text" style="width: 95%;"/>
							</td>
							<td class="tdblueM">
								${f:getMessage('EO No.')}
							</td>
							
							<td class="tdwhiteL">
								<input id="eoNo" name="eoNo" type="text" style="width: 95%;"/>
							</td>
						</tr>
	         	</table>
         	</div>
       	</td>
   	</tr>
    <tr>
        <td>
            <table border="0" cellpadding="0" cellspacing="4" align="right">
	              <tr>
	                  <td>
	                  	<button type="button" name="" value="" class="btnSearch" title="${f:getMessage('검색')}" id="searchBtn" name="searchBtn">
		                  	<span></span>
		                  	${f:getMessage('검색')}
	                  	</button>   
	                  </td>
	                  
	                  <td>
	                 	 <button title="Reset" class="btnCustom" type="reset" id="btnReset">${f:getMessage('초기화')}</button>
	                  </td>
	                  
	                  <td>
	                  	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('상세검색')}" id="detailBtn" name="detailBtn">
		                  	<span></span>
		                  	<font id="detailText" >${f:getMessage('상세검색')} + </font>
	                  	</button>   
	                  </td>
	                  
	                  <td>
		                  <a href="#">
						  	<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="absmiddle" onclick="exportAUIExcel('PART')">
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