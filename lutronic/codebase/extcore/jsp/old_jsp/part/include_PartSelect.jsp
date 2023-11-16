<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">

var partOid = "<c:out value='${paramName}' />";

$(function() {
	<%----------------------------------------------------------
	*                      품목 추가 버튼
	----------------------------------------------------------%>
	$("#addBtn").click(function () {
		if($("#maxcnt").val() != "" ) {
			if($("#partTable tr").length > $("#maxcnt").val()) {
				alert("${f:getMessage('품목을 하나이상 추가할 수 없습니다.')}");
				return;
			}
		}
		var url = getURLString("part", "selectPartPopup", "do") + "?mode="+$("#mode").val() +"&moduleType="+$("#moduleType").val() +"&state="+$("#state").val();
		openOtherName(url,"window","1180","600","status=no,scrollbars=yes,resizable=yes");
		//attache = window.showModalDialog(url,window,"help=no; scroll=yes; resizable=yes; dialogWidth=1180px; dialogHeight:880px; center:yes");
	}),
	<%----------------------------------------------------------
	*                      품목 삭제 버튼
	----------------------------------------------------------%>
	$("#delBtn").click(function () {
		var obj = $("input[name='partDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
		$("#partcheck").prop("checked", "");
	})
	
	$("#partcheck").click(function() {
		if(this.checked) {
			$("input[name='partDelete']").prop("checked", "checked");
		}else {
			$("input[name='partDelete']").prop("checked", "");
		}
	})
	
	$("#eoPart").click(function() {
		 var url = getURLString("part", "selectEOPart", "do");
		openOtherName(url,"window","1180","880","status=no,scrollbars=yes,resizable=yes");
	})
})

$(document).on('click', 'input:button[name="bom"]', function(){
	//var url = getURLString("part", "partExpand", "do") + "?partOid=" + this.id + "&moduleType="+$("#moduleType").val();
	var url = getURLString("part", "partAUIExpand", "do") + "?partOid=" + this.id + "&moduleType="+$("#moduleType").val();
	openOtherName(url,"openBom","1180","880","status=no,scrollbars=yes,resizable=yes");
})

<%----------------------------------------------------------
*                      선택 품목 데이터 추가
----------------------------------------------------------%>
function addPart(obj,isHref) {
	for (var i = 0; i < obj.length; i++) {
		if(PartDuplicateCheck(obj[i][1])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='partDelete' id='partDelete'>";
			html += "		<input type='hidden' name='partOid' id='partOid' value='" + obj[i][0] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='hidden' name='number' id='number' value='" + obj[i][1] + "' />";
			html += obj[i][1];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			
			if(isHref) {
				html += "<a href=javascript:openView('" + obj[i][0] + "')>";
				html += obj[i][2];
				html += "</a>";
			}else {
				html += obj[i][2];
			}
			
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += (obj[i][3]+"."+obj[i][4]);
			html += "	</td>";
			
			if("<c:out value='${isBom}'/>" == "true") {
				html += "	<td class='tdwhiteM'>";
				html += "		<input type='button' name='bom' id='" + obj[i][0]+ "' onclick=javascript:viewBom('" + obj[i][0]+ "') value='BOM'>";
				html += "	</td>";
			}
			/*
			if("<c:out value='${selectBom}'/>" == "true") {
				html += "	<td class='tdwhiteM'>";
				html += "		<input type='checkbox' name='isSelectBom' id='" + obj[i][0] + "' value='" + obj[i][1] + "'>";
				html += "	</td>";
			}
			*/
			html += "</tr>";
			$("#partTable").append(html);
		}
	}
}

<%----------------------------------------------------------
*                      선택 품목 데이터 추가
----------------------------------------------------------%>
function addPart2(obj,isHref) {
	for (var i = 0; i < obj.length; i++) {
		if(PartDuplicateCheck(obj[i][1])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='partDelete' id='partDelete'>";
			html += "		<input type='hidden' name='partOid' id='partOid' value='" + obj[i][0] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='hidden' name='number' id='number' value='" + obj[i][1] + "' />";
			html += obj[i][1];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			
			if(isHref) {
				html += "<a href=javascript:openView('" + obj[i][0] + "')>";
				html += obj[i][2];
				html += "</a>";
			}else {
				html += obj[i][2];
			}
			
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += (obj[i][3]+"."+obj[i][4]);
			html += "	</td>";
			
			if("<c:out value='${isBom}'/>" == "true") {
				html += "	<td class='tdwhiteM'>";
				html += "		<input type='button' name='bom' id='" + obj[i][0]+ "' onclick=javascript:viewBom('" + obj[i][0]+ "') value='BOM'>";
				html += "	</td>";
			}
			/*
			if("<c:out value='${selectBom}'/>" == "true") {
				html += "	<td class='tdwhiteM'>";
				html += "		<input type='checkbox' name='isSelectBom' id='" + obj[i][0] + "' value='" + obj[i][1] + "'>";
				html += "	</td>";
			}
			*/
			html += "</tr>";
			$("#partTable").append(html);
		}
	}
}
<%----------------------------------------------------------
*                      선택 품목 데이터 중복 검사
----------------------------------------------------------%>
function PartDuplicateCheck(number) {
	console.log("PartDuplicateCheck = ");
	var moduleType = $("#moduleType").val();
	if(moduleType == "ECO"){
		if(productGroupCheck(number)){
			alert("제품군 부품은 ECO 대상이 아닙니다.")
			return false;
		}
	}
	
	var obj = $("input[name='number']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		console.log("number = " + obj.eq(i).val());
		if(obj.eq(i).val() == number) {
			return false;
		}
	}
	return true;
}

</script>


<body>

<input type="hidden"   	name="moduleType" 	id="moduleType"    value="<c:out value="${moduleType }" />" />
<input type="hidden" 	name="mode"      	id="mode"		   value="<c:out value="${mode }" />" />
<input type="hidden"	name="maxcnt"		id="maxcnt"		   value="<c:out value="${maxcnt }" />" />
<input type="hidden"	name="state"		id="state"		   value="<c:out value="${state }"/>" />

<c:if test="${form ne 'simple' }">

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>  
	              
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
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
			
</c:if>

			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<c:if test="${form ne 'simple' }">
				<tr>
					<td width="150"></td>
					<td width="350"></td>
					<td width="150"></td>
					<td width="350"></td>
				</tr>
				</c:if>
				<tr>
					<c:if test="${form ne 'simple' }">
						<td class="tdblueM"  >
							<c:out value="${title }" />
						</td>
					</c:if>
					
					<td class="tdwhiteL" colspan="3">

						<table border="0" cellpadding="0" cellspacing="2">
						    <tr>
						        <td>
						        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('추가')}" id="addBtn" name="addBtn">
						            	<span></span>
						            	${f:getMessage('추가')}
						           	</button>
						        </td>
						        <td>
						        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('삭제')}" id="delBtn" name="delBtn">
						            	<span></span>
						            	${f:getMessage('삭제')}
						           	</button>
						           	
						        </td>
						        
					           	<c:if test="${moduleType eq 'distribute' }">
					           		<td>
										<button type="button" class="btnCustom" id="eoPart">
											<span></span>
											EO Part
										</button>						           		
					           		</td>
					           	</c:if>
						    </tr>
						</table>
						
						<table width="99%" cellspacing="0" cellpadding="1" border="0" id="partTable" align="left">
						    <tbody>
						        <tr>
						        	<td class="tdblueM" width="5%"><input type="checkbox" name="partcheck" id="partcheck" ></td>
						            <td class="tdblueM" width="25%">${f:getMessage('품목번호')}</td>
						            <td class="tdblueM" width="30%">${f:getMessage('품목명')}</td>
						            <td class="tdblueM" width="10%">${f:getMessage('Rev.')} </td>
						            
						            <c:if test="${isBom }">
						            	<td class="tdblueM" width="20%">BOM</td>
						            </c:if>
						            <!-- 
						            <c:if test="${selectBom }">
						            	<td class="tdblueM" width="10%">Balseline</td>
						            </c:if>
						             -->
						            
						        </tr>
						        <c:choose>
									<c:when test="${fn:length(list) != 0 }">
										<c:forEach items="${list}" var="partData" varStatus="status" >
											<tr>
												<td class="tdwhiteM" width="5%">
													<input type='checkbox' name='partDelete' id='partDelete'>
													<input type='hidden' name='<c:out value="${paramName}" />' id='partOid' value="<c:out value="${partData.oid }" />" />
												</td>
												
												<td class="tdwhiteM" width="25%">
													<c:out value="${partData.number }" />
													<input type='hidden' name='number' id='number' value="<c:out value="${partData.number }" />">
												</td>
												
												<td class="tdwhiteM" width="30%">
													<a href="javascript:openView('<c:out value="${partData.oid }" />')">
														<c:out value="${partData.name }" />
													</a>
												</td>
												
												<td class="tdwhiteM" width="10%">
													<c:out value="${partData.version }" />.<c:out value="${partData.iteration }" />
												</td>
												
												<c:if test="${isBom }">
													<td class="tdwhiteM" >
														<input type="button" name="bom" id="<c:out value="${partData.oid }"/>" value="BOM" >
													</td>
												</c:if>
												<!--  
												<c:if test="${selectBom }">
													<td class="tdwhiteM">
														<input type="checkbox" name="isSelectBom" <c:out value="${partData.getBaseline() }"/> value="<c:out value="${partData.number }" />">
													</td>
												</c:if>
												-->
											</tr>
										</c:forEach>
									</c:when>
								</c:choose>
						    </tbody>
						</table>
					</td>
				</tr>
			</table>
			
<c:if test="${form ne 'simple' }">

		</td>
	</tr>
</table>
</c:if>

</body>
</html>