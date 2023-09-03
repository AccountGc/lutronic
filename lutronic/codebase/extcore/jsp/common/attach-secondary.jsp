<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div class="AXUpload5" id="secondary_layer"></div>
<div class="AXUpload5QueueBox_list" id="uploadQueueBox2" style=""></div>
<script type="text/javascript">
	const secondary = new AXUpload5();
	function load() {
		secondary.setConfig({
			isSingleUpload : false,
			targetID : "secondary_layer",
			uploadFileName : "secondary",
			buttonTxt : "파일 선택",
			uploadMaxFileSize : (1024 * 1024 * 1024),
			uploadUrl : getCallUrl("/content/upload"),
			dropBoxID : "uploadQueueBox2",
			queueBoxID : "uploadQueueBox2",
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
// 				openLayer();
			},
			onComplete : function() {
				const form = document.querySelector("form");
				for (let i = 0; i < this.length; i++) {
					const secondaryTag = document.createElement("input");
					secondaryTag.type = "hidden";
					secondaryTag.name = "secondarys";
					secondaryTag.value = this[i].cacheId;
					secondaryTag.id = this[i]._id_;
					form.appendChild(secondaryTag);
				}
// 				closeLayer();
			},
			onDelete : function() {
				const key = this.file._id_;
				const el = document.getElementById(key);
				el.parentNode.removeChild(el);
			}
		})
		
// 		let params = new Object();
<%-- 		const oid = "<%= oid %>"; --%>
// 		const roleType = "secondary";
		
// 		params.oid = oid;
// 		params.roleType = roleType;
		
		
		new AXReq("/Windchill/eSolution/content/list", {
			pars : "oid=<%=oid%>&roleType=secondary",
			onsucc : function(res) {
				if (!res.e) {
					const form = document.querySelector("form");
					const data = res.secondaryFile;
					const len = data.length;
					for (let i = 0; i < len; i++) {
						const secondaryTag = document.createElement("input");
						secondaryTag.type = "hidden";
						secondaryTag.id = data[i]._id_;
						secondaryTag.name = "secondarys";
						secondaryTag.value = data[i].cacheId;
						form.appendChild(secondaryTag);
					}
					secondary.setUploadedList(data);
				}
			}
		});
	}
	load();
	
	
	//파일 전체 삭제
	function deleteAllFiles() {
		if (!confirm("전체 삭제 하시겠습니까?")) {
			return;
		}
		
		const secondarys = document.getElementsByName("secondarys");
		for (let i = secondarys.length - 1; i >= 0; i--) {
			const tag = secondarys[i];
			tag.parentNode.removeChild(tag);
		}

		var l = $("form:eq(0)").find("div.readyselect");
		$.each(l, function(idx) {
			var fid = l.eq(idx).attr("id");
			secondary.removeUploadedList(fid);
			l.eq(idx).hide();
		})
	}
	
	//첨부파일 업로드 시
	function fileUpload(){
		var file=[];
		for(var i=0; i<secondary.uploadedList.length; i++){
			file[i] = secondary.uploadedList[i].cacheId;
		}
		return file;
	}
</script>