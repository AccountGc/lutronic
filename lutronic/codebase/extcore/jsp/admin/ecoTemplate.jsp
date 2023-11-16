<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<!-- SmartEditor2 라이브러리  -->
<script type="text/javascript" src="/Windchill/extcore/jsp/smarteditor2/js/HuskyEZCreator.js" charset="utf-8"></script>
<script type="text/javascript" src="//code.jquery.com/jquery-1.11.0.min.js"></script>
</head>
<body>
	<form id="form">
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="lastNum" id="lastNum">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid">
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 설계변경 문서 템플릿 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="등록" title="등록" id="createBtn" class="blue">
<!-- 					<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();"> -->
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>설계변경 문서 템플릿 제목 <span class="red">*</span></th>
				<td class="indent5">
					<input type="text" name="title" id="title" class="width-800">
				</td>
			</tr>
			<tr>
				<th>템플릿 종류</th>
				<td class="indent5">
					<select name="type" id="type" class="width-200">
						<option value="">선택</option>
						<option value="A">변경관리 요청서(CR)</option>
						<option value="B">보고서</option>
						<option value="C">설계 및 검증 검토 회의록</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>설계변경 문서 양식</th>
				<td class="indent5">
    				<textarea name="content" id="smartEditor" style="width: 98%; height: 700px;"></textarea>
				</td>
			</tr>
		</table>
				
		<script type="text/javascript">
			var oEditors = [];
			nhn.husky.EZCreator.createInIFrame({
				oAppRef : oEditors,
				elPlaceHolder : "smartEditor", 
				sSkinURI : "/Windchill/extcore/jsp/smarteditor2/SmartEditor2Skin.html", //경로를 꼭 맞춰주세요!
				fCreator : "createSEditor2",
				htParams : {
					// 툴바 사용 여부 (true:사용/ false:사용하지 않음)
					bUseToolbar : true,
	
					// 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음)
					bUseVerticalResizer : false,
	
					// 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음)
					bUseModeChanger : false
				}
			});		

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
			
			selectbox("type");
			
		</script>
	</form>
</body>
</html>