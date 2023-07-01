<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

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
	    dataField : "level",
	    headerText : 'Level',
	    width: "5%"
	},
	{
        dataField : "partNumber",
        headerText : '${f:getMessage('품번')}',
        style: "AUI_left",
        width: "15%"
	}, 
	{
        dataField : "partName",
        headerText : '${f:getMessage('품목명')}',
        style: "AUI_left",
        width: "*",
	}, 
	{
        dataField : "partState",
        headerText : '${f:getMessage('상태')}',
        width: "5%"
	}, 
	{
        dataField : "rohsState",
        headerText : 'ROHS 상태.',
        width: "10%",
       	renderer : { // HTML 템플릿 렌더러 사용
   			type : "TemplateRenderer"
   		},
   		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
   			
   			//var temp = "<a href=javascript:openDistributeView('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
   			return setRoHSStateImg(value); // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
   		}
	},
	{
        dataField : "rohsStateName",
        headerText : 'ROHS 상태명',
        width: "10%"
       
	}, 
	{
        dataField : "rohsName",
        headerText : '${f:getMessage('물질명')}',
        width: "15%"
	},
	{
        dataField : "rohslifeState",
        headerText : '${f:getMessage('물질 상태')}',
        width: "6%"
	},
	{
        dataField : "fileName",
        headerText : '${f:getMessage('파일명')}',
        width: "15%"
	},
	{
        dataField : "docType",
        headerText : '${f:getMessage('파일구분')}',
        width: "10%"
	},
	/*
	{
        dataField : "bom",
        headerText : 'BOM',
        width: "5%",
        renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			//console.log("value =" + value);
			var temp ="<button type='button' class='btnCustom' onclick=javascript:viewBom('" + item.oid+ "')><span></span>BOM</buttom>";
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
	},
	*/

];

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	
	/*
	//Aui 그리드 설정
	var auiGridProps = {
		selectionMode : "multipleCells",
		showRowNumColumn : true,
		softRemoveRowMode : false,
		processValidData : true,
		headerHeight : headerHeight,
		rowHeight : rowHeight,
		height : 500,
		
		// 그룹핑 후 셀 병함 실행
		enableCellMerge : true, 
		// 그룹핑, 셀머지 사용 시 브랜치에 해당되는 행 표시 안함.
		showBranchOnGrouping : false, 
		// 그룹핑 패널 사용
		useGroupingPanel : true,
		
		//순으로 그룹핑을 합니다.
		displayTreeOpen : true,
		
	};
	
	// 실제로 #grid_wrap 에 그리드 생성
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	
	AUIGrid.hideColumnByDataField(myGridID, "rohsStateName");
	*/
});


$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		
		//if($('#partOid').val() == "") {
		if($('#partNumber').val() == "") {
			//alert("${f:getMessage('부품')}${f:getMessage('을(를) 선택하세요.')}");
			alert("${f:getMessage('부품')}${f:getMessage('번호를 입력해 주세요.')}");
			return;
		}
		
		
		$("#sortValue").val("");
		$("#sortCheck").val("");
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	}),
	<%----------------------------------------------------------
	*                      초기화 버튼 클릭시
	----------------------------------------------------------%>
	$("#btnReset").click(function() {
	})
	$('#searchPart').click(function() {
		var url = getURLString("part", "selectPartPopup", "do") + "?mode=single";
		openOtherName(url,"window","1180","600","status=no,scrollbars=yes,resizable=yes");
	})
	<%----------------------------------------------------------
	*                     일괄 다운로드
	----------------------------------------------------------%>
	$("#batchROHSDown").click(function() {
		
		//$("#listRoHSPart").attr("method", "post");
		//$("#listRoHSPart").attr("action", getURLString("rohs", "batchROHSDown", "do")).submit();
		
		var form = $("form[name=listRoHSPart]").serialize();
		var url	= getURLString("rohs", "batchROHSDown", "do");
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
				console.log(data.message);
				if(data.result) {
					location.href = '/Windchill/jsp/common/content/FileDownload.jsp?fileName='+data.message+'&originFileName='+data.message;
				}else {
					alert(data.message);
				}
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
		
	})
	
	$( "#partNumber" ).keypress(function( event ) {
	  if ( event.which == 13 ) {
		  var partNumber = $("#partNumber").val();
		  if($.trim(partNumber) == "") {
				alert("부품 번호를 입력해 주세요");
				return;
		  }
		  lfn_Search();
	  }
	})
})

