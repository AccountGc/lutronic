// <SCRIPT language=JavaScript src="/Windchill/portal/js/common.js"></SCRIPT>
 /**
 * @(#)	common.js
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @author Seung-hwan Choi, skyprda@e3ps.com
 */

/**************************************************************
*                      MVC URL 설정하기
****************************************************************/
var webContext = "eSolution";
var getURLString = function() {
	var url = "/Windchill/" + webContext;
	for(var i=0; i<arguments.length; i++) {
		if(i==(arguments.length-1)) {
			url += ".";
		}else {
			url += "/";
		}
		url += arguments[i];
	}
	return url;
}

/**************************************************************
*                      NumberCode Parent oid 리스트 가져오기
****************************************************************/
function common_numberCodeList(type, parentOid, search) {
	var url	= getURLString("common", "numberCodeList", "do");
	return $.ajax({
		type:"POST",
		url: url,
		data: {
			codeType : type,
			parentOid : parentOid,
			search : search
		},
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = type + " ${f:getMessage('코드 목록 오류')}";
			alert(msg);
		}
	});
}

/**************************************************************
*                      NumberCode Parent code 리스트 가져오기
****************************************************************/
function common_numberCode_PCodeList(type, pCode, search) {
	var url	= getURLString("common", "numberParentCodeList", "do");
	return $.ajax({
		type:"POST",
		url: url,
		data: {
			codeType : type,
			pCode : pCode,
			search : search
		},
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = type + " ${f:getMessage('코드 목록 오류')}";
			alert(msg);
		}
	});
}

/**************************************************************
*                      NumberCode 리스트 가져오기
****************************************************************/
function partUnitList() {
	var url	= getURLString("part", "getQuantityUnit", "do");
	return $.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = type + " ${f:getMessage('코드 목록 오류')}";
			alert(msg);
		}
	});
}

/**************************************************************
*                      Input Number Only
****************************************************************/
function common_isNumber(evt, element) {

    var charCode = (evt.which) ? evt.which : event.keyCode;
    if ((charCode < 48 || charCode > 57))
        return false;

    return true;
} 

/**************************************************************
*                      Input English Only
****************************************************************/
function common_isEnglish(evt, element) {
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if ((charCode < 97 || charCode > 122) && 
    	(charCode < 65 || charCode > 90))
        return false;

    return true;
} 

/**************************************************************
*                      자동 완성기능 AJAX
****************************************************************/
function common_autoSearchName(codeType, value) {
	var url	= getURLString("common", "autoSearchName", "do");
	return $.ajax({
		type:"POST",
		url: url,
		data: {
			codeType : codeType,
			name : value
		},
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = type + " ${f:getMessage('코드 목록 오류')}";
			alert(msg);
		}
	});
}

/**************************************************************
*                      Submit Function
****************************************************************/
window.common_submit = function(module, url, formName, movePage) {
	var form = $("form[name=" + formName + "]").serialize();
	var url	= getURLString(module, url, "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: true,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('오류가 발생하였습니다.')}";
			alert(msg + "\n" + data.message);
		}
		,beforeSend: function() {
			gfn_StartShowProcessing();
        }
		,complete: function() {
			gfn_EndShowProcessing();
        }
		,success: function(data) {
			if(data.result) {
				//alert("${f:getMessage('등록 성공하였습니다.')}");
				document.location = getURLString("part", movePage, "do");
				submitSuccess(module, movePage);
			}else {
				submitFail(data.message);
				//alert("${f:getMessage('등록 실패하였습니다.')}" + "\n" + data.message);
			}
		}
	});
}

/**************************************************************
*                      Submit Function
****************************************************************/
window.common_documentType = function(documentType, searchType) {
	var url	= getURLString("common", "documentTypeList", "do");
	return $.ajax({
		type:"POST",
		url: url,
		data: {
			documentType : documentType,
			searchType : searchType
		},
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "Document Type" + " ${f:getMessage('코드 목록 오류')}";
			alert(msg);
		}
	});
}

window.resetFunction = function() {
	deleteTableTr('partTable');
	deleteTableTr('docTable');
	deleteTableTr('rohsTable');
	deleteTableTr('completePartTable');
	deleteTableTbody('active_table');
	PRIMARY_Upload.deleteSelect('all');
	SECONDARY_Upload.deleteSelect('all');
}

window.resetFunctionECO = function() {
	deleteTableTbody('active_table');
	ECO_Upload.deleteSelect('all');
	SECONDARY_Upload.deleteSelect('all');
}

window.resetFunctionECR = function() {
	ECR_Upload.deleteSelect('all');
	SECONDARY_Upload.deleteSelect('all');
}

window.resetFunctionEO = function() {
	deleteTableTr('completePartTable');
	deleteTableTbody('active_table');
	SECONDARY_Upload.deleteSelect('all');
}

window.resetFunctionRoHS = function() {
	deleteTableTr('partTable');
	deleteTableTr('rohsTable');
	var type_length = $('input[name=roleType]').length;
	for(var i=0; i<type_length; i++) {
		var roleType = $('input[name=roleType]').eq(i).val();
		if(roleType == 'ROHS1') ROHS1_Upload.deleteSelect('all');
		else if(roleType == 'ROHS2') ROHS2_Upload.deleteSelect('all');
		else if(roleType == 'ROHS3') ROHS3_Upload.deleteSelect('all');
		else if(roleType == 'ROHS4') ROHS4_Upload.deleteSelect('all');
		else if(roleType == 'ROHS5') ROHS5_Upload.deleteSelect('all');
		else if(roleType == 'ROHS6') ROHS6_Upload.deleteSelect('all');
		else if(roleType == 'ROHS7') ROHS7_Upload.deleteSelect('all');
		else if(roleType == 'ROHS8') ROHS8_Upload.deleteSelect('all');
		else if(roleType == 'ROHS9') ROHS9_Upload.deleteSelect('all');
		else if(roleType == 'ROHS10') ROHS10_Upload.deleteSelect('all');
		else if(roleType == 'ROHS11') ROHS11_Upload.deleteSelect('all');
		else if(roleType == 'ROHS12') ROHS12_Upload.deleteSelect('all');
		else if(roleType == 'ROHS13') ROHS13_Upload.deleteSelect('all');
		else if(roleType == 'ROHS14') ROHS14_Upload.deleteSelect('all');
		else if(roleType == 'ROHS15') ROHS15_Upload.deleteSelect('all');
		else if(roleType == 'ROHS16') ROHS16_Upload.deleteSelect('all');
		else if(roleType == 'ROHS17') ROHS17_Upload.deleteSelect('all');
		else if(roleType == 'ROHS18') ROHS18_Upload.deleteSelect('all');
		else if(roleType == 'ROHS19') ROHS19_Upload.deleteSelect('all');
		else if(roleType == 'ROHS20') ROHS20_Upload.deleteSelect('all');
	}
	deleteTableTbody('filesBody');
}

window.deleteTableTbody = function(tBodyID) {
	var tBody_length = $('#'+tBodyID+' tr').length;
	for(var i=(tBody_length-1); i>=0; i--) {
		$('#'+tBodyID+' tr').eq(i).remove();
	}
}

window.deleteTableTr = function(tableId) {
	var table_length = $('#'+tableId+' tr').length;
	for(var i=(table_length-1); i>0; i--) {
		$('#'+tableId+' tr').eq(i).remove();
	}
}

