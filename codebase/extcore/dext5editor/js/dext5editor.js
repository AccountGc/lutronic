/*
 Copyright (c) 2013, Raonwiz Technology Inc. All rights reserved.
*/
if ("undefined" != typeof window && !window.DEXT5) {
	var Dext5editor = function(a) {
		DEXT5.CEditorID = a; var e = DEXT5.util.isExistEditorName(a); if (1 == e) alert("editor's name is empty. Please, check editor's name"); else {
			if (2 == e) if ("1" == DEXT5.config.IgnoreSameEditorName) a = DEXT5.util.getNewNextEditorName(a); else { alert("editor's name is already exist. Please, check editor's name"); return } 3 != e && (DEXT5.DEXTMULTIPLEID.push(a), DEXT5.DEXTMULTIPLE["dext_frame_" + a] = a, DEXT5.DEXTHOLDER[a] = DEXT5.config.EditorHolder); DEXT5.IsEditorLoadedHashTable &&
				DEXT5.IsEditorLoadedHashTable.setItem(a, ""); DEXT5.config.XhrWithCredentials && (DEXT5.ajax.xhrWithCredentials = !0); var c = new DEXT_CONFIG; if (!Array.prototype.indexOf) try { for (var b in c) "[object array]" == Object.prototype.toString.call(c[b]).toLowerCase() && (c[b].indexOf = function(a, c) { c = c || 0; for (var b = this.length; c < b;) { if (this[c] === a) return c; ++c } return -1 }) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } 0 < DEXT5.config.InitServerXml.length ? (-1 == DEXT5.config.InitServerXml.indexOf("f=") && (-1 < DEXT5.config.InitServerXml.indexOf("?") ?
					DEXT5.config.InitServerXml += "&f=dext_editor.xml" : DEXT5.config.InitServerXml += "?f=dext_editor.xml"), c.config_url = DEXT5.config.InitServerXml + "&dext5CMD=configRequest") : 0 < DEXT5.config.InitXml.length && (0 == DEXT5.config.InitXml.indexOf("http") ? c.config_url = DEXT5.config.InitXml : c.config_url = DEXT5.rootPath + "config/" + DEXT5.config.InitXml); "1" == DEXT5.config.UseConfigTimeStamp ? -1 < c.config_url.indexOf("?") ? c.config_url += "&t=" + DEXT5.util.getTimeStamp() : c.config_url += "?t=" + DEXT5.util.getTimeStamp() : -1 < c.config_url.indexOf("?") ?
						c.config_url += "&t=" + DEXT5.UrlStamp : c.config_url += "?t=" + DEXT5.UrlStamp; this.editor = null; c.editor_id = a; this.FrameID = "dext_frame_" + a; c.holderID = "dext_frame_holder" + a; this.ID = a; var g = null, h = this, k = function(b, d, e) {
							"string" == typeof b && (b = DEXT5.util.trim(b)); g = b; if (null == g) alert("ErrCode : 1000"); else {
								if ("object" == typeof g) "" == g.xml && (g = DEXT5.ajax.load(c.config_url), g = DEXT5.util.stringToXML(g)); else if (0 == g.indexOf("[OK]")) g = g.substring(4), g = DEXT5.util.base64_decode(g), g = g.substring(g.indexOf("<?")), g = DEXT5.util.stringToXML(g);
								else if (0 == g.indexOf("[OK-NE]")) g = g.substring(7), g = DEXT5.util.makeDecryptReponseMessage(g), g = g.substring(g.indexOf("<?")), g = DEXT5.util.stringToXML(g); else if (0 == g.indexOf("[OK-NE2]")) g = g.substring(8), g = DEXT5.util.makeDecryptReponseMessageEx(g), g = g.substring(g.indexOf("<?")), g = DEXT5.util.stringToXML(g); else { if (0 == g.indexOf("[FAIL]")) { alert("Error occurred reading the Editor Settings"); return } (g = DEXT5.util.stringToXML(g)) || alert("Error occurred reading the Editor Settings") } e.configXml = g; var h = DEXT5.util.xml.getAllNodes(g);
								if (h && "undefined" != typeof h.parsererror) alert("Error occurred parsing the Editor Settings"); else {
									var f = { "JS|InterworkingModuleFirst": { config: "interworkingModuleFirst" }, "JS|InterworkingModuleSecond": { config: "interworkingModuleSecond" }, "JS|InterworkingModuleThird": { config: "interworkingModuleThird" } }; f["XML|xss_protection.xss_use"] = f["JS|XssUse"] = f["JS|zXssUse"] = { config: "xss_use" }; f["XML|xss_protection.xss_remove_tags"] = f["JS|XssRemoveTags"] = f["JS|zXssRemoveTags"] = {
										config: "xss_remove_tags", configFn: function(a) {
											return "0" ==
												a ? 0 : a.toLowerCase()
										}
									}; f["XML|xss_protection.xss_remove_events"] = f["JS|XssRemoveEvents"] = f["JS|zXssRemoveEvents"] = {
										config: "xss_remove_events", configFn: function(a) {
											return "all" == a.toLowerCase() ? "onabort,onactivate,onafterprint,onafterupdate,onbeforeactivate,onbeforecopy,onbeforecut,onbeforedeactivate,onbeforeeditfocus,onbeforepaste,onbeforeprint,onbeforeunload,onbeforeupdate,onbegin,onblur,onbounce,oncellchange,onchange,onclick,oncontentready,oncontentsave,oncontextmenu,oncontrolselect,oncopy,oncut,ondataavailable,ondatasetchanged,ondatasetcomplete,ondblclick,ondeactivate,ondetach,ondocumentready,ondrag,ondragdrop,ondragend,ondragenter,ondragleave,ondragover,ondragstart,ondrop,onend,onerror,onerrorupdate,onfilterchange,onfinish,onfocus,onfocusin,onfocusout,onhelp,onhide,onkeydown,onkeypress,onkeyup,onlayoutcomplete,onload,onlosecapture,onmediacomplete,onmediaerror,onmedialoadfailed,onmousedown,onmouseenter,onmouseleave,onmousemove,onmouseout,onmouseover,onmouseup,onmousewheel,onmove,onmoveend,onmovestart,onopenstatechange,onoutofsync,onpaste,onpause,onplaystatechange,onpropertychange,onreadystatechange,onrepeat,onreset,onresize,onresizeend,onresizestart,onresume,onreverse,onrowclick,onrowenter,onrowexit,onrowout,onrowover,onrowsdelete,onrowsinserted,onsave,onscroll,onseek,onselect,onselectionchange,onselectstart,onshow,onstart,onstop,onsubmit,onsyncrestored,ontimeerror,ontrackchange,onunload,onurlflip,formaction,onwheel,ontoggle,oninput" :
												"0" == a ? 0 : a.toLowerCase()
										}
									}; f["XML|xss_protection.xss_use@allow_events_attribute"] = f["JS|XssAllowEventsAttribute"] = f["JS|zXssAllowEventsAttribute"] = { config: "xss_allow_events_attribute" }; f["XML|xss_protection.xss_use@check_attribute"] = f["JS|XssCheckAttribute"] = { config: "xss_check_attribute", configFn: function(a) { return a.split(",") } }; f["XML|xss_protection.xss_allow_url.allow_url"] = f["JS|XssAllowUrl"] = { config: "xss_allow_url", valueType: "array", jsFn: function(a) { return a = a.split(",") } }; f["JS|ZIndex"] = f["JS|zIndex"] =
										{ config: "zIndex", configFn: function(a) { if (1E4 <= (a = parseInt(a, 10))) return a } }; f["JS|EditorBorder"] = { config: "editorborder" }; f["XML|top_menu.menu"] = f["JS|TopMenuItem"] = { config: "topMenuItem", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|remove_item.item"] = f["JS|RemoveItem"] = { config: "removeItem", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|remove_context_item.item"] = f["JS|RemoveContextItem"] = { config: "removeContextItem", valueType: "array", jsFn: function(a) { return a.split(",") } };
									f["XML|setting.figure@use"] = f["JS|Figure.Use"] = { config: "figure.use" }; f["XML|setting.figure@figure_style"] = f["JS|Figure.FigureStyle"] = { config: "figure.figurestyle" }; f["XML|setting.figure@figcaption_style"] = f["JS|Figure.FigcaptionStyle"] = { config: "figure.figcaptionstyle" }; f["XML|setting.figure@default_caption"] = f["JS|Figure.DefaultCaption"] = { config: "figure.defaultcaption" }; f["XML|setting.auto_list@use"] = f["JS|AutoList.Use"] = { config: "autoList.use" }; f["XML|setting.undo.undo_count"] = f["JS|UndoCount"] = {
										config: "undoCount",
										configFn: function(a) { return parseInt(a, 10) }
									}; f["XML|setting.undo.allow_delete_count"] = f["JS|AllowDeleteCount"] = { config: "allowDeleteCount" }; f["XML|setting.undo.light_mode"] = f["JS|UseUndoLightMode"] = { config: "useUndoLightMode" }; f["XML|setting.word_count"] = f["JS|WordCount.Use"] = { config: "wordCount.use" }; f["XML|setting.word_count@limit"] = f["JS|WordCount.Limit"] = { config: "wordCount.limit", configFn: function(a) { if ("1" == c.wordCount.use) return a } }; f["XML|setting.word_count@limit_char"] = f["JS|WordCount.LimitChar"] =
										{ config: "wordCount.limitchar", configFn: function(a) { if ("1" == c.wordCount.use) return c.wordCount.limitcount = a } }; f["XML|setting.word_count@limit_byte"] = f["JS|WordCount.LimitByte"] = { config: "wordCount.limitbyte" }; f["XML|setting.word_count@limit_line"] = f["JS|WordCount.LimitLine"] = { config: "wordCount.limitline" }; f["XML|setting.word_count@count_white_space"] = f["JS|WordCount.CountWhiteSpace"] = { config: "wordCount.countwhitespace", configFn: function(a) { if ("" != c.wordCount.limit || "" != c.wordCount.limitcount) return a } };
									f["XML|setting.word_count@limit_message"] = f["JS|WordCount.LimitMessage"] = { config: "wordCount.limitmessage", configFn: function(a) { if ("1" == c.wordCount.limit) return a } }; f["XML|font_family@use_local_font"] = f["JS|UseLocalFont"] = { config: "useLocalFont" }; f["XML|font_family@use_keyin"] = f["JS|UseFontFamilyKeyin"] = { config: "useFontFamilyKeyin" }; f["XML|font_size@use_keyin"] = f["JS|UseFontSizeKeyin"] = { config: "useFontSizeKeyin" }; f["XML|font_size@use_inc_dec"] = f["JS|UseFontSizeIncDec"] = { config: "useFontSizeIncDec" };
									f["XML|line_height@use_keyin"] = f["JS|UseLineHeightKeyin"] = { config: "useLineHeightKeyin" }; f["XML|line_height@use_inc_dec"] = f["JS|UseLineHeightIncDec"] = { config: "useLineHeightIncDec" }; f["XML|letter_spacing@use_inc_dec"] = f["JS|UseLetterSpacingIncDec"] = { config: "useLetterSpacingIncDec" }; f["XML|font_family@use_recently_font"] = f["JS|UseRecentlyFont"] = { config: "useRecentlyFont" }; f["XML|setting.forbidden_word"] = f["JS|ForbiddenWord"] = { config: "forbiddenWord" }; f["XML|setting.personal_data"] = f["JS|PersonalData"] =
										{ config: "personalData" }; f["XML|setting.use_remove_style"] = f["JS|RemoveStyle.Use"] = { config: "removeStyle.use" }; f["XML|setting.use_remove_style@tag"] = f["JS|RemoveStyle.Tag"] = { config: "removeStyle.tag", configFn: function(a) { if ("1" == c.removeStyle.use) return a } }; f["XML|setting.use_remove_style@style"] = f["JS|RemoveStyle.Style"] = { config: "removeStyle.style", configFn: function(a) { if ("1" == c.removeStyle.use) return a } }; f["XML|setting.allow_unable_to_delete_msg"] = f["JS|AllowUnableToDeleteMsg"] = { config: "allowUnableToDeleteMsg" };
									f["XML|setting.font_family"] = f["JS|DefaultFontFamily"] = f["JS|UserFontFamily"] = f["JS|userFontFamily"] = { config: "defaultFontFamily", configFn: function(a) { "\ub9d1\uc740\uace0\ub515" == a && (a = "\ub9d1\uc740 \uace0\ub515"); return a } }; f["XML|setting.font_size"] = f["JS|DefaultFontSize"] = f["JS|UserFontSize"] = f["JS|userFontSize"] = { config: "defaultFontSize", configFn: function(a) { 0 > a.toString().indexOf("pt") && 0 > a.toString().indexOf("px") && (a += "pt"); return a } }; f["XML|setting.font_size@inc_dec_gap"] = f["JS|FontSizeIncDecGap"] =
										{ config: "fontSizeIncDecGap" }; f["XML|setting.line_height"] = f["JS|DefaultLineHeight"] = { config: "defaultLineHeight" }; f["XML|setting.line_height@mode"] = f["JS|LineHeightMode"] = { config: "lineHeightMode" }; f["XML|setting.line_height@inc_dec_gap"] = f["JS|LineHeightIncDecGap"] = { config: "lineHeightIncDecGap" }; f["XML|setting.letter_spacing@inc_dec_gap"] = f["JS|LetterSpacingIncDecGap"] = { config: "letterSpacingIncDecGap" }; f["XML|setting.font_margin@top"] = f["JS|DefaultFontMarginTop"] = {
											config: "defaultFontMarginTop", configFn: function(a) {
												0 >
												a.indexOf("pt") && 0 > a.indexOf("px") && (a += "px"); return a
											}
										}; f["XML|setting.font_margin@bottom"] = f["JS|DefaultFontMarginBottom"] = { config: "defaultFontMarginBottom", configFn: function(a) { 0 > a.indexOf("pt") && 0 > a.indexOf("px") && (a += "px"); return a } }; f["XML|setting.allow_img_size"] = f["JS|AllowImgSize"] = { config: "allowImgSize" }; f["XML|setting.frame_fullscreen"] = f["JS|FrameFullScreen"] = { config: "frameFullScreen" }; f["XML|setting.runtimes"] = f["JS|RunTimes"] = f["JS|Runtimes"] = { config: "runtimes" }; f["XML|setting.editor_tab_disable"] =
											f["JS|EditorTabDisable"] = { config: "EditorTabDisable" }; f["XML|setting.editor_tab_disable@tab_space"] = f["JS|TabSpace"] = { config: "tabSpace", configFn: function(a) { var b = a; if (0 < b) { a = ""; for (var c = 0; c < b; c++)a += "&nbsp;"; return a } } }; f["XML|setting.context_menu_disable"] = f["JS|ContextMenuDisable"] = { config: "contextMenuDisable" }; f["XML|setting.ie_compatible"] = f["JS|IECompatible"] = { config: "ieCompatible" }; f["XML|setting.auto_url_detect"] = f["JS|AutoUrlDetect"] = { config: "autoUrlDetect" }; f["XML|setting.office_linefix"] =
												f["JS|officeLineFix"] = f["JS|OfficeLineFix"] = { config: "officeLineFix" }; f["XML|setting.paste_remove_empty_tag"] = f["JS|PasteRemoveEmptyTag"] = { config: "pasteRemoveEmptyTag" }; f["XML|setting.scroll_overflow"] = f["JS|scrollOverflow"] = f["JS|ScrollOverflow"] = { config: "scrollOverflow" }; f["XML|setting.default_imemode"] = f["JS|DefaultImemode"] = { config: "defaultImemode" }; f["XML|setting.disable_tabletap"] = f["JS|DisableTabletap"] = { config: "disableTabletap" }; f["XML|setting.auto_body_fit"] = f["JS|AutoBodyFit"] = { config: "autoBodyFit" };
									f["XML|setting.auto_body_fit@use_noncreation_area_background"] = f["JS|UseNoncreationAreaBackground"] = { config: "useNoncreationAreaBackground" }; f["XML|setting.disable_keydown"] = f["JS|DisableKeydown"] = { config: "disableKeydown" }; f["XML|setting.custom_event.image.ondbclick"] = f["JS|CustomEventImageOndbclick"] = { config: "customEventCmd.image.ondbclick" }; f["XML|setting.custom_event.hyperlink.ondbclick"] = f["JS|CustomEventHyperlinkOndbclick"] = { config: "customEventCmd.hyperLink.ondbclick" }; f["XML|setting.pageurl.script.symbol"] =
										f["JS|SymbolUrl"] = { config: "symbolUrl" }; f["XML|setting.pageurl.script.symbol@custom_css_url"] = f["JS|SymbolCustomCssUrl"] = { config: "symbolCustomCssUrl" }; f["XML|setting.icon_name"] = f["JS|IconName"] = { config: "style.iconName" }; f["XML|setting.image_base_url"] = f["JS|ImageBaseUrl"] = { config: "imageBaseUrl" }; f["XML|setting.drag_resize"] = f["JS|DragResize"] = { config: "dragResize" }; f["XML|setting.drag_resize@apply_browser"] = f["JS|DragResizeApplyBrowser"] = { config: "dragResizeApplyBrowser", configFn: function(a) { return a.split(",") } };
									f["XML|setting.drag_resize@movable"] = f["JS|DragResizeMovable"] = { config: "dragResizeMovable" }; f["XML|setting.drag_resize@apply_div_class"] = f["JS|DragResizeApplyDivClass"] = { config: "dragResizeApplyDivClass" }; f["XML|font_family.font"] = f["JS|FontFamilyList"] = { config: "fontFamilyList", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|display_font_family.font"] = f["JS|DisplayFontFamilyList"] = { config: "displayFontFamilyList", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|font_size.size"] =
										f["JS|FontSizeList"] = { config: "fontSizeList", valueType: "array", jsFn: function(a) { return c.fontSizeList.concat(a.split(",")) } }; f["XML|line_height.line"] = f["JS|LineHeightList"] = { config: "lineHeightList", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|zoom.item"] = f["JS|ZoomList"] = { config: "zoomList", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["JS|FormattingList"] = { config: "formattingList", jsFn: function(a) { c.formattingList.push(""); return c.formattingList.concat(a.split(",")) } };
									f["XML|letter_spacing.spacing"] = f["JS|LetterSpacingList"] = { config: "letterSpacingList", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|setting.use_ruler"] = f["JS|Ruler.Use"] = { config: "ruler.use", jsFn: function(a) { c.ruler.configType = "JS"; return a }, configFn: function(a) { if ("1" == a) return c.ruler.view = "1", a } }; f["XML|setting.use_ruler@ruler_init_position"] = f["JS|Ruler.InitPosition"] = {
										config: "ruler.rulerInitPosition", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) {
											if ("JS" !=
												c.ruler.configType) return a
										}, configFn: function(a) { if ("1" == c.ruler.use) return a = DEXT5.util.parseIntOr0(a) + "", a = a.split(","), "2" != c.ruler.mode && (a = a.sort(function(a, b) { return a - b })), a }
									}; f["XML|setting.use_ruler@view_pointer"] = f["JS|Ruler.ViewPointer"] = { config: "ruler.viewPointer", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return a } }; f["XML|setting.use_ruler@view_guide_line"] = f["JS|Ruler.ViewGuideLine"] =
										{ config: "ruler.viewGuideLine", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return a } }; f["XML|setting.use_ruler@guide_line_style"] = f["JS|Ruler.GuideLineStyle"] = { config: "ruler.guideLineStyle", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return a } }; f["XML|setting.use_ruler@view_ruler"] = f["JS|Ruler.ViewRuler"] =
											{ config: "ruler.viewRuler", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return a } }; f["XML|setting.use_ruler@mode"] = f["JS|Ruler.Mode"] = {
												config: "ruler.mode", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) {
													if ("1" == c.ruler.use) {
														if ("2" == a) {
															if (DEXT5.browser.ie && 7 >= DEXT5.browser.ieVersion) return "1"; c.ruler.viewPointer =
																"1"; c.ruler.viewGuideLine = "1"; c.ruler.viewRuler = "1"; c.autoBodyFit = "1"
														} return a
													}
												}
											}; f["XML|setting.use_ruler@guide_line_color"] = f["JS|Ruler.GuideLineColor"] = { config: "ruler.guideLineColor", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return a } }; f["XML|setting.use_ruler@move_guide_line_color"] = f["JS|Ruler.MoveGuideLineColor"] = {
												config: "ruler.moveGuideLineColor", jsFn: function(a) { if ("JS" == c.ruler.configType) return a },
												xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use && "2" == c.ruler.mode) return a }
											}; f["XML|setting.use_ruler@move_guide_line_style"] = f["JS|Ruler.MoveGuideLineStyle"] = { config: "ruler.moveGuideLineStyle", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use && "2" == c.ruler.mode) return a } }; f["XML|setting.use_ruler@use_inoutdent"] = f["JS|Ruler.UseInoutdent"] = {
												config: "ruler.useInoutdent",
												jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use && "2" == c.ruler.mode) return a }
											}; f["XML|setting.use_ruler@move_gap"] = f["JS|Ruler.MoveGap"] = { config: "ruler.moveGap", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use && "2" == c.ruler.mode) return a } }; f["XML|setting.use_ruler@use_resize_event"] = f["JS|Ruler.UseResizeEvent"] =
												{ config: "ruler.useResizeEvent", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use && "2" == c.ruler.mode) return a } }; f["XML|setting.use_ruler@default_view"] = f["JS|Ruler.DefaultView"] = { config: "ruler.defaultView", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return a } }; f["XML|setting.use_ruler@auto_fit_mode"] =
													f["JS|Ruler.AutoFitMode"] = { config: "ruler.autoFitMode", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return a } }; f["XML|setting.use_ruler@fix_editor_width"] = f["JS|Ruler.FixEditorWidth"] = {
														config: "ruler.fixEditorWidth", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) {
															if ("1" == c.ruler.use && "2" == c.ruler.mode && "1" == c.ruler.autoFitMode &&
																"0" == c.useNoncreationAreaBackground) return a
														}
													}; f["XML|setting.use_ruler@use_vertical"] = f["JS|Ruler.UseVertical"] = { config: "ruler.useVertical", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return DEXT5.browser.ie && 7 >= DEXT5.browser.ieVersion && (a = 0), a } }; f["XML|setting.use_ruler@use_mouse_guide"] = f["JS|Ruler.UseMouseGuide"] = {
														config: "ruler.useMouseGuide", jsFn: function(a) { if ("JS" == c.ruler.configType) return a },
														xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use && "2" == c.ruler.mode) return a }
													}; f["XML|setting.use_ruler@use_pointer_value"] = f["JS|Ruler.UsePointerValue"] = { config: "ruler.usePointerValue", jsFn: function(a) { if ("JS" == c.ruler.configType) return a }, xmlFn: function(a) { if ("JS" != c.ruler.configType) return a }, configFn: function(a) { if ("1" == c.ruler.use) return a } }; f["XML|setting.use_horizontal_line"] = { config: "horizontalLine.use" }; f["XML|setting.use_horizontal_line@url"] =
														f["JS|UseHorizontalLine"] = { config: "horizontalLine.url", jsFn: function(a) { c.horizontalLine.use = "1"; return a }, configFn: function(a) { "0" == a && (c.horizontalLine.use = "0"); return a.split(",") } }; f["XML|setting.use_horizontal_line@height"] = f["JS|UseHorizontalLineHeight"] = { config: "horizontalLine.height", configFn: function(a) { c.horizontalLine.use = "2"; return DEXT5.util.parseIntOr0(a) } }; f["XML|setting.use_horizontal_line@style"] = f["JS|UseHorizontalLineStyle"] = { config: "horizontalLine.style" }; f["XML|setting.use_horizontal_line@repeat"] =
															f["JS|UseHorizontalLineRepeat"] = { config: "horizontalLine.repeat" }; f["XML|setting.enter_tag"] = f["JS|EnterTag"] = { config: "enterTag", configFn: function(a) { a = a.toLowerCase(); return "br" != a && "div" != a ? "" : a } }; f["XML|setting.set_default_style"] = f["JS|SetDefaultStyle.Value"] = { config: "setDefaultStyle.value" }; f["XML|setting.set_default_style@body_id"] = f["JS|SetDefaultStyle.BodyId"] = { config: "setDefaultStyle.body_id", configFn: function(a) { if ("" != c.setDefaultStyle.value && "0" != c.setDefaultStyle.value) return a } }; f["XML|setting.set_default_style@user_style"] =
																f["JS|SetDefaultStyle.UserStyle"] = { config: "setDefaultUserStyle", configFn: function(a) { if ("" != c.setDefaultStyle.value && "0" != c.setDefaultStyle.value) return a.split(",") } }; f["XML|setting.set_default_style@line_height_mode"] = f["JS|SetDefaultStyle.LineHeightMode"] = { config: "setDefaultStyle.line_height_mode", configFn: function(a) { if ("" != c.setDefaultStyle.value && "0" != c.setDefaultStyle.value) return a } }; f["XML|setting.set_default_style@dext_set_style"] = f["JS|SetDefaultStyle.DextSetStyle"] = { config: "setDefaultStyle.dext_set_style" };
									f["XML|setting.set_default_style@remove_body_style_in_set"] = f["JS|SetDefaultStyle.RemoveBodyStyleInSet"] = { config: "setDefaultStyle.removeBodyStyleInSet" }; f["XML|setting.drag_and_drop_allow"] = f["JS|DragAndDropAllow"] = { config: "dragAndDropAllow" }; f["XML|setting.limit_paste_html_length"] = f["JS|LimitPasteHtml"] = { config: "limitPasteHtmlLength.value" }; f["XML|setting.limit_paste_html_length@length"] = f["JS|LimitPasteHtmlLength"] = {
										config: "limitPasteHtmlLength.length", configFn: function(a) {
											if (c.limitPasteHtmlLength.value) return parseInt(a,
												10)
										}
									}; f["XML|setting.wrap_ptag_to_source"] = f["JS|WrapPtagToSource"] = { config: "wrapPtagToSource" }; f["XML|setting.wrap_ptag_to_source@apply_to_get_api"] = f["JS|WrapPtagToGetApi"] = { config: "wrapPtagToGetApi" }; f["XML|setting.wrap_ptag_to_source@skip_tag"] = f["JS|WrapPtagSkipTag"] = { config: "wrapPtagSkipTag" }; f["XML|setting.paste_to_image"] = f["JS|PasteToImage"] = { config: "pasteToImage", configFn: function(a) { return DEXT5.browser.mobile ? "0" : a } }; f["XML|setting.paste_to_image@excel_image_fix"] = f["JS|ExcelImageFix"] =
										{ config: "excelImageFix" }; f["XML|setting.paste_to_image@popup_mode"] = f["JS|PasteToImagePopupMode"] = { config: "pasteToImagePopupMode" }; f["XML|setting.paste_to_image@type"] = f["JS|PasteToImageType"] = { config: "pasteToImageType", configFn: function(a) { if (0 < a.length) return a = DEXT5.util.trim(a), a.split(",") } }; f["XML|setting.color_picker_input_kind"] = f["JS|ColorPickerInputKind"] = { config: "colorPickerInputKind" }; f["XML|setting.cell_selection_mode"] = f["JS|CellSelectionMode"] = { config: "cellSelectionMode" }; f["XML|setting.remove_space_in_tagname"] =
											f["JS|RemoveSpaceInTagname"] = { config: "removeSpaceInTagname" }; f["XML|setting.view_mode_browser_menu"] = f["JS|ViewModeBrowserMenu"] = { config: "viewModeBrowserMenu" }; f["XML|setting.view_mode_allow_copy"] = f["JS|ViewModeAllowCopy"] = { config: "viewModeAllowCopy" }; f["XML|setting.event_for_paste_image"] = f["JS|EventForPasteImage"] = { config: "eventForPasteImage" }; f["XML|setting.remove_colgroup"] = f["JS|RemoveColgroup"] = { config: "removeColgroup" }; f["XML|setting.use_htmlprocess_worker"] = f["JS|UseHtmlProcessByWorker"] =
												{ config: "useHtmlProcessByWorker", configFn: function(a) { return 0 == (DEXT5.browser.HTML5Supported && "Worker" in window) ? "0" : a } }; f["XML|setting.use_htmlprocess_worker@use_set_api"] = f["JS|UseHtmlProcessByWorkerSetApi"] = { config: "useHtmlProcessByWorkerSetApi", configFn: function(a) { if ("1" == c.useHtmlProcessByWorker) return a } }; f["XML|setting.un_orderedlist_property@ulClass"] = f["JS|UnOrderListDefaultClass"] = { config: "unOrderListDefaultClass" }; f["XML|setting.un_orderedlist_property@olClass"] = f["JS|OrderListDefaultClass"] =
													{ config: "orderListDefaultClass" }; f["XML|setting.use_html_correction"] = f["JS|UseHtmlCorrection"] = { config: "useHtmlCorrection" }; f["XML|setting.use_html_correction@remove_incorrect_attribute"] = f["JS|RemoveIncorrectAttribute"] = { config: "removeIncorrectAttribute", configFn: function(a) { if ("1" == c.useHtmlCorrection) return a } }; f["XML|setting.use_html_correction@replace_space"] = f["JS|ReplaceSpace"] = { config: "replaceSpace", configFn: function(a) { if ("1" == c.useHtmlCorrection) return a } }; f["XML|setting.use_html_correction@skip_tag"] =
														f["JS|SkipTagInParser"] = { config: "skipTagInParser", configFn: function(a) { if ("1" == c.useHtmlCorrection) return a } }; f["XML|setting.use_html_correction@limit_length"] = f["JS|HtmlCorrectionLimitLength"] = { config: "htmlCorrectionLimitLength", configFn: function(a) { if ("1" == c.useHtmlCorrection) return DEXT5.util.parseIntOr0(a) } }; f["XML|setting.formlist_url"] = f["JS|FormListUrl"] = { config: "forms_url", configFn: function(a) { "/" == a.substring(0, 1) && (a = location.protocol + "//" + location.host + a); return a } }; f["XML|setting.emoticon_url"] =
															f["JS|EmoticonUrl"] = { config: "emoticon_url", configFn: function(a) { "/" == a.substring(0, 1) && (a = location.protocol + "//" + location.host + a); return a } }; f["XML|setting.set_auto_save"] = f["JS|SetAutoSave.Mode"] = { config: "setAutoSave.mode" }; f["XML|setting.set_auto_save@interval"] = f["JS|SetAutoSave.Interval"] = { config: "setAutoSave.interval", configFn: function(a) { if ("1" == c.setAutoSave.mode && (a = Math.floor(10 * a) / 10, .5 <= a)) return a } }; f["XML|setting.set_auto_save@max_count"] = f["JS|SetAutoSave.MaxCount"] = {
																config: "setAutoSave.maxCount",
																configFn: function(a) { if ("0" != c.setAutoSave.mode) return 10 < DEXT5.util.parseIntOr0(a) && (a = "10"), a }
															}; f["XML|setting.set_auto_save@unique_key"] = f["JS|SetAutoSave.UniqueKey"] = { config: "setAutoSave.unique_key", configFn: function(a) { if ("0" != c.setAutoSave.mode) return a } }; f["XML|setting.set_auto_save@use_encrypt"] = f["JS|SetAutoSave.UseEncrypt"] = { config: "setAutoSave.use_encrypt", configFn: function(a) { if ("0" != c.setAutoSave.mode) return a } }; f["XML|setting.set_auto_save@use_manually_save"] = f["JS|SetAutoSave.UseManuallySave"] =
																{ config: "setAutoSave.useManuallySave" }; f["XML|setting.set_auto_save@use_manually_save_shortcut_key"] = f["JS|SetAutoSave.UseManuallySaveShortcutKey"] = { config: "setAutoSave.useManuallySaveShortcutKey", configFn: function(a) { if ("1" == c.setAutoSave.useManuallySave) return a } }; f["XML|setting.set_auto_save@save_and_start_interval"] = f["JS|SetAutoSave.SaveAndStartInterval"] = { config: "setAutoSave.saveAndStartInterval" }; f["XML|setting.set_auto_save@popup_width"] = f["JS|SetAutoSave.PopupWidth"] = { config: "setAutoSave.popupWidth" };
									f["XML|setting.set_auto_save@popup_height"] = f["JS|SetAutoSave.PopupHeight"] = { config: "setAutoSave.popupHeight" }; f["XML|setting.insert_carriage_return"] = f["JS|InsertCarriageReturn"] = { config: "insertCarriageReturn" }; f["XML|setting.return_event@mouse_event"] = f["JS|ReturnEventMouse"] = { config: "returnEvent.mouse_event" }; f["XML|setting.return_event@keyboard_event"] = f["JS|ReturnEventKeyboard"] = { config: "returnEvent.key_event" }; f["XML|setting.return_event@command_event"] = f["JS|ReturnEventCommand"] = { config: "returnEvent.command_event" };
									f["XML|setting.return_event@input_event"] = f["JS|ReturnEventInput"] = { config: "returnEvent.input_event" }; f["XML|setting.return_event@focus_event"] = f["JS|ReturnEventFocus"] = { config: "returnEvent.focus_event" }; f["XML|setting.return_event@drag_event"] = f["JS|ReturnEventDrag"] = { config: "returnEvent.drag_event" }; f["XML|setting.use_correct_in_outdent"] = f["JS|UseCorrectInOutdent"] = { config: "useCorrectInOutdent" }; f["XML|setting.browser_bugfixed.ie11_jaso"] = f["JS|Ie11BugFixedJASO"] = {
										config: "ie11_BugFixed_JASO", configFn: function(a) {
											if (DEXT5.browser.ie &&
												(7 == DEXT5.browser.trident || 12 <= DEXT5.browser.ieVersion)) return a
										}
									}; f["XML|setting.browser_bugfixed.ie11_jaso@replace_br"] = f["JS|Ie11BugFixedReplaceBr"] = { config: "ie11_BugFixed_ReplaceBr", configFn: function(a) { if ("1" == c.ie11_BugFixed_JASO) return a } }; f["XML|setting.browser_bugfixed.ie11_jaso@delete_table_align"] = f["JS|Ie11BugFixedDeleteTableAlign"] = { config: "ie11_BugFixed_DeleteTableAlign", configFn: function(a) { if ("1" == c.ie11_BugFixed_JASO) return a } }; f["XML|setting.browser_bugfixed.ie11_jaso@replace_align_margin"] =
										f["JS|Ie11BugFixedReplaceAlignMargin"] = { config: "ie11_BugFixed_ReplaceAlignMargin" }; f["XML|setting.browser_bugfixed.ie_remove_hyfont"] = f["JS|IeBugFixedHyfont"] = { config: "ie_BugFixed_Hyfont" }; f["XML|setting.browser_bugfixed.ie_remove_hyfont@replace_font"] = f["JS|IeBugFixedHyfontReplaceFont"] = { config: "ie_BugFixed_Hyfont_Replace_Font" }; f["XML|setting.browser_bugfixed.apply_all_browser"] = f["JS|IeBugFixedApplyAllBrowser"] = {
											config: "ie_BugFixed_Apply_All_Browser", configFn: function(a) {
												"1" == a && (c.ie11_BugFixed_JASO =
													"2", c.ie11_BugFixed_DeleteTableAlign = "1"); return a
											}
										}; f["XML|setting.browser_bugfixed.ie11_typing_bug_in_table"] = f["JS|Ie11BugFixedTypingBugInTable"] = { config: "ie11_BugFixed_typing_bug_in_table", configFn: function(a) { if ("0" == c.ie11_BugFixed_JASO || 0 == DEXT5.browser.ie || DEXT5.browser.ie && 11 != DEXT5.browser.ieVersion) a = 0; return a } }; f["XML|setting.replace_empty_tag_with_space"] = f["JS|ReplaceEmptyTagWithSpace"] = { config: "replaceEmptyTagWithSpace" }; f["XML|setting.image_custom_property_delimiter"] = f["JS|ImageCustomPropertyDelimiter"] =
											{ config: "imageCustomPropertyDelimiter" }; f["XML|setting.manager_mode@use"] = f["JS|ManagerMode"] = { config: "formMode" }; f["XML|setting.manager_mode.event_list.event"] = f["JS|EventList"] = { config: "eventList", valueType: "array", jsFn: function(a) { return a.split(",") }, configFn: function(a) { if ("1" == c.formMode) return a } }; f["XML|setting.manager_mode.table_lock@default_show_lock_icon_user_mode"] = f["JS|AdminTableLock.DefaultShowLockIconUserMode"] = {
												config: "adminTableLock.defaultShowLockIconUserMode", jsFn: function(a) {
													if (!c.adminTableLock.configType ||
														"JS" == c.adminTableLock.configType) return c.adminTableLock.configType = "JS", a
												}, xmlFn: function(a) { if (!c.adminTableLock.configType || "XML" == c.adminTableLock.configType) return c.adminTableLock.configType = "XML", a }
											}; f["XML|setting.manager_mode.table_lock@default_lock_name"] = f["JS|AdminTableLock.DefaultLockName"] = {
												config: "adminTableLock.defaultLockName", jsFn: function(a) { if (!c.adminTableLock.configType || "JS" == c.adminTableLock.configType) return c.adminTableLock.configType = "JS", a }, xmlFn: function(a) {
													if (!c.adminTableLock.configType ||
														"XML" == c.adminTableLock.configType) return c.adminTableLock.configType = "XML", a
												}
											}; f["XML|setting.manager_mode.table_lock@check_lock_name"] = f["JS|AdminTableLock.CheckLockName"] = { config: "adminTableLock.checkLockName", jsFn: function(a) { if (!c.adminTableLock.configType || "JS" == c.adminTableLock.configType) return c.adminTableLock.configType = "JS", a }, xmlFn: function(a) { if (!c.adminTableLock.configType || "XML" == c.adminTableLock.configType) return c.adminTableLock.configType = "XML", a }, configFn: function(a) { return a.split(",") } };
									f["XML|setting.manager_mode.table_lock@default_table_lock_mode"] = f["JS|AdminTableLock.DefaultTableLockMode"] = { config: "adminTableLock.defaultTableLockMode", jsFn: function(a) { if (!c.adminTableLock.configType || "JS" == c.adminTableLock.configType) return c.adminTableLock.configType = "JS", a }, xmlFn: function(a) { if (!c.adminTableLock.configType || "XML" == c.adminTableLock.configType) return c.adminTableLock.configType = "XML", a } }; f["XML|setting.table_lock_user_mode"] = f["JS|UserTableLock.Use"] = {
										config: "userTableLock.use",
										jsFn: function(a) { c.userTableLock.configType = "JS"; return a }, xmlFn: function(a) { c.userTableLock.configType = "XML"; return a }
									}; f["XML|setting.table_lock_user_mode@lock_name"] = f["JS|UserTableLock.LockName"] = { config: "userTableLock.lockName", jsFn: function(a) { if ("JS" == c.userTableLock.configType) return a }, xmlFn: function(a) { if ("JS" != c.userTableLock.configType) return a }, configFn: function(a) { if ("1" == c.userTableLock.use) return a.split(",") } }; f["XML|setting.table_lock_user_mode@default_table_lock_mode"] = f["JS|UserTableLock.DefaultTableLockMode"] =
										{ config: "userTableLock.defaultTableLockMode", jsFn: function(a) { if ("JS" == c.userTableLock.configType) return a }, xmlFn: function(a) { if ("JS" != c.userTableLock.configType) return a }, configFn: function(a) { if ("1" == c.userTableLock.use) return a } }; f["XML|setting.table_lock_user_mode@default_show_lock_icon"] = f["JS|UserTableLock.DefaultShowLockIcon"] = {
											config: "userTableLock.defaultShowLockIcon", jsFn: function(a) { if ("JS" == c.userTableLock.configType) return a }, xmlFn: function(a) { if ("JS" != c.userTableLock.configType) return a },
											configFn: function(a) { if ("1" == c.userTableLock.use) return a }
										}; f["XML|setting.table_lock_user_mode@table_lock_mode"] = f["JS|UserTableLock.TableLockMode"] = { config: "userTableLock.tableLockMode", jsFn: function(a) { if ("JS" == c.userTableLock.configType) return a }, xmlFn: function(a) { if ("JS" != c.userTableLock.configType) return a }, configFn: function(a) { if ("1" == c.userTableLock.use) return a } }; f["XML|setting.table_lock_user_mode@allow_change_mode"] = f["JS|UserTableLock.AllowChangeMode"] = { config: "userTableLock.allowChangeMode" };
									f["XML|setting.open_document@before_open_event"] = f["JS|OpenDocument.BeforeOpenEvent"] = { config: "openDocument.beforeOpenEvent" }; f["XML|setting.open_document.word@max_size"] = f["JS|OpenDocument.Word.MaxSize"] = { config: "openDocument.word.maxSize", xmlFn: function(a) { return parseInt(a, 0) } }; f["XML|setting.open_document.word@max_page"] = f["JS|OpenDocument.Word.MaxPage"] = { config: "openDocument.word.maxPage", xmlFn: function(a) { return parseInt(a, 0) } }; f["XML|setting.open_document.excel@max_size"] = f["JS|OpenDocument.Excel.MaxSize"] =
										{ config: "openDocument.excel.maxSize", xmlFn: function(a) { return parseInt(a, 0) } }; f["XML|setting.open_document.excel@max_sheet"] = f["JS|OpenDocument.Excel.MaxSheet"] = { config: "openDocument.excel.maxSheet", xmlFn: function(a) { return parseInt(a, 0) } }; f["XML|setting.open_document.powerpoint@max_size"] = f["JS|OpenDocument.Powerpoint.MaxSize"] = { config: "openDocument.powerpoint.maxSize", xmlFn: function(a) { return parseInt(a, 0) } }; f["XML|setting.open_document.powerpoint@max_slide"] = f["JS|OpenDocument.Powerpoint.MaxSlide"] =
											{ config: "openDocument.powerpoint.maxSlide", xmlFn: function(a) { return parseInt(a, 0) } }; f["XML|setting.open_document@use_hwp"] = f["JS|OpenDocument.UseHwp"] = { config: "openDocument.useHwp" }; f["XML|setting.open_document@use_html5_fileopen"] = f["JS|OpenDocument.UseHtml5FileOpen"] = { config: "openDocument.useHtml5FileOpen" }; f["XML|setting.remove_last_br_tag"] = f["JS|RemoveLastBrTag"] = { config: "removeLastBrTag" }; f["XML|setting.editor_body_editable"] = f["JS|EditorBodyEditable"] = {
												config: "editorBodyEditableTemp", configFn: function(a) {
													"0" ==
													a ? (c.editorBodyEditable = !1, c.editorBodyEditableEx = !1) : "1" == a && (c.editorBodyEditable = !0, c.editorBodyEditableEx = !0); return a
												}
											}; f["XML|setting.editor_body_editable@mode"] = f["JS|EditorBodyEditableMode"] = { config: "editorBodyEditableMode" }; f["XML|setting.replace_outside_image"] = f["JS|ReplaceOutsideImage"] = { config: "replaceOutsideImage.use", jsFn: function(a) { c.replaceOutsideImage.configType = "JS"; return a } }; f["XML|setting.replace_outside_image@except_domain"] = f["JS|ReplaceOutsideImageExceptDomain"] = {
												config: "replaceOutsideImage.exceptDomain",
												jsFn: function(a) { if ("JS" == c.replaceOutsideImage.configType) return a }, xmlFn: function(a) { if ("JS" != c.replaceOutsideImage.configType) return a }, configFn: function(a) { if ("1" == c.replaceOutsideImage.use) return a.split(",") }
											}; f["XML|setting.replace_outside_image@target_domain"] = f["JS|ReplaceOutsideImageTargetDomain"] = {
												config: "replaceOutsideImage.targetDomain", jsFn: function(a) { if ("JS" == c.replaceOutsideImage.configType) return a }, xmlFn: function(a) { if ("JS" != c.replaceOutsideImage.configType) return a }, configFn: function(a) {
													if ("1" ==
														c.replaceOutsideImage.use) return a.split(",")
												}
											}; f["XML|setting.remove_comment"] = f["JS|RemoveComment"] = { config: "removeComment" }; f["XML|setting.set_default_value_in_empty_tag"] = f["JS|SetDefaultValueInEmptyTag"] = { config: "setDefaultValueInEmptyTag", configFn: function(a) { return a.split(",") } }; f["XML|setting.document.doc_title"] = f["JS|Document.DocTitle"] = { config: "document.docTitle" }; f["XML|setting.hybrid.hybrid_window_mode"] = f["JS|HybridWindowMode"] = { config: "hybridWindowMode" }; f["XML|setting.use_get_htmltolowercase"] =
												f["JS|UseGetHtmlToLowerCase"] = { config: "useGetHtmlToLowerCase" }; f["XML|setting.apply_style_empty_tag"] = f["JS|ApplyStyleEmptyTag"] = { config: "applyStyleEmptyTag" }; f["XML|setting.auto_destroy"] = f["JS|AutoDestroy.Use"] = { config: "autoDestroy.use" }; f["XML|setting.auto_destroy@move_cursor"] = f["JS|AutoDestroy.MoveCursor"] = { config: "autoDestroy.moveCursor" }; f["XML|setting.init_focus"] = f["JS|InitFocus"] = {
													config: "initFocusTemp", configFn: function(a) {
														"0" == a ? (c.initFocus = !1, c.initFocusForSetAPI = !1) : "1" == a && (c.initFocus =
															!0, c.initFocusForSetAPI = !0); return a
													}
												}; f["XML|setting.empty_tag_remove_in_setapi"] = f["JS|EmptyTagRemoveInSetapi"] = { config: "emptyTagRemoveInSetapi" }; f["XML|setting.replace_empty_span_tag_in_setapi"] = f["JS|ReplaceEmptySpanTagInSetapi"] = { config: "replaceEmptySpanTagInSetapi" }; f["XML|setting.replace_empty_span_tag_in_setapi@only_table"] = f["JS|ReplaceEmptySpanTagInSetapiOnlyTable"] = { config: "replaceEmptySpanTagInSetapiOnlyTable" }; f["XML|setting.remove_mso_class"] = f["JS|RemoveMsoClass"] = { config: "removeMsoClass" };
									f["XML|setting.plugin_install_type"] = f["JS|PluginInstallType"] = { config: "pluginInstallType", configFn: function(a) { "2" == a && (c.pluginInstallFileName = "dext5editorSL.exe"); return a } }; f["XML|setting.plugin_install_type@install_url"] = f["JS|PluginInstallUrl"] = { config: "pluginInstallUrl", configFn: function(a) { if ("0" != c.pluginInstallType) return a } }; f["XML|setting.plugin_install_type@use_install_guide"] = f["JS|UsePluginInstallGuide"] = { config: "usePluginInstallGuide" }; f["XML|setting.plugin_install_type@install_guide_type"] =
										f["JS|PluginInstallGuideType"] = { config: "pluginInstallGuideType", configFn: function(a) { if ("1" == c.usePluginInstallGuide) return a } }; f["XML|setting.plugin_install_type@zIndex"] = f["JS|PluginInstallGuideZIndex"] = { config: "pluginInstallGuideZIndex", configFn: function(a) { if ("0" == c.pluginInstallGuideType) return parseInt(a, 10) } }; f["JS|PhotoEditorId"] = { config: "photoEditorId" }; f["XML|setting.table_template_list_url"] = f["JS|TableTemplateListUrl"] = {
											config: "tableTemplateListUrl", configFn: function(a) {
												"/" == a.substring(0,
													1) && (a = location.protocol + "//" + location.host + a); return a
											}
										}; f["XML|setting.table_template_list_url@use_basic_template"] = f["JS|UseBasicTemplate"] = { config: "useBasicTemplate", configFn: function(a) { if ("" != c.tableTemplateListUrl) return a } }; f["XML|setting.use_replace_image"] = f["JS|UseReplaceImage"] = { config: "useReplaceImage" }; f["XML|setting.auto_plugin_update"] = f["JS|AutoPluginUpdate"] = { config: "autoPluginUpdate" }; f["XML|setting.remove_empty_tag"] = f["JS|RemoveEmptyTag"] = { config: "removeEmptyTag" }; f["XML|setting.remove_empty_tag@use_set_value"] =
											f["JS|RemoveEmptyTagSetValue"] = { config: "removeEmptyTagSetValue", configFn: function(a) { if ("0" == c.removeEmptyTag) return a } }; f["XML|setting.remove_empty_tag@insert_nbsp_for_line_break"] = f["JS|RemoveEmptyTagInsertNbspForLineBreak"] = { config: "removeEmptyTagInsertNbspForLineBreak", configFn: function(a) { if ("0" == c.removeEmptyTag) return a } }; f["XML|setting.remove_empty_tag@replace_default_paragraph_value"] = f["JS|ReplaceDefaultParagraphValue"] = { config: "removeEmptyTagReplaceDefaultParagraphValue" }; f["JS|ButtonText001"] =
												{ config: "buttonText001" }; f["XML|setting.disable_insert_image"] = f["JS|DisableInsertImage"] = { config: "disableInsertImage", configFn: function(a) { if ("" != a) { var b = ""; return b = "all" == a.toLowerCase() ? ",image,cell,doc_bg_image,hyperlink,table,paste_image," : "," + a.toLowerCase() + "," } } }; f["XML|uploader_setting.handler_url_save_for_notes"] = f["JS|HandlerUrlSaveForNotes"] = { config: "post_url_save_for_notes" }; f["XML|extra_setting.paste_image_base64"] = f["JS|PasteImageBase64"] = { config: "paste_image_base64" }; f["XML|extra_setting.paste_image_base64@remove"] =
													f["JS|PasteImageBase64Remove"] = { config: "paste_image_base64_remove", configFn: function(a) { "1" == a && (c.pasteToImage = "0"); return a } }; f["XML|extra_setting.paste_image_base64@view_base64_source"] = f["JS|ViewImgBase64Source"] = { config: "viewImgBase64Source" }; f["XML|extra_setting.empty_tag_remove"] = f["JS|EmptyTagRemove"] = { config: "empty_tag_remove" }; f["XML|extra_setting.custom_code"] = f["JS|CustomCode"] = { config: "custom_code", configFn: function(a) { return d.CustomCode = a } }; f["XML|uploader_setting@method"] = f["JS|UploadMethod"] =
														{ config: "uploadMethod", configFn: function(a) { if ("base64" == a) { var b = "FileReader" in window; "html5" != c.runtimes || b ? c.paste_image_base64 = "1" : a = "upload"; return a.toLowerCase() } } }; f["XML|uploader_setting@use_html5mode"] = f["JS|UploadUseHTML5"] = { config: "uploadUseHTML5" }; f["XML|uploader_setting@file_name_encoding"] = f["JS|UploadFileNameEncoding"] = { config: "uploadFileNameEncoding" }; f["XML|uploader_setting@upload_image_file_object"] = f["JS|UploadImageFileObject"] = { config: "uploadImageFileObject" }; f["XML|uploader_setting.server_domain"] =
															f["JS|ServerDomain"] = { config: "serverDomain" }; f["XML|uploader_setting.image_convert_format"] = f["JS|ImageConvertFormat"] = { config: "image_convert_format" }; f["XML|uploader_setting.image_convert_width"] = f["JS|ImageConvertWidth"] = { config: "image_convert_width" }; f["XML|uploader_setting.image_convert_height"] = f["JS|ImageConvertHeight"] = { config: "image_convert_height" }; f["XML|uploader_setting.image_auto_fit"] = f["JS|ImageAutoFit"] = { config: "image_auto_fit" }; f["XML|uploader_setting.image_auto_convert"] = f["JS|ImageAutoConvert"] =
																{ config: "imageAutoConvert" }; f["XML|uploader_setting.allow_media_file_type"] = f["JS|AllowMediaFileType"] = { config: "allowMediaFileType", configFn: function(a) { if (0 < a.length) return a = a.replace(/\s/gi, ""), a.split(",") } }; f["XML|uploader_setting.allow_media_file_type@max_file_size"] = f["JS|MaxMediaFileSize"] = { config: "maxMediaFileSize", configFn: function(a) { var b = DEXT5.util.getUnit(a), b = DEXT5.util.getUnitSize(b); return parseInt(a, 10) * b } }; f["XML|uploader_setting.allow_image_file_type"] = f["JS|AllowImageFileType"] =
																	{ config: "allowImageFileType", configFn: function(a) { if (0 < a.length) return a = a.replace(/\s/gi, ""), a.split(",") } }; f["XML|uploader_setting.allow_image_file_type@max_file_size"] = f["JS|MaxImageFileSize"] = { config: "maxImageFileSize", configFn: function(a) { var b = DEXT5.util.getUnit(a), b = DEXT5.util.getUnitSize(b); return parseInt(a, 10) * b } }; f["XML|uploader_setting.allow_image_file_type@max_base64file_count"] = f["JS|MaxImageBase64fileCount"] = { config: "maxImageBase64fileCount" }; f["XML|uploader_setting.allow_flash_file_type"] =
																		f["JS|AllowFlashFileType"] = { config: "allowFlashFileType", configFn: function(a) { if (0 < a.length) return a = a.replace(/\s/gi, ""), a.split(",") } }; f["XML|uploader_setting.allow_flash_file_type@max_file_size"] = f["JS|MaxFlashFileSize"] = { config: "maxFlashFileSize", configFn: function(a) { var b = DEXT5.util.getUnit(a), b = DEXT5.util.getUnitSize(b); return parseInt(a, 10) * b } }; f["XML|uploader_setting.allow_insert_file_type"] = f["JS|AllowInsertFileType"] = {
																			config: "allowInsertFileType", configFn: function(a) {
																				if (0 < a.length) return a =
																					a.replace(/\s/gi, ""), a.split(",")
																			}
																		}; f["XML|uploader_setting.allow_insert_file_type@max_file_size"] = f["JS|MaxInsertFileSize"] = { config: "maxInsertFileSize", configFn: function(a) { var b = DEXT5.util.getUnit(a), b = DEXT5.util.getUnitSize(b); return parseInt(a, 10) * b } }; f["XML|uploader_setting.allow_video_file_type"] = f["JS|AllowVideoFileType"] = { config: "allowVideoFileType", configFn: function(a) { if (0 < a.length) return a = a.replace(/\s/gi, ""), a.split(",") } }; f["XML|uploader_setting.allow_video_file_type@max_file_size"] =
																			f["JS|MaxVideoFileSize"] = { config: "maxVideoFileSize", configFn: function(a) { var b = DEXT5.util.getUnit(a), b = DEXT5.util.getUnitSize(b); return parseInt(a, 10) * b } }; f["XML|uploader_setting.attach_file_image"] = f["JS|AttachFileImage"] = { config: "attachFileImage" }; f["XML|setting.file_delimiter@unit_delimiter"] = f["JS|UnitDelimiter"] = { config: "unitDelimiter", configFn: function(a) { return String.fromCharCode(a) } }; f["XML|setting.file_delimiter@unit_attribute_delimiter"] = f["JS|UnitAttributeDelimiter"] = {
																				config: "unitAttributeDelimiter",
																				configFn: function(a) { return String.fromCharCode(a) }
																			}; f["XML|setting.inoutdent@default_size"] = f["JS|InoutdentDefaultSize"] = { config: "inoutdentDefaultSize" }; f["XML|setting.table_property@width"] = f["JS|TableDefaultWidth"] = { config: "tableDefaultWidth", configFn: function(a) { return DEXT5.util.parseIntOr0(a) } }; f["XML|setting.table_property@height"] = f["JS|TableDefaultHeight"] = { config: "tableDefaultHeight", configFn: function(a) { return DEXT5.util.parseIntOr0(a) } }; f["XML|setting.table_property@class"] = f["JS|TableDefaultClass"] =
																				{ config: "tableDefaultClass" }; f["XML|setting.table_property@inoutdent"] = f["JS|TableDefaultInoutdent"] = { config: "tableDefaultInoutdent" }; f["XML|setting.table_property@init_inoutdent"] = f["JS|TableInitInoutdent"] = { config: "tableInitInoutdent" }; f["XML|setting.table_property@td_height"] = f["JS|TableDefaultTdHeight"] = { config: "tableDefaultTdHeight" }; f["XML|setting.table_property@row_max_count"] = f["JS|TableRowMaxCount"] = { config: "tableRowMaxCount" }; f["XML|setting.table_property@col_max_count"] = f["JS|TableColMaxCount"] =
																					{ config: "tableColMaxCount" }; f["XML|setting.table_property@allow_inoutdent_text"] = f["JS|AllowInoutdentText"] = { config: "allowInoutdentText" }; f["XML|setting.table_property@default_border_color"] = f["JS|DefaultBorderColor"] = { config: "defaultBorderColor" }; f["XML|setting.table_property@use_border_attribute"] = f["JS|UseTableBorderAttribute"] = { config: "useTableBorderAttribute" }; f["XML|setting.table_property@adjust_cursor_in_table"] = f["JS|AdjustCursorInTable"] = {
																						config: "adjustCursorInTable", configFn: function(a) {
																							return DEXT5.browser.gecko ?
																								"0" : a
																						}
																					}; f["XML|setting.table_property.use_mouse_inoutdent"] = f["JS|UseMouseTableInoutdent"] = { config: "useMouseTableInoutdentTemp", configFn: function(a) { "0" == c.dragResizeMovable && (c.useMouseTableInoutdent = "1" == a ? !0 : !1); return a } }; f["XML|setting.table_property.limit_table_inoutdent"] = f["JS|LimitTableInoutdent"] = { config: "limitTableInoutdent" }; f["XML|setting.table_property.class_list"] = f["JS|TableClassList"] = { config: "tableClassList", configFn: function(a) { return a.split(",") } }; f["XML|setting.table_property.show_line_for_border_none"] =
																						f["JS|ShowLineForBorderNone"] = { config: "showLineForBorderNone" }; f["XML|setting.table_property.show_line_for_border_none@skip_class"] = f["JS|ShowLineForBorderNoneSkipClass"] = { config: "showLineForBorderNoneSkipClass" }; f["XML|setting.table_property.show_line_for_border_none@skip_attribute"] = f["JS|ShowLineForBorderNoneSkipAttribute"] = { config: "showLineForBorderNoneSkipAttribute" }; f["XML|setting.table_property.line_style"] = f["JS|TableLineStyleList"] = { config: "tableLineStyleList", configFn: function(a) { return a.split(",") } };
									f["XML|setting.table_property.use_background_image"] = f["JS|UseTableBackgroundImage"] = { config: "useTableBackgroundImage" }; f["XML|setting.table_property.use_height"] = f["JS|TableUseHeight"] = { config: "tableUseHeight" }; f["XML|setting.table_property.set_default_tag_in_empty_cell"] = f["JS|SetDefaultTagInEmptyCell"] = { config: "setDefaultTagInEmptyCell" }; f["XML|setting.table_property.inside_padding_td_setting"] = f["JS|InsidePaddingTdSetting"] = { config: "insidePaddingTdSetting" }; f["XML|setting.hyperlink_property.target"] =
										f["JS|HyperLinkTargetList"] = { config: "hyperlinkTargetList", configFn: function(a) { return a.split(",") } }; f["XML|setting.hyperlink_property.target@basic_list"] = f["JS|HyperlLinkTargetBasicList"] = { config: "hyperlinkTargetBasicList", configFn: function(a) { return a.split(",") } }; f["XML|setting.hyperlink_property.category"] = f["JS|HyperLinkCategoryList"] = { config: "hyperlinkCategoryList", configFn: function(a) { return a.split(",") } }; f["XML|setting.hyperlink_property.protocol"] = f["JS|HyperLinkProtocolList"] = {
											config: "hyperlinkProtocolList",
											configFn: function(a) { return a.split(",") }
										}; f["XML|setting.table_property.no_resize_class"] = f["JS|TableNoResizeClass"] = { config: "tableNoResizeClass" }; f["XML|setting.table_property.no_selection_class"] = f["JS|TableNoSelectionClass"] = { config: "tableNoSelectionClass" }; f["XML|setting.table_property.no_action_class"] = f["JS|TableNoActionClass"] = { config: "tableNoActionClass" }; f["XML|setting.table_auto_adjust"] = f["JS|TableAutoAdjust"] = { config: "tableAutoAdjust" }; f["XML|setting.table_property.auto_adjust.in_paste"] =
											f["JS|TableAutoAdjustInPaste"] = { config: "tableAutoAdjustInPaste" }; f["XML|setting.table_property.auto_adjust.in_set_html"] = f["JS|TableAutoAdjustInSetHtml"] = { config: "tableAutoAdjustInSetHtml" }; f["XML|setting.table_property.near_cell_border_style"] = f["JS|TableNearCellBorderStyle"] = { config: "tableNearCellBorderStyle" }; f["XML|setting.table_property.use_diagonal"] = f["JS|UseTableDiagonal"] = { config: "useTableDiagonal" }; f["XML|setting.table_property.use_diagonal@show_in_ie_view_page"] = f["JS|ShowDiagonalInIEViewPage"] =
												{ config: "showDiagonalInIEViewPage", configFn: function(a) { "0" == c.useTableDiagonal && (a = "0"); return a } }; f["XML|setting.table_property.use_diagonal@use_notification"] = f["JS|UseNotificationForDiagonal"] = { config: "useNotificationForDiagonal" }; f["XML|setting.img_default_size@width"] = f["JS|ImgDefaultWidth"] = { config: "imgDefaultWidth" }; f["XML|setting.img_default_size@height"] = f["JS|ImgDefaultHeight"] = {
													config: "imgDefaultHeight", configFn: function(a) {
														if (-1 < a.indexOf("%") || -1 < c.imgDefaultWidth.indexOf("%") || -1 < c.imgDefaultHeight.indexOf("%")) a =
															""; return a
													}
												}; f["XML|setting.img_default_margin@margin_left"] = f["JS|ImgDefaultMarginLeft"] = { config: "imgDefaultMarginLeft" }; f["XML|setting.img_default_margin@margin_right"] = f["JS|ImgDefaultMarginRight"] = { config: "imgDefaultMarginRight" }; f["XML|setting.img_default_margin@margin_top"] = f["JS|ImgDefaultMarginTop"] = { config: "imgDefaultMarginTop" }; f["XML|setting.img_default_margin@margin_bottom"] = f["JS|ImgDefaultMarginBottom"] = { config: "imgDefaultMarginBottom" }; f["XML|setting.width"] = f["JS|Width"] = {
													config: "style.width",
													configFn: function(a) { return a + "" }
												}; f["XML|setting.height@default_min_height"] = f["JS|DefaultMinHeight"] = { config: "style.defaultMinHeight", configFn: function(a) { var b = parseInt(a); "%" == a.replace(b, "") && (a = "150px"); return a + "" } }; f["XML|setting.height"] = f["JS|Height"] = { config: "style.height", configFn: function(a) { var b = parseInt(a); if ("%" != a.replace(b, "")) { var d = parseInt(c.style.defaultMinHeight); a = (d > b ? d : b) + a.replace(b, "") } return a + "" } }; f["XML|setting.skinname"] = f["JS|SkinName"] = {
													config: "style.skinName", configFn: function(a) {
														if ("random" ==
															a) if (a = DEXT5.util.getSkinNames(), "undefined" != typeof crypto && "undefined" != typeof crypto.randomUUID) { var b = crypto.randomUUID().replace(/-/g, "").replace(/[a-zA-Z]/g, ""), b = "" == b ? 0 : 1 * parseInt(b, 10) / Math.pow(10, b.length); a = a[Math.floor(11 * b)] } else a = a[Math.floor(11 * Math.random())]; return a
													}
												}; f["JS|DefaultMessage"] = { config: "defaultMessage" }; f["JS|FirstLoadType"] = { config: "firstLoadType" }; f["JS|FirstLoadMessage"] = { config: "firstLoadMessage" }; f["JS|FileFieldID"] = { config: "fileFieldID" }; f["JS|NextTabElementId"] =
													{ config: "nextTabElementId" }; f["JS|UserFieldID"] = { config: "userFieldID" }; f["JS|UserFieldValue"] = { config: "userFieldValue" }; f["XML|uploader_setting.to_save_path_url"] = f["JS|ToSavePathURL"] = { config: "toSavePathURL" }; f["XML|uploader_setting.to_save_file_path_url"] = f["JS|ToSaveFilePathURL"] = { config: "toSaveFilePathURL" }; f["XML|setting.source_viewtype"] = f["JS|SourceViewtype"] = { config: "sourceViewtype" }; f["XML|setting.source_viewtype@use_selection_mark"] = f["JS|UseSelectionMark"] = {
														config: "useSelectionMark", configFn: function(a) {
															"1" ==
															a && (c.sourceViewtype = "2"); return a
														}
													}; f["XML|setting.source_viewtype@unformatted"] = f["JS|SourceViewtypeUnformatted"] = { config: "sourceViewtype_unformatted", configFn: function(a) { if ("3" == c.sourceViewtype) return -1 == a.indexOf("dextmark") && (a += ",dextmark"), a = a.split(",") } }; f["XML|setting.source_viewtype@empty_tag_mode"] = f["JS|SourceViewtypeEmptyTagMode"] = { config: "sourceViewtypeEmptyTagMode" }; f["XML|setting.user_css_url"] = f["JS|UserCssUrl"] = { config: "userCssUrl", configFn: function(a) { return DEXT5.util.setProtocolBaseDomainURL(a) } };
									f["XML|setting.user_css_url@always_set"] = f["JS|UserCssAlwaysSet"] = { config: "userCssAlwaysSet" }; f["XML|setting.web_font_css_url"] = f["JS|WebFontCssUrl"] = { config: "webFontCssUrl", configFn: function(a) { return DEXT5.util.setProtocolBaseDomainURL(a) } }; f["XML|setting.web_font_css_url@always_set"] = f["JS|WebFontCssAlwaysSet"] = { config: "webFontCssAlwaysSet" }; f["XML|setting.user_js_url"] = f["JS|UserJsUrl"] = { config: "userJsUrl", configFn: function(a) { return a = DEXT5.util.setProtocolBaseDomainURL(a) } }; f["XML|setting.xhtml_value"] =
										f["JS|XhtmlValue"] = { config: "xhtml_value" }; f["XML|setting.system_title"] = f["JS|SystemTitle"] = { config: "system_title", configFn: function(a) { "0" == a && (a = ""); return a } }; f["XML|setting.view_mode_auto_width"] = f["JS|ViewModeAutoWidth"] = { config: "view_mode_auto_width" }; f["XML|setting.view_mode_auto_height"] = f["JS|ViewModeAutoHeight"] = { config: "view_mode_auto_height" }; f["JS|ChangeMultiValueMode"] = { config: "changeMultiValueMode" }; f["XML|setting.allow_link_media_caption"] = f["JS|AllowLinkMediaCaption"] = { config: "allowLinkMediaCaption" };
									f["XML|setting.user_help_url"] = f["JS|UserHelpUrl"] = { config: "userHelpUrl", configFn: function(a) { if (0 == a.toLowerCase().indexOf("http")) return "/" != a.substring(a.length - 1, a.length) && (a += "/"), c.webPath.help = a } }; f["XML|setting.img_upload_contenttype"] = f["JS|ImgUploadContenttype"] = { config: "imgUploadContenttype" }; f["JS|mimeConentEncodingType"] = f["JS|MimeConentEncodingType"] = { config: "mimeConentEncodingType" }; f["JS|mimeLocalOnly"] = f["JS|MimeLocalOnly"] = { config: "mimeLocalOnly" }; f["JS|mimeRemoveHeader"] = f["JS|MimeRemoveHeader"] =
										{ config: "mimeRemoveHeader" }; f["JS|mimeFileTypeFilter"] = f["JS|MimeFileTypeFilter"] = { config: "mimeFileTypeFilter" }; f["JS|userOpenDlgTitle"] = f["JS|UserOpenDlgTitle"] = { config: "userOpenDlgTitle" }; f["JS|userOpenDlgType"] = f["JS|UserOpenDlgType"] = { config: "userOpenDlgType" }; f["JS|userOpenDlgInitDir"] = f["JS|UserOpenDlgInitDir"] = { config: "userOpenDlgInitDir" }; f["JS|userImageDlgStyle"] = f["JS|UserImageDlgStyle"] = { config: "userImageDlgStyle" }; f["JS|mimeBaseURL"] = f["JS|MimeBaseURL"] = { config: "mimeBaseURL" }; f["XML|setting.showdialog_pos"] =
											f["JS|ShowDialogPosition"] = { config: "showDialogPosition" }; f["XML|status_bar.status"] = f["JS|StatusBarItem"] = { config: "statusBarItem", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|status_bar@init_mode"] = f["JS|StatusBarInitMode"] = { config: "statusBarInitMode" }; f["XML|status_bar.status@title"] = f["JS|StatusBarItemTitle"] = { config: "statusBarTitle", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|setting.grid_color"] = f["JS|GridColor"] = { config: "gridColor" }; f["XML|setting.grid_color_nm"] =
												f["JS|GridColorName"] = { config: "gridColorName" }; f["XML|setting.grid_spans"] = f["JS|GridSpans"] = { config: "gridSpans" }; f["XML|setting.grid_form"] = f["JS|GridForm"] = { config: "gridForm" }; f["XML|setting.encoding"] = f["JS|Encoding"] = { config: "encoding" }; f["XML|setting.use_enterprise_mode"] = f["JS|UseEnterpriseMode"] = { config: "useEnterpriseMode", configFn: function(a) { "1" == a && (c.ruler.useResizeEvent = "0"); return a } }; f["XML|setting.doctype@enforce_doctype"] = f["JS|EnforceDoctype"] = {
													config: "enforceDoctype", configFn: function(a) {
														return "0" ==
															c.useEnterpriseMode ? a : 0
													}
												}; f["XML|setting.doctype"] = f["JS|DocType"] = { config: "docType" }; f["XML|setting.xmlnsname"] = f["JS|Xmlnsname"] = { config: "xmlnsname" }; f["XML|setting.show_font_real"] = f["JS|ShowFontReal"] = { config: "showRealFont" }; f["XML|setting.show_font_real@mode"] = f["JS|ShowFontRealMode"] = { config: "showRealFontMode" }; f["XML|setting.lang"] = f["JS|InitLang"] = f["JS|Lang"] = {
													config: "lang", jsFn: function(a) { if (2 <= a.length) return a }, configFn: function(a) {
														a = "" == a ? "ko-kr" : DEXT5.util.getUserLang(a); return d.UseLang =
															a
													}
												}; f["XML|setting.accessibility"] = f["JS|Accessibility"] = { config: "accessibility" }; f["XML|setting.accessibility@validation_item"] = f["JS|AccessibilityValidationItems"] = { config: "accessibilityValidationItems" }; f["XML|setting.accessibility@tab_sequence_mode"] = f["JS|TabSequenceMode"] = { config: "tabSequenceMode" }; f["XML|uploader_setting.save_foldername_rule@allow_custom_folder"] = f["JS|SaveFolderNameRuleAllowCustomFolder"] = { config: "saveFolderNameRuleAllowCustomFolder" }; f["XML|uploader_setting.save_foldername_rule"] =
													f["JS|SaveFolderNameRule"] = { config: "saveFolderNameRule" }; f["XML|uploader_setting.save_file_foldername_rule@allow_custom_folder"] = f["JS|SaveFileFolderNameRuleAllowCustomFolder"] = { config: "saveFileFolderNameRuleAllowCustomFolder" }; f["XML|uploader_setting.save_file_foldername_rule"] = f["JS|SaveFileFolderNameRule"] = { config: "saveFileFolderNameRule" }; f["XML|uploader_setting.save_filename_rule"] = f["JS|SaveFileNameRule"] = { config: "saveFileNameRule", configFn: function(a) { if ("GUID" == a || "DATETIME" == a) return a } };
									f["XML|setting.show_topmenu"] = f["JS|ShowTopMenu"] = { config: "top_menu", configFn: function(a) { return "0" == a ? "1" : "0" } }; f["XML|setting.topmenu"] = f["JS|zTopMenu"] = { config: "top_menu" }; f["XML|setting.show_toolbar"] = f["JS|ShowToolBar"] = { config: "tool_bar", configFn: function(a) { switch (a) { case "1": return "2"; case "2": return "1"; case "3": return "0" }return "3" } }; f["XML|setting.toolbar"] = f["JS|zToolBar"] = { config: "tool_bar" }; f["XML|setting.show_topstatusbar"] = f["JS|ShowTopStatusBar"] = {
										config: "top_status_bar", configFn: function(a) {
											return "0" ==
												a ? "1" : "0"
										}
									}; f["XML|setting.topstatusbar"] = f["JS|zTopStatusBar"] = { config: "top_status_bar" }; f["XML|setting.show_statusbar"] = f["JS|ShowStatusBar"] = { config: "status_bar", configFn: function(a) { return "0" == a ? "1" : "0" } }; f["XML|setting.statusbar"] = f["JS|zStatusBar"] = { config: "status_bar" }; f["XML|setting.toolbar@grouping"] = f["XML|setting.show_toolbar@grouping"] = f["JS|ToolBarGrouping"] = { config: "tool_bar_grouping" }; f["XML|setting.resizebar"] = f["JS|ResizeBar"] = { config: "resize_bar" }; f["XML|setting.statusbar@loading"] =
										f["XML|setting.show_statusbar@loading"] = f["JS|StatusBarLoading"] = { config: "statusBarLoading" }; f["XML|setting.topstatusbar@loading"] = f["XML|setting.show_topstatusbar@loading"] = f["JS|TopStatusBarLoading"] = { config: "topStatusBarLoading" }; f["XML|tool_bar_1.tool"] = f["JS|ToolBar1"] = { config: "toolBar1", valueType: "array", configFn: function(a) { "string" == typeof a && (a = a.split(",")); newValue = []; for (var b = 0; b < a.length; b++) { var c = a[b]; "chart" == c && (c = "p_chart"); newValue.push(c) } return newValue } }; f["XML|tool_bar_2.tool"] =
											f["JS|ToolBar2"] = { config: "toolBar2", valueType: "array", configFn: function(a) { "string" == typeof a && (a = a.split(",")); newValue = []; for (var b = 0; b < a.length; b++) { var c = a[b]; "chart" == c && (c = "p_chart"); newValue.push(c) } return newValue } }; f["XML|tool_bar_1.tool:mini_photo_editor"] = f["XML|tool_bar_2.tool:mini_photo_editor"] = f["JS|UseMiniImageEditor"] = {
												config: "useMiniImageEditor", configFn: function(a) {
													return DEXT5.browser.ie && 9 >= DEXT5.browser.ieVersion || "Windows" == DEXT5.UserAgent.os.name && DEXT5.browser.safari ? "0" :
														-1 < c.toolBar1.indexOf("mini_photo_editor") || -1 < c.toolBar2.indexOf("mini_photo_editor") ? "1" : "0"
												}
											}; f["XML|tool_bar_1.tool:text_paste@mode"] = f["XML|tool_bar_2.tool:text_paste@mode"] = f["JS|TextPasteMode"] = { config: "setTextPasteMode" }; f["XML|tool_bar_1.tool:apply_format@except_style"] = f["XML|tool_bar_2.tool:apply_format@except_style"] = f["JS|ApplyFormatExceptStyle"] = { config: "applyFormatExceptStyle", configFn: function(a) { return a.split(",") } }; f["XML|tool_bar_1.tool:full_screen@top"] = f["XML|tool_bar_2.tool:full_screen@top"] =
												f["JS|FullScreenTop"] = { config: "fullScreenTop", configFn: function(a) { return DEXT5.util.parseIntOr0(a) } }; f["XML|tool_bar_1.tool:full_screen@left"] = f["XML|tool_bar_2.tool:full_screen@left"] = f["JS|FullScreenLeft"] = { config: "fullScreenLeft", configFn: function(a) { return DEXT5.util.parseIntOr0(a) } }; f["XML|tool_bar_1.tool:full_screen@right"] = f["XML|tool_bar_2.tool:full_screen@right"] = f["JS|FullScreenRight"] = { config: "fullScreenRight", configFn: function(a) { return DEXT5.util.parseIntOr0(a) } }; f["XML|tool_bar_1.tool:full_screen@bottom"] =
													f["XML|tool_bar_2.tool:full_screen@bottom"] = f["JS|FullScreenBottom"] = { config: "fullScreenBottom", configFn: function(a) { return DEXT5.util.parseIntOr0(a) } }; f["XML|tool_bar_1.tool:help@start_main_page"] = f["XML|tool_bar_2.tool:help@start_main_page"] = f["JS|HelpStartMainPage"] = { config: "helpStartMainPage", configFn: function(a) { return a } }; f["XML|tool_bar_admin.tool"] = f["JS|ToolBarAdmin"] = { config: "toolBarAdmin", valueType: "array", jsFn: function(a) { return a.split(",") } }; f["XML|plugin_ex.tool"] = f["JS|PluginToolEx"] =
														{ config: "pluginToolExArr", valueType: "array", configFn: function(a) { "string" == typeof a && (a = a.split(",")); for (var b = [], c = 0; c < a.length; c++) { var d = a[c]; "mention" == d && 0 == DEXT5.browser.ES6Supported || b.push(d) } return b } }; f["XML|setting.use_auto_toolbar"] = f["JS|UseAutoToolBar"] = { config: "useAutoToolbar", configFn: function(a) { if (DEXT5.browser.mobile) return "1" == a && (c.tool_bar_grouping = "0"), a } }; f["JS|AutoToolBar"] = { config: "autoToolbar" }; f["XML|setting.auto_tool_bar.default"] = f["JS|AutoToolBar.Default"] = {
															config: "autoToolbar.default",
															configFn: function(a) { if ("1" == c.useAutoToolbar && 0 < a.length) return "0" == a ? [] : a.split(",") }
														}; f["XML|setting.auto_tool_bar.selectedSingleCell"] = f["JS|AutoToolBar.SelectedSingleCell"] = { config: "autoToolbar.selectedSingleCell", configFn: function(a) { if ("1" == c.useAutoToolbar && 0 < a.length) return "0" == a ? [] : a.split(",") } }; f["XML|setting.auto_tool_bar.selectedMultiCell"] = f["JS|AutoToolBar.SelectedMultiCell"] = {
															config: "autoToolbar.selectedMultiCell", configFn: function(a) {
																if ("1" == c.useAutoToolbar && 0 < a.length) return "0" ==
																	a ? [] : a.split(",")
															}
														}; f["XML|setting.auto_tool_bar.focusInCell"] = f["JS|AutoToolBar.FocusInCell"] = { config: "autoToolbar.focusInCell", configFn: function(a) { if ("1" == c.useAutoToolbar && 0 < a.length) return "0" == a ? [] : a.split(",") } }; f["XML|setting.auto_tool_bar.selectArea"] = f["JS|AutoToolBar.SelectArea"] = { config: "autoToolbar.selectArea", configFn: function(a) { if ("1" == c.useAutoToolbar && 0 < a.length) return "0" == a ? [] : a.split(",") } }; f["XML|setting.auto_tool_bar.focusImage"] = f["JS|AutoToolBar.FocusImage"] = {
															config: "autoToolbar.focusImage",
															configFn: function(a) { if ("1" == c.useAutoToolbar && 0 < a.length) return "0" == a ? [] : a.split(",") }
														}; f["XML|setting.mini_photo_editor.width"] = f["JS|MiniPhotoEditor.Width"] = { config: "miniPhotoEditor.width" }; f["XML|setting.mini_photo_editor.height"] = f["JS|MiniPhotoEditor.Height"] = { config: "miniPhotoEditor.height" }; f["XML|setting.mini_photo_editor.background_color"] = f["JS|MiniPhotoEditor.BackgroundColor"] = { config: "miniPhotoEditor.backgroundColor" }; f["XML|setting.mini_photo_editor.background_opacity"] = f["JS|MiniPhotoEditor.BackgroundOpacity"] =
															{ config: "miniPhotoEditor.backgroundOpacity" }; f["XML|setting.mini_photo_editor.img_tag_remove_attribute"] = f["JS|MiniPhotoEditor.ImgTagRemoveAttribute"] = { config: "miniPhotoEditor.imgTagRemoveAttribute", configFn: function(a) { return a.split(",") } }; f["JS|HasContainer"] = { config: "hasContainer" }; f["XML|setting.default_mode"] = f["JS|Mode"] = { config: "mode" }; f["XML|uploader_setting.develop_langage"] = f["JS|DevelopLangage"] = { config: "developLang" }; f["JS|InitVisible"] = {
																config: "style.InitVisible", configFn: function(a) {
																	c.style.InitVisible =
																	0 == a ? !1 : !0
																}
															}; f["XML|setting.security.encrypt_param"] = f["JS|Security.EncryptParam"] = { config: "security.encryptParam", configFn: function(a) { if ("user" != c.developLang.toLowerCase() && "none" != c.developLang.toLowerCase()) return a } }; f["XML|setting.use_gzip"] = f["JS|UseGZip"] = {
																config: "editor_url", configFn: function(a) {
																	if (DEXT5.isRelease && "1" == a) switch (c.developLang.toUpperCase()) {
																		case "JAVA": case "JSP": return DEXT5.rootPath + "pages/editor_release_java.html"; case "PHP": return DEXT5.rootPath + "pages/editor_release_php.html";
																		default: return DEXT5.rootPath + "pages/editor_release_net.html"
																	} else if (DEXT5.isRelease && "2" == a) return DEXT5.rootPath + "pages/editor_release_static_gzip.html"
																}
															}; f["JS|focusInitObjId"] = f["JS|FocusInitObjId"] = { config: "focusInitObjId", configFn: function(a) { "" != a && (c.initFocus = !1); return a } }; f["JS|focusInitEditorObjId"] = f["JS|FocusInitEditorObjId"] = { config: "focusInitEditorObjId" }; f["JS|tabIndexObjId"] = f["JS|TabIndexObjId"] = { config: "tabIndexObjId" }; f["JS|tabIndexObjValue"] = f["JS|TabIndexObjValue"] = { config: "tabIndexObjValue" };
									f["JS|SetValueObjId"] = { config: "setValueObjId" }; f["JS|LoadedEvent"] = { config: "LoadedEvent" }; f["JS|DirectEditHtmlUrl"] = { config: "directEditHtmlUrl" }; f["XML|license.trust_sites"] = f["JS|TrustSites"] = { config: "trustSites" }; f["XML|license.print_margin_ltrb"] = f["JS|PrintMarginltrb"] = { config: "printMarginltrb" }; f["XML|license.license_key"] = f["JS|LicenseKey"] = { config: "LicenseKey" }; f["XML|license.product_key"] = f["JS|ProductKey"] = { config: "productKey" }; f["XML|license.print_preview"] = f["JS|PrintPreview"] = { config: "printPreview" };
									f["XML|license.print_preview@print_landscape"] = f["JS|PrintLandscape"] = { config: "printLandscape" }; f["XML|license.print_header"] = f["JS|PrintHeader"] = { config: "printHeader", configFn: function(a) { return c.basePrintHeader = a } }; f["XML|license.print_footer"] = f["JS|PrintFooter"] = { config: "printFooter", configFn: function(a) { return c.basePrintFooter = a } }; f["XML|license.use_form_print"] = f["JS|UseFormPrint"] = { config: "useFormPrint", configFn: function(a) { "1" == a && (c.usePrintApp = a); return a } }; f["XML|license.use_print_app"] =
										f["JS|UsePrintApp"] = { config: "usePrintApp" }; f["XML|license.license_plugin"] = f["JS|PluginRoot"] = { config: "plugin_root" }; f["XML|license.plugin_use"] = f["JS|pluginUse"] = f["JS|PluginUse"] = { config: "plugInUse", configFn: function(a) { if (DEXT5.browser.ie && 12 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) a = "0"; "1" == a && (c.runtimes = "ieplugin"); return a } }; f["XML|license.image_editor_use"] = f["JS|imageEditorUse"] = f["JS|ImageEditorUse"] = { config: "imageEditorUse" }; f["XML|license.mime_use"] = f["JS|mimeUse"] = f["JS|MimeUse"] =
											{ config: "mimeUse" }; f["XML|license.mime_use@mimeCharset"] = f["JS|mimeCharset"] = f["JS|MimeCharset"] = { config: "mimeCharset" }; f["XML|license.plugin_version"] = f["JS|PluginVersion"] = { config: "pv", configFn: function(a) { if (null != a && 7 <= a.length) return DEXT5.util.makeEncryptParamEx2(a) } }; f["XML|license.plugin_keep_version"] = f["JS|PluginKeepVersion"] = { config: "pluginKeepVersion" }; f["XML|setting.user_color_picker"] = f["JS|UserColorPicker"] = {
												config: "userColorPicker", xmlFn: function(a) {
													a = DEXT5.util.replaceAll(a, "\r\n",
														""); a = DEXT5.util.replaceAll(a, "\r", ""); a = DEXT5.util.replaceAll(a, "\n", ""); a = DEXT5.util.replaceAll(a, "\t", ""); return a = DEXT5.util.replaceAll(a, unescape("%20"), "")
												}
											}; f["XML|setting.browser_spell_check"] = f["JS|BrowserSpellCheck"] = { config: "browserSpellCheck" }; f["XML|setting.use_personal_setting"] = f["JS|UsePersonalSetting"] = { config: "usePersonalSetting" }; f["XML|setting.ol_ul_tag_mode"] = f["JS|OlUlTagMode"] = { config: "olUlTagMode" }; f["XML|setting.insert_multi_image"] = f["JS|InsertMultiImage"] = {
												config: "insertMultiImage",
												configFn: function(a) { var b = { maj: 5, l: 37, mi1: "0", mi2: "0" }; if ("1" == a && 0 > DEXT5.util.compareVersion(DEXT5.util.getRV(DEXT5._$0C), DEXT5.util.makeCRV(b))) a = "0"; else if ("0" == c.uploadUseHTML5 || 0 == DEXT5.browser.HTML5Supported) a = "0"; return a }
											}; f["XML|setting.allow_open_file_type"] = f["JS|AllowOpenFileType"] = { config: "allowOpenFileType" }; f["XML|setting.remove_hwp_dummy_tag"] = f["JS|RemoveHwpDummyTag"] = {
												config: "removeHwpDummyTag", configFn: function(a) {
													"1" == a && 0 == (DEXT5.browser.ie && 10 > DEXT5.browser.ieVersion) && (a =
														"0"); return a
												}
											}; f["XML|setting.after_paste_caret_position"] = f["JS|AfterPasteCaretPosition"] = { config: "afterPasteCaretPosition" }; f["XML|setting.adjust_empty_span"] = f["JS|AdjustEmptySpan"] = { config: "adjustEmptySpan" }; f["XML|setting.use_table_paste_dialog"] = f["JS|UseTablePasteDialog"] = { config: "useTablePasteDialog" }; f["XML|setting.image_continue_insert_maintain_value"] = f["JS|ImageContinueInsertMaintainValue"] = { config: "imageContinueInsertMaintainValue" }; f["XML|setting.use_line_break"] = f["JS|UseLineBreak"] =
												{ config: "useLineBreak" }; f["XML|setting.use_line_break@word_break_type"] = f["JS|WordBreakType"] = { config: "wordBreakType", configFn: function(a) { if ("1" == c.useLineBreak) return a } }; f["XML|setting.use_line_break@word_wrap_type"] = f["JS|WordWrapType"] = { config: "wordWrapType", configFn: function(a) { if ("1" == c.useLineBreak) return a } }; f["XML|setting.use_line_break@save_line_break_to_local_storage"] = f["JS|SaveLineBreakToLocalStorage"] = { config: "saveLineBreakToLocalStorage", configFn: function(a) { if ("1" == c.useLineBreak) return a } };
									f["XML|setting.table_property.split_cell_style"] = f["JS|SplitCellStyle"] = { config: "splitCellStyle" }; f["XML|setting.keep_original_html"] = f["JS|KeepOriginalHtml"] = { config: "keepOriginalHtml", configFn: function(a) { "1" == a && (c.sourceViewtype = "0", c.useSelectionMark = "0"); return a } }; f["XML|setting.use_default_body_space"] = f["JS|DefaultBodySpace.Use"] = { config: "defaultBodySpace.use" }; f["XML|setting.use_default_body_space@mode"] = f["JS|DefaultBodySpace.Mode"] = {
										config: "defaultBodySpace.mode", configFn: function(a) {
											if ("1" ==
												c.defaultBodySpace.use) return a
										}
									}; f["XML|setting.use_default_body_space@value"] = f["JS|DefaultBodySpace.Value"] = { config: "defaultBodySpace.value", configFn: function(a) { if ("1" == c.defaultBodySpace.use) { var b = a.split(","); 0 < b.length && (c.defaultBodySpace.configValue = 3 < b.length ? { top: b[0], right: b[1], bottom: b[2], left: b[3] } : { top: b[0], right: b[0], bottom: b[0], left: b[0] }); return a } } }; f["XML|setting.plugin_temp_path"] = f["JS|PluginTempPath"] = { config: "pluginTempPath" }; f["XML|setting.ol_list_style_type_sequence"] = f["JS|OlListStyleTypeSequence"] =
										{ config: "olListStyleTypeSequence", configFn: function(a) { for (var b = a.split(","), c = b.length, d = 0; d < c; d++) { var e = d + 1; b[e] || (e = 0); a[b[d]] = b[e] } return a } }; f["XML|setting.ul_list_style_type_sequence"] = f["JS|UlListStyleTypeSequence"] = { config: "ulListStyleTypeSequence", configFn: function(a) { for (var b = a.split(","), c = b.length, d = 0; d < c; d++) { var e = d + 1; b[e] || (e = 0); a[b[d]] = b[e] } return a } }; f["XML|setting.paste_remove_span_absolute"] = f["JS|PasteRemoveSpanAbsolute"] = { config: "pasteRemoveSpanAbsolute" }; f["XML|setting.drag_move"] =
											f["JS|DragMove"] = { config: "dragMove" }; f["JS|useLog"] = f["JS|UseLog"] = { config: "useLog" }; f["XML|setting.save_html_name"] = f["JS|SaveHtmlName"] = { config: "saveHtmlName" }; f["XML|setting.table_property.insert_drag_size"] = f["JS|TableInsertDragSize"] = { config: "tableInsertDragSize" }; f["XML|setting.paste_when_table_is_last"] = f["JS|PasteWhenTableIsLast"] = { config: "pasteWhenTableIsLast" }; f["XML|setting.custom_css_url"] = f["JS|CustomCssUrl"] = { config: "style.customCssUrl" }; f["XML|setting.htmlprocess_custom_text"] = f["JS|HtmlprocessCustomText"] =
												{ config: "style.htmlprocessCustomText" }; f["XML|setting.remove_image_in_paste_excel"] = f["JS|RemoveImageInPasteExcel"] = { config: "removeImageInPasteExcel" }; f["XML|setting.remove_td_style_in_paste_ppt"] = f["JS|RemoveTdStyleInPastePpt"] = { config: "removeTdStyleInPastePpt" }; f["XML|setting.auto_move_init_focus"] = f["JS|AutoMoveInitFocus.Use"] = { config: "autoMoveInitFocus.use", configFn: function(a) { "1" == a && (c.focusInitObjId = ""); return a } }; f["JS|AutoMoveInitFocus.TargetWindow"] = {
													config: "autoMoveInitFocus.targetWindow",
													configFn: function(a) { return a }
												}; f["JS|AddHttpHeader"] = { config: "addHttpHeader", configFn: function(a) { return a } }; f["XML|setting.show_toolbar@enable_disable_mode"] = f["JS|ToolBarEnableDisableMode"] = { config: "toolBarEnableDisableMode" }; f["XML|setting.remove_temp_folder_data_day"] = f["JS|RemoveTempFolderDataDay"] = { config: "removeTempFolderDataDay" }; f["XML|setting.force_save_as_server"] = f["JS|ForceSaveAsServer"] = { config: "forceSaveAsServer" }; f["XML|setting.check_apply_word_break_for_table"] = f["JS|CheckApplyWordBreakForTable"] =
													{ config: "checkApplyWordBreakForTable" }; f["XML|setting.highlight"] = f["JS|Highlight.Use"] = { config: "highlight.use" }; f["XML|setting.highlight@color"] = f["JS|Highlight.Color"] = { config: "highlight.color" }; f["XML|setting.popup_background_holder_id"] = f["JS|PopupBackgroundHolderId"] = { config: "popupBackgroundHolderId" }; f["XML|setting.replace_line_break"] = f["JS|ReplaceLineBreak"] = { config: "replaceLineBreak" }; f["XML|plugin_setting.auto_grow_mode"] = f["JS|AutoGrowMode"] = {
														config: "autoGrowMode", configFn: function(a) {
															"0" !=
															a && c.pluginToolExArr.push("autogrow"); return a
														}
													}; f["XML|setting.adjust_currentcolor_in_set_api"] = f["JS|AdjustCurrentColorInSetApi"] = { config: "adjustCurrentColorInSetApi" }; f["XML|setting.adjust_textindent_in_paste"] = f["JS|AdjustTextIndentInPaste"] = { config: "adjustTextIndentInPaste" }; f["XML|setting.undo@mode"] = f["JS|UndoMode"] = { config: "undoMode" }; f["XML|setting.remove_dummy_font_tag_in_paste"] = f["JS|RemoveDummyFontTagInPaste"] = { config: "removeDummyFontTagInPaste" }; f["XML|setting.remove_carriage_return_in_tag"] =
														f["JS|RemoveCarriageReturnInTag"] = { config: "removeCarriageReturnInTag" }; f["XML|setting.hyperlink_property.click_ctrl_hyperlink"] = f["JS|ClickCtrlHyperlink"] = { config: "clickCtrlHyperlink", configFn: function(a) { if (DEXT5.browser.ie && 7 >= DEXT5.browser.ieVersion || DEXT5.browser.mobile) a = "0"; return a } }; f["XML|setting.remove_lang_attribute"] = f["JS|RemoveLangAttribute"] = { config: "removeLangAttribute" }; f["JS|Event.LoadedEvent"] = { config: "event.loadedEvent", configFn: function(a) { if ("function" === typeof a) return a } };
									f["JS|Event.FrameLoaded"] = { config: "event.frameLoaded", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.OnError"] = { config: "event.onError", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.SetComplete"] = { config: "event.setComplete", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.Resized"] = { config: "event.resized", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.EditorLoaded"] = {
										config: "event.editorLoaded", configFn: function(a) {
											if ("function" ===
												typeof a) return a
										}
									}; f["JS|Event.BeforePaste"] = { config: "event.beforePaste", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.CustomAction"] = { config: "event.customAction", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.AfterChangeMode"] = { config: "event.afterChangeMode", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.LanguageDefinition"] = { config: "event.languageDefinition", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.AfterPopupShow"] =
										{ config: "event.afterPopupShow", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.Mouse"] = { config: "event.mouse", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.Command"] = { config: "event.command", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.Key"] = { config: "event.key", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.Input"] = { config: "event.input", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.PasteImage"] =
											{ config: "event.pasteImage", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.ManagerImg"] = { config: "event.managerImg", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.ManagerInput"] = { config: "event.managerInput", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.ManagerSelect"] = { config: "event.managerSelect", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.ManagerTextArea"] = {
												config: "event.managerTextArea", configFn: function(a) {
													if ("function" ===
														typeof a) return a
												}
											}; f["JS|Event.ContentSizeChange"] = { config: "event.contentSizeChange", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.BeforeInsertUrl"] = { config: "event.beforeInsertUrl", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.BeforeFullScreen"] = { config: "event.beforeFullScreen", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.FullScreen"] = { config: "event.fullScreen", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.SetInsertComplete"] =
												{ config: "event.setInsertComplete", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.DialogLoaded"] = { config: "event.dialogLoaded", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.BeforeInsertHyperlink"] = { config: "event.beforeInsertHyperlink", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.InsertEmoticon"] = { config: "event.insertEmoticon", configFn: function(a) { if ("function" === typeof a) return a } }; f["JS|Event.ApplyFontStyle"] = {
													config: "event.applyFontStyle",
													configFn: function(a) { if ("function" === typeof a) return a }
												}; f["XML|setting.adjust_cell_size_after_delete_cell"] = f["JS|AdjustCellSizeAfterDeleteCell"] = { config: "adjustCellSizeAfterDeleteCell" }; f["XML|setting.remove_dummy_div_in_hwp_paste"] = f["JS|RemoveDummyDivInHwpPaste"] = { config: "removeDummyDivInHwpPaste" }; f["XML|setting.image_size_to_server"] = f["JS|ImageSizeToServer"] = { config: "imageSizeToServer" }; f["XML|setting.paste_to_text_mode"] = f["JS|PasteToTextMode"] = { config: "pasteToTextMode" }; f["XML|setting.adjust_cursor_in_relative"] =
													f["JS|AdjustCursorInRelative"] = { config: "adjustCursorInRelative", configFn: function(a) { if (DEXT5.browser.ie) return a } }; f["XML|setting.move_style_tag_to_head"] = f["JS|MoveStyleTagToHead"] = { config: "moveStyleTagToHead" }; f["XML|setting.remove_dummy_tag"] = f["JS|RemoveDummyTag"] = { config: "removeDummyTag" }; f["XML|setting.undo@limit_length"] = f["JS|UndoLimitLength"] = { config: "undoLimitLength", configFn: function(a) { if ("" != a) return DEXT5.util.parseIntOr0(a) } }; f["XML|setting.auto_fit_in_holder"] = f["JS|AutoFitInHolder"] =
														{ config: "autoFitInHolder" }; f["XML|setting.placeholder@content"] = f["JS|Placeholder.Content"] = { config: "placeholder.content", configFn: function(a) { if (DEXT5.browser.ie && 10 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) return a = a.replace(/\r?\n?\r?\n/gi, "\\A"), a = a.replace(/\\n/gi, "\\A") } }; f["XML|setting.placeholder@font_color"] = f["JS|Placeholder.FontColor"] = { config: "placeholder.fontColor", configFn: function(a) { if (DEXT5.browser.ie && 10 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) return a } }; f["XML|setting.placeholder@font_size"] =
															f["JS|Placeholder.FontSize"] = { config: "placeholder.fontSize", configFn: function(a) { if (DEXT5.browser.ie && 10 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) return 0 > a.indexOf("pt") && 0 > a.indexOf("px") && (a += "px"), a } }; f["XML|setting.placeholder@font_family"] = f["JS|Placeholder.FontFamily"] = { config: "placeholder.fontFamily", configFn: function(a) { if (DEXT5.browser.ie && 10 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) return "\ub9d1\uc740\uace0\ub515" == a && (a = "\ub9d1\uc740 \uace0\ub515"), a } }; f["XML|setting.ignore_different_editor_name"] =
																f["JS|IgnoreDifferentEditorName"] = { config: "ignoreDifferentEditorName" }; f["XML|setting.paste_text_line_break"] = f["JS|PasteTextLineBreak"] = { config: "pasteTextLineBreak" }; f["XML|setting.replace_mso_style"] = f["JS|ReplaceMsoStyle.SettingStyle"] = { config: "replaceMsoStyle.settingStyle", configFn: function(a) { return a.split(",") } }; f["XML|setting.remove_font_type.font_family"] = f["JS|RemoveFontType.FontFamily"] = { config: "removeFontType.fontFamily", configFn: function(a) { return a.split(",") } }; f["XML|setting.remove_font.type"] =
																	f["JS|RemoveFontType.Type"] = { config: "removeFontType.type", configFn: function(a) { return a.split(",") } }; f["XML|setting.forbidden_word@mode"] = f["JS|ForbiddenWordMode"] = { config: "forbiddenWordMode", configFn: function(a) { DEXT5.browser.ie && 7 >= DEXT5.browser.ieVersion && (a = "0"); return a } }; f["JS|Event.SetForbiddenWordComplete"] = { config: "event.setForbiddenWordComplete", configFn: function(a) { if ("function" === typeof a) return a } }; f["XML|setting.forbidden_word@width"] = f["JS|ForbiddenWordWidth"] = {
																		config: "forbiddenWordWidth",
																		configFn: function(a) { if ("1" == c.forbiddenWordMode) return a = DEXT5.util.parseIntOr0(a), 300 >= a && (a = 300), a }
																	}; f["XML|setting.forbidden_word@height"] = f["JS|ForbiddenWordHeight"] = { config: "forbiddenWordHeight", configFn: function(a) { if ("1" == c.forbiddenWordMode) return a = DEXT5.util.parseIntOr0(a), 150 >= a && (a = 150), a } }; f["XML|setting.clean_nested_tag_options.remove_tag"] = f["JS|CleanNestedTagOptions.RemoveTag"] = {
																		config: "cleanNestedTagOptions.removeTag", configFn: function(a) {
																			"string" == typeof a && "" != a && (a = a.replace(/ /g,
																				"")); return a
																		}
																	}; f["XML|setting.clean_nested_tag_options.allow_style"] = f["JS|CleanNestedTagOptions.AllowStyle"] = { config: "cleanNestedTagOptions.allowStyle", configFn: function(a) { "string" == typeof a && "" != a && (a = a.replace(/ /g, ""), a = a.split(",")); return a } }; f["XML|setting.clean_nested_tag_options.nested_count"] = f["JS|CleanNestedTagOptions.NestedCount"] = { config: "cleanNestedTagOptions.nestedCount", configFn: function(a) { return DEXT5.util.parseIntOr0(a) } }; f["JS|SetEmoticonObject"] = { config: "setEmoticonObject" };
									f["XML|setting.underline_and_strike_through_mode"] = f["JS|UnderlineAndStrikeThroughMode"] = { config: "underlineAndStrikeThroughMode" }; f["XML|setting.replace_rgb_to_hex"] = f["JS|ReplaceRgbToHex"] = { config: "replaceRgbToHex" }; f["XML|setting.table_property@cell_padding"] = f["JS|TableDefaultCellPadding"] = { config: "tableDefaultCellPadding", configFn: function(a) { a = DEXT5.util.parseIntOr0(a); 0 >= a && (a = 0); return a } }; f["XML|setting.use_personal_setting@font_family_use_key_in"] = f["JS|PersonalSettingUseFontFamilyKeyin"] =
										{ config: "personalSettingUseFontFamilyKeyin" }; f["XML|setting.use_personal_setting@font_size_use_key_in"] = f["JS|PersonalSettingUseFontSizeKeyin"] = { config: "personalSettingUseFontSizeKeyin" }; f["XML|setting.use_personal_setting@line_height_use_key_in"] = f["JS|PersonalSettingUseLineHeightKeyin"] = { config: "personalSettingUseLineHeightKeyin" }; f["XML|setting.file_filter_plugin"] = f["JS|FileFilterPlugin"] = { config: "fileFilterPlugin" }; f["XML|setting.file_filter_html5"] = f["JS|FileFilterHtml5"] = { config: "fileFilterHtml5" };
									f["XML|setting.security.file_extension_detector"] = f["JS|Security.FileExtensionDetector"] = { config: "security.fileExtensionDetector", configFn: function(a) { "0" == a || 0 != DEXT5.browser.HTML5Supported && "0" != c.uploadUseHTML5 || (a = "0"); return a } }; f["XML|setting.dialog_window_scroll"] = f["JS|DialogWindowScroll"] = { config: "dialogWindowScroll", configFn: function(a) { return a } }; f["XML|setting.keep_image_original_size_auto_check"] = f["JS|KeepImageOriginalSizeAutoCheck"] = {
										config: "keepImageOriginalSizeAutoCheck", configFn: function(a) {
											DEXT5.browser.mobile &&
											(a = "0"); return a
										}
									}; f["XML|setting.paragraph_attribute_type"] = f["JS|ParagraphAttributeType"] = { config: "paragraphAttributeType", configFn: function(a) { a = a.split(","); return 1 < a.length ? a : a[0] } }; f["XML|setting.use_find_replace_shortcut"] = f["JS|UseFindReplaceShortcut"] = { config: "useFindReplaceShortcut" }; f["XML|setting.use_paste_toolbar_and_context"] = f["JS|UsePasteToolbarAndContext"] = { config: "usePasteToolbarAndContext", configFn: function(a) { "1" == a && 0 == DEXT5.browser.ES6Supported && (a = "0"); return a } }; f["XML|setting.disable_error_confirm_message"] =
										f["JS|DisableErrorConfirmMessage"] = { config: "disableErrorConfirmMessage" }; f["XML|setting.table_property.delete_table_using_key"] = f["JS|DeleteTableUsingKey"] = { config: "deleteTableUsingKey" }; f["XML|setting.keep_font_family"] = f["JS|KeepFontFamily"] = { config: "keepFontFamily", configFn: function(a) { "string" == typeof a && (a = a.split(",")); return a } }; f["XML|setting.dialog_border_radius"] = f["JS|DialogBorderRadius"] = { config: "style.dialogBorderRadius" }; f["XML|setting.dialog_box_shadow"] = f["JS|DialogBoxShadow"] = { config: "style.dialogBoxShadow" };
									f["XML|setting.dialog_border"] = f["JS|DialogBorder"] = { config: "style.dialogBorder" }; f["XML|setting.auto_set_zoom"] = f["JS|AutoSetZoom.Use"] = { config: "autoSetZoom.use", configFn: function(a) { if (DEXT5.browser.ie || DEXT5.browser.gecko) a = "0"; return a } }; f["JS|AutoSetZoom.CheckNode"] = { config: "autoSetZoom.checkNode" }; f["XML|setting.custom_css_url@detail_apply"] = f["JS|CustomCssUrlDetailApply"] = {
										config: "customCssUrlDetailApply", configFn: function(a) {
											1 != DEXT5.util.parseIntOr0(a) || "" == c.style.customCssUrl ? a = "0" : a = "1";
											return a
										}
									}; f["XML|setting.validate_url_link"] = f["JS|ValidateUrlLink"] = { config: "validateUrlLink" }; f["XML|setting.resizebar@holder_sync"] = f["JS|ResizeBarHolderSync"] = { config: "resizeBarHolderSync" }; f["XML|setting.image_quality"] = f["JS|ImageQuality.Quality"] = { config: "imageQuality.quality" }; f["XML|setting.image_quality@worker_count"] = f["JS|ImageQuality.WorkerCount"] = { config: "imageQuality.workerCount" }; f["XML|setting.image_quality@extension"] = f["JS|ImageQuality.Extension"] = { config: "imageQuality.extension" };
									f["XML|setting.image_quality@over_file_size"] = f["JS|ImageQuality.OverFileSize"] = { config: "imageQuality.overFileSize" }; f["XML|setting.mobile_popup_mode"] = f["JS|MobilePopupMode"] = { config: "mobilePopupMode" }; f["XML|setting.replace_ms_style_name"] = f["JS|ReplaceMsStyleName"] = { config: "replaceMsStyleName", configFn: function(a) { return a.replace(/\s/g, "").split(",") } }; f["XML|setting.compatibility.dingbat_char"] = f["JS|Compatibility.DingBatChar"] = {
										config: "compatibility.dingBatChar", configFn: function(a) {
											a = a.toLowerCase();
											switch (a) { case "paste": a = c.compatibility.dingBatCharPaste = "1"; break; case "set": a = c.compatibility.dingBatCharSetApi = "1"; break; case "1": c.compatibility.dingBatCharPaste = "1"; c.compatibility.dingBatCharSetApi = "1"; break; case "0": c.compatibility.dingBatCharPaste = "0", c.compatibility.dingBatCharSetApi = "0" }return a
										}
									}; f["XML|setting.compatibility.auto_resize_pasted_image"] = f["JS|Compatibility.AutoResizePastedImage"] = { config: "compatibility.autoResizePastedImage" }; f["XML|setting.compatibility.hwp_paste_image_in_html5"] =
										f["JS|Compatibility.HwpPasteImageInHtml5"] = { config: "compatibility.hwpPasteImageInHtml5" }; f["XML|setting.compatibility.hwp_paste_bullet_in_html5"] = f["JS|Compatibility.HwpPasteBulletInHtml5"] = { config: "compatibility.hwpPasteBulletInHtml5" }; f["XML|setting.compatibility.hwp_process_type_in_plugin"] = f["JS|Compatibility.HwpProcessTypeInPlugin"] = { config: "compatibility.hwpProcessTypeInPlugin" }; f["XML|setting.compatibility.font_tag_to_span"] = f["JS|Compatibility.FontTagToSpan"] = { config: "compatibility.fontTagToSpan" };
									f["XML|setting.width_fix.value"] = f["JS|WidthFix.Value"] = { config: "widthFix.value", configFn: function(a) { return a = DEXT5.util.parseIntOr0(a) + "" } }; f["XML|setting.width_fix.background_color"] = f["JS|WidthFix.BackgroundColor"] = { config: "widthFix.backgroundColor" }; f["XML|setting.width_fix.default_view"] = f["JS|WidthFix.DefaultView"] = { config: "widthFix.defaultView" }; f["XML|setting.width_fix.border"] = f["JS|WidthFix.Border"] = {
										config: "widthFix.border", configFn: function(a) {
											var b = c.widthFix.border; a = a.split("|"); if (2 ==
												a.length) { var d = a[0].toLowerCase(); switch (d) { case "outline": d = "outline"; break; case "boxshadow": d = "boxShadow"; break; default: d = "" }"" != d && (b.styleName = d, b.styleValue = a[1]) } return b
										}
									}; f["XML|setting.width_fix.padding"] = f["JS|WidthFix.Padding"] = { config: "widthFix.padding", configFn: function(a) { return DEXT5.util.parseIntOr0(a) } }; f["XML|setting.insert_sourcetag_in_video"] = f["JS|InsertSourceTagInVideo"] = { config: "insertSourceTagInVideo" }; f["XML|setting.force_font_family_change"] = f["JS|ForceFontFamilyChange"] =
										{ config: "forceFontFamilyChange" }; f["XML|setting.remove_font_size_apply_h_tag"] = f["JS|RemoveFontSizeApplyHTag"] = { config: "removeFontSizeApplyHTag" }; f["XML|setting.auto_set_document_domain"] = f["JS|AutoSetDocumentDomain"] = { config: "autoSetDocumentDomain" }; f["XML|setting.editng_area_bg_color"] = f["JS|EditingAreaBgColor"] = { config: "editingAreaBgColor" }; f["XML|setting.disable_unnecessary_key_evt"] = f["JS|DisableUnnecessaryKeyEvt"] = { config: "disableUnnecessaryKeyEvt" }; var k = function(b, d, e) {
											var f = c, g = b.config;
											if (-1 < g.indexOf(".")) { for (var g = g.split("."), h = 0; h < g.length - 1; h++)f = f[g[h]]; g = g[h] } if (e) { if ((d = e(d)) || 0 === d || "" == d) f[g] = d, b["set" + a] = 1 } else f[g] = d, b["set" + a] = 1
										}; (function(b) {
											for (var c in f) {
												var d = f[c]; if (!d["set" + a]) if (0 == c.indexOf("JS|")) {
													for (var e = c.substring(3).split("."), g = e.length, l = b, m = 0; m < g; m++)l = l ? l[e[m]] : void 0; e = !1; !d.allowEmpty || l && "" != l || (e = !0); if ("boolean" == typeof l || l && "" != l || d.allowEmpty) {
														d.jsFn && (returnValue = d.jsFn(l)) && (l = returnValue); try {
															k(d, l, d.configFn), e && d.inEmpty && (d["set" +
																a] = !0)
														} catch (n) { DEXT5 && DEXT5.logMode && console && console.log(n) }
													}
												} else {
													for (var e = c.split("@"), g = e[0].split(":"), m = g[0].substring(4).split("."), t = m.length, g = 1 < g.length ? g[1] : 0, e = 1 < e.length ? e[1] : 0, l = h, q = 0; q < t; q++) { var v = m[q], l = l ? l[v] || (l[0] ? l[0][v] : void 0) : void 0; d.nodeObj = l } if (g && l) { t = !1; for (m = 0; m < l.length; m++)if (l[m]._value == g) { l = l[m]; t = !0; break } if (!t) continue } if ("undefined" != typeof l) {
														if (e) if ("array" == d.valueType) if (l.slice) {
															tempXmlValue = l.slice(); for (m = 0; m < tempXmlValue.length; m++)tempXmlValue[m] = tempXmlValue[m]._attributes ?
																tempXmlValue[m]._attributes[e] : ""; l = tempXmlValue
														} else if (l._attributes) l = [l._attributes[e]]; else continue; else if (l._attributes) l = l._attributes[e]; else continue; else { g = e = !1; if ("object" == typeof l && 0 < l.length) { l.slice && (l = l.slice()); for (m = 0; m < l.length; m++)l[m]._value && (l[m] = l[m]._value); "array" != d.valueType ? l = l[0] : e = !0 } else if ("array" == d.valueType) l = [l._value], e = !0; else if ("object" == typeof l) { for (var A in l) { g = !0; break } g || (l = "") } !e && l._value && (l = l._value); e || "string" == typeof l || (l = "") } if ("boolean" ==
															typeof l || l && "" != l || d.allowEmpty) { d.xmlFn && (returnValue = d.xmlFn(l)) && (l = returnValue); try { k(d, l, d.configFn) } catch (w) { DEXT5 && DEXT5.logMode && console && console.log(w) } }
													}
												}
											}
										})(d); b = ""; "1" == c.ie_BugFixed_Hyfont && (b += "HY*|", "string" == typeof c.ie_BugFixed_Hyfont_Replace_Font && "" != c.ie_BugFixed_Hyfont_Replace_Font && (b += c.ie_BugFixed_Hyfont_Replace_Font.replace("all", "HY*") + "|")); "string" == typeof c.forceFontFamilyChange && "" != c.forceFontFamilyChange && (b += c.forceFontFamilyChange); if ("" != b) {
											b = b.toLowerCase(); b.lastIndexOf("|") ==
												b.length - 1 && (b = b.substring(0, b.length - 1)); var l = { definedNewFont: {}, definedNewPrefixFont: {}, targetFontReg: "" }, n = b.split("|"), z = n.length; for (b = 0; b < z; b++) { var w = n[b].split(","), A = w.length, D = "", C = ""; 1 == A ? D = w[0] : 2 == A && (D = w[0], C = w[1]); D = DEXT5.util.trim(D); C = DEXT5.util.trim(C); -1 < D.indexOf("*") ? l.definedNewPrefixFont[D.replace(/\*/g, "")] = C : l.definedNewFont[D] = C } for (p in l.definedNewFont) l.targetFontReg += p + "|"; for (p in l.definedNewPrefixFont) l.targetFontReg += p + "|"; "" != l.targetFontReg && (l.targetFontReg = l.targetFontReg.substring(0,
													l.targetFontReg.length - 1)); c.forceFontFamilyChangeObject = l
										} b = ""; l = DEXT5.util.xml.getNode(g, "uploader_setting"); "" != d.HandlerUrl ? b = d.HandlerUrl : (l = DEXT5.util.xml.getNodeValue(l, "handler_url"), 0 < l.length && (b = l)); l = ""; l = 0 == b.length ? DEXT5.rootPath : "/" == b.substring(0, 1) ? location.protocol + "//" + location.host : 0 == b.toLowerCase().indexOf("http") ? "" : DEXT5.rootPath; if (0 == b.length) switch (c.developLang.toUpperCase()) {
											case "JAVA": case "JSP": b = l + "handler/upload_handler.jsp"; break; case "PHP": b = l + "handler/upload_handler.php";
												break; case "NONE": b = ""; break; default: b = l + "handler/upload_handler.ashx"
										} else { if (0 == b.toLowerCase().indexOf("http") && b.match(/:\/\/(.[^\/]+)/)[1] != window.location.host && "file:" != location.protocol) { n = ""; switch (c.developLang.toUpperCase()) { case "JAVA": case "JSP": n = DEXT5.rootPath + "handler/upload_handler.jsp"; break; case "PHP": n = DEXT5.rootPath + "handler/upload_handler.php"; break; case "NONE": n = ""; break; default: n = DEXT5.rootPath + "handler/upload_handler.ashx" }c.proxy_url = n } b = l + b } c.post_url = b; c.runtimes = DEXT5.util.GetUserRunTimeEditor(c.runtimes);
									"1" == c.mimeUse && "ieplugin" != c.runtimes && (c.uploadMethod = "base64", c.paste_image_base64 = "1"); DEXT5.browser.HTML5Supported && "Worker" in window && (window.URL || window.webkitURL) || (c.openDocument.useHtml5FileOpen = "0"); if (0 == DEXT5.browser.ajaxOnProgressSupported || "ieplugin" == c.runtimes) c.uploadUseHTML5 = "0"; 4 == c.productKey.split("_").length && (c.useServerLicense = !0); "" == c.wordCount.limitchar || "0" == c.wordCount.limitchar ? (c.wordCount.limitcount = c.wordCount.limitbyte, c.wordCount.limitunit = "byte", c.undoCount > parseInt(c.wordCount.limitbyte) &&
										(c.undoCount = parseInt(c.wordCount.limitbyte))) : (c.wordCount.limitcount = c.wordCount.limitchar, c.wordCount.limitunit = "char"); if ("1" == c.autoBodyFit) { if ("0" == c.useLineBreak || "1" == c.useLineBreak && "0" == c.wordBreakType) c.wordBreakType = "1"; c.useLineBreak = "1"; c.wordWrapType = "1" } 0 == c.plugInUse && "ieplugin" == c.runtimes && (c.plugInUse = 1); l = c.statusBarItem.length; n = !1; for (b = 0; b < l; b++)if ("design" == c.statusBarItem[b]) { n = !0; break } 0 == n && c.statusBarItem.splice(0, 0, "design"); if ("" != c.userJsUrl) {
											l = !0; for (n = 0; n < c.xss_allow_url.length; n++)if (c.xss_allow_url[n] ==
												c.userJsUrl) { l = !1; break } 1 == l && c.xss_allow_url.push(c.userJsUrl)
										} if ("1" == c.useAutoToolbar) { for (var F in c.autoToolbar) try { var I = c.autoToolbar[F]; c.autoToolbar[F] = "0" == I ? [] : I.split(",") } catch (P) { DEXT5 && DEXT5.logMode && console && console.log(P) } for (F in c.autoToolbar) for (I = c.autoToolbar[F], l = I.length, b = 0; b < l; b++)n = I[b], 0 > c.toolBar2.indexOf(n) && c.toolBar2.push(n); for (b = c.toolBar2.length - 1; 0 <= b; b--)-1 < c.toolBar1.indexOf(c.toolBar2[b]) && c.toolBar2.splice(b, 1) } "/" != c.saveFolderNameRule && "0" == c.saveFolderNameRuleAllowCustomFolder &&
											("/" == c.saveFolderNameRule.substring(0, 1) && (c.saveFolderNameRule = c.saveFolderNameRule.substring(1)), "/" != c.saveFolderNameRule.substring(c.saveFolderNameRule.length - 1) && (c.saveFolderNameRule += "/"), "YYYYMMDD/" != c.saveFolderNameRule && "YYYYMM/" != c.saveFolderNameRule && "YYYY/" != c.saveFolderNameRule && "YYYY/MM/" != c.saveFolderNameRule && "YYYY/MM/DD/" != c.saveFolderNameRule && (c.saveFolderNameRule = "YYYY/MM")); "/" != c.saveFileFolderNameRule && "0" == c.saveFileFolderNameRuleAllowCustomFolder && ("/" == c.saveFileFolderNameRule.substring(0,
												1) && (c.saveFileFolderNameRule = c.saveFileFolderNameRule.substring(1)), "/" != c.saveFileFolderNameRule.substring(c.saveFileFolderNameRule.length - 1) && (c.saveFileFolderNameRule += "/"), "YYYYMMDD/" != c.saveFileFolderNameRule && "YYYYMM/" != c.saveFileFolderNameRule && "YYYY/" != c.saveFileFolderNameRule && "YYYY/MM/" != c.saveFileFolderNameRule && "YYYY/MM/DD/" != c.saveFileFolderNameRule && (c.saveFileFolderNameRule = "")); "" != c.userFieldValue && (-1 < c.post_url.indexOf("?") ? c.post_url = c.post_url + "&" + c.userFieldID + "=" + c.userFieldValue :
													c.post_url = c.post_url + "?" + c.userFieldID + "=" + c.userFieldValue); -1 < c.disableInsertImage.indexOf(",paste_image,") && (c.pasteToImage = "0", 0 > DEXT5.util.arrayIndexOf(c.removeItem, "paste_to_image") && c.removeItem.push("paste_to_image"), 0 > DEXT5.util.arrayIndexOf(c.removeItem, "paste_to_image_detail") && c.removeItem.push("paste_to_image_detail")); if (0 < c.displayFontFamilyList.length) try {
														F = {}; var B = c.displayFontFamilyList.length; for (b = 0; b < B; b++) { var E = c.displayFontFamilyList[b].split("|"); 2 == E.length && (F[E[0]] = E[1]) } c.displayFontFamilyList =
															F
													} catch (Q) { DEXT5 && DEXT5.logMode && console && console.log(Q) } -1 < c.imgDefaultWidth.indexOf("%") && "2" == c.image_auto_fit && (c.image_auto_fit = "1"); "" == c.fileFilterHtml5 && (c.fileFilterHtml5 = DEXT5.util.getMimeFilter(c.allowImageFileType)); DEXT5.isRelease ? (c.popupCssUrl = "ko-kr" != d.UseLang ? "../css/editor_popup_" + d.UseLang + ".css" : "../css/editor_popup.css", c.dialogJSUrl = "../js/editor_dialog.js", c.fileEncodingJSUrl = "../js/editor_fileopen_encoding.min.js", c.fileEncodingWorkerJSUrl = "../js/editor_fileopen_encodingWorker.min.js",
														c.ajaxUploadJSUrl = "../js/editor_upload.min.js", c.conversionJSUrl = "../js/editor_conversion.min.js", c.htmlProcessWorkerJSUrl = "../js/editor_htmlProcessWorker.js", c.detectJSUrl = "../js/dext5editor_detector.min.js", c.clipboardJSUrl = "../js/editor_clipboard.min.js", c.imageExifJSUrl = "../js/editor_imageExif.js") : (c.popupCssUrl = "ko-kr" != d.UseLang ? "../css_dev/editor_popup_" + d.UseLang + ".css" : "../css_dev/editor_popup.css", c.dialogJSUrl = "../js_dev/editor_dialog.js", c.fileEncodingJSUrl = "../js_dev/editor_fileopen_encoding.js",
															c.fileEncodingWorkerJSUrl = "../js_dev/editor_fileopen_encodingWorker.js", c.ajaxUploadJSUrl = "../js_dev/editor_upload.js", c.conversionJSUrl = "../js_dev/editor_conversion.js", c.htmlProcessWorkerJSUrl = "../js_dev/editor_htmlProcessWorker.js", c.detectJSUrl = "../js_dev/dext5editor_detector.js", c.clipboardJSUrl = "../js_dev/editor_clipboard.js", c.imageExifJSUrl = "../js_dev/editor_imageExif.js"); if ("" != d.ItemCustomUrl.Item && "" != d.ItemCustomUrl.Url) c.itemCustomUrl.item = d.ItemCustomUrl.Item.split(","), c.itemCustomUrl.url =
																d.ItemCustomUrl.Url.split(","); else if (B = DEXT5.util.xml.getNode(g, "item_custom_url")) if (E = DEXT5.util.xml.count(B, "item"), 0 < E) for (c.itemCustomUrl.item = [], c.itemCustomUrl.url = [], b = 0; b < E; b++)F = DEXT5.util.xml.getNodeValueIdx(B, "item", b), I = DEXT5.util.xml.getNodeIdx(B, "item", b).getAttribute("url"), c.itemCustomUrl.item.push(F), c.itemCustomUrl.url.push(I); 4 < c.post_url.length && "http" == c.post_url.substring(0, 4).toLowerCase() && c.post_url.match(/:\/\/(.[^\/]+)/)[1] != window.location.host && "file:" != location.protocol &&
																	(c.isCrossDomain = !0); "1" == c.plugInUse || "0" == c.plugInUse && -1 < c.runtimes.indexOf("html5") ? (c.imageQuality.quality = parseFloat(c.imageQuality.quality), c.imageQuality.workerCount = DEXT5.util.parseIntOr0(c.imageQuality.workerCount), "" != c.imageQuality.extension && (c.imageQuality.extension = c.imageQuality.extension.replace(/ /g, "").toLowerCase()), "0" != c.imageQuality.overFileSize && (B = DEXT5.util.getUnit(c.imageQuality.overFileSize), B = DEXT5.util.getUnitSize(B), E = parseInt(c.imageQuality.overFileSize, 10), c.imageQuality.overFileSize =
																		E * B)) : (c.imageQuality.quality = 1, c.imageQuality.workerCount = 7, c.imageQuality.extension = "", c.imageQuality.overFileSize = "0"); B = c.developLang; if ("" == c.proxy_url && ("NET" == B.toUpperCase() || "JSP" == B.toUpperCase() || "JAVA" == B.toUpperCase() || "PHP" == B.toUpperCase() || "ASP" == B.toUpperCase())) {
																			var J = c.post_url; "" != c.userFieldValue && (B = c.post_url.split("?"), J = B[0]); var G = J + "?dext=test&h=1", R = function() {
																				var a = "", b = 0 < d.InitXml.length ? b : "dext_editor.xml", a = "ko-kr"; c && "undefined" != typeof c && (a = c.lang); "ko" == a || "ko-kr" ==
																					a ? (a = "Editor\uc758 \uc124\uc815\uac12\uc774 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. \uc544\ub798 URL \uc811\uadfc\uc774 \uc720\ud6a8\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.\n\n" + (J + "\n\n"), a += b + " \ud30c\uc77c\uc758 uploader_setting \uc139\uc158\uc758 <develop_langage>\uc640 <handler_url> \uc124\uc815\uac12\uc744 \ud655\uc778\ud558\uc138\uc694.") : (a = "Editor's setting is not correct. Access the following URL is not valid.\n\n" + (J + "\n\n"), a += 'Please check the settings, <handler_url> and <develop_langage> in "uploader_setting" section in the "' +
																						b + '."'); alert(a)
																			}, H = new XMLHttpRequest; H.open("GET", G); H.setRequestHeader("Content-type", "application/x-www-form-urlencoded"); H.onreadystatechange = function() { if (4 == H.readyState) { if (200 == H.status) { var a = H.responseText; null == a || "" == a ? R() : (0 == a.indexOf("_") && (a = DEXT5.util.makeDecryptReponseMessageEx2(a.substring(1))), a = a.toLowerCase(), -1 < a.indexOf("hi, dext5 editor !!!!") && -1 < a.indexOf("-") && (DEXT5._$0S = DEXT5.util.makeEncryptParamEx2(a.split("-")[1]))) } else 0 != H.status && R(); H = null; H = void 0 } }; try {
																				DEXT5.util.setAddHttpHeader(H,
																					c.addHttpHeader)
																			} catch (T) { DEXT5 && DEXT5.logMode && console && console.log(T) } H.send()
																		} else if ("" != c.proxy_url && c.isCrossDomain) {
																			J = c.post_url; "" != c.userFieldValue && (B = c.post_url.split("?"), J = B[0]); var G = J + "?dext=test&h=1", L = document.createElement("div"), B = DEXT5.util.getDefaultIframeSrc(); L.innerHTML = '<iframe name="initCheckframeEditor" id="initCheckframeEditor" style="display:none;" src="' + B + '" title="DEXT5Upload"></iframe>'; L.style.display = "none"; document.body.appendChild(L); DEXT5.util.postFormData(document,
																				G, "initCheckframeEditor", [["cd", "1"]]); DEXT5.util.addEvent(L.firstChild, "load", function() { L.firstChild.contentWindow.postMessage("check", "*") }, !0); if (window.postMessage) {
																					var O = function(a) {
																						try {
																							if (0 == G.indexOf(a.origin)) {
																								var b = DEXT5.util.trim(a.data); if (null == b || "" == b) {
																									a = ""; var e = 0 < d.InitXml.length ? e : "dext_editor.xml", b = "ko-kr"; c && "undefined" != typeof c && (b = c.lang); "ko" == b || "ko-kr" == b ? (a = "Editor\uc758 \uc124\uc815\uac12\uc774 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. \uc544\ub798 URL \uc811\uadfc\uc774 \uc720\ud6a8\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.\n\n",
																										a += J + "\n\n", a += e + " \ud30c\uc77c\uc758 uploader_setting \uc139\uc158\uc758 <develop_langage>\uc640 <handler_url> \uc124\uc815\uac12\uc744 \ud655\uc778\ud558\uc138\uc694.") : (a = "Editor's setting is not correct. Access the following URL is not valid.\n\n", a += J + "\n\n", a += 'Please check the settings, <handler_url> and <develop_langage> in "uploader_setting" section in the "' + e + '."'); alert(a)
																								} else 0 == b.indexOf("_") && (b = DEXT5.util.makeDecryptReponseMessageEx2(b.substring(1))), b = b.toLowerCase(), -1 < b.indexOf("hi, dext5 editor !!!!") &&
																									-1 < b.indexOf("-") && (DEXT5._$0S = DEXT5.util.makeEncryptParamEx2(b.split("-")[1])); document.body.removeChild(L); DEXT5.util.removeEvent(window, "message", O)
																							}
																						} catch (f) { DEXT5 && DEXT5.logMode && console && console.log(f) }
																					}; DEXT5.util.addEvent(window, "message", O)
																				}
																		} B = c.style.height; 0 == c.style.InitVisible && (B = "1px"); E = document.getElementById(c.holderID); E.style.width = c.style.width; E.style.height = B; DEXT5.browser.iOS || DEXT5.browser.mobile ? "view" == c.mode && "1" == c.view_mode_auto_height ? E.style.overflow = "hidden" : E.style.webkitOverflowScrolling =
																			"touch" : E.style.overflow = "visible"; var x; try { x = document.createElement('<iframe frameborder="0" scrolling="no">') } catch (W) { x = document.createElement("iframe"), x.setAttribute("frameborder", "0"), x.setAttribute("scrolling", "no") } E.appendChild(x); x.setAttribute("id", e.FrameID); x.setAttribute("title", "DEXT5Editor"); x.style.width = "100%"; x.style.height = "100%"; if (DEXT5.browser.iOS || DEXT5.browser.mobile) x.style.overflow = "hidden", x.style.display = "inline-block", DEXT5.browser.iOS && "11" > DEXT5.UserAgent.os.version &&
																				(x.style.height = "101%"); x.width = "100%"; x.height = "100%"; var N = e; DEXT5.util.addEvent(x, "load", function() { var b = DEXT5.util.DEXT5_CheckEditorVisible(a); try { 1 == b && "" != x.src && -1 < x.src.indexOf("editor_dummy.html") ? DEXT5.startUpEditor(a) : 1 == b && ("function" === typeof c.event.frameLoaded ? c.event.frameLoaded(a, c, N) : x.contentWindow.dext_frame_loaded_event(a, c, N), N = c = null) } catch (e) { 0 != b && "" != d.LoadErrorCustomMessage && alert(d.LoadErrorCustomMessage) } }, !1); e = c.editor_url; "1" == c.autoSetDocumentDomain && (e = DEXT5.util.setUrlForDocumentDomain(e,
																					document)); if (1 == DEXT5.util.DEXT5_CheckEditorVisible(a)) "lightview" == c.mode && (e = DEXT5.isRelease ? DEXT5.rootPath + "pages/editor_lightView_release.html?t=" + DEXT5.UrlStamp : DEXT5.rootPath + "pages/editor_lightView.html?t=" + DEXT5.UrlStamp, "1" == c.autoSetDocumentDomain && (e = DEXT5.util.setUrlForDocumentDomain(e, document))), x.src = e; else {
																						if ("1" == d.AutoStartUp) {
																							var S = function() {
																								x.contentWindow.autoStartUp = x.contentWindow.setInterval(function() {
																									try {
																										DEXT5.util.DEXT5_CheckEditorVisible(a) && (x.contentWindow.clearInterval(x.contentWindow.autoStartUp),
																											DEXT5.util.removeEvent(x, "load", S), DEXT5.startUpEditor(a))
																									} catch (b) { x.contentWindow.clearInterval(x.contentWindow.autoStartUp), DEXT5.util.removeEvent(x, "load", S) }
																								}, 1E3)
																							}; DEXT5.util.addEvent(x, "load", S, !1)
																						} x.src = c.dummy_url; x.setAttribute("dext5customsrc", e)
																					} "1" == c.forbiddenWordMode && c.pluginToolExArr.push("forbiddenword")
								}
							}
						}, e = ""; b = e = "100%"; "" != DEXT5.config.Width && (b = DEXT5.config.Width); "" != DEXT5.config.Height && (e = DEXT5.config.Height); e = '<div id="' + c.holderID + '" style="width:' + b + "; height:" + e + "; ";
			e = DEXT5.browser.iOS || DEXT5.browser.mobile ? "view" == c.mode && "1" == c.view_mode_auto_height ? e + " overflow:hidden;" : e + " overflow:hidden; -webkit-overflow-scrolling:touch; " : e + " overflow: visible;"; e += '"></div>'; b = document.getElementById(c.holderID); if ("" != DEXT5.config.EditorHolder) { var l = document.getElementById(DEXT5.config.EditorHolder); l ? b ? b.innerHTML = "" : l.innerHTML = e : document.write(e) } else b ? b.innerHTML = "" : document.write(e); this._config = c; var n = {}; DEXT5.util.objectExtend(!0, n, DEXT5.config); 0 < DEXT5.config.InitServerXml.length ?
				DEXT5.ajax.load(c.config_url, function(a) { k(a, n, h) }) : DEXT5.ajax.loadXml(c.config_url, function(a) { k(a, n, h) })
		}
	}, DEXT_CONFIG = function() {
		"" != DEXT5.config.RootPath && (DEXT5.rootPath = DEXT5.config.RootPath); DEXT5.rootPath = DEXT5.util.setProtocolBaseDomainURL(DEXT5.rootPath); this.dext_path = ""; this.lang = "ko-kr"; this.defaultLineHeight = this.defaultFontFamily = this.defaultFontSize = ""; this.defaultFontMarginBottom = this.defaultFontMarginTop = "0px"; this.tabSequenceMode = this.accessibility = "0"; this.visibility = !0; this.tool_bar =
			this.top_menu = "0"; this.top_status_bar = "1"; this.status_bar = "0"; this.xmlnsname = this.docType = this.encoding = this.gridForm = this.gridSpans = this.gridColorName = this.gridColor = ""; this.toSavePathURL = "dext5editordata"; this.toSaveFilePathURL = ""; this.saveFolderNameRule = "YYYY/MM"; this.saveFolderNameRuleAllowCustomFolder = "0"; this.saveFileFolderNameRule = ""; this.saveFileFolderNameRuleAllowCustomFolder = "0"; this.serverDomain = ""; this.saveFileNameRule = "GUID"; this.showDialogPosition = "1"; this.image_convert_height = this.image_convert_width =
				this.image_convert_format = ""; this.image_auto_fit = "0"; this.productKey = ""; this.imageEditorUse = this.plugInUse = "0"; this.firstLoadMessage = this.firstLoadType = this.defaultMessage = ""; this.fileFieldID = "Filedata"; this.printLandscape = this.printPreview = "0"; this.usePrintApp = this.basePrintFooter = this.basePrintHeader = this.printMarginltrb = this.printFooter = this.printHeader = ""; this.sourceViewtype = this.useFormPrint = "0"; this.sourceViewtype_unformatted = ""; this.useSelectionMark = this.sourceViewtypeEmptyTagMode = "0"; this.nextTabElementId =
					this.webFontCssAlwaysSet = this.webFontCssUrl = this.userCssUrl = ""; this.xhtml_value = this.empty_tag_remove = this.paste_image_base64_remove = this.paste_image_base64 = "0"; this.system_title = "DEXT5"; this.view_mode_auto_width = this.view_mode_auto_height = "0"; this.changeMultiValueMode = "doctype"; this.tableAutoAdjust = "1"; this.trustSites = ""; this.contextMenuDisable = "0"; this.enterTag = ""; this.userFieldID = "Userdata"; this.userFieldValue = ""; this.mimeCharset = "utf-8"; this.mimeConentEncodingType = "3"; this.mimeLocalOnly = this.mimeFileTypeFilter =
						"1"; this.mimeRemoveHeader = "0"; this.imgDefaultMarginBottom = this.imgDefaultMarginTop = this.imgDefaultMarginRight = this.imgDefaultMarginLeft = this.imgDefaultHeight = this.imgDefaultWidth = this.mimeBaseURL = this.userImageDlgStyle = this.userOpenDlgInitDir = this.userOpenDlgType = this.userOpenDlgTitle = ""; this.officeLineFix = "1"; this.useLog = "0"; this.EditorTabDisable = ""; this.unitDelimiter = "\f"; this.unitAttributeDelimiter = "\x0B"; this.style = {
							skinName: "blue", width: "100%", height: "600px", InitVisible: !0, isFontSizeUse: !0, defaultMinHeight: "150px",
							iconName: "icon", customCssUrl: "", htmlprocessCustomText: "<b>Processing</b>", dialogBorder: "", dialogBoxShadow: "", dialogBorderRadius: ""
						}; this.webPath = {
							image: DEXT5.rootPath + "images/editor/", page: DEXT5.rootPath + "pages/", js: DEXT5.rootPath + "js/", js_dev: DEXT5.rootPath + "js_dev/", css: DEXT5.rootPath + "css/", css_dev: DEXT5.rootPath + "css_dev/", include: DEXT5.rootPath + "include/", config: DEXT5.rootPath + "config/", help: DEXT5.rootPath + "help/", pluginInstallGuide: DEXT5.rootPath + "plugin/installguide/installguide.html", plugin: DEXT5.rootPath +
								"plugin/"
						}; this.holderID = ""; this.config_url = DEXT5.rootPath + "config/dext_editor.xml"; this.emoticon_url = DEXT5.rootPath + "config/dext_emoticon.xml"; this.forms_url = DEXT5.rootPath + "config/dext_formlist.xml"; this.post_url = DEXT5.rootPath + "handler/upload_handler.ashx"; this.proxy_url = this.post_url_save_for_notes = ""; this.plugin_root = DEXT5.rootPath + "plugin/"; this.pv = DEXT5._$0C; DEXT5.isRelease ? this.editor_url = DEXT5.browser.LocalStorageSupported && DEXT5.useFileCache ? DEXT5.rootPath + "pages/editor_release_cache.html?t=" +
							DEXT5.UrlStamp : DEXT5.rootPath + "pages/editor_release.html?t=" + DEXT5.UrlStamp : (DEXT5.useFileCache = !1, this.editor_url = DEXT5.rootPath + "pages/editor.html?t=" + DEXT5.UrlStamp); this.dummy_url = DEXT5.rootPath + "pages/editor_dummy.html?t=" + DEXT5.UrlStamp; this.pages = {
								find: "editor_find.html?t=" + DEXT5.UrlStamp, replace: "editor_replace.html?t=" + DEXT5.UrlStamp, image: "editor_image.html?t=" + DEXT5.UrlStamp, horizontal_line: "editor_horizontal_line.html?t=" + DEXT5.UrlStamp, bg_image: "dext_bg_image.html?t=" + DEXT5.UrlStamp,
								doc_bg_img: "editor_doc_bg_img.html?t=" + DEXT5.UrlStamp, flash: "editor_flash.html?t=" + DEXT5.UrlStamp, media: "editor_media.html?t=" + DEXT5.UrlStamp, table: "editor_table.html?t=" + DEXT5.UrlStamp, table_property: "editor_table_property.html?t=" + DEXT5.UrlStamp, cell_property: "editor_cell_property.html?t=" + DEXT5.UrlStamp, insert_row_column: "editor_insert_row_column.html?t=" + DEXT5.UrlStamp, row_property: "editor_row_property.html?t=" + DEXT5.UrlStamp, column_property: "editor_column_property.html?t=" + DEXT5.UrlStamp, emoticon: "editor_emoticon.html?t=" +
									DEXT5.UrlStamp, bookmark: "editor_bookmark.html?t=" + DEXT5.UrlStamp, iframe: "editor_iframe.html?t=" + DEXT5.UrlStamp, hyperlink: "editor_hyperlink.html?t=" + DEXT5.UrlStamp, split_cell: "editor_split_cell.html?t=" + DEXT5.UrlStamp, about: "editor_about.html?t=" + DEXT5.UrlStamp, symbol: "editor_symbol.html?t=" + DEXT5.UrlStamp, setting: "editor_setting.html?t=" + DEXT5.UrlStamp, template: "editor_template.html?t=" + DEXT5.UrlStamp, tool_replace: "editor_tool_replace.html?t=" + DEXT5.UrlStamp, insert_link_media: "editor_insert_link_media.html?t=" +
										DEXT5.UrlStamp, m_insert_link_media: "editor_m_insert_link_media.html?t=" + DEXT5.UrlStamp, m_image: "editor_m_image.html?t=" + DEXT5.UrlStamp, m_emoticon: "editor_m_emoticon.html?t=" + DEXT5.UrlStamp, cell_border: "editor_cell_border.html?t=" + DEXT5.UrlStamp, paste_dialog: "editor_paste.html?t=" + DEXT5.UrlStamp, shortcut: "editor_shortcut.html?t=" + DEXT5.UrlStamp, layout: "editor_layout.html?t=" + DEXT5.UrlStamp, insert_file: "editor_insert_file.html?t=" + DEXT5.UrlStamp, file_open: "editor_file_open.html?t=" + DEXT5.UrlStamp, blockquote: "editor_blockquote.html?t=" +
											DEXT5.UrlStamp, input_text: "editor_input_text.html?t=" + DEXT5.UrlStamp, input_radio: "editor_input_radio.html?t=" + DEXT5.UrlStamp, input_checkbox: "editor_input_checkbox.html?t=" + DEXT5.UrlStamp, input_button: "editor_input_button.html?t=" + DEXT5.UrlStamp, input_hidden: "editor_input_hidden.html?t=" + DEXT5.UrlStamp, input_textarea: "editor_input_textarea.html?t=" + DEXT5.UrlStamp, input_select: "editor_input_select.html?t=" + DEXT5.UrlStamp, input_image: "editor_input_image.html?t=" + DEXT5.UrlStamp, accessibility_validation: "editor_accessibility_validation.html?t=" +
												DEXT5.UrlStamp, personal_data: "editor_personal_data.html?t=" + DEXT5.UrlStamp, forbidden_word: "editor_forbidden_word.html?t=" + DEXT5.UrlStamp, table_lock_property: "editor_table_lock_property.html?t=" + DEXT5.UrlStamp, pastetoimage: "editor_pastetoimage.html?t=" + DEXT5.UrlStamp, insert_datetime: "editor_insert_datetime.html?t=" + DEXT5.UrlStamp, conversion: "editor_conversion.html?t=" + DEXT5.UrlStamp, video: "editor_video.html?t=" + DEXT5.UrlStamp, m_video: "editor_m_video.html?t=" + DEXT5.UrlStamp, spell_check: "editor_spell_check.html?t=" +
													DEXT5.UrlStamp, calculator: "editor_calculator.html?t=" + DEXT5.UrlStamp, load_auto_save: "editor_load_auto_save.html?t=" + DEXT5.UrlStamp, placeholder: "editor_placeholder.html?t=" + DEXT5.UrlStamp, paragraph_attribute: "editor_paragraph_attribute.html?t=" + DEXT5.UrlStamp, paste_table: "editor_paste_table.html?t=" + DEXT5.UrlStamp
							}; this.xss_remove_events = this.xss_remove_tags = this.replace_tagname = ""; this.xss_use = "0"; this.xss_allow_events_attribute = "1"; this.xss_check_attribute = ["src", "background", "href", "url"]; this.xss_allow_url =
								[]; this.editor_id = ""; this.mode = "edit"; this.mimeUse = "0"; this.zIndex = 99999; this.ruler = { use: "0", view: "0", rulerInitPosition: [], viewPointer: "1", viewGuideLine: "1", viewRuler: "1", guideLineStyle: "dotted", mode: "1", guideLineColor: "rgb(255, 0, 0)", moveGuideLineStyle: "dotted", moveGuideLineColor: "#666666", useInoutdent: "1", moveGap: "1", useResizeEvent: "1", defaultView: "1", autoFitMode: "0", fixEditorWidth: "1", useVertical: "0", useMouseGuide: "0", usePointerValue: "0" }; this.horizontalLine = {
									use: "0", url: [], height: "768", style: "",
									repeat: "1"
								}; this.editorborder = "1"; this.imageBaseUrl = this.custom_code = ""; this.allowMediaFileType = ["wmv", "asf"]; this.allowImageFileType = ["jpg", "jpeg", "png", "gif", "bmp"]; this.allowFlashFileType = ["swf"]; this.allowInsertFileType = "txt doc docx xls xlsx ppt pptx hwp pdf".split(" "); this.allowVideoFileType = ["mp4", "ogv", "webm"]; this.maxImageFileSize = this.maxMediaFileSize = 0; this.maxImageBase64fileCount = -1; this.maxVideoFileSize = this.maxInsertFileSize = this.maxFlashFileSize = 0; this.attachFileImage = "1"; this.tableClassList =
									[]; this.tableLineStyleList = []; this.tableDefaultTdHeight = this.tableDefaultClass = this.tableDefaultHeight = this.tableDefaultWidth = ""; this.tableLineStyleValue = {}; this.tableLineStyleValue._solid = "border-top: 1px solid #5d5d5d;"; this.tableLineStyleValue._dotted = "border-top: 1px dotted #5d5d5d;"; this.tableLineStyleValue._dashed = "border-top: 1px dashed #5d5d5d;"; this.tableLineStyleValue._double = "border-top: 3px double #5d5d5d;"; this.tableLineStyleValue._groove = "border: 3px groove #e1e1e1;"; this.tableLineStyleValue._ridge =
										"border: 3px ridge #e1e1e1;"; this.tableLineStyleValue._inset = "border: 3px inset #e1e1e1;"; this.tableLineStyleValue._outset = "border: 3px outset #e1e1e1;"; this.tableLineStyleValue._none = "border: 1px none #5d5d5d;"; this.tableLineStyleValue._hidden = "border: 1px none #5d5d5d;"; this.tableDefaultInoutdent = 20; this.tableNoSelectionClass = this.tableNoActionClass = this.tableNoResizeClass = this.tableInitInoutdent = ""; this.tableColMaxCount = this.tableRowMaxCount = "13"; this.tableUseHeight = "1"; this.allowInoutdentText =
											"0"; this.defaultBorderColor = "#000000"; this.useTableBorderAttribute = "1"; this.imageLineStyleList = []; this.imageLineStyleValue = {}; this.imageLineStyleValue._solid = "border-top: 1px solid #5d5d5d;"; this.imageLineStyleValue._dotted = "border-top: 1px dotted #5d5d5d;"; this.imageLineStyleValue._dashed = "border-top: 1px dashed #5d5d5d;"; this.imageLineStyleValue._double = "border-top: 3px double #5d5d5d;"; this.imageLineStyleValue._groove = "border: 3px groove #e1e1e1;"; this.imageLineStyleValue._ridge = "border: 3px ridge #e1e1e1;";
		this.imageLineStyleValue._inset = "border: 3px inset #e1e1e1;"; this.imageLineStyleValue._outset = "border: 3px outset #e1e1e1;"; this.imageLineStyleValue._none = "border: 1px none #5d5d5d;"; this.imageLineStyleValue._hidden = "border: 1px none #5d5d5d;"; this.showLineForBorderNone = this.tableNearCellBorderStyle = this.tableAutoAdjustInSetHtml = this.tableAutoAdjustInPaste = "0"; this.focusInitObjId = this.showLineForBorderNoneSkipAttribute = this.showLineForBorderNoneSkipClass = ""; this.autoMoveInitFocus = { use: "0", targetWindow: null };
		this.tabIndexObjValue = this.tabIndexObjId = this.focusInitEditorObjId = ""; this.setDefaultStyle = { value: "0", body_id: "dext_body", dext_set_style: "1", line_height_mode: "1", removeBodyStyleInSet: "0" }; this.setDefaultUserStyle = []; this.popupCssUrl = "../css/editor_popup.css?t=" + DEXT5.UrlStamp; this.symbolUrl = 1 == DEXT5.isRelease ? "../js/editor_symbol.js?t=" + DEXT5.UrlStamp : "../js_dev/editor_symbol.js?t=" + DEXT5.UrlStamp; this.dialogJSUrl = "../js/editor_dialog.js?t=" + DEXT5.UrlStamp; this.fileEncodingJSUrl = "../js/editor_fileopen_encoding.min.js?t=" +
			DEXT5.UrlStamp; this.fileEncodingWorkerJSUrl = "../js/editor_fileopen_encodingWorker.min.js?t=" + DEXT5.UrlStamp; this.ajaxUploadJSUrl = "../js/editor_upload.min.js?t=" + DEXT5.UrlStamp; this.htmlProcessWorkerJSUrl = "../js/editor_htmlProcessWorker.js?t=" + DEXT5.UrlStamp; this.customEventCmd = { image: { ondbclick: "" }, hyperLink: { ondbclick: "" } }; this.ieCompatible = ""; this.autoUrlDetect = "1"; this.scrollOverflow = this.pasteRemoveEmptyTag = "0"; this.autoBodyFit = this.disableTabletap = this.defaultImemode = ""; this.useNoncreationAreaBackground =
				"0"; this.disableKeydown = ""; this.dragAndDropAllow = "0"; this.limitPasteHtmlLength = { value: "0", length: 13E5 }; this.pasteToImage = "1"; this.pasteToImagePopupMode = "0"; this.pasteToImageType = ["png", "jpg"]; this.wrapPtagToGetApi = this.wrapPtagToSource = "0"; this.wrapPtagSkipTag = ""; this.eventForPasteImage = this.notRepairInViewMode = this.viewModeAllowCopy = this.viewModeBrowserMenu = this.removeSpaceInTagname = "0"; this.useHtmlCorrection = this.removeColgroup = "1"; this.useHtmlProcessByWorkerSetApi = this.useHtmlProcessByWorker = this.replaceSpace =
					this.removeIncorrectAttribute = "0"; this.skipTagInParser = this.orderListDefaultClass = this.unOrderListDefaultClass = ""; this.htmlCorrectionLimitLength = 45E4; this.runtimes = "html5"; this.setAutoSave = { mode: "2", interval: 5, maxCount: "5", unique_key: "", use_encrypt: "0", useManuallySave: "0", useManuallySaveShortcutKey: "0", saveAndStartInterval: "0", popupWidth: "480", popupHeight: "345" }; this.insertCarriageReturn = "0"; this.returnEvent = { mouse_event: "0", key_event: "0", command_event: "0", input_event: "0", focus_event: "0", drag_event: "0" };
		this.ie_BugFixed_Hyfont = this.ie11_BugFixed_typing_bug_in_table = this.ie11_BugFixed_ReplaceAlignMargin = this.ie11_BugFixed_DeleteTableAlign = this.ie11_BugFixed_ReplaceBr = this.ie11_BugFixed_JASO = this.useCorrectInOutdent = "0"; this.ie_BugFixed_Hyfont_Replace_Font = ""; this.ie_BugFixed_Apply_All_Browser = "0"; this.isLoadedEditor = !1; this.setValueObjId = ""; this.replaceEmptyTagWithSpace = "1"; this.LoadedEvent = ""; this.event = {
			loadedEvent: null, onError: null, frameLoaded: null, setComplete: null, resized: null, editorLoaded: null,
			beforePaste: null, customAction: null, afterChangeMode: null, languageDefinition: null, afterPopupShow: null, mouse: null, command: null, key: null, input: null, pasteImage: null, managerImg: null, managerInput: null, managerSelect: null, managerTextArea: null, contentSizeChange: null, beforeInsertUrl: null, beforeFullScreen: null, fullScreen: null, setInsertComplete: null, setForbiddenWordComplete: null, drag: null, focus: null, dialogLoaded: null, beforeInsertHyperlink: null, insertEmoticon: null, applyFontStyle: null
		}; this.frameFullScreen = "0";
		this.currFontFamily = ""; this.allowImgSize = "1"; this.hyperlinkTargetList = []; this.hyperlinkTargetBasicList = []; this.hyperlinkCategoryList = []; this.hyperlinkProtocolList = []; this.validateUrlLink = "1"; this.developLang = "NET"; this.formMode = "0"; this.openDocument = { beforeOpenEvent: "0", useHwp: "1", word: { maxSize: 0, maxPage: 0 }, excel: { maxSize: 0, maxSheet: 0 }, powerpoint: { maxSize: 0, maxSlide: 0 }, useHtml5FileOpen: "0" }; this.removeLastBrTag = "0"; this.editorBodyEditableEx = this.editorBodyEditable = !0; this.editorBodyEditableMode = "1";
		this.inoutdentDefaultSize = 40; this.accessibilityValidationItems = ""; this.statusBarItem = []; this.statusBarTitle = []; this.statusBarInitMode = "design"; this.dragResize = "0"; this.dragResizeApplyBrowser = []; this.dragResizeMovable = "0"; this.dragResizeApplyDivClass = ""; this.replaceOutsideImage = { use: "0", exceptDomain: [], targetDomain: [] }; this.topMenuItem = []; this.removeItem = []; this.removeContextItem = []; this.eventList = []; this.userJsUrl = ""; this.fontFamilyList = []; this.displayFontFamilyList = []; this.fontSizeList = []; this.lineHeightList =
			"100% 120% 150% 180% 200% 250% 300%".split(" "); this.zoomList = null; this.formattingList = []; this.letterSpacingList = []; this.adminTableLock = { defaultShowLockIconUserMode: "0", defaultLockName: "dext_lock", checkLockName: ["dext_lock"], defaultTableLockMode: "2" }; this.userTableLock = { use: "0", lockName: ["dext_lock"], defaultTableLockMode: "2", defaultShowLockIcon: "1", tableLockMode: "1", allowChangeMode: "0" }; this.removeComment = "1"; this.tool_bar_grouping = "0"; this.setDefaultValueInEmptyTag = "p div span h1 h2 h3 h4 h5".split(" ");
		this.userHelpUrl = this.metaHttpEquiv = this.directEditHtmlUrl = this.userCssAlwaysSet = this.photoEditorId = ""; this.useGZip = "0"; this.topStatusBarLoading = this.statusBarLoading = "1"; this.hasContainer = "0"; this.security = { encryptParam: "0", fileExtensionDetector: "0" }; this.tabSpace = "&nbsp;&nbsp;&nbsp;&nbsp;"; this.document = { docTitle: "" }; this.addHtmlToSetValue = { html: "", preOrSub: 0 }; this.imgUploadContenttype = "0"; this.imageCustomPropertyDelimiter = "|"; this.useHybrid = "0"; this.lineHeightMode = this.useGetHtmlToLowerCase = this.hybridWindowMode =
			"1"; this.letterSpacingIncDecGap = this.lineHeightIncDecGap = this.fontSizeIncDecGap = ""; this.applyStyleEmptyTag = "0"; this.undoCount = 20; this.allowDeleteCount = "0"; this.autoDestroy = { use: "0", moveCursor: "0" }; this.initFocusForSetAPI = this.initFocus = !0; this.replaceEmptySpanTagInSetapiOnlyTable = this.replaceEmptySpanTagInSetapi = this.emptyTagRemoveInSetapi = this.allowUnableToDeleteMsg = "0"; this.pluginKeepVersion = ""; this.applyFormatExceptStyle = []; this.enforceDoctype = "2"; this.pluginInstallGuideType = this.usePluginInstallGuide =
				this.pluginInstallUrl = this.pluginInstallType = this.userColorPicker = ""; this.pluginInstallGuideZIndex = 999999; this.pluginInstallFileName = "dext5editor.exe"; this.imageAutoConvert = "0"; this.uploadMethod = "upload"; this.uploadImageFileObject = this.uploadUseHTML5 = "0"; this.removeMsoClass = "1"; this.tableTemplateListUrl = ""; this.useTableBackgroundImage = this.useReplaceImage = this.useBasicTemplate = "1"; this.buttonText001 = ""; this.interworkingModuleThird = this.interworkingModuleSecond = this.interworkingModuleFirst = null; this.useServerLicense =
					!1; this.removeEmptyTag = "1"; this.removeEmptyTagSetValue = "0"; this.removeEmptyTagReplaceDefaultParagraphValue = this.removeEmptyTagInsertNbspForLineBreak = "1"; this.setTextPasteMode = this.useUndoLightMode = "0"; this.fullScreenBottom = this.fullScreenRight = this.fullScreenLeft = this.fullScreenTop = 0; this.adjustCursorInTable = "0"; this.focusInitObjIdEx = ""; this.viewImgBase64Source = "0"; this.removeStyle = { use: "0", tag: "", style: "" }; this.personalData = "email,phone,RRN"; this.forbiddenWord = ""; this.forbiddenWordMode = "0"; this.forbiddenWordWidth =
						800; this.forbiddenWordHeight = 560; this.useRecentlyFont = this.useLocalFont = "0"; this.wordCount = { use: "0", limit: "0", limitchar: "0", limitbyte: "0", limitline: "0", countwhitespace: "0", limitmessage: "0", limitcount: "0", limitunit: "char" }; this.saveHtmlName = "DEXT5"; this.useMiniImageEditor = "0"; this.miniPhotoEditor = { width: "800px", height: "600px", backgroundColor: "black", backgroundOpacity: "0.5", imgTagRemoveAttribute: [] }; this.useLetterSpacingIncDec = this.useLineHeightIncDec = this.useFontSizeIncDec = this.useLineHeightKeyin =
							this.useFontSizeKeyin = this.useFontFamilyKeyin = "0"; this.autoList = { use: "0" }; this.figure = { use: "0", figurestyle: "margin: 0px; text-align: center; display: inline-block;", figcaptionstyle: "", defaultcaption: "<br>" }; this.auditLogEnable = "0"; this.autoPluginUpdate = "1"; this.browserSpellCheck = "0"; this.useMouseTableInoutdent = !0; this.limitTableInoutdent = "0"; this.disableInsertImage = ""; this.olUlTagMode = this.usePersonalSetting = "1"; this.insertMultiImage = "0"; this.allowOpenFileType = ""; this.removeHwpDummyTag = "0"; this.excelImageFix =
								this.afterPasteCaretPosition = "1"; this.cellSelectionMode = this.colorPickerInputKind = "0"; this.tableInsertDragSize = "10,10"; this.showRealFont = "0"; this.showRealFontMode = "1"; this.imageContinueInsertMaintainValue = this.adjustEmptySpan = "0"; this.useTablePasteDialog = "1"; this.saveLineBreakToLocalStorage = this.wordWrapType = this.wordBreakType = this.useLineBreak = "0"; this.splitCellStyle = "1"; this.keepOriginalHtml = "0"; this.toolBarAdmin = []; this.toolBar1 = []; this.toolBar2 = []; this.pluginToolExArr = []; this.useAutoToolbar = "0";
		this.autoToolbar = {
			"default": ["font_size", "bold", "underline", "italic", "strike_through"], selectedSingleCell: "font_size bold underline italic strike_through separator split_cell cell_bg_color detail_cell_bg_color".split(" "), selectedMultiCell: "font_size bold underline italic strike_through separator split_cell merge_cell cell_bg_color detail_cell_bg_color table_same_width table_same_height table_same_widthheight".split(" "), focusInCell: "font_size bold underline italic strike_through separator split_cell cell_bg_color detail_cell_bg_color split_cell table_row_clone insert_row_up insert_row_down delete_row delete_column table_delete insert_column_left insert_column_right table_select_all table_select_cell table_select_row table_select_column cell_property table_property".split(" "),
			selectArea: ["font_size", "bold", "underline", "italic", "strike_through"], focusImage: ["image_align_default", "image_align_left", "image_align_right", "separator", "image_property"]
		}; this.clipImageHex = []; this.useEnterpriseMode = "0"; this.defaultBodySpace = { use: "1", mode: "1", value: "10px", configValue: { top: "10px", right: "10px", bottom: "10px", left: "10px" } }; this.pluginTempPath = ""; this.olListStyleTypeSequence = {
			decimal: "upper-alpha", "decimal-leading-zero": "upper-alpha", "upper-alpha": "lower-alpha", "lower-alpha": "upper-roman",
			"upper-roman": "lower-roman", "lower-roman": "lower-greek", "lower-greek": "decimal"
		}; this.ulListStyleTypeSequence = { disc: "circle", circle: "square", square: "disc" }; this.pasteRemoveSpanAbsolute = ""; this.dragMove = "0"; this.pasteWhenTableIsLast = "1"; this.allowLinkMediaCaption = ""; this.removeTdStyleInPastePpt = this.removeImageInPasteExcel = "0"; this.helpStartMainPage = "1"; this.addHttpHeader = {}; this.toolBarEnableDisableMode = "2"; this.removeTempFolderDataDay = "7"; this.checkApplyWordBreakForTable = this.forceSaveAsServer = "0";
		this.highlight = { use: "0", color: "" }; this.popupBackgroundHolderId = ""; this.adjustTextIndentInPaste = this.adjustCurrentColorInSetApi = this.autoGrowMode = this.replaceLineBreak = "0"; this.undoMode = "2"; this.removeDummyFontTagInPaste = "0"; this.setDefaultTagInEmptyCell = "1"; this.insidePaddingTdSetting = "0"; this.removeCarriageReturnInTag = "1"; this.clickCtrlHyperlink = "0"; this.symbolCustomCssUrl = ""; this.removeLangAttribute = "1"; this.itemCustomUrl = { item: [], url: [] }; this.adjustCellSizeAfterDeleteCell = "1"; this.moveStyleTagToHead =
			this.adjustCursorInRelative = this.pasteToTextMode = this.imageSizeToServer = this.removeDummyDivInHwpPaste = "0"; this.removeDummyTag = ""; this.undoLimitLength = -1; this.autoFitInHolder = "0"; this.placeholder = { content: "", fontColor: "#999999", fontSize: "", fontFamily: "" }; this.pasteTextLineBreak = this.ignoreDifferentEditorName = "0"; this.replaceMsoStyle = { settingStyle: [], styleValue: { "mso-spacerun": "yes" }, replaceAttributeName: "dext" }; this.isCrossDomain = !1; this.cleanNestedTagOptions = { removeTag: "", allowStyle: [], nestedCount: 50 };
		this.removeFontType = { fontFamily: ["\ub9d1\uc740 \uace0\ub515 Semilight"], type: ["monospace"] }; this.setEmoticonObject = ""; this.underlineAndStrikeThroughMode = "2"; this.replaceRgbToHex = ""; this.personalSettingUseLineHeightKeyin = this.personalSettingUseFontSizeKeyin = this.personalSettingUseFontFamilyKeyin = this.tableDefaultCellPadding = "0"; this.fileFilterHtml5 = this.fileFilterPlugin = ""; this.keepImageOriginalSizeAutoCheck = this.dialogWindowScroll = "0"; this.paragraphAttributeType = ["px", "pt", "cm"]; this.uploadFileNameEncoding =
			"0"; this.useFindReplaceShortcut = "1"; this.disableErrorConfirmMessage = this.usePasteToolbarAndContext = "0"; this.deleteTableUsingKey = "1"; this.keepFontFamily = ["wingdings"]; this.dialogBorder = this.dialogBoxShadow = this.dialogBorderRadius = ""; this.autoSetZoom = { use: "0", checkNode: null }; this.resizeBarHolderSync = this.customCssUrlDetailApply = "0"; this.imageQuality = { quality: 1, workerCount: 7, extension: "", allowOrLimit: "1", overFileSize: "0" }; this.mobilePopupMode = "2"; this.replaceMsStyleName = []; this.compatibility = {
				dingBatChar: "0",
				dingBatCharSetApi: "0", dingBatCharPaste: "0", autoResizePastedImage: "1", hwpPasteImageInHtml5: "1", hwpPasteBulletInHtml5: "0", hwpProcessTypeInPlugin: "1", fontTagToSpan: "1"
			}; this.widthFix = { value: "", backgroundColor: "", defaultView: "", border: { styleName: "boxShadow", styleValue: "rgba(0,0,0,0.2) 3px 3px 5px 0px" }, padding: 10 }; this.insertSourceTagInVideo = "0"; this.forceFontFamilyChange = ""; this.forceFontFamilyChangeObject = null; this.autoSetDocumentDomain = this.removeFontSizeApplyHTag = "0"; this.editingAreaBgColor = ""; this.useTableDiagonal =
				this.disableUnnecessaryKeyEvt = "0"; this.useNotificationForDiagonal = this.showDiagonalInIEViewPage = "1"
	}; window.DEXT5 = function() {
		return {
			isRelease: !0, logMode: !1, logLevel: "ALL", logGroupingName: "", isPopUpCssLoaded: !1, version: "DEXT5 Editor", UrlStamp: "", useFileCache: !1, rootPath: function() {
				var a = window.DEXT5_ROOTPATH || ""; if (!a) for (var e = document.getElementsByTagName("script"), c = e.length, b = null, d = 0; d < c; d++)if (b = e[d], b = b.src.match(/(^|.*[\\\/])dext5editor\.js/i)) { a = b[1]; break } -1 == a.indexOf(":/") && (a = 0 === a.indexOf("/") ?
					location.href.match(/^.*?:\/\/[^\/]*/)[0] + a : location.href.match(/^[^\?]*\/(?:)/)[0] + a); a = a.substring(0, a.length - 1); a = a.substring(0, a.lastIndexOf("/")) + "/"; if (!a) throw "DEXT5 Editor installation path could not be automatically detected."; return a
			}(), getUrl: function(a) { -1 == a.indexOf(":/") && 0 !== a.indexOf("/") && (a = this.rootPath + a); this._$0 && "/" != a.charAt(a.length - 1) && !/[&?]ver=/.test(a) && (a += (0 <= a.indexOf("?") ? "&" : "?") + "ver=" + this._$0); return a }, DEXTMULTIPLE: {}, DEXTHOLDER: {}, DEXTMULTIPLEID: [], ShowTextChangeAlert: !0,
			ShowDestroyAlert: !0, CEditorID: "", IsEditorLoadedHashTable: null, InitEditorDataHashTable: null, _CK_: [], rvi: { m1: 7, maj: ["3", "5"], mi2: ["15", "00"], l: ["01"], m2: 77, mi1: ["23", "10", "31"], s1: ".", s2: "" }, crvi: { l: ["72"], maj: 5, mi1: "0", s1: "," }
		}
	}(); DEXT5._CK_[0] = "a"; try {
		"undefined" == typeof dext5SolutionLog && "undefined" != typeof DEXT5EditorLogMode && DEXT5EditorLogMode && (DEXT5.logMode = !0, "undefined" != typeof DEXT5EditorLogModeLevel && (DEXT5.logLevel = DEXT5EditorLogModeLevel.toUpperCase()), document.write('<script src="' + DEXT5.rootPath +
			"js/log4javascript/dext5.log4javascript.min.js?t=" + DEXT5.UrlStamp + '" type="text/javascript">\x3c/script>'))
	} catch (e$$13) { DEXT5 && DEXT5.logMode && console && console.log(e$$13) } DEXT5._CK_[1] = "d"; DEXT5.browser || (DEXT5._CK_[2] = "e", DEXT5.browser = function() {
		var a = navigator.userAgent.toLowerCase(), e = window.opera, e = {
			ie: -1 < a.search("trident") || -1 < a.search("msie") || /(edge)\/((\d+)?[\w\.]+)/i.test(a) ? !0 : !1, opera: !!e && e.version, webkit: -1 < a.indexOf(" applewebkit/"), mac: -1 < a.indexOf("macintosh"), quirks: (-1 < a.search("trident") ||
				-1 < a.search("msie") || /(edge)\/((\d+)?[\w\.]+)/i.test(a)) && "BackCompat" == document.compatMode, mobile: -1 < a.indexOf("mobile") || -1 < a.indexOf("android") || -1 < a.indexOf("iphone"), iOS: /(ipad|iphone|ipod)/.test(a), isCustomDomain: function() { if (!this.ie) return !1; var a = document.domain, b = window.location.hostname; return a != b && a != "[" + b + "]" }, isHttps: "https:" == location.protocol, G_AP12: 9, G_AP24: "i"
		}; DEXT5._CK_[3] = "b"; e.gecko = "Gecko" == navigator.product && !e.webkit && !e.opera; e.ie && (e.gecko = !1); e.webkit && (-1 < a.indexOf("chrome") ?
			(e.chrome = !0, -1 < a.indexOf("opr") && (e.opera = !0, e.chrome = !1)) : e.safari = !0); DEXT5._CK_[4] = "c"; var c; e.ieVersion = 0; e.ie && (e.quirks || !document.documentMode ? -1 < a.indexOf("msie") ? c = parseFloat(a.match(/msie (\d+)/)[1]) : -1 < a.indexOf("trident") ? c = parseFloat(a.match(/rv:([\d\.]+)/)[1]) : /(edge)\/((\d+)?[\w\.]+)/i.test(a) ? (c = 12, e.chrome = !1) : c = 7 : c = document.documentMode, e.ieVersion = c, e.ie12 = 12 == c, e.ie11 = 11 == c, e.ie10 = 10 == c, e.ie9 = 9 == c, e.ie8 = 8 == c, e.ie7 = 7 == c, e.ie6 = 7 > c || e.quirks, -1 < a.indexOf("trident") ? e.trident = parseFloat(a.match(/ trident\/(\d+)/)[1]) :
				e.trident = ""); DEXT5._CK_[5] = "f"; e.gecko && (c = a.match(/rv:([\d\.]+)/)) && (c = c[1].split("."), c = 1E4 * c[0] + 100 * (c[1] || 0) + 1 * (c[2] || 0)); e.webkit && (c = parseFloat(a.match(/ applewebkit\/(\d+)/)[1])); e.HTML5Supported = "File" in window && "FileReader" in window && "Blob" in window; e.LocalStorageSupported = !1; try { window.localStorage && (window.localStorage.dext5_localstorage_test = "test", "test" == window.localStorage.dext5_localstorage_test && (e.LocalStorageSupported = !0, window.localStorage.removeItem("dext5_localstorage_test"))) } catch (b) {
					DEXT5 &&
					DEXT5.logMode && console && console.log(b)
				} DEXT5._CK_[6] = "i"; a = null; try { a = new XMLHttpRequest, e.ajaxOnProgressSupported = !!(a && "upload" in a && "onprogress" in a.upload) } catch (d) { e.ajaxOnProgressSupported = !1 } a = null; a = document.createElement("canvas"); a.getContext && a.getContext("2d") ? e.canvasSupported = !0 : e.canvasSupported = !1; a = null; a = void 0; e.ES6Supported = !0; try { new Function("(a = 0) => a") } catch (g) { e.ES6Supported = !1 } e.WorkerSupported = "Worker" in window; return e
	}()); DEXT5.UserAgent || (DEXT5.UserAgent = function() {
		var a =
			window && window.navigator && window.navigator.userAgent ? window.navigator.userAgent : "", e = { extend: function(a, b) { for (var c in b) -1 !== "browser cpu device engine os".indexOf(c) && 0 === b[c].length % 2 && (a[c] = b[c].concat(a[c])); return a }, has: function(a, b) { return "string" === typeof a ? -1 !== b.toLowerCase().indexOf(a.toLowerCase()) : !1 }, lowerize: function(a) { return a.toLowerCase() }, major: function(a) { return "string" === typeof a ? a.split(".")[0] : void 0 } }, c = function() {
				for (var b, c = 0, d, e, g, t, q, r, f = arguments; c < f.length && !q;) {
					var u =
						f[c], v = f[c + 1]; if ("undefined" === typeof b) for (g in b = {}, v) t = v[g], "object" === typeof t ? b[t[0]] = void 0 : b[t] = void 0; for (d = e = 0; d < u.length && !q;)if (q = u[d++].exec(a)) for (g = 0; g < v.length; g++)r = q[++e], t = v[g], "object" === typeof t && 0 < t.length ? 2 == t.length ? b[t[0]] = "function" == typeof t[1] ? t[1].call(this, r) : t[1] : 3 == t.length ? b[t[0]] = "function" !== typeof t[1] || t[1].exec && t[1].test ? r ? r.replace(t[1], t[2]) : void 0 : r ? t[1].call(this, r, t[2]) : void 0 : 4 == t.length && (b[t[0]] = r ? t[3].call(this, r.replace(t[1], t[2])) : void 0) : b[t] = r ? r :
							void 0; c += 2
				} return b
			}, b = function(a, b) { for (var c in b) if ("object" === typeof b[c] && 0 < b[c].length) for (var d = 0; d < b[c].length; d++) { if (e.has(b[c][d], a)) return "?" === c ? void 0 : c } else if (e.has(b[c], a)) return "?" === c ? void 0 : c; return a }, d = { ME: "4.90", "NT 3.11": "NT3.51", "NT 4.0": "NT4.0", 2E3: "NT 5.0", XP: ["NT 5.1", "NT 5.2"], Vista: "NT 6.0", 7: "NT 6.1", 8: "NT 6.2", "8.1": "NT 6.3", 10: ["NT 6.4", "NT 10.0"], RT: "ARM" }, g = [[/\((ipad|playbook);[\w\s\);-]+(rim|apple)/i], ["model", "vendor", ["type", "tablet"]], [/applecoremedia\/[\w\.]+ \((ipad)/],
			["model", ["vendor", "Apple"], ["type", "tablet"]], [/(apple\s{0,1}tv)/i], [["model", "Apple TV"], ["vendor", "Apple"]], [/(archos)\s(gamepad2?)/i, /(hp).+(touchpad)/i, /(kindle)\/([\w\.]+)/i, /\s(nook)[\w\s]+build\/(\w+)/i, /(dell)\s(strea[kpr\s\d]*[\dko])/i], ["vendor", "model", ["type", "tablet"]], [/(kf[A-z]+)\sbuild\/[\w\.]+.*silk\//i], ["model", ["vendor", "Amazon"], ["type", "tablet"]], [/(sd|kf)[0349hijorstuw]+\sbuild\/[\w\.]+.*silk\//i], [["model", b, { "Fire Phone": ["SD", "KF"] }], ["vendor", "Amazon"], ["type", "mobile"]],
			[/\((ip[honed|\s\w*]+);.+(apple)/i], ["model", "vendor", ["type", "mobile"]], [/\((ip[honed|\s\w*]+);/i], ["model", ["vendor", "Apple"], ["type", "mobile"]], [/(blackberry)[\s-]?(\w+)/i, /(blackberry|benq|palm(?=\-)|sonyericsson|acer|asus|dell|huawei|meizu|motorola|polytron)[\s_-]?([\w-]+)*/i, /(hp)\s([\w\s]+\w)/i, /(asus)-?(\w+)/i], ["vendor", "model", ["type", "mobile"]], [/\(bb10;\s(\w+)/i], ["model", ["vendor", "BlackBerry"], ["type", "mobile"]], [/android.+(transfo[prime\s]{4,10}\s\w+|eeepc|slider\s\w+|nexus 7)/i], ["model",
				["vendor", "Asus"], ["type", "tablet"]], [/(sony)\s(tablet\s[ps])\sbuild\//i, /(sony)?(?:sgp.+)\sbuild\//i], [["vendor", "Sony"], ["model", "Xperia Tablet"], ["type", "tablet"]], [/(?:sony)?(?:(?:(?:c|d)\d{4})|(?:so[-l].+))\sbuild\//i], [["vendor", "Sony"], ["model", "Xperia Phone"], ["type", "mobile"]], [/\s(ouya)\s/i, /(nintendo)\s([wids3u]+)/i], ["vendor", "model", ["type", "console"]], [/android.+;\s(shield)\sbuild/i], ["model", ["vendor", "Nvidia"], ["type", "console"]], [/(playstation\s[3portablevi]+)/i], ["model", ["vendor",
					"Sony"], ["type", "console"]], [/(sprint\s(\w+))/i], [["vendor", b, { HTC: "APA", Sprint: "Sprint" }], ["model", b, { "Evo Shift 4G": "7373KT" }], ["type", "mobile"]], [/(lenovo)\s?(S(?:5000|6000)+(?:[-][\w+]))/i], ["vendor", "model", ["type", "tablet"]], [/(htc)[;_\s-]+([\w\s]+(?=\))|\w+)*/i, /(zte)-(\w+)*/i, /(alcatel|geeksphone|huawei|lenovo|nexian|panasonic|(?=;\s)sony)[_\s-]?([\w-]+)*/i], ["vendor", ["model", /_/g, " "], ["type", "mobile"]], [/(nexus\s9)/i], ["model", ["vendor", "HTC"], ["type", "tablet"]], [/[\s\(;](xbox(?:\sone)?)[\s\);]/i],
			["model", ["vendor", "Microsoft"], ["type", "console"]], [/(kin\.[onetw]{3})/i], [["model", /\./g, " "], ["vendor", "Microsoft"], ["type", "mobile"]], [/\s(milestone|droid(?:[2-4x]|\s(?:bionic|x2|pro|razr))?(:?\s4g)?)[\w\s]+build\//i, /mot[\s-]?(\w+)*/i, /(XT\d{3,4}) build\//i], ["model", ["vendor", "Motorola"], ["type", "mobile"]], [/android.+\s(mz60\d|xoom[\s2]{0,2})\sbuild\//i], ["model", ["vendor", "Motorola"], ["type", "tablet"]], [/android.+((sch-i[89]0\d|shw-m380s|gt-p\d{4}|gt-n8000|sgh-t8[56]9|nexus 10))/i, /((SM-T\w+))/i],
			[["vendor", "Samsung"], "model", ["type", "tablet"]], [/((s[cgp]h-\w+|gt-\w+|galaxy\snexus|sm-n900))/i, /(sam[sung]*)[\s-]*(\w+-?[\w-]*)*/i, /sec-((sgh\w+))/i], [["vendor", "Samsung"], "model", ["type", "mobile"]], [/(samsung);smarttv/i], ["vendor", "model", ["type", "smarttv"]], [/\(dtv[\);].+(aquos)/i], ["model", ["vendor", "Sharp"], ["type", "smarttv"]], [/sie-(\w+)*/i], ["model", ["vendor", "Siemens"], ["type", "mobile"]], [/(maemo|nokia).*(n900|lumia\s\d+)/i, /(nokia)[\s_-]?([\w-]+)*/i], [["vendor", "Nokia"], "model", ["type",
				"mobile"]], [/android\s3\.[\s\w;-]{10}(a\d{3})/i], ["model", ["vendor", "Acer"], ["type", "tablet"]], [/android\s3\.[\s\w;-]{10}(lg?)-([06cv9]{3,4})/i], [["vendor", "LG"], "model", ["type", "tablet"]], [/(lg) netcast\.tv/i], ["vendor", "model", ["type", "smarttv"]], [/(nexus\s[45])/i, /lg[e;\s\/-]+(\w+)*/i], ["model", ["vendor", "LG"], ["type", "mobile"]], [/android.+(ideatab[a-z0-9\-\s]+)/i], ["model", ["vendor", "Lenovo"], ["type", "tablet"]], [/linux;.+((jolla));/i], ["vendor", "model", ["type", "mobile"]], [/((pebble))app\/[\d\.]+\s/i],
			["vendor", "model", ["type", "wearable"]], [/android.+;\s(glass)\s\d/i], ["model", ["vendor", "Google"], ["type", "wearable"]], [/android.+(\w+)\s+build\/hm\1/i, /android.+(hm[\s\-_]*note?[\s_]*(?:\d\w)?)\s+build/i, /android.+(mi[\s\-_]*(?:one|one[\s_]plus)?[\s_]*(?:\d\w)?)\s+build/i], [["model", /_/g, " "], ["vendor", "Xiaomi"], ["type", "mobile"]], [/(mobile|tablet);.+rv\:.+gecko\//i], [["type", e.lowerize], "vendor", "model"]], d = [[/microsoft\s(windows)\s(vista|xp)/i], ["name", "version"], [/(windows)\snt\s6\.2;\s(arm)/i,
				/(windows\sphone(?:\sos)*|windows\smobile|windows)[\s\/]?([ntce\d\.\s]+\w)/i], ["name", ["version", b, d]], [/(win(?=3|9|n)|win\s9x\s)([nt\d\.]+)/i], [["name", "Windows"], ["version", b, d]], [/\((bb)(10);/i], [["name", "BlackBerry"], "version"], [/(blackberry)\w*\/?([\w\.]+)*/i, /(tizen)[\/\s]([\w\.]+)/i, /(android|webos|palm\sos|qnx|bada|rim\stablet\sos|meego|contiki)[\/\s-]?([\w\.]+)*/i, /linux;.+(sailfish);/i], ["name", "version"], [/(symbian\s?os|symbos|s60(?=;))[\/\s-]?([\w\.]+)*/i], [["name", "Symbian"], "version"],
			[/\((series40);/i], ["name"], [/mozilla.+\(mobile;.+gecko.+firefox/i], [["name", "Firefox OS"], "version"], [/(nintendo|playstation)\s([wids3portablevu]+)/i, /(mint)[\/\s\(]?(\w+)*/i, /(mageia|vectorlinux)[;\s]/i, /(joli|[kxln]?ubuntu|debian|[open]*suse|gentoo|arch|slackware|fedora|mandriva|centos|pclinuxos|redhat|zenwalk|linpus)[\/\s-]?([\w\.-]+)*/i, /(hurd|linux)\s?([\w\.]+)*/i, /(gnu)\s?([\w\.]+)*/i], ["name", "version"], [/(cros)\s[\w]+\s([\w\.]+\w)/i], [["name", "Chromium OS"], "version"], [/(sunos)\s?([\w\.]+\d)*/i],
			[["name", "Solaris"], "version"], [/\s([frentopc-]{0,4}bsd|dragonfly)\s?([\w\.]+)*/i], ["name", "version"], [/(ip[honead]+)(?:.*os\s*([\w]+)*\slike\smac|;\sopera)/i], [["name", "iOS"], ["version", /_/g, "."]], [/(mac\sos\sx)\s?([\w\s\.]+\w)*/i, /(macintosh|mac(?=_powerpc)\s)/i], [["name", "Mac OS"], ["version", /_/g, "."]], [/((?:open)?solaris)[\/\s-]?([\w\.]+)*/i, /(haiku)\s(\w+)/i, /(aix)\s((\d)(?=\.|\)|\s)[\w\.]*)*/i, /(plan\s9|minix|beos|os\/2|amigaos|morphos|risc\sos|openvms)/i, /(unix)\s?([\w\.]+)*/i], ["name", "version"]],
		b = c.apply(this, [[/(opera\smini)\/([\w\.-]+)/i, /(opera\s[mobiletab]+).+version\/([\w\.-]+)/i, /(opera).+version\/([\w\.]+)/i, /(opera)[\/\s]+([\w\.]+)/i], ["name", "version"], [/\s(opr)\/([\w\.]+)/i], [["name", "Opera"], "version"], [/(kindle)\/([\w\.]+)/i, /(lunascape|maxthon|netfront|jasmine|blazer)[\/\s]?([\w\.]+)*/i, /(avant\s|iemobile|slim|baidu)(?:browser)?[\/\s]?([\w\.]*)/i, /(?:ms|\()(ie)\s([\w\.]+)/i, /(rekonq)\/([\w\.]+)*/i, /(chromium|flock|rockmelt|midori|epiphany|silk|skyfire|ovibrowser|bolt|iron|vivaldi)\/([\w\.-]+)/i],
		["name", "version"], [/(trident).+rv[:\s]([\w\.]+).+like\sgecko/i], [["name", "IE"], "version"], [/(edge)\/((\d+)?[\w\.]+)/i], ["name", "version"], [/(yabrowser)\/([\w\.]+)/i], [["name", "Yandex"], "version"], [/(comodo_dragon)\/([\w\.]+)/i], [["name", /_/g, " "], "version"], [/(chrome|omniweb|arora|[tizenoka]{5}\s?browser)\/v?([\w\.]+)/i, /(uc\s?browser|qqbrowser)[\/\s]?([\w\.]+)/i], ["name", "version"], [/(dolfin)\/([\w\.]+)/i], [["name", "Dolphin"], "version"], [/((?:android.+)crmo|crios)\/([\w\.]+)/i], [["name", "Chrome"],
			"version"], [/XiaoMi\/MiuiBrowser\/([\w\.]+)/i], ["version", ["name", "MIUI Browser"]], [/android.+version\/([\w\.]+)\s+(?:mobile\s?safari|safari)/i], ["version", ["name", "Android Browser"]], [/FBAV\/([\w\.]+);/i], ["version", ["name", "Facebook"]], [/version\/([\w\.]+).+?mobile\/\w+\s(safari)/i], ["version", ["name", "Mobile Safari"]], [/version\/([\w\.]+).+?(mobile\s?safari|safari)/i], ["version", "name"], [/webkit.+?(mobile\s?safari|safari)(\/[\w\.]+)/i], ["name", ["version", b, {
				"1.0": "/8", "1.2": "/1", "1.3": "/3", "2.0": "/412",
				"2.0.2": "/416", "2.0.3": "/417", "2.0.4": "/419", "?": "/"
			}]], [/(konqueror)\/([\w\.]+)/i, /(webkit|khtml)\/([\w\.]+)/i], ["name", "version"], [/(navigator|netscape)\/([\w\.-]+)/i], [["name", "Netscape"], "version"], [/(swiftfox)/i, /(icedragon|iceweasel|camino|chimera|fennec|maemo\sbrowser|minimo|conkeror)[\/\s]?([\w\.\+]+)/i, /(firefox|seamonkey|k-meleon|icecat|iceape|firebird|phoenix)\/([\w\.-]+)/i, /(mozilla)\/([\w\.]+).+rv\:.+gecko\/\d+/i, /(polaris|lynx|dillo|icab|doris|amaya|w3m|netsurf)[\/\s]?([\w\.]+)/i, /(links)\s\(([\w\.]+)/i,
			/(gobrowser)\/?([\w\.]+)*/i, /(ice\s?browser)\/v?([\w\._]+)/i, /(mosaic)[\/\s]([\w\.]+)/i], ["name", "version"]]); b.major = e.major(b.version); d = c.apply(this, d); if (c = c.apply(this, g)) void 0 == c.model && (c.model = ""), void 0 == c.type && (c.type = ""), void 0 == c.vendor && (c.vendor = ""); return { browser: b, os: d, device: c }
	}()); DEXT5.ajax || (DEXT5.ajax = function() {
		DEXT5._CK_[7] = "j"; var a = function() {
			try { var a = new XMLHttpRequest; DEXT5.ajax.xhrWithCredentials && "withCredentials" in a && (a.withCredentials = !0); return a } catch (b) {
				DEXT5 &&
				DEXT5.logMode && console && console.log(b)
			} try { return new ActiveXObject("Msxml2.XHLHTTP") } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } try { return new ActiveXObject("Microsoft.XMLHTTP") } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } return null
		}, e = function() { }, c = function(a) { return 4 == a.readyState && (200 <= a.status && 300 > a.status || 304 == a.status || 0 === a.status || 1223 == a.status) }; DEXT5._CK_[8] = "g"; var b = function(a) {
			var b = null; c(a) && (b = a.responseText); a && a.onreadystatechange && (a.onreadystatechange =
				e); return b
		}, d = function(a) { var b = null; c(a) && (b = a.responseXML, b || (b = a.responseText)); a && a.onreadystatechange && (a.onreadystatechange = e); return b }; DEXT5._CK_[9] = "h"; var g = function(b, c, d) {
			var e = !!c, g = a(); if (!g) return null; g.open("GET", b, e); e && (g.onreadystatechange = function() { 4 == g.readyState && c(d(g)) }); try { g.send(null) } catch (h) { return null } "undefined" == typeof DEXT5.DEXTMULTIPLETIMEOUT && (DEXT5.DEXTMULTIPLETIMEOUT = []); if (!e) {
				var r = setTimeout(function() {
					try { g.abort() } catch (a) {
						DEXT5 && DEXT5.logMode && console &&
						console.log(a)
					} clearTimeout(r)
				}, 5E3); DEXT5.DEXTMULTIPLETIMEOUT.push(r)
			} return e ? "" : d(g)
		}; DEXT5._CK_[10] = "k"; DEXT5._CK_[11] = "l"; DEXT5._CK_[12] = "o"; var h = function(b, c, d, e) {
			var g = !!d, h = a(); if (!h) return null; h.open("POST", b, g); h.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); g && (h.onreadystatechange = function() { 4 == h.readyState && d(e(h, c)) }); try { DEXT5.util.setAddHttpHeader(h, DEXTTOP.G_CURREDITOR._config.addHttpHeader) } catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) } try { h.send(c) } catch (f) { return null } if (!g) {
				var u =
					setTimeout(function() { try { h.abort() } catch (a) { DEXT5 && DEXT5.logMode && console && console.log(a) } clearTimeout(u) }, 5E3); "undefined" == typeof DEXT5.DEXTMULTIPLETIMEOUT && (DEXT5.DEXTMULTIPLETIMEOUT = []); DEXT5.DEXTMULTIPLETIMEOUT.push(u)
			} return g ? "" : e(h)
		}; DEXT5._CK_[13] = "p"; return {
			load: function(a, c) { return g(a, c, b) }, loadXml: function(a, b) { return g(a, b, d) }, postData: function(b, c) {
				var d; a: if (d = a()) {
					try {
						d.open("POST", b, !1); d.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); try {
							DEXT5.util.setAddHttpHeader(d,
								DEXTTOP.G_CURREDITOR._config.addHttpHeader)
						} catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } d.send(c)
					} catch (g) { d = null; break a } d = d.responseText
				} else d = null; return d
			}, postMultiPart: function(b, c, d) { a: { var e = a(); if (e) { try { e.open("POST", b, !1); try { DEXT5.util.setAddHttpHeader(e, DEXTTOP.G_CURREDITOR._config.addHttpHeader) } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } e.send(c) } catch (h) { b = null; break a } b = d ? e : e.responseText } else b = null } return b }, postDataCallback: function(a, c, d) {
				return h(a, c,
					d, b)
			}, postFileData: function(b, c) {
				var d; a: if (d = a()) {
					try {
						d.open("POST", b, !1); var e = "--------------------" + (new Date).getTime(); d.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + e); for (var e = "--" + e, g = "", h = c.split("&"), r = h.length, f = null, u = "", v = "", y = 0; y < r; y++)f = h[y].split("="), "imagedata" == f[0] ? u = f[1] : (g += e + "\r\n", g += 'Content-Disposition: form-data; name="' + f[0] + '"\r\n\r\n', g += f[1] + "\r\n"); for (var h = null, r = "", z = window.atob(u), w = z.length, h = new Uint8Array(new ArrayBuffer(w)), y = 0; y < w; y++)h[y] =
							z.charCodeAt(y), r += String.fromCharCode(h[y]); String.fromCharCode.apply([], new Uint8Array(h)); new Uint8Array(h); v = r; g += e + "\r\n"; g += 'Content-Disposition: form-data; name="Filedata"; filename="' + (new Date).getTime() + '"\r\n'; g += "Content-Type: image/png\r\n"; g += "\r\n"; g += v + "\r\n"; d.send(g + (e + "--\r\n"))
					} catch (A) { d = null; break a } d = d.responseText
				} else d = null; return d
			}
		}
	}()); DEXT5.util || (DEXT5.util = {
		G_IMG_LIST: {}, trim: function(a) {
			if ("" == a) return a; var e = a; return e = a.trim ? a.trim() : a.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,
				"")
		}, rtrim: function(a) { return "" == a ? a : a.replace(/[\s\uFEFF\xA0]+$/, "") }, ltrim: function(a) { return "" == a ? a : a.replace(/^[\s\uFEFF\xA0]+/, "") }, setClass: function(a, e) { a.className = e }, getClass: function(a) { return a.className }, checkUrl: function(a) { var e = !1; a = a.replace(/ /gm, ""); return e = (new RegExp(/(http|ftp|https|news):\/\/[\S-]+(\.[\S-]+)+([\S.,@?^=%&amp;:\/~+#-]*[\S@?^=%&amp;\/~+#-])?/)).test(a) }, getDefaultIframeSrc: function() {
			var a = "", a = "document.open();" + (DEXT5.browser.isCustomDomain() ? 'document.domain="' +
				document.domain + '";' : "") + " document.close();"; return a = DEXT5.browser.ie && 12 > DEXT5.browser.ieVersion ? "javascript:void(function(){" + encodeURIComponent(a) + "}())" : ""
		}, makeIframe: function() { var a; try { a = document.createElement('<iframe frameborder="0" >') } catch (e) { a = document.createElement("iframe") } a.style.margin = "0px"; a.style.padding = "0px"; a.frameBorder = 0; a.style.overflow = "auto"; a.style.overflowX = "hidden"; a.style.backgroundColor = "#ffffff"; a.title = "DEXT5Editor"; return a }, addEvent: function(a, e, c, b) {
			try {
				a.addEventListener ?
				a.addEventListener(e, c, b) : a.attachEvent && a.attachEvent("on" + e, c), "" != DEXT5.CEditorID ? (DEXT5.DEXTMULTIPLEEVENT = {}, DEXT5.DEXTMULTIPLEEVENT[DEXT5.CEditorID] = [], DEXT5.DEXTMULTIPLEEVENT[DEXT5.CEditorID].push({ element: a, event: e, func: c }), DEXT5.CEditorID = "") : DEXTTOP.G_CURREDITOR && DEXT5.DEXTMULTIPLEEVENT[DEXTTOP.G_CURREDITOR.ID] && DEXT5.DEXTMULTIPLEEVENT[DEXTTOP.G_CURREDITOR.ID].push({ element: a, event: e, func: c })
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
		}, addEventEx: function(a, e, c, b) {
			try {
				a.addEventListener ?
				a.addEventListener(e, c, b) : a.attachEvent && (a.detachEvent("on" + e, c), a.attachEvent("on" + e, c)), "" != DEXT5.CEditorID ? (DEXT5.DEXTMULTIPLEEVENT = {}, DEXT5.DEXTMULTIPLEEVENT[DEXT5.CEditorID] = [], DEXT5.DEXTMULTIPLEEVENT[DEXT5.CEditorID].push({ element: a, event: e, func: c }), DEXT5.CEditorID = "") : DEXTTOP.G_CURREDITOR && DEXT5.DEXTMULTIPLEEVENT[DEXTTOP.G_CURREDITOR.ID] && DEXT5.DEXTMULTIPLEEVENT[DEXTTOP.G_CURREDITOR.ID].push({ element: a, event: e, func: c })
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
		}, removeEvent: function(a,
			e, c, b) { a.removeEventListener ? a.removeEventListener(e, c, b) : a.detachEvent && a.detachEvent("on" + e, c) }, stopEvent: function(a) { "bubbles" in a ? a.bubbles && a.stopPropagation() : a.cancelBubble = !0 }, cancelEvent: function(a) { a.preventDefault ? a.preventDefault() : a.returnValue = !1 }, ajax: { xml_http_request: function() { var a; window.XMLHttpRequest ? a = new XMLHttpRequest : window.ActiveXObject && (a = new ActiveXObject("Microsoft.XMLHTTP")); return a } }, url: {
				full_url: function(a) {
					var e = ""; if (0 == a.indexOf("http://") || 0 == a.indexOf("https://")) e =
						a; else if (0 == a.indexOf("/")) e = location.protocol + "//" + location.host + a; else var e = location.href, c = e.lastIndexOf("/"), e = e.substring(0, c + 1), e = e + a; return e
				}
			}, xml: {
				count: function(a, e) { return a ? a.getElementsByTagName(e).length : 0 }, getNode: function(a, e) { return null == a || void 0 == a ? null : this.getNodeIdx(a, e, 0) }, getNodeIdx: function(a, e, c) { return a.getElementsByTagName(e)[c] }, getNodeValue: function(a, e) { return null == a || void 0 == a ? "" : this.getNodeValueIdx(a, e, 0) }, getNodeValueIdx: function(a, e, c) {
					a = this.getNodeIdx(a,
						e, c); return this.getValue(a)
				}, getValue: function(a) { var e = "", c = ""; if (void 0 != a) try { 0 < a.childNodes.length && (c = e = a.firstChild.nodeValue); try { ("product_key" == a.nodeName || "license_key" == a.nodeName || "font" == a.nodeName || "encoding" == a.nodeName || "doctype" == a.nodeName) && 2 <= a.childNodes.length && (e = a.textContent ? a.textContent : c) } catch (b) { e = c } } catch (d) { e = "parsing error" } return e }, getAllNodes: function(a) {
					var e = {}, c = function(a, d) {
						for (var e = a.childNodes, h = !1, k = 0; k < e.length; k++) {
							var l = e[k]; if (3 != l.nodeType && 8 != l.nodeType &&
								4 != l.nodeType) { var h = !0, n = {}; if (0 < l.attributes.length) { n._attributes = {}; for (var m = 0; m < l.attributes.length; m++) { var t = l.attributes[m]; n._attributes[t.nodeName] = t.nodeValue } } "undefined" == typeof d[l.nodeName] ? d[l.nodeName] = n : 0 < d[l.nodeName].length ? d[l.nodeName].push(n) : d[l.nodeName] = [d[l.nodeName], n]; 0 < l.childNodes.length && c(l, n) }
						} h || (value = a.textContent || a.nodeTypedValue || "", (e = a.firstChild) ? 4 != e.nodeType && (value = value.replace(/^[\s]+|[\s]+&/g, "")) : value = value.replace(/^[\s]+|[\s]+&/g, ""), value && "" !=
							value && (d._value = value))
					}; for (a = a.firstChild; ;)if ("dext5" != a.nodeName.toLowerCase()) a = a.nextSibling; else break; c(a, e); return e
				}
			}, htmlToLowerCase: function(a) {
				if ("1" != DEXTTOP.G_CURREDITOR._config.useGetHtmlToLowerCase || 8 < DEXTTOP.DEXT5.browser.ieVersion && 0 == DEXTTOP.DEXT5.browser.quirks || 11 <= DEXTTOP.DEXT5.browser.ieVersion && 1 == DEXTTOP.DEXT5.browser.quirks) return a; var e = RegExp("<[^>]+>", "g"); results = a.match(e); if (null == results) return a; var c = e = -1, b = 0, d = results.length; for (i = 0; i < d; i++) {
					original = results[i];
					e = original.indexOf("'"); c = original.indexOf('"'); b = 2; 1 < e * c && (-1 < original.indexOf('="') ? (results[i] = results[i].replace(/\'/g, "dext5_1q"), b = 2) : -1 < original.indexOf("='") && (results[i] = results[i].replace(/\"/g, "dext5_2q"), b = 1)); stripquoted = 1 == b ? results[i].replace(/ [^=]+= *\'[^\']*\'/g, "") : results[i].replace(/ [^=]+= *"[^"]*"/g, ""); e = RegExp(" [^=]+=[^ >]+", "g"); if (unquoted = stripquoted.match(e)) for (e = unquoted.length, j = 0; j < e; j++) {
						if ("1" == DEXTTOP.G_CURREDITOR._config.xss_use && "0" == DEXTTOP.G_CURREDITOR._config.xss_allow_events_attribute &&
							(c = DEXTTOP.G_CURREDITOR._config.xss_remove_events, "" != c)) { var c = "," + c + ",", g = unquoted[j].split("=")[0]; if (-1 < c.indexOf("," + DEXT5.util.trim(g) + ",")) return a } addquotes = 1 == b ? unquoted[j].replace(/( [^=]+=)([^ >]+)/g, "$1'$2'") : unquoted[j].replace(/( [^=]+=)([^ >]+)/g, '$1"$2"'); results[i] = results[i].replace(unquoted[j], addquotes)
					} results[i] = results[i].replace(/<\/?[^>|^ ]+/, function(a) { return a.toLowerCase() }); results[i] = 1 == b ? results[i].replace(/ [^=|^ ]+=\'/g, function(a) { return a.toLowerCase() }) : results[i].replace(/ [^=|^ ]+="/g,
						function(a) { return a.toLowerCase() }); results[i] = results[i].replace(/dext5_1q/g, "'"); results[i] = results[i].replace(/dext5_2q/g, '"'); a = a.replace(original, results[i])
				} return a
			}, hexDigits: "0123456789abcdef".split(""), hex: function(a) { return isNaN(a) ? "00" : this.hexDigits[(a - a % 16) / 16] + this.hexDigits[a % 16] }, rgb2hex: function(a) { a = a.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/); if (!(null == a || 4 > a.length)) return "#" + this.hex(a[1]) + this.hex(a[2]) + this.hex(a[3]) }, dextSetAttribute: function(a, e, c, b) {
				"string" == typeof c ?
				0 < c.length && (a.style[e] = c + b) : "number" == typeof c && (a.style[e] = c)
			}, getRealBackgoundImagePath: function(a) { return a = a.replace(/url\(\s*(["']?)\s*([^\)]*)\s*\1\s*\)/i, function(a, c, b) { return b }) }, replaceForXSS: function(a, e, c, b, d) {
				var g = !1, h = !1; try {
					if (a) {
						var k = RegExp("&#x3c;", "gi"); 1 == k.test(a) && (g = !0, a = a.replace(k, "&amp;lt;")); k = RegExp("&#x3e;", "gi"); 1 == k.test(a) && (g = !0, a = a.replace(k, "&amp;gt;")); k = RegExp("&#9;", "gi"); 1 == k.test(a) && (g = !0, a = a.replace(k, "")); k = RegExp("&#13;", "gi"); 1 == k.test(a) && (g = !0, a = a.replace(k,
							"")); if ("string" == typeof e && "" != e) for (var l = e.split(","), n = l.length, m = 0; m < n; m++) { var t = l[m], q = new RegExp("<" + t + ">", "gi"); q.test(a) && (a = a.replace(q, "!@#de_temp_script#@!"), q = RegExp("(<[a-zA-Z][a-zA-Z0-9]*[^>]*)!@#de_temp_script#@!([^>]*)", "g"), a = a.replace(q, "$1$2"), q = RegExp("!@#de_temp_script#@!", "gi"), a = a.replace(q, "<" + t + ">")) } try {
								var k = !1, r = null, f = DEXT5.util.splitBodyInnerString(a); if ("function" == typeof DOMParser) { var u = new DOMParser, v = u.parseFromString("", "text/html"); v && 9 == v.nodeType && (r = v.createElement("div")) } else r =
									document.createElement("div"); if (r) {
										r.innerHTML = f.body; for (var t = ["embed", "iframe", "object"], y = t.length, m = 0; m < y; m++)for (var z = r.getElementsByTagName(t[m]), w = z.length, q = w - 1; 0 <= q; q--) { var A = z[q], D = A.getAttribute("src"), C = !1; "string" == typeof D && (C = DEXTTOP.G_CURREDITOR._FRAMEWIN.checkXssContentType(D)); if (0 == C) { var F = A.getAttribute("data"); "string" == typeof F && (C = DEXTTOP.G_CURREDITOR._FRAMEWIN.checkXssContentType(F)) } C && (A.parentNode.removeChild(A), k = !0) } z = r.getElementsByTagName("meta"); w = z.length; for (m =
											w - 1; 0 <= m; m--) { var A = z[m], I = A.getAttribute("http-equiv"); "string" == typeof I && "refresh" == I.toLowerCase() && (A.parentNode.removeChild(A), k = !0) } k && (a = f.head + r.innerHTML + f.tail)
									} r = void 0; v && (v = void 0); u && (u = void 0)
							} catch (P) { DEXT5 && DEXT5.logMode && console && console.log(P) }
					} if ("" != c) for (var B = c.split(","), E = B.length, m = 0; m < E; m++) { var Q = B[m], J = new RegExp("<" + Q + "[^>]*>[\\s\\S]*?</" + Q + ">", "gim"); 1 == J.test(a) && (g = !0, a = a.replace(J, "")); var G = new RegExp("<" + Q + "[^>]*>", "gi"); 1 == G.test(a) && (g = !0, a = a.replace(G, "")) } if ("" !=
						b) {
							try { "function" == typeof DOMParser && (u = new DOMParser, (v = u.parseFromString("", "text/html")) && 9 == v.nodeType && (r = v.createElement("div"), f = DEXT5.util.splitBodyInnerString(a), r.innerHTML = f.body, a = f.head + r.innerHTML + f.tail)) } catch (R) { DEXT5 && DEXT5.logMode && console && console.log(R) } for (var H = b.split(","), T = H.length, m = 0; m < T; m++) {
								var L = H[m], O = new RegExp("(?:" + L + ')\\s*=\\s*"[^"]*"\\s*', "gi"), x = new RegExp("(?:" + L + ")\\s*=\\s*'[^']*'\\s*", "gi"), G = new RegExp("(?:" + L + ")\\s*?=.*?(\\(.*?\\));?(\"|'| |)", "gi"); if (O.test(a) ||
									x.test(a) || G.test(a)) g = !0, a = a.replace(O, ""), a = a.replace(x, ""), a = a.replace(G, "")
							}
					} if ("" != e) for (l = e.split(","), n = l.length, m = 0; m < n; m++) {
						var W = new RegExp("<\\/" + l[m] + ">", "gi"), N = a.match(new RegExp("<" + l[m] + "[^>]*>", "gi")); if (N) {
							var g = !0, S = N.length; e = ""; c = !1; for (b = 0; b < S; b++) {
								if ("script" == l[m]) {
									var M = N[b].match(RegExp("src\\s*=\\s*(?:'|\")([^(\"|')]*)(?:'|\")", "gi")); if (null != M) try {
										for (var U = M[0].substring(5, M[0].length - 1), K = DEXTTOP.G_CURREDITOR._config.xss_allow_url, r = 0; r < K.length; r++)if (U == K[r]) {
											c = !0;
											break
										}
									} catch (X) { h = !0 }
								} 0 == c && (e = N[b].toLowerCase(), e = e.replace("<", "&lt;"), e = e.replace(">", "&gt;"), a = a.replace(N[b], e))
							} 0 == c && (a = a.replace(W, "&lt;/" + l[m] + "&gt;"))
						}
					} null != d && void 0 != d && d && (g = !0, O = RegExp("(<[^>]*url)(\\(['\"]?(javascript|jav ascript|vbscript)([^'\")]*)['\"]?[\\)]?[;]?\\))", "gi"), a = a.replace(O, "$1)"), d = "", 0 < DEXTTOP.G_CURREDITOR._config.xss_check_attribute.length && (d = DEXTTOP.G_CURREDITOR._config.xss_check_attribute.join("|"), x = new RegExp("(<[^>]*(" + d + ')=")(javascript|jav ascript|vbscript).*?"',
						"gi"), a = a.replace(x, '$1"')))
				} catch (Y) { h = !0 } 0 == g || 1 == h ? a = "" : "" == a && (a = DEXTTOP.G_CURREDITOR._FRAMEWIN.getDefaultParagraphValue(!0)); return a
			}, removeTags: function(a, e) { if ("" != e) for (var c = e.split(","), b = null, d = c.length, g = 0, h = 0; h < d; h++)for (b = a.getElementsByTagName(c[h]), g = b.length, --g; 0 <= g; g--) { var k = b[g]; k.parentNode.removeChild(k) } }, removeEvents: function(a, e, c) { if ("" != e) { a = e.split(","); e = c.length; for (var b = 0, d = 0; d < e; d++)for (var b = a.length, g = 0; g < b; g++)c[d].removeAttribute(a[g]) } }, removeElement: function(a) {
				try {
					if (null !=
						a) { for (; a.firstChild;)a.parentNode.insertBefore(a.firstChild, a); a.parentNode.removeChild(a) }
				} catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) }
			}, removeElementWithChildNodes: function(a) {
				try {
					if (null != a) {
						if ("1" == a.nodeType && "iframe" == a.tagName.toLowerCase()) {
							var e = a.contentDocument || a.contentWindow.document || a.document; if (e) {
								if (a.contentWindow) for (var c in a.contentWindow) if (a.contentWindow.hasOwnProperty(c)) try { "location" != c && (a.contentWindow[c] = null), delete a.contentWindow[c] } catch (b) {
									DEXT5 &&
									DEXT5.logMode && console && console.log(b)
								} for (var d = e.getElementsByTagName("script"); 0 != d.length;) { d[0].parentNode.removeChild(d[0]); for (var g in d) delete d[g] } e.body && (e.body.innerHTML = ""); e.body.parentNode && (e.body.parentNode.innerHTML = "")
							} a.setAttribute("src", "")
						} for (; a.hasChildNodes();)try { a.removeChild(a.firstChild), a.firstChild = null, delete a.firstChild } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } a.parentNode.removeChild(a); try { delete null } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) }
					}
				} catch (l) {
					DEXT5 &&
					DEXT5.logMode && console && console.log(l)
				}
			}, G_AP27: "r", G_AP25: "o", officeCleanByDom: function(a, e) {
				if ("" == a || void 0 == a) return ""; a = DEXTTOP.G_CURREDITOR._FRAMEWIN.adjustMsoBorder(a); var c = !1; /([:| ])([-]{0,1}[0-9]+|[-]{0,1}[0-9]*[.]{1}[0-9]+)pt/gi.test(a) && (c = !0); /([:| ])([:| ][-]{0,1}[0-9]+|[-]{0,1}[0-9]*[.]{1}[0-9]+)cm/gi.test(a) && (c = !0); var b = null, d = 0; if (1 == c) {
					try { a = DEXTTOP.G_CURREDITOR._FRAMEWIN.applyFakeImgSrc(a) } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } c = document.createElement("div");
					c.innerHTML = a; c.style.display = "none"; DEXTTOP.G_CURREDITOR._FRAMEWIN.document.body.appendChild(c); for (var h = c.getElementsByTagName("*"), k = h.length, l = [], n = 0; n < k; n++) {
						var m = h[n], t = m.tagName; if ("TD" == t && "1" == DEXTTOP.G_CURREDITOR._config.useTableDiagonal) {
							var q = m.outerHTML, q = q.match(RegExp("<td([^>]*?)>", "gi")); if (m.getAttribute("diagonal-up") || m.getAttribute("diagonal-down")) m.getAttribute("diagonal-up") && (q = m.getAttribute("diagonal-up"), l.push({ node: m, upOrDown: "up", style: q }), m.removeAttribute("diagonal-up")),
								m.getAttribute("diagonal-down") && (q = m.getAttribute("diagonal-down"), l.push({ node: m, upOrDown: "down", style: q }), m.removeAttribute("diagonal-down")); else if (q && -1 < q[0].indexOf("diagonal-")) for (var q = q[0], r = q.match(RegExp("(diagonal-(up|down):s*[^;|^\"|^']*)", "gi")), f = r.length, u = 0; u < f; u++) { var q = r[u].split(":"), v = q[0].substring(q[0].lastIndexOf("-") + 1); l.push({ node: m, upOrDown: v, style: q[1] }) }
						} m.style.width && (q = m.style.width.toLowerCase(), -1 < q.indexOf("pt") ? (q = 4 * parseFloat(q) / 3, 0 < q && 1 > q && (q = 1), 0 > q && (q = 0),
							q = Math.round(q) + "px", m.style.width = q) : -1 < q.indexOf("cm") && (_cm = 37.795275593333 * parseFloat(q), 0 < _cm && 1 > _cm && (_cm = 1), 0 > _cm && (_cm = 0), q = Math.round(_cm) + "px", m.style.width = q)); m.style.height && (q = m.style.height.toLowerCase(), -1 < q.indexOf("pt") ? (q = 4 * parseFloat(q) / 3, 0 < q && 1 > q && (q = 1), 0 > q && (q = 0), q = Math.round(q) + "px", m.style.height = q) : -1 < q.indexOf("cm") && (_cm = 37.795275593333 * parseFloat(q), 0 < _cm && 1 > _cm && (_cm = 1), 0 > _cm && (_cm = 0), q = Math.round(_cm) + "px", m.style.height = q)); q = ""; m.style.borderStyle && (q = m.style.borderStyle);
						r = { isFloor: !1 }; "HWP" == DEXTTOP.G_CURREDITOR._FRAMEWIN.getPasteFormatType() && (r.isFloor = !0); "" != m.style.borderTop && (m.style.borderTop = DEXT5.util.replacePtOrCmToPx(m.style.borderTop, r)); "" != m.style.borderRight && (m.style.borderRight = DEXT5.util.replacePtOrCmToPx(m.style.borderRight, r)); "" != m.style.borderBottom && (m.style.borderBottom = DEXT5.util.replacePtOrCmToPx(m.style.borderBottom, r)); "" != m.style.borderLeft && (m.style.borderLeft = DEXT5.util.replacePtOrCmToPx(m.style.borderLeft, r)); "" != m.style.borderWidth &&
							(m.style.borderWidth = DEXT5.util.replacePtOrCmToPx(m.style.borderWidth, r)); "" != q && (m.style.borderStyle = q); DEXT5.util.adjustBorderStyle(m); if ("HWP" == DEXTTOP.G_CURREDITOR._FRAMEWIN.getPasteFormatType() && "TD" == m.tagName) for (q = ["borderTop", "borderRight", "borderBottom", "borderLeft"], r = q.length, f = 0; f < r; f++)if (u = m.style[q[f]], u = u.replace(/,\s+/g, ","), u = u.split(" "), 3 == u.length && "double" == u[1]) { v = DEXT5.util.parseIntOr0(u[0]); if (2 == v || 4 == v) v = 3, u[0] = v + "px"; m.style[q[f]] = u.join(" ") } "PPT" == DEXTTOP.G_CURREDITOR._FRAMEWIN.getPasteFormatType() &&
								"COL" == t && (b = DEXTTOP.G_CURREDITOR._FRAMEWIN.GetParentbyTagName(m, "table"), t = DEXT5.util.parseIntOr0(m.width), 0 == t && (t = DEXT5.util.parseIntOr0(m.style.width)), 0 < t && (m.style.width = t + 1 + "px", d++))
					} b && (h = DEXT5.util.parseIntOr0(b.style.width), 0 < h && (b.style.width = h + d + "px")); if ("1" == DEXTTOP.G_CURREDITOR._config.useTableDiagonal && DEXTTOP.G_CURREDITOR._FRAMEWIN.G_DEXT_Diagonal) for (u = 0; u < l.length; u++)DEXTTOP.G_CURREDITOR._FRAMEWIN.G_DEXT_Diagonal.makeDiagonal(l[u].node, l[u].upOrDown, l[u].style, !0); a = c.innerHTML;
					try { a = DEXTTOP.G_CURREDITOR._FRAMEWIN.applyFakeImgSrc(a, !0) } catch (y) { DEXT5 && DEXT5.logMode && console && console.log(y) } try { c.innerHTML = "", c.parentNode.removeChild(c), delete null } catch (z) { DEXT5 && DEXT5.logMode && console && console.log(z) }
				} return a
			}, officeClean: function(a, e) {
				if ("" == a || void 0 == a) return ""; var c = null, b = []; a = DEXT5.util.replaceAll(a, "hairline", "dotted"); a = DEXT5.util.officeCleanByDom(a, e); a = a.replace(/\.0px/g, "px"); a = a.replace(/\t/g, ""); a = DEXT5.util.removeOfficeDummyTag(a, "\x3c!--[if supportFields]>",
					"<![endif]--\x3e"); c = RegExp("<o:p></o:p>", "gi"); a = a.replace(c, ""); c = RegExp("<o:p>\\s+</o:p>", "gi"); a = a.replace(c, ""); c = RegExp("<o:p ([^>]+)></o:p>", "gi"); a = a.replace(c, ""); c = RegExp("<o:p ([^>]+)>\\s+</o:p>", "gi"); a = a.replace(c, ""); c = RegExp("<w:sdt[^>]*>", "gi"); a = a.replace(c, ""); c = RegExp("</w:sdt>", "gi"); a = a.replace(c, ""); c = RegExp('<[^>]+(lang=["]?en-us["])[^>]*>', "gi"); if (c = a.match(c)) for (var b = c.length, d = 0; d < b; d++) {
						var g = c[d], h = RegExp('\\slang=[\\"]?en-us[\\"]?', "gi"), g = g.replace(h, ""); a = a.replace(c[d],
							g)
					} c = RegExp("<[^>]+(mso)[^>]*>", "gi"); if (c = a.match(c)) for (b = c.length, d = 0; d < b; d++)g = c[d], "1" == DEXTTOP.G_CURREDITOR._config.removeMsoClass && (h = RegExp('\\sclass=[\\"]?(mso)\\w+[\\"]?', "gi"), g = g.replace(h, "")), g = g.replace(/&quot;/gi, "^"), h = RegExp("mso-[^:]+:\\^\\^(;?)", "gi"), g = g.replace(h, ""), h = RegExp('(\\s+)?mso-number-format:(\\s+)?"(.+?)"(\\s+)?;', "gi"), g = g.replace(h, ""), h = RegExp('(\\s+)?mso-number-format:(\\s+)?"(.+?)"(\\s+)?;?', "gi"), g = g.replace(h, ""), h = RegExp("\\s?mso-[\\w\uac00-\ud7a3\\-: ?'\"\\^@%\\.\\\\_]+; ?",
						"gi"), g = g.replace(h, ""), h = RegExp('\\s?mso-[\\w\uac00-\ud7a3\\-: ?\'"\\^@%\\.\\\\_]+" ?', "gi"), g = g.replace(h, '"'), h = RegExp("\\s?mso-[\\w\uac00-\ud7a3\\-: ?]+(['\"])", "gi"), g = g.replace(h, "$1"), g = g.replace(/\^/gi, "&quot;"), a = a.replace(c[d], g); c = new RegExp("(<span[^>]*?raon_placeholder.*?>)(?:\\s|<br>|<br />|  | |)</span>", "gim"); a = a.replace(c, "$1" + unescape("%u200B") + "</span>"); for (d = 0; 5 > d; d++) {
							b = ["o:p", "span", "font"]; h = b.length; for (g = 0; g < h; g++)-1 < a.indexOf("<" + b[g]) && (c = new RegExp("<" + b[g] + "[^>]*></" +
								b[g] + ">", "gi"), a = a.replace(c, ""), "span" != b[g] && (c = new RegExp("<" + b[g] + "[^>]*> </" + b[g] + ">", "gi"), a = a.replace(c, "&nbsp;"))); b = ["o", "v", "w", "m", "x"]; h = b.length; for (g = 0; g < h; g++)0 == g && (a = a.replace(/<o:p/gi, "<dexto:p")), -1 < a.indexOf("<" + b[g]) && (c = new RegExp("<" + b[g] + ":[^/>]+/>", "gi"), a = a.replace(c, ""), c = new RegExp("<" + b[g] + ":[^>]+>[^<]*</" + b[g] + ":[^>]+>", "gi"), a = a.replace(c, ""), "v" == b[g] && (c = new RegExp("<" + b[g] + ":[^>]+>", "gi"), a = a.replace(c, ""), c = new RegExp("</" + b[g] + ":[^>]+>", "gi"), a = a.replace(c, ""))),
									g == h - 1 && (a = a.replace(/<dexto:p/gi, "<o:p"))
						} d = "(<span[^>]*?raon_placeholder.*?>)(" + unescape("%u200B") + "?)</span>"; c = new RegExp(d, "gim"); a = a.replace(c, "$1</span>"); a = a.replace(/style=""/gi, ""); a = a.replace(/style=''/gi, ""); a = a.replace(/\s>/gi, ">"); a = DEXTTOP.DEXT5.util.replaceOneSpaceToNbsp(a); a = a.replace(/<\/td>&nbsp;<\/tr>/g, "</td></tr>"); a = a.replace(/<\/td>&nbsp;<\/td>/g, "</td></td>"); a = a.replace(/<\/th>&nbsp;<\/tr>/g, "</th></tr>"); a = a.replace(/<\/th>&nbsp;<\/th>/g, "</th></th>"); if (DEXTTOP.DEXT5.browser.ie &&
							10 > DEXTTOP.DEXT5.browser.ieVersion && (!DEXTTOP.G_CURREDITOR.setTextPaste || "1" == DEXTTOP.G_CURREDITOR.setTextPasteMode)) {
								var c = a.length, g = b = !1, k = h = "", l = ""; if (-1 == a.indexOf("<") && -1 == a.indexOf(">")) for (d = 0; d < c; d++)h = a.charAt(d), k = a.charAt(d + 1), " " != h && 32 != h.charCodeAt(0) || " " != k && 32 != k.charCodeAt(0) || (h = "&nbsp;"), l += h; else for (d = 0; d < c; d++)h = a.charAt(d), k = a.charAt(d + 1), "<" == h ? (b = !1, g = "p" == k.toLowerCase() ? !0 : !1) : ">" == h && 1 == g ? b = !0 : 1 != b || " " != h && 32 != h.charCodeAt(0) || " " != k && 32 != k.charCodeAt(0) || (h = "&nbsp;"),
									l += h; a = l
				} "1" != DEXTTOP.G_CURREDITOR.setTextPasteMode && (DEXTTOP.G_CURREDITOR.setTextPaste = !1); 0 == e && (a = DEXTTOP.DEXT5.util.removeLocalFileImage(a)); return a
			}, removeLocalFileImage: function(a) { return "" == a || void 0 == a ? "" : a = a.replace(RegExp("<(img)([^>]*?)src=('|\")(file://)[^>]+>", "gi"), "") }, html2xhtml: function(a) {
				if ("" == a || void 0 == a) return ""; for (var e = "area base br col embed frame hr img input link meta param".split(" "), c = e.length, b = null, d = RegExp(), g = 0; g < c; g++)b = e[g], d = new RegExp("<" + b + " ([^>]*)>", "gi"),
					a = a.replace(d, "<" + b + " $1 />"), d = new RegExp("<" + b + ">", "gi"), a = a.replace(d, "<" + b + " />"); return a
			}, emptyTagRemove: function(a) {
				if ("" == a || void 0 == a) return ""; try { if ("1" == DEXTTOP.G_CURREDITOR._config.empty_tag_remove) { var e = document.createElement("div"); e.innerHTML = a; for (var c = ["p", "div"], b = c.length, d = [], g = 0; g < b; g++) { for (var h = e.getElementsByTagName(c[g]), k = h.length, l = 0; l < k; l++) { var n = h[l]; "" == n.innerHTML && d.push(n) } for (; 0 < d.length;)n = d.pop(), n.parentNode.removeChild(n) } a = e.innerHTML } } catch (m) {
					DEXT5 && DEXT5.logMode &&
					console && console.log(m)
				} return a
			}, emptyTagRemoveCheckInContentTag: function(a, e) {
				if ("" == a || void 0 == a) return ""; try {
					if ("1" == DEXTTOP.G_CURREDITOR._config.empty_tag_remove) {
						var c = document.createElement("div"); c.innerHTML = a; for (var b = c.getElementsByTagName("p"), d = b.length, g, h, k = "", l = [], n = !1, m = 0; m < d; m++)if (g = b[m], k = "textContent" in g ? g.textContent : g.innerText, "" == k) { h = g.innerHTML; for (var n = !1, t = e.length, q = 0; q < t; q++)if (-1 < h.toLowerCase().indexOf("<" + e[q])) { n = !0; break } 0 == n && l.push(g) } for (; 0 < l.length;)g = l.pop(),
							c.removeChild(g); a = c.innerHTML
					}
				} catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) } return a
			}, replaceBrOrSpaceToNbspString: function(a, e) {
				reg_exp = new RegExp("<" + e + "><br></" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + ">&nbsp;</" + e + ">"); reg_exp = new RegExp("<" + e + "><br/></" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + ">&nbsp;</" + e + ">"); reg_exp = new RegExp("<" + e + "><br /></" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + ">&nbsp;</" + e + ">"); reg_exp = new RegExp("<" + e + "></" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + ">&nbsp;</" + e +
					">"); reg_exp = new RegExp("<" + e + ">\\s+</" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + ">&nbsp;</" + e + ">"); reg_exp = new RegExp("<" + e + " ([^>]+)><br></" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + " $1>&nbsp;</" + e + ">"); reg_exp = new RegExp("<" + e + " ([^>]+)><br/></" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + " $1>&nbsp;</" + e + ">"); reg_exp = new RegExp("<" + e + " ([^>]+)><br /></" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + " $1>&nbsp;</" + e + ">"); reg_exp = new RegExp("<" + e + " ([^>]+)></" + e + ">", "gi"); a = a.replace(reg_exp, "<" + e + " $1>&nbsp;</" +
						e + ">"); reg_exp = new RegExp("<" + e + " ([^>]+)>\\s+</" + e + ">", "gi"); return a = a.replace(reg_exp, "<" + e + " $1>&nbsp;</" + e + ">")
			}, htmlRevision: function(a, e) {
				if ("" == a || void 0 == a) return ""; var c = null; "1" == DEXTTOP.G_CURREDITOR._config.removeComment && (a = a.replace(/(<style[^>]*>\s*\x3c!--)|(\x3c!--.*?--\x3e)|(\x3c!--[\w\W\n\s]+?--\x3e)/gi, "$1")); if (DEXT5.browser.ie && 11 > DEXT5.browser.ieVersion || e) for (c = RegExp("<(td|th)([^>]*?>)([\\\\w\\\\W\\\\s]*?)<p([^>]*?>)<br ?/?></p>([\\\\w\\\\W\\\\s]*?)</(td|th)>", "gi"); c.test(a);)a =
					a.replace(c, "<$1$2$3<p$4&nbsp;</p>$5</$6>"); else for (c = RegExp("<(td|th)([^>]*?>)([\\\\w\\\\W\\\\s]*?)<p([^>]*?>)&nbsp;</p>([\\\\w\\\\W\\\\s]*?)</(td|th)>", "gi"); c.test(a);)a = a.replace(c, "<$1$2$3<p$4<br></p>$5</$6>"); c = new RegExp("(<span[^>]*?raon_placeholder.*?>)(?:\\s|<br>|<br />|  | |)</span>", "gim"); a = a.replace(c, "$1" + unescape("%u200B") + "</span>"); var b = DEXTTOP.G_CURREDITOR._config.setDefaultValueInEmptyTag, d = b.length; if ("1" == DEXTTOP.G_CURREDITOR._config.removeEmptyTagReplaceDefaultParagraphValue) for (var g =
						0; g < d; g++) {
							var h = b[g]; DEXT5.browser.ie && 11 > DEXT5.browser.ieVersion || e || "1" == DEXTTOP.G_CURREDITOR._config.ie11_BugFixed_ReplaceBr ? "span" == h ? "1" == DEXTTOP.G_CURREDITOR._config.adjustEmptySpan && (c = new RegExp("<" + h + "></" + h + ">", "gi"), a = a.replace(c, "<" + h + ">&nbsp;</" + h + ">"), c = new RegExp("<" + h + " ([^>]+)></" + h + ">", "gi"), a = a.replace(c, "<" + h + " $1>&nbsp;</" + h + ">")) : a = DEXT5.util.replaceBrOrSpaceToNbspString(a, h) : DEXT5.browser.ie && 11 == DEXT5.browser.ieVersion && 0 == h.indexOf("h") ? (c = new RegExp("<" + h + "></" + h + ">", "gi"),
								a = a.replace(c, "<" + h + ">" + unescape("%u200B") + "</" + h + ">"), c = new RegExp("<" + h + ">&nbsp;</" + h + ">", "gi"), a = a.replace(c, "<" + h + ">" + unescape("%u200B") + "</" + h + ">"), c = new RegExp("<" + h + ">\\s+</" + h + ">", "gi"), a = a.replace(c, "<" + h + ">" + unescape("%u200B") + "</" + h + ">"), c = new RegExp("<" + h + " ([^>]+)></" + h + ">", "gi"), a = a.replace(c, "<" + h + " $1>" + unescape("%u200B") + "</" + h + ">"), c = new RegExp("<" + h + " ([^>]+)>&nbsp;</" + h + ">", "gi"), a = a.replace(c, "<" + h + " $1>" + unescape("%u200B") + "</" + h + ">"), c = new RegExp("<" + h + " ([^>]+)>\\s+</" + h + ">",
									"gi"), a = a.replace(c, "<" + h + " $1>" + unescape("%u200B") + "</" + h + ">")) : "span" == h ? "1" == DEXTTOP.G_CURREDITOR._config.adjustEmptySpan && (c = new RegExp("<" + h + "></" + h + ">", "gi"), a = a.replace(c, "<" + h + "><br></" + h + ">"), c = new RegExp("<" + h + " ([^>]+)></" + h + ">", "gi"), a = a.replace(c, "<" + h + " $1><br></" + h + ">")) : "1" == DEXTTOP.G_CURREDITOR._config.removeEmptyTagReplaceDefaultParagraphValue && (c = new RegExp("<" + h + "></" + h + ">", "gi"), a = a.replace(c, "<" + h + "><br></" + h + ">"), c = new RegExp("<" + h + ">&nbsp;</" + h + ">", "gi"), a = a.replace(c, "<" +
										h + "><br></" + h + ">"), c = new RegExp("<" + h + ">\\s+</" + h + ">", "gi"), a = a.replace(c, "<" + h + "><br></" + h + ">"), c = new RegExp("<" + h + " ([^>]+)></" + h + ">", "gi"), a = a.replace(c, "<" + h + " $1><br></" + h + ">"), c = new RegExp("<" + h + " ([^>]+)>&nbsp;</" + h + ">", "gi"), a = a.replace(c, "<" + h + " $1><br></" + h + ">"), c = new RegExp("<" + h + " ([^>]+)>\\s+</" + h + ">", "gi"), a = a.replace(c, "<" + h + " $1><br></" + h + ">"))
					} c = "(<span[^>]*?raon_placeholder.*?>)(" + unescape("%u200B") + "?)</span>"; c = new RegExp(c, "gim"); a = a.replace(c, "$1</span>"); 0 == DEXTTOP.DEXT5.browser.ie &&
						"1" == DEXTTOP.G_CURREDITOR._config.removeCarriageReturnInTag && (c = RegExp("<([^>]*)([\r\n])(.*?)>", "igm"), c.test(a) && (a = a.replace(c, "<$1$3>")), c = RegExp("<([^>]*)([\n])(.*?)>", "igm"), c.test(a) && (a = a.replace(c, "<$1$3>"))); c = RegExp("<c:foreach", "gi"); a = a.replace(c, "<c:forEach"); c = RegExp("</c:foreach", "gi"); a = a.replace(c, "</c:forEach"); c = new RegExp(unescape("%u202c"), "g"); return a = a.replace(c, "")
			}, nbspRemove: function(a) {
				if ("" == a || void 0 == a) return ""; var e = null, e = RegExp("<p>&nbsp;</p>", "gi"); a = a.replace(e,
					"<p></p>"); e = RegExp("<p ([^>]+)>&nbsp;</p>", "gi"); return a = a.replace(e, "<p $1></p>")
			}, ImageSrcConvert: function(a, e, c) {
				var b = null; try { b = DEXTTOP.G_CURREDITOR._FRAMEWIN.getDocumentForDummy ? DEXTTOP.G_CURREDITOR._FRAMEWIN.getDocumentForDummy() : document } catch (d) { b = document } b = b.createElement("div"); b.innerHTML = a; var g = b.getElementsByTagName("img"), h = g.length, k = null, l = "", n = ""; c += "_dext5_base64_image_"; for (var m = 0; m < h; m++) {
					k = g[m]; n = c + m; try { l = k.src } catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } 1 ==
						e ? -1 < l.indexOf("data:image") && -1 < l.indexOf("base64,") && (this.G_IMG_LIST[n] = l, k.removeAttribute("src"), k.setAttribute("raon-tsrc", n)) : -1 < l.indexOf(c) && (k.src = this.G_IMG_LIST[n], this.G_IMG_LIST[n] = "")
				} e = !1; for (var q in this.G_IMG_LIST) { e = !0; break } e && (a = b.innerHTML, a = a.replace(/<img[^>]*? raon-tsrc=/gi, function(a) { return a.replace(" raon-tsrc=", " src=") })); b = null; return a
			}, getUrlString: function(a) {
				a = DEXT5.util.replaceAll(a, " ", "%20"); a = DEXT5.util.replaceAll(a, "&nbsp;", "%21"); a = DEXT5.util.replaceAll(a,
					"!", "%20"); a = DEXT5.util.replaceAll(a, '"', "%22"); a = DEXT5.util.replaceAll(a, "#", "%23"); a = DEXT5.util.replaceAll(a, "$", "%24"); a = DEXT5.util.replaceAll(a, "%", "%25"); a = DEXT5.util.replaceAll(a, "&", "%26"); a = DEXT5.util.replaceAll(a, "'", "%27"); a = DEXT5.util.replaceAll(a, "+", "%2B"); a = DEXT5.util.replaceAll(a, "/", "%2F"); a = DEXT5.util.replaceAll(a, ":", "%3A"); a = DEXT5.util.replaceAll(a, ";", "%3B"); a = DEXT5.util.replaceAll(a, "<", "%3C"); a = DEXT5.util.replaceAll(a, "=", "%3D"); a = DEXT5.util.replaceAll(a, ">", "%3E"); a = DEXT5.util.replaceAll(a,
						"?", "%3F"); return a = DEXT5.util.replaceAll(a, "@", "%40")
			}, G_AP11: 6, replaceAll: function(a, e, c) { a && "" != a && (a = a.split(e).join(c)); return a }, getSkinNames: function() { return "blue brown darkgray gold gray green orange pink purple red silver yellow".split(" ") }, replaceOneSpaceToNbsp: function(a) {
				var e = a, c, b = ""; try {
					for (var d = "span font a b strong i em strike u sup sub".split(" "), g = d.length, h = 0; h < g; h++) {
						var k = !0, l = d[h]; ("font" == l || "b" == l || "em" == l || "sup" == l || "sub" == l) && 0 > e.toLowerCase().indexOf("<" + l) && (k = !1);
						if (k) for (var n = 0; n < g; n++) {
							b = e; c = new RegExp("<" + d[h] + " *([^>?+])*>(\\s+)</" + d[n] + ">", "gi"); reg_exp2 = "u" == d[n] ? new RegExp("</" + d[h] + ">(\\s+)<" + d[n] + "[^l]", "gi") : new RegExp("</" + d[h] + ">(\\s+)<" + d[n] + " *([^>?+])*>", "gi"); reg_exp3 = new RegExp("<" + d[h] + " *([^>?+])*>(\\s+)<" + d[n] + ">", "gi"); reg_exp4 = "u" == d[n] ? new RegExp("</" + d[h] + ">(\\s+)</" + d[n] + "[^l]", "gi") : new RegExp("</" + d[h] + ">(\\s+)</" + d[n] + " *([^>?+])*>", "gi"); try {
								var m = e.match(c); if (m) for (var t = m.length, q = 0; q < t; q++) {
									var r = m[q]; if (!("b" == d[h] && -1 < r.toLowerCase().indexOf("<br"))) var f =
										/>\s+</.exec(r), u = f[0].replace(RegExp("\\s\\s", "gi"), "&nbsp;&nbsp;"), r = r.replace(f, u), e = e.replace(m[q], r)
								} if (m = e.match(reg_exp2)) for (t = m.length, q = 0; q < t; q++)r = m[q], "b" == d[n] && -1 < r.toLowerCase().indexOf("<br") || (f = />\s+</.exec(r), u = f[0].replace(RegExp("\\s\\s", "gi"), "&nbsp;&nbsp;"), r = r.replace(f, u), e = e.replace(m[q], r)); if (m = e.match(reg_exp3)) for (t = m.length, q = 0; q < t; q++)r = m[q], "b" == d[h] && -1 < r.toLowerCase().indexOf("<br") || (f = />\s+</.exec(r), u = f[0].replace(RegExp("\\s\\s", "gi"), "&nbsp;&nbsp;"), r = r.replace(f,
									u), e = e.replace(m[q], r)); if (m = e.match(reg_exp4)) for (t = m.length, q = 0; q < t; q++)r = m[q], f = />\s+</.exec(r), u = f[0].replace(RegExp("\\s\\s", "gi"), "&nbsp;&nbsp;"), r = r.replace(f, u), e = e.replace(m[q], r)
							} catch (v) { e = b }
						}
					} for (h = 0; h < g; h++)if (k = !0, l = d[h], ("font" == l || "b" == l || "em" == l || "sup" == l || "sub" == l) && 0 > b.toLowerCase().indexOf("<" + l) && (k = !1), k) {
						"b" == l ? (e = e.replace(/<br/gi, "<temp_br"), e = e.replace(/<\/br/gi, "</temp_br")) : "u" == l && (e = e.replace(/<ul/gi, "<temp_ul"), e = e.replace(/<\/ul/gi, "</temp_ul")); for (n = 0; n < g; n++)c = new RegExp("/" +
							d[h] + ">\\s<" + d[n], "gi"), e = e.replace(c, "/" + d[h] + ">&nbsp;<" + d[n]), c = new RegExp("/" + d[h] + ">\\s\\n<" + d[n], "gi"), e = e.replace(c, "/" + d[h] + ">&nbsp;<" + d[n]); "b" == l ? (e = e.replace(/<temp_br/gi, "<br"), e = e.replace(/<\/temp_br/gi, "</br")) : "u" == l && (e = e.replace(/<temp_ul/gi, "<ul"), e = e.replace(/<\/temp_ul/gi, "</ul"))
					}
				} catch (y) { e = a } return e
			}, parseIntOr0: function(a) { a = parseInt(a, 10); return isNaN(a) ? 0 : a }, parseFloatOr0: function(a) { a = parseFloat(a, 10); return isNaN(a) ? 0 : a }, getUserLang: function(a) {
				a = a.toLowerCase(); var e =
					"ko-kr"; "auto" == a && (e = "en-us", (a = navigator.language || navigator.userLanguage) || (a = e), a = a.toLowerCase()); -1 < a.indexOf("ko") ? e = "ko-kr" : -1 < a.indexOf("en") ? e = "en-us" : -1 < a.indexOf("ja") ? e = "ja-jp" : -1 < a.indexOf("zh-cn") || 0 == a.indexOf("cn") ? e = "zh-cn" : -1 < a.indexOf("zh-tw") || 0 == a.indexOf("tw") ? e = "zh-tw" : -1 < a.indexOf("vi") ? e = "vi-vn" : -1 < a.indexOf("ru") && (e = "ru-ru"); return e
			}, postFormData: function(a, e, c, b) {
				void 0 == b && (b = []); var d = a.createElement("form"); d.method = "post"; d.action = e; d.target = c; e = b.length; for (c = 0; c <
					e; c++) { var g = a.createElement("input"); g.type = "hidden"; g.name = b[c][0]; g.value = b[c][1]; d.appendChild(g) } a.body.appendChild(d); d.submit(); a.body.removeChild(d)
			}, base64_encode: function(a) {
				var e = "", c, b, d, g, h, k, l = 0; for (a = DEXT5.util.utf8_encode(a); l < a.length;)c = a.charCodeAt(l++), b = a.charCodeAt(l++), d = a.charCodeAt(l++), g = c >> 2, c = (c & 3) << 4 | b >> 4, h = (b & 15) << 2 | d >> 6, k = d & 63, isNaN(b) ? h = k = 64 : isNaN(d) && (k = 64), e = e + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(g) + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(c) +
					"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(h) + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(k); return e
			}, base64_decode: function(a, e) {
				var c = "", b, d, g, h, k, l = 0; for (a = a.replace(/[^A-Za-z0-9\+\/\=]/g, ""); l < a.length;)b = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(a.charAt(l++)), d = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(a.charAt(l++)), h = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(a.charAt(l++)),
					k = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(a.charAt(l++)), b = b << 2 | d >> 4, d = (d & 15) << 4 | h >> 2, g = (h & 3) << 6 | k, c += String.fromCharCode(b), 64 != h && (c += String.fromCharCode(d)), 64 != k && (c += String.fromCharCode(g)); 0 == !!e && (c = DEXT5.util.utf8_decode(c)); return c
			}, makeEncryptParam: function(a) { a = DEXT5.util.base64_encode(a); a = "R" + a; a = DEXT5.util.base64_encode(a); return a = a.replace(/[+]/g, "%2B") }, makeEncryptParamEx: function(a) {
				a = DEXT5.util.base64_encode(a); 10 <= a.length ? (a = DEXT5.util.insertAt(a,
					DEXT5.config.G_AP10, DEXT5.util.G_AP27), a = DEXT5.util.insertAt(a, DEXT5.util.G_AP11, DEXT5.config.G_AP.G_AP22), a = DEXT5.util.insertAt(a, DEXT5.browser.G_AP12, DEXT5.util.G_AP25), a = DEXT5.util.insertAt(a, DEXT5.config.G_AP13, DEXT5.config.G_AP23), a = DEXT5.util.insertAt(a, DEXT5.config.G_AP10, DEXT5.config.G_AP.G_AP29), a = DEXT5.util.insertAt(a, DEXT5.util.G_AP11, DEXT5.browser.G_AP24), a = DEXT5.util.insertAt(a, DEXT5.browser.G_AP12, DEXT5.config.G_AP20)) : (a = DEXT5.util.insertAt(a, a.length - 1, "$"), a = DEXT5.util.insertAt(a,
						0, "$")); return a = a.replace(/[+]/g, "%2B")
			}, makeEncryptParamEx2: function(a) { a = DEXT5.util.cipher_encode(a); return a = a.replace(/[+]/g, "%2B") }, makeEncryptParamEx3: function(a) {
				var e = DEXT5.util.makeGuid().toLowerCase(), e = DEXT5.util.encode(e), e = e.substring(0, 15), c = G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Utf8.parse(e); c.sigBytes = 16; a = G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Utf8.parse(a); a = G_CURREDITOR._FRAMEWIN.CryptoJS.AES.encrypt(a, c, { iv: c }); c = G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Base64._map; G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Base64._map =
					DEXT5._CK_.join(""); a = e + G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Base64.stringify(a.ciphertext); G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Base64._map = c; return a = a.replace(/[+]/g, "%2B")
			}, makeDecryptReponseMessage: function(a) { a = DEXT5.util.base64_decode(a); a = a.substring(1); return a = DEXT5.util.base64_decode(a) }, makeDecryptReponseMessageEx: function(a) {
				var e = function(a, b) { var d = a.split(""); d.splice(b, 1); return d = d.join("") }; a = a.replace(/ /g, ""); a = a.replace(/\r/g, ""); a = a.replace(/\n/g, ""); a = a.replace(/%2B/g, "+"); 15 <=
					a.length ? (a = e(a, 9), a = e(a, 6), a = e(a, 8), a = e(a, 7), a = e(a, 9), a = e(a, 6), a = e(a, 8)) : (a = a.replace(/#/g, ""), a = a.replace(/$/g, "")); return a = DEXT5.util.base64_decode(a)
			}, makeDecryptReponseMessageEx2: function(a) { a = a.replace(/%2B/g, "+"); return a = DEXT5.util.cipher_decode(a) }, makeDecryptReponseMessageEx3: function(a) {
				try {
					a = a.replace(/ /g, ""); a = a.replace(/\r/g, ""); a = a.replace(/\n/g, ""); a = a.replace(/%2B/g, "+"); var e = a.substring(0, 15); a = a.substring(15); e = G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Utf8.parse(e); e.sigBytes = 16;
					var c = G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Base64._map; G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Base64._map = DEXT5._CK_.join(""); a = G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Base64.parse(a); G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Base64._map = c; a = G_CURREDITOR._FRAMEWIN.CryptoJS.AES.decrypt({ ciphertext: a }, e, { iv: e }).toString(G_CURREDITOR._FRAMEWIN.CryptoJS.enc.Utf8)
				} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return a
			}, utf8_decode: function(a) {
				for (var e = "", c = 0, b = c1 = c2 = 0; c < a.length;)b = a.charCodeAt(c), 128 > b ? (e +=
					String.fromCharCode(b), c++) : 191 < b && 224 > b ? (c2 = a.charCodeAt(c + 1), e += String.fromCharCode((b & 31) << 6 | c2 & 63), c += 2) : (c2 = a.charCodeAt(c + 1), c3 = a.charCodeAt(c + 2), e += String.fromCharCode((b & 15) << 12 | (c2 & 63) << 6 | c3 & 63), c += 3); return e
			}, utf8_encode: function(a) {
				a = a.replace(/\r\n/g, "\n"); for (var e = "", c = 0; c < a.length; c++) {
					var b = a.charCodeAt(c); 128 > b ? e += String.fromCharCode(b) : (127 < b && 2048 > b ? e += String.fromCharCode(b >> 6 | 192) : (e += String.fromCharCode(b >> 12 | 224), e += String.fromCharCode(b >> 6 & 63 | 128)), e += String.fromCharCode(b &
						63 | 128))
				} return e
			}, encode: function(a) {
				var e = "", c, b, d, g, h, k, l = 0; for (a = DEXT5.util.utf8_encode(a); l < a.length;)c = a.charCodeAt(l++), b = a.charCodeAt(l++), d = a.charCodeAt(l++), g = c >> 2, c = (c & 3) << 4 | b >> 4, h = (b & 15) << 2 | d >> 6, k = d & 63, isNaN(b) ? h = k = 64 : isNaN(d) && (k = 64), e = e + "adebcfijghklopmnqruvstwyzxAHIJDBCEFLMNUVGKRSTOWXPQYZ0163847259+/=".charAt(g) + "adebcfijghklopmnqruvstwyzxAHIJDBCEFLMNUVGKRSTOWXPQYZ0163847259+/=".charAt(c) + "adebcfijghklopmnqruvstwyzxAHIJDBCEFLMNUVGKRSTOWXPQYZ0163847259+/=".charAt(h) + "adebcfijghklopmnqruvstwyzxAHIJDBCEFLMNUVGKRSTOWXPQYZ0163847259+/=".charAt(k);
				return e
			}, stringToXML: function(a) { a = DEXT5.util.trim(a); var e; try { window.DOMParser ? e = (new DOMParser).parseFromString(a, "text/xml") : (e = new ActiveXObject("Microsoft.XMLDOM"), e.async = "false", e.loadXML(a)) } catch (c) { e = null } return e }, xmlToString: function(a) { return window.ActiveXObject ? a.xml : (new XMLSerializer).serializeToString(a) }, removeOfficeDummyTag: function(a, e, c) {
				var b = a; try { for (var d = a.indexOf(e), g = a.indexOf(c); -1 < d && -1 < g;)var h = b.substring(0, d), k = b.substring(g + c.length), b = h + k, d = b.indexOf(e), g = b.indexOf(c) } catch (l) {
					b =
					a
				} return b
			}, getStyle: function(a, e) {
				var c, b = !1, d; "fontSize" == e ? G_CURREDITOR._FRAMEWIN._iframeWin.getComputedStyle ? (DEXTTOP.DEXT5.browser.ie && 11 == DEXTTOP.DEXT5.browser.ieVersion && G_CURREDITOR._config.defaultFontSize && -1 < G_CURREDITOR._config.defaultFontSize.indexOf("px") && (d = a.style.lineHeight, a.style.lineHeight = "1", b = !0), c = G_CURREDITOR._FRAMEWIN._iframeWin.getComputedStyle(a, "")) : a.currentStyle && (c = a.currentStyle) : a.currentStyle ? c = a.currentStyle : G_CURREDITOR._FRAMEWIN._iframeWin.getComputedStyle && (c =
					G_CURREDITOR._FRAMEWIN._iframeWin.getComputedStyle(a, "")); c ? "all" != e && (c.getProperty ? c = c.getProperty(e) : "fontSize" == e && b ? (c = c.lineHeight, void 0 != d && (a.style.lineHeight = d)) : c = c[e], "fontSize" == e && (0 > c.indexOf("px") && 0 > c.indexOf("pt") && 0 > c.indexOf("em") ? c = "" : -1 < c.indexOf("px") && (c = G_CURREDITOR._FRAMEWIN.getFontSizeStyle(c, "")))) : c = ""; return c
			}, hashTable: function(a) {
				this.length = 0; this.items = {}; for (var e in a) a.hasOwnProperty(e) && (this.items[e] = a[e], this.length++); this.setItem = function(a, b) {
					var d = void 0;
					this.hasItem(a) ? d = this.items[a] : this.length++; this.items[a] = b; return d
				}; this.getItem = function(a) { return this.hasItem(a) ? this.items[a] : void 0 }; this.hasItem = function(a) { return this.items.hasOwnProperty(a) }; this.removeItem = function(a) { if (this.hasItem(a)) return previous = this.items[a], this.length--, delete this.items[a], previous }; this.keys = function() { var a = [], b; for (b in this.items) this.hasItem(b) && a.push(b); return a }; this.values = function() {
					var a = [], b; for (b in this.items) this.hasItem(b) && a.push(this.items[b]);
					return a
				}; this.each = function(a) { for (var b in this.items) this.hasItem(b) && a(b, this.items[b]) }; this.clear = function() { this.items = {}; this.length = 0 }
			}, isExistEditorName: function(a) { if (void 0 == a || "" == a) return 1; var e = DEXT5.DEXTMULTIPLE["dext_frame_" + a]; return void 0 == e || null == e ? 0 : "" != DEXT5.config.EditorHolder && DEXT5.DEXTHOLDER[a] == DEXT5.config.EditorHolder ? 3 : 2 }, getNewNextEditorName: function(a) {
				var e = "", c = a.split("_"), b = 0; do e = c.length, 1 < e && (a = a.replace("_" + c[e - 1], "")), e = a + "_" + b, b++; while (0 < DEXT5.util.isExistEditorName(e));
				return e
			}, replacePtOrCmToPx: function(a, e) { try { for (var c = "object" == typeof e ? e : {}, b = a.toLowerCase().split(" "), d = b.length, g, h, k = "", l = 0; l < d; l++) { var n = b[l]; -1 < n.indexOf("pt") ? (g = 4 * parseFloat(n) / 3, 0 < g && 1 > g && (g = 1), 0 > g && (g = 0), h = !0 === c.isFloor ? Math.floor(g) + "px" : Math.round(g) + "px", k = "" != k ? k + " " + h : h) : -1 < n.indexOf("cm") ? (_cm = 37.795275593333 * parseFloat(n), 0 < _cm && 1 > _cm && (_cm = 1), 0 > _cm && (_cm = 0), h = !0 === c.isFloor ? Math.floor(_cm) + "px" : Math.round(_cm) + "px", k = "" != k ? k + " " + h : h) : k = "" != k ? k + " " + n : n } return "" != k ? k : a } catch (m) { return a } },
		GetUserRunTimeEditor: function(a) { a = a.toLowerCase(); var e = ""; "" == a || "html5" == a ? e = "html5" : "ieplugin" == a ? e = 1 == DEXT5.browser.ie && 12 > DEXT5.browser.ieVersion ? "ieplugin" : "html5" : -1 < a.indexOf("ieplugin") && 8 < a.length ? (a = DEXT5.util.parseIntOr0(a), e = 1 == DEXT5.browser.ie && a >= DEXT5.browser.ieVersion ? "ieplugin" : "html5") : e = "html5"; return e }, DEXT5_CheckEditorVisible: function(a) {
			a = document.getElementById("dext_frame_" + a); var e = !1; "undefined" != typeof a && (e = !(0 == a.offsetWidth && 0 == a.offsetHeight)) && (e = "hidden" != (window.getComputedStyle ?
				window.getComputedStyle(a, null) : a.currentStyle).visibility); return e
		}, DEXT5_IsCustomDomain: function(a) { if (!DEXT5.browser.ie) return !1; var e = a.domain; a = DEXT5.util.DEXT5_GetDocWindow(a).location.hostname; return e != a && e != "[" + a + "]" }, DEXT5_GetDocWindow: function(a) { return a.parentWindow || a.defaultView }, getUnitSize: function(a) { var e = 1; switch (a.toLowerCase()) { case "kb": e *= 1024; break; case "mb": e *= 1048576; break; case "gb": e *= 1073741824; break; case "tb": e *= 1099511627776 }return e }, getUnit: function(a) {
			a = a.toLowerCase();
			var e = ""; -1 < a.indexOf("tb") ? e = a.substring(a.indexOf("tb")) : -1 < a.indexOf("mb") ? e = a.substring(a.indexOf("mb")) : -1 < a.indexOf("gb") ? e = a.substring(a.indexOf("gb")) : -1 < a.indexOf("kb") ? e = a.substring(a.indexOf("kb")) : -1 < a.indexOf("b") && (e = a.substring(a.indexOf("b"))); return e
		}, bytesToSize: function(a) {
			a = parseInt(a, 10); var e = "0 byte"; isNaN(a) && (a = "", e = "N/A"); e = { size: 0, unit: "byte", toString: e }; if (0 == a) return e; var c = parseInt(Math.floor(Math.log(a) / Math.log(1024))); e.size = Math.round(a / Math.pow(1024, c) * 100, 2) /
				100; e.unit = ["bytes", "KB", "MB", "GB", "TB"][c]; e.toString = e.size + " " + e.unit; return e
		}, HtmlToText: function(a) {
			var e = a; try {
				var c = e.match(/<body[^>]*>([\w|\W]*)<\/body>/im); c && (e = c[1]); e = e.replace(/\r/g, ""); e = e.replace(/[\n|\t]/g, ""); e = e.replace(/[\v|\f]/g, ""); e = e.replace(/<p><br><\/p>/gi, "\n"); e = e.replace(/<P>&nbsp;<\/P>/gi, "\n"); "undefined" != typeof DEXT5 && DEXT5.browser.ie && 11 <= DEXT5.browser.ieVersion && (e = e.replace(/<br(\s)*\/?><\/p>/gi, "</p>"), e = e.replace(/<br(\s[^\/]*)?><\/p>/gi, "</p>")); var e = e.replace(/<br(\s)*\/?>/gi,
					"\n"), e = e.replace(/<br(\s[^\/]*)?>/gi, "\n"), e = e.replace(/<\/p(\s[^\/]*)?>/gi, "\n"), e = e.replace(/<\/li(\s[^\/]*)?>/gi, "\n"), e = e.replace(/<\/tr(\s[^\/]*)?>/gi, "\n"), b = e.lastIndexOf("\n"); -1 < b && "\n" == e.substring(b) && (e = e.substring(0, b)); e = e.replace(RegExp("</?[^>]*>", "gi"), ""); e = e.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&nbsp;/g, " ").replace(/&amp;/g, "&")
			} catch (d) { e = a } return e
		}, openModal: function(a, e, c) {
			if ("1" == c) {
				var b = a ? a : currWindow.document; DEXT5.util.closeModal(b); (function() {
					var a = b.createElement("div");
					b.body.appendChild(a); a.outerHTML = '<div id="dext_modal" style="position: absolute; top: 40%; left: 50%; transform: translateX(-50%); max-width: 70%; z-index: 20;" >   <div style="box-sizing: border-box; padding: 19px 25px; border: 1px solid rgba(255, 87, 87, .5); border-radius: 2px; background-color: #fff; box-shadow: 1px 1px 4px 0 rgba(255, 142, 117, .2);">       <p style="font-size:9pt;" >' + e + "</p >    </div></div>"
				})(); setTimeout(function() { DEXT5.util.closeModal(b) }, 3E3)
			}
		}, closeModal: function(a) {
			var e =
				a.getElementById("dext_modal"); e && a.body.removeChild(e)
		}, _getDEXT5editor: function(a) {
			var e = null, e = DEXT5.util.getEditorByName(a); if (void 0 == e || null == e) {
				if (-1 == location.href.indexOf("editor_container.html") && DEXT5.ShowDestroyAlert && ("undefined" == typeof DEXT5EditorDisableNotInitializedMessage || !DEXT5EditorDisableNotInitializedMessage)) {
					a = ""; try { if ("function" == typeof Error) { var c = Error(); "string" == typeof c.stack && (a = "\n\n" + c.stack) } } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } alert("Editor's Name is not correct, Please check editor's name. \ror\rThe editor was not initialized, Please check the location of api call" +
						a)
				} DEXT5.ShowDestroyAlert = !0; return null
			} return e
		}, getEditorByName: function(a) { var e = null; if (void 0 == a || "" == a) e = G_CURREDITOR; else { try { if ("1" == G_CURREDITOR._config.ignoreDifferentEditorName && 1 == DEXT5.DEXTMULTIPLEID.length) { var c = DEXT5.DEXTMULTIPLEID[0]; a != c && DEXT5.isLoadedEditorEx(c) && (a = c) } } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } e = DEXT5.DEXTMULTIPLE["dext_frame_" + a]; "object" != typeof e && (e = null) } return e }, _getEditorName: function(a) {
			if (void 0 == a || "" == a) {
				if (null != DEXT5.DEXTMULTIPLEID &&
					void 0 != DEXT5.DEXTMULTIPLEID && 1 == DEXT5.DEXTMULTIPLEID.length) return DEXT5.DEXTMULTIPLEID[0]; -1 == location.href.indexOf("editor_container.html") && DEXT5.ShowDestroyAlert && ("undefined" != typeof DEXT5EditorDisableNotInitializedMessage && DEXT5EditorDisableNotInitializedMessage || alert("Editor's Name is not correct, Please check editor's name. \ror\rThe editor was not initialized, Please check the location of api call")); DEXT5.ShowDestroyAlert = !0; return null
			} return a
		}, _setDEXT5editor: function(a) {
			a = DEXT5.util._getDEXT5editor(a);
			if (void 0 == a || null == a) return !1; "function" == typeof a._FRAMEWIN.setCurrentEditor && a._FRAMEWIN.setCurrentEditor(a); return a
		}, getValueByMultiMode: function() { switch (G_CURREDITOR._config.changeMultiValueMode) { case "doctype": return DEXT5.getHtmlValueExWithDocType(); case "htmlEx": return DEXT5.getHtmlValueEx(); case "html": return DEXT5.getHtmlValue(); case "bodyEx": return DEXT5.getBodyValueEx(); case "body": return DEXT5.getBodyValue() } }, setValueByMultiMode: function(a) {
			switch (G_CURREDITOR._config.changeMultiValueMode) {
				case "doctype": return DEXT5.setHtmlValueExWithDocType(a);
				case "htmlEx": return DEXT5.setHtmlValueEx(a); case "html": return DEXT5.setHtmlValue(a); case "bodyEx": return DEXT5.setBodyValueEx(a); case "body": return DEXT5.setBodyValue(a)
			}
		}, removeHtmlLangAttrDuplication: function(a) {
			var e = a; try { var c = a.match(/<html ([^>]+)>/i); if (c) { var b = c[0], d = /( lang+)=["']?((?:.(?!["']?\s+(?:\S+)=|[>"']))+.)["']?/i, g = b.match(RegExp("( lang+)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?", "gi")).length; if (1 < g) { for (var h = 1; h < g; h++)b = b.replace(d, ""); a = a.replace(c[0], b) } e = a } } catch (k) {
				DEXT5 &&
				DEXT5.logMode && console && console.log(k)
			} return e
		}, removeDefaultStyleDiv: function(a) { for (var e = a._DOC.getElementsByName("dext_div"), c = e.length; 0 < c;) { var b = removeCRLFChar(_iframeDoc.body.innerHTML), d = removeCRLFChar(e[c - 1].outerHTML), g = e[c - 1].getElementsByTagName("style")[0], e = removeCRLFChar(e[c - 1].innerHTML); g && (e = e.replace(removeCRLFChar(g.outerHTML), "")); b = b.replace(d, e); _iframeDoc.body.innerHTML = b; e = a._DOC.getElementsByName("dext_div"); c = e.length } }, setInLineDefaultStyle: function(a) {
			if ("2" == a._config.setDefaultStyle.value) {
				var e =
					["span", "font"], c = e.length, b = function(a, b) {
						if ("" == a.style.fontFamily) try { a.style.fontFamily = DEXTTOP.DEXT5.util.getStyle(a, "fontFamily") } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } if ("" == a.style.fontSize) {
							for (var d = !1, e = 1; 5 >= e; e++)if (null != G_CURREDITOR._FRAMEWIN.GetParentbyTagName(a, "h" + e)) { d = !0; break } if (0 == d) if ("font" == a.tagName.toLowerCase()) {
								if ("" == a.size) { e = DEXT5.util.getStyle(a, "fontSize"); try { isNaN(e) ? a.style.fontSize = e : a.size = e } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } } if ("" ==
									a.face) try { a.face = DEXT5.util.getStyle(a, "fontFamily") } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
							} else try { a.style.fontSize = DEXT5.util.getStyle(a, "fontSize") } catch (f) { DEXT5 && DEXT5.logMode && console && console.log(f) }
						} if ("" == a.style.lineHeight && "span" != a.tagName.toLowerCase()) if ("2" == b._config.setDefaultStyle.line_height_mode) {
							e = ""; try { e = DEXT5.util.getStyle(a, "lineHeight") } catch (u) { DEXT5 && DEXT5.logMode && console && console.log(u) } var d = -1 < b._config.defaultLineHeight.indexOf("px") ? !0 : !1, v = -1 <
								e.indexOf("px") ? !0 : !1; if (0 == DEXTTOP.DEXT5.browser.ie && 1 == v && 0 == d) { v = a.style.fontSize; if ("" == v) try { v = DEXT5.util.getStyle(a, "fontSize") } catch (y) { DEXT5 && DEXT5.logMode && console && console.log(y) } if ("" != v) { var d = -1 < v.indexOf("pt") ? !0 : !1, z = -1 < v.indexOf("px") ? !0 : !1; if (d || z) v = parseFloat(v), d && (v *= 4 / 3), d = -1 < b._config.defaultLineHeight.indexOf("%") ? !0 : !1, e = parseFloat(e) / v, e = Math.round(10 * e), e = 1 * e / 10, d && (e = 100 * e + "%") } } "" != e && (a.style.lineHeight = e)
						} else a.style.lineHeight = b._setting.line_height && "" != b._setting.line_height ?
							b._setting.line_height : b._config.defaultLineHeight; 0 != a.tagName.toLowerCase().indexOf("h") && "span" != a.tagName.toLowerCase() && ("" == a.style.marginTop && (a.style.marginTop = b._config.defaultFontMarginTop), "" == a.style.marginBottom && (a.style.marginBottom = b._config.defaultFontMarginBottom)); d = b._config.setDefaultUserStyle.length; for (e = 0; e < d; e++)if ("" == a.style[b._config.setDefaultUserStyle[e]]) try { a.style[b._config.setDefaultUserStyle[e]] = DEXT5.util.getStyle(a, b._config.setDefaultUserStyle[e]) } catch (w) {
								DEXT5 &&
								DEXT5.logMode && console && console.log(w)
							}
					}; if (document.createTreeWalker) (function(c) { var d; for (c = document.createTreeWalker(c, NodeFilter.SHOW_TEXT, null, !1); d = c.nextNode();) { var g = d.nodeValue, g = g.replace(/\n/g, ""), g = g.replace(/\t/g, ""); "" != g && (d = d.parentNode) && d.tagName && (g = d.tagName.toLowerCase(), -1 < ("," + e.join(",") + ",").indexOf("," + g + ",") && b(d, a)) } })(a._BODY); else for (var d = 0; d < c; d++)for (var g = a._DOC.getElementsByTagName(e[d]), h = g.length, h = h - 1; 0 <= h; h--)b(g[h], a); e = "li p h1 h2 h3 h4 h5 div".split(" ");
				c = e.length; for (d = 0; d < c; d++)for (g = a._DOC.getElementsByTagName(e[d]), h = g.length, --h; 0 <= h; h--)b(g[h], a)
			}
		}, postimageToServer: function(a, e, c, b) {
			var d = !1; b && !0 === b.base64ImageToServer && (d = !0); if (G_CURREDITOR._FRAMEWIN.G_dext5plugIn && "0" == e) {
				try {
					G_CURREDITOR._FRAMEWIN.G_dext5plugIn.postPageURL = c.post_url, G_CURREDITOR._FRAMEWIN.G_dext5plugIn.serverDomain = c.serverDomain, G_CURREDITOR._FRAMEWIN.G_dext5plugIn.toSavePathUrl = c.toSavePathURL, G_CURREDITOR._FRAMEWIN.G_dext5plugIn.sFileDataID = c.fileFieldID, G_CURREDITOR._FRAMEWIN.G_dext5plugIn.sUserFieldID =
						c.userFieldID, G_CURREDITOR._FRAMEWIN.G_dext5plugIn.sUserFieldValue = c.userFieldValue, G_CURREDITOR._FRAMEWIN.G_dext5plugIn.saveFolderNameRule = c.saveFolderNameRule
				} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } var h = ["img", "table", "td", "th"], k = h.length; b = []; for (e = 0; e < k; e++)b.push(a.body.getElementsByTagName(h[e])); b.push(Array(a.body)); h = b.length; k = []; for (e = 0; e < h; e++)for (var l = b[e], n = l.length, m = 0; m < n; m++) {
					var t = l[m], q = ""; if ("img" == t.tagName.toLowerCase()) {
						if (t.getAttribute("raon_chart")) continue;
						q = t.src
					} else q = t.style.backgroundImage, "none" == q && (q = ""), "" == q && t.getAttribute("background") && (q = t.getAttribute("background")), q = q.replace('url("', "").replace('")', ""), q = q.replace("url('", "").replace("')", ""), q = q.replace("url(", "").replace(")", ""); var q = q.replace(/\%20/g, " "), q = q.replace(/\%27/g, "'"), q = q.replace(/\%28/g, "("), q = q.replace(/\%29/g, ")"), r = ""; if (-1 < q.toLowerCase().indexOf("http:") || -1 < q.toLowerCase().indexOf("https:")) {
						if ("1" == c.replaceOutsideImage.use) {
							var f = q.replace("http://", "").replace("https://",
								"").split("/")[0], u = !0, v = G_CURREDITOR._config.replaceOutsideImage.exceptDomain, y = v.length, z = G_CURREDITOR._config.replaceOutsideImage.targetDomain, w = z.length; if (0 < w) for (var u = !1, A = 0; A < w; A++) { if (-1 < f.indexOf(z[A])) { u = !0; break } } else if (0 < y) for (u = !0, A = 0; A < y; A++)if (-1 < f.indexOf(v[A])) { u = !1; break } u && (r = G_CURREDITOR._FRAMEWIN.G_dext5plugIn.postImageToServerEx(q), r = G_CURREDITOR._FRAMEWIN.web_url_remove_char(r, !0))
						}
					} else if ("upload" == G_CURREDITOR._config.uploadMethod) r = G_CURREDITOR._FRAMEWIN.G_dext5plugIn.postImageToServer(q),
						r = G_CURREDITOR._FRAMEWIN.web_url_remove_char(r, !0); else try { r = G_CURREDITOR._FRAMEWIN.G_dext5plugIn.getBase64ImageFromLocalImage(q) } catch (D) { DEXT5 && DEXT5.logMode && console && console.log(D) } if (r && "" != r) {
							r = r.replace(/\r\n/g, ""); r = r.replace(/\n/g, ""); r = r.replace(/\t/g, ""); try {
								var C; C = "function" == typeof G_CURREDITOR._config.event.beforeInsertUrl ? G_CURREDITOR._config.event.beforeInsertUrl(DEXTTOP.G_CURREDITOR.ID, r) : DEXTTOP.DEXTWIN.dext_editor_before_insert_url_event(DEXTTOP.G_CURREDITOR.ID, r); null != C && void 0 !=
									C && (r = C)
							} catch (F) { DEXT5 && DEXT5.logMode && console && console.log(F) }
						} if ("[FAIL]" == r.substring(0, 6)) try { k.push(r) } catch (I) { DEXT5 && DEXT5.logMode && console && console.log(I) } if (r && "" != r && (q = [], "" != r && "0" != DEXTTOP.G_CURREDITOR._config.imageCustomPropertyDelimiter && (q = r.split(DEXTTOP.G_CURREDITOR._config.imageCustomPropertyDelimiter), 1 < q.length && (r = q[0])), f = r.split("?"), 2 == f.length && (r = f[0], f = f[1].split("^"), 1 == f.length ? r += "?" + f[0] : 2 != f.length && 3 == f.length && (r += "?" + f[2])), "img" == t.tagName.toLowerCase() ? ("[FAIL]" ==
							r.substring(0, 6) ? (t.setAttribute("src", DEXTTOP.G_CURREDITOR._config.webPath.image + "dialog/image_xbox.jpg"), t.setAttribute("alt", r.substring(6, r.length).replace(/"/g, "'"))) : t.src = r, t.getAttribute("border") && "" != t.getAttribute("border") && 0 != t.getAttribute("border") && (t.style.border = t.getAttribute("border") + "px solid currentColor")) : "" != r && (t.style.backgroundImage = "url(" + r + ")", t.removeAttribute("background")), q && 1 < q.length)) for (r = q.length, e = 1; e < r; e++)f = q[e].split("^"), 2 == f.length && t.setAttribute(f[0],
								f[1])
				} if (0 < k.length) try { if ("function" == typeof G_CURREDITOR._config.event.onError) G_CURREDITOR._config.event.onError(DEXTTOP.G_CURREDITOR.ID, { type: "imageUpload", message: k[0], arrFileError: k }); else DEXTTOP.DEXTWIN.dext_editor_on_error_event(DEXTTOP.G_CURREDITOR.ID, { type: "imageUpload", message: k[0], arrFileError: k }) } catch (P) { DEXT5 && DEXT5.logMode && console && console.log(P) }
			} if ("upload" == G_CURREDITOR._config.uploadMethod || d) for (a = a.body.getElementsByTagName("img"), c = a.length, C = G_CURREDITOR._FRAMEWIN, e = 0; e <
				c; e++)if (a[e].getAttribute("raon_chart") || d) b = a[e].src, "data:" == b.substring(0, 5) && (b = b.split(","), 2 == b.length && (h = C.getExtFromImageDataSrc(b[0]), C.postBase64ImageToServer(b[1], h, a[e], !1, void 0, void 0, void 0, d)))
		}, setBodyBackground: function(a) {
			a._PageProp.bshowgrid && 1 == a._PageProp.bshowgrid || "1" == a._config.horizontalLine.use && -1 < G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundImage.indexOf(a._config.horizontalLine.url[0]) || ("" != G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundImage ? a._PageProp.bodyimage =
				G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundImage : G_CURREDITOR._FRAMEWIN._iframeDoc.body.getAttribute("background") && (a._PageProp.bodyimage = G_CURREDITOR._FRAMEWIN._iframeDoc.body.getAttribute("background").replace(/\\/gi, "/")), "" != G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundColor && (a._PageProp.bodycolor = G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundColor), "" != G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundAttachment && (a._PageProp.bodyattachment = G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundAttachment),
				"" != G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundRepeat && (a._PageProp.bodyrepeat = G_CURREDITOR._FRAMEWIN._iframeDoc.body.style.backgroundRepeat))
		}, checkForOverlap: function(a, e) { var c = a.getBoundingClientRect(), b = e.getBoundingClientRect(), d = c.left <= b.left; return (d ? c : b).right > (d ? b : c).left ? (d = c.top <= b.top, (d ? c : b).bottom > (d ? b : c).top) : !1 }, leadingZeros: function(a, e) { var c = ""; a = a.toString(); if (a.length < e) for (i = 0; i < e - a.length; i++)c += "0"; return c + a }, removeDuplicatesArray: function(a) { if (!a) return null; for (var e = {}, c = [], b = 0; b < a.length; b++)a[b] in e || (c.push(a[b]), e[a[b]] = !0); return c }, jsonToString: function(a) { return DEXTTOP.G_CURREDITOR._FRAMEWIN.JSON.stringify(a) },
		stringToJson: function(a) { return DEXTTOP.G_CURREDITOR._FRAMEWIN.JSON.parse(a) }, saveJsonToLocalStorage: function(a, e) { var c = !0, b = DEXT5.util.jsonToString(e); try { window.localStorage.setItem(a, b) } catch (d) { c = !1 } return c }, loadJsonFromLocalStorage: function(a) { a = window.localStorage[a]; var e = null; a && (e = DEXT5.util.stringToJson(a)); return e }, isEmptyContents: function(a) {
			var e = !1, c = "", b = ""; if (a) {
				if (3 == a.nodeType) c = a.textContent, b = ""; else if (1 == a.nodeType) {
					if (c = a.innerText, b = a.innerHTML, -1 < ",TABLE,IMG,A,IFRAME,HR,VIDEO,OBJECT,EMBED,INPUT,BUTTON,FIGURE,TEXTAREA,BLOCKQUOTE,".indexOf("," +
						a.nodeName + ",")) return e
				} else "string" === typeof a && (b = c = a); c = c.replace(unescape("%u200B"), ""); c = c.replace(unescape("%uFEFF"), ""); b = b.replace(unescape("%u200B"), ""); b = b.replace(unescape("%uFEFF"), ""); "" == c && 0 == /<table|<img|<a|<iframe|<hr|<video|<object|<embed/ig.test(b) && (e = !0)
			} return e
		}, insertAt: function(a, e, c) { return String.prototype.insertAt ? a.insertAt(e, c) : a.substr(0, e) + c + a.substr(e) }, cipher_decode: function(a) {
			var e = DEXT5._CK_.toString(), e = e.replace(/,/gi, ""), c = "", b, d, g, h, k, l = 0; for (a = a.replace(/[^A-Za-z0-9\+\/\=]/g,
				""); l < a.length;)b = e.indexOf(a.charAt(l++)), d = e.indexOf(a.charAt(l++)), h = e.indexOf(a.charAt(l++)), k = e.indexOf(a.charAt(l++)), b = b << 2 | d >> 4, d = (d & 15) << 4 | h >> 2, g = (h & 3) << 6 | k, c += String.fromCharCode(b), 64 != h && (c += String.fromCharCode(d)), 64 != k && (c += String.fromCharCode(g)); return c = DEXT5.util.utf8_decode(c)
		}, cipher_encode: function(a) {
			var e = DEXT5._CK_.toString(), e = e.replace(/,/gi, ""), c = "", b, d, g, h, k, l, n = 0; for (a = DEXT5.util.utf8_encode(a); n < a.length;)b = a.charCodeAt(n++), d = a.charCodeAt(n++), g = a.charCodeAt(n++), h =
				b >> 2, b = (b & 3) << 4 | d >> 4, k = (d & 15) << 2 | g >> 6, l = g & 63, isNaN(d) ? k = l = 64 : isNaN(g) && (l = 64), c = c + e.charAt(h) + e.charAt(b) + e.charAt(k) + e.charAt(l); return c
		}, makeEncryptParamFinal: function(a) { var e = "", c = DEXTTOP.G_CURREDITOR._config.security.encryptParam; "1" == c ? e = DEXTTOP.DEXT5.util.makeEncryptParam(a) : "2" == c ? e = DEXTTOP.DEXT5.util.makeEncryptParamEx(a) : "3" == c && (e = DEXTTOP.DEXT5.util.makeEncryptParamEx2(a)); return e }, makeDecryptReponseMessageFinal: function(a) {
			var e = DEXTTOP.G_CURREDITOR._config.security.encryptParam; "1" ==
				e ? a = DEXTTOP.DEXT5.util.makeDecryptReponseMessage(a) : "2" == e ? a = DEXTTOP.DEXT5.util.makeDecryptReponseMessageEx(a) : "3" == e && (a = DEXTTOP.DEXT5.util.makeDecryptReponseMessageEx2(a)); return a
		}, replaceHyFont: function(a, e) { return a }, dataURItoBlob: function(a) { var e = atob(a.split(",")[1]); a = a.split(",")[0].split(":")[1].split(";")[0]; for (var c = new ArrayBuffer(e.length), b = new Uint8Array(c), d = 0; d < e.length; d++)b[d] = e.charCodeAt(d); e = new DataView(c); return new Blob([e.buffer], { type: a }) }, makeGuidTagName: function(a) {
			var e =
				0, c = (new Date).getTime().toString(32), b; if ("undefined" != typeof crypto && "undefined" != typeof crypto.randomUUID) c += crypto.randomUUID().replace(/-/g, "").substring(0, 20); else for (b = 0; 5 > b; b++)c += Math.floor(65535 * Math.random()).toString(32); return (a || "o_") + c + (e++).toString(32)
		}, makeGuid: function(a) {
			var e = ""; "undefined" != typeof crypto && "undefined" != typeof crypto.randomUUID ? e = crypto.randomUUID() : (e = function() { return (65536 * (1 + Math.random()) | 0).toString(16).substring(1) }, e = (e() + e() + "-" + e() + "-" + e() + "-" + e() + "-" +
				e() + e() + e()).toUpperCase()); void 0 != a && (e = a + "-" + e); return e
		}, checkUrlForContentSizeCheckEvent: function(a) {
			var e = !1, c = !1, b = !1; "undefined" != typeof DEXT5.config.ContentSizeChangeEvent && ("undefined" != typeof DEXT5.config.ContentSizeChangeEvent.IncludeExternalUrl && "1" == DEXT5.config.ContentSizeChangeEvent.IncludeExternalUrl && (c = !0), "undefined" != typeof DEXT5.config.ContentSizeChangeEvent.IncludeRelevantUrlString && "" != DEXT5.config.ContentSizeChangeEvent.IncludeRelevantUrlString && (b = !0)); c ? e = !0 : (c = a, "0" == c.indexOf("/") ||
				0 == c.indexOf("file:") ? e = !0 : (c = c.replace(/http:\/\//, ""), c = c.replace(/https:\/\//, ""), -1 < c.indexOf("/") && (c = c.substr(0, c.indexOf("/")), -1 < location.href.indexOf(c) && (e = !0)))); b && (e = -1 < a.indexOf(DEXT5.config.ContentSizeChangeEvent.IncludeRelevantUrlString) ? !0 : !1); 0 == a.indexOf("file://") && (e = !0); return e
		}, removeDomainFromIMGObject: function(a) {
			try {
				var e = window.location.href.substring(0, window.location.href.indexOf(window.location.pathname)), c = a.getElementsByTagName("img"); a = 0; for (var b = c.length, d; a < b; a++)d =
					!0, "undefined" != typeof DEXT5.config.ServerDomainStartWithUrl && 0 != c[a].src.indexOf(DEXT5.config.ServerDomainStartWithUrl) && (d = !1), 0 == c[a].src.indexOf(e) && d && (c[a].src = c[a].src.substr(e.length))
			} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
		}, getDocType: function(a) {
			var e = ""; if (a && a.doctype) try { var c = a.doctype, e = "<!DOCTYPE " + c.name + (c.publicId ? ' PUBLIC "' + c.publicId + '"' : "") + (!c.publicId && c.systemId ? " SYSTEM" : "") + (c.systemId ? ' "' + c.systemId + '"' : "") + ">" } catch (b) {
				DEXT5 && DEXT5.logMode && console &&
				console.log(b)
			} return e
		}, hexToBytes: function(a) { for (var e = [], c = a.length / 2, b = 0, b = 0; b < c; b++)e.push(parseInt(a.substr(2 * b, 2), 16)); return e }, bytesToBase64: function(a) { for (var e = "", c = a.length, b = 0; b < c; b += 3) { var d = a.slice(b, b + 3), g = d.length, h = [], k = void 0; if (3 > g) for (k = g; 3 > k; k++)d[k] = 0; h[0] = (252 & d[0]) >> 2; h[1] = (3 & d[0]) << 4 | d[1] >> 4; h[2] = (15 & d[1]) << 2 | (192 & d[2]) >> 6; h[3] = 63 & d[2]; for (k = 0; 4 > k; k++)e += k <= g ? "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(h[k]) : "=" } return e }, escapeHtml: function(a) {
			var e =
				null; try { e = DEXTTOP.G_CURREDITOR._FRAMEWIN.document.createElement("div") } catch (c) { e = document.createElement("div") } void 0 != e.innerText ? e.innerText = a : e.textContent = a; return e.innerHTML
		}, setProtocolBaseDomainURL: function(a) { var e = "", e = "/" == a.substring(0, 1) ? location.protocol + "//" + location.host : 4 < a.length && ("http" == a.substring(0, 4).toLowerCase() || "file:" == a.substring(0, 4).toLowerCase()) ? "" : DEXT5.rootPath; return e + a }, setAddHttpHeader: function(a, e) {
			try { for (var c in e) a.setRequestHeader(c, e[c]) } catch (b) {
				DEXT5 &&
				DEXT5.logMode && console && console.log(b)
			}
		}, getWordBreakStyle: function(a) { var e = "normal"; a = a.wordBreakType; "1" == a ? e = "break-all" : "2" == a && (e = "keep-all"); return e }, getWordWrapStyle: function(a) { var e = "normal", c = a.wordWrapType; if ("1" == a.autoBodyFit || "1" == c) e = "break-word"; return e }, adjustBorderStyle: function(a, e) {
			try {
				var c = function(a) {
					for (var b = ["borderTop", "borderRight", "borderBottom", "borderLeft"], c = b.length, d = 0; d < c; d++) {
						var e = !1, g = a.style[b[d]].toLowerCase(); "" != g && (-1 < g.indexOf("currentcolor") ? e = !0 : 2 ==
							g.split(" ").length && "currentcolor" == a.style[b[d] + "Color"].toLowerCase() && (e = !0)); if (e) { var h = g = e = "", e = DEXT5.util.getStyle(a, b[d] + "Color"), g = DEXT5.util.getStyle(a, b[d] + "Style"), h = DEXT5.util.getStyle(a, b[d] + "Width"); a.style[b[d]] = g + " " + h + " " + e }
					}
				}; if (a) c(a); else for (var b = e._DOC.getElementsByTagName("*"), d = b.length, g = 0; g < d; g++)c(b[g])
			} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
		}, getTimeStamp: function() { var a = "", a = this.makeGuid(); return a = a.replace(/-/g, "") }, arrayIndexOf: function(a, e) {
			if (a.indexOf) return a.indexOf(e);
			for (var c = -1, b = a.length, d = 0; d < b; d++)if (a[d] == e) { c = d; break } return c
		}, parseSetApiParam: function(a) { var e = { html: "", caretPos: 0 }; switch (typeof a) { case "string": e.html = a; break; case "object": e = a }return e }, overrideFn: function(a) { try { top.document.location.hostname != window.document.location.hostname && (a.alert = function(a) { top.alert(a) }, a.confirm = function(a) { return top.confirm(a) }) } catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } }, removeStyleAttribute: function(a, e) {
			null != a && (a.style.removeProperty ? a.style.removeProperty(e) :
				a.style.removeAttribute(e))
		}, isPlainObject: function(a) {
			var e; e = {}; for (var c = "Boolean Number String Function Array Date RegExp Object Error Symbol".split(" "), b = 0, d = c.length; b < d; b++)e["[object " + c[b] + "]"] = c[b].toLowerCase(); c = e.hasOwnProperty; e = !0; DEXT5.browser.ie && 9 > DEXT5.browser.ieVersion && (e = !1); var g; if (!a || "object" !== typeof a || a.nodeType || null != a && a == a.window) return !1; try { if (a.constructor && !c.call(a, "constructor") && !c.call(a.constructor.prototype, "isPrototypeOf")) return !1 } catch (h) { return !1 } if (!e) for (g in a) return c.call(a,
				g); for (g in a); return void 0 === g || c.call(a, g)
		}, objectExtend: function() {
			var a, e, c, b, d, g = arguments[0] || {}, h = 1, k = arguments.length, l = !1; "boolean" === typeof g && (l = g, g = arguments[h] || {}, h++); "object" !== typeof g && "function" !== typeof g && (g = {}); h === k && (g = this, h--); for (; h < k; h++)if (null != (d = arguments[h])) for (b in d) a = g[b], c = d[b], g !== c && (l && c && (this.isPlainObject(c) || (e = "array" === typeof c)) ? (e ? (e = !1, a = a && "array" === typeof a ? a : []) : a = a && this.isPlainObject(a) ? a : {}, g[b] = this.objectExtend(l, a, c)) : void 0 !== c && (g[b] = c));
			return g
		}, cmToMm: function(a) { return 10 * a }, mmToPx: function(a) { return 3.7795275591 * a }, convertStringtoBoolean: function(a) { try { switch ((a + "").toLowerCase().trim()) { case "yes": case "true": case "1": return !0; case "no": case "false": case "0": case null: return !1; default: return Boolean(a) } } catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } }, stringTypeIsEmpty: function(a) { return "string" == typeof a ? "" !== a ? !1 : !0 : !0 }, getEditSpacePx: function(a) {
			var e = 0, c = 0; "1" == a._config.editorborder && (e = 2); "1" == a._config.defaultBodySpace.use &&
				(c = a._config.defaultBodySpace.value.split(","), c = 4 == c.length ? DEXTTOP.DEXT5.util.parseIntOr0(c[1]) + DEXTTOP.DEXT5.util.parseIntOr0(c[3]) : 2 * DEXTTOP.DEXT5.util.parseIntOr0(c[0])); return DEXTTOP.DEXT5.util.parseIntOr0(a._FRAMEWIN.innerWidth) - e - c
		}, compareVersion: function(a, e) { for (var c = 0, b = a.replace(/,/g, "."), d = e.replace(/,/g, "."), b = b.split("."), d = d.split("."), g = b.length, h = 0; h < g; h++) { var k = parseInt(b[h], 10), l = parseInt(d[h], 10); if (k != l) { c = k > l ? 1 : -1; break } } return c }, getExtensionFromFileName: function(a) {
			var e =
				""; a && (e = a.split(".")); a = ""; 1 < e.length && (a = e[e.length - 1]); return a
		}, getMimeFilter: function(a) { var e = ""; if ("string" == typeof a) e = a.split(",").join(",."), "" != e && (e = "." + e); else if ("object" == typeof a) for (var c = 0; c < a.length; c++)e = c == a.length - 1 ? "." + e + a[c] : e + (a[c] + ",."); return e }, replaceQuotInStyle: function(a) {
			var e = a; try { e = e.replace(/(<\w{1,} .*?style=)"(.*?)"/gi, function(a, c, e) { var h = !1; -1 < e.indexOf("&quot;") && (h = !0); h && (e = e.replace(/&quot;/g, '"'), e = e.replace(/'/g, '"'), a = c + "'" + e + "'"); return a }) } catch (c) {
				e =
				a
			} return e
		}, addQueryStringToUrl: function(a, e, c) { var b = -1 < a.indexOf("?") ? "&" : "?"; return a + b + e + "=" + c }, setUrlForDocumentDomain: function(a, e) { var c = a, b = e.domain; e.location.hostname != b && (c = DEXT5.util.addQueryStringToUrl(c, "d", b)); return c }, makeRV: function(a) { var e = []; e.push(a.maj.toString()); e.push(parseInt(a.mi1.join(""), 10) * a.m1); e.push(a.mi2); e.push(a.l); return e.join(".") }, getRV: function(a) { return DEXT5.util.makeDecryptReponseMessageEx2(a) }, makeCRV: function(a) {
			var e = []; e.push(a.maj.toString()); e.push(parseInt(a.mi1));
			e.push(parseInt(a.mi2)); e.push(a.l); return e.join(",")
		}, splitBodyInnerString: function(a) { var e = "", c = "", b = ""; if (b = a.match(/<body.*?>/i)) { var b = b.index + b[0].length, e = a.substring(0, b), d = a.match(/<\/body>/i); d && (c = a.substring(d.index)); b = a.substring(b, d.index) } else b = a; return { head: e, body: b, tail: c } }
	}); DEXT5.getEditorByName = DEXT5.GetEditorByName = function(a) {
		var e = null; try { void 0 == a || "" == a ? e = G_CURREDITOR : DEXT5.isLoadedEditorEx(a) && (e = DEXT5.DEXTMULTIPLE["dext_frame_" + a]), void 0 == e && (e = null) } catch (c) {
			DEXT5 &&
			DEXT5.logMode && console && console.log(c)
		} return e
	}; DEXT5._CK_[14] = "m"; DEXT5.getEditor = DEXT5.GetEditor = function(a) { return DEXT5.util._getDEXT5editor(a) }; DEXT5._CK_[15] = "n"; DEXT5.setEditor = DEXT5.SetEditor = function(a) { return DEXT5.util._setDEXT5editor(a) }; DEXT5._CK_[16] = "q"; DEXT5.getDext5PluginVersion = DEXT5.GetDext5PluginVersion = function() { var a = "-1"; try { G_CURREDITOR._FRAMEWIN.G_dext5plugIn && (a = G_CURREDITOR._FRAMEWIN.G_dext5plugIn.getDext5PluginVersion()) } catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } return a };
	DEXT5._CK_[17] = "r"; DEXT5.setAccessibility = DEXT5.SetAccessibility = function(a, e) { try { var c = DEXT5.util.getEditorByName(e); !c || "0" != a && "1" != a && "2" != a || (c._config.accessibility = a + "") } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5._CK_[18] = "u"; DEXT5.getAccessibility = DEXT5.GetAccessibility = function(a) { var e = ""; try { var c = DEXT5.util.getEditorByName(a); c && (e = c._config.accessibility) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5._CK_[19] = "v"; DEXT5.setToSavePathUrl = DEXT5.SetToSavePathUrl =
		function(a, e) { try { var c = DEXT5.util.getEditorByName(e); c && (c._config.toSavePathURL = a) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5._CK_[20] = "s"; DEXT5.getToSavePathUrl = DEXT5.GetToSavePathUrl = function(a) { var e = ""; try { var c = DEXT5.util.getEditorByName(a); c && (e = c._config.toSavePathURL) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5._CK_[21] = "t"; DEXT5.setVisibility = DEXT5.SetVisibility = function(a, e) {
			try {
				var c = DEXT5.util.getEditorByName(e); c && (c._config.visibility =
					1 == a ? a : !1)
			} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
		}; DEXT5._CK_[22] = "w"; DEXT5.getVisibility = DEXT5.GetVisibility = function(a) { var e = ""; try { var c = DEXT5.util.getEditorByName(a); c && (e = c._config.visibility) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5._CK_[23] = "y"; DEXT5.show = DEXT5.Show = function(a) {
			try {
				var e = DEXT5.util._setDEXT5editor(a); if (e) {
					if (void 0 == a || "" == a) a = e.ID; var c = e._FRAMEWIN, b = DEXTDOC.getElementById("dext_frame_holder" + a); b && (DEXT5.setVisibility(!0, a),
						b.style.width = e._config.style.width, b.style.height = e._config.style.height, b.style.display = "", c.resizeEditor(null, !0), "1" == e._config.ruler.view && c.showRuler(e))
				}
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
		}; DEXT5._CK_[24] = "z"; DEXT5.hidden = DEXT5.Hidden = function(a) {
			try {
				var e = DEXT5.util._setDEXT5editor(a); if (e) {
					if (void 0 == a || "" == a) a = e.ID; var c = e._FRAMEWIN; DEXTDOC.getElementById("dext_context_iframe") && (DEXTDOC.getElementById("dext_context_iframe").style.display = "none", DEXTDOC.getElementById("dext_context_background") &&
						(DEXTDOC.getElementById("dext_context_background").style.display = "none", c.dialogCancel())); c.event_dext_upload_cancel(DEXTDOC.getElementById("dext_dialog")); c.hideTopMenuAndFocus(); var b = DEXTDOC.getElementById("dext_toolmenu_background" + a); b && (c.dialogCancel(), c.G_SUB_DIALOG && (c.isGroupingIcon(c.G_USE_EDITOR_ID, "", c.G_SUB_DIALOG) && c.hideGroupingBox(), c.G_SUB_DIALOG.style.display = "none"), c.G_CURRENT_IFRAME && (c.G_CURRENT_IFRAME.style.display = "none"), c.G_CURRENT_IFRAME_HOLDER && (c.G_CURRENT_IFRAME_HOLDER.style.display =
							"none"), b.style.display = "none"); var d = DEXTDOC.getElementById("dext_frame_holder" + a); d && (DEXT5.setVisibility(!1, a), d.style.height = "0px", d.style.display = "none", window.focus())
				}
			} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
		}; DEXT5._CK_[25] = "x"; DEXT5.setSize = DEXT5.SetSize = function(a, e, c) {
			try {
				if (1 == DEXT5.getVisibility(c)) {
					var b = DEXT5.util._setDEXT5editor(c); if (b) {
						var d = b._FRAMEWIN; if (void 0 == c || "" == c) c = b.ID; var g = DEXTDOC.getElementById("dext_frame_holder" + c), h = DEXTDOC.getElementById("dext_frame_" +
							c).contentWindow.document.getElementById("ue_editor_holder_" + c); 0 < DEXT5.util.parseIntOr0(a) && (-1 < a.toString().indexOf("%") || -1 < a.toString().indexOf("px") ? g.style.width = a : g.style.width = a + "px", b._config.style.width = g.style.width); 0 < DEXT5.util.parseIntOr0(e) && (-1 < e.toString().indexOf("%") ? g.style.height = e : (g.style.height = DEXT5.util.parseIntOr0(e) + "px", a = 0, d.isViewMode(b) ? (a = 2, 7 == DEXT5.browser.trident || 12 <= DEXT5.browser.ieVersion || !DEXT5.browser.quirks || (a = 0)) : a = b.baseMenuToolbarHeight, "0" == b._config.editorborder &&
								(a -= 2), b._defaultHeight = parseInt(e, 10) - a, h.style.height = b._defaultHeight + "px"), b._config.style.height = g.style.height); d.G_Ruler && d.G_Ruler.viewRulerStatus && "design" == b._currentMode && d.G_Ruler.SetRulerPosition(); d.groupingIcon(); d.G_BodyFit.widthFixStatus && (d.setBodyFitStyle(b, !0), d.setAutoBodyFit(b)); try { b.initSetSize ? b.initSetSize = !1 : b.isOccurredResizeEvent || (b.isOccurredResizeEvent = !0, "function" == typeof G_CURREDITOR._config.event.resized ? G_CURREDITOR._config.event.resized(b.Frame) : DEXTWIN.dext_editor_resized_event(b.Frame)) } catch (k) {
									DEXT5 &&
									DEXT5.logMode && console && console.log(k)
								}
					}
				}
			} catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) }
		}; DEXT5._CK_[26] = "A"; DEXT5.getImages = DEXT5.GetImages = function(a, e) {
			var c = ""; try {
				var b = DEXT5.util._setDEXT5editor(a); if (b) {
					var d = b._FRAMEWIN, g = b.getEditorMode(); "source" != g && "text" != g || b.setChangeMode("design"); d.ReplaceImageToRealObject(); 1 != e && d.changeBodyContenteditable(!1); d.changeBodyImageProperty(!0); for (var h = b._DOC.getElementsByTagName("img"), k = g = "", l = -1, n = h.length, m = 0; m < n; m++)g = h[m].getAttribute("src"),
						-1 < g.indexOf("data:image") ? k = "" : (l = g.lastIndexOf("/"), k = g.substring(l + 1)), c = "" == c ? g + b._config.unitAttributeDelimiter + k : c + b._config.unitDelimiter + g + b._config.unitAttributeDelimiter + k; d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0); d.changeBodyImageProperty(!1); d.ReplaceRealObjectToImage(); d.changeBodyContenteditable(!0)
				}
			} catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } return c
		}; DEXT5._CK_[27] = "H"; DEXT5.getImagesEx = DEXT5.GetImagesEx = function(a, e) {
			var c = ""; try {
				var b = DEXT5.util._setDEXT5editor(a);
				if (b) {
					var d = b._FRAMEWIN, g = b.getEditorMode(); "source" != g && "text" != g || b.setChangeMode("design"); d.ReplaceImageToRealObject(); 1 != e && d.changeBodyContenteditable(!1); d.changeBodyImageProperty(!0); var h = b._DOC.body.outerHTML, k = g = "", l = -1, n = RegExp("<img[^>]*src=(.*?)>", "gi"), m = h.match(RegExp("<[^>]*url\\((.*?)\\)", "gi")), t = h.match(n); if (m) for (var q = m.length, r = 0; r < q; r++)(g = m[r].match("url\\((.*?)\\)")[1].replace(/"/gi, "").replace(/'/gi, "").replace(/&quot;/gi, "")) && "" != g && (-1 < g.indexOf("data:image") ? k = "" : (l =
						g.lastIndexOf("/"), k = g.substring(l + 1)), c = "" == c ? g + b._config.unitAttributeDelimiter + k : c + b._config.unitDelimiter + g + b._config.unitAttributeDelimiter + k); if (t) for (var f = t.length, r = 0; r < f; r++)(g = t[r].match("src=\"(.*?)\"|src='(.*?)'")[1].replace(/"/gi, "").replace(/'/gi, "")) && "" != g && (-1 < g.indexOf("data:image") ? k = "" : (l = g.lastIndexOf("/"), k = g.substring(l + 1)), c = "" == c ? g + b._config.unitAttributeDelimiter + k : c + b._config.unitDelimiter + g + b._config.unitAttributeDelimiter + k); d.G_BodyFit.noncreationAreaBackgroundStatus &&
							d.setBodyFitStyle(b, !0); d.changeBodyImageProperty(!1); d.ReplaceRealObjectToImage(); d.changeBodyContenteditable(!0)
				}
			} catch (u) { DEXT5 && DEXT5.logMode && console && console.log(u) } return c
		}; DEXT5._CK_[28] = "I"; DEXT5.getContentsUrl = DEXT5.GetContentsUrl = function(a, e) {
			var c = ""; try {
				var b = DEXT5.util._setDEXT5editor(a); if (b) {
					var d = b._FRAMEWIN, g = b.getEditorMode(); "source" != g && "text" != g || b.setChangeMode("design"); d.ReplaceImageToRealObject(); 1 != e && d.changeBodyContenteditable(!1); d.changeBodyImageProperty(!0); var h =
						b._DOC.body.outerHTML, k = g = "", l = -1, n = RegExp("<[^>]*url\\((.*?)\\)", "gi"), m = RegExp("<img[^>]*src=(.*?)>", "gi"), t = RegExp("<embed[^>]*src=(.*?)>", "gi"), q = RegExp("<a[^>]*DEXT5InsertFile(.*?)>", "gi"); regExp5 = RegExp("<video[^>]*src=(.*?)>", "gi"); var r = h.match(n), f = h.match(m), u = h.match(t), v = h.match(q), y = h.match(regExp5); if (r) for (var z = r.length, w = 0; w < z; w++)(g = r[w].match("url\\((.*?)\\)")[1].replace(/"/gi, "").replace(/'/gi, "").replace(/&quot;/gi, "")) && "" != g && (l = g.lastIndexOf("/"), k = g.substring(l + 1), c = "" == c ?
							g + b._config.unitAttributeDelimiter + k : c + b._config.unitDelimiter + g + b._config.unitAttributeDelimiter + k); if (f) for (var A = f.length, w = 0; w < A; w++)(g = f[w].match("src=\"(.*?)\"|src='(.*?)'")[1].replace(/"/gi, "").replace(/'/gi, "")) && "" != g && (l = g.lastIndexOf("/"), k = g.substring(l + 1), c = "" == c ? g + b._config.unitAttributeDelimiter + k : c + b._config.unitDelimiter + g + b._config.unitAttributeDelimiter + k); if (u) for (A = u.length, w = 0; w < A; w++) {
								var g = "", D = u[w].match("src=\"(.*?)\"|src='(.*?)'"); null == D && (D = u[w].match("src=(.*?)[ >]"));
								D && (g = D[1].replace(/"/gi, "").replace(/'/gi, "")); g && "" != g && (l = g.lastIndexOf("/"), k = g.substring(l + 1), c = "" == c ? g + b._config.unitAttributeDelimiter + k : c + b._config.unitDelimiter + g + b._config.unitAttributeDelimiter + k)
							} if (v) for (A = v.length, w = 0; w < A; w++)(g = v[w].match("href=\"(.*?)\"|href='(.*?)'")[1].replace(/"/gi, "").replace(/'/gi, "")) && "" != g && (l = g.lastIndexOf("/"), k = g.substring(l + 1), c = "" == c ? g + b._config.unitAttributeDelimiter + k : c + b._config.unitDelimiter + g + b._config.unitAttributeDelimiter + k); if (y) for (var C = y.length,
								w = 0; w < C; w++)(g = y[w].match("src=\"(.*?)\"|src='(.*?)'")[1].replace(/"/gi, "").replace(/'/gi, "")) && "" != g && (l = g.lastIndexOf("/"), k = g.substring(l + 1), c = "" == c ? g + b._config.unitAttributeDelimiter + k : c + b._config.unitDelimiter + g + b._config.unitAttributeDelimiter + k); d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0); d.changeBodyImageProperty(!1); d.ReplaceRealObjectToImage(); d.changeBodyContenteditable(!0)
				}
			} catch (F) { DEXT5 && DEXT5.logMode && console && console.log(F) } return c
		}; DEXT5._CK_[29] = "J"; DEXT5.getServerImages =
			DEXT5.GetServerImages = function(a, e, c) {
				var b = ""; try {
					a = a.toLowerCase(); var d = DEXT5.util._setDEXT5editor(e); if (d) {
						var g = d._FRAMEWIN, h = d.getEditorMode(); "source" != h && "text" != h || d.setChangeMode("design"); g.ReplaceImageToRealObject(); 1 != c && g.changeBodyContenteditable(!1); g.changeBodyImageProperty(!0); var k = d._DOC.getElementsByTagName("img"); c = e = ""; for (var h = -1, l = k.length, n = 0; n < l; n++)e = k[n].src, -1 < e.toLowerCase().indexOf(a) && (h = e.lastIndexOf("/"), c = e.substring(h + 1), b = "" == b ? e + d._config.unitAttributeDelimiter +
							c : b + d._config.unitDelimiter + e + d._config.unitAttributeDelimiter + c); g.G_BodyFit.noncreationAreaBackgroundStatus && g.setBodyFitStyle(d, !0); d._PageProp.bshowgrid && 1 == d._PageProp.bshowgrid && g.changeBodyImageProperty(!1); g.ReplaceRealObjectToImage(); g.changeBodyContenteditable(!0)
					}
				} catch (m) { DEXT5 && DEXT5.logMode && console && console.log(m) } return b
			}; DEXT5._CK_[30] = "D"; DEXT5.setHtmlValueExWithDocType = DEXT5.SetHtmlValueExWithDocType = function(a, e) {
				var c = DEXT5.util.parseSetApiParam(a); a = c.html; if ("" == a || "" == DEXT5.util.trim(a)) DEXT5.setBodyValue("",
					e); else try {
						if (e = DEXT5.util._getEditorName(e), null != e) if (DEXT5.isLoadedEditorEx(e)) try {
							var b = DEXT5.util._setDEXT5editor(e); if (b) if ("lightview" == b._config.mode) DEXTTOP.G_CURREDITOR = b, b._FRAMEWIN.lightViewFunc("setHtmlValueExWithDocType", a, b); else {
								b.setValueIsBusy = !0; var d = b._FRAMEWIN; d.setDisableUndoRedo(a, b); d.getApplyDragResize(b) && b.dext_dragresize.resizeHandleClear(); a = d.addHtmlToSetValue(b, a); a = d.removeCarriageReturn(b, a); d.setChangeModeForSetApi(b); d.setGlobalTableDefaultValue(); b.UndoManager.reset();
								"1" == b._config.emptyTagRemoveInSetapi && (a = d.CleanZeroChar(a)); "CCODE_002" == b._config.custom_code && (a = a.replace(/<pstyle.*?>/gi, ""), a = a.replace(/<spanstyle.*?>/gi, "")); a = d.removeDummyTag(a); a = d.removeIncorrectSpaceInTag(a); a = d.DEXT5_EDITOR.HTMLParser.RemoveOfficeTag2(a); a = d.externalPageBreakDataRaplaceInEditor(a); var g = function(a) {
									"1" == b._config.useHtmlProcessByWorkerSetApi && (d.destoryWebWorkerVar(), d.hideProcessingBackground()); a = d.removeTagStyle(a); a = d.htmlAsciiToChar(a); a = DEXT5.util.htmlRevision(a);
									a = d.xssReplaceScript(null, a); b._config.userCssUrl && "" != b._config.userCssUrl && b._config.userCssAlwaysSet && "1" == b._config.userCssAlwaysSet && (a = d.userCssSet(a, b._config.userCssUrl)); b._config.webFontCssUrl && "" != b._config.webFontCssUrl && b._config.webFontCssAlwaysSet && "1" == b._config.webFontCssAlwaysSet && (a = d.userCssSet(a, b._config.webFontCssUrl)); a = d.adjustInputChecked(a); "1" == b._config.ie_BugFixed_Hyfont && (a = DEXT5.util.replaceHyFont(a, b)); a = d.replaceFontFamilyInHtml(a, b); "1" == b._config.replaceEmptySpanTagInSetapi &&
										(a = d.replaceEmptySpanTag(a)); d.command_InsertDogBGImg(b.ID, b._EDITOR.design, "Y", "", "", "", "", []); try { for (var e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onBeforeDocumentWrite) { var g = d.G_DEPlugin[e].onBeforeDocumentWrite({ html: a }); g && "string" == typeof g.html && (a = g.html) } } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } a = d.insertCarriageReturnBeforeCloseCell(a); a = d.removeEditorAttribute(a); d.setHtmlValueWithDocTypeToEditor(a, !0, b); d.replaceEmptySpaceInATag(b._BODY); try {
											for (e in d.G_DEPlugin) if ("function" ===
												typeof d.G_DEPlugin[e].onAfterDocumentWrite) d.G_DEPlugin[e].onAfterDocumentWrite("SetHtmlValueExWithDocType")
										} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } "1" == b._config.compatibility.dingBatCharSetApi && d.dingBatFont("", b); d.replaceFontTagToSpan(b); "1" == b._config.setDefaultStyle.removeBodyStyleInSet && (b._BODY.removeAttribute("style"), "" != b._BODY.className && (b._BODY.className = "")); "" != b._config.placeholder.content && d.placeholderControl(b, "set"); "0px" == b._BODY.style.width && (b._BODY.style.removeProperty ?
											b._BODY.style.removeProperty("width") : b._BODY.style.removeAttribute("width")); "1" == b._config.removeEmptyTagSetValue && d.setEmptyTagWhiteSpace(b); "0" != b._config.setDefaultStyle.value && "0" != b._config.setDefaultStyle.dext_set_style && b._BODY.id != b._config.setDefaultStyle.body_id && (b._BODY.id = b._config.setDefaultStyle.body_id); d.G_BodyFit.SetBodyWidth(); b._config.zoomList && 0 < b._config.zoomList.length && d.command_zoom(b.ID, d.document.getElementById("dext5_design_" + b.ID)); "2" == b._config.olUlTagMode && d.adjustOlUlTag(b._DOC);
									setTimeout(function() { for (var a = d._iframeDoc.getElementsByTagName("input"), c = a.length, e = 0; e < c; e++)"radio" == a[e].type && null != a[e].getAttribute("dext5checked") && (a[e].checked = !0, a[e].setAttribute("checked", "checked"), a[e].removeAttribute("dext5checked")); d.adjustInputNodeForFF(b._DOC, !0) }, 10); 0 == d.isViewMode(b) && (b._dextCustomDataMode = !0, "1" == b._config.formMode ? (d.ReplaceRealEventToCustomData(), d.ReplaceRealObjectToImage(!1)) : d.ReplaceRealObjectToImage()); d.xssReplaceScript(d._iframeDoc); d.setScrollOverflow(b);
									d.setStyleForTableBorderNodeClass(b); d.setCssForFormMode(b); "1" == b._config.adjustCurrentColorInSetApi && DEXT5.util.adjustBorderStyle(null, b); b.ShowTableBorder && (b.ShowTableBorder = !1); b._iconEnable(""); 0 == d.isViewMode(b) && d.setBodyDefaultValue(); d.wrapPtagForNotBlockTag(b); d.removeEmptySpanBRTag(b._BODY); d.replaceBrTag(b); d.fn_IEJASOBug(b); d.removeLastBrTag(b); 0 == d.isViewMode(b) && (d.removeNbspInPTag(b), d.replaceClassForBorder(b, b._BODY, "show"), d.replaceClassForBookmark(b, b._BODY, "show")); DEXT5.util.setBodyBackground(b);
									0 == d.isViewMode(b) && "2" != b._config.undoMode && (b.UndoManager.putUndo(), b.UndoManager.charCount = 0, b.UndoManager.canUndo = !1, b.UndoManager.canRedo = !1); b._iconEnable(""); d.insertImageSrc(b); d.setClassTableAndCellLock(b); d.set_view_mode_auto_height(b); "1" == b._config.tableAutoAdjustInSetHtml && d.command_AdjustTableAndCellWidth(b.ID, b, { type: "setHtml" }); d.setAdjustTableBorder(b._DOC); "show_blocks" == G_CURREDITOR.ShowBlocks && (G_CURREDITOR.ShowBlocks = "", d.command_showBlocks(b.ID, b)); d.G_Ruler && d.G_Ruler.SetRulerPosition();
									d.setAutoBodyFit(b); d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0); DEXT5.browser.ie || "1" != b._config.useTableDiagonal ? d.deleteDiagonalLine(null, null, b) : (d.G_DEXT_Diagonal.setResizeForDiagonal(), d.G_DEXT_Diagonal.util.replaceEmptySpaceInDiagonal()); setTimeout(function() {
										if (d.setFocusToBody(b)) d.setFocusChildForStyle(b._BODY, c.caretPos); else {
											var a = null; "" != b._config.focusInitObjId ? a = DEXTTOP.DEXTDOC.getElementById(b._config.focusInitObjId) : b.autoMoveInitFocusData.targetNode && (a =
												b.autoMoveInitFocusData.targetNode); null != a ? a.focus() : DEXT5.browser.ie && (DEXTTOP.focus(), DEXTTOP.document.focus(), DEXTTOP.document.body.focus())
										}
									}, 1); setTimeout(function() {
										try {
											d.adjustScroll(b); d.command_BeforeSetCompleteSpellCheck(b); d.g_findRepalceRange = null; try { "" != b._config.focusInitEditorObjId && (DEXT5.setFocusToObject(b._config.focusInitEditorObjId, !1, b.ID), b._config.focusInitEditorObjId = "") } catch (a) { DEXT5 && DEXT5.logMode && console.log(a) } try {
												"function" == typeof b._config.event.setComplete ? b._config.event.setComplete(b.ID) :
												DEXTTOP.dext_editor_set_complete_event(b.ID)
											} catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } try { d.contentChangeEventWrapper() } catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } try { d.onChange({ editor: b }) } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } b.UndoManager.reset(); "2" == b._config.undoMode && b.UndoManager.putUndo(!0); b.setValueIsBusy = !1
										} catch (h) { d.restoreValueInSetError(b) }
									}, 300)
								}; "1" == b._config.useHtmlCorrection ? "1" == b._config.useHtmlProcessByWorkerSetApi ? (d.showProcessingBackground(),
									d.releaseProcessHtmlWorker(), d.fn_processHtmlWorker({ editorBrowser: { ie: DEXT5.browser.ie, ieVersion: DEXT5.browser.ieVersion, gecko: DEXT5.browser.gecko }, editorConfig: b._config, callFn: "htmlTagRevision", callFnParam: [a, !1], callBackFn: g })) : (a = d.htmlTagRevision(a, !1), g(a)) : g(a)
							}
						} catch (h) { d.restoreValueInSetError(b) } else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setHtmlValueExWithDocType", value: c })
					} catch (k) {
						DEXT5 &&
						DEXT5.logMode && console && console.log(k)
					}
			}; DEXT5._CK_[31] = "B"; DEXT5.getHtmlValueExWithDocType = DEXT5.GetHtmlValueExWithDocType = function(a, e, c) {
				var b = ""; try {
					var d = DEXT5.util._setDEXT5editor(a); if (d) {
						0 != !!e || "undefined" != typeof c && 1 != c || d.UndoManager.reset(); var g = d._FRAMEWIN; if (1 == e) return g.getHTMLForAutoSave(d, "doctype"); try { for (var h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onBeforeGetApi) g.G_DEPlugin[h].onBeforeGetApi({ targetDoc: d._DOC }) } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } g.beforeGetApi(d);
						var l = d.getEditorMode(); "source" != l && "text" != l || d.setChangeMode("design"); g.checkDefaultMessage(); g.clearAllFormControlSelected(); g.ReplaceBase64ImageToArray(d._config, d._FRAMEWIN._iframeDoc.body); g.setRemoveClass(["td", "th"], ["DEXT_dot"]); g.replaceClassForBorder(d, d._BODY, "hidden"); g.replaceClassForBookmark(d, d._BODY, "hidden"); g.ReplaceImageToRealObject(); g.xssReplaceScript(g._iframeDoc); g.ClearDraggingTableAllTable(); "1" == d._config.formMode && g.ReplaceCustomDataToRealEvent(); var n, m; if (1 == e) try {
							DEXT5.browser.ie ?
							(n = Math.max(g._iframeDoc.documentElement.scrollLeft, g._iframeDoc.body.scrollLeft), m = Math.max(g._iframeDoc.documentElement.scrollTop, g._iframeDoc.body.scrollTop)) : (n = g._iframeWin.scrollX, m = g._iframeWin.scrollY)
						} catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } 1 != e && g.changeBodyContenteditable(!1); g.changeBodyImageProperty(!0); try { DEXT5.util.postimageToServer(d._DOC, d._config.mimeUse, d._config) } catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) } 1 != e && (g.removeEmptySpanBRTag(d._BODY), "1" ==
							d._config.wrapPtagToGetApi && g.wrapPtagForNotBlockTag(d), g.removeFakeLineHeight(d._BODY), DEXT5.util.setInLineDefaultStyle(d)); g.setMatchSelectedValue(d._BODY); g.setMatchInputValue(d._BODY, e); g.adjustInputNodeForFF(d._DOC, !1); 1 != e && g.removeScrollStyleForIOS(d); g.setEmptyTagWhiteSpace(d); l = c = a = ""; d._BODY.style.transformOrigin && "" != d._BODY.style.transformOrigin && (a = d._BODY.style.transformOrigin, d._BODY.style.transformOrigin = ""); d._BODY.style.transform && "" != d._BODY.style.transform && (c = d._BODY.style.transform,
								d._BODY.style.transform = ""); d._BODY.style.zoom && "" != d._BODY.style.zoom && (l = d._BODY.style.zoom, d._BODY.style.zoom = ""); "" != d._config.placeholder.content && g.placeholderControl(d, "remove"); "1" == d._config.useTableDiagonal && "0" == d._config.showDiagonalInIEViewPage && g.G_DEXT_Diagonal && g.G_DEXT_Diagonal.findDiagonal() && g.G_DEXT_Diagonal.addStyleForIE(); b = d._DOC.documentElement.outerHTML; "" != d._config.placeholder.content && g.placeholderControl(d, "set"); "" != a && (d._BODY.style.transformOrigin = a); "" != c && (d._BODY.style.transform =
									c); "" != l && (d._BODY.style.zoom = l); b = DEXT5.util.removeHtmlLangAttrDuplication(b); b = g.dummyTagClassClear(b); d._PageProp.bshowgrid && 1 == d._PageProp.bshowgrid && g.changeBodyImageProperty(!1); "1" == d._config.formMode ? (g.ReplaceRealEventToCustomData(), g.ReplaceRealObjectToImage(!1)) : g.ReplaceRealObjectToImage(); g.changeBodyContenteditable(!0); if (1 == e) try { g._iframeWin.scroll(n, m) } catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) } void 0 != d._PageProp.doctype && 0 < d._docType[d._PageProp.doctype].length && (b =
										d._docType[d._PageProp.doctype] + b); 1 == DEXT5.browser.ie && (b = DEXT5.util.htmlToLowerCase(b)); b = g.RemoveUnnecessaryChar(b); b = g.CleanZeroChar(b); n = !1; "1" == d._config.replaceEmptyTagWithSpace && (n = !0); b = DEXT5.util.htmlRevision(b, n); "1" == d._config.xhtml_value && (b = DEXT5.util.html2xhtml(b)); b = g.removeEditorCss(b, d); g.setAddClass(["td", "th"], ["DEXT_dot"]); 0 == g.isViewMode(d) && (g.replaceClassForBorder(d, d._BODY, "show"), g.replaceClassForBookmark(d, d._BODY, "show")); b = g.replaceLineBreak(d, b); b = g.insertCarriageReturn(d,
											b); b = g.ReplaceArrayToBase64Image(d._config, d._FRAMEWIN._iframeDoc.body, b); g.setScrollStyleForIOS(d); g.G_Ruler && g.G_Ruler.SetRulerPosition(); "" != d._config.placeholder.content && g.placeholderControl(d, "class"); "1" == d._config.replaceRgbToHex && (b = g.replaceColorRgbToHex(b)); b = g.replaceMsStyleName(b, d); g.G_DEPlugin.webfontloader && "1" == g.G_DEPlugin.webfontloader.config.uselocalstorage && (b = g.G_DEPlugin.webfontloader.removeBase64WebFontFromHead(b)); try {
												for (h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onAfterGetApi) {
													var f =
														g.G_DEPlugin[h].onAfterGetApi({ isAuto: e, html: b }); "string" == typeof f && (b = f)
												}
											} catch (u) { DEXT5 && DEXT5.logMode && console && console.log(u) } b = g.removeDomainFromIMGObjectInVirtualDom(d, b); g.afterGetApi(d)
					}
				} catch (v) { DEXT5 && DEXT5.logMode && console && console.log(v) } return b
			}; DEXT5._CK_[32] = "C"; DEXT5.setHtmlValueEx = DEXT5.SetHtmlValueEx = function(a, e) {
				var c = DEXT5.util.parseSetApiParam(a); a = c.html; if ("" == a || "" == DEXT5.util.trim(a)) DEXT5.setBodyValue("", e); else try {
					if (e = DEXT5.util._getEditorName(e), null != e) if (DEXT5.isLoadedEditorEx(e)) try {
						var b =
							DEXT5.util._setDEXT5editor(e); if (b) if ("lightview" == b._config.mode) DEXTTOP.G_CURREDITOR = b, b._FRAMEWIN.lightViewFunc("setHtmlValueEx", a, b); else {
								b.setValueIsBusy = !0; var d = b._FRAMEWIN; d.setDisableUndoRedo(a, b); d.getApplyDragResize(b) && b.dext_dragresize.resizeHandleClear(); a = d.addHtmlToSetValue(b, a); a = d.removeCarriageReturn(b, a); d.setChangeModeForSetApi(b); d.setGlobalTableDefaultValue(); b.UndoManager.reset(); "1" == b._config.emptyTagRemoveInSetapi && (a = d.CleanZeroChar(a)); "CCODE_002" == b._config.custom_code &&
									(a = a.replace(/<pstyle.*?>/gi, ""), a = a.replace(/<spanstyle.*?>/gi, "")); a = d.removeDummyTag(a); a = d.removeIncorrectSpaceInTag(a); a = d.DEXT5_EDITOR.HTMLParser.RemoveOfficeTag2(a); a = d.externalPageBreakDataRaplaceInEditor(a); var g = function(a) {
										"1" == b._config.useHtmlProcessByWorkerSetApi && (d.destoryWebWorkerVar(), d.hideProcessingBackground()); a = d.removeTagStyle(a); a = d.htmlAsciiToChar(a); a = DEXT5.util.htmlRevision(a); a = d.xssReplaceScript(null, a); b._config.userCssUrl && "" != b._config.userCssUrl && b._config.userCssAlwaysSet &&
											"1" == b._config.userCssAlwaysSet && (a = d.userCssSet(a, b._config.userCssUrl)); b._config.webFontCssUrl && "" != b._config.webFontCssUrl && b._config.webFontCssAlwaysSet && "1" == b._config.webFontCssAlwaysSet && (a = d.userCssSet(a, b._config.webFontCssUrl)); a = d.adjustInputChecked(a); "1" == b._config.ie_BugFixed_Hyfont && (a = DEXT5.util.replaceHyFont(a, b)); a = d.replaceFontFamilyInHtml(a, b); "1" == b._config.replaceEmptySpanTagInSetapi && (a = d.replaceEmptySpanTag(a)); d.command_InsertDogBGImg(b.ID, b._EDITOR.design, "Y", "", "", "", "",
												[]); try { for (var e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onBeforeDocumentWrite) { var g = d.G_DEPlugin[e].onBeforeDocumentWrite({ html: a }); g && "string" == typeof g.html && (a = g.html) } } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } a = d.insertCarriageReturnBeforeCloseCell(a); a = d.removeEditorAttribute(a); d.setHtmlValueToEditor(a, !0, b); d.replaceEmptySpaceInATag(b._BODY); try { for (e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onAfterDocumentWrite) d.G_DEPlugin[e].onAfterDocumentWrite("SetHtmlValueEx") } catch (k) {
													DEXT5 &&
													DEXT5.logMode && console && console.log(k)
												} "1" == b._config.compatibility.dingBatCharSetApi && d.dingBatFont("", b); d.replaceFontTagToSpan(b); "1" == b._config.setDefaultStyle.removeBodyStyleInSet && (b._BODY.removeAttribute("style"), "" != b._BODY.className && (b._BODY.className = "")); "" != b._config.placeholder.content && d.placeholderControl(b, "set"); "0px" == b._BODY.style.width && (b._BODY.style.removeProperty ? b._BODY.style.removeProperty("width") : b._BODY.style.removeAttribute("width")); "1" == b._config.removeEmptyTagSetValue &&
													d.setEmptyTagWhiteSpace(b); "0" != b._config.setDefaultStyle.value && "0" != b._config.setDefaultStyle.dext_set_style && b._BODY.id != b._config.setDefaultStyle.body_id && (b._BODY.id = b._config.setDefaultStyle.body_id); d.G_BodyFit.SetBodyWidth(); b._config.zoomList && 0 < b._config.zoomList.length && d.command_zoom(b.ID, d.document.getElementById("dext5_design_" + b.ID)); "2" == b._config.olUlTagMode && d.adjustOlUlTag(b._DOC); setTimeout(function() {
														for (var a = d._iframeDoc.getElementsByTagName("input"), c = a.length, e = 0; e < c; e++)"radio" ==
															a[e].type && null != a[e].getAttribute("dext5checked") && (a[e].checked = !0, a[e].setAttribute("checked", "checked"), a[e].removeAttribute("dext5checked")); d.adjustInputNodeForFF(b._DOC, !0)
													}, 10); 0 == d.isViewMode(b) && (b._dextCustomDataMode = !0, "1" == b._config.formMode ? (d.ReplaceRealEventToCustomData(), d.ReplaceRealObjectToImage(!1)) : d.ReplaceRealObjectToImage()); d.xssReplaceScript(d._iframeDoc); d.setScrollOverflow(b); d.setStyleForTableBorderNodeClass(b); d.setCssForFormMode(b); "1" == b._config.adjustCurrentColorInSetApi &&
														DEXT5.util.adjustBorderStyle(null, b); b.ShowTableBorder && (b.ShowTableBorder = !1); b._iconEnable(""); 0 == d.isViewMode(b) && d.setBodyDefaultValue(); d.wrapPtagForNotBlockTag(b); d.removeEmptySpanBRTag(b._BODY); d.replaceBrTag(b); d.fn_IEJASOBug(b); d.removeLastBrTag(b); 0 == d.isViewMode(b) && (d.removeNbspInPTag(b), d.replaceClassForBorder(b, b._BODY, "show"), d.replaceClassForBookmark(b, b._BODY, "show")); DEXT5.util.setBodyBackground(b); 0 == d.isViewMode(b) && "2" != b._config.undoMode && (b.UndoManager.putUndo(), b.UndoManager.charCount =
															0, b.UndoManager.canUndo = !1, b.UndoManager.canRedo = !1); b._iconEnable(""); d.insertImageSrc(b); d.setClassTableAndCellLock(b); d.set_view_mode_auto_height(b); "1" == b._config.tableAutoAdjustInSetHtml && d.command_AdjustTableAndCellWidth(b.ID, b, { type: "setHtml" }); d.setAdjustTableBorder(b._DOC); "show_blocks" == G_CURREDITOR.ShowBlocks && (G_CURREDITOR.ShowBlocks = "", d.command_showBlocks(b.ID, b)); d.G_Ruler && d.G_Ruler.SetRulerPosition(); d.setAutoBodyFit(b); d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b,
																!0); DEXT5.browser.ie || "1" != b._config.useTableDiagonal ? d.deleteDiagonalLine(null, null, b) : (d.G_DEXT_Diagonal.setResizeForDiagonal(), d.G_DEXT_Diagonal.util.replaceEmptySpaceInDiagonal()); setTimeout(function() {
																	if (d.setFocusToBody(b)) d.setFocusChildForStyle(b._BODY, c.caretPos); else {
																		var a = null; "" != b._config.focusInitObjId ? a = DEXTTOP.DEXTDOC.getElementById(b._config.focusInitObjId) : b.autoMoveInitFocusData.targetNode && (a = b.autoMoveInitFocusData.targetNode); null != a ? a.focus() : DEXT5.browser.ie && (DEXTTOP.focus(),
																			DEXTTOP.document.focus(), DEXTTOP.document.body.focus())
																	}
																}, 1); setTimeout(function() {
																	try {
																		d.adjustScroll(b); d.command_BeforeSetCompleteSpellCheck(b); d.g_findRepalceRange = null; try { "" != b._config.focusInitEditorObjId && (DEXT5.setFocusToObject(b._config.focusInitEditorObjId, !1, b.ID), b._config.focusInitEditorObjId = "") } catch (a) { DEXT5 && DEXT5.logMode && console.log(a) } try { "function" == typeof b._config.event.setComplete ? b._config.event.setComplete(b.ID) : DEXTTOP.dext_editor_set_complete_event(b.ID) } catch (c) {
																			DEXT5 &&
																			DEXT5.logMode && console && console.log(c)
																		} try { d.contentChangeEventWrapper() } catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } try { d.onChange({ editor: b }) } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } b.UndoManager.reset(); "2" == b._config.undoMode && b.UndoManager.putUndo(!0); b.setValueIsBusy = !1
																	} catch (h) { d.restoreValueInSetError(b) }
																}, 300)
									}; "1" == b._config.useHtmlCorrection ? "1" == b._config.useHtmlProcessByWorkerSetApi ? (d.showProcessingBackground(), d.releaseProcessHtmlWorker(), d.fn_processHtmlWorker({
										editorBrowser: {
											ie: DEXT5.browser.ie,
											ieVersion: DEXT5.browser.ieVersion, gecko: DEXT5.browser.gecko
										}, editorConfig: b._config, callFn: "htmlTagRevision", callFnParam: [a, !1], callBackFn: g
									})) : (a = d.htmlTagRevision(a, !1), g(a)) : g(a)
							}
					} catch (h) { d.restoreValueInSetError(b) } else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setHtmlValueEx", value: c })
				} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) }
			}; DEXT5._CK_[33] = "E"; DEXT5.getHtmlValueEx = DEXT5.GetHtmlValueEx =
				function(a, e, c) {
					var b = ""; try {
						var d = DEXT5.util._setDEXT5editor(a); if (d) {
							0 != !!e || "undefined" != typeof c && 1 != c || d.UndoManager.reset(); var g = d._FRAMEWIN; if (1 == e) return g.getHTMLForAutoSave(d, "htmlEx"); try { for (var h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onBeforeGetApi) g.G_DEPlugin[h].onBeforeGetApi({ targetDoc: d._DOC }) } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } g.beforeGetApi(d); var l = d.getEditorMode(); "source" != l && "text" != l || d.setChangeMode("design"); g.checkDefaultMessage();
							g.clearAllFormControlSelected(); g.ReplaceBase64ImageToArray(d._config, d._FRAMEWIN._iframeDoc.body); g.setRemoveClass(["td", "th"], ["DEXT_dot"]); g.replaceClassForBorder(d, d._BODY, "hidden"); g.replaceClassForBookmark(d, d._BODY, "hidden"); g.ReplaceImageToRealObject(); g.xssReplaceScript(g._iframeDoc); g.ClearDraggingTableAllTable(); "1" == d._config.formMode && g.ReplaceCustomDataToRealEvent(); var n, m; if (1 == e) try {
								DEXT5.browser.ie ? (n = Math.max(g._iframeDoc.documentElement.scrollLeft, g._iframeDoc.body.scrollLeft),
									m = Math.max(g._iframeDoc.documentElement.scrollTop, g._iframeDoc.body.scrollTop)) : (n = g._iframeWin.scrollX, m = g._iframeWin.scrollY)
							} catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } 1 != e && g.changeBodyContenteditable(!1); g.changeBodyImageProperty(!0); try { DEXT5.util.postimageToServer(d._DOC, d._config.mimeUse, d._config) } catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) } 1 != e && (g.removeEmptySpanBRTag(d._BODY), "1" == d._config.wrapPtagToGetApi && g.wrapPtagForNotBlockTag(d), g.removeFakeLineHeight(d._BODY),
								DEXT5.util.setInLineDefaultStyle(d)); g.setMatchSelectedValue(d._BODY); g.setMatchInputValue(d._BODY, e); g.adjustInputNodeForFF(d._DOC, !1); 1 != e && g.removeScrollStyleForIOS(d); g.setEmptyTagWhiteSpace(d); l = c = a = ""; d._BODY.style.transformOrigin && "" != d._BODY.style.transformOrigin && (a = d._BODY.style.transformOrigin, d._BODY.style.transformOrigin = ""); d._BODY.style.transform && "" != d._BODY.style.transform && (c = d._BODY.style.transform, d._BODY.style.transform = ""); d._BODY.style.zoom && "" != d._BODY.style.zoom && (l = d._BODY.style.zoom,
									d._BODY.style.zoom = ""); "" != d._config.placeholder.content && g.placeholderControl(d, "remove"); "1" == d._config.useTableDiagonal && "0" == d._config.showDiagonalInIEViewPage && g.G_DEXT_Diagonal && g.G_DEXT_Diagonal.findDiagonal() && g.G_DEXT_Diagonal.addStyleForIE(); b = d._DOC.documentElement.outerHTML; "" != d._config.placeholder.content && g.placeholderControl(d, "set"); "" != a && (d._BODY.style.transformOrigin = a); "" != c && (d._BODY.style.transform = c); "" != l && (d._BODY.style.zoom = l); b = DEXT5.util.removeHtmlLangAttrDuplication(b);
							b = g.dummyTagClassClear(b); d._PageProp.bshowgrid && 1 == d._PageProp.bshowgrid && g.changeBodyImageProperty(!1); "1" == d._config.formMode ? (g.ReplaceRealEventToCustomData(), g.ReplaceRealObjectToImage(!1)) : g.ReplaceRealObjectToImage(); g.changeBodyContenteditable(!0); if (1 == e) try { g._iframeWin.scroll(n, m) } catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) } 1 == DEXT5.browser.ie && (b = DEXT5.util.htmlToLowerCase(b)); b = g.RemoveUnnecessaryChar(b); b = g.CleanZeroChar(b); n = !1; "1" == d._config.replaceEmptyTagWithSpace && (n =
								!0); b = DEXT5.util.htmlRevision(b, n); "1" == d._config.xhtml_value && (b = DEXT5.util.html2xhtml(b)); b = g.removeEditorCss(b, d); g.setAddClass(["td", "th"], ["DEXT_dot"]); 0 == g.isViewMode(d) && (g.replaceClassForBorder(d, d._BODY, "show"), g.replaceClassForBookmark(d, d._BODY, "show")); b = g.replaceLineBreak(d, b); b = g.insertCarriageReturn(d, b); b = g.ReplaceArrayToBase64Image(d._config, d._FRAMEWIN._iframeDoc.body, b); g.setScrollStyleForIOS(d); g.G_Ruler && g.G_Ruler.SetRulerPosition(); "" != d._config.placeholder.content && g.placeholderControl(d,
									"class"); "1" == d._config.replaceRgbToHex && (b = g.replaceColorRgbToHex(b)); b = g.replaceMsStyleName(b, d); g.G_DEPlugin.webfontloader && "1" == g.G_DEPlugin.webfontloader.config.uselocalstorage && (b = g.G_DEPlugin.webfontloader.removeBase64WebFontFromHead(b)); try { for (h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onAfterGetApi) { var f = g.G_DEPlugin[h].onAfterGetApi({ isAuto: e, html: b }); "string" == typeof f && (b = f) } } catch (u) { DEXT5 && DEXT5.logMode && console && console.log(u) } b = g.removeDomainFromIMGObjectInVirtualDom(d,
										b); g.afterGetApi(d)
						}
					} catch (v) { DEXT5 && DEXT5.logMode && console && console.log(v) } return b
				}; DEXT5._CK_[34] = "F"; DEXT5.setHtmlValue = DEXT5.SetHtmlValue = function(a, e) {
					var c = DEXT5.util.parseSetApiParam(a); a = c.html; if ("" == a || "" == DEXT5.util.trim(a)) DEXT5.setBodyValue("", e); else try {
						if (e = DEXT5.util._getEditorName(e), null != e) if (DEXT5.isLoadedEditorEx(e)) try {
							var b = DEXT5.util._setDEXT5editor(e); if (b) if ("lightview" == b._config.mode) DEXTTOP.G_CURREDITOR = b, b._FRAMEWIN.lightViewFunc("setHtmlValue", a, b); else {
								b.setValueIsBusy =
								!0; var d = b._FRAMEWIN; d.setDisableUndoRedo(a, b); d.getApplyDragResize(b) && b.dext_dragresize.resizeHandleClear(); a = d.addHtmlToSetValue(b, a); a = d.removeCarriageReturn(b, a); d.setChangeModeForSetApi(b); d.setGlobalTableDefaultValue(); b.UndoManager.reset(); "1" == b._config.emptyTagRemoveInSetapi && (a = d.CleanZeroChar(a)); "CCODE_002" == b._config.custom_code && (a = a.replace(/<pstyle.*?>/gi, ""), a = a.replace(/<spanstyle.*?>/gi, "")); a = d.removeDummyTag(a); a = d.removeIncorrectSpaceInTag(a); a = d.DEXT5_EDITOR.HTMLParser.RemoveOfficeTag2(a);
								a = d.externalPageBreakDataRaplaceInEditor(a); var g = function(a) {
									"1" == b._config.useHtmlProcessByWorkerSetApi && (d.destoryWebWorkerVar(), d.hideProcessingBackground()); a = d.removeTagStyle(a); a = d.htmlAsciiToChar(a); a = DEXT5.util.htmlRevision(a); a = d.xssReplaceScript(null, a); b._config.userCssUrl && "" != b._config.userCssUrl && b._config.userCssAlwaysSet && "1" == b._config.userCssAlwaysSet && (a = d.userCssSet(a, b._config.userCssUrl)); b._config.webFontCssUrl && "" != b._config.webFontCssUrl && b._config.webFontCssAlwaysSet && "1" ==
										b._config.webFontCssAlwaysSet && (a = d.userCssSet(a, b._config.webFontCssUrl)); a = d.adjustInputChecked(a); "1" == b._config.ie_BugFixed_Hyfont && (a = DEXT5.util.replaceHyFont(a, b)); a = d.replaceFontFamilyInHtml(a, b); "1" == b._config.replaceEmptySpanTagInSetapi && (a = d.replaceEmptySpanTag(a)); d.command_InsertDogBGImg(b.ID, b._EDITOR.design, "Y", "", "", "", "", []); try {
											for (var e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onBeforeDocumentWrite) {
												var g = d.G_DEPlugin[e].onBeforeDocumentWrite({ html: a }); g && "string" ==
													typeof g.html && (a = g.html)
											}
										} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } a = d.insertCarriageReturnBeforeCloseCell(a); a = d.removeEditorAttribute(a); d.setHeadValueToEditor(a, b); d.replaceEmptySpaceInATag(b._BODY); try { for (e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onAfterDocumentWrite) d.G_DEPlugin[e].onAfterDocumentWrite("SetHtmlValue") } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } "1" == b._config.compatibility.dingBatCharSetApi && d.dingBatFont("", b); d.replaceFontTagToSpan(b);
									"1" == b._config.setDefaultStyle.removeBodyStyleInSet && (b._BODY.removeAttribute("style"), "" != b._BODY.className && (b._BODY.className = "")); "" != b._config.placeholder.content && d.placeholderControl(b, "set"); "0px" == b._BODY.style.width && (b._BODY.style.removeProperty ? b._BODY.style.removeProperty("width") : b._BODY.style.removeAttribute("width")); "1" == b._config.removeEmptyTagSetValue && d.setEmptyTagWhiteSpace(b); "0" != b._config.setDefaultStyle.value && "0" != b._config.setDefaultStyle.dext_set_style && b._BODY.id != b._config.setDefaultStyle.body_id &&
										(b._BODY.id = b._config.setDefaultStyle.body_id); d.G_BodyFit.SetBodyWidth(); b._config.zoomList && 0 < b._config.zoomList.length && d.command_zoom(b.ID, d.document.getElementById("dext5_design_" + b.ID)); "2" == b._config.olUlTagMode && d.adjustOlUlTag(b._DOC); setTimeout(function() {
											for (var a = d._iframeDoc.getElementsByTagName("input"), c = a.length, e = 0; e < c; e++)"radio" == a[e].type && null != a[e].getAttribute("dext5checked") && (a[e].checked = !0, a[e].setAttribute("checked", "checked"), a[e].removeAttribute("dext5checked")); d.adjustInputNodeForFF(b._DOC,
												!0)
										}, 10); 0 == d.isViewMode(b) && (b._dextCustomDataMode = !0, "1" == b._config.formMode ? (d.ReplaceRealEventToCustomData(), d.ReplaceRealObjectToImage(!1)) : d.ReplaceRealObjectToImage(), d.replaceClassForBorder(b, b._BODY, "show"), d.replaceClassForBookmark(b, b._BODY, "show")); d.xssReplaceScript(d._iframeDoc); d.setScrollOverflow(b); d.setStyleForTableBorderNodeClass(b); d.setCssForFormMode(b); "1" == b._config.adjustCurrentColorInSetApi && DEXT5.util.adjustBorderStyle(null, b); b.ShowTableBorder && (b.ShowTableBorder = !1); b._iconEnable("");
									0 == d.isViewMode(b) && d.setBodyDefaultValue(); d.wrapPtagForNotBlockTag(b); d.removeEmptySpanBRTag(b._BODY); d.replaceBrTag(b); d.fn_IEJASOBug(b); d.removeLastBrTag(b); 0 == d.isViewMode(b) && d.removeNbspInPTag(b); DEXT5.util.setBodyBackground(b); 0 == d.isViewMode(b) && "2" != b._config.undoMode && (b.UndoManager.putUndo(), b.UndoManager.charCount = 0, b.UndoManager.canUndo = !1, b.UndoManager.canRedo = !1); b._iconEnable(""); d.insertImageSrc(b); d.setClassTableAndCellLock(b); d.set_view_mode_auto_height(b); "1" == b._config.tableAutoAdjustInSetHtml &&
										d.command_AdjustTableAndCellWidth(b.ID, b, { type: "setHtml" }); d.setAdjustTableBorder(b._DOC); "show_blocks" == G_CURREDITOR.ShowBlocks && (G_CURREDITOR.ShowBlocks = "", d.command_showBlocks(b.ID, b)); d.G_Ruler && d.G_Ruler.SetRulerPosition(); d.setAutoBodyFit(b); d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0); DEXT5.browser.ie || "1" != b._config.useTableDiagonal ? d.deleteDiagonalLine(null, null, b) : (d.G_DEXT_Diagonal.setResizeForDiagonal(), d.G_DEXT_Diagonal.util.replaceEmptySpaceInDiagonal()); setTimeout(function() {
											if (d.setFocusToBody(b)) d.setFocusChildForStyle(b._BODY,
												c.caretPos); else { var a = null; "" != b._config.focusInitObjId ? a = DEXTTOP.DEXTDOC.getElementById(b._config.focusInitObjId) : b.autoMoveInitFocusData.targetNode && (a = b.autoMoveInitFocusData.targetNode); null != a ? a.focus() : DEXT5.browser.ie && (DEXTTOP.focus(), DEXTTOP.document.focus(), DEXTTOP.document.body.focus()) }
										}, 1); setTimeout(function() {
											try {
												d.adjustScroll(b); d.command_BeforeSetCompleteSpellCheck(b); d.g_findRepalceRange = null; try {
													"" != b._config.focusInitEditorObjId && (DEXT5.setFocusToObject(b._config.focusInitEditorObjId,
														!1, b.ID), b._config.focusInitEditorObjId = "")
												} catch (a) { DEXT5 && DEXT5.logMode && console.log(a) } try { "function" == typeof b._config.event.setComplete ? b._config.event.setComplete(b.ID) : DEXTTOP.dext_editor_set_complete_event(b.ID) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } try { d.contentChangeEventWrapper() } catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } try { d.onChange({ editor: b }) } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } b.UndoManager.reset(); "2" == b._config.undoMode && b.UndoManager.putUndo(!0);
												b.setValueIsBusy = !1
											} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
										}, 300)
								}; "1" == b._config.useHtmlCorrection ? "1" == b._config.useHtmlProcessByWorkerSetApi ? (d.showProcessingBackground(), d.releaseProcessHtmlWorker(), d.fn_processHtmlWorker({ editorBrowser: { ie: DEXT5.browser.ie, ieVersion: DEXT5.browser.ieVersion, gecko: DEXT5.browser.gecko }, editorConfig: b._config, callFn: "htmlTagRevision", callFnParam: [a, !1], callBackFn: g })) : (a = d.htmlTagRevision(a, !1), g(a)) : g(a)
							}
						} catch (h) {
							DEXT5 && DEXT5.logMode && console &&
							console.log(h)
						} else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setHtmlValue", value: c })
					} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) }
				}; DEXT5._CK_[35] = "L"; DEXT5.getHtmlValue = DEXT5.GetHtmlValue = function(a, e, c) {
					var b = ""; try {
						var d = DEXT5.util._setDEXT5editor(a); if (d) {
							0 != !!e || "undefined" != typeof c && 1 != c || d.UndoManager.reset(); var g = d._FRAMEWIN; if (1 == e) return g.getHTMLForAutoSave(d, "html"); try {
								for (var h in g.G_DEPlugin) if ("function" ===
									typeof g.G_DEPlugin[h].onBeforeGetApi) g.G_DEPlugin[h].onBeforeGetApi({ targetDoc: d._DOC })
							} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } g.beforeGetApi(d); var l = d.getEditorMode(); "source" != l && "text" != l || d.setChangeMode("design"); g.checkDefaultMessage(); g.clearAllFormControlSelected(); g.ReplaceBase64ImageToArray(d._config, d._FRAMEWIN._iframeDoc.body); g.setRemoveClass(["td", "th"], ["DEXT_dot"]); g.replaceClassForBorder(d, d._BODY, "hidden"); g.replaceClassForBookmark(d, d._BODY, "hidden"); g.ReplaceImageToRealObject();
							g.xssReplaceScript(g._iframeDoc); g.ClearDraggingTableAllTable(); "1" == d._config.formMode && g.ReplaceCustomDataToRealEvent(); var n, m; if (1 == e) try { DEXT5.browser.ie ? (n = Math.max(g._iframeDoc.documentElement.scrollLeft, g._iframeDoc.body.scrollLeft), m = Math.max(g._iframeDoc.documentElement.scrollTop, g._iframeDoc.body.scrollTop)) : (n = g._iframeWin.scrollX, m = g._iframeWin.scrollY) } catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } 1 != e && g.changeBodyContenteditable(!1); g.changeBodyImageProperty(!0); try {
								DEXT5.util.postimageToServer(d._DOC,
									d._config.mimeUse, d._config)
							} catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) } 1 != e && (g.removeEmptySpanBRTag(d._BODY), "1" == d._config.wrapPtagToGetApi && g.wrapPtagForNotBlockTag(d), g.removeFakeLineHeight(d._BODY), DEXT5.util.setInLineDefaultStyle(d)); g.setMatchSelectedValue(d._BODY); g.setMatchInputValue(d._BODY, e); g.adjustInputNodeForFF(d._DOC, !1); 1 != e && g.removeScrollStyleForIOS(d); g.setEmptyTagWhiteSpace(d); l = c = a = ""; d._BODY.style.transformOrigin && "" != d._BODY.style.transformOrigin && (a = d._BODY.style.transformOrigin,
								d._BODY.style.transformOrigin = ""); d._BODY.style.transform && "" != d._BODY.style.transform && (c = d._BODY.style.transform, d._BODY.style.transform = ""); d._BODY.style.zoom && "" != d._BODY.style.zoom && (l = d._BODY.style.zoom, d._BODY.style.zoom = ""); "" != d._config.placeholder.content && g.placeholderControl(d, "remove"); "1" == d._config.useTableDiagonal && "0" == d._config.showDiagonalInIEViewPage && g.G_DEXT_Diagonal && g.G_DEXT_Diagonal.findDiagonal() && g.G_DEXT_Diagonal.addStyleForIE(); b = d._DOC.documentElement.innerHTML; "" != d._config.placeholder.content &&
									g.placeholderControl(d, "set"); "" != a && (d._BODY.style.transformOrigin = a); "" != c && (d._BODY.style.transform = c); "" != l && (d._BODY.style.zoom = l); b = g.dummyTagClassClear(b); d._PageProp.bshowgrid && 1 == d._PageProp.bshowgrid && g.changeBodyImageProperty(!1); "1" == d._config.formMode ? (g.ReplaceRealEventToCustomData(), g.ReplaceRealObjectToImage(!1)) : g.ReplaceRealObjectToImage(); g.changeBodyContenteditable(!0); if (1 == e) try { g._iframeWin.scroll(n, m) } catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) } 1 == DEXT5.browser.ie &&
										(b = DEXT5.util.htmlToLowerCase(b)); b = g.RemoveUnnecessaryChar(b); b = g.CleanZeroChar(b); n = !1; "1" == d._config.replaceEmptyTagWithSpace && (n = !0); b = DEXT5.util.htmlRevision(b, n); "1" == d._config.xhtml_value && (b = DEXT5.util.html2xhtml(b)); b = g.removeEditorCss(b, d); g.setAddClass(["td", "th"], ["DEXT_dot"]); 0 == g.isViewMode(d) && (g.replaceClassForBorder(d, d._BODY, "show"), g.replaceClassForBookmark(d, d._BODY, "show")); b = g.replaceLineBreak(d, b); b = g.insertCarriageReturn(d, b); b = g.ReplaceArrayToBase64Image(d._config, d._FRAMEWIN._iframeDoc.body,
											b); g.setScrollStyleForIOS(d); g.G_Ruler && g.G_Ruler.SetRulerPosition(); "" != d._config.placeholder.content && g.placeholderControl(d, "class"); "1" == d._config.replaceRgbToHex && (b = g.replaceColorRgbToHex(b)); b = g.replaceMsStyleName(b, d); g.G_DEPlugin.webfontloader && "1" == g.G_DEPlugin.webfontloader.config.uselocalstorage && (b = g.G_DEPlugin.webfontloader.removeBase64WebFontFromHead(b)); try {
												for (h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onAfterGetApi) {
													var f = g.G_DEPlugin[h].onAfterGetApi({ isAuto: e, html: b });
													"string" == typeof f && (b = f)
												}
											} catch (u) { DEXT5 && DEXT5.logMode && console && console.log(u) } b = g.removeDomainFromIMGObjectInVirtualDom(d, b); g.afterGetApi(d)
						}
					} catch (v) { DEXT5 && DEXT5.logMode && console && console.log(v) } return b
				}; DEXT5._CK_[36] = "M"; DEXT5.setBodyValueExLikeDiv = DEXT5.SetBodyValueExLikeDiv = function(a, e) {
					var c = DEXT5.util.parseSetApiParam(a); a = c.html; if ("" == a || "" == DEXT5.util.trim(a)) DEXT5.setBodyValue("", e); else try {
						if (e = DEXT5.util._getEditorName(e), null != e) if (DEXT5.isLoadedEditorEx(e)) try {
							var b = DEXT5.util._setDEXT5editor(e);
							if (b) if ("lightview" == b._config.mode) DEXTTOP.G_CURREDITOR = b, b._FRAMEWIN.lightViewFunc("setBodyValueExLikeDiv", a, b); else {
								b.setValueIsBusy = !0; var d = b._FRAMEWIN; d.setDisableUndoRedo(a, b); d.getApplyDragResize(b) && b.dext_dragresize.resizeHandleClear(); a = d.removeCarriageReturn(b, a); d.setChangeModeForSetApi(b); d.setGlobalTableDefaultValue(); b.UndoManager.reset(); "1" == b._config.emptyTagRemoveInSetapi && (a = d.CleanZeroChar(a)); "CCODE_002" == b._config.custom_code && (a = a.replace(/<pstyle.*?>/gi, ""), a = a.replace(/<spanstyle.*?>/gi,
									"")); a = d.removeDummyTag(a); a = d.removeIncorrectSpaceInTag(a); a = d.DEXT5_EDITOR.HTMLParser.RemoveOfficeTag2(a); a = d.externalPageBreakDataRaplaceInEditor(a); var g = function(a) {
										"1" == b._config.useHtmlProcessByWorkerSetApi && (d.destoryWebWorkerVar(), d.hideProcessingBackground()); a = d.removeTagStyle(a); a = d.htmlAsciiToChar(a); a = d.fakeBodyDivForBackground(a, !0); a = DEXT5.util.htmlRevision(a); a = d.xssReplaceScript(null, a); a = d.adjustInputChecked(a); "1" == b._config.ie_BugFixed_Hyfont && (a = DEXT5.util.replaceHyFont(a, b));
										a = d.replaceFontFamilyInHtml(a, b); "1" == b._config.replaceEmptySpanTagInSetapi && (a = d.replaceEmptySpanTag(a)); d.command_InsertDogBGImg(b.ID, b._EDITOR.design, "Y", "", "", "", "", []); try { for (var e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onBeforeDocumentWrite) { var g = d.G_DEPlugin[e].onBeforeDocumentWrite({ html: a }); g && "string" == typeof g.html && (a = g.html) } } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } a = d.insertCarriageReturnBeforeCloseCell(a); a = d.removeEditorAttribute(a); d.setBodyValueToEditorEx(a,
											b); d.replaceEmptySpaceInATag(b._BODY); try { for (e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onAfterDocumentWrite) d.G_DEPlugin[e].onAfterDocumentWrite("SetBodyValueExLikeDiv") } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } "1" == b._config.compatibility.dingBatCharSetApi && d.dingBatFont("", b); d.replaceFontTagToSpan(b); "1" == b._config.setDefaultStyle.removeBodyStyleInSet && (b._BODY.removeAttribute("style"), "" != b._BODY.className && (b._BODY.className = "")); "" != b._config.placeholder.content &&
												d.placeholderControl(b, "set"); "0px" == b._BODY.style.width && (b._BODY.style.removeProperty ? b._BODY.style.removeProperty("width") : b._BODY.style.removeAttribute("width")); "0" != b._config.setDefaultStyle.value && "0" != b._config.setDefaultStyle.dext_set_style && b._BODY.id != b._config.setDefaultStyle.body_id && (b._BODY.id = b._config.setDefaultStyle.body_id); d.G_BodyFit.SetBodyWidth(); b._config.zoomList && 0 < b._config.zoomList.length && d.command_zoom(b.ID, d.document.getElementById("dext5_design_" + b.ID)); "2" == b._config.olUlTagMode &&
													d.adjustOlUlTag(b._DOC); setTimeout(function() { for (var a = d._iframeDoc.getElementsByTagName("input"), c = a.length, e = 0; e < c; e++)"radio" == a[e].type && null != a[e].getAttribute("dext5checked") && (a[e].checked = !0, a[e].setAttribute("checked", "checked"), a[e].removeAttribute("dext5checked")); d.adjustInputNodeForFF(b._DOC, !0) }, 10); 0 == d.isViewMode(b) && (b._dextCustomDataMode = !0, "1" == b._config.formMode ? (d.ReplaceRealEventToCustomData(), d.ReplaceRealObjectToImage(!1)) : d.ReplaceRealObjectToImage(), d.replaceClassForBorder(b,
														b._BODY, "show"), d.replaceClassForBookmark(b, b._BODY, "show")); d.xssReplaceScript(d._iframeDoc.body); d.setStyleForTableBorderNodeClass(b); d.setCssForFormMode(b); "1" == b._config.adjustCurrentColorInSetApi && DEXT5.util.adjustBorderStyle(null, b); b.ShowTableBorder && (b.ShowTableBorder = !1); b._iconEnable(""); 0 == d.isViewMode(b) && d.setBodyDefaultValue(); d.wrapPtagForNotBlockTag(b); d.removeEmptySpanBRTag(b._BODY); d.replaceBrTag(b); d.fn_IEJASOBug(b); d.removeLastBrTag(b); 0 == d.isViewMode(b) && d.removeNbspInPTag(b);
										DEXT5.util.setBodyBackground(b); 0 == d.isViewMode(b) && (b.UndoManager.putUndo(), b.UndoManager.charCount = 0, b.UndoManager.canUndo = !1, b.UndoManager.canRedo = !1); b._iconEnable(""); d.insertImageSrc(b); d.G_Ruler && d.G_Ruler.SetRulerPosition(); d.setAutoBodyFit(b); d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0); d.setClassTableAndCellLock(b); (DEXT5.browser.chrome || DEXT5.browser.opera || DEXT5.browser.safari) && d.set_view_mode_auto_height(b); "1" == b._config.tableAutoAdjustInSetHtml && d.command_AdjustTableAndCellWidth(b.ID,
											b, { type: "setHtml" }); d.setAdjustTableBorder(b._DOC); DEXT5.browser.ie || "1" != b._config.useTableDiagonal ? d.deleteDiagonalLine(null, null, b) : (d.G_DEXT_Diagonal.setResizeForDiagonal(), d.G_DEXT_Diagonal.util.replaceEmptySpaceInDiagonal()); setTimeout(function() {
												if (d.setFocusToBody(b)) d.setFocusChildForStyle(b._BODY, c.caretPos); else {
													var a = null; "" != b._config.focusInitObjId ? a = DEXTTOP.DEXTDOC.getElementById(b._config.focusInitObjId) : b.autoMoveInitFocusData.targetNode && (a = b.autoMoveInitFocusData.targetNode); null !=
														a ? a.focus() : DEXT5.browser.ie && (DEXTTOP.focus(), DEXTTOP.document.focus(), DEXTTOP.document.body.focus())
												}
											}, 1); setTimeout(function() {
												try {
													d.adjustScroll(b); d.command_BeforeSetCompleteSpellCheck(b); d.g_findRepalceRange = null; try { "" != b._config.focusInitEditorObjId && (DEXT5.setFocusToObject(b._config.focusInitEditorObjId, !1, b.ID), b._config.focusInitEditorObjId = "") } catch (a) { DEXT5 && DEXT5.logMode && console.log(a) } try { "function" == typeof b._config.event.setComplete ? b._config.event.setComplete(b.ID) : DEXTTOP.dext_editor_set_complete_event(b.ID) } catch (c) {
														DEXT5 &&
														DEXT5.logMode && console && console.log(c)
													} try { d.contentChangeEventWrapper() } catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } try { d.onChange({ editor: b }) } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } b.UndoManager.reset(); b.setValueIsBusy = !1
												} catch (h) { d.restoreValueInSetError(b) }
											}, 300)
									}; "1" == b._config.useHtmlCorrection ? "1" == b._config.useHtmlProcessByWorkerSetApi ? (d.showProcessingBackground(), d.releaseProcessHtmlWorker(), d.fn_processHtmlWorker({
										editorBrowser: {
											ie: DEXT5.browser.ie, ieVersion: DEXT5.browser.ieVersion,
											gecko: DEXT5.browser.gecko
										}, editorConfig: b._config, callFn: "htmlTagRevision", callFnParam: [a, !1], callBackFn: g
									})) : (a = d.htmlTagRevision(a, !1), g(a)) : g(a)
							}
						} catch (h) { d.restoreValueInSetError(b) } else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setBodyValueExLikeDiv", value: c })
					} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) }
				}; DEXT5._CK_[37] = "N"; DEXT5.getBodyValueExLikeDiv = DEXT5.GetBodyValueExLikeDiv = function(a,
					e, c) {
						var b = ""; try {
							var d = DEXT5.util._setDEXT5editor(a); if (d) {
								0 != !!e || "undefined" != typeof c && 1 != c || d.UndoManager.reset(); var g = d._FRAMEWIN; if (1 == e) return g.getHTMLForAutoSave(d, "bodyEx"); try { for (var h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onBeforeGetApi) g.G_DEPlugin[h].onBeforeGetApi({ targetDoc: d._DOC }) } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } g.beforeGetApi(d); var l = d.getEditorMode(); "source" != l && "text" != l || d.setChangeMode("design"); g.checkDefaultMessage(); g.clearAllFormControlSelected();
								g.ReplaceBase64ImageToArray(d._config, d._FRAMEWIN._iframeDoc.body); g.setRemoveClass(["td", "th"], ["DEXT_dot"]); g.replaceClassForBorder(d, d._BODY, "hidden"); g.replaceClassForBookmark(d, d._BODY, "hidden"); g.ReplaceImageToRealObject(); g.xssReplaceScript(g._iframeDoc.body); g.ClearDraggingTableAllTable(); "1" == d._config.formMode && g.ReplaceCustomDataToRealEvent(); var n, m; if (1 == e) try {
									DEXT5.browser.ie ? (n = Math.max(g._iframeDoc.documentElement.scrollLeft, g._iframeDoc.body.scrollLeft), m = Math.max(g._iframeDoc.documentElement.scrollTop,
										g._iframeDoc.body.scrollTop)) : (n = g._iframeWin.scrollX, m = g._iframeWin.scrollY)
								} catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } 1 != e && g.changeBodyContenteditable(!1); g.changeBodyImageProperty(!0); try { DEXT5.util.postimageToServer(d._DOC, d._config.mimeUse, d._config) } catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) } 1 != e && (g.removeEmptySpanBRTag(d._BODY), "1" == d._config.wrapPtagToGetApi && g.wrapPtagForNotBlockTag(d), g.removeFakeLineHeight(d._BODY), DEXT5.util.setInLineDefaultStyle(d)); g.setMatchSelectedValue(d._BODY);
								g.setMatchInputValue(d._BODY, e); g.adjustInputNodeForFF(d._DOC, !1); g.setEmptyTagWhiteSpace(d); l = c = a = ""; d._BODY.style.transformOrigin && "" != d._BODY.style.transformOrigin && (a = d._BODY.style.transformOrigin, d._BODY.style.transformOrigin = ""); d._BODY.style.transform && "" != d._BODY.style.transform && (c = d._BODY.style.transform, d._BODY.style.transform = ""); d._BODY.style.zoom && "" != d._BODY.style.zoom && (l = d._BODY.style.zoom, d._BODY.style.zoom = ""); "" != d._config.placeholder.content && g.placeholderControl(d, "remove");
								"1" == d._config.useTableDiagonal && "0" == d._config.showDiagonalInIEViewPage && g.G_DEXT_Diagonal && g.G_DEXT_Diagonal.findDiagonal() && g.G_DEXT_Diagonal.addStyleForIE(); b = d._BODY.outerHTML; "" != d._config.placeholder.content && g.placeholderControl(d, "set"); "" != a && (d._BODY.style.transformOrigin = a); "" != c && (d._BODY.style.transform = c); "" != l && (d._BODY.style.zoom = l); b = g.dummyTagClassClear(b); b = g.fakeBodyDivForBackground(b, !1); d._PageProp.bshowgrid && 1 == d._PageProp.bshowgrid && g.changeBodyImageProperty(!1); "1" == d._config.formMode ?
									(g.ReplaceRealEventToCustomData(), g.ReplaceRealObjectToImage(!1)) : g.ReplaceRealObjectToImage(); g.changeBodyContenteditable(!0); if (1 == e) try { g._iframeWin.scroll(n, m) } catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) } 1 == DEXT5.browser.ie && (b = DEXT5.util.htmlToLowerCase(b)); b = g.RemoveUnnecessaryChar(b); b = g.CleanZeroChar(b); n = !1; "1" == d._config.replaceEmptyTagWithSpace && (n = !0); b = DEXT5.util.htmlRevision(b, n); "1" == d._config.xhtml_value && (b = DEXT5.util.html2xhtml(b)); g.setAddClass(["td", "th"], ["DEXT_dot"]);
								0 == g.isViewMode(d) && (g.replaceClassForBorder(d, d._BODY, "show"), g.replaceClassForBookmark(d, d._BODY, "show")); b = g.replaceLineBreak(d, b); b = g.insertCarriageReturn(d, b); b = g.ReplaceArrayToBase64Image(d._config, d._FRAMEWIN._iframeDoc.body, b); g.G_Ruler && g.G_Ruler.SetRulerPosition(); "" != d._config.placeholder.content && g.placeholderControl(d, "class"); "1" == d._config.replaceRgbToHex && (b = g.replaceColorRgbToHex(b)); b = g.replaceMsStyleName(b, d); try {
									for (h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onAfterGetApi) {
										var f =
											g.G_DEPlugin[h].onAfterGetApi({ isAuto: e, html: b }); "string" == typeof f && (b = f)
									}
								} catch (u) { DEXT5 && DEXT5.logMode && console && console.log(u) } b = g.removeDomainFromIMGObjectInVirtualDom(d, b); g.afterGetApi(d)
							}
						} catch (v) { DEXT5 && DEXT5.logMode && console && console.log(v) } return b
				}; DEXT5._CK_[38] = "U"; DEXT5.setBodyValueEx = DEXT5.SetBodyValueEx = function(a, e) {
					var c = DEXT5.util.parseSetApiParam(a); a = c.html; if ("" == a || "" == DEXT5.util.trim(a)) DEXT5.setBodyValue("", e); else try {
						if (e = DEXT5.util._getEditorName(e), null != e) if (DEXT5.isLoadedEditorEx(e)) try {
							var b =
								DEXT5.util._setDEXT5editor(e); if (b) if ("lightview" == b._config.mode) DEXTTOP.G_CURREDITOR = b, b._FRAMEWIN.lightViewFunc("setBodyValueEx", a, b); else {
									b.setValueIsBusy = !0; var d = b._FRAMEWIN; d.setDisableUndoRedo(a, b); d.getApplyDragResize(b) && b.dext_dragresize.resizeHandleClear(); a = d.addHtmlToSetValue(b, a); a = d.removeCarriageReturn(b, a); d.setChangeModeForSetApi(b); d.setGlobalTableDefaultValue(); b.UndoManager.reset(); "1" == b._config.emptyTagRemoveInSetapi && (a = d.CleanZeroChar(a)); "CCODE_002" == b._config.custom_code &&
										(a = a.replace(/<pstyle.*?>/gi, ""), a = a.replace(/<spanstyle.*?>/gi, "")); a = d.removeDummyTag(a); a = d.removeIncorrectSpaceInTag(a); a = d.DEXT5_EDITOR.HTMLParser.RemoveOfficeTag2(a); a = d.externalPageBreakDataRaplaceInEditor(a); var g = function(a) {
											"1" == b._config.useHtmlProcessByWorkerSetApi && (d.destoryWebWorkerVar(), d.hideProcessingBackground()); a = d.removeTagStyle(a); a = d.htmlAsciiToChar(a); a = DEXT5.util.htmlRevision(a); a = d.xssReplaceScript(null, a); "1" == b._config.replaceEmptySpanTagInSetapi && (a = d.replaceEmptySpanTag(a));
											d.command_InsertDogBGImg(b.ID, b._EDITOR.design, "Y", "", "", "", "", []); try { for (var e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onBeforeDocumentWrite) { var g = d.G_DEPlugin[e].onBeforeDocumentWrite({ html: a }); g && "string" == typeof g.html && (a = g.html) } } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } a = d.insertCarriageReturnBeforeCloseCell(a); a = d.removeEditorAttribute(a); d.setBodyValueToEditorEx(a, b); d.replaceEmptySpaceInATag(b._BODY); try { for (e in d.G_DEPlugin) if ("function" === typeof d.G_DEPlugin[e].onAfterDocumentWrite) d.G_DEPlugin[e].onAfterDocumentWrite("SetBodyValueEx") } catch (k) {
												DEXT5 &&
												DEXT5.logMode && console && console.log(k)
											} "1" == b._config.compatibility.dingBatCharSetApi && d.dingBatFont("", b); d.replaceFontTagToSpan(b); "1" == b._config.setDefaultStyle.removeBodyStyleInSet && (b._BODY.removeAttribute("style"), "" != b._BODY.className && (b._BODY.className = "")); "" != b._config.placeholder.content && d.placeholderControl(b, "set"); "0px" == b._BODY.style.width && (b._BODY.style.removeProperty ? b._BODY.style.removeProperty("width") : b._BODY.style.removeAttribute("width")); "1" == b._config.removeEmptyTagSetValue &&
												d.setEmptyTagWhiteSpace(b); "0" != b._config.setDefaultStyle.value && "0" != b._config.setDefaultStyle.dext_set_style && b._BODY.id != b._config.setDefaultStyle.body_id && (b._BODY.id = b._config.setDefaultStyle.body_id); d.G_BodyFit.SetBodyWidth(); b._config.zoomList && 0 < b._config.zoomList.length && d.command_zoom(b.ID, d.document.getElementById("dext5_design_" + b.ID)); "2" == b._config.olUlTagMode && d.adjustOlUlTag(b._DOC); a = d._iframeDoc.getElementsByTagName("input"); e = a.length; for (g = 0; g < e; g++)"radio" == a[g].type && null !=
													a[g].getAttribute("dext5checked") && (a[g].checked = !0, a[g].setAttribute("checked", "checked"), a[g].removeAttribute("dext5checked")); d.adjustInputNodeForFF(b._DOC, !0); 0 == d.isViewMode(b) && (b._dextCustomDataMode = !0, "1" == b._config.formMode ? (d.ReplaceRealEventToCustomData(), d.ReplaceRealObjectToImage(!1)) : d.ReplaceRealObjectToImage(), d.replaceClassForBorder(b, b._BODY, "show"), d.replaceClassForBookmark(b, b._BODY, "show")); d.xssReplaceScript(d._iframeDoc.body); d.setScrollOverflow(b); d.setStyleForTableBorderNodeClass(b);
											d.setCssForFormMode(b); "1" == b._config.adjustCurrentColorInSetApi && DEXT5.util.adjustBorderStyle(null, b); b.ShowTableBorder && (b.ShowTableBorder = !1); b._iconEnable(""); 0 == d.isViewMode(b) && d.setBodyDefaultValue(); d.wrapPtagForNotBlockTag(b); d.removeEmptySpanBRTag(b._BODY); d.replaceBrTag(b); d.fn_IEJASOBug(b); d.removeLastBrTag(b); 0 == d.isViewMode(b) && d.removeNbspInPTag(b); DEXT5.util.setBodyBackground(b); 0 == d.isViewMode(b) && "2" != b._config.undoMode && (b.UndoManager.putUndo(), b.UndoManager.charCount = 0, b.UndoManager.canUndo =
												!1, b.UndoManager.canRedo = !1); b._iconEnable(""); d.insertImageSrc(b); d.setClassTableAndCellLock(b); (DEXT5.browser.chrome || DEXT5.browser.opera || DEXT5.browser.safari) && d.set_view_mode_auto_height(b); "1" == b._config.tableAutoAdjustInSetHtml && d.command_AdjustTableAndCellWidth(b.ID, b, { type: "setHtml" }); d.setAdjustTableBorder(b._DOC); d.G_Ruler && d.G_Ruler.SetRulerPosition(); d.setAutoBodyFit(b); d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0); DEXT5.browser.ie || "1" != b._config.useTableDiagonal ?
													d.deleteDiagonalLine(null, null, b) : (d.G_DEXT_Diagonal.setResizeForDiagonal(), d.G_DEXT_Diagonal.util.replaceEmptySpaceInDiagonal()); setTimeout(function() { if (d.setFocusToBody(b)) d.setFocusChildForStyle(b._BODY, c.caretPos); else { var a = null; "" != b._config.focusInitObjId ? a = DEXTTOP.DEXTDOC.getElementById(b._config.focusInitObjId) : b.autoMoveInitFocusData.targetNode && (a = b.autoMoveInitFocusData.targetNode); null != a ? a.focus() : DEXT5.browser.ie && (DEXTTOP.focus(), DEXTTOP.document.focus(), DEXTTOP.document.body.focus()) } },
														1); setTimeout(function() {
															try {
																d.adjustScroll(b); d.command_BeforeSetCompleteSpellCheck(b); d.g_findRepalceRange = null; try { "" != b._config.focusInitEditorObjId && (DEXT5.setFocusToObject(b._config.focusInitEditorObjId, !1, b.ID), b._config.focusInitEditorObjId = "") } catch (a) { DEXT5 && DEXT5.logMode && console.log(a) } try { "function" == typeof b._config.event.setComplete ? b._config.event.setComplete(b.ID) : DEXTTOP.dext_editor_set_complete_event(b.ID) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } try { d.contentChangeEventWrapper() } catch (e) {
																	DEXT5 &&
																	DEXT5.logMode && console && console.log(e)
																} try { d.onChange({ editor: b }) } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } b.UndoManager.reset(); "2" == b._config.undoMode && b.UndoManager.putUndo(!0); b.setValueIsBusy = !1
															} catch (h) { d.restoreValueInSetError(b) }
														}, 300)
										}; "1" == b._config.useHtmlCorrection ? "1" == b._config.useHtmlProcessByWorkerSetApi ? (d.showProcessingBackground(), d.releaseProcessHtmlWorker(), d.fn_processHtmlWorker({
											editorBrowser: { ie: DEXT5.browser.ie, ieVersion: DEXT5.browser.ieVersion, gecko: DEXT5.browser.gecko },
											editorConfig: b._config, callFn: "htmlTagRevision", callFnParam: [a, !1], callBackFn: g
										})) : (a = d.htmlTagRevision(a, !1), g(a)) : g(a)
								}
						} catch (h) { d.restoreValueInSetError(b) } else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setBodyValueEx", value: c })
					} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) }
				}; DEXT5._CK_[39] = "V"; DEXT5.getBodyValueEx = DEXT5.GetBodyValueEx = function(a, e, c) {
					var b = ""; try {
						var d = DEXT5.util._setDEXT5editor(a);
						if (d) {
							0 != !!e || "undefined" != typeof c && 1 != c || d.UndoManager.reset(); var g = d._FRAMEWIN; if (1 == e) return g.getHTMLForAutoSave(d, "bodyEx"); try { for (var h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onBeforeGetApi) g.G_DEPlugin[h].onBeforeGetApi({ targetDoc: d._DOC }) } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } g.beforeGetApi(d); var l = d.getEditorMode(); "source" != l && "text" != l || d.setChangeMode("design"); g.checkDefaultMessage(); g.clearAllFormControlSelected(); g.ReplaceBase64ImageToArray(d._config,
								d._FRAMEWIN._iframeDoc.body); g.setRemoveClass(["td", "th"], ["DEXT_dot"]); g.replaceClassForBorder(d, d._BODY, "hidden"); g.replaceClassForBookmark(d, d._BODY, "hidden"); g.ReplaceImageToRealObject(); g.xssReplaceScript(g._iframeDoc.body); g.ClearDraggingTableAllTable(); "1" == d._config.formMode && g.ReplaceCustomDataToRealEvent(); var n, m; if (1 == e) try {
									DEXT5.browser.ie ? (n = Math.max(g._iframeDoc.documentElement.scrollLeft, g._iframeDoc.body.scrollLeft), m = Math.max(g._iframeDoc.documentElement.scrollTop, g._iframeDoc.body.scrollTop)) :
									(n = g._iframeWin.scrollX, m = g._iframeWin.scrollY)
								} catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } 1 != e && g.changeBodyContenteditable(!1); g.changeBodyImageProperty(!0); try { DEXT5.util.postimageToServer(d._DOC, d._config.mimeUse, d._config) } catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) } 1 != e && (g.removeEmptySpanBRTag(d._BODY), "1" == d._config.wrapPtagToGetApi && g.wrapPtagForNotBlockTag(d), g.removeFakeLineHeight(d._BODY), DEXT5.util.setInLineDefaultStyle(d)); g.setMatchSelectedValue(d._BODY); g.setMatchInputValue(d._BODY,
									e); g.adjustInputNodeForFF(d._DOC, !1); g.setEmptyTagWhiteSpace(d); l = c = a = ""; d._BODY.style.transformOrigin && "" != d._BODY.style.transformOrigin && (a = d._BODY.style.transformOrigin, d._BODY.style.transformOrigin = ""); d._BODY.style.transform && "" != d._BODY.style.transform && (c = d._BODY.style.transform, d._BODY.style.transform = ""); d._BODY.style.zoom && "" != d._BODY.style.zoom && (l = d._BODY.style.zoom, d._BODY.style.zoom = ""); "" != d._config.placeholder.content && g.placeholderControl(d, "remove"); "1" == d._config.useTableDiagonal &&
										"0" == d._config.showDiagonalInIEViewPage && g.G_DEXT_Diagonal && g.G_DEXT_Diagonal.findDiagonal() && g.G_DEXT_Diagonal.addStyleForIE(); b = d._BODY.outerHTML; "" != d._config.placeholder.content && g.placeholderControl(d, "set"); "" != a && (d._BODY.style.transformOrigin = a); "" != c && (d._BODY.style.transform = c); "" != l && (d._BODY.style.zoom = l); b = g.dummyTagClassClear(b); d._PageProp.bshowgrid && 1 == d._PageProp.bshowgrid && g.changeBodyImageProperty(!1); "1" == d._config.formMode ? (g.ReplaceRealEventToCustomData(), g.ReplaceRealObjectToImage(!1)) :
											g.ReplaceRealObjectToImage(); g.changeBodyContenteditable(!0); if (1 == e) try { g._iframeWin.scroll(n, m) } catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) } 1 == DEXT5.browser.ie && (b = DEXT5.util.htmlToLowerCase(b)); b = g.RemoveUnnecessaryChar(b); b = g.CleanZeroChar(b); n = !1; "1" == d._config.replaceEmptyTagWithSpace && (n = !0); b = DEXT5.util.htmlRevision(b, n); "1" == d._config.xhtml_value && (b = DEXT5.util.html2xhtml(b)); g.setAddClass(["td", "th"], ["DEXT_dot"]); 0 == g.isViewMode(d) && (g.replaceClassForBorder(d, d._BODY, "show"),
												g.replaceClassForBookmark(d, d._BODY, "show")); b = g.replaceLineBreak(d, b); b = g.insertCarriageReturn(d, b); b = g.ReplaceArrayToBase64Image(d._config, d._FRAMEWIN._iframeDoc.body, b); g.G_Ruler && g.G_Ruler.SetRulerPosition(); "" != d._config.placeholder.content && g.placeholderControl(d, "class"); "1" == d._config.replaceRgbToHex && (b = g.replaceColorRgbToHex(b)); b = g.replaceMsStyleName(b, d); try {
													for (h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onAfterGetApi) {
														var f = g.G_DEPlugin[h].onAfterGetApi({ isAuto: e, html: b });
														"string" == typeof f && (b = f)
													}
												} catch (u) { DEXT5 && DEXT5.logMode && console && console.log(u) } b = g.removeDomainFromIMGObjectInVirtualDom(d, b); g.afterGetApi(d)
						}
					} catch (v) { DEXT5 && DEXT5.logMode && console && console.log(v) } return b
				}; DEXT5._CK_[40] = "G"; DEXT5.setBodyValue = DEXT5.SetBodyValue = function(a, e, c, b, d) {
					try {
						var g = DEXT5.util.parseSetApiParam(a); a = g.html; !c || "" != e && void 0 != e || (e = DEXTTOP.G_CURREDITOR.ID); e = DEXT5.util._getEditorName(e); if (null != e) if (DEXT5.isLoadedEditorEx(e)) try {
							var h = DEXT5.util._setDEXT5editor(e); if (h) if ("lightview" ==
								h._config.mode) DEXTTOP.G_CURREDITOR = h, h._FRAMEWIN.lightViewFunc("setBodyValue", a, h); else {
									0 == !!c && 0 == !!b && (h.setValueIsBusy = !0); if ("" == a || "" == DEXT5.util.trim(a)) a = "<p></p>"; var k = h._FRAMEWIN; k.setDisableUndoRedo(a, h); k.getApplyDragResize(h) && h.dext_dragresize.resizeHandleClear(); a = k.addHtmlToSetValue(h, a); a = k.removeCarriageReturn(h, a); k.setChangeModeForSetApi(h, b); k.setGlobalTableDefaultValue(); h.UndoManager.reset(); "1" == h._config.emptyTagRemoveInSetapi && (a = k.CleanZeroChar(a)); "CCODE_002" == h._config.custom_code &&
										(a = a.replace(/<pstyle.*?>/gi, ""), a = a.replace(/<spanstyle.*?>/gi, "")); a = k.removeDummyTag(a); a = k.removeIncorrectSpaceInTag(a); a = k.DEXT5_EDITOR.HTMLParser.RemoveOfficeTag2(a); a = k.externalPageBreakDataRaplaceInEditor(a); e = function(a) {
											"1" == h._config.useHtmlProcessByWorkerSetApi && (k.destoryWebWorkerVar(), k.hideProcessingBackground()); a = k.removeTagStyle(a); a = k.htmlAsciiToChar(a); var e = "BackCompat" == k._iframeDoc.compatMode; DEXT5.browser.ie && 7 < DEXT5.browser.ieVersion && 11 > DEXT5.browser.ieVersion && 1 != e ? 0 == !!c &&
												(a = DEXT5.util.htmlRevision(a)) : a = DEXT5.util.htmlRevision(a); 1 != b && (a = k.xssReplaceScript(null, a)); a = k.adjustInputChecked(a); "1" == h._config.ie_BugFixed_Hyfont && (a = DEXT5.util.replaceHyFont(a, h)); a = k.replaceFontFamilyInHtml(a, h); "1" == h._config.replaceEmptySpanTagInSetapi && (a = k.replaceEmptySpanTag(a)); try { for (var l in k.G_DEPlugin) if ("function" === typeof k.G_DEPlugin[l].onBeforeDocumentWrite) { var n = k.G_DEPlugin[l].onBeforeDocumentWrite({ html: a }); n && "string" == typeof n.html && (a = n.html) } } catch (f) {
													DEXT5 && DEXT5.logMode &&
													console && console.log(f)
												} a = k.insertCarriageReturnBeforeCloseCell(a); a = k.removeEditorAttribute(a); k.setBodyValueToEditor(a, h); k.replaceEmptySpaceInATag(h._BODY); try { for (l in k.G_DEPlugin) if ("function" === typeof k.G_DEPlugin[l].onAfterDocumentWrite) k.G_DEPlugin[l].onAfterDocumentWrite("SetBodyValue") } catch (u) { DEXT5 && DEXT5.logMode && console && console.log(u) } "1" == h._config.compatibility.dingBatCharSetApi && k.dingBatFont("", h); k.replaceFontTagToSpan(h); "1" == h._config.setDefaultStyle.removeBodyStyleInSet && (h._BODY.removeAttribute("style"),
													"" != h._BODY.className && (h._BODY.className = "")); "" != h._config.placeholder.content && k.placeholderControl(h, "set"); "0px" == h._BODY.style.width && (h._BODY.style.removeProperty ? h._BODY.style.removeProperty("width") : h._BODY.style.removeAttribute("width")); "1" == h._config.removeEmptyTagSetValue && k.setEmptyTagWhiteSpace(h); "0" != h._config.setDefaultStyle.value && "0" != h._config.setDefaultStyle.dext_set_style && h._BODY.id != h._config.setDefaultStyle.body_id && (h._BODY.id = h._config.setDefaultStyle.body_id); k.G_BodyFit.SetBodyWidth();
											"2" == h._config.olUlTagMode && k.adjustOlUlTag(h._DOC); a = k._iframeDoc.getElementsByTagName("input"); e = a.length; for (l = 0; l < e; l++)"radio" == a[l].type && null != a[l].getAttribute("dext5checked") && (a[l].checked = !0, a[l].setAttribute("checked", "checked"), a[l].removeAttribute("dext5checked")); k.adjustInputNodeForFF(h._DOC, !0); 1 != c && 0 == k.isViewMode(h) && (h._dextCustomDataMode = !0, "1" == h._config.formMode ? (k.ReplaceRealEventToCustomData(), k.ReplaceRealObjectToImage(!1)) : k.ReplaceRealObjectToImage(), k.replaceClassForBookmark(h,
												h._BODY, "show")); k.xssReplaceScript(k._iframeDoc.body); d && k.ReplaceRealObjectToImage(); k.setStyleForTableBorderNodeClass(h); k.setCssForFormMode(h); "1" == h._config.adjustCurrentColorInSetApi && DEXT5.util.adjustBorderStyle(null, h); h.ShowTableBorder && (h.ShowTableBorder = !1); h._iconEnable(""); 0 == k.isViewMode(h) && k.setBodyDefaultValue(); k.wrapPtagForNotBlockTag(h); k.removeEmptySpanBRTag(h._BODY); k.replaceBrTag(h); k.fn_IEJASOBug(h); k.removeLastBrTag(h); 0 == k.isViewMode(h) && k.removeNbspInPTag(h); 1 != c && 0 == k.isViewMode(h) &&
													(k.replaceClassForBorder(h, h._BODY, "show"), k.replaceClassForBookmark(h, h._BODY, "show")); DEXT5.util.setBodyBackground(h); 0 == k.isViewMode(h) && "2" != h._config.undoMode && (h.UndoManager.putUndo(), h.UndoManager.charCount = 0, h.UndoManager.canUndo = !1, h.UndoManager.canRedo = !1); h._iconEnable(""); k.insertImageSrc(h, null, b); k.setClassTableAndCellLock(h); (DEXT5.browser.chrome || DEXT5.browser.opera || DEXT5.browser.safari) && k.set_view_mode_auto_height(h); "1" == h._config.tableAutoAdjustInSetHtml && k.command_AdjustTableAndCellWidth(h.ID,
														h, { type: "setHtml" }); k.setAdjustTableBorder(h._DOC); b && "1" == h._config.useSelectionMark && k.setRangeSelectionMarkInDesign(); k.G_Ruler && k.G_Ruler.SetRulerPosition(); k.setAutoBodyFit(h); k.G_BodyFit.noncreationAreaBackgroundStatus && k.setBodyFitStyle(h, !0); DEXT5.browser.ie || "1" != h._config.useTableDiagonal ? k.deleteDiagonalLine(null, null, h) : (k.G_DEXT_Diagonal.setResizeForDiagonal(), k.G_DEXT_Diagonal.util.replaceEmptySpaceInDiagonal()); setTimeout(function() {
															if (k.setFocusToBody(h)) k.setFocusChildForStyle(h._BODY,
																g.caretPos); else { var a = null; "" != h._config.focusInitObjId ? a = DEXTTOP.DEXTDOC.getElementById(h._config.focusInitObjId) : h.autoMoveInitFocusData.targetNode && (a = h.autoMoveInitFocusData.targetNode); null != a ? a.focus() : DEXT5.browser.ie && (DEXTTOP.focus(), DEXTTOP.document.focus(), DEXTTOP.document.body.focus()) }
														}, 1); b || setTimeout(function() {
															try {
																k.adjustScroll(h); k.command_BeforeSetCompleteSpellCheck(h); k.g_findRepalceRange = null; try {
																	"" != h._config.focusInitEditorObjId && (DEXT5.setFocusToObject(h._config.focusInitEditorObjId,
																		!1, h.ID), h._config.focusInitEditorObjId = "")
																} catch (a) { DEXT5 && DEXT5.logMode && console.log(a) } try { "function" == typeof h._config.event.setComplete ? h._config.event.setComplete(h.ID) : DEXTTOP.dext_editor_set_complete_event(h.ID) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } try { k.contentChangeEventWrapper() } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } try { k.onChange({ editor: h }) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } h.UndoManager.reset(); "2" == h._config.undoMode && h.UndoManager.putUndo(!0);
																h.setValueIsBusy = !1
															} catch (e) { k.restoreValueInSetError(h) }
														}, 300)
										}; "1" == h._config.useHtmlCorrection ? "1" == h._config.useHtmlProcessByWorkerSetApi ? (k.showProcessingBackground(), k.releaseProcessHtmlWorker(), k.fn_processHtmlWorker({ editorBrowser: { ie: DEXT5.browser.ie, ieVersion: DEXT5.browser.ieVersion, gecko: DEXT5.browser.gecko }, editorConfig: h._config, callFn: "htmlTagRevision", callFnParam: [a, !1], callBackFn: e })) : (a = k.htmlTagRevision(a, !1), e(a)) : e(a)
							}
						} catch (l) { k.restoreValueInSetError(h) } else null == DEXT5.InitEditorDataHashTable &&
							(DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setBodyValue", value: g })
					} catch (n) { DEXT5 && DEXT5.logMode && console && console.log(n) }
				}; DEXT5._CK_[41] = "K"; DEXT5.getBodyValue = DEXT5.GetBodyValue = function(a, e, c) {
					var b = ""; try {
						var d = DEXT5.util._setDEXT5editor(a); if (d) {
							0 != !!e || "undefined" != typeof c && 1 != c || d.UndoManager.reset(); var g = d._FRAMEWIN; if (1 == e) return g.getHTMLForAutoSave(d, "body"); try { for (var h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onBeforeGetApi) g.G_DEPlugin[h].onBeforeGetApi({ targetDoc: d._DOC }) } catch (k) {
								DEXT5 &&
								DEXT5.logMode && console && console.log(k)
							} g.beforeGetApi(d); var l = d.getEditorMode(); "source" != l && "text" != l || d.setChangeMode("design"); g.checkDefaultMessage(); g.clearAllFormControlSelected(); g.ReplaceBase64ImageToArray(d._config, d._FRAMEWIN._iframeDoc.body); g.setRemoveClass(["td", "th"], ["DEXT_dot"]); g.replaceClassForBorder(d, d._BODY, "hidden"); g.replaceClassForBookmark(d, d._BODY, "hidden"); g.ReplaceImageToRealObject(); g.xssReplaceScript(g._iframeDoc.body); g.ClearDraggingTableAllTable(); "1" == d._config.formMode &&
								g.ReplaceCustomDataToRealEvent(); g.changeBodyImageProperty(!0); try { DEXT5.util.postimageToServer(d._DOC, d._config.mimeUse, d._config) } catch (n) { DEXT5 && DEXT5.logMode && console && console.log(n) } 1 != e && (g.removeEmptySpanBRTag(d._BODY), "1" == d._config.wrapPtagToGetApi && g.wrapPtagForNotBlockTag(d), g.removeFakeLineHeight(d._BODY), DEXT5.util.setInLineDefaultStyle(d)); g.setMatchSelectedValue(d._BODY); g.setMatchInputValue(d._BODY, e); g.adjustInputNodeForFF(d._DOC, !1); g.setEmptyTagWhiteSpace(d); "" != d._config.placeholder.content &&
									g.placeholderControl(d, "remove"); b = d._BODY.innerHTML; "" != d._config.placeholder.content && g.placeholderControl(d, "set"); b = g.dummyTagClassClear(b); d._PageProp.bshowgrid && 1 == d._PageProp.bshowgrid && g.changeBodyImageProperty(!1); "1" == d._config.formMode ? (g.ReplaceRealEventToCustomData(), g.ReplaceRealObjectToImage(!1)) : g.ReplaceRealObjectToImage(); 1 == DEXT5.browser.ie && (b = DEXT5.util.htmlToLowerCase(b)); b = g.RemoveUnnecessaryChar(b); b = g.CleanZeroChar(b); a = !1; "1" == d._config.replaceEmptyTagWithSpace && (a = !0); b =
										DEXT5.util.htmlRevision(b, a); "1" == d._config.xhtml_value && (b = DEXT5.util.html2xhtml(b)); g.setAddClass(["td", "th"], ["DEXT_dot"]); 0 == g.isViewMode(d) && (g.replaceClassForBorder(d, d._BODY, "show"), g.replaceClassForBookmark(d, d._BODY, "show")); b = g.replaceLineBreak(d, b); b = g.insertCarriageReturn(d, b); b = g.ReplaceArrayToBase64Image(d._config, d._FRAMEWIN._iframeDoc.body, b); g.G_Ruler && g.G_Ruler.SetRulerPosition(); "" != d._config.placeholder.content && g.placeholderControl(d, "class"); "1" == d._config.replaceRgbToHex && (b =
											g.replaceColorRgbToHex(b)); b = g.replaceMsStyleName(b, d); try { for (h in g.G_DEPlugin) if ("function" === typeof g.G_DEPlugin[h].onAfterGetApi) { var m = g.G_DEPlugin[h].onAfterGetApi({ isAuto: e, html: b }); "string" == typeof m && (b = m) } } catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } b = g.removeDomainFromIMGObjectInVirtualDom(d, b); g.afterGetApi(d)
						}
					} catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) } return b
				}; DEXT5._CK_[42] = "R"; DEXT5.loadHtmlValueExFromURL = DEXT5.LoadHtmlValueExFromURL = function(a, e) {
					try {
						if (e =
							DEXT5.util._getEditorName(e), null != e) if (DEXT5.isLoadedEditorEx(e)) try { if (DEXT5.util._setDEXT5editor(e)) { var c = DEXT5.ajax.load(a); DEXT5.setHtmlContents(c, e) } } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "loadHtmlValueExFromURL", value: a })
					} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
				}; DEXT5._CK_[43] = "S"; DEXT5.loadHtmlValueExFromServerURL =
					DEXT5.LoadHtmlValueExFromServerURL = function(a, e) {
						try {
							if (e = DEXT5.util._getEditorName(e), null != e) if (DEXT5.isLoadedEditorEx(e)) try {
								var c = DEXT5.util._setDEXT5editor(e); if (c) {
									0 != a.toLowerCase().indexOf("http:") && 0 != a.toLowerCase().indexOf("https:") && (0 < a.toLowerCase().indexOf("www.") ? a = location.protocol + "//" + a : 0 == a.toLowerCase().indexOf("/") && (a = location.protocol + "//" + location.host + a)); var b = c._config.post_url + "?load_html_url=" + encodeURI(a), d = DEXT5.ajax.load(b); -1 >= d.indexOf("[OK]") ? alert(d) : (d = d.substring(4),
										DEXT5.setHtmlContents(d, e))
								}
							} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "loadHtmlValueExFromServerURL", value: a })
						} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
					}; DEXT5._CK_[44] = "T"; DEXT5.setDirectEditHtmlUrl = DEXT5.SetDirectEditHtmlUrl = function(a, e) {
						if (a && "" != a) try {
							if (e = DEXT5.util._getEditorName(e), null != e) if (DEXT5.isLoadedEditorEx(e)) try {
								var c =
									DEXT5.util._setDEXT5editor(e); if (c) { c.ShowTableBorder && (c.ShowTableBorder = !1); c._config.directEditHtmlUrl = a; var b = c._FRAMEWIN; b.getApplyDragResize(c) && c.dext_dragresize.resizeHandleClear(); b.loadDirectEditHtmlUrl(c._config) }
							} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setDirectEditHtmlUrl", value: a })
						} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
					};
	DEXT5._CK_[45] = "O"; DEXT5.getBodyTextValue = DEXT5.GetBodyTextValue = function(a) {
		var e = ""; try {
			var c = DEXT5.util._setDEXT5editor(a), b = c._FRAMEWIN; if (c) try {
				var d = c.getEditorMode(); "source" != d && "text" != d || c.setChangeMode("design"); var g = c._BODY; b.deleteDiagonalLine(null, null, c); if ("string" == typeof g.innerText) {
					var h = g.outerHTML, h = h.replace(/<[a-zA-Z]+(\s+.*?)>/g, function(a, b) { return a.replace(b, "") }), h = h.replace(/<body>\s*</gi, "<"), h = h.replace(/>\s*<\/body>/gi, ">"), h = h.replace(/<li>/gi, "<p>"), h = h.replace(/<\/li>/gi,
						"</p>"), h = h.replace(/<td>((?:.|\s)*?)<\/td>/gi, function(a, b) { 0 > b.toLowerCase().indexOf("<p>") && (a = a.replace("<td>", "<p>"), a = a.replace("</td>", "</p>")); return a }), h = h.replace(/<th>((?:.|\s)*?)<\/th>/gi, function(a, b) { 0 > b.toLowerCase().indexOf("<p>") && (a = a.replace("<th>", "<p>"), a = a.replace("</th>", "</p>")); return a }), h = h.replace(/<p><\/p>/gi, ""), h = h.replace(/<p>(?:<span>)*(<br>)(?:<\/span>)*<\/p>/gi, "<br>"), h = h.replace(/<p>(?:<span>)*(&nbsp;)(?:<\/span>)*<\/p>/gi, "<br>"), h = h.replace(/<(?!br\s*\/?>|\/?p\s*\/?>)[^>]+>/gi,
							""), h = h.replace(/<p>/gi, ""), h = h.replace(/<br><\/p>/gi, "</p>"), h = h.replace(/<\/p>/gi, "<br>"), h = h.replace(/<br>$/i, ""), k = c._DOC.createElement("div"); k.style.position = "absolute"; k.style.left = "-1000px"; c._DOC.body.appendChild(k); k.innerHTML = h; e = k.innerText; k.parentNode.removeChild(k)
				} else {
					e = g.innerHTML; e = e.replace(RegExp("<[^>]*>s*([^<>\\n]*\\n[^<>\\n]*)*s*</[^>]*>", "g"), function(a) { return a.replace(/\n/g, " ") }); e = e.replace(/\r/g, ""); e = e.replace(/[\n|\t]/g, ""); e = e.replace(/[\v|\f]/g, ""); e = e.replace(/<[a-zA-Z]+(\s+.*?)>/g,
						function(a, b) { return a.replace(b, "") }); e = e.replace(/<p><\/p>/gi, ""); e = e.replace(/<p>(?:<span>)*(<br>)(?:<\/span>)*<\/p>/gi, "\n"); e = e.replace(/<p>(?:<span>)*(&nbsp;)(?:<\/span>)*<\/p>/gi, "\n"); DEXT5.browser.ie && 11 == DEXT5.browser.ieVersion && (e = e.replace(/<br(\s)*\/?><\/p>/gi, "</p>"), e = e.replace(/<br(\s[^\/]*)?><\/p>/gi, "</p>")); var e = e.replace(/<br(\s)*\/?>/gi, "\n"), e = e.replace(/<br(\s[^\/]*)?>/gi, "\n"), e = e.replace(/<\/p(\s[^\/]*)?>/gi, "\n"), e = e.replace(/<\/li(\s[^\/]*)?>/gi, "\n"), e = e.replace(/<\/tr(\s[^\/]*)?>/gi,
							"\n"), l = e.lastIndexOf("\n"); -1 < l && "\n" == e.substring(l) && (e = e.substring(0, l))
				} e = b.removeStripTags(e, null); e = b.unhtmlSpecialChars(e)
			} catch (n) { e = "" }
		} catch (m) { DEXT5 && DEXT5.logMode && console && console.log(m) } return e
	}; DEXT5._CK_[46] = "W"; DEXT5.dext5EncodeMime = DEXT5.Dext5EncodeMime = function(a, e) {
		try {
			if (void 0 == a || "" == a) return "0"; var c = G_CURREDITOR._FRAMEWIN; if (c.G_dext5plugIn) {
				var b = G_CURREDITOR._config; if ("1" == b.mimeUse) {
					c.G_dext5plugIn.mimeCharset = b.mimeCharset; c.G_dext5plugIn.mimeConentEncodingType = b.mimeConentEncodingType;
					c.G_dext5plugIn.mimeRemoveHeader = b.mimeRemoveHeader; c.G_dext5plugIn.mimeLocalOnly = b.mimeLocalOnly; c.G_dext5plugIn.mimeSetListInit(); var d = b.mimeFileTypeFilter.toString(2), g = (2).toString(2), h = (4).toString(2), k = (8).toString(2); c.ReplaceImageToRealObject(); c.xssReplaceScript(c._iframeDoc); c.changeBodyImageProperty(!0); if (d & 1) {
						for (var l = c._iframeDoc.getElementsByTagName("IMG"), n = l.length, m = 0; m < n; m++) {
							var t = ""; try {
								t = l[m].src, RegExp("[^A-Za-z0-9_./-~]", "g").test(t.replace("http://", "").replace("https://",
									"")) && (t = l[m].outerHTML.replace(/<img .*?src="(.*?)"[^>]*>/i, "$1"))
							} catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) } c.G_dext5plugIn.mimeAddEncodingFile(t, 0)
						} for (var b = ["table", "td", "body"], r = b.length, m = 0; m < r; m++)for (var l = c._iframeDoc.getElementsByTagName(b[m]), n = l.length, f = 0; f < n; f++) {
							var u = l[f].style.backgroundImage; "" == u && l[f].getAttribute("background") && (u = l[f].getAttribute("background")); u = u.replace('url("', "").replace('")', ""); u = u.replace("url('", "").replace("')", ""); u = u.replace("url(",
								"").replace(")", ""); "" != u && "none" != u.toLowerCase() && "initial" != u.toLowerCase() && c.G_dext5plugIn.mimeAddEncodingFile(u, 0)
						}
					} if (d & g) for (l = c._iframeDoc.getElementsByTagName("LINK"), n = l.length, m = 0; m < n; m++)t = l[m].href, 0 < t.length && c.G_dext5plugIn.mimeAddEncodingFile(t, 1); if (d & h) for (l = c._iframeDoc.getElementsByTagName("SCRIPT"), n = l.length, m = 0; m < n; m++)t = l[m].src, 0 < t.length && c.G_dext5plugIn.mimeAddEncodingFile(t, 1); if (d & k) try {
						l = c._iframeDoc.getElementsByTagName("EMBED"); n = l.length; for (m = 0; m < n; m++)t = l[m].src,
							0 < t.length && c.G_dext5plugIn.mimeAddEncodingFile(t, 0); l = c._iframeDoc.getElementsByTagName("OBJECT"); n = l.length; for (m = 0; m < n; m++)t = l[m].data, 0 < t.length && c.G_dext5plugIn.mimeAddEncodingFile(t, 0)
					} catch (v) { DEXT5 && DEXT5.logMode && console && console.log(v) } c.G_BodyFit.noncreationAreaBackgroundStatus && c.setBodyFitStyle(G_CURREDITOR, !0); c.ReplaceRealObjectToImage(); var y = c.G_dext5plugIn.Dext5EncodeMime(a, e); return 0 > y.indexOf("boundary") ? "0" : y
				}
			}
		} catch (z) { DEXT5 && DEXT5.logMode && console && console.log(z) } return "0"
	};
	DEXT5.dext5EncodeMimeEx = DEXT5.Dext5EncodeMimeEx = function(a, e, c) { try { if (void 0 == a || "" == a) return "0"; var b = G_CURREDITOR._FRAMEWIN; b.G_dext5plugIn ? "function" === typeof c && c(DEXT5.Dext5EncodeMime(a, e)) : b.G_MIME_OBJ.toMIME(a, e, c) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5._CK_[47] = "X"; DEXT5.dext5DecodeMime = DEXT5.Dext5DecodeMime = function(a) {
		var e = "0"; try { e = G_CURREDITOR._FRAMEWIN.G_dext5plugIn ? G_CURREDITOR._FRAMEWIN.G_dext5plugIn.Dext5DecodeMime(a) : G_CURREDITOR._FRAMEWIN.G_MIME_OBJ.toHTML(a) } catch (c) {
			DEXT5 &&
			DEXT5.logMode && console && console.log(c)
		} return e
	}; DEXT5._CK_[48] = "P"; DEXT5.dext5DecodeMimeEx = DEXT5.Dext5DecodeMimeEx = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(e); if (c && "1" == c._config.mimeUse) {
				var b = c._FRAMEWIN, d = "", d = b.G_dext5plugIn ? b.G_dext5plugIn.Dext5DecodeMime(a) : b.G_MIME_OBJ.toHTML(a); if (0 < d.length && "0" != d) {
					var g = c._config.xss_use, h = c._config.xss_remove_tags, k = c._config.xss_remove_events; c._config.xss_use = ""; c._config.xss_remove_tags = ""; c._config.xss_remove_events = ""; DEXT5.setHtmlValueEx(d,
						e); c._config.xss_use = g; c._config.xss_remove_tags = h; c._config.xss_remove_events = k
				}
			}
		} catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) }
	}; DEXT5._CK_[49] = "Q"; DEXT5.isEmpty = DEXT5.IsEmpty = function(a) {
		var e = !1; try {
			var c = DEXT5.util._setDEXT5editor(a); if (c) {
				if ("" != c._config.defaultMessage) return !0; var b = c._FRAMEWIN, d = b._iframeDoc.body.textContent || b._iframeDoc.body.innerText; "" != d && (d = d.replace(/ /gi, ""), d = d.replace(/\s/gi, ""), d = d.replace(/\t/g, ""), d = d.replace(/\r?\n?\r?\n/g, ""), d = d.replace(/&nbsp;/gi,
					""), d = d.replace(/<br \/>/gi, ""), d = d.replace(/<br>/gi, ""), d = d.replace(/<p *([^>?+])*><\/p>/gi, "")); var g = b._iframeDoc.body.innerHTML; if ("" != g) {
						var g = g.replace(/ /gi, ""), g = g.replace(/\s/gi, ""), g = g.replace(/\t/g, ""), g = g.replace(/\r?\n?\r?\n/g, ""), g = g.replace(/&nbsp;/gi, ""), g = g.replace(/<br \/>/gi, ""), g = g.replace(/<br>/gi, ""), h = c._config.removeEmptyTag; c._config.removeEmptyTag = "1"; g = b.CleanZeroChar(g); c._config.removeEmptyTag = h; g = g.replace(/<p *([^>?+])*><\/p>/gi, ""); 1 == DEXT5.util.isEmptyContents(g) &&
							(g = "")
					} e = 0 == d.length && 0 == g.length ? !0 : !1
			} else e = !1
		} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } return e
	}; DEXT5._CK_[50] = "Y"; DEXT5.isEmptyToString = DEXT5.IsEmptyToString = function(a) { var e = !1, e = DEXT5.isEmpty(a); return e = e.toString().toLowerCase() }; DEXT5._CK_[51] = "Z"; DEXT5.setInsertHTMLToObject = DEXT5.SetInsertHTMLToObject = function(a, e, c) {
		if (void 0 != a && "" != a && void 0 != e && "" != e) try {
			var b = DEXT5.util._setDEXT5editor(c); if (b) {
				var d = b._FRAMEWIN, g = b._DOC.getElementById(e); a = d.DEXT5_EDITOR.HTMLParser.RemoveOfficeTag2(a);
				a = d.externalPageBreakDataRaplaceInEditor(a); "1" == b._config.replaceEmptySpanTagInSetapi && (a = d.replaceEmptySpanTag(a)); g && (g.innerHTML = a); setTimeout(function() { try { "function" == typeof b._config.event.setInsertComplete ? b._config.event.setInsertComplete(b.ID) : DEXTTOP.dext_editor_set_insert_complete_event(b.ID) } catch (a) { DEXT5 && DEXT5.logMode && console && console.log(a) } }, 200)
			}
		} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
	}; DEXT5._CK_[52] = "0"; DEXT5.setInsertHTML = DEXT5.SetInsertHTML = function(a, e) {
		if (void 0 !=
			a && "" != a) try {
				var c = DEXT5.util._setDEXT5editor(e); if (c) {
					var b = c._FRAMEWIN; b.restoreSelection(); b.setFocusToBody(); setTimeout(function() {
						0 < c.UndoManager.charCount && c.UndoManager.putUndo(); b.ReplaceImageToRealObject(); b.xssReplaceScript(b._iframeDoc); b.ClearDraggingTableAllTable(); if (!DEXT5.browser.chrome && !DEXT5.browser.opera) {
							var d = document.createElement("div"); d.innerHTML = a; d.lastChild && 1 == d.lastChild.nodeType && -1 < ",input,select,button,textarea,".indexOf("," + d.lastChild.tagName.toLowerCase() + ",") &&
								(a += unescape("%uFEFF"))
						} a = b.DEXT5_EDITOR.HTMLParser.RemoveOfficeTag2(a); a = b.externalPageBreakDataRaplaceInEditor(a); "1" == c._config.replaceEmptySpanTagInSetapi && (a = b.replaceEmptySpanTag(a)); a = b.removeEditorAttribute(a); try { b.pasteHtmlAtCaretHuge(a, !0, null, c.isTablePaste, null) } catch (e) { DEXT5 && DEXT5.logMode && console && console.log(e) } b.isViewMode(c) || (c._dextCustomDataMode = !0, b.ReplaceRealObjectToImage()); try { b.onChange({ editor: c, isPossibleDirty: !0 }) } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } c.UndoManager.putUndo();
						c.UndoManager.charCount = 0; c._iconEnable(""); try { "function" == typeof c._config.event.setInsertComplete ? c._config.event.setInsertComplete(c.ID) : DEXTTOP.dext_editor_set_insert_complete_event(c.ID) } catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) }
					}, 200)
				}
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
	}; DEXT5._CK_[53] = "1"; DEXT5.setInsertHTMLEx = DEXT5.SetInsertHTMLEx = function(a, e, c) {
		if (void 0 != a && "" != a) try {
			var b = DEXT5.util._setDEXT5editor(c), d = b._FRAMEWIN; b && (d.restoreSelection(), d.setFocusToBody(),
				setTimeout(function() {
					if ("" != e && (0 == e || 1 == e)) { var c = b._BODY; if (1 == DEXT5.browser.chrome || 1 == DEXT5.browser.opera) { var g = document.createElement("p"); g.innerHTML = "<br>"; c = g; 0 == e ? b._BODY.insertBefore(g, b._BODY.firstChild) : 1 == e && b._BODY.appendChild(g) } d.doSetCaretPosition(c, e) } 0 < b.UndoManager.charCount && b.UndoManager.putUndo(); d.ReplaceImageToRealObject(); d.xssReplaceScript(d._iframeDoc); d.ClearDraggingTableAllTable(); a = d.externalPageBreakDataRaplaceInEditor(a); a = d.removeEditorAttribute(a); if (1 == DEXT5.browser.chrome ||
						1 == DEXT5.browser.opera || 1 == DEXT5.browser.gecko) { 1 == DEXT5.browser.gecko && (c = document.createElement("div"), c.innerHTML = a, c.lastChild && 1 == c.lastChild.nodeType && -1 < ",input,select,button,textarea,".indexOf("," + c.lastChild.tagName.toLowerCase() + ",") && (a += unescape("%uFEFF"))); try { d._iframeDoc.execCommand("inserthtml", !1, a) } catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) } } else {
							c = document.createElement("div"); c.innerHTML = a; c.lastChild && 1 == c.lastChild.nodeType && -1 < ",input,select,button,textarea,".indexOf("," +
								c.lastChild.tagName.toLowerCase() + ",") && (a += unescape("%uFEFF")); try { d.pasteHtmlAtCaretHuge(a, !0) } catch (n) { DEXT5 && DEXT5.logMode && console && console.log(n) }
					} d.isViewMode(b) || (b._dextCustomDataMode = !0, d.ReplaceRealObjectToImage()); try { d.onChange({ editor: b, isPossibleDirty: !0 }) } catch (m) { DEXT5 && DEXT5.logMode && console && console.log(m) } b.UndoManager.putUndo(); b.UndoManager.charCount = 0; b._iconEnable(""); try { "function" == typeof b._config.event.setInsertComplete ? b._config.event.setInsertComplete(b.ID) : DEXTTOP.dext_editor_set_insert_complete_event(b.ID) } catch (t) {
						DEXT5 &&
						DEXT5.logMode && console && console.log(t)
					}
				}, 200))
		} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
	}; DEXT5._CK_[54] = "6"; DEXT5.setInsertText = DEXT5.SetInsertText = function(a, e) {
		if (void 0 != a && "" != a) try {
			var c = DEXT5.util._setDEXT5editor(e), b = c._FRAMEWIN; if (c) {
				b.dialogCancel(); var d = b.getFirstRange().range; d.collapse(!1); d.pasteText(a); d.collapse(!0); try { b.onChange({ editor: c, isPossibleDirty: !0 }) } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } c.UndoManager.putUndo(); setTimeout(function() {
					try {
						"function" ==
						typeof c._config.event.setInsertComplete ? c._config.event.setInsertComplete(c.ID) : DEXTTOP.dext_editor_set_insert_complete_event(c.ID)
					} catch (a) { DEXT5 && DEXT5.logMode && console && console.log(a) }
				}, 200)
			}
		} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
	}; DEXT5._CK_[55] = "3"; DEXT5.setEditorBorder = DEXT5.SetEditorBorder = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(e); if (c) {
				var b = c._FRAMEWIN; "string" === typeof a && (a = DEXT5.util.convertStringtoBoolean(a)); c._config.editorborder = a ? "1" : "0"; b.resizeEditor(c,
					!1); b.setEditorBorder(c)
			}
		} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
	}; DEXT5._CK_[56] = "8"; DEXT5.setEditorMode = DEXT5.SetEditorMode = function(a, e) {
		try {
			var c = !1, b = DEXT5.util._setDEXT5editor(e); if (b) if ("lightview" == b._config.mode) DEXTTOP.G_CURREDITOR = b, b._FRAMEWIN.lightViewFunc("setEditorMode", a, b); else {
				var d = b._FRAMEWIN, g = !1; if ("edit" == a && "edit" != b._config.mode) {
					b._BODY.contentEditable = !0; b._config.mode = "edit"; if ((DEXTTOP.DEXT5.browser.mobile || DEXT5.browser.iOS) && "1" == b._config.view_mode_auto_height) {
						var h =
							DEXTDOC.getElementById(b._config.holderID), k = DEXTDOC.getElementById("dext_frame_" + b.ID), l = k.contentWindow.document, n = l.getElementById("ue_editor_holder_" + b.ID); h.style.webkitOverflowScrolling = "touch"; n.style.webkitOverflowScrolling = "touch"; h.style.overflow = ""; n.style.overflow = ""
					} "0" == b._config.top_menu && (d.document.getElementById("dext_menu_" + b.ID).style.display = ""); if (3 > parseInt(b._config.tool_bar, 10)) {
						d.document.getElementById("dext_toolbar_" + b.ID).style.display = ""; if ("1" == b._config.tool_bar || "0" ==
							b._config.tool_bar) d.document.getElementById("dext_tab_tool2_" + b.ID).style.display = ""; if ("2" == b._config.tool_bar || "0" == b._config.tool_bar) d.document.getElementById("dext_tab_tool1_" + b.ID).style.display = ""
					} "0" == b._config.status_bar && (d.document.getElementById("dext_statusbar_" + b.ID).style.height = "23px", d.document.getElementById("dext_statusbar_" + b.ID).style.display = "", d.document.getElementById("div_resizebar_" + b.ID) && (d.document.getElementById("div_resizebar_" + b.ID).style.display = "")); "0" == b._config.top_status_bar &&
						(d.document.getElementById("dext_topstatusbar_" + b.ID).style.height = "28px", d.document.getElementById("dext_topstatusbar_" + b.ID).style.display = ""); if ("2" == b._config.horizontalLine.use) { var m = d.document.getElementById("dext_horizontal_line_" + b.ID); m && (m.style.display = "") } g = !0; b._BODY.style.cursor = "text"
				} else if (("view" == a || "lightview" == a) && "view" != b._config.mode) {
					b._BODY.contentEditable = !1; a = b._config.mode = "view"; (DEXTTOP.DEXT5.browser.mobile || DEXTTOP.DEXT5.browser.iOS) && "1" == b._config.view_mode_auto_height &&
						(h = d.DEXTTOP.DEXTDOC.getElementById(b._config.holderID), k = DEXTTOP.DEXTDOC.getElementById("dext_frame_" + b.ID), l = k.contentWindow.document, n = l.getElementById("ue_editor_holder_" + b.ID), h.style.webkitOverflowScrolling = "touch", n.style.webkitOverflowScrolling = "touch", h.style.overflow = "hidden", n.style.overflow = "hidden"); "0" == b._config.top_menu && (d.document.getElementById("dext_menu_" + b.ID).style.display = "none"); if (3 > parseInt(b._config.tool_bar, 10)) {
							d.document.getElementById("dext_toolbar_" + b.ID).style.display =
							"none"; if ("1" == b._config.tool_bar || "0" == b._config.tool_bar) d.document.getElementById("dext_tab_tool2_" + b.ID).style.display = "none"; if ("2" == b._config.tool_bar || "0" == b._config.tool_bar) d.document.getElementById("dext_tab_tool1_" + b.ID).style.display = "none"
						} "0" == b._config.status_bar && (d.document.getElementById("dext_statusbar_" + b.ID).style.height = "1px", d.document.getElementById("dext_statusbar_" + b.ID).style.display = "none", d.document.getElementById("div_resizebar_" + b.ID) && (d.document.getElementById("div_resizebar_" +
							b.ID).style.display = "none")); "0" == b._config.top_status_bar && (d.document.getElementById("dext_topstatusbar_" + b.ID).style.height = "1px", d.document.getElementById("dext_topstatusbar_" + b.ID).style.display = "none"); "2" == b._config.horizontalLine.use && (m = d.document.getElementById("dext_horizontal_line_" + b.ID)) && (m.style.display = "none"); c = g = !0; b._BODY.style.cursor = "auto"
				} if (g) {
					d.resizeEditor(null, !1); var t = DEXT5.getHtmlValueEx(b.ID); DEXT5.setHtmlValueEx(t, b.ID, c); "view" == a ? (d.hideRuler(b), 0 == DEXT5.browser.ie &&
						DEXTTOP.focus(), DEXTDOC.body.focus(), "" != b._config.placeholder.content && d.placeholderControl(b, "remove")) : (d.G_Ruler && d.G_Ruler.viewRulerStatus && d.G_Ruler.SetRulerPosition(), "" != b._config.placeholder.content && d.placeholderControl(b, "set"))
				} d.setEditorIframeTitle(b); d.groupingIcon(); d.setEditorBorder(b)
			}
		} catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) }
	}; DEXT5._CK_[57] = "4"; DEXT5.editorPrint = DEXT5.EditorPrint = function(a) {
		try {
			var e = DEXT5.util._setDEXT5editor(a); if (e) try {
				"ieplugin" == e._config.runtimes ?
				DEXT5.doPrint("", "", a) : e._FRAMEWIN.command_print("", e)
			} catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) }
		} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
	}; DEXT5._CK_[58] = "7"; DEXT5.doPrint = DEXT5.DoPrint = function(a, e, c) {
		try {
			var b = DEXT5.util._setDEXT5editor(c); if (b) if ("ieplugin" == b._config.runtimes) {
				var d = b._config.printPreview; b._config.printPreview = "1"; b.setChangeMode("preview"); setTimeout(function() {
					var c; c = b._EDITOR.preview.contentDocument ? b._EDITOR.preview.contentDocument : b._EDITOR.preview.contentWindow.document;
					DEXT5.browser.ie && (c = c.getElementById("dext5printpreview")) && c.doPrintDirect(a, e, ""); b.setChangeMode("design")
				}, 350); b._config.printPreview = d
			} else DEXT5.editorPrint(c)
		} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
	}; DEXT5._CK_[59] = "2"; DEXT5.doPrintPreview = DEXT5.DoPrintPreview = function(a, e, c) {
		try {
			var b = DEXT5.util._setDEXT5editor(c); if (b) if (G_CURREDITOR._FRAMEWIN.G_dext5plugIn) {
				var d = b._config.printPreview; b._config.printPreview = "1"; b.setChangeMode("preview"); setTimeout(function() {
					var c; c =
						b._EDITOR.preview.contentDocument ? b._EDITOR.preview.contentDocument : b._EDITOR.preview.contentWindow.document; DEXT5.browser.ie && (c = c.getElementById("dext5printpreview")) && c.doPrintPreview(a, e, ""); b.setChangeMode("design")
				}, 350); b._config.printPreview = d
			} else DEXT5.editorPrint(c)
		} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
	}; DEXT5._CK_[60] = "5"; DEXT5.doSaveHTML = DEXT5.DoSaveHTML = function(a, e, c, b) {
		var d = ""; try {
			DEXT5.util._setDEXT5editor(b) && G_CURREDITOR._FRAMEWIN.G_dext5plugIn && (d = G_CURREDITOR._FRAMEWIN.G_dext5plugIn.doSaveHTML(a,
				e, c, ""))
		} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } return d
	}; DEXT5.doSaveHTMLEx = DEXT5.DoSaveHTMLEx = function(a, e) { var c = ""; try { DEXT5.util._setDEXT5editor(e) && G_CURREDITOR._FRAMEWIN.G_dext5plugIn && (c = G_CURREDITOR._FRAMEWIN.G_dext5plugIn.doSaveHTML(a.strHtml, a.strSavePath, "string" == typeof a.strCharSet && "" != a.strCharSet ? a.strCharSet : "utf-8", !0 === a.bNotOpenDialog ? "hide" : "")) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return c }; DEXT5._CK_[61] = "9"; DEXT5.addUserCssUrl = DEXT5.AddUserCssUrl =
		function(a, e) { try { var c = DEXT5.util._setDEXT5editor(e); if (c) { var b = c._FRAMEWIN, d = c._DOC.getElementsByTagName("head")[0]; d && b.createDynamicCssLinkToHeadTag(c._DOC, d, a) } } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } }; DEXT5._CK_[62] = "+"; DEXT5.clearUserCssUrl = DEXT5.ClearUserCssUrl = function(a, e) { try { var c = DEXT5.util._setDEXT5editor(e); if (c) { var b = c._FRAMEWIN, d = c._DOC.getElementsByTagName("head")[0]; d && b.clearDynamicCssLinkToHeadTag(d, a) } } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } };
	DEXT5.addWebFontCssUrl = DEXT5.AddWebFontCssUrl = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(e); if (c) {
				var b = c._FRAMEWIN, d = c._DOC.getElementsByTagName("head")[0]; if (d) {
					a = DEXT5.util.setProtocolBaseDomainURL(a); b.createDynamicCssLinkToHeadTag(c._DOC, d, a); var g = b.getDialogWindow().document.getElementById("dext_fontfamily_iframe_" + c.ID); g ? b.createDynamicCssLinkToHeadTag(g.contentWindow.document, g.contentWindow.document.getElementsByTagName("head")[0], a) : c._config.webFontCssUrl = a; b.setFocusToBody();
					try { setTimeout(function() { var a = b.getFirstRange().range, c = null; a && a.startContainer && (c = b.getMyElementNode(a.startContainer), b.setMenuIconRealable(c)) }, 10) } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
				}
			}
		} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) }
	}; DEXT5._CK_[63] = "/"; DEXT5.setUserCssText = DEXT5.SetUserCssText = function(a, e) {
		try { var c = DEXT5.util._setDEXT5editor(e); if (c) { var b = c._FRAMEWIN, d = c._DOC.getElementsByTagName("head")[0]; d && b.addDynamicCssToHeadTag(c._DOC, d, a) } } catch (g) {
			DEXT5 &&
			DEXT5.logMode && console && console.log(g)
		}
	}; DEXT5._CK_[64] = "="; DEXT5.clearUserCssText = DEXT5.ClearUserCssText = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); if (e) { var c = e._FRAMEWIN, b = e._DOC.getElementsByTagName("head")[0]; b && c.addDynamicCssToHeadTag(e._DOC, b, "") } } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5.addUserJsUrl = DEXT5.AddUserJsUrl = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(e); if (c) {
				a = DEXT5.util.setProtocolBaseDomainURL(a); var b = !1; if ("1" == c._config.xss_use) for (var d =
					a.toLowerCase(), g = 0; g < c._config.xss_allow_url.length; g++) { if (c._config.xss_allow_url[g].toLowerCase() == d) { b = !0; break } } else b = !0; if (1 == b) { var h = c._FRAMEWIN, k = c._DOC.getElementsByTagName("head")[0]; k && (h.createDynamicJsLinkToHeadTag(c._DOC, k, a), h.setFocusToBody()) }
			}
		} catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) }
	}; DEXT5.clearUserJsUrl = DEXT5.ClearUserJsUrl = function(a) {
		try {
			var e = DEXT5.util._setDEXT5editor(a); if (e) {
				var c = e._FRAMEWIN, b = e._DOC.getElementsByTagName("head")[0]; b && (c.clearDynamicJsLinkToHeadTag(b),
					c.setFocusToBody())
			}
		} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
	}; DEXT5.setUserJsText = DEXT5.SetUserJsText = function(a, e) { try { var c = DEXT5.util._setDEXT5editor(e); if (c) { var b = c._FRAMEWIN, d = c._DOC.getElementsByTagName("head")[0]; d && (b.addDynamicJsToHeadTag(c._DOC, d, a), b.setFocusToBody()) } } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } }; DEXT5.clearUserJsText = DEXT5.ClearUserJsText = function(a) {
		try {
			var e = DEXT5.util._setDEXT5editor(a); if (e) {
				var c = e._FRAMEWIN, b = e._DOC.getElementsByTagName("head")[0];
				b && (c.addDynamicJsToHeadTag(e._DOC, b, ""), c.setFocusToBody())
			}
		} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
	}; DEXT5.setNextTabElementId = DEXT5.SetNextTabElementId = function(a, e) { try { var c = DEXT5.util._setDEXT5editor(e); c && (c._config.NextTabElementId = a) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5.setFullScreen = DEXT5.SetFullScreen = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.command_fullScreen(e.ID, e) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } };
	DEXT5.loadAutoSaveHtml = DEXT5.LoadAutoSaveHtml = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); if (e) { var c = e._FRAMEWIN.getAutoSaveHtml_userID(a, e._config.setAutoSave.unique_key, e._config.setAutoSave.maxCount); c && "" != c && DEXT5.setHtmlValueExWithDocType(c[c.length - 1].html, a) } } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5.saveCurrValueInMultiValue = DEXT5.SaveCurrValueInMultiValue = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(a); if (c) {
				var b = DEXT5.util.getValueByMultiMode(), d = [];
				d.push(b); for (b = 2; b < arguments.length; b++)d.push(arguments[b]); c.changeMultiValue[e] || (c.changeMultiValue.valueLength += 1); c.changeMultiValue[e] = d
			}
		} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
	}; DEXT5.setNextValueInMultiValue = DEXT5.SetNextValueInMultiValue = function(a, e) {
		var c = null; try {
			var b = DEXT5.util._setDEXT5editor(a); if (b) {
				if (b.changeMultiValue[e]) { var d = b.changeMultiValue[e][0]; DEXT5.util.setValueByMultiMode(d) } else d = "<p><br></p>", DEXT5.browser.ie && 11 > DEXT5.browser.ieVersion && (d = "<p>&nbsp</p>"),
					DEXT5.setBodyValue(d); c = b.changeMultiValue[e] ? b.changeMultiValue[e] : null
			}
		} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } return c
	}; DEXT5.getMultiValue = DEXT5.GetMultiValue = function(a, e) { var c = null; try { var b = DEXT5.util._setDEXT5editor(a); b && (c = b.changeMultiValue[e]) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } return c }; DEXT5.getMultiValueLength = DEXT5.GetMultiValueLength = function(a) {
		var e = null; try { var c = DEXT5.util._setDEXT5editor(a); c && (e = c.changeMultiValue.valueLength) } catch (b) {
			DEXT5 &&
			DEXT5.logMode && console && console.log(b)
		} return e
	}; DEXT5.setEditorChangeMode = DEXT5.SetEditorChangeMode = function(a, e) { try { var c = DEXT5.util._setDEXT5editor(e); c && c.setChangeMode(a) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5.setUserFontFamily = DEXT5.SetUserFontFamily = function(a, e) {
		try {
			if (void 0 != a && null != a && "" != a) {
				var c = DEXT5.util._setDEXT5editor(e); if (c && ("\ub9d1\uc740\uace0\ub515" == a && (a = "\ub9d1\uc740 \uace0\ub515"), c._DOC.body.style.fontFamily = a, "1" == c._setting.show_font_real)) {
					var b =
						document.getElementById("ue_" + c.ID + "font_family"); b && (b.innerHTML = "<span>" + a + "</span>", c._setting.font_family = a)
				}
			}
		} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
	}; DEXT5.setUserFontSize = DEXT5.SetUserFontSize = function(a, e) {
		try { if (void 0 != a && null != a && "" != a) { var c = DEXT5.util._setDEXT5editor(e); if (c && (c._DOC.body.style.fontSize = a + "pt", "1" == c._setting.show_font_real)) { var b = document.getElementById("ue_" + c.ID + "font_size"); b && (b.innerHTML = "<span>" + a + "pt</span>", c._setting.font_size = a + "pt") } } } catch (d) {
			DEXT5 &&
			DEXT5.logMode && console && console.log(d)
		}
	}; DEXT5.setFocusToEditor = DEXT5.SetFocusToEditor = function(a) {
		try {
			var e = DEXT5.util._setDEXT5editor(a); e && (e.isInitFocusHandler = !1, e.setFocusToBody(), setTimeout(function() {
				var a = e._FRAMEWIN, c = a.getFirstRange().range; c && c.startContainer == c.endContainer && 1 == c.collapsed && ((elem = a.getMyElementNode(c.startContainer)) && elem.tagName && "p" == elem.tagName.toLowerCase() && ("" == elem.innerHTML || "<br>" == elem.innerHTML.toLowerCase()) ? a.doSetCaretPosition(elem, 0) : !elem || elem && elem.tagName &&
					"body" == elem.tagName.toLowerCase() ? ((c = e._BODY.firstChild) && "" == c.innerHTML && (c.innerHTML = "<br>"), a.doSetCaretPosition(c, 0)) : a.setFocusToBody()); a.checkDefaultMessage()
			}, 1))
		} catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) }
	}; DEXT5.setRulerPosition = DEXT5.SetRulerPosition = function(a, e) { if (void 0 != a && "" != a) try { var c = DEXT5.util._setDEXT5editor(e); if (c) { var b = c._FRAMEWIN; 1 == c._config.ruler.use && b.G_Ruler.SetRulerPositionApi(a) } } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5.setClassStyle =
		DEXT5.SetClassStyle = function(a, e) { try { var c = DEXT5.util._setDEXT5editor(a); c && c._FRAMEWIN.command_setClassStyle(c, e) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5.removeClassStyle = DEXT5.RemoveClassStyle = function(a, e) { try { var c = DEXT5.util._setDEXT5editor(a); c && c._FRAMEWIN.command_removeClassStyle(c, e) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5.inImage = DEXT5.InImage = function(a, e) {
			if (void 0 != a && "" != a) try {
				var c = DEXT5.util._setDEXT5editor(e); c && (c.setFocusToBody(), c._DOC.execCommand("InsertImage",
					!1, a))
			} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
		}; DEXT5.setFontFamily = DEXT5.SetFontFamily = function(a, e) { if (void 0 != a && "" != a) try { var c = DEXT5.util._setDEXT5editor(e); c && c._FRAMEWIN.command_fontfamily(c.ID, c._EDITOR.design, a) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5.setFontSize = DEXT5.SetFontSize = function(a, e) {
			if (void 0 != a && "" != a) try { var c = DEXT5.util._setDEXT5editor(e); c && c._FRAMEWIN.command_fontsize(c.ID, c._EDITOR.design, a) } catch (b) {
				DEXT5 && DEXT5.logMode && console &&
				console.log(b)
			}
		}; DEXT5.setFontFormat = DEXT5.SetFontFormat = function(a, e) {
			if (void 0 != a && "" != a) try {
				var c = DEXT5.util._setDEXT5editor(e); if (c) {
					var b = c._FRAMEWIN; switch (a) {
						case "bold": b.command_bold(c.ID, c); break; case "underline": b.command_underline(c.ID, c); break; case "italic": b.command_italic(c.ID, c); break; case "strike_through": b.command_strikeThrough(c.ID, c); break; case "superscript": b.command_superscript(c.ID, c); break; case "subscript": b.command_subscript(c.ID, c); break; case "remove_format": b.command_removeFormat(c.ID,
							c)
					}
				}
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
		}; DEXT5.setFontColor = DEXT5.SetFontColor = function(a, e) { if (void 0 != a && "" != a) try { var c = DEXT5.util._setDEXT5editor(e); c && c._FRAMEWIN.command_fontColor(c.ID, c._EDITOR.design, a) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5.setFontBgColor = DEXT5.SetFontBgColor = function(a, e) {
			if (void 0 != a && "" != a) try { var c = DEXT5.util._setDEXT5editor(e); c && c._FRAMEWIN.command_fontBgColor(c.ID, c._EDITOR.design, a) } catch (b) {
				DEXT5 && DEXT5.logMode && console &&
				console.log(b)
			}
		}; DEXT5.selectAll = DEXT5.SelectAll = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.command_selectAll(e.ID, e) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.removeCss = DEXT5.RemoveCss = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.command_removeCss(e.ID, e) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.setOriginalHtmlValue = DEXT5.SetOriginalHtmlValue = function(a, e) {
			try {
				var c = DEXT5.util.parseSetApiParam(a); a = c.html; e =
					DEXT5.util._getEditorName(e); if (null != e) if (DEXT5.isLoadedEditorEx(e)) try { var b = DEXT5.util._setDEXT5editor(e); if (b) if ("lightview" == b._config.mode) DEXTTOP.G_CURREDITOR = b, b._FRAMEWIN.lightViewFunc("setOriginalHtmlValue", a, b); else { var d = b._FRAMEWIN; 1 == d.isViewMode(b) && (d.setGlobalTableDefaultValue(), d.FixFlashError2(d._iframeDoc), d._iframeDoc.open("text/html", "replace"), d.isCustomDomain(document) && (d._iframeDoc.domain = document.domain), d._iframeDoc.write(a), d._iframeDoc.close(), b._load_editor_frame(!1)) } } catch (g) {
						DEXT5 &&
						DEXT5.logMode && console && console.log(g)
					} else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setOriginalHtmlValue", value: c })
			} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
		}; DEXT5.getDeletedImageUrl = DEXT5.GetDeletedImageUrl = function(a, e) {
			var c = []; try {
				var b = DEXT5.util._setDEXT5editor(a); if (b) {
					"string" === typeof e && (e = DEXT5.util.convertStringtoBoolean(e)); var d = b._FRAMEWIN, g = b.setImageArr, h = g.length, k =
						b.getEditorMode(); "source" != k && "text" != k || b.setChangeMode("design"); d.ReplaceImageToRealObject(); 1 != e && d.changeBodyContenteditable(!1); d.changeBodyImageProperty(!0); var l = b._DOC.body.outerHTML; d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0); d.changeBodyImageProperty(!1); d.ReplaceRealObjectToImage(); d.changeBodyContenteditable(!0); for (b = 0; b < h; b++)0 > l.indexOf(g[b]) && c.push(g[b])
				}
			} catch (n) { c = null } return c
		}; DEXT5.getDeletedElementsUrl = DEXT5.GetDeletedElementsUrl = function(a, e) {
			var c =
				[]; try {
					var b = DEXT5.util._setDEXT5editor(a); if (b) {
						var d = b._FRAMEWIN, g = b.getEditorMode(); "source" != g && "text" != g || b.setChangeMode("design"); d.ReplaceImageToRealObject(); 1 != e && d.changeBodyContenteditable(!1); d.changeBodyImageProperty(!0); var h = b._DOC.body.outerHTML; d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0); d.changeBodyImageProperty(!1); d.ReplaceRealObjectToImage(); d.changeBodyContenteditable(!0); for (var k = b.setElementsArr, l = k.length, g = 0; g < l; g++)0 > h.indexOf(k[g]) && c.push(k[g]);
						d.G_BodyFit.noncreationAreaBackgroundStatus && d.setBodyFitStyle(b, !0)
					}
				} catch (n) { c = null } return c
		}; DEXT5.showTopMenu = DEXT5.ShowTopMenu = function(a, e) {
			try { var c = DEXT5.util._setDEXT5editor(e); if (c) { var b = c._FRAMEWIN; 0 == b.isViewMode(c) && ("0" == a ? (b.document.getElementById("dext_menu_" + c.ID).style.display = "none", c._config.top_menu = "1") : (b.document.getElementById("dext_menu_" + c.ID).style.display = "", c._config.top_menu = "0"), b.setEditorIframeTitle(c), b.resizeEditor(c, !1), b.setEditorBorder(c)) } } catch (d) {
				DEXT5 &&
				DEXT5.logMode && console && console.log(d)
			}
		}; DEXT5.showToolbar = DEXT5.ShowToolbar = function(a, e) {
			try {
				var c = DEXT5.util._setDEXT5editor(e); if (c) {
					var b = c._FRAMEWIN; 0 == b.isViewMode(c) && ("0" == a ? (b.document.getElementById("dext_toolbar_" + c.ID).style.display = "none", c._config.tool_bar = "3") : "1" == a ? (b.document.getElementById("dext_tab_tool1_" + c.ID).style.display = "", b.document.getElementById("dext_tab_tool2_" + c.ID).style.display = "none", b.document.getElementById("dext_toolbar_" + c.ID).style.display = "", c._config.tool_bar =
						"2", "1" == c._config.tool_bar_grouping && (b.groupingIcon(c), b.setPositionGroupingDiv(1))) : "2" == a ? (b.document.getElementById("dext_tab_tool1_" + c.ID).style.display = "none", b.document.getElementById("dext_tab_tool2_" + c.ID).style.display = "", b.document.getElementById("dext_toolbar_" + c.ID).style.display = "", c._config.tool_bar = "1", "1" == c._config.tool_bar_grouping && (b.groupingIcon(c), b.setPositionGroupingDiv(2))) : (b.document.getElementById("dext_tab_tool1_" + c.ID).style.display = "", b.document.getElementById("dext_tab_tool2_" +
							c.ID).style.display = "", b.document.getElementById("dext_toolbar_" + c.ID).style.display = "", c._config.tool_bar = "0", "1" == c._config.tool_bar_grouping && (b.groupingIcon(c), b.setPositionGroupingDiv(1), b.setPositionGroupingDiv(2))), b.setEditorIframeTitle(c), b.resizeEditor(c, !1), b.setEditorBorder(c))
				}
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
		}; DEXT5.showStatusbar = DEXT5.ShowStatusbar = function(a, e) {
			try {
				var c = DEXT5.util._setDEXT5editor(e); if (c) {
					var b = c._FRAMEWIN; 0 == b.isViewMode(c) && ("0" == a ? (b.document.getElementById("dext_statusbar_" +
						c.ID).style.display = "none", c._config.status_bar = "1") : (b.document.getElementById("dext_statusbar_" + c.ID).style.display = "", c._config.status_bar = "0"), b.setEditorIframeTitle(c), b.resizeEditor(c, !1), b.setEditorBorder(c))
				}
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
		}; DEXT5.setHtmlContents = DEXT5.SetHtmlContents = function(a, e) { DEXT5.setHtmlContentsEw(a, e); try { G_CURREDITOR._FRAMEWIN.onChange({ editor: DEXTTOP.G_CURREDITOR }) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.isLoadedEditor =
			DEXT5.IsLoadedEditor = function(a) { var e = null; try { var c = DEXT5.util._setDEXT5editor(a); c && (e = c._config.isLoadedEditor) } catch (b) { e = !1 } return e }; DEXT5.setHtmlContentsEw = DEXT5.SetHtmlContentsEw = function(a, e) {
				try {
					var c = DEXT5.util.parseSetApiParam(a); e = DEXT5.util._getEditorName(e); if (null != e) if (DEXT5.isLoadedEditorEx(e)) try {
						if (DEXT5.util._setDEXT5editor(e)) {
							var b = c.html.toLowerCase(), d, g, h, k; d = g = h = k = -1; d = b.indexOf("<!doctype"); g = b.indexOf("<html"); h = b.indexOf("<head"); 0 > h && (h = b.indexOf("<meta")); 0 > h && (h =
								b.indexOf("<title")); k = b.indexOf("<body"); -1 < d && d < g && g < k ? DEXT5.setHtmlValueExWithDocType(c, e) : -1 < g && g < k ? DEXT5.setHtmlValueEx(c, e) : -1 < h && h < k ? DEXT5.setHtmlValue(c, e) : -1 < k ? DEXT5.setBodyValueEx(c, e) : DEXT5.setBodyValue(c, e)
						}
					} catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) } else null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setHtmlContents", value: c })
				} catch (n) { DEXT5 && DEXT5.logMode && console && console.log(n) }
			};
	DEXT5.isLoadedEditorEx = DEXT5.IsLoadedEditorEx = function(a) {
		var e = !1; try { var c = document.getElementById("dext_frame_" + a); if (c && c.contentWindow.document.getElementById("editorContentArea") && DEXT5.IsEditorLoadedHashTable) { var b = DEXT5.IsEditorLoadedHashTable.getItem(a); "undefined" != typeof b && "1" == b && (e = !0) } if (!e && DEXT5.IsEditorLoadedHashTable) try { b = DEXT5.IsEditorLoadedHashTable.getItem(a), "undefined" != typeof b && DEXT5.IsEditorLoadedHashTable.setItem(a, "0") } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } } catch (g) {
			DEXT5 &&
			DEXT5.logMode && console && console.log(g)
		} return e
	}; DEXT5.setHeightForDisplay = DEXT5.SetHeightForDisplay = function(a) {
		try { var e = DEXT5.util._setDEXT5editor(a); if (e && "view" == e._config.mode) if ("1" == e._config.view_mode_auto_height || "1" == e._config.view_mode_auto_width) if (DEXT5.isEmpty()) DEXT5.setSize(e._config.style.width, e._config.style.height, e.ID); else { var c = DEXT5.getHtmlValueExWithDocType(e.ID); DEXT5.setHtmlValueExWithDocType(c, e.ID) } else DEXT5.setSize(e._config.style.width, e._config.style.height, e.ID) } catch (b) {
			DEXT5 &&
			DEXT5.logMode && console && console.log(b)
		}
	}; DEXT5.setFocusToObject = DEXT5.SetFocusToObject = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(e); if (c) {
				var b = c._FRAMEWIN; c.isInitFocusHandler = !1; var d = null; "string" == typeof a ? d = c._DOC.getElementById(a) : "object" == typeof a && (d = a); if (null != d && void 0 != d) {
					if (DEXT5.browser.ie && d.firstChild && 3 != d.firstChild.nodeType) for (d = d.firstChild; d;)if (1 == d.nodeType && d.firstChild && 3 != d.firstChild.nodeType) d = d.firstChild; else break; d && (!d.tagName || "IMG" != d.tagName && "BR" != d.tagName ||
						(d = d.parentNode), b.setFocusToBody(), setTimeout(function() {
							try {
								if (!d.tagName || "TEXTAREA" != d.tagName && "INPUT" != d.tagName) {
									var a = b._iframeDoc.body.clientHeight, c = b._iframeDoc.body.clientWidth, e = b._iframeDoc.body.scrollTop, g = e + a, m = b._iframeDoc.body.scrollLeft, t = m + c; b._iframeWin.scroll(0, 0); var q = b.getClientRect(d); e > q.top ? (b._iframeWin.scroll(m, q.top), e = b._iframeDoc.body.scrollTop) : g < q.top && (a = 50 - a, b._iframeWin.scroll(m, q.top + (0 > a ? a : 0)), e = b._iframeDoc.body.scrollTop); m > q.left ? b._iframeWin.scroll(q.left,
										e) : t < q.left ? (c = 50 - c, b._iframeWin.scroll(q.left + (0 > c ? c : 0), e)) : b._iframeWin.scroll(m, e); b.doSetCaretPosition(d, !1)
								} else d.focus()
							} catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) }
						}, 1))
				}
			}
		} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
	}; DEXT5.getEditorSize = DEXT5.GetEditorSize = function(a) { var e = null; try { var c = DEXT5.util._setDEXT5editor(a); c && (e = { width: c._config.style.width, height: c._config.style.height }) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5.isDirty = DEXT5.IsDirty =
		function(a) {
			try {
				var e = DEXT5.util._setDEXT5editor(a); if (e) {
					if (1 == e._FRAMEWIN.G_IsPossibleDirty) {
						var c = e._BODY.innerHTML, c = c.replace(/\r?\n?\r?\n/g, ""), c = c.replace(/>\t+</g, "><"), c = c.replace(/> +</g, "><"), c = c.replace(/&nbsp;/g, " "), c = DEXT5.util.nbspRemove(c), b = e._editingCheckedValue; "" == b && (b = e._FRAMEWIN.G_BODY_DEFAULT_VALUE); b = b.replace(/\r?\n?\r?\n/g, ""); b = b.replace(/>\t+</g, "><"); b = b.replace(/> +</g, "><"); b = b.replace(/&nbsp;/g, " "); b = DEXT5.util.nbspRemove(b); return c == b ? e._FRAMEWIN.G_IsPossibleDirty =
							!1 : !0
					} return !1
				}
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
		}; DEXT5.setDirty = DEXT5.SetDirty = function(a, e) {
			try {
				"undefined" != typeof arguments && 1 == arguments.length && (e = a); var c = DEXT5.util._setDEXT5editor(e); c && (c._FRAMEWIN.G_IsPossibleDirty = !0, "undefined" != typeof arguments && 2 <= arguments.length ? ("undefined" === typeof a ? a = c._BODY.innerHTML : -1 < a.indexOf("<body") && (a = a.substring(a.indexOf("<body") + 5), a = a.substring(a.indexOf("<")), a = a.substring(0, a.indexOf("</body>"))), "" == a && (a = c._FRAMEWIN.G_BODY_DEFAULT_VALUE),
					c._editingCheckedValue = a) : c._editingCheckedValue = c._BODY.innerHTML)
			} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
		}; DEXT5.getAccessibilityValidation = DEXT5.GetAccessibilityValidation = function(a) { var e = !1; try { var c = DEXT5.util._setDEXT5editor(a); if (c) { var b = c._FRAMEWIN, d = c.getEditorMode(); "source" != d && "text" != d || c.setChangeMode("design"); var g = b.getViolationNodes(c), e = 0 < g.violateNodes.length || 0 < g.idSamNodes.length ? !1 : !0 } } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } return e }; DEXT5.setAccessibilityValidation =
			DEXT5.SetAccessibilityValidation = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); if (e) { var c = e._FRAMEWIN, b = e.getEditorMode(); "source" != b && "text" != b || e.setChangeMode("design"); c.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.accessibility_validation) } } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5.setEditorBodyEditable = DEXT5.SetEditorBodyEditable = function(a, e) {
				try {
					var c = DEXT5.util._setDEXT5editor(e); c && ("string" === typeof a && (a = DEXT5.util.convertStringtoBoolean(a)),
						a ? (c._BODY.contentEditable = !0, c._config.editorBodyEditable = !0, c._iconEnable("default")) : (c._BODY.contentEditable = !1, c._config.editorBodyEditable = !1, c._iconEnable("editableFalse"), c.dext_dragresize && c.dext_dragresize.resizeHandleClear()))
				} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
			}; DEXT5.getEditorStyle = DEXT5.GetEditorStyle = function(a, e, c) { var b = null; try { DEXT5.util._setDEXT5editor(c) && (b = DEXT5.util.getStyle(a, e)) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } return b }; DEXT5.setAdjustTableBorderStyle =
				DEXT5.SetAdjustTableBorderStyle = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.setAdjustTableBorder(e._DOC) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.getPersonalDataValidation = DEXT5.GetPersonalDataValidation = function(a) {
					var e = !1; try { var c = DEXT5.util._setDEXT5editor(a); if (c) { var b = c._FRAMEWIN, d = c.getEditorMode(); "source" != d && "text" != d || c.setChangeMode("design"); var g = b.getPersonalData(c), h; for (h in g) if (g[h]) { e = !0; break } } } catch (k) {
						DEXT5 && DEXT5.logMode && console &&
						console.log(k)
					} return e
				}; DEXT5.setPersonalDataValidation = DEXT5.SetPersonalDataValidation = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); if (e) { var c = e._FRAMEWIN, b = e.getEditorMode(); "source" != b && "text" != b || e.setChangeMode("design"); c.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.personal_data) } } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5.getForbiddenWordValidation = DEXT5.GetForbiddenWordValidation = function(a) {
					var e = !1; try {
						var c = DEXT5.util._setDEXT5editor(a);
						if (c) { var b = c._FRAMEWIN, d = c.getEditorMode(); "source" != d && "text" != d || c.setChangeMode("design"); e = b.getForbiddenWord(c) ? !0 : !1 }
					} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } return e
				}; DEXT5.setForbiddenWordValidation = DEXT5.SetForbiddenWordValidation = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); if (e) { var c = e._FRAMEWIN, b = e.getEditorMode(); "source" != b && "text" != b || e.setChangeMode("design"); c.command_forbidden_word(e.ID, e, "API") } } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5.getCaretElement =
					DEXT5.GetCaretElement = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); if (e) { var c, b = e._FRAMEWIN, d = b.getFirstRange(), g = d.range; if (g && g.startContainer) if ((c = d.sel.focusNode) && 3 == c.nodeType) for (; 1 != c.nodeType;)if (c.parentNode) c = c.parentNode; else break; else c = b.getMyElementNode(g.startContainer); return c } } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } }; DEXT5.getCaretElementEx = DEXT5.GetCaretElementEx = function(a) {
						try {
							var e = DEXT5.util._setDEXT5editor(a); if (e) {
								var c = e._FRAMEWIN; return c.G_CURSOR_ELEMENT &&
									"html" != c.G_CURSOR_ELEMENT.nodeName.toLowerCase() && "body" != c.G_CURSOR_ELEMENT.nodeName.toLowerCase() ? c.G_CURSOR_ELEMENT : DEXT5.getCaretElement(a)
							}
						} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
					}; DEXT5.setCaretMousePosition = DEXT5.SetCaretMousePosition = function(a, e) {
						try {
							var c = DEXT5.util._setDEXT5editor(e); if (c) {
								var b = c._FRAMEWIN; if (DEXTTOP.DEXT5.browser.ie && 0 == DEXTTOP.DEXT5.browser.quirks && 7 != DEXTTOP.DEXT5.browser.ieVersion) {
									var d = b.getFirstRange().range; d && 1 == d.collapsed && b._iframeDoc.body.createTextRange &&
										(d = b._iframeDoc.body.createTextRange(), d.moveToPoint(a.X, a.Y), d.select()); b.setFocusToBody()
								} else if (DEXTTOP.DEXT5.browser.chrome || DEXTTOP.DEXT5.browser.opera) { if (1 == b.getFirstRange().range.collapsed) try { b.createSelectionFromPoint(a.X, a.Y, a.X, a.Y), b.setFocusToBody() } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } } else b.setFocusToBody(); if ((elem = b.getFirstRange().sel.focusNode) && 3 == elem.nodeType) for (; 1 != elem.nodeType;)if (elem.parentNode) elem = elem.parentNode; else break; else (d = b.getFirstRange().range) &&
									d.startContainer == d.endContainer && 1 == d.collapsed && (elem = b.getMyElementNode(d.startContainer)); b.setMenuIconRealable(elem)
							}
						} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
					}; DEXT5.setLockCommand = DEXT5.SetLockCommand = function(a, e, c) {
						try { var b = DEXT5.util._setDEXT5editor(c); if (b) { var d = "," + b.lockCommand.join(",") + ","; e ? 0 > d.indexOf("," + a + ",") && b.lockCommand.push(a) : (d = d.replace("," + a + ",", ","), d = "," == d ? "" : d.substring(1, d.length - 1), b.lockCommand = d.split(",")); b._iconEnable("") } } catch (g) {
							DEXT5 && DEXT5.logMode &&
							console && console.log(g)
						}
					}; DEXT5.setDext5DomMode = DEXT5.SetDext5DomMode = function(a, e) {
						try { var c = DEXT5.util._setDEXT5editor(e); if (c) { var b = c._FRAMEWIN; if (1 == a) { var d = c.getEditorMode(); "source" != d && "text" != d || c.setChangeMode("design"); b.ReplaceImageToRealObject(); b.ClearDraggingTableAllTable(); b.changeBodyImageProperty(!0); b.G_BodyFit.noncreationAreaBackgroundStatus && b.setBodyFitStyle(c, !0) } else c._PageProp.bshowgrid && 1 == c._PageProp.bshowgrid && b.changeBodyImageProperty(!1), b.ReplaceRealObjectToImage() } } catch (g) {
							DEXT5 &&
							DEXT5.logMode && console && console.log(g)
						}
					}; DEXT5.getDext5DocumentDom = DEXT5.GetDext5DocumentDom = function(a) { var e = null; try { var c = DEXT5.util._setDEXT5editor(a); c && (e = c._DOC) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5.getDext5Dom = DEXT5.GetDext5Dom = function(a) { var e = null; try { var c = DEXT5.util._setDEXT5editor(a); c && (e = c._DOC.documentElement) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5.getDext5BodyDom = DEXT5.GetDext5BodyDom = function(a) {
						var e = null; try {
							var c =
								DEXT5.util._setDEXT5editor(a); c && (e = c._BODY)
						} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e
					}; DEXT5.getValueInTextMode = DEXT5.GetValueInTextMode = function(a) { var e = ""; try { var c = DEXT5.util._setDEXT5editor(a); c && ("text" != c.getEditorMode() && (DEXT5.ShowTextChangeAlert = !1, c.setChangeMode("text"), DEXT5.ShowTextChangeAlert = !0), e = c._EDITOR.text.value) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5.setValueInTextMode = DEXT5.SetValueInTextMode = function(a, e) {
						try {
							var c = DEXT5.util._setDEXT5editor(e);
							c && ("text" != c.getEditorMode() && (DEXT5.ShowTextChangeAlert = !1, c.setChangeMode("text"), DEXT5.ShowTextChangeAlert = !0), c._EDITOR.text.value = a, c._EDITOR.text.focus())
						} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
					}; DEXT5.getD5Dom = DEXT5.GetD5Dom = function(a) {
						var e = {}; try {
							var c = DEXT5.util._setDEXT5editor(a); if (c) {
								var b = c._BODY.getElementsByTagName("*"), d = b.length; e.document = c._DOC; e.body = c._BODY; e.all = c._DOC.all; for (a = 0; a < d; a++) {
									var g = b[a]; if (g.id && 0 < g.id.length) {
										var h = g.id; if (null == e[h] || void 0 ==
											e[h]) e[h] = g
									} else if (g.getAttribute("name") && 0 < g.getAttribute("name").length) if (h = g.getAttribute("name"), null == e[h] || void 0 == e[h]) e[h] = g; else { if (e[h] && "undefined" == typeof e[h].length) { var k = e[h]; delete e[h]; e[h] = [k] } e[h].push(g) }
								}
							}
						} catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) } return e
					}; DEXT5.$ = function(a, e) {
						var c = null; try {
							var b = DEXT5.util._setDEXT5editor(e); if (b) {
								var d = b._DOC; if (void 0 == a || "" == a || "*" == a) c = d.getElementsByTagName("*"); else {
									b = ""; 0 < a.length && (b = a.substring(0, 1)); var g = a.indexOf("*=");
									if ("#" == b) return a = a.replace("#", ""), d.getElementById(a); if ("." == b || -1 < g) { var h = ""; if ("." == b) h = a.substring(1, a.length); else { var k = a.split("="); 2 == k.length && "classname*" == k[0].toLowerCase() && 0 < k[1].length && (h = k[1]) } h = h.toLowerCase(); if ("" == h) return c; for (var l = d.getElementsByTagName("*"), n = l.length, m = 0; m < n; m++) { var t = l[m], q = t.className; void 0 != q && (q = q.toLowerCase()); "." == b && h == q ? (null == c && (c = []), c.push(t)) : -1 < g && h.length <= q.length && h == q.substring(0, h.length) && (null == c && (c = []), c.push(t)) } } else if (-1 <
										a.indexOf(":")) {
											var r = a.split(":"); if (2 == r.length && "form" == r[0].toLowerCase() && 0 < r[1].length) {
												var f = r[1].toLowerCase(), g = ["INPUT", "SELECT", "TEXTAREA", "BUTTON"]; if ("all" == f) for (h = g.length, m = 0; m < h; m++) { if (l = d.getElementsByTagName(g[m]), n = l.length, 0 < n) for (null == c && (c = []), k = 0; k < n; k++)c.push(l[k]) } else {
													l = d.getElementsByTagName("INPUT"); n = l.length; for (m = 0; m < n; m++)l[m] && void 0 != l[m].type && l[m].type == f && (null == c && (c = []), c.push(l[m])); l = []; if ("button" == f || "select" == f || "textarea" == f) l = d.getElementsByTagName(f.toUpperCase());
													n = l.length; for (m = 0; m < n; m++)null == c && (c = []), c.push(l[m])
												}
											}
									} else -1 < a.indexOf("=") ? (r = a.split("="), 2 == r.length && "name" == r[0].toLowerCase() && 0 < r[1].length && (c = d.getElementsByName(r[1]))) : (a = a.toUpperCase(), c = d.getElementsByTagName(a))
								}
							}
						} catch (u) { c = null } return c
					}; DEXT5.getElementById = DEXT5.GetElementById = function(a, e) { var c = null; try { var b = DEXT5.util._setDEXT5editor(e); b && (c = b._DOC.getElementById(a)) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } return c }; DEXT5.getElementsByName = DEXT5.GetElementsByName =
						function(a, e) { var c = []; try { var b = DEXT5.util._setDEXT5editor(e); b && (c = b._DOC.getElementsByName(a)) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } return c }; DEXT5.getElementsByTagName = DEXT5.GetElementsByTagName = function(a, e) { var c = []; try { var b = DEXT5.util._setDEXT5editor(e); if (b) { var d = b._DOC; a.toUpperCase(); c = d.getElementsByTagName(a) } } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } return c }; DEXT5.setElementInnerHTML = DEXT5.SetElementInnerHTML = function(a, e, c) {
							var b = ""; try {
								var d = DEXT5.util._setDEXT5editor(c);
								if (d) { var g = d._DOC.getElementById(a); g && (g.innerHTML = e) }
							} catch (h) { b = "An error has occurred during the current function operation." } return b
						}; DEXT5.setElementInnerText = DEXT5.SetElementInnerText = function(a, e, c) { var b = ""; try { var d = DEXT5.util._setDEXT5editor(c); if (d) { var g = d._DOC.getElementById(a); g && (g.innerText = e) } } catch (h) { b = "An error has occurred during the current function operation." } return b }; DEXT5.setFormDataTextValue = DEXT5.SetFormDataTextValue = function(a, e, c) {
							var b = ""; try {
								var d = DEXT5.util._setDEXT5editor(c);
								if (d) { var g = d._DOC.getElementsByName(a); if (0 < g.length) { var h = g[0]; h && (h.value = e) } }
							} catch (k) { b = "An error has occurred during the current function operation." } return b
						}; DEXT5.addUserD5Event = DEXT5.AddUserD5Event = function(a, e, c, b) {
							var d = ""; if (null == a || void 0 == a || 0 >= a.length) return "The parameter is incorrect : id [First parameter]"; if (null == e || void 0 == e || 0 >= e.length) return "The parameter is incorrect : eventName [Second parameter]"; if ("function" !== typeof c) return "The parameter is incorrect : function [Third parameter]";
							try { var g = DEXT5.util._setDEXT5editor(b); if (g) { var h = g._DOC.getElementById(a); h ? DEXT5.util.addEvent(h, e, c) : d = "Not found id's object" } } catch (k) { d = "An error has occurred during the current function operation." } return d
						}; DEXT5.setCaretBeforeOrAfter = DEXT5.SetCaretBeforeOrAfter = function(a, e) {
							try {
								if (a) {
									var c = a.node ? a.node : null; if (null != c) {
										var b = a.pos ? a.pos : "before", d = DEXT5.util._setDEXT5editor(e); if (d) {
											var g = d._FRAMEWIN.getFirstRange(), h = g.range, b = b.charAt(0).toUpperCase() + b.slice(1); h["setStart" + b](c);
											h["setEnd" + b](c); g.sel.removeAllRanges(); g.sel.addRange(h); d._LastRange = h
										}
									}
								}
							} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) }
						}; DEXT5.removeNode = DEXT5.RemoveNode = function(a, e) { try { if (a) { var c = a.node ? a.node : null; null != c && DEXT5.util._setDEXT5editor(e) && a.node.parentNode.removeChild(c) } } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } }; DEXT5.inputTextfield = DEXT5.InputTextfield = function(a) {
							try {
								var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page,
									e._config.pages.input_text)
							} catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) }
						}; DEXT5.inputRadio = DEXT5.InputRadio = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.input_radio) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.inputCheckbox = DEXT5.InputCheckbox = function(a) {
							try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.input_checkbox) } catch (c) {
								DEXT5 &&
								DEXT5.logMode && console && console.log(c)
							}
						}; DEXT5.inputButton = DEXT5.InputButton = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.input_button) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.inputHiddenfield = DEXT5.InputHiddenfield = function(a) {
							try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.input_hidden) } catch (c) {
								DEXT5 &&
								DEXT5.logMode && console && console.log(c)
							}
						}; DEXT5.inputTextarea = DEXT5.InputTextarea = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.input_textarea) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.inputSelect = DEXT5.InputSelect = function(a) {
							try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.input_select) } catch (c) {
								DEXT5 &&
								DEXT5.logMode && console && console.log(c)
							}
						}; DEXT5.inputImagebutton = DEXT5.InputImagebutton = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); e && e._FRAMEWIN.DEXT5_EDITOR.prototype.dialog.show(e._config.webPath.page, e._config.pages.input_image) } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.insertInput = DEXT5.InsertInput = function(a, e, c) {
							try {
								var b = DEXT5.util._setDEXT5editor(c); if (b) {
									var d = b._FRAMEWIN, g = b._DOC; d.restoreSelection(); d.setFocusToBody(); 0 < b.UndoManager.charCount && b.UndoManager.putUndo();
									var h = d.getFirstRange().range, k; k = null == e ? g.createElement("input") : e; c = ""; for (var l in a) switch (l.toLowerCase()) {
										case "type": k.type = a[l]; break; case "name": c = a[l]; break; case "id": k.id = a[l]; break; case "class": k.className = a[l]; break; case "title": k.title = a[l]; break; case "value": k.setAttribute(l, a[l]); break; case "size": "" != a[l] && (k.size = a[l]); break; case "maxlength": "" != a[l] && (k.maxlength = a[l]); break; case "disabled": 0 != a[l] && (k.disabled = !0); break; case "readonly": 0 != a[l] && (k.readonly = !0); break; case "checked": 0 !=
											a[l] && (k.checked = !0, k.setAttribute(l, a[l])); break; case "alt": "" != a[l] && (k.alt = a[l]); break; case "style.width": "" != a[l] && (k.style.width = a[l]); break; case "style.height": "" != a[l] && (k.style.height = a[l]); break; case "style.text_align": "" != a[l] && (k.style.textAlign = a[l])
									}if (null == e) {
										h.deleteContents(); if (DEXT5.browser.ie && 8 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) "" != c && k.setAttribute("name", c), h.insertNode(k), h.selectNode(k); else { var n = k.outerHTML; "" != c && (n = n.replace(">", ' name="' + c + '" >')); h.pasteHtml(n) } "hidden" ==
											k.type && (G_CURREDITOR._dextCustomDataMode = !0, ReplaceRealObjectToImage(!1)); h.collapse(!1); d.rangy.getSelection(d).removeAllRanges(); d.rangy.getSelection(d).addRange(h)
									} else DEXT5.browser.ie && 8 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie ? "" != c && k.setAttribute("name", c) : (n = k.outerHTML, "" != c && (n = n.replace(">", ' name="' + c + '" >')), k.outerHTML = n); b.UndoManager.putUndo(); b.UndoManager.charCount = 0
								}
							} catch (m) { DEXT5 && DEXT5.logMode && console && console.log(m) }
						}; DEXT5.insertTextarea = DEXT5.InsertTextarea = function(a,
							e, c) {
								try {
									var b = DEXT5.util._setDEXT5editor(c); if (b) {
										var d = b._FRAMEWIN, g = b._DOC; d.restoreSelection(); d.setFocusToBody(); 0 < b.UndoManager.charCount && b.UndoManager.putUndo(); var h = d.getFirstRange().range, k; k = null == e ? g.createElement("textarea") : e; var g = c = "", l; for (l in a) switch (l.toLowerCase()) {
											case "name": c = a[l]; break; case "id": k.id = a[l]; break; case "class": k.className = a[l]; break; case "title": k.title = a[l]; break; case "value": k.setAttribute(l, a[l]); g = a[l]; break; case "rows": "" != a[l] && (k.rows = a[l]); break; case "cols": "" !=
												a[l] && (k.cols = a[l]); break; case "disabled": 0 != a[l] && (k.disabled = a[l]); break; case "readonly": 0 != a[l] && (k.readonly = a[l]); break; case "style.width": "" != a[l] && (k.style.width = a[l]); break; case "style.height": "" != a[l] && (k.style.height = a[l]); break; case "style.text_align": "" != a[l] && (k.style.textAlign = a[l])
										}k.innerHTML = g; if (null == e) {
											h.deleteContents(); if (DEXT5.browser.ie && 8 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) "" != c && k.setAttribute("name", c), h.insertNode(k), h.selectNode(k); else {
												var n = k.outerHTML; "" != c &&
													(n = n.replace(">", ' name="' + c + '" >')); h.pasteHtml(n)
											} h.collapse(!1); d.rangy.getSelection(d).removeAllRanges(); d.rangy.getSelection(d).addRange(h)
										} else DEXT5.browser.ie && 8 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie ? "" != c && k.setAttribute("name", c) : (n = k.outerHTML, "" != c && (n = n.replace(">", ' name="' + c + '" >')), k.outerHTML = n); b.UndoManager.putUndo(); b.UndoManager.charCount = 0
									}
								} catch (m) { DEXT5 && DEXT5.logMode && console && console.log(m) }
						}; DEXT5.insertSelect = DEXT5.InsertSelect = function(a, e, c, b, d, g) {
							try {
								var h = DEXT5.util._setDEXT5editor(g);
								if (h) {
									var k = h._FRAMEWIN, l = h._DOC; k.restoreSelection(); k.setFocusToBody(); 0 < h.UndoManager.charCount && h.UndoManager.putUndo(); var n = k.getFirstRange().range, m; m = null == d ? l.createElement("select") : d; g = ""; for (var t in a) switch (t.toLowerCase()) {
										case "name": g = a[t]; break; case "id": m.id = a[t]; break; case "class": m.className = a[t]; break; case "title": m.title = a[t]; break; case "multiple": m.multiple = a[t]; break; case "size": m.size = a[t]; break; case "disabled": 0 != a[t] && (m.disabled = a[t]); break; case "style.width": "" != a[t] &&
											(m.style.width = a[t]); break; case "style.height": "" != a[t] && (m.style.height = a[t])
									}if (null != d) for (var q = d.childNodes.length, r = 0; r < q; r++)d.removeChild(d.childNodes[r]); q = e.length; for (r = 0; r < q; r++) { var f = l.createElement("option"); f.innerHTML = e[r]; f.setAttribute("value", c[r]); "" != b[r] && (f.selected = b[r], f.setAttribute("selected", b[r])); m.appendChild(f) } n.deleteContents(); if (DEXT5.browser.ie && 8 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) "" != g && m.setAttribute("name", g), n.insertNode(m), n.selectNode(m); else {
										var u =
											m.outerHTML; "" != g && (u = u.replace(">", ' name="' + g + '" >')); n.pasteHtml(u)
									} n.collapse(!1); k.rangy.getSelection(k).removeAllRanges(); k.rangy.getSelection(k).addRange(n); h.UndoManager.putUndo(); h.UndoManager.charCount = 0
								}
							} catch (v) { DEXT5 && DEXT5.logMode && console && console.log(v) }
						}; DEXT5.insertImg = DEXT5.InsertImg = function(a, e, c) {
							try {
								var b = DEXT5.util._setDEXT5editor(c); if (b) {
									var d = b._FRAMEWIN, g = b._DOC; d.restoreSelection(); d.setFocusToBody(); 0 < b.UndoManager.charCount && b.UndoManager.putUndo(); var h = d.getFirstRange().range,
										k; k = null == e ? g.createElement("img") : e; e = ""; for (var l in a) switch (l.toLowerCase()) { case "name": e = a[l]; break; case "id": k.id = a[l]; break; case "class": k.className = a[l]; break; case "title": k.title = a[l]; break; case "src": k.src = a[l]; break; case "alt": "" != a[l] && (k.alt = a[l]); break; case "style.width": "" != a[l] && (k.style.width = a[l]); break; case "style.height": "" != a[l] && (k.style.height = a[l]); break; case "style.text_align": "" != a[l] && (k.style.textAlign = a[l]) }h.deleteContents(); if (DEXT5.browser.ie && 8 <= DEXT5.browser.ieVersion ||
											!DEXT5.browser.ie) "" != e && k.setAttribute("name", e), h.insertNode(k), h.selectNode(k); else { var n = k.outerHTML; "" != e && (n = n.replace(">", ' name="' + e + '" >')); h.pasteHtml(n) } h.collapse(!1); d.rangy.getSelection(d).removeAllRanges(); d.rangy.getSelection(d).addRange(h); b.UndoManager.putUndo(); b.UndoManager.charCount = 0
								}
							} catch (m) { DEXT5 && DEXT5.logMode && console && console.log(m) }
						}; DEXT5.insertDiv = DEXT5.InsertDiv = function(a, e, c) {
							try {
								var b = DEXT5.util._setDEXT5editor(c); if (b) {
									var d = b._FRAMEWIN, g = b._DOC; d.restoreSelection();
									d.setFocusToBody(); 0 < b.UndoManager.charCount && b.UndoManager.putUndo(); var h = d.getFirstRange().range, k; k = null == e ? g.createElement("div") : e; e = ""; for (var l in a) switch (l.toLowerCase()) {
										case "name": e = a[l]; break; case "id": k.id = a[l]; break; case "class": k.className = a[l]; break; case "title": k.title = a[l]; break; case "alt": "" != a[l] && (k.alt = a[l]); break; case "style.width": "" != a[l] && (k.style.width = a[l]); break; case "style.height": "" != a[l] && (k.style.height = a[l]); break; case "style.text_align": "" != a[l] && (k.style.textAlign =
											a[l])
									}h.deleteContents(); if (DEXT5.browser.ie && 8 <= DEXT5.browser.ieVersion || !DEXT5.browser.ie) "" != e && k.setAttribute("name", e), h.insertNode(k), h.selectNode(k); else { var n = k.outerHTML; "" != e && (n = n.replace(">", ' name="' + e + '" >')); h.pasteHtml(n) } h.collapse(!1); d.rangy.getSelection(d).removeAllRanges(); d.rangy.getSelection(d).addRange(h); b.UndoManager.putUndo(); b.UndoManager.charCount = 0
								}
							} catch (m) { DEXT5 && DEXT5.logMode && console && console.log(m) }
						}; DEXT5.insertDynamicTable = DEXT5.InsertDynamicTable = function(a) {
							try {
								var e =
									DEXT5.util._setDEXT5editor(a); if (e) { var c = e._FRAMEWIN; c.restoreSelection(); c.setFocusToBody(); 0 < e.UndoManager.charCount && e.UndoManager.putUndo(); var b = c.getFirstRange().range, d = c.getMyElementNode(b.startContainer); if (d) { var g = c.GetParentbyTagName(d, "table"); g && (g.className = "DEXT_fiVe_EdiTor_DynGird", b.collapse(!1), c.rangy.getSelection(c).removeAllRanges(), c.rangy.getSelection(c).addRange(b), e.UndoManager.putUndo(), e.UndoManager.charCount = 0) } }
							} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
						};
	DEXT5.deleteDynamicTable = DEXT5.DeleteDynamicTable = function(a) {
		try { var e = DEXT5.util._setDEXT5editor(a); if (e) { var c = e._FRAMEWIN; c.restoreSelection(); c.setFocusToBody(); 0 < e.UndoManager.charCount && e.UndoManager.putUndo(); var b = c.getFirstRange().range, d = c.getMyElementNode(b.startContainer); if (d) { var g = c.GetParentbyTagName(d, "table"); g && (g.className = "", b.collapse(!1), c.rangy.getSelection(c).removeAllRanges(), c.rangy.getSelection(c).addRange(b), e.UndoManager.putUndo(), e.UndoManager.charCount = 0) } } } catch (h) {
			DEXT5 &&
			DEXT5.logMode && console && console.log(h)
		}
	}; DEXT5.changeImageData = DEXT5.ChangeImageData = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(e); if (c) {
				var b = c._FRAMEWIN; b.restoreSelection(); b.setFocusToBody(); 0 < c.UndoManager.charCount && c.UndoManager.putUndo(); var d = b.getFirstRange().range; G_SELECTED_ELEMENT.src = a.src; G_SELECTED_ELEMENT.style.width = a.width + "px"; G_SELECTED_ELEMENT.style.height = a.height + "px"; d.collapse(!1); b.rangy.getSelection(b).removeAllRanges(); b.rangy.getSelection(b).addRange(d); c.UndoManager.putUndo();
				c.UndoManager.charCount = 0
			}
		} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
	}; DEXT5.getSelectedCell = DEXT5.GetSelectedCell = function(a) { try { var e = DEXT5.util._setDEXT5editor(a); if (e) { var c = e._FRAMEWIN, b = c.GetTableSelectionCells(G_SELECTED_ELEMENT); if (0 == b.length) { c.restoreSelection(); var d = c.getFirstRange().range; if (d) { var g = c.GetTDTHCell(d.startContainer); g && b.push(g) } } return b } } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } return null }; DEXT5.setSelectCell = DEXT5.SetSelectCell = function(a,
		e) { try { var c = DEXT5.util._setDEXT5editor(e); if (c) { var b = c._FRAMEWIN, d = a ? a : {}; d.editor = c; b.setSelectCell(d) } } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } }; DEXT5.setHorizontalLine = DEXT5.SetHorizontalLine = function(a, e) {
			try {
				var c = DEXT5.util._setDEXT5editor(e); if (c) {
					var b = c._FRAMEWIN; if (a && "" != a) {
						c._config.horizontalLine.use = "1"; c._config.horizontalLine.url = a.split(","); for (b = 0; b < c._config.horizontalLine.url.length; b++); c._BODY.style.backgroundImage = 'url("' + a + '")'; c._BODY.style.backgroundRepeat =
							"repeat"; c._BODY.style.margin = "0px 10px 10px 10px"
					} else c._config.horizontalLine.use = "0", c._config.horizontalLine.url = [], c._BODY.style.backgroundImage = "", c._BODY.style.backgroundRepeat = "", c._BODY.style.margin = "10px", b.changeBodyImageProperty(!0), b.G_BodyFit.noncreationAreaBackgroundStatus && b.setBodyFitStyle(c, !0)
				}
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
		}; DEXT5.dextCommands = DEXT5.DextCommands = function(a, e) {
			try { var c = DEXT5.util._setDEXT5editor(e); c && c._dextCommands(c.ID, a) } catch (b) {
				DEXT5 &&
				DEXT5.logMode && console && console.log(b)
			}
		}; DEXT5.addHtmlToSetValue = DEXT5.AddHtmlToSetValue = function(a, e, c) { if (void 0 != a && "" != a) try { var b = DEXT5.util._setDEXT5editor(c); b && (b._config.addHtmlToSetValue.html = a, void 0 != e && (b._config.addHtmlToSetValue.preOrSub = DEXTTOP.DEXT5.util.parseIntOr0(e))) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5.getFileSize = DEXT5.GetFileSize = function(a, e, c) {
			if (null != a && void 0 != a && "" != a && ("[object Array]" !== Object.prototype.toString.call(a) || 0 != a.length)) try {
				var b =
					DEXT5.util._setDEXT5editor(c); if (b) {
						c = "urlAddress"; "php" == b._config.developLang.toLowerCase() && (c = "urlAddress[]"); var d = [], g = []; if ("[object Array]" === Object.prototype.toString.call(a)) for (var h = a.length, k = 0; k < h; k++) { var l = a[k]; "ieplugin" == b._config.runtimes && 0 != l.toLowerCase().indexOf("http://") && 0 != l.toLowerCase().indexOf("https://") && 0 != l.toLowerCase().indexOf("/") ? g.push(l) : d.push(l) } else "ieplugin" == b._config.runtimes && 0 != a.toLowerCase().indexOf("http://") && 0 != a.toLowerCase().indexOf("https://") &&
							0 != a.toLowerCase().indexOf("/") ? g.push(a) : d.push(a); var n = 0, m = g.length; if (0 != m) for (k = 0; k < m; k++)try { g[k] = g[k].replace(/\%20/g, " "), g[k] = g[k].replace(/\%27/g, "'"), g[k] = g[k].replace(/\%28/g, "("), g[k] = g[k].replace(/\%29/g, ")"), n += parseInt(b._FRAMEWIN.G_dext5plugIn.getSizeByLocation(g[k]), 10) } catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } var q = d.length; if (0 == q) e && e(n); else {
								var r = "", f = "", f = "" != b._config.post_url_save_for_notes ? b._config.post_url_save_for_notes : b._config.post_url; "undefined" != typeof DEXT5.config.ContentSizeChangeEvent &&
									"undefined" != typeof DEXT5.config.ContentSizeChangeEvent.HandlerUrl && "" != DEXT5.config.ContentSizeChangeEvent.HandlerUrl && (f = DEXT5.config.ContentSizeChangeEvent.HandlerUrl); if ("" != b._config.proxy_url) {
										var u = DEXT5.util.getDefaultIframeSrc(), v; try { v = document.createElement('<iframe frameborder="0" >') } catch (y) { v = document.createElement("iframe") } v.id = "download_frame"; v.name = "download_frame"; v.src = u; v.setAttribute("id", "download_frame"); v.setAttribute("name", "download_frame"); v.setAttribute("src", ""); v.style.display =
											"none"; v.frameBorder = 0; v.title = "DEXT5Editor download"; document.body.appendChild(v); DEXT5.util.addEvent(v, "load", function() { v.contentWindow.postMessage("check", "*") }); if (window.postMessage) { var z = function(a) { if (0 == f.indexOf(a.origin)) { r = a.data; if (null == r || "" == r) r = ""; n += parseInt(r, 10); DEXT5.util.removeEvent(window, "message", z); document.body.removeChild(v); e && e(n) } }; DEXT5.util.addEvent(window, "message", z) } var w; if ("0" != b._config.security.encryptParam) for (w = [["dext5CMD", "gfs"], ["pe", b._config.security.encryptParam],
											["cd", "1"]], k = 0; k < q; k++)w.push([c, DEXT5.util.makeEncryptParamFinal(d[k])]); else for (w = [["dext5CMD", "gfs"], ["cd", "1"]], k = 0; k < q; k++)w.push([c, encodeURIComponent(d[k])]); DEXT5.util.postFormData(document, f, "download_frame", w)
									} else {
										a = ""; if ("0" != b._config.security.encryptParam) { a = "dext5CMD=gfs"; for (k = 0; k < q; k++)a += "&" + c + "=" + DEXT5.util.makeEncryptParamFinal(d[k]); a += "&pe=" + b._config.security.encryptParam } else for (a = "dext5CMD=gfs", k = 0; k < q; k++)a += "&" + c + "=" + encodeURI(encodeURIComponent(d[k])); DEXT5.ajax.postDataCallback(f,
											a, function(a) { a = null != a && "" != a ? DEXT5.util.trim(a) : ""; n += parseInt(a, 10); e && e(n) })
								}
							}
					}
			} catch (A) { DEXT5 && DEXT5.logMode && console && console.log(A) }
		}; DEXT5.getFileSizeEx = DEXT5.GetFileSizeEx = function(a, e, c) {
			if (null != a && void 0 != a && "" != a && ("[object Array]" !== Object.prototype.toString.call(a) || 0 != a.length)) try {
				var b = DEXT5.util._setDEXT5editor(c); if (b) {
					c = "urlAddress"; "php" == b._config.developLang.toLowerCase() && (c = "urlAddress[]"); var d = !1, g = []; if ("[object Array]" === Object.prototype.toString.call(a)) for (var h = a.length,
						k = 0; k < h; k++) { var l = a[k]; if ("ieplugin" == b._config.runtimes && 0 != l.toLowerCase().indexOf("http://") && 0 != l.toLowerCase().indexOf("https://") && 0 != l.toLowerCase().indexOf("/")) { var n = ""; try { l = l.replace(/\%20/g, " "), l = l.replace(/\%27/g, "'"), l = l.replace(/\%28/g, "("), l = l.replace(/\%29/g, ")"), n += b._FRAMEWIN.G_dext5plugIn.getSizeByLocation(l) } catch (m) { DEXT5 && DEXT5.logMode && console && console.log(m) } a[k] = "|" + g.length; g.push(n) } else d = !0 } else if ("ieplugin" == b._config.runtimes && 0 != a.toLowerCase().indexOf("http://") &&
							0 != a.toLowerCase().indexOf("https://") && 0 != a.toLowerCase().indexOf("/")) { n = ""; try { a = a.replace(/\%20/g, " "), a = a.replace(/\%27/g, "'"), a = a.replace(/\%28/g, "("), a = a.replace(/\%29/g, ")"), n += b._FRAMEWIN.G_dext5plugIn.getSizeByLocation(a) } catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) } a = ["|" + g.length]; g.push(n) } else a = [a], d = !0; h = a.length; if (d) {
								var q = "", r = "", r = "" != b._config.post_url_save_for_notes ? b._config.post_url_save_for_notes : b._config.post_url; "undefined" != typeof DEXT5.config.ContentSizeChangeEvent &&
									"undefined" != typeof DEXT5.config.ContentSizeChangeEvent.HandlerUrl && "" != DEXT5.config.ContentSizeChangeEvent.HandlerUrl && (r = DEXT5.config.ContentSizeChangeEvent.HandlerUrl); if ("" != b._config.proxy_url) {
										var f = DEXT5.util.getDefaultIframeSrc(), u; try { u = document.createElement('<iframe frameborder="0" >') } catch (v) { u = document.createElement("iframe") } u.id = "download_frame"; u.name = "download_frame"; u.src = f; u.setAttribute("id", "download_frame"); u.setAttribute("name", "download_frame"); u.setAttribute("src", ""); u.style.display =
											"none"; u.frameBorder = 0; u.title = "DEXT5Editor download"; document.body.appendChild(u); DEXT5.util.addEvent(u, "load", function() { u.contentWindow.postMessage("check", "*") }); if (window.postMessage) {
												var y = function(a) { if (0 == r.indexOf(a.origin)) { q = a.data; if (null == q || "" == q) q = ""; a = []; "" != q && (a = q.split(",")); for (var b = "", c = a.length, d = 0; d < c; d++)"" != b && (b += ","), b = 0 == a[d].indexOf("|") ? b + g[parseInt(a[d].substring(1), 10)] : b + a[d]; DEXT5.util.removeEvent(window, "message", y); document.body.removeChild(u); e && e(b) } }; DEXT5.util.addEvent(window,
													"message", y)
											} var z; if ("0" != b._config.security.encryptParam) for (z = [["dext5CMD", "gfs"], ["pe", b._config.security.encryptParam], ["cd", "1"], ["gfsex", "1"]], k = 0; k < h; k++)z.push([c, DEXT5.util.makeEncryptParamFinal(a[k])]); else for (z = [["dext5CMD", "gfs"], ["cd", "1"], ["gfsex", "1"]], k = 0; k < h; k++)z.push([c, encodeURIComponent(a[k])]); DEXT5.util.postFormData(document, r, "download_frame", z)
									} else {
										l = ""; if ("0" != b._config.security.encryptParam) {
											l = "dext5CMD=gfs"; for (k = 0; k < h; k++)l += "&" + c + "=" + DEXT5.util.makeEncryptParamFinal(a[k]);
											l += "&pe=" + b._config.security.encryptParam
										} else for (l = "dext5CMD=gfs", k = 0; k < h; k++)l += "&" + c + "=" + encodeURI(encodeURIComponent(a[k])); DEXT5.ajax.postDataCallback(r, l + "&gfsex=1", function(a) { a = null != a && "" != a ? DEXT5.util.trim(a) : ""; var b = []; "" != a && (b = a.split(",")); a = ""; for (var c = b.length, d = 0; d < c; d++)"" != a && (a += ","), a = 0 == b[d].indexOf("|") ? a + g[parseInt(b[d].substring(1), 10)] : a + b[d]; e && e(a) })
								}
							} else if (e) { b = ""; for (k = 0; k < h; k++)"" != b && (b += ","), b += g[parseInt(a[k].substring(1), 10)]; e(b) }
				}
			} catch (w) {
				DEXT5 && DEXT5.logMode &&
				console && console.log(w)
			}
		}; DEXT5.destroy = DEXT5.Destroy = function(a, e) {
			try {
				if (editorTemp = null, "object" == typeof a ? editorTemp = a : (DEXT5.ShowDestroyAlert = !1, editorTemp = DEXT5.util._setDEXT5editor(a)), editorTemp) {
					var c = editorTemp._FRAMEWIN, b = c.getDialogWindow(), d = c.getDialogDocument(); try {
						if ("1" == editorTemp._config.useMiniImageEditor) {
							try { b.KMINIPHOTO.Destroy() } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } try { d.getElementById("k_mini_photo").parentNode.removeChild(d.getElementById("k_mini_photo")) } catch (h) {
								DEXT5 &&
								DEXT5.logMode && console && console.log(h)
							}
						}
					} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } try { c.onDestroy({ editor: editorTemp }) } catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) } try {
						var n = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0, m = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0, t = document.createElement("input"); t.setAttribute("type", "input"); document.body.appendChild(t); t.focus(); t.parentNode.removeChild(t); var q =
							window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0; m == (window.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft || 0) && n == q || scrollTo(m, n)
					} catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) } if (DEXT5.DEXTMULTIPLEEVENT[editorTemp.ID]) {
						editorEventList = DEXT5.DEXTMULTIPLEEVENT[editorTemp.ID]; for (var f = 0, u = editorEventList.length; f < u; f++)try {
							editorEventList[f].element && (DEXT5.util.removeEvent(editorEventList[f].element, editorEventList[f].event, editorEventList[f].func),
								editorEventList[f].func = null, delete editorEventList[f].func)
						} catch (v) { DEXT5 && DEXT5.logMode && console && console.log(v) } try { editorEventList && (editorEventList = null, delete editorEventList, DEXT5.DEXTMULTIPLEEVENT[editorTemp.ID] = null, delete DEXT5.DEXTMULTIPLEEVENT[editorTemp.ID]) } catch (y) { DEXT5 && DEXT5.logMode && console && console.log(y) }
					} try {
						DEXT5.browser.ie ? (DEXT5.util.removeEvent(editorTemp._DOC.body, "beforedeactivate", c._iframeDoc_BlurHandler), DEXT5.util.removeEvent(editorTemp._DOC.body, "focus", c._iframeDoc_FocusHandler),
							DEXT5.util.removeEvent(document.body, "focus", c._dextiframe_Focus)) : (DEXT5.util.removeEvent(editorTemp._DOC, "blur", c._iframeDoc_BlurHandler), DEXT5.util.removeEvent(editorTemp._DOC, "focus", c._iframeDoc_FocusHandler), DEXT5.util.removeEvent(window, "focus", c._dextiframe_Focus))
					} catch (z) { DEXT5 && DEXT5.logMode && console && console.log(z) } try {
						DEXT5.util.removeEvent(DEXTWIN, "resize", editorTemp.toolmenu_bg_resize), DEXT5.util.removeEvent(DEXTWIN, "resize", c.resizeDextWin), DEXT5.util.removeEvent(DEXTWIN, "resize",
							editorTemp.topmenu_bg_resize), DEXT5.util.removeEvent(b, "resize", editorTemp.context_bg_resize), DEXT5.util.removeEvent(b, "resize", c.setLayerbgResize)
					} catch (w) { DEXT5 && DEXT5.logMode && console && console.log(w) } try { "1" == editorTemp._config.resize_bar && (editorTemp.dextResize.remove(d), delete editorTemp.dextResize) } catch (A) { DEXT5 && DEXT5.logMode && console && console.log(A) } try { "1" == editorTemp._config.dragResize && (editorTemp.dext_dragresize.remove(editorTemp._DOC), delete editorTemp.dext_dragresize) } catch (D) {
						DEXT5 &&
						DEXT5.logMode && console && console.log(D)
					} DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_context_iframe")); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_dialog")); DEXT5.util.removeElementWithChildNodes(document.getElementById("dext5_paste_temp_frame")); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_context_background")); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_toolmenu_background" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_topmenu_background" +
						editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_topmenu_iframe" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_formatting_iframe_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_fontfamily_iframe_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_fontsize_iframe_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_lineheight_iframe_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_detail_list_number_iframe_" +
							editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_detail_list_bullets_iframe_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_detail_align_group_iframe_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_detail_table_group_iframe_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(editorTemp._FRAMEWIN.document.getElementById("dext5_design_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(editorTemp._FRAMEWIN.document.getElementById("dext5_source_" +
								editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(editorTemp._FRAMEWIN.document.getElementById("dext5_preview_" + editorTemp.ID)); DEXT5.util.removeElementWithChildNodes(editorTemp._FRAMEWIN.document.getElementById("dext5_text_" + editorTemp.ID)); try { for (var C in c.G_DEPlugin) if ("function" === typeof c.G_DEPlugin[C].onDestroy) c.G_DEPlugin[C].onDestroy() } catch (F) { DEXT5 && DEXT5.logMode && console && console.log(F) } try { editorTemp._FRAMEWIN.DEXT5_EDITOR && (editorTemp._FRAMEWIN.DEXT5_EDITOR.prototype = null, delete editorTemp._FRAMEWIN.DEXT5_EDITOR.prototype) } catch (I) {
									DEXT5 &&
									DEXT5.logMode && console && console.log(I)
								} try { editorTemp._FRAMEWIN.DextDragResize && (editorTemp._FRAMEWIN.DextDragResize.prototype = null, delete editorTemp._FRAMEWIN.DextDragResize.prototype, editorTemp._FRAMEWIN.DextDragResize = null, delete editorTemp._FRAMEWIN.DextDragResize) } catch (P) { DEXT5 && DEXT5.logMode && console && console.log(P) } if ("undefined" != typeof editorTemp.UndoManager) try { editorTemp.UndoManager.reset(), editorTemp.UndoManager = null, delete editorTemp.UndoManager } catch (B) {
									DEXT5 && DEXT5.logMode && console &&
									console.log(B)
								} try { editorTemp._FRAMEWIN.UndoManager && (editorTemp._FRAMEWIN.UndoManager.prototype = null, delete editorTemp._FRAMEWIN.UndoManager.prototype, editorTemp._FRAMEWIN.UndoManager = null, delete editorTemp._FRAMEWIN.UndoManager) } catch (E) { DEXT5 && DEXT5.logMode && console && console.log(E) } try { editorTemp._FRAMEWIN.Range && (editorTemp._FRAMEWIN.Range.prototype = null, delete editorTemp._FRAMEWIN.Range.prototype, editorTemp._FRAMEWIN.Range = null, delete editorTemp._FRAMEWIN.Range) } catch (Q) {
									DEXT5 && DEXT5.logMode &&
									console && console.log(Q)
								} var J = editorTemp._config.autoDestroy.moveCursor; if (DEXT5.DEXTMULTIPLE["dext_frame_" + editorTemp.ID]) {
									DEXT5.IsEditorLoadedHashTable && "undefined" != typeof DEXT5.IsEditorLoadedHashTable.getItem(editorTemp.ID) && DEXT5.IsEditorLoadedHashTable.removeItem(editorTemp.ID); for (var f = 0, G = DEXT5.DEXTMULTIPLETIMEOUT, u = G.length; f < u; f++)if (G[f]) try { window.clearTimeout(G[f]), G[f] = null, delete G[f] } catch (R) { DEXT5 && DEXT5.logMode && console && console.log(R) } try { DEXT5.DEXTMULTIPLETIMEOUT = null, delete DEXT5.DEXTMULTIPLETIMEOUT } catch (H) {
										DEXT5 &&
										DEXT5.logMode && console && console.log(H)
									} if (editorTemp._FRAMEWIN.DEXT5_CONTEXT._config) try { delete editorTemp._FRAMEWIN.DEXT5_CONTEXT._config, editorTemp._FRAMEWIN.DEXT5_CONTEXT = null, delete editorTemp._FRAMEWIN.DEXT5_CONTEXT } catch (T) { DEXT5 && DEXT5.logMode && console && console.log(T) } editorTemp._FRAMEWIN._dext_editor._config && delete editorTemp._FRAMEWIN._dext_editor._config; editorTemp._FRAMEWIN._dext_editor.Frame.editor && delete editorTemp._FRAMEWIN._dext_editor.Frame.editor; editorTemp._FRAMEWIN._dext_editor.Frame &&
										delete editorTemp._FRAMEWIN._dext_editor.Frame; editorTemp._FRAMEWIN.DEXT5_EDITOR.HTMLParser && delete editorTemp._FRAMEWIN.DEXT5_EDITOR.HTMLParser; editorTemp._FRAMEWIN.DEXT5_EDITOR.HWPFilter && delete editorTemp._FRAMEWIN.DEXT5_EDITOR.HWPFilter; if (editorTemp._FRAMEWIN.DEXT5_EDITOR) { try { delete editorTemp._FRAMEWIN.DEXT5_EDITOR } catch (L) { editorTemp._FRAMEWIN.DEXT5_EDITOR = null } editorTemp._FRAMEWIN.DEXT5_EDITOR && (editorTemp._FRAMEWIN.DEXT5_EDITOR = null) } if (editorTemp._FRAMEWIN.DextResizeObj) {
											try { delete editorTemp._FRAMEWIN.DextResizeObj } catch (O) {
												editorTemp._FRAMEWIN.DextResizeObj =
												null
											} editorTemp._FRAMEWIN.DextResizeObj && (editorTemp._FRAMEWIN.DextResizeObj = null)
										} if (editorTemp._FRAMEWIN.resizebar_props.editor) { try { delete editorTemp._FRAMEWIN.resizebar_props.editor } catch (x) { editorTemp._FRAMEWIN.resizebar_props.editor = null } try { delete editorTemp._FRAMEWIN.resizebar_props } catch (W) { editorTemp._FRAMEWIN.resizebar_props = null } editorTemp.dextResize && delete editorTemp.dextResize } if (editorTemp._FRAMEWIN.DextResize) {
											try { delete editorTemp._FRAMEWIN.DextResize } catch (N) {
												editorTemp._FRAMEWIN.DextResize =
												null
											} editorTemp._FRAMEWIN.DextResize && (editorTemp._FRAMEWIN.DextResize = null)
										} if (editorTemp._FRAMEWIN._dext_editor) { try { delete editorTemp._FRAMEWIN._dext_editor } catch (S) { editorTemp._FRAMEWIN._dext_editor = null } editorTemp._FRAMEWIN._dext_editor && (editorTemp._FRAMEWIN._dext_editor = null) } var M = editorTemp.ID, U = editorTemp.FrameID, K; for (K in editorTemp) if (editorTemp.hasOwnProperty(K)) try { editorTemp[K] = null, delete editorTemp[K] } catch (X) { DEXT5 && DEXT5.logMode && console && console.log(X) } try {
											editorTemp._BODY && (editorTemp._BODY =
												null, delete editorTemp._BODY)
										} catch (Y) { DEXT5 && DEXT5.logMode && console && console.log(Y) } try { editorTemp._DOC && (editorTemp._DOC = null, delete editorTemp._DOC) } catch (aa) { DEXT5 && DEXT5.logMode && console && console.log(aa) } DEXT5.util.removeElementWithChildNodes(document.getElementById(U)); try { DEXT5.util.removeElementWithChildNodes(d.getElementById("dext_frame_holder" + M)) } catch (ba) { DEXT5 && DEXT5.logMode && console && console.log(ba) } var f = 0, Z; for (Z in DEXT5.DEXTHOLDER) if (Z == M) break; else f++; DEXT5.DEXTMULTIPLEID.splice(f,
											1); DEXT5.DEXTMULTIPLE["dext_frame_" + M] && delete DEXT5.DEXTMULTIPLE["dext_frame_" + M]; "undefined" != typeof DEXT5.DEXTHOLDER[M] && delete DEXT5.DEXTHOLDER[M]; if (DEXT5.DEXTMULTIPLEID && 0 < DEXT5.DEXTMULTIPLEID.length) G_CURREDITOR = DEXT5.DEXTMULTIPLE["dext_frame_" + DEXT5.DEXTMULTIPLEID[0]]; else { try { G_CURREDITOR = null, delete G_CURREDITOR } catch (ca) { DEXT5 && DEXT5.logMode && console && console.log(ca) } try { delete DEXT5.DEXTMULTIPLEEVENT } catch (da) { DEXT5 && DEXT5.logMode && console && console.log(da) } }
								} editorTemp = null; delete editorTemp;
					if ("1" == J) {
						try { d.body.focus() } catch (ea) { DEXT5 && DEXT5.logMode && console && console.log(ea) } c = !1; try { for (var V = d.getElementsByTagName("input"), fa = V.length, b = 0; b < fa; b++)if ("text" == V[b].type) { V[b].focus(); c = !0; break } } catch (ga) { DEXT5 && DEXT5.logMode && console && console.log(ga) } try { !c && d.getElementsByTagName("textarea")[0] && (d.getElementsByTagName("textarea")[0].focus(), c = !0) } catch (ha) { DEXT5 && DEXT5.logMode && console && console.log(ha) } try { c || d.getElementsByTagName("select")[0] && d.getElementsByTagName("select")[0].focus() } catch (ia) {
							DEXT5 &&
							DEXT5.logMode && console && console.log(ia)
						}
					} if (e) {
						DEXTTOP.G_COPIEDCELLDATA = null; DEXTTOP.IsCrossDomain = null; DEXTTOP.G_COPIEDTABLE = null; DEXTTOP.G_SELECTED_ELEMENT = null; DEXTTOP.G_SELECTED_IMAGE_ELEMENT = null; try { delete DEXTTOP.G_COPIEDCELLDATA, delete DEXTTOP.IsCrossDomain, delete DEXTTOP.G_COPIEDTABLE, delete DEXTTOP.G_SELECTED_ELEMENT, delete DEXTTOP.G_SELECTED_IMAGE_ELEMENT } catch (ja) { DEXT5 && DEXT5.logMode && console && console.log(ja) } try { DEXTTOP = DEXTDOC = DEXTWIN = null, delete DEXTTOP, delete DEXTDOC, delete DEXTWIN } catch (ka) {
							DEXT5 &&
							DEXT5.logMode && console && console.log(ka)
						} for (K in DEXT5) if (DEXT5.hasOwnProperty(K)) try { DEXT5[K] = null, delete DEXT5[K] } catch (la) { DEXT5 && DEXT5.logMode && console && console.log(la) } try { DEXT5 = null, delete DEXT5 } catch (ma) { DEXT5 && DEXT5.logMode && console && console.log(ma) } try { Dext5editor = DEXT_CONFIG = null } catch (na) { DEXT5 && DEXT5.logMode && console && console.log(na) } if ("function" === typeof dext_editor_loaded_event) try { dext_editor_loaded_event = null, delete dext_editor_loaded_event } catch (oa) {
							DEXT5 && DEXT5.logMode && console &&
							console.log(oa)
						} if ("function" === typeof dext_editor_beforepaste_event) try { dext_editor_beforepaste_event = null, delete dext_editor_beforepaste_event } catch (pa) { DEXT5 && DEXT5.logMode && console && console.log(pa) } if ("function" === typeof dext_editor_custom_action) try { dext_editor_custom_action = null, delete dext_editor_custom_action } catch (qa) { DEXT5 && DEXT5.logMode && console && console.log(qa) } if ("function" === typeof dext_editor_set_complete_event) try { dext_editor_set_complete_event = null, delete dext_editor_set_complete_event } catch (ra) {
							DEXT5 &&
							DEXT5.logMode && console && console.log(ra)
						}
					}
				}
			} catch (sa) { DEXT5 && DEXT5.logMode && console && console.log(sa) }
		}; DEXT5.getByteLength = DEXT5.GetByteLength = function(a) { var e = 0; try { var c, b, d; for (c = b = 0; d = a.charCodeAt(b++); c += d >> 11 ? 3 : d >> 7 ? 2 : 1); e = c } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } return e }; DEXT5.getUserRuntimeMode = DEXT5.GetUserRuntimeMode = function(a) { var e = null; try { var c = DEXT5.util._setDEXT5editor(a); c && (e = c._config.runtimes) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5.getPluginVersion =
			DEXT5.GetPluginVersion = function(a) { var e = null; try { var c = DEXT5.util._setDEXT5editor(a); c && (e = c._FRAMEWIN.G_dext5plugIn.getDext5PluginVersion()) } catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) } return e }; DEXT5.isInstallSucceed = DEXT5.IsInstallSucceed = function(a) { var e = !0; try { var c = DEXT5.util._setDEXT5editor(a); if (c) { var b = c._FRAMEWIN; "ieplugin" == c._config.runtimes && (b.G_dext5plugIn && "" != G_dext5plugIn.getDext5PluginVersion() || (e = !1)) } } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } return e };
	DEXT5.isPluginInstalled = DEXT5.IsPluginInstalled = function(a) { var e = !1; try { var c = DEXT5.util._setDEXT5editor(a); if (c) { var b = c._FRAMEWIN; "ieplugin" == c._config.runtimes && b.G_dext5plugIn && "" != b.G_dext5plugIn.getDext5PluginVersion() && (e = !0) } } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } return e }; DEXT5.createHyperLink = DEXT5.CreateHyperLink = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(e); if (c) {
				var b = c._FRAMEWIN; a.type = "hyperlink"; a.target || (a.target = ""); a.title || (a.title = ""); b.command_InsertHyperLink(c.ID,
					c._EDITOR.design, a)
			}
		} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
	}; DEXT5.setTableEdgeReduce = DEXT5.SetTableEdgeReduce = function(a, e) {
		try {
			var c = DEXT5.util._setDEXT5editor(e); if (c) {
				var b = c._FRAMEWIN; if (b) for (var d = b._iframeDoc.body.getElementsByTagName("table"), g = d.length, c = 0; c < g; c++) {
					"N" == a ? d[c].style.borderCollapse = "collapse" : "Y" == a && (d[c].style.borderCollapse = "separate"); "" == d[c].style.borderTop && (d[c].style.borderTop = "1px solid rgb(0, 0, 0)"); "" == d[c].style.borderLeft && (d[c].style.borderLeft =
						"1px solid rgb(0, 0, 0)"); "" == d[c].style.borderBottom && (d[c].style.borderBottom = "1px solid rgb(0, 0, 0)"); "" == d[c].style.borderRight && (d[c].style.borderRight = "1px solid rgb(0, 0, 0)"); for (var h = d[c].rows.length, k = b = 0; k < h; k++)for (var b = d[c].rows[k].cells.length, l = 0; l < b; l++)"" == d[c].rows[k].cells[l].style.borderTop && (d[c].rows[k].cells[l].style.borderTop = "1px solid rgb(0, 0, 0)"), "" == d[c].rows[k].cells[l].style.borderLeft && (d[c].rows[k].cells[l].style.borderLeft = "1px solid rgb(0, 0, 0)"), "" == d[c].rows[k].cells[l].style.borderBottom &&
							(d[c].rows[k].cells[l].style.borderBottom = "1px solid rgb(0, 0, 0)"), "" == d[c].rows[k].cells[l].style.borderRight && (d[c].rows[k].cells[l].style.borderRight = "1px solid rgb(0, 0, 0)")
				}
			}
		} catch (n) { DEXT5 && DEXT5.logMode && console && console.log(n) }
	}; DEXT5.requestPluginInstall = DEXT5.RequestPluginInstall = function(a, e, c) {
		try { var b = DEXT5.util._setDEXT5editor(c); if (b && (DEXTTOP.G_CURREDITOR = b, "ieplugin" == b._config.runtimes)) { var d = b._FRAMEWIN; d && d.requestPluginInstall(window, document) } } catch (g) {
			DEXT5 && DEXT5.logMode &&
			console && console.log(g)
		}
	}; DEXT5.getCaretObject = DEXT5.GetCaretObject = function(a) { var e = null; try { var c = DEXT5.util._setDEXT5editor(a); if (c) { DEXTTOP.G_CURREDITOR = c; var b = c._FRAMEWIN; b.restoreSelection(); b.setFocusToBody(); var d = b.getFirstRange().range.commonAncestorContainer; if (d) for (e = d; e && 1 !== e.nodeType;)e = e.parentNode } } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } return e }; DEXT5.replaceBlocktoBr = DEXT5.ReplaceBlocktoBr = function(a) {
		try {
			var e = RegExp("<p[^>]*>", "gi"); a = a.replace(e, ""); e = RegExp("</?p>",
				"gi"); a = a.replace(e, "<br />")
		} catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } return a
	}; DEXT5.getMetaTag = DEXT5.GetMetaTag = function(a, e) { var c = ""; try { if ("" == a) return ""; var b = DEXT5.getEditorByName(DEXTTOP.G_CURREDITOR.ID); if (b) for (var d = b._FRAMEWIN._iframeDoc.getElementsByTagName("META"), b = 0, g = d.length, h; b < g; b++)if (h = d[b], h.name.toLowerCase() == a.toLowerCase()) { c = h.content; break } } catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) } return c }; DEXT5.setMetaTag = DEXT5.SetMetaTag = function(a, e, c) {
		try {
			if ("" !=
				a && "" != e) { var b = DEXT5.getEditorByName(DEXTTOP.G_CURREDITOR.ID); if (b) { c = !1; for (var d = b._FRAMEWIN._iframeDoc.getElementsByTagName("META"), g = 0, h = d.length, k; g < h; g++)if (k = d[g], k.name.toLowerCase() == a.toLowerCase()) { k.content = e; c = !0; break } if (0 == c) { var l = b._FRAMEWIN._iframeDoc.createElement("META"); l.name = a; l.content = e; b._FRAMEWIN._iframeDoc.getElementsByTagName("head")[0].appendChild(l) } } }
		} catch (n) { DEXT5 && DEXT5.logMode && console && console.log(n) }
	}; DEXT5.convertHWPFilter = DEXT5.ConvertHWPFilter = function(a,
		e) { var c = a; try { var b = null; try { b = DEXT5.getEditorByName(DEXTTOP.G_CURREDITOR.ID) } catch (d) { b = DEXT5.getEditorByName("") } if (b) { var g; "string" == typeof e ? (g = e, e = {}) : "object" == typeof e ? g = e.extraID : e = {}; c = b._FRAMEWIN.DEXT5_EDITOR.HWPFilter.HTMLtoXML(a, g, e) } } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } return c }; DEXT5.convertMMToPxUnit = DEXT5.ConvertMMToPxUnit = function(a) {
			var e = a; try {
				if (-1 < e.indexOf("mm")) {
					var c = function(a) {
						var b = a, c = !1; -1 < a.indexOf("mm") && (b = b.replace(/mm/ig, ""), c = !0); c && (b = b / .264583 +
							"px"); return b
					}, b = !1, d = !1, e = e.replace(/ width=['\"](\d*(\.\d+)?mm)['\"]/ig, function(a, d) { b = !0; return ' width="' + c(d) + '"' }), e = e.replace(/ height=['\"](\d*(\.\d+)?mm)['\"]/ig, function(a, b) { d = !0; return ' height="' + c(b) + '"' }); 0 == b && (e = e.replace(/ width=(\d*(\.\d+)?mm)/ig, function(a, b) { return ' width="' + c(b) + '"' })); 0 == d && (e = e.replace(/ height=(\d*(\.\d+)?mm)/ig, function(a, b) { return ' height="' + c(b) + '"' }))
				}
			} catch (g) { e = a } return e
		}; DEXT5.getBodyTextLength = DEXT5.GetBodyTextLength = function(a) {
			var e; try {
				var c = DEXT5.getEditorByName(a);
				if (c) { var b = c._FRAMEWIN._iframeDoc.body.innerText, b = b.replace(/\r\n/g, ""); 0 == c._config.wordCount.countwhitespace && (b = b.replace(/ /gim, "")); e = b.length }
			} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } return e
		}; DEXT5.getBodyLineCount = DEXT5.GetBodyLineCount = function(a) { var e = 0; try { var c = DEXT5.getEditorByName(a); if (c) { var b = c._FRAMEWIN, d = b.getInnerText(b._iframeDoc.body, !0); d.match(/\n/gi) && (e = d.match(/\n/gi).length) } } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } return e }; DEXT5.appendTableRow =
			DEXT5.AppendTableRow = function(a, e, c, b, d) {
				try {
					var g = DEXT5.util._setDEXT5editor(d); if (g) {
						var h = g._FRAMEWIN, k = h._iframeDoc.getElementById(a); if (k && k.tagName && "table" == k.tagName.toLowerCase()) {
							a = c; var l = k.rows, k = null; "undefined" != typeof c && "" != c.toString() ? k = l[c] : (a = l.length - 1, k = l[l.length - 1]); k && ("undefined" != typeof b && "1" == b.toString() ? h.InsertRowUpDown(!0, !1, null, k.cells[0]) : (a = 1 * a + 1, h.InsertRowUpDown(!1, !1, null, k.cells[0]))); trow = l[a]; var n = trow.cells, m = n.length; e = e.split("\f"); for (c = 0; c < m; c++)e[c] &&
								(n[c].innerHTML = e[c])
						}
					}
				} catch (t) { DEXT5 && DEXT5.logMode && console && console.log(t) }
			}; DEXT5.cleanNestedHtml = DEXT5.CleanNestedHtml = function(a, e, c) { try { var b = DEXT5.util._setDEXT5editor(c); b && b._FRAMEWIN.DEXT5_EDITOR.HTMLParser.CleanNestedHtml(b, a, e) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5.cleanNestedHtmlEx = DEXT5.CleanNestedHtmlEx = function(a, e, c, b) {
				try { var d = DEXT5.util._setDEXT5editor(b); d && d._FRAMEWIN.DEXT5_EDITOR.HTMLParser.CleanNestedHtml(d, a, e, c) } catch (g) {
					DEXT5 && DEXT5.logMode &&
					console && console.log(g)
				}
			}; DEXT5.cleanNestedHtmlForNode = DEXT5.CleanNestedHtmlForNode = function(a, e, c, b) { var d = ""; try { var g = DEXT5.util._setDEXT5editor(b); if (g) { if ("" == c || 0 == c) c = "p"; d = g._FRAMEWIN.DEXT5_EDITOR.HTMLParser.CleanNestedHtml(g, "2", e, c, a) } } catch (h) { d = html } return d }; DEXT5.cleanNestedTag = DEXT5.CleanNestedTag = function(a, e, c, b, d, g) {
				try {
					var h = DEXT5.util._setDEXT5editor(g); if (h) {
						var k = g = null; if (e && "" != e) if ("string" == typeof e) { var l = document.createElement("div"); l.innerHTML = e; k = l } else "object" == typeof e &&
							1 == e.nodeType && (k = e); else k = h._DOC; k && (e = "", void 0 != b && "" != b && (e = DEXT5.util.parseIntOr0(b)), h._FRAMEWIN.DEXT5_EDITOR.HTMLParser.CheckNeedNestedHTML(k, e, d) && (e = !0, a && "" != a && (e = confirm(a)), e && (a = "", k != h._DOC && (a = k), g = h._FRAMEWIN.DEXT5_EDITOR.HTMLParser.CleanNestedHtml(h, c, b, d, a)))); return g
					}
				} catch (n) { DEXT5 && DEXT5.logMode && console && console.log(n) }
			}; DEXT5.convertHtmlToText = DEXT5.ConvertHtmlToText = function(a, e) {
				var c = a; try {
					var b = DEXT5.util._setDEXT5editor(e); b.setTextPaste = DEXTTOP.DEXT5.browser.ie && 10 >
						DEXTTOP.DEXT5.browser.ieVersion ? !0 : !1; var d = b._FRAMEWIN, c = d.removeDummyfontTagInTable(c), c = d.htmlTagRevision(c, !0, !1), c = c.replace(/\r/g, ""), c = c.replace(/[\n|\t]/g, ""), c = c.replace(/[\v|\f]/g, ""), c = c.replace(/(\s+)<td/gi, "<td"), c = c.replace(/(\s+)<th/gi, "<th"), c = c.replace(/(\s+)<tr/gi, "<tr"), c = c.replace(/(\s+)?<\/td>(\s+)?/gi, "&nbsp;"), c = c.replace(/(\s+)?<\/th>(\s+)?/gi, "&nbsp;"), c = c.replace(/<p><br><\/p>/gi, "\n"), c = c.replace(/<P>&nbsp;<\/P>/gi, "\n"), c = c.replace(/<div><br><\/div>/gi, "\n"), c = c.replace(/<div>&nbsp;<\/div>/gi,
							"\n"), c = c.replace(/<br(\s)*\/?>/gi, "\n"), c = c.replace(/<br(\s[^\/]*)?>/gi, "\n"), c = c.replace(/<\/p(\s[^\/]*)?>/gi, "\n"), c = c.replace(/<\/div(\s[^\/]*)?>/gi, "\n"), c = c.replace(/(\s+)?<\/tr(\s[^\/]*)?>(\s+)?/gi, "\n"), c = c.replace(/<\/li(\s[^\/]*)?>/gi, "\n"), g = RegExp("(<li(s[^/]*)?>)(.+?)(<ol)", "gi"), c = c.replace(g, "$1$3\n$4"), g = RegExp("(<li(s[^/]*)?>)(.+?)(<ul)", "gi"), c = c.replace(g, "$1$3\n$4"); nIdx = c.lastIndexOf("\n"); -1 < nIdx && "\n" == c.substring(nIdx) && (c = c.substring(0, nIdx)); c = d.removeStripTags(c, null); c =
								c.replace(/(\n\r|\n|\r)/gm, "\n"); c = c.replace(/(\n \n)/gm, "\n\n"); c = c.replace(/(\n\t\n)/gm, "\n\n"); c = c.replace(/(\n\n\n\n\n\n\n)/gm, "\n\n"); c = c.replace(/(\n\n\n\n\n\n)/gm, "\n\n"); c = c.replace(/(\n\n\n\n\n)/gm, "\n\n"); c = c.replace(/(\n\n\n\n)/gm, "\n\n"); c = c.replace(/(\n\n\n)/gm, "\n\n"); c = DEXT5.util.trim(c); c = d.addLineBreaker(b._config.enterTag, c); c = c.replace(/&amp;#13;/gi, ""); c = c.replace(/&#13;/gi, "")
				} catch (h) { c = a } return c
			}; DEXT5.addHttpHeaderEx = DEXT5.AddHttpHeaderEx = function(a, e, c) {
				try {
					var b = DEXT5.getEditorByName(c);
					if (b) { var d = b._FRAMEWIN; if (d && (b._config.addHttpHeader[a] = e, "ieplugin" == b._config.runtimes)) try { for (var g in b._config.addHttpHeader) d.G_dext5plugIn.AddHttpHeader(g, b._config.addHttpHeader[g]) } catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) } }
				} catch (k) { DEXT5 && DEXT5.logMode && console && console.log(k) }
			}; DEXT5.contentsSizeCheck = DEXT5.ContentsSizeCheck = function(a, e, c, b) {
				try {
					var d = DEXT5.getEditorByName(b); if (d) {
						DEXTTOP.G_CURREDITOR = d; var g = d._FRAMEWIN; if (g) {
							if (null == a || void 0 == a || "" == a) a = "0"; if (null ==
								e || void 0 == e || "" == e) e = 0; var h = DEXT5.util.getUnit(e), k = DEXT5.util.getUnitSize(h); e = parseInt(e, 10) * k; b = ""; "0" == a && (b = d._config.post_url.replace("http://", ""), b = b.replace("https://", ""), -1 < b.indexOf("/") && (b = b.substring(0, b.indexOf("/"))), -1 < b.indexOf(":") && (b = b.substring(0, b.indexOf(":")))); var l = DEXT5.GetContentsUrl(d.ID), h = []; "" != l && (h = l.split(d._config.unitDelimiter)); for (var n = h.length, l = [], m = 0, k = 0; k < n; k++) {
									var t = h[k].split(d._config.unitAttributeDelimiter)[0]; if ("ieplugin" == d._config.runtimes &&
										0 != t.toLowerCase().indexOf("http://") && 0 != t.toLowerCase().indexOf("https://") && 0 != t.toLowerCase().indexOf("/")) try { t = t.replace(/\%20/g, " "), t = t.replace(/\%27/g, "'"), t = t.replace(/\%28/g, "("), t = t.replace(/\%29/g, ")"), m += parseInt(g.G_dext5plugIn.getSizeByLocation(t), 10) } catch (q) { DEXT5 && DEXT5.logMode && console && console.log(q) } else -1 == t.toLowerCase().indexOf("/images/editor/emoticon/") && ("1" == a || "0" == a && 0 == t.toLowerCase().replace("http://", "").replace("https://", "").indexOf(b.toLowerCase())) && l.push(t)
								} 0 <
									l.length ? DEXT5.GetFileSize(l, function(a) { m += parseInt(a, 10); c && c(e < m) }, d.ID) : c && c(e < m)
						}
					}
				} catch (r) { DEXT5 && DEXT5.logMode && console && console.log(r) }
			}; DEXT5.setContentsInitSize = DEXT5.SetContentsInitSize = function(a, e) {
				try { if ("undefined" != typeof a) { var c = DEXT5.getEditorByName(e); if (c) { DEXTTOP.G_CURREDITOR = c; var b = DEXTTOP.G_CURREDITOR._FRAMEWIN, d = c = 0, g; for (g in a) b.G_IMG_CURRENT_ARRAYLIST.push(g), b.G_IMG_CURRENT_LIST[g] = a[g], b.G_IMG_TOTAL_LIST[g] = a[g], c++, d += a[g]; b.G_IMG_TOTAL_COUNT = c; b.G_IMG_TOTAL_SIZE = d } } } catch (h) {
					DEXT5 &&
					DEXT5.logMode && console && console.log(h)
				}
			}; DEXT5.setTempRootDirectory = DEXT5.SetTempRootDirectory = function(a, e) { try { var c = DEXT5.getEditorByName(e); if (c) { var b = c._FRAMEWIN; if (b && "ieplugin" == c._config.runtimes) try { b.G_dext5plugIn.SetTempRootDirectory(a) } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } } } catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) } }; DEXT5.manuallyTempSave = DEXT5.ManuallyTempSave = function(a) {
				try {
					var e = DEXT5.getEditorByName(a); if (e) {
						var c = DEXT5.getHtmlValueExWithDocType(e.ID,
							!0); e._FRAMEWIN.fn_saveToLocalStorage(e, c) && e._setting.auto_save.toString().split(",")[1] && null != e._setting.auto_save_fn && (DEXTTOP.DEXTWIN.clearInterval(e._setting.auto_save_fn), e._setting.auto_save_fn = e._FRAMEWIN.fn_AutoSaveInterval(e))
					}
				} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
			}; DEXT5.closeDialogPopup = DEXT5.CloseDialogPopup = function(a) {
				var e, c; try { if (e = DEXT5.util._setDEXT5editor(a)) { c = e._FRAMEWIN; var b = DEXTDOC.getElementById("dext_dialog"); "undefined" != typeof b && c.event_dext_upload_cancel(b) } } catch (d) {
					DEXT5 &&
					DEXT5.logMode && console && console.log(d)
				}
			}; DEXT5.findWord = DEXT5.FindWord = function(a, e) {
				var c, b; try {
					if (c = DEXT5.util._setDEXT5editor(e)) if (b = c._FRAMEWIN, "undefined" != typeof a && "undefined" != typeof a.searchText) {
						var d = "undefined" != typeof a.caseSensitive ? a.caseSensitive : !1, g = "undefined" != typeof a.revertEndDocument ? a.revertEndDocument : !0, h = "undefined" != typeof a.wordByWord ? a.wordByWord : !1, k = "undefined" != typeof a.searchDirection ? a.searchDirection : !0; if (1 == !!a.initFocus) {
							b.focus(); b.document.body.focus(); var l =
								b.getFirstRange().range; if (k) l.setStart(b._iframeDoc.body, 0), l.setEnd(b._iframeDoc.body, 0); else { var n; try { n = b._iframeDoc.body.childNodes.length } catch (m) { n = 0 } l.setStart(b._iframeDoc.body, n); l.setEnd(b._iframeDoc.body, n) } var t = b._iframeWin.getSelection ? b._iframeWin.getSelection() : b._iframeDoc.selection; t && l && (l.select ? l.select() : t.removeAllRanges && t.addRange && (t.removeAllRanges(), t.addRange(l))); c._LastRange = l; b.g_findRepalceRange = null
						} b.event_dext_find(a.searchText, d, g, h, k)
					}
				} catch (q) {
					DEXT5 && DEXT5.logMode &&
					console && console.log(q)
				}
			}; DEXT5.setHighlight = DEXT5.SetHighlight = function(a, e) {
				var c, b; try {
					(c = DEXT5.getEditorByName(e)) && "1" == c._config.highlight.use && "undefined" != typeof a && "undefined" != typeof a.searchText && "undefined" != typeof a.addOrRemove && (a.addOrRemove += "", a.addOrRemove = a.addOrRemove.toLowerCase(), "add" == a.addOrRemove || "remove" == a.addOrRemove) && (b = c._FRAMEWIN, "undefined" == typeof a.caseSensitive && (a.caseSensitive = !1), "undefined" == typeof a.wholeWordsOnly && (a.wholeWordsOnly = !1), "remove" == a.addOrRemove &&
						"view" != c._config.mode && b.changeBodyContenteditable(!0), c._FRAMEWIN.command_highlightAll(c, c._FRAMEWIN.document.getElementById("dext5_design_" + c.ID), a.addOrRemove, a.searchText, a.caseSensitive, a.wholeWordsOnly), "add" == a.addOrRemove && "view" != c._config.mode && b.changeBodyContenteditable(!1))
				} catch (d) { c && "undefined" != typeof c && "view" != c._config.mode && b.changeBodyContenteditable(!0) }
			}; DEXT5.setUserField = DEXT5.SetUserField = function(a, e, c) {
				var b, d; try {
					if (b = DEXT5.getEditorByName(c)) {
						var g = b._config.post_url;
						c = ""; var h; if (-1 < g.indexOf("?")) { c = g.substring(g.indexOf("?") + 1); g = g.substring(0, g.indexOf("?")); h = c.split("&"); c = 0; for (var k = h.length, l; c < k; c++)if (l = h[c].split("="), l[0] == b._config.userFieldID) { h[c] = a + "=" + e; break } } else h.push(a + "=" + e); g = g + "?" + h.join("&"); b._config.post_url = g; d = b._FRAMEWIN; d.G_dext5plugIn && "ieplugin" == b._config.runtimes && (d.G_dext5plugIn.sUserFieldID = a, d.G_dext5plugIn.sUserFieldValue = e, d.G_dext5plugIn.postPageURL = g); b._config.userFieldID = a; b._config.userFieldValue = e
					}
				} catch (n) {
					DEXT5 &&
					DEXT5.logMode && console && console.log(n)
				}
			}; DEXT5.setReadOnly = DEXT5.SetReadOnly = function(a, e, c) {
				try {
					var b = DEXT5.getEditorByName(c); if (b) {
						var d = b._FRAMEWIN; "undefined" != typeof arguments && 3 == arguments.length && "undefined" != typeof e && (e = e.replace(/ /gi, ""), d.G_ICON_READONLY_ACTIVATION_SET = e.split(",")); b._config.editorBodyEditableMode = "2"; a ? (b._config.editorBodyEditable = !1, b._iconEnable("default"), b._iconEnable("editableFalse"), "" != b._config.placeholder.content && d.placeholderControl(b, "remove")) : (b._config.editorBodyEditable =
							!0, b._iconEnable("default"), "" != b._config.placeholder.content && (d.placeholderControl(b, "set"), d.placeholderControl(b, "class"))); d.changeBodyContenteditable(!a); var g = d.document.getElementById("ue_" + b.ID + "source"); g && (g.style.display = a ? "none" : ""); var h = d.document.getElementById("ue_" + b.ID + "preview"); h && (h.style.display = a ? "none" : ""); var k = d.document.getElementById("ue_" + b.ID + "text"); k && (k.style.display = a ? "none" : "")
					}
				} catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) }
			}; DEXT5.setCustomDisableIconList =
				DEXT5.SetCustomDisableIconList = function(a, e) { try { var c = DEXT5.getEditorByName(e); if (c) { var b = c._FRAMEWIN; "undefined" != typeof a && (a = a.replace(/ /ig, ""), b.G_ICON_CUSTOM_ADD_DISABLED_SET = "" != a ? a.split(",") : [], c._iconEnable(""), c._iconEnable("default")) } } catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) } }; DEXT5.addFormData = DEXT5.AddFormData = function(a, e, c) {
					var b, d; try {
						if (a && "" != a && null != e && void 0 != e && (b = DEXT5.getEditorByName(c))) {
							d = b._FRAMEWIN; var g = d.G_FormData, h = g.length; c = !0; for (var k = 0; k < h; k++)if (g[k].form_name.toLowerCase() ==
								a.toLowerCase()) { g[k].form_value = e; c = !1; break } c && d.G_FormData.push({ form_name: a, form_value: e }); "ieplugin" == b._config.runtimes && d.G_dext5plugIn.AddUserFormData(a, encodeURIComponent(e))
						}
					} catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) }
				}; DEXT5.setZoom = DEXT5.SetZoom = function(a, e) {
					try { 0 == !!a && (a = "100%"); var c = DEXT5.getEditorByName(e); if (c) { var b = c._FRAMEWIN, d = b.document.getElementById("ue_" + c.ID + "zoom_text"); d && (d.value = a, b.command_zoom(c.ID, b.document.getElementById("dext5_design_" + c.ID))) } } catch (g) {
						DEXT5 &&
						DEXT5.logMode && console && console.log(g)
					}
				}; DEXT5.getSelectedContent = DEXT5.GetSelectedContent = function(a) {
					try {
						var e = DEXT5.getEditorByName(a); if (e) {
							a = { text: "", html: "" }; var c = e._FRAMEWIN, b = c.getFirstRange().range; if (null == b || 1 == b.collapsed || void 0 == b) b = c._dext_editor._LastRange; if (null != b && b.cloneContents()) {
								dummyDiv = document.createElement("div"); dummyDiv.appendChild(b.cloneContents()); a.html = dummyDiv.innerHTML; for (var d = dummyDiv.getElementsByTagName("p"), g = d.length, e = 0; e < g; e++) {
									var h = d[e]; "" == h.textContent &&
										0 == RegExp("<br.*\\n*<br", "gim").test(h.innerHTML) && (h.innerHTML = "")
								} var k = dummyDiv.innerHTML, k = k.replace(/\r/g, ""), k = k.replace(/[\n|\t]/g, ""), k = k.replace(/[\v|\f]/g, ""), k = k.replace(/<p><br><\/p>/gi, "\n"), k = k.replace(/<P>&nbsp;<\/P>/gi, "\n"), k = k.replace(/<br(\s)*\/?>/gi, "\n"), k = k.replace(/<br(\s[^\/]*)?>/gi, "\n"), k = k.replace(/<\/p(\s[^\/]*)?>/gi, "\n"), k = k.replace(/<\/li(\s[^\/]*)?>/gi, "\n"), k = k.replace(/<\/tr(\s[^\/]*)?>/gi, "\n"), k = k.replace(/<\/h[1-5](\s[^\/]*)?>/gi, "\n"); nIdx = k.lastIndexOf("\n");
								-1 < nIdx && "\n" == k.substring(nIdx) && (k = k.substring(0, nIdx)); k = c.removeStripTags(k, null); k = c.unhtmlSpecialChars(k); a.text = k; delete dummyDiv
							} return a
						}
					} catch (l) { DEXT5 && DEXT5.logMode && console && console.log(l) }
				}; DEXT5.replaceTextAll = DEXT5.ReplaceTextAll = function(a, e) {
					var c, b; try {
						if (c = DEXT5.util._setDEXT5editor(e)) b = c._FRAMEWIN, "undefined" != typeof a && "undefined" != typeof a.searchText && "undefined" != typeof a.replaceText && b.event_dext_all_replace(a.searchText, a.replaceText, "undefined" != typeof a.caseSensitive ?
							a.caseSensitive : !1, null, "undefined" != typeof a.wordByWord ? a.wordByWord : !1, null, "undefined" != typeof a.showAlert ? !a.showAlert : !0, "undefined" != typeof a.selection ? a.selection : !1)
					} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
				}; DEXT5.replaceText = DEXT5.ReplaceText = function(a, e) {
					var c, b; try {
						if (c = DEXT5.util._setDEXT5editor(e)) b = c._FRAMEWIN, "undefined" != typeof a && "undefined" != typeof a.searchText && "undefined" != typeof a.replaceText && b.event_dext_replace(a.searchText, a.replaceText, "undefined" != typeof a.caseSensitive ?
							a.caseSensitive : !1, "undefined" != typeof a.reverse ? !a.reverse : !0, "undefined" != typeof a.wordByWord ? a.wordByWord : !1, "undefined" != typeof a.direction ? a.direction : !1, "undefined" != typeof a.showLocation ? !a.showLocation : !0)
					} catch (d) { DEXT5 && DEXT5.logMode && console && console.log(d) }
				}; DEXT5.setEditorBodyWidth = DEXT5.SetEditorBodyWidth = function(a, e) {
					try {
						var c = DEXT5.util._setDEXT5editor(e); if (c) {
							var b = c._config; if ("1" == b.ruler.use && "1" == b.autoBodyFit) {
								var d = c._FRAMEWIN, c = a; "string" == typeof c && (c = parseInt(c, 10)); 0 <
									c && (d.G_BodyFit.SetBodyWidth(c), d.G_Ruler && d.G_Ruler.SetRulerPosition())
							}
						}
					} catch (g) { DEXT5 && DEXT5.logMode && console && console.log(g) }
				}; DEXT5.getStatusBarMode = DEXT5.GetStatusBarMode = function(a) { try { var e = DEXT5.getEditor(a); if (e) return e.getEditorMode() } catch (c) { DEXT5 && DEXT5.logMode && console && console.log(c) } }; DEXT5.replaceAllContents = DEXT5.ReplaceAllContents = function(a, e) {
					try {
						if (0 != !!a) {
							var c = DEXT5.getEditor(e); if (c) {
								var b = c._FRAMEWIN, d = { searchValue: "", newValue: "", ignoreCapital: !0, ignoreAlert: !0 }, g; for (g in a) d[g] =
									a[g]; "" == d.searchValue && "" == d.newValue || b.command_replaceAll(c.ID, c._EDITOR.design, d.searchValue, d.newValue, !d.ignoreCapital, !1, !0, !1, d.ignoreAlert, !1)
							}
						}
					} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
				}; DEXT5.setHorizontalLineHeight = DEXT5.SetHorizontalLineHeight = function(a, e) {
					try {
						if (0 != !!a) {
							var c = DEXT5.util._setDEXT5editor(e); if (c) {
								var b = c._FRAMEWIN, d = c._config, g = d.horizontalLine.height; "undefined" != typeof a.height && (g = DEXT5.util.parseIntOr0(a.height), d.horizontalLine.height = g); "undefined" !=
									typeof a.repeat && (d.horizontalLine.repeat = a.repeat); "2" == d.horizontalLine.use && b.set_horizontal_line_2(c)
							}
						}
					} catch (h) { DEXT5 && DEXT5.logMode && console && console.log(h) }
				}; DEXT5.getWrittenAreaSize = DEXT5.GetWrittenAreaSize = function(a) {
					try {
						var e = DEXT5.util._setDEXT5editor(a); a = { width: 0, height: 0, min_width: 0, min_height: 0 }; if (e) {
							var c = e.getEditorMode(); "source" != c && "preview" != c && "text" != c || e.setChangeMode("design"); var b = e._BODY, d = e._DOC.documentElement, g = e._FRAMEWIN.document.getElementById("ue_editor_holder_" +
								e.ID); a.min_width = g.clientWidth; a.min_height = g.clientHeight; a.width = Math.max(b.scrollWidth, b.offsetWidth, d.clientWidth, d.scrollWidth, d.offsetWidth, g.clientWidth); a.height = Math.max(b.scrollHeight, b.offsetHeight, d.clientHeight, d.scrollHeight, d.offsetHeight, g.clientHeight); var h = DEXTTOP.G_CURREDITOR._FRAMEWIN.document.body.offsetHeight <= b.scrollHeight; if (DEXTTOP.G_CURREDITOR._FRAMEWIN.document.body.offsetWidth <= b.scrollWidth || h) a.width += 15, a.height += 30
						} return a
					} catch (k) {
						DEXT5 && DEXT5.logMode && console &&
						console.log(k)
					}
				}; DEXT5.getFontStyleForCaret = DEXT5.GetFontStyleForCaret = function(a) { var e = { fontFamily: "", fontSize: "", lineHeight: "" }; if (e = DEXT5.util._setDEXT5editor(a)) { if ("design" != e.getEditorMode()) return null; a = e._FRAMEWIN; a.dext_src_init(e._EDITOR.design); a.restoreSelection(); var e = null, c = a.getFirstRange().range; c && c.startContainer && (e = a.getMyElementNode(c.startContainer)); return null == e ? null : e = a.getFontStyle(e) } return null }; DEXT5.setBodyTextValue = DEXT5.SetBodyTextValue = function(a, e) {
					try {
						var c =
							DEXT5.util.parseSetApiParam(a); a = c.html; e = DEXT5.util._getEditorName(e); null != e && (DEXT5.isLoadedEditorEx(e) ? (a = DEXTTOP.G_CURREDITOR._FRAMEWIN.htmlSpecialChars(a), a = DEXTTOP.G_CURREDITOR._FRAMEWIN.addLineBreaker(DEXTTOP.G_CURREDITOR._config.enterTag, a), DEXT5.setBodyValue(a, e)) : (null == DEXT5.InitEditorDataHashTable && (DEXT5.InitEditorDataHashTable = new DEXT5.util.hashTable), DEXT5.InitEditorDataHashTable.setItem(e, { mode: "setBodyTextValue", value: c })))
					} catch (b) { DEXT5 && DEXT5.logMode && console && console.log(b) }
				};
	var rv = DEXT5.rvi, rva = [rv.maj.join(rv.s1), parseInt(rv.mi1.join(rv.s2), 10) * rv.m1, rv.mi2.join(rv.s2), rv.l[0]]; DEXT5.UrlStamp = parseInt(rv.mi1.join(rv.s2), 10) * rv.m2 + rv.l[0]; DEXT5._$0 = DEXT5.util.makeEncryptParamEx2(rva.join(rv.s1)); delete DEXT5.rvi; DEXT5._$0S = ""; DEXT5._$0C = ""; var crvi = DEXT5.crvi, crva = [crvi.maj, crvi.mi1, crvi.mi1, crvi.l]; DEXT5._$0C = DEXT5.util.makeEncryptParamEx2(crva.join(crvi.s1)); delete DEXT5.crvi; "undefined" != typeof window && window.DEXT5 && (DEXT5.config = {
		Width: "", Height: "", SkinName: "", InitXml: "",
		InitServerXml: "", ToSavePathURL: "", ToSaveFilePathURL: "", ServerDomain: "", HandlerUrl: "", InitVisible: !0, DefaultMessage: "", FirstLoadType: "", FirstLoadMessage: "", FileFieldID: "", EditorHolder: "", TrustSites: "", zTopMenu: "", zToolBar: "", zTopStatusBar: "", zStatusBar: "", G_AP10: 8, ShowTopMenu: "", ShowToolBar: "", ShowTopStatusBar: "", ShowStatusBar: "", zXssUse: "", zXssRemoveTags: "", zXssRemoveEvents: "", zXssAllowEventsAttribute: "", XssUse: "", XssRemoveTags: "", XssRemoveEvents: "", XssAllowEventsAttribute: "", XssCheckAttribute: "",
		XssAllowUrl: "", Mode: "", zIndex: "", ZIndex: "", NextTabElementId: "", IgnoreSameEditorName: "0", ChangeMultiValueMode: "", EditorBorder: "", GridColor: "", GridColorName: "", GridSpans: "", GridForm: "", Encoding: "", DocType: "", Xmlnsname: "", ShowFontReal: "", ShowFontRealMode: "", InitLang: "", Lang: "", UseLang: "", IconName: "", CustomCode: "", EnterTag: "", userFontFamily: "", userFontSize: "", UserFontFamily: "", UserFontSize: "", DefaultFontFamily: "", DefaultFontSize: "", DefaultLineHeight: "", DefaultFontMarginTop: "", DefaultFontMarginBottom: "",
		UserFieldID: "", UserFieldValue: "", LicenseKey: "", mimeCharset: "", mimeConentEncodingType: "", mimeFileTypeFilter: "", mimeLocalOnly: "", mimeRemoveHeader: "", userOpenDlgTitle: "", userOpenDlgType: "", userOpenDlgInitDir: "", userImageDlgStyle: "", mimeBaseURL: "", focusInitObjId: "", AutoMoveInitFocus: { Use: "", TargetWindow: "" }, focusInitEditorObjId: "", tabIndexObjId: "", tabIndexObjValue: "", pluginUse: "", imageEditorUse: "", mimeUse: "", officeLineFix: "", useLog: "", MimeCharset: "", MimeConentEncodingType: "", MimeFileTypeFilter: "", MimeLocalOnly: "",
		MimeRemoveHeader: "", UserOpenDlgTitle: "", UserOpenDlgType: "", UserOpenDlgInitDir: "", UserImageDlgStyle: "", MimeBaseURL: "", FocusInitObjId: "", FocusInitEditorObjId: "", TabIndexObjId: "", TabIndexObjValue: "", PluginUse: "", ImageEditorUse: "", MimeUse: "", OfficeLineFix: "", UseLog: "", RootPath: "", EditorTabDisable: "", FormListUrl: "", EmoticonUrl: "", RunTimes: "", Runtimes: "", MouseEventCancel: !1, KeyEventCancel: !1, InputEventCancel: !1, DragEventCancel: !1, SetValueObjId: "", LoadedEvent: "", Event: {
			LoadedEvent: null, OnError: null, FrameLoaded: null,
			SetComplete: null, Resized: null, EditorLoaded: null, BeforePaste: null, CustomAction: null, AfterChangeMode: null, LanguageDefinition: null, AfterPopupShow: null, Mouse: null, Command: null, Key: null, Input: null, PasteImage: null, ManagerImg: null, ManagerInput: null, ManagerSelect: null, ManagerTextArea: null, ContentSizeChange: null, BeforeInsertUrl: null, BeforeFullScreen: null, FullScreen: null, SetInsertComplete: null, SetForbiddenWordComplete: null, Drag: null, Focus: null, DialogLoaded: null, BeforeInsertHyperlink: null, InsertEmoticon: null,
			ApplyFontStyle: null
		}, scrollOverflow: "", ScrollOverflow: "", G_AP: { G_AP29: "w", G_AP22: "a" }, DocType: "", DevelopLangage: "", UnitDelimiter: "", UnitAttributeDelimiter: "", EditorBodyEditable: "", EditorBodyEditableMode: "", SymbolUrl: "", AccessibilityValidationItems: "", TabSequenceMode: "", ViewModeAutoHeight: "", ViewModeAutoWidth: "", ShowDialogPosition: "", StatusBarItem: "", StatusBarItemTitle: "", StatusBarInitMode: "", ImageBaseUrl: "", DragResize: "", DragResizeApplyBrowser: "", DragResizeMovable: "", DragResizeApplyDivClass: "", ReplaceOutsideImage: "",
		ReplaceOutsideImageExceptDomain: "", ReplaceOutsideImageTargetDomain: "", RemoveItem: "", RemoveContextItem: "", ManagerMode: "", EventList: "", TableLock: { Use: "", LockName: "", ShowLockIconUserMode: "", TableLockMode: "" }, FontFamilyList: "", DisplayFontFamilyList: "", FontSizeList: "", LineHeightList: "", ZoomList: "", FormattingList: "", LetterSpacingList: "", FrameFullScreen: "", DialogWindow: null, AdminTableLock: { DefaultShowLockIconUserMode: "", DefaultLockName: "", CheckLockName: "", DefaultTableLockMode: "" }, UserTableLock: {
			Use: "", LockName: "",
			DefaultTableLockMode: "", DefaultShowLockIcon: "", TableLockMode: "", AllowChangeMode: ""
		}, RemoveComment: "", SaveFolderNameRule: "", SaveFolderNameRuleAllowCustomFolder: "", SaveFileFolderNameRule: "", SaveFileFolderNameRuleAllowCustomFolder: "", ToolBarGrouping: "", SetDefaultValueInEmptyTag: "", LoadErrorCustomMessage: "", PhotoEditorId: "", ProductKey: "", LicenseKey: "", DirectEditHtmlUrl: "", UserHelpUrl: "", UseGZip: "0", StatusBarLoading: "", TopStatusBarLoading: "", HandlerUrlSaveForNotes: "", HasContainer: "", Security: {
			EncryptParam: "",
			FileExtensionDetector: ""
		}, TabSpace: "", AutoBodyFit: "", UseNoncreationAreaBackground: "", Ruler: { Use: "", Mode: "", InitPosition: "", ViewPointer: "", ViewGuideLine: "", ViewRuler: "", GuideLineStyle: "", GuideLineColor: "", MoveGuideLineStyle: "", MoveGuideLineColor: "", UseInoutdent: "", MoveGap: "", UseResizeEvent: "", DefaultView: "", AutoFitMode: "", FixEditorWidth: "", UseVertical: "", UseMouseGuide: "", UsePointerValue: "" }, UseConfigTimeStamp: "1", UseHorizontalLine: "", UseHorizontalLineHeight: "", UseHorizontalLineStyle: "", UseHorizontalLineRepeat: "",
		Document: { DocTitle: "" }, ImgUploadContenttype: "", ImageCustomPropertyDelimiter: "", LineHeightMode: "", FontSizeIncDecGap: "", LineHeightIncDecGap: "", LetterSpacingIncDecGap: "", PrintMarginltrb: "", ApplyStyleEmptyTag: "", UndoCount: "", AllowDeleteCount: "", AutoDestroy: { Use: "", MoveCursor: "" }, InitFocus: "", AllowUnableToDeleteMsg: "", PasteToImage: "", PasteToImagePopupMode: "", PasteToImageType: "", ImageAutoFit: "", SaveFileNameRule: "", ImageConvertFormat: "", ImageConvertWidth: "", ImageConvertHeight: "", Accessibility: "", SourceViewtype: "",
		UserCssUrl: "", UserCssAlwaysSet: "", WebFontCssUrl: "", WebFontCssAlwaysSet: "", UserJsUrl: "", SystemTitle: "", PasteRemoveEmptyTag: "", DefaultImemode: "", DisableTabletap: "", EmptyTagRemoveInSetapi: "", ReplaceEmptySpanTagInSetapi: "", ReplaceEmptySpanTagInSetapiOnlyTable: "", WrapPtagToSource: "", WrapPtagToGetApi: "", WrapPtagSkipTag: "", UseHtmlCorrection: "", RemoveIncorrectAttribute: "", ReplaceSpace: "", UseHtmlProcessByWorker: "", UseHtmlProcessByWorkerSetApi: "", UnOrderListDefaultClass: "", OrderListDefaultClass: "", SkipTagInParser: "",
		HtmlCorrectionLimitLength: "", G_AP20: "z", ShowLineForBorderNone: "", ShowLineForBorderNoneSkipClass: "", ShowLineForBorderNoneSkipAttribute: "", TableAutoAdjust: "", PluginKeepVersion: "", ApplyFormatExceptStyle: "", EnforceDoctype: "", UserColorPicker: "", PluginInstallType: "", PluginInstallUrl: "", UsePluginInstallGuide: "", PluginInstallGuideType: "", PluginInstallGuideZIndex: "", ImageAutoConvert: "", UploadMethod: "", UploadImageFileObject: "", UploadUseHTML5: "", RemoveMsoClass: "", TableTemplateListUrl: "", UseBasicTemplate: "",
		UseReplaceImage: "", UseTableBackgroundImage: "", OpenDocument: { BeforeOpenEvent: "", UseHwp: "", Word: { MaxSize: "", MaxPage: "" }, Excel: { MaxSize: "", MaxSheet: "" }, Powerpoint: { MaxSize: "", MaxSlide: "" }, UseHtml5FileOpen: "" }, ButtonText001: "", MaxMediaFileSize: "", MaxImageFileSize: "", MaxImageBase64fileCount: "", MaxFlashFileSize: "", MaxInsertFileSize: "", MaxVideoFileSize: "", TableDefaultWidth: "", TableDefaultHeight: "", TableDefaultClass: "", TableDefaultInoutdent: "", TableInitInoutdent: "", TableDefaultTdHeight: "", TableRowMaxCount: "",
		TableColMaxCount: "", TableUseHeight: "", AllowInoutdentText: "", DefaultBorderColor: "", UseTableBorderAttribute: "", TableClassList: "", TableLineStyleList: "", TableNoResizeClass: "", TableNoSelectionClass: "", TableNoActionClass: "", TableAutoAdjustInPaste: "", TableAutoAdjustInSetHtml: "", TableNearCellBorderStyle: "", RemoveEmptyTag: "", RemoveEmptyTagSetValue: "", RemoveEmptyTagInsertNbspForLineBreak: "", RemoveEmptyTagReplaceDefaultParagraphValue: "", PasteImageBase64Remove: "", UseUndoLightMode: "", FullScreenTop: "", FullScreenLeft: "",
		FullScreenRight: "", FullScreenBottom: "", TextPasteMode: "", AdjustCursorInTable: "", ViewImgBase64Source: "", G_AP13: 7, RemoveStyle: { Use: "", Tag: "", Style: "" }, PersonalData: "", ForbiddenWord: "", ForbiddenWordMode: "", ForbiddenWordWidth: "", ForbiddenWordHeight: "", UseLocalFont: "", UseRecentlyFont: "", WordCount: { Use: "", Limit: "", LimitChar: "", LimitByte: "", LimitLine: "", CountWhiteSpace: "", LimitMessage: "", LimitCount: "" }, SetAutoSave: {
			Mode: "", Interval: "", MaxCount: "", UniqueKey: "", UseEncrypt: "", UseManuallySave: "", UseManuallySaveShortcutKey: "",
			SaveAndStartInterval: "", PopupWidth: "", PopupHeight: ""
		}, SaveHtmlName: "", MiniPhotoEditor: { Width: "", Height: "", BackgroundColor: "", BackgroundOpacity: "", ImgTagRemoveAttribute: "" }, UseFontFamilyKeyin: "", UseFontSizeKeyin: "", UseLineHeightKeyin: "", UseFontSizeIncDec: "", UseLineHeightIncDec: "", UseLetterSpacingIncDec: "", AutoList: { Use: "" }, Figure: { Use: "", FigureStyle: "", FigcaptionStyle: "", DefaultCaption: "" }, AutoPluginUpdate: "", G_AP23: "n", BrowserSpellCheck: "", UseMouseTableInoutdent: "", LimitTableInoutdent: "", DisableInsertImage: "",
		UsePersonalSetting: "", OlUlTagMode: "", InsertMultiImage: "", AllowOpenFileType: "", RemoveHwpDummyTag: "", AfterPasteCaretPosition: "", ExcelImageFix: "", ColorPickerInputKind: "", CellSelectionMode: "", TableInsertDragSize: "", AdjustEmptySpan: "", ImageContinueInsertMaintainValue: "", UseTablePasteDialog: "", UseLineBreak: "", WordBreakType: "", WordWrapType: "", SaveLineBreakToLocalStorage: "", SplitCellStyle: "", KeepOriginalHtml: "", UseAutoToolBar: "", AutoToolBar: null, UseEnterpriseMode: "", DefaultBodySpace: { Use: "", Mode: "", Value: "" },
		PluginTempPath: "", OlListStyleTypeSequence: "", UlListStyleTypeSequence: "", PasteRemoveSpanAbsolute: "", UseFormPrint: "", DragMove: "", PasteWhenTableIsLast: "", AllowImgSize: "", ContextMenuDisable: "", IECompatible: "", AutoUrlDetect: "", CustomEventImageOndbclick: "", CustomEventHyperlinkOndbclick: "", HorizontalLine: "", SetDefaultStyle: { Value: "", BodyId: "", UserStyle: "", LineHeightMode: "", DextSetStyle: "", RemoveBodyStyleInSet: "" }, DragAndDropAllow: "", LimitPasteHtml: "", LimitPasteHtmlLength: "", RemoveSpaceInTagname: "", ViewModeBrowserMenu: "",
		ViewModeAllowCopy: "", EventForPasteImage: "", RemoveColgroup: "", InsertCarriageReturn: "", ReturnEventMouse: "", ReturnEventKeyboard: "", ReturnEventCommand: "", ReturnEventFocus: "", ReturnEventDrag: "", ReturnEventInput: "", UseCorrectInOutdent: "", Ie11BugFixedJASO: "", Ie11BugFixedReplaceBr: "", Ie11BugFixedDeleteTableAlign: "", Ie11BugFixedReplaceAlignMargin: "", Ie11BugFixedTypingBugInTable: "", IeBugFixedHyfont: "", IeBugFixedHyfontReplaceFont: "", IeBugFixedApplyAllBrowser: "", ReplaceEmptyTagWithSpace: "", RemoveLastBrTag: "",
		HybridWindowMode: "", UseGetHtmlToLowerCase: "", PasteImageBase64: "", EmptyTagRemove: "", CustomCode: "", AllowMediaFileType: "", AllowImageFileType: "", AllowFlashFileType: "", AllowInsertFileType: "", AttachFileImage: "", InoutdentDefaultSize: "", HyperLinkTargetList: "", HyperlLinkTargetBasicList: "", HyperLinkCategoryList: "", HyperLinkProtocolList: "", ValidateUrlLink: "", ImgDefaultWidth: "", ImgDefaultHeight: "", ImgDefaultMarginLeft: "", ImgDefaultMarginRight: "", ImgDefaultMarginTop: "", ImgDefaultMarginBottom: "", UseSelectionMark: "",
		SourceViewtypeUnformatted: "", SourceViewtypeEmptyTagMode: "", ResizeBar: "", ToolBarAdmin: "", ToolBar1: "", ToolBar2: "", UseMiniImageEditor: "", AutoToolbar: { Default: "", SelectedSingleCell: "", SelectedMultiCell: "", FocusInCell: "", SelectArea: "", FocusImage: "" }, PrintPreview: "", PrintLandscape: "", PrintHeader: "", PrintFooter: "", UsePrintApp: "", PluginRoot: "", PluginVersion: "", AllowLinkMediaCaption: "", CustomCssUrl: "", HtmlprocessCustomText: "", RemoveImageInPasteExcel: "", RemoveTdStyleInPastePpt: "", HelpStartMainPage: "", AddHttpHeader: "",
		ToolBarEnableDisableMode: "", RemoveTempFolderDataDay: "", ForceSaveAsServer: "", CheckApplyWordBreakForTable: "", Highlight: { Use: "", Color: "" }, PopupBackgroundHolderId: "", ReplaceLineBreak: "", AutoGrowMode: "", AdjustCurrentColorInSetApi: "", AdjustTextIndentInPaste: "", UndoMode: "", RemoveDummyFontTagInPaste: "", SetDefaultTagInEmptyCell: "", InsidePaddingTdSetting: "", RemoveCarriageReturnInTag: "", ClickCtrlHyperlink: "", SymbolCustomCssUrl: "", RemoveLangAttribute: "", ItemCustomUrl: { Item: "", Url: "" }, AdjustCellSizeAfterDeleteCell: "",
		RemoveDummyDivInHwpPaste: "", ImageSizeToServer: "", PasteToTextMode: "", AdjustCursorInRelative: "", MoveStyleTagToHead: "", RemoveDummyTag: "", UndoLimitLength: "", AutoFitInHolder: "", Placeholder: { Content: "", FontColor: "", FontSize: "", FontFamily: "" }, IgnoreDifferentEditorName: "", PasteTextLineBreak: "", ReplaceMsoStyle: { SettingStyle: "", StyleValue: null, ReplaceAttributeName: "dext" }, CleanNestedTagOptions: { RemoveTag: "", AllowStyle: "", NestedCount: "" }, RemoveFontType: { FontFamily: "", Type: "" }, SetEmoticonObject: "", UnderlineAndStrikeThroughMode: "",
		ReplaceRgbToHex: "", TableDefaultCellPadding: "", PersonalSettingUseFontFamilyKeyin: "", PersonalSettingUseFontSizeKeyin: "", PersonalSettingUseLineHeightKeyin: "", FileFilterPlugin: "", FileFilterHtml5: "", DialogWindowScroll: "", KeepImageOriginalSizeAutoCheck: "", ParagraphAttributeType: "", UploadFileNameEncoding: "", UseFindReplaceShortcut: "", UsePasteToolbarAndContext: "", DisableErrorConfirmMessage: "", DeleteTableUsingKey: "", KeepFontFamily: "", AutoSetZoom: { Use: "", CheckNode: null }, CustomCssUrlDetailApply: "", ResizeBarHolderSync: "",
		ImageQuality: "", MobilePopupMode: "", ReplaceMsStyleName: "", Compatibility: { DingBatChar: "", AutoResizePastedImage: "", HwpPasteImageInHtml5: "", HwpPasteBulletInHtml5: "", HwpProcessTypeInPlugin: "", FontTagToSpan: "" }, InsertSourceTagInVideo: "", ForceFontFamilyChange: "", WidthFix: { Value: "", BackgroundColor: "", DefaultView: "", Border: "", Padding: "" }, AutoSetDocumentDomain: "", EditingAreaBgColor: "", DisableUnnecessaryKeyEvt: "", RemoveFontSizeApplyHTag: "", UseTableDiagonal: "", ShowDiagonalInIEViewPage: "", UseNotificationForDiagonal: "",
		DefaultMinHeight: ""
	}); "undefined" != typeof window && window.DEXT5 && (DEXT5.startUpEditor = DEXT5.StartUpEditor = function(a) {
		try { void 0 != a && "" != a || 1 != DEXT5.DEXTMULTIPLEID.length || (a = DEXT5.DEXTMULTIPLEID[0]); var e = document.getElementById("dext_frame_" + a); if ("undefined" != typeof e && -1 < e.getAttribute("src").indexOf("editor_dummy.html")) { var c = e.getAttribute("dext5customsrc"); e.setAttribute("dext5customsrc", ""); e.removeAttribute("dext5customsrc"); e.src = c; e.contentWindow.location.replace(c) } } catch (b) {
			DEXT5 && DEXT5.logMode &&
			console && console.log(b)
		}
	})
};
