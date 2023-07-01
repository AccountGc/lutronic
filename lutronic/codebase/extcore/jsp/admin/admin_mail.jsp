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
	$("#btnReset").click(function() {
		$("#createUser").show();
		$("#updateUser").hide();
		$("#deleteUser").hide();
	})
	$("#createUser").click(function() {
		if($.trim($("#name").val()) == "" ) {
			alert("${f:getMessage('이름을 입력하세요.')}");
			return;
		}
		if($.trim($("#email").val()) == "" ) {
			alert("${f:getMessage('이메일을 입력하세요.')}");
			return;
		}
		actionMailUser("create");
	})
	$("#updateUser").click(function() {
		if($.trim($("#name").val()) == "" ) {
			alert("${f:getMessage('이름을 입력하세요.')}");
			return;
		}
		if($.trim($("#email").val()) == "" ) {
			alert("${f:getMessage('이메일을 입력하세요.')}");
			return;
		}
		actionMailUser("update");
	})
	$("#deleteUser").click(function() {
		actionMailUser("delete");
	})
})

function actionMailUser(command) {
	var name = $("#name").val();
	var email = $("#email").val();
	var enable = $("#enable").is(":checked");
	var oid = $("#oid").val();
	
	var url	= getURLString("admin", "actionMailUser", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {command:command, name:name, email:email, enable:enable, oid:oid},
		dataType:"json",
		async: false,
		cache: false,

		success:function(data){
			alert(data.msg);
			document.location = getURLString("admin", "admin_mail", "do");
		},
		error:function(data){
			//var msg = "${f:getMessage('데이터 검색오류')}";
			alert(data.msg);
		}

	});
}

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '${f:getMessage("이름")}'
			,col02 : '${f:getMessage("이메일")}'
			,col03 : '${f:getMessage("활성화")}'
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
	sWidth += "40";
	sWidth += ",40";
	sWidth += ",20";
	
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
	
	documentListGrid.attachEvent("onRowSelect", function(id,ind){
		
		$("#createUser").hide();
		$("#updateUser").show();
		$("#deleteUser").show();
		
		var name = documentListGrid.cells(id,0).getValue();
		var email = documentListGrid.cells(id,1).getValue();
		var enabled = documentListGrid.cells(id,2).getValue();
		
		$("#name").val(name);
		$("#email").val(email);
		
		if(enabled == "true") {
			$('input:checkBox[name="enable"]').prop("checked", true);
		}else {
			$('input:checkBox[name="enable"]').prop("checked", false);
		}
		
		$("#oid").val(id);
		
	});
	
	documentListGrid.init();
}

function lfn_Search() {
	var form = $("form[name=admin_mail]").serialize();
	var url = getURLString("admin", "admin_mailAction", "do");
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


</script>


<body>

<form name="admin_mail" id="admin_mail" method="post">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<input type=hidden name=oid id=oid >

<!--//여백 테이블-->
<table width="100%" border="0" cellpadding="0" cellspacing="20">
	<tr align="center" height="5">
		<td>
			<table width="100%" border="0" cellpadding="10" cellspacing="3">
				<tr align="center" height="5">
                	<td valign="top" style="padding:0px 0px 0px 0px" height="3">
                    
                        <!-- 상단 제목 부분 -->
                    	<div style="overflow: auto;">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td align="left" class="Sub_TBG" >
										<div id="mainTitle">${f:getMessage('외부 메일 관리')}</div>
									</td>
									<td class="Sub_TRight"></td>
								</tr>
							</table>
						</div>
                        
                    </td>
                </tr>
                
                <tr align="center" height="5">
                    <td valign="top" style="padding:0px 0px 0px 0px" height="3">
                    
                    	<!-- 파란 라인 -->
                    	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
							<tr>
							<td height=1 width=100%></td>
							</tr>
						</table>

                    	<!-- input 폼 -->
                        <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
                        	<col width='10%'><col width='30%'><col width='10%'><col width='30%'><col width='10%'><col width='10%'>
                        	<tr bgcolor="ffffff" height="35">
                                <td class="tdblueM">${f:getMessage('이름')}</td>
                                <td class="tdwhiteL">
                                	<input type=text name=name id=name size=20>
                                </td>
                                <td class="tdblueM">${f:getMessage('이메일')}</td>
                                <td class="tdwhiteL">
                                	<input type=text name=email id=email size=20>
                                </td>
                                <td class="tdblueM">${f:getMessage('활성화')}</td>
                                <td class="tdwhiteL">
                                	<input type=checkbox name=enable id=enable>
                                </td>                                       
                            </tr>
                        </table>
                        
                    </td>
                </tr>
                
                <tr align="center" height="5">
                    <td valign="top" style="padding:0px 0px 0px 0px" height="3">
                        
                        <!-- 등록과 초기화 버튼 -->
                    	<table border="0" cellpadding="0" cellspacing="4" align="right">
			            	<tr>
			                	<td>
			                		<button type="button" class="btnCRUD" id="createUser">
			                			<span></span>
			                			${f:getMessage('등록')}
			                		</button>	
			                		
			                		<button type="button" class="btnCRUD" id="updateUser" style="display: none;">
			                			<span></span>
			                			${f:getMessage('수정')}
			                		</button>
			                					                  
			                  	</td>
			                  	
			                  	<td>
			                  		<button type="button" class="btnCRUD" id="deleteUser" style="display: none;">
			                			<span></span>
			                			${f:getMessage('삭제')}
			                		</button>
			                  	</td>
			                  					                  
			                  	<td>
			                  		<button title="Reset" class="btnCustom" type="reset" id="btnReset">
				                 	 	<span></span>${f:getMessage('초기화')}
				                 	 </button>
			                  	</td>					                 
			              	</tr>
			         	 </table>
         	 
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
			<div id="pagingBox"></div>
		</td>
	</tr>
    
</table>
</form>

</body>
</html>