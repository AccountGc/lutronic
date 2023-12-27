/************************************************
 * app > thr > SCRIPT_SAMPLE_THR_UDC_DYNAMIC_DEXT5Editor.js
 * Created at 2023. 07. 20. 오전 10:15:48.
 *
 * @author RAONWIZ Dev Team
 ************************************************/

function makeEditor() {
	var udcDEXT5Editor01 = app.lookup( "udcDEXT5Editor01" );

	if ( typeof udcDEXT5Editor01 === "undefined" ) {
		var udcSampleHolder = app.lookup( "udcSampleHolder" );

		// udc 컨트롤 속성
		var voProp = {
			"id": "udcDEXT5Editor01",
			"autoSize": "none",
			"width": "800px",
			"height": "300px"
		};

		var udcDEXT5Editor01 = new udc.thr.udcDEXT5Editor( "udcDEXT5Editor01" );
		// 에디터 속성
		udcDEXT5Editor01.setAppProperty( "Lang", "en-us" );

		// 에디터 이벤트
		udcDEXT5Editor01.addEventListener( "dext_editor_loaded_event", function ( e ) {

		} );

		udcSampleHolder.addChild( udcDEXT5Editor01, voProp );
	}
}

function removeEditor() {
	var udcSampleHolder = app.lookup( "udcSampleHolder" );

	var udcDEXT5Editor01 = app.lookup( "udcDEXT5Editor01" );
	if ( typeof udcDEXT5Editor01 !== "undefined" ) {
		udcSampleHolder.removeChild( udcDEXT5Editor01, false );
	}
}

/*
 * "실행" 버튼(btnSetHtml)에서 click 이벤트 발생 시 호출.
 * 사용자가 컨트롤을 클릭할 때 발생하는 이벤트.
 */
function onBtnMakeEditorClick( e ) {
	makeEditor();

	var vcAce = app.lookup( "udcSampleThr" );
	vcAce.value = makeEditor + "";
}

/*
 * "실행" 버튼(btnGetHtml)에서 click 이벤트 발생 시 호출.
 * 사용자가 컨트롤을 클릭할 때 발생하는 이벤트.
 */
function onBtnRemoveEditorClick( e ) {
	removeEditor();

	var vcAce = app.lookup( "udcSampleThr" );
	vcAce.value = removeEditor + "";
}