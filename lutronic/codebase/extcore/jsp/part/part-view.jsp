<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.common.comments.CommentsData"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.part.beans.PartData"%>
<%@page import="net.sf.json.JSONArray"%>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
PartData data = (PartData) request.getAttribute("data");
List<CommentsData> cList = (List<CommentsData>) request.getAttribute("cList");
String pnum = (String) request.getAttribute("pnum");
WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
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
			<input type="button" value="상위품목" title="상위품목"  id="upItem">
			<input type="button" value="하위품목" title="하위품목"  id="downItem">
			<input type="button" value="END ITEM" title="END ITEM"  id="endItem">
<%-- 			<% if(!data.isLateste()){ %> --%>
<%-- 				<input type="button" value="최신Rev." title="최신Rev."  id="latestBtn" value="data.latestOid()"> --%>
<%-- 			<% } %> --%>
			<input type="button"  value="Rev.이력"  title="Rev.이력" id="versionBtn">
			<input type="button"  value="BOM"  title="BOM"  id="auiBom">
			<input type="button"  value="BOM Editor"  title="BOM Editor"  id="bomE">
			<input type="button"  value="Compare"  title="Compare"  id="Compare">
<%-- 			<% if(!data.isLateste()){ %> --%>
			<% if("DEATH".equals(data.getState()) && isAdmin){ %>
			<input type="button"  value="복원"  title="복원"  id="restore">
			<% } %>
<%-- 			<% } %> --%>
<%-- 			<% if(data.isClearing()){ %> --%>
			<input type="button"  value="속성 Clearing"  title="속성 Clearing"  id="attributeCleaning">
<%-- 			<% } %> --%>
<%-- 			<% if(data.isFamliyModify()){ %> --%>
<!-- 			<input type="button"  value="Family 테이블 수정"  title="Family 테이블 수정"  id="updatefamily"> -->
<%-- 			<% } %> --%>
			<%
			if (isAdmin) {
			%>
			<%
			}
			%>
			<input type="button" value="일괄 수정" title="일괄 수정"  id="packageUpdate">
			<input type="button" value="수정" title="수정" class="blue" id="updateBtn" onclick="update();">
			<input type="button" value="삭제" title="삭제" class="red" id="deleteBtn">
			<input type="button" value="채번" title="채번"  id="orderNumber">
			<input type="button" value="채번(새버전)" title="채번(새버전)"  id="orderNumber_NewVersion">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
			<% if("DEV_APPROVED".equals(data.getState())){ %>
				<input type="button" value="상태변경" title="상태변경"  id="changeDev">
			<% } %>
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본 정보</a>
		</li>
		<li>
			<a href="#tabs-2">주도면</a>
		</li>
		<li>
			<a href="#tabs-3">참조 항목</a>
		</li>
		<li>
			<a href="#tabs-4">관련 도면</a>
		</li>
		<li>
			<a href="#tabs-5">관련 문서</a>
		</li>
		<li>
			<a href="#tabs-6">관련 물질</a>
		</li>
		<li>
			<a href="#tabs-7">관련 ECO</a>
		</li>
		<li>
			<a href="#tabs-8">관련 개별 업무</a>
		</li>
		<% if(isAdmin){ %>
		<li>
			<a href="#tabs-9">관리자 속성</a>
		</li>
		<% } %>
		<li>
			<a href="#tabs-10">버전 정보</a>
		</li>
		<li>
			<a href="#tabs-11">환경규제문서</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="500">
				<col width="150">
				<col width="500">
			</colgroup>
			<tr>
				<th class="lb" colspan="5"><%= data.getName() %></th>
			</tr>
			<tr>
				<th class="lb">품목번호</th>
				<td class="indent5"><%=data.getNumber()%></td>
				<th class="lb">품목분류</th>
				<td class="indent5">
<%-- 					<%=data.getLocation()%> --%>
				</td>
				<td class="tdwhiteL" align="center" rowspan="7">
