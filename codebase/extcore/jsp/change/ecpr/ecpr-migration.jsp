<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.vc.VersionControlServerHelper"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="com.e3ps.download.DownloadHistory"%>
<%@page import="com.e3ps.org.MailUser"%>
<%@page import="com.e3ps.org.MailWTobjectLink"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.InputStream"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="com.e3ps.change.EcprToDocumentLink"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.e3ps.change.CrToEcprLink"%>
<%@page import="com.e3ps.change.EcrToEcrLink"%>
<%@page import="com.e3ps.change.EcoToEcprLink"%>
<%@page import="com.e3ps.change.RequestOrderLink"%>
<%@page import="com.e3ps.change.CrToDocumentLink"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="wt.folder.FolderEntry"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.lifecycle.LifeCycleTemplate"%>
<%@page import="com.e3ps.change.ECPRRequest"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="com.e3ps.change.EChangeRequest"%>
<%@page import="wt.query.QuerySpec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
delete();

QuerySpec query = new QuerySpec();
int idx = query.appendClassList(EChangeRequest.class, true);
SearchCondition sc = new SearchCondition(EChangeRequest.class, EChangeRequest.EO_NUMBER, "LIKE", "%ECPR%");
query.appendWhere(sc, new int[] { idx });
QueryResult qr = PersistenceHelper.manager.find(query);
int i = 0;
System.out.println("전체 개수 = " + qr.size());
while (qr.hasMoreElements()) {
	Object[] obj = (Object[]) qr.nextElement();
	EChangeRequest e = (EChangeRequest) obj[0];
	System.out.println(e.getEoNumber() + "<br>");

	ECPRRequest ecpr = ECPRRequest.newECPRRequest();
	ecpr.setEoNumber(e.getEoNumber());
	ecpr.setEoName(e.getEoName());
	ecpr.setCreateDepart(e.getCreateDepart());
	ecpr.setCreateDate(e.getCreateDate());
	ecpr.setWriter(e.getWriter());
	ecpr.setChangeSection(e.getChangeSection());
	ecpr.setModel(e.getModel());
	ecpr.setContents(e.getContents());
	ecpr.setEoCommentA(e.getEoCommentA());
	ecpr.setEoCommentB(e.getEoCommentB());
	ecpr.setEoCommentC(e.getEoCommentC());
	ecpr.setEoCommentD(e.getEoCommentD());
	ecpr.setEoCommentE(e.getEoCommentE());
	ecpr.setApproveDate(e.getApproveDate());
	ecpr.setEoApproveDate(e.getEoApproveDate());
	ecpr.setEoType(e.getEoType());
	ecpr.setOwnership(e.getOwnership());
	ecpr.setPeriod("PR004");
	ecpr.setIsNew(false);
	String location = "/Default/설계변경/ECPR";
	String lifecycle = "LC_Default";

	Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
	FolderHelper.assignLocation((FolderEntry) ecpr, folder);
	// 문서 lifeCycle 설정
	LifeCycleHelper.setLifeCycle(ecpr,
	LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
	ecpr = (ECPRRequest) PersistenceHelper.manager.save(ecpr);

	PersistenceHelper.manager.save(ecpr);
	ecpr = (ECPRRequest) PersistenceHelper.manager.refresh(ecpr);

	LifeCycleHelper.service.setLifeCycleState(ecpr, State.toState(e.getLifeCycleState().toString()));

	saveLink(e, ecpr);

	// 첨부 파일

	String temp = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "mig";
	File f = new File(temp);
	if (!f.exists()) {
		f.mkdirs();
	}

	QueryResult rs = ContentHelper.service.getContentsByRole(e, ContentRoleType.toContentRoleType("ECR"));
	while (rs.hasMoreElements()) {
		ApplicationData data = (ApplicationData) rs.nextElement();
		byte[] buffer = new byte[10240];
		InputStream is = ContentServerHelper.service.findLocalContentStream(data);
		String name = data.getFileName().replaceAll("->", "").replaceAll("\"", " ");
		System.out.println("name=" + name);
		File file = new File(temp + File.separator + name);
		FileOutputStream fos = new FileOutputStream(file);
		int j = 0;
		while ((j = is.read(buffer, 0, 10240)) > 0) {
	fos.write(buffer, 0, j);
		}
		fos.close();
		is.close();

		ApplicationData applicationData = ApplicationData.newApplicationData(ecpr);
		applicationData.setRole(ContentRoleType.PRIMARY);
		PersistenceHelper.manager.save(applicationData);
		ContentServerHelper.service.updateContent(ecpr, applicationData, file.getPath());
	}

	rs.reset();
	rs = ContentHelper.service.getContentsByRole(e, ContentRoleType.SECONDARY);
	while (rs.hasMoreElements()) {
		ApplicationData data = (ApplicationData) rs.nextElement();
		byte[] buffer = new byte[10240];
		InputStream is = ContentServerHelper.service.findLocalContentStream(data);
		String name = data.getFileName().replaceAll("->", "").replaceAll("\"", " ");
		System.out.println("name1=" + name);
		File file = new File(temp + File.separator + name);
		FileOutputStream fos = new FileOutputStream(file);
		int j = 0;
		while ((j = is.read(buffer, 0, 10240)) > 0) {
	fos.write(buffer, 0, j);
		}
		fos.close();
		is.close();

		ApplicationData applicationData = ApplicationData.newApplicationData(ecpr);
		applicationData.setRole(ContentRoleType.SECONDARY);
		PersistenceHelper.manager.save(applicationData);
		ContentServerHelper.service.updateContent(ecpr, applicationData, file.getPath());
	}

	update(e, ecpr);

	System.out.println("i=" + i + "번쨰 등록 완료");
	i++;
}
%>

<%!private void update(EChangeRequest e, ECPRRequest ecpr) throws Exception {
		Connection con = null;
		Statement st = null;
		try {

			con = DriverManager.getConnection("jdbc:oracle:thin:@//pdmdb:1521/wind", "dbadmin", "dbadmin");
			st = con.createStatement();

			long ids = e.getCreator().getObject().getPersistInfo().getObjectIdentifier().getId();

			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ECPRREQUEST");
			sb.append(" SET CREATESTAMPA2='" + e.getCreateTimestamp().toString().substring(0, 10) + "'");
			sb.append(", CLASSNAMEKEYA7='wt.org.WTUser'");
			sb.append(", IDA3A7=" + ids + "");

			st.executeUpdate(sb.toString());
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			st.close();
			con.close();
		}
	}%>


<%!private void saveLink(EChangeRequest e, ECPRRequest ecpr) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(e, "eco", RequestOrderLink.class);
		while (qr.hasMoreElements()) {
			EChangeOrder eco = (EChangeOrder) qr.nextElement();
			EcoToEcprLink link = EcoToEcprLink.newEcoToEcprLink(eco, ecpr);
			PersistenceHelper.manager.save(link);
		}

		qr.reset();
		qr = PersistenceHelper.manager.navigate(e, "useBy", EcrToEcrLink.class);
		while (qr.hasMoreElements()) {
			EChangeRequest cr = (EChangeRequest) qr.nextElement();
			CrToEcprLink link = CrToEcprLink.newCrToEcprLink(cr, ecpr);
			PersistenceHelper.manager.save(link);
		}

		qr.reset();
		qr = PersistenceHelper.manager.navigate(e, "doc", CrToDocumentLink.class);
		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			EcprToDocumentLink link = EcprToDocumentLink.newEcprToDocumentLink(ecpr, doc);
			PersistenceHelper.manager.save(link);
		}

		qr.reset();
		qr = PersistenceHelper.manager.navigate((WTObject) e, "user", MailWTobjectLink.class);
		while (qr.hasMoreElements()) {
			MailUser user = (MailUser) qr.nextElement();
			MailWTobjectLink link = MailWTobjectLink.newMailWTobjectLink((WTObject) ecpr, user);
			PersistenceHelper.manager.delete(link);
		}

		QuerySpec qs = new QuerySpec();
		int i = qs.appendClassList(DownloadHistory.class, true);
		QuerySpecUtils.toEquals(qs, i, DownloadHistory.class, "persistReference.key.id", ecpr);
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			Object[] oo = (Object[]) rs.nextElement();
			DownloadHistory d = (DownloadHistory) oo[0];

			DownloadHistory history = DownloadHistory.newDownloadHistory();
			history.setName(d.getName());
			history.setPersist(ecpr);
			history.setCnt(1);
			history.setUser(d.getUser());
			PersistenceHelper.manager.save(history);

		}

	}%>

