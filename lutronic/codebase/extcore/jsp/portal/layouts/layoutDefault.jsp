<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="f" uri="/WEB-INF/functions.tld"%>
<html>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<!-- 각 화면 개발시 Tag 복사해서 붙여넣고 사용할것 -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet" type="text/css" href="/Windchill/extcore/jsp/js/dhtmlx/dhtmlx.css" />
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/default.css" type="text/css">
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/css.css" type="text/css">
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/e3ps.css" type="text/css">
<!-- AUIGrid -->
<link rel="stylesheet" href="/Windchill/extcore/AUIGrid/AUIGrid_style.css" type="text/css">

<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/dhtmlx.js"></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/dhtmlxPaging.js"></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/jquery.json-2.4.min.js"></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/pageGrid.js"></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/common.js"></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/popup.js"></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/script.js"></script>
<!-- AUIGrid -->
<script type="text/javascript" src="/Windchill/extcore/AUIGrid/AUIGrid.js"></script>
<script type="text/javascript" src="/Windchill/extcore/AUIGrid/AUIGridLicense.js"></script>
<script type="text/javascript" src="/Windchill/extcore/AUIGrid/AUIGrid_paging.js"></script>
<title>LUTRONIC PDM</title>

<style>
#div_root {
	width: 100%;
}

#div_top {
	width: 100%;
	height: 90%
}

#div_body {
	width: 100%;
	margin-left: 2px;
	margin-right: 2px;
	margin-top: 2px;
	border: 1px;
}

#div_menu {
	width: 15%;
	margin: 2px;
	float: left;
}

#div_scroll {
	width: 8px;
	float: left;
}

#div_main {
	width: 80%;
	float: left;
	margin: 2px;
	background-size: 100%;
}

.Sub_Right_LT {
	background: url(/Windchill/extcore/jsp/portal/images/base_design/Sub_Right_LT.gif);
	background-repeat: no-repeat;
	width: 19px;
	height: 29px;
}

.Sub_Right_LBG {
	background: url(/Windchill/extcore/jsp/portal/images/base_design/Sub_Right_LBG.gif);
	background-repeat: repeat-y;
	vertical-align: top;
	padding-top: 20px;
}

.Sub_Right_LB {
	background: url(/Windchill/extcore/jsp/portal/images/base_design/Sub_Right_LB.gif);
	background-repeat: no-repeat;
	width: 19px;
	height: 4px;
}

.scrollTop {
	position: fixed;
	right: 0px;
	bottom: 10px;
	background: url(/Windchill/extcore/jsp/portal/img/Blue_03_s.gif);
	width: 30px;
	height: 30px;
	cursor: pointer;
}
</style>

<script>
	$(document).ready(function() {
<%----------------------------------------------------------
	*                    textarea  자동 조절
	----------------------------------------------------------%>
	$('.textarea_autoSize').on('keyup', 'textarea', function(e) {
			$(this).css('height', 'auto');
			$(this).height(this.scrollHeight);
		});
		$('.textarea_autoSize').find('textarea').keyup();

	})

	$(function() {
		$("#errorDIV_close").click(function() {
			$("#errorDIV").hide();
		})

		$(document).keydown(function(event) {
			var charCode = (event.which) ? event.which : event.keyCode;

			//alert(charCode)
			if (charCode == 8) {

				//alert(($('input').is(':focus') || $('textarea').is(':focus') || $('select').is(':focus')));

				if (!($('input').is(':focus') || $('textarea').is(':focus') || $('select').is(':focus'))) {
					if (confirm("${f:getMessage('이전 페이지로 돌아가시겠습니까?')}")) {
						history.back();
					}
					return false;
				}
				/*
				alert('input >>>>> ' + $('input').is(':focus')
						+ '\n testarea >>>>> ' + $('textarea').is(':focus')
						+ '\n select >>>>> ' + $('select').is(':focus'));
				
				if(confirm("${f:getMessage('이전 페이지로 돌아가시겠습니까?')}")){
					history.back();
				}
				return false;
				 */
			}
		})

		$('#scrollTop').click(function() {
			$('html').animate({
				scrollTop : 0
			}, 'fast');
		})
	})

	function switchMenu() {
		var smenu = document.getElementById("subMenu_table");
		var smenuh = document.getElementById("subMenu_table_h");
		var button = document.getElementById("hiddenArrow");
		if ('none' == smenu.style.display) {
			smenu.style.display = "";
			smenuh.style.display = "none";
			button.src = "/Windchill/extcore/jsp/portal/images/base_design/layer_hidden.gif";
			button.title = "${f:getMessage('서브메뉴')} ${f:getMessage('닫기')}";
			leftMenuSlide(true)
		} else {
			smenu.style.display = "none";
			smenuh.style.display = "";
			button.src = "/Windchill/extcore/jsp/portal/images/base_design/layer_show.gif";
			button.title = "${f:getMessage('서브메뉴')} ${f:getMessage('열기')}";
			/* leftMenuSlide(true) */
		}

		if ($("#xmlString").val() != null) {
			redrawGrid();
		}
	}

	function submitSuccess(module, movePage) {
		alert("${f:getMessage('등록 성공하였습니다.')}");
		document.location = getURLString(module, movePage, "do");
	}

	function submitFail(message) {
		alert("${f:getMessage('등록에 실패하였습니다.')}\n" + message);
	}
