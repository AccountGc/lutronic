<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
WTPart root = (WTPart) request.getAttribute("root");
%>
<link href="/Windchill/extcore/component/fancytree/src/skin-win8/ui.fancytree.css?v=4" rel="stylesheet">
<link href="/Windchill/extcore/component/contextmenu/dist/jquery.contextMenu.css" rel="stylesheet">
<script src="/Windchill/extcore/component/fancytree/src/jquery-ui-dependencies/jquery.fancytree.ui-deps.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.clones.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.dnd5.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.filter.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.table.js"></script>
<script src="/Windchill/extcore/component/jqueryui/jquery.ui-contextmenu.min.js"></script>
<script src="/Windchill/extcore/component/contextmenu/dist/jquery.contextMenu.min.js"></script>
<!-- <script src="//cdn.jsdelivr.net/npm/jquery-contextmenu@2.9.2/dist/jquery.contextMenu.min.js"></script> -->
<!-- <script src="/Windchill/extcore/component/fancytree/3rd-party/extensions/contextmenu/js/jquery.fancytree.contextMenu.js"></script> -->
<!-- <link rel="stylesheet" href="//cdn.jsdelivr.net/npm/jquery-contextmenu@2.9.2/dist/jquery.contextMenu.min.css"> -->

<style type="text/css">
#treetable, #righttable {
	width: 100%;
	border-top: 2px solid #86bff9;
	font-size: 12px;
}

#treetable th, #righttable th {
	background-color: #f2f2f2;
	height: 30px;
	border-bottom: 1px solid #a6a6a6;
	border-right: 1px solid #a6a6a6;
}

#treetable th:first-child, #righttable th:first-child {
	border-left: 1px solid #a6a6a6;
}

#treetable td, #righttable td {
	height: 30px;
	border-bottom: 1px solid #dedede;
	border-right: 1px solid #dedede;
}

#treetable td:first-child, #righttable td:first-child {
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
				BOM 편집 &nbsp;&nbsp;
				<div class="pretty p-switch">
					<input type="checkbox" name="skip" value="true" onclick="_reload();">
					<div class="state p-success">
						<label>
							<b>더미제외</b>
						</label>
					</div>
				</div>
			</div>

		</td>
		<td class="right">
			<!-- 			<select name="depth" id="depth" onchange="loadDepth();" class="AXSelect width-120"> -->
			<!-- 				<option value="0">전체확장</option> -->
			<!-- 				<option value="1">1레벨</option> -->
			<!-- 				<option value="2" selected="selected">2레벨</option> -->
			<!-- 				<option value="3">3레벨</option> -->
			<!-- 				<option value="4">4레벨</option> -->
			<!-- 				<option value="5">5레벨</option> -->
			<!-- 				<option value="6">전체펼치기</option> -->
			<!-- 			</select> -->
			&nbsp;
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="search-table" style="border: none;">
	<colgroup>
		<col width="49%">
		<col width="40px;">
		<col width="10%">
		<col width="39%">
	</colgroup>
	<tr>
		<td style="border: none;">&nbsp;</td>
		<td style="border: none;">&nbsp;</td>
		<th style="border-top: 2px solid #86bff9;">품목 번호/명</th>
		<td class="indent5" style="border-top: 2px solid #86bff9;">
			<input type="text" name="number" id="number" class="width-400" readonly="readonly" onclick="load();">
			<input type="hidden" name="poid" id="poid">
			<input type="button" value="품목선택" title="품목선택" onclick="load();" class="blue">
			<input type="button" value="초기화" title="초기화" onclick="destroy();">
		</td>
	</tr>
</table>
<br>
<table>
	<colgroup>
		<col width="49%">
		<col width="40px;">
		<col width="49%">
	</colgroup>
	<tr>
		<td valign="top">
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
		</td>
		<td valign="top">&nbsp;</td>
		<td valign="top">
			<table id="righttable">
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
		</td>
	</tr>
