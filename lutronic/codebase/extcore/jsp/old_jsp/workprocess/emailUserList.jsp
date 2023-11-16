<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	
	$(document).on('click', 'input:checkbox[name="allCheck"]', function(){
		if(this.checked) {
			documentListGrid.setColLabel(0, "<input type=checkbox name=allCheck id=allCheck checked>");
			var rows = documentListGrid.getRowsNum();
			
			for(var i = 0; i < rows; i++) {
				var rowId = documentListGrid.getRowId(i);
				if(documentListGrid.cells(rowId,2).getValue() == "") {
					
				} else {
					documentListGrid.cellById(rowId, 0).setChecked(true);
				}
			}
			
		}else {
			documentListGrid.setColLabel(0, "<input type=checkbox name=allCheck id=allCheck>");
			var rows = documentListGrid.getRowsNum();
			for(var i = 0; i < rows; i++) {
				var rowId = documentListGrid.getRowId(i);
				if(documentListGrid.cells(rowId,2).getValue() == "") {

				} else {
					documentListGrid.cellById(rowId, 0).setChecked(false);
				}
			}
		}
	});
	
})

window.resetSearch = function() {
	lfn_DhtmlxGridInit();
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}
<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : ''
			,col02 : '${f:getMessage('이름')}'
			,col03 : '${f:getMessage('이메일')}'
	}
	
	var sHeader = "";
	//sHeader +=       COLNAMEARR.col01;
	sHeader +=       '<input type=checkbox name=allCheck id=allCheck>';
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";

	var sWidth = "";
	sWidth += "10";
	sWidth += ",30";
	sWidth += ",60";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	
	var sColType = "";
	sColType += "ch";
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

function lfn_Search(){
	var workOid = "<c:out value='${workOid }'/>";
	var form = $("form[name=emailUserList]").serialize();
	//var url	= getURLString("groupware", "emailUserListAction", "do");
	var url	= getURLString("groupware", "emailUserListNewAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		//data: {workOid: workOid},
		data:form,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			//documentListGrid.clearAll();
			//documentListGrid.loadXMLString(data);
			//documentListGrid.parse(data,"xml");
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
		},beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	});
}

$(function() {
	$("#onSelect").click(function() {
		var checked = documentListGrid.getCheckedRows(0);
		
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
			return;
		}
		
		var array = checked.split(",");
		var returnArr = new Array();
		var oids = "";
		
		for(var i =0; i < array.length; i++) {
			
			var oid = array[i];		
   			var name = documentListGrid.cells(array[i], 1).getValue();				 // 이름
   			var email = documentListGrid.cells(array[i], 2).getValue();				 // 이메일
   			
   			oids += oid + ",";
   			
  			returnArr[i] = new Array();
   			returnArr[i][0] = oid;
   			returnArr[i][1] = name;
   			returnArr[i][2] = email;
		}
		emailUserAction(oids);
		/*
		var result = emailUserAction(oids);
		
		if(result.responseText == "true") {
			opener.addEmailUser(returnArr);
			self.close();
		}else {
			alert("외부 메일 지정시 오류가 발생 하였습니다.");
			return;
		}
		*/
	});
	$(document).keypress(function(event) {
		
		 if(event.which == 13  ) {
			 $("#searchBtn").click();
			 return false;
		 }
	});
	$("#searchBtn").click(function (){
		$("#command").val("search");
		$("#sessionId").val("");
		lfn_Search();
	})
})

function emailUserAction(userOid) {
	var workOid = "<c:out value='${workOid }'/>";
	var command = "addMaileUser";
	var url	= getURLString("groupware", "emailUserAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {workOid: workOid,command: command, userOid: userOid},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},
		success:function(data){
			if(data.result) {
				var list = data.list;
				var returnArr = new Array();
				for(var i =0; i < list.length; i++) {
					returnArr[i] = new Array();
		   			returnArr[i][0] = list[i].oid;
		   			returnArr[i][1] = list[i].name;
		   			returnArr[i][2] = list[i].email;
		   			opener.addEmailUser(returnArr);
		   			
		   			//self.close();
				}
				if(list.length>0){
					alert("추가되었습니다.");
				}
			}else {
				alert("${f:getMessage('외부 메일 지정시 오류가 발생 했습니다.')}");
				return;
			}
		}
	});
}
</script>

<body>

<form name="emailUserList" id="emailUserList">
<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->

<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>
<input type="hidden" name="command" id="command" value="" />
<input type="hidden" name="workOid"    id="workOid"     value="<c:out value='${workOid }'/>"/>

<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
				<tr> 
					<td height=30 width=99% align=center><B><font color=white></font></B></td>
				</tr>
			</table>
			
			<table width="99%" border="0" align="center" cellpadding="1" cellspacing="1" >
				<tr>
					<td><img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />&nbsp;${f:getMessage('외부 유저 메일')}</td>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
							<tr>
								<td>
									${f:getMessage('이름')}
								</td>

										<td>
										<input type="text" id="named"
											name="named" size="30" class="txt_field" style="width: 30%"
											value="" /></td>
								<td>
				                  	<button type="button" name="" value="" class="btnSearch" title="${f:getMessage('검색')}" id="searchBtn" name="searchBtn">
					                  	<span></span>
					                  	${f:getMessage('검색')}
				                  	</button>   
				                  </td>
								<td>
									<button type="button" class="btnCRUD" id="onSelect">
										<span></span>
										${f:getMessage('선택')}
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