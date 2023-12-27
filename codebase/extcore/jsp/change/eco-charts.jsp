<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> ECO
			</div>
		</td>
	</tr>
</table>
<div class="left">
	<div id="eco_barChart"></div>
</div>
<div class="right">
	<div id="eco_pieChart"></div>
</div>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script type="text/javascript">

	//Create the bar chart
	Highcharts.chart('eco_barChart', {
 		chart: {
     		type: 'column'
 		},
		title: {
		    align: 'left',
		    text: '변경사유'
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
	                y: 43.06,
	            },
	            {
	                name: '영업/마케팅',
	                y: 29.84,
	            },
	            {
	                name: '기능/성능 변경',
	                y: 4.18,
	            },
	            {
	                name: '공정 변경',
	                y: 23.12,
	            },
	            {
	                name: '자재 변경',
	                y: 32.33,
	            },
	            {
	                name: '허가/규제 변경',
	                y: 30.45,
	            },
	            {
	                name: '품질',
	                y: 25.45,
	            },
	            {
	                name: '라벨링',
	                y: 48.45,
	            },
	            {
	                name: '기타',
	                y: 14.582,
	            }
	        ]
	    }]
	});
	
	//Create the pie chart
	Highcharts.chart('eco_pieChart', {
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
	            y: 28.67,
	            sliced: true,
	            selected: true
	        }, {
	            name: '제조',
	            y: 21.77
	        },  {
	            name: '생산',
	            y: 42.86
	        }, {
	            name: '기타',
	            y: 9.63
	        }]
	    }]
	});

</script>