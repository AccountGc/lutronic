<%@page contentType="text/html; charset=UTF-8"%>

<link rel="stylesheet" href="/Windchill/extcore/jsp/css/e3ps.css" type="text/css">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<script language="javascript" src="/Windchill/jsp/js/common.js"></script>

<script type="text/javascript">

$(function() {
	<%----------------------------------------------------------
	*                      수정버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function() {
		if($.trim($("input[name='location']").val()) == '/Default/Document') {
			alert("${f:getMessage('문서분류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($.trim($("#documentName").val()) == '') {
			alert("${f:getMessage('문서종류')}${f:getMessage('을(를) 입력하세요.')}");
			$("#documentName").focus();
			return;
		}
		
		if($("#documentType").val() == '') {
			alert("${f:getMessage('문서분류')}${f:getMessage('을(를) 선택하세요.')}");
			$("#documentType").focus();
			return;
		}
		
		if(!textAreaLengthCheckId('description','2000','${f:getMessage('설명')}')){
			return;
		}
		
		if($("#preseration").val() == '') {
			alert("${f:getMessage('보존기간')}${f:getMessage('을(를) 선택하세요.')}");
			$("#preseration").focus();
			return;
		}
		
		if($("#moldtype").val() == '') {
			alert("${f:getMessage('금형타입 ')}${f:getMessage('을(를) 선택하세요.')}");
			$("#moldtype").focus();
			return;
		}
		
		if($("#PRIMARY").length == 0 && $("#PRIMARY_delocIds").length == 0) {
			alert("${f:getMessage('주 첨부파일')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if (confirm("${f:getMessage('수정하시겠습니까?')}")){
			var form = $("form[name=documentModifyForm]").serialize();
			var url	= getURLString("doc", "updateDocumentAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data:form,
				dataType:"json",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('등록 오류')}";
					alert(msg);
				},

				success:function(data){
					if(data.result) {
						alert("${f:getMessage('수정 성공하였습니다.')}");
						location.href = getURLString("doc", "viewDocument", "do") + "?oid="+data.oid;
					}else {
						alert("${f:getMessage('수정 실패하였습니다.')} \n" + data.message);
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
	$("input[name=documentName]").keyup(function (event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		if((charCode == 38 || charCode == 40) ) {
			if(!$( "#"+this.id+"Search" ).is( ":hidden" )){
				var isAdd = false;
				if(charCode == 38){
					isAdd = true;
				}
				moveDocumentNameFocus(this.id, isAdd);
			}
		} else if(charCode == 13 || charCode == 27){
			$("#" + this.id + "Search").hide();
		} else {
			autoSearchDocumentName(this.id, this.value);
		}
		
	})
	
	$("input[name=documentName]").focusout(function () {
		$("#" + this.id + "Search").hide();
	})
})

<%----------------------------------------------------------
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.moveDocumentNameFocus = function(id,isAdd) {
	var removeCount = 0;
	var addCount = 0;
	var l = $("#" + id + "UL li").length;
	for(var i=0; i<l; i++){
		var cls = $("#" + id + "UL li").eq(i).attr('class');
		if(cls == 'hover') {
			$("#" + id + "UL li").eq(i).removeClass("hover");
			removeCount = i;
			if(isAdd){
				addCount = (i-1);
			}else if(!isAdd) {
				addCount = (i+1);
			}
			break;
		}
	}
	if(addCount == l) {
		addCount = 0;
	}
	$("#" + id + "UL li").eq(addCount).addClass("hover");
	$("#" + id).val($("#" + id + "UL li").eq(addCount).text());
}

<%----------------------------------------------------------
*                      품목명 입력시 이름 검색
----------------------------------------------------------%>
window.autoSearchDocumentName = function(id, value) {
	var codeType = id.toUpperCase();
	var data = common_autoSearchName(codeType, value);
	addSearchList(id, eval(data.responseText), false);
}

<%----------------------------------------------------------
*                      품목명 입력시 데이터 리스트 보여주기
----------------------------------------------------------%>
window.addSearchList = function(id, data, isRemove) {
	$("#" + id + "UL li").remove();
	if(isRemove) {
		$("#" + this.id + "Search").hide();
	}else {
		if(data.length > 0) {
			$("#" + id + "Search").show();
			for(var i=0; i<data.length; i++) {
				$("#" + id + "UL").append("<li title='" + id + "' class=''>" + data[i].name);
			}
		}else {
			$("#" + id + "Search").hide();
		}
	}
}

<%----------------------------------------------------------
*                      품목명 데이터 마우스 올렸을때
----------------------------------------------------------%>
$(document).on("mouseover", 'div > ul > li', function() {
	var partName = $(this).attr("title");
	$(this).addClass("hover");
	$("#" + partName).val($(this).text());
})

<%----------------------------------------------------------
*                      품목명 데이터 마우스 뺄때
----------------------------------------------------------%>
$(document).on("mouseout", 'div > ul > li', function() {
	$(this).removeClass("hover");
})
</script>

<style>
.hover{ 
 	  cursor: default;
      background:#dedede;
}
</style>

<form name="documentModifyForm" id="documentModifyForm" method="post" enctype="multipart/form-data">

<input type="hidden" name="oid"  id="oid" value="<c:out value="${docData.oid }" />" />
<c:if test="${docData.getDocumentType() eq '금형문서'}">
<input type="hidden" name="location" 		id="location" 				value="/Default/금형문서">
<input type="hidden" name="documentName" 		id="documentName" 		value="documentName">
</c:if>


<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr align="center" height="5">
		<td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
				</tr>
			</table>
		
			<table width="100%" border="0" cellpadding="0" cellspacing="3" align="center">
				<tr>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                    <td align="center">
									<table>
										<tr>
											<td>
												<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
													<span></span>
													${f:getMessage('수정')}
												</button>
											</td>
											
											<td>
												<button type="button" name="approveBtn" id="approveBtn" class="btnCustom" onclick="javascript:history.back();">
													<span></span>
													${f:getMessage('이전페이지')}
												</button>
											</td>
										</tr>
									</table>
								</td>
							</tr>
			            </table>
					</td>
				</tr>
				
				<tr>
					<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>${f:getMessage('문서')}${f:getMessage('정보')}</b></td>
				</tr>
				
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan=2>
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
							<tr><td height=1 width=100%></td></tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
							<colgroup>
								<col width="150">
								<col width="350">
								<col width="150">
								<col width="350">
							</colgroup>
			
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM" colspan="4">
									<b> 
										<c:out value="${docData.number }" /> 
										[<c:out value="${docData.name }" />]
									</b>
								</td>
							</tr>
							
							<c:if test="${docData.getDocumentType() ne '금형문서'}">
								<tr bgcolor="ffffff" height="35">
									<td class="tdblueM">${f:getMessage('문서분류')} <span class="style1">*</span></td>
									<td class="tdwhiteL" colspan="3">
										<jsp:include page="/eSolution/folder/include_FolderSelect.do">
											<jsp:param value="/Default/Document" name="root"/>
											<jsp:param value="/Default${docData.location }" name="folder"/>
										</jsp:include>
									</td>
								</tr>
							</c:if>
							
							<tr bgcolor="ffffff" height="35">
							
								<c:choose>
									<c:when test="${docData.getDocumentType() ne '금형문서' }">
										<td class="tdblueM">
											${f:getMessage('문서종류')}
											<span class="style1">*</span>
										</td>
										
										<td class="tdwhiteL" >
											<input name="documentName" id="documentName" class="txt_field" size="85" style="width:90%" maxlength="80" value="<c:out value="${docData.getDocumentName(1) }" />"/>
						
											<div id="documentNameSearch" style="display: none; border: 1px solid black ; position: absolute; background-color: white; z-index: 1;">
												<ul id="documentNameUL" style="list-style-type: none; padding-left: 5px; text-align: left; ">
												</ul>
											</div>
										</td>
										
										<td class="tdblueM">
											${f:getMessage('문서명')}
										</td>
										
										<td class="tdwhiteL" >
											<input name="docName" id="docName" class="txt_field" size="85" maxlength="80" engnum="engnum" style="width:90%" value="<c:out value="${docData.getDocumentName(2) }" />"/>
										</td>
									</c:when>
									
									<c:otherwise>
										<td class="tdblueM">
											${f:getMessage('문서명')}
											<span class="style1">*</span>
										</td>
										<td class="tdwhiteL" >
											<input name="docName" id="docName" class="txt_field" size="85" maxlength="80" engnum="engnum" style="width:90%" value="<c:out value="${docData.name }" />"/>
										</td>
									</c:otherwise>
								
								</c:choose>
							
								
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('문서')}${f:getMessage('설명')}</td>
								<td class="tdwhiteL" colspan="3">
									<div class="textarea_autoSize">
										<textarea name="description" id="description" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('설명')}')"><c:out value="${docData.getDescription(false) }" escapeXml="false"/></textarea>
									</div>
								</td>
							</tr>
							
							<tr>
								<td class="tdblueM">${f:getMessage('수정사유')}</td>
								<td class="tdwhiteL" colspan="3">
								<textarea name="iterationNote" id="iterationNote" cols="80" rows="2" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('iterationNote', '100', '${f:getMessage('수정사유')}')"></textarea>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('주 첨부파일')} <span class="style1">*</span></td>
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
										<jsp:param name="formId" value="documentModifyForm"/>
										<jsp:param name="type" value="PRIMARY"/>
										<jsp:param name="oid" value="${docData.oid }"/>
										<jsp:param name="btnId" value="updateBtn" />
									</jsp:include>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('첨부파일')}</td>
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
										<jsp:param name="formId" value="documentModifyForm"/>
										<jsp:param name="oid" value="${docData.oid }"/>
										<jsp:param name="btnId" value="updateBtn" />
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

<!-- 속정 정보 -->
<jsp:include page="/eSolution/common/include_createAttributes.do">
	<jsp:param value="doc" name="module"/>
	<jsp:param value="${f:getMessage('속성 정보')}" name="title"/>
	<jsp:param value="${docData.oid }" name="oid"/>
</jsp:include>

<!-- 관련 품목 -->

<c:choose>
	<c:when test="${docData.getDocumentType() eq '금형문서' }">	
	<jsp:include page="/eSolution/part/include_PartSelect.do" flush="false" >
		<jsp:param name="moduleType" value="doc"/>
		<jsp:param name="title" value="${f:getMessage('관련 품목')}"/>
		<jsp:param name="paramName" value="partOid"/>
		<jsp:param name="oid" value="${docData.oid }"/>
		<jsp:param name="state" value="APPROVED"/>
	</jsp:include>
	</c:when>
	<c:otherwise>
	<jsp:include page="/eSolution/part/include_PartSelect.do" flush="false" >
		<jsp:param name="moduleType" value="doc"/>
		<jsp:param name="title" value="${f:getMessage('관련 품목')}"/>
		<jsp:param name="paramName" value="partOid"/>
		<jsp:param name="oid" value="${docData.oid }"/>
	</jsp:include>
	</c:otherwise>
</c:choose>



<!-- 관련 문서 -->
<jsp:include page="/eSolution/doc/include_DocumentSelect.do">
	<jsp:param value="${f:getMessage('관련 문서')}" name="title"/>
	<jsp:param value="docOid" name="paramName"/>
	<jsp:param name="oid" value="${docData.oid }"/>
	<jsp:param value="doc" name="moduleType"/>
</jsp:include>
</form>
