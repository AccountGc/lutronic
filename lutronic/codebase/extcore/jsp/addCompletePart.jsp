<%@page import="wt.pom.Transaction"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="com.e3ps.change.EOCompletePartLink"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String ecoOid = "com.e3ps.change.EChangeOrder:192256335";
	String partOid = "wt.part.WTPart:195665208";
	/*
	"wt.part.WTPart:192935487",
	"wt.part.WTPart:195664000",
	"wt.part.WTPart:195665236",
	"wt.part.WTPart:195665293",
	"wt.part.WTPart:195664332",
	"wt.part.WTPart:195665321",
	"wt.part.WTPart:195665208"
	*/
	
	EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(ecoOid);
	WTPart part = (WTPart) CommonUtil.getObject(partOid);
	
	String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
	String state = part.getState().toString();
	//A 면서 작업중인 것은 제외 한다.
	Transaction trx = new Transaction();
	try{
		
		trx.start();
		
		if(version.equals("A") && "INWORK".equals(state)){
			out.println("품목이 A버전일 때, 작업중인 품목은 완제품에 추가되지 않습니다. " + part.getNumber() +"<br/>");
		}else{
			EOCompletePartLink link = EOCompletePartLink.newEOCompletePartLink((WTPartMaster)part.getMaster(), eco);
			link.setVersion(version);
			
			PersistenceHelper.manager.save(link);
			
			out.println("완제품 목록 추가 완료 !!! " + eco.getEoName() + "  / " + eco.getEoNumber() + " // " + part.getNumber() + "  / " + part.getName() + "<br/>");
		}
		
		trx.commit();
	    trx = null;
	    
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		if(trx!=null){
			trx.rollback();
	   }
	}

%>