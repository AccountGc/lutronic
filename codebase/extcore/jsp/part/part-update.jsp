<%@page import="com.e3ps.part.dto.PartData"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.part.QuantityUnit"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
PartData data = (PartData) request.getAttribute("data");
ArrayList<NumberCode> modelList = (ArrayList<NumberCode>) request.getAttribute("modelList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
ArrayList<NumberCode> matList = (ArrayList<NumberCode>) request.getAttribute("matList");
ArrayList<NumberCode> productmethodList = (ArrayList<NumberCode>) request.getAttribute("productmethodList");
ArrayList<NumberCode> finishList = (ArrayList<NumberCode>) request.getAttribute("finishList");
QuantityUnit[] unitList = (QuantityUnit[]) request.getAttribute("unitList");
%>
<input type="hidden" name="oid" id="oid" value="<%= data.getOid() %>" />
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				품목 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정완료" title="수정완료" class="blue" onclick="update('false');">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="180">
		<col width="180">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">품목번호</th>
		<td class="indent5" colspan="3">
			<%= data.getNumber() != null ? data.getNumber() : "" %>
		</td>
	</tr>
	<tr>
		<th class="req lb">품목분류</th>
		<td class="indent5" colspan="3">
			<input type="hidden" name="location" id="location" value="<%=data.getLocation()%>">
			<span id="locationText"><%=data.getLocation()%></span>
			<input type="button" value="폴더선택" title="폴더선택" onclick="folder();" class="blue">
		</td>
	</tr>
	<tr>
		<th rowspan="4" class="lb">품목명</th>
		<th class="lb">대제목</th>
		<td class="indent5">
			<input id="partName1" name="partName1" class='partName width-300' type="text" value="<%= data.getPartName1() %>" >
			<div id="partName1Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
				<ul id="partName1UL" style="list-style-type: none; padding-left: 0px;">
				</ul>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">중제목</th>
		<td class="indent5">
			<input id="partName2" name="partName2" class='partName width-300' type="text" value="<%= data.getPartName2() %>" >
			<div id="partName2Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
				<ul id="partName2UL" style="list-style-type: none; padding-left: 0px;">
				</ul>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">소제목</th>
		<td class="indent5">
			<input id="partName3" name="partName3" class='partName width-300' type="text" value="<%= data.getPartName3() %>" >
			<div id="partName3Search" style="display: none; border: 1px solid black ; position: absolute; background-color: white; width: 26%">
				<ul id="partName3UL" style="list-style-type: none; padding-left: 0px;">
				</ul>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb req">사용자 Key in</th>
		<td class="indent5">
			<input id="partName4" name="partName4" class='partName width-300' type="text" value="<%= data.getPartName4() %>" >
		</td>
	</tr>
	<tr>
		<td class="tdblueM" id="auto" colspan="3" >
			<div>
				<span style="font-weight: bold; vertical-align: middle;" id="displayName">
					<%= data != null ? data.getName() : "" %>
				</span>
			</div>
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
		<th class="req lb">프로젝트코드 <span style="color:red;">*</span></th>
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
		<th class="req">제작방법 <span style="color:red;">*</span></th>
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
		<th class="req lb">부서 <span style="color:red;">*</span></th>
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
		<th class="req">단위 <span style="color:red;">*</span></th>
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
		<th class="lb">무게(g)</th>
		<td class="indent5">
			<input type="text" name="weight" id="weight" class="width-200" value="<% if (data.getWeight() != null) { %><%= data.getWeight() %><% } %>">
		</td>
		<th>MANUFACTURER</th>
		<td class="indent5">
			<input type="text" name="manufacture" id="manufacture" class="width-200">
		</td>
	</tr>
	<tr>
		<th class="lb">재질</th>
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
		<th class="lb">OEM Info.</th>
		<td class="indent5">
			<input type="text" name="remarks" id="remarks" class="width-200" value="<% if (data.getRemark() != null) { %><%= data.getRemark() %><% } %>">
		</td>
		<th>사양</th>
		<td class="indent5">
			<input type="text" name="specification" id="specification" class="width-200" value="<% if (data.getSpecification() != null) { %><%= data.getSpecification() %><% } %>">
		</td>
	</tr>
<!-- 	<tr> -->
<!-- 		<th class="lb">결재</th> -->
<!-- 		<td colspan="3"> -->
<%-- 			<jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp"> --%>
<%-- 				<jsp:param value="" name="oid" /> --%>
<%-- 				<jsp:param value="create" name="mode" /> --%>
<%-- 			</jsp:include> --%>
<!-- 		</td> -->
<!-- 	</tr> -->
</table>

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

<!-- 관련 문서 -->
<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
	<jsp:param value="<%= data.getOid() %>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="250" name="height" />
</jsp:include>

<!-- 관련 RoHS -->
<jsp:include page="/extcore/jsp/rohs/include/rohs-include.jsp">
	<jsp:param value="<%= data.getOid() %>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="250" name="height" />
</jsp:include>

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

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="수정완료" title="수정완료" class="blue" onclick="update('false');">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<script>
function folder() {
	const location = decodeURIComponent("/Default/PART_Drawing");
	const url = getCallUrl("/folder/popup?location=" + location);
	_popup(url, 500, 600, "n");
}

document.addEventListener("DOMContentLoaded", function() {
	createAUIGrid90(columns90);
	createAUIGrid106(columns106);
// 	createAUIGrid8(columns8);
	AUIGrid.resize(myGridID90);
	AUIGrid.resize(myGridID106);
// 	AUIGrid.resize(myGridID8);
	document.getElementById("partName1").focus();
	selectbox("state");
	selectbox("model");
	selectbox("productmethod");
	selectbox("deptcode");
	selectbox("unit");
	selectbox("mat");
	selectbox("finish");
	
	axdom("#manufacture").bindSelector({
		reserveKeys: {
			options: "list",
			optionValue: "oid",
			optionText: "name"
		},
		optionPrintLength: "all",
		onsearch: function(id, obj, callBack) {
			const value = document.getElementById(id).value;
			const params = new Object();
			const url = getCallUrl("/code/finder");
			params.value = value;
			params.codeType = "MANUFACTURE";
			params.obj = obj;
			call(url, params, function(data) {
				callBack({
					options: data.list
				})
			})
		},
		onchange: function() {
			const id = this.targetID;
			const value = this.selectedOption.oid
			document.getElementById(id + "Oid").value = value;
		},
	})	
});

window.addEventListener("resize", function() {
	AUIGrid.resize(docGridID);
	AUIGrid.resize(myGridID106);
// 	AUIGrid.resize(myGridID8);
});

//품목명 입력 시 
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

// 품목명에서 ↑,↓ 입력 시 발생 메서드
const movePartNameFocus = function(id,isAdd) {
	var removeCount = 0;
	var addCount = 0;
	var l = $("#" + id + "UL li").length;
	for(var i=0; i<l; i++){
		var cls = $("#" + id + "UL li").eq(i).attr('class');
		if(cls == 'hover') {
			$("#" + id + "UL li").eq(i).removeClass("hover");
			removeCount = i;
			if(isAdd){
				addCount = (i-1);
			}else if(!isAdd) {
				addCount = (i+1);
			}
			break;
		}
	}
	if(addCount == l) {
		addCount = 0;
	}
	$("#" + id + "UL li").eq(addCount).addClass("hover");
	$("#" + id).val($("#" + id + "UL li").eq(addCount).text());
}

// 품목명 입력 시 품목명 리스트 출력 메서드
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

// 품목명 가져오기 메서드
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

// 품목명 입력시 데이터 리스트 보여주기
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

// 품목명 데이터 마우스 올렸을때
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

//품목명 데이터 마우스 뺄때
$(document).on("mouseout", 'div > ul > li', function() {
	$(this).removeClass("hover") ;
})

$('#partName4').focusout(function() {
	$('#partName4').val(this.value.toUpperCase());
})

$(".partName").focusout(function () {
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
	
	if(!$.trim($('#partName4').val()) == '') {
		if(!$.trim(name) == '') {
			name += '_';
		}
		name += $('#partName4').val();
	}
	
	$('#displayName').html(name);
})

// 무게 입력 메서드
$(function() {
	// 무게 입력 중
	$('#weight').keypress(function(event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		return (charCode == 46) || common_isNumber(event, this);
	})
	// 무게 입력 중
	$("#weight").keyup(function() {
		var result = this.value.replace(/[\ㄱ-ㅎㅏ-ㅣ|가-힣]/gi,'');
		$("#weight").val(result);
	})
})

// 수정 메서드
function update(temp) {
	const partName1 = document.getElementById("partName1").value;
	const partName2 = document.getElementById("partName2").value;
	const partName3 = document.getElementById("partName3").value;
	const partName4 = document.getElementById("partName4").value;
	const model = document.getElementById("model").value;
	const productmethod = document.getElementById("productmethod").value;
	const deptcode = document.getElementById("deptcode").value;
	const unit =  document.getElementById("unit").value;
	const temprary = JSON.parse(temp);
	
	const primary = document.querySelector("input[name=primary]") == null ? "" : document.querySelector("input[name=primary]").value;
	const secondary = toArray("secondarys");
	
	// 관련문서
	const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
	// 결재선
// 	const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
	// RoHs
	const rows106 = AUIGrid.getGridDataWithState(myGridID106, "gridState");

	if (temprary) {
		if (!confirm("임시저장하시겠습니까??")) {
			return false;
		}
		
// 		if (addRows8.length > 0) {
// 			alert("결재선 지정을 해지해주세요.")
// 			return false;
// 		}
		
	} else {
		if(isEmpty(location)){
			alert("품목구분을 입력하세요.");
			return;					
		}
		if(isEmpty(partName4)){
			alert("Key-in을 입력하세요.");
			return;					
		}
		if(isEmpty(model)){
			alert("프로젝트 코드를 입력하세요.");
			return;					
		}
		if(isEmpty(productmethod)){
			alert("제작방법을 입력하세요.");
			return;					
		}
		if(isEmpty(deptcode)){
			alert("부서를 입력하세요.");
			return;					
		}
		if(isEmpty(unit)){
			alert("단위를 입력하세요.");
			return;					
		}
		
		if (!confirm("수정하시겠습니까?")) {
			return false;
		}
	}
	
	const params ={
			oid : toId("oid"),
			partName1 : partName1,
			partName2 : partName2,
			partName3 : partName3,
			partName4 : partName4,
			model : model,
			productmethod : productmethod,
			deptcode : deptcode,
			unit : unit,
			temprary : temprary,
			primary : primary,
			secondary : secondary,
			weight : toId("weight"),
			manufacture : toId("manufacture"),
			mat : toId("mat"),
			finish : toId("finish"),
			remarks : toId("remarks"),
			specification : toId("specification"),
			location : toId("location"),
			// 링크 데이터
			rows90 : rows90,
			rows106 : rows106,
	};
	
// 	toRegister(params, addRows8);
	
	const url = getCallUrl("/part/update");
	openLayer();
	call(url, params, function(data) {
		if (data.result) {
			alert("수정 성공하였습니다.");
			document.location.href = getCallUrl("/part/view?oid=" + data.oid);
			opener.loadGridData();
		} else {
			alert("수정 실패하였습니다. \n" + data.msg);
			closeLayer();
		}
	});
};
</script>