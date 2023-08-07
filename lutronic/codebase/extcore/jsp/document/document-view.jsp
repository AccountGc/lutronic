<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
<%@page import="com.e3ps.common.comments.CommentsData"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.beans.DocumentData"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
DocumentData data = (DocumentData) request.getAttribute("docData");
List<CommentsData> cList = (List<CommentsData>) request.getAttribute("cList");
String pnum = (String) request.getAttribute("pnum");
WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<% if(data.getDocumentType().equals("금형문서")){ %>
					금형 상세보기
				<% }else{ %>
					문서 상세보기
				<% } %>
			</div>
		</td>
		<td class="right">
			<%
			if(data.isLatest()){
			%>
				<%
				if(data.getState().equals("APPROVED")){
				%>
					<input type="button" value="개정" title="개정" id="reviseBtn">
				<%	
				}
				%>
				<!-- 회수 권한 승인중 && 소유자 || 관리자 -->
				<%
				if(data.isWithDraw()){
				%>
					<input type="button" value="결재회수" title="결재회수" id="withDrawBtn">
				<%	
				}
				%>
				<%
				if(data.getState().equals("INWORK") || data.getState().equals("BATCHAPPROVAL") || data.getState().equals("REWORK")){
				%>
					<input type="button" value="수정" title="수정" class="blue" id="updateBtn">
					<input type="button" value="삭제" title="삭제" class="red" id="deleteBtn">
				<%	
				}
				%>
			<%
			}else{
			%>
				<input type="button" value="최신Rev." title="최신Rev." id="lastestBtn">
			<%	
			}
			%>
			<input type="button" value="Rev.이력" title="Rev.이력" id="versionBtn">
			<input type="button" value="다운로드이력" title="다운로드이력" id="downloadBtn">
			<input type="button" value="결재이력" title="결재이력" id="approveBtn">
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
			<a href="#tabs-3">관련 개발업무</a>
		</li>
		<li>
			<a href="#tabs-4">관련 문서</a>
		</li>
		<li>
			<a href="#tabs-5">관련 ECO</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="350">
				<col width="150">
				<col width="350">
			</colgroup>
			<tr>
				<% if(data.getDocumentType().equals("금형문서")){ %>
				<th class="lb">금형번호</th>
				<% }else{ %>
				<th class="lb">문서번호</th>
				<% } %>
				<td class="indent5"><%=data.getNumber()%></td>
				<% if(data.getDocumentType().equals("금형문서")){ %>
				<th class="lb">금형분류</th>
				<% }else{ %>
				<th class="lb">문서분류</th>
				<% } %>
				<td class="indent5"><%=data.getLocation()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=data.getState()%></td>
				<th class="lb">Rev.</th>
				<td class="indent5">
				</td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=data.getCreator()%></td>
				<th class="lb">수정자</th>
				<td class="indent5"><%=data.getModifier()%></td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5"><%=data.getCreateDate()%></td>
				<th class="lb">수정일</th>
				<td class="indent5"><%=data.getModifyDate()%></td>
			</tr>
			<tr>
				<th class="lb">문서유형</th>
				<td class="indent5"><%=data.getDocumentType()%></td>
				<th class="lb">결재방식</th>
				<td class="indent5"><%=data.getApprovalType()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly"><%=data.getDescription() != null ? data.getDescription() : ""%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td colspan="3" class="indent5">
		<%-- 			<jsp:include page="/eSolution/content/includeAttachFileView"> --%>
		<%-- 				<jsp:param value="p" name="type"/> --%>
		<%-- 				<jsp:param value="<%= data.getOid() %>" name="oid"/> --%>
		<%-- 			</jsp:include> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일
					<br>
					<input type="button" value="일괄 다운" title="일괄 다운"  onclick="">
				</th>
				<td colspan="3" class="indent5">
		<%-- 			<jsp:include page="/eSolution/content/includeAttachFileView"> --%>
		<%-- 				<jsp:param value="<%= data.getOid() %>" name="oid"/> --%>
		<%-- 			</jsp:include> --%>
				</td>
			</tr>
		</table>
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 속성
					</div>
				</td>
			</tr>
		</table>
		<%if(data.getDocumentType().equals("금형문서")){%>
			<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
				<jsp:param value="<%=data.getOid()%>" name="oid" />
				<jsp:param value="mold" name="module"/>
			</jsp:include>
		<%}else{ %>
			<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
				<jsp:param value="<%=data.getOid()%>" name="oid" />
				<jsp:param value="<%=data.getCreator()%>" name="creator" />
				<jsp:param value="doc" name="module"/>
			</jsp:include>
		<%} %>
	</div>
	<div id="tabs-2">
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/part/include_viewPart.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
			<jsp:param value="doc" name="moduleType"/>
			<jsp:param value="관련 품목" name="title" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 관련 개발업무 -->
		<jsp:include page="/extcore/jsp/development/include_viewDevelopment.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
			<jsp:param value="doc" name="moduleType"/>
		</jsp:include>
	</div>
	<div id="tabs-4">
		<!-- 관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include_viewDocument.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
			<jsp:param value="doc" name="moduleType"/>
		</jsp:include>
	</div>
	<div id="tabs-5">
		<!-- 관련 ECO -->
		<jsp:include page="/extcore/jsp/change/include_view_doc_eco.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
			<jsp:param value="doc" name="moduleType"/>
		</jsp:include>
	</div>
