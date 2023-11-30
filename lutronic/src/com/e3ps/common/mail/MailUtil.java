/**
 * @(#) MailUtil.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2005. 3. 3..
 *	@author Cho Sung Ok, jerred@e3ps.com
 *	@desc	
 */

package com.e3ps.common.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.StreamData;
import wt.content.Streamed;
import wt.doc.WTDocument;
import wt.fc.LobLocator;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleManaged;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTProperties;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.jdf.config.ConfigEx;
import com.e3ps.common.jdf.config.ConfigExImpl;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.workprocess.service.WorklistHelper;
import com.e3ps.org.dto.PeopleDTO;

public class MailUtil {
	public static final MailUtil manager = new MailUtil();
	static final boolean VERBOSE = ConfigImpl.getInstance().getBoolean("develop.verbose", false);
	static final boolean enableMail = ConfigImpl.getInstance().getBoolean("e3ps.mail.enable", true);

	protected MailUtil() {
	}

	/**
	 * host, sendId, sendPass 설정하기...
	 * 
	 * @param hash
	 * @return
	 */
	public static boolean sendMail2(Hashtable hash) throws Exception {

		ConfigExImpl conf = ConfigEx.getInstance("eSolution");
		String host = conf.getString("mail.smtp.host");

		if (enableMail) {
			HashMap to = (HashMap) hash.get("TO");
			HashMap from = (HashMap) hash.get("FROM");
			String subject = (String) hash.get("SUBJECT");
			String content = (String) hash.get("CONTENT");
			Vector attache = (Vector) hash.get("ATTACHE");

			try {
				SendMail mail = new SendMail();
				// WTUser from = (WTUser)SessionHelper.manager.getPrincipal();
				// System.out.println("Sender : " + from.getFullName() + "," + from.getEMail());
				mail.setFromMailAddress((String) from.get("EMAIL"), (String) from.get("NAME"));

				if (to != null && to.size() > 0) {
					Object[] objArr = to.keySet().toArray();
					String emails = "";
					String toname = "";
					for (int i = 0; i < objArr.length; i++) {
						emails = (String) objArr[i];
						toname = (String) to.get(emails);
//						System.out.println("To Mail :" + emails);
//						System.out.println("To name :" + toname);

						if (emails.indexOf("@") < 0)
							continue;

						mail.setToMailAddress(emails, toname);
					}

				} else {
					throw new MailException("받는 사람 설정오류");
				}

				mail.setSubject(subject);

				String message = " Text 메일 메시지 내용 ";
				String htmlMessage = "<html><font color='red'> HTML 메일 메시지 내용</font></html>";
				// String[] fileNames = { "c:/attachFile1.zip","c:/attachFile2.txt" } ;
				String[] fileNames = {};

				if (content != null) {
					mail.setHtmlAndFile(content, fileNames);
				} else {
					mail.setHtmlAndFile(htmlMessage, fileNames);
				}
				// mail.setHtml(htmlMessage);
				// mail.setText(message);

				/**
				 * @Todo 개인 서버에서 주석처리함.
				 */
				mail.send(); // 메일 전송

				return true;
			} catch (Exception e) {
				throw e;
				// return false;
			}
		} else {
			return false;
		}
	}

