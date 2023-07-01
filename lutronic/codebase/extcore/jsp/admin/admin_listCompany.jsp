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

$(function () {
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	})
	$("#setDepart").click(function() {
		var url = getURLString("admin", "admin_setDepartment", "do") + "?departmentOid="+$("#departmentOid").val();
		openWindow(url,'Dept',"550","400");
	})
	$("#setDuty").click(function() {
		var url = getURLString("admin", "admin_setDuty", "do");
		openWindow(url,'Duty', "550","400");
	})
	$("#setChief").click(function() {
		var checked = documentListGrid.getCheckedRows(0);
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
			return;
		}
		
		var array = checked.split(",");
		var oid = array[0];
		
		var name = documentListGrid.cells(oid, 3).getValue();

		var url	= getURLString("admin", "admin_actionChief", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:{userOid: oid},
			dataType:"json",
			async: false,
			cache: false,

			error:function(data){
				var msg = "${f:getMessage('데이터 검색오류')}";
				alert(msg);
			},

			success:function(data){
				if(data) {
					alert(name + "${f:getMessage('님이 부서장으로 등록 되었습니다.')}");
				}else {
					alert(name + "${f:getMessage('의 부서를 지정해 주세요.')}");
				}
				lfn_Search();
			}
		});
		
	})
	
	//usetToGroup
	
	$("#usetToGroup").click(function() {
		
		$("#admin_listCompany").attr("method", "post");
		$("#admin_listCompany").attr("action", getURLString("excelDown", "usetToGroupExcel", "do")).submit();
		
	})
})

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit() {
	var COLNAMEARR = {
			 col01 : ''
			,col02 : '${f:getMessage("번호")}'
			,col03 : '${f:getMessage("아이디")}'
			,col04 : '${f:getMessage("이름")}'
			,col05 : '${f:getMessage("부서")}'
			,col06 : '${f:getMessage("직위")}'
			,col07 : '${f:getMessage("이메일")}'
			,col08 : '${f:getMessage("부서장")}'
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
	sWidth += "3";
	sWidth += ",10";
	sWidth += ",20";
	sWidth += ",20";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",20";
	sWidth += ",7";
	
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
	sColType += "ra";
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
	
	documentListGrid.init();
}
<%----------------------------------------------------------
*                      데이터 검색 AJAX
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=admin_listCompany]").serialize();
	var url	= getURLString("admin", "admin_listCompanyAction", "do");
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
			
			$("#total").html(data.totalCount);
			
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
</script>

<body>

<form name="admin_listCompany" id="admin_listCompany">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<input type="hidden" name="departmentOid" id="departmentOid" >

<!--//여백 테이블-->
<table width="100%" border="0" cellpadding="0" cellspacing="20">
	<tr align="center" height="5px">
		<td>	
			<table width="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr align="center" height="5px">
					<td valign="top" style="padding:0px 0px 0px 0px" height="5px">
					
						<!-- 상단 제목 부분 -->
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr height="5px" >
								<td align="left" class="Sub_TBG" >
									<div id="mainTitle">${f:getMessage('부서 관리')}</div>
								</td>
								<td class="Sub_TRight"></td>
							</tr>
						</table>
					</td>
				</tr>
			
				<tr align="center" height="5">
					<td valign="top" style="padding:0px 0px 0px 0px" headers="3">
						
						<!-- 파란 라인 -->
                       	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
							<tr><td height="1" width="100%"></td></tr>
						</table>
						
						<!-- input 폼 -->
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
						<col width='100'><col><col width='100'><col>				
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('아이디')}</td>
								<td class="tdwhiteL">
									<input name="id" class="txt_field" size="30" value=""/>
								</td>
								<td class="tdblueM">${f:getMessage('이름')}</td>
								<td class="tdwhiteL">
									<input name="name" class="txt_field" size="30" value=""/>
								</td>
							</tr>
						</table>
						
					</td>
				</tr>
				<tr align="center" height="5">
					<td valign="top" style="padding:0px 0px 0px 0px" height="3">
						
						<!-- 검색과 설정 버튼 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			            	<tr>
			                	<td>
				                  	<button type="button" id="searchBtn" class="btnSearch">
				                  		<span></span>
				                  		${f:getMessage('검색')}
				                  	</button>
				            	</td>
				            	<td>
				            		<button type="button" id="setDepart" class="btnCustom" style="display: none;">
										<span></span>
										${f:getMessage('부서 설정')}
									</button>
									
									<button type="button" id="setDuty" class="btnCustom">
										<span></span>
										${f:getMessage('직위 설정')}
									</button>
									
									<button type="button" id="usetToGroup" class="btnCustom">
										<span></span>
										${f:getMessage('유저별 그룹 다운로드')}
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
		<td valign="top" style="padding:0px 0px 0px 0px" height="3" align="left">
				
			<!-- 부서장지정버튼 -->
			<table border="0" cellpadding="0" cellspacing="4" align="left">
				<tr>
					<td>
						<span id="total"></span>${f:getMessage('명의 사용자가 등록 되어 있습니다.')}
					</td>
					<td>
						<button type="button" class="btnCustom" id="setChief">
							<span></span>
							${f:getMessage('부서장 지정')}
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
			<div id="pagingBox"></div>
		</td>
	</tr>
	
</table>

</form>

</body>
</html>