<%----------------------------------------------------------
*                      선택 품목 데이터 추가
----------------------------------------------------------%>
function addPart(obj,isHref) {
	$('#partNumber').val(obj[0][1]);
	$('#partOid').val(obj[0][0]);
}

<%----------------------------------------------------------
*                      데이터 검색 AJAX
----------------------------------------------------------%>
function  lfn_Search(){
	/*
	var url	= getURLString("rohs", "listRoHSPartAction", "do") + "?partOid="+$('#partOid').val();
	documentListGrid.clearAndLoad(url, null, "json");
	*/
	var form = $("form[name=listRoHSPart]").serialize();
	var url	= getURLString("rohs", "listAUIRoHSPartAction", "do");
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
			//var gridData = data;
			var columns = new Array();
			var totalLevel = data.totalLevel;
			var gridData = data.partRohlist;
			var totalState = data.totalState;
			var passCount = data.passCount;
			var totalCount = data.totalCount;
			var greenCount = data.greenCount;
			var blackCount = data.blackCount;
			var continueCount = data.continueCount;
			var dumyCount = data.dumyCount;
			var orangeCount = data.orangeCount;
			var listCount = data.listCount;
			var isDumy_SonPartsCount = data.isDumy_SonPartsCount;
			
			var redCount = data.redCount;
			totalState = totalState+"%";
			//totalState ="<b><font color='red'>"+totalState+"%</font><b>"
			console.log("greenCount:"+greenCount+"\tblackCount:"+blackCount+"\tredCount:"+redCount+"\torangeCount:"+orangeCount+"\tcontinueCount:"+continueCount+"\tdumyCount:"+dumyCount+"\tlistCount:"+listCount+"\tisDumy_SonPartsCount="+isDumy_SonPartsCount);
			amount =" ("+passCount+"/"+totalCount+")";
			//$('#totalState').html(totalState)
			//$('#amount').html(amount)
			$('#totalStateBox').val(totalState+amount)
			for(var i=0 ; i <= totalLevel ; i++){
				var column = new Object();
				column["DISPLAYNAME"] = "L"+i;
				column["NAME"] = "L"+i;
				column["WIDTH"] = "2%";
				columns.push(column);
				
			}
			columns = setDefalutColumn(columns)
			
			var gridData = new Object();
			gridData["columns"] = columns;
			gridData["tableInfoList"] = [];
			gridData["itemList"] = data.partRohlist;
			
			createDynamicAUIGrid(gridData);
			
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
*                      DefalutColumn Setting
----------------------------------------------------------%>
function setDefalutColumn( columns){
	var column = new Object();
	column["NAME"] = "partNumber";
	column["DISPLAYNAME"] = '${f:getMessage('품번')}';
	column["STYLE"] = "AUI_left";
	column["WIDTH"] = "7%";
	
	columns.push(column);
	
	column = new Object();
	column["NAME"] = "partName";
	column["DISPLAYNAME"] = '${f:getMessage('품목명')}';
	column["STYLE"] = "AUI_left";
	column["WIDTH"] = "*";
	columns.push(column);
	
	column = new Object();
	column["NAME"] = "partState";
	column["DISPLAYNAME"] = '${f:getMessage('상태')}';
	//column["STYLE"] = "AUI_left";
	column["WIDTH"] = "5%";
	columns.push(column);
	
	column = new Object();
	column["NAME"] = "rohsState";
	column["DISPLAYNAME"] = 'ROHS ${f:getMessage('상태')}';
	//column["STYLE"] = "AUI_left";
	column["WIDTH"] = "7%";
	columns.push(column);
	
	column = new Object();
	column["NAME"] = "rohsStateName";
	column["DISPLAYNAME"] = '${f:getMessage('상태 색상')}';
	//column["STYLE"] = "AUI_left";
	column["WIDTH"] = "5%";
	columns.push(column);
	
	column = new Object();
	column["NAME"] = "rohsNumber";
	column["DISPLAYNAME"] = '${f:getMessage('물질번호')}';
	//column["STYLE"] = "AUI_left";
	column["WIDTH"] = "7%";
	columns.push(column)
	
	column = new Object();
	column["NAME"] = "rohsName";
	column["DISPLAYNAME"] = '${f:getMessage('물질명')}';
	//column["STYLE"] = "AUI_left";
	column["WIDTH"] = "13%";
	columns.push(column)
	
	column = new Object();
	column["NAME"] = "rohslifeState";
	column["DISPLAYNAME"] = '${f:getMessage('물질 상태')}';
	//column["STYLE"] = "AUI_left";
	column["WIDTH"] = "6%";
	columns.push(column)
	
	column = new Object();
	column["NAME"] = "fileName";
	column["DISPLAYNAME"] = '${f:getMessage('파일명')}';
	//column["STYLE"] = "AUI_left";
	column["WIDTH"] = "10%";
	columns.push(column)
	
	column = new Object();
	column["NAME"] = "docType";
	column["DISPLAYNAME"] = '${f:getMessage('파일구분')}';
	//column["STYLE"] = "AUI_left";
	column["WIDTH"] = "6%";
	columns.push(column)
	
	return columns;
}

<%----------------------------------------------------------
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	AUIGrid.resize(myGridID);
}
<%----------------------------------------------------------
*                     ROHS 상태 이미지
----------------------------------------------------------%>
function setRoHSStateImg(rohsState){
	
	var state ="<img src='/Windchill/jsp/portal/images/tree/task_ready.gif'>";
	if(rohsState == "3"){
		state ="<img src='/Windchill/jsp/portal/images/tree/task_red.gif'>";
	}else if(rohsState =="1"){
		state ="<img src='/Windchill/jsp/portal/images/tree/task_orange.gif'>";
	}else if(rohsState =="2"){
		state ="<img src='/Windchill/jsp/portal/images/tree/task_complete.gif'>";
	}
	
	return state;
}


 <%----------------------------------------------------------
 *                 동적으로 그리드 Header 생성
 ----------------------------------------------------------%>
