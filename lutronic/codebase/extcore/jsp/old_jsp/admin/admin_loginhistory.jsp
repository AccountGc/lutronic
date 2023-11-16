<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	lfn_DhtmlxGridInit();
	lfn_Search();
})

$(function() {
	$("#searchBtn").click(function() {
		$("#sessionId").val("");
		$("#page").val("1");
		lfn_Search();
	})
})

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '${f:getMessage("이름")}'
			,col02 : '${f:getMessage("아이디")}'
			,col03 : '${f:getMessage("접속시간")}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";

	var sWidth = "";
	sWidth += "30";
	sWidth += ",30";
	sWidth += ",40";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",left";
	
	var sColType = "";
	sColType += "ro";
	sColType += ",ro";
	sColType += ",ro";

	var sColSorting = "";
	sColSorting += "na";
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
	
	documentListGrid.init();
}

function lfn_Search() {
	var form = $("form[name=admin_loginHistory]").serialize();
	var url	= getURLString("admin", "admin_loginHistoryAction", "do");
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

function onExcelDown() {
	$("#admin_loginHistory").attr("method", "post");
	$("#admin_loginHistory").attr("action", getURLString("excelDown", "loginHistoryxcelDown", "do")).submit();
}

</script>

<body>

<form name="admin_loginHistory" id="admin_loginHistory" >

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!--//여백 테이블-->
<table width="100%" border="0" cellpadding="0" cellspacing="20" > 
    <tr align=center height=5>       
        <td>
            <table width="100%" border="0" cellpadding="10" cellspacing="3" >
                <tr  align=center height=5>
                    <td valign="top" style="padding:0px 0px 0px 0px" height=3 >

						<!-- 상단 제목 -->
						<div style="overflow: auto;">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td align="left" class="Sub_TBG" >
										<div id="mainTitle">${f:getMessage('로그인 이력관리')}</div>
									</td>
									<td class="Sub_TRight"></td>
								</tr>
							</table>
						</div>
                     
	                 </td>
	             </tr>
             
	             <tr align=center height=5>
	                 <td valign="top" style="padding:0px 0px 0px 0px" height=3 >
	
						<!-- 파란 라인 -->
	                 	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
							<tr><td height=1 width=100%></td></tr>
						</table>
	                 	
	                 	<!-- input 테이블 -->
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
							<col width='10%'><col width='40%'><col width='10%'><col width='40%'>
						    <tr bgcolor="ffffff" height="35">
						        <td class="tdblueM">${f:getMessage('이름')}</td>  
						                                               
						        <td class="tdwhiteL">
						        	<input type=text name=userName value="">
								</td>
								
								<td class="tdblueM">${f:getMessage('아이디')}</td>
								
								<td class="tdwhiteL">	 
									<input type=text name=userId value="">
								</td>
	           				</tr>
	        			</table>
	        
	   				 </td>
				</tr>
				
				<tr  align=center height=5>
				    <td valign="top" style="padding:0px 0px 0px 0px" height=3 >
	
						<!-- 등록과 초기화 버튼 -->
		     			<table border="0" cellpadding="0" cellspacing="4" align="right">
							<tr>
	    						<td>
	    							<button type="button" id="searchBtn" class="btnSearch">
	    								<span></span>
	    								${f:getMessage('검색')}
	    							</button>
	    						</td>
	    						
								<td>
									<button title="Reset" class="btnCustom" type="reset" id="btnReset">
				                 	 	<span></span>${f:getMessage('초기화')}
				                 	 </button>
								</td>
								<td>
	    							<a href="javascript:onExcelDown();">
					  					<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="middle">
				    				</a>
				    			</td>
	          					
	      					</tr>
						</table>
	               
	           		</td>
	       		</tr>
	       		
	       		<tr>
					<td valign="top" style="padding:0px 0px 0px 0px" height=3 >
						<div id="listGridBox" style="width:100%;" ></div>
					</td>
				</tr>
				
				<tr>
					<td valign="top" style="padding:0px 0px 0px 0px" height=3 >
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