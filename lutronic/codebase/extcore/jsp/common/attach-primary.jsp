<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
String row = request.getParameter("row");
%>
<div class="AXUpload5" id="primary_layer"></div>
<%
if (mode != null) {
%>
<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="추가" title="추가" class="blue" onclick="addBtn();">
		</td>
	</tr>
</table>
<%
}
%>
<script type="text/javascript">
	var imgurl;
	var sendName;
	const primary = new AXUpload5();
	function load() {
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
			onUpload : function() {
				const form = document.querySelector("form");
				const primaryTag = document.createElement("input");
				primaryTag.type = "hidden";
				primaryTag.name = "primarys";
				primaryTag.value = this.cacheId;
				primaryTag.id = this._id_;
				form.appendChild(primaryTag);
			},
			onDelete : function() {
				const key = this.file._id_;
				const el = document.getElementById(key);
				el.parentNode.removeChild(el);
				
				const primarys = document.getElementsByName("primarys");
					const tag = primarys[0];
					if(tag.id === key){
						tag.parentNode.removeChild(tag);
					}
			}
		})
	
		new AXReq("/Windchill/eSolution/content/list", {
			pars : "oid=<%=oid%>&roleType=primary",
			onsucc : function(res) {
				if (!res.e) {
					const form = document.querySelector("form");
					const data = res.primaryFile;
					const len = data.length;
					for (let i = 0; i < len; i++) {
						const primaryTag = document.createElement("input");
						primaryTag.type = "hidden";
						primaryTag.id = data[i].tagId;
						primaryTag.name = "primarys";
						primaryTag.value = data[i].cacheId;
						form.appendChild(primaryTag);
					}
					primary.setUploadedList(data);
					imgurl = data[0].filePath + data[0].name;
					$("#sign_preview").attr("src", imgurl);
				}
			}
		});
	}
	load();

	//이미지 미리보기
	function signPreview(uploadPath) {
		$("#sign_preview").attr("src", uploadPath);
	}

	//파일 전체 삭제
	function deleteAllFiles() {
		if (!confirm("전체 삭제 하시겠습니까?")) {
			return;
		}

		const primarys = document.getElementsByName("primarys");
		for (var i = primarys.length - 1; i >= 0; i--) {
			const tag = primarys[i];
			tag.parentNode.removeChild(tag);
		}
		var l = $("form:eq(0)").find("div.readyselect");
		$.each(l, function(idx) {
			var fid = l.eq(idx).attr("id");
			primary.removeUploadedList(fid);
			l.eq(idx).hide();
		})
	}

	//첨부파일 업로드 시
	function fileUpload() {
		var file = [];
		for (var i = 0; i < primary.uploadedList.length; i++) {
			file[i] = primary.uploadedList[i].cacheId;
		}
		return file;
	}
	
	
	//물질 일괄등록으로 전송
	function addBtn(){
		var params = new Object();
		params.file = fileUpload();
		params.row = "<%=row%>";
		params.fileName = sendName;
		self.close();
		opener.addFile(params);
	}
</script>