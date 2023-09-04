<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div id="attachSecondaryFile" style="display: flex; flex-direction: column; padding: 10px 5px; gap: 3px;"></div>
<script>
	new AXReq("/Windchill/eSolution/content/list", {
		pars : "oid=<%=oid%>&roleType=secondary",
		onsucc : function(res) {
			if (!res.e) {
				const data = res.secondaryFile;
				const len = data.length;
				for (let i = 0; i < len; i++) {
					document.querySelector("#attachSecondaryFile").innerHTML += "<span id='" + data[i].oid + "' class='attachFiles' style='cursor: pointer; text-decoration: underline;'>" +  data[i].name + "</span>";
				}
			}
		}
	});
	
	document.addEventListener("click", (e)=>{
		if(e.target.classList.contains("attachFiles")){
			const oid = e.target.id;
			document.location.href = "/Windchill/eSolution/content/download?oid=" + oid;
		}
	})
</script>


