<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<script type="text/javascript">
$(function() {
	$('#${textParam}').keyup(function (event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		
		if((charCode == 38 || charCode == 40) ) {
			if(!$( '#userNameSearch' ).is( ':hidden' )){
				var isAdd = false;
				if(charCode == 38){
					isAdd = true;
				}
				moveUserNameFocus('userName', isAdd);
			}
		} else if(charCode == 13 || charCode == 27){
			$('#userNameSearch').hide();
		} else if(charCode == 8) {
			if($.trim($('#${textParam}').val()) == '') {
				$('#${hiddenParam}').val('');
				$('#${extraParam}').val('');
				$('#userNameSearch').hide();
			} else {
				autoSearchUserName(this.value);
			}
		} else {
			autoSearchUserName(this.value);
		}
	})
	$('#${textParam}').focusout(function () {
		$('#userNameSearch').hide();
	})
	$('#userSearchBtn').click(function() {
		searchUser('','${searchMode}','${hiddenParam}','${textParam}','${userType}','${returnFunction}');
	})
	$('#userDeleteBtn').click(function() {
		$('#${hiddenParam}').val('');
		$('#${textParam}').val('');
		$('#${extraParam}').val('');
	})
})

<%----------------------------------------------------------
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.moveUserNameFocus = function(id,isAdd) {
	var addCount = 0;
	var l = $('#userNameUL li').length;
	
	for(var i=0; i<l; i++){
		var cls = $('#userNameUL li').eq(i).attr('class');
		if(cls == 'hover') {
			$('#userNameUL li').eq(i).removeClass('hover');
			
			if(isAdd){
				addCount = (i-1);
			}else if(!isAdd) {
				addCount = (i+1);
			}
			
			break;
		}
	}
	if(addCount == l) {
		addCount = 0;
	}
	
	$('#userNameUL li').eq(addCount).addClass('hover');
	$('#${textParam}').val($('#userNameUL li').eq(addCount).attr('title'));
	$('#${hiddenParam}').val($('#userNameUL li').eq(addCount).attr('id'));
	$('#${extraParam}').val($('#userNameUL li').eq(addCount).attr('value'));
}

window.autoSearchUserName = function(value) {
	if(value == '') {
		$("#userNameUL li").remove();
		$("#userNameSearch").hide();
	}else {
		var url	= getURLString('common', 'autoSearchUserName', 'do');
		$.ajax({
			type:'POST'
			,url: url
			,data: {
				value : value,
				userType : '${userType}'
			}
			,dataType:'json'
			,async: false
			,cache: false
			,success:function(data){
				addSearchList(data);
			}
		});
	}
}

window.addSearchList = function(data) {
	$('#userNameUL li').remove();
	if(data.length > 0) {
		$('#userNameSearch').show();
		for(var i=0; i<data.length; i++) {
			
			var html = '';
			html += '<li title="' + data[i].name + '" id="' + data[i].oid + '" value="' + data[i].department + '" class="">';
			html += data[i].name;
			html += ' "' + data[i].department + '" <' + data[i].duty + '>';
			
			$('#userNameUL').append(html);
		}
	}else {
		$('#userNameSearch').hide();
	}
}

<%----------------------------------------------------------
*                      데이터 마우스 올렸을때
----------------------------------------------------------%>
$(document).on('mouseover', '#userNameSearch > ul > li', function() {
	
	$('#userNameUL li').each(function() {
		var cls = $(this).attr('class');
		if(cls == 'hover') {
			$(this).removeClass('hover');
		}
	})
	var name = $(this).attr('title');
	var oid = $(this).attr('id');
	var departmentName = $(this).attr('value');
	$(this).addClass('hover');
	$('#${hiddenParam}').val(oid);
	$('#${textParam}').val(name);
	$('#${extraParam}').val(departmentName);
})

function activityUser(data){
	
	$("#activeUserDepart").val(data[0][3]);
	$("#activeUser").val(data[0][6]);
	$("#activeUserName").val(data[0][1]);
}

<%----------------------------------------------------------
*                      데이터 마우스 뺄때
----------------------------------------------------------%>
$(document).on('mouseout', '#userNameSearch > ul > li', function() {
	$(this).removeClass('hover');
})
</script>

<style>
.hover{ 
 	  cursor: default;
      background:#dedede;
}
</style>
<div>
	<input type="text" name='${extraParam}' id='${extraParam}' value='<c:out value="${extraValue}"/>' style='display: none'>
	<input type="text" name='${hiddenParam}' id='${hiddenParam}' value='<c:out value="${hiddenValue}"/>' style='display: none'>
	<input type="text" name='${textParam}' id='${textParam}'  value='<c:out value="${textValue}"/>' class='txt_field' style='width: 200px; vertical-align: middle;'>
	
	<img src='/Windchill/jsp/portal/images/s_search.gif' border=0 id='userSearchBtn' style='cursor: pointer; vertical-align: middle;'>
	
	<img src='/Windchill/jsp/portal/images/x.gif' border=0 id='userDeleteBtn' style='cursor: pointer; vertical-align: middle;'>
</div>

<div id='userNameSearch' style='width: 250px; display: none; border: 1px solid black ; position: absolute; background-color: white; z-index: 1;'>
	<ul id='userNameUL' style='list-style-type: none; padding-left: 5px; text-align: left;'>
		
	</ul>
</div>