	public boolean sendMail(Hashtable hash) {

		ConfigExImpl conf = ConfigEx.getInstance("eSolution");

		if (enableMail) {
			HashMap to = (HashMap) hash.get("TO");
			String subject = (String) hash.get("SUBJECT");
			String content = (String) hash.get("CONTENT");
			Vector attache = (Vector) hash.get("ATTACHE");

			try {

				String mailTo = conf.getString("email.admin.mailTo");
				String mailToName = conf.getString("email.admin.name");

				System.out.println(mailTo + "==" + mailToName);

				SendMail mail = new SendMail();
				// WTUser from = (WTUser)SessionHelper.manager.getPrincipal();

				mail.setFromMailAddress(mailTo, mailToName);
				// mail.setHtmlAndFile("배포파일", fileName);
				if (to != null && to.size() > 0) {
					Object[] objArr = to.keySet().toArray();
					String emails = "";
					String toname = "";
					String[] emailsArray = new String[objArr.length];
					String[] tonameArray = new String[objArr.length];
					for (int i = 0; i < objArr.length; i++) {
						emails = (String) objArr[i];
						toname = (String) to.get(emails);
						// System.out.println("To Mail :" + emails);
						// System.out.println("To name :" + toname);

						if (emails == null || emails.indexOf("@") < 0)
							continue;
						emailsArray[i] = emails;
						tonameArray[i] = toname;
						// mail.setToMailAddress(emails, toname);
					}

					mail.setToMailAddress(emailsArray, tonameArray);
				} else {
					throw new MailException("받는 사람 설정오류");
				}

				mail.setSubject(subject);

				String message = " Text 메일 메시지 내용 ";
				String htmlMessage = "<html><font color='red'> HTML 메일 메시지 내용</font></html>";
				// String[] fileNames = { "c:/attachFile1.zip","c:/attachFile2.txt" } ;
				String[] fileNames = {};
				if (attache != null) {
					fileNames = new String[attache.size()];
					for (int i = 0; i < attache.size(); i++) {
						fileNames[i] = (String) attache.get(i);

					}
				}

				if (content != null) {
					mail.setHtmlAndFile(content, fileNames);
				} else {
					mail.setHtmlAndFile(htmlMessage, fileNames);
				}

				// mail.setHtml(htmlMessage);
				// mail.setText(message);

				/**
				 * @Todo 개인 서버에서 주석처리함.
				 */
				mail.send(); // 메일 전송

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	private File getFile(ContentHolder contentholder, ApplicationData applicationdata) throws Exception {
		// ContentHolder contentholder =
		// applicationdata.getHolderLink().getContentHolder();
		Streamed streamed = (Streamed) PersistenceHelper.manager.refresh(applicationdata.getStreamData().getObjectId());
		LobLocator loblocator = null;
		if (streamed instanceof StreamData) {
			applicationdata = (ApplicationData) PersistenceHelper.manager.refresh(applicationdata);
			streamed = (Streamed) PersistenceHelper.manager.refresh(applicationdata.getStreamData().getObjectId());
			try {
				loblocator.setObjectIdentifier(((ObjectReference) streamed).getObjectId());
				((StreamData) streamed).setLobLoc(loblocator);
			} catch (Exception exception) {
			}
		}

		String tempDir = System.getProperty("java.io.tmpdir");
		InputStream in = streamed.retrieveStream();
		File attachfile = new File(tempDir + File.separator + applicationdata.getFileName()); // 파일 저장 위치
		FileOutputStream fileOut = new FileOutputStream(attachfile);
		byte[] buffer = new byte[1024];
		int c;
		while ((c = in.read(buffer)) != -1)
			fileOut.write(buffer, 0, c);
		fileOut.close();

		return attachfile;
	}

	public static void sendSimpleMail(String subject, String content, String url, String[] toId) {

		try {

			ConfigExImpl conf = ConfigEx.getInstance("eSolution");

			if (!enableMail)
				return;

//			System.out.println("@@ call sendMailFtp()  title = " + subject);

			HashMap fromHash = new HashMap();
			fromHash.put("EMAIL", "plmadmin@pemtron.com");
			fromHash.put("NAME", "PDM 관리자");

			HashMap toHash = new HashMap();
			for (int i = 0; i < toId.length; i++) {
				WTUser toUser = OrganizationServicesHelper.manager.getAuthenticatedUser(toId[i]);
				if (toUser.getEMail() == null)
					continue;
				toHash.put(toUser.getEMail(), toUser.getFullName());
			}

			Hashtable hash = new Hashtable();
			hash.put("SUBJECT", subject);
			hash.put("CONTENT", content);
			if (url != null) {
				url = WTProperties.getServerCodebase().toString() + url;
				url = URLEncoder.encode(url);
				hash.put("URL", url);
			}

			MailHtmlContentTemplate template = MailHtmlContentTemplate.getInstance();
			content = template.htmlContent(hash, "CommonMail.html");

			Hashtable mailHash = new Hashtable();
			mailHash.put("FROM", fromHash);
			mailHash.put("TO", toHash);
			mailHash.put("SUBJECT", subject);
			mailHash.put("CONTENT", content);
			EMailScheduler.createProcessItem(mailHash);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getHtmlTemplate(String mailType, String url, String subject, String author, String title,
			String startDate, String creator) throws Exception {
		return getHtmlTemplate(mailType, url, subject, author, title, startDate, creator, false);
	}

	public static String getHtmlTemplate(String mailType, String url, String subject, String author, String title,
			String startDate, String creator, boolean work) throws Exception {

		MailHtmlContentTemplate template = MailHtmlContentTemplate.getInstance();

		StringBuffer content = new StringBuffer();

		String approve = "결재가";
		String approve2 = "결재";

		if (work) {
			approve = "작업이";
			approve2 = "작업";
		}

		if (mailType.equals("pressingApproval")) {
			content.append(approve + " 지연되고 있습니다.");
			content.append(" <BR> " + approve2 + " 요청에 대한 빠른 처리 바랍니다.");
		} else if (mailType.equals("requestApproval")) {
			content.append(approve + " 요청되었습니다.");
			content.append(" <BR> " + approve2 + " 요청에 대한 처리 바랍니다.");
		} else if (mailType.equals("create")) {
			content.append(approve + " 등록되었습니다.");
			content.append(" <BR> " + approve2 + "에 대한 확인 바랍니다.");
		} else {
			content.append(approve + " 완료 되었습니다.");
			content.append(" <BR> " + approve2 + " 완료에 대한 확인 바랍니다.");
		}

		Hashtable hash = new Hashtable();

		hash.put("SUBJECT", subject);
		hash.put("TO", author);
		hash.put("CONTENT", content.toString());
		hash.put("TITLE", title);
		hash.put("STARTDATE", startDate);
		hash.put("CREATOR", creator);
		if (url != null) {
			url = URLEncoder.encode(url);
			hash.put("URL", url);
		}

		return template.htmlContent(hash, "ApprovalMail.html");
	}

	/**
	 * 작업공간함 발생시 메일 발송
	 * 
	 * @param item
	 * @return
	 */
	public boolean taskNoticeMail(WorkItem item) {
		HashMap toPerson = null;
		wt.org.WTUser adminUser = null;
		String mcontent = "";
		String subject = "";
		try {

			if (!enableMail)
				return false;

			item.getSource().getObject();

			LifeCycleManaged pbo = (LifeCycleManaged) item.getPrimaryBusinessObject().getObject();
			WfActivity activity = (WfActivity) item.getSource().getObject();
			WfAssignedActivityTemplate waf = (WfAssignedActivityTemplate) activity.getTemplateReference().getObject();
			WTPrincipalReference wp = item.getOwnership().getOwner();
			wt.org.WTPrincipal principal = wp.getPrincipal();

			String[] processTarget = WorklistHelper.service.getWorkItemName((WTObject) pbo);
			String viewString = processTarget[1] + " (" + processTarget[2] + ")";

			String workName = activity.getName();
			WfProcess p = (WfProcess) activity.getParentProcessRef().getObject();
			System.out.println(" p.getCreator =============" + p.getCreator().getFullName());

			// System.out.println(item.getPersistInfo().getObjectIdentifier());
			if ("결재".equals(workName)) {
				Vector userList = (Vector) WorklistHelper.service.getWfProcessVariableValue(p, "userList");
				if (userList != null)
					for (int x = userList.size() - 1; x > -1; x--) {
						Hashtable hash = (Hashtable) userList.get(x);
						if (hash.containsValue((wt.org.WTUser) principal))
							workName = (String) hash.get("type");
					}
			}
			String state = pbo.getLifeCycleState().getDisplay(Message.getLocale());

			String deadlineStr = "";
			Timestamp deadline = activity.getDeadline();
			if (deadline != null) {
				if (deadline.after(new java.util.Date()))
					deadlineStr = ("" + deadline).substring(0, 10);
				else
					deadlineStr = ("" + deadline).substring(0, 10);
			} else {
				deadlineStr = "";
			}

			// subject...
			if (null != processTarget[0] && processTarget[0].length() > 0)
				subject = processTarget[0] + "의 " + workName + "요청 알림 메일입니다.";
			else
				subject = processTarget[1] + "의 " + workName + "요청 알림 메일입니다.";
			String description = "";
			String creatorName = "";
			if (pbo instanceof WTPart) {
				WTPart part = (WTPart) pbo;
				PeopleDTO creator = new PeopleDTO(part.getCreator());
				creatorName = StringUtil.checkNull(creator.name);
				description = "";
			} else if (pbo instanceof ECOChange) {
				ECOChange eo = (ECOChange) pbo;
				PeopleDTO creator = new PeopleDTO(eo.getCreator());
				creatorName = StringUtil.checkNull(creator.name);
				description = StringUtil.checkNull(eo.getEoCommentA());
				workName = "수신";
			} else if (pbo instanceof WTDocument) {
				WTDocument doc = (WTDocument) pbo;
				PeopleDTO creator = new PeopleDTO(doc.getCreator());
				creatorName = StringUtil.checkNull(creator.name);
				description = StringUtil.checkNull(doc.getDescription());
			}

			// html content...
			Hashtable chash = new Hashtable();
			if (null != processTarget[0] && processTarget[0].length() > 0)
				chash.put("gubun", processTarget[0]);
			else
				chash.put("gubun", processTarget[1]);
			chash.put("viewString", viewString);
			chash.put("workName", workName);
			chash.put("deadlineStr", deadlineStr);
			chash.put("creatorName", creatorName);
			chash.put("description", description);

			MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
			mcontent = mhct.htmlContent(chash, "mail_notice.html");

			// 받는 사람...
			toPerson = new HashMap();
			String toEmail = wp.getEMail();
			if (toEmail == null || toEmail.length() == 0) {

				// toEmail = "wslee@e3ps.com"; //TEST

				/*
				 * Config conf = ConfigImpl.getInstance(); String company =
				 * conf.getString("company").trim(); if ("auto".equals(company)) toEmail =
				 * wp.getName() + "@" + "i-jy.com"; else toEmail = wp.getName() + "@" +
				 * "i-jy.com";
				 */
			}
			// System.out.println("email addr : " + toEmail);
			String toFullName = wp.getFullName();
			toPerson.put(toEmail, toFullName);

			adminUser = (wt.org.WTUser) wt.session.SessionHelper.manager.getAdministrator();
			// System.out.println("admin-->> " + adminUser.getFullName() + " : " +
			// adminUser.getEMail());

		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable hash = new Hashtable();
		hash.put("FROM", adminUser);
		hash.put("TO", toPerson);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		return sendMail(hash);
	}

	/**
	 * 외부메일 유저
	 * 
	 * @param item
	 * @param mailUserMap
	 */
	public void sendeOutSideMail(WTObject obj, HashMap mailUserMap) {
		// System.out.println("[MailUtil.manager.sendeOutSideMail : obj =" +obj);
		// System.out.println("[MailUtil.manager.sendeOutSideMail : mailUserMap ="
		// +mailUserMap.size());
		// System.out.println("[MailUtil.manager.sendeOutSideMail : enableMail ="
		// +enableMail);
		wt.org.WTUser adminUser = null;
		String mcontent = "";
		String subject = "";
		String workName = "";
		String viewString = "";
		String creatorName = "";
		String description = "";
		try {

			if (!enableMail)
				return;

			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				workName = "문서";
				viewString = "[" + doc.getNumber() + "] " + doc.getName();
				subject = "[" + doc.getNumber() + "] " + doc.getName() + " 의 문서가 승인 되었습니다.";
				PeopleDTO creator = new PeopleDTO(doc.getCreator());
				creatorName = StringUtil.checkNull(creator.name);
				description = StringUtil.checkNull(doc.getDescription());
			} else if (obj instanceof EChangeRequest || obj instanceof EChangeOrder) {
				ECOChange eo = (ECOChange) obj;
				workName = "설변";
				viewString = "[" + eo.getEoNumber() + "] " + eo.getEoName();
				subject = "[" + eo.getEoNumber() + "] " + eo.getEoName() + " 의 설변이 완료 되었습니다.";
				PeopleDTO creator = new PeopleDTO(eo.getCreator());
				creatorName = StringUtil.checkNull(creator.name);
				description = StringUtil.checkNull(eo.getEoCommentA());
			} else {
				return;
			}

			// subject...
			// subject = processTarget[0] + "의 " + workName + " 알림 메일입니다.";

			// html content...
			Hashtable chash = new Hashtable();
			chash.put("viewString", viewString);
			chash.put("workName", workName);
			chash.put("creatorName", creatorName);
			chash.put("description", description);

			MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
			mcontent = mhct.htmlContent(chash, "outside_mail_notice.html");
			adminUser = (wt.org.WTUser) wt.session.SessionHelper.manager.getAdministrator();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable hash = new Hashtable();
		hash.put("FROM", adminUser);
		hash.put("TO", mailUserMap);
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", mcontent);

		boolean isSend = sendMail(hash);
		// System.out.println("sendeOutSideMail isSend="+isSend);
	}

}
