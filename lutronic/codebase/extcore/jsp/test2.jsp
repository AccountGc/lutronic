<%@page import="wt.admin.AdministrativeDomain"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.org.People"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

People p = People.newPeople();
p.setName("김준호");
p.setUser(user);
PersistenceHelper.manager.save(p);
out.println("성공!");
%>