<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript">
$(document).ready(function() {
})

$(function() {
	$('img[name=linkDelete]').click(function() {
		
		if (confirm("${f:getMessage('삭제하시겠습니까?')}")){
			
			var url	= getURLString("changeECO", "deleteCompletePartAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: {
					linkOid : this.id,
				},
				async: true,
				cache: false,
				error: function(data) {
					alert("${f:getMessage('완제품 삭제시 오류 발생')}");
				},
				success:function(data){
					
					if(data.result) {
						alert(data.message)
						location.reload();
					}else {
						alert("${f:getMessage('삭제 실패하였습니다.')}\n" + data.message);
					}
				}
			});
			
		}
	})
	<%----------------------------------------------------------
	*                      구성원 접기 / 펼치기
	----------------------------------------------------------%>
	$("#include_CompletePartViewToggle").click(function() {
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
<table width="100%" border="0" cellpadding="1" cellspacing="0" align="center" style="padding-bottom:5px">
	<tr> 
		<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr bgcolor="ffffff" height="5">
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('완제품 품목')}
			</b>
		</td>
		
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_CompletePartViewToggle" alt='include_CompletePartView' >
		</td>
	</tr>	
	
	<tr>
		<td colspan="2">
			<div id='include_CompletePartView'>

				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
					<tr>
			  			<td height="1" width="100%"></td>
					</tr>
				</table>
				
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="table-layout:fixed">
					<tr>
						<td class="tdblueM" width="20%">${f:getMessage('품목번호')}</td>
						<td class="tdblueM" width="20%">${f:getMessage('품목명')}</td>
						<td class="tdblueM" width="5%">${f:getMessage('상태')}</td>
						<td class="tdblueM" width="5%">Rev.</td>
						<td class="tdblueM" width="5%">${f:getMessage('등록자')}</td>
						<td class="tdblueM" width="5%">${f:getMessage('등록일')}</td>
						<td class="tdblueM" width="5%">BOM</td>
						<td class="tdblueM" width="5%">도면</td>
					</tr>
					
					<tbody id="completeTableBody">
						<c:choose>
							<c:when test="${fn:length(list) != 0 }">
								<c:forEach items="${list}" var="partMap"  varStatus="status">
									<tr>
										<td class="tdwhiteM" >
										<c:if test="${partMap.isDelete }">
										<!-- 
											<img src="/Windchill/jsp/portal/images/x.gif" border=0 name="linkDelete" id="<c:out value="${partMap.likOid }"/>" style="cursor: pointer;">
										-->
										</c:if>
											<c:out value="${partMap.Number }" />
										</td>
										<td class="tdwhiteM" >
											<c:choose>
													<c:when test="${distribute eq 'true' }">
														<c:choose>
																<c:when test="${partMap.isApproved}">
																	<a href="javascript:openDistributeView('<c:out value="${partMap.Oid }" />')">
																		<c:out value="${partMap.Name }" />
																	</a>
																</c:when>
																<c:otherwise>
																	<c:out value="${partMap.Name }" />
																</c:otherwise>
															</c:choose>	
														</c:when>
													<c:otherwise>
														<a href="javascript:openView('<c:out value="${partMap.Oid }" />')">
															<c:out value="${partMap.Name }" />
														</a>
													</c:otherwise>
											</c:choose>	
										</td>
										<td class="tdwhiteM" >
											<c:out value="${partMap.State }" />
										</td>
										<td class="tdwhiteM" >
											<c:out value="${partMap.ver }" />
										</td>
										<td class="tdwhiteM" >
											<c:out value="${partMap.Creator }" />
										</td>
										
										<td class="tdwhiteM" >
											<c:out value="${partMap.CreateDate }" />
										</td>
											
										<td class='tdwhiteM'>
											<c:choose>
													<c:when test="${distribute eq 'true' }">
														<button type="button" name="bom" id="bom" class="btnCustom" onclick="distributeBOM('${partMap.Oid}','${partMap.baselineOid}')">BOM</button>
													</c:when>
													<c:otherwise>
														<button type='button' name='bom' id='bom' class='btnCustom' onclick="auiBom('<c:out value="${partMap.Oid }" />','')">BOM</button>
														
													</c:otherwise>
											</c:choose>	
										</td>
										<td class='tdwhiteM'>
											<button type='button' name='bom' id='bom' class='btnCustom' onclick="batchBOMDrawingDownLoad('<c:out value="${partMap.Oid }" />','<c:out value="${ecoData.oid }" />')">
											<span></span>도면다운로드</button>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
									<tr>
										<td class="tdwhiteM" colspan="8" width="100%" align="center" id="noList">${f:getMessage('검색 결과가 없습니다.')}</td>
									</tr>
							
							</c:otherwise>
							
						</c:choose>		
									
					</tbody>
				</table>
			</div>
		</td>
	</tr>
</table>
