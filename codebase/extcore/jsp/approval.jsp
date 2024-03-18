<%@page import="java.util.Base64"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.sql.Blob"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
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
String url = "jdbc:oracle:thin:@192.168.254.111:1521:WIND";
String username = "dbadmin";
String password = "dbadmin";

// JDBC 연결 변수
Connection connection = null;
Statement st = null;
ResultSet rs = null;
try {
	// 오라클 JDBC 드라이버 로드
	Class.forName("oracle.jdbc.driver.OracleDriver");

	// 데이터베이스에 연결
	connection = DriverManager.getConnection(url, username, password);
	st = connection.createStatement();

	String sql = "SELECT WFCOMMENT FROM WFITEMUSERLINK";

	rs = st.executeQuery(sql);
	while (rs.next()) {
		Blob blob = rs.getBlob(1);
		byte[] bytes = blob.getBytes(1, (int)blob.length());
		blob.free();
		
		String comment = Base64.getEncoder().encodeToString(bytes);
		out.println(comment);
// 		String ida2a2 = (String) rs.getString("ida2a2");
// 		String name = (String) rs.getString("name");
	}

} catch (ClassNotFoundException e) {
	System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
	e.printStackTrace();
} catch (SQLException e) {
	out.println("데이터베이스 연결 중 오류가 발생했습니다.");
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


