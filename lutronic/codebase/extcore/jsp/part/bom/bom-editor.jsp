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
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.dnd5.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.filter.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.table.js"></script>
<script src="//cdn.jsdelivr.net/npm/jquery-contextmenu@2.9.2/dist/jquery.contextMenu.min.js"></script>
<script src="/Windchill/extcore/component/fancytree/3rd-party/extensions/contextmenu/js/jquery.fancytree.contextMenu.js"></script>
<link rel="stylesheet" href="//cdn.jsdelivr.net/npm/jquery-contextmenu@2.9.2/dist/jquery.contextMenu.min.css">

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

#treetable td input[type='text'] {
	border: 1px solid #a6a6a6;
	background: #fff;
	color: #333;
	-webkit-border-radius: 2px;
	-moz-border-radius: 2px;
	border-radius: 2px;
	font-size: 14px;
	/* 	height: 15px; */
	padding: 5px;
}
</style>

<input type="hidden" name="oid" id="oid" value="<%=oid%>">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				BOM 편집
			</div>
		</td>
		<td class="right">
			<select name="depth" id="depth" onchange="loadDepth();" class="AXSelect width-120">
				<option value="0">전체확장</option>
				<option value="1">1레벨</option>
				<option value="2" selected="selected">2레벨</option>
				<option value="3">3레벨</option>
				<option value="4">4레벨</option>
				<option value="5">5레벨</option>
			</select>
			&nbsp;
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

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

	document.addEventListener("DOMContentLoaded", function() {

		$("#treetable").fancytree({
			extensions : [ "dnd5", "filter", "table", "contextMenu" ],
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
			preInit : function() {
				openLayer();
			},
			init : function() {
				closeLayer();
			},
			// 			postProcess : function() {
			// 				openLayer();
			// 			},
			// 			loadChildren : function() {
			// 				closeLayer();
			// 			},
			lazyLoad : function(event, data) {
				const node = data.node;
				const level = node.data.level;
				const skip = true;
				const params = {
					oid : node.data.oid,
					level : level,
					skip : skip
				}
				data.result = {
					url : "/Windchill/plm/bom/editorLazyLoad",
					data : params,
					type : "POST"
				};
			},
			renderColumns : function(event, data) {
				const node = data.node;
				const list = node.tr.querySelectorAll("td");
				list[0].style.textAlign = "center";
				list[2].style.textAlign = "center";
				list[2].textContent = node.data.thumb;
				list[2].style.textAlign = "center";
				list[2].textContent = node.data.level;
				list[3].style.textAlign = "center";
				list[3].textContent = node.data.number;
				list[5].style.textAlign = "center";
				list[5].textContent = node.data.qty + "개";
				list[6].style.textAlign = "center";
				list[6].textContent = node.data.version;
				list[7].style.textAlign = "center";
				list[7].textContent = node.data.state;
				list[8].style.textAlign = "center";
				list[8].textContent = node.data.creator;
			},
			contextMenu : {
				menu : {
					"cut" : {
						"name" : "잘라내기",
						"icon" : "cut"
					},
					"copy" : {
						"name" : "복사",
						"icon" : "copy"
					},
					"paste" : {
						"name" : "붙여넣기",
						"icon" : "paste"
					},
					"delete" : {
						"name" : "삭제",
						"icon" : "delete",
						"disabled" : true
					},
					"sep1" : "---------",
					"quit" : {
						"name" : "Quit",
						"icon" : "quit"
					},
					"sep2" : "---------",
					"fold1" : {
						"name" : "Sub group",
						"items" : {
							"fold1-key1" : {
								"name" : "Foo bar"
							},
							"fold2" : {
								"name" : "Sub group 2",
								"items" : {
									"fold2-key1" : {
										"name" : "alpha"
									},
									"fold2-key2" : {
										"name" : "bravo"
									},
									"fold2-key3" : {
										"name" : "charlie"
									}
								}
							},
							"fold1-key3" : {
								"name" : "delta"
							}
						}
					},
					"fold1a" : {
						"name" : "Other group",
						"items" : {
							"fold1a-key1" : {
								"name" : "echo"
							},
							"fold1a-key2" : {
								"name" : "foxtrot"
							},
							"fold1a-key3" : {
								"name" : "golf"
							}
						}
					}
				},
				actions : function(node, action, options) {
					$("#selected-action").text("Selected action '" + action + "' on node " + node + ".");
				}
			},
		});
		selectbox("depth");
	});
	
</script>