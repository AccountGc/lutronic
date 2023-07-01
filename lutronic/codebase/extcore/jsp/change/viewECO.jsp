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
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		location.href = getURLString("changeECO", "updateEO", "do") + "?oid="+$("#oid").val();
	}),
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#deleteBtn").click(function () {
		
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){
			return;
		}
		var oid = $("#oid").val();
		confirmSecond(oid);
	}),
	<%----------------------------------------------------------
	*                      산출물 버튼
	----------------------------------------------------------%>
	$("#viewECA").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("changeECA" , "viewECA", "do") + "?oid="+oid;
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
	
	<%----------------------------------------------------------
	*                      결재 회수 버튼
	----------------------------------------------------------%>
	$("#withDrawBtn").click(function() {
		
		var url	= getURLString("common", "withDrawPopup", "do") + "?oid="+$("#oid").val();
		openOtherName(url,"withDrawBtn","400","220","status=no,scrollbars=yes,resizable=yes");
	})
	
	$("#erpSend").click(function() {
		if (!confirm("${f:getMessage('ERP 전송 하시게습니까?')}")){
			return;
		}
		
		var form = $("form[name=viewECOForm]").serialize();
		var url	= getURLString("erp", "erpSendAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('ERP 전송 오류')}");
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
	
	$('#excelDown').click(function() {
		var url = getURLString("changeECO", "excelDown", "do");
		console.log(this.value);
		console.log(url);
		$.ajax({
			type:"POST",
			url: url,
			data:{
				oid : $('#oid').val(),
				eoType : this.value
			},
			dataType:"json",
			async: false,
			cache: false,
			success:function(data){
				console.log(data);
				if(data.result) {
					location.href = '/Windchill/jsp/common/content/FileDownload.jsp?fileName='+data.message+'&originFileName='+data.message;
				}else {
					alert(data.message);
				}
			}
		});
	})
	
	<%----------------------------------------------------------
	*                     일괄 다운로드
	----------------------------------------------------------%>
	$("#batchSecondaryDown").click(function() {
		
		var form = $("form[name=viewECOForm]").serialize();
		var url	= getURLString("common", "batchSecondaryDown", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			
			error:function(data){
				var msg = "${f:getMessage('데이터 검색오류')}";
				alert(msg);
			},
			
			success:function(data){
				console.log(data.message);
				if(data.result) {
					location.href = '/Windchill/jsp/common/content/FileDownload.jsp?fileName='+data.message+'&originFileName='+data.message;
				}else {
					alert(data.message);
				}
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


<%----------------------------------------------------------
*                      BOM 버튼
----------------------------------------------------------%>

function viewBom(oid){
	
	var str = getURLString("part", "PartTree", "do") + "?oid="+oid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBOM", opts+rest);
    newwin.focus();
    
}
function confirmSecond(oid){
	var str = getURLString("common", "deleteCheck", "do") + "?oid="+oid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=500,height=250,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "deleteCheck", opts+rest);
    newwin.focus();
}
</script>

<body>

<form name="viewECOForm" id="viewECOForm" method="post" >
<input type="hidden" name="oid" id="oid" value="<c:out value="${ecoData.oid }" />" />
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
					<td >
						<img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
						<b>
						<c:choose>
							<c:when test= "${ecoData.eoType == 'CHANGE'}">
								ECO
							</c:when>
							<c:otherwise>
								EO
							</c:otherwise>
						</c:choose>
						${f:getMessage('상세보기')}
						</b>
					</td>
					
					<td>
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                
			                	<!-- 회수 권한 승인중 && 소유자 || 관리자 -->
								<c:if test='${ecoData.isWithDraw()}'>
									<td>
										<button type="button" name="withDrawBtn" id="withDrawBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('결재회수')}
										</button>
									</td>
								</c:if>
								<!-- ERP 전송&& 승인됨  && 관리자  && ERP 전송 hisotry 없을시 -->
								<c:if test='${ecoData.isERPSend()}'>
									<td>
										<button type="button" name="erpSend" id="erpSend" class="btnCRUD">
											<span></span>
											${f:getMessage('ERP 전송')}
										</button>
									</td>
								</c:if>
								
			                	<c:if test="${ecoData.isModify() }" >
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
								
								<c:if test="${ecoData.isApproved() }" >
									<c:choose>
										<c:when test= "${ecoData.eoType == 'CHANGE'}">
											<td>
												<button type="button" name="excelDown" id="excelDown" class="btnCustom" value='eco'>
													<span></span>
													Excel
													${f:getMessage('다운')}
												</button>
											</td>
										</c:when>
										<c:otherwise>
											<td>
												<button type="button" name="excelDown" id="excelDown" class="btnCustom" value='eo'>
													<span></span>
													Excel
													${f:getMessage('다운')}
												</button>
											</td>
										</c:otherwise>
									</c:choose>
								</c:if>
								
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
									<c:choose>
										<c:when test= "${ecoData.eoType == 'CHANGE'}">
											ECO${f:getMessage('제목')}
										</c:when>
										
										<c:otherwise>
											EO${f:getMessage('제목')}
										</c:otherwise>
									</c:choose>
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<c:out value="${ecoData.name }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									<c:choose>
										<c:when test= "${ecoData.eoType == 'CHANGE'}">
											ECO${f:getMessage('번호')}
										</c:when>
										
										<c:otherwise>
											EO${f:getMessage('번호')}
										</c:otherwise>
									</c:choose>
								</td>
								
								<td class="tdwhiteL">
									<c:out value="${ecoData.number }"/>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('상태')}
								</TD>
								
								<td class="tdwhiteL">
									<c:out value="${ecoData.getLifecycle() }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록자')}</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.creator }"/>
								</td>
								
								<td class="tdblueM">${f:getMessage('구분')}
								<td class="tdwhiteL">
								<c:out value="${ecoData.getEoTypeDisplay() }"/>
								</td>
								
							</tr>
							<c:if test="${ecoData.eoType eq 'CHANGE'}">
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('인허가변경')}</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.getlicensingDisplay() }" escapeXml="false"/>
									
								</td>
								<td class="tdblueM">${f:getMessage('위험 통제')}</td>
								<td class="tdwhiteL">
							
									<c:out value="${ecoData.getRiskTypeName() }" escapeXml="false"/>
								</td>
							</tr>
							</c:if>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('등록일')}</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.createDate }"/>
								</td>
								<td class="tdblueM">${f:getMessage('수정일')}</td>
								<td class="tdwhiteL">
									<c:out value="${ecoData.modifyDate }"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('승인일')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${ecoData.eoApproveDate }"/>
								</td>
								
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('제품명')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:out value="${ecoData.getModelDisplay() }"/>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">
								<c:choose>
								<c:when test= "${ecoData.eoType == 'CHANGE'}">${f:getMessage('변경사유')}</c:when>
								<c:otherwise>${f:getMessage('제품 설계 개요')}</c:otherwise>
								</c:choose>
								</td>
			                    <td class="tdwhiteL" colspan="3">
			                    	<div class="textarea_autoSize">
										<textarea name="eoCommentA" id="eoCommentA"  readonly><c:out value="${ecoData.commentA }" escapeXml="false" /></textarea>
									</div>
								</td>
							</tr>
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">
								<c:choose>
								<c:when test= "${ecoData.eoType == 'CHANGE'}">${f:getMessage('변경사항 ')}</c:when>
								<c:otherwise>${f:getMessage('특기사항')}</c:otherwise>
								</c:choose>
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentB" id="eoCommentB"  readonly><c:out value="${ecoData.commentB }" escapeXml="false" /></textarea>
									</div>
								</td>
							</tr>
							<c:if test="${ecoData.eoType eq 'CHANGE'}">
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('특기사항')}</td>
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="eoCommentC" id="eoCommentC"  readonly><c:out value="${ecoData.commentC }" escapeXml="false" /></textarea>
									</div>
								</td>
							</tr>
							</c:if>
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('기타사항')}</td>
								<td class="tdwhiteL" colspan="3">
									<c:choose>
									<c:when test= "${ecoData.eoType == 'CHANGE'}">
										<div class="textarea_autoSize">
											<textarea name="eoCommentD" id="eoCommentD"  readonly><c:out value="${ecoData.commentD }" escapeXml="false" /></textarea>
										</div>
									</c:when>
									<c:otherwise>
										<div class="textarea_autoSize">
											<textarea name="eoCommentC" id="eoCommentC"  readonly><c:out value="${ecoData.commentC }" escapeXml="false" /></textarea>
										</div>
									</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<c:if test='${ecoData.eoType eq "CHANGE"}'>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('설계변경 부품 내역파일')}</td>
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFileView">
										<jsp:param value="ECO" name="type"/>
										<jsp:param value="${ecoData.oid }" name="oid"/>
									</jsp:include>
								</td>
							</tr>
							</c:if>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('첨부파일')}
									<br>
									<button type="button" name="batchSecondaryDown" id="batchSecondaryDown" class="btnCustom">
										<span></span>
										${f:getMessage('일괄 다운')}
									</button>
									
								</td>					
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFileView">
										<jsp:param value="${ecoData.oid }" name="oid"/>
									</jsp:include>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			<!-- 활동 현황 -->
			<jsp:include page="/eSolution/changeECA/include_ECAView.do" flush="false" />
			
			<!-- 완제품 품목 -->
			<jsp:include page="/eSolution/changeECO/include_CompletePartView.do" flush="false" />
			
			<c:if test="${ecoData.eoType eq 'CHANGE'}">
			
			<!-- 대상 품목 -->
			<jsp:include page="/eSolution/changeECO/include_ChangePartView.do" flush="false" />
			
			<!-- 설계변경내역 -->
			<!--jsp:include page="/eSolution/changeECO/include_EulView.do" flush="false" /-->
			
			<!-- 관련 ECR -->
			<jsp:include page="/eSolution/changeECR/include_ECRView.do" flush="false" />
			
			<!-- 관련 ECN -->
			<!--jsp:include page="/eSolution/changeECN/include_ECNView.do" flush="false" /-->
			</c:if>
		</td>
	</tr>
</table>
</form>
</body>
</html>