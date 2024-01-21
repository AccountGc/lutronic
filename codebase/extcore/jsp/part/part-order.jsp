<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
    	String oid = (String)request.getAttribute("oid");
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<style type="text/css">
/* 계층 트리 아이콘  Windchill/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/plus.gif*/
.aui-grid-tree-plus-icon {
	display: inline-block;
	width:16px;
	height:16px;
	border:none;
	background: url(/Windchill/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/plus.gif) 50% 50% no-repeat;
	background-size:16px;
	vertical-align:bottom;
	margin: 0 2px 0 0;
}
/*평쳤을때 이미지*/
	display: inline-block;
.aui-grid-tree-minus-icon {
	width:16px;
	height:16px;
	border:none;
	background: url(/Windchill/jsp/js/dhtmlx/imgs/dhxgrid_skyblue/tree/minus.gif) 50% 50% no-repeat;
	background-size:16px;
	vertical-align:bottom;
	margin: 0 2px 0 0;
}
/*데이터 앞 이미지*/
.aui-grid-tree-branch-icon {
	display: inline-block;
	width: 16px;
	height: 16px;
	background: url(/Windchill/wtcore/images/part.gif) 50% 50% no-repeat;
	background-size:16px;
	vertical-align: bottom;
	
}

.aui-grid-tree-branch-open-icon {
	display: inline-block;
	width: 16px;
	height: 16px;
	background: url(/Windchill/wtcore/images/part.gif) 50% 50% no-repeat;
	background-size:16px;
	vertical-align: bottom;
	
}

.aui-grid-tree-leaf-icon {
	display: inline-block;
	width: 16px;
	height:16px;
	background: url(/Windchill/wtcore/images/part.gif) no-repeat;
	background-size:16px;
	vertical-align: bottom;
	margin: 0 2px 0 4px;
}
/* 계층 트리 아이콘 끝*/

.AUI_left {
	text-align: left;
}
.AUI_Header_Requisite {
	background-color: #ff00ff;
}

.deletedColor {
	background-color: #ffd7d7;
}

/* 드랍 리스트 왼쪽 정렬 재정의*/
.aui-grid-drop-list-ul {
    text-align:left;
}

</style>
</head>

<script type="text/javascript">
<%----------------------------------------------------------
*                      AUI Tree 그리드 
----------------------------------------------------------%>
var myBOMGridID;

