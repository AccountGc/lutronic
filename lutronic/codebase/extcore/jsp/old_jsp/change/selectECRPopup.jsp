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
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	gfn_InitCalendar("preCreateDate", "preCreateDateBtn");
	gfn_InitCalendar("postCreateDate", "postCreateDateBtn");
	gfn_InitCalendar("preApproveDate", "preApproveDateBtn");
	gfn_InitCalendar("postApproveDate", "postApproveDateBtn");
	numberCodeList('CHANGESECTION',"");
	lfn_getStateList('LC_Default');
	lfn_Search();
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
	var id = "changeSection";
	
	for(var i=0; i<data.length; i++) {
		
		$("#"+ id).append("<input type='checkbox' name="+id+" id="+id+" value='" + data[i].code + "'>"+ data[i].name);
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
	*                      초기화 버튼
	----------------------------------------------------------%>
	$("#btnReset").click(function() {
		$("#creator").val("");
	})
	
	$("#detailSearch").click(function() {
		if($('#SearchDetailECR').css('display') == 'none'){ 
			$("#detailText").html("${f:getMessage('상세검색')} -");
		   	$('#SearchDetailECR').show(); 
		} else { 
			$("#detailText").html("${f:getMessage('상세검색')} +");
		   	$('#SearchDetailECR').hide(); 
		}
	})
	
	<%----------------------------------------------------------
	*                      선택 버튼 클릭시
	----------------------------------------------------------%>
	$("#selectBtn").click(function () {
		var checked = documentListGrid.getCheckedRows(0);
		
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
		}
		
		var array = checked.split(",");
		var returnArr = new Array();
		
		for(var i =0; i < array.length; i++) {
			var oid = array[i];															 // OID
			
			var number = documentListGrid.cells(array[i], 2).getValue();				 // ECR 번호
   			var name = documentListGrid.cells(array[i], 3).getValue();				 // ECR 제목
   			var state = documentListGrid.cells(array[i], 8).getValue();				 // ECR 상태
   			var creator = documentListGrid.cells(array[i], 9).getValue();				 // ECR 작성자
   			var createDate = documentListGrid.cells(array[i], 10).getValue();				 // ECR 작성일
  			returnArr[i] = new Array();
   			returnArr[i][0] = oid;
   			returnArr[i][1] = number;
   			returnArr[i][2] = name;
   			returnArr[i][3] = state;
   			returnArr[i][4] = creator;
   			returnArr[i][5] = createDate;
   			
   			
		}
		opener.parent.addECR(returnArr);
		//self.close();
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
	})
	$(document).keypress(function(event) {
		 if(event.which == 13 && $( '#userNameSearch' ).is( ':hidden' )) {
			 resetSearch();
		 }
	})
})

window.resetSearch = function() {
	lfn_DhtmlxGridInit();
	$("#sortValue").val("");
	$("#sortCheck").val("");
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}

<%----------------------------------------------------------
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=changeRequestForm]").serialize();
	var url	= getURLString("changeECR", "listECRAction", "do") + "?select=true";
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
*                      CheckBox를 비활성화 하기 위한 함수
----------------------------------------------------------%>
function IsCellDisable(){
	var rows = documentListGrid.getRowsNum();
	for(var i = 0; i < rows; i++) {
		var rowId = documentListGrid.getRowId(i);
		if("false" == documentListGrid.cells(rowId,13).getValue()) {
			documentListGrid.cellById(rowId, 0).setDisabled(true);
		}
	}
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
			$("#state").append("<option value=''>선택</option>");
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					$("#state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
				}
			}
			
		}
	});
}

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
function numberCodeList(type) {
	var form = $("form[name=changeRequestForm]").serialize();
	var url	= getURLString("common", "numberCodeList", "do") + "?type="+type;
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = type + " ${f:getMessage('코드 목록 오류')}";
			alert(msg);
		},

		success:function(data){
			addSelectList(type,data);
		}
	});
}

