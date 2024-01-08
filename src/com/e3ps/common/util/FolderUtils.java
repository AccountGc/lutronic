package com.e3ps.common.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.clients.folder.FolderTaskLogic;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.folder.SubFolderIdentity;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class FolderUtils {

	private FolderUtils() {

	}

	/**
	 * 폴더 구조 트리로 가져오기
	 */
	public static JSONArray tree(Map<String, String> params) throws Exception {
		String location = params.get("location");
		SubFolder root = (SubFolder) FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
		SubFolder proot = (SubFolder) root.getParentFolder().getObject();

		JSONArray list = new JSONArray();
		JSONObject rootNode = new JSONObject();

		rootNode.put("isNew", false);
		rootNode.put("poid", proot != null ? proot.getPersistInfo().getObjectIdentifier().getStringValue() : "");
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("location", root.getFolderPath());
		rootNode.put("name", root.getName());

		JSONArray children = new JSONArray();
		Enumeration result = FolderTaskLogic.getSubFolders(root);
		while (result.hasMoreElements()) {
			Folder child = (Folder) result.nextElement();
			JSONObject node = new JSONObject();
			node.put("poid", root.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("location", child.getFolderPath());
			node.put("name", child.getName());
			node.put("isNew", false);
			tree(child, node);
			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * 폴더 구조 트리로 가져오기 재귀함수
	 */
	private static void tree(Folder parent, JSONObject parentNode) throws Exception {
		JSONArray children = new JSONArray();
		Enumeration result = FolderTaskLogic.getSubFolders(parent);
		while (result.hasMoreElements()) {
			Folder child = (Folder) result.nextElement();
			JSONObject node = new JSONObject();
			node.put("poid", parent.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("location", child.getFolderPath());
			node.put("name", child.getName());
			node.put("isNew", false);
			tree(child, node);
			children.add(node);
		}
		parentNode.put("children", children);
	}

	/**
	 * 자식 폴더 모두 가져오기
	 */
	public static ArrayList<Folder> recurciveFolder(Folder parent, ArrayList<Folder> list) throws Exception {
		QueryResult result = FolderHelper.service.findSubFolders(parent);
		while (result.hasMoreElements()) {
			SubFolder sub = (SubFolder) result.nextElement();
			recurciveFolder(sub, list);
			list.add(sub);
		}
		return list;
	}

	/**
	 * 모든 폴더 가져오기
	 */
	public static ArrayList<Folder> loadAllFolder(String location, String container) throws Exception {
		Folder root = null;
		if ("product".equalsIgnoreCase(container)) {
			root = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
		} else if ("document".equalsIgnoreCase(container)) {
			root = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
		} else if ("library".equalsIgnoreCase(container)) {
			root = FolderTaskLogic.getFolder(location, CommonUtil.getWTLibraryContainer());
		}

	)	ArrayList<Folder> list = new ArrayList<>();

		Enumeration result = FolderTaskLogic.getSubFolders(root);
		while (result.hasMoreElements()) {
			Folder folder = (Folder) result.nextElement();
			list.add(folder);
			recurciveFolder(folder, list);
		}
		return list;
	}

	/**
	 * 하위 폴더 리스트 가져오기
	 */
	public static ArrayList<Folder> getSubFolders(Folder root, ArrayList<Folder> folders) throws Exception {
		QueryResult result = FolderHelper.service.findSubFolders(root);
		while (result.hasMoreElements()) {
			SubFolder sub = (SubFolder) result.nextElement();
			folders.add(sub);
			getSubFolders(sub, folders);
		}
		return folders;
	}

	/**
	 * 폴더 트리 저장
	 * 
	 * @param params
	 */
	public static void treeSave(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, String>> editRows = (ArrayList<Map<String, String>>) params.get("editRows");
		ArrayList<Map<String, String>> addRows = (ArrayList<Map<String, String>>) params.get("addRows");

		for (Map<String, String> addRow : addRows) {
			String parentRowId = addRow.get("parentRowId");
			String name = addRow.get("name");
			SubFolder pf = (SubFolder) CommonUtil.getObject(parentRowId);
			boolean exist = exist(pf, name);
			if (exist) {
				throw new Exception("동일 레벨에 같은 이름의 폴더가 이미 존재합니다.");
			}

			FolderHelper.service.createSubFolder(pf.getFolderPath() + "/" + name, WCUtil.getWTContainerRef());
		}

		for (Map<String, String> editRow : editRows) {
			String oid = editRow.get("oid");
			String parentRowId = editRow.get("poid");
			String name = editRow.get("name");
			SubFolder f = (SubFolder) CommonUtil.getObject(oid);
			SubFolder pf = (SubFolder) CommonUtil.getObject(parentRowId);
			boolean exist = exist(pf, name);
			if (exist) {
				throw new Exception("동일 레벨에 같은 이름의 폴더가 이미 존재합니다.");
			}
			SubFolderIdentity identity = (SubFolderIdentity) f.getIdentificationObject();
			identity.setName(name);
			IdentityHelper.service.changeIdentity(f, identity);
		}
	}

	/**
	 * 같은 레벨에 같은 명의 폴더가 있는지 확인
	 */
	private static boolean exist(Folder pfolder, String name) throws Exception {
		boolean isExist = false;

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SubFolder.class, true);

		SearchCondition sc = new SearchCondition(SubFolder.class, "folderingInfo.parentFolder.key.id", "=",
				pfolder.getPersistInfo().getObjectIdentifier().getId());

		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(SubFolder.class, SubFolder.NAME, "=", name);
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			isExist = true;
		}
		return isExist;
	}
}
