<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<script type="text/javascript">

$(document).ready(function() {
	lfn_getStateList('LC_Development');
})

<%----------------------------------------------------------
*                      Lifecycle 상태 가져오기
----------------------------------------------------------%>
function lfn_getStateList(lifecycle) {
	var url	= getURLString("WFItem", "lifecycleList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			lifecycle : lifecycle
		},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			$("#state").find("option").remove();
			$("#state").append("<option value=''>${f:getMessage('선택')}</option>");
			if(data.length > 0) {
				for(var i=0; i<data.length; i++) {
					$("#state").append("<option value='" + data[i].value + "'>" + data[i].name + "</option>");
				}
			}
			
		}
	});
}


$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function() {
		divPageLoad('devBody', 'updateDevelopment', $('#oid').val());
	})
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#deleteBtn").click(function () {
		deleteDevlopment();
	})
	<%----------------------------------------------------------
	*                      완료 취소 버튼
	----------------------------------------------------------%>
	$('#cancelDevelopmentBtn').click(function() {
		changeState($('#oid').val(), 'PROGRESS', 1);
	})
	<%----------------------------------------------------------
	*                      완료 버튼
	----------------------------------------------------------%>
	$('#completeDevelopmentBtn').click(function() {
		changeState($('#oid').val(), 'COMPLETED', 1);
	})
	<%----------------------------------------------------------
	*                      상태 변경 이벤트
	----------------------------------------------------------%>
	$('#state').change(function() {
		var state = this.value;
		changeState($('#oid').val(), state, 1);
	})
	
})
</script>

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td>
						<img src="/Windchill/jsp/portal/img/bt_01.gif" >
						<b>
							${f:getMessage('개발업무')}
							${f:getMessage('상세보기')}
						</b>
					</td>
					
					<td>
					
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                	<c:if test='${masterData.isAdmin() || masterData.isDm() }'>
			                
			                		<c:if test='${masterData.isState("COMPLETED") }'>
						               	<td>
											<button type="button" name="cancelDevelopmentBtn" id="cancelDevelopmentBtn" class="btnCRUD">
												<span></span>
												${f:getMessage('완료 취소')}
											</button>
										</td>
									</c:if>
									
									<c:if test='${masterData.isState("PROGRESS") }'>
						               	<td>
											<button type="button" name="completeDevelopmentBtn" id="completeDevelopmentBtn" class="btnCRUD">
												<span></span>
												${f:getMessage('완료')}
											</button>
										</td>
									</c:if>
									
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
							<col width="150">
							<col width="350">
							<col width="150">
							<col width="350">
							
							<%-- 
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('업무명')}
								</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${masterData.name }"/>
								</td>
							</tr>
							--%>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('프로젝트코드')}</td>
								<td class="tdwhiteL">
									<c:out value="${masterData.model }"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('프로젝트명')}</TD>
								<td class="tdwhiteL">
									<c:out value="${masterData.name }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('예상 시작일')}</td>
								<td class="tdwhiteL">
									<c:out value="${masterData.developmentStart }"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('예상 종료일')}</td>
								<td class="tdwhiteL">
									<c:out value="${masterData.developmentEnd}"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('관리자')}</td>
								<td class="tdwhiteL">
									<c:out value="${masterData.dmName }"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('등록자')}</td>
								<td class="tdwhiteL">
									<c:out value="${masterData.creator }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('상태')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${masterData.state }"/>
									<c:if test='${masterData.isAdmin() || masterData.isDm() }'>
										<select name='state' id='state'>
										</select>
									</c:if>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('설명')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${masterData.getDescription(true) }" escapeXml="false"/>
								</td>
							</tr>
							
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
