<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
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
	$("#include_PartViewToggle").click(function() {
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

<!-- 관련 부품 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr>                
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				<c:out value="${title}" />
			</b>
		</td>
		
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_PartViewToggle" alt='include_PartView' >
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<div id='include_PartView'>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
		   			<tr>
		       			<td height="1" width="100%"></td>
	    			</tr>
				</table>
				
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="table-layout:fixed">
					<tr bgcolor="9acd32" height="25">
						<td class="tdblueM" width="20%">${f:getMessage('품목')}${f:getMessage('번호')}</td>
						<td class="tdblueM" width="35%" style="word-break:break-all;">${f:getMessage('품목명')}</td>
						<td class="tdblueM" width="10%">${f:getMessage('상태')}</td>
						<td class="tdblueM" width="10%">${f:getMessage('Rev.')}</td>
						<td class="tdblueM" width="10%">${f:getMessage('등록자')}</td>
						<td class="tdblueM" width="10%">${f:getMessage('수정일')}</td>
					</tr>
					
					<tbody id="partTableBody">
						
						<c:choose>
							<c:when test="${fn:length(list) != 0 }">
								<c:forEach items="${list}" var="partData" >
									<tr bgcolor="ffffff" height="22">
										
										<td class="tdwhiteL">
											<c:out value="${partData.number }" />
										</td>
										
										<td class="tdwhiteL" title='<c:out value="${partData.name }" />'>
											<div style="width:350;border:0;padding:0;margin:0;text-overflow:ellipsis;overflow:hidden;">
												<nobr>
													<c:choose>
														<c:when test="${distribute eq 'true' }">
															<c:choose>
																	<c:when test="${partData.isApproved()}">
																		<a href="javascript:openDistributeView('<c:out value="${partData.oid }" />')">
																			<c:out value="${partData.name }" />
																		</a>
																	</c:when>
																	<c:otherwise>
																		<c:out value="${partData.name }" />
																	</c:otherwise>
																</c:choose>	
														</c:when>
														<c:otherwise>
															<a href="javascript:openView('<c:out value="${partData.oid }" />')">
																<c:out value="${partData.name }" />
															</a>
														</c:otherwise>
													</c:choose>	
												</nobr>
											</div>
										</td>							
										
										<td class="tdwhiteM">
											<c:out value="${partData.lifecycle }" />
										</td>
										
										<td class="tdwhiteM">
											<c:out value="${partData.version }" />.<c:out value="${partData.iteration }" />
										</td>
										
										<td class="tdwhiteM">
											<c:out value="${partData.creator }" />
										</td>
										
										<td class="tdwhiteM">
											<c:out value="${partData.dateSubString(false) }" />
										</td>
									</tr>
								</c:forEach>
							</c:when>
							
							<c:otherwise>
								<tr>
				           		 	<td class="tdwhiteM" colspan="6" width="100%" align="center"><c:out value="${title}" />${f:getMessage('이(가) 없습니다.')}</td>
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