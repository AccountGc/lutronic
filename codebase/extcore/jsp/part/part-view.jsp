<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.part.WTPartDescribeLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.common.comments.beans.CommentsDTO"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.part.dto.PartDTO"%>
<%@page import="net.sf.json.JSONArray"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
PartDTO dto = (PartDTO) request.getAttribute("dto");
Map<String, String> pdf = dto.getPdf();
Map<String, String> dxf = dto.getDxf();
Map<String, String> step = dto.getStep();
WTPart part = (WTPart) CommonUtil.getObject(dto.getOid());
List<CommentsDTO> list = dto.getComments();
String pnum = (String) request.getAttribute("pnum");
WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="bomSelect" id="bomSelect" value="" />
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				품목 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="BOM" title="BOM" onclick="view();">
			<input type="button" value="BOM 에디터" title="BOM 에디터" onclick="editor();">
			<input type="button" value="COMPARE" title="COMPARE" onclick="compare();">
			<%
			if ("DEATH".equals(dto.getState()) && isAdmin) {
			%>
			<input type="button" value="복원" title="복원" onclick="restore();">
			<%
			}
			%>
			<input type="button" value="속성 CLEARING" class="red" title="속성 CLEARING" onclick="_clean();">
			<%
			if (isAdmin) {
			%>
			<%
			}
			%>
			<input type="button" value="일괄 수정" title="일괄 수정" onclick="packageUpdate();">
			<input type="button" value="수정" title="수정" class="blue" onclick="update();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<input type="button" value="채번" title="채번" onclick="change();">
			<input type="button" value="채번(새버전)" title="채번(새버전)" onclick="orderNumber_NewVersion();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
			<%
			if ("DEV_APPROVED".equals(dto.getState())) {
			%>
			<input type="button" value="상태변경" title="상태변경" onclick="changeDev();">
			<%
			}
			%>
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본 정보</a>
		</li>
		<li>
			<a href="#tabs-3">참조 항목</a>
		</li>
		<li>
			<a href="#tabs-4">관련 객체</a>
		</li>
		<li>
			<a href="#tabs-7">이력 관리</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="300">
				<col width="130">
				<col width="300">
				<col width="80">
				<col width="150">
			</colgroup>
			<tr>
				<th class="lb" colspan="6"><%=dto.getName()%></th>
			</tr>
			<tr>
				<th class="lb">품목번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>품목분류</th>
				<td class="indent5">
					<%=dto.getLocation()%>
				</td>
				<td class="" align="center" rowspan="5" colspan="2">
					<jsp:include page="/extcore/jsp/common/thumbnail-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5">
					<%=dto.getState()%>
				</td>
				<th>REV</th>
				<td class="indent5">
					<%=dto.getVersion()%>
					<%
					if (!dto.isLatest()) {
					%>
					&nbsp;
					<b>
						<a href="javascript:latest();">(최신버전으로)</a>
					</b>
					<%
					}
					%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5"><%=dto.getCreateDate()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifyDate()%></td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/content/include_primaryFileView.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						품목 속성
					</div>
				</td>
			</tr>
		</table>
		<jsp:include page="/extcore/jsp/common/attributes-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="part" name="module" />
		</jsp:include>
		<div id="comments-layer">
			<table class="button-table">
				<tr>
					<td class="left">
						<div class="header">
							<img src="/Windchill/extcore/images/header.png">
							댓글
						</div>
					</td>
				</tr>
			</table>
			<%
			for (CommentsDTO cm : list) {
				int depth = cm.getDepth();
				ArrayList<CommentsDTO> reply = cm.getReply();
			%>
			<table class="view-table">
				<tr>
					<th class="lb" style="background-color: rgb(193, 235, 255); width: 158px">
						<%=cm.getCreator()%>
						<br>
						<%=cm.getCreatedDate()%>
					</th>
					<td class="indent5">
						<textarea rows="5" readonly="readonly" style="resize: none;"><%=cm.getComment()%></textarea>
					</td>
					<td class="center" style="width: 80px">
						<input type="button" value="답글" title="답글" class="blue mb5" data-bs-toggle="modal" data-bs-target="#reply" onclick="sendReply('<%=cm.getOid()%>', '<%=cm.getDepth()%>');">
						<input type="button" value="수정" title="수정" class="mb5" data-bs-toggle="modal" data-bs-target="#modify" onclick="sendUpdate('<%=cm.getOid()%>', '<%=cm.getDepth()%>');">
						<%
						if (isAdmin) {
						%>
						<input type="button" value="삭제" title="삭제" class="red" onclick="cmdel('<%=cm.getOid()%>');">
						<%
						}
						%>
					</td>
				</tr>
			</table>
			<br>
			<!-- 답글 -->
			<%
			for (CommentsDTO dd : reply) {
				int width = dd.getDepth() * 25;
			%>
			<table class="view-table" style="border-top: none;">
				<tr>
					<td style="width: <%=width%>px; border-bottom: none; border-left: none; text-align: left; text-align: right; font-size: 22px;">⤷&nbsp;</td>
					<th class="lb" style="background-color: rgb(193, 235, 255); border-top: 2px solid #86bff9; width: 158px">
						<%=dd.getCreator()%>
						<br>
						<%=dd.getCreatedDate()%>
					</th>
					<td class="indent5" style="border-top: 2px solid #86bff9;">
						<textarea rows="5" readonly="readonly" style="resize: none;"><%=dd.getComment()%></textarea>
					</td>
					<td class="center" style="border-top: 2px solid #86bff9; width: 80px">
						<input type="button" value="답글" title="답글" class="blue mb5" data-bs-toggle="modal" data-bs-target="#reply" onclick="sendReply('<%=dd.getOid()%>', '<%=dd.getDepth()%>');">
						<input type="button" value="수정" title="수정" class="mb5" data-bs-toggle="modal" data-bs-target="#modify" onclick="sendUpdate('<%=dd.getOid()%>', '<%=dd.getComment()%>');">
						<%
						if (isAdmin) {
						%>
						<input type="button" value="삭제" title="삭제" class="red" onclick="cmdel('<%=dd.getOid()%>');">
						<%
						}
						%>
					</td>
				</tr>
			</table>
			<br>
			<%
			}
			%>
			<%
			}
			%>
			<table class="view-table">
				<colgroup>
					<col width="155">
					<col width="*">
				</colgroup>
				<tr>
					<th class="lb">댓글</th>
					<td class="indent5">
						<textarea rows="5" name="comments" id="comments" style="resize: none;"></textarea>
					</td>
				</tr>
			</table>
			<table class="button-table">
				<tr>
					<td class="right">
						<input type="button" value="댓글 등록" title="댓글 등록" class="blue" onclick="_write('0');">
					</td>
				</tr>
			</table>
		</div>
		<!-- 댓글 모달 -->
		<%@include file="/extcore/jsp/common/include/comments-include.jsp"%>
	</div>

	<!-- 참조 항목 -->
	<div id="tabs-3">
		<jsp:include page="/extcore/jsp/drawing/include_viewReferenceBy.jsp">
			<jsp:param value="part" name="moduleType" />
			<jsp:param value="<%=dto.getEpmOid()%>" name="oid" />
		</jsp:include>
	</div>

	<!-- 관련 객체 -->
	<div id="tabs-4">
		<jsp:include page="/extcore/jsp/part/part-reference-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>

	<!-- 이력관리 -->
	<div id="tabs-7">
		<jsp:include page="/extcore/jsp/part/include/part-record-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>

