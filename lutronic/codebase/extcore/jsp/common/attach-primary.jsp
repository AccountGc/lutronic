<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div class="AXUpload5" id="primary_layer"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox1" style="height: 150px;"></div>
<script type="text/javascript">
	var imgurl;
	const primary = new AXUpload5();
	function load() {
		primary.setConfig({
			isSingleUpload : false,
			targetID : "primary_layer",
			uploadFileName : "primary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/content/upload"),
			dropBoxID : "uploadQueueBox1",
			queueBoxID : "uploadQueueBox1",
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
			onStart : function() {
// 				openLayer();
			},
			onComplete : function() {
				const form = document.querySelector("form");
				for (let i = 0; i < this.length; i++) {
					const primaryTag = document.createElement("input");
					primaryTag.type = "hidden";
					primaryTag.name = "primarys";
					primaryTag.value = this[i].cacheId;
					primaryTag.id = this[i].tagId;
					form.appendChild(primaryTag);
					var uploadPath = this[i].filePath + this[i].saveName;
					signPreview(uploadPath);
				}
// 				closeLayer();
			},
			onDelete : function() {
				const key = this.file.tagId;
				const el = document.getElementById(key);
				el.parentNode.removeChild(el);
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
					$("#sign_preview").attr("src",imgurl);
				}
			}
		});
	}
	load();
	
	//이미지 미리보기
	function signPreview(uploadPath){
		$("#sign_preview").attr("src",uploadPath);
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
	function fileUpload(){
		var file=[];
		for(var i=0; i<primary.uploadedList.length; i++){
			file[i] = primary.uploadedList[i].cacheId;
		}
		return file;
	}
</script>