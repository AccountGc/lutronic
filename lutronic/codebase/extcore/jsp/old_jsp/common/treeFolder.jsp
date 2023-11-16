<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script>
var deptTree;
var bodyClick;
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	lfn_SearchFolderTree();
});

<%----------------------------------------------------------
*                      TextArea 정보로 Tree 그리기
----------------------------------------------------------%>
function lfn_SearchFolderTree(){
    folderTree  =  new dhtmlXTreeObject("folderTreeList","100%","100%",0);
    folderTree.setOnClickHandler(lfn_FolderTreeOnClick);
    folderTree.setImagePath("/Windchill/jsp/js/dhtmlx/imgs/dhxtree_skyblue/");
    folderTree.deleteChildItems(0);
    eval('var treeData='+$("#folderTreeData").val());
    folderTree.loadJSONObject(treeData);
    folderTree.sortTree(1,'ASC',true);
    folderTree.openItem(1);
    var childSize    = folderTree.getAttribute('1', "childSize");
    for(var i =0; i < childSize; i ++){
    	var childId = folderTree.getChildItemIdByIndex('1',i);
    	var childchildSize = folderTree.getAttribute(childId, "childSize");
		if(childchildSize>0){
	    	folderTree.insertNewChild(childId,"123",0,0,0,0,0,"SELECT");
	    	folderTree.closeAllItems(childId);
			folderTree.deleteChildItems(childId);
		}
    }
    
    folderTree.attachEvent("onOpenEnd",function(id,state){
    	if(bodyClick == 'OK'){
    		return;
    	}
		folderTree.selectItem(id);
		var oid    = folderTree.getAttribute(id, "oid");
		var isOpen    = folderTree.getAttribute(id, "isOpen");
		
		if(!isOpen){
			Search(oid,id);
		}else if(isOpen == 'true'){
			folderTree.setAttribute(id, "isOpen","false");
			folderTree.closeItem(id);
		}else{
			folderTree.setAttribute(id, "isOpen","true");
			folderTree.openItem(id);
		}
		
		bodyClick ="";
		
	})
    
    
}


function  Search(oid,id){
	var url	= getURLString("folder", "getNextTree", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {oid : oid},
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "데이터 검색오류";
			alert(msg);
		},

		success:function(data){
			var selected = id;
			add(data,id);
		}
	});
}

function add(data,selected) {
	var arr = [];
	for(var i = 0 ; i < data.length; i ++ ){
		var value = data[i].text;
		var newID = data[i].id;
		folderTree.setAttribute(selected,"isOpen","true");
		folderTree.insertNewChild(selected,newID,value,0,0,0,0,"SELECT");
		folderTree.setAttribute(newID,"oid",data[i].oid);
		folderTree.setAttribute(newID,"level",data[i].level);
		folderTree.setAttribute(newID,"location",data[i].location);
		folderTree.setAttribute(newID,"parenOid",data[i].parenOid);
		folderTree.setAttribute(newID,"childSize",data[i].childSize);
		folderTree.setAttribute(newID,"text",value);
		if(data[i].childSize>0){
			arr.push(newID);
		}
	}
	folderTree.sortTree(selected,'ASC',true);
	folderTree.openItem(selected);
	for(var i = 0 ; i < arr.length; i ++ ){
		folderTree.insertNewChild(arr[i],"123",0,0,0,0,0,"SELECT");
		folderTree.closeAllItems(arr[i]);
		folderTree.deleteChildItems(arr[i]);
	}
}


<%----------------------------------------------------------
*                      Tree 선택시 location,oid 넘김
----------------------------------------------------------%>
function lfn_FolderTreeOnClick(id){
	bodyClick = 'OK';
	folderTree.selectItem(id);
	var location   = folderTree.getAttribute(id, "location");
	var oid    = folderTree.getAttribute(id, "oid");
	var isOpen    = folderTree.getAttribute(id, "isOpen");
	var childSize    = folderTree.getAttribute(id, "childSize");
	if(!isOpen){ //open 한적이 있다면 추가 X
		Search(oid,id);
	}else if(isOpen == 'true'){
		folderTree.setAttribute(id, "isOpen","false");
		folderTree.closeItem(id);
	}else{
		folderTree.setAttribute(id, "isOpen","true");
		folderTree.openItem(id);
	}
	setLocationDocument(oid,location,true);
	bodyClick = '';
}
</script>


<table width="100%" height="40" border="0" cellpadding="0" cellspacing="0" bgcolor="ffffff" style="margin:0px 0px 10px 0px">
	<tr>
		<td align="left">
			<div id="folderTreeList" class="t_ln" style="width:210px; height:510px; overflow: auto; "></div>
			<textarea  id="folderTreeData" style="display: none;" >${treeString}</textarea >
		</td>
	</tr>
</table>
