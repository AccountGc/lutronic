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
			<table class="search-table">
				<colgroup>
					<col width="130">
					<col width="*">
				</colgroup>
				<tr>
					<th>문서 분류</th>
					<td class="indent5">
						<input type="text" name="number" id="number" class="width-300" readonly="readonly">
						<input type="hidden" name="poid" id="poid">
						<input type="button" value="품목선택" title="품목선택" onclick="load();" class="blue">
						<input type="button" value="초기화" title="초기화" onclick="destroy();">
					</td>
				</tr>
			</table>
			<br>
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
	const oid = document.getElementById("oid").value;
	const skip = true;
	let CLIPBOARD = null;
	document.addEventListener("DOMContentLoaded", function() {
		$("#treetable").fancytree({
			extensions : [ "dnd5", "filter", "table", "contextMenu" ],
			checkbox : true,
			quicksearch : true,
			debugLevel : 0,
			dnd5 : {
				autoExpandMS : 500,
				preventRecursion : true, // Prevent dropping nodes on own descendants
				preventVoidMoves : true, // Prevent moving nodes 'before self', etc.
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
					const hitMode = data.hitMode;
					if (hitMode === "after" || hitMode === "before") {
						return false;
					}
					data.otherNode.copyTo(node, data.hitMode);
					drop(data.otherNode, node);
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
				const isCheckOut = node.data.isCheckOut;
				if (isCheckOut) {
					// 					node.tr.style.backgroundColor = "yellow";
				}
				const list = node.tr.querySelectorAll("td");
				list[0].style.textAlign = "center";
				list[1].style.textAlign = "center";
				list[1].textContent = node.data.thumb;
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
					"copy" : {
						"name" : "복사",
						"icon" : "copy"
					},
					"cut" : {
						"name" : "잘라내기",
						"icon" : "cut"
					},
					"paste" : {
						"name" : "붙여넣기",
						"icon" : "paste"
					},
					"removeLink" : {
						"name" : "삭제",
						"icon" : "delete",
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
					"new" : {
						"name" : "신규품목추가",
						"icon" : "new"
					},
					"exist" : {
						"name" : "기존품목추가",
						"icon" : "exist"
					},
					"sep3" : "---------",
					"replace_new" : {
						"name" : "신규품목교체",
						"icon" : "replace"
					},
					"replace_exist" : {
						"name" : "기존품목교체",
						"icon" : "replace"
					}
				},
				actions : function(node, action, options) {
					process(node, action);
				}
			},
		}).on("nodeCommand", function(event, data) {
			const tree = $.ui.fancytree.getTree(this);
			const refNode = null;
			const moveMode = null;
			let node = tree.getActiveNode();
			switch (data.cmd) {
			case "cut":

				break;
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
				if (CLIPBOARD.mode === "cut") {
					CLIPBOARD.data.moveTo(node, "child");
					CLIPBOARD.data.setActive();
				} else if (CLIPBOARD.mode === "copy") {
					node.addChildren(CLIPBOARD.data);
					nodePaste(node, CLIPBOARD.data);
				}
				break;
			case "removeLink":
				process(node, "removeLink");
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
		selectbox("depth");
	});

	// 마우스 우클릭 액션
	function process(node, action) {
		let url;
		const oid = node.data.oid;
		const poid = node.data.poid;
		const link = node.data.link;
		const params = new Object();
		logger(oid);

		// 기존항목교체
		if (action === "replace_new") {
		} else if (action === "replace_exist") {
			url = getCallUrl("/part/popup?method=replace_exist&multi=false");
			_popup(url, 1600, 800, "n");
			// 신규 생성 후 붙이기
		} else if (action === "new") {
			url = getCallUrl("/part/append");
			_popup(url, 1600, 800, "n");
			// 기존 삽입
		} else if (action === "cut") {
			// 잘라내기
		} else if (action === "exist") {
			url = getCallUrl("/part/popup?method=exist&multi=false");
			_popup(url, 1600, 800, "n");
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
			node.addChildren(CLIPBOARD.data);
			nodePaste(node, CLIPBOARD.data);
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
		}
	}

	// 드랍
	function drop(otherNode, node) {
		const poid = node.data.oid; // 모
		const oid = otherNode.data.oid; // 자
		const url = getCallUrl("/bom/drop");
		const params = {
			poid : poid,
			oid : oid
		}
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				const resNode = data.resNode;
				updateNode(node, resNode);
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
		const url = getCallUrl("/bom/replace_exist");
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
		const item = arr[0].item;

		const poid = node.data.oid; // 체크아웃될 대상
		const oid = item.part_oid; // 붙여지는 대상
		const url = getCallUrl("/bom/exist");
		const params = {
			poid : poid,
			oid : oid
		};
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				const resNode = data.resNode;
				updateNode(node, resNode);
			}
			closeLayer();
		})
		// 창닫기인데...
		callBack(true, true, "");
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
				const resNode = data.resNode;
				updateNode(node, resNode);
			}
		})
	}

	function nodePaste(node, copyNode) {
		const poid = node.data.oid; // 붙여넣기 되어지는 대상의 부모
		const oid = copyNode.data.oid;
		logger(poid);
		logger(oid);
		if (poid === oid) {
			alert("붙여넣기 하려는 품목과 붙여지는 품목이 동일합니다.");
			return false;
		}
		const url = getCallUrl("/bom/paste");
		const params = {
			poid : poid,
			oid : oid
		};
		openLayer();
		call(url, params, function(data) {
			if (data.result) {
				const resNode = data.resNode;
				updateNode(node, resNode);
			}
			closeLayer();
		})
	}

	// 추가 하는순간...무조건 LAZY변경.
	function updateNode(node, resNode) {
		node.fromDict({
			lazy : true,
			id : resNode.oid,
			icon : resNode.icon,
			data : {
				oid : resNode.oid,
				version : resNode.version
			}
		});
		if (node.isLazy()) {
			node.load(true);
		}
		// 		node.renderTitle();
		// 		selectbox("depth");
	}

	// 품목 추가
	function load() {
		url = getCallUrl("/part/popup?method=loadTree&multi=false");
		_popup(url, 1600, 800, "n");
	}

	// 오른쪽 트리 파괴
	function destroy() {
		document.getElementById("number").value = "";
		document.getElementById("poid").value = "";
		const tree = $.ui.fancytree.getTree("#righttable");
		tree.destroy();
	}

	// 오른쪽 트리 로드
	function loadTree(arr, callBack) {
		const item = arr[0].item;
		const poid = item.part_oid;
		const number = item.number;
		const name = item.name;
		document.getElementById("number").value = number + " / " + name;
		document.getElementById("poid").value = poid;
		$("#righttable").fancytree({
			extensions : [ "dnd5", "table" ],
			debugLevel : 0,
			dnd5 : {
				autoExpandMS : 100,
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
			filter : {
				autoExpand : true,
			},
			table : {
				indentation : 20,
				nodeColumnIdx : 4,
			},
			source : $.ajax({
				url : "/Windchill/plm/bom/loadEditor?oid=" + poid + "&skip=true",
				type : "POST",
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
				list[1].style.textAlign = "center";
				list[1].textContent = node.data.thumb;
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
		}).on("nodeCommand", function(event, data) {
			const tree = $.ui.fancytree.getTree(this);
			const refNode = null;
			const moveMode = null;
			let node = tree.getActiveNode();
			switch (data.cmd) {
			case "copy":
				CLIPBOARD = {
					mode : data.cmd,
					data : node.toDict(true, function(dict, node) {
						delete dict.key;
					}),
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
		callBack(true, true, "");
	}
</script>