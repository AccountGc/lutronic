<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
$(document).ready(function () {
	checkDummy();
	
	
})
$(function() {
	<%----------------------------------------------------------
	*                      구성원 접기 / 펼치기
	----------------------------------------------------------%>
	$("#include_ChangePartViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})
})

function bomView2(oid){
			
		var str = getURLString("part", "PartTree", "do") + "?oid="+oid;
	    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	    leftpos = (screen.width - 1000)/ 2;
	    toppos = (screen.height - 600) / 2 ;
	    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
	    var newwin = window.open( str , "viewBOM", opts+rest);
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


$(function(){
	$('#checkDummy').click(function() {
		checkDummy();
	})
})

function checkDummy(){
	status = $("#true").css("display");
	if (status == "none") {
	    //$("#true").css("display","");
	    $("[name=true]").each(function(idx){   
	    	$(this).css("display","");
      	});

	}
	else {
	    //$("#true").css("display","none");
		 $("[name=true]").each(function(idx){   
		    	$(this).css("display","none");
	      	});
	}
}
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
				${f:getMessage('대상품목')} <input type="checkbox" name="checkDummy" id="checkDummy" value="true" checked > 더미숨김
			</b> 
		</td>
		
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ChangePartViewToggle" alt='include_ChangePartView' >
		</td>
	</tr>	

	<tr>
		<td colspan="2">
			<div id='include_ChangePartView'>
				<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
					<tr>
			  			<td height="1" width="100%"></td>
					</tr>
				</table>
				<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="table-layout:fixed">
					<col width="10%"><col width="10%"><col width="5%"><col width="5%"><col width="5%">
					<col width="10%"><col width="5%"><col width="5%"><col width="5%">
					<col width="5%"><col width="5%">
					<tr>
						<td class="tdblueM" rowspan="2">${f:getMessage('품목번호')}</td>
						<td class="tdblueM" colspan="4">${f:getMessage('변경전')}</td>
						<td class="tdblueM" colspan="4">${f:getMessage('변경후')}</td>
						<td class="tdblueM" colspan="2">${f:getMessage('BOM')}</td>
					</tr>	
					<tr>
						<td class="tdblueM" >${f:getMessage('품목명')}</td>
						<td class="tdblueM" >${f:getMessage('상태')}</td>
						<td class="tdblueM" >Rev.</td>
						<td class="tdblueM" >${f:getMessage('등록자')}</td>
						<td class="tdblueM" >${f:getMessage('품목명')}</td>
						<td class="tdblueM" >${f:getMessage('상태')}</td>
						<td class="tdblueM" >Rev.</td>
						<td class="tdblueM" >${f:getMessage('등록자')}</td>
						<td class="tdblueM" >BOM ${f:getMessage('비교')}</td>
						<td class="tdblueM" >BOM ${f:getMessage('보기')}</td>
					</tr>
					<tbody id="ecoPartList">
						<c:choose>
							<c:when test="${fn:length(list) != 0 }">
								<c:forEach items="${list}" var="partData"  varStatus="status">
									<tr id=<c:out value="${partData.isDummy }"  /> name=<c:out value="${partData.isDummy }"  /> >
										<td class="tdwhiteM" >
											<c:out value="${partData.number }" />
										</td>
										<c:choose>
											<c:when test="${partData.nextPart == null }">
												<td class="tdwhiteM" >-</td>
												<td class="tdwhiteM" >-</td>
												<td class="tdwhiteM" >-</td>
												<td class="tdwhiteM" >-</td>
												<td class="tdwhiteM" >
													<c:choose>
														<c:when test="${distribute eq 'true' }">
														<a href="javascript:openDistributeView('<c:out value="${partData.partOid }" />')"><c:out value="${partData.name }" /></a>
														</c:when>
														<c:otherwise>
														<a href="javascript:openView('<c:out value="${partData.partOid }" />')"><c:out value="${partData.name }" /></a>
														</c:otherwise>
													</c:choose>
												</td>
												<td class="tdwhiteM" ><c:out value="${partData.state }" /></td>
												<td class="tdwhiteM" ><c:out value="${partData.version }" />.<c:out value="${partData.iteration }" /></td>
												<td class="tdwhiteM" ><c:out value="${partData.userCreatorName }" /></td>
												<td class="tdwhiteM" ><button type="button" name="bomCompare" id="bomCompare" class="btnCustom" onclick="ecaBomCompare('${partData.partOid }')">BOM비교</button></td>
												<td class="tdwhiteM" >
													<c:choose>
														<c:when test="${distribute eq 'true' }">
															<button type="button" name="bom" id="bom" class="btnCustom" onclick="distributeBOM('${partData.partOid}','${partData.baselineOid}')">BOM</button>
														</c:when>
														<c:otherwise>
															<button type="button" name="bomView" id="${partData.partOid }" class="btnCustom" onclick="auiBom('${partData.partOid }','')">BOM보기</button>
														</c:otherwise>
													</c:choose>
												</td>
											</c:when>
											<c:otherwise>
												<td class="tdwhiteM" >
													<c:choose>
														<c:when test="${distribute eq 'true' }">
															<a href="javascript:openDistributeView('<c:out value="${partData.partOid }" />')"><c:out value="${partData.name }" /></a>
														</c:when>
														<c:otherwise>
															<a href="javascript:openView('<c:out value="${partData.partOid }" />')"><c:out value="${partData.name }" /></a>
														</c:otherwise>
													</c:choose>
												</td>
												<td class="tdwhiteM" ><c:out value="${partData.state }" /></td>
												<td class="tdwhiteM" ><c:out value="${partData.version }" />.<c:out value="${partData.iteration }" /></td>
												<td class="tdwhiteM" ><c:out value="${partData.userCreatorName }" /></td>
												<td class="tdwhiteM" >
													<c:choose>
														<c:when test="${distribute eq 'true' }">
															<a href="javascript:openDistributeView('<c:out value="${partData.nextOid }" />')"><c:out value="${partData.name }" /></a>
														</c:when>
														<c:otherwise>
															<a href="javascript:openView('<c:out value="${partData.nextOid }" />')"><c:out value="${partData.name }" /></a>
														</c:otherwise>
													</c:choose>
												</td>
												<td class="tdwhiteM" ><c:out value="${partData.nextState }" /></td>
												<td class="tdwhiteM" ><c:out value="${partData.nextVersion }" />.<c:out value="${partData.nextIteration }" /></td>
												<td class="tdwhiteM" ><c:out value="${partData.nextUserCreaterName }" /></td>
												<td class="tdwhiteM" ><button type="button" name="bomCompare" id="bomCompare" class="btnCustom" onclick="ecaBomCompare('${partData.partOid }')">BOM비교</button></td>
												<td class="tdwhiteM" >
													<c:choose>
														<c:when test="${distribute eq 'true' }">
															<button type="button" name="bom" id="bom" class="btnCustom" onclick="distributeBOM('${partData.nextOid}','${partData.baselineOid}')">BOM</button>
														</c:when>
														<c:otherwise>
															<button type="button" name="bomView" id="${partData.nextOid }" class="btnCustom" onclick="auiBom('${partData.nextOid }','')">BOM보기</button>
														</c:otherwise>
													</c:choose>
												</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td class="tdwhiteM" colspan="9" width="100%" align="center" id="noList">${f:getMessage('검색 결과가 없습니다.')}</td>
								</tr>
							</c:otherwise>
						</c:choose>
						
					</tbody>
				</table>
			</div>
		</td>
	</tr>
</table>
