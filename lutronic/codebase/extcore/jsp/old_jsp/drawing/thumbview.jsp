<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript">
$(function () {
	$("#rePublish").click(function () {
		rePublish();
	})
})

function rePublish(){
	//alert($("#oid").val());
	if (!confirm("${f:getMessage('재변환 하시겠습니까?')}")){ return; }
	var url	= getURLString("drawing", "cadRePublish", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			oid : $("#oid").val()
		},
		dataType:"json",
		async: true,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('등록 오류')}";
			alert(msg);
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
		,success: function(data) {
			if(data.result) {
				alert("${f:getMessage('변환 되었습니다.')}");
				location.reload();
			}else {
				alert(data.message);
			}
		}
	});
}

function openCreoViewWVSPopup(oid) {
	// #. CreoView창을 연다
	var sURL = "/Windchill/wtcore/jsp/wvs/edrview.jsp?viewIfPublished=1&sendToPublisher=" + oid;
	
	var params = {};
	sURL = createQueryStringURL(sURL, params);
	
	var sName = "";
	var nWidth = 830;
	var nHeight = 600;
	var bMoveCenter = true;
	var bStatus = true;
	var bScrollbars = true;
	var bResizable = true;

	return openWindow(sURL, sName, nWidth, nHeight, bMoveCenter, bStatus, bScrollbars, bResizable);
}

function createQueryStringURL(baseURL, paramObj) {

    var url = baseURL;

    var queryString = "";
    if (paramObj != null) {
              for (var attrName in paramObj) {
                         if (queryString != "") {
                                   queryString += "&";
                         }
                         if (Object.prototype.toString.call(paramObj[attrName]) == "[object Array]") {
                                   var arrayQueryString = "";
                                   for (var i = 0; i < paramObj[attrName].length; i++) {
                                              if (arrayQueryString != "") {
                                                         arrayQueryString += "&";
                                              }
                                              arrayQueryString += attrName + "=" + encodeURIComponent(paramObj[attrName][i]);
                                   }
                                   queryString += arrayQueryString;
                         } else {
                                   queryString += attrName + "=" + encodeURIComponent(getPopupParamValue(paramObj[attrName]));
                         }
              }
    }

    if (queryString != "") {
              if (url.indexOf("?") < 0) {
                         url += "?" + queryString;
              } else {
                         url += "&" + queryString;
              }
    }

    return url;
}

</script>

<div align="center">
	<table>
		<tr>
			<td>
				<a href="<c:out value='${visualizationData }' escapeXml="true" />" >
					<img src="<c:out value="${thumb }" escapeXml="false"/>"  border="0" width="250" height="170">
				</a>
			</td>
		</tr>
		<tr>
			<td align="center">
			<button type="button" name="rePublish" id="rePublish" class="btnCRUD">
			<span></span>재변환
												
			</button>
			</td>
		</tr>
	</table>
</div>