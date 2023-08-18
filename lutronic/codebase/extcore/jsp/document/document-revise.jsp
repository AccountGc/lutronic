<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
String module = (String) request.getAttribute("module");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>

<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
<input type="hidden" name="module" id="module" value="<%= module %>" />
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 문서 결재 타입 수정
			</div>
		</td>
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button"  value="개정"  title="개정"  class="reviseBtn"  id="reviseBtn" name="reviseBtn" onclick="revise();">
			<input type="button" value="닫기" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="req lb">결재방식</th>
		<td>&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default" checked="checked">
				<div class="state p-success">
					<label> <b>기본결재</b>
					</label>
				</div>
			</div>&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default_NonWF">
				<div class="state p-success">
					<label> <b>일괄결재</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
</table>
<script type="text/javascript">

function revise(){
	if(confirm("개정하시겠습니까?")){
		const oid = document.getElementById("oid").value;
		const module = document.getElementById("module").value;
		const lifecycle = document.getElementById("lifecycle").value;
		const url = getCallUrl("/doc/document-revise");
		const params = new Object();
		params.oid = oid;
		params.module = module;
		params.lifecycle = lifecycle;
		call(url, params, function(data) {
			if (data.result) {
				alert(data.msg + "개정 성공하였습니다.");
				if(module == 'rohs') {
					opener.location.href = getCallUrl("/rohs/view?oid="+ oid);
				}else {
					opener.location.href = getCallUrl("/doc/view?oid="+ oid);
				}
				self.close();
			} else {
				alert("개정에 실패하였습니다.  \n" + data.msg);
			}
		});
	}
}

<%--
// $(function() {
// 	$('#reviseBtn').click(function() {
// 		if(confirm("${f:getMessage('개정하시겠습니까?')}")){
// 			var form = $("form[name=reviseDocumentPopup]").serialize();
// 			var url	= getURLString("doc", "reviseDocument", "do");
// 			$.ajax({
// 				type:"POST",
// 				url: url,
// 				data: form,
// 				dataType:"json",
// 				async: true,
// 				cache: false,
// 				error:function(data){
// 					var msg = "${f:getMessage('등록 오류')}";
// 					alert(msg);
// 				},

// 				success:function(data){
// 					if(data.result) {
// 						alert("${f:getMessage('개정 성공하였습니다.')}");
// 						if($('#module').val() == 'rohs') {
// 							opener.location.href = getURLString("rohs", "viewRohs", "do") + "?oid="+data.oid;
// 						}else {
// 							opener.location.href = getURLString("doc", "viewDocument", "do") + "?oid="+data.oid;
// 						}
// 						self.close();
// 					}else {
// 						alert("${f:getMessage('개정 실패하였습니다.')} \n" + data.message);
// 					}
// 				}
// 				,beforeSend: function() {
// 					gfn_StartShowProcessing();
// 		        }
// 				,complete: function() {
// 					gfn_EndShowProcessing();
// 		        }
// 			});
// 		}
// 	})
// })
--%>
</script>
</body>
</html>