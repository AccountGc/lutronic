<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
var isSeq; 
var seqNm;
$(document).ready(function(){
	lfn_DhtmlxGridInit();
	lfn_Search();
	codeFildeInit();
	 
});

$(function() {
	$("#reset").click(function() {
		$("#create").show();
		$("#update").hide();
		//$("#delete").hide();
		if(!isSeq){
			$("#code").removeAttr("disabled");
		}
		
	})
	$("#create").click(function() {
		
		if($.trim($("#name").val()) == ""){
			alert("${f:getMessage('이름(국문)을 입력하세요.')}");
			return;
		}
		
		if( !isSeq && $.trim($("#code").val()) == ""){
			alert("${f:getMessage('코드를 입력하세요.')}");
			return;
		}
				
		if($.trim($("#sort").val()) == ""){
			alert("${f:getMessage('소트를 입력하세요.')}");
			return;
		}
		
		if(!confirm("${f:getMessage('등록하시겠습니까?')}")){
			return;
		}
		numberCodeAction("create");
	})
	$("#update").click(function() {
		if($.trim($("#name").val()) == ""){
			alert("${f:getMessage('이름(국문)을 입력하세요.')}");
			return;
		}
		/*
		if($.trim($("#code").val()) == ""){
			alert("${f:getMessage('코드를 입력하세요.')}");
			return;
		}
		*/		
		if($.trim($("#sort").val()) == ""){
			alert("${f:getMessage('소트를 입력하세요.')}");
			return;
		}
		
		if(!confirm("${f:getMessage('수정하시겠습니까?')}")){
			return;
		}
		numberCodeAction("update");
	})
	$("#delete").click(function() {
		if(!confirm("${f:getMessage('삭제하시겠습니까?')}")){
			return;
		}
		numberCodeAction("delete");
	})
})

function lfn_DhtmlxGridInit() {
	var COLNAMEARR = {
			 col01 : '${f:getMessage("이름(국문)")}'
			,col02 : '${f:getMessage("이름(영문)")}'
			,col03 : '${f:getMessage("코드")}'
			,col04 : '${f:getMessage("소트")}'
			,col05 : '${f:getMessage("설명")}'
			,col06 : '${f:getMessage("활성화")}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	sHeader += "," + COLNAMEARR.col05;
	sHeader += "," + COLNAMEARR.col06;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";

	var sWidth = "";
	sWidth += "15";
	sWidth += ",15";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",40";
	sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
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

	var sColSorting = "";
	sColSorting += "na";
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
	
	documentListGrid.attachEvent("onRowSelect", function(id,ind){
		
		$("#create").hide();
		$("#update").show();
		//$("#delete").show();
		
		var name = documentListGrid.cells(id,0).getValue();
		var engName = documentListGrid.cells(id,1).getValue();
		var code = documentListGrid.cells(id,2).getValue();
		var sort = documentListGrid.cells(id,3).getValue();
		var description = documentListGrid.cells(id,4).getValue();
		var enabled = documentListGrid.cells(id,5).getValue();
		
		$("#name").val(name);
		$("#engName").val(engName);
		$("#code").val(code);
		$("#code").attr("disabled","true");
		$("#sort").val(sort);
		$("#description").val(description);
		
		if(enabled == "true") {
			$('input:checkBox[name="enabled"]').prop("checked", true);
		}else {
			$('input:checkBox[name="enabled"]').prop("checked", false);
		}
		
		$("#oid").val(id);
	});
	
	documentListGrid.init();
}

function lfn_Search() {
	var form = $("form[name=admin_numberCodeList]").serialize();
	var url	= getURLString("admin", "admin_numberCodeAction", "do");
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

function numberCodeAction(command) {
	$("#command").val(command);
	var form = $("form[name=admin_numberCodeList]").serialize();
	var url	= getURLString("admin", "numberCodeAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			//alert(msg);
			location.reload();
		},

		success:function(data){
			alert(data);
			$("#reset").click();
			$("#page").val(1);
			$("#sessionId").val("");
			lfn_Search();
		}
	});
}

function codeFildeInit(){
	//getIsSeq();
	isSeq = getIsSeq();
	seqNm = getSeqNm();
	
	if(isSeq){
		$("#code").val(seqNm);
		$("#code").attr("disabled","true");
	}
	
}

function getIsSeq(){

	if($.trim($("#isSeq").val()) == "true"){
		return true;
	}else{
		return false;
	}
	
}	

