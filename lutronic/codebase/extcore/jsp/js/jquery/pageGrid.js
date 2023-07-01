// <SCRIPT language=JavaScript src="/Windchill/jsp/grid/ysGrid.js"></SCRIPT>
 /**
 * @(#)	common.js
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @author ysjung@e3ps.com
 */
var totalPageMsg;
var totalSizeMsg;
var rowViewMsg;
var viewCntMsg;

function gridPageMsgSet(pageMsg,sizeMsg,viewMsg,cntMsg){
	alert("AAAA")
	totalPageMsg = pageMsg;
	totalSizeMsg = sizeMsg;
	rowViewMsg = viewMsg;
	viewCntMsg = cntMsg;
}

var defaultdefaultTableList;
function initPage(tblist1,totalSize,currentPage, sessionOid){
	alert(currentPage);
	defaultTableList = tblist1;
//	 alert("tblist1::"+tblist1+",totalSize::"+totalSize+",currentPage"+currentPage);  
   // 변수로 그리드아이디, 총 데이터 수, 현재 페이지를 받는다
   if(currentPage==""){
       var currentPage = $('#'+tblist1).getGridParam('page');
   }
   // 한 페이지에 보여줄 페이지 수 (ex:1 2 3 4 5)
   var pageCount = 10;
   // 그리드 데이터 전체의 페이지 수
   var totalPage = Math.ceil(totalSize/$('#'+tblist1).getGridParam('rowNum'));
   // 전체 페이지 수를 한화면에 보여줄 페이지로 나눈다.
   var totalPageList = Math.ceil(totalPage/pageCount);
   // 페이지 리스트가 몇번째 리스트인지
   var pageList=Math.ceil(currentPage/pageCount);
  
   // 페이지 리스트가 1보다 작으면 1로 초기화
   if(pageList<1) pageList=1;
   // 페이지 리스트가 총 페이지 리스트보다 커지면 총 페이지 리스트로 설정
   if(pageList>totalPageList) pageList = totalPageList;
   // 시작 페이지
   var startPageList=((pageList-1)*pageCount)+1;
   // 끝 페이지
   var endPageList=startPageList+pageCount-1;
  	   
   // 시작 페이지와 끝페이지가 1보다 작으면 1로 설정
   // 끝 페이지가 마지막 페이지보다 클 경우 마지막 페이지값으로 설정
   if(startPageList<1) startPageList=1;
   if(endPageList>totalPage) endPageList=totalPage;
   if(endPageList<1) endPageList=1;
  
   // 페이징 DIV에 넣어줄 태그 생성변수
   var pageInner="<table border=0 cellspacing=0 cellpadding=0 width=100% align=center bgcolor=white><tr bgcolor=white>";
   pageInner += "<td class='small' width=200><span class='small'>["+totalPageMsg+":"+totalPage+"]["+totalSizeMsg+":"+totalSize+"]</span></td>";
   /////////////////////////////////
   
   
	pageInner +="  <td>";
	pageInner +="		<table border=0 align=center cellpadding=0 cellspacing=0  bgcolor=white>";
	pageInner +="			<tr  bgcolor=white>";
	pageInner +="				<td width='30' align='center'>";
	// 페이지 리스트가 1이나 데이터가 없을 경우 (링크 빼고 흐린 이미지로 변경)
	if(pageList<2){
		pageInner+=" <img src='/Windchill/extCore/dsec/js/jqgrid/images/base_design/BBS_start_d.gif'></td>";
		pageInner+=" <td width='1' bgcolor='#dddddd'></td>";
		pageInner+=" <td width='30' class='quick' align='center'><img src='/Windchill/extCore/dsec/js/jqgrid/images/base_design/BBS_prev_d.gif'></td>";
		pageInner+=" <td width='1' bgcolor='#dddddd'></td>";
		
   }
   // 이전 페이지 리스트가 있을 경우 (링크넣고 뚜렷한 이미지로 변경)
   if(pageList>1){
		pageInner+="<a class='first' href='javascript:firstPage("+sessionOid+")'><img src='/Windchill/extCore/dsec/js/jqgrid/images/base_design/BBS_start.gif'></a>";
		pageInner+="</td>";
		pageInner+="				<td width='1' bgcolor='#dddddd'></td>";
		pageInner+="				<td width='30' class='quick' align='center'><a class='pre' href='javascript:prePage("+totalSize+","+sessionOid+")' class='smallblue'><img src='/Windchill/extCore/dsec/js/jqgrid/images/base_design/BBS_prev.gif' border='0' align='middle'></a></td>";
		pageInner+="				<td width='1' bgcolor='#dddddd'></td>";
   }
	   
   
	// 페이지 숫자를 찍으며 태그생성 (현재페이지는 강조태그)
	for(var i=startPageList; i<=endPageList; i++){
		pageInner += "<td style='width:20px;text-align:center;cursor:pointer' onMouseOver='this.style.background=\"#ECECEC\"' OnMouseOut='this.style.background=\"\"' class='nav_on' OnClick=\"javascript:goPage("+(i)+","+sessionOid+")\" >";

		if(i==currentPage){
			pageInner += "<b>"+(i)+"</b>";
		}else{
			pageInner += (i);
		}
		pageInner += "</td>";
	}
   
	// 다음 페이지 리스트가 있을 경우
	if(totalPageList>pageList){
		pageInner+="				<td width='1' bgcolor='#dddddd'></td>";
		pageInner+="				<td width='30' align='center'><a href='javascript:nextPage("+totalSize+","+sessionOid+")' class='smallblue'><img src='/Windchill/extCore/dsec/js/jqgrid/images/base_design/BBS_next.gif' border='0' align='middle'></a></td>";
		pageInner+="				<td width='1' bgcolor='#dddddd'></td>";
		pageInner+="				<td width='30'align='center'>";
		pageInner+="<a href='javascript:lastPage("+totalPage+","+sessionOid+")' class='small'><img src='/Windchill/extCore/dsec/js/jqgrid/images/base_design/BBS_end.gif' border='0' align='middle'></a>";
	}
	// 현재 페이지리스트가 마지막 페이지 리스트일 경우
	if(totalPageList==pageList){
		pageInner+="				<td width='1' bgcolor='#dddddd'></td>";
		pageInner+="				<td width='30' align='center'><img src='/Windchill/extCore/dsec/js/jqgrid/images/base_design/BBS_next_d.gif'></td>";
		pageInner+="				<td width='1' bgcolor='#dddddd'></td>";
		pageInner+="				<td width='30'align='center'><img src='/Windchill/extCore/dsec/js/jqgrid/images/base_design/BBS_end_d.gif'></a>";
	}
	
	pageInner +="				</td>";
	pageInner +="			</tr>";
	pageInner +="		</table>";
	pageInner +="  </td>";
   
   
   ////////////////////////////////////////////////
	pageInner +=" <td class='small' align='right'  width=200>"+rowViewMsg+" <select id='pSize' name='pSize' onchange='javascript:selectpSize()'>";
	pageInner +=" <option value='10' >10</option>";
	pageInner +=" <option value='15' >15</option>";
	pageInner +=" <option value='20' >20</option>";
	pageInner +=" <option value='30' >30</option>";
	pageInner +=" <option value='50' >50</option>";
	pageInner +=" <option value='100' >100</option>";
	pageInner +="</select> "+viewCntMsg+"</td>";
	pageInner +="</tr>";
	pageInner +="</table>";

   // 페이징할 DIV태그에 우선 내용을 비우고 페이징 태그삽입
   $("#"+defaultTableList).html("");
   $("#"+defaultTableList).append(pageInner);
   
   //var rowNum = $('#'+tblist1).getGridParam('rowNum');
   //$("select[name='pSize']").val(rowNum);
   //$("option[value="+rowNum+"]").attr('selected','selected');
}


