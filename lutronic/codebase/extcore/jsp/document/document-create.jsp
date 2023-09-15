<%@page import="wt.doc.DocumentType"%>
<%@page import="wt.ownership.Ownership"%>
<%@page import="com.e3ps.common.util.SequenceDao"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="com.e3ps.rohs.ROHSMaterial"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.folder.FolderEntry"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.inf.container.WTContainerHelper"%>
<%@page import="wt.inf.container.WTContainerRef"%>
<%@page import="wt.org.OrganizationServicesHelper"%>
<%@page import="wt.org.WTOrganization"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> preserationList = (ArrayList<NumberCode>) request.getAttribute("preserationList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
%>
<!-- AUIGrid -->
<%-- <%@include file="/extcore/jsp/common/aui/auigrid.jsp"%> --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
	<form id="form">
		<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT%>">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서 정보
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">문서분류</th>
				<td class="indent5">
					<span id="location"> /Default/Document </span>
				</td>
				<th class="req">결재방식</th>
				<td class="indent5">
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default" checked="checked">
						<div class="state p-success">
							<label>
								<b>기본결재</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default_NonWF">
						<div class="state p-success">
							<label>
								<b>일괄결재</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">문서종류</th>
				<td class="indent5">
					<input type="text" name="documentName" id="documentName" class="width-200">
					<div id="documentNameSearch" style="display: none; border: 1px solid black; position: absolute; background-color: white; z-index: 1;">
						<ul id="documentNameUL" style="list-style-type: none; padding-left: 5px; text-align: left;">
						</ul>
					</div>
				</td>
				<th class="">문서명</th>
				<td class="indent5">
					<input type="text" name="docName" id="docName" class="width-200">
				</td>
			</tr>
			<tr>
				<th class="req lb">문서유형</th>
				<td class="indent5">
					<select name="documentType" id="documentType" class="width-200">
						<option value="">선택</option>
						<option value="$$NDDocument">일반문서</option>
						<option value="$$RDDocument">개발문서</option>
						<option value="$$APDocument">승인원</option>
						<option value="$$RADocument">인증문서</option>
						<option value="$$MMDocument">금형문서</option>
						<option value="$$DSDocument">개발소스</option>
						<option value="$$DDDocument">배포자료</option>
						<option value="$$ROHS">ROHS</option>
						<option value="$$ETDocument">기타문서</option>
					</select>
				</td>
				<th class="req">보존기간</th>
				<td class="indent5">
					<select name="preseration" id="preseration" class="width-200">
						<%
						for (NumberCode preseration : preserationList) {
						%>
						<option value="<%=preseration.getCode()%>" <% if("영구".equals(preseration.getName())){ %> selected <% } %>><%=preseration.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb">프로젝트코드</th>
				<td class="indent5">
					<select name="model" id="model" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode model : modelList) {
						%>
						<option value="<%=model.getCode()%>"><%=model.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
				<th class="">부서</th>
				<td class="indent5">
					<select name="deptcode" id="deptcode" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode deptcode : deptcodeList) {
						%>
						<option value="<%=deptcode.getCode()%>"><%=deptcode.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="lb">내부 문서번호</th>
				<td class="indent5">
					<input type="text" name="interalnumber" id="interalnumber" class="width-200">
				</td>
				<th class="">작성자</th>
				<td class="indent5">
					<input type="text" name="writer" id="writer" class="width-200">
				</td>
			</tr>
			<tr>
				<th class="lb">문서설명</th>
				<td colspan="3" class="indent5">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<!-- 관련 품목 -->
		<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
		</jsp:include>

		<!-- 	관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp">
			<jsp:param value="관련 문서" name="title" />
			<jsp:param value="" name="oid" />
		</jsp:include>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="기안" title="기안" class="red" onclick="create('false');">
					<input type="button" value="결재선 지정" title="결재선 지정" class="blue" onclick="">
					<input type="button" value="임시저장" title="임시저장" class="" onclick="">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
	
		function folder() {
			const location = decodeURIComponent("/Default/문서");
			const url = getCallUrl("/folder?location=" + location + "&container=product&method=setNumber&multi=false");
			popup(url, 500, 600);
		}

		function setNumber(item) {
			const url = getCallUrl("/doc/setNumber");
			const params = new Object();
			params.loc = item.location;
			call(url, params, function(data) {
				document.getElementById("loc").innerHTML = item.location;
				document.getElementById("location").value = item.location;
				document.getElementById("number").value = data.number;
			})
		}
		
		document.addEventListener("DOMContentLoaded", function() {
			selectbox("model");
			selectbox("preseration");
			selectbox("documentType");
			selectbox("deptcode");
		});

		function create(isSelf) {
			const location = document.getElementById("location").value;
			const lifecycle = document.getElementById("lifecycle").value;
			const documentName = document.getElementById("documentName").value;
			const docName = document.getElementById("docName");
			const documentType = document.getElementById("documentType").value;
			const preseration = document.getElementById("preseration").value;
			const model = document.getElementById("model").value;
			const deptcode = document.getElementById("deptcode").value;
			const interalnumber = document.getElementById("interalnumber").value;
			const writer = document.getElementById("writer").value;
			const description = document.getElementById("description").value;
			const _primary = toArray("primary");
			const primary = _primary[0];
			const secondary = toArray("secondarys");
			let docOids = [];
			const appendDoc = AUIGrid.getGridData(myGridID90);
			if(appendDoc.length > 0){
				for(let i = 0; i < appendDoc.length; i++){
					docOids.push(appendDoc[i].oid)
				}
			}
			let partOids = [];
			const appendPart = AUIGrid.getGridData(partGridID);
			if(appendPart.length > 0){
				for(let i = 0; i < appendPart.length; i++){
					partOids.push(appendPart[i].part_oid);
				}
			}
			
// 			if(isEmpty($("#lifecycle").val())){
// 				alert("결재방식을 입력하세요.");
// 				return;					
// 			}
// 			if(isEmpty($("#documentName").val())){
// 				alert("문서종류를 입력하세요.");
// 				return;					
// 			}
// 			if(isEmpty($("#documentType").val())){
// 				alert("문서유형을 입력하세요.");
// 				return;					
// 			}
// 			if(isEmpty($("#preseration").val())){
// 				alert("보존기간을 입력하세요.");
// 				return;					
// 			}
			if (!confirm("등록 하시겠습니까?")) {
				return false;
			}

			const params = new Object();
			const url = getCallUrl("/doc/create");
			params.lifecycle = lifecycle;
			params.documentName = documentName;
			params.docName = docName.value;
			params.preseration = preseration;
			params.model = model;
			params.deptcode = deptcode;
			params.interalnumber = interalnumber;
			params.writer = writer;
			params.self = JSON.parse(isSelf);
			params.description = description;
			params.location = location;
			params.documentType = documentType;
			params.primary = primary;
			params.secondary = secondary;
			params.docOids = docOids;
			params.partOids = partOids;
			debugger;
			parent.openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					document.location.href = getCallUrl("/doc/list");
				}
				parent.closeLayer();
			});
		};

		// jquery 삭제를 해가는 쪽으로 한다..
		document.addEventListener("DOMContentLoaded", function() {
			// DOM이 로드된 후 실행할 코드 작성
			createAUIGrid2(columnsPart);
			AUIGrid.resize(partGridID);
			createAUIGrid90(columns90);
			AUIGrid.resize(myGridID90);
			document.getElementById("docName").focus();
		});

		window.addEventListener("resize", function() {
			AUIGrid.resize(partGridID);
			AUIGrid.resize(docGridID);
		});
		
		// 문서명 키 입력 시 메서드
		document.querySelector("#documentName").addEventListener('keyup', (event) => {
			
			const charCode = (event.which) ? event.which : event.keyCode;
			
			if(charCode == 38 || charCode == 40){
				const searchElem = document.querySelector("#" + event.id + "Search");
				
				if(searchElem && !searchElem.hidden){
					const isAdd = (charCode === 38);
					moveDocumentNameFocus(this.id, isAdd);
				}
			} else if(charCode == 13 || charCode == 27){
				const searchElem = document.querySelector("#" + event.id + "Search");
				
				if(searchElem){
					searchElem.style.display = "none";
				}
			} else {
				searchDocumentName(event.target.id, event.target.value);
			}
		});
		
		$("input[name=documentName]").focusout(function () {
			$("#" + this.id + "Search").hide();
		})
		
		// 문서종류 입력 시 documentName 리스트 출력 메서드
		const searchDocumentName = function(id, value) {
			const codeType = id.toUpperCase();
				
			autoSearchName(codeType, value)
				.then(result => {
					addSearchList(id, result, false);
				})
				.catch(error => {
					console.error(error);
				})
		}
		
		// documentName 가져오기 메서드
		const autoSearchName = function(codeType, value) {
			
			const url = getCallUrl("/common/autoSearchName");
			 const params = {
				 codeType: codeType,	
				 value: value
		    };
			 
			 return new Promise((resolve, reject) => {
		        call(url, params, function(dataList) {
		            const result = dataList.map(data => data); 
		            resolve(result); 
		        });
		    });
		}

		<%----------------------------------------------------------
		*                      문서명 입력시 데이터 리스트 보여주기
		----------------------------------------------------------%>
		const addSearchList = function(id, data, isRemove) {
			$("#" + id + "UL li").remove();
			if(isRemove) {
				$("#" + this.id + "Search").hide();
			}else {
				if(data.length > 0) {
					$("#" + id + "Search").show();
					for(var i=0; i<data.length; i++) {
						$("#" + id + "UL").append("<li title='" + id + "' class=''>" + data[i].name);
					}
				}else {
					$("#" + id + "Search").hide();
				}
			}
		}

		<%----------------------------------------------------------
		*                      문서명 데이터 마우스 올렸을때
		----------------------------------------------------------%>
		$(document).on("mouseover", 'div > ul > li', function() {
			var partName = $(this).attr("title");
			$(this).addClass("hover");
			$("#" + partName).val($(this).text());
		})

		<%----------------------------------------------------------
		*                      문서명 데이터 마우스 뺄때
		----------------------------------------------------------%>
		$(document).on("mouseout", 'div > ul > li', function() {
			$(this).removeClass("hover");
		})
		
	</script>
	</form>
</body>
</html>