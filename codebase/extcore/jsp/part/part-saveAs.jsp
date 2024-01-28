<%@page import="com.e3ps.part.service.PartHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<link href="/Windchill/extcore/component/fancytree/src/skin-win8/ui.fancytree.css?v=4" rel="stylesheet">
<link href="/Windchill/extcore/component/contextmenu/dist/jquery.contextMenu.css" rel="stylesheet">
<script src="/Windchill/extcore/component/fancytree/src/jquery-ui-dependencies/jquery.fancytree.ui-deps.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.dnd5.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.filter.js"></script>
<script src="/Windchill/extcore/component/fancytree/src/jquery.fancytree.table.js"></script>
<script src="/Windchill/extcore/component/jqueryui/jquery.ui-contextmenu.min.js"></script>
<script src="/Windchill/extcore/component/contextmenu/dist/jquery.contextMenu.min.js"></script>

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
</head>
<body>

	<table>
		<colgroup>
			<col width="49%">
			<col width="40px;">
			<col width="49%">
		</colgroup>
		<tr>
			<td valign="top">
				<table class="search-table">
					<colgroup>
						<col width="130">
						<col width="*">
					</colgroup>
					<tr>
						<th>품목번호/명</th>
						<td class="indent5">
							<input type="text" name="number" id="number" class="width-400" readonly="readonly" onclick="load();">
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
						<th class="req">품목 다른 품번</th>
						<td class="indent5">
							<input type="text" name="saveAsNum" id="saveAsNum" class="width-400">
							<input type="button" value="다른 품번으로 저장" title="다른 품번으로저장" onclick="saveAs();" class="gray">
						</td>
					</tr>
					<tr>
						<th class="req lb">품목분류</th>
						<td class="indent5">
							<input type="hidden" name="location" id="location" value="/Default/PART_Drawing">
							<span id="locationText"><%=PartHelper.PART_ROOT%></span>
							<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">
						</td>
					</tr>
				</table>
				<br>
				<table id="treetable">
					<colgroup>
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
</body>
<script type="text/javascript">
	function folder() {
		const location = decodeURIComponent("/Default/PART_Drawing");
		const url = getCallUrl("/folder/popup?location=" + location);
		_popup(url, 500, 600, "n");
	}

	function saveAs() {
		const tree = $.ui.fancytree.getTree("#righttable");
		const location = document.getElementById("location").value;
		const saveAsNum = document.getElementById("saveAsNum");

		if (location === "/Default/PART_Drawing") {
			alert("품목분류를 선택하세요.");
			folder();
			return false;
		}

		if (tree === null) {
			alert("다른 품번으로 저장할 BOM을 먼저 선택하세요.");
			saveAsNum.focus();
			return false;
		}

		if (saveAsNum.value === "") {
			alert("저장할 다른 품번을 입력하세요.");
			saveAsNum.focus();
			return false;
		}

		const url = getCallUrl("/bom/saveAs");
		const node = tree.getNodeByKey("_2");
		const params = {
			oid : node.data.oid,
			link : node.data.link,
			saveAsNum : saveAsNum.value,
			location : location
		};
		parent.openLayer();
		logger(params);

		if (!confirm("저장하시겠습니까?")) {
			return false;
		}

		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				const oid = data.copy;
				copyTreeLoad(oid);
			} else {
				parent.closeLayer();
			}
		})
	}

	//품목 추가
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
		destroy();
		const item = arr[0].item;
		const poid = item.part_oid;
		const number = item.number;
		const name = item.name;
		document.getElementById("number").value = number + " / " + name;
		document.getElementById("poid").value = poid;
		$("#righttable").fancytree({
			extensions : [ "table" ],
			debugLevel : 0,
			checkbox : false,
			selectMode : 2,
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
				nodeColumnIdx : 3,
			},
			source : $.ajax({
				url : "/Windchill/plm/bom/loadEditor?oid=" + poid + "&skip=true",
				type : "POST",
				cache : false
			}),
			preInit : function() {
				parent.openLayer();
			},
			init : function() {
				parent.closeLayer();
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
				logger(node);
				const list = node.tr.querySelectorAll("td");
				const thum = node.data.thum;
				list[0].style.textAlign = "center";
				if (thum != undefined) {
					list[0].innerHTML = "<img src=" + node.data.thum + ">";
				} else {
					list[0].innerHTML = "";
				}
				list[1].style.textAlign = "center";
				list[1].textContent = node.data.level;
				list[2].style.textAlign = "center";
				list[2].textContent = node.data.number;
				list[4].style.textAlign = "center";
				list[4].textContent = node.data.qty + "개";
				list[5].style.textAlign = "center";
				list[5].textContent = node.data.version;
				list[6].style.textAlign = "center";
				list[6].textContent = node.data.state;
				list[7].style.textAlign = "center";
				list[7].textContent = node.data.creator;
			},
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
			}, ],
			beforeOpen : function(event, ui) {
				const node = $.ui.fancytree.getNode(ui.target);
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

	function process(tree, node, selectedNodes, action) {
		let url;
		const oid = node.data.oid;

		// 정보보기
		if (action === "info") {
			url = getCallUrl("/part/view?oid=" + oid);
			_popup(url, 1400, 700, "n");
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
	// 오른쪽 트리 로드
	function copyTreeLoad(oid) {
		$("#treetable").fancytree({
			extensions : [ "table" ],
			debugLevel : 0,
			checkbox : false,
			selectMode : 2,
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
				nodeColumnIdx : 3,
			},
			source : $.ajax({
				url : "/Windchill/plm/bom/loadEditor?oid=" + oid + "&skip=true",
				type : "POST",
				cache : false
			}),
			preInit : function() {
				parent.openLayer();
			},
			init : function() {
				parent.closeLayer();
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
				const thum = node.data.thum;
				list[0].style.textAlign = "center";
				if (thum != undefined) {
					list[0].innerHTML = "<img src=" + node.data.thum + ">";
				} else {
					list[0].innerHTML = "";
				}
				list[1].style.textAlign = "center";
				list[1].textContent = node.data.level;
				list[2].style.textAlign = "center";
				list[2].textContent = node.data.number;
				list[4].style.textAlign = "center";
				list[4].textContent = node.data.qty + "개";
				list[5].style.textAlign = "center";
				list[5].textContent = node.data.version;
				list[6].style.textAlign = "center";
				list[6].textContent = node.data.state;
				list[7].style.textAlign = "center";
				list[7].textContent = node.data.creator;
			},
		});

		$("#treetable").contextmenu({
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
			}, ],
			beforeOpen : function(event, ui) {
				const node = $.ui.fancytree.getNode(ui.target);
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
	}
</script>
</html>