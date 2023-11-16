<%@page contentType="text/html; charset=UTF-8"%>

<link rel="stylesheet" href="/Windchill/extcore/jsp/css/e3ps.css" type="text/css">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<script language="javascript" src="/Windchill/jsp/js/common.js"></script>

<script type="text/javascript">

$(function () {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		if(!validationCheck()){
			return;
		}
		if($.trim($("input[name='location']").val()) == '/Default/PART_Drawing') {
			alert("${f:getMessage('도면분류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		console.log("PRIMARY val --> ");
		console.log($("#PRIMARY").val());
		if($("#PRIMARY").length == 0 && $("#PRIMARY_delocIds").length == 0) {
			alert("${f:getMessage('주 첨부파일')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(!confirm("${f:getMessage('수정하시겠습니까?')}")){
			return;
		}
		
		gfn_StartShowProcessing();
		$("#drawingModifyForm").attr("action", getURLString("drawing", "updateDrawingAction", "do") + "?oid=" + $("#oid").val()).submit();
	})
	$("#testBtn").click(function () {
		console.log("PRIMARY val --> ");
		console.log($("#PRIMARY").val());
	})
})

<%----------------------------------------------------------
*                      필수값 체크하기
----------------------------------------------------------%>
function validationCheck(){
	var oldCadName = "${epmData.cadName }" ;
	var cadPath = $("#PRIMARY").val();
	if("" == cadPath || cadPath == "undefined" || cadPath == null){ return true; }
	
	var fileCheck = cadPath.substring(cadPath.indexOf(".")+1).toUpperCase();
	var n = cadPath.lastIndexOf(oldCadName);
	if(n < 0){
		alert("${f:getMessage('파일명이 동일해야 합니다.')}");
		return false;
	}
	
	return true;
}

</script>
<body>

<form name="drawingModifyForm" id="drawingModifyForm" method="post" enctype="multipart/form-data">

<input type="hidden"	name="oid"	id="oid"	value="<c:out value="${epmData.oid }" />" >

<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
        <td valign="top" style="padding:0px 0px 0px 0px">
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color="white">${f:getMessage('도면')} ${f:getMessage('수정')}</font></B></td>
		   		</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
				<tr>
					<td colspan="2"><img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width="10" height="9" />&nbsp;${f:getMessage('도면')} ${f:getMessage('수정')}</td>
					<td colspan="3">
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			               		<td>
									<button type="button" name="testBtn" id="testBtn" class="btnCRUD">
										<span></span>
										${f:getMessage('test')}
									</button>
								</td>
			                    <td>
									<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
										<span></span>
										${f:getMessage('수정')}
									</button>
								</td>
								
								<td>
									<button type="button" name="approveBtn" id="approveBtn" class="btnCustom" onclick="javascript:history.back();">
										<span></span>
										${f:getMessage('이전페이지')}
									</button>
								</td>
							</tr>
			            </table>
					</td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
					<tr><td height="1" width="100%"></td></tr>
			</table>						            


            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	            <tr>
	            	<td width="15%"></td>
	            	<td width="35%"></td>
	            	<td width="15%"></td>
	            	<td width="35%"></td>
	            </tr>

	            <tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
							${f:getMessage('도면분류')}
							<span style="color: red;">*</span>
					</td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/folder/include_FolderSelect.do">
							<jsp:param value="/Default/PART_Drawing" name="root"/>
							<jsp:param value="/Default${epmData.getLocation() }" name="folder"/>
						</jsp:include>
					</td>
				</tr>
	           
                <tr>
                	<td class="tdblueM">
                		${f:getMessage('번호')}
                	</td>
                	
                    <td class="tdwhiteL">
                    	<c:out value="${epmData.number }" />
                    </td>
                   
                    <td class="tdblueM" >
                    	${f:getMessage('도면명')}
                    </td>
                    
                    <td class="tdwhiteL">
                    	<c:out value="${epmData.name }" />
                    	<input type="hidden" name="pdmName" id="pdmName" value="<c:out value="${epmData.name }" />">
                    </td>
                </tr>
               
             	<c:choose>
	               	<c:when test="${!epmData.wgm && epmData.part != '' }">
						 <tr bgcolor="ffffff" height=35>
							<td class="tdblueM">
								${f:getMessage('주 첨부파일')}
								<span style="color: red;">*</span>
							</td>
							
							<td class="tdwhiteL" colspan="3">
								<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
									<jsp:param name="formId" value="drawingModifyForm"/>
									<jsp:param name="type" value="PRIMARY"/>
									<jsp:param name="oid" value="${epmData.oid }"/>
								</jsp:include>
							</td>
						</tr>
	               	</c:when>
	               	<c:otherwise>
						<tr bgcolor="ffffff" height=35>
							<td class="tdblueM">
								${f:getMessage('주 첨부파일')}
								<span style="COLOR: red;">*</span>
							</td>
							
							<td class="tdwhiteL" colspan="3">
								<jsp:include page="/eSolution/content/includeAttachFileView">
									<jsp:param value="p" name="type"/>
									<jsp:param value="${epmData.oid }" name="oid"/>
								</jsp:include>
							</td>
						</tr>
	               	</c:otherwise>
                </c:choose>
                
				<tr bgcolor="ffffff" height="35">
                    <td class="tdblueM">
                    	${f:getMessage('도면')}${f:getMessage('설명')}
                    </td>
                    
                    <td class="tdwhiteL" colspan="3">
                       <div class="textarea_autoSize">
							<textarea name="description" id="description" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('설명')}')"><c:out value="${epmData.getDescription() }" escapeXml="false"/></textarea>
						</div>
                    </td>
                </tr>

				<%-- <tr>
					<td class="tdblueM">${f:getMessage('수정사유')}</td>
					<td class="tdwhiteL" colspan="3">
					<textarea name="iterationNote" cols="80" rows="2" class="fm_area" style="width:96%" onchange="textAreaLengthCheckName('iterationNote', '100', '${f:getMessage('수정사유')}')"></textarea>
					</td>
				</tr> --%>
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="drawingModifyForm"/>
							<jsp:param name="oid" value="${epmData.oid }"/>
						</jsp:include>
					</td>
				</tr>
            </table>
		</td>
	</tr>
</table>
     
<!-- 관련 품목 -->
<jsp:include page="/eSolution/part/include_PartSelect.do" flush="false" >
	<jsp:param name="moduleType" value="drawing"/>
	<jsp:param name="title" value="${f:getMessage('관련 품목')}"/>
	<jsp:param name="oid" value="${epmData.oid }"/>
	<jsp:param name="paramName" value="partOid"/>
</jsp:include>
				
				
				

</form>

</body>
</html>