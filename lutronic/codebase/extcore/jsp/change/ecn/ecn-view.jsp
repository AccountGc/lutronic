<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.ecn.dto.EcnDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcnDTO dto = (EcnDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
ArrayList<Map<String, String>> list = NumberCodeHelper.manager.getCountry();
%>

<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECN 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="Erp전송" title="Erp전송" class="blue" onclick="modify();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_ecn" style="height: 740px; border-top: 1px solid #3180c3; margin: 5px;"></div>

<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "품목명",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "number",
		headerText : "품목번호",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		headerText : "확정 인허가일",
		children : [ {
			<%
			int i=1;
			for(Map<String,String> map : list){
			%>	
				dataField : "<%=map.get("code")%>",
				headerText : "<%=map.get("name")%>",
				dataType : "string",
				filter : {
					showIcon : true,
					inline : true
				},
			<%if(i!=list.size()){%>
			}, {
			<%}
				i++;
			}
			%>
		}]
	} ]
	
	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			enableFilter : true,
			autoGridHeight : true,
			wordWrap : true,
		}
		myGridID = AUIGrid.create("#grid_ecn", columnLayout, props);
// 		AUIGrid.setGridData(myGridID,"");
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});
	
	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/ecn/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				clsoeLayer();
			}
		}, "DELETE");
	}
</script>