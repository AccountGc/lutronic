<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script type="text/javascript"> 
var dpartTree;
var codeType;
var codeTypeName;
$(document).ready(function() {
	codeType = "<c:out value='${codeType}'/>";
	codeTypeName = "<c:out value='${codeTypeName}'/>";
	var url	= getURLString("admin", "admin_numberCodeTreeAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{codeType: codeType},
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "Tree ${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			dpartTree = new dTree('dpartTree', '/Windchill/jsp/part/images/tree');
			
			for(var i=0; i<data.length; i++) {
				
				
				if(i==0) {
					dpartTree.add(data[i].codeType, '-1', codeTypeName+"", "JavaScript:gotoView('', '');");
				}
				
				if(data[i].isParent == "true") {
					dpartTree.add(data[i].id, data[i].parent, "["+data[i].code+"]"+data[i].name, "JavaScript:gotoView('" + data[i].id + "', '" + data[i].name + "');", data[i].id);
				}else if(data[i].isParent == "false") {
					dpartTree.add(data[i].id, data[i].codeType, "["+data[i].code+"]"+data[i].name, "JavaScript:gotoView('" + data[i].id + "', '" + data[i].name + "');", data[i].id);
				}
					
			}
			$("#codeTree").html(dpartTree.toString());
		}
	});
	
	$("#iframe1").attr("src", getURLString("admin", "admin_numberCodeList", "do") + "?codeType="+ codeType);
})

function gotoView(oid,name) {
	
	$("#iframe1").attr("src", getURLString("admin", "admin_numberCodeList", "do")+ "?codeType="+codeType+"&parentOid=" + oid+"&title="+name);
}

</script>

<table width="100%" height="99%" border="0" cellpadding="0" cellspacing="0" bgcolor="ffffff" style="margin:0px 0px 0px 0px">
	<tr>
    	<td width="19%" valign="top" id="codeTree">
    		
    	</td>
        
   		<td width="80%" valign="top">
			<iframe frameborder=1 width="99%" height="698px" name=iframe1 id=iframe1 scrolling=auto >
			</iframe>
		</td>
	</tr>
</table>
                