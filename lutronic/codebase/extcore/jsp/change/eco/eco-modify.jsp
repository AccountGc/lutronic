<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.eco.dto.EcoDTO"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcoDTO dto = (EcoDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
Map<String, Object> contentMap = dto.getContentMap();
String aOid = "";
if (contentMap != null) {
    aOid = contentMap.get("aoid") == null ? "" : contentMap.get("aoid").toString();
}
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
    <tr>
        <td class="left">
            <div class="header">
                <img src="/Windchill/extcore/images/header.png">
                ECO 수정
            </div>
        </td>
        <td class="right">
            <input type="button" value="수정" title="수정" class="blue" onclick="modify();">
            <input type="button" value="이전" title="이전" onclick="history.back();">
            <input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
        </td>
    </tr>
</table>
<table class="create-table">
    <colgroup>
        <col width="180">
        <col width="*">
        <col width="180">
        <col width="*">
    </colgroup>
    <tr>
        <th class="req lb">ECO 제목</th>
        <td class="indent5" colspan="3">
            <input type="text" name="name" id="name" class="width-400" value="<%=dto.getName()%>">
        </td>
    </tr>
    <tr>
        <th class="lb">변경사유</th>
        <td class="indent5" colspan="3">
            <textarea id="eoCommentA" name="eoCommentA" rows="10"><%=dto.getEoCommentA()%></textarea>
        </td>
    </tr>
    <tr>
        <th class="lb">변경사항</th>
        <td class="indent5" colspan="3">
            <textarea name="eoCommentB" id="eoCommentB" rows="10"><%=dto.getEoCommentB()%></textarea>
        </td>
    </tr>
    <tr>
        <th class="lb">인허가변경</th>
        <td>
            &nbsp;
            <div class="pretty p-switch">
                <input type="radio" name="licensing" id="licensing" value="NONE" <%if ("NONE".equals(dto.getLicensing())) {%> checked="checked" <%}%>>
                <div class="state p-success">
                    <label>
                        <b>N/A</b>
                    </label>
                </div>
            </div>
            &nbsp;
            <div class="pretty p-switch">
                <input type="radio" name="licensing" value="0" <%if ("0".equals(dto.getLicensing())) {%> checked="checked" <%}%>>
                <div class="state p-success">
                    <label>
                        <b>불필요</b>
                    </label>
                </div>
            </div>
            &nbsp;
            <div class="pretty p-switch">
                <input type="radio" name="licensing" value="1" <%if ("1".equals(dto.getLicensing())) {%> checked="checked" <%}%>>
                <div class="state p-success">
                    <label>
                        <b>필요</b>
                    </label>
                </div>
            </div>
        </td>
        <th>위험통제</th>
        <td>
            &nbsp;
            <div class="pretty p-switch">
                <input type="radio" name="riskType" value="NONE" <%if ("NONE".equals(dto.getRiskType())) {%> checked="checked" <%}%>>
                <div class="state p-success">
                    <label>
                        <b>N/A</b>
                    </label>
                </div>
            </div>
            <div class="pretty p-switch">
                <input type="radio" name="riskType" value="0" <%if ("0".equals(dto.getRiskType())) {%> checked="checked" <%}%>>
                <div class="state p-success">
                    <label>
                        <b>불필요</b>
                    </label>
                </div>
            </div>
            &nbsp;
            <div class="pretty p-switch">
                <input type="radio" name="riskType" value="1" <%if ("1".equals(dto.getRiskType())) {%> checked="checked" <%}%>>
                <div class="state p-success">
                    <label>
                        <b>필요</b>
                    </label>
                </div>
            </div>
        </td>
    </tr>
    <tr>
        <th class="lb">특기사항</th>
        <td class="indent5" colspan="3">
            <textarea name="eoCommentC" id="eoCommentC" rows="10"><%=dto.getEoCommentC()%></textarea>
        </td>
    </tr>
    <tr>
        <th class="lb">기타사항</th>
        <td class="indent5" colspan="3">
            <textarea name="eoCommentD" id="eoCommentD" rows="10"><%=dto.getEoCommentD()%></textarea>
        </td>
    </tr>
    <tr>
        <th class="lb">
            설계변경 부품 내역파일
