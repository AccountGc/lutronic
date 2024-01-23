<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="com.e3ps.download.DownloadHistory"%>
<%@page import="com.e3ps.org.MailUser"%>
<%@page import="com.e3ps.org.MailWTobjectLink"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="com.e3ps.change.EcrmToDocumentLink"%>
<%@page import="com.e3ps.doc.DocumentToDocumentLink"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="wt.folder.FolderEntry"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="com.e3ps.common.iba.IBAUtil"%>
<%@page import="com.e3ps.change.ECRMRequest"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.folder.IteratedFolderMemberLink"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="com.e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.folder.Folder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
delete();

String loc = "/Default/문서/03. 2023년(이전문서)/01.공유폴더/06.설계변경 위험관리";
Folder ff = FolderTaskLogic.getFolder(loc, WCUtil.getWTContainerRef());

QuerySpec query = new QuerySpec();
int idx = query.appendClassList(WTDocument.class, true);
int idx_m = query.appendClassList(WTDocumentMaster.class, false);

query.setAdvancedQueryEnabled(true);
query.setDescendantQuery(false);

QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
		WTAttributeNameIfc.ID_NAME, idx, idx_m);

query.appendAnd();

int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
SearchCondition fsc = new SearchCondition(fca, "=", new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
fsc.setOuterJoin(0);
query.appendWhere(fsc, new int[] { f_idx, idx });
query.appendAnd();
long fid = ff.getPersistInfo().getObjectIdentifier().getId();
query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
		new int[] { f_idx });

QuerySpecUtils.toLatest(query, idx, WTDocument.class);
QueryResult result = PersistenceHelper.manager.find(query);
out.println("=" + result.size());
int i = 0;
while (result.hasMoreElements()) {
	Object[] obj = (Object[]) result.nextElement();
	WTDocument doc = (WTDocument) obj[0];

	ECRMRequest ecrm = ECRMRequest.newECRMRequest();
	ecrm.setEoName(doc.getName());
	ecrm.setEoNumber(doc.getNumber());
	ecrm.setModel(IBAUtil.getStringValue(doc, "MODEL"));
	ecrm.setIsNew(false);
	ecrm.setEoCommentA(doc.getDescription());
	ecrm.setPeriod("PR004");

	String deptcode_code = IBAUtil.getStringValue(doc, "DEPTCODE");
	String deptcode_name = keyToValue(deptcode_code, "DEPTCODE");
	ecrm.setCreateDepart(deptcode_name);

	String writer = IBAUtil.getStringValue(doc, "DSGN");
	ecrm.setWriter(writer); // 작성자
	ecrm.setCreateDate(doc.getCreateTimestamp().toString().substring(0, 10));

	String state = doc.getLifeCycleState().toString();

	if ("APPROVED".equals(state)) {
		ecrm.setApproveDate(doc.getModifyTimestamp().toString().substring(0, 10));
	}

	String location = "/Default/설계변경/ECRM";
	String lifecycle = "LC_Default";

	Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
	FolderHelper.assignLocation((FolderEntry) ecrm, folder);
	// 문서 lifeCycle 설정
	LifeCycleHelper.setLifeCycle(ecrm,
	LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
	ecrm = (ECRMRequest) PersistenceHelper.manager.save(ecrm);

	ecrm = (ECRMRequest) PersistenceHelper.manager.refresh(ecrm);

	LifeCycleHelper.service.setLifeCycleState(ecrm, State.toState(state));

	saveLink(doc, ecrm);

	String temp = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "mig";
	File f = new File(temp);
	if (!f.exists()) {
		f.mkdirs();
	}

	QueryResult rs = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
	while (rs.hasMoreElements()) {
		ApplicationData data = (ApplicationData) rs.nextElement();
		byte[] buffer = new byte[10240];
		InputStream is = ContentServerHelper.service.findLocalContentStream(data);
		String name = data.getFileName().replaceAll("->", "").replaceAll("\"", " ");
		File file = new File(temp + File.separator + name);
		FileOutputStream fos = new FileOutputStream(file);
		int j = 0;
		while ((j = is.read(buffer, 0, 10240)) > 0) {
	fos.write(buffer, 0, j);
		}
		fos.close();
		is.close();

		ApplicationData applicationData = ApplicationData.newApplicationData(ecrm);
		applicationData.setRole(ContentRoleType.PRIMARY);
		PersistenceHelper.manager.save(applicationData);
		ContentServerHelper.service.updateContent(ecrm, applicationData, file.getPath());
	}

	rs.reset();
	rs = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
	while (rs.hasMoreElements()) {
		ApplicationData data = (ApplicationData) rs.nextElement();
		byte[] buffer = new byte[10240];
		InputStream is = ContentServerHelper.service.findLocalContentStream(data);
		String name = data.getFileName().replaceAll("->", "").replaceAll("\"", " ");
		File file = new File(temp + File.separator + name);
		FileOutputStream fos = new FileOutputStream(file);
		int j = 0;
		while ((j = is.read(buffer, 0, 10240)) > 0) {
	fos.write(buffer, 0, j);
		}
		fos.close();
		is.close();

		ApplicationData applicationData = ApplicationData.newApplicationData(ecrm);
		applicationData.setRole(ContentRoleType.SECONDARY);
		PersistenceHelper.manager.save(applicationData);
		ContentServerHelper.service.updateContent(ecrm, applicationData, file.getPath());
	}
	System.out.println("i = " + i + " 번째 ECRM 등록 완료!");
	i++;
}
out.println("종료");
%>

<%!private void saveLink(WTDocument doc, ECRMRequest ecrm) throws Exception {
		// 관련 문서
		QueryResult qr = PersistenceHelper.manager.navigate(doc, "useBy", DocumentToDocumentLink.class);
		while (qr.hasMoreElements()) {
			WTDocument ref = (WTDocument) qr.nextElement();
			EcrmToDocumentLink link = EcrmToDocumentLink.newEcrmToDocumentLink(ecrm, ref);
			PersistenceHelper.manager.save(link);
		}

		qr.reset();
		qr = PersistenceHelper.manager.navigate((WTObject) doc, "user", MailWTobjectLink.class);
		while (qr.hasMoreElements()) {
			MailUser user = (MailUser) qr.nextElement();
			MailWTobjectLink link = MailWTobjectLink.newMailWTobjectLink((WTObject) ecrm, user);
			PersistenceHelper.manager.save(link);
		}

		QuerySpec qs = new QuerySpec();
		int i = qs.appendClassList(DownloadHistory.class, true);
		QuerySpecUtils.toEquals(qs, i, DownloadHistory.class, "persistReference.key.id", doc);
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			Object[] oo = (Object[]) rs.nextElement();
			DownloadHistory d = (DownloadHistory) oo[0];

			DownloadHistory history = DownloadHistory.newDownloadHistory();
			history.setName(d.getName());
			history.setPersist(ecrm);
			history.setCnt(1);
			history.setUser(d.getUser());
			PersistenceHelper.manager.save(history);
		}

	}%>


