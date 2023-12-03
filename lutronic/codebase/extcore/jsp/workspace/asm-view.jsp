<%@page import="com.e3ps.workspace.dto.AsmDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
AsmDTO dto = (AsmDTO) request.getAttribute("dto");
%>
꾸며라..