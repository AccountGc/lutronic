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
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	lfn_DhtmlxGridInit();
	lfn_Search();
});

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit(){
	var COLNAMEARR = {
			 col01 : ''
			,col02 : '${f:getMessage('번호')}'
			,col03 : ''
			,col04 : '${f:getMessage('품목번호')}'
			,col05 : '${f:getMessage('품목명')}'
			,col06 : 'Rev.'
			,col07 : '${f:getMessage('상태')}'
			,col08 : '${f:getMessage('등록자')}'
			,col09 : '${f:getMessage('등록일')}'
			,col10 : '${f:getMessage('최종수정일')}'
			,col11 : 'BOM'
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

	var sWidth = "";
	sWidth += "3";
	sWidth += ",3";
	sWidth += ",3";
	sWidth += ",21";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",10";
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	
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
*                      데이터 검색
----------------------------------------------------------%>
function  lfn_Search(){
	var form = $("form[name=selectEOPart]").serialize();
	var url	= getURLString("part", "selectEOPartAction", "do");
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

$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		$("#sortValue").val("");
		$("#sortCheck").val("");
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
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
			
			var oid = array[i];		
   			var number = documentListGrid.cells(array[i], 3).getValue();				 // 품목코드
   			var name = documentListGrid.cells(array[i], 4).getValue();				 // 품명
   			var version = documentListGrid.cells(array[i], 5).getValue();				 // 버전
   			var state = documentListGrid.cells(array[i], 6).getValue();	 		 // 상태
   			var creator = documentListGrid.cells(array[i], 7).getValue();     // 등록자
 			var createDate = documentListGrid.cells(array[i], 8).getValue();			 	 // 등록일
  			var updateDate = documentListGrid.cells(array[i], 9).getValue();			 	 // 수정일
  			var bom = documentListGrid.cells(array[i], 10).getValue();			 	 // BOM
  				
  			returnArr[i] = new Array();
   			returnArr[i][0] = oid;
   			returnArr[i][1] = number;
   			returnArr[i][2] = name;
   			returnArr[i][3] = version;
   			returnArr[i][4] = state;
   			returnArr[i][5] = creator;
  			returnArr[i][6] = createDate;
  			returnArr[i][7] = updateDate;
  			returnArr[i][8] = bom;
		}
		
		opener.parent.addPart(returnArr);
		self.close();
	})
})
</script>

<body>

<form name="selectEOPart" id="selectEOPart">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="hidden" name="xmlString" id="xmlString" value="" />

<!-- 정렬을 사용할때 사용하는 변수 -->
<input type="hidden" name="sortValue" id="sortValue" />
<input type="hidden" name="sortCheck" id="sortCheck" />

<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td valign=top width=100%>
            <table width="100%"  border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
                <tr align="center" height="5">
                    <td>
                        <table width="100%" border="0" cellpadding="0" cellspacing="3" >
                            <tr align="center">
                                <td valign="top" style="padding:0px 0px 0px 0px">
                                    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
                                        <tr>
                                            <td height=1 width=100%></td>
                                        </tr>
                                    </table>
                                    <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"
                                            style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
                                    <col width="13%"><col width="37%"><col width="13%"><col width="37%">
                                        <tr>
                                            <td class="tdblueM">${f:getMessage('번호')}</td>
                                            <td class="tdwhiteL"><input type="text" name="number"></td>
                                            <td class="tdblueM">${f:getMessage('제목')}</td>
                                            <td class="tdwhiteL"><input type="text" name="name"></td>
                                        </tr>
                                        <tr>
                                            <td class="tdblueM">EO Type</td>
                                            <td class="tdwhiteL" colspan="3">
                                            <input type="radio" name="eoType" value="ECO">ECO
                                            <input type="radio" name="eoType" value="IPO">${f:getMessage('신제품')}&${f:getMessage('부품')}
                                            <input type="radio" name="eoType" value="MCO">Minor
                                            </td>
                                        </tr>
                                    </table>
                                 </td>
                              </tr>
                              <tr>
                                <td align="center">
                                    <table>
                                        <tr>
                                            <td>
                                            	<button type="button" id="searchBtn" class="btnSearch">
                                            		<span></span>
                                            		${f:getMessage('검색')}
                                            	</button>
                                            </td>
                                            <td>
                                            	<button type="button" id="selectBtn" class="btnCustom">
                                            		<span></span>
                                            		${f:getMessage('선택')}
                                            	</button>
                                            </td>
                                            <td>
                                            	<button type="button" class="btnClose" onclick="self.close()">
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

    <tr height="35">
		<td>
			<div id="pagingBox"></div>
		</td>
	</tr>
	
</table>

</form>

</body>
</html>