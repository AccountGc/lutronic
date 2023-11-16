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
*                      AUI 그리드 
----------------------------------------------------------%>
var myGridID;
var myGridID2;
var defaultGridHeight = 61;
var rowHeight = 26;
var headerHeight = 24;
var footerHeight = 30;

//

<%----------------------------------------------------------
*                      변경 부품 칼럼 레이아웃 작성
----------------------------------------------------------%>
var columnLayout = [ 
	
	{
	    dataField : "matnr",
	    headerText : '${f:getMessage('품목번호')} ',
	    style: "left",
	    width: "10%",
	},
	
	{
	    dataField : "maktx",
	    headerText : '${f:getMessage('품목명')}',
	    style: "AUI_left",
	    width: "*%",
	    
	    renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			var temp = "<a href=javascript:openDistributeView('" + item.partOid + "') style='line-height:26px;'>" + value + "</a>"
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
		
	}, 
	{
	    dataField : "zeivr",
	    headerText : '${f:getMessage('REV')}',
	    width: "5%"
	}, 
	{
	    dataField : "revise",
	    headerText : '${f:getMessage('개정')}',
	    width: "5%"
	},
	{
	    dataField : "zspec",
	    headerText : '${f:getMessage('사양')}',
	    width: "20%"
	}, 
	{
	    dataField : "zmodel",
	    headerText : '${f:getMessage('프로젝트')}',
	    width: "15%"
	},
	{
	    dataField : "state",
	    headerText : '${f:getMessage('제작방법')}',
	    width: "10%"
	},
	{
	    dataField : "manufacture",
	    headerText : '${f:getMessage('업체')}',
	    width: "10%"
	},
	{
	    dataField : "zdept",
	    headerText : '${f:getMessage('부서')}',
	    width: "7%"
	}
	
	
];
<%----------------------------------------------------------
*                      변경 BOM 칼럼 레이아웃 작성
----------------------------------------------------------%>
var columnLayout2 = [ 
                	
	{
	    dataField : "matnr",
	    headerText : '${f:getMessage('모품목번호')} ',
	    style: "AUI_right",
	    width: "15%",
	},
	{
	    dataField : "pVer",
	    headerText : '${f:getMessage('REV')} ',
	    width: "5%",
	},
	{
	    dataField : "pName",
	    headerText : '${f:getMessage('모품목명')}',
	    style: "AUI_left",
	    width: "20%",
	    /*
	    renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			var temp = "<a href=javascript:openDistributeView('" + item.pOid + "') style='line-height:26px;'>" + value + "</a>"
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
		*/
	}, 
	{
	    dataField : "idnrk",
	    headerText : '${f:getMessage('자품목번호')}',
	    width: "15%"
	}, 
	{
	    dataField : "cVer",
	    headerText : '${f:getMessage('REV')} ',
	    width: "5%",
	},
	{
	    dataField : "cName",
	    headerText : '${f:getMessage('자품목명')}',
	    style: "AUI_left",
	    width: "20%",
	    renderer : { // HTML 템플릿 렌더러 사용
			type : "TemplateRenderer"
		},
		labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
			var temp = "<a href=javascript:openDistributeView('" + item.cOid + "') style='line-height:26px;'>" + value + "</a>"
			return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
		}
	}, 
	{
	    dataField : "zitmsta",
	    headerText : '${f:getMessage('변경코드')}',
	    width: "10%"
	},
	{
	    dataField : "menge",
	    headerText : '${f:getMessage('수량')}',
	    width: "5%"
	},
	{
	    dataField : "meins",
	    headerText : '${f:getMessage('단위')}',
	    width: "5%"
	}
	
];

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	
	//Aui 그리드 설정
	var auiGridProps = {
		selectionMode : "multipleCells",
		showRowNumColumn : true,
		softRemoveRowMode : false,
		processValidData : true,
		headerHeight : headerHeight,
		rowHeight : rowHeight,
		height : 250,
	};
	
	// 실제로 #grid_wrap 에 그리드 생성
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	searchPART();
	
	
	var auiGridProps2 = {
			selectionMode : "multipleCells",
			showRowNumColumn : true,
			softRemoveRowMode : false,
			processValidData : true,
			headerHeight : headerHeight,
			rowHeight : rowHeight,
			height : 300,
			// 그룹핑 후 셀 병함 실행
			enableCellMerge : true, 
			// 그룹핑, 셀머지 사용 시 브랜치에 해당되는 행 표시 안함.
			showBranchOnGrouping : false, 
			// 그룹핑 패널 사용
			useGroupingPanel : true,
			//그룹핑 필드
			groupingFields : ["matnr", "pName","pVer"],
			//순으로 그룹핑을 합니다.
			displayTreeOpen : true,
			
			
	};
	myGridID2 = AUIGrid.create("#grid_wrap2", columnLayout2, auiGridProps2);
	AUIGrid.bind(myGridID2, "cellClick", function( event ) {
	      var rowItems = AUIGrid.getSelectedItems(myGridID2);
	    //  console.log("cellClick maktx =");
	      if( event.dataField == "pName" ) {
	    	//  console.log("pName =" + rowItems[0].item.pName);
	   	  	var pOid = rowItems[0].item.pOid;  //oid
	   	 	openDistributeView(pOid);
	      }
	});
	searchBOM();
	
});

$(function () {
	
})

<%----------------------------------------------------------
*                     PART 검색
----------------------------------------------------------%>
function  searchPART(){
	var form = $("form[name=changePartForm]").serialize();
	var url	= getURLString("distribute", "listPARTERPAction", "do");
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
*                     PART 검색
----------------------------------------------------------%>
function  searchBOM(){
	var form = $("form[name=changePartForm]").serialize();
	var url	= getURLString("distribute", "listBOMERPAction", "do");
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
			AUIGrid.setGridData(myGridID2, gridData);
			
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	});
}


</script>

<body>

<form method="post" name="changePartForm" action="">

<input type="hidden" name="ecoNumber" id="ecoNumber" value="<c:out value="${ecoNumber }" />" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
    <tr>
        <td>
            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center">
		   				<B><font color=white><c:out value="${ecoNumber }" /></font></B>
		   			</td>
		   		</tr>
			</table>
            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
            	<tr>
					<td width="150">
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width="10" height="9" />
						<b>
							${f:getMessage('변경 자재 확인')}
						</b>
					</td>
					
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                    <td>
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
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>변경 부품 내역</b>
				<a href="#">
				<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="absmiddle" onclick="exportAUIExcel('PART_CHANGE')">
				
		</td>
	</tr>
	<tr >
		<td>
			<div id="grid_wrap" style="width: 100%; height:250px; margin-top: 2px; border-top: 3px solid #244b9c;">
			</div>
		</td>
	</tr> 
	<tr>
		<td align="left" height="20">
		</td>
	</tr>
	<tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>변경 BOM 내역</b>
				<a href="#">
				<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="absmiddle" onclick="exportAUIExcel2('BOM_CHANGE')">
				</a>
		</td>
	</tr>
	
	
	<tr >
		<td>
			<div id="grid_wrap2" style="width: 100%; height:300px; margin-top: 2px; border-top: 3px solid #244b9c;">
			</div>
		</td>
	</tr>
</table>

</form>

</body>
</html>