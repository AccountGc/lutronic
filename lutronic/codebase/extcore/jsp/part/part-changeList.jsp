<%@page import="wt.session.SessionHelper"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.part.dto.PartData"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%@page import="net.sf.json.JSONArray"%>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
PartData data = (PartData) request.getAttribute("data");
WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				품목 변경이력
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="javascript:self.close();">
		</td>
	</tr>
</table>
<div id="grid_partChange" style="height: 645px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let partChangeGridID;
	const columnPartChange = [ {
		dataField : "model",
		headerText : "모델",
		dataType : "string",
		width : 180,
	}, {
		headerText : "변경이력",
		children : [{
			dataField : "version",
			headerText : "REV",
			dataType : "string",
			width: 120,
		}, {
			dataField : "number",
			headerText : "품번",
			dataType : "string",
			width: 180,
		}, {
			dataField : "name",
			headerText : "품명",
			dataType : "string",
			width: 230,
		}]
	}, {
		dataField : "eoNumber",
		headerText : "ECO NO.",
		dataType : "string",
		width : 180,
	}, {
		headerText : "변경 적용 시점",
		children : [{
			dataField : "ko",
			headerText : "국내",
			dataType : "string",
			width: 180,
		}, {
			dataField : "usa",
			headerText : "미국",
			dataType : "string",
			width: 180,
		}, {
			dataField : "aue",
			headerText : "호주",
			dataType : "string",
			width: 180,
		}, {
			dataField : "brazil",
			headerText : "브라질",
			dataType : "string",
			width: 180,
		}, {
			dataField : "saudi",
			headerText : "사우디",
			dataType : "string",
			width: 180,
		}, {
			dataField : "canada",
			headerText : "캐나다",
			dataType : "string",
			width: 180,
		}, {
			dataField : "intl",
			headerText : "INT",
			dataType : "string",
			width: 180
		}]
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGridPartChange(columnLayout) {
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
		partChangeGridID = AUIGrid.create("#grid_partChange", columnLayout, props);
		
		var json = [{
		    "model" : "A_MODEL",
		    "version" : "A.3",
		    "number" : 1010100100,
		    "name" : "MODULE_BOARD_LD DRIVER",
		    "eoNumber" : "C2308001",
		    "ko" : "2022-01-10",
		    "usa" : "2022-01-12",
		    "aue" : "2022-01-20",
		    "brazil" : "2022-01-14",
		    "saudi" : "2022-02-10",
		    "canada" : "2022-01-30",
		    "intl" : "2022-01-22",
		}, {
			"model" : "B_MODEL",
		    "version" : "A.5",
		    "number" : 1010100101,
		    "name" : "MODULE_BOARD_LD DRIVER1",
		    "eoNumber" : "C2308002",
		    "ko" : "2022-05-01",
		    "usa" : "2022-05-11",
		    "aue" : "2022-05-11",
		    "brazil" : "2022-05-20",
		    "saudi" : "2022-05-08",
		    "canada" : "2022-05-15",
		    "intl" : "2022-05-01",
		}, {
			"model" : "B_MODEL",
		    "version" : "B.1",
		    "number" : 1010100102,
		    "name" : "MODULE_BOARD_LD DRIVER2",
		    "eoNumber" : "C2308003",
		    "ko" : "",
		    "usa" : "2023-02-09",
		    "aue" : "2023-02-10",
		    "brazil" : "2023-02-15",
		    "saudi" : "2023-03-07",
		    "canada" : "2023-02-26",
		    "intl" : "2023-02-11",
		}, {
			"model" : "D_MODEL",
		    "version" : "D.6",
		    "number" : 1010100103,
		    "name" : "MODULE_BOARD_LD DRIVER3",
		    "eoNumber" : "C2308004",
		    "ko" : "",
		    "usa" : "2023-05-12",
		    "aue" : "2023-05-13",
		    "brazil" : "2023-05-11",
		    "saudi" : "2023-05-17",
		    "canada" : "2023-05-18",
		    "intl" : "2023-05-20",
		}]
		AUIGrid.setGridData(partChangeGridID, json);
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGridPartChange(columnPartChange);
		AUIGrid.resize(partChangeGridID);
	});

</script>