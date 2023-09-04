<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript" src="/Windchill/jsp/js/dhtmlx/dhtmlx.js" ></script>



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

<script>

var rowId = 0;

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	lfn_DhtmlxGridInit();
	viewPartBomAction('0', $('#oid').val(), "true", '');
	
	partTreeGrid.enableAutoHeight(true, parseInt($(window).height())-100, true);

  $(window).resize(function() { // 브라우저 사이즈 감지
	  partTreeGrid.enableAutoHeight(true, parseInt($(window).height())-100, true);
  })
	
})

<%----------------------------------------------------------
*                      view 리스트 가져오기
----------------------------------------------------------%>
function getViews() {
	var form = $("form[name=PartTreeForm]").serialize();
	var url	= getURLString("common", "getViews", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "View 목록 에러";
			alert(msg);
		},

		success:function(data){
			for(var i=0; i<data.length; i++) {
				$("#view").append("<option value='"+data[i]+"'>" + data[i] + "</option>");
			}
		}
	});
}

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
		rowId = 0;
		partTreeGrid.clearAll();
		viewPartBomAction('0', $('#oid').val(), "true", '');
	})
	<%----------------------------------------------------------
	*                      베이스라인 리스트 변경시
	----------------------------------------------------------%>
	$("#baseline").change(function() {
		rowId = 0;
		partTreeGrid.clearAll();
		viewPartBomAction('0', $('#oid').val(), "true", this.value);
	})
	<%----------------------------------------------------------
	*                      정전개 / 역전개 변경시
	----------------------------------------------------------%>
	$("#desc").change(function() {
		rowId = 0;
		partTreeGrid.clearAll();
		viewPartBomAction('0', $('#oid').val(), "true", '');
	})
	<%----------------------------------------------------------
	*                      베이스라인 비교 버튼
	----------------------------------------------------------%>
	$("#compare").click(function() {
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
	})
	
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
	//document.location = getURLString("part", "viewPartBom", "do") + "?oid="+oid;
	$('#oid').val(oid);
	rowId = 0;
	partTreeGrid.clearAll();
	viewPartBomAction('0', oid, "true", '');
}

function baselinePartTree(baseOid) {
	$("#baseline").val(baseOid);
	rowId = 0;
	partTreeGrid.clearAll();
	viewPartBomAction('0', $('#oid').val(), "true", baseOid);
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
	partTreeGrid.enableAutoHeight(true, '500', true);
	partTreeGrid.setInitWidthsP(sWidth);
	partTreeGrid.setColAlign(sColAlign);
	partTreeGrid.setColTypes(sColType);
	partTreeGrid.setColSorting(sColSorting);
	
	partTreeGrid.attachEvent("onRowDblClicked",function(rowId,cellIndex){
		
		// 하위가 열려있을때
		if(partTreeGrid.getOpenState(rowId)) {
			partTreeGrid.closeItem(rowId);
		}
		// 하위가 닫혀있을때
		else {
			var parentOid = partTreeGrid.getSelectedId(); 
			
			// 자식이 몇개 있는지 검사
			var child = partTreeGrid.hasChildren(rowId);
			
			// Id로 cell 검사
			var cell = partTreeGrid.cellById(rowId, 0);
			
			// 하위 ID 리스트 가져오기
			var subItems = partTreeGrid.getSubItems(rowId).split(',');
			
			// 선택된 Row level 
			var level = partTreeGrid.getLevel(rowId);
			
			// 자식의 개수가 0이면 하위 리스트 검색
			if(child == 0){
				viewPartBomAction(rowId, rowId.split('#')[1], "false", '');
			}
			// 자식의 개수가 0이 아니면 현재가지고 있는 자식을 연다.
			else {
				partTreeGrid.openItem(rowId)
			}
			
		}
		
	})
	
	/*
	partTreeGrid.attachEvent("onOpenStart", function(rowId){
		alert(rowId);
		var parentOid = rowId; 
		//alert(parentOid);
		// 자식이 몇개 있는지 검사
		var child = partTreeGrid.hasChildren(parentOid);
		//alert(child);
		// 자식의 개수가 0이면 하위 리스트 검색
		if(child == 0){
			viewPartBomAction(parentOid, parentOid.split('#')[1], "false", '');
		}
	});
	*/
	
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
function viewPartBomAction(id, oid, isTopAssy, baseline){
	var url	= getURLString("part", "viewPartBomAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{
			oid : oid,
			isTopAssy : isTopAssy,
			parentId : id,
			desc : $('#desc').val(),
			baseline : baseline
		},
		dataType:"json",
		async: true,
		cache: false,
	
		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},
	
		success:function(data){
			addChildData(id, oid, data);
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
	    }
		,complete: function() {
			gfn_EndShowProcessing();
	    }
	});
}

window.addChildData = function(id, parentId, data){
	var level = 0;
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			// 부모의 level +1
			level = (partTreeGrid.getLevel(data[i].parentId) + 1);
			//if(duplicationCheck(data[i].parentId, data[i].number)) {
				partTreeGrid.addRow(rowId + '#' + data[i].oid, 
						[
						 '<a href=javascript:openView("' + data[i].oid + '")>' + data[i].number + '</a>',
						 data[i].dwgNo, 
						 level,
						 data[i].name,
						 data[i].state,
						 data[i].version,
						 data[i].specification,
						 data[i].unit,
						 data[i].quantity,
						 data[i].weight,
						 data[i].ecoNo,
						 data[i].deptcode
						], 
						0, data[i].parentId, data[i].partIcon, data[i].isChildren);
			//}
			partTreeGrid.openItem(data[i].parentId);
			rowId++;
		}
	}
}

window.duplicationCheck = function(parnetId, number) {
	var subItems = partTreeGrid.getSubItems(parnetId);
	var subItem = subItems.split(',');
	for(var i=0; i<subItem.length; i++){
	}
	return true;
}

</script>