package com.e3ps.common.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.e3ps.change.ECOChange;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.MailUser;
import com.e3ps.org.MailWTobjectLink;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.AsmApproval;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.session.SessionHelper;

public class MailUtils {

	public static final MailUtils manager = new MailUtils();

	private static final String processQueueName = "SendMailProcessQueue";
	private static final String className = "com.e3ps.common.mail.MailUtils";
	private static final String sendWorkDataMailMethod = "sendWorkDataMail"; // 결재선 지정 메일
	private static final String sendApprovalMailMethod = "sendApprovalMail"; // 결재 메일
	private static final String sendAgreeMailMethod = "sendAgreeMail"; // 합의 메일
	private static final String sendReceiveMailMethod = "sendReceiveMail"; // 수신 메일
	private static final String sendExternalMailMethod = "sendExternalMail"; // 외부메일
	private static final String sendSubmitterMailMethod = "sendSubmitterMail"; // 기안자 메일

	private MailUtils() {

	}

	public void sendSAPErrorMail(LifeCycleManaged lcm, String targetName, String errorMsg) throws Exception {
		Hashtable<String, Object> hash = new Hashtable<>();
		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();

		// EO 전송의,ECO 전송의, ECN 전송의
		String subject = targetName + "의 에러 알림 메일입니다.";

		HashMap<String, String> to = new HashMap<>();
		WTUser toUser = (WTUser) SessionHelper.manager.getPrincipal();
		if (!StringUtil.checkString(toUser.getEMail())) {
			throw new Exception("받는 사람 = " + toUser.getFullName() + " 이메일 주소가 없습니다.");
		}

		// 받는 사람 최종 결재자로??
		to.put(toUser.getEMail(), toUser.getFullName());

		String gubun = "";
		String creatorName = "";
		String loc = "";
		String description = "";
		String number = "";
		if (lcm instanceof EChangeOrder) {
			EChangeOrder e = (EChangeOrder) lcm;
			if (e.getEoType().equals("CHANGE")) {
				gubun = "ECO SAP 전송";
				loc = "ECO";
			} else {
				gubun = "EO SAP 전송";
				loc = "EO";
			}
			number = e.getEoNumber();
			description = e.getEoCommentA();
			creatorName = e.getCreatorFullName();
		}

		Hashtable<String, String> data = new Hashtable<>();
		data.put("gubun", gubun);
		data.put("creatorName", creatorName);
		data.put("workName", "에러");
		data.put("Location", loc);
		data.put("number", number);
		data.put("errorMsg", errorMsg);
		data.put("description", description != null ? description : "");

		MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
		String mcontent = mhct.htmlContent(data, "sap_notice.html");

		hash.put("FROM", fromUser);
		hash.put("TO", to);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		sendMail(hash);
	}

	/**
	 * 결재선 지정 메일
	 */
	public static void sendWorkDataMail(Hashtable<String, String> h) throws Exception {
		System.out.println("결재선 지정 메일 호출!!!");

		String oid = h.get("oid");
		LifeCycleManaged lcm = (LifeCycleManaged) CommonUtil.getObject(oid);

		Hashtable<String, Object> hash = new Hashtable<>();
		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();

		String targetName = getTargetName(lcm);
		WTUser toUser = getToUser(lcm);
		String subject = targetName + "의 결재선 지정요청 알림 메일입니다.";

		System.out.println("전송자 = " + toUser.getEMail());

		HashMap<String, String> to = new HashMap<>();
//		WTUser toUser = (WTUser) SessionHelper.manager.getPrincipal();
		if (!StringUtil.checkString(toUser.getEMail())) {
			throw new Exception("받는 사람 = " + toUser.getFullName() + " 이메일 주소가 없습니다.");
		}

		to.put(toUser.getEMail(), toUser.getFullName());

		Hashtable<String, String> data = setParseData(lcm);
		data.put("workName", "결재선지정");
		data.put("Location", "결재선지정");

		MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
		String mcontent = mhct.htmlContent(data, "mail_notice.html");

		hash.put("FROM", fromUser);
		hash.put("TO", to);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		sendMail(hash);
		System.out.println("결재선 지정 메일 종료!!");
	}

