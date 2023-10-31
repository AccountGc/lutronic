package com.e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.workflow.work.WorkItem;

import com.e3ps.admin.dto.MailUserDTO;
import com.e3ps.common.mail.MailUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.MailUser;
import com.e3ps.org.MailWTobjectLink;

public class StandardMailUserService extends StandardManager implements MailUserService {

	public static StandardMailUserService newStandardMailUserService() throws Exception {
		final StandardMailUserService instance = new StandardMailUserService();
		instance.initialize();
		return instance;
	}

	/**
	 * 외부메일 유저 검색
	 * 
	 * @param map
	 * @return
	 */
	@Override
	public QuerySpec getQuery(HashMap map) {
		QuerySpec query = null;
		try {
			String name = StringUtil.checkNull((String) map.get("name"));
			String email = StringUtil.checkNull((String) map.get("email"));
			String command = StringUtil.checkNull((String) map.get("command"));
			String isDisable = StringUtil.checkNull((String) map.get("isDisable"));
			query = new QuerySpec(MailUser.class);
			// System.out.println("command="+command);
			// System.out.println("name="+name);
			if (command.equals("search")) {
				if (email.length() > 0) {
					if (query.getConditionCount() > 0)
						query.appendAnd();
					query.appendWhere(new SearchCondition(MailUser.class, MailUser.EMAIL, SearchCondition.LIKE,
							"%" + email.toUpperCase() + "%", true), new int[] { 0 });
				}

				if (name.length() > 0) {
					if (query.getConditionCount() > 0)
						query.appendAnd();
					query.appendWhere(new SearchCondition(MailUser.class, MailUser.NAME, SearchCondition.LIKE,
							"%" + name.toUpperCase() + "%"), new int[] { 0 });
				}
			}

			if (isDisable.equals("true")) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(MailUser.class, MailUser.IS_DISABLE, SearchCondition.IS_TRUE),
						new int[] { 0 });
			}
			query.appendOrderBy(new OrderBy(new ClassAttribute(MailUser.class, MailUser.NAME), false), new int[] { 0 });

