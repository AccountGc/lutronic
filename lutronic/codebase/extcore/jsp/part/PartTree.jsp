<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	lfn_DhtmlxGridInit();
	
	var param = $("form[name=PartTreeForm]").serialize();
	var url	= getURLString("part", "getPartTreeAction", "do")+"?";
	
	partTreeGrid.clearAndLoad(url+param, null, "json");
	
	partTreeGrid.enableAutoHeight(true, parseInt($(window).height())-100, true);

	  $(window).resize(function() { // 브라우저 사이즈 감지
		  partTreeGrid.enableAutoHeight(true, parseInt($(window).height())-100, true);
	  })
	
})

$(function() {
	<%----------------------------------------------------------
	*                      상위품목 버튼
	----------------------------------------------------------%>
	$("#upItem").click(function() {
		viewBomList("up");
	})
	<%----------------------------------------------------------
	*                      하위품목 버튼
	----------------------------------------------------------%>
	$("#downItem").click(function() {
		viewBomList("down");
	})
	<%----------------------------------------------------------
	*                      END ITEM 버튼
	----------------------------------------------------------%>
	$("#endItem").click(function() {
		viewBomList("end");
	})
	<%----------------------------------------------------------
	*                      상세정보 버튼
	----------------------------------------------------------%>
	$("#detailView").click(function() {
		openView($("#oid").val());
	})
	$("#latestBom").click(function() {
		$("#baseline").val("");
		document.location = getURLString("part", "PartTree", "do") + "?oid="+$("#oid").val();
	})
	<%----------------------------------------------------------
	*                      베이스라인 리스트 변경시
	----------------------------------------------------------%>
	$("#baseline").change(function() {
		alert("1111");
		getPartTreeAction();
	})
	<%----------------------------------------------------------
	*                      정전개 / 역전개 변경시
	----------------------------------------------------------%>
	$("#desc").change(function() {
		getPartTreeAction();
	})
	<%----------------------------------------------------------
	*                      베이스라인 비교 버튼
	----------------------------------------------------------%>
	$("#compare").click(function() {
		/*
	    if($("input[name='baseCheck']:checked").length == 0) {
	    	alert("비교할 BOM이 선택 되지 않았습니다.");
	    	return;
	    }
	    
	    var ss = $("input[name='baseCheck']:checked").attr("value").split("$");
	    
	    $("#oid2").val(ss[0]);
	    $("#baseline2").val(ss[1]);
	    
	    $("#PartTreeForm").attr("action", getURLString("part", "partTreeCompare", "do")).submit();
	    */
	    
	    $("#oid2").val($('#baseline option:selected').attr('title'));
	    $("#baseline2").val($('#baseline').val());
	    
	    $("#PartTreeForm").attr("action", getURLString("part", "partTreeCompare", "do")).submit();
	    
	})
	<%----------------------------------------------------------
	*                      Excel Down 버튼
	----------------------------------------------------------%>
	$("#excelDown").click(function() {
		$("#PartTreeForm").attr("method", "post");
		$("#PartTreeForm").attr("action", getURLString("excelDown", "partTreeExcelDown", "do")).submit();
	});
	<%----------------------------------------------------------
	*                      Drawing Down 버튼
	----------------------------------------------------------%>
	$("#drawingDown").click(function() {
		$("#PartTreeForm").attr("method", "post");
		$("#PartTreeForm").attr("action", getURLString("drawing", "partTreeDrawingDown", "do")).submit();
	})
	
	
})

<%----------------------------------------------------------
*                      Bom Type에 따른 bom 검색
----------------------------------------------------------%>
function viewBomList(bomType){
	var str = getURLString("part", "bomPartList", "do") + "?oid="+$("#oid").val()+"&bomType="+bomType;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=600,height=500,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBomList", opts+rest);
    newwin.focus();
}

<%----------------------------------------------------------
*                      부품번호 선택시 Bom보기
----------------------------------------------------------%>
function gotoViewPartTree(oid) {
	document.location = getURLString("part", "PartTree", "do") + "?oid="+oid;
}

