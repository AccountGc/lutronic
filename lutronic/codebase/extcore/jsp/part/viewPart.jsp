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
	*                      상위품목 버튼
	----------------------------------------------------------%>
	$("#upItem").click(function() {
		viewBomList("up");
	})
	<%----------------------------------------------------------
	*                      하위품목 버튼
	----------------------------------------------------------%>
	$("#downItem").click(function() {
		viewBomList("down");
	})
	<%----------------------------------------------------------
	*                      END ITEM 버튼
	----------------------------------------------------------%>
	$("#endItem").click(function() {
		viewBomList("end");
	})
	
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateBtn").click(function () {
		location.href = getURLString("part", "updatePart", "do") + "?oid=" + $("#oid").val();
	}),
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#delBtn").click(function () {
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){ return; }
		var form = $("form[name=partViewForm]").serialize();
		var url	= getURLString("part", "deletePartAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				alert(data.msg);
				if(data.result) {
					if(parent.opener.$("#sessionId").val() == "undefined" || parent.opener.$("#sessionId").val() == null){
						parent.opener.location.reload();
					}else {
						parent.opener.$("#sessionId").val("");
						parent.opener.lfn_Search();
					}
					window.close();
				}
			}
		});
	}),
	<%----------------------------------------------------------
	*                      최신버전 버튼
	----------------------------------------------------------%>
	$("#latestBtn").click(function () {
		var loid = this.value;
		openView(loid);
	}),
	<%----------------------------------------------------------
	*                      버전이력 버튼
	----------------------------------------------------------%>
	$("#versionBtn").click(function () {
		var oid = $("#oid").val();
		var url = getURLString("common", "versionHistory", "do") + "?oid=" + oid;
		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
	}),
	
	<%----------------------------------------------------------
	*                     AUI BOM 버튼
	----------------------------------------------------------%>
	/*
	$(document).keypress(function(event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		
		$("#bomSelect").val("true");
		
		console.log("keypress charCode =" + charCode );
		
	})
	*/
	$(document).keydown(function(event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		if(charCode ==17){
			$("#bomSelect").val("true");
		}else{
			$("#bomSelect").val("false");
		}
		
		//console.log("keydown charCode =" + charCode );
	})
	
	
	
	
	$("#auiBom").click(function() {
		var oid =$("#oid").val();
		
		
		if($("#bomSelect").val() =="true"){
			bom2View();
			$("#bomSelect").val("false");
		}else{
			auiBom(oid,'');
		}
	});
	$("#auiBom2").click(function() {
		var oid =$("#oid").val();
		
		
		if($("#bomSelect").val() =="true"){
			bom2View();
			$("#bomSelect").val("false");
		}else{
			auiBom2(oid,'');
		}
	})
	
	<%----------------------------------------------------------
	*                      BOM 버튼
	----------------------------------------------------------%>
	$("#bom").click(function() {
		var str = getURLString("part", "PartTree", "do") + "?oid="+$("#oid").val();
	    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	    leftpos = (screen.width - 1000)/ 2;
	    toppos = (screen.height - 600) / 2 ;
	    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
	    var newwin = window.open( str , "viewBOM", opts+rest);
	    newwin.focus();
	    
	})
	
	<%----------------------------------------------------------
	*                      BOM 버튼
	----------------------------------------------------------%>
	$("#bom2").click(function() {
		//var str = getURLString("part", "PartTree", "do") + "?oid="+$("#oid").val();
		var str = getURLString("part", "viewPartBom", "do") + "?oid="+$("#oid").val();
	    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	    leftpos = (screen.width - 1000)/ 2;
	    toppos = (screen.height - 600) / 2 ;
	    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
	    var newwin = window.open( str , "viewBOM2", opts+rest);
	    newwin.focus();
	    
	})
	
	$("#packageUpdate").click(function() {
		//var str = getURLString("part", "updatePackagePart", "do") + "?oid=" + $("#oid").val();
		var str = getURLString("part", "updateAUIPackagePart", "do") + "?oid=" + $("#oid").val();
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1500,height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "updatePackagePart", opts+rest);
		newwin.focus();
	})
	
	$("#bomE").click(function() {
		var pdmOid = "${partData.getPDMLinkProductOid()}";
		var vrOid = "${partData.vrOid}";
		var str = "/Windchill/netmarkets/jsp/explorer/installmsg.jsp?message=ok"
				  + "&oid=" + vrOid +
				  "&containerId=" + pdmOid + "&applet=com.ptc.windchill.explorer.structureexplorer.StructureExplorerApplet&jars=ptcAnnotator.jar,lib/pview.jar,lib/json.jar&appId=ptc.pdm.ProductStructureExplorer&launchEmpty=false&explorerName=%EC%A0%9C%ED%92%88+%EA%B5%AC%EC%A1%B0+%ED%83%90%EC%83%89%EA%B8%B0&ncid="
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	    leftpos = (screen.width - 1000)/ 2;
	    toppos = (screen.height - 600) / 2 ;
	    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
	    var newwin = window.open( str , "BOMEditor", opts+rest);
	    newwin.focus();
	})
	
	$("#Compare").click(function() {
		var str = "/Windchill/netmarkets/jsp/structureCompare/StructureCompare.jsp?oid=OR:" + $("#oid").val() + "&ncId=2374138740478986248&locale=ko"
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1300,height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "compareBOM", opts+rest);
		newwin.focus();
	})
	
	$("#orderNumber").click(function() {
		var str = getURLString("part", "partChange", "do") + "?partOid=" + $("#oid").val();
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1500,height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "viewBOM", opts+rest);
		newwin.focus();
	})
	
	$("#orderNumber_NewVersion").click(function() {
		//var str = getURLString("part", "partChange", "do") + "?partOid=" + $("#oid").val();
		var str = getURLString("part", "updateAUIPartChange", "do") + "?partOid=" + $("#oid").val();
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1500,height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "viewBOM", opts+rest);
		newwin.focus();
	})
	
	$("#disuse").click(function() {
		if (confirm("${f:getMessage('폐기하시겠습니까?')}")){ 
			partStateChange('DEATH');
		}
	})
	
	$("#restore").click(function() {
		if (confirm("${f:getMessage('복원하시겠습니까?')}")){ 
			partStateChange('INWORK');
		}
	})
	$('#changeDev').click(function() {
		if (confirm("${f:getMessage('변경하시겠습니까?')}")){ 
			partStateChange('INWORK');
		}
	})
	
	$("#updatefamily").click(function() {//updatePackagePart
		var str = getURLString("part", "updatefamilyPart", "do") + "?oid=" + $("#oid").val();
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1300,height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "updatefamily", opts+rest);
		newwin.focus();
	})
	
	
	$("#attributeCleaning").click(function() {
		if (confirm("${f:getMessage('속성 Clearing 하시겠습니까?')}")){ 
			var form = $("form[name=partViewForm]").serialize();
			var url	= getURLString("part", "attributeCleaning", "do");
			$.ajax({
				type:"POST",
				url: url,
				data:form,
				dataType:"json",
				async: true,
				cache: false,
				error: function(data) {
					alert("${f:getMessage('삭제 오류 발생')}");
				},
				success:function(data){
					console.log(data);
					alert(data.message);
					if(data.result) {
						
						location.reload();
					}
				}
			});
		}
	})
	
})