//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

function firstPage(sessionOid){
	$("#"+defaultTableList).jqGrid('setGridParam', {
		postData:{
		cPage:1,
		sessionOid : sessionOid,
		searchChk : "false"
		}
	}).trigger("reloadGrid");
}
//그리드 이전페이지 이동
function prePage(totalSize, sessionOid){
	var currentPage = parseInt($('#'+defaultTableList).getGridParam('page'));
	$("#currentPage").val(currentPage);
	var pageCount = 10;
	currentPage-=pageCount;
	pageList=Math.ceil(currentPage/pageCount);
	currentPage=(pageList-1)*pageCount+pageCount;
	initPage(defaultTableList,totalSize,currentPage, sessionOid);
	$("#"+defaultTableList).jqGrid('setGridParam', {
		postData:{
		cPage:currentPage,
		sessionOid : sessionOid,
		searchChk : "false"
		}
	}).trigger("reloadGrid");

}
//그리드 다음페이지 이동    
function nextPage(totalSize, sessionOid){
	var currentPage = parseInt($('#'+defaultTableList).getGridParam('page'));

	$("#currentPage").val(currentPage);
	var pageCount = 10;
	currentPage+=pageCount;
	pageList=Math.ceil(currentPage/pageCount);
	$("#pageList").val(pageList);
	currentPage=(pageList-1)*pageCount+1;
	initPage(defaultTableList,totalSize,currentPage, sessionOid);
	$("#"+defaultTableList).jqGrid('setGridParam', {
		postData:{
		cPage : currentPage,
		sessionOid : sessionOid,
		searchChk : "false"
		}
	}).trigger("reloadGrid");
}
//그리드 마지막페이지 이동
function lastPage(totalSize, sessionOid){
	var currentPage = $('#'+defaultTableList).getGridParam('page');
	$("#currentPage").val(currentPage);
	$("#"+defaultTableList).jqGrid('setGridParam', {
		postData:{
			cPage : totalSize,
			sessionOid : sessionOid,
			searchChk : "false"
		}
	}).trigger("reloadGrid");
}
//그리드 페이지 이동
function goPage(num, sessionOid){
	//var currentPage = $('#'+defaultTableList).getGridParam('page');
	$("#"+defaultTableList).setGridParam({
		postData:{
			cPage : num,
			sessionOid : sessionOid,
			searchChk : "false"
	   }
	}).trigger("reloadGrid");
	$("#"+defaultTableList).navGrid("#pager",{refreshstate:'current'});
	$("#page").val(num);
}
function selectpSize(){
	
	var pSize = $("select[name='pSize']").val();
	$("#"+defaultTableList).jqGrid('setGridParam', {
		rowNum : pSize,
		postData:{
			pSize : pSize,
			searchChk : "false",
			cPage : 1
		}
	}).trigger("reloadGrid");
	
	//$("option[value='"+pSize+"']").attr('selected','selected');
	$("#"+defaultTableList).navGrid("#pager",{refreshstate:'current'});
}