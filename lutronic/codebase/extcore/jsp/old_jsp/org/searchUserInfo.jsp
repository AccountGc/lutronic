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

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	lfn_DhtmlxGridInit();
	lfn_Search();
});

<%----------------------------------------------------------
*                      부서 선택시
----------------------------------------------------------%>
function lfn_DeptTreeOnClick(id){
	var name   = deptTree.getAttribute(id, "text");
	var oid    = deptTree.getAttribute(id, "oid");
	var code   = deptTree.getAttribute(id, "code");
	
	$("#deptName").html("<B>" + name + "</B>");
	if("ROOT" == code ) {
		code = "";
	}
	$("#deptCode").val(code);
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
			,col02 : '${f:getMessage('이름')}' 						// 이름
			,col03 : '${f:getMessage('직위')}'						// 부서코드
			,col04 : '${f:getMessage('부서')}'								// 부서
			,col05 : '${f:getMessage('이메일')}'								// ???
			,col06 : '${f:getMessage('부서장')}'								// 부서장
			,col07 : 'WTOID'								// 부서장
			,col08 : '${f:getMessage('권한')}'
			,col09 : '${f:getMessage('업무')}'
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
	
	var sWidth = "";
	sWidth += "5";
	sWidth += ",30";
	sWidth += ",15";
	sWidth += ",15";
	sWidth += ",25";
	sWidth += ",10";
	sWidth += ",0";
	sWidth += ",0";
	sWidth += ",0";
	
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
	
	var sColType = "";
	if("${mode}" == "single") {
		sColType += "ra";
	}else {
		sColType += "ch";
	}
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
    
	documentListGrid.setColumnHidden(6,true);
	documentListGrid.setColumnHidden(7,true);
	documentListGrid.setColumnHidden(8,true);
	
	documentListGrid.init();
}

$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	})
	
	$("#keyvalue").keypress(function(event) {
		 if( event.which == 13) {
			 $("#sessionId").val("");
			 $("#page").val(1);
			 lfn_Search();
		 }
	})
	<%----------------------------------------------------------
	*                      선택 버튼 클릭시
	----------------------------------------------------------%>
	$("#selectBtn").click(function () {
		var checked = documentListGrid.getCheckedRows(0);
		
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
			return;
		}
		
		obj1 = "${obj1}";
		obj2 = "${obj2}";
		
		var array = checked.split(",");
		var returnArr = new Array();
		
		if("${reFunc}" == "") {
		
			if("${mode}" == "single") {
				if("${userType}" == "people") {
					$("#"+obj1, opener.document).val(array[0]);
				}else if("${userType}" == "wtuser") {
					$("#"+obj1, opener.document).val(documentListGrid.cells(array[0], 6).getValue());
				}
				$("#"+obj2, opener.document).val(documentListGrid.cells(array[0], 1).getValue());
				
			}else {
			
				for(var i =0; i < array.length; i++) {
					
					var oid = array[i];															 // OID
		   			var name = documentListGrid.cells(array[i], 1).getValue();				 // 이름
		   			var duty = documentListGrid.cells(array[i], 2).getValue();				 	 // 직책
		   			var deptName = documentListGrid.cells(array[i], 3).getValue();				 // 부서명
		   			var email = documentListGrid.cells(array[i], 4).getValue();	 		 // 이메일
		   			var chief = documentListGrid.cells(array[i], 5).getValue();     // 부서장
		   			var wtOid = documentListGrid.cells(array[i], 6).getValue(); 	// WTUser OID
		  				
		  			returnArr[i] = new Array();
		   			returnArr[i][0] = oid;
		   			returnArr[i][1] = name;
		   			returnArr[i][2] = duty;
		   			returnArr[i][3] = deptName;
		   			returnArr[i][4] = email;
		   			returnArr[i][5] = chief;
		   			returnArr[i][6] = wtOid;
				}
				
				opener.parent.addUser(returnArr,obj1,obj2);
			}
		}else {
			for(var i =0; i < array.length; i++) {
				
				var oid = array[i];															 // OID
	   			var name = documentListGrid.cells(array[i], 1).getValue();				 // 이름
	   			var duty = documentListGrid.cells(array[i], 2).getValue();				 	 // 직책
	   			var deptName = documentListGrid.cells(array[i], 3).getValue();				 // 부서명
	   			var email = documentListGrid.cells(array[i], 4).getValue();	 		 // 이메일
	   			var chief = documentListGrid.cells(array[i], 5).getValue();     // 부서장
	   			var wtOid = documentListGrid.cells(array[i], 6).getValue(); 	// WTUser OID
	  				
	  			returnArr[i] = new Array();
	   			returnArr[i][0] = oid;
	   			returnArr[i][1] = name;
	   			returnArr[i][2] = duty;
	   			returnArr[i][3] = deptName;
	   			returnArr[i][4] = email;
	   			returnArr[i][5] = chief;
	   			returnArr[i][6] = wtOid;
			}
		}
		
		
		<c:choose>
			<c:when test="${not empty reFunc }">
			parent.opener.${reFunc}(returnArr);
			</c:when>
		</c:choose>
		
		self.close();
	})
})

<%----------------------------------------------------------
*                      데이터 검색
----------------------------------------------------------%>
function lfn_Search() {
	var form = $("form[name=selectPeopleForm]").serialize();
	var url	= getURLString("user", "searchUserAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('검색오류')}";
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

</script>

<body>

<form id="selectPeopleForm" name="selectPeopleForm">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="5"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<input type="hidden"	name="deptCode"		id="deptCode"	value="">

<input type="text" style="display: none">

<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top" width="180" background="/Windchill/portal/images/ds_sub.gif" bgcolor="ffffff" >
			
			<jsp:include page="/eSolution/department/treeDepartment.do">
				<jsp:param value="ROOT" name="code"/>
			</jsp:include>
			
		</td>
		<td valign="top">
			<table width=100% >
				<tr>
					<td>
			
						<table width=95% height=40 align=center border=0>
							<tr>
								<td>
									<table border=0 cellpadding=0 cellspacing=0 >
										<tr>
											<td>[</td>
											<td nowrap>
												<span id="deptName">
													<b>LUTRONIC</b>
												</span>
											</td>
											<td>]</td>
										</tr>
									</table>
								</td>
							
								<td align="right">
									<table border=0 cellpadding="0" cellspacing=0 align="right">
										<tr>
											<td>
												${f:getMessage('이름')}&nbsp;:&nbsp;
												<input type="text" name="keyvalue" size="15" id="keyvalue" style="ime-mode:active;" value="">
						                    </td>
						      				<td>
							        			<table border=0 cellpadding="0" cellspacing=2 align="right">
						                            <tr>
						                            	<td>
							                                <button type="button" class="btnSearch" title="${f:getMessage('검색')}" id="searchBtn" name="searchBtn">
											                  	<span></span>
											                  	${f:getMessage('검색')}
										                  	</button>
									                  	</td>
						                                
						                                <td> 
						                                	<button type="button" class="btnCustom" title="${f:getMessage('선택')}" id="selectBtn" name="selectBtn">
											                  	<span></span>
											                  	${f:getMessage('선택')}
										                  	</button>
						                                </td>
						                                
						                                <td> 
						                                	<button type="button" class="btnClose" title="${f:getMessage('닫기')}" id="closeBtn" name="closeBtn" onclick="self.close()">
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
						<table width="100%" border="0" cellpadding="2" cellspacing="1" align="center" valign=top>
							<tr height="35">
								<td>
									<div id="pagingBox"></div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
</table>				

</form>

</body>
</html>