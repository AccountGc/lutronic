<%@page import="com.e3ps.org.service.OrgHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
%>
<div class="AXUpload5" id="primary_layer"></div>
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
// 			uploadMaxFileSize : (1024 * 1024 * 1024),
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
			onStart : function() {
				if(opener == null) {
					parent.openLayer();
				} 
// 				else {
// 					openLayer();
// 				}
			},
			onComplete : function() {
				if(opener == null) {
					parent.closeLayer();
				} else {
					closeLayer();
				}
			},			
			onUpload : function() {
				const form = document.querySelector("form");
				const primaryTag = document.createElement("input");
				primaryTag.type = "hidden";
				primaryTag.name = "primary";
				primaryTag.value = this.cacheId+"/"+this.name;
				primaryTag.id = this._id_;
				form.appendChild(primaryTag);
			},
			onDelete : function() {
				// 주 첨부 파일은 무조건 하나니깐 그냥 삭제 처리한다...
				const el = document.querySelector("input[name=primary]");
				el.parentNode.removeChild(el);
			}
		})

		const url = getCallUrl("/content/list?oid=<%=oid%>&roleType=primary");
		call(url, null, function(res) {
			if (res.primaryFile) {
				const form = document.querySelector("form");
				const data = res.primaryFile;
				const len = data.length;
				for (let i = 0; i < len; i++) {
					const primaryTag = document.createElement("input");
					primaryTag.type = "hidden";
					primaryTag.id = data[i].tagId;
					primaryTag.name = "primary";
					primaryTag.value = data[i].cacheId;
					form.appendChild(primaryTag);
				}
				primary.setUploadedList(data);
				imgurl = data[0].filePath + data[0].name;
				$("#sign_preview").attr("src", imgurl);
			}
		});
	}
	load();

	//이미지 미리보기
	function signPreview(uploadPath) {
		$("#sign_preview").attr("src", uploadPath);
	}

	//첨부파일 업로드 시
	function fileUpload() {
		var file = [];
		for (var i = 0; i < primary.uploadedList.length; i++) {
			file[i] = primary.uploadedList[i].cacheId;
		}
		return file;
	}
</script>