<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
var totSize = 0;
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	lfn_DhtmlxGridInit();
	lfn_Search();
	gfn_InitCalendar("predate", "predateBtn");
	gfn_InitCalendar("postdate", "postdateBtn");
});

$(function(){
	<%----------------------------------------------------------
	*                      검색 버튼 클릭시
	----------------------------------------------------------%>
	$("#searchBtn").click(function (){
		$("#sortValue").val("");
		$("#sortCheck").val("");
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	})
})

function lfn_DhtmlxGridInit() {
	var COLNAMEARR = {
			 col01 : '${f:getMessage("이름")}'
			,col02 : '${f:getMessage("아이디")}'
			,col03 : '${f:getMessage("해당모듈")}'
			,col04 : '${f:getMessage("다운횟수")}'
			,col05 : '${f:getMessage("다운시간")}'
			,col06 : '${f:getMessage("다운사유")}'
	}
	
	var sHeader = "";
	sHeader +=       COLNAMEARR.col01;
	sHeader += "," + COLNAMEARR.col02;
	sHeader += "," + COLNAMEARR.col03;
	sHeader += "," + COLNAMEARR.col04;
	sHeader += "," + COLNAMEARR.col05;
	sHeader += "," + COLNAMEARR.col06;
	
	var sHeaderAlign = new Array();
	sHeaderAlign[0]  = "text-align:center;";
	sHeaderAlign[1]  = "text-align:center;";
	sHeaderAlign[2]  = "text-align:center;";
	sHeaderAlign[3]  = "text-align:center;";
	sHeaderAlign[4]  = "text-align:center;";
	sHeaderAlign[5]  = "text-align:center;";

	var sWidth = "";
	sWidth += "8";
	sWidth += ",7";
	sWidth += ",40";
	sWidth += ",5";
	sWidth += ",10";
	sWidth += ",30"
	
	var sColAlign = "";
	sColAlign += "center";
	sColAlign += ",center";
	sColAlign += ",left";
	sColAlign += ",center";
	sColAlign += ",center";
	sColAlign += ",left";
	
	var sColType = "";
	sColType += "ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";
	sColType += ",ro";

	var sColSorting = "";
	sColSorting += "na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	sColSorting += ",na";
	
	documentListGrid = new dhtmlXGridObject('listGridBox');
	documentListGrid.setImagePath("/Windchill/jsp/js/dhtmlx/imgs/");
	documentListGrid.setHeader(sHeader,null,sHeaderAlign);
	documentListGrid.enableAutoHeight(true);
	documentListGrid.setInitWidthsP(sWidth);
	documentListGrid.setColAlign(sColAlign);
	documentListGrid.setColTypes(sColType);
	documentListGrid.setColSorting(sColSorting);
	
	/*grid text copy option*/
    documentListGrid.enableBlockSelection();
    documentListGrid.forceLabelSelection(true);
    documentListGrid.attachEvent("onKeyPress",function onKeyPressed(code,ctrl,shift){
        if(code==67&&ctrl){
            if(!documentListGrid._selectionArea) return alert("return");
            documentListGrid.setCSVDelimiter("\t");
            documentListGrid.copyBlockToClipboard();
	    }
	    return;
	});
	
	documentListGrid.init();
}

