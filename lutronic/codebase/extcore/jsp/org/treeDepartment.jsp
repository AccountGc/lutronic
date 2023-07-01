<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
	lfn_SearchDeptTree();
});

<%----------------------------------------------------------
*                      부서 트리구성
----------------------------------------------------------%>
function lfn_SearchDeptTree(){
    deptTree  =  new dhtmlXTreeObject("deptTreeList","100%","100%",0);
    deptTree.setOnClickHandler(lfn_DeptTreeOnClick);
    deptTree.setImagePath("/Windchill/jsp/js/dhtmlx/imgs/dhxtree_skyblue/");
    deptTree.deleteChildItems(0);
    eval('var treeData='+$("#deptTreeData").val());
    deptTree.loadJSONObject(treeData);
    deptTree.openAllItems(0);
}

</script>


<table width="100%" height="40" border="0" cellpadding="0" cellspacing="0" bgcolor="ffffff" style="margin:0px 0px 10px 0px">
	<tr>
		<td>
			<div id="deptTreeList" class="t_ln" style="width: 100%; height: 400px; overflow: auto"></div>
			<textarea  id="deptTreeData" style="display:none" ><c:out value="${json }" /></textarea >
		</td>
	</tr>
</table>