<%!private String keyToValue(String code, String codeType) throws Exception {
		String value = "";
		NumberCode n = NumberCodeHelper.manager.getNumberCode(code, codeType);
		if (n != null) {
			value = n.getName();
		} else {
			value = code;
		}
		return value;
	}%>

<%!private void delete() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ECRMRequest.class, true);
		SearchCondition sc = new SearchCondition(ECRMRequest.class, ECRMRequest.EO_NUMBER, SearchCondition.NOT_LIKE,
				"%ECRM%");
		query.appendWhere(sc, new int[] { idx });

		QueryResult rs = PersistenceHelper.manager.find(query);
		System.out.println("rs=" + rs.size());
		int k = 0;
		while (rs.hasMoreElements()) {
			Object[] obj = (Object[]) rs.nextElement();
			ECRMRequest ecrm = (ECRMRequest) obj[0];

			QueryResult qr = PersistenceHelper.manager.navigate(ecrm, "doc", EcrmToDocumentLink.class, false);
			while (qr.hasMoreElements()) {
				EcrmToDocumentLink link = (EcrmToDocumentLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			qr.reset();
			qr = PersistenceHelper.manager.navigate((WTObject) ecrm, "user", MailWTobjectLink.class, false);
			while (qr.hasMoreElements()) {
				MailWTobjectLink link = (MailWTobjectLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			QuerySpec qs = new QuerySpec();
			int i = qs.appendClassList(DownloadHistory.class, true);
			QuerySpecUtils.toEquals(qs, i, DownloadHistory.class, "persistReference.key.id", ecrm);
			QueryResult result = PersistenceHelper.manager.find(qs);
			while (result.hasMoreElements()) {
				Object[] oo = (Object[]) result.nextElement();
				DownloadHistory h = (DownloadHistory) oo[0];
				PersistenceHelper.manager.delete(h);
			}

			PersistenceHelper.manager.delete(ecrm);
			System.out.println("k = " + k + "번째 ECRM 삭제");
			k++;
		}

	}%>