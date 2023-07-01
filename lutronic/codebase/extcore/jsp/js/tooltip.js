 /**
 * @(#)	tooltip.js
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @author Seung-hwan Choi, skyprda@e3ps.com
 * @desc You must insert between <BODY> and </BODY> html-tag
 * <a onmouseover="openToolTip('????')" onmouseout="closeToolTip()">
 */
document.write("<DIV ID=e3ps_tooltip style='POSITION:absolute;display:none;Z-INDEX:200;background:#CCFF00;padding:4px;border:1 solid'></DIV>");
document.write("<iframe id=e3ps_tooltip2 src='javascript:false' scrolling=no frameborder=0 style='position:absolute; top:0px; left:0px; display:none;'></iframe>");

var nav=false;
var old=false;
var iex=(document.all);
var div_e3ps_tooltip_style;
var div_e3ps_tooltip2_style;

if(navigator.appName=="Netscape")
{
	(document.layers) ? nav=true : old=true;
}

if(!old)
{
	div_e3ps_tooltip_style = (nav) ? document.e3ps_tooltip : document.getElementById('e3ps_tooltip').style;
	div_e3ps_tooltip2_style = (nav) ? document.e3ps_tooltip2 : document.getElementById('e3ps_tooltip2').style;
	
	if(nav) document.captureEvents(Event.MOUSEMOVE);
	document.onmousemove = get_mouse;
}

function openToolTip(msg, bgcolor)
{
	if(old)
	{
		alert(msg);
		return;
	} 
	else
	{
		if(nav)
		{
			div_e3ps_tooltip_style.document.write(msg);
			div_e3ps_tooltip_style.document.close();
			div_e3ps_tooltip_style.display = "block";
			div_e3ps_tooltip2_style.display = "block";
		}
		if(iex)
		{
			document.getElementById('e3ps_tooltip').innerHTML = msg;
			div_e3ps_tooltip2_style.display = "block";
			if(bgcolor != null) 	div_e3ps_tooltip_style.background = bgcolor;
			div_e3ps_tooltip_style.display = "block";
			
		}
	}
}

function get_mouse(e)
{
	var x= (nav) ? e.pageX : event.clientX+document.body.scrollLeft; 
	div_e3ps_tooltip_style.left = x + (-60);
	var y=(nav) ? e.pageY : event.clientY+document.body.scrollTop;
	div_e3ps_tooltip_style.top = y+ 20;

	document.getElementById('e3ps_tooltip2').width = document.getElementById('e3ps_tooltip').offsetWidth;
	document.getElementById('e3ps_tooltip2').height = document.getElementById('e3ps_tooltip').offsetHeight;
	div_e3ps_tooltip2_style.top = div_e3ps_tooltip_style.top;
	div_e3ps_tooltip2_style.left = div_e3ps_tooltip_style.left;
	div_e3ps_tooltip2_style.zIndex = div_e3ps_tooltip_style.zIndex - 1;
}

function closeToolTip()
{
	if(!old)
	{
		div_e3ps_tooltip_style.display = "none";
		div_e3ps_tooltip2_style.display = "none";
	}
}