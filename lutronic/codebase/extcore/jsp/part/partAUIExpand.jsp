<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
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
.aui-grid-tree-minus-icon {
	display: inline-block;
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
<script type="text/javascript">
<%----------------------------------------------------------
*                      AUI Tree 그리드 
----------------------------------------------------------%>
var myBOMGridID;

var defaultGridHeight = 61;
var rowHeight = 26;
var headerHeight = 24;
var footerHeight = 30;
//AUIGrid 칼럼 설정
var columnLayout = [ {
	dataField: "rowId",
	headerText: "rowId",
	editable : false,
	width : "3%"
},
{
	dataField : "level",
	headerText : "Level",
	width: "5%",
	filter : {
		showIcon : true
	},
	
}, 
{
	dataField : "number",
	headerText : "부품번호",
	width: "10%",
	editable : false,
	filter : {
		showIcon : true
	},
}, 
{
	dataField: "name",
	headerText: "부품명",
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
		var temp = "<a href=javascript:gotoViewPartTree('" + item.oid + "') style='line-height:26px;'>" + value + "</a>"
		return temp; // HTML 템플릿 반환..그대도 innerHTML 속성값으로 처리됨
	}
},
//isSelect
//Line
//lineImg
//icon
{
	dataField : "state",
	headerText : "상태",
	width: "10%",
	editable : false,
	filter : {
		showIcon : true
	},
},
{
	dataField : "rev",//partVersion.partIteration
	headerText : "버전",
	width: "10%",
	editable : false,
	filter : {
		showIcon : true
	},
},{
	dataField : "unit",
	headerText : "단위",
	width: "10%",
	editable : false,
	filter : {
		showIcon : true
	},
},{
	dataField : "quantity",
	headerText : "수량",
	width: "10%",
	editable : false,
	filter : {
		showIcon : true
	},

},{
	dataField : "isSelect",
	headerText : "Check",
	width: "10%",
	visible : false
}, 
];
$(document).ready(function() {
	var partOid = "<c:out value='${partOid}'/>";
	var moduleType = "<c:out value='${moduleType}'/>";
	$("#partOid").val(partOid);
	$("#moduleType").val(moduleType);
	var desc = $("#desc").val();
	var auiGridProps = {
			
			/********트리 그리드************/
				softRemoveRowMode : false,
				
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
				// 전체 선택 체크박스가 독립적인 역할을 할지 여부
				independentAllCheckBox : true,
				// 계층 구조에서 내 부모 행의 treeIdField 참고 필드명
				treeIdRefField : "parent",
				
				enableSorting : false,
				//isSelect 체크
				rowStyleFunction: function(rowIndex, item) {
					if($.trim(item.isSelect) == "false") { 
						console.log(item.number);
						return "deletedColor";
					}
					return null;
				}, 
				rowCheckableFunction : function(rowIndex, isChecked, item) {
					if($.trim(item.isSelect) == "false") {  //사용자 체크 못하게 함.
						return false;
					}
					return true;
				},
				rowCheckDisabledFunction : function(rowIndex, isChecked, item) {
					if($.trim(item.isSelect) == "false") { 
						return false; // false 반환하면 disabled 처리됨
					}
					return true;
				}
				//fixed 고정
				//fixedColumnCount : 7
		};
		// 실제로 #grid_wrap 에 그리드 생성
		myBOMGridID = AUIGrid.create("#grid_wrap", columnLayout, auiGridProps);
		// 전체 체크박스 클릭 이벤트 바인딩
		AUIGrid.bind(myBOMGridID, "rowAllChkClick", function( event ) {
			if(event.checked) {
				// name 의 값들 얻기
				var uniqueValues = AUIGrid.getColumnDistinctValues(event.pid, "isSelect");
				// Anna 제거하기
				uniqueValues.splice(uniqueValues.indexOf("false"),1);
				AUIGrid.setCheckedRowsByValue(event.pid, "isSelect", uniqueValues);
			} else {
				AUIGrid.setCheckedRowsByValue(event.pid, "isSelect", []);
			}
		});
	partExpandAction(partOid,moduleType,desc);
	
	popupAUIResize();
})

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

