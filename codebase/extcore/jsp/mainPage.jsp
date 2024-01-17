<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="wt.fc.PagingSessionHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.e3ps.groupware.notice.Notice"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Map<String, ArrayList<Map<String, Integer>>> dataMap = (Map<String, ArrayList<Map<String, Integer>>>) request
		.getAttribute("dataMap");
String start = (String) request.getAttribute("start");
ArrayList<Map<String, Integer>> complete = (ArrayList<Map<String, Integer>>) dataMap.get("complete");
ArrayList<Map<String, Integer>> progress = (ArrayList<Map<String, Integer>>) dataMap.get("progress");
Map<String, Integer> drill = (Map<String, Integer>) request.getAttribute("drill");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<style type="text/css">
</style>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%@include file="/extcore/jsp/common/highchart.jsp"%>
</head>
<body>
	<form>
		<!-- 결재 및 공지사항 -->
		<table style="height: 400px;">
			<colgroup>
				<col width="49%">
				<col width="30px;">
				<col width="49%">
			</colgroup>
			<tr>
				<td valign="top">
					<table>
						<tr>
							<td class="left">
								<img style="position: relative; top: 5px; right: 14px;" src="/Windchill/extcore/images/approval.jpg">
							</td>
						</tr>
					</table>
				</td>
				<td valign="top">&nbsp;</td>
				<td class="left" valign="top">
					<table>
						<tr>
							<td class="left">
								<img style="position: relative; top: 5px; right: 14px;" src="/Windchill/extcore/images/notice.jpg">
								<!-- 								<div style="float: right; position: relative; top: 23px; right: 20px;"> -->
								<!-- 									<b>+ 더보기</b> -->
								<!-- 								</div> -->
							</td>
						</tr>
						<tr>
							<td style="border: 2px solid #86bff9; height: 250px;" valign="top">
								<table>
									<colgroup>
										<col width="70px;">
										<col width="*">
										<col width="100px;">
										<col width="120px;">
									</colgroup>
									<tr>
										<th style="text-align: center; padding: 5px;">
											번호
											<div style="border-top: 1px solid black; margin-top: 6px;"></div>
										</th>
										<th style="text-align: center; padding: 5px;">
											제목
											<div style="border-top: 1px solid black; margin-top: 6px;"></div>
										</th>
										<th style="text-align: center; padding: 5px;">
											등록자
											<div style="border-top: 1px solid black; margin-top: 6px;"></div>
										</th>
										<th style="text-align: center; padding: 5px;">
											등록일
											<div style="border-top: 1px solid black; margin-top: 6px;"></div>
										</th>
									</tr>
									<%
									QuerySpec query = new QuerySpec();
									int idx = query.appendClassList(Notice.class, true);
									QuerySpecUtils.toOrderBy(query, idx, Notice.class, Notice.CREATE_TIMESTAMP, true);
									QueryResult rs = PagingSessionHelper.openPagingSession(0, 10, query);
									int k = 0;
									while (rs.hasMoreElements()) {
										Object[] obj = (Object[]) rs.nextElement();
										Notice n = (Notice) obj[0];
										String oid = n.getPersistInfo().getObjectIdentifier().getStringValue();
									%>
									<tr>
										<td style="text-align: center; padding: 5px;"><%=++k%></td>
										<td style="text-align: left; text-indent: 10px; padding: 5px;">
											<a href="javascript:view('<%=oid%>');"><%=n.getTitle()%></a>
										</td>
										<td style="text-align: center; padding: 5px;"><%=n.getOwner().getFullName()%></td>
										<td style="text-align: center; padding: 5px;"><%=n.getCreateTimestamp().toString().substring(0, 10)%></td>
									</tr>
									<%
									}
									%>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>


		<!-- 차트 -->
		<table>
			<colgroup>
				<col width="50%">
				<col width="50%">
			</colgroup>
			<tr>
				<td>
					<div id="container1"></div>
					<script type="text/javascript">
					Highcharts.chart('container1', {
					    chart: {
					        type: 'column'
					    },
					    title: {
					        align: 'left',
					        text: 'CR 변경사유 별 현황',
					    },
					    xAxis: {
					        type: 'category'
					    },
					    yAxis: {
					        title: {
					            text: 'CR 변경사유'
					        }
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
					    series: [
					        {
					            name: '변경사유',
					            colorByPoint: true,
					            data: [
					            	<%
					            		Iterator it = drill.keySet().iterator();
					            		while(it.hasNext()) {
					            			String key = (String)it.next();
					            			int value = drill.get(key);
					            	%>
					                {
					                    name: '<%=key%>',
					                    y: <%=value%>,
					                    drilldown: '<%=key%>'
					                },
					            	<%
					            		}
					            	%>
					            ]
					        }
					    ],
					    drilldown: {
					        breadcrumbs: {
					            position: {
					                align: 'right'
					            }
					        },
					        series: [
					            {
					                name: 'Chrome',
					                id: 'Chrome',
					                data: [
					                    [
					                        'v65.0',
					                        0.1
					                    ],
					                    [
					                        'v64.0',
					                        1.3
					                    ],
					                    [
					                        'v63.0',
					                        53.02
					                    ],
					                    [
					                        'v62.0',
					                        1.4
					                    ],
					                    [
					                        'v61.0',
					                        0.88
					                    ],
					                    [
					                        'v60.0',
					                        0.56
					                    ],
					                    [
					                        'v59.0',
					                        0.45
					                    ],
					                    [
					                        'v58.0',
					                        0.49
					                    ],
					                    [
					                        'v57.0',
					                        0.32
					                    ],
					                    [
					                        'v56.0',
					                        0.29
					                    ],
					                    [
					                        'v55.0',
					                        0.79
					                    ],
					                    [
					                        'v54.0',
					                        0.18
					                    ],
					                    [
					                        'v51.0',
					                        0.13
					                    ],
					                    [
					                        'v49.0',
					                        2.16
					                    ],
					                    [
					                        'v48.0',
					                        0.13
					                    ],
					                    [
					                        'v47.0',
					                        0.11
					                    ],
					                    [
					                        'v43.0',
					                        0.17
					                    ],
					                    [
					                        'v29.0',
					                        0.26
					                    ]
					                ]
					            },
					            {
					                name: 'Firefox',
					                id: 'Firefox',
					                data: [
					                    [
					                        'v58.0',
					                        1.02
					                    ],
					                    [
					                        'v57.0',
					                        7.36
					                    ],
					                    [
					                        'v56.0',
					                        0.35
					                    ],
					                    [
					                        'v55.0',
					                        0.11
					                    ],
					                    [
					                        'v54.0',
					                        0.1
					                    ],
					                    [
					                        'v52.0',
					                        0.95
					                    ],
					                    [
					                        'v51.0',
					                        0.15
					                    ],
					                    [
					                        'v50.0',
					                        0.1
					                    ],
					                    [
					                        'v48.0',
					                        0.31
					                    ],
					                    [
					                        'v47.0',
					                        0.12
					                    ]
					                ]
					            },
					            {
					                name: 'Internet Explorer',
					                id: 'Internet Explorer',
					                data: [
					                    [
					                        'v11.0',
					                        6.2
					                    ],
					                    [
					                        'v10.0',
					                        0.29
					                    ],
					                    [
					                        'v9.0',
					                        0.27
					                    ],
					                    [
					                        'v8.0',
					                        0.47
					                    ]
					                ]
					            },
					            {
					                name: 'Safari',
					                id: 'Safari',
					                data: [
					                    [
					                        'v11.0',
					                        3.39
					                    ],
					                    [
					                        'v10.1',
					                        0.96
					                    ],
					                    [
					                        'v10.0',
					                        0.36
					                    ],
					                    [
					                        'v9.1',
					                        0.54
					                    ],
					                    [
					                        'v9.0',
					                        0.13
					                    ],
					                    [
					                        'v5.1',
					                        0.2
					                    ]
					                ]
					            },
					            {
					                name: 'Edge',
					                id: 'Edge',
					                data: [
					                    [
					                        'v16',
					                        2.6
					                    ],
					                    [
					                        'v15',
					                        0.92
					                    ],
					                    [
					                        'v14',
					                        0.4
					                    ],
					                    [
					                        'v13',
					                        0.1
					                    ]
					                ]
					            },
					            {
					                name: 'Opera',
					                id: 'Opera',
					                data: [
					                    [
					                        'v50.0',
					                        0.96
					                    ],
					                    [
					                        'v49.0',
					                        0.82
					                    ],
					                    [
					                        'v12.1',
					                        0.14
					                    ]
					                ]
					            }
					        ]
					    }
					});

					</script>
				</td>

				<td>
					<div id="container"></div>
					<script type="text/javascript">
						Highcharts.chart('container', {
							chart : {
								type : 'column'
							},
							title : {
								text : 'ECO 년도별 현황',
								align : 'left'
							},
							xAxis : {
								categories : [ 
									<%for (int i = 0; i < 5; i++) {
	String s = String.valueOf(Integer.parseInt(start) + i);%>
									'<%=s%>년',
									<%}%>
								]
							},
							yAxis : {
								min : 0,
								title : {
									text: '개수(개)',
								}
							},
							plotOptions : {
								column : {
									pointPadding : 0.2,
									borderWidth : 0
								}
							},
							series : [ {
								name : '진행중',
								data : [ 
									<%for (int i = 0; i < progress.size(); i++) {
	Map<String, Integer> map = (Map<String, Integer>) progress.get(i);%>
									<%=map.get(start + i)%>,
									<%}%>
								]
							}, {
								name : '완료됨',
								data : [
									<%for (int i = 0; i < complete.size(); i++) {
	Map<String, Integer> map = (Map<String, Integer>) complete.get(i);%>
									<%=map.get(start + i)%>,
									<%}%>
									
								]	
							} ]
						});
					</script>
				</td>
			</tr>
		</table>


		<script type="text/javascript">
			function view(oid) {
				const url = getCallUrl("/notice/view?oid=" + oid);
				_popup(url, 1000, 500, "n");
			}

			document.addEventListener("DOMContentLoaded", function() {
			});
		</script>
	</form>
</body>
</html>