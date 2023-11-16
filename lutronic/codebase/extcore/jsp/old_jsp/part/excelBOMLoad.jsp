<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<SCRIPT LANGUAGE=JavaScript>

$(function() {
	$("#saveExcel").click(function() {
		alert("${f:getMessage('등록하시겠습니까?')}");
		if($("#excelFile").val() ==''){
			alert("Excel ${f:getMessage('파일을 선택하세요.')}");
			return;
		}
		
		$("#excelBomLoad").attr("target","list");
		$("#excelBomLoad").attr("action", getURLString("part", "excelBomLoadAction", "do")).submit();
	})
	
	$("#download").click(function() {
		location='/Windchill/jsp/part/BOM_Loader.xls';
	})
})

function inputFile(FileName){
	var pForm = document.PartDrawingForm;

   var lain = FileName.lastIndexOf('.');
	
    if(lain > 0){
    	var excelType = FileName.substring(lain+1);
    	if(excelType != 'xls' && excelType != 'xlsx') {
    		alert("Excel${f:getMessage('만 등록 가능합니다.')}");
    		valueRemove();
    		
    		return;
    	}
        //document.drawingCreateForm.name.value = fvalue.substring(lain+1);
    }else{
    	alert("Excel${f:getMessage('만 등록 가능합니다.')}");
    	valueRemove();
        return;
    }
}

function valueRemove() {
	var pForm = document.PartDrawingForm;
	pForm.excelFile.select();
	document.execCommand('Delete');
}

function download(){
	location='/Windchill/jsp/part/BOM_Loader.xls'
}
</SCRIPT>


<form name="excelBomLoad" id="excelBomLoad" method=post enctype="multipart/form-data">

<input type="hidden" name="cmd"  value="excelLoad"            />
<input type="hidden" name="fid"  value=""            />
<input type="hidden" name="location" value=""/>

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp; ${f:getMessage('품목')}${f:getMessage('관리')} > BOM ${f:getMessage('일괄등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="0" valign="top"> <!--//여백 테이블-->
	<tr height=5 valign="top">
		<td>
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
			    <tr  align=center>
			        <td valign="top" style="padding:0px 0px 0px 0px">
			        	
			            <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
			                <tr><td height=1 width=100%></td></tr>
			            </table>
			            
			            <table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			            <col width='15%'><col width='35%'><col width='15%'><col width='35%'>
			                <tr bgcolor="ffffff" height=35>
				                <td class="tdblueM"> Excel ${f:getMessage('업로드')}<span style="COLOR: red;">*</span>
				                	<table border="0" cellpadding="0" cellspacing="0" align=center>
			               			 	<tr>
			                				<td>
			                					<button type="button" class="btnCustom" id="download">
			                						<span></span>
			                						${f:getMessage('양식다운')}
			                					</button>
			                				</td>
			                		 	</tr>
			                		</table>
				                </td>
				                
				                <td class="tdwhiteL" >
					                <input type="file" name="excelFile" id="excelFile" class="txt_field" size="90" border="0" onchange="inputFile(this.value)">&nbsp;
					                
					                <button type="button" class="btnCRUD" id="saveExcel">
					                	<span></span>
					                	${f:getMessage('등록')}
					                </button>
				                </td>
				            </tr>
		           		 </table>
			        </td>
			    </tr>
			</table>
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
                <tr bgcolor="#ffffff">
                    <td>
                        <iframe src="" id="list" name="list" frameborder="0" width="100%" height="500" scrolling="no">
                        </iframe>
                    </td>
                </tr>
            </table>	
        </td>
    </tr>
</table>

</form>
<!-- InnoAP upload DIV -->