	/**
	 * 메일 받을 사람
	 */
	private static WTUser getToUser(LifeCycleManaged lcm) throws Exception {
		WTUser toUser = null;
		if (lcm instanceof WTDocument) {
			WTDocument d = (WTDocument) lcm;
			toUser = (WTUser) d.getCreator().getPrincipal();
		} else if (lcm instanceof EChangeOrder) {
			EChangeOrder e = (EChangeOrder) lcm;
			toUser = (WTUser) e.getCreator().getPrincipal();
		} else if (lcm instanceof AsmApproval) {
			AsmApproval asm = (AsmApproval) lcm;
			toUser = (WTUser) asm.getCreator().getPrincipal();
		} else if (lcm instanceof ECPRRequest) {
			ECPRRequest ecpr = (ECPRRequest) lcm;
			toUser = (WTUser) ecpr.getCreator().getPrincipal();
		} else if (lcm instanceof ECRMRequest) {
			ECRMRequest ecrm = (ECRMRequest) lcm;
			toUser = (WTUser) ecrm.getCreator().getPrincipal();
		} else if (lcm instanceof EChangeRequest) {
			EChangeRequest e = (EChangeRequest) lcm;
			toUser = (WTUser) e.getCreator().getPrincipal();
		}
		return toUser;
	}

	/**
	 * 메일 전달용 데이터
	 */
	private static Hashtable<String, String> setParseData(LifeCycleManaged lcm) throws Exception {
		Hashtable<String, String> hash = new Hashtable<>();

		String description = "";
		String creatorName = "";
		String type = "";
		String viewString = "";
		if (lcm instanceof WTPart) {
			WTPart part = (WTPart) lcm;
			creatorName = part.getCreatorFullName();
			description = "";
			type = "부품";
			viewString = part.getNumber() + "-[" + part.getName() + "]";
		} else if (lcm instanceof ECPRRequest) {
			ECPRRequest ecpr = (ECPRRequest) lcm;
			creatorName = ecpr.getCreatorFullName();
			description = ecpr.getContents();
			type = "ECPR";
			viewString = ecpr.getEoNumber() + "-[" + ecpr.getEoName() + "]";
		} else if (lcm instanceof ECRMRequest) {
			ECRMRequest ecrm = (ECRMRequest) lcm;
			creatorName = ecrm.getCreatorFullName();
			description = ecrm.getContents();
			type = "ECRM";
			viewString = ecrm.getEoNumber() + "-[" + ecrm.getEoName() + "]";
		} else if (lcm instanceof ECOChange) {
			ECOChange e = (ECOChange) lcm;
			creatorName = e.getCreatorFullName();
			description = e.getEoCommentA();
			if ("CHANGE".equals(e.getEoType())) {
				type = "ECO";
			} else {
				type = "EO";
			}
			viewString = e.getEoNumber() + "-[" + e.getEoName() + "]";
		} else if (lcm instanceof WTDocument) {
			WTDocument doc = (WTDocument) lcm;
			creatorName = doc.getCreatorFullName();
			description = doc.getDescription();
			if (doc.getDocType().toString().equals("$$MMDocument")) {
				type = "금형문서";
			} else {
				type = "문서";
			}
			viewString = doc.getNumber() + "-[" + doc.getName() + "]";
		} else if (lcm instanceof ROHSMaterial) {
			ROHSMaterial rohs = (ROHSMaterial) lcm;
			creatorName = rohs.getCreatorFullName();
			description = rohs.getDescription();
			type = "RoHS";
			viewString = rohs.getNumber() + "-[" + rohs.getName() + "]";
		} else if (lcm instanceof AsmApproval) {
			AsmApproval asm = (AsmApproval) lcm;
			creatorName = asm.getCreatorFullName();
			description = asm.getDescription();
			type = "일괄결재";
			viewString = asm.getNumber() + "-[" + asm.getName() + "]";
		} else if (lcm instanceof EChangeNotice) {
			EChangeNotice ecn = (EChangeNotice) lcm;
			creatorName = ecn.getCreatorFullName();
			description = ecn.getEoCommentA();
			type = "ECN";
			viewString = ecn.getEoNumber() + "-[" + ecn.getEoName() + "]";
		}
		hash.put("viewString", viewString);
		hash.put("gubun", type);
		hash.put("creatorName", creatorName);

		if (StringUtil.checkString(description)) {
			hash.put("description", description);
		} else {
			hash.put("description", "");
		}

		return hash;
	}

