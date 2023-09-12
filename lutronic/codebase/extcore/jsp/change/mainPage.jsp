<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
	<form>
		<!-- 설계변경 진행상황 (CR 기준) -->
		<jsp:include page="/extcore/jsp/change/cr-progress.jsp"></jsp:include>
		
		<!-- 설계변경 진행상황 (EO 기준) -->
		<jsp:include page="/extcore/jsp/change/eo-progress.jsp"></jsp:include>
		
		<input type="button" value="차트 열기/닫기" title="차트 열기/닫기" id="chartBtn">
		
		<div id="chartDiv" style="display: none;">
			<div>
			    <div class="left">
			    	<jsp:include page="/extcore/jsp/change/cr-charts.jsp"></jsp:include>
			    </div>
			    <div class="right">
			    	<jsp:include page="/extcore/jsp/change/eo-charts.jsp"></jsp:include>
			    </div>
			</div>
			<div>
			    <div class="left">
			    	<jsp:include page="/extcore/jsp/change/eco-charts.jsp"></jsp:include>
			    </div>
			    <div class="right">
			    	<jsp:include page="/extcore/jsp/change/ecn-charts.jsp"></jsp:include>
			    </div>
			</div>
		</div>	
		
		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				createAUIGridCR(columnCR);
				AUIGrid.resize(crGridID);
				createAUIGridEO(columnEO);
				AUIGrid.resize(eoGridID);
			});
			
			// 차트 열기/닫기
			var isChartExpanded = false;
			$("#chartBtn").click(function() {
				if (!isChartExpanded) {
					$("#chartDiv").show();
					isChartExpanded = true;
				} else {
					$("#chartDiv").hide();
					isChartExpanded = false;
				}
			});
			
		</script>
		<style>
		    div.left {
		        width: 50%;
		        float: left;
		        box-sizing: border-box;
		        
		    }
		    div.right {
		        width: 50%;
		        float: right;
		        box-sizing: border-box;
		        
		    }
		</style>
	</form>
</body>
</html>