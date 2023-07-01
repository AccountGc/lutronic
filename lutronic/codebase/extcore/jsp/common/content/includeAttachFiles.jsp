<%@page import="com.e3ps.common.content.FileRequest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/js/axisj/ui/bulldog/page.css">
<script type="text/javascript" src="/Windchill/jsp/js/axisj/dist/AXJ.all.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/js/axisj/ui/bulldog/AXJ.min.css">
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/js/axisj/ui/bulldog/AXUpload5.css">
<script type="text/javascript" src="/Windchill/jsp/js/axisj/jquery/jquery.min.js"></script>
<div id="m_fileFullPath" style="display: none;">${m_fileFullPath}</div>
<script type="text/javascript">
var console = window.console || {log:function(){}};
var count =0;
$(document.body).ready(function(){
	count++;
	${componentName}Obj.pageStart();
	console.log('업로드 리스트');
	<c:if test="${!empty uploadedList}">
		var ${componentName}uploadedList = ${uploadedList};
		
		${componentName}Upload.setUploadedList(${componentName}uploadedList);
		<c:if test="${'SECONDARY' ne type}">
			var file = ${componentName}uploadedList;
			console.log(file);
			var filelocation = file.cashId;
			console.log('filelocation : ' +filelocation);
			
			console.log('file.formId : ' +file.formId);
			console.log("description :: "+'${description}');
			console.log("${m_fileFullPath} :: "+'${m_fileFullPath}');
			let fPath = document.querySelector("#m_fileFullPath").innerText;
			console.log(fPath);
			//fPath = fPath.replaceAll("\\","/");
			fPath = fPath.replace(/\\/gi,"/");
			let files_arr = fPath.split("/");
			let lastFileName = files_arr[files_arr.length-1];
			console.log(fPath);
			console.log(lastFileName);
			console.log('${type}');
			
			if(null == filelocation){
				$("form[id='"+file.formId+"']").append('<input type="hidden" name="${empty description ? type : description }" id="${empty description ? type : description }" value="'+file.roleType+'/'+file.name+'/${empty description ? type : description }" />');
			}else{
				$("form[id='"+file.formId+"']").append('<input type="hidden" name="${empty description ? type : description }" id="${empty description ? type : description }" value="'+file.cacheId+'/'+file.name+'/${empty description ? type : description }" />');
			}
			$("form[id='"+file.formId+"']").append("<input type='hidden' name='${type}_${description}delocIds' id='${type}_${description}delocIds' value='${description}delocIds_${type}' />");
			

		</c:if>
		<c:if test="${'SECONDARY' eq type}">
		$('#fileAttachCount').html(${componentName}uploadedList.length);
		$('#fileAttachedCount').val(${componentName}uploadedList.length);
		for( var i = 0; i < ${componentName}uploadedList.length; i++ ){
			var file = ${componentName}uploadedList[i];
			var fileName = file.name;
			$("form[id='"+file.formId+"']").append("<input type='hidden' name='${description}delocIds' id='${description}delocIds' value='"+file.${description}delocId+"' />");
		}
		</c:if>
	</c:if>
	
});
</script>

<c:choose>
	<c:when test="${'SECONDARY' eq type}">
		<input type='hidden' id='fileAttachedCount' name='fileAttachedCount' value=''>
		<div class="AXUpload5" id="${componentName}UploadBtn" style=' margin-top: 5px;'></div>
	</c:when>
	
	<c:otherwise>
		<div class="AXUpload5" id="${componentName}UploadBtn" style='height:50px; margin-top: 5px;'></div>
	</c:otherwise>
</c:choose>

<iframe id="${componentName}downloadFrame" name="${componentName}downloadFrame"  style="display:none;"></iframe>

<c:if test="${'SECONDARY' eq type}">
	<div class="H10"></div>
	<div id="${componentName}uploadQueueBox" class="AXUpload5QueueBox_list" style="height:150px;width:100%;"></div>
</c:if>

<script type="text/javascript">
var btnId = "${btnId}";
var masterHost = location.protocol + "//" + location.host+"/Windchill/";
var agent = navigator.userAgent.toLowerCase();		//브라우저 버전 정보
var isIE =  (navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1); //IE버전인지 아닌지 확인

var flashUrl = "/Windchill/jsp/axisj/lib/swfupload.swf";
var flash9Url = "/Windchill/jsp/axisj/lib/swfupload_fp9.swf";
var uploadUrl = "/Windchill/jsp/common/content/FileUpload.jsp";
var deleteUrl = "/Windchill/jsp/common/content/FileDelete.jsp";