function disabledAllBtn()
{
	var f = document.forms[0];
    for(var i=0 ; i<f.length ; i++){
		if(f[i].type=="button")
			f[i].disabled = true;
	}
	f = document.getElementsByTagName('A');
	for(var i=0 ; i<f.length ; i++){
		f[i].disabled = true;
		//f[i].href = '#';
	}
}
 
function enabledAllBtn()
{
	var f = document.forms[0];
    for(var i=0 ; i<f.length ; i++){
		if(f[i].type=="button")
			f[i].disabled = false;
	}
	f = document.getElementsByTagName('A');
	for(var i=0 ; i<f.length ; i++){
		f[i].disabled = false;
		//f[i].href = '#';
	}
} 
 
 
function isNullData(str)
{
	if(str.length == 0)
		return true;
	for(var i=0;i<str.length;i++)
		if(str.charCodeAt(i) != 32)
			return false;
	return true;
}

function checkField(obj, fieldName)
{
	if(isNullData(obj.value))
	{
		alert( fieldName + unescape("%uC744%28%uB97C%29%20%uC785%uB825%uD558%uC138%uC694"));
		obj.focus();
		return true;
	}
	return false;
}

function checkFieldLength(obj, limit, fieldName)
{
	if(!isNullData(obj.value))
	{
		if(obj.value.length > limit)
		{
			alert( fieldName + " " +limit + unescape("%uC790 %uAE4C%uC9C0%20%uC785%uB825%uAC00%uB2A5%uD569%uB2C8%uB2E4"));
			obj.createTextRange();
			obj.select();
			return true;
		}
	}
	return false;
}

function openWindow(url, name, width, height) 
{ 
	getOpenWindow(url, name, width, height);
}

function openWindow2(url, name, width, height) 
{ 
	getOpenWindow2(url, name, width, height);
}

function getOpenWindow(url, name, width, height)
{
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	if(width == 'full')
	{
//		rest = "width=" + (screen.availWidth-10) + ",height=" + (screen.availHeight-60)+',left=0,top=0';
		
		leftpos = (screen.availWidth - screen.availWidth *0.9 )/ 2; 
		toppos = (screen.availHeight - screen.availHeight *0.9 - 30 ) / 2 ; 

		rest = "width=" + (screen.availWidth * 0.9 ) + ",height=" + (screen.availHeight * 0.9 )+',left=' + leftpos + ',top=' + toppos;
	}
	else
	{
		leftpos = (screen.availWidth - width)/ 2; 
		toppos = (screen.availHeight - 60 - height) / 2 ; 

		rest = "width=" + width + ",height=" + height+',left=' + leftpos + ',top=' + toppos;
	}
	
	var newwin = open( url , name, opts+rest);
	newwin.focus();
	return newwin;
}

function getOpenWindow2(url, name, width, height)
{
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
	if(width == 'full')
	{
//		rest = "width=" + (screen.availWidth-10) + ",height=" + (screen.availHeight-60)+',left=0,top=0';
		
		leftpos = (screen.availWidth - screen.availWidth *0.9 )/ 2; 
		toppos = (screen.availHeight - screen.availHeight *0.9 - 30 ) / 2 ; 

		rest = "width=" + (screen.availWidth * 0.9 ) + ",height=" + (screen.availHeight * 0.9 )+',left=' + leftpos + ',top=' + toppos;
	}
	else
	{
		leftpos = (screen.availWidth - width)/ 2; 
		toppos = (screen.availHeight - 60 - height) / 2 ; 

		rest = "width=" + width + ",height=" + height+',left=' + leftpos + ',top=' + toppos;
	}
	
	var newwin = open( url , name, opts+rest);
	newwin.focus();
	return newwin;
}


function reSubmit(){
	 document.forms[0].submit();
}

function oneClick(check)
{
	if ( !check.checked ) return;
	var chk=document.forms[0];
	str = check.value;
	for ( var i = 0 ; i < chk.length ; i++ ) {
		if ( chk[i].type == "checkbox" ) 
			if ( str != chk[i].value ) 
				chk[i].checked=false;
	}
}

function getSelect()
{
    var chk=document.forms[0];
    var str=""; 
    for(var i=0;i<chk.length;i++){
		if(chk[i].type=="checkbox"){
			if(chk[i].checked==true){
			   str = chk[i].value;
			   break;
			}
		}
	}
	return str;
 }

function closeWindow()
{
	if( opener != null ) self.close();
	else history.back();
}

function isNotNumData(str) {
	if(str.length == 0)
		return true;
	for(var i=0;i<str.length;i++){
		var sss = str.charCodeAt(i);
		if(48 > sss || 57 < sss  ){
			return true;
		}
	}
	return false;
}

function IntegerParseInt(str){ //parseInt 에서 변경
	var result = 0;
	var level = 1;
	for(var i=0; i<str.length-1; i++)
		level *= 10;
	for(var i=0;i<str.length;i++){
		var sss = str.charCodeAt(i)-48;
		result += (level * sss);
		level = level / 10;
	}
	return result;
}

var screenWidth = screen.availWidth/2-150;
var screenHeight = screen.availHeight/2-75;

function openSameName(url,width,height,state){
	var opt = launchCenter(width,height);
	if ( state.length > 0 ) opt = opt + ", " + state
	var windowWin = window.open(url,"newwindow",opt);
	windowWin.resizeTo(width,height);
	windowWin.focus();
}

function openOtherName(url,name,width,height,state){
	var opt = launchCenter(width,height);
	if ( state.length > 0 ) opt = opt + ", " + state
	var windowWin = window.open(url,name,opt);
	windowWin.resizeTo(width,height);
	windowWin.focus();
}

function launchCenter(width,height) {
  var str = "height=" + height + ",innerHeight=" + height;
  str += ",width=" + width + ",innerWidth=" + width;
  if (window.screen) {
    var ah = screen.availHeight - 30;
    var aw = screen.availWidth - 10;

    var xc = (aw - width) / 2;
    var yc = (ah - height) / 2;

    str += ",left=" + xc + ",screenX=" + xc;
    str += ",top=" + yc + ",screenY=" + yc;
  }
  return str;
}
function addBgColorEvent()
{
	var f = document.forms[0];
	for (var i=f.length-1 ; i>-1 ; i-- )
	{
		f[i].onfocus = changeColor1;
		f[i].onblur = changeColor2;
	}
}
function changeColor1()
{
	event.srcElement.style.backgroundColor='#efefef';
}
function changeColor2()
{
	event.srcElement.style.backgroundColor='#ffffff';
}

function printTitle(_title)
{
	document.write("<table border=0 cellpadding=0 cellspacing=0 >");
	document.write("<tr>");
	document.write("<td><img src=/Windchill/jsp/portal/images/title2_left.gif></td>");
	document.write("<td background=/Windchill/jsp/portal/images/title_back.gif>");
	document.write(_title);
	document.write("</td>");
	document.write("<td><img src=/Windchill/jsp/portal/images/title2_right.gif></td>");
	document.write("</tr>");
	document.write("</table>");
}

// get length of String
function getBytes(_value)
{
	var count = 0;
	var tmpStr = new String(_value);
	
	var onechar;
	for (var i=tmpStr.length-1 ; i>-1 ; i-- )
	{
    	onechar = tmpStr.charAt(i);
    	if (escape(onechar).length > 4) count += 3;
	    else count += 1;
  	}
	return count;
}

