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
<script type="text/javascript">
$(document).ready(function() {
	lfn_DhtmlxGridInit();
	
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
	gfn_InitCalendar("predate_modify", "predate_modifyBtn");
	gfn_InitCalendar("postdate_modify", "postdate_modifyBtn");
	lfn_getStateList();
	
	numberCodeList('model', '');
	numberCodeList('deptcode', '');
	numberCodeList('preseration', '');
	documentType('GENERAL', '');
	
	
	lfn_Search();
})

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '<input type=checkbox name=allCheck id=allCheck>'
			,col02 : '${f:getMessage('문서')}${f:getMessage('번호')}'
			,col03 : '${f:getMessage('내부문서')}${f:getMessage('번호')}'
			,col04 : '${f:getMessage('프로젝트 코드')}'
			,col05 : '${f:getMessage('문서명')}'
			,col06 : '${f:getMessage('상태')}'
			,col07 : '${f:getMessage('Rev.')}'
			,col08 : '${f:getMessage('등록자')}'
			,col09 : '${f:getMessage('등록일자')}'
			//,col08 : '${f:getMessage('파일')}'
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
	//sHeader += "," + COLNAMEARR.col08;
	
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
	//sHeaderAlign[7]  = "text-align:center;";

	var sWidth = "";
	sWidth += "3";
	sWidth += ",20";
	sWidth += ",20";
	sWidth += ",15";
	sWidth += ",37";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	//sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	//sColAlign += ",center";
	
	var sColType = "";
	sColType += "ch";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	//sColType += ",ro";

	var sColSorting = "";
	sColSorting += "na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	//sColSorting += ",na";
	
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

<%----------------------------------------------------------
*                      데이터 검색 AJAX
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=createDocumentLink]").serialize();
	var url	= getURLString("doc", "createDocumentLinkAction", "do");
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

$(function() {
	$("#search").click(function() {
		resetSearch();
	})
	$("#select").click(function() {
		var checked = documentListGrid.getCheckedRows(0);
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
			return;
		}
		
		var array = checked.split(",");
		var returnArr = new Array();
		
		for(var i =0; i < array.length; i++) {
			var docOid = array[i];		
			var docNumber = documentListGrid.cells(array[i], 1).getValue();
			if(DuplicateCheck(docNumber)) {
				linkDocument(docOid,"<c:out value='${parentOid}'/>");
			}else {
				alert(docNumber + "${f:getMessage('가 중복 되었습니다.')}");
			}
		}
		if($('#type').val() == 'active'){
			$(opener.location).attr('href', 'javascript:pageReload()');
		}
		/*
		var docNumber = documentListGrid.cells(docOid, 1).getValue();
		
		var obj = $('input[name=docNumber]', opener.document);
		for(var i=0; i<obj.length; i++) {
			if(docNumber == obj.eq(i).val()) {
				alert(docNumber + "${f:getMessage('가 중복 되었습니다.')}");
				return;
			}
		}
		linkDocument(docOid,"<c:out value='${parentOid}'/>");
		*/
	})
	$(document).keypress(function(event) {
		 if(event.which == 13) {
			 resetSearch();
		 }
	})
	$("#detailBtn").click(function () {
		if($('#SearchDetailProject').css('display') == 'none'){ 
			$("#detailText").html("${f:getMessage('상세검색')} -");
		   	$('#SearchDetailProject').show(); 
		} else { 
			$("#detailText").html("${f:getMessage('상세검색')} +");
		   	$('#SearchDetailProject').hide(); 
		}
	})
})

window.resetSearch = function() {
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}

window.DuplicateCheck = function(docNumber) {
	var obj = $('input[name=docNumber]', opener.document);
	for(var i=0; i<obj.length; i++) {
		if(docNumber == obj.eq(i).val()) {
			return false;
		}
	}
	return true;
}

