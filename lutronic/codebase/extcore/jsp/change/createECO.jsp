<%@page import="com.e3ps.common.iba.AttributeKey.ECOKey"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript" src="/Windchill/jsp/js/eo.js" ></script>
<script type="text/javascript">
var searchUserId = "";				// 담당자 검색시 ID를 저장하기 위한 변수
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	numberCodeList('LICENSING', '');
	
	
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
	var id = "licensing";
	
	$("#"+ id).append("<input type='radio' name="+id+" id="+id+" value=''> 해제");
	for(var i=0; i<data.length; i++) {
		
		$("#"+ id).append("<input type='radio' name="+id+" id="+id+" value='" + data[i].code + "'>"+ data[i].name);
	}
	
}
$(function() {
	
	
	<%----------------------------------------------------------
	*                      등록
	----------------------------------------------------------%>
	$("#saveNewProduct").click(function () {
		
		if($.trim($("#name").val()) == "") {
			alert("${f:getMessage('제목')} ${f:getMessage('을(를) 입력하세요.')}");
			$("#name").focus();
			return false;
		}
		
		/*
		if($("input[name='model']").size() == 0 ){
			alert(" ${f:getMessage('제품명')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($.trim($("#completeOid").size())  == 0 ) {
			alert("${f:getMessage('완제품 품번')} ${f:getMessage('을(를) 선택하세요.')}");
			return;		
		}
		*/
		
		if(!textAreaLengthCheckId('eoCommentA','3000','${f:getMessage("변경 사유")}')){
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentB','3000','${f:getMessage("변경 사항")}')){
			return;
		}
		
		if($.trim($("#partOid").size()) == 0) {
			//alert("${f:getMessage('설계변경 부품')} ${f:getMessage('을(를) 선택하세요.')}");
			//return;		
		}
		
		if(!textAreaLengthCheckId('eoCommentC','3000','${f:getMessage("특기사항")}')){
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentD','3000','${f:getMessage("기타사항")}')){
			return;
		}
		
		if(!checkActivity()){
			return;
		}
		
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("changeECO", "createECOAction", "createECO", "listECO");
		}
		
	})
	
	<%----------------------------------------------------------
	*                      제품  추가 버튼
	----------------------------------------------------------%>
	$("#addNumberCode").click(function() {
		
		var url = getURLString("common", "popup_numberCodeList", "do")+ "?codeType=MODEL";
		openOtherName(url,"selectCodePopup","800","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      제품  삭제 버튼
	----------------------------------------------------------%>
	$("#delNumberCode").click(function() {
		
		delteNumberCode();
	})
		
	$("#download").click(function() {
		location="/Windchill/jsp/change/eco_part.xlsx";
	})
	$('#listBtn').click(function() {
		location.href = getURLString("changeECO", "listECO", "do");
	})
	
	$('#resetBtn').click(function() {
		resetFunctionECO();
		deleteAllCode();
	})
})


</script>

<body>

<form name=createECO id="createECO" method="post" style="padding:0px;margin:0px" enctype="multipart/form-data">
<input type="hidden" name="cmd" id="cmd" value="save" />
<input type=hidden name=eoType value='CHANGE'>
<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							${f:getMessage('ECO')}
							${f:getMessage('관리')}
							>
							${f:getMessage('ECO')}
							${f:getMessage('등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>


<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
<tr  height=5><td>

<table width="100%" border="0" cellpadding="0" cellspacing="0">

	<tr align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
				<tr><td height=1 width=100%></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
				<tr>
	            	<td width="20%"></td>
					<td width="30%"></td>
					<td width="20"></td>
					<td width="30%"></td>
			    </tr>

				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM" >
						ECO${f:getMessage('제목')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<input name="name" id="name" class="txt_field" size="85" style="width:98%"   value="" />
					</td>
				</tr>
				<tr>	
					<td class="tdblueM">
						${f:getMessage('관련')}
						CR/ECPR
					</td>
					
	             	<td class="tdwhiteL" colspan="3">
	             	<jsp:include page="/eSolution/changeECR/include_ECRSelect.do" flush="false" />
	             		
					</td>
				</tr>
				<!--  
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">제품명<span class="style1">*</span></td>
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
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">${f:getMessage('완제품 품목')}<span style="color:red;">*</span></td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/part/include_CompletePartSelect.do" flush="false" >
							<jsp:param name="moduleType" value="CompletePart"/>
							<jsp:param name="mode" value="mutil" />
							<jsp:param name="paramName" value="CompletePartOid"/>
							<jsp:param name="form" value="simple" />
							<jsp:param name="isBom" value="false" />
							<jsp:param name="selectBom" value="false" />
							
						</jsp:include>
					</td>
				</tr>
				-->
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
							<textarea name="eoCommentB" id="eoCommentB" rows="10" onchange="textAreaLengthCheckName('eoCommentB', '4000', '${f:getMessage('변경 사항')}')"></textarea>
						</div>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">
						${f:getMessage('설계변경 부품')}
						<!-- <span style="color:red;">*</span> -->
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/part/include_PartSelect.do" flush="false" >
							<jsp:param name="moduleType" value="ECO"/>
							<jsp:param name="mode" value="mutil" />
							<jsp:param name="paramName" value="partOid"/>
							<jsp:param name="form" value="simple" />
							<jsp:param name="isBom" value="true" />
							<jsp:param name="selectBom" value="true" />
							
						</jsp:include>
						<br>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">
						${f:getMessage('인허가변경')}
					</td>
					
					<td class="tdwhiteL" >
						<div id='licensing'></div>	
					</td>
					<td class="tdblueM">
						${f:getMessage('위험 통제')}
					</td>
					
					<td class="tdwhiteL" >
						<div id='riskType'></div>
						<input type='radio' name="riskType" id="riskType" value=''> 해제	
						<input type='radio' name="riskType" id="riskType" value='0'> 불필요
						<input type='radio' name="riskType" id="riskType" value='1'> 필요 
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">
						${f:getMessage('특기사항')}
					</td>
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="eoCommentC" id="eoCommentC" rows="5" onchange="textAreaLengthCheckName('eoCommentB', '4000', '${f:getMessage('특기사항')}')"></textarea>
						</div>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">
						${f:getMessage('기타사항')}
						
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="eoCommentD" id="eoCommentD" rows="5" onchange="textAreaLengthCheckName('eoCommentB', '4000', '${f:getMessage('기타사항')}')"></textarea>
						</div>
					</td>
				</tr>
				
				<tr>
					<td class="tdblueM">
						${f:getMessage('설계변경 부품 내역파일')}
						<button type="button" class="btnCustom" id="download">
       						<span></span>
       						${f:getMessage('양식다운')}
       					</button>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="createECO"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="type" value="ECO"/>
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
							<jsp:param name="formId" value="createECO"/>
						</jsp:include>
					</td>
				</tr>
				<tr bgcolor="ffffff" height="35" >
					<td class="tdwhiteL" colspan="4">
						<!-- 활동 현황 추가 -->
						<jsp:include page="/eSolution/changeECA/include_CreateActivity.do" flush="false" />
	                </td>
                </tr>
                
			</table>
		</td>
	</tr>
</table>
		</td>
	</tr>
</table>

<table border="0" cellpadding="0" cellspacing="4" align="center">
    <tr>
      	<td> 
      		<button type="button" class="btnCRUD" id="saveNewProduct">
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
			<button type='button' title="${f:getMessage('목록')}" class="btnCustom" name="listBtn" id="listBtn">
				<span></span>
				${f:getMessage('목록')}
			</button>
		</td>
    </tr>
</table>

</form>

</body>
</html>