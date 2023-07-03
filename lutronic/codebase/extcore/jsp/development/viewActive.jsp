<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/extcore/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">
$(document).ready(function() {
	divPageLoad('docLink', $('#oid').val(), 'doc', '${f:getMessage('관련 문서') }', 'include_documentLink', $('#enabled').val());
	divPageLoad('drawingLink', $('#oid').val(), 'drawing', '${f:getMessage('관련 도면') }', 'include_drawingLink', $('#enabled').val());
	divPageLoad('partLink', $('#oid').val(), 'part', '${f:getMessage('관련 품목') }', 'include_partLink', $('#enabled').val());
	divPageLoad('workerComment', $('#oid').val(), 'development', '${f:getMessage('수행자 의견') }', 'include_viewComment', $('#enabled').val());
	divPageLoad('workerAttach', $('#oid').val(), 'development', '${f:getMessage('수행자 첨부파일') }', 'include_viewWorkerAttach', $('#enabled').val());
})

$(function() {
	<%----------------------------------------------------------
	*                      Activity 완료 버튼
	----------------------------------------------------------%>
	$('#completeBtn').click(function() {
		if (confirm("${f:getMessage('작업 완료 하시겠습니까?')}")){
			changeState('COMPLETED');
		}
	})
	<%----------------------------------------------------------
	*                      Activity 수정 버튼
	----------------------------------------------------------%>
	$('#updateBtn').click(function() {
		document.location = getURLString('development', 'updateActive', 'do') + "?oid=" + $('#oid').val();
	})
	<%----------------------------------------------------------
	*                      Activity 삭제 버튼
	----------------------------------------------------------%>
	$('#deleteBtn').click(function() {
		if (confirm("${f:getMessage('삭제 하시겠습니까?')}")){
			var url	= getURLString("development", "deleteActiveAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: {
					oid : $('#oid').val()
				},
				dataType:"json",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('등록 오류')}";
					alert(msg);
				},

				success:function(data){
					if(data.result) {
						alert("${f:getMessage('삭제 성공하였습니다.')}");
						if($('#search', opener.document).val() == 'true') {
							$(opener.location).attr('href', 'javascript:lfn_DhtmlxGridInit()');
							$("#sortValue", opener.document).val("");
							$("#sortCheck", opener.document).val("");
							$("#sessionId", opener.document).val("");
							$("#page", opener.document).val(1);
							$(opener.location).attr('href', 'javascript:lfn_Search()');
						}else if($('#activeAction', opener.document).val() == 'true') {
							$(opener.location).attr('href', 'javascript:activeAction()');
						}else {
							$(opener.location).reload();
						}
						window.close();
					}else {
						alert("${f:getMessage('삭제 실패하였습니다.')} \n" + data.message);
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
	})
	<%----------------------------------------------------------
	*                      Activity 완료 취소 버튼
	----------------------------------------------------------%>
	$('#cancelBtn').click(function() {
		if (confirm("${f:getMessage('완료 취소 하시겠습니까?')}")){
			changeState('PROGRESS');
		}
	})
	<%----------------------------------------------------------
	*                      Activity 완료 요청 버튼
	----------------------------------------------------------%>
	$('#requestComplete').click(function() {
		if (confirm("${f:getMessage('완료 요청 하시겠습니까?')}")){
			var url	= getURLString("development", "requestCompleteAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: {
					oid : $('#oid').val()
				},
				dataType:"json",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('등록 오류')}";
					alert(msg);
				},

				success:function(data){
					alert(data.message);
					if(data.result) {
						activeReload();
					}
					
				}
			});
		}
	})
	$('#cancelRequest').click(function() {
		if (confirm("${f:getMessage('완료 요청을 취소 하시겠습니까?')}")){
			changeState('PROGRESS');
		}
	})
})