function getSeqNm(){
	return $("#seqNm").val();
}

</script>

<form id="admin_numberCodeList" name="admin_numberCodeList">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<input type="hidden" name="codeType" id="codeType" value="<c:out value="${codeType}"/>" />
<input type="hidden" name="isSeq" id="isSeq" value="<c:out value="${isSeq}"/>" />
<input type="hidden" name="seqNm" id="seqNm" value="<c:out value="${seqNm}"/>" />
<input type="hidden" name="command" id="command" />
<input type="hidden" name="oid" id="oid" />
<input type="hidden" name="parentOid" id="parentOid" value="<c:out value="${parentOid}"/>"/>

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="20"  > <!--//여백 테이블-->

	<tr  align=center height=5>
    	<td valign="top" style="padding:0px 0px 0px 0px" height=3 >
     		<table width="100%" border="0" cellpadding="10" cellspacing="3" >
				<tr  align=center height=5>
   					<td valign="top" style="padding:0px 0px 0px 0px" height=3 >

						<!-- 상단 제목 부분 -->                        
						<div style="overflow: auto;">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td align="left" class="Sub_TBG" >
										<div id="mainTitle"> 
											[
												<c:out value="${title }" />
											]
										</div>
									</td>
									<td class="Sub_TRight"></td>								
								</tr>
							</table>
						</div>                        	
                   
					</td>
				</tr>
        	</table>
		</td>
	</tr>

	<tr align=center height=5>
		<td valign="top" style="padding:0px 0px 0px 0px" height=3 >
		
			<!-- 파란 라인 -->
        	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
                    	
                    	<!-- input 박스 -->
         	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" >
                <tr	bgcolor="ffffff" align=center>
                    <td class="tdblueM">${f:getMessage('이름(국문)')}</td>
                    <td class="tdwhiteL">
                    	<input type=text name=name id=name size=20>
                    </td>
                    <td class="tdblueM">${f:getMessage('이름(영문)')}</td>
                    <td class="tdwhiteL">
                    	<input type=text name=engName id=engName size=20>
                    </td>                                
                    <td class="tdblueM">${f:getMessage('코드')}</td>
                    <td class="tdwhiteL">
                    	<input type=text name=code id=code size=10>
                    </td>
                </tr>
                <tr>
                    <td class="tdblueM">${f:getMessage('소트')}</td>
                    <td class="tdwhiteL">
                    	<input type=text name=sort id=sort size=10>
                    </td>
                    <td class="tdblueM">${f:getMessage('설명')}</td>
                    <td class="tdwhiteL">
                    	<input type=text name=description id=description >
                    </td>
                    <td class="tdblueM">${f:getMessage('활성화')}</td>
                    <td class="tdwhiteL">
                    	<input type=checkbox name=enabled id=enabled value="true" >
                    </td>                                  
                </tr>
                         
				<tr align=center height=5>
                	<td valign="top" style="padding:0px 0px 0px 0px" height=3 colspan="6" >
                         
                         <!-- 등록과 초기화 버튼 -->
                		<table border="0" cellpadding="0" cellspacing="4" align="right">
			            	<tr>
			                	<td>
			                		<button type="button" id="create" class="btnCRUD">
			                			<span></span>
			                			${f:getMessage('등록')}
			                		</button>
			                		
		                    		<button type="button" id="update" class="btnCRUD" style="display: none;">
			                			<span></span>
			                			${f:getMessage('수정')}
			                		</button>
			                		
		                    		<button type="button" id="delete" class="btnCRUD" style="display: none;">
			                			<span></span>
			                			${f:getMessage('삭제')}
			                		</button>
			                		
			                		<button type="reset" id="reset" class="btnCustom" >
			                			<span></span>
			                			${f:getMessage('초기화')}
			                		</button>
			                	</td>					                 
			            	</tr>
	          			</table>
                         
                	</td>
             	</tr>
				
         	</table>
		</td>
	</tr>

	<tr align=center >
   		<td valign="top" style="padding:0px 0px 0px 0px" >
			<div id="listGridBox" style="width:100%;" ></div>
		</td>
	</tr>

	<tr>
		<td valign="top" style="padding:0px 0px 0px 0px">
			<div id="pagingBox"></div>
		</td>
	</tr>
	
</table>

<DIV id="lodingDIV" class='loading_edge' style='display: none;'>
	<img src="/Windchill/jsp/portal/images/loading.gif" />
</DIV>

</form>