function selectOptionTrue(obj, val)
{
	if(obj == null || val == null || val.length==0) return;
	
	for(var i=obj.length-1 ; i>-1 ; i--)
	{
		if(obj[i].value == val) 
		{
			obj[i].selected = true;
			break;
		}
	}
}

function selectCheckTrue(obj, val)
{
	if(obj == null || val == null || val.length==0) return;
	
	if(obj.length == null)
	{
		obj.checked = true;
	}
	else
	{
		for(var i=obj.length-1 ; i>-1 ; i--)
		{
			if(obj[i].value == val) 
			{
				obj[i].checked = true;
				break;
			}
		}
	}
}

function COMMON_openPopup(nw,nh) {
	var opt,args = COMMON_openPopup.arguments;

	opt = "width="+nw+",height="+nh+",scrollbars=yes,resizable=yes"+
		( (args.length>2)?",left="+args[2]:"")+
		( (args.length>3)?"top="+args[3]:"");
	return window.open("about:blank","",opt);
}

function common_CheckStrLength(obj, maxmsglen){

	var temp;
	var f = obj.value.length;
	var msglen = maxmsglen; //최대 길이
	var tmpstr = "";
	var strlen;

// 초기 최대길이를 텍스트 박스에 뿌려준다.
  for(k=0;k<f;k++){
	  temp = obj.value.charAt(k);
	  if(escape(temp).length > 4)
		  msglen -= 1;
	  else
		  msglen--;
	  if(msglen < 0){
		  alert("총 영문 / 한글 "+maxmsglen+"자 까지 쓰실 수 있습니다.");
		  obj.value = tmpstr;
		  break;
	  }
	  else{
		  tmpstr += temp;
	  }
  }
}

/*=============================================================================* 
 * 입력값이 숫자인지를 확인한다. 
 * param : obj 입력폼
 * style='IME-MODE: disabled' : 입력창에 한글입력방지
 *============================================================================*/
function SetNum(obj){
	val=obj.value;
	re=/[^0-9]/gi;
	obj.value=val.replace(re,"");
}

/**
*
*/
function viewProcessHistoryInfo(oid){
	openWindow("/Windchill/jsp/groupware/workprocess/ProcessHistoryInfo.jsp?oid="+oid+"&isPopup=true", "ProcessHistoryInfo", 845, 245);
}

function setButtonTag(_name, _width, _script, _class, _color)
{
    var sb = "";
    var rwidth = _name.length * 8;
    if (rwidth > _width) _width = rwidth;
    
    if(_color==null)_color = "4E4E4E";

    sb = "<a style='FONT-SIZE: 8pt;' onclick=\"" + _script + "\" style='cursor:hand;color="+_color+"' onMouseOver=\"all._text.style.color='#0393c8'\" onMouseOut=\"all._text.style.color='"+_color+"'\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
    sb += "<tr>";
    sb += "<td width='7'><img src='/Windchill/jsp/portal/img/btn_left.gif' alt='' width='7' height='20'></td>";
    sb += "<td valign='middle' background='/Windchill/jsp/portal/img/btn_mid.gif'>";
    sb += "<table align='center' border='0' cellspacing='0' cellpadding='0'>";
    sb += "<tr>";
    sb += "<td><div id='_text' align='center'>" + _name + "</div></td>";
    sb += "</tr>";
    sb += "</table>";
    sb += "</td>";
    sb += "<td width='12'><img src='/Windchill/jsp/portal/img/btn_right.gif' alt='' width='12' height='20'></td>";
    sb += "</tr>";
    sb += "</table></a>";
    document.write(sb);
}

function setButtonTagForScript(_name, _width, _script, _class)
{
    var sb = "";
    var rwidth = _name.length * 8;
    if (rwidth > _width) _width = rwidth;

    sb = "<a style='FONT-SIZE: 8pt;' onclick=\"" + _script + "\" style='cursor:hand;' onMouseOver=\"all._text.style.color='#0393c8'\" onMouseOut=\"all._text.style.color='#4E4E4E'\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
    sb += "<tr>";
    sb += "<td width='7'><img src='/Windchill/jsp/portal/img/btn_left.gif' alt='' width='7' height='20'></td>";
    sb += "<td valign='middle' background='/Windchill/jsp/portal/img/btn_mid.gif'>";
    sb += "<table align='center' border='0' cellspacing='0' cellpadding='0'>";
    sb += "<tr>";
    sb += "<td><div id='_text' align='center'>" + _name + "</div></td>";
    sb += "</tr>";
    sb += "</table>";
    sb += "</td>";
    sb += "<td width='12'><img src='/Windchill/jsp/portal/img/btn_right.gif' alt='' width='12' height='20'></td>";
    sb += "</tr>";
    sb += "</table></a>";
    return sb;
}

function setButtonTag3D(_name, _width, _script, _class)
{
    var sb = "";
    var rwidth = _name.length * 8;
    if (rwidth > _width) _width = rwidth;
    
    sb = "<a onclick=\"" + _script + "\" style='cursor:pointer;' onMouseOver=\"all._text.style.color='#0393c8'\" onMouseOut=\"all._text.style.color='#4E4E4E'\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
    sb += "<tr>";
    sb += "<td width='8'><img src='/Windchill/jsp/portal/img/btn_left_01.gif' alt='' width='8' height='22'></td>";
    sb += "<td valign='middle' background='/Windchill/jsp/portal/img/btn_mid_01.gif'>";
    sb += "<table align='center' border='0' cellspacing='0' cellpadding='0'>";
    sb += "<tr>";
    sb += "<td><div id='_text' align='center'>" + _name + "</div></td>";
    sb += "</tr>";
    sb += "</table>";
    sb += "</td>";
    sb += "<td width='12'><img src='/Windchill/jsp/portal/img/btn_right_01.gif' alt='' width='12' height='22'></td>";
    sb += "</tr>";
    sb += "</table></a>";
    document.write(sb);
}

