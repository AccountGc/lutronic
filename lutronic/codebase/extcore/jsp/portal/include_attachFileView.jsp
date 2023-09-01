<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<div id="attachFile" style="display: flex; flex-direction: column; padding: 10px 5px; gap: 3px;"></div>
<script>
	new AXReq("/Windchill/eSolution/content/list", {
		pars : "oid=<%=oid%>&roleType=primary",
		onsucc : function(res) {
			if (!res.e) {
				const form = document.querySelector("form");
				const data = res.primaryFile;
				const len = data.length;
				console.log(data);
				for (let i = 0; i < len; i++) {
					document.querySelector("#attachFile").innerHTML += "<span id='" + data[i]._id_ + "' class='attachFiles' style='cursor: pointer;'>" +  data[i].name + "</span>";
				}
				primary.setUploadedList(data);
				imgurl = data[0].filePath + data[0].name;
				$("#sign_preview").attr("src",imgurl);
			}
		}
	});
</script>