<%--                    	<jsp:include page="/eSolution/drawing/thumbview.do" flush="true"> --%>
<%-- 						<jsp:param name="oid" value="<%=data.getEpmOid() %>" /> --%>
<%-- 					</jsp:include> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5">
<%-- 					<%=data.getLifecycle()%> --%>
				</td>
				<th class="lb">Rev.</th>
				<td class="indent5">
<%-- 					<%=data.getVersion()%>.<%=data.getIteration %>(<%=data.getViewName %>) --%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=data.getCreator()%></td>
				<th class="lb">수정자</th>
				<td class="indent5">
<%-- 					<%=data.getModifier()%> --%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5" ><%=data.getCreateDate()%></td>
				<th class="lb">수정일</th>
				<td class="indent5" ><%=data.getModifyDate()%></td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/content/include_primaryFileView.jsp">
						<jsp:param value="<%= data.getOid() %>" name="oid"/>
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
		<jsp:include page="/extcore/jsp/common/attributes_include.jsp">
			<jsp:param value="<%=data.getOid()%>" name="oid" />
			<jsp:param value="part" name="module"/>
		</jsp:include>
	</div>
	
	<!-- 	주도면 -->
	<div id="tabs-2">
		<jsp:include page="/extcore/jsp/drawing/drawingView_include.jsp">
			<jsp:param value="part" name="moduleType"/>
			<jsp:param value="main" name="epmType" />
			<jsp:param value="<%=data.getOid() %>" name="oid"/>
			<jsp:param value="주도면" name="title"/>
			<jsp:param value="epmOid" name="paramName"/>
		</jsp:include>
	</div>	
	
	<!-- 참조 항목 -->
	<div id="tabs-3">
		<jsp:include page="/extcore/jsp/drawing/include_viewReferenceBy.jsp">
			<jsp:param value="part" name="moduleType"/>
			<jsp:param value="<%=data.getEpmOid() %>" name="oid" />
		</jsp:include>
	</div>
	
	<!-- 관련 도면 -->
<!-- 	<div id="tabs-4"> -->
<%-- 		<jsp:include page="/extcore/jsp/drawing/drawingView_include.jsp"> --%>
<%-- 			<jsp:param value="part" name="moduleType"/> --%>
<%-- 			<jsp:param value="<%=data.getOid() %>" name="oid"/> --%>
<%-- 			<jsp:param value="관련 도면" name="title"/> --%>
<%-- 			<jsp:param value="epmOid" name="paramName"/> --%>
<%-- 		</jsp:include> --%>
<!-- 	</div> -->
	
	<!-- 관련 문서 -->
	<div id="tabs-5">
		<jsp:include page="/extcore/jsp/document/include_viewDocument.jsp">
			<jsp:param value="part" name="moduleType"/>
			<jsp:param value="<%=data.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	
	<!-- 관련 물질 -->
	<div id="tabs-6">
		<jsp:include page="/extcore/jsp/rohs/include_viewRohs.jsp" flush="false">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
			<jsp:param value="part" name="module"/>
			<jsp:param value="관련 RoHs" name="title"/>
			<jsp:param value="composition" name="roleType"/>
		</jsp:include>
	</div>
	
	<!-- 관련 ECO -->
