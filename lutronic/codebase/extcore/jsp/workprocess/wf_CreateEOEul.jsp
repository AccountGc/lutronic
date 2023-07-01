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
		openWindow(str, 'editor', 280, 360);
	})
	
	$("#revisionBtn").click(function() {
		if($("input[name='revisableOid']:checked").length == 0 ){
			alert("${f:getMessage('개정 대상')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		var arr = new Array();
		$("input[name='revisableOid']").each(function() {
			if(this.checked) {
				arr.push(this.value);
			}
		})
		var url = getURLString("changeECO", "batchRevision", "do") + "?ecoOid="+$("#eoOid").val()+"&revisableOid="+arr;
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
	   var newWin = window.open('','BatchRevision','toolbar=no,location=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,top=100,left=100,width=1024,height=768');


	   form = document.forms("apprvoalForm");
	   form.method = "post";
	   form.target = "BatchRevision";
	   form.action = getURLString("changeECO", "batchRevision", "do");
	   form.submit();

	   newWin.focus();
	}

function bomEditor(pdmOid,vrOid) {
	
	var str = "/Windchill/netmarkets/jsp/explorer/installmsg.jsp?message=ok"
			  + "&oid=" + vrOid +
			  "&containerId=" + pdmOid + "&applet=com.ptc.windchill.explorer.structureexplorer.StructureExplorerApplet&jars=ptcAnnotator.jar,lib/pview.jar,lib/json.jar&appId=ptc.pdm.ProductStructureExplorer&launchEmpty=false&explorerName=%EC%A0%9C%ED%92%88+%EA%B5%AC%EC%A1%B0+%ED%83%90%EC%83%89%EA%B8%B0&ncid="
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "BOMEditor", opts+rest);
    newwin.focus();
}

function ecaBomCompare(oid){
	
	var str = "/Windchill/netmarkets/jsp/structureCompare/StructureCompare.jsp?oid="+oid+"&ncId=2374138740478986248&locale=ko"
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	leftpos = (screen.width - 1000)/ 2;
	toppos = (screen.height - 600) / 2 ;
	rest = "width=1300,height=600,left=" + leftpos + ',top=' + toppos;
	var newwin = window.open( str , "compareBOM", opts+rest);
	newwin.focus();
	
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
						<button type="button" class="btnCustom" title="${f:getMessage('일괄개정')}" id="revisionBtn" name="revisionBtn">
		                 	<span></span>
		                 	${f:getMessage('부품개정')}
	                	</button>
						<button type="button" class="btnCustom" title="${f:getMessage('품목추가')}" id="addPart" name="addPart">
		                 	<span></span>
		                 	${f:getMessage('품목변경')}
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
				<col width="5%"><col width="3%"><col width="10%"><col width="15%"><col width="3%"><col width="5%">
				<col width="5%">
				<col width="15%"><col width="5%"><col width="3%"><col width="5%"><col width="10%"><col width="*">
				<col width="5%"><col width="5%">
				<tr>
					<td class="tdblueM" colspan="7">${f:getMessage('개정 전')}</td>
					<td class="tdblueM" colspan="6">${f:getMessage('개정 후')}</td>
					<td class="tdblueM" colspan="2">${f:getMessage('BOM')}</td>
				</tr>
				
				<tr bgcolor="9acd32" height="25">
					<td class="tdblueM"><input name="chkAll" id="chkAll" type="checkbox" class="Checkbox" ></td>
					<td class="tdblueM">${f:getMessage('번호')}</td>
					<td class="tdblueM">${f:getMessage('품목번호')}</td>
					<td class="tdblueM">${f:getMessage('품목명')}</td>
					<td class="tdblueM">Rev.</td>
					<td class="tdblueM" >${f:getMessage('등록자')}</td>
					<td class="tdblueM">${f:getMessage('상태')}</td>
					<td class="tdblueM">${f:getMessage('품목번호')}</td>
					<td class="tdblueM">Rev.</td>
					<td class="tdblueM" >${f:getMessage('등록자')}</td>
					<td class="tdblueM">${f:getMessage('상태')}</td>
					<td class="tdblueM">${f:getMessage('주 도면')}</td>
					<td class="tdblueM">${f:getMessage('참조항목')}</td>
					<td class="tdblueM" >${f:getMessage('BOM 편집')}</td>
					<td class="tdblueM" >${f:getMessage('BOM 비교')}</td>
				
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
									
									<c:choose>
										<c:when test="${partData.nextPart == null }">
											<td class="tdwhiteM" colspan="6" >
												<b>${f:getMessage('개정된 데이터가 없습니다.')}</b>
											</td>
										</c:when>
										
										<c:otherwise>
											
											<td class="tdwhiteM" >
												<a href="javascript:openView('${partData.nextOid }')">
													<c:out value="${partData.nextNumber }" />
												</a>
											</td>
											
											<td class="tdwhiteM" >
												<c:out value="${partData.nextVersion }" />.<c:out value="${partData.nextIteration }" />
											</td>
											<td class="tdwhiteM" ><c:out value="${partData.nextUserCreaterName }" /></td>
											<td class="tdwhiteM" >
												<c:out value="${partData.nextState }" />
											</td>
											<c:choose>
												<c:when test="${partData.epm3dOid ne null }">
													
													<td class="tdwhiteM" >
														<c:out value="${partData.epm3dNumber }" />
													</td>
													
													<td class="tdwhiteM" >
														<c:forEach items="${partData.list}" var="AA">
															<c:out value="${AA.epm2Number }"></c:out>
														</c:forEach>
													</td>
												</c:when>
												
												<c:otherwise>
													<td class="tdwhiteM" colspan="2"><b>${f:getMessage('주도면이 없습니다.')}</b></td>
												</c:otherwise>
											</c:choose>
											
											
										</c:otherwise>
									</c:choose>
									<td class="tdwhiteM" >
										<c:choose>
											<c:when test="${partData.nextPart == null and partData.stateKey =='INWORK' }">
												<button type="button" name="bomE" id="bomE" class="btnCustom" onclick="bomEditor('${partData.productOid }','${partData.partOid }')">
												<span></span>
												BOM편집
												</button>
											</c:when>
											<c:when test="${partData.nextPart == null and partData.stateKey ne'INWORK' }">
												&nbsp;
											</c:when>
											<c:otherwise>
												<button type="button" name="bomE" id="bomE" class="btnCustom" onclick="bomEditor('${partData.productOid }','${partData.nextOid }')">
												<span></span>
												BOM편집
												</button>
											</c:otherwise>
										</c:choose>
									</td>
									<td class="tdwhiteM" >
										<c:choose>
											<c:when test="${partData.nextPart == null }">
												<button type="button" name="bomCompare" id="bomCompare" class="btnCustom" onclick="ecaBomCompare('${partData.partOid }')">
												<span></span>
												BOM비교
												</button>
											</c:when>
											<c:otherwise>
												<button type="button" name="bomCompare" id="bomCompare" class="btnCustom" onclick="ecaBomCompare('${partData.nextOid }')">
												<span></span>
												BOM비교
												</button>
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
			           		 	<td class="tdwhiteM" colspan="12" width="100%" align="center" id="noList">${f:getMessage('검색 결과가 없습니다.')}</td>
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