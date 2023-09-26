<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.e3ps.part.dto.PartData"%>
<%
String oid = (String) request.getAttribute("oid");
PartData data = (PartData) request.getAttribute("data");
String partName1 = "";
String partName2 = "";
String partName3 = "";
String partName4 = "";
if(data != null){
	String[] partName = data.getName().split("_");
	partName1 = partName[0];
	partName2 = partName[1];
	partName3 = partName[2];
	partName4 = partName[3];
}
%>
<form name="updateDevelopmentForm" id="updateDevelopmentForm" method="post" >
<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
<table width="100%" border="0" cellpadding="0" cellspacing="0" > 
	<tr height="5">
		<td>
			<table class="button-table">
				<tr>
					<td class="left">
						<div class="header">
							<img src="/Windchill/extcore/images/header.png">&nbsp;품목 수정
						</div>
					</td>
					<td class="right">
						<input type="button" value="이전페이지" name="backBtn" id="backBtn" onclick="javascript:history.back();">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr align="center">
		<td valign="top" colspan="2">
			<table class="search-table">
				<colgroup>
					<col width="180">
					<col width="180">
					<col width="*">
				</colgroup>
				<tr>
					<th>품목번호</th>
					<td class="indent5" colspan="3">
						<%= data != null ? data.getNumber() : "" %>
					</td>
				</tr>
				<tr>
					<th>품목분류 <span style="color:red;">*</span></th>
					<td class="indent5" colspan="3">
						<span id="locationName">
							<%= data != null ? data.getLocation() : "" %>
						</span>
					</td>
				</tr>
				<tr>
					<th rowspan="4">품목명 <span style="color:red;">*</span></th>
					<th>대제목</th>
					<td class="indent5">
						<input id="partName1" name="partName1" class='partName width-300' type="text" value="<%= partName1 %>" >
						<div id="partName1Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
							<ul id="partName1UL" style="list-style-type: none; padding-left: 0px;">
							</ul>
						</div>
					</td>
				</tr>
				<tr>
					<th>중제목</th>
					<td class="indent5">
						<input id="partName2" name="partName2" class='partName width-300' type="text" value="<%= partName2 %>" >
						<div id="partName2Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
							<ul id="partName2UL" style="list-style-type: none; padding-left: 0px;">
							</ul>
						</div>
					</td>
				</tr>
				<tr>
					<th>소제목</th>
					<td class="indent5">
						<input id="partName3" name="partName3" class='partName width-300' type="text" value="<%= partName3 %>" >
						<div id="partName3Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
							<ul id="partName3UL" style="list-style-type: none; padding-left: 0px;">
							</ul>
						</div>
					</td>
				</tr>
				<tr>
					<th>사용자 Key in</th>
					<td class="indent5">
						<input id="partName4" name="partName4" class='partName width-300' type="text" value="<%= partName4 %>" >
					</td>
				</tr>
				<tr>
					<td class="tdblueM" id="auto" colspan="3" >
						<div>
							<span style="font-weight: bold; vertical-align: middle;" id="displayName">
								<c:out value="<%= data != null ? data.getName() : "" %>" />
							</span>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 품목 속성
					</div>
				</td>
			</tr>
		</table>
		
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>프로젝트코드 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="model" id="model" class="width-500" value="<%= data != null ? data.getModel() : "" %>">
				</td>
				<th>제작방법 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="productmethod" id="productmethod" class="width-500" value="<%= data != null ? data.getProductmethod() : "" %>">
				</td>
			</tr>
			<tr>
				<th>부서 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="deptcode" id="deptcode" class="width-500">
				</td>
				<th>단위 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="unit" id="unit" class="width-500">
				</td>
			</tr>
			<tr>
				<th>무게(g)</th>
				<td class="indent5">
					<input type="text" name="weight" id="weight" class="width-500">
				</td>
				<th>MANUFACTURER</th>
				<td class="indent5">
					<input type="text" name="manufacture" id="manufacture" class="width-500">
				</td>
			</tr>
			<tr>
				<th>재질</th>
				<td class="indent5">
					<input type="text" name="mat" id="mat" class="width-500">
				</td>
				<th>후처리</th>
				<td class="indent5">
					<input type="text" name="finish" id="finish" class="width-500">
				</td>
			</tr>
			<tr>
				<th>OEM Info.</th>
				<td class="indent5">
					<input type="text" name="remarks" id="remarks" class="width-500">
				</td>
				<th>사양</th>
				<td class="indent5">
					<input type="text" name="specification" id="specification" class="width-500">
				</td>
			</tr>
		</table>
		<br>
		<!-- 	주도면 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 주 도면&nbsp;&nbsp;
						<span class="red">(메카 : CAD파일), (광학/제어/파워/인증 : PDF파일)</span>
					</div>
				</td>
			</tr>
		</table>
		
		<table class="search-table">
			<colgroup>
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th>주 도면</th>
				<td class="indent5" colspan="3">
				</td>
			</tr>
		</table>
		<br>
		
		<!-- 관련 문서 -->