<%----------------------------------------------------------
*                      AUI 그리드 Size 조절
----------------------------------------------------------%>
	$(window).resize(function() {

		var width = "";
		if ($("#menuTD").is(":hidden")) {
			width = null;
		} else {
			width = $(window).width() - (250 + 16 + 10);
		}

		if (window.myGridID) {
			//console.log("resize myGridID  width =" + width);
			AUIGrid.resize(window.myGridID, width);

		}

		if (window.myGridID2) {
			//console.log("resize myGridID2  width =" + width);
			AUIGrid.resize(window.myGridID2, width);

		}

	})

	$(function() {
		$("#hiddenArrow").click(function() {
			leftMenuSlide($("#menuTD").is(":hidden"));
		})
	})

	var allChecked = true;
	$(document).on("click", "#grid_wrap .aui-grid-row-num-header", function() {
		var selectionMode = AUIGrid.getProp(myGridID, "selectionMode");
		if (selectionMode == "multipleCells") {
			if (allChecked) {
				var data = AUIGrid.getGridData(myGridID);
				var layout = AUIGrid.getColumnLayout(myGridID);
				AUIGrid.setSelectionBlock(myGridID, 0, data.length, 0, layout.length);
				allChecked = false;
			} else {
				AUIGrid.setSelectionBlock(myGridID);
				allChecked = true;
			}
		}
	})

	var allChecked2 = true;
	$(document).on("click", "#grid_wrap2 .aui-grid-row-num-header", function() {
		var selectionMode = AUIGrid.getProp(myGridID2, "selectionMode");
		if (selectionMode == "multipleCells") {
			if (allChecked2) {
				var data = AUIGrid.getGridData(myGridID2);
				var layout = AUIGrid.getColumnLayout(myGridID2);
				AUIGrid.setSelectionBlock(myGridID2, 0, data.length, 0, layout.length);
				allChecked2 = false;
			} else {
				AUIGrid.setSelectionBlock(myGridID2);
				allChecked2 = true;
			}
		}
	})

	window.leftMenuSlide = function(isMenuSlide) {
		//console.log(">>>>>>>>>>>>> leftMenuSlide isMenuSlide =" + isMenuSlide +"<<<<<<<<<<<<<<<<");
		var width = "";

		if (isMenuSlide) {
			width = $(window).width() - (250 + 16 + 10);
			$("#menuTD").show();
			$("#hiddenArrow").attr("src", "/Windchill/extcore/jsp/portal/images/base_design/layer_hidden.gif");
			$("#hiddenArrow").attr("title", "${f:getMessage('서브메뉴')} ${f:getMessage('닫기')}");
			//setCookie("subMenuHidden", 1, 999999999);
		} else {
			width = $(window).width() - (16 + 10);
			$("#menuTD").hide();
			$("#hiddenArrow").attr("src", "/Windchill/extcore/jsp/portal/images/base_design/layer_show.gif");
			$("#hiddenArrow").attr("title", "${f:getMessage('서브메뉴')} ${f:getMessage('열기')}");
			//setCookie("subMenuHidden", 0, -1);
		}
		$("#bodyTD").css("width", width);

		if (isMenuSlide) {
		} else {
			width = null;
		}

		if (window.myGridID) {
			//console.log("leftMenuSlide myGridID  width =" + width);
			AUIGrid.resize(window.myGridID, width, null);
		}

		if (window.myGridID2) {
			//console.log("leftMenuSlide myGridID2 width =" + width);
			AUIGrid.resize(window.myGridID2, width, null);
		}
	}
