
/**
 * 	사용자 검색 팝업
 * @param frmName   form 이름 ---- 현재 사용 용도 없음
 * @param mode		single / multi  단일선택 / 멀티선택
 * @param obj1		단일 선택일때 obj1 값에 선택된 데이터의 OID값이 입력됨 (userType이 people 이면 PeopleOid, wtuser 이면 WTUserOid 값)
 * @param obj2		단일 선택일떄 obj2 값에 선택된 데이터의 이름값이 입력됨
 * @param userType	obj1에 받을 OID 값이 PeopelOid, WTUserOid 인지 선택 Default값은 People OID
 * @param reFunc	선택후 return된 function명을 입력 -- reFunc
 */

function searchUser(frmName, mode, obj1, obj2, userType, reFunc) {
	if(userType == "undefined" || userType == null){
		userType = "people";
	}
	
	if(reFunc == "undefined" || reFunc == null){
		reFunc = "";
	}
	var url = getURLString("user", "searchUserInfo", "do") + "?mode="+mode+"&obj1="+obj1+"&obj2="+obj2+"&userType="+userType+"&reFunc="+reFunc;
	openOtherName(url,"searchUser","900","450","status=no,scrollbars=yes,resizable=yes");
}

function processHistory(url) {
	var screenWidth = screen.availWidth/2-320;
	var screenHeight = screen.availHeight/2-300;
	var windowWin = window.open(url,"processHistory","status=no,resizable=yes, scrollbars=yes,width=800,height=600,top="+screenHeight/2+",left="+screenWidth/2);
	windowWin.focus();
}


var dragObject = null;
var mouseOffset = null;
var firstX = null;
var firstY = null;

function showThum( num, imgpath, oid, copyTag){
	if(imgpath!="") {
		var image = new Image();
		image.src = imgpath;
		var text; 
		text ='<table cellpadding="1" cellspacing="1" bgcolor="#FFFFFF" border-style:solid;" border=1 width=195>'; 
		text += '<tr align = center>';
		text += '<td>';
		text += '<img id=thumImg title="'+num+'" src = ' + imgpath + ' border = 0 style="cursor:move">';

		text += '<br>';
		text += '</td>';
		text += '</tr>';
		text += '<tr>';
		text += '<td align=center><table width=100% border=0 cellpadding="0" cellspacing="0"><tr align=center>';
		text += '<td align=center valign=middle><a href="JavaScript:openView(\''+oid+'\')" title="상세보기"><b>'+num+'</a></td>';
		text += '<td align=right width=40><a href="JavaScript:hideThum()">[닫기]</a></td></tr></table></td>';
		text += '</tr>';
		text += '</table>'; 
		
		var t;
		t = document.getElementById('imgView');
		t.innerHTML=text; 
		var obj = document.getElementById(oid);
		
		
		var pos = findPos2(obj);
		$("div#imgView").css({"left":(pos[0]+33)+"px"});
	    $("div#imgView").css({"top":(pos[1]+15)+"px"});

		tig = document.getElementById('thumImg');
		
		tig.onmousemove = mouseMove;
		tig.onmouseup = mouseUp;
		tig.onmousedown = function(event){
			dragObject = t;
			mouseOffset = getMouseOffset(t,event);
			
			return false;
		};
		t.style.visibility='visible'; 

	}
}

function findPos2(obj) {
	var curleft = curtop = curbuttom = 0;
	if (obj.offsetParent) {
		curleft = obj.offsetLeft
		curtop = obj.offsetTop
		while (obj = obj.offsetParent) {
			curleft += obj.offsetLeft
			curtop += obj.offsetTop
		}
	}
	return [curleft,curtop];
}


function hideThum(){ 
	var t;
	t = document.getElementById('imgView');
	t.innerHTML=''; 
	t.style.visibility='hidden'; 
}

function mouseUp(event){
	dragObject = null;
}

function mouseMove(event){
	if(!dragObject)return;

	event = event ? event : window.event;
	var mousePos = mousePosition(event);
	t = document.getElementById('imgView');

	if(dragObject){
		dragObject.style.top = mousePos.y - mouseOffset.y + "px";
		dragObject.style.left = mousePos.x - mouseOffset.x + "px";
		return false;
	}
}

function getMouseOffset(target,event){
	event = event ? event : window.event;
	var mousePos = mousePosition(event);
	var x = mousePos.x - target.offsetLeft;
	var y = mousePos.y - target.offsetTop;

	return new mousePoint(x,y);
}

function mousePosition(event){
	var x = Number(event.clientX);
	var y = Number(event.clientY);
	return new mousePoint(x,y);
}

function mousePoint(x,y){
	this.x = x;
	this.y = y;
}