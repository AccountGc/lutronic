/**
 *
 */
var mygrid;

var defaultImgPath;
var labelFIRST;
var labelFINALLY;
var labelPRIOR;
var labelNEXT;

function gfn_InitDhtmlPaging(path,first,last,prior,next) {
	defaultImgPath = path;
	labelFIRST = first;
	labelFINALLY = last;
	labelPRIOR = prior;
	labalNEXT = next;
}

function gfn_InitCalendar(inputSTR, buttonSTR){
	var myCalendar = new dhtmlXCalendarObject(
		    {input:inputSTR,button:buttonSTR}
		);

	myCalendar.setDateFormat("%Y-%m-%d");
	myCalendar.setWeekStartDay(7);
}

/*
 *  입력 데이터를 오늘 날짜와 비교 오늘날짜 작으면 false 리턴
 */
function gfn_compareDateToDay(inputDate) {
	var date = new Date();
	var year = date.getFullYear();
	var month = date.getMonth()+1;
	if((month+"").length < 2){
		month = "0" + month;
	}
	var day = date.getDate();
	if((day+"").length < 2){
		day = "0" + day;
	}
	
	var today = year + "-" + month + "-" + day;
	return gfn_compareDate(today,inputDate);
}

/*
 *  데이터1을 데이터2와 비교 데이터1이 크면 false
 */
function gfn_compareDate(standardDate, intputDate) {
	var sDateSplit = standardDate.split("-");
	var standardYear = sDateSplit[0];
	var standardMonth = sDateSplit[1];
	var standardDay = sDateSplit[2];
	
	var standard = standardYear + "" + standardMonth + "" + standardDay;
	
	var iDateSplit = intputDate.split("-");
	var inputYear = iDateSplit[0];
	var inputMonth = iDateSplit[1];
	var inputDay = iDateSplit[2];
	
	var input = inputYear + "" + inputMonth + "" + inputDay;
	return standard <= input;
	
}