function setButtonTag3DL(_name, _width, _script, _class)
{
    var sb = "";
    var rwidth = _name.length * 8;
    if (rwidth > _width) _width = rwidth;
    
    sb = "<a onclick=\"" + _script + "\" style='cursor:hand;' onMouseOver=\"all._text.style.color='#0393c8'\" onMouseOut=\"all._text.style.color='#4E4E4E'\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
    sb += "<tr>";
    sb += "<td width='12'><img src='/Windchill/jsp/portal/img/btn_right_01.gif' alt='' width='12' height='22'></td>";
    sb += "<td width='8'><img src='/Windchill/jsp/portal/img/btn_left_01.gif' alt='' width='8' height='22'></td>";
    sb += "<td valign='middle' background='/Windchill/jsp/portal/img/btn_mid_01.gif'>";
    sb += "<table align='center' border='0' cellspacing='0' cellpadding='0'>";
    sb += "<tr>";
    sb += "<td><div id='_text' align='center'>" + _name + "</div></td>";
    sb += "</tr>";
    sb += "</table>";
    sb += "</td>";
    sb += "</tr>";
    sb += "</table></a>";
    document.write(sb);
}
function SWStart(UserID, SWnUrl, drmApplyCheck, filename) {
	if (drmApplyCheck = true){
		var authkey = "ELCT-EYOE-TUIT-QDOQ";

		/***** 1. SWorkGetStateOfSWInstall *****/
		// -- S-WORK 설치 유무 체크
		// -- 0: 설치/동작 정상, -1:동작안함, -2:설치 안됨.
		var ret = SWELCtrl.SWorkGetStateOfSWInstall();
		if (ret == 0){
			/***** 2. SWorkGetDetailStatus *****/
	 	    // -- 0:로그인하세요, 1:로그인하세요, 2:잠시 후 다시 로그인하세요, 3:정상
		    // -- 나머지 : 잠시 후 다시 로그인 하세요.
			var ret1 = SWELCtrl.SWorkGetDetailStatus(authkey);

			if (ret1 == 0 || ret1 == 1){
				//alert("로그인하세요");
				DSXRATC1.RequestS6 ("DSG_START_LOGIN",UserID,"","",1,1);
			}else if (ret1 == 2){
				alert("로그인 중입니다. 잠시 후 다시 로그인하세요");
			}else if (ret1 == 3){
				/***** 3. SWUserInfo *****/
				// -- 사용자 정보를 확인 후 정보가 일치하면 다음단계로
				var ret2 = SWELCtrl.SWorkGetUserInfo(authkey);
				var SWid = SWELCtrl.UserID;

				if (SWid == UserID){  // - S-Work 설치 시 입력한 사용자 ID 와 PLM 로그인 ID의 동일여부 판단.
				    /***** 4. SWorkGetDriveLetterString *****/
					// -- 보안 드라이브 정보 확인(Z:)
					// ????? 보안 드라이브 정보가 확인이 안되었을 경우 종료? pass??
					var ret3 = SWELCtrl.SWorkGetDriveLetterString(authkey);
					var drive = SWELCtrl.OutBuf;

					/***** 5. SWorkSetProcessEx *****/
					// -- IE 권한 제어
					var ret4 = SWELCtrl.SWorkSetProcessEx(authkey, 2, 1);
					if (ret4 == "0"){
						if (SWnUrl != "") DSDown11.Down3("http://"+document.location.host+"/"+SWnUrl, filename, true, false);
					}else{
						alert ("IE 권한 제어에 실패 하였습니다.");
					}
				/***** 3. SWUserInfo *****/
				// 3. 사용자 정보가 일치하지 않음.
				}else{				
					alert ("사용자 정보가 일치하지 않습니다. \n 사용자 정보 확인 후 다시 시도하시기 바랍니다.");
				}
			/***** 2. SWorkGetDetailStatus *****/
			}else if (ret1 == 111){	
				alert("키 인증에 실패하였습니다. 담당자에게 확인하세요.");
			}else if (ret1 == 4){
	         alert("현재 오프라인 로그인 상태 입니다.");
			}else if (ret1 == 7){
	         alert("현재 로그아웃 진행상태 입니다.");
			}
		/***** 1. SWorkGetStateOfSWInstall *****/
		}else if (ret == -1){
			alert("S-Work을 로그인 합니다.");
			DSXRATC1.RequestS6 ("DSG_START_LOGIN",UserID,"","",1,1);
		}else{
	     alert("S-Work이 설치되지 않았습니다. \n 설치 후 다시 이용하시기 바랍니다.");
	     alert("S-Work이 설치 되지 않았습니다. \n S-Work 다운로드 페이지로 이동합니다.");
		 var url = "http://150.150.103.25/download_Inside.html";
		 var win = window.open(url , '', 'width=800, height=600, top=150,left=300, scrollbars=yes, resizable=yes, location=no');
		 win.focus();  // 열린 페이지에 포커스를 준다. 열림과동시에 앞쪽으로
		}
	}else{
		if (SWnUrl != "") DSDown11.Down3("http://"+document.location.host+"/"+SWnUrl, filename, true, false);
	}
}

function SWCall(downUrl){
  var dUrl = downUrl;
  document.SWCallForm.action = dUrl;
  document.SWCallForm.submit();
}

function SWRelease() {
	var authkey = "ELCT-EYOE-TUIT-QDOQ";
	var statuscheck = SWELCtrl.SWorkGetDetailStatus(authkey);
	if (statuscheck == 3){
		var SWorkRelease = "";
		SWorkRelease = SWELCtrl.SWorkReleaseProcess(authkey, 2);
		//alert("IE 권한 제어 해제가 정상적으로 이루어 졌습니다.");
	}
}


function popup(fileName, intWidth, intHeight){//, intLeft, intTop, vScrollbars, vResizable, vStatus){
	var winname_1;
	var openF = 0;
    today = new Date();
    winName = today.getTime();

    var fileName, intWidth, intHeight;
    var screenWidth = screen.availwidth;
    var screenHeight = screen.availheight;

    if(intWidth >= screenWidth){
            intWidth = screenWidth - 40;
            vScrollbars = 1;
    }
    if(intHeight >= screenHeight){
            intHeight = screenHeight - 40;
            intWidth = intWidth + 20;
            vScrollbars = 1;
    }
    //if(intLeft == 'auto' || intTop == 'auto'){ 
            var intLeft = (screenWidth - intWidth) / 2;
            var intTop = (screenHeight - intHeight) / 2;
    //}
    var features = eval("'width=" + intWidth + ",height=" + intHeight + ",left=" + intLeft + ",top=" + intTop + ", scrollbars=1, resizable=1, status=1'");
    if(openF == 1){
            if(winname_1.closed){
                    winname_1 = window.open(fileName,winName,features);
            }else{
                    winname_1.close();
                    winname_1 = window.open(fileName,winName,features);
            }
    }else{
            winname_1 = window.open(fileName,winName,features);
            openF = 1;
    }
    winname_1.focus();
}

function openView(oid, width, height, auth) {
	if(width == null) {
		width = 'full';
		height = 'full';
	}
	var url = getViewURL(oid);
	if(url != "") {
		if(auth != null)
			url += '&key=auth&value='+auth; 
		
		if( oid.indexOf("EPMDocument") > 0) {
			popup(url, 1180, 800);
		}else if( oid.indexOf("Document") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("WTDocument") > 0){
			popup(url, 1180, 800);		
		}else if( oid.indexOf("WTPart") > 0){
			popup(url, 1180, 800);		
		}else if( oid.indexOf("EChangeNotify") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("DistributeCompany") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("Distribute") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("devMaster") > 0) {
			popup(url, 1180, 800);
		}else if( oid.indexOf("devActive") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("AsmApproval") > 0) {
			popup(url, 1180, 800);
		}else if( oid.indexOf("ROHSMaterial") > 0) {
			popup(url, 1180, 800);	
		}else{
			//openWindow( url, '', width, height);	
			popup(url, 1100, 600);
		}
	}
}

