<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
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
})

$(function() {
	$("#excelFile").change(function() {
		var file = $("#excelFile").val();
		if(file != "") {
			var ext1 = file.substring(file.length-3, file.length);
			var ext2 = file.substring(file.length-4, file.length);
			if(ext1 != "xls" && ext2 != "xlsx") {
				alert("Excel${f:getMessage('만 등록 가능합니다.')}");
				$("#excelFile").val("");
			}
		}
	})
	$("#saveExcel").click(function() {
		
		if($.trim($("#fid").val()) == "") {
			alert("${f:getMessage('품목분류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($("#excelFile").val() == "") {
			alert("Excel ${f:getMessage('파일을 선택하세요.')}");
			return;
		}
		
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			gfn_StartShowProcessing();
			$("#createPackagePart").attr("target", "list");
			$("#createPackagePart").attr("action", getURLString("part", "createPackagePartAction", "do")).submit();
		}
	})
	$("#download").click(function() {
		location="/Windchill/jsp/part/createPartPackage.xlsx";
	})
})

window.closeProcessing = function() {
	gfn_EndShowProcessing();
}
</script>

<body>

<form name="createPackagePart" id="createPackagePart" method="post" enctype="multipart/form-data">

<input type="hidden" name="fid"				id="fid" 			value="">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							${f:getMessage('제품')}/${f:getMessage('품목')}
							${f:getMessage('관리')}
							>
							${f:getMessage('제품')}/${f:getMessage('품목')}
							${f:getMessage('일괄등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr  align=center>
        <td valign="top" style="padding:0px 0px 0px 0px">
        	
            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
                <tr><td height=1 width=100%></td></tr>
            </table>
            
            <table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
            <col width='15%'><col width='35%'><col width='15%'><col width='35%'>
            
	            <tr>
			        <td class="tdblueM">
			        	${f:getMessage('품목분류')}
			        </td>
			        
			        <td class="tdwhiteL">
			        	<b>
			        		<span id="locationName">
			        			/Default/PART_Drawing
			        		</span>
			        	</b>
			        </td>
			    </tr>
                <tr bgcolor="ffffff" style="height: 50px;">
	                <td class="tdblueM">
		                Excel ${f:getMessage('업로드')}
		                <span style="COLOR: red;">*</span>
		                
	                	<table border="0" cellpadding="0" cellspacing="0" align=center>
               			 	<tr>
                				<td>
                					<button type="button" class="btnCustom" id="download">
                						<span></span>
                						${f:getMessage('양식다운')}
                					</button>
                				</td>
                		 	</tr>
                		</table>
	                </td>
	                
	                <td class="tdwhiteL" >
		                <input type="file" name="excelFile" id="excelFile" class="txt_field" size="90" border="0">&nbsp;
		                
		                <button type="button" class="btnCRUD" id="saveExcel">
		                	<span></span>
		                	${f:getMessage('등록')}
		                </button>
	                </td>
	            </tr>
	            
	            <tr bgcolor="ffffff">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="createPackagePart"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="btnId" value="saveExcel" />
						</jsp:include>
					</td>
				</tr>
				
       		</table>
        </td>
    </tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr bgcolor="#ffffff">
        <td>
            <iframe src="" id="list" name="list" frameborder="0" width="100%" height="500" scrolling="auto">
            </iframe>
        </td>
    </tr>
</table>	

</form>

</body>
</html>