<%!private void delete() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ECPRRequest.class, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			ECPRRequest ecpr = (ECPRRequest) obj[0];

			QueryResult result = PersistenceHelper.manager.navigate(ecpr, "cr", CrToEcprLink.class, false);
			while (result.hasMoreElements()) {
				CrToEcprLink link = (CrToEcprLink) result.nextElement();
				PersistenceServerHelper.manager.remove(link);
			}

			result.reset();
			result = PersistenceHelper.manager.navigate(ecpr, "eco", EcoToEcprLink.class, false);
			while (result.hasMoreElements()) {
				EcoToEcprLink link = (EcoToEcprLink) result.nextElement();
				PersistenceServerHelper.manager.remove(link);
			}

			result.reset();
			result = PersistenceHelper.manager.navigate(ecpr, "doc", EcprToDocumentLink.class, false);
			while (result.hasMoreElements()) {
				EcprToDocumentLink link = (EcprToDocumentLink) result.nextElement();
				PersistenceServerHelper.manager.remove(link);
			}

			result.reset();
			result = PersistenceHelper.manager.navigate((WTObject) ecpr, "user", MailWTobjectLink.class, false);
			while (result.hasMoreElements()) {
				MailWTobjectLink link = (MailWTobjectLink) result.nextElement();
				PersistenceServerHelper.manager.remove(link);
			}

			QuerySpec qs = new QuerySpec();
			int i = qs.appendClassList(DownloadHistory.class, true);
			QuerySpecUtils.toEquals(qs, i, DownloadHistory.class, "persistReference.key.id", ecpr);
			QueryResult rs = PersistenceHelper.manager.find(qs);
			while (rs.hasMoreElements()) {
				Object[] oo = (Object[]) rs.nextElement();
				DownloadHistory d = (DownloadHistory) oo[0];
				PersistenceHelper.manager.delete(d);
			}

			PersistenceHelper.manager.delete(ecpr);
			System.out.println("삭제 = " + ecpr.getEoNumber());
		}
	}%>