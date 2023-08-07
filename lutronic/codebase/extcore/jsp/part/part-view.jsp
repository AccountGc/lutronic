<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.common.comments.CommentsData"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.part.beans.PartData"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
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
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				품목 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="속성 Clearing" title="속성 Clearing" class="blue" onclick="">
			<input type="button" value="일괄 수정" title="일괄 수정" onclick="">
			<input type="button" value="수정" title="수정" onclick="update('modify');">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="채번" title="채번"  onclick="">
			<input type="button" value="채번(새버전)" title="채번(새버전)"  onclick="">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
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
				<th>품목분류</th>
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
				<th>Rev.</th>
				<td class="indent5">
<%-- 					<%=data.getVersion()%>.<%=data.getIteration %>(<%=data.getViewName %>) --%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=data.getCreator()%></td>
				<th>수정자</th>
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
<%-- 					<jsp:include page="/eSolution/content/includeAttachFileView"> --%>
<%-- 						<jsp:param value="<%=data.getOid() %>" name="oid"/> --%>
<%-- 					</jsp:include> --%>
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
	<div id="tabs-4">
<%-- 		<jsp:include page="/extcore/jsp/drawing/drawingView_include.jsp"> --%>
<%-- 			<jsp:param value="part" name="moduleType"/> --%>
<%-- 			<jsp:param value="<%=data.getOid() %>" name="oid"/> --%>
<%-- 			<jsp:param value="관련 도면" name="title"/> --%>
<%-- 			<jsp:param value="epmOid" name="paramName"/> --%>
<%-- 		</jsp:include> --%>
	</div>
	
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
	<div id="tabs-7">
		<jsp:include page="/extcore/jsp/change/include_view_ecr_eco.jsp">
			<jsp:param value="part" name="moduleType"/>
			<jsp:param value="<%=data.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	
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
	function update(mode) {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/update?oid=" + oid + "&mode=" + mode);
		openLayer();
		document.location.href = url;
	}

	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
// 				opener.loadGridData();
				self.close();
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

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				let isCreated = false;
				switch (tabId) {
				case "tabs-2":
					isCreated = AUIGrid.isCreated(drawingGridID);
					if (isCreated) {
						AUIGrid.resize(drawingGridID);
					} else {
						createAUIGridDrawing(columnsDrawing);
					}
					break;
				case "tabs-3":
					isCreated = AUIGrid.isCreated(refbyGridID);
					if (isCreated) {
						AUIGrid.resize(refbyGridID);
					} else {
						createAUIGrid3(columnRefby);
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
					} else {
						createAUIGrid5(columnDoc);
					}
					break;
				case "tabs-6":
					isCreated = AUIGrid.isCreated(rohs2GridID);
					if (isCreated) {
						AUIGrid.resize(rohs2GridID);
					} else {
						createAUIGridRohs2(columnRohs2);
					}
					break;
				case "tabs-7":
					isCreated = AUIGrid.isCreated(ecoGridID);
					if (isCreated) {
						AUIGrid.resize(ecoGridID);
					} else {
						createAUIGrid7(columnEco);
					}
					break;
				case "tabs-8":
					isCreated = AUIGrid.isCreated(devGridID);
					if (isCreated) {
						AUIGrid.resize(devGridID);
					} else {
						createAUIGrid4(columnDev);
					}
					break;
				case "tabs-9":
					isCreated = AUIGrid.isCreated(adminGridID);
					if (isCreated) {
						AUIGrid.resize(adminGridID);
					} else {
						createAUIGridAdmin(columnsAdmin);
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
	});
</script>