	/**
	 * 메일중 대산 객체 이름 반환
	 */
	private static String getTargetName(LifeCycleManaged lcm) {
		String target = "";
		if (lcm instanceof WTDocument) {
			WTDocument d = (WTDocument) lcm;
			String t = d.getDocType().toString();
			if ("$$MMDocument".equals(t)) {
				target = "금형";
			} else {
				target = "문서";
			}
		} else if (lcm instanceof ROHSMaterial) {
			target = "RoHS";
		} else if (lcm instanceof EChangeRequest) {
			target = "CR";
		} else if (lcm instanceof EChangeOrder) {
			EChangeOrder e = (EChangeOrder) lcm;
			if (e.getEoType().trim().equals("CHANGE")) {
				target = "ECO";
			} else {
				target = "EO";
			}
		} else if (lcm instanceof AsmApproval) {
			target = "일괄결재";
		} else if (lcm instanceof ECPRRequest) {
			target = "ECPR";
		} else if (lcm instanceof ECRMRequest) {
			target = "ECRM";
		}
		return target;
	}

	/**
	 * 메일 전송
	 */
	private static void sendMail(Hashtable<String, Object> hash) throws Exception {

		HashMap<String, String> to = (HashMap<String, String>) hash.get("TO");
		String subject = (String) hash.get("SUBJECT");
		String content = (String) hash.get("CONTENT");

		SendMail mail = new SendMail();
		mail.setFromMailAddress("kcplm@pro-packer.com", "PDM ADMIN");

		Object[] objArr = to.keySet().toArray();
		String emails = "";
		String toname = "";
		String[] emailsArray = new String[objArr.length];
		String[] tonameArray = new String[objArr.length];
		for (int i = 0; i < objArr.length; i++) {
			emails = (String) objArr[i];
			toname = (String) to.get(emails);
			emailsArray[i] = emails;
			tonameArray[i] = toname;
		}

		mail.setToMailAddress(emailsArray, tonameArray);
		mail.setSubject(subject);
//		mail.setText(content, "UTF-8");
		mail.setHtml(content);
//		mail.setHtmlAndFile(content, new String[] {});
		mail.send(); // 메일 전송
	}

	/**
	 * 수신 메일 전송
	 */
	public static void sendReceiveMail(Hashtable<String, String> h) throws Exception {
		System.out.println("수신 요청 메일 호출!!");
		String oid = h.get("oid");
		Persistable per = CommonUtil.getObject(oid);

		ApprovalLine receiveLine = (ApprovalLine) CommonUtil.getObject(h.get("line"));

		Hashtable<String, Object> hash = new Hashtable<>();
		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();

		String targetName = getTargetName((LifeCycleManaged) per);
		String subject = targetName + "의 수신 요청 알림 메일입니다.";

		HashMap<String, String> to = new HashMap<>();
//			WTUser toUser = (WTUser) SessionHelper.manager.getPrincipal();
		WTUser toUser = (WTUser) receiveLine.getOwnership().getOwner().getPrincipal();
		if (!StringUtil.checkString(toUser.getEMail())) {
			throw new Exception("받는 사람 = " + toUser.getFullName() + " 이메일 주소가 없습니다.");
		}

		to.put(toUser.getEMail(), toUser.getFullName());

		Hashtable<String, String> data = setParseData((LifeCycleManaged) per);
		data.put("workName", "수신");
		data.put("Location", "수신함");

		MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
		String mcontent = mhct.htmlContent(data, "mail_notice.html");

		hash.put("FROM", fromUser);
		hash.put("TO", to);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		sendMail(hash);
		System.out.println("수신 요청 메일 종료!!");
	}

	/**
	 * 합의 메일 전송
	 */
	public static void sendAgreeMail(Hashtable<String, String> h) throws Exception {
		System.out.println("합의 요청 메일 호출!!");
		String oid = h.get("oid");
		Persistable per = CommonUtil.getObject(oid);

		ApprovalLine agreeLine = (ApprovalLine) CommonUtil.getObject(h.get("line"));

		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();
		Hashtable<String, Object> hash = new Hashtable<>();

		String targetName = getTargetName((LifeCycleManaged) per);
		String subject = targetName + "의 합의 요청 알림 메일입니다.";

		HashMap<String, String> to = new HashMap<>();
		WTUser toUser = (WTUser) agreeLine.getOwnership().getOwner().getPrincipal();
		if (!StringUtil.checkString(toUser.getEMail())) {
			throw new Exception("받는 사람 = " + toUser.getFullName() + " 이메일 주소가 없습니다.");
		}

		to.put(toUser.getEMail(), toUser.getFullName());

		Hashtable<String, String> data = setParseData((LifeCycleManaged) per);
		data.put("workName", "합의");
		data.put("Location", "합의함");

		MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
		String mcontent = mhct.htmlContent(data, "mail_notice.html");

		hash.put("FROM", fromUser);
		hash.put("TO", to);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		sendMail(hash);
		System.out.println("합의 요청 메일 종료!!");
	}

