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
	$("#include_ChangeECOViewToggle").click(function() {
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

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr> 
	               
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b><c:out value="${title }" /></b>
		</td>
		
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ChangeECOViewToggle" alt='include_ChangeECOView' >
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<div id='include_ChangeECOView'>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
		   			<tr>
		       			<td height="1" width="100%"></td>
	    			</tr>
				</table>
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
					<tr>
	                    <td class="tdblueM" width="24%">
	                     	ECO${f:getMessage('번호')}
	                     </td>
	                     
	                     <td class="tdblueM" width="37%">
	                     	ECO${f:getMessage('제목')}
	                     </td>
	                     
	                     <td class="tdblueM" width="19%">
	                     	${f:getMessage('상태')}
	                     </td>
	                     
	                     <td class="tdblueM" width="11%">
	                     	${f:getMessage('등록자')}
	                     </td>
	                     
	                     <td class="tdblueM" width="9%">
	                     	${f:getMessage('수정일')}
	                     </td>
	                </tr>
	                
	                <c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list }" var="eoData">
								<tr>
	                    			<td class="tdwhiteL">
	                   					<c:out value="${eoData.number }" />
	                    			</td>
	                    			
	                    			<td class="tdwhiteL">
	                    				<c:choose>
												<c:when test="${distribute eq 'true' }">
														<c:choose>
															<c:when test="${eoData.isApproved()}">
																<a href="javascript:openDistributeView('<c:out value="${eoData.oid }" />')">
																	<c:out value="${eoData.name }" />
																</a>
															</c:when>
															<c:otherwise>
																<c:out value="${eoData.name }" />
															</c:otherwise>
														</c:choose>	
													</c:when>
												<c:otherwise>
													<a href="javascript:openView('<c:out value="${eoData.oid }" />')">
														<c:out value="${eoData.name }" />
													</a>
												</c:otherwise>
										</c:choose>	
	                    			</td>
	                    			
	                    			<td class="tdwhiteM" align="center">
	                    				<c:out value="${eoData.getLifecycle() }" />
	                    			</td>
	                    			
	                    			<td class="tdwhiteM" align="center">
	                    				<c:out value="${eoData.creator }" />
	                    			</td>
	                    			
	                    			<td class="tdwhiteM">
	                    				<c:out value="${eoData.dateSubString(false) }" />
	                    			</td>
	                    			
	                    		</tr>
							</c:forEach>
						</c:when>
						
						<c:otherwise>
							<tr>
			                    <td class="tdwhiteM" colspan="5"><c:out value="${title }" /> ${f:getMessage('이(가) 없습니다.')}</td>
			                </tr>
						</c:otherwise>
					</c:choose>
					
				</table>
			</div>
		</td>
	</tr>
</table>
</body>
</html>