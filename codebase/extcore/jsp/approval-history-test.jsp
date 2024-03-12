<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.e3ps.workspace.AsmApproval"%>
<%@page import="com.e3ps.workspace.AppPerLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.Persistable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String url = "jdbc:oracle:thin:@localhost:1521:WIND";
String username = "dbadmin";
String password = "dbadmin";

// JDBC 연결 변수
Connection connection = null;

try {
	// 오라클 JDBC 드라이버 로드
	Class.forName("oracle.jdbc.driver.OracleDriver");

	// 데이터베이스에 연결
	connection = DriverManager.getConnection(url, username, password);

	if (connection != null) {
		System.out.println("Oracle 데이터베이스에 성공적으로 연결되었습니다.");
		// 여기에 추가적인 작업을 수행할 수 있습니다.
	} else {
		System.out.println("Oracle 데이터베이스에 연결하는데 실패했습니다.");
	}
} catch (ClassNotFoundException e) {
	System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
	e.printStackTrace();
} catch (SQLException e) {
	System.out.println("데이터베이스 연결 중 오류가 발생했습니다.");
	e.printStackTrace();
} finally {
	// 연결 해제
	if (connection != null) {
		try {
	connection.close();
		} catch (SQLException e) {
	e.printStackTrace();
		}
	}
}
%>


