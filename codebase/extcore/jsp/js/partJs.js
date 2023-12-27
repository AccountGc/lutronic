/**
 * 제품군에 따른 프로젝트
 * @param value
 */
function projectNameSearch(value){
	if(value == ""){ return; }
	var frm = document.PartDrawingForm;
	frm.project.options.length = 1;
	url = '/Windchill/jsp/part/ajax/PartAjaxAction.jsp';
	param = "command=setProject&code="+value;
	postCallServer(url, param, searchProjectName, true);
}

function searchProjectName(req){
	var frm = document.PartDrawingForm;
	var xmlDoc = req.responseXML;
	var pjInfo = xmlDoc.selectNodes("//pj_info");
	
	var l_codeName = pjInfo[0].getElementsByTagName("l_codeName");
	var l_codeNumber = pjInfo[0].getElementsByTagName("l_codeNumber");
	
	for(var h= 0 ; h <l_codeName.length ; h++){
		var l_code = l_codeNumber[h].text;
		var l_name = l_codeName[h].text;
		
		var parcodename = "[" + l_code + "] " + l_name;
		frm.project.options.add(new Option(parcodename, l_code));
	}
}
/**
 * 부품구분에 따른 UNIT(기능)
 * @param value
 */
function unitNameSearch(value){
	if(value == ""){ return; }
	
	var frm = document.PartDrawingForm;
	alert(frm);
	frm.unit.options.length = 1;
	url = '/Windchill/jsp/part/ajax/PartAjaxAction.jsp';
	param = "command=setUnit&code="+value;
	postCallServer(url, param, searchUnitName, true);
}

function searchUnitName(req){
	var frm = document.PartDrawingForm;
	var xmlDoc = req.responseXML;
	var pjInfo = xmlDoc.selectNodes("//unit_info");
	
	var l_codeName = pjInfo[0].getElementsByTagName("l_codeName");
	var l_codeNumber = pjInfo[0].getElementsByTagName("l_codeNumber");
	
	for(var h= 0 ; h <l_codeName.length ; h++){
		var l_code = l_codeNumber[h].text;
		var l_name = l_codeName[h].text;
		
		var parcodename = "[" + l_code + "] " + l_name;
		frm.unit.options.add(new Option(parcodename, l_code));
	}
}

/**
 * 품번카피 시 부품구분에 따른 UNIT(기능)
 * @param value
 */
function unitNameSearch2(value){
	if(value == ""){ return; }
	
	var frm = document.PartDrawingForm;
	frm.unit.options.length = 1;
	url = '/Windchill/jsp/part/PartAjaxAction.jsp';
	param = "command=setUnit&code="+value;
	postCallServer(url, param, searchUnitName2, true);
}

function searchUnitName2(req){
	var frm = document.PartDrawingForm;
	var xmlDoc = req.responseXML;
	var pjInfo = xmlDoc.selectNodes("//unit_info");
	
	var l_codeName = pjInfo[0].getElementsByTagName("l_codeName");
	var l_codeNumber = pjInfo[0].getElementsByTagName("l_codeNumber");
	
	for(var h= 0 ; h <l_codeName.length ; h++){
		var l_code = l_codeNumber[h].text;
		var l_name = l_codeName[h].text;
		
		var parcodename = "[" + l_code + "] " + l_name;
		frm.unit.options.add(new Option(parcodename, l_code));
	}
}

function setDisplay(val){
	
	if(val.length == 2 && val.substring(0,1) == "B"){
		document.getElementById("table1").style.display = "none";
		document.getElementById("table2").style.display = "none";
		document.getElementById("table3").style.display = "block";
		document.getElementById("table4").style.display = "none";
		document.getElementById("table5").style.display = "none";
		document.getElementById("table6").style.display = "none";
		document.getElementById("table7").style.display = "none";
		document.getElementById("table8").style.display = "block";
		document.getElementById("table9").style.display = "none";
		document.getElementById("table10").style.display = "none";
		document.getElementById("table11").style.display = "none";
		document.getElementById("table12").style.display = "none";
	}else{//if(val == "M" || val == "E" || val == "T") {
		document.getElementById("table1").style.display = "block";
		document.getElementById("table2").style.display = "block";
		document.getElementById("table3").style.display = "block";
		document.getElementById("table4").style.display = "block";
		document.getElementById("table5").style.display = "block";
		document.getElementById("table6").style.display = "block";
		document.getElementById("table7").style.display = "block";
		document.getElementById("table8").style.display = "block";
		document.getElementById("table9").style.display = "block";
		document.getElementById("table10").style.display = "block";
		document.getElementById("table11").style.display = "block";
		document.getElementById("table12").style.display = "block";
	}
	
}


function setCodeNum(num){
	document.all.codeNum.innerHTML = num;	
}

