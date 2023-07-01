<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
})

$(function() {
	$("#doSubmit").click(function() {
		
		var predate = $("#predate").val();
		var postdate = $("#postdate").val()
		
		if(predate == ""){
			alert("${f:getMessage('등록일자의 시작일을 입력하세요.')}");
			return;
		}else if(postdate == "") {
			alert("${f:getMessage('등록일자의 종료을 입력하세요.')}")
			return;
		}
		
		//if(!checkDate(predate, postdate)) {
		//	return;
		//}
		
		if(!confirm(predate+" ${f:getMessage('부터')} "+postdate+" ${f:getMessage('까지의 변환요청을 진행 하시겠습니까?')}")) {
			return;
		}

		var form = $("form[name=multiPublishing]").serialize();
		var url	= getURLString("groupware", "multiPublishingAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: false,
			cache: false,

			error:function(data){
				var msg = "${f:getMessage('데이터 검색오류')}";
				alert(msg);
			},

			success:function(data){
				if(data.msg != "") {
					alert(data.msg);
				}
				if("S" == data.result) {
					$("#multiPublishing")[0].reset();
				}
			}
		});
		
	})
})
</script>

<body>

<form name="multiPublishing" id="multiPublishing" >

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('도면 재변환')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="400" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td align="center" valign="top" width=100% style="padding-right:4;padding-left:4">
			<table width="400" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
				<tr height=5>
					<td>
						<table width="400" border="0" cellpadding="0" cellspacing="3">
						    <tr align="center">
						        <td valign="top" style="padding:0px 0px 0px 0px">
						            <table width="400" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
						                <tr><td height="1" width="100%"></td></tr>
						            </table>
						            <table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
							            <tr>
							                <td class="tdblueM">${f:getMessage('등록일자')}</td>
											<td class="tdwhiteL">
												<input name="predate" id="predate" class="txt_field" size="12" engnum="engnum" maxlength=15 readonly/>
												<a href="JavaScript:void(0);">
													<img src="/Windchill/jsp/portal/images/calendar_icon.gif" id="predateBtn" border=0>
												</a>
												
												<a href="JavaScript:clearText('predate')">
													<img src="/Windchill/jsp/portal/images/x.gif" border=0>
												</a>
												~
												<input name="postdate" id="postdate" class="txt_field" size="12" engnum="engnum" maxlength=15 readonly />
												<a href="JavaScript:void(0);">
													<img src="/Windchill/jsp/portal/images/calendar_icon.gif" id="postdateBtn" border=0>
												</a>
												
												<a href="JavaScript:clearText('postdate')">
													<img src="/Windchill/jsp/portal/images/x.gif" border=0>
												</a>
											</td>
										</tr>
						            </table>
						        </td>
						    </tr>
						    
						    <tr>
						        <td align="center" colspan="2">
						            <table border="0" cellpadding="0" cellspacing="4" align="center">
						                <tr>
						                    <td>
						                    	<button type="button" class="btnCRUD" id="doSubmit">
						                    		<span></span>
						                    		${f:getMessage('변환요청')}
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
		</td>
	</tr>
</table>
</form>
</body>
</html>