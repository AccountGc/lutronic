<%@page import="com.e3ps.part.dto.PartData"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.part.QuantityUnit"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
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
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> matList = (ArrayList<NumberCode>) request.getAttribute("matList");
ArrayList<NumberCode> productmethodList = (ArrayList<NumberCode>) request.getAttribute("productmethodList");
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
ArrayList<NumberCode> finishList = (ArrayList<NumberCode>) request.getAttribute("finishList");
QuantityUnit[] unitList = (QuantityUnit[]) request.getAttribute("unitList");
%>
<input type="hidden" name="oid" id="oid" value="<%= data.getOid() %>" />
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
						<%= data.getNumber() != null ? data.getNumber() : "" %>
					</td>
				</tr>
				<tr>
					<th>품목분류 <span style="color:red;">*</span></th>
					<td class="indent5" colspan="3">
						<input type="hidden" name="location"  id="location"  value="<%= data.getLocation() != null ? data.getLocation() : "" %>">
						<span id="locationText">
							<%= data.getLocation() != null ? data.getLocation() : "" %>
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
			<select name="model" id="model" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode model : modelList) {
				%>
				<option value="<%=model.getCode() %>" <% if(data.getModel().equals(model.getCode())){ %> selected <% } %>><%=model.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>제작방법 <span style="color:red;">*</span></th>
		<td class="indent5">
			<select name="productmethod" id="productmethod" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode productmethod : productmethodList) {
				%>
				<option value="<%=productmethod.getCode() %>" <% if(data.getProductmethod().equals(productmethod.getCode())){ %> selected <% } %>><%=productmethod.getName()%></option>
				<%
				}
				%>
			</select>
	</tr>
	<tr>
		<th>부서 <span style="color:red;">*</span></th>
		<td class="indent5">
			<select name="deptcode" id="deptcode" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode deptcode : deptcodeList) {
				%>
				<option value="<%=deptcode.getCode() %>" <% if(data.getDeptcode().equals(deptcode.getCode())){ %> selected <% } %>><%=deptcode.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>단위 <span style="color:red;">*</span></th>
		<td class="indent5">
			<select name="unit" id="unit" class="width-200">
				<option value="">선택</option>
				<%
				for (QuantityUnit unit : unitList) {
				%>
				<option value="<%=unit.toString() %>" <% if(data.getUnit().equals(unit.toString())){ %> selected <% } %>><%=unit.getDisplay() %></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>무게(g)</th>
		<td class="indent5">
			<input type="text" name="weight" id="weight" class="width-200" value="<% if (data.getWeight() != null) { %><%= data.getWeight() %><% } %>">
		</td>
		<th>MANUFACTURER</th>
		<td class="indent5">
			<select name="manufacture" id="manufacture" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode manufacture : manufactureList) {
				%>
				<option value="<%=manufacture.getCode() %>" <% if(data.getManufacture() != null && data.getManufacture().equals(manufacture.getCode())) { %> selected <% } %>><%=manufacture.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>재질</th>
		<td class="indent5">
			<select name="mat" id="mat" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode mat : matList) {
				%>
				<option value="<%=mat.getCode() %>" <% if(data.getMat() != null && data.getMat().equals(mat.getCode())){ %> selected <% } %>><%=mat.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>후처리</th>
		<td class="indent5">
			<select name="finish" id="finish" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode finish : finishList) {
				%>
				<option value="<%=finish.getCode() %>" <% if(data.getFinish() != null && data.getFinish().equals(finish.getCode())) { %> selected <% } %>><%=finish.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>OEM Info.</th>
		<td class="indent5">
			<input type="text" name="remarks" id="remarks" class="width-200" value="<% if (data.getRemark() != null) { %><%= data.getRemark() %><% } %>">
		</td>
		<th>사양</th>
		<td class="indent5">
			<input type="text" name="specification" id="specification" class="width-200" value="<% if (data.getSpecification() != null) { %><%= data.getSpecification() %><% } %>">
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
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th>주 도면</th>
		<td class="indent5" >
			<jsp:include page="/extcore/jsp/common/attach-primary-drawing.jsp">
				<jsp:param value="<%= data.getOid() %>" name="oid" />
				<jsp:param value="modify" name="mode" />
			</jsp:include>
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
				<jsp:param value="<%= data.getOid() %>" name="oid"/>
				<jsp:param value="관련 RoHs" name="title" />
				<jsp:param value="part" name="module"/>
				<jsp:param value="update" name="mode"/>
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
				<jsp:param value="<%= data.getOid() %>" name="oid" />
				<jsp:param value="modify" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<br>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="수정" title="수정" id="updateBtn" class="blue" />
		</td>
	</tr>
</table>
<script>
document.addEventListener("DOMContentLoaded", function() {
	createAUIGrid6(columnsRohs);
	AUIGrid.resize(rohsGridID);
	document.getElementById("partName1").focus();
	selectbox("state");
	selectbox("model");
	selectbox("productmethod");
	selectbox("deptcode");
	selectbox("unit");
	selectbox("mat");
	selectbox("finish");
	selectbox("manufacture");
});

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
*                      품목명 데이터 마우스 뺄때
----------------------------------------------------------%>
$(document).on("mouseout", 'div > ul > li', function() {
	$(this).removeClass("hover") ;
})

$('#partName4').focusout(function() {
	$('#partName4').val(this.value.toUpperCase());
})

$(function() {
	<%----------------------------------------------------------
	*                      Weight 입력 중
	----------------------------------------------------------%>
	$('#weight').keypress(function(event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		return (charCode == 46) || common_isNumber(event, this);
	})
	<%----------------------------------------------------------
	*                      Weight 입력시
	----------------------------------------------------------%>
	$("#weight").keyup(function() {
		var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
		$("#weight").val(result);
	})
})
	
<%----------------------------------------------------------
*                      수정버튼
----------------------------------------------------------%>
$("#updateBtn").click(function() {
	const oid = document.getElementById("oid").value;
	const location = document.getElementById("location").value;
	const partName1 = document.getElementById("partName1").value;
	const partName2 = document.getElementById("partName2").value;
	const partName3 = document.getElementById("partName3").value;
	const partName4 = document.getElementById("partName4").value;
	const model = document.getElementById("model").value;
	const productmethod = document.getElementById("productmethod").value;
	const deptcode = document.getElementById("deptcode").value;
	const unit = document.getElementById("unit").value;
	const weight = document.getElementById("weight").value;
	const manufacture = document.getElementById("manufacture").value;
	const mat = document.getElementById("mat").value;
	const finish = document.getElementById("finish").value;
	const remarks = document.getElementById("remarks").value;
	const specification = document.getElementById("specification").value;
	const primary = document.querySelector("input[name=primary]") == null ? "" : document.querySelector("input[name=primary]").value;
	const secondary = toArray("secondarys");
	const delocIds = toArray("delocIds");
	
//     let docOids = [];
//     const appendDoc = AUIGrid.getGridData(myGridID90);
//     if(appendDoc.length > 0){
//         for(let i = 0; i < appendDoc.length; i++){
//             docOids.push(appendDoc[i].oid)
//         }
//     }
    
    let rohsOids = [];
    const appendRohs = AUIGrid.getGridData(rohsGridID);
    if(appendRohs.length > 0){
        for(let i = 0; i < appendRohs.length; i++){
        	rohsOids.push(appendRohs[i].oid)
        }
    }

    if(isEmpty(location)){
		alert("품목구분을 입력하세요.");
		return;					
	}
	if(isEmpty(partName1) || isEmpty(partName2) || isEmpty(partName3) || isEmpty(partName4)){
		alert("품목명을 입력하세요.");
		return;					
	}
	if(isEmpty($("#model").val())){
		alert("프로젝트 코드를 입력하세요.");
		return;					
	}
	if(isEmpty($("#productmethod").val())){
		alert("제작방법을 입력하세요.");
		return;					
	}
	if(isEmpty($("#deptcode").val())){
		alert("부서를 입력하세요.");
		return;					
	}
	if(isEmpty($("#unit").val())){
		alert("단위를 입력하세요.");
		return;					
	}
	
			
	if (confirm("수정하시겠습니까?")){
		
		let params = new Object();
		const url = getCallUrl("/part/update");
		params.oid = oid;
		params.location = location;
		params.partName1 = partName1;
		params.partName2 =  partName2;
		params.partName3 =  partName3;
		params.partName4 =  partName4;
		params.model = model;
		params.productmethod = productmethod;
		params.deptcode = deptcode;
		params.unit =unit;
		params.weight = weight;
		params.manufacture = manufacture;
		params.mat = mat;
		params.finish = finish;
		params.remarks = remarks;
		params.specification = specification;
		params.primary = primary;
		params.rohsOids = rohsOids;
		params.secondary = secondary;
		
		call(url, params, function(data) {
			if (data.result) {
				alert("수정 성공하였습니다.");
				document.location.href = getCallUrl("/part/view?oid=" + data.oid);
			} else {
				alert("수정 실패하였습니다. \n" + data.msg);
			}
		});
	}
})
</script>