function bom2View(){
	//var str = getURLString("part", "PartTree", "do") + "?oid="+$("#oid").val();
	var str = getURLString("part", "viewPartBom", "do") + "?oid="+$("#oid").val();
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBOM2", opts+rest);
    newwin.focus();
}

window.partStateChange = function(state) {
	var url	= getURLString("part", "partStateChange", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			oid : $("#oid").val(),
			state : state
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
				alert("${f:getMessage('상태가 변경되었습니다.')}");
				location.reload();
			}else {
				alert(data.message);
			}
		}
	});
}


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

<%----------------------------------------------------------
*                      Bom Type에 따른 bom 검색
----------------------------------------------------------%>
function viewBomList(bomType){
	var str = getURLString("part", "bomPartList", "do") + "?oid="+$("#oid").val()+"&bomType="+bomType;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=600,height=500,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewBomList", opts+rest);
    newwin.focus();
}
</script>

<body>

<form method="post" name="partViewForm" action="">

<input type="hidden" name="oid" id="oid" value="<c:out value="${partData.oid }" />" />
<input type="hidden" name="latestOid" id="latestOid" value="" />
<input type="hidden" name="bomSelect" id="bomSelect" value="" />
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
					<td width="100">
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width="10" height="9" />
						<b>
							${f:getMessage('품목')}
							${f:getMessage('상세보기')}
						</b>
					</td>
					
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="left">
							<td>
								<button type="button" name="upItem" id="upItem" class="btnCustom" style="width: 80px">
									<span></span>
									상위품목
								</button>
							</td>
							
				    		<td>
				    			<button type="button" name="downItem" id="downItem" class="btnCustom" style="width: 80px">
									<span></span>
									하위품목
								</button>
							</td>
							
				    		<td>
				    			<button type="button" name="endItem" id="endItem" class="btnCustom" style="width: 90px">
									<span></span>
									END ITEM
								</button>
							</td>
							<c:if test="${!partData.isLatest() }">
			                    <td>
			                    	<button type="button" name="latestBtn" id="latestBtn" class="btnCustom" value="<c:out value='${partData.latestOid() }'/>">
										<span></span>
										${f:getMessage('최신Rev.')}
									</button>
			                    </td>
		                    </c:if>
		                    
		                    <td>
		                    	<button type="button" name="versionBtn" id="versionBtn" class="btnCustom">
									<span></span>
									${f:getMessage('Rev.이력')}
								</button>
		                    </td>
		                    
		                    <td>
		                    	<button type="button" name="auiBom" id="auiBom" class="btnCustom">
									<span></span>
									BOM
								</button>
		                    </td>
		                    <td>
		                    	<button type="button" name="auiBom2" id="auiBom2" class="btnCustom" title="클릭 또는 방향키 입력시 하위 데이터 요청됨.">
									<span></span>
										단계별  BOM
								</button>
		                    </td>
		                    <c:if test="${partData.isLatest() }">
			                    <td>
			                    	<button type="button" name="bomE" id="bomE" class="btnCustom">
										<span></span>
										BOM Editor
									</button>
			                    </td>
		                    </c:if>
		                    
		                    <td>
		                    	<button type="button" name="Compare" id="Compare" class="btnCustom">
									<span></span>
									Compare
								</button>
		                    </td>
						</table>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                
			                	<c:if test='${partData.isLatest()}'>
				                	<c:if test='${partData.isState("DEATH") && isAdmin }'>
				                		
					                	<td>
					                		<button type="button" name="restore" id="restore" class="btnCRUD">
												<span></span>
												${f:getMessage('복원')}
											</button>
					                	</td>
					                	--%>
				                	</c:if>
				                	<c:if test='${partData.isClearing()}'>
				                	
				                	<td>
				                		<button type="button" name="attributeCleaning" id="attributeCleaning" class="btnCRUD">
												<span></span>
												${f:getMessage('속성 Clearing')}
										</button>
									</td>
									
				                	</c:if>
				                	
			                	
			                		<c:if test='${partData.isFamliyModify()}'>
			                		<td>
			                			<button type="button" name="updatefamily" id="updatefamily" class="btnCRUD">
												<span></span>
												${f:getMessage('Family 테이블 수정')}
										</button>
									</td>
			                		</c:if>
				                	<c:if test='${partData.isWorking()}'>
				                		<c:if test="${isAdmin }">
				                			<%--
						                	<td>
						                		<button type="button" name="disuse" id="disuse" class="btnCRUD">
													<span></span>
													${f:getMessage('폐기')}
												</button>
						                	</td>
						                	--%>
					                	</c:if>
				                
					                	<td>
					                		<button type="button" name="packageUpdate" id="packageUpdate" class="btnCRUD">
												<span></span>
												${f:getMessage('일괄 수정')}
											</button>
					                	</td>
			                   		
					                    <td>
					                    	<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
												<span></span>
												${f:getMessage('수정')}
											</button>
					                    </td>
				                    
					                    <td>
					                    	<button type="button" name="delBtn" id="delBtn" class="btnCRUD">
												<span></span>
												${f:getMessage('삭제')}
											</button>
					                    </td>
			                    	
				                    	<td>
					                    	<button type="button" name="orderNumber" id="orderNumber" class="btnCRUD">
												<span></span>
												${f:getMessage('채번')}
											</button>
					                    </td>
					                    <td>
					                    	<button type="button" name="orderNumber_NewVersion" id="orderNumber_NewVersion" class="btnCRUD">
												<span></span>
												${f:getMessage('채번(새버전)')}
											</button>
					                    </td>
					                    
			                    	</c:if>
			                    	
			                    	<c:if test='${partData.isState("DEV_APPROVED")}'>
		                    			<td>
					                    	<button type="button" name="changeDev" id="changeDev" class="btnCRUD">
												<span></span>
												${f:getMessage('상태변경')}
											</button>
					                    </td>
		                    		</c:if>
			                    	
		                    	</c:if>
		                    
		                    	
			                    <!--  
			                    <td>
			                    	<button type="button" name="bom" id="bom" class="btnCustom">
										<span></span>
										BOM
									</button>
			                    </td>
			                    
			                    <td>
			                    	<button type="button" name="bom2" id="bom2" class="btnCustom">
										<span></span>
										BOM2
									</button>
			                    </td>
			                    -->
			                    
			                    
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
</jsp:include>