function lfn_Search() {
	var form = $("form[name=admin_downLoadHistory]").serialize();
	var url	= getURLString("admin", "admin_downLoadHistoryAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			var vArr = new Array();
			vArr[0] = data.rows    ;
			vArr[1] = data.formPage   ;
			vArr[2] = data.totalCount ;
			vArr[3] = data.totalPage  ;
			totSize = vArr[2];
			vArr[4] = data.currentPage;
			vArr[5] = data.startPage  ;
			vArr[6] = data.endPage    ;
			vArr[7] = data.sessionId  ;
			
			documentListGrid.clearAll();
			documentListGrid.loadXMLString(data.xmlString);
			
			gfn_SetPaging(vArr,"pagingBox");
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
	});
}
function onExcelDown() {
	$("#admin_downLoadHistory").attr("method", "post");
	/* if(totSize>50000){
		var msg = "${f:getMessage('데이터가 너무 많아 엑셀에 저장이 안됩니다.')}";
		alert(msg);
		return;
	} */
	$("#admin_downLoadHistory").attr("action", getURLString("excelDown", "downLoadHistoryExcelDown", "do")).submit();
	
	//$("#admin_loginHistory").attr("action", getURLString("excelDown", "loginHistoryxcelDown", "do")).submit();
}
function gotoView(type) {
	$("#type").val(type);
	$("#sortValue").val("");
	$("#sortCheck").val("");
	$("#sessionId").val("");
	$("#page").val(1);
	lfn_Search();
}

</script>

<body>

<form name="admin_downLoadHistory" id="admin_downLoadHistory">

<!--  Paging 처리를 위한 Hidden 변수 -->
<input type="hidden" name="page"         id="page"          value="1"/>						<!-- page No -->
<input type="hidden" name="formPage"     id="formPage"      value="10"/>					<!-- 한 화면에 나타나는 페이지수 -->
<input type="hidden" name="rows"     	 id="rows"    		value="10"/>					<!-- 한 화면에 나타나는 데이터 수 -->
<input type="hidden" name="sessionId"    id="sessionId"     value=""/>

<input type="hidden" name="type" id="type" />