function createDynamicAUIGrid(data) {
	
	var columnInfoList = data.columns; // 칼럼 정보
	var gridData = data.itemList; // 실 데이터
	
	//alert(gridData);
	tableInfoList = data.tableInfoList;
		
	// 이전에 그리드가 생성되었다면 제거함.
	if(AUIGrid.isCreated(myGridID)) {
		AUIGrid.destroy(myGridID);
	}
	
	var firstRow;
	var columnLayoutInfo;
	if(columnInfoList && columnInfoList.length > 0) {
		columnLayoutInfo = getDynamicColumns(columnInfoList); //칼럼 정보를 바탕으로 칼럼 레이아웃 동적 생성
	} else {
		alert("칼럼 정보가 없어 그리드를 생성할 수 없습니다.");
		return;
	}
	
	if(!columnLayoutInfo || !columnLayoutInfo.columnLayout) {
		alert("칼럼 정보가 없어 그리드를 생성할 수 없습니다.");
		return;
	}
	
	// 그리드 속성
	var gridProps = {
			selectionMode : "multipleCells",
			showRowNumColumn : true,
			softRemoveRowMode : false,
			processValidData : true,
			headerHeight : headerHeight,
			rowHeight : rowHeight,
			showAutoNoDataMessage : false,
			height : 500,
			
			// 그룹핑 후 셀 병함 실행
			enableCellMerge : true, 
			// 그룹핑, 셀머지 사용 시 브랜치에 해당되는 행 표시 안함.
			showBranchOnGrouping : false, 
			// 그룹핑 패널 사용
			useGroupingPanel : true,
			
			//순으로 그룹핑을 합니다.
			displayTreeOpen : true,
			//fixed 
			
			
	}
	gridProps.fixedColumnCount = columnInfoList.length-8;
	//console.log("columnInfoList.length"+columnInfoList.length);
	
	// 실제로 #grid_wrap 에 그리드 생성
	myGridID = AUIGrid.create("#grid_wrap", columnLayoutInfo.columnLayout, gridProps);
	// 데이터 삽입
	AUIGrid.setGridData(myGridID, gridData);
	
	
	
	
	AUIGrid.hideColumnByDataField(myGridID, "rohsStateName");
}
<%----------------------------------------------------------
*                  동적으로 칼럼 생성하여 반환.
----------------------------------------------------------%>
function getDynamicColumns(columnInfoList) {
	
	var columnLayout = [];
	var column;
	var cInfo;
	var width = 0;
	for(var i=0, len=columnInfoList.length; i<len; i++) {
		cInfo = columnInfoList[i];
		column = {};
		
		column.dataField = cInfo.NAME;
		
		width = Math.max(width, 100);
		
		column.headerText = cInfo.DISPLAYNAME;
		column.style = cInfo.STYLE;
		if(cInfo.WIDTH){
			column.width = cInfo.WIDTH;
		}
		
		if(cInfo.NAME == "rohsState"){
			column.renderer = {
				type : "TemplateRenderer"
			},
			column.labelFunction = function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
				
				//var temp = "<a href=javascript:openDistributeView('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
				return setRoHSStateImg(value); // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
			}
		}else if(cInfo.NAME == "partName"){
			column.renderer = {
				type : "TemplateRenderer"
			},
			column.labelFunction = function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
					
				var temp = "<a href=javascript:openView('" + item.partOid + "') style='line-height:26px;'>" + value + "</a>"
				return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
			}
		}
		
		columnLayout.push(column);
		
	}
	
	return { "rowIdField" : "_$uid", "columnLayout" : columnLayout };
}
<%----------------------------------------------------------
*                  Excel 다룬로드
----------------------------------------------------------%>
function exportRohs(){
	
	var fileName = $('#partNumber').val()+"_ROHS";
	exportAUIExcel(fileName);
}