<%----------------------------------------------------------
*                      Activity 상태 변경
----------------------------------------------------------%>
window.changeState = function(state) {
	var url	= getURLString("development", "changeStateAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			oid : $('#oid').val(),
			state : state
		},
		dataType:"json",
		async: true,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('등록 오류')}";
			alert(msg);
		},

		success:function(data){
			if(data.result) {
				alert("${f:getMessage('작업을 성공하였습니다.')}");
				location.href = getURLString("development", "viewActive", "do") + "?oid="+$('#oid').val();
				$(opener.location).attr('href', 'javascript:activeAction()');
			}else {
				alert("${f:getMessage('작업을 실패하였습니다.')} \n" + data.message);
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

<%----------------------------------------------------------
*                      DIV 페이지 로딩 설정
----------------------------------------------------------%>
window.divPageLoad = function(divId, oid, moduleName, title, url, enabled) {
	if(url.length > 0) {
		var url	= getURLString(moduleName, url, "do");
		$.ajax({
			type:"POST",
			url: url,
			data: {
				oid : oid,
				title : title,
				module : 'active',
				enabled : enabled
			},
			success:function(data){
				$('#' + divId).html(data);
			}
		});
	} else {
		$('#' + divId).html('');
	}
}

window.activeReload = function() {
	location.reload();
}

</script>

<body>

<form name="viewActiveForm" id="viewActiveForm" >

<input type='hidden' name='enabled' id='enabled' value="<c:out value='${devActiveData.isDm() || devActiveData.isAdmin() || devActiveData.isWorker() }'/>" />
<input type="hidden" name="oid" id="oid" value="<c:out value="${devActiveData.oid }"/>" />
 
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
					<td >
						<img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
						ACTIVITY
						${f:getMessage('상세보기')}
					</td>
					
					<td>
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                
			                	<c:if test='${devActiveData.isAdmin() || devActiveData.isDm() || devActiveData.isWorker() }'>
				                	<c:if test='${devActiveData.isState("PROGRESS") }'>
					                	<td>
											<button type="button" name="requestComplete" id="requestComplete" class="btnCRUD">
												<span></span>
												${f:getMessage('완료 요청')}
											</button>
										</td>
									</c:if>
									<c:if test='${devActiveData.isState("REQUEST_COMPLETED") }'>
										<td>
											<button type="button" name="cancelRequest" id="cancelRequest" class="btnCRUD">
												<span></span>
												${f:getMessage('완료 요청 취소')}
											</button>
										</td>
									</c:if>
								</c:if>
			                
			                	<c:if test='${devActiveData.isAdmin() || devActiveData.isDm() }'>
			                		<c:if test='${devActiveData.isState("COMPLETED") }'>
					                	<td>
											<button type="button" name="cancelBtn" id="cancelBtn" class="btnCRUD">
												<span></span>
												${f:getMessage('완료 취소')}
											</button>
										</td>
									</c:if>
								
									<c:if test='${devActiveData.isState("REQUEST_COMPLETED") }'>
					                	<td>
											<button type="button" name="completeBtn" id="completeBtn" class="btnCRUD">
												<span></span>
												${f:getMessage('완료')}
											</button>
										</td>
									</c:if>
									
									<c:if test='${devActiveData.isState("PROGRESS") }'>
										<td>
											<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
												<span></span>
												${f:getMessage('수정')}
											</button>
										</td>
									
										<c:if test="${!devActiveData.isDelete() }">
											<td>
												<button type="button" name="deleteBtn" id="deleteBtn" class="btnCRUD">
													<span></span>
													${f:getMessage('삭제')}
												</button>
											</td>
										</c:if>
									</c:if>
								</c:if>
								
								<td>
		                			<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
		                				<span></span>
		                				${f:getMessage('닫기')}
		                			</button>
		                		</td>
		                		
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
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									ACTIVITY${f:getMessage('명')}
								</td>
								
								<td class="tdwhiteL">
									<c:out value="${devActiveData.name }"/>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('수행자')}
								</td>
								
								<td class="tdwhiteL">
									<c:out value="${devActiveData.workerName }" />
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('완료 요청일')}
								</td>
								
								<td class="tdwhiteL">
									<c:out value="${devActiveData.activeDate }" />
								</td>
								
								<td class="tdblueM">
									${f:getMessage('완료일')}
								</td>
								
								<td class="tdwhiteL">
									<c:out value="${devActiveData.finishDate }" />
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('상태')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<c:out value="${devActiveData.state }" />
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('설명')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<c:out value="${devActiveData.getDescription(true) }" escapeXml="false" />
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('첨부파일')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFileView">
										<jsp:param value="${devActiveData.oid }" name="oid"/>
									</jsp:include>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

<!-- 수행자 의견 -->
<div id='workerComment'>
</div>

<!-- 수행자 첨부파일 -->
<div id='workerAttach'>
</div>

<br>

<!-- 문서 링크 -->
<div id='docLink'>
</div>

<!-- 도면 링크 -->
<div id='drawingLink'>
</div>

<!-- 품목 링크 -->
<div id='partLink'>
</div>

</body>
</html>