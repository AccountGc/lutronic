<%@page import="com.e3ps.common.iba.AttributeKey.ECOKey"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.change.service.ECOSearchHelper"%>
<%@page import="com.e3ps.change.EOCompletePartLink"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.change.beans.ECOData"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
String oid = "com.e3ps.change.EChangeOrder:192511518";
EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
ECOData ecoData = new ECOData(eco);
List<Map<String,Object>> list2 = ecoData.getCompletePartList();

List<EOCompletePartLink> list= ECOSearchHelper.service.getCompletePartLink(eco);
//( (이 CHANGE (ECO) 자동으로 생성,승인됨이 아닌것 , 작성자  ) || 관리자 ) && ECO
String eoType = eco.getEoType();
String state = eco.getLifeCycleState().toString();
String userName = eco.getCreator().getName();
String sessionName = SessionHelper.getPrincipal().getName();

boolean isECO = eoType.equals(ECOKey.ECO_CHANGE);
boolean isCreate = userName.equals(sessionName);
boolean isstate = !state.equals("APPROVED") ;
boolean isDelete = ((isCreate && isstate) || CommonUtil.isAdmin()) && isECO;

%>