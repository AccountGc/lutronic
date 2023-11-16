<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">

$(function() {
	<%----------------------------------------------------------
	*                      구성원 접기 / 펼치기
	----------------------------------------------------------%>
	$("#include_ReferenceByViewToggle").click(function() {
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

<div id=imgView style="visibility=hidden; position:absolute; left:78px; top:165px; width:400px; height:62px; z-index:1; border-width:1px; border-style:none; filter:progid:DXImageTransform.Microsoft.Shadow(color=#4B4B4B,Direction=135,Strength=3);border-color:black;"></div>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr>
	
	<tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				<c:out value="${title }" />
			</b>
		</td>
		
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ReferenceByViewToggle" alt='include_ReferenceByView' >
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<div id='include_ReferenceByView'>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
		   			<tr>
		       			<td height="1" width="100%"></td>
	    			</tr>
				</table>
				
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="table-layout:fixed">
				    <tr>
				        <td class="tdblueM" width="20%">${f:getMessage('도면번호')}</td>
				        <td class="tdblueM" width="4%">&nbsp;</td>
				        <td class="tdblueM" width="30%" style="word-break:break-all;">${f:getMessage('도면명')}</td>
				        <td class="tdblueM" width="13%">${f:getMessage('상태')}</td>
				        <td class="tdblueM" width="6%">Rev.</td>
				        <td class="tdblueM" width="7%">${f:getMessage('종속유형')}</td>
				        <td class="tdblueM" width="11%">${f:getMessage('등록자')}</td>
				        <td class="tdblueM" width="9%">${f:getMessage('수정일')}</td>
				    </tr>
					<c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list }" var="list">
								<tr>
									<td class="tdwhiteL">
										<c:choose>
												<c:when test="${distribute eq 'true' }">
														<c:choose>
															<c:when test="${list.isApproved()}">
																<a href="javascript:openDistributeView('<c:out value="${list.oid }" />')">
																	<c:out value="${list.number }" />
																</a>
															</c:when>
															<c:otherwise>
																<c:out value="${list.number }" />
															</c:otherwise>
														</c:choose>	
													</c:when>
												<c:otherwise>
													<a href="javascript:openView('<c:out value="${list.oid }" />')">
														<c:out value="${list.number }" />
													</a>
												</c:otherwise>
										</c:choose>	
									</td>
									
									<td class="tdwhiteM">
										<c:choose>
											<c:when test="${list.thum eq null }">
												<img src="<c:out value="${list.thum_mini }" />" id="<c:out value="${list.oid }" />" >
											</c:when>
											
											<c:otherwise>
												<a href='javascript:showThum("<c:out value="${list.number }" />","<c:out value="${list.getThum() }" />","<c:out value="${list.oid }" />","<c:out value="${list.getCopyTag() }" />")'>
													<img src="<c:out value="${list.getThum_mini() }" />" id="<c:out value="${list.oid }" />" >
												</a>
											</c:otherwise>
										</c:choose>
									</td>
									
									<td class="tdwhiteL" title="<c:out value="${list.number }" />">
										<c:out value="${list.name }" />
									</td>
									
									<td class="tdwhiteM" align="center">
										<c:out value="${list.state }" />
									</td>
									
									<td class="tdwhiteM" align="center">
										<c:out value="${list.version }" />.<c:out value="${list.iteration }" />
									</td>
									
									<td class="tdwhiteM" align="center">
										<c:out value="${list.linkRefernceType }" />
									</td>
									
									<td class="tdwhiteM">
										<c:out value="${list.creator }" />
									</td>
									
									<td class="tdwhiteM">
										<c:out value="${list.dateSubString(false) }" />
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td class="tdwhiteM0" colspan="8" width="100%" align="center"><c:out value="${title }" />${f:getMessage('이(가) 없습니다.')}</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</table>
			</div>
		</td>
	</tr>
</table>