function getViewURL(oid) {
	
	var url = "";
	if( oid.indexOf("WTDocument") > 0) {
		url = getURLString("doc", "viewDocument", "do");
	} else if( oid.indexOf("EPMDocument") > 0) {
		url = getURLString("drawing", "viewDrawing", "do");
	} else if( oid.indexOf("WTPart") > 0) {
		url = getURLString("part", "viewPart", "do");
	} else if( oid.indexOf("EChangeOrder") > 0) {
		url = getURLString("changeECO", "viewECO", "do");
	} else if( oid.indexOf("EChangeRequest") > 0) { 
		url = getURLString("changeECR", "viewECR", "do");
	} else if( oid.indexOf("EChangeNotice") > 0 ) {
		url = getURLString("changeECN", "viewECN", "do");
	} else if( oid.indexOf("DistributeCompany") > 0 ) {
		url = getURLString("distribute", "viewCompany", "do");
	} else if( oid.indexOf("Distribute") > 0 ) {
		url = getURLString("distribute", "viewDistribute", "do");
	}else if( oid.indexOf("ProjectRegistApproval") > 0 || oid.indexOf("EProject") > 0) { 
		url = getURLString("project", "viewProjectPopup", "do");
	}else if(oid.indexOf("devMaster") > 0) {
		url = getURLString("development", "bodyDevelopmentPopup", "do");
	}else if(oid.indexOf("devTask") > 0){
		url = getURLString("development", "viewTask", "do");
	}else if(oid.indexOf("devActive") > 0){
		url = getURLString("development", "viewActive", "do");
	}else if(oid.indexOf("AsmApproval") > 0) {
		url = getURLString("asmApproval", "viewAsm", "do");
	}else if(oid.indexOf("ROHSMaterial") > 0) {
		url = getURLString("rohs", "viewRohs", "do");
	}
	
	//alert(url);
	
	if(url != "") return url+"?oid="+oid+"&popup=true";
	return "";
}


//textarea 길이 체크
function textAreaLengthCheckId(nameid, maxsize, type){
	var desc = document.getElementById(nameid).value;
	var ochar = "";
	var obite = 0;
	for(var i=0; i<desc.length; i++){
		ochar = desc.charAt(i);
		if(escape(ochar).length>4){
			obite +=2;
		}else{
			obite++;
		}
	}
	
	if(obite > maxsize){
		//alert(type +'의 글자수를 초과하였습니다.');
		alert("["+obite +"]"+type + '은 최대 ' + maxsize + '자 까지 입력할 수 있습니다.');
		document.getElementById(nameid).focus();
		return false;
	}
	return true;
}

//textarea 길이 체크
function textAreaLengthCheckName(nameid, maxsize, type){
	var desc = document.getElementsByName(nameid)[0].value;
	var ochar = "";
	var obite = 0;
	for(var i=0; i<desc.length; i++){
		ochar = desc.charAt(i);
		if(escape(ochar).length>4){
			obite +=2;
		}else{
			obite++;
		}
	}
	
	if(obite > maxsize){
		//alert(type +'의 글자수를 초과하였습니다.');
		alert(type + '은 최대 ' + maxsize + '자 까지 입력할 수 있습니다.');
		document.getElementById(nameid).focus();
		return false;
	}
	return true;
}


//특수문자 체크 id
function filterCheckById(nameid){
	var chktext = /[\{\}\?.,;:|\)*~`!^\+┼<>@\#$%&\'\"\\\(\=]/gi;
	
	var obj = document.getElementById(nameid);
	
	if (chktext.test(obj.value)) {
	   alert("특수문자는 입력하실 수 없습니다.");
	   obj.focus();
	   return false;
	}
	return true;
}


//특수문자 체크 name
function filterCheckByName(nameid){
	var chktext = /[\{\}\?.,;:|\)*~`!^\+┼<>@\#$%&\'\"\\\(\=]/gi;
	
	var obj = document.getElementsByName(nameid)[0];
	
	if (chktext.test(obj.value)) {
	   alert("특수문자는 입력하실 수 없습니다.");
	   obj.focus();
	   return false;
	}
	return true;
}


/**
 * intOLEcmd : 	7 인쇄 미리 보기 ieExecWB(7)
 * 			   	8 페이지 설정, ieExecWB(8)
 *  			6인쇄하기(대화상자 표시),ieExecWB(6)
 *  			6 인쇄 바로 하기 ieExecWB(6,-1)
 * intOLEparam :  -1 인쇄미 보기
 * @param intOLEcmd
 * @param intOLEparam
 */
function ieExecWB( intOLEcmd, intOLEparam )
{
// 웹 브라우저 컨트롤 생성
var WebBrowser = '<OBJECT ID="WebBrowser1" WIDTH=0 HEIGHT=0 CLASSID="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2"></OBJECT>';
 
// 웹 페이지에 객체 삽입
document.body.insertAdjacentHTML('beforeEnd', WebBrowser);
 
// if intOLEparam이 정의되어 있지 않으면 디폴트 값 설정
if ( ( ! intOLEparam ) || ( intOLEparam < -1 )  || (intOLEparam > 1 ) )
        intOLEparam = 1;
 
// ExexWB 메쏘드 실행
WebBrowser1.ExecWB( intOLEcmd, intOLEparam );
 
// 객체 해제
WebBrowser1.outerHTML = "";
}

/**
 * 결재 이력 정보 보기
 */
function historyWork(oid) {
	
	var url = "/Windchill/jsp/workprocess/HistoryWork.jsp?oid=" + oid;
	openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
}

//날짜 체크
function checkDate(a,b){
	
	var start = a.split("-");
	var end = b.split("-");
	var strDate = start[0]+ start[1]-1+ start[2];
	var endDate = end[0]+ end[1]-1+ end[2];
	
	if(endDate==null || endDate.length==0)return true;
	
	if(strDate > endDate){
		alert("종료일이 시작일보다 작을 수 없습니다.");
		return false;
	}
	
	return true;
}


//Enter 검색(doSubmit();)
function onKeyPress(){
	if(window.event){
		if(window.event.keyCode == 13) {
			doSubmit();
		}
	}else return;
}

/** 
 * 유저 Search
 * userName : value
 * hiddenFild : hidden user 
 * formName : 
 */

function onKeyPressUser(userName,hiddenFild,formName,isSearch){
	
	if(window.event){
		if(window.event.keyCode == 13) {
			//alert("2");
			
			SearchAjaxUser(userName,hiddenFild,formName,isSearch);
		}
	}else return;
	
}

function SearchAjaxUser( userName,hiddenFild,formName,isSearch ){
	if( userName.length == 0 || hiddenFild.length == 0 || formName.length == 0){
		return;
	}
	url = '/Windchill/jsp/org/AjaxSearchUser.jsp';
	param = "targetValue="+userName+"&hiddenFild="+hiddenFild+"&formName="+formName+"&isSearch="+isSearch;
	//alert(param);
	postCallServer(url, param, setAjaxUser, true);
}


function setAjaxUser(req){
	
	var xmlDoc = req.responseXML;
	var message = xmlDoc.selectNodes("//message");
	var l_message = message[0].getElementsByTagName("l_message");	
	var l_hiddenFild = message[0].getElementsByTagName("l_hiddenFild");	
	var l_formName = message[0].getElementsByTagName("l_formName");	
	var l_isSearch = message[0].getElementsByTagName("l_isSearch");	
	
	var hiddenFild = decodeURIComponent(l_hiddenFild[0].text);
	var formName = decodeURIComponent(l_formName[0].text);
	var message = decodeURIComponent(l_message[0].text);	
	var isSearch = decodeURIComponent(l_isSearch[0].text);	
	if(message =="false"){
		alert("입력값을 확인해 주세요. \n 오류가 발생 하였습니다.");
		return false;
	}
	//UserInfo
	var userInfo = xmlDoc.selectNodes("//data_info");

	var l_name = userInfo[0].getElementsByTagName("l_name");	//이름
	var l_value = userInfo[0].getElementsByTagName("l_value");	//people OID
	
	var form = eval("document."+formName);
	var hiddenUser = eval("document."+formName+"."+hiddenFild);
	var viewUser = eval("document."+formName+"."+hiddenFild+"Name");
	if(l_value.length == 1){
		var Poid = decodeURIComponent(l_value[0].text);
		hiddenUser.value= Poid;
		if(isSearch =="true"){
			form.submit();
		}
		
		return true;
	}else if(l_value.length == 0){
		alert("존재 하지 않는 유저입니다.");
		return false;
	}else if(l_value.length > 1){
		var name = decodeURIComponent(l_name[0].text);
		selectUserOption(hiddenUser, viewUser, name);
		return false;
	}
	return true;
}