</div>
<script type="text/javascript">
	// BOM 뷰
	function view() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/bom/view?oid=" + oid);
		_popup(url, 1600, 800, "n");
	}

	// 수정
	function update() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/update?oid=" + oid);
		document.location.href = url;
	};

	//삭제
	function _delete() {

		if (!confirm("연관된 내용들이 다 삭제 됩니다. (EX:문서, EO, ECO..)\n삭제하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		}, "GET");
	}

	// 최신버전으로 페이지 이동
	function latest() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/latest?oid=" + oid);
		_popup(url, 1600, 550, "n");
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				let isCreated = false;
				switch (tabId) {
				case "tabs-1":
					$(".comment-table").show();
					break;
				case "tabs-2":
					isCreated = AUIGrid.isCreated(drawingGridID);
					if (isCreated) {
						AUIGrid.resize(drawingGridID);
						$(".comment-table").hide();
					} else {
						createAUIGridDrawing(columnsDrawing);
						$(".comment-table").hide();
					}
					break;
				case "tabs-3":
					isCreated = AUIGrid.isCreated(refbyGridID);
					if (isCreated) {
						AUIGrid.resize(refbyGridID);
						$(".comment-table").hide();
					} else {
						createAUIGrid3(columnRefby);
						$(".comment-table").hide();
					}
					break;
				case "tabs-4":
					isCreated90 = AUIGrid.isCreated(myGridID90);
					if (isCreated90) {
						AUIGrid.resize(myGridID90);
						$(".comment-table").hide();
					} else {
						createAUIGrid90(columns90);
						$(".comment-table").hide();
					}

					isCreated106 = AUIGrid.isCreated(myGridID106);
					if (isCreated106) {
						AUIGrid.resize(myGridID106);
						$(".comment-table").hide();
					} else {
						createAUIGrid106(columns106);
						$(".comment-table").hide();
					}
					break;
				case "tabs-5":
					isCreated = AUIGrid.isCreated(adminGridID);
					if (isCreated) {
						AUIGrid.resize(adminGridID);
						$(".comment-table").hide();
					} else {
						createAUIGridAdmin(columnsAdmin);
						$(".comment-table").hide();
					}
					break;
				case "tabs-6":
					isCreated = AUIGrid.isCreated(enDocGridID);
					if (isCreated) {
						AUIGrid.resize(enDocGridID);
						$(".comment-table").hide();
					} else {
						createAUIGridEnDoc(columnEnDoc);
						$(".comment-table").hide();
					}
					break;
				case "tabs-7":
					const isCreated50 = AUIGrid.isCreated(myGridID50); // 버전이력
					if (isCreated50) {
						AUIGrid.resize(myGridID50);
					} else {
						createAUIGrid50(columns50);
					}
					const isCreated51 = AUIGrid.isCreated(myGridID51); // 다운로드이력
					if (isCreated51) {
						AUIGrid.resize(myGridID51);
					} else {
						createAUIGrid51(columns51);
					}
					break;
				case "tabs-8":
					const isCreated80 = AUIGrid.isCreated(myGridID80); // 상위 품목
					if (isCreated80) {
						AUIGrid.resize(myGridID80);
					} else {
						createAUIGrid80(columns80);
					}

					const isCreated81 = AUIGrid.isCreated(myGridID81); // 하위 품목
					if (isCreated81) {
						AUIGrid.resize(myGridID81);
					} else {
						createAUIGrid81(columns81);
					}

					const isCreated82 = AUIGrid.isCreated(myGridID82); // end item
					if (isCreated82) {
						AUIGrid.resize(myGridID82);
					} else {
						createAUIGrid82(columns82);
					}
					break;
				}
			},
		});
	});

	function _clean() {
		if (!confirm("속성 CLEANING을 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/_clean");
		const params = {
			oid : oid
		};
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			}
			closeLayer();
		})
	}

	// BOM 에디터
	function editor() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/bom/editor?oid=" + oid);
		_popup(url, "", "", "f");
	}

	window.addEventListener("resize", function() {
		AUIGrid.resize(drawingGridID);
		AUIGrid.resize(refbyGridID);
		AUIGrid.resize(docGridID);
		AUIGrid.resize(rohs2GridID);
		AUIGrid.resize(ecoGridID);
		AUIGrid.resize(devGridID);
		AUIGrid.resize(adminGridID);
		AUIGrid.resize(verGridID);
		AUIGrid.resize(enDocGridID);
	});

	const oid = document.querySelector("#oid").value;
