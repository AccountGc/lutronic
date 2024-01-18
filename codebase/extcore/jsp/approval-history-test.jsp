<%@page import="com.e3ps.workspace.AsmApproval"%>
<%@page import="com.e3ps.workspace.AppPerLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.Persistable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "wt.doc.WTDocment:208783921";
Persistable per = CommonUtil.getObject(oid);
//일괄 결재 
if (per instanceof WTDocument) {
	QueryResult rs = PersistenceHelper.manager.navigate(per, "approval", AppPerLink.class, false);
	if (rs.hasMoreElements()) {
		AsmApproval app = (AsmApproval) rs.nextElement();
		per = app;
	}
}
%>


<%!
private void getWFItem(Persistable per) throws Exception {
	WFIte
}

WFItem item = null;
String oid = CommonUtil.getOIDString(wtobject);
long oidLong = 0;
String versionStr = " ";
try {
    if (wtobject instanceof LifeCycleManaged) {
        if (wtobject instanceof RevisionControlled) {
            RevisionControlled rc = (RevisionControlled) wtobject;
            versionStr = rc.getVersionIdentifier().getValue();
            oid = CommonUtil.getOIDString((Master) rc.getMaster());
            oidLong = CommonUtil.getOIDLongValue((Master) rc.getMaster());
        }
        else {
            oidLong = CommonUtil.getOIDLongValue(wtobject);
        }
    }

    Class target = WFItem.class;
    QuerySpec query = new QuerySpec();
    int idx = query.appendClassList(target, true);
    query.appendWhere(new SearchCondition(target, "wfObjectReference.key.classname", "=", oid.substring(0, oid.lastIndexOf(":"))),
                      new int[] { idx });
    query.appendAnd();
    query.appendWhere(new SearchCondition(target, "wfObjectReference.key.id", "=", oidLong), new int[] { idx });
    query.appendAnd();
    if (!" ".equals(versionStr)) query.appendWhere(new SearchCondition(target, "objectVersion", "=", versionStr), new int[] { idx });
    else query.appendWhere(new SearchCondition(target, "objectVersion", true), new int[] { idx });
    
    QueryResult qr = PersistenceHelper.manager.find(query);
    while (qr.hasMoreElements())
    {
        Object[] obj = (Object[]) qr.nextElement();
        WFItem element = (WFItem) obj[0];
        item = element;
    }
    Logger.user.println("> WFItemHelper.manager.getWFItem : query = " + query);
    //System.out.println("> WFItemHelper.manager.getWFItem : query = " + query);
}
catch (Exception e)
{
    e.printStackTrace();
}

return item;


%>