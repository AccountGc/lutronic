<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		location.href = getURLString("asmApproval", "updateAsm", "do") + "?oid="+$("#oid").val();
	}),
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#deleteBtn").click(function () {
		
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){
			return;
		}
		
		var form = $("form[name=viewASMForm]").serialize();
		var url	= getURLString("asmApproval", "deleteAsmAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				alert(data.msg);
				if(data.result) {
					if(parent.opener.$("#sessionId").val() == "undefined" || parent.opener.$("#sessionId").val() == null){
						parent.opener.location.reload();
					}else {
						parent.opener.$("#sessionId").val("");
						parent.opener.lfn_Search();
					}
					window.close();
				}else {
					document.location = data.view;
				}
			}
		});
	}),
	
	<%----------------------------------------------------------
	*                      다운로드 이력 버튼
	----------------------------------------------------------%>
	$("#downloadBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "downloadHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	}),
	<%----------------------------------------------------------
	*                      결재이력 버튼
	----------------------------------------------------------%>
	$("#approveBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("groupware", "historyWork", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      결재 회수 버튼
	----------------------------------------------------------%>
	$("#withDrawBtn").click(function() {
		
		var url	= getURLString("common", "withDrawPopup", "do") + "?oid="+$("#oid").val();
		openOtherName(url,"withDrawBtn","400","220","status=no,scrollbars=yes,resizable=yes");
	})
	/*
	$("#withDrawBtn").click(function() {
		if (!confirm("${f:getMessage('결재 회수 하시겠습니까?')}")){
			return;
		}
		
		var form = $("form[name=viewASMForm]").serialize();
		var url	= getURLString("common", "withDrawAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('결재 회수 오류 발생')}");
			},
			success:function(data){
				alert(data.message);
				location.reload();
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
	})
	*/
})

</script>

<body>

<form name="viewASMForm" method="post" >
<input type="hidden" name="oid" id="oid" value="<c:out value="${asmData.oid }" />" />
<input type="hidden" name="cmd" id="cmd" value="" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
				</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td ><img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />&nbsp;${f:getMessage('일괄결재')} ${f:getMessage('상세보기')} </td>
					<td>
					
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                	<!-- 회수 권한 승인중 && 소유자 || 관리자 -->
								<c:if test='${asmData.isWithDraw()}'>
									<td>
										<button type="button" name="withDrawBtn" id="withDrawBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('결재회수')}
										</button>
									</td>
								</c:if>
			                
			                	<c:if test="${asmData.isModify() }">
									<td>
										<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('수정')}
										</button>
									</td>
								
									<td>
										<button type="button" name="deleteBtn" id="deleteBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('삭제')}
										</button>
									</td>
								
								</c:if>
								
								<td>
									<button type="button" name="approveBtn" id="approveBtn" class="btnCustom">
										<span></span>
										${f:getMessage('결재이력')}
									</button>
								</td>
								<td>
									<button type="button" name="downloadBtn" id="downloadBtn" class="btnCustom">
										<span></span>
										${f:getMessage('다운로드이력')}
									</button>
								</td>
								<!--  
		                		<td>
		                			<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
		                				<span></span>
		                				${f:getMessage('닫기')}
		                			</button>
		                		</td>
		                		-->
							</tr>
			            </table>
			            <!-- 버튼 테이블 끝 -->
					</td>
				</tr>
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
								<tr><td height="1" width="100%"></td></tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
						<col width="13%"><col width="37%"><col width="13%"><col width="37%">
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM" colspan="4">
									<b>
										<c:out value="${asmData.name }"/>
									</b>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">일괄결재${f:getMessage('번호')}</td>
								<td class="tdwhiteL">
									<c:out value="${asmData.number }"/>
								</td>
								<td class="tdblueM">${f:getMessage('상태')}</TD>
								<td class="tdwhiteL">
									<c:out value="${asmData.getLifecycle() }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록자')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${asmData.creator }"/>
								</td>
								
								
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록일')}</td>
								<td class="tdwhiteL">
									<c:out value="${asmData.dateSubString(true)}"/>
								</td>
								<td class="tdblueM">${f:getMessage('수정일')}</TD>
								<td class="tdwhiteL">
									<c:out value="${asmData.dateSubString(false)}"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('설명')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${asmData.viewDescription }" escapeXml="false"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			<!-- 관련 ECO -->
			<table width="100%" border="0" cellpadding="0" cellspacing="1" >
				<tr bgcolor="ffffff" height="5">
					<td colspan="5">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
						<b>${f:getMessage('일괄 결재 대상')}</b>
					</td>
				</tr>
			</table>
			
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height="1" width="100%"></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<tr height="25">
					<td class="tdblueM" width="5%">No</td>
					<td class="tdblueM" width="25%">${f:getMessage('번호')}</td> 
					<td class="tdblueM" width="40%">${f:getMessage('제목')}</td> 
					<td class="tdblueM" width="10%">${f:getMessage('상태')}</td> 
					<td class="tdblueM" width="10%">${f:getMessage('등록자')}</td> 
					<td class="tdblueM" width="10%">${f:getMessage('등록일')}</td> 
				</tr>
				<c:choose>
					<c:when test="${fn:length(asmData.getObjectToLoink()) != 0 }">
							<c:forEach items="${asmData.getObjectToLoink() }" var="objectLink" varStatus="status" >
								<tr height="25">
								<td class="tdwhiteM" align="center">
									${status.index + 1}
								</td>
								<td class="tdwhiteL">
									<c:out value="${objectLink.Number }" />
								</td>
								<td class="tdwhiteL">
									<a href="javascript:openView('<c:out value="${objectLink.Oid }" />')">
										<c:out value="${objectLink.Name }" />
									</a>
								</td>
								<td class="tdwhiteM" >
									<c:out value="${objectLink.State }" />
								</td>
								<td class="tdwhiteM" >
									<c:out value="${objectLink.Creator }" />
								</td>
								<td class="tdwhiteM" >
									<c:out value="${objectLink.CreateDate }" />
								</td>
								</tr>
							</c:forEach>
					</c:when>
					<c:otherwise>
		                <tr>
		                    <td class="tdwhiteM" colspan="6">${f:getMessage('검색 결과가 없습니다.')}</td>
		                </tr>
					</c:otherwise>
				</c:choose>	
			</table>		
		</td>
	</tr>
</table>
							
					
</form>
</body>
</html>