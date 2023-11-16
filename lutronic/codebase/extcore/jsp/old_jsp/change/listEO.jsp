<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="/Windchill/jsp/js/eo.js" ></script>
<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	lfn_DhtmlxGridInit();
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	gfn_InitCalendar("preApproveDate", "preApproveDateBtn");
	gfn_InitCalendar("postApproveDate", "postApproveDateBtn");
	lfn_getStateList('LC_ECO');
	lfn_Search();
})

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
	lfn_DhtmlxGridInit();
	$("#sortValue").val("");
	$("#sortCheck").val("");
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}

<%----------------------------------------------------------
*                      Lifecycle 상태 가져오기
----------------------------------------------------------%>
function lfn_getStateList(lifecycle) {
	var form = $("form[name=changeOrderForm]").serialize();
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
	var form = $("form[name=changeOrderForm]").serialize();
	var url	= getURLString("changeECO", "listEOAction", "do");
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
			 col01 : '${f:getMessage("번호")}'
			,col02 : 'EO${f:getMessage("번호")}'
			,col03 : 'EO${f:getMessage("제목")}'
			,col04 : 'EO${f:getMessage("구분")}'
			,col05 : '${f:getMessage("상태")}'
			,col06 : '${f:getMessage("등록자")}'
			,col07 : '${f:getMessage("승인일")}'
			,col08 : '${f:getMessage("등록일")}'
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
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";
	sHeaderAlign[6]  = "text-align:center;";
	sHeaderAlign[7]  = "text-align:center;";

	var sWidth = "";
	sWidth += "5";
	sWidth += ",20";
	sWidth += ",25";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",left";
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
	sColType += ",ro";
	sColType += ",ro";

	var sColSorting = "";
	sColSorting += "na";
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
			sortValue = "eoNumber";
			header = COLNAMEARR.col02;
		}else if(rowId == 2 ){
			sortValue = "eoName";
			header = COLNAMEARR.col03;
		}else if(rowId == 5 ) {
			sortValue = "creator.key.id";
			header = COLNAMEARR.col06;
		}else if(rowId == 6) {
			sortValue = "thePersistInfo.createStamp";
			header = COLNAMEARR.col07;
		}else if(rowId == 7) {
			sortValue = "eoApproveDate";
			header = COLNAMEARR.col08;
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
			documentListGrid.setColLabel(5, COLNAMEARR.col06);
			documentListGrid.setColLabel(6, COLNAMEARR.col07);
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

function onExcelDown() {
	$("#changeOrderForm").attr("method", "post");
	$("#changeOrderForm").attr("action", getURLString("excelDown", "EOExcelDown", "do")).submit();
}

</script>

<body>
<form name="changeOrderForm" id="changeOrderForm" method=post style="padding:0px;margin:0px">
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
							&nbsp; EO ${f:getMessage("관리")} > EO ${f:getMessage("검색")}
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
						EO${f:getMessage("번호")}
					</td>
					
					<td class="tdwhiteL">
						<input name="number" id="number" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
					<td class="tdblueM">
						EO${f:getMessage("제목")}
					</td>
					
					<td class="tdwhiteL">
						<input name="name" id="name" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
					
				</tr>
				
				<tr>
					<td class="tdblueM">${f:getMessage("등록자")}</td>
	                <td class="tdwhiteL">
	                	<%-- 
	                   	<input type="hidden" id="creator" name="creator" value="" />
						<input type="text" class="txt_field" id="tempcreator" name="tempcreator" size="30" style="width:30%" value="" readOnly/>
						
						<a href="JavaScript:searchUser('changeOrderForm','single','creator','tempcreator','people')">
							<img src="/Windchill/jsp/portal/images/s_search.gif" border="0" />
						</a>
						
						<a href="JavaScript:clearText('creator');clearText('creatorName');">
							<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
						</a>
						--%>
						
						<jsp:include page="/eSolution/common/userSearchForm.do">
							<jsp:param value="single" name="searchMode"/>
							<jsp:param value="creator" name="hiddenParam"/>
							<jsp:param value="tempcreator" name="textParam"/>
							<jsp:param value="people" name="userType"/>
						</jsp:include>
					</td>
					
					<td class="tdblueM" >
						${f:getMessage("상태")}
					</td>
					
                    <td class="tdwhiteL" >
	                    <select name="state" id="state" >
 							<option value=''>${f:getMessage("선택")}</option>
						</select>
                    </td>
				</tr>
				
				<tr>
                    <td class="tdblueM">
						${f:getMessage("등록일")}
					</td>
					
					<td class="tdwhiteL">
						<input name="predate" id="predate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="predateBtn" id="predateBtn" ></a>
						<a href="JavaScript:clearText('predate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
						~
						<input name="postdate" id="postdate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postdateBtn" id="postdateBtn" ></a>
						<a href="JavaScript:clearText('postdate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
					</td>
					
					<td class="tdblueM">
						EO${f:getMessage("구분")}
					</td>
					
					<td class="tdwhiteL">
						<input type="radio" name=eoType value="">${f:getMessage("없음")}
						<input type="radio" name=eoType value="DEV">${f:getMessage("개발")}
						<input type="radio" name=eoType value="PRODUCT">${f:getMessage("양산")}
					</td>
					
                </tr>
                <tr>
                	<td class="tdblueM">
						${f:getMessage("승인일")}
					</td>
                	<td class="tdwhiteL" colspan="3">
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
                    <td><script>setButtonTag3D("초기화","60","document.changeOrderForm.reset(), clearText2();","");</script></td> -->
				    <td>
	                  	<button type="button" class="btnSearch" title="${f:getMessage('검색')}" id="searchBtn" name="searchBtn">
		                  	<span></span>
		                  	${f:getMessage("검색")}
	                  	</button>   
	                  </td>
	                  
	                  <td>
	                 	 <button title="Reset" class="btnCustom" type="reset" id="btnReset">${f:getMessage("초기화")}</button>
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
		<td>
			<div id="listGridBox" style="width:100%;" ></div>
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