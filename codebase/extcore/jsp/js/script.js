// 롤오버
<!--
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->

// popup
function popupWindow(popupUrl, windowName, windowAttr)
{
    var win = window.open(popupUrl, windowName, windowAttr);
    win.focus();
}

function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}



// 플래시화일
function createEmbedFlash(flashPath, flashWidth, flashHeight, flashBGColor)
{
    var flashDivHtml = '';

    flashDivHtml += '<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" width="'+flashWidth+'" height="'+flashHeight+'">';
    flashDivHtml += '  <param name="allowScriptAccess" value="sameDomain"/>';
    flashDivHtml += '  <param name="movie" value="'+flashPath+'"/>';
    flashDivHtml += '  <param name="wmode" value="transparent"/>';
    flashDivHtml += '  <param name="quality" value="high"/>';
    flashDivHtml += '  <param name="bgcolor" value="#'+flashBGColor+'"/>';
    flashDivHtml += '  <embed src="'+flashPath+'" quality="high" wmode="transparent" bgcolor="#'+flashBGColor+'" width="'+flashWidth+'" height="'+flashHeight+'" align="middle" allowscriptaccess="sameDomain"  type="application/x-shockwave-flash" pluginpage="http://www.macromedia.com/go/getflashplayer"></embed>';
    flashDivHtml += '</object>';

    document.write(flashDivHtml);
}

// on click
function movePaage(menuNo){
  
  parent.movePaage(menuNo);

}

// 탭
<!--
var flag = '1';
	
	function news(val) {
		flag = val;
		if(flag == '1') {
			a1.style.display = ""; a2.style.display = "none";
			
			document.T1.src='img/main/con_tab01.gif';
			document.T2.src='img/main/con_tab02_1.gif'
			
		} else if(flag == '2') {
			a1.style.display = "none";	a2.style.display = "";
			document.T1.src='img/main/con_tab01_1.gif';
			document.T2.src='img/main/con_tab02.gif'
		}
	}

function MM_showHideLayers() { //v6.0
  var i,p,v,obj,args=MM_showHideLayers.arguments;
  for (i=0; i<(args.length-2); i+=3) if ((obj=MM_findObj(args[i]))!=null) { v=args[i+2];
    if (obj.style) { obj=obj.style; v=(v=='show')?'visible':(v=='hide')?'hidden':v; }
    obj.visibility=v; }
}
//-->