</table>
<script type="text/javascript">
	let CLIPBOARD = null;
	document.addEventListener("DOMContentLoaded", function() {
		leftTreeLoad();
		selectbox("depth");
	});

	function leftTreeLoad() {
		const oid = document.getElementById("oid").value;
		const dummy = document.querySelector("input[name=skip]:checked");
		let skip;
		if (dummy !== null) {
			skip = "true";
		} else {
			skip = "false";
		}
		const tree = $.ui.fancytree.getTree("#treetable");
		if (tree !== null) {
			tree.destroy();
		}

		$("#treetable").fancytree({
			extensions : [ "dnd5", "filter", "table", "clones" ],
			// 			checkbox : true,
			quicksearch : true,
			debugLevel : 0,
			selectMode : 2,
			dnd5 : {
				autoExpandMS : 500,
				preventRecursion : true, // Prevent dropping nodes on own descendants
				preventVoidMoves : true, // Prevent moving nodes 'before self', etc.
				dragStart : function(node, data) {
					return false;
				},
				dragEnter : function(node, data) {
					const sameTree = (data.otherNode.tree === data.tree);
					if (sameTree) {
						return false;
					}
					return true;
				},
				dragOver : function(node, data) {
					if (data.hitMode === "after") {
						return false;
					}

					if (data.hitMode === "before") {
						return false;
					}
					return true;
				},
				dragDrop : function(node, data) {
					const sourceNodes = data.otherNodeList;
					const hitMode = data.hitMode;
					if (hitMode === "after" || hitMode === "before") {
						return false;
					}

					for (let i = 0; i < sourceNodes.length; i++) {
						const nn = sourceNodes[i];
						nn.copyTo(node, data.hitMode);
					}
					drop(node, sourceNodes);
				},
			},
			filter : {
				autoExpand : true,
			},
			loadChildren : function(event, data) {
				data.node.visit(function(subNode) {
					if (subNode.isLazy() && !subNode.isLoaded()) {
						subNode.resetLazy();
						subNode.load();
					}
				});
			},
			table : {
				indentation : 20,
				nodeColumnIdx : 4,
			},
			source : $.ajax({
				url : "/Windchill/plm/bom/loadEditor?oid=" + oid + "&skip=" + skip,
				type : "POST",
				cache : false
			}),
			preInit : function() {
				openLayer();
			},
			init : function() {
				closeLayer();
			},
			lazyLoad : function(event, data) {
				const node = data.node;
				const level = node.data.level;
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
				const level = node.getIndex() + 1;
				const isCheckOut = node.data.isCheckOut;
				const isNew = node.data.isNew;
				if (isNew) {
					node.tr.style.backgroundColor = "#FFCBCB";
				} else {
					node.tr.style.backgroundColor = "white";
				}

				const key = node.key;
				const thum = node.data.thum;
				const list = node.tr.querySelectorAll("td");
				list[0].style.textAlign = "center";
				list[0].textContent = level;
				list[1].style.textAlign = "center";
				if (thum != undefined) {
					list[1].innerHTML = "<img src=" + node.data.thum + ">";
				} else {
					list[1].innerHTML = "";
				}
				const isRoot = node.data.isRoot;
				list[2].style.textAlign = "center";
				list[2].textContent = node.data.level;
				list[3].style.textAlign = "center";
				list[3].textContent = node.data.number;
				list[5].style.textAlign = "center";
				if (isRoot) {
					list[5].textContent = node.data.qty;
				} else {
					list[5].innerHTML = "<input type='text' onkeydown='validate(this, event);' data-key='" + key + "' onblur='update(this, event);' style='width: 40px;' id='" + key + "' value='" + node.data.qty + "'>";
				}

				list[6].style.textAlign = "center";
				list[6].textContent = node.data.version;
				list[7].style.textAlign = "center";
				list[7].textContent = node.data.state;
				if (node.data.state === "승인됨") {
					list[7].style.color = "red";
					list[7].style.fontWeight = "bold";
				}

				list[8].style.textAlign = "center";
				list[8].textContent = node.data.creator;
			},
			beforeSelect : function(event, data) {
			}
		}).on("nodeCommand", function(event, data) {
			const tree = $.ui.fancytree.getTree(this);
			const refNode = null;
			const moveMode = null;
			let node = tree.getActiveNode();
			const selectedNodes = tree.getSelectedNodes();
			const parent = node.parent;
			const parentIsApproved = parent.data.state === "승인됨";
			const isApproved = node.data.state === "승인됨";
			switch (data.cmd) {
			case "copy":
				CLIPBOARD = {
					mode : data.cmd,
					data : node.toDict(true, function(dict, node) {
						delete dict.key;
					}),
				};
				break;
			case "clear":
				CLIPBOARD = null;
				break;
			case "paste":
				if (CLIPBOARD.mode === "copy") {
					if (!parentIsApproved && !isApproved) {
						nodePaste(node, CLIPBOARD.data);
					}
				}
				break;
			case "removeLink":
				process(tree, node, selectedNodes, "removeLink");
				break;
			default:
				return;
			}
		}).on("keydown", function(e) {
			let cmd = null;
			switch ($.ui.fancytree.eventToString(e)) {
			case "ctrl+c":
				cmd = "copy";
				break;
			case "ctrl+v":
				cmd = "paste";
				break;
			case "del":
				cmd = "removeLink";
				break;
			}
			if (cmd) {
				$(this).trigger("nodeCommand", {
					cmd : cmd
				});
				return false;
			}
		});
	}

	// 마우스 우클릭 옵션
	$("#treetable").contextmenu({
		delegate : "span.fancytree-node",
		menu : [ {
			title : "정보보기",
			cmd : "info",
			uiIcon : "ui-icon-info",
		}, {
			title : "----"
		}, {
			title : "확장",
			uiIcon : "ui-icon-expand",
			children : [ {
				title : "1 레벨",
				cmd : "expand_1",
				uiIcon : "ui-icon-expand",
			}, {
				title : "2 레벨",
				cmd : "expand_2",
				uiIcon : "ui-icon-expand",
			}, {
				title : "3 레벨",
				cmd : "expand_3",
				uiIcon : "ui-icon-expand",
			}, {
				title : "4 레벨",
				cmd : "expand_4",
				uiIcon : "ui-icon-expand",
			}, {
				title : "5 레벨",
				cmd : "expand_5",
				uiIcon : "ui-icon-expand",
			}, {
				title : "전체확장",
				cmd : "expand",
				uiIcon : "ui-icon-expand",
			}, {
				title : "전체축소",
				cmd : "collapse",
				uiIcon : "ui-icon-collapse",
			} ]
		}, {
			title : "----"
		}, {
			title : "교체",
			uiIcon : "ui-icon-replace",
			cmd : "replace",
			children : [ {
				title : "기존부품교체",
				cmd : "replace_exist",
				uiIcon : "ui-icon-oldpart",
			}, {
				title : "신규부품교체",
				cmd : "replace_new",
				uiIcon : "ui-icon-newpart",
			} ]
		}, {
			title : "추가",
			uiIcon : "ui-icon-adds",
			cmd : "insert",
			children : [ {
				title : "기존부품추가",
				cmd : "exist",
				uiIcon : "ui-icon-oldpart",
			}, {
				title : "신규부품추가",
				cmd : "new",
				uiIcon : "ui-icon-newpart",
			} ]
		}, {
			title : "삭제",
			cmd : "removeLink",
			uiIcon : "ui-icon-remove",
		}, {
			title : "----"
		}, {
			title : "체크인",
			cmd : "checkin",
			uiIcon : "ui-icon-checkin",
			disabled : true
		}, {
			title : "체크아웃",
			cmd : "checkout",
			uiIcon : "ui-icon-checkout",
			disabled : true
		}, {
			title : "체크아웃취소",
			cmd : "undocheckout",
			uiIcon : "ui-icon-undocheckout",
			disabled : true
		}, {
			title : "----"
		}, {
			title : "복사",
			cmd : "copy",
			uiIcon : "ui-icon-copy",
			disabled : true,
		}, {
			title : "붙여넣기",
			cmd : "paste",
			uiIcon : "ui-icon-paste",
			disabled : true,
		}, {
			title : "비우기",
			cmd : "empty",
			uiIcon : "ui-icon-empty",
			disabled : true,
		}, ],
		beforeOpen : function(event, ui) {
			const tree = $("#treetable").fancytree("getTree");
			const selectedNodes = tree.getSelectedNodes();
			const node = $.ui.fancytree.getNode(ui.target);
			const parent = node.parent;
			const isCheckOut = node.data.isCheckOut;
			const parentIsApproved = parent.data.state === "승인됨";
			const isApproved = node.data.state === "승인됨";

			// 삭제 승인됨 상태가 아니여야함..
			// 부모도 승인됨 상태가 아니여야할거같은데??
			$("#treetable").contextmenu("enableEntry", "removeLink", !parentIsApproved);
			$("#treetable").contextmenu("enableEntry", "removeMultiLink", selectedNodes.length > 1);

			// 추가 자체도.. 승인됨이 일단 아닐 경우
			$("#treetable").contextmenu("enableEntry", "insert", !isApproved);
			$("#treetable").contextmenu("enableEntry", "replace", !parentIsApproved);

			// 체크인 체크아웃 체크아웃취소
			$("#treetable").contextmenu("enableEntry", "checkin", isCheckOut);

			// 부모도 승인됨이 아니고, 자신도 승인됨이 아니여야함, 자신이 체크아웃 상태가 아니여야함
			$("#treetable").contextmenu("enableEntry", "checkout", !isCheckOut && (!isApproved && !parentIsApproved));
			$("#treetable").contextmenu("enableEntry", "undocheckout", isCheckOut);

			// 복사 체크아웃상태는 못하게
			$("#treetable").contextmenu("enableEntry", "copy", !isCheckOut);

			// 붙여넣기
			$("#treetable").contextmenu("enableEntry", "paste", !!CLIPBOARD && (!isApproved && !parentIsApproved));

			$("#treetable").contextmenu("enableEntry", "empty", !!CLIPBOARD);

			node.setActive();
		},
		select : function(event, ui) {
			const that = this;
			const tree = $("#treetable").fancytree("getTree");
			const node = tree.getActiveNode();
			const selectedNodes = tree.getSelectedNodes();
			process(tree, node, selectedNodes, ui.cmd);
		},
	});

	// 마우스 우클릭 액션
	function process(tree, node, selectedNodes, action) {
		let url;
		const oid = node.data.oid;
		const poid = node.data.poid;
		const link = node.data.link;
		const params = new Object();
		// 		const tree = $("#treetable").fancytree("getTree");
		const parent = node.parent;
		const parentIsApproved = parent.data.state === "승인됨";
		const isApproved = node.data.state === "승인됨";

		// 정보보기
		if (action === "info") {
			url = getCallUrl("/part/view?oid=" + oid);
			_popup(url, 1400, 700, "n");
		} else if (action === "replace_new") {
			url = getCallUrl("/part/append?method=replace_new");
			_popup(url, 1400, 700, "n");
		} else if (action === "replace_exist") {
			url = getCallUrl("/part/popup?method=replace_exist&multi=false");
			_popup(url, 1400, 700, "n");
			// 신규 생성 후 붙이기
		} else if (action === "new") {
			url = getCallUrl("/part/append?method=append");
			_popup(url, 1400, 700, "n");
			// 잘라내기
		} else if (action === "exist") {
			url = getCallUrl("/part/popup?method=exist&multi=true");
			_popup(url, 1400, 700, "n");
			// 복사
		} else if (action === "copy") {
			CLIPBOARD = {
				mode : "copy",
				data : node.toDict(true, function(dict, node) {
					delete dict.key;
				}),
			};
			// 붙여넣기
		} else if (action === "paste") {
			if (!parentIsApproved && !isApproved) {
				nodePaste(node, CLIPBOARD.data);
			}
		} else if (action === "checkout") {
			openLayer();
			url = getCallUrl("/bom/checkout?oid=" + oid);
			call(url, null, function(data) {
				if (data.result) {
					const resNode = data.resNode;
					updateNode(node, resNode);
				}
				closeLayer();
			}, "GET");
		} else if (action === "undocheckout") {
			openLayer();
			url = getCallUrl("/bom/undocheckout?oid=" + oid);
			call(url, null, function(data) {
				if (data.result) {
					const resNode = data.resNode;
					updateNode(node, resNode);
				}
				closeLayer();
			}, "GET");
		} else if (action === "checkin") {
			openLayer();
			url = getCallUrl("/bom/checkin?oid=" + oid);
			call(url, null, function(data) {
				if (data.result) {
					const resNode = data.resNode;
					updateNode(node, resNode);
				}
				closeLayer();
			}, "GET");
			// 삭제
		} else if (action === "removeLink") {
			openLayer();
			url = getCallUrl("/bom/removeLink");
			params.poid = poid;
			params.oid = oid;
			call(url, params, function(data) {
				if (data.result) {
					const resNode = data.resNode;
					const parent = node.getParent();
					updateNode(parent, resNode);
				}
				closeLayer();
			});
		} else if (action === "removeMultiLink") {
			if (selectedNodes.length === 0) {
				alert("삭제할 품목을 선택하세요.");
				return false;
			}
			let comp;
			const arr = new Array();
			for (let i = 0; i < selectedNodes.length; i++) {
				const poid = selectedNodes[i].data.poid;
				const target = selectedNodes[i].data.oid;
				if (comp === undefined) {
					comp = poid;
				} else {
					if (comp !== poid) {
						alert("모품목이 다른 품목을 다중으로 삭제 불가능합니다.");
						return false;
					}
				}
				arr.push(target);
			}
			openLayer();
			url = getCallUrl("/bom/removeMultiLink");
			params.poid = poid;
			params.arr = arr;
			call(url, params, function(data) {
				if (data.result) {
					const resNode = data.resNode;
					const parent = node.getParent();
					updateNode(parent, resNode);
				}
				closeLayer();
			});
		}

		const rootNode = tree.getRootNode();
		// 확장 관련
		if (action === "expand_1") {
			// 			openTreeToLevel(1);
			levelExpand(rootNode, 1);
		} else if (action === "expand_2") {
			// 			openTreeToLevel(2);
			levelExpand(rootNode, 2);
		} else if (action === "expand_3") {
			// 			openTreeToLevel(3);
			levelExpand(rootNode, 3);
		} else if (action === "expand_4") {
			// 			openTreeToLevel(4);
			levelExpand(rootNode, 4);
		} else if (action === "expand_5") {
			levelExpand(rootNode, 5);
		} else if (action === "expand") {
			tree.expandAll();
		} else if (action === "collapse") {
			tree.expandAll(false);
		}
	}

	// 확장
	function levelExpand(rootNode, level) {
		rootNode.visit(function(subNode) {
			if (subNode.getLevel() <= level) {
				subNode.setExpanded(true);
			}
		});
	}

	// 드랍
	function drop(node, sourceNodes) {
		const arr = new Array();
		const _arr = new Array();
		for (let i = 0; i < sourceNodes.length; i++) {
			arr.push(sourceNodes[i].data.oid);
			_arr.push(String(sourceNodes[i].data.qty));
		}
		const poid = node.data.oid; // 모
		const url = getCallUrl("/bom/drop");
		const params = {
			poid : poid,
			arr : arr,
			_arr : _arr
		}
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				const isNew = data.isNew;
				const resNode = data.resNode;
				updateNodeNewMarker(node, resNode, data.refList);
			}
			closeLayer();
		});
	}

	// 기존항목 교체
	function replace_exist(arr, callBack) {
		const tree = $("#treetable").fancytree("getTree");
		const node = tree.getActiveNode();
		const item = arr[0].item; // 교체하는대상
		const parent = node.getParent();
		const roid = node.data.oid; // 교체되어지는 대상 OID
		const poid = parent.data.oid; // 교체되어지는 대상의 부모
		const oid = item.part_oid; // 붙여지는 대상
		const url = getCallUrl("/bom/replace");
		const params = {
			poid : poid,
			roid : roid,
			oid : oid
		};
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				const resNode = data.resNode;
				updateNode(parent, resNode);
			}
			closeLayer();
		})
		// 창닫기인데...
		callBack(true, true, "");
	}

	// 기존 품목 추가
	function exist(arr, callBack) {
		const tree = $("#treetable").fancytree("getTree");
		const node = tree.getActiveNode();

		const list = new Array();
		for (let i = 0; i < arr.length; i++) {
			const item = arr[i].item;
			const oid = item.part_oid; // 붙여지는 대상
			list.push(oid);
		}
		const poid = node.data.oid; // 체크아웃될 대상
		const url = getCallUrl("/bom/exist");
		const params = {
			poid : poid,
			list : list
		};
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				const isNew = data.isNew;
				const resNode = data.resNode;
				updateNodeNewMarker(node, resNode, data.refList);
			}
			closeLayer();
		})
		// 창닫기인데...
		callBack(true, false, "");
	}

	// 신규 품목 교체
	function replace_new(oid) {
		const tree = $("#treetable").fancytree("getTree");
		const node = tree.getActiveNode();
		const parent = node.getParent();
		const roid = node.data.oid;
		const poid = parent.data.oid; // 교체되어지는 대상의 부모
		const url = getCallUrl("/bom/replace");
		const params = {
			poid : poid,
			roid : roid,
			oid : oid
		};
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				const resNode = data.resNode;
				updateNode(parent, resNode);
			}
			closeLayer();
		})
	}

	// 신규 품목 추가
	function append(oid) {
		const tree = $("#treetable").fancytree("getTree");
		const node = tree.getActiveNode(); // 활성 노드
		const url = getCallUrl("/bom/append");
		const params = {
			poid : node.data.oid,
			oid : oid
		};
		call(url, params, function(data) {
			if (data.result) {
				const isNew = data.isNew;
				const resNode = data.resNode;
				updateNodeNewMarker(node, resNode, data.refList);
			}
		})
	}

	function nodePaste(node, copyNodes) {
		const poid = node.data.oid; // 붙여넣기 되어지는 대상의 부모
		const arr = new Array();
		const _arr = new Array();
		for (let i = 0; i < copyNodes.length; i++) {
			const oid = copyNodes[i].data.oid;
			const qty = copyNodes[i].data.qty;
			const num = copyNodes[i].data.number;
			if (poid === oid) {
				alert("붙여넣는 품번 : " + num + " 품목이 상위품번과 같습니다.");
				return false;
			}
			arr.push(oid);
			_arr.push(String(qty));
		}
		const url = getCallUrl("/bom/paste");
		const params = {
			poid : poid,
			arr : arr,
			_arr : _arr
		};
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				node.addChildren(CLIPBOARD.data);
				const resNode = data.resNode;
				updateNode(node, resNode);
			}
			closeLayer();
		})
	}

	// 추가 하는순간...무조건 LAZY변경 및 신규 표기
	function updateNodeNewMarker(node, resNode, refList) {
		node.fromDict({
			lazy : true,
			id : resNode.oid,
			icon : resNode.icon,
			data : {
				oid : resNode.oid,
				version : resNode.version,
				isCheckOut : resNode.isCheckOut
			}
		});
		if (node.isLazy()) {
			node.resetLazy();
			node.setExpanded(true);
		}

		setTimeout(function() {
			const tree = $("#treetable").fancytree("getTree");
			for (let i = 0; i < refList.length; i++) {
				const refKey = refList[i];
				const ns = tree.getNodesByRef(refKey);
				for (let k = 0; k < ns.length; k++) {
					const rNode = ns[k];
					rNode.data.isNew = true;
					rNode.renderTitle();
				}
			}
		}, 500);
	}

	// 추가 하는순간...무조건 LAZY변경.
	function updateNode(node, resNode) {
		node.fromDict({
			lazy : true,
			id : resNode.oid,
			icon : resNode.icon,
			data : {
				oid : resNode.oid,
				version : resNode.version,
				isCheckOut : resNode.isCheckOut
			}
		});
		if (node.isLazy()) {
			node.resetLazy();
			node.setExpanded(true);
		}
	}

	// 숫자 체크
	let v;
	function validate(obj, event) {
		v = Number(obj.value);
	}

	// 수량 업데이트
	function update(obj, event) {
		const value = Number(obj.value);
		const key = obj.getAttribute("data-key");
		// 		const keyCode = event.which || event.keyCode;
		// 	    if ((keyCode >= 48 && keyCode <= 57) || (keyCode >= 96 && keyCode <= 105)) {
		// 	    } else {
		// 	    	alert("숫자만 입력가능합니다.");
		// 	        event.preventDefault();
		// 	        document.getElementById(key).value = v;
		// 	    }

		const tree = $("#treetable").fancytree("getTree");
		const node = tree.getNodeByKey(key);
		const parent = node.parent;

		if (parent.data.state === "승인됨") {
			alert("부모품목이 승인됨 상태여서 수량 수정이 불가능합니다.");
			document.getElementById(key).value = node.data.qty;
			return false;
		}

		const link = node.data.link;
		const poid = node.data.poid;
		const oid = node.data.oid;
		const url = getCallUrl("/bom/update");
		openLayer();
		const params = {
			link : link,
			oid : oid,
			poid : poid,
			value : value
		};
		call(url, params, function(data) {
			if (data.result) {
				const resNode = data.resNode;
				updateNode(parent, resNode);
			}
			closeLayer();
		})
	}

	function openTreeToLevel(level) {
		const tree = $("#treetable").fancytree("getTree");
		const rootNode = tree.getRootNode();
		for (let i = 0; i < level; i++) {
			rootNode.visit(function(node) {
				if (node.lazy) {
					if (node.getLevel() <= level) {
						node.load();
						// 						node.setExpanded(true);
					}
				}
			});
		}
	}

	// 품목 추가
	function load() {
		url = getCallUrl("/part/popup?method=loadTree&multi=false");
		_popup(url, 1400, 700, "n");
	}

	// 오른쪽 트리 파괴
	function destroy() {
		document.getElementById("number").value = "";
		document.getElementById("poid").value = "";
		const tree = $.ui.fancytree.getTree("#righttable");
		if (tree !== null) {
			tree.destroy();
		}
	}

	// 오른쪽 트리 로드
	function loadTree(arr, callBack) {
		const dummy = document.querySelector("input[name=skip]:checked");
		let skip;
		if (dummy !== null) {
			skip = "true";
		} else {
			skip = "false";
		}
		destroy();
		const item = arr[0].item;
		const poid = item.part_oid;
		const number = item.number;
		const name = item.name;
		document.getElementById("number").value = number + " / " + name;
		document.getElementById("poid").value = poid;
		$("#righttable").fancytree({
			extensions : [ "dnd5", "table" ],
			debugLevel : 0,
			// 			checkbox : true,
			selectMode : 2,
			dnd5 : {
				autoExpandMS : 100,
				multiSource : true,
				dragStart : function(node, data) {
					return true;
				},
				dragEnter : function(node, data) {
					const sameTree = (data.otherNode.tree === data.tree);
					if (sameTree) {
						return false;
					}
					return true;
				},
			},
			loadChildren : function(event, data) {
				data.node.visit(function(subNode) {
					if (subNode.isLazy() && !subNode.isLoaded()) {
						subNode.resetLazy();
						subNode.load();
					}
				});
			},
			filter : {
				autoExpand : true,
			},
			table : {
				indentation : 20,
				nodeColumnIdx : 4,
			},
			source : $.ajax({
				url : "/Windchill/plm/bom/loadEditor?oid=" + poid + "&skip=" + skip,
				type : "POST",
				cache : false
			}),
			preInit : function() {
				openLayer();
			},
			init : function() {
				closeLayer();
			},
			lazyLoad : function(event, data) {
				const node = data.node;
				const level = node.data.level;
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
				const thum = node.data.thum;
				const level = node.getIndex() + 1;
				list[0].style.textAlign = "center";
				list[0].textContent = level;
				list[1].style.textAlign = "center";
				if (thum != undefined) {
					list[1].innerHTML = "<img src=" + node.data.thum + ">";
				} else {
					list[1].innerHTML = "";
				}
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
			click : function(event, data) {
				if (event.originalEvent.ctrlKey) { // Ctrl 키를 누른 상태에서만 선택
					data.node.toggleSelected();
				} else {
					data.tree.visit(function(node) {
						node.setSelected(false);
					});
					data.node.setSelected(true);
				}
			}
		}).on("nodeCommand", function(event, data) {
			const tree = $.ui.fancytree.getTree(this);
			const refNode = null;
			const moveMode = null;
			let node = tree.getActiveNode();
			const selectedNodes = tree.getSelectedNodes();
			switch (data.cmd) {
			case "copy":
				CLIPBOARD = {
					mode : data.cmd,
					data : selectedNodes,
				// 					data : node.toDict(true, function(dict, node) {
				// 						delete dict.key;
				// 					}),
				};
				break;
			}
		}).on("keydown", function(e) {
			let cmd = null;
			switch ($.ui.fancytree.eventToString(e)) {
			case "ctrl+c":
				cmd = "copy";
				break;
			}
			if (cmd) {
				$(this).trigger("nodeCommand", {
					cmd : cmd
				});
				return false;
			}
		});

		$("#righttable").contextmenu({
			delegate : "span.fancytree-node",
			menu : [ {
				title : "정보보기",
				cmd : "info",
				uiIcon : "ui-icon-info",
			}, {
				title : "확장",
				uiIcon : "ui-icon-expand",
				children : [ {
					title : "1 레벨",
					cmd : "expand_1",
					uiIcon : "ui-icon-expand",
				}, {
					title : "2 레벨",
					cmd : "expand_2",
					uiIcon : "ui-icon-expand",
				}, {
					title : "3 레벨",
					cmd : "expand_3",
					uiIcon : "ui-icon-expand",
				}, {
					title : "4 레벨",
					cmd : "expand_4",
					uiIcon : "ui-icon-expand",
				}, {
					title : "5 레벨",
					cmd : "expand_5",
					uiIcon : "ui-icon-expand",
				}, {
					title : "전체확장",
					cmd : "expand",
					uiIcon : "ui-icon-expand",
				}, {
					title : "전체축소",
					cmd : "collapse",
					uiIcon : "ui-icon-collapse",
				} ]
			}, {
				title : "----"
			}, {
				title : "복사",
				cmd : "copy",
				uiIcon : "ui-icon-copy",
			}, {
				title : "비우기",
				cmd : "empty",
				uiIcon : "ui-icon-empty",
				disabled : true,
			}, ],
			beforeOpen : function(event, ui) {
				const node = $.ui.fancytree.getNode(ui.target);
				$("#righttable").contextmenu("enableEntry", "empty", !!CLIPBOARD);
				node.setActive();
			},
			select : function(event, ui) {
				const that = this;
				const tree = $("#righttable").fancytree("getTree");
				const node = tree.getActiveNode();
				const selectedNodes = tree.getSelectedNodes();
				process(tree, node, selectedNodes, ui.cmd);
			},
		});
		callBack(true, true, "");
	}

	function _reload() {
		leftTreeLoad();
	}
</script>