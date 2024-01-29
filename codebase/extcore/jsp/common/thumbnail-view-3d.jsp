<%@page import="wt.epm.structure.EPMStructureHelper"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="com.ptc.wpcfg.deliverables.library.EPMDocumentMaker"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.epm.structure.EPMReferenceLink"%>
<%@page import="wt.query.QuerySpec"%>
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
EPMDocument e = null;
if (representable instanceof EPMDocument) {
	EPMDocument epm = (EPMDocument) representable;
	if (epm.getDocType().toString().equals("CADDRAWING")) {
		QueryResult qr = EPMStructureHelper.service.navigateReferences(epm, null, false);
		EPMReferenceLink link = null;
		while (qr.hasMoreElements()) {
	link = (EPMReferenceLink) qr.nextElement();
	String referenceType = link.getReferenceType().toString();
	EPMDocumentMaster master = (EPMDocumentMaster) link.getReferences();
	e = DrawingHelper.manager.latest(master);
	if (e.getDocType().toString().equals("CADCOMPONENT") && referenceType.equals("DRAWING")) {
		part = PartHelper.manager.getPart(e);
		representation = PublishUtils.getRepresentation(e, true, null, false);
	}
		}
	} else {
// 		part = PartHelper.manager.getPart(epm);
		representation = PublishUtils.getRepresentation(epm, true, null, false);
	}
} else if (representable instanceof WTPart) {
	part = (WTPart) representable;
	representation = PublishUtils.getRepresentation(e, true, null, false);
}

String temp = WTProperties.getLocalProperties().getProperty("wt.codebase.location");
String path = temp + File.separator + "extcore" + File.separator + "jsp" + File.separator + "part" + File.separator
		+ "pvz";
File dir = new File(path);
if (!dir.exists()) {
	dir.mkdirs();
}
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