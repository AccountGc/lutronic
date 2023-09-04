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
				const form = document.querySelector("form");
				const data = res.secondaryFile;
				const len = data.length;
				console.log(data);
				for (let i = 0; i < len; i++) {
					document.querySelector("#attachSecondaryFile").innerHTML += "<span id='" + data[i]._id_ + "' class='attachFiles' style='cursor: pointer;'>" +  data[i].name + "</span>";
				}
				secondary.setUploadedList(data);
			}
		}
	});
</script>