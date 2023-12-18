<%@page import="com.e3ps.change.service.ECOHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray json = ECOHelper.manager.getCompletePartList(oid);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 완제품 품목
			</div>
		</td>
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_CompletePartViewToggle" alt='include_CompletePartView' >
		</td>
	</tr>
</table>
<div id="grid_complePart" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let complePartGridID;
	const columnComplePart = [ {
		dataField : "eoNumber",
		headerText : "품목번호",
		dataType : "string",
		width : 180,
	}, {
		dataField : "eoName",
		headerText : "품목명",
		dataType : "string",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/project/info?oid=" + oid);
				popup(url);
			}
		},
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 180,
	}, {
		dataField : "creator",
		headerText : "등록자",
		dataType : "string",
		width : 180,
	}, {
		dataField : "createDate",
		headerText : "등록일",
		dataType : "string",
		width : 180,
	}, {
		dataField : "",
		headerText : "BOM",
		dataType : "string",
		width : 180,
	}, {
		dataField : "",
		headerText : "도면",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid2(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			rowCheckToRadio : true,
			fillColumnSizeMode: true,
		}
		complePartGridID = AUIGrid.create("#grid_complePart", columnLayout, props);
		AUIGrid.setGridData(complePartGridID, <%=json%>);
	}
	
	$('img[name=linkDelete]').click(function() {
		if (confirm("$삭제하시겠습니까?")){
			
			var url	= getURLString("changeECO", "deleteCompletePartAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: {
					linkOid : this.id,
				},
				async: true,
				cache: false,
				error: function(data) {
					alert("완제품 삭제시 오류 발생");
				},
				success:function(data){
					
					if(data.result) {
						alert(data.message)
						location.reload();
					}else {
						alert("삭제 실패하였습니다." + data.message);
					}
				}
			});
			
		}
	})
	
	//구성원 접기/펼치기
	$("#include_CompletePartViewToggle").click(function() {
		var divId = $(this).attr('alt');
		if ( $( "#" + divId ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#" + divId).slideToggle();
	})

</script>