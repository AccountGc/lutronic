<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html>
<link rel="stylesheet" href="/Windchill/jsp/css/e3ps.css" type="text/css">
<link rel="stylesheet" href="/Windchill/jsp/css/css.css" type="text/css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${f:getMessage('결재선 이름 입력')}</title>
</head>

<script language="javascript">
<!--
function checkLineName(inputType) {

	var inputName = document.publishForm.lineName.value;
	inputName = inputName.trim();
	
	// alert(inputName);
	
	if( inputType == "cancel" ) {
		window.returnValue = "cancel";   
		window.close();
		
	} else  if( inputName == "" ) {
		alert("<bean:message key='E3PS.Msg.1063' />!");
		return;
		
	} else if( inputName != null || inputName != "" ) {
		window.returnValue = inputName;   
		window.close();

	} else {
		alert("<bean:message key='E3PS.Msg.1063' />!");
		return;
	}
}

String.prototype.ltrim = function() {
    var re = /\s*((\S+\s*)*)/;
    return this.replace(re, "$1");
   }
 
   String.prototype.rtrim = function() {
    var re = /((\s*\S+)*)\s*/;
    return this.replace(re, "$1");
   }
 
   String.prototype.trim = function() {
    return this.ltrim().rtrim();
   }


   function windEdit_Enter(arg) {
   	if(arg == 1) {
   		return false;
   	} else 
   		if(arg == 2){
   			checkLineName('');
   		}
   }
//-->
</script>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form method=post name="publishForm"  method=post onSubmit="return windEdit_Enter(1)">
<table width="366" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="366" height="28"><br>
      <table width="366" height="22" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="333" background="../../img/pop_title_bar.gif"><table width="300" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="50">&nbsp;</td>
                <td>${f:getMessage('결재선 이름 입력')}</td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
        </tr>
      </table> </td>
  </tr>
  <tr>
    <td><br>
      <table width="300" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
        <tr>
          <td height="40"  class="a_con_05"><div align="center">
              <input type="text" name="lineName" OnKeyDown="if(event.keyCode==13) windEdit_Enter(2);" >
            
            </div></TD>
        </TR>
      </table>
      <table width="300" height="40" border="0" align="center">
        <tr>
          <td><table width="150" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr>
                <td><div align="center"><a href="javascript:checkLineName('');"><img src="../portal/img/check.gif" width="50" height="18"></div></a></td>
                <!--td><div align="center"><a href="javascript:checkLineName('cancel');"><img src="../../img/close.gif" width="50" height="18"></div></a></td-->
              </tr>
            </table></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td>
      <table width="366" height="8" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td></td>
        </tr>
      </table> </td>
  </tr>
  <tr>
    <td background="../portal/img/pop_03.gif"></td>
  </tr>
</table>
</form>
</body>
</html>