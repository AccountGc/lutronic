<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<head>
<script type="text/javascript">
function oneClickHW(check,userName)
{
	if ( !check.checked ) return;
	$("#newCommentEditUserName").text(userName);
	//alert(userName);
}
<%----------------------------------------------------------
*                      수정 버튼
----------------------------------------------------------%>
function update(){
	alert(1);
	if(confirm("${f:getMessage('수정 하시겠습니까?')}")){
		
		var form = $("form[name=updateHistoryWorkCommentForm]").serialize();
		var url	= getURLString("groupware", "updateHistoryWorkCommentAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error:function(data){
				var msg = "${f:getMessage('등록 오류')}";
				alert(msg);
			},

			success:function(data){
				if(data.result) {
					alert("${f:getMessage('수정 성공하였습니다.')}");
					location.href = getURLString("groupware", "historyWork", "do") + "?oid=${oid}";
				}else {
					alert("${f:getMessage('수정 실패하였습니다.')} \n" + data.message);
				}
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
	}
}

</script>
</head>
<body>
<form name="updateHistoryWorkCommentForm" id="updateHistoryWorkCommentForm">
<input type="hidden" name="mode"/> 
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
					<td ><img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />&nbsp;${f:getMessage('결재이력')}
					</td>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
								<td>
								
									<c:if test="${isAuth }">
										<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD" onclick="update();">
				               				<span></span>
				               				${f:getMessage('결재의견 수정')}
				               			</button>
			               			</c:if>
									<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
			               				<span></span>
			               				${f:getMessage('닫기')}
			               			</button>
								</td>
							</tr>
			            </table>
					</td>
				</tr>
				
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan=2>
					
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
								<tr>
									<td height="1" width="100%"></td>
								</tr>
						</table>
						
						<table name='approverInfo' id='approverInfo' border="0" cellpadding="0" cellspacing="0" width="100%"> <!--//여백 테이블-->
							 <tr> 
						   	   <td class="tdblueM" width="5%">&nbsp;</TD>
							   <td class="tdblueM" width="5%">${f:getMessage('순서')}</TD>
							   <td class="tdblueM" width="10%">${f:getMessage('구분')}</TD>
							   <td class="tdblueM" width="12%">${f:getMessage('부서')}</TD>
							   <td class="tdblueM" width="10%">${f:getMessage('이름')}</TD>
							   <td class="tdblueM" width="17%">${f:getMessage('결재일')}</TD>
							   <td class="tdblueM" width="7%">${f:getMessage('결재')}</TD>
							   <td class="tdblueM" width="25%">${f:getMessage('결재의견')}</TD>
							 </tr>
							 
							 <c:choose>
								<c:when test="${fn:length(appList) != 0 }">
									<c:forEach items="${appList }" var="appList">
										<tr>
											<td class="tdwhiteM0" >
												<c:choose>
													<c:when test="${appList.isAuthComment }">
														<c:if test="${appList.isCommentEdit }">
															<input type="radio" name="historyLinkOid" id="historyLinkOid" onclick="oneClickHW(this,'${appList.userName}')" value="<c:out value="${appList.historyLinkOid }"  />">
														</c:if>
													</c:when>
													<c:otherwise>
														&nbsp;
													</c:otherwise>
												</c:choose>
											</td>
							 				<td class="tdwhiteM0" >
							 					<c:out value="${appList.processOrder }" />
							 				</td>
							 				
							 				<td class="tdwhiteM0" >
							 					<c:out value="${appList.activityName }" />
							 				</td>
							 				
							 				<td class="tdwhiteM0" >
							 					<c:out value="${appList.deptName }" />
							 				</td>
							 				
							 				<td class="tdwhiteM0" >
							 					<c:out value="${appList.userName }" />
							 				</td>
							 				
							 				<td class="tdwhiteM0" >
							 					<c:out value="${appList.processDate }" />
							 				</td>
							 				
							 				<td class="tdwhiteM0" >
							 					<c:out value="${appList.state }" />
							 				</td>
							 				
							 				<td class="tdwhiteM0" >
					 							<c:out value="${appList.comment }" />
							 				</td>
									</c:forEach>
								</c:when>
								
								<c:otherwise>
									<tr>
						          		<td class="tdwhiteM0" colspan="8">${f:getMessage('결재이력이 없습니다.')}</td>
						          	</tr>
								</c:otherwise>
								
							</c:choose>
							<c:if test="${isAuth }">
								<tr>
									<td class="tdblueM" colspan="3">
										${f:getMessage('결재 담당자')} : 
										<span id="newCommentEditUserName"></span>
										<br>
										${f:getMessage('수정할 결재 의견')}
									</td>
									<td class="tdwhiteM0" colspan="5">
										<textarea rows="5" cols="100" name="newComment" id="newComment">
			 							</textarea>
									</td>
								</tr>
							</c:if>
						</table>
					</td>
					
				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
		<td height="5">&nbsp;</td> 
	</tr>
	<tr>
		<td>
			<table width="99%" border="0" cellpadding="0" cellspacing="0" align="center">
				<tr>
					<td>
						<img src="/Windchill/jsp/portal/img/bt_01.gif" > <b>${f:getMessage('외부 유저 메일')}</b>
						<div style="width:100%;overflow-x:hidden;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  style="table-layout:fixed">
							<tr>
								<td class="tdblueM" width="50%">${f:getMessage('이름')}</td>
								<td class="tdblueM" width="50%" style="word-break:break-all;">${f:getMessage('이메일')}</td>
							</tr>
								<c:forEach items="${mailList }" var="mailList">
									<tr>
					                  	<td class="tdwhiteM">
					                  		<c:out value="${mailList.name }" />
										</td>
										<td class="tdwhiteM">
											<c:out value="${mailList.email }" />
										</td>
									</tr>
								</c:forEach>
								
			         	 </table>
			        	</div>
			        </td>
			  </tr>
			</table>
		</td>
	</tr>	
	<!-- 외부 유저 END-->
	
</table>
</form>
</body>
</html>