<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
<!--
#baselineCompare table td img{
	float:left;
	margin-top:5px;
}
-->
</style>
<script type="text/javascript">
$(function() {
	$("#viewBom").click(function() {
		//document.location = getURLString("part", "PartTree", "do") + "?oid="+$("#oid").val();
		history.back();
	})
	$("#viewPart").click(function() {
		openView($("#oid").val());
	})
});

<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	lfn_DhtmlxGridInit();
	var param = $("form[name=BomCompareForm]").serialize();
	var url	= getURLString("part", "getBaseLineCompare", "do") + "?";
	BaseLineGrid.clearAndLoad(url+param, null, "json");
	
	BaseLineGrid.enableAutoHeight(true, parseInt($(window).height())-100, true);

	  $(window).resize(function() { // 브라우저 사이즈 감지
		  BaseLineGrid.enableAutoHeight(true, parseInt($(window).height())-100, true);
	  })
	
});

<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
function lfn_DhtmlxGridInit(){
	var COLNAMEARR = {
			col01 : 'level' 
			,col02 : '부품번호'
			,col03 : '부품명'
			,col04 : 'Rev.'
			,col05 : '단위'
			,col06 : '수량'
			//,col07 : '품목번호'  6 ,5
			//,col08 : '기준 수량'  7 ,6
			,col09 : '&nbsp;'
			,col10 : 'level'
			,col11 : '부품번호'
			,col12 : '부품명'
			,col13 : 'Rev.'
			,col14 : '단위'
			,col15 : '수량'
	}
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	sHeader += "," + COLNAMEARR.col05;
	sHeader += "," + COLNAMEARR.col06;
	//sHeader += "," + COLNAMEARR.col07;
	//sHeader += "," + COLNAMEARR.col08;
	sHeader += "," + COLNAMEARR.col09;
	sHeader += "," + COLNAMEARR.col10;
	sHeader += "," + COLNAMEARR.col11;
	sHeader += "," + COLNAMEARR.col12;
	sHeader += "," + COLNAMEARR.col13;
	sHeader += "," + COLNAMEARR.col14;
	sHeader += "," + COLNAMEARR.col15;
	
	window.console.log(sHeader);
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";
	sHeaderAlign[6]  = "text-align:center;";
	//sHeaderAlign[7]  = "text-align:center;";
	//sHeaderAlign[8]  = "text-align:center;";
	sHeaderAlign[9]  = "text-align:center;";
	sHeaderAlign[10]  = "text-align:center;";
	sHeaderAlign[11]  = "text-align:center;";
	sHeaderAlign[12]  = "text-align:center;";
	sHeaderAlign[13]  = "text-align:center;";
	sHeaderAlign[14]  = "text-align:center;";
	sHeaderAlign[15]  = "text-align:center;";
	var sWidth = "";
	sWidth += "3";
	sWidth += ",20";
	sWidth += ",20";
	sWidth += ",3";
	sWidth += ",3";
	sWidth += ",3";
	//sWidth += ",7";6
	//sWidth += ",6";7
	sWidth += ",2";
	sWidth += ",3";
	sWidth += ",20";
	sWidth += ",20";
	sWidth += ",3";
	sWidth += ",3";
	sWidth += ",3";
	
	var sColAlign = "";
	sColAlign += "left";
	sColAlign += ",left";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",center";
	//sColAlign += ",left"; 6
	//sColAlign += ",center";7
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",left";
	sColAlign += ",left";
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
	//sColType += ",ro";6
	//sColType += ",ro";7
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
	//sColSorting += ",na";6
	//sColSorting += ",na";7
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	
	BaseLineGrid = new dhtmlXGridObject('baselineCompare');
	BaseLineGrid.setImagePath("/Windchill/jsp/js/dhtmlx/imgs/");
	BaseLineGrid.setIconsPath("/Windchill");
	
	BaseLineGrid.setHeader("${title1 },#cspan,#cspan,#cspan,#cspan,&nbsp;,${title2},#cspan,#cspan,#cspan,#cspan",null,sHeaderAlign);
	//BaseLineGrid.attachHeader(["부품번호","부품명","Rev.","단위","수량","품목번호","기준 수량","&nbsp;","부품번호","부품명","버전","단위","수량"]);
	BaseLineGrid.attachHeader(["Level","부품번호","부품명","Rev.","단위","수량","&nbsp;","Level","부품번호","부품명","Rev.","단위","수량"]);

	//BaseLineGrid.setHeader(sHeader,null,sHeaderAlign);
	//BaseLineGrid.enableAutoHeight(true);
	BaseLineGrid.enableAutoHeight(true, parseInt($(window).height())-100, true);
	BaseLineGrid.setInitWidthsP(sWidth);
	BaseLineGrid.setColAlign(sColAlign);
	BaseLineGrid.setColTypes(sColType);
	BaseLineGrid.setColSorting(sColSorting);
	/*
	BaseLineGrid.attachEvent("onDataReady",function(){
    	var ids = BaseLineGrid.getAllRowIds().split(",");
    	
    	for( var i = 0; i < ids.length; i++ ){
    		var bgcolor = BaseLineGrid.getUserData(ids[i],"bgcolor");
    		//alert(bgcolor);
    		BaseLineGrid.setRowTextStyle(ids[i], "background-color: " + bgcolor + ";");
    	}
    });
	*/
	/*grid text copy option*/
    BaseLineGrid.enableBlockSelection();
    BaseLineGrid.forceLabelSelection(true);
    BaseLineGrid.attachEvent("onKeyPress",function onKeyPressed(code,ctrl,shift){
        if(code==67&&ctrl){
            if(!BaseLineGrid._selectionArea) return alert("return");
            BaseLineGrid.setCSVDelimiter("\t");
            BaseLineGrid.copyBlockToClipboard();
	    }
	    return;
	});
    BaseLineGrid.attachEvent("onEditCell", function(){
    	return false;
    });
    
	BaseLineGrid.init();
	
}
</script>
<form name=BomCompareForm id=BomCompareForm  method=post >
<input type=hidden name=oid id=oid value="<c:out value='${oid }'/>">
<input type=hidden name=oid2 id=oid2 value="<c:out value='${oid2 }'/>">
<input type=hidden name=baseline id=baseline value="<c:out value='${baseline }'/>">
<input type=hidden name=baseline2 id=baseline2 value="<c:out value='${baseline2 }'/>">

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
	    
		    <table width="100%"  border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	    		<tr>
	    			<td height=1 width=100%></td>
	    		</tr>
			</table>
		    
		    <table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  style="table-layout:fixed">
			    <tr height="30">
				    <td>
				    </td>
				    <td align=right>
		                <table border="0" cellpadding="0" cellspacing="4" align="right">
		                    <tr>
		                    	<td><font color="red">red</font> : 삭제,
		                    	<font color="blue">blue</font> : 추가,
		                    	<font color="#FFD700">gold</font> :변경(수량,단위)
		                    	</td>
		                        <td> 
		                        	<button type="button" class="btnCustom" id="viewBom">
		                        		<span></span>
		                        		BOM보기
		                        	</button>
		                        </td>
		                        
		                        <td>
		                        	<button type="button" class="btnCustom" id="viewPart">
		                        		<span></span>
		                        		상세정보
		                        	</button>
		                        </td>
		                        
		                        <td>
		                        	<button type="button" class="btnClose" onclick="self.close();">
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
			<div id="baselineCompare" style="width:100%;"></div>
		</td>
	</tr>
</table>

</form>
