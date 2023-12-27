<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<form name="partChange">
<input type="hidden" name="oid" id="oid" value="<%= oid %>" />
<input type="hidden" name="checkDummy" id="checkDummy" value="false">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">&nbsp;부품 진채번
			</div>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr height="5" align=center>
		<td>
			<table class="button-table">
				<tr>
					<td class="left">
						<input type="button" value="펼치기" name="depthAllSelect">
					</td>
					<td class="right">
						<input type="button" value="필터 초기화" name="filterInit">
						<input type="button" value="속성 일괄 적용" name="batchAttribute">
						<input type="button" value="저장" name="setNumber" class="btnCRUD">
						<input type="button" value="닫기" name="closeBtn" class="btnClose" onclick="self.close();">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>
</form>
<script>
let myGridID;
function _layout() {
	return [ {
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
		headerText: "REV",
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
			 }
		} 
	},
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
		renderer : {
	    	type : "DropDownListRenderer",
	    	keyField : "code",
	    	valueField : "value",
	    	descendants : [ "main"], // 자손 필드들
	    	listFunction : function(rowIndex, columnIndex, item, dataField) {
	    		
	   	     if(item.isCheckPartType!="가능"){
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
	    	keyField : "code",
	    	valueField : "value",
	    	listFunction : function(rowIndex, columnIndex, item, dataField) {
	      	     if(item.isCheckPartType!="가능"){
	      			item.middle = "";
	      	    	 return [];
	      	     }
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
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				
				if(item.partState == "승인됨"){
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
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				if(item.partState == "승인됨"){
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
		editRenderer : {
			type : "ComboBoxRenderer",
			autoCompleteMode : true, // 자동완성 모드 설정
			autoEasyMode : true, // 자동완성 모드일 때 자동 선택할지 여부 (기본값 : false)
			showEditorBtnOver : true, // 마우스 오버 시 에디터버턴 보이기
			listFunction : function(rowIndex, columnIndex, item, dataField) {
				if(item.partState == "승인됨"){
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
	];
}

function createAUIGrid(columnLayout) {
	const props = {
		headerHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		fillColumnSizeMode: false,
		showAutoNoDataMessage : false,
		selectionMode : "multipleCells",
		hoverMode : "singleRow",
		enableMovingColumn : true,
		enableFilter : true,
		showInlineFilter : false,
		useContextMenu : true,
		enableRightDownFocus : true,
		filterLayerWidth : 320,
		filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
	};
	myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
	loadGridData();
	AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
	AUIGrid.bind(myGridID, "vScrollChange", function(event) {
		hideContextMenu();
		vScrollChangeHandler(event);
	});
	AUIGrid.bind(myGridID, "hScrollChange", function(event) {
		hideContextMenu();
	});
}

function loadGridData() {
	const params = new Object();
	const url = getCallUrl("/part/partChange");
	const oid = document.querySelector("#oid").value;
	params.oid = oid;
	AUIGrid.showAjaxLoader(myGridID);
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(myGridID);
		if (data.result) {
			AUIGrid.setGridData(myGridID, data.list);
		} else {
			alert(data.msg);
		}
	});
}

document.addEventListener("DOMContentLoaded", function() {
	const columns = loadColumnLayout("downloadHistory");
	const contenxtHeader = genColumnHtml(columns);
	createAUIGrid(columns);
	AUIGrid.resize(myGridID);
});
</script>