$(document).on('click', 'input:checkbox[name="allCheck"]', function(){
	if(this.checked) {
		this.checked = true;
		documentListGrid.setColLabel(0, "<input type=checkbox name=allCheck id=allCheck checked>");
		var rows = documentListGrid.getRowsNum();
		for(var i = 0; i < rows; i++) {
			var rowId = documentListGrid.getRowId(i);
			documentListGrid.cellById(rowId, 0).setChecked(true);
		}
	}else {
		documentListGrid.setColLabel(0, "<input type=checkbox name=allCheck id=allCheck>");
		var rows = documentListGrid.getRowsNum();
		for(var i = 0; i < rows; i++) {
			var rowId = documentListGrid.getRowId(i);
			documentListGrid.cellById(rowId, 0).setChecked(false);
		}
	}
})


<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	var COLNAMEARR = {
			 col00 :  '<input type=checkbox name=allCheck id=allCheck>'
			,col01 : '${f:getMessage("번호")}'
			,col02 : 'CR/ECPR${f:getMessage("번호")}'
			,col03 : 'CR/ECPR${f:getMessage("제목")}'
			,col04 : '${f:getMessage("변경구분")}'
			,col05 : '${f:getMessage("작성자")}'//
			,col06 : '${f:getMessage("작성부서")}'
			,col07 : '${f:getMessage("작성일")}'//
			,col08 : '${f:getMessage("상태")}'
			,col09 : '${f:getMessage("등록자")}'
			,col10 : '${f:getMessage("등록일")}'
			,col11 : ''
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col00;
	sHeader += "," + COLNAMEARR.col01;
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
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";
	sHeaderAlign[6]  = "text-align:center;";
	sHeaderAlign[7]  = "text-align:center;";
	sHeaderAlign[8]  = "text-align:center;";
	sHeaderAlign[9]  = "text-align:center;";
	sHeaderAlign[10]  = "text-align:center;";
	sHeaderAlign[11]  = "text-align:center;";

	var sWidth = "";
	sWidth += "3";
	sWidth += ",3";
	sWidth += ",15";
	sWidth += ",19";
	sWidth += ",12"; //변경구분 15+3
	sWidth += ",8";
	sWidth += ",8";
	sWidth += ",8";
	sWidth += ",8";//상태
	sWidth += ",8";
	sWidth += ",8";
	sWidth += ",0";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",center"; //변경구분
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center"; //상태
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	
	var sColType = "";
	sColType += "ch";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";//변경구분
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";//상태
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";

	var sColSorting = "";
	sColSorting += "na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";//변경구분
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";//상태
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
    documentListGrid.setColumnHidden(11,true);
	documentListGrid.attachEvent("onHeaderClick",function(rowId,cellIndex){
		
		var sortValue = "";
		var header = "";
		
		if(rowId == 1) {
			sortValue = "eoNumber";
			header = COLNAMEARR.col02;
		}else if(rowId == 2 ){
			sortValue = "eoName";
			header = COLNAMEARR.col03;
		}else if(rowId == 8 ) {
			sortValue = "creator";
			header = COLNAMEARR.col09;
		}else if(rowId == 9) {
			sortValue = "thePersistInfo.createStamp";
			header = COLNAMEARR.col10;
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
			documentListGrid.setColLabel(5, COLNAMEARR.col09);
			documentListGrid.setColLabel(6, COLNAMEARR.col10);
			documentListGrid.setColLabel(rowId, header);
			
			$("#sortValue").val(sortValue);
			$("#sessionId").val("");
			$("#page").val(1);
			lfn_Search();
		}
	});
	
	documentListGrid.init();
}


</script>
<body>

