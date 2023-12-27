<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
<%
String oid = (String) request.getAttribute("oid");
 String method = (String) request.getAttribute("method");
%>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="저장" title="저장" class="blue" onclick="save();">
			<input type="button" value="닫기" title="닫기" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb req">주 첨부파일</th>
		<td class="indent5">
			<div class="AXUpload5" id="primary_layer"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	const primary = new AXUpload5();
	let data;
	function uploader() {
		primary.setConfig({
			isSingleUpload : true,
			targetID : "primary_layer",
			uploadFileName : "primary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/content/upload"),
			uploadPars : {
				roleType : "primary"
			},
			deleteUrl : getCallUrl("/content/delete"),
			fileKeys : {
				name : "name",
				type : "type",
				saveName : "saveName",
				fileSize : "fileSize",
				uploadedPath : "uploadedPath",
				roleType : "roleType",
				cacheId : "cacheId",
			},
			onStart : function() {
				openLayer();
			},
			onComplete : function() {
				closeLayer();				
			},
			onUpload : function() {
				data = this;
			},
			onDelete : function() {
				data = undefined;
			}
		})
	}
	
	uploader();
	
	function save() {
		if(data === undefined) {
			alert("첨부파일을 추가하세요.");
			return false;
		}
		opener.<%=method%>(data);
		self.close();
	}
</script>