<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">

	<tr>
		<td bgcolor=EDE9DD valign="top" height="100%" >
              	
              	<!-- 왼쪽 서브메뉴 -->
			<table width="100%" border="0" cellpadding="0" cellspacing="1">
			
				<colgroup>
					<col width="200px">
					<col width="auto">
				</colgroup>
			
				<tr>
					<td colspan="2" style="cursor:pointer" height="25" bgcolor=white valign="top">
                          
						<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">
							<tr>
								<td bgcolor="EDE9DD" valign="top" height="100%">			
									<!-- 왼쪽서브메뉴 -->
									<table width="200"  border="0" cellpadding="0" cellspacing="1">				
										<tr height="20" style="padding-left:10px">
											<td height="25" bgcolor=white>
												<a href="javaScript:gotoView('EPMDocument')">${f:getMessage('도면관리')}</a>
											</td>
										</tr>				
										<tr height="20" style="padding-left:10px">
											<td height="25" bgcolor=white>
												<a href="javaScript:gotoView('WTPart')" >${f:getMessage('품목관리')}</a>
											</td>
										</tr>				
										<tr height="20" style="padding-left:10px">
											<td height="25" bgcolor=white>
												<a href="javaScript:gotoView('change')">${f:getMessage('설계변경')}</a>
											</td>
										</tr>				
										<tr height="20" style="padding-left:10px">
											<td height="25" bgcolor=white>
												<a href="javaScript:gotoView('WTDocument')" >${f:getMessage('문서관리')}</a>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>

					<!--  다운로드 이력관리 -->

					<td height="100%" valign=top>
                    
                    	<!-- 오른쪽 리스트 -->
                    	<table width=100%  border="0" cellpadding="0" cellspacing="0">
                            <tr align=center height=5>
                                <td bgcolor=white height="100%">
					
									<table width="100%" border="0" cellpadding="0" cellspacing="20"  > <!--//여백 테이블-->
								        <tr align=center height=5>            
								            <td>
								                <table width="100%" border="0" cellpadding="10" cellspacing="3" >
								                    <tr  align=center height=5>
								                        <td valign="top" style="padding:0px 0px 0px 0px" height=3 >
								                        	
								                        	<!-- 상단 제목 부분 -->                        
								                        	<div style="overflow: auto;">
																<table width="100%" border="0" cellspacing="0" cellpadding="0">
																	<tr>
																		<td align="left" class="Sub_TBG" >
																			<div id="mainTitle">${f:getMessage('다운로드 이력관리')}</div>
																		</td>
																		<td class="Sub_TRight"></td>								
																	</tr>
																</table>
															</div>                        	
								                            
								                        </td>
								                    </tr>
								                    <tr align=center height=5>
								                        <td valign="top" style="padding:0px 0px 0px 0px" height=3 >
								                        
								                        	<!-- 파란 라인 -->
								                        	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
																<tr><td height=1 width=100%></td></tr>
															</table>
															
															<!-- input 폼 -->
								                            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
								                            <col width='10%'><col width='30%'><col width='10%'><col width='30%'><col width='10%'><col width='10%'>
								                                <tr  bgcolor="ffffff" height="35">
								                                    <td class="tdblueM">${f:getMessage('유저 검색')}</td>
								                                    <td class="tdwhiteL">
								                                    	<input TYPE="hidden" name="manager" id="manager" >
								                                    	<input type="text" class="input100" name="tempmanager" id="tempmanager" readonly>
								                                    	
																		<a href="JavaScript:searchUser('documentForm','single','manager','tempmanager','wtuser');">
																			<img src="/Windchill/jsp/portal/images/s_search.gif"  border="0" align="absmiddle">
																		</a>
																		
																		<a href="JavaScript:clearText('manager');clearText('tempmanager')">
																			<img src="/Windchill/jsp/portal/images/x.gif" border=0 align="absmiddle">
																		</a>
								                                    </td>                              
								                                </tr>
								                                 <tr  bgcolor="ffffff" height="35">
											                                <td class="tdblueM">
																		${f:getMessage('다운로드일')}
																	</td>
																	
																	<td class="tdwhiteL" colspan="3">
																	    <input name="predate" id="predate" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
																		<a href="javascript:void(0);">
																			<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="predateBtn" id="predateBtn" />
																		</a>
																		<a href="javascript:void(0);" onclick="clearText('predate');">
																			<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
																		</a>
																		~
																		<input name="postdate" id="postdate" class="txt_field" size="12"  maxlength="15" readonly="readonly" />
																		<a href="javascript:void(0);">
																			<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border="0" name="postdateBtn" id="postdateBtn" />
																		</a>
																		<a href="javascript:void(0);" onclick="clearText('postdate');">
																			<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
																		</a>
																	</td>
																	</tr>
								                            </table>						
															
								                        </td>
								                    </tr>
								                    <tr  align=center height=5>
								                        <td valign="top" style="padding:0px 0px 0px 0px" height=3 >
								                            
								                            <!-- 검색과 초기화 버튼 -->
								                        	<table border="0" cellpadding="0" cellspacing="4" align="right">
													        	<tr>
													            	<td>
													                	<button type="button" class="btnSearch" id="searchBtn">
													                		<span></span>
													                		${f:getMessage('검색')}
													                	</button>
													            	</td>
													                  
													            	<td>
													            		<button title="Reset" class="btnCustom" type="reset" id="btnReset">
													                 	 	<span></span>${f:getMessage('초기화')}
													                 	 </button>
													           		</td>
													                  
													              	  <td>
														                	<a href="javascript:onExcelDown();">
																		  		<img src="/Windchill/jsp/portal/images/icon/fileicon/xls.gif" alt="${f:getMessage('엑셀다운')}" border="0" align="middle">
																	    	</a>
																	    </td>
													              </tr>
													       	</table>
								                            
								                        </td>
								                    </tr>
								                    
								                    <tr>
														<td>
															<div id="listGridBox" style="width:100%;" ></div>
														</td>
													</tr>
													
													<tr>
														<td>
															<div id="pagingBox"></div>
														</td>
													</tr>
								                    
								                </table>
								            </td>
								        </tr>
								    </table>
								    
							    </td>
						    
						    </tr>
					    
					    </table>
					
					</td>

				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>