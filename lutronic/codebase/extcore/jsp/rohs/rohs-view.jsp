<%@page import="com.e3ps.common.comments.beans.CommentsDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.rohs.dto.RohsData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
RohsData dto = (RohsData) request.getAttribute("dto");
List<Map<String,Object>> list = (List<Map<String,Object>>) request.getAttribute("list");
ArrayList<CommentsDTO> commentsList = dto.getComments();
%>

<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				RoHS 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if(dto.isLatest()){
			%>
				<input type="button" value="물질복사" title="물질복사" class="" id="copyRohs">
				<%
				if(dto.getState().equals("APPROVED")){
				%>
					<input type="button" value="개정" title="개정" class="" id="reviseBtn">
				<%	
				}
				%>
				<!-- 회수 권한 승인중 && 소유자 || 관리자 -->
				<%
				if(dto.isWithDraw()){
				%>
					<input type="button" value="결재회수" title="결재회수" class="" id="withDrawBtn">
				<%	
				}
				%>
				<%
				if(dto.getState().equals("INWORK") || dto.getState().equals("REWORK")){
				%>
					<input type="button" value="수정" title="수정" class="blue" id="updateBtn">
					<input type="button" value="삭제" title="삭제" class="red" id="deleteBtn">
				<%	
				}
				%>
			<%	
			}
			%>
			<input type="button" value="Rev.이력" title="Rev.이력" class="" id="versionBtn">
			<input type="button" value="다운로드이력" title="다운로드이력" class="" id="downloadBtn">
			<input type="button" value="결재이력" title="결재이력" class="" id="approveBtn">
			<input type="button" value="닫기" title="닫기" class="gray" id="closeBtn" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본 정보</a>
		</li>
		<li>
			<a href="#tabs-2">관련 품목</a>
		</li>
		<li>
			<a href="#tabs-3">관련 대표 물질</a>
		</li>
		<li>
			<a href="#tabs-4">관련 물질</a>
		</li>
		<li>
			<a href="#tabs-5">첨부파일</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th>물질명</th>
				<td colspan="3"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th>물질 번호</th>
				<td><%=dto.getNumber()%></td>
				<th>협력업체</th>
				<td><%=dto.getManufactureDisplay()%></td>
			</tr>
			<tr>
				<th>상태</th>
				<td><%=dto.getStateDisplay()%></td>
				<th>Rev.</th>
				<td><%=dto.getVersion()%></td>
			</tr>
			<tr>
				<th>등록자</th>
				<td><%=dto.getCreator()%></td>
				<th>수정자</th>
				<td><%=dto.getModifier()%></td>
			</tr>
			<tr>
				<th>등록일</th>
				<td><%=dto.getCreateDate()%></td>
				<th>수정일</th>
				<td><%=dto.getModifyDate()%></td>
			</tr>
			<tr>
				<th>결재방식</th>
				<td><%=dto.getApprovalTypeDisplay() == null ? "" : dto.getApprovalTypeDisplay()%></td>
				<th>설명</th>
				<td><%=dto.getDescription() == null ? "" : dto.getDescription()%></td>
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
			for (CommentsDTO cm : commentsList) {
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
								<input type="button" value="수정" title="수정" class="mb5" data-bs-toggle="modal" data-bs-target="#modify" onclick="sendUpdate('<%=dd.getOid()%>', '<%=dd.getDepth()%>');">
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
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/part/include_viewPart.jsp" flush="false" >
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
			<jsp:param value="관련 품목" name="title" />
			<jsp:param value="rohs" name="moduleType"/>
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 관련 대표 물질 -->
		<jsp:include page="/extcore/jsp/rohs/include_viewRohs.jsp" flush="false">
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="represent" name="roleType"/>
			<jsp:param value="관련 대표 물질" name="title"/>
		</jsp:include>
	</div>
	<div id="tabs-4">
		<!-- 관련 물질 -->
		<jsp:include page="/extcore/jsp/rohs/include_viewRohs.jsp" flush="false">
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="composition" name="roleType"/>
			<jsp:param value="관련 물질" name="title"/>
		</jsp:include>
	</div>
	<div id="tabs-5">
		<!-- 첨부 파일 -->
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<%
			if(list.size()>0){
				for(Map<String,Object> file : list){
				%>
					<tr>
						<th><%=file.get("fileType") %></th>
						<td><a href="<%=file.get("fileDown")%>"><%=file.get("fileName") %></a></td>
					</tr>
				<%	
				}
			}else{
			%>	
				<tr>
					<td colspan="2" class="center">데이터가 없습니다.</td>
				</tr>
			<%
			}
			%>
		</table>
	</div>		
</div>

<script type="text/javascript">
	//수정
	$("#updateBtn").click(function () {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/rohs/update?oid=" + oid);
		document.location.href = url;
	})
	
	//삭제
	$("#deleteBtn").click(function () {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/rohs/delete");
		let params = new Object();
		params.oid = oid;
		call(url, params, function(data) {
			if (data.result) {
				alert(data.msg);
				opener.loadGridData();
				self.close();
			}else{
				alert(data.msg);
			}
		});
	})
			
	//개정
	$("#reviseBtn").click(function () {
		const oid = $("#oid").val();
		const url = getCallUrl("/rohs/reviseRohs?oid=" + oid);
		document.location.href = url;
	})
	
	//버전이력
	$("#versionBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/common/versionHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	//다운로드 이력
	$("#downloadBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/common/downloadHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	//결재이력
	$("#approveBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/groupware/workHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	//copy
	$('#copyRohs').click(function() {
		const oid = $("#oid").val();
		const url = getCallUrl("/rohs/copyRohs?oid=" + oid);
		_popup(url, 1500, 550, "n");
	})
	
	//결재 회수
	$("#withDrawBtn").click(function() {
		const oid = $("#oid").val();
		const url = getCallUrl("/common/withDrawPopup?oid=" + oid);
		_popup(url, 1500, 550, "n");
	})
	
	//일괄 다운로드
	$("#batchSecondaryDown").click(function() {
		var form = $("form[name=rohsViewForm]").serialize();
		var url	= getURLString("common", "batchSecondaryDown", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			
			error:function(data){
				var msg = "데이터 검색오류";
				alert(msg);
			},
			
			success:function(data){
				console.log(data.message);
				if(data.result) {
					location.href = '/Windchill/jsp/common/content/FileDownload.jsp?fileName='+data.message+'&originFileName='+data.message;
				}else {
					alert(data.message);
				}
			}
			,beforeSend: function() {
				gfn_StartShowProcessing();
	        }
			,complete: function() {
				gfn_EndShowProcessing();
	        }
		});
		
	})
	
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-2":
					const isCreated1 = AUIGrid.isCreated(partGridID);
					if (isCreated1) {
						AUIGrid.resize(partGridID);
					} else {
						createAUIGrid1(columnPart);
					}
					break;
				case "tabs-3":
					const isCreated2 = AUIGrid.isCreated(rohsGridID);
					if (isCreated2) {
						AUIGrid.resize(rohsGridID);
					} else {
						createAUIGridRohs1(columnRohs);
					}
					break;
				case "tabs-4":
					const isCreated3 = AUIGrid.isCreated(rohs2GridID);
					if (isCreated3) {
						AUIGrid.resize(rohs2GridID);
					} else {
						createAUIGridRohs2(columnRohs2);
					}
					break;
				}
			}
		});
		createAUIGrid1(columnPart);
		AUIGrid.resize(partGridID);
		createAUIGridRohs1(columnRohs);
		AUIGrid.resize(rohsGridID);
		createAUIGridRohs2(columnRohs2);
		AUIGrid.resize(rohs2GridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(partGridID);
		AUIGrid.resize(rohsGridID);
		AUIGrid.resize(rohs2GridID);
	});
</script>