//page set
function gfn_SetPaging(vArr,pagingId){
	
	var rows       = vArr[0];
	var formPage    = vArr[1];
	var totalCount  = vArr[2];
	var totalPage   = vArr[3];
	var currentPage = vArr[4];
	var startPage   = vArr[5];
	var endPage     = vArr[6];
	var sessionId   = vArr[7];

	document.getElementById("rows"    	).value = rows  			 ;
	document.getElementById("formPage"   	).value = formPage  		 ;
	document.getElementById("sessionId"    ).value = sessionId    		 ;

	//document.getElementById("totalCount"	).innerHTML = totalCount	 ;
	//document.getElementById("totalPage" 	).innerHTML = totalPage 	 ;
	//document.getElementById("currentPage" 	).innerHTML = currentPage 	 ;

	var tempVal = "";
	tempVal += "\n totalPage   => "+totalPage  ;
	tempVal += "\n startPage   => "+startPage  ;
	tempVal += "\n endPage     => "+endPage    ;
	tempVal += "\n currentPage => "+currentPage;
	tempVal += "\n totalCount  => "+totalCount ;
	
	
	var pageHtml = "";
	
	pageHtml += "<table border=0 cellspacing=0 cellpadding=0 width=100% align=center bgcolor=white>";
	pageHtml += "	<tr bgcolor=white>";
	pageHtml += "		<td class='small' width=200><span class='small'>[Total Page : "+totalPage+"][Total Content : "+totalCount+"]</span></td>"
	pageHtml += "		<td>";
	pageHtml += "			<table border=0 align=center cellpadding=0 cellspacing=0  bgcolor=white>";
	pageHtml += "				<tr  bgcolor=white>";
	pageHtml += "					<td width='30' align='center'>";

	if ( currentPage >= 1 && totalPage > 1) {
		pageHtml += "					<a href='javascript:lfn_GotoPage(1);'><img src='/Windchill/jsp/portal/images/base_design/BBS_start.gif' alt='" + labelFIRST + "'></a>";
	} else {
		pageHtml += "					<img src='/Windchill/jsp/portal/images/base_design/BBS_start.gif' alt='" + labelFIRST +"'>";
	}

	pageHtml += "					</td>";
	pageHtml += "					<td width='1' bgcolor='#dddddd'></td>";
	pageHtml += "					<td width='30' class='quick' align='center'>";
	
	if ( startPage > 1 && totalPage > 1 ) {
		pageHtml += "					<a href='javascript:lfn_GotoPage("+(startPage-1)+");'><img src='/Windchill/jsp/portal/images/base_design/BBS_prev.gif' alt='" + labelPRIOR + "'></a>";
	} else {
		pageHtml += "					<img src='/Windchill/jsp/portal/images/base_design/BBS_prev.gif' alt='" + labelPRIOR + "'>";
	}
	
	pageHtml += "					</td>";
	pageHtml += "					<td width='1' bgcolor='#dddddd'></td>";

	if ( startPage >= 1 && totalPage > 1 ) {
		for ( var i=startPage; i<=endPage; i++) {
			if ( i == currentPage ) {
				pageHtml += "			<td style='padding:2 8 0 7;' class='nav_on'>";
				pageHtml += "			&nbsp;<B> "+i+" </B>&nbsp;";
				continue;
			} else {
				pageHtml += "			<td style='padding:2 8 0 7; cursor:hand' onMouseOver='this.style.background=\"#ECECEC\"' OnMouseOut='this.style.background=\"\"' class='nav_on'>";
				pageHtml += "			&nbsp;<a href='javascript:lfn_GotoPage("+i+");'> "+i+" </a>&nbsp;";
			}
			pageHtml += "			</td>";
		}
	} else {
		pageHtml += "				<td style='padding:2 8 0 7; cursor:hand' onMouseOver='this.style.background=\"#ECECEC\"' OnMouseOut='this.style.background=\"\"' class='nav_on'>";
		pageHtml += "					&nbsp;<B> 1 </B>&nbsp;";
		pageHtml += "				</td>";
	}
	
	pageHtml += "					<td width='1' bgcolor='#dddddd'></td>";
	pageHtml += "					<td width='30' align='center'>";

	if ( endPage < totalPage && totalPage > 1 ) {
		pageHtml += "					<a href='javascript:lfn_GotoPage("+(endPage+1)+");'><img src='/Windchill/jsp/portal/images/base_design/BBS_next.gif' alert='" + labelNEXT + "'></a>";
	} else {
		pageHtml += "					<img src='/Windchill/jsp/portal/images/base_design/BBS_next.gif' alt='" + labelNEXT + "'>";
	}

	pageHtml += "					</td>";
	pageHtml += "					<td width='1' bgcolor='#dddddd'></td>";
	pageHtml += "					<td width='30'align='center'>";
	
	if ( currentPage >= 1 && totalPage > 1 ) {
		pageHtml += "					<a href='javascript:lfn_GotoPage("+totalPage+");'><img src='/Windchill/jsp/portal/images/base_design/BBS_end.gif' alt='" + labelFINALLY + "'></a>";
	} else {
		pageHtml += "					<img src='/Windchill/jsp/portal/images/base_design/BBS_end.gif' alt='" + labelFINALLY + "'>";
	}
	
	pageHtml += "					</td>";
	pageHtml += "				</tr>";
	pageHtml += "			</table>";
	pageHtml += "		</td>";
	pageHtml += "		<td class='small' align='right'  width=200>";
	pageHtml += "			<select id='pageRow' name='pageRow' onchange= lfn_changeRow() >";
	pageHtml += "				<option value='10'" + (rows==10? "selected" : "") + " >" + 10 + "</option>";
	pageHtml += "				<option value='15'" + (rows==15? "selected" : "") + " >" + 15 + "</option>";
	pageHtml += "				<option value='20'" + (rows==20? "selected" : "") + " >" + 20 + "</option>";
	pageHtml += "				<option value='30'" + (rows==30? "selected" : "") + " >" + 30 + "</option>";
	pageHtml += "				<option value='50'" + (rows==50? "selected" : "") + " >" + 50 + "</option>";
	pageHtml += "				<option value='100'" + (rows==100? "selected" : "") + " >" + 100 + "</option>";
	pageHtml += "			</select>";
	pageHtml += "			Row View";
	pageHtml += "		</td>";
	pageHtml += "	</tr>";
	pageHtml += "</table>";
	
	document.getElementById(pagingId).innerHTML = "";
	document.getElementById(pagingId).innerHTML = pageHtml;

}
// 페이지 이동
function lfn_GotoPage(pageNum){
	document.getElementById("page").value = ""+pageNum;
	lfn_Search();
}

function lfn_changeRow() {
	$("#rows").val($("#pageRow").val());
	$("#page").val(1);
	lfn_Search();
}