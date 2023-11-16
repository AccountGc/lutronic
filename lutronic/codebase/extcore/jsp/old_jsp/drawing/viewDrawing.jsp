<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
$(function() {
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#deleteBtn").click(function () {
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){ return; }
		var form = $("form[name=drawingViewForm]").serialize();
		var url	= getURLString("drawing", "deleteDrwaingAction", "do");
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
				}else {
					
				}
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
	}),
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		location.href = getURLString("drawing", "updateDrawing", "do") + "?oid="+$("#oid").val();
	}),
	<%----------------------------------------------------------
	*                      버전이력 버튼
	----------------------------------------------------------%>
	$("#versionBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "versionHistory", "do") + "?oid=" + oid;
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
	
	$("#lastestBtn").click(function() {
		var oid = this.value;
		openView(oid);
	})
	$("#updateName").click(function() {
		var oid = this.value;
		if (!confirm("${f:getMessage('이름 동기화 하겠습니까?')}")){ return; }
		var form = $("form[name=drawingViewForm]").serialize();
		var url	= getURLString("drawing", "updateNameAction", "do");
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
				alert(data.message );
				if(data.result) {
					location.reload();
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



function createCDialogWindow(dialogURL, dialogName, w, h, statusBar)
{
	if( typeof(_use_wvs_cookie) != "undefined" ) {
		var cookie_value = document.cookie;
		if( cookie_value != null ) {
			var loc = cookie_value.indexOf("wvs_ContainerOid=");
			if( loc >= 0 ) {
				var subp = cookie_value.substring(loc+4);
				loc = subp.indexOf(";");
				if( loc >= 0 ) subp = subp.substring(0, loc);
				dialogURL += "&" + subp;
			}
		}
	}else {
		var vm_url = "" + document.location;
		if( vm_url != null ) {
			var loc = vm_url.indexOf("ContainerOid=");
			if( loc >= 0 ) {
				var subp = vm_url.substring(loc);
				loc = subp.indexOf("&");
				if( loc >= 0 ) subp = subp.substring(0, loc);
				dialogURL += "&" + subp;
			}
		}
	}

	openWindow(dialogURL, dialogName, w, h, statusBar);
}

</script>
<body>

<form name="drawingViewForm">

<input type="hidden" name="oid" id="oid" value="<c:out value="${epmData.oid }" />" />

<div name="imgView" id="imgView" style="visibility=hidden; position:absolute; left:78px; top:165px; width:400px; height:62px; z-index:1; border-width:1px; border-style:none; filter:progid:DXImageTransform.Microsoft.Shadow(color=#4B4B4B,Direction=135,Strength=3);border-color:black;"></div>
<!-- 버튼 테이블 -->
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
        <td>
            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center">
			   			<B>
				   			<font color="white">
				   				${f:getMessage('도면')}
				   				${f:getMessage('상세보기')}
				   			</font>
			   			</B>
		   			</td>
		   		</tr>
			</table>
            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
            	<tr>
					<td width="150">
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						<b>
							${f:getMessage('도면')}
							${f:getMessage('상세보기')}
						</b>
					</td>
					
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
				        	<tr>
				        		<c:if test="${epmData.isNameSyschronization() }">
				        			<td>
	                    				<button type="button" name="updateName" id="updateName" class="btnCRUD" value="<c:out value='${epmData.lepmOid }'/>">
											<span></span>
											${f:getMessage('Name 동기화')}
										</button>
			                    	</td>
				        		</c:if>
				        
				        		<c:if test="${epmData.getUpdate() }">
			                    	<td>
	                    				<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('수정')}
										</button>
			                    	</td>
			                    </c:if>
			                    
			                    <c:if test="${epmData.getUpdate() }">
			                    	<td>
	                    				<button type="button" name="deleteBtn" id="deleteBtn" class="btnCRUD">
											<span></span>
											${f:getMessage('삭제')}
										</button>
			                    	</td>
			                    </c:if>
			                    
			                    <c:if test="${epmData.getLatest() }">
			                		<td>
										<button type="button" name="latestBtn" id="lastestBtn" class="btnCustom" value="<c:out value='${epmData.lepmOid }'/>">
											<span></span>
											${f:getMessage('최신버전')}
										</button>
									</td>
			                	</c:if>
			                    
			                    <td>
									<button type="button" name="versionBtn" id="versionBtn" class="btnCustom">
										<span></span>
										${f:getMessage('버전이력')}
									</button>
								</td>
								
								<td>
									<button type="button" name="downloadBtn" id="downloadBtn" class="btnCustom">
										<span></span>
										${f:getMessage('다운로드이력')}
									</button>
								</td>
								
								<c:if test="${epmData.getApprove() }">
									<td>
										<button type="button" name="approveBtn" id="approveBtn" class="btnCustom">
											<span></span>
											${f:getMessage('결재이력')}
										</button>
									</td>
								</c:if>
								
								<td>
		                			<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
		                				<span></span>
		                				${f:getMessage('닫기')}
		                			</button>
		                		</td>
							</tr>
				    	</table>
					</td>
				</tr>
            </table>
    	</td>
	</tr>
</table>

<!-- 도면 정보 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height="1" width="100%"></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			<col width="110"/><col width="240"/><col width="110"/><col width="240"/><col width="240"/>
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM" colspan="5">
						<b>${epmData.icon }[<c:out value="${epmData.name }" />]</b>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('도면')}${f:getMessage('번호')}
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.number }" />
					</td>
					
					<td class="tdblueM">
						${f:getMessage('도면분류')}
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.getLocation() }" />
					</td>
					
					<td class="tdwhiteL" rowspan="5" >
					 	<jsp:include page="/eSolution/drawing/thumbview.do" flush="true">
							<jsp:param name="oid" value='${epmData.oid}'/>
						</jsp:include>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('상태')}
					</td>				
						
					<td class="tdwhiteL" >
						<c:out value="${epmData.getState() }" />
					</td>
					
					<td class="tdblueM">
						Rev.
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.getVersion() }" />.<c:out value="${epmData.iteration }" />
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('등록자')}
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.creator }" />
					</td>
					
					<td class="tdblueM">
						${f:getMessage('수정자')}
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.modifier }" />
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('등록일')}
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.createDate }" />
					</td>
					<td class="tdblueM">
						${f:getMessage('수정일')}
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.modifyDate }" />
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('도면구분')}
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.getCadType() }" />
					</td>
					
					<td class="tdblueM">
						${f:getMessage('도면파일')}
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${epmData.getCADName() }" escapeXml="false"/>
					<!--  
						<jsp:include page="/eSolution/content/includeAttachFileView">
							<jsp:param value="p" name="type"/>
							<jsp:param value="${epmData.oid }" name="oid"/>
						</jsp:include>
						-->
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('주 부품')}
					</td>
					
					<td class="tdwhiteL" title="<c:out value="${epmData.getpName() }" />" >
						<a href="javascript:openView('<c:out value="${epmData.getpOid() }" />')" > 
							<c:out value="${epmData.getpNum() }" />
						</a>
                    </td>
                    
                    <td class="tdblueM">
                    	ApplicationType
                    </td>
                    
					<td class="tdwhiteL" colspan="2">
						<c:out value="${epmData.getApplicationType() }" escapeXml="false" />
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						dxf
					</td>
					
					 <td class="tdwhiteL" colspan="4" style="word-break:break-all;">
						<c:out value="${epmData.getPDFFile() }" escapeXml="false"/>
					</td>
				</tr>
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					
					 <td class="tdwhiteL" colspan="4" style="word-break:break-all;">
						<jsp:include page="/eSolution/content/includeAttachFileView">
							<jsp:param value="${epmData.oid }" name="oid"/>
						</jsp:include>
					</td>
				</tr>
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('도면')}${f:getMessage('설명')}
					</td>
					
                    <td class="tdwhiteL" colspan="4" style="word-break:break-all;">
                    	<div class="textarea_autoSize">
                    	<textarea name="description" id="description"  readonly><c:out value="${epmData.getDescription() }" escapeXml="false" /></textarea>
                    	</div>
                    </td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 속성 정보 -->