function selectUserOption(inputObj, inputLabel, name) {
	
    var url = "/Windchill/jsp/org/SelectPeopleFrm.jsp?mode=s&keyvalue=" + encodeURI(name);
    
    var attache = window.showModalDialog(url, window,"help=no; scroll=no; resizable=yes; dialogWidth=750px; dialogHeight:450px; center:yes");
    if(typeof attache == "undefined" || attache == null) {
        return;
    }
    addList(attache, inputObj, inputLabel);
}

//팝업창용 유저.
//select user
function selectUser(inputObj, inputLabel) {
    var url = "/Windchill/jsp/org/SelectPeopleFrm.jsp?mode=s";
    var attache = window.showModalDialog(url,window,"help=no; scroll=no; resizable=yes; dialogWidth=750px; dialogHeight:450px; center:yes");
    if(typeof attache == "undefined" || attache == null) {
        return;
    }
    addList(attache, inputObj, inputLabel);
}

function addList(arrObj, inputObj, inputLabel) {
    if(!arrObj.length) {
        return;
    }
    var subarr;
    var peopleOid;//
    var userName;//
    for(var i = 0; i < arrObj.length; i++) {
        subarr = arrObj[i];
        peopleOid = subarr[1];//
        userName = subarr[4];//

        inputObj.value = peopleOid;
        inputLabel.value = userName;
    }
}

function cancelUser(inputObj, inputLabel, value){
	
    var span2 = document.getElementById(inputLabel);
    var userName = span2.innerHTML;//
    
    var aaa = document.getElementById(inputObj);
    var peopleOid =  aaa.value;//

    if(peopleOid==null)peopleOid = "";
    var tempArry = peopleOid.split(",");
    var tempArry2 = userName.split("|");

    var newPeopleOid = "";
    var newPeopleLabel = "";

    for(var j=0; j< tempArry.length; j++){
    	
        if(tempArry[j]==null || tempArry[j].length==0 || tempArry[j] == value){
            continue;
        }
        
        newPeopleOid += tempArry[j] + ",";//
        newPeopleLabel += tempArry2[j] + "|";
    }
        
    aaa.value = newPeopleOid;
    span2.innerHTML = newPeopleLabel;    
}

/**
 * 유저및 부서정보
 * @param inputObj
 * @param inputLabel
 * @param inputDepart
 */
function selectUserDepart(inputObj, inputLabel,inputDepart) {
    var url = "/Windchill/jsp/org/SelectPeopleFrm.jsp?mode=s&chief=false";
    
    attache = window.showModalDialog(url,window,"help=no; scroll=no; resizable=yes; dialogWidth=750px; dialogHeight:450px; center:yes");
    if(typeof attache == "undefined" || attache == null) {
        return;
    }

    addListDepart(attache, inputObj, inputLabel,inputDepart);
}

function addListDepart(arrObj, inputObj, inputLabel,inputDepart) {
    if(arrObj.length == 0) {
        return;
    }
	
    var span2 = document.getElementById(inputLabel);
    var span3 = document.getElementById(inputDepart);
    var aaa = document.getElementById(inputObj);
    
    var peopleOid =  aaa.value;//
    var userName = span2.innerHTML;//
	var departName = span3.innerHTML;
    
    if(peopleOid==null)peopleOid = "";
    var tempArry = peopleOid.split(",");

    for(var i = 0; i < arrObj.length; i++) {
        var subarr = arrObj[i];

        if(    exist2(tempArry, subarr[1])    ) {
            continue;
        }
        
        peopleOid = subarr[1];//
        departName = subarr[5];
        userName = subarr[4] +  "  <a href='JavaScript:cancelUserDepart( \""+inputObj+"\",\""+inputDepart+"\",  \"" +inputLabel+ "\" , \""+subarr[1]+"\")'>X</a>";//
    	
    }
	
    aaa.value = peopleOid;
    span2.innerHTML = userName;
    span3.innerHTML = departName;
}

function cancelUserDepart(inputObj,inputDepart, inputLabel, value){
	
    var span2 = document.getElementById(inputLabel);
    var userName = span2.innerHTML;//
    
    var span3 = document.getElementById(inputDepart);
    var departName = span3.innerHTML;//
    
    var aaa = document.getElementById(inputObj);
    var peopleOid =  aaa.value;//

    if(peopleOid==null)peopleOid = "";
    var tempArry = peopleOid.split(",");
    var tempArry2 = userName.split("|");

    var newPeopleOid = "";
    var newPeopleLabel = "";
	
    aaa.value = "";//newPeopleOid;
    span2.innerHTML = "";
    span3.innerHTML = "";
}

/**/
function clearText(str){
	var tartxt = document.getElementById(str);
	tartxt.value = "";
}
/*일진 추가 */
/*공백 제거*/
function objectTrim(str){
	
	//앞뒤 공백 제거
	str = str.replace(/(^\s*)|(\s*$)/gi, "");
	
	//왼쪽 공백 제거
	//str.replace(/^\s+/, "");
	
    //오른쪽 공백 제거
	//str.replace(/\s+$/, "");
    
    return str;
}

function setToDay(id) {
	var today = new Date();
	var year =  today.getFullYear();
	var month = today.getMonth() + 1;
	var date = today.getDate();
	if(month < 10) {
		month = "0"+month;
	}
	$("#"+id).val(year + "-"+ month + "-" + date);
}


/*---------------------------------------------------------------------------------------*
 *                            로딩바 Start
 *---------------------------------------------------------------------------------------*/
function gfn_StartShowProcessing()
{
	$("#lodingDIV").css('display','');
	//$("#div1").show();
	//var div1 = document.getElementById('lodingDIV');
	//div1.style.display	= "";
}

/*---------------------------------------------------------------------------------------*
 *                             로딩바 End
 *---------------------------------------------------------------------------------------*/
function gfn_EndShowProcessing(){
	$("#lodingDIV").css('display','none');
	//$("#lodingDIV").attr("display","none");
	//$("#div1").hide();
	//var div1 = document.getElementById("div1");
	//div1.style.display = "none";
}
/*---------------------------------------------------------------------------------------*
 *                             날짜 입력 정합성 체크 Start format : 2016-06-01
 *---------------------------------------------------------------------------------------*/

function checkDate(obj,date){
	//alert(!isValidDate(date));
	if(!isValidDate(date)){
		//this.value="";
		$('#' + obj).val("");
	}
}

/*
 * 날짜가 유효한지 검사
 */