<!-- 참조 항목 -->
<jsp:include page="/eSolution/drawing/include_ReferenceBy.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${f:getMessage('참조 항목')}" name="title"/>
	<jsp:param value="${partData.getEpmOid() }" name="oid"/>
</jsp:include>

<!-- 관련 도면 -->
<jsp:include page="/eSolution/drawing/include_DrawingView.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련 도면')}" name="title"/>
	<jsp:param value="epmOid" name="paramName"/>
</jsp:include>

<!-- 관련 문서 -->
<jsp:include page="/eSolution/doc/include_DocumentView.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련 문서')}" name="title"/>
	<jsp:param value="docOid" name="paramName"/>
</jsp:include>

<!-- 관련 물질 -->
<jsp:include page="/eSolution/rohs/include_RohsView.do">
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련')} RoHS" name="title"/>
	<jsp:param value="part" name="module"/>
</jsp:include>

<!-- 관련 ECO -->
<jsp:include page="/eSolution/changeECO/include_ChangeECOView.do">
	<jsp:param name="moduleType" value="part"/>
	<jsp:param name="title" value="${f:getMessage('관련')} ECO"/>
	<jsp:param name="oid" value="${partData.oid }"/>
</jsp:include>

<!-- 관련 개별 업무 -->
<jsp:include page="/eSolution/development/include_DevelopmentView.do">
	<jsp:param value="part" name="moduleType"/>
	<jsp:param value="${partData.oid }" name="oid"/>
	<jsp:param value="${f:getMessage('관련 개발업무')}" name="title"/>
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