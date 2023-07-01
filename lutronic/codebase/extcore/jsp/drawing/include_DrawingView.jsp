<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
<c:if test="${epmType eq ''}">
$(function() {
	<%----------------------------------------------------------
	*                      구성원 접기 / 펼치기
	----------------------------------------------------------%>
	$("#include_DrawingViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#include_DrawingView_" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#include_DrawingView_" + divId).slideToggle();
	})
})
</c:if>
</script>

<div id=imgView style="visibility=hidden; position:absolute; left:78px; top:165px; width:400px; height:62px; z-index:1; border-width:1px; border-style:none; filter:progid:DXImageTransform.Microsoft.Shadow(color=#4B4B4B,Direction=135,Strength=3);border-color:black;"></div>

<body>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >

	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr>  
	              
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				<c:out value="${title }" />
			</b>
		</td>
		
		<td align="right">
			<c:if test="${epmType eq ''}">
				<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id='include_DrawingViewToggle' alt='${epmType}' >
			</c:if>
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<div id='include_DrawingView_${epmType}'>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
		   			<tr>
		       			<td height="1" width="100%"></td>
	    			</tr>
				</table>
				
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
					<tr>
	                    <td class="tdblueM" width="20%">
	                    	${f:getMessage('도면번호')}
	                    </td>
	                    
	                    <td class="tdblueM" width="4%">
	                    	&nbsp;
	                    </td>
	                    
	                    <td class="tdblueM" width="37%" style="word-break:break-all;">
	                    	${f:getMessage('도면명')}
	                    </td>
	                    
	                    <td class="tdblueM" width="13%">
	                    	${f:getMessage('상태')}
	                    </td>
	                    
	                    <td class="tdblueM" width="6%">
	                    	Rev.
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
							<c:forEach items="${list }" var="epmData">
								<tr>
									<td class="tdwhiteL">
										<c:out value="${epmData.number }" />
									</td>
									
									<td class="tdwhiteM">
										<c:choose>
											<c:when test="${epmData.thum eq null }">
												<img src="<c:out value="${epmData.getThum_mini() }" />" id="<c:out value="${epmData.oid }" />" >
											</c:when>
											
											<c:otherwise>
												<a href='javascript:showThum("<c:out value="${epmData.number }" />","<c:out value="${epmData.getThum() }" />","<c:out value="${epmData.oid }" />","<c:out value="${epmData.getCopyTag() }" />")'>
													<img src="<c:out value="${epmData.getThum_mini() }" />" id="<c:out value="${epmData.oid }" />" >
												</a>
											</c:otherwise>
										</c:choose>
									</td>
									
									<td class="tdwhiteL" title="">
										<c:choose>
												<c:when test="${distribute eq 'true' }">
														<c:choose>
															<c:when test="${epmData.isApproved()}">
																<a href="javascript:openDistributeView('<c:out value="${epmData.oid }" />')">
																	<c:out value="${epmData.name }" />
																</a>
															</c:when>
															<c:otherwise>
																<c:out value="${epmData.name }" />
															</c:otherwise>
														</c:choose>	
													</c:when>
												<c:otherwise>
													<a href="javascript:openView('<c:out value="${epmData.oid }" />')">
														<c:out value="${epmData.name }" />
													</a>
												</c:otherwise>
										</c:choose>	
									</td>
									
									<td class="tdwhiteM" align="center">
										<c:out value="${epmData.getState() }" />
									</td>
									
									<td class="tdwhiteM" align="center">
										<c:out value="${epmData.version }" />.<c:out value="${epmData.iteration }" />
									</td>
									
									<td class="tdwhiteM">
										<c:out value="${epmData.creator }" />
									</td>
									
									<td class="tdwhiteM">
										<c:out value="${epmData.dateSubString(false) }" />
									</td>
								</tr>
							</c:forEach>
						</c:when>
						
						<c:otherwise>
							<tr>
			                    <td class="tdwhiteM" colspan="7"><c:out value="${title }" />${f:getMessage('이(가) 없습니다.')}</td>
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