function isValidDate(d) {
    // 포맷에 안맞으면 false리턴
	//alert(d +":" + isDateFormat(d));
    if(!isDateFormat(d)) {
        return false;
    }

    var month_day = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

    var dateToken = d.split('-');
    var year = Number(dateToken[0]);
    var month = Number(dateToken[1]);
    var day = Number(dateToken[2]);
    
    // 날짜가 0이면 false
    if(day == 0) {
        return false;
    }

    var isValid = false;

    // 윤년일때
    if(isLeaf(year)) {
        if(month == 2) {
            if(day <= month_day[month-1] + 1) {
                isValid = true;
            }
        } else {
            if(day <= month_day[month-1]) {
                isValid = true;
            }
        }
    } else {
        if(day <= month_day[month-1]) {
            isValid = true;
        }
    }

    return isValid;
}

/*
 * 날짜포맷에 맞는지 검사
 */
function isDateFormat(d) {
	
    var df = /[0-9]{4}-[0-9]{2}-[0-9]{2}/;
    return d.match(df);
}

/*
 * 윤년여부 검사
 */
function isLeaf(year) {
    var leaf = false;

    if(year % 4 == 0) {
        leaf = true;

        if(year % 100 == 0) {
            leaf = false;
        }

        if(year % 400 == 0) {
            leaf = true;
        }
    }

    return leaf;
}

/*---------------------------------------------------------------------------------------*
 *                             날짜 입력 정합성 체크 END format : 2016-06-01
 *---------------------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------------------*
 *                           TextArea Size 조절
 *---------------------------------------------------------------------------------------*/
function textAreaIncrease(textName){
	//document.getElementById(textName).rows++;
	document.getElementById(textName).rows =  document.getElementById(textName).rows + 5;
}

function textAreaDecrease(textName){
	document.getElementById(textName).rows =  document.getElementById(textName).rows - 5;
}

function textAreaInit(textName,size){
	document.getElementById(textName).rows =  size;
}

/*---------------------------------------------------------------------------------------*
 *                            배포 관련 추가 2017-03-10
 *---------------------------------------------------------------------------------------*/

/**************************************************************
*                     배포 URL
****************************************************************/
function openDistributeView(oid, width, height, auth) {
	if(width == null) {
		width = 'full';
		height = 'full';
	}
	var url = getDistributeViewURL(oid);
	if(url != "") {
		if(auth != null)
			url += '&key=auth&value='+auth; 
		
		if( oid.indexOf("EPMDocument") > 0) {
			popup(url, 1180, 800);
		}else if( oid.indexOf("Document") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("WTDocument") > 0){
			popup(url, 1180, 800);		
		}else if( oid.indexOf("WTPart") > 0){
			popup(url, 1180, 800);		
		}else if( oid.indexOf("EChangeNotify") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("DistributeCompany") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("Distribute") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("devMaster") > 0) {
			popup(url, 1180, 800);
		}else if( oid.indexOf("devActive") > 0){
			popup(url, 1180, 800);
		}else if( oid.indexOf("AsmApproval") > 0) {
			popup(url, 1180, 800);
		}else if( oid.indexOf("ROHSMaterial") > 0) {
			popup(url, 1180, 800);	
		}else{
			//openWindow( url, '', width, height);	
			popup(url, 1100, 600);
		}
	}
}

function getDistributeViewURL(oid) {
	
	var url = "";
	if( oid.indexOf("WTDocument") > 0) {
		url = getURLString("distribute", "viewDocument", "do");
	} else if( oid.indexOf("EPMDocument") > 0) {
		url = getURLString("distribute", "viewDrawing", "do");
	} else if( oid.indexOf("WTPart") > 0) {
		url = getURLString("distribute", "viewPart", "do");
	} else if( oid.indexOf("EChangeOrder") > 0) {
		url = getURLString("distribute", "viewECO", "do");
	} else if( oid.indexOf("EChangeRequest") > 0) { 
		url = getURLString("distribute", "viewECR", "do");
	} else if( oid.indexOf("EChangeNotice") > 0 ) {
		url = getURLString("changeECN", "viewECN", "do");
	} else if( oid.indexOf("DistributeCompany") > 0 ) {
		url = getURLString("distribute", "viewCompany", "do");
	} else if( oid.indexOf("Distribute") > 0 ) {
		url = getURLString("distribute", "viewDistribute", "do");
	}else if( oid.indexOf("ProjectRegistApproval") > 0 || oid.indexOf("EProject") > 0) { 
		url = getURLString("project", "viewProjectPopup", "do");
	}else if(oid.indexOf("devMaster") > 0) {
		url = getURLString("development", "bodyDevelopmentPopup", "do");
	}else if(oid.indexOf("devTask") > 0){
		url = getURLString("development", "viewTask", "do");
	}else if(oid.indexOf("devActive") > 0){
		url = getURLString("development", "viewActive", "do");
	}else if(oid.indexOf("AsmApproval") > 0) {
		url = getURLString("asmApproval", "viewAsm", "do");
	}else if(oid.indexOf("ROHSMaterial") > 0) {
		url = getURLString("distribute", "viewRohs", "do");
	}
	
	//alert(url);
	
	if(url != "") return url+"?oid="+oid+"&popup=true";
	return "";
}

/**************************************************************
*                     완제품 검색에서만 사용
****************************************************************/
function openProductView(oid){
	var url = url = getURLString("distribute", "viewProduct", "do");
	url = url+"?oid="+oid+"&popup=true";
	popup(url, 1180, 800);	
}

/**************************************************************
*                     오늘 날짜 yyyy-mm-dd;
****************************************************************/
function getToDate() {
    var d = new Date();
 
    var s =
        leadingZeros(d.getFullYear(), 4) + '-' +
        leadingZeros(d.getMonth() + 1, 2) + '-' +
        leadingZeros(d.getDate(), 2);
 
    return s;
}
 
function leadingZeros(n, digits) {
    var zero = '';
    n = n.toString();
 
    if (n.length < digits) {
        for (i = 0; i < digits - n.length; i++)
            zero += '0';
    }
    return zero + n;
}

/**************************************************************
*                     AUI 그리드 엑셀 출력
****************************************************************/
function exportAUIExcel(excelFileName) {
	
	
	var  fileName = excelFileName +"_"+getToDate();
	
	if(window.myGridID) {
		AUIGrid.setProp(myGridID, "exportURL", "/Windchill/AUIGrid/export.jsp?fileName="+fileName);
		AUIGrid.exportToXlsx(myGridID);
	}else {
		alert("출력할 데이터가 없습니다. ");
	}
}

function exportAUIExcel2(excelFileName) {
	
	
	var  fileName = excelFileName +"_"+getToDate();
	
	if(window.myGridID2) {
		AUIGrid.setProp(myGridID2, "exportURL", "/Windchill/AUIGrid/export.jsp?fileName="+fileName);
		AUIGrid.exportToXlsx(myGridID2);
	}else {
		alert("출력할 데이터가 없습니다. ");
	}
}

function exportAUIBOMExcel(excelFileName) {
	
	
	var  fileName = excelFileName +"_"+getToDate();
	
	if(window.myBOMGridID) {
		AUIGrid.setProp(myBOMGridID, "exportURL", "/Windchill/AUIGrid/export.jsp?fileName="+fileName);
		AUIGrid.exportToXlsx(myBOMGridID);
	}else {
		alert("출력할 데이터가 없습니다. ");
	}
}