<form name="changeRequestForm" name="changeRequestForm" method=post style="padding:0px;margin:0px">

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

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr  align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
				<tr><td height=1 width=100%></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			<col width='10%'><col width='40%'><col width='10%'><col width='40%'>
				<tr>
					<td class="tdblueM">CR/ECPR ${f:getMessage("제목")}</td>
					<td class="tdwhiteL">
						<input name="name" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
					<td class="tdblueM">CR/ECPR ${f:getMessage("번호")}</td>
					<td class="tdwhiteL">
						<input name="number" class="txt_field" size="30" style="width:90%" value=""/>
					</td>
				</tr>
				<tr>
					<td class="tdblueM">
						${f:getMessage("등록일자")}
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
					<td class="tdblueM">${f:getMessage("등록자")}</td>
	                <td class="tdwhiteL">
	                	<%-- 
	                   	<input type="hidden" id="creator" name="creator" value="" />
						<input type="text" id="creatorName" name="creatorName" size="30" style="width:30%" value="" class="txt_field" readOnly/>
						
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
						</jsp:include>
					</td>
				</tr>
				<tr>
                    <td class="tdblueM">
                    	${f:getMessage("상태")}
                    </td>
                    <td class="tdwhiteL" colspan="3">
	                    <select name="state" id="state">
 							<option value=''>${f:getMessage("선택")}</option>
						</select>
                    </td>
					
                </tr>
               </table>
                <!-- 상세 검색 start -->
                <div id="SearchDetailECR" style="display: none;" >
					<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
		                <colgroup>
						<col width='10%'>
						<col width='40%'>
						<col width='10%'>
						<col width='40%'>
						</colgroup>
		                <tr bgcolor="ffffff" height="35">
							<td class="tdblueM">${f:getMessage('작성일')}</td>
							<td class="tdwhiteL">
								<input name="preCreateDate" id="preCreateDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
								<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="preCreateDateBtn" id="preCreateDateBtn" ></a>
								<a href="JavaScript:clearText('preCreateDate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
								~
								<input name="postCreateDate" id="postCreateDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
								<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postCreateDateBtn" id="postCreateDateBtn" ></a>
								<a href="JavaScript:clearText('postCreateDate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
							</td>
							<td class="tdblueM">${f:getMessage('승인일')}</td>
							<td class="tdwhiteL">
								<input name="preApproveDate" id="preApproveDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
								<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="preApproveDateBtn" id="preApproveDateBtn" ></a>
								<a href="JavaScript:clearText('approveDate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>	
								~
								<input name="postApproveDate" id="postApproveDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
								<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postApproveDateBtn" id="postApproveDateBtn" ></a>
								<a href="JavaScript:clearText('approveDate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>	
							</td>
						</tr>
						<tr bgcolor="ffffff" height="35">
							<td class="tdblueM">${f:getMessage('작성부서')}</td>
							<td class="tdwhiteL" >
								<input name="createDepart" id="createDepart" class="txt_field" size="85" style="width:98%"/>
							</td>
							<td class="tdblueM">${f:getMessage('작성자')}</td>
							<td class="tdwhiteL" >
								<input name="writer" id="writer" class="txt_field" size="85" style="width:98%"/>
							</td>
						</tr>              
		                <tr bgcolor="ffffff" height="35">
							<td class="tdblueM">제품명</td>
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
							<td class="tdblueM">${f:getMessage('제안자')}</td>
							<td class="tdwhiteL" >
								<input name="proposer" id="proposer" class="txt_field" style="width:98%"/>
							</td>
							<td class="tdblueM">${f:getMessage('변경구분')}</td>
							<td class="tdwhiteL" >
								<div id='changeSection'></div>	
							</td>
						</tr>
					</table>
				</div>
			 <!-- 상세 검색 end -->
			
		</td>
	</tr>
	
	<tr height=35>
		<td align="right">
			<table border="0" cellpadding="0" cellspacing="4" align="right">
                <tr>
	                  
	            	<td>
		            	<button type="button" class="btnCRUD" title="${f:getMessage('선택')}" id="selectBtn" name="selectBtn">
		                	<span></span>
		                  	${f:getMessage('선택')}
	                	</button>
					</td>
					
					<td>
	                	<button type="button" class="btnSearch" title="검색" id="searchBtn" name="searchBtn">
		                  	<span></span>
		                  	${f:getMessage('검색')}
	                	</button>   
	             	</td>
	                  
	            	<td>
	                	<button title="Reset" class="btnCustom" type="reset" id="btnReset">
	                 	 	<span></span>
	                 	 	${f:getMessage('초기화')}
	                	</button>
	            	</td>
	                  
	            	<td>
	                	<button title="Reset" class="btnCustom" type="button" id="detailSearch">
	                 		<span></span>
	                 	 	${f:getMessage('상세검색')}
	                	</button>
	            	</td>
				      
	                <td>
		            	<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
	               			<span></span>
	               			${f:getMessage('닫기')}
	               		</button>
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