<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
WTPart root = (WTPart) request.getAttribute("root");
%>
<link href="/Windchill/extcore/component/fancytree/src/skin-win8/ui.fancytree.css" rel="stylesheet">
<script src="/Windchill/extcore/component/fancytree/src/jquery-ui-dependencies/jquery.fancytree.ui-deps.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.clones.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.dnd5.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.edit.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.filter.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.table.js"></script>

<style type="text/css">
#treetable {
	width: 100%;
	border-top: 2px solid #86bff9;
	font-size: 12px;
}

#treetable th {
	background-color: #f2f2f2;
	height: 30px;
	border-bottom: 1px solid #a6a6a6;
	border-right: 1px solid #a6a6a6;
}

#treetable th:first-child {
	border-left: 1px solid #a6a6a6;
}

#treetable td {
	height: 30px;
	border-bottom: 1px solid #dedede;
	border-right: 1px solid #dedede;
}

#treetable td:first-child {
	border-left: 1px solid #dedede;
}
</style>

<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table id="treetable">
	<colgroup>
		<col width="40px;"></col>
		<col width="40px;"></col>
		<col width="60px;"></col>
		<col width="120px;"></col>
		<col width="*"></col>
		<col width="80px;"></col>
		<col width="80px;"></col>
		<col width="80px;"></col>
		<col width="100px;"></col>
	</colgroup>
	<thead>
		<tr>
			<th></th>
			<th></th>
			<th>레벨</th>
			<th>부품번호</th>
			<th>부품명</th>
			<th>수량</th>
			<th>REV</th>
			<th>상태</th>
			<th>작성자</th>
		</tr>
	</thead>
</table>
<script type="text/javascript">
	const oid = document.getElementById("oid").value;
	const skip = true;
	$(function() {
		var sourceUrl = "https://cdn.jsdelivr.net/gh/mar10/assets@master/fancytree/ajax_101k.json";

		$("#treetable").fancytree({
			extensions : [ "clones", "dnd5", "edit", "filter", "table" ],
			checkbox : true,
			quicksearch : true,
			dnd5 : {
				autoExpandMS : 1500,
				dragStart : function(node, data) {
					return true;
				},
				dragEnter : function(node, data) {
					return true;
				},
				dragDrop : function(node, data) {
					var transfer = data.dataTransfer;

					if (data.otherNode) {
						data.otherNode.moveTo(node, data.hitMode);
					} else {
						node.addNode({
							title : transfer.getData("text")
						}, data.hitMode);
					}
					// Expand target node when a child was created:
					if (data.hitMode === "over") {
						node.setExpanded();
					}
				},
			},
			edit : {
			// triggerStart: ["f2", "mac+enter", "shift+click"],
			},
			filter : {
				autoExpand : true,
			},
			table : {
				indentation : 20,
				nodeColumnIdx : 4,
				checkboxColumnIdx : 0
			},
			source : $.ajax({
				url : "/Windchill/plm/bom/loadEditor?oid=" + oid + "&skip=" + skip,
				type : "POST",
			}),
			lazyLoad : function(event, data) {
				data.result = {
					url : "ajax-sub2.json"
				}
			},
			renderColumns : function(event, data) {
				const node = data.node;
				logger(node);
				const list = node.tr.querySelectorAll("td");
				list[0].style.textAlign = "center";
				list[2].textContent = node.getIndexHier();
				list[2].style.textAlign = "center";
				tdList[3].textContent = node.getIndexHier();
				tdList[4].textContent = node.getIndexHier();
			},
		});

		$("#expandAll").on("click", function(e) {
			$.ui.fancytree.getTree().expandAll();
		});
		$("#collapseAll").on("click", function(e) {
			$.ui.fancytree.getTree().expandAll(false);
		});

	});
</script>