/**************************************************************
*                    배포에서 Baseline BoM
****************************************************************/
function distributeBOM(partOid,baselineOid){
	
	var str = getURLString("distribute", "viewPartBom", "do") + "?oid="+partOid+"&baseline="+baselineOid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1200,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , partOid, opts+rest);
    newwin.focus();
}

function viewBaselineBom(partOid){
	var str = getURLString("distribute", "viewBaselineBom", "do") + "?oid="+partOid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , partOid, opts+rest);
    newwin.focus();
}

/**************************************************************
*                   PDM AUI BoM
****************************************************************/
function auiBom(partOid,baselineOid){
	
	var str = getURLString("part", "viewAUIPartBom", "do") + "?oid="+partOid+"&baseline="+baselineOid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1200,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , partOid, opts+rest);
    newwin.focus();
}
function auiBom2(partOid,baselineOid){
	
	var str = getURLString("part", "viewAUIPartBom2", "do") + "?oid="+partOid+"&baseline="+baselineOid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1200,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , partOid, opts+rest);
    newwin.focus();
}

/**************************************************************
*                    배포에서 Baseline BoM 다운
****************************************************************/
function distributeBOMDown(partOid,baselineOid){
	
	var str = getURLString("distribute", "viewAUIBOMDwon", "do") + "?oid="+partOid+"&baseline="+baselineOid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=1100,height=600,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "viewAUIBOMDwon", opts+rest);
    newwin.focus();
}

/**************************************************************
*                 EO  일괄 다운로드 - 산출물,도면 
****************************************************************/
function batchEODownLoad(oid,downType){
	
	var str = getURLString("common", "batchEODownLoad", "do") +"?oid="+oid+"&downType="+downType;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=500,height=300,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "batchEODownLoad", opts+rest);
    newwin.focus();
}

/**************************************************************
*                 BOM 도면
****************************************************************/
function batchBOMDrawingDownLoad(oid,ecoOid){
	
	var str = getURLString("common", "batchBOMDrawingDown", "do") +"?oid="+oid+"&ecoOid="+ecoOid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=500,height=300,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "batchBOMDrawingDown", opts+rest);
    newwin.focus();
}

/**************************************************************
*                 BOM -선택적 산출물,도면
****************************************************************/
function batchBOMSelectDownLoad(oid,downType){
	
	var str = getURLString("common", "batchBOMSelectDownLoad", "do") +"?oid="+oid+"&downType="+downType;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = (screen.width - 1000)/ 2;
    toppos = (screen.height - 600) / 2 ;
    rest = "width=500,height=300,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , "batchBOMSelectDownLoad", opts+rest);
    newwin.focus();
}

/**************************************************************
*                 메인 팝업
****************************************************************/
function checkPopUP(){
	
	var form = $("form[name=main]").serialize();
	var url	= getURLString("groupware", "popupNoticeAction", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:form,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('상태 리스트 검색오류')}";
			alert(msg);
		},

		success:function(data){
			
			//console.log("data.length = "+data.length)
			//	mainPopUP(data.oid)
			var position = 0 ;
			for(var i = 0 ;  i < data.length ; i++ ){
				
				//console.log("data.oid = "+data[i].oid +",position =" + position);
				position = position +40;
				if(mainIsPopup(data[i].oid)){
					mainPopUP(data[i].oid,position);
				}
				
			}
			
		}
	});
}


/**************************************************************
*                공지사항 팝업 
****************************************************************/
function mainPopUP(oid,position){
	
	var str = getURLString("groupware", "viewNotice", "do") + "?oid="+oid+"&isPopup=true"
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    leftpos = ((screen.width - 1000)/ 2)+ position;
    toppos = ((screen.height - 600) / 2)+ position ;
    rest = "width=600,height=450,left=" + leftpos + ',top=' + toppos;
    var newwin = window.open( str , oid, opts+rest);
   
    newwin.focus();
}	

function  mainIsPopup(oid) {
    var isPopup = false;
    cValue =getNoticeCookie(oid);
    console.log(oid+" cookie =" + getNoticeCookie(oid));
    if(cValue ==""){
    	isPopup = true;
    }
	return isPopup
}	

//쿠키 가져오기
function getNoticeCookie(cName) {
     cName = cName + '=';
     var cookieData = document.cookie;
     var start = cookieData.indexOf(cName);
     var cValue = '';
     if(start != -1){
          start += cName.length;
          var end = cookieData.indexOf(';', start);
          if(end == -1)end = cookieData.length;
          cValue = cookieData.substring(start, end);
     }
     return unescape(cValue);
}


/**************************************************************
*                AUI 그리드 Popup size 조절
****************************************************************/
function popupAUIResize(){
	
	var width = "";
	width = $(window).width()-10;
	var height = $(window).height()-20;
	
	if (window.myGridID){
		AUIGrid.resize(window.myGridID, width);
	}

	if (window.myGridID2){
		AUIGrid.resize(window.myGridID2, width);
	}
	if (window.myBOMGridID){
		AUIGrid.resize(window.myBOMGridID, width,height-100);
	}

}

function _data($form) {
	var o = {};
	var a = $form.serializeArray();
	$.each(a, function() {
		var name = $.trim(this.name),
			value = $.trim(this.value);

		if (name == "secondary" || name == "poid" || name == "doid" || name == "appOid" || name == "refOid") {

			if (o[name]) {
				o[name].push(value || '');
			} else {
				o[name] = [];
				o[name].push(value || '');
			}

		} else if (o[name]) {

			if (!o[name].push) {
				o[name] = [o[name]];
			}
			o[name].push(value || '');
		} else {
			o[name] = value || '';
		}
	});

	return o;
}

function isEmpty(object){
   return object === null || "undefined" === typeof object || (isObject(object) && !Object.keys(object).length && !isDate(object)) || (isString(object) && object.trim() === "") || (isArray(object) && object.length === 0);
}

 function isObject(object) {
        return object !== null && "object" === typeof object;
}

function isDate(object) {
        return isObject(object) && typeof object.getTime === "function";
}

 function isString(object) {
        return "string" === typeof object;
}

function isArray(object) {
        return Array.isArray(object);
}

function formatDate(date, format) {
        if (date === typeof object) {
            date = new Date(date);
        }
        var me = date;
        
       return format.replace(/(yyyy|yy|MM|M|dd|d|HH|H|mm|m|ss|s)/g, function(match) {
            switch (match) {
                case "yyyy":
                    return me.getFullYear();
                case "yy":
                    var v = me.getFullYear() % 100;
                    return v > 10 ? v : "0" + v;
                case "MM":
                case "M":
                    var v = me.getMonth() + 1;
                    return match.length === 1 || v >= 10 ? v : "0" + v;
                case "dd":
                case "d":
                    var v = me.getDate();
                    return match.length === 1 || v >= 10 ? v : "0" + v;
                case "HH":
                case "H":
                    var v = me.getHours();
                    return match.length === 1 || v >= 10 ? v : "0" + v;
                case "mm":
                case "m":
                    var v = me.getMinutes();
                    return match.length === 1 || v >= 10 ? v : "0" + v;
                case "ss":
                case "s":
                    var v = me.getSeconds();
                    return match.length === 1 || v >= 10 ? v : "0" + v;
                default:
                    return match;
            }
        });
}