</div>

<div class="comment-table">
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 댓글 <span class="blue"><%=cList.size() %></span>
				</div>
			</td>
		</tr>
	</table>
	
	<%
	for(int i=0; i<cList.size(); i++){
	%>
		<table class="view-table">
			<colgroup>
				<col width="100">
				<col width="*">
				<col width="100">
			</colgroup>
			<tr>
				<th class="lb" style="background-color: lime;"><%=cList.get(i).getCreator() %></th>
				<td class="indent5" >
					<%if(cList.get(i).getOPerson()!=null){
					%>
						<span class="btn-link">⤷@<%=cList.get(i).getOPerson()%></span>
					<%
					}
					%>
					<textarea rows="5"  readonly="readonly"><%=cList.get(i).getComments() %></textarea>
				</td>
				<td align="center">
					<input type="button" value="답글" title="답글" class="mb2 blue" data-bs-toggle="modal" data-bs-target="#staticBackdrop" onclick="modalSubmit(<%=cList.get(i).getCNum()%>,<%=cList.get(i).getCStep()%>,'<%=cList.get(i).getCreator()%>');">
					<%if(isAdmin==true || sessionUser.getName().equals(cList.get(i).getId())){
					%>
						<input type="button" value="수정" title="수정" class="mb2" data-bs-toggle="modal" data-bs-target="#replyUpdate" onclick="modalUpSubmit('<%=cList.get(i).getOid()%>','<%=cList.get(i).getComments()%>');">
						<input type="button" value="삭제" title="삭제" class="red" onclick="replyDeleteBtn('<%=cList.get(i).getOid()%>');">
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
	<table class="view-table">
		<colgroup>
			<col width="100">
			<col width="*">
		</colgroup>
		<tr>
			<th class="lb">댓글</th>
			<td class="indent5">
				<textarea rows="5" id="comments"></textarea>
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="right">
				<input type="button" value="댓글 등록" title="댓글 등록" class="blue" id="commentsBtn">
			</td>
		</tr>
	</table>
</div>

<!-- Modal 등록 -->
<div class="modal fade" id="staticBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
  	<div class="modal-dialog">
    	<div class="modal-content">
      		<div class="modal-header">
        		<h5 class="modal-title" id="staticBackdropLabel">답글 등록</h5>
      		</div>
      		<div class="modal-body" style="width:100%;">
        		<textarea rows="10" id="replyCreate" style="width:100%;"></textarea>
      		</div>
      		<div class="modal-footer">
        		<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
        		<button type="button" class="btn btn-primary" id="replyCreateBtn">등록</button>
      		</div>
   		</div>
	</div>
</div>

<!-- Modal 수정 -->
<div class="modal fade" id="replyUpdate" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
  	<div class="modal-dialog">
    	<div class="modal-content">
      		<div class="modal-header">
        		<h5 class="modal-title" id="staticBackdropLabel">댓글 수정</h5>
      		</div>
      		<div class="modal-body" style="width:100%;">
        		<textarea rows="10" id="replyModify" style="width:100%;"></textarea>
      		</div>
      		<div class="modal-footer">
        		<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
        		<button type="button" class="btn btn-success" id="replyModifyBtn">수정</button>
      		</div>
   		</div>
	</div>
</div>

<script type="text/javascript">
	//수정
	$("#updateBtn").click(function () {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/update?oid=" + oid + "&mode=" + mode);
		openLayer();
		document.location.href = url;
	})

	//삭제
	$("#deleteBtn").click(function () {
	
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
	
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/delete?oid=" + oid);
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
	//				opener.loadGridData();
				self.close();
			}
		}, "GET");
	})
	
	//댓글 등록
	$("#commentsBtn").click(function () {
		var param_num = "<%=pnum%>";
		var num;
		if(isEmpty(param_num)){
			num = 0;
		}else{
			num = Number(param_num) +1;
		}
		var oid = document.getElementById("oid").value;
		var comments = $("#comments").val();
		if(isEmpty(comments)){
			alert("댓글을 입력해주세요.");
			return;
		}
		
		if (!confirm("댓글을 등록 하시겠습니까?")) {
			return;
		}
		
		var params = {"oid": oid
								, "comments" : comments
								, "num" : num
								, "step" : 0};
		
		var url = getCallUrl("/doc/createComments");
		call(url, params, function(data) {
			if(data.result){
				alert("댓글이 등록 되었습니다.");
				location.reload();
			}else{
				alert(data.msg);
			}
		});
	})
	
	var reNum;
	var reStep;
	var rePerson;
	//Modal 클릭시 데이터 보냄
	function modalSubmit(num, step, person){
		reNum = num;
		reStep = step;
		rePerson = person;
	}
	
	//답글 등록
	$("#replyCreateBtn").click(function () {
		var comments = $("#replyCreate").val();
		if(isEmpty(comments)){
			alert("답글을 입력해주세요.");
			return;
		}
		
		if (!confirm("답글을 등록 하시겠습니까?")) {
			return;
		}
		
		var params = {"oid": oid
								, "comments" : comments
								, "num" : reNum
								, "step" : reStep+1
								, "person" : rePerson};
		
		var url = getCallUrl("/doc/createComments");
		call(url, params, function(data) {
			if(data.result){
				alert("답글이 등록 되었습니다.");
				location.reload();
			}else{
				alert(data.msg);
			}
		});
	})
	
	var updateOid;
	function modalUpSubmit(oid, reply){
		updateOid = oid;
		$("#replyModify").val(reply);
	}
	
	//댓글 수정
	$("#replyModifyBtn").click(function () {
		var reply = $("#replyModify").val();
		
		if (!confirm("수정 하시겠습니까?")) {
			return;
		}
		
		var params = {"oid": updateOid
								, "comments" : reply};
		
		var url = getCallUrl("/doc/updateComments");
		call(url, params, function(data) {
			if(data.result){
				alert(data.msg);
				location.reload();
			}else{
				alert(data.msg);
			}
		});
	})
	
	//댓글 삭제
	function replyDeleteBtn(oid){
		if (!confirm("삭제 하시겠습니까?")) {
			return;
		}
		
		var url = getCallUrl("/doc/deleteComments?oid=" + oid);
		call(url, null, function(data) {
			if (data.result) {
				alert(data.msg);
				location.reload();
			} else {
				alert(data.msg);
			}
		}, "GET");
		
	}	
	//개정
	$("#reviseBtn").click(function () {
		var url	= getURLString("doc", "reviseDocumentPopup", "do") + "?oid="+$("#oid").val();
		openOtherName(url,"reviseDocumentPopup","350","200","status=no,scrollbars=yes,resizable=yes");
	})
	
	//버전이력
	$("#versionBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "versionHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	//다운로드 이력
	$("#downloadBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "downloadHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	//결재이력
	$("#approveBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("groupware", "historyWork", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	})
	
	//최신버전
	$("#lastestBtn").click(function() {
		var oid = this.value;
		openView(oid);
	})
	
	//결재 회수
	$("#withDrawBtn").click(function() {
		
		var url	= getURLString("common", "withDrawPopup", "do") + "?oid="+$("#oid").val();
		openOtherName(url,"withDrawBtn","400","220","status=no,scrollbars=yes,resizable=yes");
	})
	
	//일괄 다운로드
	$("#batchSecondaryDown").click(function() {
		var form = $("form[name=documentViewForm]").serialize();
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
				case "tabs-1":
					$(".comment-table").show();
					break;
				case "tabs-2":
					const isCreated1 = AUIGrid.isCreated(partGridID);
					if (isCreated1) {
						AUIGrid.resize(partGridID);
						$(".comment-table").hide();
					} else {
						createAUIGrid1(columnPart);
						$(".comment-table").hide();
					}
					break;
				case "tabs-3":
					const isCreated2 = AUIGrid.isCreated(devGridID);
					if (isCreated2) {
						AUIGrid.resize(devGridID);
						$(".comment-table").hide();
					} else {
						createAUIGrid4(columnDev);
						$(".comment-table").hide();
					}
					break;	
				case "tabs-4":
					const isCreated3 = AUIGrid.isCreated(docGridID);
					if (isCreated3) {
						AUIGrid.resize(docGridID);
						$(".comment-table").hide();
					} else {
						createAUIGrid5(columnDoc);
						$(".comment-table").hide();
					}
					break;	
				case "tabs-5":
					const isCreated4 = AUIGrid.isCreated(ecoGridID);
					if (isCreated4) {
						AUIGrid.resize(ecoGridID);
						$(".comment-table").hide();
					} else {
						createAUIGrid7(columnEco);
						$(".comment-table").hide();
					}
					break;	
				}
			}
		});
		createAUIGrid1(columnPart);
		AUIGrid.resize(partGridID);
		createAUIGrid4(columnDev);
		AUIGrid.resize(devGridID);
		createAUIGrid5(columnDoc);
		AUIGrid.resize(docGridID);
		createAUIGrid7(columnEco);
		AUIGrid.resize(ecoGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(partGridID);
		AUIGrid.resize(devGridID);
		AUIGrid.resize(docGridID);
		AUIGrid.resize(ecoGridID);
	});
	
	//Modal
// 	var myModal = document.getElementById('myModal')
// 	var myInput = document.getElementById('myInput')
	
// 	myModal.addEventListener('shown.bs.modal', function () {
// 	  myInput.focus()
// 	})
	
</script>
<style>
	.lb{
		text-align: center;
	}
</style>