	/**
	 * 합의 메일 전송 테스트
	 */
	public void sendAgreeMailTest(Persistable per, ArrayList<WTUser> ll) throws Exception {
		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();

		int i = 0;
		for (WTUser toUser : ll) {
//		for (ApprovalLine agreeLine : ll) {

			System.out.println("메일 전송 테스트...! = " + i);

			Hashtable<String, Object> hash = new Hashtable<>();

			String targetName = getTargetName((LifeCycleManaged) per);
			String subject = targetName + "의 합의 요청 알림 메일입니다.";

			HashMap<String, String> to = new HashMap<>();
//			WTUser toUser = (WTUser) agreeLine.getOwnership().getOwner().getPrincipal();
			if (!StringUtil.checkString(toUser.getEMail())) {
				throw new Exception("받는 사람 = " + toUser.getFullName() + " 이메일 주소가 없습니다.");
			}

			to.put(toUser.getEMail(), toUser.getFullName());

			Hashtable<String, String> data = setParseData((LifeCycleManaged) per);
			data.put("workName", "수신");
			data.put("Location", "수신함");

			MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
			String mcontent = mhct.htmlContent(data, "mail_notice.html");

			hash.put("FROM", fromUser);
			hash.put("TO", to);
			hash.put("SUBJECT", subject);
			hash.put("CONTENT", mcontent);

			sendMail(hash);
			i++;
		}
	}

	/**
	 * ECN 담당자 지정 요청 메일
	 */
	public void sendEcnWorkUser(EChangeOrder eco) throws Exception {
		Hashtable<String, Object> hash = new Hashtable<>();
		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();
		String subject = "ECN 담당자 지정 요청 알림 메일입니다.";

		HashMap<String, String> to = new HashMap<>();
//		WTUser toUser = (WTUser) SessionHelper.manager.getPrincipal();
		WTUser toUser = OrganizationServicesHelper.manager.getAuthenticatedUser("cdpark");
		if (!StringUtil.checkString(toUser.getEMail())) {
			throw new Exception("받는 사람 = " + toUser.getFullName() + " 이메일 주소가 없습니다.");
		}

		to.put(toUser.getEMail(), toUser.getFullName());

		Hashtable<String, String> data = new Hashtable<>();
		data.put("viewString", "ECN 담당자 지정");
		data.put("gubun", "ECN");
		data.put("creatorName", eco.getCreatorFullName()); // eco 등록자로 한다.
		data.put("description", eco.getEoNumber() + " SAP 전송이 완료되었습니다.<br>담당자를 지정해주세요.");
		data.put("workName", "ECN 담당자 지정");
		data.put("Location", "ECN");

		MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
		String mcontent = mhct.htmlContent(data, "ecn_notice.html");

		hash.put("FROM", fromUser);
		hash.put("TO", to);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		sendMail(hash);
	}

	/**
	 * 외부 메일
	 */
	public static void sendExternalMail(Hashtable<String, String> h) throws Exception {
		System.out.println("외부 메일 호출!!");
		String oid = h.get("oid");
		Persistable per = CommonUtil.getObject(oid);

		QueryResult qr = PersistenceHelper.manager.navigate(per, "user", MailWTobjectLink.class);
		while (qr.hasMoreElements()) {
			MailUser mailUser = (MailUser) qr.nextElement();

			WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();
			Hashtable<String, Object> hash = new Hashtable<>();

			String targetName = getTargetName((LifeCycleManaged) per);
			String subject = targetName + "의 수신전용(외부메일용) 메일입니다.";

			HashMap<String, String> to = new HashMap<>();
//		WTUser toUser = (WTUser) approvalLine.getOwnership().getOwner().getPrincipal();
			if (!StringUtil.checkString(mailUser.getEmail())) {
				throw new Exception("받는 사람 = " + mailUser.getName() + " 이메일 주소가 없습니다.");
			}

			to.put(mailUser.getEmail(), mailUser.getName());

			Hashtable<String, String> data = setParseData((LifeCycleManaged) per);
//		data.put("workName", "결재");
//		data.put("Location", "결재함");

			MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
			String mcontent = mhct.htmlContent(data, "mail_notice.html");

			hash.put("FROM", fromUser);
			hash.put("TO", to);
			hash.put("SUBJECT", subject);
			hash.put("CONTENT", mcontent);

			sendMail(hash);
		}
		System.out.println("외부 메일 종료!!");
	}