<%-- 		<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp"> --%>
<%-- 			<jsp:param value="part" name="moduleType"/> --%>
<%-- 			<jsp:param value="<%= data.getOid() %>" name="oid"/> --%>
<%-- 			<jsp:param value="관련 문서" name="title"/> --%>
<%-- 			<jsp:param value="docOid" name="paramName"/> --%>
<%-- 		</jsp:include> --%>
		<br>
		
		
		<!-- 관련 rohs -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 관련 RoHS
					</div>
				</td>
			</tr>
		</table>
		
		<table class="search-table">
			<colgroup>
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th>관련 RoHS</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/rohs/include_selectRohs.jsp">
						<jsp:param value="관련 RoHS" name="title"/>
						<jsp:param name="paramName" value="rohsOid"/>
						<jsp:param value="<%= data.getOid() %>" name="oid"/>
						<jsp:param value="part" name="module"/>
					</jsp:include>
				</td>
			</tr>
		</table>
		
		<!-- 첨부파일 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 첨부파일
					</div>
				</td>
			</tr>
		</table>
		
		<table class="search-table">
			<colgroup>
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<br>
		
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" id="createBtn" class="blue" onclick="create('false');" />
					<input type="button" value="초기화" title="초기화" id="resetBtn" />
					<input type="button" value="목록" title="목록" id="listBtn" />
				</td>
			</tr>
		</table>
		
	</form>
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
	$("#" + this.id + "Search").hide();
	
	var name = '';
	
	if(!$.trim($('#partName1').val()) == '') {
		name += $('#partName1').val();
	}
	
	if(!$.trim($('#partName2').val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partName2').val();
	}
	
	if(!$.trim($('#partName3').val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partName3').val();
	}
	
	if(!$.trim($('#partNameCustom').val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partNameCustom').val();
	}
	
	$('#displayName').html(name);
	
	
})

// 품목명 입력 시 
$(".partName").keyup(function (event) {
	var charCode = (event.which) ? event.which : event.keyCode;
	if((charCode == 38 || charCode == 40) ) {
		if(!$( "#"+this.id+"Search" ).is( ":hidden" )){
			var isAdd = false;
			if(charCode == 38){
				isAdd = true;
			}
			movePartNameFocus(this.id, isAdd);
		}
	} else if(charCode == 13 || charCode == 27){
		$("#" + this.id + "Search").hide();
	} else if(charCode == 8) {
		if($.trim($(this).val()) == '') {
			$("#" + this.id + "Search").hide();
		} else {
			autoSearchPartName(this.id, this.value);
		}
	} else {
		autoSearchPartName(this.id, this.value);
	}
})

// partName 입력 시 partName 리스트 출력 메서드
const autoSearchPartName = function(id, value) {
	if($.trim(value) == "") {
		addSearchList(id, '', true);
	} else {
		var codeType = id.toUpperCase();
		
		autoSearchName(codeType, value)
		.then(result => {
			addSearchList(id, result, false);
		})
		.catch(error => {
			console.error(error);
		})
	}
}

// partName 가져오기 메서드
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
*                      품목명 입력시 데이터 리스트 보여주기
----------------------------------------------------------%>
const addSearchList = function(id, data, isRemove) {
	$("#" + id + "UL li").remove();
	if(isRemove) {
		$("#" + this.id + "Search").hide();
	} else{
		if(data.length > 0) {
			$("#" + id + "Search").show();
			for(var i=0; i<data.length; i++) {
				$("#" + id + "UL").append("<li title='" + id + "'>" + data[i].name);
			}
		} else {
			$("#" + id + "Search").hide();
		}
	}
}

<%----------------------------------------------------------
*                      품목명 데이터 마우스 올렸을때
----------------------------------------------------------%>
$(document).on("mouseover", 'div > ul > li', function() {
	var partName = $(this).attr("title");
	
	$("#" + partName + "UL li").each(function() {
		var cls = $(this).attr('class');
		if(cls == 'hover') {
			$(this).removeClass('hover');
		}
	})
	
	$(this).addClass("hover") ;
	$("#" + partName).val($(this).text());
})
	
	<%----------------------------------------------------------
	*                      수정버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function() {
		const partName1 = document.getElementById("partName1").value;
		const partType1 = document.getElementById("partType1").value;
		const partName2 = document.getElementById("partName2").value;
		const partType2 = document.getElementById("partType2").value;
		const partName3 = document.getElementById("partName3").value;
		const partType3 = document.getElementById("partType3").value;
		const partName4 = document.getElementById("partName4").value;
		const seq = document.getElementById("seq").value;
		const etc = document.getElementById("etc").value;
		const model = document.getElementById("model").value;
		const productmethod = document.getElementById("productmethod").value;
		const deptcode = document.getElementById("deptcode").value;
		const weight = document.getElementById("weight").value;
		const manufacture = document.getElementById("manufacture").value;
		const mat = document.getElementById("mat").value;
		const finish = document.getElementById("finish").value;
		const remarks = document.getElementById("remarks").value;
		const specification = document.getElementById("specification").value;
		const unit = document.getElementById("unit").value;
		const primarys = toArray("primarys");
		const wtPartType = document.getElementById("wtPartType").value;
		const source = document.getElementById("source").value;
		const lifecycle = document.getElementById("lifecycle").value;
		const view = document.getElementById("view").value;
		const fid = document.getElementById("fid").value;
		const location = document.getElementById("location").value;
		
		const oid = document.getElementById("oid").value;
		const location = document.getElementById("location").value;
		const docName = document.getElementById("docName");
		const lifecycle = document.getElementById("lifecycle").value;
		const description = document.getElementById("description").value;
		const iterationNote = document.getElementById("iterationNote").value;
		const primarys = toArray("primarys");

// 		if($.trim($("#partName1").val()) == ""
// 			   && $.trim($("#partName2").val()) == ""
// 			   && $.trim($("#partName3").val()) == ""
// 			   && $.trim($("#partNameCustom").val()) == "" ) {
// 				alert("품목명을(를) 입력하세요.");
// 				$("#partName1").focus();
// 				return;
// 			}else if($("#displayName").text().length > 40) {
// 				alert("품목명은(는) 40자 이내로 입력하세요.");
// 				return;
// 			}
// 			if($("#model").val() == "") {
// 				alert("프로젝트코드을(를) 선택하세요.");
// 				$("#model").focus();
// 				return;
// 			}
			
// 			if($("#productmethod").val() == "") {
// 				alert("제작방법을(를) 선택하세요.");
// 				$("#productmethod").focus();
// 				return;
// 			}
			
// 			if($("#deptcode").val() == "") {
// 				alert("부서을(를) 선택하세요.");
// 				$("#deptcode").focus();
// 				return;
// 			}
			
// 			if($("#unit").val() == "") {
// 				alert("단위을(를) 선택하세요.");
// 				$("#unit").focus();
// 				return;
// 			}
			
		if (confirm("수정하시겠습니까?")){
			
			const params = new Object();
			const url = getCallUrl("/part/create");
			params.partName1 = "MODULE";
			params.partType1 = partType1;
			params.partName2 = "BOARD";
			params.partType2 = partType2;
			params.partName3 = "LD DRIVER";
			params.partType3 = partType3;
			params.partName4 = "";
			params.seq = seq;
			params.etc = etc;
			params.model = model;
			params.productmethod = productmethod;
			params.deptcode = deptcode;
			params.weight = weight;
			params.manufacture = manufacture;
			params.mat = mat;
			params.finish = finish;
			params.remarks = remarks;
			params.specification = specification;
			params.unit =unit;
			params.primarys = primarys;
			params.wtPartType = wtPartType;
			params.source = source;
			params.lifecycle = lifecycle;
			params.view = view;
			params.fid = fid;
			params.location = location;
			
			call(url, params, function(data) {
				if (data.result) {
					alert("수정 성공하였습니다.");
					location.href = getCallUrl("/doc/view?oid=" + data.oid);
				} else {
					alert("수정 실패하였습니다. \n" + data.msg);
				}
			});
		}
	})
})
</script>