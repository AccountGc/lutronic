package com.e3ps.doc.access.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.access.FolderAccessWTGroupLink;
import com.e3ps.doc.access.FolderAccessWTUserLink;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.SubFolder;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;

public class AccessHelper {

	public static final AccessService service = ServiceFactory.getService(AccessService.class);
	public static final AccessHelper manager = new AccessHelper();

	/**
	 * 사용자 리스트
	 */
	public JSONArray getUserList(Folder f) throws Exception {
		ArrayList<String> containsList = getContainsUser(f);
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTUser.class, true);
		QuerySpecUtils.toBooleanAnd(query, idx, WTUser.class, WTUser.DISABLED, false);
		QuerySpecUtils.toBooleanAnd(query, idx, WTUser.class, WTUser.REPAIR_NEEDED, false);
		QuerySpecUtils.toOrderBy(query, idx, WTUser.class, WTUser.FULL_NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTUser user = (WTUser) obj[0];
			Map<String, String> map = new HashMap<>();
			String oid = user.getPersistInfo().getObjectIdentifier().getStringValue();
			if (containsList.contains(oid)) {
				continue;
			}
			map.put("oid", oid);
			map.put("name", user.getFullName());
			map.put("id", user.getName());
			map.put("type", "사용자");
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 그룹 리스트
	 */
	public JSONArray getGroupList(Folder f) throws Exception {
		ArrayList<String> containsList = getContainsGroup(f);
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTGroup.class, true);
		QuerySpecUtils.toOrderBy(query, idx, WTGroup.class, WTGroup.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTGroup group = (WTGroup) obj[0];
			Map<String, String> map = new HashMap<>();
			String oid = group.getPersistInfo().getObjectIdentifier().getStringValue();
			if (containsList.contains(oid)) {
				continue;
			}
			map.put("oid", oid);
			map.put("name", group.getName());
			map.put("type", "그룹");
			list.add(map);
		}

		return JSONArray.fromObject(list);
	}

