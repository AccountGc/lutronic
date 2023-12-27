<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.code.NumberCodeType"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// NumberCode c = NumberCode.newNumberCode();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>
<body>
    <form>
        <input type="hidden" name="sessionid" id="sessionid">
        <input type="hidden" name="curPage" id="curPage">
        <input type="hidden" name="type" id="type" />

        <table class="create-table">
            <colgroup>
                <col width="150">
                <col width="150">
                <col width="*">
            </colgroup>
            <tr>
                <th class="lb">문서</th>
                <td class="indent5" colspan="2">
                    <input type="file" name="document" id="document">
                </td>
            </tr>
            <tr>
                <th class="lb">제품</th>
                <td class="indent5" colspan="2">
                    <input type="file" name="part" id="part">
                </td>
            </tr>
            <tr>
                <th class="lb">도면</th>
                <td class="indent5" colspan="2">
                    <input type="file" name="drawing" id="drawing">
                </td>
            </tr>
            <tr>
                <th class="lb" rowspan="2">물질</th>
                <th class="lb">일괄등록</th>
                <td class="indent5">
                    <input type="file" name="rohs" id="rohs">
                </td>
            </tr>
            <tr>
                <th class="lb">링크등록</th>
                <td class="indent5">
                    <input type="file" name="rohsLink" id="rohsLink">
                </td>
            </tr>
        </table>
        
        <table class="button-table">
            <tr>
                <td class="center">
                    <input type="button" value="등록" title="등록" class="blue">
                    <input type="button" value="초기화" title="초기화">
                </td>
            </tr>
        </table>
        <script type="text/javascript">
        </script>
    </form>
</body>
</html>