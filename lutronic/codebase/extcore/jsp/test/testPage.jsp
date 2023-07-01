<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>

<script type="text/javascript" src="/Windchill/jsp/js/dhtmlx/dhtmlx.js" ></script>

<script type="text/javascript">
$(document).ready(function() {
	lfn_DhtmlxGridInit();
	search();
})


<%----------------------------------------------------------
*                      그리드 초기 설정
----------------------------------------------------------%>
window.lfn_DhtmlxGridInit = function() {
	
	var COLNAMEARR = {
			 col01 : '${f:getMessage('번호')}'
			,col02 : '${f:getMessage('문서')}${f:getMessage('번호')}'
			,col03 : '${f:getMessage('문서명')}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";

	var sWidth = "";
	sWidth += "30";
	sWidth += ",30";
	sWidth += ",40";
	
	var sColAlign = "";
	sColAlign += "left";
	sColAlign += ",center";
	sColAlign += ",left";
	
	var sColType = "";
	sColType += "tree";
	sColType += ",ro";
	sColType += ",ro";

	var sColSorting = "";
	sColSorting += "na";
	sColSorting += ",na";
	sColSorting += ",na";
	
	documentListGrid = new dhtmlXGridObject('listGridBox');
	documentListGrid.setImagePath("/Windchill/jsp/js/dhtmlx/imgs/");
	documentListGrid.setHeader(sHeader,null,sHeaderAlign);
	documentListGrid.enableAutoHeight(true);
	documentListGrid.setInitWidthsP(sWidth);
	documentListGrid.setColAlign(sColAlign);
	documentListGrid.setColTypes(sColType);
	documentListGrid.setColSorting(sColSorting);
	documentListGrid.init();

	documentListGrid.attachEvent("onRowDblClicked",function(rowId,cellIndex){
		var z = documentListGrid.getSelectedId(); 
		
		// 자식이 몇개 있는지 검사
		documentListGrid.hasChildren(z)
		
		// Id로 cell 검사
		var cell = documentListGrid.cellById(z, 0);
		
		// 하위 ID 리스트 가져오기
		var subItems = documentListGrid.getSubItems(z);
		
		searchChildNode(z);
	})
	
}

window.search = function(){
	var form = $("form[name=testPage]").serialize();
	var url	= getURLString("test", "testPageAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			documentListGrid.clearAll();
			documentListGrid.loadXMLString(data);
			
			$("#xmlString").val(data);
			
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	});
}

window.searchChildNode = function(id) {
	var url	= getURLString("test", "testPageChildAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{
			parentOid : id
		},
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			addChildData(id,data);
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	});
}

window.addChildData = function(parentId, data) {
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			if(duplicationCheck(parentId, data[i].oid)) {
				documentListGrid.addRow(data[i].oid,[data[i].name, data[i].code, data[i].oid], 0, parentId);
			}
		}
		documentListGrid.openItem(parentId);
	}else {
		alert('하위가 없습니다.');
	}
}

window.duplicationCheck = function(parnetId, oid) {
	var subItems = documentListGrid.getSubItems(parnetId);
	var subItem = subItems.split(',');
	
	for(var i=0; i<subItem.length; i++){
		if(subItem[i] == oid) {
			return false;
		}
	}
	return true;
}

$(function() {
	$('#add').click(function() {
		/**
		
		자식 추가
		void addRow(number/string new_id,
				array text ,
				number ind,
				number/string parent_id,
				string img,
				boolean child);
		new_id number/string new row id 
		text  array an array of cell labels in a row 
		ind number position of a row (set to null, for using parentId) 
		parent_id number/string id of the parent row 
		img string img url for new row 
		child boolean child flag (optional) 
		
		*/
		documentListGrid.addRow((new Date()).valueOf(),['new row','text','text'],0,0);
		
		//documentListGrid.getOpenState(checked);
		//documentListGrid.openItem(z);
	})
})

</script>

<body>

<br>
<br>
<br>

<form name='testPage' id='testPage'>

<!-- 서브 메뉴 닫을때 dhtmlx를 다시 그리기 위한 변수 -->
<input type="text" name="xmlString" id="xmlString" value="" />

<input type="button" name='add'	 id='add' value='Add'>

<div id="listGridBox" style="width:100%;" >
</div>

</form>

</body>
</html>