<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

$(function() {
	<%----------------------------------------------------------
	*                      산출물 버튼
	----------------------------------------------------------%>
	$("#viewECA").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("change", "viewECA", "do") + "?oid="+oid;
		openWindow(url, "eca", 1000, 600);
	}),
	<%----------------------------------------------------------
	*                      다운로드 이력 버튼
	----------------------------------------------------------%>
	$("#downloadBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "downloadHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	}),
	<%----------------------------------------------------------
	*                      결재이력 버튼
	----------------------------------------------------------%>
	$("#approveBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("groupware", "historyWork", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
})

</script>

<body>

<form name="viewECOForm" method="post" >
<input type="hidden" name="oid" id="oid" value="<c:out value="${eoData.oid }" />" />
<input type="hidden" name="cmd" id="cmd" value="" />

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
					<td ><img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />&nbsp;<c:out value="${eoData.eoType }"/> ${f:getMessage('상세보기')} </td>
					<td>
					
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
								<td>
									<button type="button" name="versionBtn" id="viewECA" class="btnCustom">
										<span></span>
										${f:getMessage('산출물')}
									</button>
								</td>
								<td>
									<button type="button" name="approveBtn" id="approveBtn" class="btnCustom">
										<span></span>
										${f:getMessage('결재이력')}
									</button>
								</td>
								<td>
									<button type="button" name="downloadBtn" id="downloadBtn" class="btnCustom">
										<span></span>
										${f:getMessage('다운로드이력')}
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
						<col width="13%"><col width="37%"><col width="13%"><col width="37%">
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('제목')}
								</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${eoData.name }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM"><c:out value="${eoData.eoType }"/>${f:getMessage('번호')}</td>
								<td class="tdwhiteL">
									<c:out value="${eoData.number }"/>
								</td>
								<td class="tdblueM"><c:out value="${eoData.eoType }"/>${f:getMessage('상태')}</TD>
								<td class="tdwhiteL">
									<c:out value="${eoData.lifecycle }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM"><c:out value="${eoData.eoType }"/>${f:getMessage('담당자')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${eoData.creator }"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			<!-- 활동 현황 -->
			<jsp:include page="/eSolution/changeECA/include_ECAView.do" flush="false" />
			
			
			<!-- 관련 ECN -->
			<jsp:include page="/eSolution/changeECO/include_ECOView.do" flush="false" />
		</td>
	</tr>
</table>
</form>
</body>
</html>