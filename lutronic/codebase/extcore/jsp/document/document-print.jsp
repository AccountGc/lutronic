<%@page import="java.util.Map"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<style type="text/css">
@page {size: auto;margin-top: 30mm;}
@media print {
	html, body {
		border: 1px solid white;
		height: 99%;
		page-break-after: avoid;
		page-break-before: avoid;
	}
}
</style>
<div id="newContent"></div>

<script type="text/javascript">
	var data = localStorage.getItem("data");
	var contentDiv = document.getElementById("newContent");
	contentDiv.innerHTML = data;
	
	window.opener.goPrint(contentDiv);
</script>