<c:if test="${!epmData.isCreoDrawing() }">
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>${f:getMessage('속성')}</b>
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
	     	<jsp:include page="/eSolution/common/include_Attributes.do">
				<jsp:param value="${epmData.oid }" name="oid"/>
				<jsp:param value="drawing" name="module"/>
				<jsp:param value="view" name="mode"/>
			</jsp:include>
		</td>
	</tr>
</table>
</c:if>
<!-- 참조 -->
<jsp:include page="/eSolution/drawing/include_Reference.do">
	<jsp:param name="moduleType" value="drawing" />
	<jsp:param name="title" value="${f:getMessage('참조')}" />
	<jsp:param name="oid" value="${epmData.oid }" />
</jsp:include>

<!-- 참조 항목  -->
<jsp:include page="/eSolution/drawing/include_ReferenceBy.do">
	<jsp:param value="${epmData.oid }" name="oid"/>
</jsp:include>

<!-- 참조 품목 -->
<jsp:include page="/eSolution/part/include_PartView.do" flush="false" >
	<jsp:param name="moduleType" value="drawing"/>
	<jsp:param name="title" value="${f:getMessage('참조품목')}"/>
	<jsp:param name="oid" value="${epmData.oid }"/>
</jsp:include>

<!-- 관련 개발업무 -->
<jsp:include page="/eSolution/development/include_DevelopmentView.do">
	<jsp:param value="drawing" name="moduleType"/>
	<jsp:param value="${epmData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련 개발업무')}" name="title"/>
</jsp:include>

<c:if test="${isAdmin }">

<!-- 관리자 속성 -->
<jsp:include page="/eSolution/common/include_adminAttribute.do">
	<jsp:param name="module" value="drawing"/>
	<jsp:param name="oid" value="${epmData.oid }"/>
</jsp:include>

</c:if>

</form>

</body>
</html>