	/**
	 * 권한 목록
	 */
	public JSONArray getAuthList(Folder f) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		QueryResult qr = PersistenceHelper.manager.navigate(f, "user", FolderAccessWTUserLink.class);
		while (qr.hasMoreElements()) {
			WTUser user = (WTUser) qr.nextElement();
			Map<String, String> map = new HashMap<>();
			map.put("oid", user.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", user.getFullName());
			map.put("id", user.getName());
			map.put("type", "사용자");
			list.add(map);
		}

		qr.reset();
		qr = PersistenceHelper.manager.navigate(f, "group", FolderAccessWTGroupLink.class);
		while (qr.hasMoreElements()) {
			WTGroup group = (WTGroup) qr.nextElement();
			Map<String, String> map = new HashMap<>();
			map.put("oid", group.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", group.getName());
			map.put("type", "그룹");
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 사용자 권한 목록 OID 리스트
	 */
	private ArrayList<String> getContainsUser(Folder f) throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		QueryResult qr = PersistenceHelper.manager.navigate(f, "user", FolderAccessWTUserLink.class);
		while (qr.hasMoreElements()) {
			WTUser user = (WTUser) qr.nextElement();
			String oid = user.getPersistInfo().getObjectIdentifier().getStringValue();
			list.add(oid);
		}
		return list;
	}

	/**
	 * 그룹 권한 목록 OID 리스트
	 */
	private ArrayList<String> getContainsGroup(Folder f) throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		QueryResult qr = PersistenceHelper.manager.navigate(f, "group", FolderAccessWTGroupLink.class);
		while (qr.hasMoreElements()) {
			WTGroup group = (WTGroup) qr.nextElement();
			String oid = group.getPersistInfo().getObjectIdentifier().getStringValue();
			list.add(oid);
		}
		return list;
	}

	/**
	 * 권한 체크
	 */
	public boolean isPermission(String oid) throws Exception {
//		if(CommonUtil.isAdmin()) {
//			return true;
//		}
		Persistable per = CommonUtil.getObject(oid);
		return isPermission(per);
	}

	/**
	 * 권한 체크
	 */
	public boolean isPermission(Persistable per) throws Exception {

		// 객체 OID
		boolean isAuth = false;
		SubFolder f = null;
		String folderPath = null;
		if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			folderPath = doc.getLocation();
		} else if (per instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) per;
			folderPath = epm.getLocation();
		} else if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			folderPath = part.getLocation();
		}

		f = (SubFolder) FolderTaskLogic.getFolder(folderPath, WCUtil.getWTContainerRef());

		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		// 사용자 권한 부터 체크
		boolean isUserAuth = isFolderUserAuth(f, user);
		// 권한이 없다면 그룹 레벨에서 다시 체크한다..
		if (!isUserAuth) {
			boolean isGroupAuth = isFolderGroupAuth(f, user);
			if (isGroupAuth) {
				isAuth = true;
			}
		} else {
			isAuth = true;
		}
		return isAuth;
	}

	/**
	 * 그룹 권한 체크
	 */
	private boolean isFolderGroupAuth(SubFolder f, WTUser user) throws Exception {
		Enumeration<WTPrincipalReference> en = user.parentGroups();
		while (en.hasMoreElements()) {
			WTPrincipalReference ref = (WTPrincipalReference) en.nextElement();
			WTGroup group = (WTGroup) ref.getPrincipal();
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(FolderAccessWTGroupLink.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, FolderAccessWTGroupLink.class, "roleAObjectRef.key.id", f);
			QuerySpecUtils.toEqualsAnd(query, idx, FolderAccessWTGroupLink.class, "roleBObjectRef.key.id", group);
			QueryResult qr = PersistenceHelper.manager.find(query);
			if (qr.hasMoreElements()) {
				return true;
			} else {
				SubFolder parent = (SubFolder) f.getParentFolder().getObject();
				if (parent != null) {
					boolean isGo = isParentFolderGroupAuth(parent, group);
					if (!isGo) {
						continue;
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 상위 폴더 = 그룹
	 */
	private boolean isParentFolderGroupAuth(SubFolder f, WTGroup group) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FolderAccessWTGroupLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, FolderAccessWTGroupLink.class, "roleAObjectRef.key.id", f);
		QuerySpecUtils.toEqualsAnd(query, idx, FolderAccessWTGroupLink.class, "roleBObjectRef.key.id", group);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			return true;
		} else {
			SubFolder parent = (SubFolder) f.getParentFolder().getObject();
			if (parent != null) {
				return isParentFolderGroupAuth(parent, group);
			}
		}
		return false;
	}

	/**
	 * 사용자 권한 체크 - 없다면 부모로 올라간다...
	 */
	private boolean isFolderUserAuth(SubFolder f, WTUser user) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FolderAccessWTUserLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, FolderAccessWTUserLink.class, "roleAObjectRef.key.id", f);
		QuerySpecUtils.toEqualsAnd(query, idx, FolderAccessWTUserLink.class, "roleBObjectRef.key.id", user);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			return true;
		} else {
			SubFolder parent = (SubFolder) f.getParentFolder().getObject();
			if (parent != null) {
				return isParentFolderUserAuth(parent, user);
			}
		}
		return false;
	}

	/**
	 * 상위 폴더 권한 체크
	 */
	private boolean isParentFolderUserAuth(SubFolder f, WTUser user) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FolderAccessWTUserLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, FolderAccessWTUserLink.class, "roleAObjectRef.key.id", f);
		QuerySpecUtils.toEqualsAnd(query, idx, FolderAccessWTUserLink.class, "roleBObjectRef.key.id", user);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			return true;
		} else {
			SubFolder parent = (SubFolder) f.getParentFolder().getObject();
			if (parent != null) {
				return isParentFolderUserAuth(parent, user);
			}
		}
		return false;
	}
}
