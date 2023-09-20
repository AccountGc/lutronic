<%@page import="com.e3ps.common.comments.CommentsData"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
DocumentDTO dto = (DocumentDTO) request.getAttribute("dto");
List<CommentsData> cList = (List<CommentsData>) request.getAttribute("cList");
String pnum = (String) request.getAttribute("pnum");
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
			<input type="button" value="개정" title="개정">
			<input type="button" value="결재회수" title="결재회수">
			<input type="button" value="수정" title="수정" class="blue">
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<input type="button" value="최신Rev." title="최신Rev.">
<!-- 			<input type="button" value="Rev.이력" title="Rev.이력"> -->
<!-- 			<input type="button" value="다운로드이력" title="다운로드이력"> -->
<!-- 			<input type="button" value="결재이력" title="결재이력"> -->
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
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">문서번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>문서분류</th>
				<td class="indent5"><%=dto.getLocation()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
				<th>REV</th>
				<td class="indent5"><%=dto.getVersion()%>.<%=dto.getIteration()%></td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5"><%=dto.getCreatedDate()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate()%></td>
			</tr>
			<tr>
				<th class="lb">문서유형</th>
				<td class="indent5"><%=dto.getDocumentType()%></td>
				<th>결재방식</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly" id="description" rows="30"><%=dto.getDescription() == null ? "" : dto.getDescription()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">
					첨부파일
					<!-- 					<input type="button" value="일괄 다운" title="일괄 다운" onclick=""> -->
				</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
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
						속성
					</div>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/part/include_viewPart.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="doc" name="moduleType" />
			<jsp:param value="관련 품목" name="title" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 관련 개발업무 -->
		<jsp:include page="/extcore/jsp/development/include_viewDevelopment.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="doc" name="moduleType" />
		</jsp:include>
	</div>
</div>

<div class="comment-table">
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png">
					댓글
					<span class="blue"><%=cList.size()%></span>
				</div>
			</td>
		</tr>
	</table>

	<%
	for (int i = 0; i < cList.size(); i++) {
	%>
	<table class="view-table">
		<tr>
			<%
			if (cList.get(i).getDeleteYN().equals("N")) {
			%>
			<%
			if (cList.get(i).getCStep() > 0) {
				int w = cList.get(i).getCStep() * 30;
			%>
			<td width="<%=w%>px"></td>
			<%
			}
			%>
			<th class="lb" style="background-color: skyblue;" width="110px"><%=cList.get(i).getCreator()%></th>
			<td class="indent5" style="padding: 0, 0, 0, 5px;">
				<%
				if (cList.get(i).getOPerson() != null) {
				%>
				<span class="btn-link">
					⤷@<%=cList.get(i).getOPerson()%></span>
				<%
				}
				%>
				<textarea rows="5" readonly="readonly"><%=cList.get(i).getComments()%></textarea>
			</td>
			<td align="center" style="padding: 0, 0, 0, 5px;" width="100px">
				<input type="button" value="답글" title="답글" class="mb2 blue" data-bs-toggle="modal" data-bs-target="#staticBackdrop" onclick="modalSubmit(<%=cList.get(i).getCNum()%>,<%=cList.get(i).getCStep()%>,'<%=cList.get(i).getCreator()%>');">
				<%
				// 				if (isAdmin == true || sessionUser.getName().equals(cList.get(i).getId())) {
				%>
				<input type="button" value="수정" title="수정" class="mb2" data-bs-toggle="modal" data-bs-target="#replyUpdate" onclick="modalUpSubmit('<%=cList.get(i).getOid()%>','<%=cList.get(i).getComments()%>');">
				<input type="button" value="삭제" title="삭제" class="red" onclick="replyDeleteBtn('<%=cList.get(i).getOid()%>');">
				<%
				// 				}
				%>
			</td>
			<%
			} else {
			%>
			<td class="indent5" colspan="3">
				<span class="btn-link">
					⤷@<%=cList.get(i).getOPerson()%></span>
				<br>
				삭제된 글입니다.
			</td>
			<%
			}
			%>
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
			<th class="lb" width="110px">댓글</th>
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
			<div class="modal-body" style="width: 100%;">
				<textarea rows="10" id="replyCreate" style="width: 100%;"></textarea>
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
			<div class="modal-body" style="width: 100%;">
				<textarea rows="10" id="replyModify" style="width: 100%;"></textarea>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
				<button type="button" class="btn btn-success" id="replyModifyBtn">수정</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">

const oEditors = [];
nhn.husky.EZCreator.createInIFrame({
	oAppRef : oEditors,
	elPlaceHolder : "description", //textarea ID 입력
	sSkinURI : "/Windchill/extcore/smarteditor2/SmartEditor2Skin.html", //martEditor2Skin.html 경로 입력
	fCreator : "createSEditor2",
	htParams : {
		bUseToolbar : false,
		bUseVerticalResizer : false,
		bUseModeChanger : false
	},
	fOnAppLoad : function(){
			oEditors.getById["description"].exec("DISABLE_WYSIWYG");
			oEditors.getById["description"].exec("DISABLE_ALL_UI");
	},
});

	//수정
	function update () {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/update?oid=" + oid);
		document.location.href = url;
	};

	//삭제
	function _delete() {
		const oid = document.getElementById("oid").value;
		if(!confirm("삭제 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/doc/delete?oid="+oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if(data.result) {
				self.close();
				opener.loadGridData();
			} else {
				closeLayer();
			}
		}, "GET");
	}
	
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
	
	//답글 Modal에 데이터 보냄
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
		var oid = document.getElementById("oid").value;
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
	//수정 Modal에 데이터 보냄
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
	
	//일괄 다운로드
	function batchSecondaryDown() {
		const form = $("form[name=documentViewForm]").serialize();
		const url = getCallUrl("/common/zip");
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
	}
	
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
	
</script>