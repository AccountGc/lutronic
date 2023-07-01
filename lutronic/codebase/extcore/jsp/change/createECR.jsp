<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript" src="/Windchill/jsp/js/eo.js" ></script>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	numberCodeList('CHANGESECTION', '');
	gfn_InitCalendar("createDate", "createDateBtn");
	gfn_InitCalendar("approveDate", "approveDateBtn");
	
})

window.numberCodeList = function(id, parentCode1) {
	var type = id.toUpperCase();
	var data = common_numberCodeList(type, parentCode1, false);
	
	addSelectList(id, eval(data.responseText));
}
<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
function addSelectList(type,data){
	var id = "changeSection";
	
	for(var i=0; i<data.length; i++) {
		
		$("#"+ id).append("<input type='checkbox' name="+id+" id="+id+" value='" + data[i].code + "'>"+ data[i].name);
	}
	
}

$(function() {
	<%----------------------------------------------------------
	*                      등록 버튼
	----------------------------------------------------------%>
	$("#createBtn").click(function() {
		
		if($.trim($("#name").val()) == "" ) {
			alert("CR/ECPR ${f:getMessage('제목')} ${f:getMessage('을(를) 입력하세요.')}");
			$('#name').focus();
			return;		
		}
		
		if($.trim($("#number").val()) == "" ) {
			alert("CR/ECPR ${f:getMessage('번호')} ${f:getMessage('을(를) 입력하세요.')}");
			$('#number').focus();
			return;		
		}
		
		if($("input[name='model']").size() == 0 ){
			alert(" ${f:getMessage('제품명')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentA','2000','${f:getMessage("변경 사유")}')){
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentB','2000','${f:getMessage("변경 사항")}')){
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentC','2000','${f:getMessage("참고 사항")}')){
			return;
		}
		
		/*
		gfn_StartShowProcessing();
		$("#changeCreateForm").attr("action", getURLString("changeECR", "createECRtAction", "do")).submit()
		*/
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("changeECR", "createECRtAction", "changeCreateForm", "listECR").submit();
		}
	}),
	<%----------------------------------------------------------
	*                      목록버튼
	----------------------------------------------------------%>
	$("#listBtn").click(function() {
		location.href = getURLString("change", "listECR", "do");
	})
	
	<%----------------------------------------------------------
	*                      제품  추가 버튼
	----------------------------------------------------------%>
	$("#addNumberCode").click(function() {
		
		var url = getURLString("common", "popup_numberCodeList", "do")+ "?codeType=MODEL&disable=true";
		openOtherName(url,"selectCodePopup","800","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      제품  삭제 버튼
	----------------------------------------------------------%>
	$("#delNumberCode").click(function() {
		
		delteNumberCode();
	})
	
	$('#resetBtn').click(function() {
		resetFunctionECR();
		deleteAllCode();
	})
	
})


</script>
<body>

<form name=changeCreateForm id="changeCreateForm" method="post" style="padding:0px;margin:0px" enctype="multipart/form-data">
<input type="hidden" name="cmd"		id="cmd"		 	value="save"     />
<input type="hidden" name="eoType"		id="eoType"		 	value="ECR"     />
<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp; CR/ECPR ${f:getMessage('관리')} > CR/ECPR ${f:getMessage('등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>


<table width="100%" border="0" cellpadding="0" cellspacing="3">
    <tr align="center">
        <td valign="top" style="padding:0px 0px 0px 0px">
            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
                <tr><td height="1" width="100%"></td></tr>
            </table>
            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	            <tr>
	            	<td width="20%"></td>
					<td width="30%"></td>
					
					<td width="20%"></td>
					<td width="30%"></td>
					
	            </tr>
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">
						CR/ECPR${f:getMessage('제목')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" >
						<input name="name" id="name" class="txt_field" size="85" style="width:98%"/>
					</td>
					
					<td class="tdblueM">
						CR/ECPR${f:getMessage('번호')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" >
						<input name="number" id="number" class="txt_field" size="85" style="width:98%"/>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('작성일')}
					</td>
					
					<td class="tdwhiteL" >
						<input name="createDate" id="createDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="createDateBtn" id="createDateBtn" ></a>
						<a href="JavaScript:clearText('createDate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('승인일')}
					</td>
					
					<td class="tdwhiteL" >
						<input name="approveDate" id="approveDate" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						<a href="javascript:void(0);"><img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="approveDateBtn" id="approveDateBtn" ></a>
						<a href="JavaScript:clearText('approveDate')"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('작성부서')}
					</td>
					
					<td class="tdwhiteL" >
						<input name="createDepart" id="createDepart" class="txt_field" size="85" style="width:98%"/>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('작성자')}
					</td>
					
					<td class="tdwhiteL" >
						<input name="writer" id="writer" class="txt_field" size="85" style="width:98%"/>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						제품명
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<table border="0" cellpadding="0" cellspacing="2" width="100%">
							<tr align="left">
								<td>
									<button type="button" name="addNumberCode" id="addNumberCode" class="btnCustom">
										<span></span>
										${f:getMessage('추가')}
									</button>
									
									<button type="button" name="delNumberCode" id="delNumberCode" class="btnCustom">
										<span></span>
										${f:getMessage('삭제')}
									</button>
								</td>
							</tr>
							
							<tr>
								<td>
									<div id='modeltable'></div>	
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('제안자')}
					</td>
					
					<td class="tdwhiteL" >
						<input name="proposer" id="proposer" class="txt_field" style="width:98%"/>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('변경구분')}
					</td>
					
					<td class="tdwhiteL"  valign="bottom">
						<div id='changeSection' ></div>	
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('변경사유')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="eoCommentA" id="eoCommentA" rows="10" onchange="textAreaLengthCheckName('eoCommentA', '4000', '${f:getMessage('변경 사유')}')"></textarea>
						</div>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('변경사항')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="eoCommentB" id="eoCommentB" rows="10" onchange="textAreaLengthCheckName('eoCommentB', '4000', '${f:getMessage('변경사항')}')"></textarea>
						</div>
					</td>
				</tr>
				<tr bgcolor="ffffff" height="35">	
					<td class="tdblueM">
						${f:getMessage('관련')}
						CR/ECPR
					</td>
					
	             	<td class="tdwhiteL" colspan="3">
	             	<jsp:include page="/eSolution/changeECR/include_ECRSelect.do" flush="false" />
	             		
					</td>
				</tr>
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('참고사항')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="eoCommentC" id="eoCommentC" rows="10" onchange="textAreaLengthCheckName('eoCommentC', '4000', '${f:getMessage('참고사항')}')"></textarea>
						</div>
					</td>
				</tr>
				
				<tr>
					<td class="tdblueM">
						${f:getMessage('주 첨부파일')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="changeCreateForm"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="type" value="ECR"/>
							<jsp:param name="btnId" value="createBtn" />
						</jsp:include>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="changeCreateForm"/>
						</jsp:include>
					</td>
				</tr>
			</table>
			<!-- 버튼 -->
			<table width="100%" border="0" cellpadding="0" cellspacing="3" >
				<tr>
					<td align="center" colspan="2">
						<table border="0" cellpadding="0" cellspacing="4" align="center">
							<tr>
								<td>
									<button type="button" class="btnCRUD" title="${f:getMessage('등록')}" id="createBtn" name="createBtn">
					                  	<span></span>
					                  	${f:getMessage('등록')}
				                  	</button>
								</td>
								
								<td>
									<button title="Reset" class="btnCustom" type="reset" name="resetBtn" id="resetBtn">
										<span></span>
										${f:getMessage('초기화')}
									</button>
								</td>
								
								<td>
									<button title="${f:getMessage('목록')}" class="btnCustom" type="button" name="listBtn" id="listBtn">
										<span></span>
										${f:getMessage('목록')}
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

</form>

</body>
</html>