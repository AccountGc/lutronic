<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
$(function () {
	
	<%----------------------------------------------------------
	*                      버전이력 버튼
	----------------------------------------------------------%>
	$("#versionBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "versionHistory", "do") + "?oid=" + oid+"&distribute=true";
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	}),
	<%----------------------------------------------------------
	*                      END ITEM 버튼
	----------------------------------------------------------%>
	$("#endItem").click(function() {
		var str = getURLString("part", "bomPartList", "do") + "?oid="+$("#oid").val()+"&bomType=end";
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "ViewPartEndItem", opts+rest);
		newwin.focus();
	}),
	<%----------------------------------------------------------
	*                      BOM 버튼
	----------------------------------------------------------%>
	$("#bom").click(function() {
		
	    
	    viewBaselineBom($("#oid").val());
	})
	
	$("#auiBom2").click(function() {
		var oid =$("#oid").val();
		auiBom2(oid,'');
	})
	
})



function createCDialogWindow(dialogURL, dialogName, w, h, statusBar)
{
	if( typeof(_use_wvs_cookie) != "undefined" ) {
		var cookie_value = document.cookie;
		if( cookie_value != null ) {
			var loc = cookie_value.indexOf("wvs_ContainerOid=");
			if( loc >= 0 ) {
				var subp = cookie_value.substring(loc+4);
				loc = subp.indexOf(";");
				if( loc >= 0 ) subp = subp.substring(0, loc);
				dialogURL += "&" + subp;
			}
		}
	}else {
		var vm_url = "" + document.location;
		if( vm_url != null ) {
			var loc = vm_url.indexOf("ContainerOid=");
			if( loc >= 0 ) {
				var subp = vm_url.substring(loc);
				loc = subp.indexOf("&");
				if( loc >= 0 ) subp = subp.substring(0, loc);
				dialogURL += "&" + subp;
			}
		}
	}

	openWindow(dialogURL, dialogName, w, h, statusBar);
}
</script>

<body>

<form method="post" name="partViewForm" action="">

