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
	<%----------------------------------------------------------
	*                      구성원 접기 / 펼치기
	----------------------------------------------------------%>
	$("#include_ECOViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})
})

</script>

<body>

<table width="100%" border="0" cellpadding="1" cellspacing="0" align="center" style="padding-bottom:5px">
	<tr> 
		<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr bgcolor="ffffff" height="5">
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('관련')} ECO
			</b>
		</td>
		
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ECOViewToggle" alt='include_ECOView' >
		</td>
	</tr>	

	<tr>
		<td colspan="2">
			<div id='include_ECOView'>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
					<tr>
			  			<td height="1" width="100%"></td>
					</tr>
				</table>
				
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="table-layout:fixed">
					<tr bgcolor="9acd32" height="25">
						<td class="tdblueM" width="20%">ECO${f:getMessage('번호')}</td>
						<td class="tdblueM" width="*">ECO${f:getMessage('제목')}</td>
						<td class="tdblueM" width="10%">${f:getMessage('상태')}</td>
						<td class="tdblueM" width="15%">${f:getMessage('등록자')}</td>
						<td class="tdblueM" width="10%">${f:getMessage('완료일')}</td>
					</tr>
					
					<tbody id="ecaTableBody">
						<c:choose>
							<c:when test="${fn:length(list) != 0 }">
								<c:forEach items="${list}" var="ecooData"  varStatus="status">
									<tr>
										<td class="tdwhiteM">
											<c:out value="${ecooData.number }" />
										</td>
										<td class="tdwhiteM">
											<c:choose>
												<c:when test="${distribute eq 'true' }">
														<c:choose>
															<c:when test="${ecooData.isApproved()}">
																<a href="javascript:openDistributeView('<c:out value="${ecooData.oid }" />')">
																	<c:out value="${ecooData.name }" />
																</a>
															</c:when>
															<c:otherwise>
																<c:out value="${ecooData.name }" />
															</c:otherwise>
														</c:choose>	
													</c:when>
												<c:otherwise>
													<a href="javascript:openView('<c:out value="${ecooData.oid }" />')">
														<c:out value="${ecooData.name }" />
													</a>
												</c:otherwise>
											</c:choose>	
										</td>
										<td class="tdwhiteM">
											<c:out value="${ecooData.getLifecycle() }" />
										</td>
										<td class="tdwhiteM">
											<c:out value="${ecooData.creator }" />
										</td>
										<td class="tdwhiteM">
											<c:out value="${ecooData.eoApproveDate }" />
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
				           		 	<td class="tdwhiteM" colspan="5" align="center">${f:getMessage('검색 결과가 없습니다.')}</td>
				            	</tr>	
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
		</td>
	</tr>
</table>


</body>
</html>