function baselinePartTree(baseOid) {
	$("#baseline").val(baseOid);
	getPartTreeAction();
}

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit(){
	var COLNAMEARR = {
			 col01 : '${f:getMessage('부품번호')}'
			,col02 : '도면번호'
			,col03 : 'level'
			,col04 : '${f:getMessage('부품명')}'
			,col05 : '${f:getMessage('상태')}'
			,col06 : 'Rev'
			,col07 : '${f:getMessage('사양')}'
			,col08 : '${f:getMessage('단위')}'
			,col09 : 'QTY'
			,col10 : '${f:getMessage('무게')}(g)'
			,col11 : 'ECO NO'
			,col12 : '${f:getMessage('부서')}'
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
	sHeader += "," + COLNAMEARR.col12;
	
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
	sWidth += "15";
	sWidth += ",10";
	sWidth += ",5";
	sWidth += ",14";
	sWidth += ",5";
	sWidth += ",5";
	sWidth += ",15";
	sWidth += ",3";
	sWidth += ",3";
	sWidth += ",10";
	sWidth += ",10";
	sWidth += ",5";
	

	var sColAlign = "";
	sColAlign += "left";
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
	sColAlign += ",center";
	
	var sColType = "";
	sColType += "tree";
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
	sColSorting += ",na";
	
	partTreeGrid = new dhtmlXGridObject('partTree');
	partTreeGrid.setImagePath("/Windchill/jsp/js/dhtmlx/imgs/");
	partTreeGrid.setIconsPath("/Windchill");
	partTreeGrid.setHeader(sHeader,null,sHeaderAlign);
	partTreeGrid.enableAutoHeight(true, parseInt($(window).height())-100, true);
	partTreeGrid.setInitWidthsP(sWidth);
	partTreeGrid.setColAlign(sColAlign);
	partTreeGrid.setColTypes(sColType);
	partTreeGrid.setColSorting(sColSorting);
	
	/*grid text copy option*/
    partTreeGrid.enableBlockSelection();
    partTreeGrid.forceLabelSelection(true);
    
    partTreeGrid.attachEvent("onKeyPress",function onKeyPressed(code,ctrl,shift){
        if(code==67&&ctrl){
            if(!partTreeGrid._selectionArea) return alert("return");
            partTreeGrid.setCSVDelimiter("\t");
            partTreeGrid.copyBlockToClipboard();
	    }
	    return;
	});
    partTreeGrid.attachEvent("onEditCell", function(){
    	return false;
    });
    
	partTreeGrid.init();
	
}

<%----------------------------------------------------------
*                      Bom 리스트 검색
----------------------------------------------------------%>
function getPartTreeAction(){
	
	var param = $("form[name=PartTreeForm]").serialize();
	var url	= getURLString("part", "getPartTreeAction", "do");
	
	$.ajax({
		url: url,
		type: "POST",
		data: param,
		dataType:"json",
		async: true,
		error:function(data){
			var msg = "데이터 검색에러";
			alert(msg);
		},
		success: function(data){
			window.console.log(data);
			if(!data.result){
				alert(data.msg);
			}
			partTreeGrid.clearAll();
			if(data.rows != ""){
				partTreeGrid.parse(data,"json");
			}
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	});
};
</script>

<form name=PartTreeForm id="PartTreeForm" method="post">

<input type="hidden" name="viewName" id="viewName" value="<c:out value='${view }'/>" />

<input type="hidden" name="oid" id="oid" value="<c:out value='${oid }'/>"/>

<input type="hidden" name="oid2" id="oid2"/>
<input type="hidden" name="baseline2" id="baseline2"/>

<table width="100%" border="0" cellpadding="0" cellspacing="0" >

	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color=white>BOM</font></B></td>
		   		</tr>
			</table>
		    <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
		    	<tr height="30">
		    		<td width="40%" align="left">
		    		
			    		<select name="baseline" id="baseline">
			    			<option value=''>-
			    			</option>
				    		<c:forEach items="${list }" var="baseline">
				    			<option value='<c:out value="${baseline.baseOid }" />' title="<c:out value="${baseline.partOid }" />">
				    				<c:out value="${baseline.baseName }" />
				    			</option>
				    		</c:forEach>
			    		</select>
			    		
			    		<select name="desc" id="desc">
			    			<option value="true" >정전개</option>
			    			<option value="false">역전개</option>
			    		</select>
			    	</td>
			    	
		    		<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
							<tr>
								<td>
									<button type="button" name="upItem" id="upItem" class="btnCustom" style="width: 80px">
										<span></span>
										상위품목
									</button>
								</td>
								
					    		<td>
					    			<button type="button" name="downItem" id="downItem" class="btnCustom" style="width: 80px">
										<span></span>
										하위품목
									</button>
								</td>
								
					    		<td>
					    			<button type="button" name="endItem" id="endItem" class="btnCustom" style="width: 90px">
										<span></span>
										END ITEM
									</button>
								</td>
								
								<%-- 
								<c:if test="${bsobj ne null }">
									<td>
										<button type="button" name="latestBom" id="latestBom" class="btnCustom" style="width: 110px">
											<span></span>
											최신BOM전개
										</button>
									</td>
								</c:if>
								--%>
								
								<td>
									<button type="button" name="excelDown" id="excelDown" class="btnCustom" style="width: 100px">
										<span></span>
										Excel Down
									</button>
								</td>
								
								<td>
									<button type="button" name="drawingDown" id="drawingDown" class="btnCustom" style="width: 120px">
										<span></span>
										도면 일괄 Down
									</button>
								</td>
								<%-- 
								<td>
									<button type="button" name="detailView" id="detailView" class="btnCustom" style="width: 80px">
										<span></span>
										상세정보
									</button>
								</td>
								--%>
								
								<td>
									<button type="button" name="compare" id="compare" class="btnCustom" style="width: 120px">
										<span></span>
										베이스라인 비교
									</button>
								</td>
								
								
								<td>
									<button type="button" name="" id="" class="btnClose" onclick="self.close()" style="width: 50px">
										<span></span>
										닫기
									</button>
								</td>
							</tr>
						</table>
		    		</td>
		    	</tr>
		    </table>
		</td>
	</tr>
	
	
	<tr align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">
		
			<table width="100%"  border="0" cellpadding="0" cellspacing="0" class="tablehead" align="center">
	    		<tr>
	    			<td height=1 width=100%></td>
	    		</tr>
			</table>
    		<div id="partTree" style="width:100%;"></div>
		</td>
	</tr>

</table>

</form>
