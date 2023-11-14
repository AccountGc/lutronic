package com.e3ps.part.bom.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.ThumbnailUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;

public class BomHelper {

	public static final BomService service = ServiceFactory.getService(BomService.class);
	public static final BomHelper manager = new BomHelper();

	/**
	 * BOM 뷰 화면
	 */
	public JSONArray loadStructure(Map<String, Object> params) throws Exception {
		String baseLine = (String) params.get("baseLine"); // 베이스 라인 OID
		boolean skip = (boolean) params.get("skip"); // 더미 품목 제외
		String oid = (String) params.get("oid");
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		JSONArray list = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("thumb", ThumbnailUtil.thumbnailSmall(root));
		rootNode.put("level", 1);
		rootNode.put("number", root.getNumber());
		rootNode.put("name", root.getName());
		rootNode.put("state", root.getLifeCycleState().getDisplay());
		rootNode.put("version", root.getVersionIdentifier().getSeries().getValue() + "."
				+ root.getIterationIdentifier().getSeries().getValue());
		rootNode.put("creator", root.getCreatorFullName());
		rootNode.put("isRoot", true);
		rootNode.put("link", "");

		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(root);
		boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(root);
		if (isCheckOut) {
			rootNode.put("isCheckOut", isCheckOut);
		}
		if (isWorkCopy) {
			rootNode.put("isWorkCopy", isWorkCopy);
		}

		JSONArray children = new JSONArray();

		View view = ViewHelper.service.getView(root.getViewName());
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);
		int level = 2;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];
			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("poid", root.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb", ThumbnailUtil.thumbnailSmall(p));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("creator", p.getCreatorFullName());
			node.put("isRoot", false);
			node.put("link", link.getPersistInfo().getObjectIdentifier().getStringValue());
			isCheckOut = WorkInProgressHelper.isCheckedOut(p);
			isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
			if (isWorkCopy) {
				node.put("isWorkCopy", isWorkCopy);
			}
//			loadEditor(p, node, level);
			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * 설변 활동 BOM 에디터
	 */
	public JSONArray loadEditor(String oid) throws Exception {
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(root);
		// 체크아웃시 체크아웃된 데이터 가져오기
		if (isCheckOut) {
			root = (WTPart) WorkInProgressHelper.service.workingCopyOf(root);
		}
		return loadEditor(root);
	}

	/**
	 * 설변 활동 BOM 에디터
	 */
	public JSONArray loadEditor(WTPart root) throws Exception {
		JSONArray list = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("thumb", ThumbnailUtil.thumbnailSmall(root));
		rootNode.put("level", 1);
		rootNode.put("number", root.getNumber());
		rootNode.put("name", root.getName());
		rootNode.put("state", root.getLifeCycleState().getDisplay());
		rootNode.put("version", root.getVersionIdentifier().getSeries().getValue() + "."
				+ root.getIterationIdentifier().getSeries().getValue());
		rootNode.put("creator", root.getCreatorFullName());
		rootNode.put("isRoot", true);
		rootNode.put("link", "");

		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(root);
		boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(root);
		if (isCheckOut) {
			rootNode.put("isCheckOut", isCheckOut);
		}
		if (isWorkCopy) {
			rootNode.put("isWorkCopy", isWorkCopy);
		}

		JSONArray children = new JSONArray();

		View view = ViewHelper.service.getView(root.getViewName());
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);
		int level = 2;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];
			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("poid", root.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb", ThumbnailUtil.thumbnailSmall(p));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("creator", p.getCreatorFullName());
			node.put("isRoot", false);
			node.put("link", link.getPersistInfo().getObjectIdentifier().getStringValue());
			isCheckOut = WorkInProgressHelper.isCheckedOut(p);
			isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
			if (isWorkCopy) {
				node.put("isWorkCopy", isWorkCopy);
			}
//			loadEditor(p, node, level);
			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * BOM 에디터 LAZY 로드
	 */
	public ArrayList<Map<String, Object>> lazyLoad(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		WTPart parent = (WTPart) CommonUtil.getObject(oid);
		View view = ViewHelper.service.getView(parent.getViewName());
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(parent, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];
			Map<String, Object> node = new HashMap<>();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("poid", parent.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb", ThumbnailUtil.thumbnailSmall(p));
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("creator", p.getCreatorFullName());
			node.put("isRoot", false);
			node.put("link", link.getPersistInfo().getObjectIdentifier().getStringValue());
			boolean isCheckOut = WorkInProgressHelper.isCheckedOut(p);
			boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
			if (isWorkCopy) {
				node.put("isWorkCopy", isWorkCopy);
			}
			list.add(node);
		}
		return list;
	}

	/**
	 * WTPartUsageLink 가져오는 함수
	 */
	public WTPartUsageLink getUsageLink(WTPart parent, WTPartMaster child) throws Exception {
		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(WTPartUsageLink.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, WTPartUsageLink.class, "roleAObjectRef.key.id", parent);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPartUsageLink.class, "roleBObjectRef.key.id", child);

		QueryResult result = PersistenceHelper.manager.find(query);

		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			return link;
		}
		return null;
	}
}
