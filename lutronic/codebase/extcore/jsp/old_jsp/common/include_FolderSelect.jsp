<%@page contentType="text/html; charset=UTF-8"%>

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript">

<%----------------------------------------------------------
*                      item 선택시 데이터 넘기고 폴더구조 숨기기
----------------------------------------------------------%>
function selectDocumentTreeItem( oid , plocation ,isLast) {
	$("#id_folder").val(oid);
	$("#id_location").val(plocation);
	$("#treedocument").hide();
	if($("#isLast").val() != null) $("#isLast").val(isLast);
}

<%----------------------------------------------------------
*                     페이지 초기시 tree 보이기/숨기기
----------------------------------------------------------%>
function showhideDocumentTree() {
	
	var a = $("#ssssssss").offset()
	$("div#treedocument").css({"left":(a.left+$("#ssssssss").width())+"px"});
    $("div#treedocument").css({"top":(a.top)+"px"});
    
	if ($("#treedocument").is(":visible")) {
		$("#treedocument").hide();
	}else {
		
		
		$("#treedocument").show();
	}
}

<%----------------------------------------------------------
*                      Tree Clear 하는 듯.. 사용 하지 않는거 같음.
----------------------------------------------------------%>
function clearDocumentTextInclude() {
	$("#id_folder").val("");
	$("#id_location").val("");
}
</script>
<link rel="StyleSheet" href="/Windchill/jsp/css/dtree.css" type="text/css" />

<input id="id_location" name="location" type="text" class="txt_field" size="35" style="width:70%" value="<c:out value="${location }" />" readonly>

<input id="id_folder" name="${paramName}" type="hidden"  value="<c:out value="${oid }" />" readonly>

<a href="javascript:showhideDocumentTree();">
	<img src="/Windchill/jsp/portal/images/s_search.gif" border=0 id="ssssssss">
</a>

<div id=treedocument style="display:none;; position:absolute; background-color:#FFFFFF;border :1px solid Silver; left:78px; top:165px; width:230px; height:350px; overflow:auto; z-index:1; border-width:1px; border-style:none; filter:progid:DXImageTransform.Microsoft.Shadow(color=#4B4B4B,Direction=135,Strength=3);border-color:black;">

	<script type="text/javascript" src="/Windchill/jsp/js/dtree.js"></script>
	<script type="text/javascript">
		var d = new dTree('d','/Windchill/jsp/part/images/tree');
		
		<c:forEach items="${list}" var="list">
		
		d.add("<c:out value='${list.id}' />","<c:out value='${list.level}'/>","<c:out value='${list.name}'  escapeXml='false'/>","JavaScript:selectDocumentTreeItem('<c:out value='${list.oid}'/>','<c:out value='${list.location}'/>','<c:out value='${list.isLast}'/>')","<c:out value='${list.s1}'/>");
		
		</c:forEach>
		
		document.write(d);
	</script>
</div>