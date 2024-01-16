<%@page import="wt.fc.PagingSessionHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.e3ps.groupware.notice.Notice"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

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
</head>
<body>
	<form>
		<!-- 결재 및 공지사항 -->
		<table style="height: 400px; border: 1px solid black;">
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