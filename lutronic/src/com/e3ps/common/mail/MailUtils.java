package com.e3ps.common.mail;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.jdf.config.ConfigEx;
import com.e3ps.common.jdf.config.ConfigExImpl;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.workspace.AsmApproval;

import wt.doc.WTDocument;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class MailUtils {

	public static final MailUtils manager = new MailUtils();

	protected MailUtils() {

	}

	/**
	 * 결재선 지정 메일
	 */
	public void sendWorkDataMail(LifeCycleManaged lcm, String workName) throws Exception {
		Hashtable<String, Object> hash = new Hashtable<>();
		WTUser fromUser = (WTUser) SessionHelper.manager.getAdministrator();

		String targetName = getTargetName(lcm);
		String subject = targetName + "의 " + workName + "요청 알림 메일입니다.";

		HashMap<String, String> to = new HashMap<>();
		WTUser toUser = (WTUser) SessionHelper.manager.getPrincipal();
		to.put(toUser.getEMail(), toUser.getFullName());

		Hashtable<String, String> data = setParseData(lcm);
		data.put("workName", workName);
		data.put("gubun", workName);

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
		if (lcm instanceof WTPart) {
			WTPart part = (WTPart) lcm;
			creatorName = part.getCreatorFullName();
			description = "";
		} else if (lcm instanceof ECOChange) {
			ECOChange e = (ECOChange) lcm;
			creatorName = e.getCreatorFullName();
			description = e.getEoCommentA();
		} else if (lcm instanceof WTDocument) {
			WTDocument doc = (WTDocument) lcm;
			creatorName = doc.getCreatorFullName();
			description = doc.getDescription();
		}
		hash.put("creatorName", creatorName);
		hash.put("description", description);
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
		mail.setHtmlAndFile(content, new String[] {});
		mail.send(); // 메일 전송
	}
}