var defaultGridHeight = 61;
var rowHeight = 26;
var headerHeight = 24;
var footerHeight = 30;
var expanded = false;
var gubunList = [{"code":"-","oid":"-","value":"-"}];
<c:forEach var="i" items="${list}">
var mainvalueList_${i.oidL} = [{"code":"-","oid":"-","value":"-"}];
var middleValueList_${i.oidL} = [{"code":"-","oid":"-","value":"-"}];
</c:forEach>
//var mainvalueList = [{"code":"-","oid":"-","value":"-"}];
//var middleValueList = [{"code":"-","oid":"-","value":"-"}];
var partName1List = ["-"];
var partName2List =["-"];
var partName3List =["-"];
var partName4List =["-"];
//AUIGrid 칼럼 설정
var columnLayout = [ {
	dataField: "rowId",
	headerText: "rowId",
	editable : false,
	width : "1%"
},
{
	dataField : "level",
	headerText : "Level",
	width: "3%",
	filter : {
		showIcon : true
	},
	
},

{
	dataField : "number",
	headerText : "품목번호",
	width: "10%",
	editable : false,
	filter : {
		showIcon : true
	},
}, 
{
	dataField : "oidL",
	headerText : " ",
	width: "0.01%",
	editable : false,
	filter : {
		showIcon : false
	},
}, 
{
	dataField: "name",
	headerText: "품목명",
	style: "AUI_left",
	editable : false,
	width : "15%",
	filter : {
		showIcon : true
	},
	renderer : { // HTML 템플릿 렌더러 사용
		type : "TemplateRenderer"
	},
	labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
		var temp = "<a href=javascript:openView('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
		return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
	}
},
{
	dataField: "partState",
	headerText: "상태",
	width: "5%",
	filter : {
		showIcon : true
	},
}, 
{
	dataField: "rev",
	headerText: "Rev.",
	editable : false,
	width : "5%",
	filter : {
		showIcon : true
	},
},
{
	dataField: "isCheckPartType",
	headerText: "수정 가능",
	editable : true,
	width : "5%",
	filter : {
		showIcon : true
	},
	 renderer : { // HTML 템플릿 렌더러 사용
		type : "CheckBoxEditRenderer",
	 	editable : true, // 체크박스 편집 활성화 여부(기본값 : false) 
        checkValue : "가능", // true, false 인 경우가 기본 
        unCheckValue : "불가능", 
        disabledFunction :  function(rowIndex, columnIndex, value, isChecked, item, dataField ) {
        	if(item.partState == "승인됨") {
				return true;
			}
	        return item.isDisablePartType;
		 }//<input type="checkbox" id="c1" value="name" checked="checked" onclick="checkboxChangeHandler(event)">
	} 
	/* renderer : { // HTML 템플릿 렌더러 사용
		type : "TemplateRenderer"
	},
	labelFunction : function (rowIndex, columnIndex, value, headerText, item ) { // HTML 템플릿 작성
		var temp = "<input type='checkbox' name='partType' value='"+item.number+"' checked='checked'"+item.disabled+" onclick='checkboxChangeHandler(event);'>";
		return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
	} */
},
/* {
	dataField: "partType",
	headerText: " ",
	width: "5%"
}, */
{
	dataField: "realPartNumber",
	headerText: "진도번",
	width: "7%",
	enableRestore : true,
	filter : {
		showIcon : true
	}
	,
	editRenderer : {
	    type : "InputEditRenderer",
	}
},
{ 
	dataField: "gubun",
	headerText: "품목구분",
	headerStyle : "AUI_Header_Requisite",
	enableRestore : true,
	style: "AUI_left",
	width: "10%",
	/* labelFunction : function(  rowIndex, columnIndex, value, headerText, item ) { 
		var reStr = "";
    	for(var i=0,len=gubunList.length; i<len; i++) {
    		if(value == gubunList[i].code){
    			reStr = gubunList[i].value
    			break;
    		}
    	}
    	return reStr;
	},
	editRenderer : {
		type : "DropDownListRenderer",
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
    	list : gubunList,
    	keyField : "code",
    	valueField : "value"
    },
    
    listTemplateFunction : function(rowIndex, columnIndex, text, item, dataField, listItem) {
		var html = '';
		for(var n in listItem) {
			html += '<span>' + listItem[n] + '</span>';
		}
		
		return html;
	} */
	renderer : {
    	type : "DropDownListRenderer",
    //	list : gubunList,
    	keyField : "code",
    	valueField : "value",
    	descendants : [ "main"], // 자손 필드들
		//sdescendantDefaultValues : [{"code":"-","oid":"-","value":"-"}], // 변경 시 자손들에게 기본값 지정
    	listFunction : function(rowIndex, columnIndex, item, dataField) {
    		
   	     ///setNumberCodelList(d'PARTTYPE','','mainvalueList');
   	     if(item.isCheckPartType!="가능"){
   	    	//console.log(rowIndex);
   			item.gubun = "";
   	    	 return [];
   	     }   	    	
   	     return gubunList;
   		}
    },

	
},
{ 
	dataField: "main",
	headerText: "대분류",
	headerStyle : "AUI_Header_Requisite",
	style: "AUI_left",
	enableRestore : true,
	width: "10%",
	renderer : {
    	type : "DropDownListRenderer",
    	//list : mainvalueList,
    	keyField : "code",
    	valueField : "value",
    	descendants : [ "middle"], // 자손 필드들
    	listFunction : function(rowIndex, columnIndex, item, dataField) {
   	     if(item.isCheckPartType!="가능"){
   	    	//console.log(rowIndex);
   			item.main = "";
   	    	 return [];
   	     }
   		  <c:forEach var="i" items="${list}">
			   	if(item.oidL=='${i.oidL}'){
			   		return mainvalueList_${i.oidL};
			   	}
 		  </c:forEach>
   		}
    }
    
},
{ 
	dataField: "middle",
	enableRestore : true,
	headerText: "중분류",
	headerStyle : "AUI_Header_Requisite",
	style: "AUI_left",
	width: "10%",
	
	renderer : {
    	type : "DropDownListRenderer",
    	//list : middleValueList,
    	keyField : "code",
    	valueField : "value",
    	listFunction : function(rowIndex, columnIndex, item, dataField) {
      	     if(item.isCheckPartType!="가능"){
      	    	//console.log(rowIndex);
      			item.middle = "";
      	    	 return [];
      	     }
      	    	
      	   <c:forEach var="i" items="${list}">
		   	if(item.oidL=='${i.oidL}'){
		   		return middleValueList_${i.oidL};
		   	}
	 	 </c:forEach>
      		}
    }
},
{
	dataField: "partNameSeq",
	headerText: "Seq",
	width: "5%",
	enableRestore : true,
	filter : {
		showIcon : true
	},
	editRenderer : {
        type : "InputEditRenderer",
        onlyNumeric : true, // 0~9 까지만 허용
        maxlength : 3
        //allowPoint : true // onlyNumeric 인 경우 소수점(.) 도 허용
  }
},
{
	dataField: "etc",
	headerText: "기타",
	width: "5%",
	enableRestore : true,
	filter : {
		showIcon : true
	},
	editRenderer : {
        type : "InputEditRenderer",
        onlyNumeric : true,
        maxlength : 2
  }
},
{
	dataField : "partNumberSeqs",
	headerText : " ",
	width: "0.01%",
	editable : false,
	filter : {
		showIcon : false
	},
},
{
	dataField: "partName1",
	headerText: "품목명1",
	enableRestore : true,
	width: "8%",
	headerTooltip : {show:true, tooltipHtml : "출력 리스트를 사용자 정의 하여 복잡한 구조로 작성. key-value 모드<br>이름에 대하여 자동완성을 설정. 'A' 을 눌러 보십시오."},
	filter : {
		showIcon : true
	},
	/* editRenderer : {
		type : "RemoteListRenderer",
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
		remoter : function( request, response ) { // remoter 지정 필수
			if(String(request.term).length < 1) {
				alert("1글자 이상 입력하십시오.");
				response(false); // 데이터 요청이 없는 경우 반드시 false 삽입하십시오.
				return;
			}
			var codeType = "partName1".toUpperCase();
			
			//var url	= getURLString("common", 'autoSearchNameRtnName', 'do');
			return $.ajax({
				type:"POST",
				url: url,
				data: {
					codeType : codeType,
					name : request.term
				},
				dataType:"json",
				async: false,
				cache: false,
				error:function(data){
					var msg = type + " ${f:getMessage('코드 목록 오류')}";
					alert(msg);
				},
				success: function(data) {
					// 성공 시 완전한 배열 객체로 삽입하십시오.
					response(data); 
				}
			});
		}
	} */
	editRenderer : {
		type : "ComboBoxRenderer",
		autoCompleteMode : true, // 자동완성 모드 설정
		autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
		listFunction : function(rowIndex, columnIndex, item, dataField) {
			
			if(item.partState == "승인됨"){
      	    	//console.log(rowIndex);
      			item.partName1 = "";
      	    	 return [];
      	     }
      	    	
      	     return partName1List;
		}
	}
	
},
{
	dataField: "partName2",
	headerText: "품목명2",
	enableRestore : true,
	width: "8%",
	headerTooltip : {show:true, tooltipHtml : "출력 리스트를 사용자 정의 하여 복잡한 구조로 작성. key-value 모드<br>이름에 대하여 자동완성을 설정. 'A' 을 눌러 보십시오."},
	filter : {
		showIcon : true
	},
	/* editRenderer : {
		type : "RemoteListRenderer",
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
		remoter : function( request, response ) { // remoter 지정 필수
			if(String(request.term).length < 1) {
				alert("1글자 이상 입력하십시오.");
				response(false); // 데이터 요청이 없는 경우 반드시 false 삽입하십시오.
				return;
			}
			var codeType = "partName2".toUpperCase();
			
			var url	= getURLString("common", "autoSearchNameRtnName", "do");
			return $.ajax({
				type:"POST",
				url: url,
				data: {
					codeType : codeType,
					name : request.term
				},
				dataType:"json",
				async: false,
				cache: false,
				error:function(data){
					var msg = type + " ${f:getMessage('코드 목록 오류')}";
					alert(msg);
				},
				success: function(data) {
					// 성공 시 완전한 배열 객체로 삽입하십시오.
					response(data); 
				}
			});
		}
	} */
	editRenderer : {
		type : "ComboBoxRenderer",
		autoCompleteMode : true, // 자동완성 모드 설정
		autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
		listFunction : function(rowIndex, columnIndex, item, dataField) {
			if(item.partState == "승인됨"){
      	    	//console.log(rowIndex);
      			item.partName2 = "";
      	    	 return [];
      	     }
      	    	
      	     return partName2List;
		}
	}
},
{
	dataField: "partName3",
	headerText: "품목명3",
	enableRestore : true,
	width: "8%",
	headerTooltip : {show:true, tooltipHtml : "출력 리스트를 사용자 정의 하여 복잡한 구조로 작성. key-value 모드<br>이름에 대하여 자동완성을 설정. 'A' 을 눌러 보십시오."},
	filter : {
		showIcon : true
	},
	/* editRenderer : {
		type : "RemoteListRenderer",
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
		remoter : function( request, response ) { // remoter 지정 필수
			if(String(request.term).length < 1) {
				alert("1글자 이상 입력하십시오.");
				response(false); // 데이터 요청이 없는 경우 반드시 false 삽입하십시오.
				return;
			}
			var codeType = "partName3".toUpperCase();
			
			var url	= getURLString("common", "autoSearchNameRtnName", "do");
			return $.ajax({
				type:"POST",
				url: url,
				data: {
					codeType : codeType,
					name : request.term
				},
				dataType:"json",
				async: false,
				cache: false,
				error:function(data){
					var msg = type + " ${f:getMessage('코드 목록 오류')}";
					alert(msg);
				},
				success: function(data) {
					// 성공 시 완전한 배열 객체로 삽입하십시오.
					response(data); 
				}
			});
		}
	} */
	editRenderer : {
		type : "ComboBoxRenderer",
		autoCompleteMode : true, // 자동완성 모드 설정
		autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
		listFunction : function(rowIndex, columnIndex, item, dataField) {
			if(item.partState == "승인됨"){
      	    	//console.log(rowIndex);
      			item.partName3 = "";
      	    	 return [];
      	     }
      	    	
      	     return partName3List;
		}
	}
},
{
	dataField: "partName4",
	headerText: "품목명4",
	enableRestore : true,
	width: "8%",
	headerTooltip : {show:true, tooltipHtml : "출력 리스트를 사용자 정의 하여 복잡한 구조로 작성. key-value 모드<br>이름에 대하여 자동완성을 설정. 'A' 을 눌러 보십시오."},
	filter : {
		showIcon : true
	},
	/* editRenderer : {
		type : "RemoteListRenderer",
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
		remoter : function( request, response ) { // remoter 지정 필수
			if(String(request.term).length < 1) {
				alert("1글자 이상 입력하십시오.");
				response(false); // 데이터 요청이 없는 경우 반드시 false 삽입하십시오.
				return;
			}
			var codeType = "partName3".toUpperCase();
			
			var url	= getURLString("common", "autoSearchNameRtnName", "do");
			return $.ajax({
				type:"POST",
				url: url,
				data: {
					codeType : codeType,
					name : request.term
				},
				dataType:"json",
				async: false,
				cache: false,
				error:function(data){
					var msg = type + " ${f:getMessage('코드 목록 오류')}";
					alert(msg);
				},
				success: function(data) {
					// 성공 시 완전한 배열 객체로 삽입하십시오.
					response(data); 
				}
			});
		}
	} */
	editRenderer : {
		type : "ComboBoxRenderer",
		autoCompleteMode : true, // 자동완성 모드 설정
		autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
		showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
		listFunction : function(rowIndex, columnIndex, item, dataField) {
			if(item.partState == "승인됨"){
      	    	//console.log(rowIndex);
      			item.partName4 = "";
      	    	 return [];
      	     }
      	    	
      	     return partName4List;
		}
	}
},{
	dataField: "partName",
	headerText: "품목명",
	width: "8%",
	enableRestore : true,
	filter : {
		showIcon : true
	}
}
/* ,
{
	dataField: "drawingName",
	headerText: "도면명",
	width: "8%",
	filter : {
		showIcon : true
	}
} */
];
                    