<!-- 	<div id="tabs-7"> -->
<%-- 		<jsp:include page="/extcore/jsp/change/include_view_ecr_eco.jsp"> --%>
<%-- 			<jsp:param value="part" name="moduleType"/> --%>
<%-- 			<jsp:param value="<%=data.getOid() %>" name="oid" /> --%>
<%-- 		</jsp:include> --%>
<!-- 	</div> -->
	
	<!-- 관련 개별 업무 -->
	<div id="tabs-8">
		<jsp:include page="/extcore/jsp/development/include_viewDevelopment.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
			<jsp:param value="part" name="moduleType"/>
		</jsp:include>
	</div>
	
	<% if(isAdmin){ %>
	<!-- 관리자 속성 -->
	<div id="tabs-9">
		<jsp:include page="/extcore/jsp/common/adminAttributes_include.jsp">
			<jsp:param value="part" name="module"/>
			<jsp:param value="<%=data.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	<% } %>
	
	<!-- 버전 정보 -->
	<div id="tabs-10">
		<jsp:include page="/extcore/jsp/development/include_viewVersionInfo.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	
	<!-- 환경규제문서 -->
	<div id="tabs-11">
		<jsp:include page="/extcore/jsp/document/include_environmentalRegulatoryDocument.jsp">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
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
			<tr>
				<%
				if(cList.get(i).getDeleteYN().equals("N")){
				%>
					<%
					if(cList.get(i).getCStep()>0){
						int w = cList.get(i).getCStep() * 30;
					%>
						<td width="<%=w%>px"></td>
					<%
					}
					%>
					<th class="lb" style="background-color: skyblue;" width="110px"><%=cList.get(i).getCreator() %></th>
					<td class="indent5" style="padding:0,0,0,5px;">
						<%
						if(cList.get(i).getOPerson()!=null){
						%>
							<span class="btn-link">⤷@<%=cList.get(i).getOPerson()%></span>
						<%
						}
						%>
						<textarea rows="5"  readonly="readonly"><%=cList.get(i).getComments() %></textarea>
					</td>
					<td align="center" style="padding:0,0,0,5px;" width="100px">
						<input type="button" value="답글" title="답글" class="mb2 blue" data-bs-toggle="modal" data-bs-target="#staticBackdrop" onclick="modalSubmit(<%=cList.get(i).getCNum()%>,<%=cList.get(i).getCStep()%>,'<%=cList.get(i).getCreator()%>');">
						<%if(isAdmin==true || sessionUser.getName().equals(cList.get(i).getId())){
						%>
							<input type="button" value="수정" title="수정" class="mb2" data-bs-toggle="modal" data-bs-target="#replyUpdate" onclick="modalUpSubmit('<%=cList.get(i).getOid()%>','<%=cList.get(i).getComments()%>');">
							<input type="button" value="삭제" title="삭제" class="red" onclick="replyDeleteBtn('<%=cList.get(i).getOid()%>');">
						<%
						}
						%>
					</td>
				<%	
				}else{
				%>
					<td class="indent5" colspan="3">
						<span class="btn-link">⤷@<%=cList.get(i).getOPerson()%></span>
						<br>삭제된 글입니다.
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
	// 수정
	function update () {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/update?oid=" + oid);
		document.location.href = url;
	};
	
	//삭제
	$("#deleteBtn").click(function () {
		
			if (!confirm("삭제 하시겠습니까?")) {
				return false;
			}
		
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/delete");
		const params = new Object();
		params.oid = oid;
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				if(parent.opener.$("#sessionId").val() == "undefined" || parent.opener.$("#sessionId").val() == null){
					parent.opener.location.reload();
				}else {
					parent.opener.$("#sessionId").val("");
					parent.opener.lfn_Search();
				}
				window.close();
			}
		});
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
		
		var url = getCallUrl("/part/createComments");
		call(url, params, function(data) {
			if(data.result){
				alert("댓글이 등록 되었습니다.");
				location.reload();
			}else{
				alert(data.msg);
			}
		});
	});
	
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
		
		var url = getCallUrl("/part/createComments");
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
		
		var url = getCallUrl("/part/updateComments");
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
		var url = getCallUrl("/part/deleteComments?oid=" + oid);
		call(url, null, function(data) {
			if (data.result) {
				alert(data.msg);
				location.reload();
			} else {
				alert(data.msg);
			}
		}, "GET");
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
// 				case "tabs-4":
// 					const isCreated = AUIGrid.isCreated(myGridID1);
// 					if (isCreated) {
// 						AUIGrid.resize(myGridID1);
// 					} else {
// 						createAUIGrid1(columns1);
// 					}
// 					break;
				case "tabs-5":
					isCreated = AUIGrid.isCreated(docGridID);
					if (isCreated) {
						AUIGrid.resize(docGridID);
						$(".comment-table").hide();
					} else {
						createAUIGrid5(columnDoc);
						$(".comment-table").hide();
					}
					break;
				case "tabs-6":
					isCreated = AUIGrid.isCreated(rohs2GridID);
					if (isCreated) {
						AUIGrid.resize(rohs2GridID);
						$(".comment-table").hide();
					} else {
						createAUIGridRohs2(columnRohs2);
						$(".comment-table").hide();
					}
					break;
				case "tabs-7":
					isCreated = AUIGrid.isCreated(ecoGridID);
					if (isCreated) {
						AUIGrid.resize(ecoGridID);
						$(".comment-table").hide();
					} else {
						createAUIGrid7(columnEco);
						$(".comment-table").hide();
					}
					break;
				case "tabs-8":
					isCreated = AUIGrid.isCreated(devGridID);
					if (isCreated) {
						AUIGrid.resize(devGridID);
						$(".comment-table").hide();
					} else {
						createAUIGrid4(columnDev);
						$(".comment-table").hide();
					}
					break;
				case "tabs-9":
					isCreated = AUIGrid.isCreated(adminGridID);
					if (isCreated) {
						AUIGrid.resize(adminGridID);
						$(".comment-table").hide();
					} else {
						createAUIGridAdmin(columnsAdmin);
						$(".comment-table").hide();
					}
					break;
				case "tabs-10":
					isCreated = AUIGrid.isCreated(verGridID);
					if (isCreated) {
						AUIGrid.resize(verGridID);
						$(".comment-table").hide();
					} else {
						createAUIGridVer(columnVer);
						$(".comment-table").hide();
					}
					break;	
				case "tabs-11":
					isCreated = AUIGrid.isCreated(enDocGridID);
					if (isCreated) {
						AUIGrid.resize(enDocGridID);
						$(".comment-table").hide();
					} else {
						createAUIGridEnDoc(columnEnDoc);
						$(".comment-table").hide();
					}
					break;	
				}
			},
		});
	});

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
	*                      상위품목 버튼
	----------------------------------------------------------%>
	$("#upItem").click(function() {
		viewBomList("up");
	})
	<%----------------------------------------------------------
	*                      하위품목 버튼
	----------------------------------------------------------%>
	$("#downItem").click(function() {
		viewBomList("down");
	})
	<%----------------------------------------------------------
	*                      END ITEM 버튼
	----------------------------------------------------------%>
	$("#endItem").click(function() {
		viewBomList("end");
	})
	<%----------------------------------------------------------
	*                      최신버전 버튼
	----------------------------------------------------------%>
	$("#latestBtn").click(function () {
		const loid = this.value;
		openView(loid);
	}),
	<%----------------------------------------------------------
	*                      버전이력 버튼
	----------------------------------------------------------%>
	$("#versionBtn").click(function () {
		const url = getCallUrl("/common/versionHistory?oid=" + oid);
		_popup(url, 830, 600,"n");
	}),
	<%----------------------------------------------------------
	*                     AUI BOM 버튼
	----------------------------------------------------------%>
	$(document).keydown(function(event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		if(charCode ==17){
			$("#bomSelect").val("true");
		}else{
			$("#bomSelect").val("false");
		}
	})
	
	$("#auiBom").click(function() {
		auiBom(oid,'');
	});
	
	$("#packageUpdate").click(function() {
		const url = getCallUrl("/part/updateAUIPackagePart?oid=" + oid);
		_popup(url, 1500, 600,"n");
	})
	
	$("#bomE").click(function() {
<%-- 		var pdmOid = "<%= data.getPDMLinkProductOid() %>"; --%>
<%-- 		var vrOid = "<%= data.getVrOid() %>"; --%>
		debugger;
		
		var url = getCallUrl("/part/bomEditor") + "?oid="+oid;
		_popup(url, "1400", "600", "n");
		
// 		var str = "/Windchill/netmarkets/jsp/explorer/installmsg.jsp?message=ok"
// 				  + "&oid=" + vrOid +
// 				  "&containerId=" + pdmOid + "&applet=com.ptc.windchill.explorer.structureexplorer.StructureExplorerApplet&jars=ptcAnnotator.jar,lib/pview.jar,lib/json.jar&appId=ptc.pdm.ProductStructureExplorer&launchEmpty=false&explorerName=%EC%A0%9C%ED%92%88+%EA%B5%AC%EC%A1%B0+%ED%83%90%EC%83%89%EA%B8%B0&ncid="
// 	    _popup(str, 1100, 600,"n");
	})
	
	$("#Compare").click(function() {
		var str = "/Windchill/netmarkets/jsp/structureCompare/StructureCompare.jsp?oid=OR:" + $("#oid").val() + "&ncId=2374138740478986248&locale=ko"
		_popup(str, 1300, 600,"n");
	})
	
	$("#orderNumber").click(function() {
		const url = getCallUrl("/part/partChange?oid=" + oid);
		_popup(url, 1500, 600,"n");
	})
	
	$("#orderNumber_NewVersion").click(function() {
		const url = getCallUrl("/part/updateAUIPartChange?oid=" + oid);
		_popup(url, 1500, 600,"n");
	})
	
	$("#disuse").click(function() {
		if (confirm("폐기하시겠습니까?")){ 
			partStateChange('DEATH');
		}
	})
	
	$("#restore").click(function() {
		if (confirm("복원하시겠습니까?")){ 
			partStateChange('INWORK');
		}
	})
	$('#changeDev').click(function() {
		if (confirm("변경하시겠습니까?")){ 
			partStateChange('INWORK');
		}
	})
	
	$("#updatefamily").click(function() {//updatePackagePart
		const url = getCallUrl("/part/updatefamilyPart?oid=" + oid);
		popup(url, 1300, 600);
	})
	
	
	$("#attributeCleaning").click(function() {
		if (confirm("속성 Clearing 하시겠습니까?")){ 
			
			var form = $("form[name=partViewForm]").serialize();
			var url	= getCallUrl("/part/attributeCleaning");
			var params = {"oid": $("#oid").val() };

			call(url, params, function(data) {
				alert(data.message);
				if(data.result) {
					location.reload();
				}
			},"POST");
		}
	})
	
