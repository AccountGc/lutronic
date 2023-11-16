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

$(document).ready(function() {
	
	var oid = '${oid}';
	lfn_DhtmlxGridInit();
	setRootDefinition(oid);
	lfn_Search();
	//setButtonControl();
	//rootInit();
})

<%----------------------------------------------------------
*                      버튼 설정
----------------------------------------------------------%>
$(function() {
	
	<%----------------------------------------------------------
	*                      Root 추가 
	----------------------------------------------------------%>
	$("#createRootDefinition").click(function() {
		//alert("test");
		var url = getURLString("admin", "createRootDefinition", "do");
		openOtherName(url,"window","500","400","status=no,scrollbars=yes,resizable=yes");
		
	})
	
	<%----------------------------------------------------------
	*                      Root 수정
	----------------------------------------------------------%>
	$("#updateRootDefinition").click(function() {
		
		var url = getURLString("admin", "updateRootDefinition", "do") + "?oid="+$("#rootOid").val();
		openOtherName(url,"window","500","400","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      Root 삭제
	----------------------------------------------------------%>
	$("#deleteRootDefinition").click(function() {
		
		if(documentListGrid.getRowsNum()>0){
			alert("${f:getMessage('활동이 있을경우 삭제할 수 없습니다')}");
			return;
		}
		
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){
			
			return;
		}

		var form = $("form[name=admin_listChangeActivity]").serialize();
		var url	= getURLString("admin", "deleteRootDefinition", "do");
		
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				
				if(data.result) {
					
						location.reload();
					
					
				}else {
					alert(data.msg);
				}
			}
		});
		
	})
	
	<%----------------------------------------------------------
	*                      활동 추가 
	----------------------------------------------------------%>
	$("#createActivity").click(function() {
		var url = getURLString("admin", "createActivityDefinition", "do") + "?oid="+$("#rootOid").val();
		openOtherName(url,"window","500","500","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      활동 삭제
	----------------------------------------------------------%>
	$("#deleteActivity").click(function() {
		
		var checked = documentListGrid.getCheckedRows(1);
		
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
			return;
		}
		
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){
			return;
		}
		
		
		var array = checked.split(",");
		var returnArr = new Array();
		var deleteOid ="";
		for(var i =0; i < array.length; i++) {
			var oid = array[i];	
			deleteOid = deleteOid + oid+","
		}
		
		$("#deleteOid").val(deleteOid);
		
		var form = $("form[name=admin_listChangeActivity]").serialize();
		var url	= getURLString("admin", "deleteActivityDefinition", "do");
		
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				
				if(data.result) {
					
					lfn_Search();
					
					
				}else {
					alert(data.msg);
				}
			}
		});
		
	})
	
	<%----------------------------------------------------------
	*                      Root 변경시 
	----------------------------------------------------------%>
	$("#rootOid").change(function() {
		lfn_Search();
	})
})

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	
	var COLNAMEARR = {
			 col01 : '${f:getMessage("단계")}'
			,col02 : ''
			,col03 : '${f:getMessage("활동명")}'
			,col04 : '${f:getMessage("활동구분")}'
			,col05 : '${f:getMessage("담당부서")}'
			,col06 : '${f:getMessage("담당자")}'
			,col07 : '${f:getMessage("sort")}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	sHeader += "," + COLNAMEARR.col05;
	sHeader += "," + COLNAMEARR.col06;
	sHeader += "," + COLNAMEARR.col07;
	
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";
	sHeaderAlign[6]  = "text-align:center;";

	var sWidth = "";
	sWidth += "10";
	sWidth += ",5";
	sWidth += ",45";
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
	
	var sColType = "";
	sColType += "ro";
	sColType += ",ch";
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
*                     Activity 검색
----------------------------------------------------------%>
function lfn_Search() {
	var form = $("form[name=admin_listChangeActivity]").serialize();
	var url	= getURLString("admin", "admin_listChangeActivityAction", "do");
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
			/*
			var vArr = new Array();
			vArr[0] = data.rows    ;
			vArr[1] = data.formPage   ;
			vArr[2] = data.totalCount ;
			vArr[3] = data.totalPage  ;
			vArr[4] = data.currentPage;
			vArr[5] = data.startPage  ;
			vArr[6] = data.endPage    ;
			vArr[7] = data.sessionId  ;
			*/
			documentListGrid.clearAll();
			documentListGrid.loadXMLString(data.xmlString);
			//alert(data.xmlString);
			$("#xmlString").val(data.xmlString);
			
			//gfn_SetPaging(vArr,"pagingBox");
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
			setButtonControl();
        }
		
		
	});
}



<%----------------------------------------------------------
*                     Button 제어
----------------------------------------------------------%>
function setButtonControl(){
	//alert($("#rootOid").val())
	if($("#rootOid").val() ==""){
		
		$("#updateRootDefinition").hide();
		$("#deleteRootDefinition").hide();
		$("#createActivity").hide();
		$("#deleteActivity").hide();
	}else{
		$("#updateRootDefinition").show();
		$("#deleteRootDefinition").show();
		$("#createActivity").show();
		$("#deleteActivity").show();
	}
	
}
<%----------------------------------------------------------
*                     Activity 수정 팝업
----------------------------------------------------------%>
function updateActivityDefinition(oid){
	
	var url = getURLString("admin", "updateActivityDefinition", "do") + "?oid="+oid;
	openOtherName(url,"window","500","500","status=no,scrollbars=yes,resizable=yes");
}
</script>

<body>
<form name="admin_listChangeActivity" id="admin_listChangeActivity" >
<input type="hidden" name="deleteOid" id="deleteOid">
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
										<div id="mainTitle">${f:getMessage('설계변경활동단계')}</div>
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
	   				 </td>
				</tr>
				
				<tr   height=5>
				    <td valign="top" style="padding:0px 0px 0px 0px" height=3 >
	
						<!-- 등록과 초기화 버튼 -->
		     			<table border="0" cellpadding="0" cellspacing="4"  width="100%">
							<tr>
								<td  align="left" width="20%">
									<b>ROOT : </b>
									<select id="rootOid" name="rootOid" style="width:40%">
									</select>
								</td>
	    						<td  align="right">
	    							<button title="Root 추가" class="btnCRUD" type="button" id="createRootDefinition" >
	    								<span></span>
	    								${f:getMessage('Root 추가')}
	    							</button>
	    							<button title="Root 수정" class="btnCRUD" type="button" id="updateRootDefinition">
				                 	 	<span></span>${f:getMessage('Root 수정')}
				                 	</button>
				                 	<button title="Root 삭제" class="btnCRUD" type="button" id="deleteRootDefinition">
				                 	 	<span></span>${f:getMessage('Root 삭제')}
				                 	</button>
				                 	<button title="활동추가" class="btnCRUD" type="button" id="createActivity">
				                 	 	<span></span>${f:getMessage('활동추가')}
				                 	</button>
				                 	<button title="활동삭제" class="btnCRUD" type="button" id="deleteActivity">
				                 	 	<span></span>${f:getMessage('활동삭제')}
				                 	</button>
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
				
				
            </table>
        </td>
	</tr>
</table>

</form>

</body>
</html>