<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<html>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<!-- 각 화면 개발시 Tag 복사해서 붙여넣고 사용할것 -->
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<link rel="stylesheet" type="text/css" href="/Windchill/extcore/jsp/js/dhtmlx/dhtmlx.css" />
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/default.css" type="text/css">
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/css.css" type="text/css">
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/e3ps.css" type="text/css">

<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/jquery-1.11.1.min.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/dhtmlx/dhtmlx.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/dhtmlx/dhtmlxPaging.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/jquery.json-2.4.min.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/pageGrid.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/common.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/popup.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/script.js" ></script>

<title>
	LUTRONIC PDM
</title>

<style>
#div_root{
width:100%;
}

#div_top{
width:100%;
height:90%
}

#div_body{
width:100%;
margin-left: 2px;
margin-right: 2px;
margin-top : 2px;
border: 1px;
}

#div_menu{
width:15%;
margin: 2px;
float:left;
}

#div_scroll{
width:8px;
float:left;
}

#div_main{
width:80%;
float:left;
margin: 2px;
background-size:100%;
}

.Sub_Right_LT { background:url(/Windchill/extcore/jsp/portal/images/base_design/Sub_Right_LT.gif); background-repeat:no-repeat; width:19px; height:29px;}
.Sub_Right_LBG { background:url(/Windchill/extcore/jsp/portal/images/base_design/Sub_Right_LBG.gif); background-repeat:repeat-y; vertical-align:top; padding-top:20px;}
.Sub_Right_LB { background:url(/Windchill/extcore/jsp/portal/images/base_design/Sub_Right_LB.gif); background-repeat:no-repeat; width:19px; height:4px;}
</style>

<script>

window.initSwitch = function(){
    var smenu = document.getElementById("subMenu_table");
    var vv = getCookie("subMenuSwitch2");
    if(vv==null)return;
    smenu.style.display = getCookie("subMenuSwitch2");

    var smenuh = document.getElementById("subMenu_table_h");

    if('none' == smenu.style.display){
        smenuh.style.display="";
    }else{
        smenuh.style.display="none";
    }

}

window.switchMenu = function(){
    var smenu = document.getElementById("subMenu_table");
    var smenuh = document.getElementById("subMenu_table_h");
    var button = document.getElementById("hiddenArrow");
    if('none' == smenu.style.display){
        smenu.style.display="";
        smenuh.style.display="none";
        button.src = "/Windchill/extcore/jsp/portal/images/base_design/layer_hidden.gif";
        button.title = "<bean:message key='E3PS.Msg.578' />";
    }else{
        smenu.style.display="none";
        smenuh.style.display="";
        button.src = "/Windchill/jsp/portal/images/base_design/layer_show.gif";
        button.title = "<bean:message key='E3PS.Msg.577' />";
    }
    setCookie("subMenuSwitch2",smenu.style.display,null,'/Windchill/jsp/help/');
}

window.setCookie = function( name, value, expires, path, domain, secure ) {
    var today = new Date();
    today.setTime( today.getTime() );
    if ( expires ) {
        expires = expires * 1000 * 60 * 60 * 24;
    }
    var expires_date = new Date( today.getTime() + (expires) );
    document.cookie = name+"="+escape( value ) +
        ( ( expires ) ? ";expires="+expires_date.toGMTString() : "" ) + //expires.toGMTString()
        ( ( path ) ? ";path=" + path : "" ) +
        ( ( domain ) ? ";domain=" + domain : "" ) +
        ( ( secure ) ? ";secure" : "" );
}

window.getCookie = function( name ) {
    var start = document.cookie.indexOf( name + "=" );
    var len = start + name.length + 1;
    if ( ( !start ) && ( name != document.cookie.substring( 0, name.length ) ) ) {
        return null;
    }
    if ( start == -1 ) return null;
    var end = document.cookie.indexOf( ";", len );
    if ( end == -1 ) end = document.cookie.length;
    return unescape( document.cookie.substring( len, end ) );
}

</script>
<table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
    <tr>
        <td style="padding:5px 4px 0px 4px">
            <table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%" >
                <tr>
                    <td class="TLeft_BG"></td>
                    <td class="Top_BG"></td>
                    <td class="TRight_BG"></td>
                </tr>
                <tr>
                    <td class="Left_BG"></td>
                    <td width="99%" style="padding:3; vertical-align:top">
						<table border="0" width="100%" height="680" cellpadding="0" cellspacing="0">
							<tr>
								<td valign=top width=0 background="/Windchill/jsp/portal/images/ds_sub.gif" >
									<table border=0 id=subMenu_table  width=220  height="100%"  style="display:"  cellpadding="0" cellspacing="0" >
										<tr>
											<td class="Subinfo_img"></td>
											<td valign=top  >
                                                <tiles:insertAttribute name="menu" />
                                            </td>
										</tr>
									</table>
                                </td>
                                
                                <td valign=top width=0  height=100%  >
                                    <table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td class="Sub_Right_LT"></td>
                                        </tr>
                                        <tr>
                                            <td class="Sub_Right_LBG">
	                                            <a href="#" onclick="switchMenu()">
	                                            	<img id="hiddenArrow" src="/Windchill/jsp/portal/images/base_design/layer_hidden.gif" border=0 title="${f:getMessage('서브메뉴')} ${f:getMessage('닫기')}">
	                                            </a>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="Sub_Right_LB"></td>
                                        </tr>
                                    </table>
                                </td>
                                
                                <script type="text/javascript">initSwitch();</script>
                                
                                <td valign=top width=100%>
                                    <table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
                                    
										<tr>
											<td align="left" valign=top height=42>
												<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
													<tr>
														<td></td>
														
														<td>
															&nbsp;
															<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
															&nbsp; Help Desk > <c:out value="${title }" />
														</td>
														
														<td align="right">
															<button type="button" class="btnClose" onclick="self.close();">
																<span></span>
																${f:getMessage('닫기')}
															</button>
														</td>
													</tr>
												</table>
											</td>
										</tr>
                                    
                                        <tr>
                                            <td valign=top>
												<tiles:insertAttribute name="body" />
											</td>
											
                                            <td class="Main_RightBG"></td>
                                        </tr>
                                        <tr>
                                            <td class="Main_BBG"></td>
                                            <td class="Main_BR"></td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td class="Right_BG"></td>
                </tr>
                <tr>
                    <td class="Left_BG"></td>
                    <td align=right ></td>
                    <td class="Right_BG"></td>
                </tr>
                <tr>
                    <td class="BottomL_BG"></td>
                    <td class="Bottom_BG"></td>
                    <td class="BottomR_BG"></td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<DIV id="lodingDIV" class='loading_edge' style='display: none;'>
	<img src="/Windchill/jsp/portal/images/loading.gif" />
</DIV>

</html>