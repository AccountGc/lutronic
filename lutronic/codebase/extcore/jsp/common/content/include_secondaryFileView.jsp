<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div id="attachSecondaryFile" style="display: flex; flex-direction: column; padding: 10px 5px; gap: 3px;"></div>
<script>
	// 첨부파일 불러오기 메서드
	new AXReq("/Windchill/plm/content/list", {
		pars : "oid=<%=oid%>&roleType=secondary",
		onsucc : function(res) {
			if (!res.e) {
				const data = res.secondaryFile;
				const len = data.length;
				for (let i = 0; i < len; i++) {
					document.querySelector("#attachSecondaryFile").innerHTML += "<span id='" + data[i].oid + "' class='attachSecondaryFiles' style='cursor: pointer; text-decoration: underline;'>" +  data[i].name + "</span>";
				}
			}
		}
	});
	
	// 클릭 시 다운로드 메서드
	document.addEventListener("click", (e)=>{
		if(e.target.classList.contains("attachSecondaryFiles")){
			const oid = e.target.id;
			document.location.href = "/Windchill/plm/content/download?oid=" + oid;
		}
	})
</script>


