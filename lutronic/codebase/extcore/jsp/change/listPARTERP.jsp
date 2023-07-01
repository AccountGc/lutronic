<%@page import="com.e3ps.common.iba.AttributeKey.ECOKey"%>
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
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	lfn_DhtmlxGridInit();
	gfn_InitCalendar("preCreateDate", "preCreateDateBtn");
	gfn_InitCalendar("postCreateDate", "postCreateDateBtn");
	lfn_Search();
})

$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		lfn_DhtmlxGridInit();
		$("#sortValue").val("");
		$("#sortCheck").val("");
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	}),
	<%----------------------------------------------------------
	*                      초기화 버튼
	----------------------------------------------------------%>
	$("#btnReset").click(function() {
		$("#creator").val("");
	})
	
	<%----------------------------------------------------------
	*                      ERP 전송 현황 체크
	----------------------------------------------------------%>
	$("#ERPCheckBtn").click(function (){
		if($.trim($("#startZifno").val()) == ""){
			alert("ERP ${f:getMessage('인터페이스 번호를 입력하세요.')}");
			$("#startZifno").focus();
			return;
		}
		
		var form = $("form[name=partERPForm]").serialize();
		var url	= getURLString("erp", "erpCheckAction", "do");
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
				alert(data.message)
				if(data.result){
					lfn_Search()
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

})




<%----------------------------------------------------------
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	
	var form = $("form[name=partERPForm]").serialize();
	var url	= getURLString("erp", "listPARTERPAction", "do");
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
			
			var vArr = new Array();
			vArr[0] = data.rows    ;
			vArr[1] = data.formPage   ;
			vArr[2] = data.totalCount ;
			vArr[3] = data.totalPage  ;
			vArr[4] = data.currentPage;
			vArr[5] = data.startPage  ;
			vArr[6] = data.endPage    ;
			vArr[7] = data.sessionId  ;
			
			documentListGrid.clearAll();
			documentListGrid.loadXMLString(data.xmlString);
			
			$("#xmlString").val(data.xmlString);
			
			gfn_SetPaging(vArr,"pagingBox");
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
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	var COLNAMEARR = {
			 col01 : '${f:getMessage("번호")}'           //
			,col02 : '${f:getMessage("I/F 번호")}'		//sort zifno  col02
			,col03 : '${f:getMessage("자재번호")}'			//sort  matnr  col03
			,col04 : '${f:getMessage("자재명")}'			//sort  maktx  col04
			,col05 : '${f:getMessage("단위")}'
			,col06 : '${f:getMessage("자재그룹")}'
			,col07 : '${f:getMessage("순중량")}'
			,col08 : '${f:getMessage("중량단위 (g)")}'
			,col09 : '${f:getMessage("재질")}'
			,col10 : '${f:getMessage("사양")}'
			,col11 : '${f:getMessage("후처리")}'
			,col12 : '${f:getMessage("프로젝트")}'
			,col13 : '${f:getMessage("제작방법")}'
			,col14 : '${f:getMessage("부서")}'
			,col15 : '${f:getMessage("부품분류")}'
			,col16 : '${f:getMessage("대분류")}'
			,col17 : '${f:getMessage("중분류")}'
			,col18 : '${f:getMessage("변경번호")}'        //sort  aennr
			,col19 : '${f:getMessage("REV")}'
			,col20 : 'I/F ${f:getMessage("상태")}'   ///sort  zifsta  col20
			,col21 : 'I/F ${f:getMessage("결과")}'
			,col22 : '${f:getMessage("전송일")}'
			,col23 : 'ERP ${f:getMessage("적용 상태")}'
			,col24 : 'ERP ${f:getMessage("적용 결과")}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	sHeader += "," + COLNAMEARR.col05;
	sHeader += "," + COLNAMEARR.col06;
	sHeader += "," + COLNAMEARR.col07;
	sHeader += "," + COLNAMEARR.col08;
	sHeader += "," + COLNAMEARR.col09;
	sHeader += "," + COLNAMEARR.col10;
	sHeader += "," + COLNAMEARR.col11;
	sHeader += "," + COLNAMEARR.col12;
	sHeader += "," + COLNAMEARR.col13;
	sHeader += "," + COLNAMEARR.col14;
	sHeader += "," + COLNAMEARR.col15;
	sHeader += "," + COLNAMEARR.col16;
	sHeader += "," + COLNAMEARR.col17;
	sHeader += "," + COLNAMEARR.col18;
	sHeader += "," + COLNAMEARR.col19;
	sHeader += "," + COLNAMEARR.col20;
	sHeader += "," + COLNAMEARR.col21;
	sHeader += "," + COLNAMEARR.col22;
	sHeader += "," + COLNAMEARR.col23;
	sHeader += "," + COLNAMEARR.col24;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";
	sHeaderAlign[7]  = "text-align:center;";
	sHeaderAlign[8]  = "text-align:center;";
	sHeaderAlign[9]  = "text-align:center;";
	sHeaderAlign[10]  = "text-align:center;";
	sHeaderAlign[11]  = "text-align:center;";
	sHeaderAlign[12]  = "text-align:center;";
	sHeaderAlign[13]  = "text-align:center;";
	sHeaderAlign[14]  = "text-align:center;";
	sHeaderAlign[15]  = "text-align:center;";
	sHeaderAlign[16]  = "text-align:center;";
	sHeaderAlign[17]  = "text-align:center;";
	sHeaderAlign[18]  = "text-align:center;";
	sHeaderAlign[19]  = "text-align:center;";
	sHeaderAlign[20]  = "text-align:center;";
	sHeaderAlign[21]  = "text-align:center;";
	sHeaderAlign[22]  = "text-align:center;";
	sHeaderAlign[23]  = "text-align:center;";
	

	var sWidth = "";
	sWidth += "5";	
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",5"; //단위 5
	sWidth += ",5"; 
	sWidth += ",5"; //순중량 7
	sWidth += ",5";
	sWidth += ",10";
	sWidth += ",10";  //10
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",5";  //부서 13
	sWidth += ",5"; //부품 분류 14
	sWidth += ",5";
	sWidth += ",5";
	sWidth += ",5";
	sWidth += ",10";
	sWidth += ",5";
	sWidth += ",6";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center"; 
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";  //10
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	
	
	var sColType = "";
	sColType += "ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";  //7
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";  //10
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";

	var sColSorting = "";
	sColSorting += "na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";  //7
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";  //10
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	
	
	documentListGrid = new dhtmlXGridObject('listGridBox');
	documentListGrid.setImagePath("/Windchill/jsp/js/dhtmlx/imgs/");
	documentListGrid.setHeader(sHeader,null,sHeaderAlign);
	documentListGrid.enableAutoHeight(true);
	documentListGrid.setInitWidthsP(sWidth);
	documentListGrid.setColAlign(sColAlign);
	documentListGrid.setColTypes(sColType);
	documentListGrid.setColSorting(sColSorting);
	
	/*grid text copy option*/
    documentListGrid.enableBlockSelection();
    documentListGrid.forceLabelSelection(true);
    documentListGrid.attachEvent("onKeyPress",function onKeyPressed(code,ctrl,shift){
        if(code==67&&ctrl){
            if(!documentListGrid._selectionArea) return alert("return");
            documentListGrid.setCSVDelimiter("\t");
            documentListGrid.copyBlockToClipboard();
	    }
	    return;
	});
	
	documentListGrid.attachEvent("onHeaderClick",function(rowId,cellIndex){
		
		var sortValue = "";
		var header = "";
		
		if(rowId == 1) {
			sortValue = "zifno";      // 인터페이스번호
			header = COLNAMEARR.col02;  
		}else if(rowId == 2 ){		  //자재번호
			sortValue = "matnr";
			header = COLNAMEARR.col03;
		}else if(rowId == 3 ) {
			sortValue = "maktx";		//자재명
			header = COLNAMEARR.col04;
		}else if(rowId == 17 ) {
			sortValue = "maktx";		//변경번호
			header = COLNAMEARR.col18;	
		}else if(rowId == 19 ) {
			sortValue = "zifsta";		//인터페이스상태
			header = COLNAMEARR.col20;	
		}else if(rowId == 22) {
			sortValue = "thePersistInfo.createStamp";  //전송일
			header = COLNAMEARR.col21;
		}else if(rowId == 22) {
			sortValue = "returnZifsta";  //ERP 전송 결과
			header = COLNAMEARR.col23;
		}
		
		
		
		if(sortValue != "") {
			if($("#sortValue").val() != sortValue) {
				$("#sortCheck").val("");
			}
			
			if("true" == $("#sortCheck").val()) {
				$("#sortCheck").val("false");
				header += " ▼";
			} else {
				$("#sortCheck").val("true");
				header += " ▲";
			}

			documentListGrid.setColLabel(1, COLNAMEARR.col02);
			documentListGrid.setColLabel(2, COLNAMEARR.col03);
			documentListGrid.setColLabel(3, COLNAMEARR.col04);
			documentListGrid.setColLabel(17, COLNAMEARR.col18);
			documentListGrid.setColLabel(19, COLNAMEARR.col20);
			documentListGrid.setColLabel(22, COLNAMEARR.col21);
			documentListGrid.setColLabel(23, COLNAMEARR.col22);
			documentListGrid.setColLabel(rowId, header);
			
			$("#sortValue").val(sortValue);
			$("#sessionId").val("");
			$("#page").val(1);
			lfn_Search();
		}
	});
	
	documentListGrid.init();
}

<%----------------------------------------------------------
*                      그리드 다시 그리기
----------------------------------------------------------%>
function redrawGrid(){
	documentListGrid.clearAll();
	documentListGrid.loadXMLString($("#xmlString").val());
}



</script>

<body>
<form name="partERPForm" id="partERPForm" method=post style="padding:0px;margin:0px">
<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<!-- 폴더 선택시 검색 function을 호출하기 위한 변수 -->
<input type="hidden" name="search" id="search" value="true" />

<!-- 정렬을 사용할때 사용하는 변수 -->
<input type="hidden" name="sortValue" id="sortValue" />
<input type="hidden" name="sortCheck" id="sortCheck" />

<input type="hidden" name="lifecycle" id="lifecycle" value="LC_ECO">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp; ERP ${f:getMessage('전송')}&nbsp;${f:getMessage('현황')} > PART ${f:getMessage('전송')}&nbsp;${f:getMessage('현황')}
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
					<td class="tdblueM">${f:getMessage("인터페이스")}&nbsp;${f:getMessage("번호")}</td>
					<td class="tdwhiteL">
						<input name="zifno" id="zifno" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
					<td class="tdblueM">${f:getMessage("자재")}&nbsp;${f:getMessage("번호")}</td>
					<td class="tdwhiteL">
						<input name="matnr" id="matnr" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
				</tr>
				<tr>
					<td class="tdblueM">${f:getMessage("자재명")}</td>
					<td class="tdwhiteL">
						<input name="maktx" id="maktx" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
					<td class="tdblueM">${f:getMessage("변경")}&nbsp;${f:getMessage("번호")}</td>
					<td class="tdwhiteL">
						<input name="aennr" id="aennr" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
				</tr>
				<tr>
					<td class="tdblueM">${f:getMessage("인터페이스상태")}</td>
					<td class="tdwhiteL">
						<input type="radio"name="zifsta" id="zifsta" value=""/>ALL,<input type="radio" name="zifsta" id="zifsta" value="S"/>${f:getMessage("성공")},<input type="radio" name="zifsta" id="zifsta" value="F"/>실패
					</td>
					<td class="tdblueM">${f:getMessage("전송일")}</td>
					<td class="tdwhiteL">
						<input name="preCreateDate" id="preCreateDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="preCreateDateBtn" id="preCreateDateBtn" ></a>
						<a href="JavaScript:clearText('preCreateDate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
						~
						<input name="postCreateDate" id="postCreateDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postCreateDateBtn" id="postCreateDateBtn" ></a>
						<a href="JavaScript:clearText('postCreateDate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
					</td>
				</tr>
				<tr>
					<td class="tdblueM">ERP ${f:getMessage("적용 상태")}</td>
					<td class="tdwhiteL">
						<input type="radio"name="returnZifsta" id="returnZifsta" value=""/>ALL,<input type="radio" name="returnZifsta" id="returnZifsta" value="S"/>${f:getMessage("성공")},<input type="radio" name="returnZifsta" id="returnZifsta" value="E"/>실패
					</td>
					<td class="tdblueM">${f:getMessage("ERP 적용 체크")}</td>
					<td class="tdwhiteL">
						<input name="startZifno" id="startZifno" class="txt_field" size="30" style="width:30%" value=""/>
						~
						<input name="endZifno" id="endZifno" class="txt_field" size="30" style="width:30%" value=""/>
					</td>
				</tr>
				
			</table>
		
			<table border="0" cellpadding="0" cellspacing="4" align="right">
                <tr>
                     <td>
	                  	<button type="button" class="btnCRUD" title="${f:getMessage('검색')}" id="ERPCheckBtn" name="ERPCheckBtn">
		                  	<span></span>
		                  	${f:getMessage("ERP 적용 현황 체크")}
	                  	</button>   
	                  </td>
				     <td>
	                  	<button type="button" class="btnSearch" title="${f:getMessage('검색')}" id="searchBtn" name="searchBtn">
		                  	<span></span>
		                  	${f:getMessage("검색")}
	                  	</button>   
	                  </td>
	                  
	                  <td>
	                 	 <button title="Reset" class="btnCustom" type="reset" id="btnReset">${f:getMessage("초기화")}</button>
	                  </td>
	                  <!--  
	                  <td>
		                  <a href="javascript:onExcelDown();">
						  	<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="middle">
					      </a>
				      </td>
				      -->
                </tr>
            </table>
            
		</td>
	</tr>
	<tr>
		<td>
			<div id="listGridBox" style="width:100%;" >111</div>
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="2" cellspacing="1" align="center" align="center">
				<tr height="35">
					<td>
						<div id="pagingBox"></div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
		
</form>
</body>
</html>