<!--             <img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" style="position: relative; top: 2px;"> -->
        </th>
        <td class="indent5" colspan="3">
            <jsp:include page="/extcore/jsp/common/attach-eco.jsp">
                <jsp:param value="<%=aOid%>" name="oid" />
                <jsp:param value="modify" name="mode" />
                <jsp:param value="ECO" name="roleType" />
            </jsp:include>

        </td>
    </tr>
    <tr>
        <th class="lb">첨부파일</th>
        <td class="indent5" colspan="3">
            <jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
                <jsp:param value="<%=dto.getOid()%>" name="oid" />
                <jsp:param value="modify" name="mode" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <th class="lb">결재</th>
        <td colspan="3">
            <jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp">
                <jsp:param value="" name="oid" />
                <jsp:param value="create" name="mode" />
            </jsp:include>
        </td>
    </tr>
</table>
<!-- 설계변경 품목 -->
<jsp:include page="/extcore/jsp/change/eco/include/eco-part-include.jsp">
    <jsp:param value="<%=dto.getOid()%>" name="oid" />
    <jsp:param value="update" name="mode" />
    <jsp:param value="true" name="multi" />
</jsp:include>
<!-- 관련 CR -->
<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
    <jsp:param value="<%=dto.getOid()%>" name="oid" />
    <jsp:param value="update" name="mode" />
    <jsp:param value="true" name="header" />
    <jsp:param value="true" name="multi" />
    <jsp:param value="250" name="height" />
</jsp:include>
<!-- 설변 활동 -->
<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
    <jsp:param value="<%=dto.getOid()%>" name="oid" />
    <jsp:param value="update" name="mode" />
    <jsp:param value="true" name="multi" />
    <jsp:param value="250" name="height" />
</jsp:include>
<table class="button-table">
    <tr>
        <td class="center">
            <input type="button" value="수정" title="수정" class="blue" onclick="modify();">
            <input type="button" value="이전" title="이전" onclick="history.back();">
            <input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
        </td>
    </tr>
</table>
<script type="text/javascript">
    function modify() {
        const oid = document.getElementById("oid").value;
        const name = document.getElementById("name");
        if (!confirm("수정 하시겠습니까?")) {
            return false;
        }

        const eoCommentA = toId("eoCommentA");
        const eoCommentB = toId("eoCommentB");
        const eoCommentC = toId("eoCommentC");
        const eoCommentD = toId("eoCommentD");
        const secondarys = toArray("secondarys");
        const primary = document.querySelector("input[name=primary]");
        const riskType = document.querySelector("input[name=riskType]:checked").value;
        const licensing = document.querySelector("input[name=licensing]:checked").value;
        var rows101 = AUIGrid.getGridDataWithState(myGridID101, "gridState");
        rows101 = rows101.filter(function(item) {
            return item.gridState != "removed";
        });

        var rows200 = AUIGrid.getGridDataWithState(myGridID200, "gridState");
        rows200 = rows200.filter(function(item) {
            return item.gridState != "removed";
        });

        var rows500 = AUIGrid.getGridDataWithState(myGridID500, "gridState");
        rows500 = rows500.filter(function(item) {
            return item.gridState != "removed";
        });

        if (isEmpty(name.value)) {
            alert("ECO 제목을 입력해주세요.");
            return;
        }

        if (primary == null) {
            alert("설계변경 부품 내역파일을 첨부해주세요.");
            return;
        }
        const params = {
            name : name.value,
            riskType : riskType,
            licensing : licensing,
            secondarys : secondarys,
            primary : primary.value,
            eoCommentA : eoCommentA,
            eoCommentB : eoCommentB,
            eoCommentC : eoCommentC,
            eoCommentD : eoCommentD,
			rows101 : rows101, // 관련CR
			rows200 : rows200, // 설변활동
			rows500 : rows500, // 설변품목
			oid : oid
		};
		const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
		toRegister(params, addRows8); // 결재선 세팅
		const url = getCallUrl("/eco/modify");
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			} else {
				closeLayer();
			}
		});
	}

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid101(columns101);
		createAUIGrid200(columns200);
		createAUIGrid500(columns500);
		createAUIGrid8(columns8);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID500);
		AUIGrid.resize(myGridID200);
		AUIGrid.resize(myGridID8);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID500);
		AUIGrid.resize(myGridID200);
		AUIGrid.resize(myGridID8);
	});
</script>