$(document).ready(function() {
	setNumberCodelList('PARTTYPE','','gubunList');
	setNumberCodelList("PARTNAME1",'',"partName1List");
	setNumberCodelList("PARTNAME2",'',"partName2List");
	setNumberCodelList("PARTNAME3",'',"partName3List");
	setNumberCodelList("PARTNAME4",'',"partName4List");
	//Aui 그리드 설정
	var auiGridProps = {
		
		/********트리 그리드************/
			softRemoveRowMode : false,
			
			showStateColumn : true,
			
			showRowCheckColumn : true,
			// singleRow 선택모드
			selectionMode : "multipleCells",
			
			showSelectionBorder : true,
			
			displayTreeOpen : true,
			// 편집 가능 여부
			editable : true,
			
			// 엑스트라 행 체크박스 칼럼이 트리 의존적인지 여부
			// 트리 의존적인 경우, 부모를 체크하면 자식도 체크됨.
			rowCheckDependingTree : true,
			
			// 트리 컬럼(즉, 폴딩 아이콘 출력 칼럼) 을 인덱스1번으로 설정함(디폴트 0번임)
			treeColumnIndex : 2,
			
			// 필터사용
			enableFilter : true,
			
			// 일반 데이터를 트리로 표현할지 여부(treeIdField, treeIdRefField 설정 필수)
			flat2tree : true,
			
			// 행의 고유 필드명
			rowIdField : "rowId",
			
			// 트리의 고유 필드명
			treeIdField : "id",
			
			// 계층 구조에서 내 부모 행의 treeIdField 참고 필드명
			treeIdRefField : "parent",
			
			enableSorting : false,
			//승인됨 체크
			rowStyleFunction: function(rowIndex, item) {
				if($.trim(item.partState) == "승인됨") { 
					return "deletedColor";
				}
				return null;
			},/* 
			rowCheckableFunction : function(rowIndex, isChecked, item) {
				if($.trim(item.isCheckPartType) == "false") {  //사용자 체크 못하게 함.
					return false;
				}
				return true;
			},
			rowCheckDisabledFunction : function(rowIndex, isChecked, item) {
				if($.trim(item.partState) == "승인됨" || $.trim(item.isDisablePartType) =="true") { 
					return false; // false 반환하면 disabled 처리됨
				}
				return true;
			},*/
			//fixed 고정
			fixedColumnCount : 7
	};
	// 실제로 #grid_wrap 에 그리드 생성
	myBOMGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
	// ready 이벤트 바인딩
	// 전체 체크박스 클릭 이벤트 바인딩
	AUIGrid.bind(myBOMGridID, "rowAllChkClick", function( event ) {
		if(event.checked) {
			// name 의 값들 얻기
			var uniqueValues = AUIGrid.getColumnDistinctValues(event.pid, "partState");
			// Anna 제거하기
			uniqueValues.splice(uniqueValues.indexOf("승인됨"),1);
			AUIGrid.setCheckedRowsByValue(event.pid, "partState", uniqueValues);
		} else {
			AUIGrid.setCheckedRowsByValue(event.pid, "partState", []);
		}
	});
	AUIGrid.bind(myBOMGridID, "rowCheckClick", function( event ) {
		var item = event.item;
	      var rowIndex = event.rowIndex;
	      varchecked = event.checked;
	      /* if(item._$groupParentValue && item._$isBranch) { // 그룹핑하여 계층형으로 출력된 경우
	          console.log("rowIndex : " + rowIndex + ", group : " + item._$groupParentValue + ", isBranch : " + item._$isBranch + ", checked : " + checked);
	      } */
	      checkboxChangeHandler(event);
	});
	
	// 에디팅 정상 종료 이벤트 바인딩
	AUIGrid.bind(myBOMGridID, "cellEditBegin", function( event ) {
		if(event.dataField == "partNameSeq" || event.dataField == "etc") {
			if(event.item.isDisablePartType && event.item.isCheckPartType!="가능" && (null==event.item.realPartNumber)) {
				return false; // false 반환. 기본 행위인 편집 불가
			}
		}
		if(event.dataField == "partName1" || event.dataField == "partName2" ||
			event.dataField == "partName3" || event.dataField == "partName4" ) {
			if($.trim(event.item.partState) == "승인됨") {
				return false; // false 반환. 기본 행위인 편집 불가
			}
		}
	});
	AUIGrid.bind(myBOMGridID, ["cellEditEnd","cellClick"], function( event ) {
       if(event.type == "cellClick"){ 
       }else if(event.type == "cellEditEnd"){ 
    	   if(event.dataField=="isCheckPartType"){
    		   checkboxChangeHandler(event);
    	   }else if(event.dataField=="gubun" || event.dataField=="main" ||event.dataField=="middle"|| event.dataField=="partNameSeq" || event.dataField=="etc"){
    		   selectboxChangeHandler(event);
    	   }else if((event.dataField=="partName1"|| event.dataField=="partName2" || event.dataField=="partName3" || event.dataField=="partName4" )){
    		   editTextChangeHandler(event);
    	   }
       }
 });
	//BOM Search
	viewAUIPartBomAction();
	
	popupAUIResize();
	
	/*
	AUIGrid.bind(myBOMGridID, "rowCheckClick", function( event ) {
	     
		var item = event.item;
	    var rowIndex = event.rowIndex;
	    varchecked = event.checked;
	    var selectIndexItems = AUIGrid.getSelectedItems(myBOMGridID);
	    
		var rowIdArray = [];
 	    var uniqueRowIdArray = []; 
 	   // uniqueRowIdArray.push(rowIndex);
	    
	    if(selectIndexItems.length > 0){
	    
	 	    $.each(selectIndexItems, function(n, v) {
	 	    	
	 	          rowIdArray.push (v.rowIdValue); // rowIdField 의 값만 배열에 보관.
	 	    });
			
	 	    // rowIdField 의 배열 중 모든 셀에 대하여 rowIdField 값이기 때문에 중복이 발생하고 있음.
	 	    // 따라서 중복 제거 시킴.
	 	    $.each(rowIdArray, function(n, v) {
	 	         if(uniqueRowIdArray.indexOf(v) == -1) uniqueRowIdArray.push(v);
	 	    });
	    }
		
	    if(varchecked){
 	 	// 엑스트라 체크박스 체크 하기
 	 		AUIGrid.setCheckedRowsByIds(myBOMGridID, uniqueRowIdArray);
 	 	}else{
 	 		 AUIGrid.addUncheckedRowsByIds(myBOMGridID, uniqueRowIdArray);
 	 	}
	    
	 	
	   

	});
	*/
	
	//console.log("gubunList = " + gubunList.length);
	/*
	AUIGrid.bind(myBOMGridID, "filtering", function( event ) {
		var dataFiled = new Object();
	    var i = 0 ;
		for(var n in event.filterCache) {
			 console.log( "dataField = "+ n  +" ,filter = " + event.filterCache[n] );
	          
	           i++;
	     }
	     var gridItemList = AUIGrid.getGridData(myBOMGridID)
	     console.log("gridItem length= " + gridItemList.length);
	     for(var i = 0 ; i < gridItemList.length ;  i++ ){
	    	 console.log("gridItem number =" +gridItemList[i].number +","+gridItemList[i].model +);
	    	 if()
	     }
	});
	*/
	// 에디팅 시작 이벤트 바인딩
	/*
	AUIGrid.bind(myBOMGridID, "cellEditEndBefore", function(event) { 
        // 셀이 Anna 인 경우 
       console.log("cellEditEndBefore event " + event.item.number)
    });
	// 에디팅 정상 종료 이벤트 바인딩
	AUIGrid.bind(myBOMGridID, "cellEditEnd", function( event ) {
		console.log("cellEditEnd event " + event.item.number)
	})
	
	
	
	/*
	AUIGrid.bind(myBOMGridID, "selectionChange", function( event ) {
	       alert("rowIndex : " + event.rowIndex + ", columnIndex  =" + event.item.number );
	 });
	
	*/
	/*
	AUIGrid.bind(myBOMGridID, "rowAllChkClick", function( event ) {
		var rowList = AUIGrid.getCheckedRowItems(myBOMGridID);
	})
	*/
	/*
	AUIGrid.bind(myBOMGridID, "rowCheckClick", function( event ) {
		// console.log("event.item :" + event.item);
		 var item = event.item
		 
		 childCheckBox(item);
		 
		// console.log("childList" + event.item);
  	   	  for(var i= 0 ; i < childList.length ; i++){
  	   		var childItem = childList[i]
  	   	
  	   		console.log(childItem.number+","+childItem.rowId + childItem.state);
  	   		//if(childItem.state == "승인됨"){
  	   			//AUIGrid.addUncheckedRowsByIds(myBOMGridID,"state","승인됨");
  	   			AUIGrid.addUncheckedRowsByIds(myBOMGridID,childItem.rowId);
  	   		//}
  	    	
  	   	  }
			//console.log("rowIndex : " + event.rowIndex + ",  event.item.rowId  =" + event.item.rowId + " ,"+event.item.number);
	      // var childList = AUIGrid.getDescendantsByRowId(event.item.rowId);
	       //console.log("childList =" + childList)
	       //
	       /*
	       var rowList = AUIGrid.getCheckedRowItemsAll(myBOMGridID);
	       for( var i = 0 ; i < rowList.length ;  i++){
	    	  // console.log(rowList+",rowList[0] = "+rowList[0] +",rowList[1]" + rowList[1]);
	    	   var item = rowList[i];
	    	   console.log(item.number);
	       }
	       */
	       /*
	       var rowList = AUIGrid.getCheckedRowItems(myBOMGridID);
	       for( var i = 0 ; i < rowList.length ;  i++){
	    	   console.log(rowList[i].item.);
	    	  
	    	   
	       }
	       
	});
	*/
});
$(function() {
	$("#depthAllSelect").click(function() {
		showItemsOnAll();
	});
	$("#setNumber").click(function() {
		if(validationCheck()) {
			return;
		}
		var param = new Object();
		$("input[type=hidden]").each(function() {
			if($.trim(this.name) != "") {
				param[this.name] = this.value;
			}
		});
		//var rowList = AUIGrid.getEditedRowItems(myBOMGridID);//AUIGrid.getCheckedRowItems(myBOMGridID);
		//console.log(rowList);
		//console.log("rowList ="+rowList.length);
		/* param.rowList = rowList;*/
		if (confirm("${f:getMessage('변경하시겠습니까?')}")){
			
			//var form = $("form[name=updatePackagePart]").serialize();
// 			var url	= getURLString("part", "updateAUIPartChangeAction", "do");
			var url = getCallUrl("/part/updateAUIPartChangeAction");
			$.ajax({
				type:"POST",
				url: url,
				data : JSON.stringify(param),
				dataType:"json",
				contentType : "application/json; charset=UTF-8",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('변경 오류')}";
					alert(msg);
				},

				success:function(data){
					if(data.result) {
						alert("${f:getMessage('변경 성공하였습니다.')}");
						console.log("data.oid="+data.oid);
// 						location.href = getURLString("part", "updateAUIPartChange", "do") + "?
						location.href = getCallUrl("/part/order?oid=" + data.oid);
					}else {
						alert("${f:getMessage('변경 실패하였습니다.')}\n" + data.message);
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
	});
	$('#filterInit').click(function() {
		AUIGrid.clearFilterAll(myBOMGridID);
	})
	$('#batchAttribute').click(function() {
		batchAttribute();
	});
});
function apply(dataMap){
	//alert(dataMap)
	
	//alert(dataMap["model"]);
	var gubun = dataMap["gubun"];
	var gubunOid = dataMap["gubunOid"];
	var main = dataMap["main"];
	var mainOid = dataMap["mainOid"];
	var middle = dataMap["middle"];
	
	
	//setNumberCodelList('PARTTYPE',gubunOid,"mainvalueList");
	//setNumberCodelList('PARTTYPE',mainOid,"middleValueList");
	var checkedItems = AUIGrid.getCheckedRowItems(myBOMGridID);//AUIGrid.getCheckedRowItemsAll(myBOMGridID);//
	
	for(var i = 0; i < checkedItems.length; i++){
		var checkedItem = checkedItems[i].item;
		
		var number = checkedItem.number;
		var rowId = checkedItem.rowId;
		var isCheckPartType = checkedItem.isCheckPartType;
		var oidL = checkedItem.oidL;
		if(isCheckPartType=="가능") {
			//console.log(rowId+",ORG number =" + number +",model =" + checkedItem.model+",productmethod ="+checkedItem.productmethod);
			var changegubun = "";
			var changemain = "";
			var changemiddle = "";
			var changeseqs = "";
			
			if(gubun.length == ""){
				
				changegubun = checkedItem.gubun;
			}else{
				changegubun = gubun
			}
			if(main =="" ){
				
				changemain = checkedItem.main;
			}else{
				changemain = main;
			}
			
			if(middle ==""){
				changemiddle = checkedItem.specification;
			}else{
				changemiddle = middle;
			}
			changeseqs = "change";
			
			<c:forEach var="i" items="${list}">
			 if("${i.oidL}"==oidL){
				setNumberCodelList('PARTTYPE',gubunOid,"mainvalueList_${i.oidL}");
				setNumberCodelList('PARTTYPE',mainOid,"middleValueList_${i.oidL}");
			 }
			</c:forEach>
			
		   // var updateItem = {id : checkedItem.id ,  remarks : model}
			var index = AUIGrid.rowIdToIndex(myBOMGridID, checkedItem.rowId);	
			var checkedItem = {
					//rowId : rowId, 
					gubun : changegubun,
					main : changemain,
					middle : changemiddle,
					partNumberSeqs : changeseqs,
					realPartNumber : gubun+main+middle+"_____"
			}
			
			AUIGrid.updateRow(myBOMGridID, checkedItem, index);
			//AUIGrid.updateRowsById(myBOMGridID,checkedItem)
		}
		
	}
}

<%----------------------------------------------------------
*                      Bom Type에 따른 bom 검색
----------------------------------------------------------%>
function batchAttribute(){
// 	var str = getURLString("part", "batchPartNumber", "do");
	var str = getCallUrl("/part/batchPartNumber");
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=600,height=500,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "batchPartNumber", opts+rest);
    newwin.focus();
}
<%----------------------------------------------------------
*                      validation Check
----------------------------------------------------------%>
function validationCheck() {
	var checkedItems = AUIGrid.getEditedRowItems(myBOMGridID);//AUIGrid.getEditedRowItems(myBOMGridID); getCheckedRowItems
	
	if(checkedItems.length == 0){
		alert("${f:getMessage('수정된 ')}${f:getMessage(' 품목이 없습니다.')}");
		return true;
	}else{
		console.clear();
		$("input[type=hidden]").each(function() {
			if($.trim(this.name) != "" && this.name!="oid") {
				 $(this).remove();
			}
		})
		
		var isStartpartNumberSeqs = false;
		var cnt = 0;
		for(var i = 0; i < checkedItems.length; i++){
			
			var checkedItem = checkedItems[i];
			var number = checkedItem.number;
			var rowId = checkedItem.rowId;
			var realPartNumber = checkedItem.realPartNumber;
			var gubun = checkedItem.gubun;
			var main = checkedItem.main;
			var middle = checkedItem.middle;
			var partNameSeq = checkedItem.partNameSeq;
			var etc = checkedItem.etc;
			var partNumberSeqs = checkedItem.partNumberSeqs;
			var partName1 = checkedItem.partName1;
			var partName2 = checkedItem.partName2;
			var partName3 = checkedItem.partName3;
			var partName4 = checkedItem.partName4;
			var realPartName = checkedItem.partName;
			var isCheckPartType = checkedItem.isCheckPartType;
			var changeOid = checkedItem.oid;
			var isPartNameEx = true;
			
			if(isCheckPartType=="가능"){
				if(gubun == ""){
					alert("["+rowId+"]"+number+"의  ${f:getMessage('품목구분')}${f:getMessage('을(를) 선택하세요.')}");
					return true;
				}
				if(main == ""){
					alert("["+rowId+"]"+number+"의    ${f:getMessage('대분류')}${f:getMessage('을(를) 선택하세요.')}");
					return true;
				}
				if(middle == ""){
					alert("["+rowId+"]"+number+"의   ${f:getMessage('중분류')}${f:getMessage('을(를) 선택하세요.')}");
					return true;
				}

				if(typeof(realPartNumber)!="undefined" && null!=realPartNumber && realPartNumber.length>=5){
					appendHDPartNumber(changeOid,realPartNumber);
					appendHDPartSeq(changeOid,partNameSeq);
					if(typeof(partNumberSeqs)!="undefined" && null!=partNumberSeqs && partNumberSeqs.length>0){
						appendHDPartNumberADDSeqs(changeOid,cnt);
						cnt++;
					}
					appendHDPartETC(changeOid,etc);
				}else if(typeof(realPartNumber)!="undefined" && null!=realPartNumber && realPartNumber.length<5){
					alert("["+rowId+"]"+number+"의 ${f:getMessage('진도번')}${f:getMessage('을(를) 채번 하세요.')}");
					return true;
				}
			}
			
			if(typeof(realPartName)!="undefined" && null!=realPartName && realPartName.length>0){
				var tmp = realPartName.split("_");
				var tmpIdx = tmp.length;
				appendHDPartName(changeOid,realPartName);
				if(typeof(partName1)!="undefined" && null!=partName1 && partName1.length>0)
					appendHDPartNames(changeOid,1,partName1);
				if(typeof(partName2)!="undefined" && null!=partName2 && partName2.length>0)
					appendHDPartNames(changeOid,2,partName2);
				if(typeof(partName3)!="undefined" && null!=partName3 && partName3.length>0)
					appendHDPartNames(changeOid,3,partName3);
				if(typeof(partName4)!="undefined" && null!=partName4 && partName4.length>0)
					appendHDPartNames(changeOid,4,partName4);
			}else if(typeof(realPartNumber)=="undefined"){
				alert("["+rowId+"]"+number+"의 ${f:getMessage('품목명')}${f:getMessage('을(를) 입력 하세요.')}");
				return true;
			}else if(typeof(realPartName)!="undefined" && null!=realPartName && realPartName.length==0){
				alert("["+rowId+"]"+number+"의 ${f:getMessage('품목명')}${f:getMessage('을(를) 입력 하세요.')}");
				return true;
			}
		}
	}
}
function appendHDPartNumberADDSeqs(name,value){
	var input = document.createElement("input");
	input.setAttribute("type", "hidden");
	input.setAttribute("name", "partNumberSeqs_" +name);
	input.setAttribute("value", value);
	document.getElementById("updateAUIPartChange").appendChild(input);
}
function appendHDPartETC(name,value){
	var input = document.createElement("input");
	input.setAttribute("type", "hidden");
	input.setAttribute("name", "etc_" +name);
	input.setAttribute("value", value);
	document.getElementById("updateAUIPartChange").appendChild(input);
}
function appendHDPartSeq(name,value){
	var input = document.createElement("input");
	input.setAttribute("type", "hidden");
	input.setAttribute("name", "seq_" +name);
	input.setAttribute("value", value);
	document.getElementById("updateAUIPartChange").appendChild(input);
}
function appendHDPartNumber(name,value){
	var input = document.createElement("input");
	input.setAttribute("type", "hidden");
	input.setAttribute("name", "realPNumber_" +name);
	input.setAttribute("value", value);
	document.getElementById("updateAUIPartChange").appendChild(input);
}
function appendHDPartName(name,value){
	var input = document.createElement("input");
	input.setAttribute("type", "hidden");
	input.setAttribute("name", "realPName_" +name);
	input.setAttribute("value", value);
	document.getElementById("updateAUIPartChange").appendChild(input);
}
function appendHDPartNames(name,idx,value){
	var input = document.createElement("input");
	input.setAttribute("type", "hidden");
	input.setAttribute("name", "realPName"+idx+"_" +name);
	input.setAttribute("value", value);
	document.getElementById("updateAUIPartChange").appendChild(input);
}
//체크 박스 편집 핸들러
function editTextChangeHandler(event) {
	if(event.type == "cellEditEnd") {
		// 체크 박스에 맞는 rowItem 얻기
		var rowItem = AUIGrid.getItemByRowIndex(myBOMGridID, event.rowIndex);
		// 체크박스가 Active 로 변했다면 available 필드 값도 true 로 바꿔서 Available 도 변하게 만듬
		//rowItem.partName = rowItem.partName1+" "+rowItem.partName2+" "+rowItem.partName3+" "+rowItem.partName4;
	/* 	if(rowItem.partName1.length==0 || rowItem.partName2.length==0 || rowItem.partName3.length==0 || rowItem.partName4.length>0){
			rowItem.partName = "";
		} */
		var tmp = rowItem.partName1+"_"+rowItem.partName2+"_"+rowItem.partName3+"_"+rowItem.partName4;
		tmp = check(tmp);
		rowItem.partName = tmp;
		
		$("#realPName").val(rowItem.partName);
		// row 데이터 업데이트
		AUIGrid.updateRow(myBOMGridID, rowItem, event.rowIndex);
	
		// 하단에 정보 출력
		//document.getElementById("ellapse").innerHTML = "Editing End : ( " + event.rowIndex  + ", " + event.columnIndex + ") : " + event.value;
	}
};
function check(tmp){
	var data = "";
	for(var i=0,len=tmp.length; i<len; i++) {
		var c =  tmp.charAt(i);
		var c1 =  tmp.charAt(i+1);
		if(c=="_" && c1=="_"){
			data+=""
		}else{
			data+=c;
		}
	}
	if(data.lastIndexOf("-")==(data.length-1)){
		data = data.substring(0,data.length-1);
	}
	if(data.lastIndexOf("_")==(data.length-1)){
		data = data.substring(0,data.length-1);
	}
	if(data.indexOf("_")==(0)){
		data = data.substring(1,data.length);
	}
	if(data.indexOf("_")==(data.length)){
		data = data.substring(0,data.length-1);
	}
	return data;
}
function checkboxChangeHandler(event) {
	//console.log(event.dataField);
	if(event.type == "cellEditEnd") {
	//	console.log(event.dataField);
		// 체크 박스에 맞는 rowItem 얻기
		var rowItem = AUIGrid.getItemByRowIndex(myBOMGridID, event.rowIndex);
		// 체크박스가 Active 로 변했다면 available 필드 값도 true 로 바꿔서 Available 도 변하게 만듬
		if(event.dataField=="isCheckPartType" && event.value!="가능"){
			//rowItem.gubun = "";
			//rowItem.main = "";
			//rowItem.middle = "";
			//rowItem.partNameSeq = "";
			//rowItem.etc = "";
			//rowItem.realPartNumber = "";
			//mainvalueList =[{"code":"-","oid":"-","value":"-"}];
			//middleValueList =[{"code":"-","oid":"-","value":"-"}];
			
			//AUIGrid.updateRow(myBOMGridID, rowItem, event.rowIndex);
			restoreSelectedEditedCells(rowItem,event.rowIndex);
		}
	}
}
//선택 행이 수정 행이라면, 수정 취소 즉, 원래 값으로 복구 시키기
function restoreSelectedEditedCells(rowItem,idx) {
	AUIGrid.restoreEditedRows(myBOMGridID, idx);
	var item = { rowId : idx,  realPartNumber : "" , isCheckPartType : "불가능"}; // ID 가 20인 행을 찾아 price 2000, name 우리로 변경
    AUIGrid.updateRowsById(myBOMGridID, item,false); // 1개 업데이트
    var item = { rowId : idx,  isCheckPartType : "불가능"};
    AUIGrid.updateRowsById(myBOMGridID, item,false); // 1개 업데이트
//	items (Array or Object) : 복수 또는 1개의 행을 업데이트 합니다.
//	isMarkEdited (Boolean) : 셀 수정 마커(marker) 표시를 할지 여부를 나타냅니다.(기본값 : true)
};
function getParentOid(objList,selectIdx){
	var pOIDreturn = "";
	 for(var i=0,len=objList.length; i<len; i++) {
 		
 		if(selectIdx == objList[i].code){
 			pOIDreturn = objList[i].oid
 			break;
 		}
	}
	 return pOIDreturn;
}
function getParentV(objList,selectIdx){
	var pOIDreturn = "";
	 for(var i=0,len=objList.length; i<len; i++) {
 		if(selectIdx == objList[i].code){
 			pOIDreturn = objList[i].value;
 			break;
 		}
	}
	 return pOIDreturn;
}
function selectboxChangeHandler(event,field) {
	if(event.type == "cellEditEnd") {
		// 체크 박스에 맞는 rowItem 얻기
		var rowItem = AUIGrid.getItemByRowIndex(myBOMGridID, event.rowIndex);
		if((event.dataField=="gubun" || event.dataField=="main" || event.dataField=="middle" )&& event.value.length>0 ){
			var parentOid = "";
			var parentValue ="";
			var objList;
			var selectIdx;
			var callList = "";
			if(event.dataField=="gubun"){
				objList =gubunList;
				selectIdx = rowItem.gubun ;
				callList = "mainvalueList_"+rowItem.oidL;
			}else if(event.dataField=="main"){
				<c:forEach var="i" items="${list}">
			   	if(rowItem.oidL=='${i.oidL}'){
			   		objList = mainvalueList_${i.oidL};
			   	}
			   	</c:forEach>
				selectIdx = rowItem.main ;
				callList = "middleValueList_"+rowItem.oidL;
			}else if(event.dataField=="middle"){
				<c:forEach var="i" items="${list}">
				if(rowItem.oidL=='${i.oidL}'){
			   		objList = middleValueList_${i.oidL};
			   	}
			   	</c:forEach>
			}
    		if(typeof(rowItem.partNumberSeqs)!="undefined" && rowItem.partNumberSeqs.length>0){
    			rowItem.partNumberSeqs = "";
    		}
    		
    		parentOid = getParentOid(objList,selectIdx);
    		parentValue= getParentV(gubunList,rowItem.gubun);
			setNumberCodelList('PARTTYPE',parentOid,callList);
			if(event.dataField=="gubun"){
				if(typeof(rowItem.main)!="undefined" && rowItem.main.length>0){
					<c:forEach var="i" items="${list}">
					if(rowItem.oidL=='${i.oidL}'){
				   		parentOid = getParentOid(mainvalueList_${i.oidL},rowItem.main);
		   				parentValue = getParentV(mainvalueList_${i.oidL},rowItem.main);
						setNumberCodelList('PARTTYPE',parentOid,'middleValueList_${i.oidL}');
				   	}
				   	</c:forEach>
	   				
				}
			}
			var partNumber1 = changeString(rowItem.gubun,1);
			var partNumber2 = changeString(rowItem.main,2);
			var partNumber3 = changeString(rowItem.middle,2);
			var partNumber4 = changeString(rowItem.partNameSeq,3);
			var partNumber5 = changeString(rowItem.etc,2);
			rowItem.realPartNumber = partNumber1+partNumber2+partNumber3;
			if(partNumber4=="___" && partNumber5.length==2)
				rowItem.realPartNumber = partNumber1+partNumber2+partNumber3+"___"+partNumber5;
			else if(partNumber4!="___" && partNumber5.length==2)
				rowItem.realPartNumber = partNumber1+partNumber2+partNumber3+partNumber4+partNumber5;
			else if(partNumber4!="___" && (partNumber5=="__") )
				rowItem.realPartNumber = partNumber1+partNumber2+partNumber3+partNumber4;
			else if(partNumber4=="___" && (partNumber5=="__") )
				rowItem.realPartNumber = partNumber1+partNumber2+partNumber3;
			
			AUIGrid.updateRow(myBOMGridID, rowItem, event.rowIndex);
			
			
		}else if((event.dataField=="partNameSeq" || event.dataField=="etc" )/* && event.value.length>0 */){
			var partNumber1 = changeString(rowItem.gubun,1);
			var partNumber2 = changeString(rowItem.main,2);
			var partNumber3 = changeString(rowItem.middle,2);
			var partNumber4 = changeString(rowItem.partNameSeq,3);
			var partNumber5 = changeString(rowItem.etc,2);
			if(partNumber1!="_" && partNumber2!="__" &&  partNumber3!="__"){
				if( partNumber4=="___" && partNumber5.length==2){
					rowItem.realPartNumber = partNumber1+partNumber2+partNumber3+"___"+partNumber5;
				}else if(partNumber4!="___" && partNumber5.length==2){
					rowItem.realPartNumber = partNumber1+partNumber2+partNumber3+partNumber4+partNumber5;
				}else if(partNumber4.length>0 && (partNumber5=="__") ){
					rowItem.realPartNumber = partNumber1+partNumber2+partNumber3+partNumber4+"__";
				}else if((partNumber4=="___") && ( partNumber5=="__" ) ){
					rowItem.realPartNumber = partNumber1+partNumber2+partNumber3;
				}else if(partNumber4.length==3){
					rowItem.realPartNumber = partNumber1+partNumber2+partNumber3+partNumber4+partNumber5;
				}else if(partNumber5.length==2){
					rowItem.realPartNumber = partNumber1+partNumber2+partNumber3+partNumber4+partNumber5;
				}
				AUIGrid.updateRow(myBOMGridID, rowItem, event.rowIndex);
			}
		}
		
	}
}
function changeString(value,leng){
	var data =  value;
	var tmp = "";
	if(typeof(value) == "undefined" || value.length==0){
		data =  "";
		for(var i=0; i<leng; i++) {
			tmp+="_";
		}
		data = tmp;
	}
	else{
		if(value.length==leng) return data;
		else{
			for(var i=0; i<(leng-value.length); i++) {
				tmp+="_";
			}
			data = tmp+ value;
		}
	}  
	return data;
}
<%----------------------------------------------------------
*                      NumberCode Setup
----------------------------------------------------------%>

function setNumberCodelList(type, parentCode1,objlist){ //numberCodeList
	
	var data = common_numberCodeList(type,parentCode1, false);
	var data2 =eval(data.responseText);
	for(var i=0; i<data2.length; i++) {
		
		var dataMap = new Object();
		
		dataMap["code"] = data2[i].code;
		dataMap["oid"] = data2[i].oid;
		dataMap["value"] = "["+data2[i].code+"]"+data2[i].name;
		
		var code = data2[i].code;
		var value = data2[i].name;
		if(type =="PARTTYPE"){
			if(objlist=="gubunList")
				gubunList[i] = dataMap;
			<c:forEach var="i" items="${list}">
			else if(objlist=="mainvalueList_${i.oidL}")
				mainvalueList_${i.oidL}[i] = dataMap;
			else if(objlist=="middleValueList_${i.oidL}")
				middleValueList_${i.oidL}[i] = dataMap;
			</c:forEach>
			/* else if(objlist=="mainvalueList")
				mainvalueList[i] = dataMap;
			else if(objlist=="middleValueList")
				middleValueList[i] = dataMap; */
		}else if(type == "PARTNAME1" || type == "PARTNAME2" || type == "PARTNAME3"){
			if(objlist=="partName1List")
				partName1List[i] = value;
			else if(objlist=="partName2List")
				partName2List[i] = value;
			else if(objlist=="partName3List")
				partName3List[i] = value;
		}
	}
	if(type =="PARTTYPE"){
		/* 
		if(objlist=="mainvalueList"){
			console.log("mainvalueList 정리");
			console.log("data2.length="+data2.length);
			console.log(mainvalueList[data2.length]);
			 if(typeof(mainvalueList[data2.length])!="undefined" ){
				 console.log("c="+(mainvalueList.length-data2.length));
					mainvalueList.splice(data2.length,mainvalueList.length-data2.length);
					console.log(mainvalueList);
			} 
		}else if(objlist=="middleValueList"){
			console.log("middleValueList 정리");
			console.log("data2.length="+data2.length);
			console.log(middleValueList[data2.length]);
			 if(typeof(middleValueList[data2.length])!="undefined" ){
				 console.log("c="+(middleValueList.length-data2.length));
			   	middleValueList.splice(data2.length,middleValueList.length-data2.length);
			   	console.log(middleValueList);
			} 
		} */
		<c:forEach var="i" items="${list}">
		if(objlist=="mainvalueList_${i.oidL}"){
			console.log("mainvalueList_${i.oidL} 정리");
			console.log("data2.length="+data2.length);
			console.log(mainvalueList_${i.oidL}[data2.length]);
			 if(typeof(mainvalueList_${i.oidL}[data2.length])!="undefined" ){
				mainvalueList_${i.oidL}.splice(data2.length,mainvalueList_${i.oidL}.length-data2.length);
				console.log(mainvalueList_${i.oidL});
			} 
		}else if(objlist=="middleValueList_${i.oidL}"){
			console.log("middleValueList_${i.oidL} 정리");
			console.log("data2.length="+data2.length);
			console.log(middleValueList_${i.oidL}[data2.length]);
			 if(typeof(middleValueList_${i.oidL}[data2.length])!="undefined" ){
			   	middleValueList_${i.oidL}.splice(data2.length,middleValueList_${i.oidL}.length-data2.length);
			   	console.log(middleValueList_${i.oidL});
			} 
		}
		
		</c:forEach>
	}
}
<%----------------------------------------------------------
*                      선택한 Depth 만큼 펴칠기
----------------------------------------------------------%>
function showItemsOnDepth(event) {
	var depthSelect = document.getElementById("depthSelect");
	var  depth = depthSelect.value;
	
	// 해당 depth 까지 오픈함
	AUIGrid.showItemsOnDepth(myBOMGridID, Number(depth) );
}
<%----------------------------------------------------------
*                      선택한 Depth 만큼 펴칠기
----------------------------------------------------------%>
function showItemsOnAll(event) {
	var totalDepth = AUIGrid.getTreeTotalDepth(myBOMGridID);
	// 해당 depth 까지 오픈함
	AUIGrid.showItemsOnDepth(myBOMGridID, Number(totalDepth) );
}
<%----------------------------------------------------------
*                     동적으로 Level 표시
----------------------------------------------------------%>
function setDepthList(totalDepth){
	$("#depthSelect").empty();
	$("#depthSelect").append("<option value='default' selected='selected' disabled='disabled'>특정 계층 선택</option>");
	
	for(var i=1; i<=totalDepth; i++) {
		var temp = i;
		$("#depthSelect").append("<option value='" +i + "'> Level " +(temp-1)+ "</option>");
	}
}

<%----------------------------------------------------------
*                      Bom 리스트 검색
----------------------------------------------------------%>
function viewAUIPartBomAction(){
	xpanded = false;
	var form = $("form[name=updateAUIPartChange]").serialize();
	var url	= getCallUrl("/part/updateAUIPartChangeSearchAction");
	var params = {
		oid : document.getElementById("oid").value,
		checkDummy : document.getElementById("checkDummy").value
	}
	$.ajax({
		type:"POST",
		url: url,
		data : JSON.stringify(params),
		dataType:"json",
		async: true,
		cache: false,
		contentType: "application/json; charset=UTF-8",
		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},
	
		success:function(data){
			//console.log(data);
			var gridData = data;
			
			AUIGrid.setGridData(myBOMGridID, gridData);
			
			var totalDepth = AUIGrid.getTreeTotalDepth(myBOMGridID);
			setDepthList(totalDepth)
			
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
	    }
		,complete: function() {
			gfn_EndShowProcessing();
	    }
	});
}
</script>

<style>
.hover{ 
 	  cursor: default;
      background:#dedede;
}
</style>

<body>

<form name=updateAUIPartChange id=updateAUIPartChange  method=post  >

<input type="hidden" name="oid" id="oid" value="<%=oid %>">
<input type="hidden" name="checkDummy" id="checkDummy" value="false">


<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align=center>
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="1" class="tablehead" align=center style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color=white>${f:getMessage('품목')}${f:getMessage('진채번')}</font></B></td>
		   		</tr>
			</table>
			
		    <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
		    	<tr height="30">
		    	<td>
						<table border="0" cellpadding="0" cellspacing="4" align="left">
							<tr>
								<td>
									<table border="0" cellpadding="0" cellspacing="4" align=left>
										<tr>
											<!-- <td>
												<input type="checkbox" name="checkDummy" id="checkDummy" value="" checked onchange="viewAUIPartBomAction()" > 더미제외
											</td> -->
											<td>
												<button type="button" class="btnCustom" id="depthAllSelect" >
													<span></span>
													${f:getMessage('펼치기')}
												</button>
											</td>
											<td>
												<select id="depthSelect" onchange="showItemsOnDepth()">
												</select>
											</td>
										</tr>
									</table>
		    					</td>
							</tr>
						</table>
		    		</td>
		    		<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
							<tr>
								<td>
									<table border="0" cellpadding="0" cellspacing="4" align=right>
										<tr>
											<td>
												<button type="button" class="btnCustom" id="filterInit"  >
													<span></span>
													${f:getMessage('필터 초기화')}
												</button>
											</td>
											<td>
												<button type="button" class="btnCustom" id="batchAttribute"  >
													<span></span>
													${f:getMessage('속성 일괄 적용')}
												</button>
											</td>
											<td>
												<button type="button" class="btnCRUD" id="setNumber">
													<span></span>
													${f:getMessage('저장')}
												</button>
											</td>
											<td>
												<button type="button" class="btnClose" onclick="self.close();" >
													<span></span>
													${f:getMessage('닫기')}
												</button>
											</td>
										</tr>
									</table>
							    </td>
							</tr>
						</table>
		    		</td>
		    	</tr>
		    </table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="grid_wrap" style="width: 100%; height:500px; margin-top: 2px; border-top: 3px solid #244b9c;">
			</div>
		</td>
	</tr>
	
</table>
</form>

</body>
</html>