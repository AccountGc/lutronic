<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div id="attachPrimaryFile" style="display: flex; flex-direction: column; padding: 10px 5px; gap: 3px;"></div>
<script>
	new AXReq("/Windchill/plm/content/list", {
		pars : "oid=<%=oid%>&roleType=primary",
		onsucc : function(res) {
			if (!res.e) {
				const form = document.querySelector("form");
				const data = res.primaryFile;
				const len = data.length;
				console.log(data);
				for (let i = 0; i < len; i++) {
					document.querySelector("#attachPrimaryFile").innerHTML += "<span id='" + data[i].oid + "' class='attachPrimaryFiles' style='cursor: pointer; text-decoration: underline;'>" +  data[i].name + "</span>";
				}
				primary.setUploadedList(data);
				imgurl = data[0].filePath + data[0].name;
				$("#sign_preview").attr("src",imgurl);
			}
		}
	});
	
	// 클릭 시 다운로드 메서드
	document.addEventListener("click", (e)=>{
		if(e.target.classList.contains("attachPrimaryFiles")){
			const oid = e.target.id;
			document.location.href = "/Windchill/plm/content/download?oid=" + oid;
		}
	})
</script>