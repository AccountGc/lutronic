/************************************************
 * app > thr > SCRIPT_SAMPLE_THR_UDC_DEXT5Editor.js
 * Created at 2023. 07. 20. 오전 10:15:48.
 *
 * @author RAONWIZ Dev Team
 ************************************************/

function setEditorValue() {
	var ipb1 = app.lookup("ipb1");	
	var udcDEXT5Editor01 = app.lookup("udcDEXT5Editor01");
	
	udcDEXT5Editor01.setHtmlContents(ipb1.value);	
}
 
function getEditorValue() {
	var lblVal = app.lookup("lblVal")
	var udcDEXT5Editor01 = app.lookup("udcDEXT5Editor01");
	
	lblVal.value = udcDEXT5Editor01.getBodyValue();	
}

/*
 * "실행" 버튼(btnSetHtml)에서 click 이벤트 발생 시 호출.
 * 사용자가 컨트롤을 클릭할 때 발생하는 이벤트.
 */
function onBtnSetHtmlClick(e){
	setEditorValue();
	
	var vcAce = app.lookup("udcSampleThr");
	vcAce.value = setEditorValue + "";
}

/*
 * "실행" 버튼(btnGetHtml)에서 click 이벤트 발생 시 호출.
 * 사용자가 컨트롤을 클릭할 때 발생하는 이벤트.
 */
function onBtnGetHtmlClick(e){
	getEditorValue();
	
	var vcAce = app.lookup("udcSampleThr");
	vcAce.value = getEditorValue + "";
}

/*
 * 사용자 정의 컨트롤에서 dext_editor_loaded_event 이벤트 발생 시 호출.
 * 에디터 생성완료 이벤트
 */
function onUdcDEXT5Editor01Dext_editor_loaded_event(e){
	var udcDEXT5Editor01 = e.control;

	//udcDEXT5Editor01.setHtmlContents("<p>hello - udcDEXT5Editor01 editor_loaded_event</p>");
}

/*
 * 사용자 정의 컨트롤에서 dext_editor_language_definition_event 이벤트 발생 시 호출.
 * 에디터 생성 시 표시문자를 재정의 하기 위한 이벤트
 */
function onUdcDEXT5Editor01Dext_editor_language_definition_event(e) {
	var udcDEXT5Editor01 = e.control;
	var _userData = e.userData;
	
	if(_userData && _userData.dext5_lang) {
		// 예시) 변경을 원하는 경우
		//_userData.dext5_lang.icon.design = "디자인 :)";
	}	
}

/*
 * 사용자 정의 컨트롤에서 dext_editor_beforepaste_event 이벤트 발생 시 호출.
 * 에디터 붙여넣기 전 이벤트
 */
function onUdcDEXT5Editor01Dext_editor_beforepaste_event(e){
	var udcDEXT5Editor01 = e.control;
	var _userData = e.userData;
	
	// 예시) 변경을 원하는 경우
	//_userData.pasteSource = "test";
	
	DEXT5.EventCallbackHashTable.setItem( _userData.editorId,  { pasteSource: _userData.pasteSource } );	
}