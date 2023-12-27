// <SCRIPT language=JavaScript src="/Windchill/portal/js/common.js"></SCRIPT>
 /**
  * EO 에서 사용하는 script
  */
/**************************************************************
*                      검색 제품 추가
****************************************************************/
function addNumberCode(obj) {
	
	for (var i = 0; i < obj.length; i++) {
		
		if(DocDuplicateCheck(obj[i][1])) {
			var html = ""
			html += "<div id='"+obj[i][1]+"' style='float: left'> ";
			html += "<input type=checkbox name=modelcode id=modelcode value='"+obj[i][1]+"'>";
			html += "<input type=hidden name=model id=model value='"+obj[i][1]+"'>";
			html += obj[i][0]+"</div>";
	
			$("#modeltable").append(html);
		}
	}
}
/**************************************************************
*                       제품 중복 검색
****************************************************************/
function DocDuplicateCheck(code) {
	var obj = $("input[name='modelcode']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		if(obj.eq(i).val() == code) {
			return false;
		}
	}
	return true;
	
}
/**************************************************************
*                         제품 삭제
****************************************************************/
function delteNumberCode(){
	var obj = $("input[name='modelcode']");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		
		if(obj.eq(i).is(":checked")) {
			
			$("#" + obj.eq(i).val()).remove();
		
		}
	}
	
}
/**************************************************************
*                         제품 초기화
****************************************************************/
function deleteAllCode(){
	var obj = $("input[name='modelcode']");
	var checkCount = obj.size();
	
	for (var i = 0; i < checkCount; i++) {
			$("#" + obj.eq(i).val()).remove();
		
	}
	
}

/**************************************************************
*                         제품 초기화
****************************************************************/
function deleteAllCompleteParts(){
	var obj = $("input[name='deleteCompletePartCheck']");
	var checkCount = obj.size();
	
	for (var i = 0; i < checkCount; i++) {
			$("#" + obj.eq(i).val()).remove();
		
	}
	
}

function popupCodeList(data){
	
	alert("data length = "+data.length);
	//for (var i = 0; i < value.length; i++) {
		
	//}
	
}


/**************************************************************
*                        ROOT 목록
****************************************************************/
function setRootDefinition (oid) {
	
	var url	= getURLString("admin", "getRootDefinition", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('Root 에러')}";
			alert(msg);
		},
		
		
		success:function(data){
			$("#rootOid").append("<option value=''> 선택 </option>");
			for(var i=0; i<data.length; i++) {
				
				if(data[i].oid == oid){
					$("#rootOid").append("<option value='" + data[i].oid + "' selected>" + data[i].name + "</option>");
				}else{
					$("#rootOid").append("<option value='" + data[i].oid + "'>" + data[i].name + "</option>");
				}
				
			}
			
			
		}
		
	});
}

/**************************************************************
*                         진도번 체크 
****************************************************************/

function partNumberCheck(number){
	
	//10자리, 앞자리가 1,2,3,4,6,8,   5,7,9
	
	var firstNumber = number.substring(0,1)
	
	var charCode = firstNumber.charCodeAt(0)
	
	if ((charCode < 49 || charCode > 57)){
		return false;
	}
	
	if(charCode ==5 || charCode ==7 || charCode == 9){
		return false;
	}
	
	if(productGroupCheck(number)){
		return false;
	}
	
	return true;
} 

/**************************************************************
*                        제품군 체크  11111 00000 : 제품군 (앞자리뒷자리 5개 00000)
****************************************************************/
function productGroupCheck(number){
	
	var firstNumber = number.substring(0,1)
	
	var endNumber = number.substring(5,10);
	
	if(firstNumber== "1" && endNumber == "00000"){
		return true;
	}
	
	return false;
}

/**************************************************************
*                       changePart 상세 보기
****************************************************************/
function changePartView(ecoNumber){
		console.log("ecoNumber =" + ecoNumber);	
		
		var str = getURLString("distribute", "viewChangePart", "do") + "?ecoNumber="+ecoNumber;
	    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	    leftpos = (screen.width - 1000)/ 2;
	    toppos = (screen.height - 600) / 2 ;
	    rest = "width=1100,height=700,left=" + leftpos + ',top=' + toppos;
	    var newwin = window.open( str , "changePart", opts+rest);
	    newwin.focus();	    
}
