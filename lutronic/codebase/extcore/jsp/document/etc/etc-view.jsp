<%@page import="wt.iba.definition.litedefinition.IBAUtility"%>
<%@page import="com.e3ps.common.comments.beans.CommentsDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.doc.etc.dto.EtcDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
EtcDTO dto = (EtcDTO) request.getAttribute("dto");
ArrayList<CommentsDTO> list = dto.getComments();
String type = (String) request.getAttribute("type");
%>
<style type="text/css">
iframe {
	margin-top: 3px;
}
</style>
<script type="text/javascript" src="/Windchill/extcore/smarteditor2/js/HuskyEZCreator.js"></script>

<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="내용인쇄" title="내용인쇄" onclick="print();">
			<%
			if (dto.is_revise()) {
			%>
			<input type="button" value="개정" title="개정" onclick="update('revise');">
			<%
			}
			%>
			<%
			if (dto.is_modify()) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="update('modify');">
			<%
			}
			%>
			<%
			if (dto.is_delete()) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<!-- 			<input type="button" value="최신Rev." title="최신Rev."> -->
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본 정보</a>
		</li>
		<li>
			<a href="#tabs-2">관련 객체</a>
		</li>
		<li>
			<a href="#tabs-3">이력 관리</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="450">
				<col width="130">
				<col width="450">
				<col width="130">
				<col width="450">
			</colgroup>
			<tr>
				<th class="lb">문서번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>문서분류</th>
				<td class="indent5"><%=dto.getLocation()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">REV</th>
				<td class="indent5">
					<%=dto.getVersion()%>.<%=dto.getIteration()%>
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
				<th>등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5"><%=dto.getCreatedDate()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate()%></td>
				<th>문서유형</th>
				<td class="indent5"><%=dto.getDocumentType_name()%></td>
			</tr>
			<tr>
				<th class="lb">결재방식</th>
				<td class="indent5"><%=dto.getApprovaltype_name()%></td>
				<th>내부문서번호</th>
				<td class="indent5"><%=dto.getInteralnumber()%></td>
				<th>프로젝트 코드</th>
				<td class="indent5"><%=dto.getModel_name()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getWriter()%></td>
				<th>보존기간</th>
				<td class="indent5"><%=dto.getPreseration_name()%></td>
				<th>부서</th>
				<td class="indent5"><%=dto.getDeptcode_name()%></td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td colspan="5" class="indent5">
					<textarea name="content" id="content" rows="30"><%=dto.getContent()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="5" class="indent5">
					<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getDescription() == null ? "" : dto.getDescription()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">
					첨부파일
					<!-- 					<input type="button" value="일괄 다운" title="일괄 다운" onclick=""> -->
				</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>

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
					<th class="lb" style="background-color: rgb(193, 235, 255); width: 100px">
						<%=cm.getCreator()%>
						<br>
						<%=cm.getCreatedDate()%>
					</th>
					<td class="indent5">
						<textarea rows="5" readonly="readonly" style="resize: none;"><%=cm.getComment()%></textarea>
					</td>
					<td class="center" style="width: 80px">
						<input type="button" value="답글" title="답글" class="blue mb5" data-bs-toggle="modal" data-bs-target="#reply" onclick="sendReply('<%=cm.getOid()%>', '<%=cm.getDepth()%>');">
						<input type="button" value="수정" title="수정" class="mb5" data-bs-toggle="modal" data-bs-target="#modify" onclick="sendUpdate('<%=cm.getOid()%>', '<%=cm.getComment()%>');">
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
					<th class="lb" style="background-color: rgb(193, 235, 255); border-top: 2px solid #86bff9; width: 100px">
						<%=dd.getCreator()%>
						<br>
						<%=dd.getCreatedDate()%>
					</th>
					<td class="indent5" style="border-top: 2px solid #86bff9;">
						<textarea rows="5" readonly="readonly" style="resize: none;"><%=dd.getComment()%></textarea>
					</td>
					<td class="center" style="border-top: 2px solid #86bff9; width: 80px">
						<input type="button" value="답글" title="답글" class="blue mb5" data-bs-toggle="modal" data-bs-target="#reply" onclick="sendReply('<%=dd.getOid()%>', '<%=dd.getDepth()%>');">
						<input type="button" value="수정" title="수정" class="mb5" data-bs-toggle="modal" data-bs-target="#modify" onclick="sendUpdate('<%=dd.getOid()%>', '<%//=dd.getComment()%>');">
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
					<col width="100">
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

	<div id="tabs-2">
		<!-- 관련 객체 -->
		<jsp:include page="/extcore/jsp/document/etc/include/etc-reference-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 이력관리 -->
		<jsp:include page="/extcore/jsp/document/etc/include/etc-record-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>


