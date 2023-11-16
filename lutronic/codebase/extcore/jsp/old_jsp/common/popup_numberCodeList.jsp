<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>

$(document).ready(function(){
	lfn_DhtmlxGridInit();
	lfn_Search();
	 
});

function lfn_DhtmlxGridInit() {
	var COLNAMEARR = {
			 col01 : '<input type=checkbox name=allCheck id=allCheck>'
			,col02 : '${f:getMessage("이름(국문)")}'
			,col03 : '${f:getMessage("코드")}'
			,col04 : '${f:getMessage("설명")}'
		
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	

	var sWidth = "";
	sWidth += "15";
	sWidth += ",30";
	sWidth += ",20";
	sWidth += ",*";
	
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	
	
	var sColType = "";
	sColType += "ch";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";

	var sColSorting = "";
	sColSorting += "na";
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

function lfn_Search() {
	var form = $("form[name=popup_numberCodeList]").serialize();
	var url	= getURLString("common", "popup_numberCodeAction", "do");
	
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


$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		lfn_DhtmlxGridInit();
		//$("#sortValue").val("");
		//$("#sortCheck").val("");
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	}),
	<%----------------------------------------------------------
	*                      초기화 버튼
	----------------------------------------------------------%>
	$("#btnReset").click(function() {
		$("#creator").val("");
	})
	
	<%----------------------------------------------------------
	*                      선택 버튼 클릭시
	----------------------------------------------------------%>
	$("#selectBtn").click(function () {
		var type = "<c:out value='${type}'/>";
		
		var checked = documentListGrid.getCheckedRows(0);
		
		if(checked == '') {
			alert("${f:getMessage('선택된 데이터가 없습니다.')}");
			return;
		}
		
		var array = checked.split(",");

		
		
		var returnArr = new Array();
		
		for(var i =0; i < array.length; i++) {
			
																	 // OID
   			var name = documentListGrid.cells(array[i], 1).getValue();				 // 이름
   			var code = documentListGrid.cells(array[i], 2).getValue();				 // 코드
   				
  			returnArr[i] = new Array();
   			returnArr[i][0] = name;
   			returnArr[i][1] = code;
   			
		}
		//alert(returnArr);
		opener.addNumberCode(returnArr);
		//}
		
	})
	
	
})
	<%----------------------------------------------------------
	*                      Header checkbox 선택 
	----------------------------------------------------------%>
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
});
$(document).keypress(function(event) {
	
		 if(event.which == 13) {
			 lfn_DhtmlxGridInit();
				//$("#sortValue").val("");
				//$("#sortCheck").val("");
				$("#sessionId").val("");
				$("#page").val(1);
				lfn_Search();
		 }
	});


</script>

<form id="popup_numberCodeList" name="popup_numberCodeList">

<!-- NumberCode 사용/미사용 변수 -->
<input type='hidden' name='disable' id='disable' value='<c:out value="${disable }"/>'/>

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<!-- 정렬을 사용할때 사용하는 변수 -->
<input type="hidden" name="sortValue" id="sortValue" />
<input type="hidden" name="sortCheck" id="sortCheck" />

<input type="hidden" name="codeType" id="codeType" value="<c:out value="${codeType}"/>" />

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="3"  > <!--//여백 테이블-->
	<tr align=center height=5>
		<td valign="top" style="padding:0px 0px 0px 0px" height=3 >
		
			<!-- 파란 라인 -->
        	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
         	<table width="100%" border="1" cellpadding="0" cellspacing="0" align="center" >
                <tr	bgcolor="ffffff" align=center>
                    <td class="tdblueM">${f:getMessage('이름(국문)')}</td>
                    <td class="tdwhiteL">
                    	<input type=text name=name id=name size=20>
                    </td>
                                                  
                    <td class="tdblueM">${f:getMessage('코드')}</td>
                    <td class="tdwhiteL">
                    	<input type=text name=code id=code size=10>
                    </td>
                </tr>
             </table>
		</td>
	</tr>
	<tr >
		<td align="right">
			   <!-- 등록과 초기화 버튼 -->
      		<table border="0" cellpadding="0" cellspacing="4" align="right">
                <tr>
	           		<td>
		              	<button type="button" class="btnCRUD" title="${f:getMessage('선택')}" id="selectBtn" name="selectBtn">
		                	<span></span>
		                  	${f:getMessage('선택')}
						</button>
					</td>
				      
				    <td>
	                	<button type="button" class="btnSearch" title="${f:getMessage('검색')}" id="searchBtn" name="searchBtn">
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
						<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
							<span></span>
							${f:getMessage('닫기')}
						</button>
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