			// System.out.println(query);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return query;

	}

	/**
	 * 외부 메일 유저 등록
	 * 
	 * @param map
	 * @return
	 */
	@Override
	public Map<String, String> createMailUser(HashMap map) {

		// String msg ="";
		Map<String, String> msg = null;
		try {
			String name = StringUtil.checkNull((String) map.get("name"));
			String email = StringUtil.checkNull((String) map.get("email"));
			String enable = StringUtil.checkNull((String) map.get("enable"));

			boolean isDisable = enable.equals("true") ? true : false;

			MailUser user = MailUser.newMailUser();
			user.setName(name);
			user.setEmail(email);
			user.setIsDisable(isDisable);
			PersistenceHelper.manager.save(user);
			msg = new HashMap<String, String>();
			msg.put("msg", Message.get("정상적으로 등록되었습니다."));
		} catch (Exception e) {
			msg = new HashMap<String, String>();
			msg.put("msg", Message.get("등록시 오류가 발생 했습니다."));
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * 외부메일 유저 수정
	 * 
	 * @param map
	 * @return
	 */
	@Override
	public Map<String, String> modifyMailUser(HashMap map) {

		// String msg ="";
		Map<String, String> msg = null;
		try {
			String oid = StringUtil.checkNull((String) map.get("oid"));
			String name = StringUtil.checkNull((String) map.get("name"));
			String email = StringUtil.checkNull((String) map.get("email"));
			String enable = StringUtil.checkNull((String) map.get("enable"));

			boolean isDisable = enable.equals("true") ? true : false;
			System.out.println("oid-> " + oid);
			System.out.println("name-> " + name);
			System.out.println("email-> " + email);
			System.out.println("enable-> " + enable);

			MailUser user = (MailUser) CommonUtil.getObject(oid);
			user.setName(name);
			user.setEmail(email);
			user.setIsDisable(isDisable);
			PersistenceHelper.manager.save(user);
			msg = new HashMap<String, String>();
			msg.put("msg", Message.get("정상적으로 수정 되었습니다."));
			System.out.println("msg-> " + msg);
		} catch (Exception e) {
			msg = new HashMap<String, String>();
			msg.put("msg", Message.get("수정시 오류가 발생 했습니다."));
			e.printStackTrace();
		}

		return msg;

	}

	/**
	 * 외부메일 유저 삭제
	 * 
	 * @param oid
	 * @return
	 */
	@Override
	public Map<String, String> deleteMailUser(String oid) {
		// String msg ="";
		Map<String, String> msg = null;
		try {
			MailUser user = (MailUser) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(user);
			msg = new HashMap<String, String>();
			msg.put("msg", Message.get("정상적으로 삭제 되었습니다."));
		} catch (Exception e) {
			msg = new HashMap<String, String>();
			msg.put("msg", Message.get("삭제시 오류가 발생 했습니다."));
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * WTObject 와 mailUser Link 생성
	 * 
	 * @param map
	 * @return
	 */
	@Override
	public Vector<MailWTobjectLink> createMailUserLink(HashMap map) throws Exception {

		Vector<MailWTobjectLink> vec = new Vector<MailWTobjectLink>();
		String workOid = StringUtil.checkNull((String) map.get("workOid"));
		String userOid = StringUtil.checkNull((String) map.get("userOid"));

		WorkItem workItem = (WorkItem) CommonUtil.getObject(workOid);
		WTObject obj = (WTObject) workItem.getPrimaryBusinessObject().getObject();

		String[] userOids = userOid.split(",");

		/* 기존 외부 유저 중복 체크 */
		Vector<MailWTobjectLink> veclink = getMailUserLinkList(CommonUtil.getOIDString(obj));
		Vector vecUser = new Vector();
		for (int i = 0; i < veclink.size(); i++) {
			MailWTobjectLink link = veclink.get(i);
			vecUser.add(CommonUtil.getOIDString(link.getUser()));
		}

		for (int i = 0; i < userOids.length; i++) {

			MailUser user = (MailUser) CommonUtil.getObject(userOids[i]);
			if (vecUser.contains(userOids[i]))
				continue;
			MailWTobjectLink link = MailWTobjectLink.newMailWTobjectLink(obj, user);
			PersistenceHelper.manager.save(link);
			vec.add(link);

		}

		return vec;
	}

	@Override
	public boolean deleteMailUserLink(HashMap map) throws Exception {
		boolean isDelte = false;
		String linkOid = StringUtil.checkNull((String) map.get("linkOid"));

		String[] linkOids = linkOid.split(",");

		for (int i = 0; i < linkOids.length; i++) {
			String _linkOid = linkOids[i].trim();
			MailWTobjectLink link = (MailWTobjectLink) CommonUtil.getObject(_linkOid);
			PersistenceHelper.manager.delete(link);
		}

		isDelte = true;

		return isDelte;
	}

	/**
	 * 결재 객체 별로 외부 유저 메일
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public Vector<MailWTobjectLink> getMailUserLinkList(String oid) {

		Vector<MailWTobjectLink> veclink = new Vector<MailWTobjectLink>();
		try {

			WTObject objTemp = null;
			WTObject obj = (WTObject) CommonUtil.getObject(oid);

			if (obj instanceof WorkItem) {
				WorkItem workItem = (WorkItem) obj;
				objTemp = (WTObject) workItem.getPrimaryBusinessObject().getObject();

			} else {
				objTemp = obj;
			}

			QueryResult rt = PersistenceHelper.navigate(objTemp, "user", MailWTobjectLink.class, false);

			while (rt.hasMoreElements()) {
				MailWTobjectLink link = (MailWTobjectLink) rt.nextElement();
				veclink.add(link);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return veclink;
	}

	/**
	 * 외부 유저 메일 전송
	 * 
	 * @param wtobject
	 */
	@Override
	public void sendWTObjectMailUser(WTObject wtobject) {
		// System.out.println("[MailUserHelp.sendWTObjectMailUser : WTObject ="
		// +wtobject);
		Vector<MailWTobjectLink> vecLink = getMailUserLinkList(CommonUtil.getOIDString(wtobject));
		HashMap mailUserMap = new HashMap();
		for (int i = 0; i < vecLink.size(); i++) {
			MailWTobjectLink link = vecLink.get(i);

			String userName = link.getUser().getName();
			String userMail = link.getUser().getEmail();

			mailUserMap.put(userMail, userName);
		}
		// System.out.println("[MailUserHelp.sendWTObjectMailUser : mailUserMap.size()
		// =" +mailUserMap.size());
		if (mailUserMap.size() > 0) {
			MailUtil.manager.sendeOutSideMail(wtobject, mailUserMap);
		}

	}

	@Override
	public void adminMailSave(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addList = (ArrayList<Map<String, Object>>) params.get("addRow");
		ArrayList<Map<String, Object>> editList = (ArrayList<Map<String, Object>>) params.get("editRow");
		ArrayList<Map<String, Object>> removeList = (ArrayList<Map<String, Object>>) params.get("removeRow");
		Transaction trx = new Transaction();
		try {
			trx.start();

			// 추가

			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	@Override
	public void mailSave(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRow = (ArrayList<Map<String, Object>>) params.get("addRow");
		ArrayList<Map<String, Object>> editRow = (ArrayList<Map<String, Object>>) params.get("editRow");
		ArrayList<Map<String, Object>> removeRow = (ArrayList<Map<String, Object>>) params.get("removeRow");
		Transaction trs = new Transaction();
		try {
			trs.start();

			if(addRow != null) {
				for (Map<String, Object> map : addRow) {
					String name = (String) map.get("name");
					String email = (String) map.get("email");
					boolean enable = (boolean) map.get("enable");
					
					MailUser mail = MailUser.newMailUser();
					mail.setName(name);
					mail.setEmail(email);
					mail.setIsDisable(enable);
					PersistenceHelper.manager.save(mail);
				}
			}

			if(editRow != null) {
				for (Map<String, Object> map : editRow) {
					String oid = (String) map.get("oid");
					String name = (String) map.get("name");
					String email = (String) map.get("email");
					boolean enable = (boolean) map.get("enable");
					MailUser mail = (MailUser) CommonUtil.getObject(oid);
					mail.setName(name);
					mail.setEmail(email);
					mail.setIsDisable(enable);
					PersistenceHelper.manager.modify(mail);
				}
			}

			// 삭제
			if(removeRow != null) {
				for (Map<String, Object> map : removeRow) {
					String oid = (String) map.get("oid");
					MailUser mail = (MailUser) CommonUtil.getObject(oid);
					PersistenceHelper.manager.delete(mail);
				}				
			}

			trs.commit();
			trs = null;
		} catch (

		Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void saveLink(Persistable per, ArrayList<Map<String, String>> params) throws Exception {

		for (Map<String, String> map : params) {
			String s = map.get("oid");
			MailUser user = (MailUser) CommonUtil.getObject(s);
			MailWTobjectLink link = MailWTobjectLink.newMailWTobjectLink((WTObject) per, user);
			PersistenceHelper.manager.save(link);
		}
	}

	@Override
	public void deleteLink(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ArrayList<MailWTobjectLink> list = MailUserHelper.manager.navigate(oid);
			for (MailWTobjectLink link : list) {
				PersistenceHelper.manager.delete(link);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
