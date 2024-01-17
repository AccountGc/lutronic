<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.ptc.wvs.server.ui.RepHelper"%>
<%@page import="com.ptc.wvs.common.ui.Representer"%>
<%@page import="java.io.File"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.representation.Representation"%>
<%@page import="wt.representation.Representable"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.viewmarkup.Viewable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getParameter("oid");
Representable representable = (Representable) CommonUtil.getObject(oid);
WTPart part = null;
Representation representation = null;
if (representable instanceof EPMDocument) {
	EPMDocument epm = (EPMDocument) representable;
	part = PartHelper.manager.getPart(epm);
	representation = PublishUtils.getRepresentation(part, true, null, false);
} else if (representable instanceof WTPart) {
	part = (WTPart) representable;
	representation = PublishUtils.getRepresentation(part, true, null, false);
}

String temp = WTProperties.getLocalProperties().getProperty("wt.codebase.location");
String path = temp + File.separator + "extcore" + File.separator + "jsp" + File.separator + "part" + File.separator
		+ "pvz";
File dir = new File(path);
if (!dir.exists()) {
	dir.mkdirs();
}
System.out.println("===" + representable);
System.out.println("!!!!!!!!!!!!" + representation);
if (representation != null) {
	Representer pre = new Representer();
	RepHelper.saveAsZIPFile(representation.getPersistInfo().getObjectIdentifier().getStringValue(), true, true,
	path + File.separator + "viewable.pvz");
}
%>
<script src="/Windchill/extcore/jsp/common/js/ptc/thingview/thingview.js"></script>
<script type="text/javascript">
	
<%if (representation != null) {%>
	var app;
	var session;
	var model;
	window.onload = function() {
		ThingView.init("/Windchill/extcore/jsp/common/js/ptc/thingview", function() {
			ThingView.SetDefaultSystemPreferences(Module.ApplicationType.CREOVIEW);
			app = ThingView.CreateCVApplication("CreoViewWebGLDiv");
			session = app.GetSession();
			session.LoadStructNodeWithURL("/Windchill/extcore/jsp/part/pvz/viewable.pvz", true, function(structNode, errors) {
				var shapeScene = session.MakeShapeScene(true);
				var shapeView = shapeScene.MakeShapeView(document.getElementById("CreoViewWebGLDiv").childNodes[0].id, true);
				model = shapeScene.MakeModel();
				model.LoadStructNode(structNode, true, true, function(success, isStructure, errorStack) {
				});
			});
		});
	};
<%}%>
	
</script>
<div id="CreoViewWebGLDiv" style="height: 95%; border: 0; margin: auto"></div>