function bom2View(){
	//var str = getURLString("part", "PartTree", "do") + "?oid="+$("#oid").val();
	var str = getURLString("part", "viewPartBom", "do") + "?oid="+$("#oid").val();
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBOM2", opts+rest);
    newwin.focus();
}

window.partStateChange = function(state) {
	var url	= getURLString("part", "partStateChange", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			oid : $("#oid").val(),
			state : state
		},
		dataType:"json",
		async: true,
		cache: false,
		error:function(data){
			var msg = "등록 오류";
			alert(msg);
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
		,success: function(data) {
			if(data.result) {
				alert("상태가 변경되었습니다.");
				location.reload();
			}else {
				alert(data.message);
			}
		}
	});
}


function createCDialogWindow(dialogURL, dialogName, w, h, statusBar)
{
	if( typeof(_use_wvs_cookie) != "undefined" ) {
		var cookie_value = document.cookie;
		if( cookie_value != null ) {
			var loc = cookie_value.indexOf("wvs_ContainerOid=");
			if( loc >= 0 ) {
				var subp = cookie_value.substring(loc+4);
				loc = subp.indexOf(";");
				if( loc >= 0 ) subp = subp.substring(0, loc);
				dialogURL += "&" + subp;
			}
		}
	}else {
		var vm_url = "" + document.location;
		if( vm_url != null ) {
			var loc = vm_url.indexOf("ContainerOid=");
			if( loc >= 0 ) {
				var subp = vm_url.substring(loc);
				loc = subp.indexOf("&");
				if( loc >= 0 ) subp = subp.substring(0, loc);
				dialogURL += "&" + subp;
			}
		}
	}

	openWindow(dialogURL, dialogName, w, h, statusBar);
}
	
	
	<%----------------------------------------------------------
	*                      Bom Type에 따른 bom 검색
	----------------------------------------------------------%>
	function viewBomList(bomType){
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/part/bomPartList?oid=" + oid + "&bomType=" + bomType);
		_popup(url, 800, 550,"n");
	}
</script>