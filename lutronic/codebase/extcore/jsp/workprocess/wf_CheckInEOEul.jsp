<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
var oid = "<c:out value='${eoOid}' />";
$(function() {
	<%----------------------------------------------------------
	*                      품목 추가 버튼
	----------------------------------------------------------%>
	$("#addPart").click(function () {
		var url = getURLString("change", "AddECOPart", "do") + "?oid="+$("#eoOid").val();
		openOtherName(url,"window","1180","880","status=no,scrollbars=yes,resizable=yes");
		
	})
	
	$("#createEul").click(function() {
		var eulLength = $("input[name='check']").length;
		var vv = "";
		for(var i=0; i < eulLength; i++) {
			$("input[name='check']").each(function() {
				if(this.check) {
					vv = this.value;
				}
			})
		}
		var str="/Windchill/jsp/change/EulBEditorCnt.jsp?oid="+$("#eoOid").val()+"&eul="+vv;
		//var str="/Windchill/jsp/part/editor/BomEditorCnt.jsp?oid="+oid+"&eul="+vv;
		//var str="/Windchill/jsp/change/BomEditorCnt.jsp?oid="+oid+"&eul="+vv;
		openWindow(str, 'editor', 280, 360);
	})
	
	$("#revisionBtn").click(function() {
		if($("input[name='revisableOid']:checked").length == 0 ){
			alert("${f:getMessage('수정 대상')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		var arr = new Array();
		$("input[name='revisableOid']").each(function() {
			if(this.checked) {
				arr.push(this.value);
			}
		})
		var url = getURLString("changeECO", "batchCheckInOut", "do") + "?ecoOid="+$("#eoOid").val()+"&revisableOid="+arr;
		openOtherName(url,"window","1180","880","status=no,scrollbars=yes,resizable=yes");
	})
	
	$("#chkAll").click(function() {
		var checked = this.checked;
		$("input[name='revisableOid']").each(function() {
			this.checked = checked;
		})
	})
})

function reload() {
	location.reload();	
}

function lfn_BatchRevision() {
	   // 해당기능 개발여부 확인필요
	   //openWindow("/Windchill/jsp/change/eco/BatchRevision.jsp?"+param, "BatchRevision", '800', '600');
	   var newWin = window.open('','BatchRevision','toolbar=no,location=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,top=100,left=100,width=1024,height=768');


	   form = document.forms("apprvoalForm");
	   form.method = "post";
	   form.target = "BatchRevision";
	   //form.target = newWin.name;
	   form.action = getURLString("changeECO", "batchRevision", "do");
	   form.submit();

	   newWin.focus();
	}
</script>

<body>

<input type="hidden" name="eoOid" id="eoOid" value="<c:out value="${eoOid }"/>">
<table width="100%" border="0" cellpadding="1" cellspacing="0" align="center" style="padding-bottom:5px">
	<tr> 
		<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr bgcolor="ffffff" height="5">
		<td>
<!------------------------------------------------------------ 일괄개정 START------------------------------------------------------------------------------------->		
			<table width="100%" border="0" cellpadding="0" cellspacing="4" >
				<tr>
					<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>${f:getMessage('품목목록')}</b></td>
					<td align="right" colspan="2">
						<button type="button" class="btnCustom" title="일괄수정" id="revisionBtn" name="revisionBtn">
		                 	<span></span>
		                 	${f:getMessage('일괄수정')}
	                	</button>
						<button type="button" class="btnCustom" title="품목추가" id="addPart" name="addPart">
		                 	<span></span>
		                 	${f:getMessage('품목추가')}
	                	</button>
						<button type="button" class="btnCustom" title="v" id="reflash" name="reflash" onclick="location.reload();">
		                 	<span></span>
		                 	${f:getMessage('새로고침')}
	                	</button>
					</td>
				</tr>	
			</table>

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr>
		  			<td height="1" width="100%"></td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="table-layout:fixed">
				<col width="5%"><col width="5%"><col width="10%"><col width="14%"><col width="5%"><col width="5%"><col width="10%"><col width="10%">
				<col width="12%"><col width="12%"><col width="12%">
				
				<tr bgcolor="9acd32" height="25">
					<td class="tdblueM"><input name="chkAll" id="chkAll" type="checkbox" class="Checkbox" ></td>
					<td class="tdblueM">${f:getMessage('번호')}</td>
					<td class="tdblueM">${f:getMessage('품목번호')}</td>
					<td class="tdblueM">${f:getMessage('품명')}</td>
					<td class="tdblueM">Rev.</td>
					<td class="tdblueM" >${f:getMessage('등록자')}</td>
					<td class="tdblueM">${f:getMessage('상태')}</td>
					<td class="tdblueM">BOM</td>
					<td class="tdblueM">${f:getMessage('주도면')}</td>
					<td class="tdblueM">${f:getMessage('참조항목')}</td>
					<td class="tdblueM">${f:getMessage('관련도면')}</td>
				</tr>
				
				<tbody id="ecaTableBody">
					<c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list}" var="partData"  varStatus="status">
								<tr>
									<td class="tdwhiteM">
										<c:choose>
											<c:when test="${partData.revisable eq 'true' }">
												<input name="revisableOid" id="revisableOid" type="checkbox" class="Checkbox" value = "${partData.linkOid }">
											</c:when>
											<c:otherwise>&nbsp;</c:otherwise>
										</c:choose>
									</td>
									<td class="tdwhiteM" >
										<c:out value="${partData.idxPart }" />
									</td>
									<td class="tdwhiteM" >
										<c:out value="${partData.number }" />
									</td>
									<td class="tdwhiteM" >
										<a href="javascript:openView('${partData.partOid }')">
											<c:out value="${partData.name }" />
										</a>
									</td>
									<td class="tdwhiteM" >
										<c:out value="${partData.version }" />.<c:out value="${partData.iteration }"/>
									</td>
									<td class="tdwhiteM" ><c:out value="${partData.userCreatorName }" /></td>
									<td class="tdwhiteM" >
										<c:out value="${partData.state }" />
									</td>
									<td class="tdwhiteM" >
										<c:out value="${partData.BOMCheck }" escapeXml="false"/>
									</td>
									
									<td class="tdwhiteM" >
										<c:out value="${partData.epm3dNumber }" />
									</td>
									
									<td class="tdwhiteM" >
										<c:forEach items="${partData.list}" var="AA">
											<c:out value="${AA.epm2Number }"></c:out>
										</c:forEach>
									</td>
									<td class="tdwhiteM">
										<c:forEach items="${partData.descEpm2 }" var="descEpm"	varStatus="i">
											<c:out value="${descEpm }" />
											<c:if test="${!i.last}">
												<br>
											</c:if>
										</c:forEach>
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
			           		 	<td class="tdwhiteM" colspan="12" width="100%" align="center" id="noList">검색 결과가 없습니다.</td>
			            	</tr>	
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
			<!-- 활동 현황 -->
			<%-- <jsp:include page="/eSolution/changeECO/include_EulList.do" flush="false" /> --%>
		</td>
	</tr>
</table>

</body>
</html>