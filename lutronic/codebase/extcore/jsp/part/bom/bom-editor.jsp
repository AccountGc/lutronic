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
					"checkout" : {
						"name" : "체크아웃",
						"icon" : "quit"
					},
					"undocheckout" : {
						"name" : "체크아웃취소",
						"icon" : "quit"
					},
					"checkin" : {
						"name" : "체크인",
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
					process(node, action);
				}
			},
		});
		selectbox("depth");
	});

	// 마우스 우클릭 액션
	function process(node, action) {
		let url;
		const oid = node.data.oid;
		const params = new Object();
		openLayer();
		logger(oid);
		if (action === "checkout") {
			url = getCallUrl("/bom/checkout?oid=" + oid);
			call(url, params, function(data) {
				if (data.result) {
					const resNode = data.resNode;
					refresh(node, resNode);
				}
				closeLayer();
			}, "GET");
		} else if (action === "undocheckout") {
			url = getCallUrl("/bom/undocheckout?oid=" + oid);
			call(url, params, function(data) {
				if (data.result) {
					const resNode = data.resNode;
					refresh(node, resNode);
				}
				closeLayer();
			}, "GET");
		} else if (action === "checkin") {
			url = getCallUrl("/bom/checkin?oid=" + oid);
			call(url, params, function(data) {
				if (data.result) {
					const resNode = data.resNode;
					refresh(node, resNode);
				}
				closeLayer();
			}, "GET");
		}
	}

	function refresh(node, resNode) {
		node.fromDict({
			id : resNode.oid,
			icon : resNode.icon,
			data : {
				oid : resNode.oid,
				version : resNode.version
			}
		});
		if (node.isLazy()) {
			node.resetLazy();
			node.load();
		}
	}

	$(function() {
		// 		$("#bomEditView").contextmenu({
		// 			delegate : "span.fancytree-node",
		// 			menu : [ {
		// 				title : "새 보기 버전",
		// 				cmd : "newViewVersion",
		// 				uiIcon : "ui-icon-newpart",
		// 			}, {
		// 				title : "----"
		// 			}, {
		// 				title : "확장",
		// 				uiIcon : "ui-icon-expand",
		// 				children : [ {
		// 					title : "1 레벨",
		// 					cmd : "l1expands",
		// 					uiIcon : "ui-icon-expand",
		// 				}, {
		// 					title : "2 레벨",
		// 					cmd : "l2expands",
		// 					uiIcon : "ui-icon-expand",
		// 				}, {
		// 					title : "3 레벨",
		// 					cmd : "l3expands",
		// 					uiIcon : "ui-icon-expand",
		// 				}, {
		// 					title : "4 레벨",
		// 					cmd : "l4expands",
		// 					uiIcon : "ui-icon-expand",
		// 				}, {
		// 					title : "전체확장",
		// 					cmd : "expands",
		// 					uiIcon : "ui-icon-expand",
		// 				}, {
		// 					title : "전체축소",
		// 					cmd : "collapse",
		// 					uiIcon : "ui-icon-collapse",
		// 				} ]
		// 			}, {
		// 				title : "----"
		// 			}, {
		// 				title : "삽입",
		// 				uiIcon : "ui-icon-adds",
		// 				cmd : "insert",
		// 				disabled : true,
		// 				children : [ {
		// 					title : "신규부품추가",
		// 					cmd : "addNewPart",
		// 					uiIcon : "ui-icon-newpart",
		// 				}, {
		// 					title : "기존부품추가",
		// 					cmd : "addExistPart",
		// 					uiIcon : "ui-icon-oldpart",
		// 				} ]
		// 			}, {
		// 				title : "구조변경",
		// 				uiIcon : "ui-icon-structure",
		// 				cmd : "struct",
		// 				disabled : true,
		// 				children : [ {
		// 					title : "위로",
		// 					cmd : "moveUp",
		// 					uiIcon : "ui-icon-upPart",
		// 					disabled : true,
		// 				}, {
		// 					title : "아래로",
		// 					cmd : "moveDown",
		// 					uiIcon : "ui-icon-downPart",
		// 					disabled : true,
		// 				}, {
		// 					title : "오른쪽",
		// 					cmd : "indent",
		// 					uiIcon : "ui-icon-outdent",
		// 					disabled : true,
		// 				}, {
		// 					title : "왼쪽",
		// 					cmd : "outdent",
		// 					uiIcon : "ui-icon-indent",
		// 					disabled : true,
		// 				} ]
		// 			}, {
		// 				title : "제거",
		// 				cmd : "remove",
		// 				uiIcon : "ui-icon-remove",
		// 				disabled : true
		// 			}, {
		// 				title : "----"
		// 			}, {
		// 				title : "편집",
		// 				uiIcon : "ui-icon-edits",
		// 				children : [ {
		// 					title : "개정",
		// 					cmd : "revise",
		// 					uiIcon : "ui-icon-revise",
		// 					disabled : true
		// 				}, {
		// 					title : "체크인",
		// 					cmd : "checkin",
		// 					uiIcon : "ui-icon-checkin",
		// 					disabled : true
		// 				}, {
		// 					title : "체크아웃",
		// 					cmd : "checkout",
		// 					uiIcon : "ui-icon-checkout",
		// 					disabled : true
		// 				}, {
		// 					title : "체크아웃취소",
		// 					cmd : "undocheckout",
		// 					uiIcon : "ui-icon-undocheckout",
		// 					disabled : true
		// 				} ]
		// 			}, {
		// 				title : "----"
		// 			}, {
		// 				title : "잘라내기",
		// 				cmd : "cut",
		// 				uiIcon : "ui-icon-cut",
		// 				disabled : true,
		// 			}, {
		// 				title : "복사하기",
		// 				cmd : "copy",
		// 				uiIcon : "ui-icon-copy",
		// 				disabled : true,
		// 			}, {
		// 				title : "붙여넣기",
		// 				cmd : "paste",
		// 				uiIcon : "ui-icon-paste",
		// 				disabled : true,
		// 			}, {
		// 				title : "비우기",
		// 				cmd : "empty",
		// 				uiIcon : "ui-icon-empty",
		// 				disabled : true,
		// 			}, ],
		// 			beforeOpen : function(event, ui) {
		// 				var node = $.ui.fancytree.getNode(ui.target);

		// 				var type = node.type;
		// 				var struct = true;
		// 				if (type == "root") {
		// 					struct = false;
		// 				}

		// 				refNode = node.getPrevSibling();
		// 				var indent = true;
		// 				if (refNode != undefined) {
		// 					if (refNode != undefined) {
		// 						if (node.data.treeKey === refNode.data.treeKey) {
		// 							indent = false;
		// 						}

		// 						if (refNode.data.state === "릴리즈됨" || refNode.data.state === "결재 중" || refNode.data.state === "폐기") {
		// 							indent = false;
		// 						}
		// 					}
		// 				}

		// 				var moveup = true;
		// 				if (node.parent.data.state === "릴리즈됨" || node.parent.data.state === "결재 중" || node.parent.data.state === "폐기") {
		// 					moveup = false;
		// 				}

		// 				var movedown = true;
		// 				if (node.parent.data.state === "릴리즈됨" || node.parent.data.state === "결재 중" || node.parent.data.state === "폐기") {
		// 					movedown = false;
		// 				}

		// 				var outdent = true;
		// 				if (node.parent.parent != undefined) {
		// 					if (node.parent.parent.data.state === "릴리즈됨" || node.parent.parent.data.state === "결재 중" || node.parent.parent.data.state === "폐기") {
		// 						outdent = false;
		// 					}
		// 				}

		// 				var remove = true;
		// 				if (node.parent != undefined) {
		// 					if (node.parent.data.state === "릴리즈됨" || node.parent.data.state === "결재 중" || node.parent.data.state === "폐기") {
		// 						remove = false;
		// 					}
		// 				}

		// 				$("#bomEditView").contextmenu("enableEntry", "paste", !!CLIPBOARD);
		// 				$("#bomEditView").contextmenu("enableEntry", "empty", !!CLIPBOARD);

		// 				$("#bomEditView").contextmenu("enableEntry", "remove", node.type !== "root" && remove);

		// 				$("#bomEditView").contextmenu("enableEntry", "insert", (node.data.state !== "릴리즈됨" && node.data.state !== "결재 중" && node.data.state !== "폐기"));

		// 				$("#bomEditView").contextmenu("enableEntry", "checkout", node.data.cstate === "c/i" && (node.data.state !== "릴리즈됨" && node.data.state !== "결재 중" && node.data.state !== "폐기"));
		// 				$("#bomEditView").contextmenu("enableEntry", "checkin", node.data.cstate === "wrk" || node.data.cstate === "c/o");
		// 				$("#bomEditView").contextmenu("enableEntry", "undocheckout", node.data.cstate === "wrk" || node.data.cstate === "c/o");
		// 				$("#bomEditView").contextmenu("enableEntry", "revise", (node.data.cstate === "c/i" && node.data.state === "릴리즈됨"));

		// 				$("#bomEditView").contextmenu("enableEntry", "struct", (node.parent.data.state !== "릴리즈됨" && node.parent.data.state !== "결재 중" && node.parent.data.state !== "폐기"));

		// 				$("#bomEditView").contextmenu("enableEntry", "indent", (node.parent.data.state !== "릴리즈됨" && node.parent.data.state !== "결재 중" && node.parent.data.state !== "폐기") && (indent && struct));

		// 				$("#bomEditView").contextmenu("enableEntry", "outdent", (outdent && struct));

		// 				$("#bomEditView").contextmenu("enableEntry", "moveUp", (moveup && struct));
		// 				$("#bomEditView").contextmenu("enableEntry", "moveDown", (movedown && struct));

		// 				$("#bomEditView").contextmenu("enableEntry", "copy", node.type != "root");
		// 				$("#bomEditView").contextmenu("enableEntry", "cut", node.type != "root");

		// 				node.setActive();
		// 			},
		// 			select : function(event, ui) {
		// 				var that = this;
		// 				// delay the event, so the menu can close and the
		// 				// click event does
		// 				// not interfere with the edit control
		// 				setTimeout(function() {
		// 					$(that).trigger("nodeCommand", {
		// 						cmd : ui.cmd
		// 					});
		// 				}, 100);
		// 			},
		// 		});
	})
</script>