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
	$("#include_ChangeECOViewToggle_${moduleType}").click(function() {
		
		checkECOList();
		/*
		var divId = $(this).attr('alt');
		
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
		*/
	})
	
	
	
})

function checkECOList(){
	
	//status = $("#CHANGE").css("display");
	
	//console.log("status =" + status);
	//if (status == "none") {
	    $("#CHANGE").css("display","none");
	//}
	//else {
	   // $("#CHANGE").css("display","none");
	//}
	
}
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
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ChangeECOViewToggle_${moduleType}" alt='include_ChangeECOView' >
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
		   			<tr>
		       			<td height="1" width="100%"></td>
	    			</tr>
				</table>
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
					<tr >
	                    <td class="tdblueM" width="10%">
	                     	ECO${f:getMessage('번호')}
	                     </td>
	                     
	                     <td class="tdblueM" width="*">
	                     	ECO${f:getMessage('제목')}
	                     </td>
	                     
	                     <td class="tdblueM" width="10%">
	                     	${f:getMessage('상태')}
	                     </td>
	                     
	                     <td class="tdblueM" width="10%">
	                     	${f:getMessage('등록자')}
	                     </td>
	                     
	                     <td class="tdblueM" width="10%">
	                     	${f:getMessage('승인일')}
	                     </td>
	                     <td class="tdblueM" width="15%">
	                     	${f:getMessage('BOM')}
	                     </td>
	                     <td class="tdblueM" width="10%">
	                     	${f:getMessage('산출물')}
	                     </td>
	                     
	                     <c:if test="${moduleType eq 'CHANGE'}">
	                     <td class="tdblueM" width="10%">
	                     	${f:getMessage('도면')}
	                     </td>
	                     <td class="tdblueM" width="10%">
	                     	${f:getMessage('변경 부품 확인')}
	                     </td>
	                     
	                     </c:if>
	                </tr>
	                
	                <c:choose>
						<c:when test="${fn:length(list) != 0 }">
							<c:forEach items="${list }" var="eoData">
								<tr id='<c:out value="${moduleType }" />'  >
	                    			<td class="tdwhiteL">
	                   					<c:out value="${eoData.number }" />
	                    			</td>
	                    			
	                    			<td class="tdwhiteL">
	                    				<c:choose>
	                    					<c:when test= "${eoData.isApproved()}">
	                    						<a href="javascript:openDistributeView('<c:out value="${eoData.oid }" />')">
		                    						<c:out value="${eoData.name }" />
	                    						</a>
	                    					</c:when>
	                    					<c:otherwise>
	                    						<c:out value="${eoData.name }" />
	                    					</c:otherwise>
	                    				</c:choose>
	                    			</td>
	                    			<td class="tdwhiteM" align="center">
	                    				<c:out value="${eoData.eo.state }" />
	                    			</td>
	                    			<td class="tdwhiteM" align="center">
	                    				<c:out value="${eoData.creator }" />
	                    			</td>
	                    			<%-- <td class="tdwhiteM">
	                    				<c:out value="${eoData.dateSubString(true) }" />
	                    			</td> --%>
	                    			<td class="tdwhiteM">
	                    				<c:out value="${eoData.eoApproveDate }" />
	                    			</td>
	                    			<td class="tdwhiteM">
	                    				<button type="button" name="bom" id="bom" class="btnCustom" onclick="distributeBOM('${partData.oid}','${eoData.partToEoBaseline}')">BOM</button>&nbsp;&nbsp;&nbsp;&nbsp;
	                    				<button type="button" name="Excel" id="Excel" class="btnCustom" onclick="distributeBOMDown('${partData.oid}','${eoData.partToEoBaseline}')">Excel</button>
	                    			</td>
	                    			<td class="tdwhiteM">
	                    			<button type='button' class='btnCustom' onclick=javascript:batchEODownLoad('${eoData.oid }','attach') >산출물 다운</button>
	                    			</td>
	                    			<c:if test="${moduleType eq 'CHANGE'}">
	                    			<td class="tdwhiteM">
	                    			<button type='button' class='btnCustom' onclick=javascript:batchEODownLoad('${eoData.oid }','drawing') >도면다운</button>
	                    			</td>
	                    			<td class="tdwhiteM">
	                    				<button type="button" name="changePart" id="changePart" class="btnCustom" onclick="changePartView('${eoData.number }')"/>보기</button>
	                    			</td>
	                    			</c:if>
	                    		</tr>
							</c:forEach>
						</c:when>
						
						<c:otherwise>
							<tr>
								<c:choose>
	                    			<c:when test="${moduleType eq 'CHANGE'}">
								 		<td class="tdwhiteM" colspan="9"><c:out value="${title }" /> ${f:getMessage('이(가) 없습니다.')}</td>
								 	</c:when>
									<c:otherwise>
	                    				<td class="tdwhiteM" colspan="7"><c:out value="${title }" /> ${f:getMessage('이(가) 없습니다.')}</td>
	                    			</c:otherwise>
	                    		</c:choose>
			                   
			                </tr>
						</c:otherwise>
					</c:choose>
					
				</table>
		
		</td>
	</tr>
</table>
</body>
</html>