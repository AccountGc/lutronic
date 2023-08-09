<%@page import="com.e3ps.change.service.ChangeWfHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String distribute = StringUtil.checkNull(request.getParameter("distribute"));
boolean checkDummy = StringUtil.checkReplaceStr(request.getParameter("checkDummy"), "false").equals("true") ? true : false;
JSONArray json = ChangeWfHelper.manager.wf_CheckPart(oid, checkDummy, distribute);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 대상 품목
				<input type="checkbox" name="checkDummy" id="checkDummy" value="true" checked > 더미숨김
			</div>
		</td>
		<td align="right">
			<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="include_ChangePartViewToggle" alt='include_ChangePartView' >
		</td>
	</tr>
</table>
<div id="grid_changePart" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let changePartGridID;
	const columnChangePart = [ {
		dataField : "number",
		headerText : "품목번호",
		width : 180,
	}, {
		headerText : "변경전",
		children : [ {
			dataField : "name",
			headerText : "품목명",
			width : 100,
			editable : false
		}, {
			dataField : "state",
			headerText : "상태",
			width : 100,
			editable : false
		}, {
			dataField : "version",
			headerText : "Rev.",
			width : 100,
			editable : false
		}, {
			dataField : "creator",
			headerText : "등록자",
			width : 100,
			editable : false
		} ]
	}, {
		headerText : "변경후",
		children : [ {
			dataField : "name",
			headerText : "품목명",
			width : 100,
			editable : false
		}, {
			dataField : "state",
			headerText : "상태",
			width : 100,
			editable : false
		}, {
			dataField : "version",
			headerText : "Rev.",
			width : 100,
			editable : false
		}, {
			dataField : "creator",
			headerText : "등록자",
			width : 100,
			editable : false
		} ]
	}, {
		headerText : "BOM",
		children : [ {
			dataField : "",
			headerText : "비교",
			width : 100,
			editable : false
		}, {
			dataField : "",
			headerText : "보기",
			width : 100,
			editable : false
		} ]
	}, {
		dataField : "nextPart",
		visible : false
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGridChangePart(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode: true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			rowCheckToRadio : true,
		}
		changePartGridID = AUIGrid.create("#grid_changePart", columnLayout, props);
		AUIGrid.setGridData(changePartGridID, <%=json%>);
	}
	
	$(function() {
		checkDummy();
		//구성원 접기/펼치기
		$("#include_ChangePartViewToggle").click(function() {
			var divId = $(this).attr('alt');
			if ( $( "#" + divId ).is( ":hidden" ) ) {
				$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
			}else{
				$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
			}
			$("#" + divId).slideToggle();
		})
	})

	function bomView2(oid){
		var str = getURLString("part", "PartTree", "do") + "?oid="+oid;
	    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	    leftpos = (screen.width - 1000)/ 2;
	    toppos = (screen.height - 600) / 2 ;
	    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
	    var newwin = window.open( str , "viewBOM", opts+rest);
	    newwin.focus();	    
	}

	function ecaBomCompare(oid){
		var str = "/Windchill/netmarkets/jsp/structureCompare/StructureCompare.jsp?oid="+oid+"&ncId=2374138740478986248&locale=ko"
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1000)/ 2;
		toppos = (screen.height - 600) / 2 ;
		rest = "width=1300,height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open( str , "compareBOM", opts+rest);
		newwin.focus();
		
	}

	$(function(){
		$('#checkDummy').click(function() {
			checkDummy();
		})
	})

	function checkDummy(){
		status = $("#true").css("display");
		if (status == "none") {
		    //$("#true").css("display","");
		    $("[name=true]").each(function(idx){   
		    	$(this).css("display","");
	      	});

		}else {
		    //$("#true").css("display","none");
			 $("[name=true]").each(function(idx){   
		    	$(this).css("display","none");
	      	});
		}
	}
</script>