$(function() {
	 $("#onSelect").click(function() {
		 var checkedItems = AUIGrid.getCheckedRowItems(myBOMGridID);//AUIGrid.getEditedRowItems(myBOMGridID); getCheckedRowItems
			
			if(checkedItems.length == 0){
				alert("${f:getMessage('수정된 ')}${f:getMessage(' 부품이 없습니다.')}");
				return;
			}else{
				var returnArr = new Array();
				for(var i = 0; i < checkedItems.length; i++){
					var checkedItem = checkedItems[i];
					
					var number = checkedItem.item.number;
					var name = checkedItem.item.name;
					var rowId = checkedItem.item.rowId;
					console.log("rowId="+rowId);
					var tmp = checkedItem.item.rev;
					var data = tmp.split(".");
					var ver = data[0];
					var itr = data[1];
					var changeOid = checkedItem.item.oid;
					var returnStr = changeOid + "†" + number + "†" + name + "†" + ver+ "†" + itr;
					console.log("changeOid="+changeOid+"\tnumber="+number+"\tname="+name+"\tver="+ver+"\titr="+itr);
					returnArr[i] = new Array();
					var array = returnStr.split("†");
					for(var j=0; j<array.length; j++) {
						returnArr[i][j] = array[j];
						console.log("returnArr["+i+"]["+j+"]="+returnArr[i][j]);
					}
					
				}
				opener.parent.addPart(returnArr,true);
				//self.close();
			}
	}); 
    
	$("#desc").change(function() {
		var partOid = $("#partOid").val();
		var moduleType = "<c:out value='${moduleType}'/>";
		partExpandAction(partOid,moduleType,this.value);
	})
})

function partExpandAction(partOid,moduleType,desc) {
	
	$("#expandBody > tr").remove();
	
	var url	= getURLString("part", "partExpandAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{partOid:partOid,moduleType:moduleType, desc:desc} ,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "PartTree ${f:getMessage('목록 오류')}";
			alert(msg);
		},

		success:function(data){
			//addPartExpandData(data);
			var gridData = data;
			
			AUIGrid.setGridData(myBOMGridID, gridData);
			
			var totalDepth = AUIGrid.getTreeTotalDepth(myBOMGridID);
			//setDepthList(totalDepth);
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
	    }
		,complete: function() {
			gfn_EndShowProcessing();
	    }

	});
}


function gotoViewPartTree(partOid) {
	$("#partOid").val(partOid);
	var moduleType = "<c:out value='${moduleType}'/>";
	var desc = $("#desc").val();
	partExpandAction(partOid,moduleType,desc)
}


</script>

<body>

<input type="hidden" id="partOid" name="partOid" >
<input type="hidden" id="moduleType" name="moduleType" >

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="3" >
    <tr align=center>
        <td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					 <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
				   		<tr> 
				   			<td height="30" width="93%" align="center"><B><font color=white>BOM ${f:getMessage('품목')}</font></B></td>
				   		</tr>
					</table>
						<table border="0" cellpadding="1" cellspacing="1" align="right">
			            	<tr>
			                	<td>
			                  		<select name=desc id=desc >
									    <option value="true" >${f:getMessage('정전개')}</option>
										<option value="false">${f:getMessage('역전개')}</option>
									</select>
								</td>
			                 	<td>
			                 		<button type="button" class="btnCustom" id="onSelect">
			                 			<span></span>
			                 			${f:getMessage('선택')}
			                 		</button>
			                 	<td>
			                 		<button type="button" class="btnClose" onclick="self.close();">
			                 			<span></span>
			                 			${f:getMessage('닫기')}
			                 		</button>
			                 	</td>
			             	</tr>
			        	</table>
					</td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="3" >
				<tr align=center>
					<td valign="top" style="padding:0px 0px 0px 0px">
			
						<%-- <table width="100%"  border="0" cellpadding="1" cellspacing="0"  class="tablehead" align=center>
						    <tr><td height=1 width=100%></td></tr>
						</table>
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center  >
						    <tr height=25>
						    	<td class="tdblueM" width="5%"><input name="chkAll" id="chkAll" type="checkbox" class="Checkbox"></td>
						        <td class="tdblueM" width="25%">${f:getMessage('품목번호')}</td>
								<td class="tdblueM" width="4%">level</td>       
						        <td class="tdblueM" width="4%">&nbsp;</td>
						        <td class="tdblueM" width="21%">${f:getMessage('품목명')}</td>
						        <td class="tdblueM" width="5%">${f:getMessage('상태')}</td>
						        <td class="tdblueM" width="8%">${f:getMessage('Rev.')}</td>
						        <td class="tdblueM" width="5%">${f:getMessage('단위')}</td>
						        <td class="tdblueM" width="5%">${f:getMessage('수량')}</td>
						    </tr>
						    
						    <tbody id="expandBody">
						    
						    </tbody>
						    
					    </table> --%>
					    <div id="grid_wrap" style="width: 100%; height:500px; margin-top: 2px; border-top: 3px solid #244b9c;">
						</div>
			    	</td>
			    </tr>
			</table>
				 
		</td>
	</tr>
</table>

</body>
</html>