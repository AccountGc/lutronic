<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%-- <%@include file="/extcore/jsp/common/aui/auigrid.jsp"%> --%>
<%-- <input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT %>"> --%>
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
		<input type="hidden" name="cmd" id="cmd" value="save" />
		<input type=hidden name="eoType" value="CHANGE">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> ECO 등록
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="*">
				<col width="*">
				<col width="*">
				<col width="*">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">ECO 제목</th>
				<td class="indent5" colspan="7"><input type="text" name="eoName" id="eoName" class="width-200"></td>
			</tr>
			<tr>
				<th class="lb">관련 CR/ECPR</th>
				<td colspan="7">
					<jsp:include page="/extcore/jsp/change/include_selectEcr.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td class="indent5" colspan="7"><textarea name="eoCommentA" id="eoCommentA" rows="6"></textarea></td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td class="indent5" colspan="7"><textarea name="eoCommentB" id="eoCommentB" rows="6"></textarea></td>
			</tr>
			<tr>
				<th class="req lb">설계변경 부품</th>
				<td colspan="7">
					<!-- 	관련 품목 -->
					<jsp:include page="/extcore/jsp/change/include_selectPart.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">인허가변경</th>
				<td colspan="3">&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing"  id="licensing" value="NONE" checked="checked">
						<div class="state p-success">
							<label> <b>N/A</b>
							</label>
						</div>
					</div> &nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" value="0">
						<div class="state p-success">
							<label> <b>불필요</b>
							</label>
						</div>
					</div> &nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" value="1">
						<div class="state p-success">
							<label> <b>필요</b>
							</label>
						</div>
					</div>
				</td>
				<th>위험통제</th>
				<td colspan="3"> &nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="">
						<div class="state p-success">
							<label> <b>N/A</b>
							</label>
						</div>
					</div>
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="0">
						<div class="state p-success">
							<label> <b>불필요</b>
							</label>
						</div>
					</div> &nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="1">
						<div class="state p-success">
							<label> <b>필요</b>
							</label>
						</div>
					</div> 
				</td>
			</tr>
			<tr>
				<th class="lb">특기사항</th>
				<td class="indent5" colspan="7"><textarea name="eoCommentC" id="eoCommentC" rows="6"></textarea></td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td class="indent5" colspan="7"><textarea name="eoCommentD" id="eoCommentD" rows="6"></textarea></td>
			</tr>
			<tr>
				<th class="lb">
					설계변경 부품 내역파일
					<input type="button"  value="양식다운"  title="양식다운"  id="download">
				</th>
				<td class="indent5" colspan="7">
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
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 활동설정
					</div>
				</td>
				<td class="right">
					활동 불러오기 :
					<select name="state" id="state" class="width-200">
							<option value="">선택</option>
							<option value="INWORK">작업 중</option>
							<option value="UNDERAPPROVAL">승인 중</option>
							<option value="APPROVED">승인됨</option>
							<option value="RETURN">반려됨</option>
					</select>
					<input type="button"  value="활동추가"  title="활동추가"  class="btnCRUD"  id="createActivity">
					<input type="button" value="활동삭제" title="활동삭제"  class="btnCRUD"  id="deleteActivity">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<tr>
				<th>Step</th>
				<th><input type="checkbox"/></th>
				<th>활동명</th>
				<th>활동구분</th>
				<th>부서</th>
				<th>담당자</th>
				<th>완료 요청일</th>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button"  value="등록"  title="등록"  class="blue"  id="createBtn">
					<input type="button" value="초기화" title="초기화"  class="btnCRUD"  id="resetBtn">
					<input type="button" value="이전" title="이전" onclick="javascript:history.back();">
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
	
			$("#createBtn").click(function() {
				if(isEmpty($("#eoName").val())) {
					alert("제목을 입력하세요.");
					return;
				}
				
				if (!confirm("등록 하시겠습니까?")) {
					return;
				}
				
				var params = _data($("#form"));
				var url = getCallUrl("/changeECO/create");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.href = getCallUrl("/changeECO/list");
					}else{
						alert(data.msg);
					}
				});
			})
			
			$("#listBtn").click(function() {
				location.href = getCallUrl("/changeECO/list");
			});
	
			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				createAUIGrid1(columnsEcr);
				createAUIGrid2(columnsPart);
				AUIGrid.resize(partGridID);
				AUIGrid.resize(ecrGridID);
				document.getElementById("name").focus();
			});
	
			window.addEventListener("resize", function() {
				AUIGrid.resize(partGridID);
				AUIGrid.resize(ecrGridID);
			});
		</script>
	</form>	
</body>
</html>