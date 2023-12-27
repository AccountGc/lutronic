/************************************************
 * udc > thr > udcDEXT5Editor.js
 * Created at 2023. 7. 20. 오후 11:00:18.
 *
 * @author RAONWIZ Dev Team
 ************************************************/
var ComponentId;
var ComponentConfig = {};

/**
 * UDC 컨트롤이 그리드의 뷰 모드에서 표시할 텍스트를 반환합니다.
 */
exports.getText = function(){
	return "";
};

/**
 * 에디터에 HTML을 입력합니다.
 * @param {String} htmlData
 */
exports.setHtmlContents = function(htmlData){
	exbuilder.DEXT5.setHtmlContents(htmlData, ComponentId);
};
/**
 * 에디터에 입력한 BODY HTML을 가져옵니다.
 */
exports.getBodyValue = function(){
	return exbuilder.DEXT5.getBodyValue(ComponentId);
};

/**
 * 에디터 객체를 소멸시킵니다.
 */
exports.dispose = function(){
	// 객체 소멸시 호출됨.
	exbuilder.DEXT5.dispose(ComponentId);
};

/*
 * 쉘에서 init 이벤트 발생 시 호출.
 */
function onShlComponentInit(e){
	if(e.content){
		e.preventDefault();
	}
}

/*
 * 쉘에서 load 이벤트 발생 시 호출.
 */
function onShlComponentLoad(e){
	// make component id
	if(typeof app.getHost() === "undefined") {
		ComponentId = "dext5editor";
	} else {
		ComponentId = app.getHost().id || "dext5editor";	
	}
	
	// make component config properties	
	var allProps = app.getAllAppProperties();
	var keys = Object.keys(allProps);
	for (var idx = 0; idx < keys.length; idx++) {
		var key = keys[ idx ];
		var value = allProps[ key ];
		
		if(value) {
			ComponentConfig[ key.trim() ] =  value;	// prevent space
		}
	}

	// load resources 
	if(typeof window.DEXT5 === "undefined" || typeof window.exbuilder === "undefined") {
		var _resourceLoader = new cpr.core.ResourceLoader();			
		_resourceLoader.addScript("./thirdparty/dext5editor/js/dext5editor.js");		
		_resourceLoader.load().then(function() {
			var _resourceLoaderPlugin = new cpr.core.ResourceLoader();
			_resourceLoaderPlugin.addScript("./thirdparty/dext5editor/plugin/exbuilder/6/dext5editor.exbuilder6.js");
			_resourceLoaderPlugin.load().then(function() {
				exbuilder.DEXT5.makeComponentHolder( e.content, ComponentId );
				exbuilder.DEXT5.makeComponentObject( app, ComponentId, ComponentConfig );
			});
		});
	} else {
		exbuilder.DEXT5.makeComponentHolder(e.content, ComponentId);
		exbuilder.DEXT5.makeComponentObject(app, ComponentId, ComponentConfig);
	}
}

// support ie8-
if(typeof String.prototype.trim === "undefined") {
	String.prototype.trim = function() {
    	return this.replace(/^\s+|\s+$/g,"");
	}
}