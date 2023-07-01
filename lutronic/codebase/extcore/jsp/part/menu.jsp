<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<html>
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	var menu = $("#menu").val();
	
	$("#"+menu).css('background', '#ECECEC');
});

<%----------------------------------------------------------
*                      페이지 이동
----------------------------------------------------------%>
function gotoMenu(a, createType){
	var url = getURLString("part", a, "do");
	document.location = url;
}

<%----------------------------------------------------------
*                      폴더 선택시
----------------------------------------------------------%>
function setLocationDocument(foid,loc,isLast){
	$("#locationName").html(loc);
	$("#fid").val(foid);
	if($("#search").val() == "true") {
		$("#sortValue").val("");
		$("#sortCheck").val("");
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	}
}

function openBOMEditor() {
	var pdmOid;
	var url	= getURLString("common", "getPDMLinkProductOid", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,
		error: function(data) {
		},
		success:function(data){
			pdmOid = data;
		}
	});
	
	var str = "/Windchill/netmarkets/jsp/explorer/installmsg.jsp?message=ok&containerId="
			+ "wt.pdmlink.PDMLinkProduct:48155" +
			"&applet=com.ptc.windchill.explorer.structureexplorer.StructureExplorerApplet&jars=ptcAnnotator.jar,lib/pview.jar,lib/json.jar&appId=ptc.pdm.ProductStructureExplorer&launchEmpty=false&explorerName=%EC%A0%9C%ED%92%88+%EA%B5%AC%EC%A1%B0+%ED%83%90%EC%83%89%EA%B8%B0&ncid=";
	var name = "BOMEditor2";
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	leftpos = (screen.width - 1000)/ 2;
	toppos = (screen.height - 600) / 2 ;
	rest = "width=1300,height=600,left=" + leftpos + ',top=' + toppos;
	var newwin = window.open( str , name, opts+rest);
	newwin.focus();
	
}

</script>



<body>
<form name="menuForm" method="post" action="">
<input type="hidden" name="menu" id="menu" value="<c:out value="${menu }" />">

	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
	
		<tr>
			<td align="center" valign="top">
				<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
					<tr>
						<td align="left" valign=top height=42>
							<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
								<tr>
									<td></td>
									<td>
										<img src="/Windchill/jsp/portal/img/ars_bt_01.gif" width="11" height="11">
										<span id ="menuTitle">
											${f:getMessage('품목')}
											${f:getMessage('관리')}
										</span>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				
					<tr>
						<td align="left" valign="top" width=100% style="padding-right:2;padding-left:2">
							<table border="0" cellpadding="0" cellspacing="0" width="100%" class=menu>
							
								<tr id="menu1" height="20" style="padding-left:10" bgcolor="white">
									<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('listPart')">
										<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">
										${f:getMessage('제품')}/${f:getMessage('품목')}
										${f:getMessage('검색')}
									</td>
								</tr>
								
								<tr id="menu2" height="20" style="padding-left:10" bgcolor="white">
									<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('createPart')">
										<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">
										${f:getMessage('제품')}/${f:getMessage('품목')}
										${f:getMessage('등록')}
									</td>
								</tr>
								
								<tr id="menu3" height="20" style="padding-left:10" bgcolor="white">
									<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('createAUIPackagePart')">
										<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">
										${f:getMessage('제품')}/${f:getMessage('품목')}
										${f:getMessage('일괄등록')}
									</td>
								</tr>
								
								<tr height="20" style="padding-left:10" bgcolor="white">
									<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:openBOMEditor()">
										<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">
										BOM EDITOR
									</td>
								</tr>
								
								<tr>
									<td colspan="2"><hr></td>
								</tr>
						    </table>
						
							<div style="width:100%;overflow-x:hidden;overflow-y:auto;border:0px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:0px 0px;">
								<div id="scrollBox" style="OVERFLOW-Y:auto;OVERFLOW-X:auto; width:240; height:510" onscroll="true">
								    <jsp:include page="/eSolution/folder/treeFolder.do">
										<jsp:param value="/Default/PART_Drawing" name="folder"/>
									</jsp:include>
								</div>
							</div>
						</td>
					</tr>
					
				</table>
			</td>
		</tr>
	</table>
</form>
</body>
</html>