</div>




<script type="text/javascript">
const oid = document.getElementById("oid").value;
	// 에디터 로드가 느려서 처리..
	const oEditors = [];
	nhn.husky.EZCreator.createInIFrame({
		oAppRef : oEditors,
		elPlaceHolder : "content", //textarea ID 입력
		sSkinURI : "/Windchill/extcore/smarteditor2/SmartEditor2Skin.html", //martEditor2Skin.html 경로 입력
		fCreator : "createSEditor2",
		htParams : {
			bUseToolbar : false,
			bUseVerticalResizer : false,
			bUseModeChanger : false
		},
		fOnAppLoad : function() {
			oEditors.getById["content"].exec("DISABLE_WYSIWYG");
			oEditors.getById["content"].exec("DISABLE_ALL_UI");
		},
	});
	
	// 최신버전으로 페이지 이동
	function latest() {
		const url = getCallUrl("/etc/latest?oid=" + oid);
		document.location.href = url;
	}

	//수정 및 개정
	function update(mode) {
		const type = "<%= type %>";
		const url = getCallUrl("/etc/update?oid=" + oid + "&mode=" + mode + "&type=" + type);
		document.location.href = url;
	};

	//삭제
	function _delete() {
		const oid = document.getElementById("oid").value;
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/etc/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				self.close();
				opener.loadGridData();
			} else {
				closeLayer();
			}
		}, "DELETE");
	}

	//일괄 다운로드
	function batchSecondaryDown() {
		const form = $("form[name=documentViewForm]").serialize();
		const url = getCallUrl("/common/zip");
		$.ajax({
			type : "POST",
			url : url,
			data : form,
			dataType : "json",
			async : true,
			cache : false,

			error : function(data) {
				var msg = "데이터 검색오류";
				alert(msg);
			},

			success : function(data) {
				console.log(data.message);
				if (data.result) {
					location.href = '/Windchill/jsp/common/content/FileDownload.jsp?fileName=' + data.message + '&originFileName=' + data.message;
				} else {
					alert(data.message);
				}
			},
			beforeSend : function() {
				gfn_StartShowProcessing();
			},
			complete : function() {
				gfn_EndShowProcessing();
			}
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					break;
				case "tabs-2":
					const isCreated90 = AUIGrid.isCreated(myGridID90); // 문서
					if (isCreated90) {
						AUIGrid.resize(myGridID90);
					} else {
						createAUIGrid90(columns90);
					}
					const isCreated91 = AUIGrid.isCreated(myGridID91); // 품목
					if (isCreated91) {
						AUIGrid.resize(myGridID91);
					} else {
						createAUIGrid91(columns91);
					}
					const isCreated100 = AUIGrid.isCreated(myGridID100); // EO
					if (isCreated100) {
						AUIGrid.resize(myGridID100);
					} else {
						createAUIGrid100(columns100);
					}
					const isCreated105 = AUIGrid.isCreated(myGridID105); // ECO
					if (isCreated105) {
						AUIGrid.resize(myGridID105);
					} else {
						createAUIGrid105(columns105);
					}
					const isCreated101 = AUIGrid.isCreated(myGridID101); // CR
					if (isCreated101) {
						AUIGrid.resize(myGridID101);
					} else {
						createAUIGrid101(columns101);
					}
					const isCreatedEcpr = AUIGrid.isCreated(myGridID103); // ECPR
					if (isCreatedEcpr) {
						AUIGrid.resize(myGridID103);
					} else {
						createAUIGrid103(columns103);
					}
					break;
				case "tabs-3":
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
					const isCreated10000 = AUIGrid.isCreated(myGridID10000); // 다운로드이력
					if (isCreated10000) {
						AUIGrid.resize(myGridID10000);
					} else {
						createAUIGrid10000(columns10000);
					}
					const isCreated10001 = AUIGrid.isCreated(myGridID10001); // 외부 유저 메일
					if (isCreated10001) {
						AUIGrid.resize(myGridID10001);
					} else {
						createAUIGrid10001(columns10001);
					}
					break;
				}
			}
		});
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID91);
		AUIGrid.resize(myGridID100);
		AUIGrid.resize(myGridID105);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID103);
		AUIGrid.resize(myGridID50);
		AUIGrid.resize(myGridID51);
		AUIGrid.resize(myGridID10000);
	});
</script>