</script>

<table width="100%" valign=top cellpadding="0" cellspacing="0">
	<tr valign=top>
		<td valign=top>
			<tiles:insertAttribute name="header" />
		</td>
	</tr>
</table>

<table width="100%" valign=top style="" id='bodyTable'>
	<tr valign=top>
		<td valign=top>
			<table cellpadding="0" cellspacing="0" border=0 height=100% width="100%">
				<tr>
					<td class="TLeft_BG"></td>
					<td class="Top_BG"></td>
					<td class="TRight_BG"></td>
				</tr>

				<tr>
					<td class="Left_BG"></td>
					<td width="99%" style="padding: 3; vertical-align: top">
						<table border="0" width="100%" height="680" cellpadding="0" cellspacing="0">
							<tr>
								<td valign=top width=0 background="/Windchill/extcore/jsp/portal/images/ds_sub.gif">
									<table border=0 id=subMenu_table width=220 height="100%" style="display:" cellpadding="0" cellspacing="0">
										<tr>
											<td valign=top>
												<tiles:insertAttribute name="menu" />
											</td>
										</tr>
									</table>

									<table border=0 id=subMenu_table_h width=1 height="100%" style="display: none" cellpadding="0" cellspacing="0">
										<tr>
											<td valign=top height="100%">

												<table width="100%" border="0" cellspacing="0" cellpadding="0" height=100%>
													<tr>
														<td align="center" valign="top">
															<table border="0" height="100%" class="info_table" cellspacing="0" cellpadding="0" width=100% style="table-layout: auto">
																<tr>
																	<td align="center" class="Subinfo_img">
																		<span id="menuTitle"></span>
																	</td>
																</tr>
																<tr>
																	<td align="center" valign="top" width=99% style="padding-right: 4; padding-left: 4"></td>
																</tr>
																<tr>
																	<td class="info_btm"></td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>

								<td valign=top width=0 height=100%>
									<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="Sub_Right_LT"></td>
										</tr>
										<tr>
											<td class="Sub_Right_LBG">
												<a href="JavaScript:switchMenu()">
													<img id="hiddenArrow" src="/Windchill/extcore/jsp/portal/images/base_design/layer_hidden.gif" border=0 title="서브메뉴 닫기">
												</a>
											</td>
										</tr>
										<tr>
											<td class="Sub_Right_LB"></td>
										</tr>
									</table>
								</td>

								<td valign=top width=100%>
									<tiles:insertAttribute name="body" />
								</td>
							</tr>
						</table>
					</td>
					<td class="Right_BG"></td>
					<td width=10px>&nbsp;&nbsp;</td>
					<td valign=top></td>
				</tr>

				<tr>
					<td class="Left_BG"></td>
					<td align=right></td>
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

<div id='scrollTop' class='scrollTop'></div>

<DIV id="lodingDIV" class='loading_edge' style='display: none;'>
	<img src="/Windchill/extcore/jsp/portal/images/loading.gif" />
</DIV>

<DIV id="errorDIV" style="display: none; width: 100%; height: 100%; position: fixed; top: 0; left: 0; background: rgba(300, 300, 300, 0.8);">
	<div style="width: 100%">
		<table style="width: 100%">
			<tr style="text-align: right;">
				<td>
					<img alt="" src="/Windchill/extcore/jsp/portal/img/x.gif" id="errorDIV_close" style="cursor: pointer;">
				</td>
			</tr>

		</table>
	</div>

	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>

	<span style="background-color: white; border: 1px solid red; margin-top: 25%; margin-left: 50%" id="errorText"> </span>
</DIV>

</html>