</script>

<body>

<form name="listRoHSPart" id="listRoHSPart" >

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<!-- 폴더 선택시 검색 function을 호출하기 위한 변수 -->
<input type="hidden" name="search" id="search" value="true" />

<!-- 정렬을 사용할때 사용하는 변수 -->
<input type="hidden" name="sortValue" id="sortValue" />
<input type="hidden" name="sortCheck" id="sortCheck" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							RoHS
							${f:getMessage('관리')} 
							> 
							${f:getMessage('부품현황')} 
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->
	<tr align="center">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
						
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<colgroup>
					<col width='10%'>
					<col width='40%'>
					<col width='10%'>
					<col width='40%'>
				</colgroup>
				
				<tr bgcolor="ffffff" height="35">
				
					<td class="tdblueM">
						${f:getMessage('부품')}
					</td>
					<td class="tdwhiteL">
						<input type='hidden' name='partOid' id='partOid' />
						<input type='text' name='partNumber' id='partNumber' !readonly="readonly" />
						<input type='text' name='totalStateBox' id='totalStateBox' !readonly="readonly" size='10'/ disabled=true>
						<img src="/Windchill/jsp/portal/images/s_search.gif" border=0 id='searchPart' style="cursor: pointer;">
						<span id="totalState" style=""></span> <span id="amount" style=""></span>
			           
					</td>
				</tr>
			
			</table>
			
		</td>
	</tr>
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="4" align="right">
	              <tr>
	              	
	                  <td>
	                  	<button type="button" name="" value="" class="btnSearch" title="검색" id="searchBtn" name="searchBtn">
		                  	<span></span>
		                  	${f:getMessage('검색')}
	                  	</button>   
	                  </td>
	                   <td>
	              	 	<button type="button" name="batchROHSDown" id="batchROHSDown" class="btnCustom">
							<span></span>
							${f:getMessage('일괄 다운')}
						</button>
	              	 </td>
	                  <td>
	                 	 <button title="Reset" class="btnCustom" type="reset" id="btnReset">
	                 	 	<span></span>
	                 	 	${f:getMessage('초기화')}
	                 	 </button>
	                  </td>
	                  
	                  <td>
		                  <a href="#">
						  	<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="absmiddle" onclick="exportRohs()">
					      </a>
				      </td>
	              </tr>
	          </table>
		</td>
	</tr>
	
	<tr>
		<td>
			<div id="grid_wrap" style="width: 100%; height:500px; margin-top: 10px; border-top: 3px solid #244b9c;">
			</div>
		</td>
  	</tr>
	
</table>

</form>

</body>
</html>