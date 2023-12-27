<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> EO
			</div>
		</td>
	</tr>
</table>
<div class="left">
	<div id="eo_barChart"></div>
</div>
<div class="right">
	<div id="eo_pieChart"></div>
</div>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script type="text/javascript">

	//Create the bar chart
	Highcharts.chart('eo_barChart', {
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
	                y: 23.06,
	            },
	            {
	                name: '영업/마케팅',
	                y: 39.84,
	            },
	            {
	                name: '기능/성능 변경',
	                y: 14.18,
	            },
	            {
	                name: '공정 변경',
	                y: 40.12,
	            },
	            {
	                name: '자재 변경',
	                y: 12.33,
	            },
	            {
	                name: '허가/규제 변경',
	                y: 20.45,
	            },
	            {
	                name: '품질',
	                y: 20.45,
	            },
	            {
	                name: '라벨링',
	                y: 30.45,
	            },
	            {
	                name: '기타',
	                y: 25.582,
	            }
	        ]
	    }]
	});
	
	//Create the pie chart
	Highcharts.chart('eo_pieChart', {
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
	            y: 30.67,
	            sliced: true,
	            selected: true
	        }, {
	            name: '제조',
	            y: 54.77
	        },  {
	            name: '생산',
	            y: 14.86
	        }, {
	            name: '기타',
	            y: 12.63
	        }]
	    }]
	});

</script>