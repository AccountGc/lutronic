package com.e3ps.common.mail;

import java.util.HashMap;
import java.util.Hashtable;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.util.StringUtil;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.workspace.AsmApproval;

import wt.doc.WTDocument;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;

public class MailUtils {

	public static final MailUtils manager = new MailUtils();

	protected MailUtils() {

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
	public void sendWorkDataMail(LifeCycleManaged lcm, String workName, String loc) throws Exception {
		Hashtable<String, Object> hash = new Hashtable<>();
		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();

		String targetName = getTargetName(lcm);
		String subject = targetName + "의 " + workName + "요청 알림 메일입니다.";

		HashMap<String, String> to = new HashMap<>();
		WTUser toUser = (WTUser) SessionHelper.manager.getPrincipal();
		if (!StringUtil.checkString(toUser.getEMail())) {
			throw new Exception("받는 사람 = " + toUser.getFullName() + " 이메일 주소가 없습니다.");
		}

		to.put(toUser.getEMail(), toUser.getFullName());

		Hashtable<String, String> data = setParseData(lcm);
		data.put("workName", workName);
		data.put("Location", loc);

		MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
		String mcontent = mhct.htmlContent(data, "mail_notice.html");

		hash.put("FROM", fromUser);
		hash.put("TO", to);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		sendMail(hash);
	}

	/**
	 * 메일 전달용 데이터
	 */
	private Hashtable<String, String> setParseData(LifeCycleManaged lcm) throws Exception {
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
	private String getTargetName(LifeCycleManaged lcm) {
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
		}
		return target;
	}

	/**
	 * 메일 전송
	 */
	public void sendMail(Hashtable<String, Object> hash) throws Exception {

		HashMap<String, String> to = (HashMap<String, String>) hash.get("TO");
		String subject = (String) hash.get("SUBJECT");
		String content = (String) hash.get("CONTENT");

		SendMail mail = new SendMail();
		mail.setFromMailAddress("pdm-admin@lutronic.com", "PDM ADMIN");

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
}