	/**
	 * 외부 메일
	 */
	public static void sendSubmitterMail(Hashtable<String, String> h) throws Exception {
		System.out.println("기안자 메일 호출!!");
		String oid = h.get("oid");
		String line = h.get("line");
		ApprovalLine submit = (ApprovalLine) CommonUtil.getObject(line);
		Persistable per = CommonUtil.getObject(oid);

		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();
		Hashtable<String, Object> hash = new Hashtable<>();

		String targetName = getTargetName((LifeCycleManaged) per);
		String subject = targetName + "의 기안자전용 메일입니다.";

		WTUser toUser = (WTUser) submit.getOwnership().getOwner().getPrincipal();
		HashMap<String, String> to = new HashMap<>();
		if (!StringUtil.checkString(toUser.getEMail())) {
			throw new Exception("받는 사람 = " + toUser.getName() + " 이메일 주소가 없습니다.");
		}

		to.put(toUser.getEMail(), toUser.getName());

		Hashtable<String, String> data = setParseData((LifeCycleManaged) per);

		MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
		String mcontent = mhct.htmlContent(data, "mail_notice.html");

		hash.put("FROM", fromUser);
		hash.put("TO", to);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		sendMail(hash);
		System.out.println("기안자 메일 종료!!");
	}

	/**
	 * 결재 요청 메일
	 */
	public static void sendApprovalMail(Hashtable<String, String> h) throws Exception {
		System.out.println("결재 요청 메일 호출!!");
		String oid = h.get("oid");
		Persistable per = CommonUtil.getObject(oid);

		ApprovalLine approvalLine = (ApprovalLine) CommonUtil.getObject(h.get("line"));

		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();
		Hashtable<String, Object> hash = new Hashtable<>();

		String targetName = getTargetName((LifeCycleManaged) per);
		String subject = targetName + "의 결재 요청 알림 메일입니다.";

		HashMap<String, String> to = new HashMap<>();
		WTUser toUser = (WTUser) approvalLine.getOwnership().getOwner().getPrincipal();
		if (!StringUtil.checkString(toUser.getEMail())) {
			throw new Exception("받는 사람 = " + toUser.getFullName() + " 이메일 주소가 없습니다.");
		}

		to.put(toUser.getEMail(), toUser.getFullName());

		Hashtable<String, String> data = setParseData((LifeCycleManaged) per);
		data.put("workName", "결재");
		data.put("Location", "결재함");

		MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
		String mcontent = mhct.htmlContent(data, "mail_notice.html");

		hash.put("FROM", fromUser);
		hash.put("TO", to);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		sendMail(hash);
		System.out.println("결재 요청 메일 종료!!");
	}

	/**
	 * 결재선 지정 메일 백그라운 호출
	 */
	public void sendWorkDataMailMethod(Persistable per) throws Exception {

		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", per.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, sendWorkDataMailMethod, className, argClasses, argObjects);
	}

	/**
	 * 결재 메일 백그라운드 호출
	 */
	public void sendApprovalMailMethod(Persistable per, ApprovalLine approvalLine) throws Exception {

		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", per.getPersistInfo().getObjectIdentifier().getStringValue());
		hash.put("line", approvalLine.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, sendApprovalMailMethod, className, argClasses, argObjects);
	}

	/**
	 * 합의 메일 백그라운드 호출
	 */
	public void sendAgreeMailMethod(Persistable per, ApprovalLine agreeLine) throws Exception {

		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", per.getPersistInfo().getObjectIdentifier().getStringValue());
		hash.put("line", agreeLine.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, sendAgreeMailMethod, className, argClasses, argObjects);
	}

	/**
	 * 수신 메일 백그라운드 호출
	 */
	public void sendReceiveMailMethod(Persistable per, ApprovalLine receiveLine) throws Exception {

		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", per.getPersistInfo().getObjectIdentifier().getStringValue());
		hash.put("line", receiveLine.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, sendReceiveMailMethod, className, argClasses, argObjects);
	}

	/**
	 * 외부 메일 백그라운드 호출
	 */
	public void sendExternalMailMethod(Persistable per) throws Exception {

		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", per.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, sendExternalMailMethod, className, argClasses, argObjects);
	}

	/**
	 * 기안자 메일 전송
	 */
	public void sendSubmitterMailMethod(Persistable per, ApprovalLine submit) throws Exception {
		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", per.getPersistInfo().getObjectIdentifier().getStringValue());
		hash.put("line", submit.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, sendSubmitterMailMethod, className, argClasses, argObjects);
	}
}