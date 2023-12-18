<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<link rel="stylesheet" href="/Windchill/extcore/css/approval.css?v=1">
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				결재선 지정
			</div>
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="260">
		<col width="5">
		<col width="300">
		<col width="5">
		<col width="150">
		<col width="5">
		<col width="*">
	</colgroup>
	<tr>
		<!-- 개인결재선 -->
		<td valign="top">
			<input type="text" name="name" id="name" class="AXInput " style="width: 120px;">
			<input type="button" value="저장" title="저장" onclick="_save();" style="position: relative; top: 1.5px;">
			<input type="button" value="삭제" title="삭제" onclick="_delete();" style="position: relative; top: 1.5px;" class="red">
			<div id="grid5000" style="height: 786px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
			<script type="text/javascript">
				let myGridID5000;
				const columns5000 = [ {
					dataField : "name",
					headerText : "개인 결재선 이름",
					dataType : "string",
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "favorite",
					headerText : "즐겨찾기",
					dataType : "boolean",
					width : 80,
					renderer : {
						type : "TemplateRenderer"
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						const oid = item.oid;
						let checked = "";
						if (value) {
							checked = ' checked="checked"';
						}
						let html = "<input type='checkbox' name='name'" + checked + " onclick=\"favorite(this, '" + oid + "');\">";
						return html;
					},
					filter : {
						showIcon : false,
						inline : false
					},
				} ]

				function favorite(obj, oid) {
					const url = getCallUrl("/workspace/favorite")
					const params = {
						oid : oid,
						checked : JSON.parse(obj.checked)
					}
					AUIGrid.showAjaxLoader(myGridID5000);
					openLayer();
					call(url, params, function(data) {
						alert(data.msg);
						if (data.result) {
							loadLine();
							loadFavorite();
						} else {
							AUIGrid.removeAjaxLoader(myGridID5000);
						}
						closeLayer();
					})
				}

				function createAUIGrid5000(columnLayout) {
					const props = {
						headerHeight : 30,
						showRowNumColumn : true,
						showRowCheckColumn : true,
						rowNumHeaderText : "번호",
						selectionMode : "multipleRows",
						hoverMode : "singleRow",
						showAutoNoDataMessage : false,
						showRowCheckColumn : true,
						rowCheckToRadio : true,
						editable : true,
						enableFilter : true,
						showInlineFilter : true,
					}
					myGridID5000 = AUIGrid.create("#grid5000", columnLayout, props);
					AUIGrid.bind(myGridID5000, "cellDoubleClick", function(event) {
						if(!confirm("선택한 개인결재선 정보를 불러옵니다.")) {
							return false;
						}
						const oid = event.item.oid;
						const url = getCallUrl("/workspace/loadFavorite?oid=" + oid);
						AUIGrid.showAjaxLoader(myGridID2000);
						AUIGrid.showAjaxLoader(myGridID3000);
						AUIGrid.showAjaxLoader(myGridID4000);
						openLayer();
						call(url, null, function(data) {
							AUIGrid.removeAjaxLoader(myGridID2000);
							AUIGrid.removeAjaxLoader(myGridID3000);
							AUIGrid.removeAjaxLoader(myGridID4000);
							if (data.result) {
								const approval = data.approval;
								const agree = data.agree;
								const receive = data.receive;
								AUIGrid.setGridData(myGridID2000, agree);
								AUIGrid.setGridData(myGridID3000, approval);
								AUIGrid.setGridData(myGridID4000, receive);
							} else {
								alert(data.msg);
							}
							closeLayer();
						}, "GET");
					})
				}
			</script>
		</td>
		<!-- 공란 -->
		<td>&nbsp;</td>
		<!-- 조직도 -->
		<td valign="top">
			<div id="grid900" style="height: 350px; border-top: 1px solid #3180c3;"></div>
			<script type="text/javascript">
				let myGridID900;
				const approvals = window.approvals;
				const agrees = window.agrees;
				const receives = window.receives;
				const columns900 = [ {
					dataField : "name",
					headerText : "부서명",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				} ]

				function createAUIGrid900(columnLayout) {
					const props = {
						headerHeight : 30,
						rowIdField : "id",
						showRowNumColumn : true,
						showAutoNoDataMessage : false,
						rowNumHeaderText : "번호",
						fillColumnSizeMode : true,
						selectionMode : "multipleRows",
						hoverMode : "singleRow",
						enableDrop : false,
						enableFilter : true,
						showInlineFilter : true,
// 						displayTreeOpen : true
					}
					myGridID900 = AUIGrid.create("#grid900", columnLayout, props);
					load900();
					AUIGrid.bind(myGridID900, "selectionChange", auiGridSelectionChangeHandler);
					AUIGrid.bind(myGridID900, "cellDoubleClick", auiCellDoubleClickHandler);
				}
				
				function auiCellDoubleClickHandler(event) {
					const item = event.item;
					const oid = item.oid;
					// 부서일 경우만 모두 ...
					
					const radioGroup = document.getElementsByName("lineType");
					let selectedValue;
					for (const radioButton of radioGroup) {
						if (radioButton.checked) {
							selectedValue = radioButton.value;
							break;
						}
					}
					
					if(oid.indexOf("Department") > -1) {
						const url = getCallUrl("/department/specify?oid="+oid);
						call(url, null, function(data) {
							const rows = data.list;
							if("agree" === selectedValue) {
								
								for(let i=0; i<rows.length; i++) {
									const oid = rows[i].woid;
									const name = rows[i].name;
									const isUnique1 = AUIGrid.isUniqueValue(myGridID2000, "woid", oid);
									if(!isUnique1) {
										alert("합의라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
									
									const isUnique2 = AUIGrid.isUniqueValue(myGridID3000, "woid", oid);
									if(!isUnique2) {
										alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
									
									const isUnique3 = AUIGrid.isUniqueValue(myGridID4000, "woid", oid);
									if(!isUnique3) {
										alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
								}
								
								AUIGrid.addRow(myGridID2000, rows, "last");
							} else if("approval" === selectedValue) {
								
								for(let i=0; i<rows.length; i++) {
									const oid = rows[i].woid;
									const name = rows[i].name;
									const isUnique1 = AUIGrid.isUniqueValue(myGridID2000, "woid", oid);
									if(!isUnique1) {
										alert("합의라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
									
									const isUnique2 = AUIGrid.isUniqueValue(myGridID3000, "woid", oid);
									if(!isUnique2) {
										alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
									
									const isUnique3 = AUIGrid.isUniqueValue(myGridID4000, "woid", oid);
									if(!isUnique3) {
										alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
								}
								
								AUIGrid.addRow(myGridID3000, rows, "last");
							} else if("receive" === selectedValue) {
								
								for(let i=0; i<rows.length; i++) {
									const oid = rows[i].woid;
									const name = rows[i].name;
									const isUnique1 = AUIGrid.isUniqueValue(myGridID2000, "woid", oid);
									if(!isUnique1) {
										alert("합의라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
									
									const isUnique2 = AUIGrid.isUniqueValue(myGridID3000, "woid", oid);
									if(!isUnique2) {
										alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
									
									const isUnique3 = AUIGrid.isUniqueValue(myGridID4000, "woid", oid);
									if(!isUnique3) {
										alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
										return false;
									}
								}
								AUIGrid.addRow(myGridID4000, rows, "last");
							}
						}, "GET");
					}
				}

				function load900() {
					const url = getCallUrl("/department/load900");
					AUIGrid.showAjaxLoader(myGridID900);
					call(url, null, function(data) {
						AUIGrid.removeAjaxLoader(myGridID900);
						AUIGrid.addTreeRow(myGridID900, data.list);
					}, "GET");
				}

				let timerId = null;
				function auiGridSelectionChangeHandler(event) {
					if (timerId) {
						clearTimeout(timerId);
					}
					timerId = setTimeout(function() {
						const primeCell = event.primeCell;
						const rowItem = primeCell.item;
						const oid = rowItem.oid;
						// 부서일 경우만..
						if(oid.indexOf("Department") > -1) {
							load1000(oid);
						}
					}, 500);
				}

				// 사용자 로드
				function load1000(oid) {
					const url = getCallUrl("/org/load1000?oid=" + oid);
					AUIGrid.showAjaxLoader(myGridID1000);
					call(url, null, function(data) {
						AUIGrid.removeAjaxLoader(myGridID1000);
						AUIGrid.setGridData(myGridID1000, data.list);
					}, "GET");
				}
			</script>
			<!-- 공백라인 -->
			<br>
			<div id="grid1000" style="height: 450px; border-top: 1px solid #3180c3;"></div>
			<script type="text/javascript">
				let myGridID1000;
				const columns1000 = [ {
					dataField : "name",
					headerText : "이름",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "department_name",
					headerText : "부서",
					dataType : "string",
					filter : {
						showIcon : false,
						inline : true
					},
				} ]

				function createAUIGrid1000(columnLayout) {
					const props = {
						headerHeight : 30,
						showRowNumColumn : true,
						rowNumHeaderText : "번호",
						selectionMode : "multipleRows",
						hoverMode : "singleRow",
						showAutoNoDataMessage : false,
						showRowCheckColumn : true,
						enableRowCheckShiftKey : true,
						showDragKnobColumn : true,
						enableDrag : true,
						enableMultipleDrag : true,
						enableDrop : false,
						dropToOthers : true,
						enableFilter : true,
						showInlineFilter : true,
					}
					myGridID1000 = AUIGrid.create("#grid1000", columnLayout, props);
					// 					AUIGrid.bind(myGridID1000, "cellClick", cellClickHandler);
					// 					const oid = document.getElementById("oid").value;
					// 					loadDepartmentUser(oid);
					// 					AUIGrid.bind(_$myGridID, "dropEndBefore", function(event) {
					// 						event.isMoveMode = false;

					// 						const pids = [ "#agree_wrap", "#approval_wrap", "#receive_wrap" ];
					// 						const items = event.items;
					// 						let copy = true;
					// 						for (let i = 0; i < items.length; i++) {
					// 							const item = event.items[0];
					// 							for (let k = 0; k < pids.length; k++) {
					// 								const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid);
					// 								if (!notHave) {
					// 									copy = false;
					// 									break;
					// 								}
					// 							}
					// 						}
					// 						return copy;
					// 					});
				}
			</script>

		</td>
		<!-- 공란 -->
		<td>&nbsp;</td>

		<!-- 결재선 지정 버튼 구역 -->
		<td>
			<table class="select-table">
				<tr>
					<td class="center">결재타입</td>
				</tr>
				<tr>
					<td class="center">
						<div class="pretty p-switch">
							<input type="radio" name="lineType" value="agree">
							<div class="state p-success">
								<label>
									<b>합의</b>
								</label>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="center">
						<div class="pretty p-switch">
							<input type="radio" name="lineType" checked="checked" value="approval">
							<div class="state p-success">
								<label>
									<b>결재</b>
								</label>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="center">
						<div class="pretty p-switch">
							<input type="radio" name="lineType" value="receive">
							<div class="state p-success">
								<label>
									<b>수신</b>
								</label>
							</div>
						</div>
					</td>
				</tr>
			</table>
			<table class="button-table">
				<tr>
					<td class="center">
						<input type="button" value="추가" title="추가" onclick="moveRow();">
						<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
					</td>
				</tr>
				<tr>
					<td class="center pt5">
						<input type="button" value="전체 삭제" title="전체 삭제" class="blue" onclick="_clear();">
					</td>
				</tr>
				<tr>
					<td class="center pt5">
						<input type="button" value="지정" title="지정" class="orange" onclick="register();">
						<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
					</td>
				</tr>
			</table>
		</td>

		<!-- 공란 -->
		<td>&nbsp;</td>

		<!-- 결재선 지정 -->
		<td valign="top">
			<table>
				<tr>
					<td>
						<table class="button-table">
							<tr>
								<td class="left">
									<div class="header">
										<img src="/Windchill/extcore/images/header.png">
										합의 라인
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<div id="grid2000" style="height: 233px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let myGridID2000
							const columns2000 = [ {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 130,
							}, {
								dataField : "id",
								headerText : "아이디",
								dataType : "string",
								width : 130,
							}, {
								dataField : "duty",
								headerText : "직급",
								dataType : "string",
								width : 130,
							}, {
								dataField : "department_name",
								headerText : "부서",
								dataType : "string",
							}, {
								dataField : "woid",
								visible : false
							} ]
							function createAUIGrid2000(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : false,
									selectionMode : "multipleRows",
									hoverMode : "singleRow",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true,
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : true,
									dropToOthers : true,
									useContextMenu : true,
									contextMenuItems : [ {
										label : "선택된 행 삭제",
										callback : contextItemHandler
									} ],
								}
								myGridID2000 = AUIGrid.create("#grid2000", columnLayout, props);
								// 								AUIGrid.bind(_myGridID1, "dropEndBefore", function(event) {
								// 									const pids = [ "#approval_wrap", "#receive_wrap" ];
								// 									const items = event.items;
								// 									let copy = true;
								// 									for (let i = 0; i < items.length; i++) {
								// 										const item = event.items[0];
								// 										for (let k = 0; k < pids.length; k++) {
								// 											const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid);
								// 											if (!notHave) {
								// 												copy = false;
								// 												break;
								// 											}
								// 										}
								// 									}
								// 									return copy;
								// 								});
								AUIGrid.setGridData(myGridID2000, agrees);
							}
						</script>
					</td>
				</tr>
				<tr>
					<td>
						<table class="button-table">
							<tr>
								<td class="left">
									<div class="header">
										<img src="/Windchill/extcore/images/header.png">
										결재 라인
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<div id="grid3000" style="height: 233px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let myGridID3000;
							const columns3000 = [ {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 130,
							}, {
								dataField : "id",
								headerText : "아이디",
								dataType : "string",
								width : 130,
							}, {
								dataField : "duty",
								headerText : "직급",
								dataType : "string",
								width : 130,
							}, {
								dataField : "department_name",
								headerText : "부서",
								dataType : "string",
							}, {
								dataField : "woid",
								visible : false
							} ]
							function createAUIGrid3000(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "순서",
									selectionMode : "multipleRows",
									hoverMode : "singleRow",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true,
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : true,
									dropToOthers : true,
									useContextMenu : true,
									contextMenuItems : [ {
										label : "선택된 행 삭제",
										callback : contextItemHandler
									} ],
								}
								myGridID3000 = AUIGrid.create("#grid3000", columnLayout, props);
								// 								AUIGrid.bind(myGridID3000, "dropEndBefore", function(event) {
								// 									const pids = [ "#agree_wrap", "#receive_wrap" ];
								// 									const items = event.items;
								// 									let copy = true;
								// 									for (let i = 0; i < items.length; i++) {
								// 										const item = event.items[0];
								// 										for (let k = 0; k < pids.length; k++) {
								// 											const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid);
								// 											if (!notHave) {
								// 												copy = false;
								// 												break;
								// 											}
								// 										}
								// 									}
								// 									return copy;
								// 								});
								AUIGrid.setGridData(myGridID3000, approvals);
							}
						</script>
					</td>
				</tr>
				<tr>
					<td>
						<table class="button-table">
							<tr>
								<td class="left">
									<div class="header">
										<img src="/Windchill/extcore/images/header.png">
										수신 라인
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<div id="grid4000" style="height: 233px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let myGridID4000;
							const columns4000 = [ {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 130,
							}, {
								dataField : "id",
								headerText : "아이디",
								dataType : "string",
								width : 130,
							}, {
								dataField : "duty",
								headerText : "직급",
								dataType : "string",
								width : 130,
							}, {
								dataField : "department_name",
								headerText : "부서",
								dataType : "string",
							}, {
								dataField : "woid",
								visible : false
							} ]
							function createAUIGrid4000(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : false,
									selectionMode : "multipleRows",
									hoverMode : "singleRow",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true,
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : true,
									dropToOthers : true,
									useContextMenu : true,
									contextMenuItems : [ {
										label : "선택된 행 삭제",
										callback : contextItemHandler
									} ],
								}
								myGridID4000 = AUIGrid.create("#grid4000", columnLayout, props);
								// 								AUIGrid.bind(myGridID4000, "dropEndBefore", function(event) {
								// 									const pids = [ "#agree_wrap", "#approval_wrap" ];
								// 									const items = event.items;
								// 									let copy = true;
								// 									for (let i = 0; i < items.length; i++) {
								// 										const item = event.items[0];
								// 										for (let k = 0; k < pids.length; k++) {
								// 											const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid);
								// 											if (!notHave) {
								// 												copy = false;
								// 												break;
								// 											}
								// 										}
								// 									}
								// 									return copy;
								// 								});
								AUIGrid.setGridData(myGridID4000, receives);
							}
						</script>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<script type="text/javascript">
	// 기본 스크립트
	
	// 추가 버튼
function moveRow() {
	const radioGroup = document.getElementsByName("lineType");
	let selectedValue;
	for (const radioButton of radioGroup) {
		if (radioButton.checked) {
			selectedValue = radioButton.value;
			break;
		}
	}
	
	const rows = AUIGrid.getCheckedRowItemsAll(myGridID1000);
	if (rows.length <= 0) {
		alert("선택된 사용자가 없습니다.");
		return false;
	}
	
	if("agree" === selectedValue) {
		
// 		for(let i=0; i<rows.length; i++) {
// 			const oid = rows[i].woid;
// 			const name = rows[i].name;
// 			const isUnique1 = AUIGrid.isUniqueValue(myGridID2000, "woid", oid);
// 			if(!isUnique1) {
// 				alert("합의라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
			
// 			const isUnique2 = AUIGrid.isUniqueValue(myGridID3000, "woid", oid);
// 			if(!isUnique2) {
// 				alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
			
// 			const isUnique3 = AUIGrid.isUniqueValue(myGridID4000, "woid", oid);
// 			if(!isUnique3) {
// 				alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
// 		}
		
		AUIGrid.addRow(myGridID2000, rows, "last");
	} else if("approval" === selectedValue) {
		
// 		for(let i=0; i<rows.length; i++) {
// 			const oid = rows[i].woid;
// 			const name = rows[i].name;
// 			const isUnique1 = AUIGrid.isUniqueValue(myGridID2000, "woid", oid);
// 			if(!isUnique1) {
// 				alert("합의라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
			
// 			const isUnique2 = AUIGrid.isUniqueValue(myGridID3000, "woid", oid);
// 			if(!isUnique2) {
// 				alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
			
// 			const isUnique3 = AUIGrid.isUniqueValue(myGridID4000, "woid", oid);
// 			if(!isUnique3) {
// 				alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
// 		}
		
		AUIGrid.addRow(myGridID3000, rows, "last");
	} else if("receive" === selectedValue) {
		
// 		for(let i=0; i<rows.length; i++) {
// 			const oid = rows[i].woid;
// 			const name = rows[i].name;
// 			const isUnique1 = AUIGrid.isUniqueValue(myGridID2000, "woid", oid);
// 			if(!isUnique1) {
// 				alert("합의라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
			
// 			const isUnique2 = AUIGrid.isUniqueValue(myGridID3000, "woid", oid);
// 			if(!isUnique2) {
// 				alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
			
// 			const isUnique3 = AUIGrid.isUniqueValue(myGridID4000, "woid", oid);
// 			if(!isUnique3) {
// 				alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
// 				return false;
// 			}
// 		}
		AUIGrid.addRow(myGridID4000, rows, "last");
	}
	AUIGrid.setAllCheckedRows(myGridID1000);
}

	// 전체삭제
	function _clear() {
		
		if(!confirm("지정된 모든 결재선이 초기화 됩니다?")) {
			return false;
		}
		
		AUIGrid.clearGridData(myGridID2000);
		AUIGrid.clearGridData(myGridID3000);
		AUIGrid.clearGridData(myGridID4000);
	}
	
	function loadLine() {
		const url = getCallUrl("/workspace/loadLine")
		const name = document.getElementById("name").value;
		const params = {
				name : name
		}
		AUIGrid.showAjaxLoader(myGridID5000);
		openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID5000);
			if (data.result) {
				AUIGrid.setGridData(myGridID5000, data.list);
			} else {
				alert(data.msg);
			}
			closeLayer();
		})
	}
	
	
	function contextItemHandler(event) {
		const item = new Object();
		switch (event.contextIndex) {
		case 0:
			const selectedItems = AUIGrid.getSelectedItems(event.pid);
			for (let i = selectedItems.length - 1; i >= 0; i--) {
				const rowIndex = selectedItems[i].rowIndex;
				AUIGrid.removeRow(event.pid, rowIndex);
			}
			break;
		}
	}

	// 저장
	function _save() {
		const name = document.getElementById("name").value;
		if(name === "") {
			alert("개인결재선 이름을 입력하세요.");
			toFocus("name");
			return false;
		}
		
		const agree = AUIGrid.getGridData(myGridID2000);
		const approval = AUIGrid.getGridData(myGridID3000);
		const receive = AUIGrid.getGridData(myGridID4000);
		
		if(approval.length === 0) {
			alert("최소 하나이상의 결재선이 지정되어야합니다.");
			return false;
		}
		

		if(!confirm("저장 하시겠습니까?")) {
			return false;
		}
		
		const url = getCallUrl("/workspace/save");
		const params = {
			name : name,
			approvalList : approval,
			agreeList : agree,
			receiveList : receive
		}
		AUIGrid.showAjaxLoader(myGridID5000);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if(data.result) {
				document.getElementById("name").value = "";
				loadLine();
				// 그리드 클리어
				AUIGrid.clearGridData(myGridID2000);
				AUIGrid.clearGridData(myGridID3000);
				AUIGrid.clearGridData(myGridID4000);
// 				loadFavorite();
			} else {
				AUIGrid.removeAjaxLoader(myGridID5000);
			}
			closeLayer();
		})
	}

	function _delete() {
		const checked = AUIGrid.getCheckedRowItems(myGridID5000);
		if(checked.length === 0) {
			alert("삭제할 개인결재선을 선택하세요.");
			return false;
		}
		
		const oid = checked[0].item.oid;
		const url = getCallUrl("/workspace/delete?oid="+oid);
		AUIGrid.showAjaxLoader(myGridID5000);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if(data.result) {
				loadGridLine();
			} else {
				AUIGrid.removeAjaxLoader(myGridID5000);
			}
			closeLayer();
		}, "DELETE");
	}


	// 기존 즐겨찾기 된거 불러오는 함수
	function loadFavorite() {
		const url = getCallUrl("/workspace/loadFavorite");
		const params = new Object();
		call(url, params, function(data) {
			const approval = data.approval;
			const agree = data.agree;
			const receive = data.receive;
			AUIGrid.setGridData(myGridID2000, agree);
			AUIGrid.setGridData(myGridID3000, approval);
			AUIGrid.setGridData(myGridID4000, receive);
		})
	}

	// 결재선 지정
	function register() {
		const agree = AUIGrid.getGridData(myGridID2000);
		const approval = AUIGrid.getGridData(myGridID3000);
		const receive = AUIGrid.getGridData(myGridID4000);
		opener.setLine(agree, approval, receive);
		self.close();
	}
	
	// 결재선 삭제
	function deleteRow() {
	const checkedAgree = AUIGrid.getCheckedRowItems(myGridID2000);
	for (let i = checkedAgree.length - 1; i >= 0; i--) {
		const rowIndex = checkedAgree[i].rowIndex;
		AUIGrid.removeRow(myGridID2000, rowIndex);
	}

	const checkedApproval = AUIGrid.getCheckedRowItems(myGridID3000);
	for (let i = checkedApproval.length - 1; i >= 0; i--) {
		let rowIndex = checkedApproval[i].rowIndex;
		AUIGrid.removeRow(myGridID3000, rowIndex);
	}

	const checkedReceive = AUIGrid.getCheckedRowItems(myGridID4000);
	for (let i = checkedReceive.length - 1; i >= 0; i--) {
		const rowIndex = checkedItems[i].rowIndex;
		AUIGrid.removeRow(myGridID4000, rowIndex);
	}
}

	
	document.addEventListener("DOMContentLoaded", function() {
		const oid = document.getElementById("oid").value;
		createAUIGrid900(columns900); // 조직도
		AUIGrid.resize(myGridID900);
		createAUIGrid1000(columns1000); // 조직도
		AUIGrid.resize(myGridID1000);
		createAUIGrid2000(columns2000); // 합의
		AUIGrid.resize(myGridID2000);
		createAUIGrid3000(columns3000); // 결재
		AUIGrid.resize(myGridID3000);
		createAUIGrid4000(columns4000); // 수신
		AUIGrid.resize(myGridID4000);
		createAUIGrid5000(columns5000); // 개인결재선
		AUIGrid.resize(myGridID5000);
		load1000(oid);
		loadLine();
		loadFavorite();
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID900); // 조직도
		AUIGrid.resize(myGridID1000);
		AUIGrid.resize(myGridID2000);
		AUIGrid.resize(myGridID3000);
		AUIGrid.resize(myGridID4000);
		AUIGrid.resize(myGridID5000);
	});
</script>