<input type="hidden" name="oid" id="oid" value="<c:out value="${partData.oid }" />" />
<input type="hidden" name="latestOid" id="latestOid" value="" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
    <tr>
        <td>
            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center">
		   				<B><font color=white>${f:getMessage('품목')} ${f:getMessage('상세보기')}</font></B>
		   			</td>
		   		</tr>
			</table>
            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
            	<tr>
					<td width="150">
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width="10" height="9" />
						<b>
							${f:getMessage('품목')}
							${f:getMessage('상세보기')}
						</b>
					</td>
					 <td>
		                <button type="button" name="auiBom2" id="auiBom2" class="btnCustom" title="클릭 또는 방향키 입력시 하위 데이터 요청됨.">
							<span></span>
								단계별  BOM
						</button>
		             </td>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                
		                    	<td>
			                    	<button type="button" name="versionBtn" id="versionBtn" class="btnCustom">
										<span></span>
										${f:getMessage('Rev.이력')}
									</button>
			                    </td>
			                    
			                    <td>
			                    	<button type="button" name="bom" id="bom" class="btnCustom">
										<span></span>
										BOM
									</button>
			                    </td>
			                    
			                   
			                    
			                    <td>
			                    	<button type="button" name="" id="" class="btnClose" onclick="self.close()">
										<span></span>
										${f:getMessage('닫기')}
									</button>
			                    </td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align="center">
		<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100%></td></tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<col width="15%"/>
				<col width="20%"/>
				<col width="15%"/>
				<col width="20%"/>
				<col width="30%"/>
			
				<tr>
			    	<td class="tdblueM" colspan="5">
			    		<b><c:out value="${partData.icon }" escapeXml="false"/> <c:out value="${partData.name }" /></b>
			    	</td>
			    </tr>
			    
                <tr>
                    <td class="tdblueM">
                    	${f:getMessage('품목번호')}
                    </td>
                    
                    <td class="tdwhiteL" >
                    	<c:out value="${partData.number }" />
                    </td>
                    
                    <td class="tdblueM">
                    	${f:getMessage('품목분류')}
                    </td>
                    
                    <td class="tdwhiteL">
                    	<c:out value="${partData.getLocation() }" />
                    </td>
                   
                    <td class="tdwhiteL" align="center" rowspan="7">
                    	<jsp:include page="/eSolution/drawing/thumbview.do" flush="true">
							<jsp:param name="oid" value='${partData.getEpmOid() }'/>
						</jsp:include>
					</td>
                </tr>
                
                <tr>
                	<td class="tdblueM">
                		${f:getMessage('상태')}
                	</td>
                	
                    <td class="tdwhiteL">
                    	<c:out value="${partData.lifecycle }" />
                    </td>
                    
                    <td class="tdblueM">
                    	${f:getMessage('Rev.')}
                    </td>
                    
                    <td class="tdwhiteL">
                    	<c:out value="${partData.version }" />.<c:out value="${partData.iteration }" />(<c:out value="${partData.viewName }" />)
                    </td>
                </tr>
                
          		<tr>
                	<td class="tdblueM">
                		${f:getMessage('등록자')}
                	</td>
                    <td class="tdwhiteL">
                    	<c:out value="${partData.creator }" />
                    </td>
                    
                    <td class="tdblueM">
                    	${f:getMessage('수정자')}
                    </td>
                    
                    <td class="tdwhiteL">
                    	<c:out value="${partData.modifier }" />
                    </td>
                </tr>
                
          		<tr>
                    <td class="tdblueM">
                    	${f:getMessage('등록일')}
                    </td>
                    
                    <td class="tdwhiteL">
                    	<c:out value="${partData.createDate }" />
                    </td>
                    
                    <td class="tdblueM">
                    	${f:getMessage('수정일')}
                    </td>
                    
                    <td class="tdwhiteL">
                    	<c:out value="${partData.modifyDate }" />
                    </td>
                </tr>
                
             	<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFileView">
							<jsp:param value="${partData.oid }" name="oid"/>
						</jsp:include>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr>
	
	<tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>${f:getMessage('품목 속성')}</b>
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			<jsp:include page="/eSolution/common/include_Attributes.do">
				<jsp:param value="${partData.oid }" name="oid"/>
				<jsp:param value="part" name="module"/>
				<jsp:param value="view" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>

<!-- 주 도면 -->
<jsp:include page="/eSolution/drawing/include_DrawingView.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="main" name="epmType" />
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('주 도면')}" name="title"/>
	<jsp:param value="epmOid" name="paramName"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<!-- 참조 항목 -->
<jsp:include page="/eSolution/drawing/include_ReferenceBy.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${f:getMessage('참조 항목')}" name="title"/>
	<jsp:param value="${partData.getEpmOid() }" name="oid"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<!-- 관련 도면 -->
<jsp:include page="/eSolution/drawing/include_DrawingView.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련 도면')}" name="title"/>
	<jsp:param value="epmOid" name="paramName"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<!-- 관련 문서 -->
<jsp:include page="/eSolution/doc/include_DocumentView.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련 문서')}" name="title"/>
	<jsp:param value="docOid" name="paramName"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<!-- 관련 물질 -->
<jsp:include page="/eSolution/rohs/include_RohsView.do">
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련')} RoHS" name="title"/>
	<jsp:param value="part" name="module"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<!-- 관련 ECO -->
<jsp:include page="/eSolution/changeECO/include_ChangeECOView.do">
	<jsp:param name="moduleType" value="part"/>
	<jsp:param name="title" value="${f:getMessage('관련')} ECO"/>
	<jsp:param name="oid" value="${partData.oid }"/>
	<jsp:param value="true" name="distribute" />
</jsp:include>

<c:if test="${isAdmin }">

<!-- 관리자 속성 -->
<jsp:include page="/eSolution/common/include_adminAttribute.do">
	<jsp:param name="module" value="part"/>
	<jsp:param name="oid" value="${partData.oid }"/>
</jsp:include>

</c:if>

</form>

</body>
</html>