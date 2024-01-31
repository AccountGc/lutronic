<%@page import="wt.lifecycle.LifeCycleTemplate"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String oid = "com.e3ps.change.EChangeOrder:208027737";
	EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
	
	
	LifeCycleManaged lcm = (LifeCycleManaged) eco;

	LifeCycleTemplate lct = (LifeCycleTemplate)lcm.getLifeCycleTemplate().getObject();
	if(!lct.isLatestIteration()) {
		System.out.println("아님...");
	}
	
	LifeCycleHelper.service.reassign(lcm, LifeCycleHelper.service
			.getLifeCycleTemplateReference(lcm.getLifeCycleName(), WCUtil.getWTContainerRef())); // Lifecycle
// 	LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) lcm, State.toState("LINE_REGISTER"));
			System.out.println("상태값 변경..");
%>