$("#${componentName}DownloadFrame").hide();		//프레임 숨기기

var ${componentName}Upload = new AXUpload5();

AXConfig.AXUpload5.deleteConfirm = "${f:getMessage('삭제하시겠습니까?')}" ;		//삭제하시겠습니까?
AXConfig.AXUpload5.uploadSelectTxt = "";

var ${componentName}Obj = {
		pageStart: function(){
			${componentName}Obj.upload.init();
		},
		upload: {
			init: function(){
				${componentName}Upload.setConfig({
					targetID:"${componentName}UploadBtn",
					isSingleUpload: ${'SECONDARY' eq type ? false : true },
					targetButtonClass:"Blue",
					uploadFileName:"${type}",
					crossDomain:true,
					buttonTxt:'${f:getMessage('파일찾기')}', //첨부파일
					<c:if test="${'SECONDARY' eq type}">
					dropBoxID:"${componentName}uploadQueueBox",
					queueBoxID:"${componentName}uploadQueueBox", // upload queue targetID
					</c:if>
					
					flash_url : flashUrl,
					flash9_url : flash9Url,
					onClickUploadedItem: function(){ // 업로드된 목록을 클릭했을 때.
						var fileLink = "${url}${dsecPath}/jsp/FileDownload.jsp?fileName="+ this.saveName.dec()+"&originFileName="+this.name.dec();
						console.log("hi");
						if(this.cacheId.dec() == ""){
							fileLink = location.protocol + "//" + location.host+"/Windchill/"+this.uploadedPath.dec();
						}
						if (isIE) {
							window.open(fileLink, "_blank", "width=500,height=500");
						}else{
							$("#${componentName}downloadFrame").attr("src",fileLink);
						}
					},
					//uploadMaxFileSize:(5000000*1024*1024), // 업로드될 개별 파일 사이즈 (클라이언트에서 제한하는 사이즈 이지 서버에서 설정되는 값이 아닙니다.)
					//uploadMaxFileCount:0, // 업로드될 파일갯수 제한 0 은 무제한 싱글모드에선 자동으로 1개
					//uploadMaxFileSize:(1000000*1024*1024), // 업로드될 개별 파일 사이즈 (클라이언트에서 제한하는 사이즈 이지 서버에서 설정되는 값이 아닙니다.) - 100MB
					uploadMaxFileSize:(1024*1024)*100,		//									- 100MB
					uploadMaxComment: '${f:getMessage('파일당 최대')}' + '100MB' + '${f:getMessage('까지 업로드 할 수 있습니다.')}',
					uploadMaxFileCount:100, // 업로드될 파일갯수 제한 0 은 무제한 싱글모드에선 자동으로 1개		- 100개
					uploadUrl: uploadUrl,
					uploadPars:{masterHost : masterHost, userId:'${userName}', userName:'${userName}', roleType : '${type}', formId : '${formId}', description : '${description}' },
					deleteUrl: deleteUrl,
					//deletePars:{userID:'tom', userName:'액시스'},
					//file_types: "image/*|application/acad|application/msword|application/mspowerpoint|application/excel|text/plain|image/tiff",
					//file_types: "text/plain",
					//file_type_checkMsg : "지원하지 않는 파일 형식 입니다. 첨부파일을 삭제후 다시 첨부하세요.", //지원하지 않는 파일 형식 입니다. 첨부파일을 삭제후 다시 첨부하세요.\r\n (ex. *.exe, *.zip, *.txt, *.jsp, *.js ...)
					//html5_file_types : "*.easm;*.wmp;*.zip;*.txt;*.doc;*.docx;*.ppt;*.pptx;*.xls;*.xlsx;*.csv;*.pps;*.rtf;*.jpg;*.jpeg;*.gif;*.bmp;*.tif;*.tiff;*.pdf;*.hwp;*.dwg;*.dxf;*.pcb;*.sch;*.pcbfile;*.schfile;*.catpart;*.catdrawing;*.catproduct;*.model;*.prt;*.stp;*.step;*.igs;*.iges;*.cgm;*.epsi;*.plt;*.ol;*.dwg;*.dxf;*.cgm;*.hex;",
					//flash_file_types: "*.easm;*.wmp;*.zip;*.txt;*.doc;*.docx;*.ppt;*.pptx;*.xls;*.xlsx;*.csv;*.pps;*.rtf;*.jpg;*.jpeg;*.gif;*.bmp;*.tif;*.tiff;*.pdf;*.hwp;*.dwg;*.dxf;*.pcb;*.sch;*.pcbfile;*.schfile;*.catpart;*.catdrawing;*.catproduct;*.model;*.prt;*.stp;*.step;*.igs;*.iges;*.cgm;*.epsi;*.plt;*.ol;*.dwg;*.dxf;*.cgm;*.hex;",
	                //flash_file_types_description:"File",
					fileKeys:{ // 서버에서 리턴하는 json key 정의 (id는 예약어 사용할 수 없음)
						name:"name",
						type:"type",
						saveName:"saveName",
						fileSize:"fileSize",
						uploadedPath:"uploadedPath",
						thumbPath:"thumbUrl",
						roleType : "roleType",
						//delocId : "delocId",
						${description}delocId : "${description}delocId",
						cacheId : "cacheId",
						description : "description"
					},
					onUpload : function(file){
						var fileName = file.name;
						console.log("file.cacheId is : "+file.cacheId);
						//alert('${empty description ? type : description }');
						<c:if test="${'SECONDARY' ne type}">
							$('input[name="${empty description ? type : description }"]').remove();
						</c:if>
						console.log('${description}');
						$("form[id='${formId}']").append('<input type="hidden" name="${empty description ? type : description }" id="${empty description ? type : description }" value="'+file.cacheId+'/'+file.name+'/${empty description ? type : description }" />');
						window.console.log($("input[name='${empty description ? type : description }']").val());
						if( typeof fileListSet == 'function' ) {
							console.log("fileListSet run!!!" );
							fileListSet();
						}

					},
					onComplete: function(){
						if(btnId != 'none') {
							$("#" + btnId).attr("disabled", false);
						}
						var list = ${componentName}Upload.getUploadedList("json");
						  var count = Object.keys(list).length;
						//alert(count);
						<c:if test="${'SECONDARY' eq type}">
							//$('#fileAttachCount').html(($("input[name='${description}delocIds']").length += $("input[name='${empty description ? type : description }']").length));
							$('#fileAttachCount').html(count);
						</c:if>
						//$("#uploadCancelBtn").get(0).disabled = true; // 전송중지 버튼 제어
						//is${secType}Processing = false;
					},
					onStart: function(){
						if(btnId != 'none') {
							$("#" + btnId).attr("disabled", true);
						}
						//$("#uploadCancelBtn").get(0).disabled = false; // 전송중지 버튼 제어
						//is${secType}Processing = true;
					},
					onDelete: function(file){
						$('input[name="${empty description ? type : description }"]:input[value="'+file.cacheId+'/'+file.name+'/${empty description ? type : description }"]').remove();
						$("input[name='${empty description ? "" : description}delocIds']:input[value='"+file.${description}delocId+"']").remove();
						//$("input[name='${type}_${empty description ? "" : description}delocIds']:input[value='${description}delocIds_${type}']").remove();
						$("input[name='${type}_${empty description ? "" : description}delocIds']:input[value='${description}delocIds_${type}']").val(file.name);
						<c:if test="${'SECONDARY' eq type}">
							//$('#fileAttachCount').html(($("input[name='${description}delocIds']").length + $("input[name='${empty description ? type : description }']").length));
							var list = ${componentName}Upload.getUploadedList("json");
						    var count = Object.keys(list).length;
							$('#fileAttachCount').html(count);
						</c:if>
						//${type}_${description}delocIds
					},
					onError: function(errorType, extData){
						if(errorType == "html5Support"){
							//dialog.push('The File APIs are not fully supported in this browser.');
						}else if(errorType == "fileSize"){
							// 파일사이즈가 초과된 파일을 업로드 할 수 없습니다. 업로드 목록에서 제외 합니다.
							alert('파일사이즈가 초과된 파일을 업로드 할 수 없습니다. 업로드 목록에서 제외 합니다.\n(' + extData.name + ' : ' + extData.size.byte() + ')');
						}else if(errorType == "fileCount"){
							// 업로드 갯수 초과 초과된 파일은 업로드 되지 않습니다.
							alert('업로드 갯수 초과 초과된 파일은 업로드 되지 않습니다.');
						}else if(errorType == "-200"){
							// 서버오류가 발생하여 파일업로드를 실패했습니다.
							alert('서버오류가 발생하여 파일업로드를 실패했습니다.');
							secondaryUpload.cancelUpload();
						}
					}
				});
			}
		}
}
</script>