<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

$(function() {
	$("#addDoc").click(function() {
		var row = $("#docBody > tr").length;
		var docTypeName = "docType"+row;
		var html = "";
		html += "<tr>";
		html += "	<td class='tdwhiteM'>";
		html += "		<input type='checkbox' name='docDelete'>";
		html += "	</td>";
		html += "	<td class='tdwhiteL'>";
		html += "		<input type='text' name='docName' maxlength='80' style='width:99%' />"
		html += "	</td>";
		html += "	<td class='tdwhiteM'>";
		html += "		<input type=file name='docFile' style='width:99%'>";
		html += "	</td>";
		
		<c:if test="${control }">
		html += "	<td class='tdwhiteM'>";
		html += "		<input type='checkbox' name='"+docTypeName+"' value='OraCadDoc' " + isPCB + " >";
		html += "	</td>";
		</c:if>
		
		html += "</tr>";
		
		$("#docBody").append(html);
		
	})
	
	<%----------------------------------------------------------
	*                      관련 문서 삭제
	----------------------------------------------------------%>
	$("#delDoc").click(function() {
		var obj = $("input[name='docDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
	})
})


</script>

<body>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >	
	<tr bgcolor="#ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>        
	        
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b><c:out value="${title }" /></b>
		</td>
	</tr>
	
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<tr>
					<td class="tdblueM" colspan="2" width="20%">
						<c:out value="${title }" /><span style="COLOR: red; display: none;" id="documentPath">*</span>
					</td>
					<td class="tdwhiteL" colspan="4">
			 			<table id="innerTempTable" style="display:none">
						    <tr>
						        <td class="tdwhiteM"></td>
						        <td class="tdwhiteM"></td>
						        <td class="tdwhiteM"></td>
						        <td class="tdwhiteM"></td>
						    </tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="left">
									<table border="0" cellpadding="0" cellspacing="2">
									
										<colgroup>
											<col width="10%">
											<col width="10%">
											<col width="80%">
										</colgroup>
									
										<tbody>
									
										<tr>
											<td>
												<button type="button" name="addDoc" id="addDoc" class="btnCustom">
													<span></span>
													${f:getMessage('추가')}
												</button>
											</td>
											
											<td>
												<button type="button" name="delDoc" id="delDoc" class="btnCustom">
													<span></span>
													${f:getMessage('삭제')}
												</button>
											</td>
											
											<td style="width: 100%">
												<jsp:include page="/eSolution/folder/include_FolderSelect.do">
													<jsp:param value="/Default/Document" name="root"/>
													<jsp:param value="docPath" name="paramName"/>
												</jsp:include>
											</td>
											
										</tr>
										
										</tbody>
									</table>
								</td>
							</tr>
							<tr>
								<td>
								<div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
									<table width="100%" cellspacing="0" cellpadding="1" border="0" id="docTable" align="center">
										<tbody>
											<tr>
												<td class="tdblueM"  width="5%"></td>
												<td class="tdblueM"  width="35%">${f:getMessage('제목')}&nbsp;<span style="COLOR: red;">*</span></td>
												<td class="tdblueM"  width="50%">${f:getMessage('첨부')}&nbsp;<span style="COLOR: red;">*</span></td>
												<c:if test="${control }">
													<td class="tdblueM0" width="5%">${f:getMessage('제어파일')}</td>
												</c:if>
											</tr>
											
											<tbody id="docBody">
											
											</tbody>
											
										</tbody>
									</table>
								</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>