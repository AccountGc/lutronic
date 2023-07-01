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
*                      페이지 초기설정
----------------------------------------------------------%>

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
		
		if($("input[name='eoType']:checked").length == 0 ){
			alert("${f:getMessage('구분')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($("input[name='model']").size() == 0 ){
			alert(" ${f:getMessage('제품명')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($.trim($("#completeOid").size()) == "") {
			alert("${f:getMessage('완제품 품번')} ${f:getMessage('을(를) 선택하세요.')}");
			return;		
		}
		
		if(!textAreaLengthCheckId('eoCommentA','2000','${f:getMessage("제품 설계 개요")}')){
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentB','2000','${f:getMessage("특기사항")}')){
			return;
		}
		
		if(!textAreaLengthCheckId('eoCommentC','2000','${f:getMessage("기타사항")}')){
			return;
		}
		
		if(!checkActivity()){
			return;
		}
		
		if(!compeletPartCheck()){
			return;
		}
		
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("changeECO", "createEOAction", "createEO", "listEO").submit();
		}
		
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
	$('#listBtn').click(function() {
		location.href = getURLString("changeECO", "listEO", "do");
	})
	
	$('#resetBtn').click(function() {
		resetFunctionEO();
		deleteAllCode();
	})
})

function compeletPartCheck(){
	
	var obj = $("input[name='completeNumber']");
	var checkCount = obj.size();
	
	for (var i = 0; i < checkCount; i++) {
		
		if(!partCheck(obj.eq(i).val())){
			return false;
		}
	}
	
	return true;
}
/**************************************************************
*                       EO 등록시 EO 구분에 따른 완제품 품목 조건
****************************************************************/
function partCheck(number){
	
	//DEV 1인 아닌 부품,PRODUCT 1인 부품 
	//partNumberCheck eo.js
	if(!partNumberCheck(number)|| number.length !=10){
		alert(number +"${f:getMessage('가채번 ,더미 ,제품군 부품은 선택 할수 없습니다.')}" )
		return false;
	}
	
	var eoType = $("input[name=eoType]:checked").val();
	var firstNumber = number.substring(0,1)
	//DEV 1인 아닌 부품,PRODUCT 1인 부품 
	if(eoType == "DEV"){
		if(firstNumber=="1"){
			alert("${f:getMessage('개발 EO는 는 완제품을 선택 할수 없습니다.')}" )
			return false;
		}
	}else{
		if(firstNumber !="1"){
			alert("${f:getMessage('양산 EO는 는 완제품만 선택할수 있습니다.')}" )
			return false;
		}
	}
	
	return true;
	
}


</script>

<body>

<form name=createEO id="createEO" method="post" style="padding:0px;margin:0px" enctype="multipart/form-data">
<input type="hidden" name="cmd" id="cmd" value="save" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp; ${f:getMessage('EO')} ${f:getMessage('관리')} > ${f:getMessage('EO')} ${f:getMessage('등록')}
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
						EO${f:getMessage('제목')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" >
						<input name="name" id="name" class="txt_field" size="85" style="width:98%"   value="" />
					</td>
					
					<td class="tdblueM" >
						EO${f:getMessage('구분')}
						<span class="style1">*</span>
					</td>
					<td class="tdwhiteL" >
						<input type="radio" name=eoType value="DEV">${f:getMessage('개발')}
						<input type="radio" name=eoType value="PRODUCT">${f:getMessage('양산')}
					</td>
				</tr>
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('제품명')}
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
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">
						${f:getMessage('완제품 품목')}
						<span style="color:red;">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/part/include_CompletePartSelect.do" flush="false" >
							<jsp:param name="moduleType" value="EO"/>
							<jsp:param name="mode" value="mutil" />
							<jsp:param name="paramName" value="CompletePartOid"/>
							<jsp:param name="form" value="simple" />
							<jsp:param name="isBom" value="false" />
							<jsp:param name="selectBom" value="false" />
							
						</jsp:include>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('제품 설계 개요')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="eoCommentA" id="eoCommentA" rows="5" onchange="textAreaLengthCheckName('eoCommentA', '4000', '${f:getMessage('제품 설계 개요')}')"></textarea>
						</div>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('특기사항')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="eoCommentB" id="eoCommentB" rows="5" onchange="textAreaLengthCheckName('eoCommentB', '4000', '${f:getMessage('특기사항')}')"></textarea>
						</div>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">
						${f:getMessage('기타사항')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="eoCommentC" id="eoCommentC" rows="5" onchange="textAreaLengthCheckName('eoCommentC', '4000', '${f:getMessage('기타사항')}')"></textarea>
						</div>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="createEO"/>
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
			<button title="${f:getMessage('목록')}" class="btnCustom" type="button" name="listBtn" id="listBtn">
				<span></span>
				${f:getMessage('목록')}
			</button>
		</td>
    </tr>
</table>

</form>

</body>
</html>