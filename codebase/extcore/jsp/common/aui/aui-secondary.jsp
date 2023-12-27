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
		<th class="lb">첨부파일</th>
		<td class="indent5">
			<div class="AXUpload5" id="secondary_layer"></div>
			<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: 290px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	const secondary = new AXUpload5();
	let data = [];
	function uploader() {
		secondary.setConfig({
			isSingleUpload : false,
			targetID : "secondary_layer",
			uploadFileName : "secondary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/content/upload"),
			dropBoxID : "uploadQueueBox",
			queueBoxID : "uploadQueueBox",
			uploadPars : {
				roleType : "secondary"
			},
			uploadMaxFileCount : 100,
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
			onUpload : function() {
				data.push(this);
			},
			onComplete : function() {
				closeLayer();				
			},
			onDelete : function() {
				const key = this.file.tagId;
				for(let i=0; i<data.length; i++) {
					const item = data[i];
					const tagId = item.tagId;
					if(key === tagId) {
						data.splice(i);
					}
				}
			}
		})
	}
	
	uploader();
	
	function save() {
		logger(data);
		if(data.length === 0) {
			alert("첨부파일을 추가하세요.");
			return false;
		}
		opener.<%=method%>(data);
		self.close();
	}
</script>