window.linkDocument = function(docOid, parentOid) {
	var url	= getURLString('doc', 'linkDocumentAction', "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		data: {
			docOid : docOid,
			parentOid : parentOid,
			type : $('#type').val()
		},
		success:function(data){
			if(data.result) {
				if($('#type').val() == 'active'){
					
				}else{
					$(opener.location).attr('href', 'javascript:docLinkReload()');
				}
			}else {
				alert(data.message);
			}
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
window.numberCodeList = function(id) {
	var data = common_numberCodeList(id.toUpperCase(), '', true);
	
	addSelectList(id, eval(data.responseText));
}
window.documentType = function(documentType, searchType) {
	var data = common_documentType(documentType, '');
	
	addSelectList('documentType', eval(data.responseText));
}
window.addSelectList = function(id,data){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "'>";
			
			if(id != 'documentType') {
				html += " [" + data[i].code + "] ";
			} 
			html += data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}
function lfn_getStateList() {
	var form = $("form[name=documentForm]").serialize();
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
</script>

<body>

<form name="createDocumentLink" id="createDocumentLink">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<input type="hidden" name="parentOid"    id="parentOid"     value="<c:out value='${parentOid}'/>" />
<input type="hidden" name="type" id="type" value="<c:out value="${type }"/>">

<table id="table1" width="100%" bgcolor="#ffffff" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td class="pL4"><!-- Top P/T & Nav section Start -->
			<table width="100%" height="24" cellpadding="0" cellspacing="0"
				border="0" style="border-bottom:1 solid #CECFCE">
				<tr>
					<td width="20%" valign="bottom"><img
						src="/Windchill/jsp/portal/images/icon/dot_icon03.gif" width="10" height="9" border="0"
						align="absmiddle">&nbsp;<b>${f:getMessage('문서')}${f:getMessage('선택')}</b></td>
					<td width="80%" align="right" class="Nav"></td>
				</tr>
			</table>
		<!-- Top P/T & Nav section End -->
		</td>
	</tr>
	
	<tr>
		<td><!-- Top Line Table Start -->
			<table width="100%" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td colspan="6" height="3" bgcolor="#E7E7E7"></td>
				</tr>
				<tr>
					<td colspan="6" height="1" bgcolor="#636163"></td>
				</tr>
				<tr>
					<td colspan="6" height="3"></td>
				</tr>
			</table>
		<!-- Top Line Table End -->
		</td>
	</tr>
	
	<tr>
		<td>
			<table width="100%" cellpadding="0" cellspacing="0"
				style="color:#4A494A; LEFT: 0px;TOP: 0px;table-layout:fixed;">
				<tr align=center>
					<td valign="top" style="padding:0px 0px 0px 0px">
						<table width="100%" border="0" cellpadding="0" cellspacing="0"
							align=center>
							<col width='10%'>
							<col width='40%'>
							<col width='10%'>
							<col width='40%'>
							<tr>
								<td class="tdblueM"> 
									${f:getMessage('문서분류')}
								</td>
								
								<td class="tdwhiteL">
									<jsp:include page="/eSolution/folder/include_FolderSelect.do">
										<jsp:param value="/Default/Document" name="root"/>
										<jsp:param value="${folder }" name="folder"/>
									</jsp:include>
								</td>
								
								<td class="tdblueM"> ${f:getMessage('Rev.')}</td>
								
								<td class="tdwhiteL">
									<input name="islastversion" type="radio" class="Checkbox" value="true" checked="checked" >
									${f:getMessage('최신Rev.')}
									<input name="islastversion" type="radio" class="Checkbox" value="false" >
									${f:getMessage('모든Rev.')}
								</td>
							</tr>
							<tr>
								<td class="tdblueM">
									${f:getMessage('문서')}${f:getMessage('번호')} 
								</td>
								
								<td class="tdwhiteL">
									<input name="docNumber" id="docNumber" class="input100" style="width:90%" value="" />
								</td>
								<td class="tdblueM">
									${f:getMessage('문서명')}
								</td>
								
								<td class="tdwhiteL" >
									<input name="docName" id="docName" class="input100" style="width:90%" value="" />
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('등록일')}
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
									${f:getMessage('수정일')}
								</td>
								
								<td class="tdwhiteL">
									<input name="predate_modify" id="predate_modify" class="txt_field" size="12"  maxlength=15 readonly  value=""/>
									<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="predate_modifyBtn" id="predate_modifyBtn" ></a>
									<a href="JavaScript:clearText('predate_modify')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
									~
									<input name="postdate_modify" id="postdate_modify" class="txt_field" size="12"  maxlength=15 readonly  value=""/>
									<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="postdate_modifyBtn" id="postdate_modifyBtn" ></a>
									<a href="JavaScript:clearText('postdate_modify')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('등록자')}
								</td>
								
								<td class="tdwhiteL">
								
									<%-- 
									<input TYPE="hidden" name="creator" id="creator" value="">
									<input type="text" name="creatorName" id="creatorName" value="" class="txt_field" readonly>
			
									<a href="JavaScript:searchUser('documentForm','single','creator','creatorName','people')">
										<img src="/Windchill/jsp/portal/images/s_search.gif" border=0>
									</a>
									
									<a href="JavaScript:clearText('creator');clearText('creatorName')">
										<img src="/Windchill/jsp/portal/images/x.gif" border=0>
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
								
								<td class="tdblueM">
									${f:getMessage('상태')}
								</td>
								
								<td class="tdwhiteL" >
				                    <select name="state" id="state">
				                    </select>
			                    </td>
							</tr>
							
						</table>
						
						<div id="SearchDetailProject" style="display: none;" >
							<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
								<colgroup>
									<col width='10%'>
									<col width='40%'>
									<col width='10%'>
									<col width='40%'>
								</colgroup>
								
								<tr bgcolor="ffffff" height="35">
								
									<td class="tdblueM">
										${f:getMessage('문서유형')}
									</td>
									
									<td class="tdwhiteL" >
										<select id='documentType' name='documentType' style="width: 95%">
										</select>
									</td>
									
									<td class="tdblueM">
									${f:getMessage('보존기간')}
									</td>
									
									<td class="tdwhiteL">
										<select id='preseration' name='preseration' style="width: 95%">
										</select>
									</td>
									
								</tr>
								
								<tr bgcolor="ffffff" height="35">
									<td class="tdblueM">
										${f:getMessage('프로젝트코드')}
									</td>
									
									<td class="tdwhiteL" >
										<select id='model' name='model' style="width: 95%">
										</select>
									</td>
									
									<td class="tdblueM">
										${f:getMessage('부서')}
									</td>
									
									<td class="tdwhiteL">
										<select id='deptcode' name='deptcode' style="width: 95%">
										</select>
									</td>
								</tr>
								
								<tr bgcolor="ffffff" height="35">
									<td class="tdblueM">
										${f:getMessage('내부 문서번호')}
									</td>
									
									<td class="tdwhiteL">
										<input name="interalnumber" id="interalnumber" class="txt_field" size="85" style="width:90%" maxlength="80" />
									</td>
									
									<td class="tdblueM">
										${f:getMessage('작성자')}
									</td>
									
									<td class="tdwhiteL">
										<input name="writer" id="writer" class="txt_field" size="85" style="width:90%" maxlength="80" />
									</td>
								</tr>
								
								<tr bgcolor="ffffff" height="35">
									<td class="tdblueM">
										${f:getMessage('내용')}
									</td>
									
									<td class="tdwhiteL" colspan="4">
										<textarea name="description" id="description" cols="10" rows="5" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('문서')}${f:getMessage('설명')}')"></textarea>
									</td>
								</tr>
							
						</table>
					</td>
				</tr>
				
				<tr>
					<td>
						<table width="100%" cellpadding="0" cellspacing="0" border="0">
							<tr>
								<td><!-- Bottom Line Table Start -->
									<table width="100%" cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td colspan="6" height="3"></td>
										</tr>
										
										<tr>
											<td colspan="6" height="1" bgcolor="#636163"></td>
										</tr>
										
										<tr>
											<td colspan="6" height="3" bgcolor="#EFEFEF"></td>
										</tr>
									</table>
								<!-- Bottom Line Table End -->
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
											
				<tr>
					<td valign="top" ><!-- Sub Title 01 & Button Start -->
						<table width="100%" height="26" cellpadding="0" cellspacing="0"
							border="0">
							<tr>
								<td width="20%" class=""></td>
								<td width="80%" align="Right" style="padding-right:8">
									<table border="0" cellpadding="0" cellspacing="4" align="right">
								        <tr>
									        
									        <td>
									        	<button type="button" class="btnCRUD" id="select">
									        		<span></span>
									        		${f:getMessage('선택')}
									        	</button>
									        </td>
									        
									        <td>
									        	<button type="button" class="btnSearch" id="search">
									        		<span></span>
									        		${f:getMessage('검색')}
									        	</button>
									        </td>
											<td>
												<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('상세검색')}" id="detailBtn" name="detailBtn">
													<span></span>
													<font id="detailText" >${f:getMessage('상세검색')} + </font>
												</button>   
											</td>
									        
											<td>
												<button type="button" class="btnClose" onclick="self.close();">
													<span></span>
													${f:getMessage('닫기')}
												</button>
											</td>
											
										</tr>
									</table>	
								</td>
							</tr>
						</table>
					<!-- Sub Title 01 & Button End -->
					</td>
				</tr>
				
					
				<tr>
					<td height="20px"></td>
				</tr>
				
				<tr>
					<td>
						<div id="listGridBox" style="width:100%;"></div>
					</td>
				</tr>
				
				<tr>
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