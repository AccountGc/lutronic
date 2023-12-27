function openCal(variableName)
{
	var str="/Windchill/jsp/common/calendar.jsp?obj="+variableName;
	var opts = "toolbar=0,location=0,directory=0,status=0,menubar=0,scrollbars=0,resizable=0,";
	leftpos = (screen.width - 224)/ 2; 
	toppos = (screen.height - 230) / 2 ; 
	rest = "width=224,height=230,left=" + leftpos + ',top=' + toppos;
	
	var newwin = window.open( str , "calendar", opts+rest);
	newwin.focus();
}
/*
function openCal(variableName, date)
{
	var str="/Windchill/jsp/common/calendar.jsp?obj="+variableName;
	var opts = "toolbar=0,location=0,directory=0,status=0,menubar=0,scrollbars=0,resizable=0,";
	leftpos = (screen.width - 224)/ 2; 
	toppos = (screen.height - 230) / 2 ; 
	rest = "width=224,height=230,left=" + leftpos + ',top=' + toppos;
	
	var newwin = window.open( str , "calendar", opts+rest);
	newwin.focus();
}
*/
function selectOneUser(varName)
{
	var str="/Windchill/jsp/groupware/company/selectPeopleFrm.jsp?function=addUserToTarget&mode=s&target="+varName;
	openWindow(str, "SelectPeople", 500, 470);
}
function delOneUser(variableName)
{
	eval("document.forms[0]."+variableName+".value = ''");
	eval("document.forms[0].temp"+variableName+".value = ''");
}

function convertCal(variableName)
{
	var temp = eval("document.forms[0]."+variableName+".value");

	var tempCal;
	var tempYear;
	var tempMonth;
	var tempDay;

	if(temp != "")
	{
		data = temp.split("/");

		if(!isNaN(data[0]) && !isNaN(data[1]) && !isNaN(data[2]))
		{
			//Year Setting
			if(data[0].length > 4 || data[0].length < 1)
			{
				alert("????????? ????????????. ?????? ????????? ????????????.");
				eval("document.forms[0]."+variableName+".value=''");
				eval("document.forms[0]."+variableName+".focus()");
				return true;
			}
			else if(data[0].length == 4)
			{
				tempYear = data[0];
			}
			else if(data[0].length == 3)
			{
				tempYear = '2' + data[0];
			}
			else if(data[0].length == 2)
			{
				tempYear = '20' + data[0];
			}
			else if(data[0].length == 1)
			{
				tempYear = '200' + data[0];
			}

			//Month Setting
			if(data[1].length > 2 || data[1].length < 1)
			{
				alert("?????? ????????????. ?????? ????????? ????????????.");
				eval("document.forms[0]."+variableName+".value=''");
				eval("document.forms[0]."+variableName+".focus()");
				return true;
			}
			else if(data[1].length == 2)
			{
				if(parseInt(data[1]) > 12 || parseInt(data[1]) < 1)
				{
					alert("?????? ????????????. ?????? ????????? ????????????.");
					eval("document.forms[0]."+variableName+".value=''");
					eval("document.forms[0]."+variableName+".focus()");
					return true;
				}
				else
					tempMonth = data[1];
			}
			else if(data[1].length == 1)
			{
				if(parseInt(data[1]) > 12 || parseInt(data[1]) < 1)
				{
					alert("?????? ????????????. ?????? ????????? ????????????.");
					eval("document.forms[0]."+variableName+".value=''");
					eval("document.forms[0]."+variableName+".focus()");
					return true;
				}
				else
					tempMonth = '0' + data[1];
			}

			//Days Setting
			if(data[2].length > 2 || data[2].length < 1)
			{
				alert("????????? ????????????. ?????? ????????? ????????????.");
				eval("document.forms[0]."+variableName+".value=''");
				eval("document.forms[0]."+variableName+".focus()");
				return true;
			}
			else if(data[2].length == 2)
			{
				if(parseInt(data[2]) > 31 || parseInt(data[2]) < 1)
				{
					alert("????????? ????????????. ?????? ????????? ????????????.");
					eval("document.forms[0]."+variableName+".value=''");
					eval("document.forms[0]."+variableName+".focus()");
					return true;
				}
				else
					tempDay = data[2];
			}
			else if(data[2].length == 1)
			{
				if(parseInt(data[2]) > 31 || parseInt(data[2]) < 1)
				{
					alert("????????? ????????????. ?????? ????????? ????????????.");
					eval("document.forms[0]."+variableName+".value=''");
					eval("document.forms[0]."+variableName+".focus()");
					return true;
				}
				else
					tempDay = '0' + data[2];
			}

			//Result
			tempCal = tempYear + "/" + tempMonth + "/" + tempDay;
			eval("document.forms[0]."+variableName+".value='"+tempCal+"'");
		}
		else
		{
			alert("?????? ?????? ????????? ????????????. ?????? ????????? ????????????.(???: 2005/10/11)");
			eval("document.forms[0]."+variableName+".value=''");
			eval("document.forms[0]."+variableName+".focus()");
			return true;
		}
	}
}