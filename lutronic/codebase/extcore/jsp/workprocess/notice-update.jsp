<%@page import="com.e3ps.groupware.notice.beans.NoticeData"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	NoticeData data = (NoticeData) request.getAttribute("data");
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
	<form id="form">
		<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 공지사항 수정
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>제목 <span class="red">*</span></th>
				<td class="indent5">
					<input type="text" name="title" id="title" class="width-800" value="<%=data.getTitle()%>">
				</td>
			</tr>
			<tr>
				<th>팝업 유무</th>
				<td class="indent5">
					<div class="pretty p-switch">
						<input type="radio" name="isPopup" value="true" id="T" checked="checked">
						<div class="state p-success">
							<label for="T"> <b>팝업 O</b>
							</label>
						</div>
					</div> &nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="isPopup" value="false" id="F">
						<div class="state p-success">
							<label for="F"> <b>팝업 X</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th>내용</th>
				<td class="indent5">
					<textarea name="contents" id="contents" cols="80" rows="10" class="fm_area" style="width:90%" onKeyUp="common_CheckStrLength(this, 4000)" onChange="common_CheckStrLength(this, 4000)"><%=data.getContents()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="<%=data.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
				
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="수정" title="수정" class="blue" id="update">
					<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			$("#update").click(function() {
				if(isEmpty($("#title").val())) {
					alert("제목을 입력하세요.");
					return;
				}
				
				if (!confirm("수정 하시겠습니까?")) {
					return;
				}
				
				var params = _data($("#form"));
				var url = getCallUrl("/groupware/updateNotice");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						opener.loadGridData();
						self.close();
					}else{
						alert(data.msg);
					}
				});
			})

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

		</script>
	</form>
</body>
</html>