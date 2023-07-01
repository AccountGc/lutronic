<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		location.href = getURLString("rohs", "updateRohs", "do") + "?oid="+$("#oid").val();
	})
	<%----------------------------------------------------------
	*                      개정 버튼
	----------------------------------------------------------%>
	$("#reviseBtn").click(function () {
		var url	= getURLString("doc", "reviseDocumentPopup", "do") + "?oid="+$("#oid").val()+"&module=rohs";
		openOtherName(url,"reviseDocumentPopup","350","200","status=no,scrollbars=yes,resizable=yes");
	})
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#deleteBtn").click(function () {
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){ return; }
		var form = $("form[name=rohsViewForm ]").serialize();
		var url	= getURLString("rohs", "deleteRohsAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				alert(data.msg);
				if(data.result) {
					if(parent.opener.$("#sessionId").val() == "undefined" || parent.opener.$("#sessionId").val() == null){
						parent.opener.location.reload();
					}else {
						parent.opener.$("#sessionId").val("");
						parent.opener.lfn_Search();
					}
					window.close();
				}
			}
		});
	}),
	<%----------------------------------------------------------
	*                      버전이력 버튼
	----------------------------------------------------------%>
	$("#versionBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "versionHistory", "do") + "?oid=" + oid+"&distribute=true";
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	<%----------------------------------------------------------
	*                      다운로드 이력 버튼
	----------------------------------------------------------%>
	$("#downloadBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "downloadHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	<%----------------------------------------------------------
	*                      결재이력 버튼
	----------------------------------------------------------%>
	$("#approveBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("groupware", "historyWork", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	<%----------------------------------------------------------
	*                      최신버전 버튼
	----------------------------------------------------------%>
	$("#lastestBtn").click(function() {
		var oid = this.value;
		openView(oid);
	})
	
	<%----------------------------------------------------------
	*                      Copy 버튼
	----------------------------------------------------------%>
	$('#copyRohs').click(function() {
		var url = getURLString("rohs", "copyRohs", "do") + '?oid='+$('#oid').val();
		openOtherName(url,"copyRohs","830","300","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      결재 회수 버튼
	----------------------------------------------------------%>
	$("#withDrawBtn").click(function() {
		if (!confirm("${f:getMessage('결재 회수 하시겠습니까?')}")){
			return;
		}
		
		var form = $("form[name=rohsViewForm]").serialize();
		var url	= getURLString("common", "withDrawAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('결재 회수 오류 발생')}");
			},
			success:function(data){
				alert(data.message);
				location.reload();
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
	})
	
})

</script>

<body>

<form name="rohsViewForm" method="post" >

<input type="hidden" name="oid" id="oid" value="<c:out value="${rohsData.oid }" />" />
<input type="hidden" name="cmd" id="cmd" value="" />

<div name="imgView" id="imgView" style="visibility=hidden; position:absolute; left:78px; top:165px; width:400px; height:62px; z-index:1; border-width:1px; border-style:none; filter:progid:DXImageTransform.Microsoft.Shadow(color=#4B4B4B,Direction=135,Strength=3);border-color:black;"></div>
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
					<td>
						<img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
						<b>
							RoHS
							${f:getMessage('상세보기')}
						</b>
					</td>
					
					<td>
					
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                	
			                	

								<td>
									<button type="button" name="versionBtn" id="versionBtn" class="btnCustom">
										<span></span>
										${f:getMessage('Rev.이력')}
									</button>
								</td>
								
								<td>
									<button type="button" name="downloadBtn" id="downloadBtn" class="btnCustom">
										<span></span>
										${f:getMessage('다운로드이력')}
									</button>
								</td>
								
								<td>
									<button type="button" name="approveBtn" id="approveBtn" class="btnCustom">
										<span></span>
										${f:getMessage('결재이력')}
									</button>
								</td>
								
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
								<td class="tdblueM" colspan="4">
								
									<c:out value="${rohsData.icon}" escapeXml="false" />
									<b>
										<c:out value="${rohsData.name }"/>
									</b>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('물질')}${f:getMessage('번호')}</td>
								<td class="tdwhiteL">
									<c:out value="${rohsData.number }"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('협력업체')}</td>
			                    <td class="tdwhiteL"  style="word-break:break-all;" >
			                    	<c:out value="${rohsData.getManufactureDisplay(true) }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('상태')}</td>
								<td class="tdwhiteL">
									<c:out value="${rohsData.lifecycle }"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('Rev.')}</td>
								<td class="tdwhiteL">
									<c:out value="${rohsData.version }"/>.<c:out value="${rohsData.iteration }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록자')}</td>
								<td class="tdwhiteL">
									<c:out value="${rohsData.creator }"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('수정자')}</td>
								<td class="tdwhiteL">
									<c:out value="${rohsData.modifier }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록일')}</td>
								<td class="tdwhiteL">
									<c:out value="${rohsData.createDate }"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('수정일')}</td>
								<td class="tdwhiteL">
									<c:out value="${rohsData.modifyDate }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('결재방식')}</td>
			                    <td class="tdwhiteL"  style="word-break:break-all;"  colspan="3">
			                    	<c:out value="${rohsData.getApprovalType() }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('첨부파일')}</td>					
								<td class="tdwhiteL" colspan="3">
									<table border="0" cellpadding="0" cellspacing="0" style="width: 100%">
										<tr>
											<td>
												<table width="100%" cellspacing="0" cellpadding="1" border="0" id="fileTable" align="center">
													<tr>
													    <td class="tdblueM" width="*">${f:getMessage('첨부파일')}</td>
													    <td class="tdblueM" width="25%">${f:getMessage('파일구분')} </td>
													  	<td class="tdblueM" width="20%">${f:getMessage('발행일')}</td>
													</tr>
													
													<c:choose>
														<c:when test="${fn:length(list) != 0 }">
															<c:forEach items="${list}" var="contentData" >
																<tr>
																	<td class="tdwhiteL">
																		<c:out value="${contentData.fileDown }" escapeXml="false" />
																	</td>
																	<td class="tdwhiteM">
																		<c:out value="${contentData.fileType }"/>
																	</td>
																	<td class="tdwhiteM">
																		<c:out value="${contentData.fileDate }"/>
																	</td>
																</tr>
																
															</c:forEach>
														</c:when>
													</c:choose>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 관련 부품 -->
<jsp:include page="/eSolution/part/include_PartView.do" flush="false" >
	<jsp:param name="moduleType" value="rohs"/>
	<jsp:param name="title" value="${f:getMessage('관련 품목')}"/>
	<jsp:param name="oid" value="${rohsData.oid }"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<!-- 관련 대표 물질 -->
<jsp:include page="/eSolution/rohs/include_RohsView.do">
	<jsp:param value="represent" name="roleType"/>
	<jsp:param value="${rohsData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련 대표 물질')}" name="title"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<!-- 관련 물질 -->
<jsp:include page="/eSolution/rohs/include_RohsView.do">
	<jsp:param value="composition" name="roleType"/>
	<jsp:param value="${rohsData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련')} RoHS" name="title"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<c:if test="${isAdmin }">
<!-- 관리자 속성 -->
<jsp:include page="/eSolution/common/include_adminAttribute.do">
	<jsp:param name="module" value="doc"/>
	<jsp:param name="oid" value="${rohsData.oid }"/>
</jsp:include>
</c:if>
</form>
</body>
</html>