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

		<div>
    <div class="left">
    	<div id="barChart"></div>
    </div>
    <div class="right">
    	<div id="pieChart"></div>
    </div>
</div>

<script src="https://code.highcharts.com/highcharts.js"></script>
<script type="text/javascript">

	//Create the bar chart
	Highcharts.chart('barChart', {
 		chart: {
     		type: 'column'
 		},
		title: {
		    align: 'left',
		    text: 'ECO 변경사유'
		},
//  subtitle: {
//      align: 'left',
//      text: 'Click the columns to view versions. Source: <a href="http://statcounter.com" target="_blank">statcounter.com</a>'
//  },
		accessibility: {
		    announceNewData: {
		        enabled: true
		    }
		},
		xAxis: {
		    type: 'category'
		},
		yAxis: {
		    title: {
//          text: 'Total percent market share'
			}

		},
		legend: {
		    enabled: false
		},
		plotOptions: {
		    series: {
		        borderWidth: 0,
		        dataLabels: {
		            enabled: true,
		            format: '{point.y:.1f}%'
		        }
		    }
		},

		tooltip: {
		    headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
		    pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
		},

		series: [{
	        name: '변경사유',
	        colorByPoint: true,
	        data: [
	            {
	                name: '원가 절감',
	                y: 63.06,
	            },
	            {
	                name: '영업/마케팅',
	                y: 19.84,
	            },
	            {
	                name: '기능/성능 변경',
	                y: 4.18,
	            },
	            {
	                name: '공정 변경',
	                y: 10.12,
	            },
	            {
	                name: '자재 변경',
	                y: 22.33,
	            },
	            {
	                name: '허가/규제 변경',
	                y: 30.45,
	            },
	            {
	                name: '품질',
	                y: 40.45,
	            },
	            {
	                name: '라벨링',
	                y: 10.45,
	            },
	            {
	                name: '기타',
	                y: 5.582,
	            }
	        ]
	    }]
	});
	
	//Create the pie chart
	Highcharts.chart('pieChart', {
	    chart: {
	        plotBackgroundColor: null,
	        plotBorderWidth: null,
	        plotShadow: false,
	        type: 'pie'
	    },
	    title: {
	        text: '작성부서',
	        align: 'left'
	    },
	    tooltip: {
		    headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
		    pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b><br/>'
		},
// 	    tooltip: {
// 	        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
// 	    },
	    accessibility: {
	        point: {
	            valueSuffix: '%'
	        }
	    },
	    plotOptions: {
	        pie: {
	            allowPointSelect: true,
	            cursor: 'pointer',
	            dataLabels: {
	                enabled: true,
	                format: '<b>{point.name}</b>: {point.percentage:.1f} %'
	            }
	        }
	    },
	    series: [{
	        name: '작성부서',
	        colorByPoint: true,
	        data: [{
	            name: '연구소',
	            y: 70.67,
	            sliced: true,
	            selected: true
	        }, {
	            name: '제조',
	            y: 14.77
	        },  {
	            name: '생산',
	            y: 4.86
	        }, {
	            name: '기타',
	            y: 2.63
	        }]
	    }]
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