<%----------------------------------------------------------
*                     AUI BOM 버튼
----------------------------------------------------------%>
	$(document).keydown(function(event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		if (charCode == 17) {
			$("#bomSelect").val("true");
		} else {
			$("#bomSelect").val("false");
		}
	})

	$("#auiBom").click(function() {
		auiBom(oid, '');
	});

	function packageUpdate() {
		const url = getCallUrl("/part/updateAUIPackagePart?oid=" + oid);
		_popup(url, 1500, 600, "n");
	}

	$("#bomE").click(function() {
		var url = getCallUrl("/part/bomEditor") + "?oid=" + oid;
		_popup(url, "1400", "600", "n");

	})

	function compare() {
		const oid = document.getElementById("oid").value;
		const url = "/Windchill/netmarkets/jsp/structureCompare/StructureCompare.jsp?oid=OR:" + oid + "&ncId=5304500442831603818&locale=ko";
		_popup(url, 1600, 600, "n");
	}

	function change() {
		const url = getCallUrl("/part/change?oid=" + oid);
		_popup(url, 1500, 600, "n");
	}

	function orderNumber_NewVersion() {
		const url = getCallUrl("/part/updateAUIPartChange?oid=" + oid);
		_popup(url, 1500, 600, "n");
	}

	function restore() {
		if (confirm("복원하시겠습니까?")) {
			partStateChange('INWORK');
		}
	}
	function changeDev() {
		if (confirm("변경하시겠습니까?")) {
			partStateChange('INWORK');
		}
	}

	window.partStateChange = function(state) {
		var url = getURLString("part", "partStateChange", "do");
		$.ajax({
			type : "POST",
			url : url,
			data : {
				oid : $("#oid").val(),
				state : state
			},
			dataType : "json",
			async : true,
			cache : false,
			error : function(data) {
				var msg = "등록 오류";
				alert(msg);
			},
			beforeSend : function() {
				gfn_StartShowProcessing();
			},
			complete : function() {
				gfn_EndShowProcessing();
			},
			success : function(data) {
				if (data.result) {
					alert("상태가 변경되었습니다.");
					location.reload();
				} else {
					alert(data.message);
				}
			}
		});
	}
</script>