package com.e3ps.part.bom.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.ThumbnailUtil;
import com.e3ps.part.bom.util.BomComparator;
import com.e3ps.part.service.PartHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartBaselineConfigSpec;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;

public class BomHelper {

	public static final BomService service = ServiceFactory.getService(BomService.class);
	public static final BomHelper manager = new BomHelper();

	/**
	 * 더미 품목
	 */
	private boolean skip(WTPart p) throws Exception {
		String number = p.getNumber();
		if (number.length() > 10) {
			return true;
		}

		if (!Pattern.matches("^[0-9]+$", number)) {
			return true;
		}

		return false;
	}

	/**
	 * 설변 활동 BOM 에디터
	 */
	public JSONArray loadEditor(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		boolean skip = Boolean.parseBoolean((String) params.get("skip"));
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(root);
		// 체크아웃시 체크아웃된 데이터 가져오기
		System.out.println("isCheckOut=" + isCheckOut);
		if (isCheckOut) {
			root = (WTPart) WorkInProgressHelper.service.workingCopyOf(root);
		}
		return loadEditor(root, skip);
	}

	/**
	 * 설변 활동 BOM 에디터
	 */
	public JSONArray loadEditor(WTPart root, boolean skip) throws Exception {
		JSONArray list = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("thumb", ThumbnailUtil.thumbnailSmall(root));
		rootNode.put("level", 1);
		rootNode.put("number", root.getNumber());
		rootNode.put("name", root.getName());
		rootNode.put("title", root.getName());
		rootNode.put("state", root.getLifeCycleState().getDisplay());
		rootNode.put("version", root.getVersionIdentifier().getSeries().getValue() + "."
				+ root.getIterationIdentifier().getSeries().getValue());
		rootNode.put("creator", root.getCreatorFullName());
		rootNode.put("isRoot", true);
		rootNode.put("isRevise", root.getLifeCycleState().toString().equals("APPROVED"));
		rootNode.put("link", "");
		rootNode.put("qty", 1);
		rootNode.put("expanded", true);

		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(root);
		if (isCheckOut) {
			rootNode.put("icon", "/Windchill/wtcore/images/part_checkout.png");
		} else {
			rootNode.put("icon", "/Windchill/wtcore/images/part.gif");
		}
		rootNode.put("isCheckOut", isCheckOut);

		JSONArray children = new JSONArray();

		String viewName = root.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		State state = root.getLifeCycleState();
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

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}

			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("poid", root.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb", ThumbnailUtil.thumbnailSmall(p));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("title", p.getName());
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("creator", p.getCreatorFullName());
			node.put("isRoot", false);
			node.put("isRevise", p.getLifeCycleState().toString().equals("APPROVED"));
			node.put("link", link.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("qty", link.getQuantity().getAmount());
			node.put("expanded", false);
			isCheckOut = WorkInProgressHelper.isCheckedOut(p);
			if (isCheckOut) {
				node.put("icon", "/Windchill/wtcore/images/part_checkout.png");
			} else {
				node.put("icon", "/Windchill/wtcore/images/part.gif");
			}
			node.put("isCheckOut", isCheckOut);

			boolean isLazy = isLazy(p, view, state, skip);
			if (!isLazy) {
				node.put("lazy", false);
//				node.put("expanded", false);
			} else {
				node.put("lazy", true);
//				node.put("expanded", true);
			}
			children.add(node);
			Collections.sort(children, new BomComparator());
		}
		rootNode.put("children", children);
		list.add(rootNode);
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

	/**
	 * BOM 뷰 화면
	 */
	public JSONArray loadStructure(Map<String, Object> params) throws Exception {
		JSONArray list = new JSONArray();
		boolean desc = (boolean) params.get("desc"); // 정전개
		String baseline = (String) params.get("baseline");
		boolean skip = (boolean) params.get("skip"); // 더미 품목 제외
		String oid = (String) params.get("oid");
		if (desc) { // 정전개
			if (StringUtil.checkString(baseline)) {
				list = descendants(list, oid, skip, baseline);
			} else {
				list = descendants(list, oid, skip);
			}
		} else { // 역전개
			if (StringUtil.checkString(baseline)) {
				list = ancestor(list, oid, skip, baseline);
			} else {
				list = ancestor(list, oid, skip);
			}
		}
		// 오름 차순
		return list;
	}

	/**
	 * 역전개
	 */
	private JSONArray ancestor(JSONArray list, String oid, boolean skip) throws Exception {
		WTPart end = (WTPart) CommonUtil.getObject(oid);
		String[] endRtn = getDwgInfo(end);
		View view = ViewHelper.service.getView(end.getViewName());
		String state = end.getLifeCycleState().toString();

		// 역전개 루트
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", end.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("thumb_3d", ThumbnailUtil.thumbnailSmall(end));
		rootNode.put("thumb_2d", ThumbnailUtil.thumbnailSmall(endRtn[1]));
		rootNode.put("level", 1);
		rootNode.put("number", end.getNumber());
		rootNode.put("name", end.getName());
		rootNode.put("dwg_no", endRtn[0]);
		rootNode.put("dwg_oid", endRtn[1]);
		rootNode.put("state", end.getLifeCycleState().getDisplay());
		rootNode.put("version", end.getVersionIdentifier().getSeries().getValue() + "."
				+ end.getIterationIdentifier().getSeries().getValue());
		rootNode.put("modifier", end.getModifierFullName());
		rootNode.put("qty", 1);
		rootNode.put("model", IBAUtils.getStringValue(end, "MODEL"));
		rootNode.put("remarks", IBAUtils.getStringValue(end, "REMARKS"));
		rootNode.put("specification", IBAUtils.getStringValue(end, "SPECIFICATION"));
		rootNode.put("deptcode", IBAUtils.getStringValue(end, "DEPTCODE"));
		rootNode.put("productmethod", IBAUtils.getStringValue(end, "PRODUCTMETHOD"));
		rootNode.put("ecoNo", IBAUtils.getStringValue(end, "CHANGENO"));
		rootNode.put("manufacture", IBAUtils.getStringValue(end, "MANUFACTURE"));

		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(end);
//		boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(end);
		if (isCheckOut) {
			rootNode.put("isCheckOut", isCheckOut);
		}
//		if (isWorkCopy) {
//			rootNode.put("isWorkCopy", isWorkCopy);
//		}

		WTPartMaster master = (WTPartMaster) end.getMaster();
		QuerySpec query = new QuerySpec();
		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });
		query.appendAnd();
		query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { idx_part });

		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

//		if (state != null) {
//			query.appendAnd();
//			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
//		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);
		QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		JSONArray children = new JSONArray();
		int level = 2;
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			String[] childRtn = getDwgInfo(p);
			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb_3d", ThumbnailUtil.thumbnailSmall(p));
			node.put("thumb_2d", ThumbnailUtil.thumbnailSmall(childRtn[1]));
			node.put("dwg_no", childRtn[0]);
			node.put("dwg_oid", childRtn[1]);
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("modifier", p.getModifierFullName());
			node.put("qty", link.getQuantity().getAmount());
			node.put("model", IBAUtils.getStringValue(p, "MODEL"));
			node.put("remarks", IBAUtils.getStringValue(p, "REMARKS"));
			node.put("specification", IBAUtils.getStringValue(p, "SPECIFICATION"));
			node.put("deptcode", IBAUtils.getStringValue(p, "DEPTCODE"));
			node.put("productmethod", IBAUtils.getStringValue(p, "PRODUCTMETHOD"));
			node.put("ecoNo", IBAUtils.getStringValue(p, "CHANGENO"));
			node.put("manufacture", IBAUtils.getStringValue(p, "MANUFACTURE"));

			isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(master, view, state, skip);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}

			children.add(node);
		}

		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * 베이스라인 역전개
	 */
	private JSONArray ancestor(JSONArray list, String oid, boolean skip, String baseline) throws Exception {
		WTPart end = (WTPart) CommonUtil.getObject(oid);
		String state = end.getLifeCycleState().toString();
		String[] endRtn = getDwgInfo(end);
		// 역전개 루트
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", end.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("thumb_3d", ThumbnailUtil.thumbnailSmall(end));
		rootNode.put("thumb_2d", ThumbnailUtil.thumbnailSmall(endRtn[1]));
		rootNode.put("level", 1);
		rootNode.put("number", end.getNumber());
		rootNode.put("name", end.getName());
		rootNode.put("dwg_no", endRtn[0]);
		rootNode.put("dwg_oid", endRtn[1]);
		rootNode.put("state", end.getLifeCycleState().getDisplay());
		rootNode.put("version", end.getVersionIdentifier().getSeries().getValue() + "."
				+ end.getIterationIdentifier().getSeries().getValue());
		rootNode.put("modifier", end.getModifierFullName());
		rootNode.put("qty", 1);
		rootNode.put("model", IBAUtils.getStringValue(end, "MODEL"));
		rootNode.put("remarks", IBAUtils.getStringValue(end, "REMARKS"));
		rootNode.put("specification", IBAUtils.getStringValue(end, "SPECIFICATION"));
		rootNode.put("deptcode", IBAUtils.getStringValue(end, "DEPTCODE"));
		rootNode.put("productmethod", IBAUtils.getStringValue(end, "PRODUCTMETHOD"));
		rootNode.put("ecoNo", IBAUtils.getStringValue(end, "CHANGENO"));
		rootNode.put("manufacture", IBAUtils.getStringValue(end, "MANUFACTURE"));

		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(end);
//		boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(end);
		if (isCheckOut) {
			rootNode.put("isCheckOut", isCheckOut);
		}
//		if (isWorkCopy) {
//			rootNode.put("isWorkCopy", isWorkCopy);
//		}

		WTPartMaster master = (WTPartMaster) end.getMaster();
		QuerySpec query = new QuerySpec();
		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });

//		if (state != null) {
//			query.appendAnd();
//			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
//					new int[] { idx_part });
//		}

		if (baseline != null) {
			Baseline baseLine = (Baseline) CommonUtil.getObject(baseline);
			int idx_b = query.addClassList(BaselineMember.class, false);
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id",
					BaselineMember.class, "roleBObjectRef.key.id"), new int[] { idx_part, idx_b });
			query.appendAnd();
			query.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
					baseLine.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_b });
		}

		QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		JSONArray children = new JSONArray();
		int level = 2;
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			String[] childRtn = getDwgInfo(p);
			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb_3d", ThumbnailUtil.thumbnailSmall(p));
			node.put("thumb_2d", ThumbnailUtil.thumbnailSmall(childRtn[1]));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("dwg_no", childRtn[0]);
			node.put("dwg_oid", childRtn[1]);
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("modifier", p.getModifierFullName());
			node.put("qty", link.getQuantity().getAmount());
			node.put("model", IBAUtils.getStringValue(p, "MODEL"));
			node.put("remarks", IBAUtils.getStringValue(p, "REMARKS"));
			node.put("specification", IBAUtils.getStringValue(p, "SPECIFICATION"));
			node.put("deptcode", IBAUtils.getStringValue(p, "DEPTCODE"));
			node.put("productmethod", IBAUtils.getStringValue(p, "PRODUCTMETHOD"));
			node.put("ecoNo", IBAUtils.getStringValue(p, "CHANGENO"));
			node.put("manufacture", IBAUtils.getStringValue(p, "MANUFACTURE"));

			isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(master, baseline, state, skip);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}

			children.add(node);
		}

		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * 일반 정전개
	 */
	private JSONArray descendants(JSONArray list, String oid, boolean skip) throws Exception {
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		String[] rootRtn = getDwgInfo(root);
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("thumb_3d", ThumbnailUtil.thumbnailSmall(root));
		rootNode.put("thumb_2d", ThumbnailUtil.thumbnailSmall(rootRtn[1]));
		rootNode.put("level", 1);
		rootNode.put("number", root.getNumber());
		rootNode.put("name", root.getName());
		rootNode.put("dwg_no", rootRtn[0]);
		rootNode.put("dwg_oid", rootRtn[1]);
		rootNode.put("state", root.getLifeCycleState().getDisplay());
		rootNode.put("version", root.getVersionIdentifier().getSeries().getValue() + "."
				+ root.getIterationIdentifier().getSeries().getValue());
		rootNode.put("modifier", root.getModifierFullName());
		rootNode.put("qty", 1);
		rootNode.put("model", IBAUtils.getStringValue(root, "MODEL"));
		rootNode.put("remarks", IBAUtils.getStringValue(root, "REMARKS"));
		rootNode.put("specification", IBAUtils.getStringValue(root, "SPECIFICATION"));
		rootNode.put("deptcode", IBAUtils.getStringValue(root, "DEPTCODE"));
		rootNode.put("productmethod", IBAUtils.getStringValue(root, "PRODUCTMETHOD"));
		rootNode.put("ecoNo", IBAUtils.getStringValue(root, "CHANGENO"));
		rootNode.put("manufacture", IBAUtils.getStringValue(root, "MANUFACTURE"));

		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(root);
//		boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(root);
		if (isCheckOut) {
			rootNode.put("isCheckOut", isCheckOut);
		}
//		if (isWorkCopy) {
//			rootNode.put("isWorkCopy", isWorkCopy);
//		}

		View view = ViewHelper.service.getView(root.getViewName());
		State state = root.getLifeCycleState();
		JSONArray children = new JSONArray();

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

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			String[] childRtn = getDwgInfo(p);
			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb_3d", ThumbnailUtil.thumbnailSmall(p));
			node.put("thumb_2d", ThumbnailUtil.thumbnailSmall(childRtn[1]));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("dwg_no", childRtn[0]);
			node.put("dwg_oid", childRtn[1]);
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("modifier", p.getModifierFullName());
			node.put("qty", link.getQuantity().getAmount());
			node.put("model", IBAUtils.getStringValue(p, "MODEL"));
			node.put("remarks", IBAUtils.getStringValue(p, "REMARKS"));
			node.put("specification", IBAUtils.getStringValue(p, "SPECIFICATION"));
			node.put("deptcode", IBAUtils.getStringValue(p, "DEPTCODE"));
			node.put("productmethod", IBAUtils.getStringValue(p, "PRODUCTMETHOD"));
			node.put("ecoNo", IBAUtils.getStringValue(p, "CHANGENO"));
			node.put("manufacture", IBAUtils.getStringValue(p, "MANUFACTURE"));

			isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(p, view, state, skip);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}

			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * 베이스 라인 정전개
	 */
	private JSONArray descendants(JSONArray list, String oid, boolean skip, String baseline) throws Exception {
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		String[] rootRtn = getDwgInfo(root);
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("thumb_3d", ThumbnailUtil.thumbnailSmall(root));
		rootNode.put("thumb_2d", ThumbnailUtil.thumbnailSmall(rootRtn[1]));
		rootNode.put("level", 1);
		rootNode.put("dwg_no", rootRtn[0]);
		rootNode.put("dwg_oid", rootRtn[1]);
		rootNode.put("number", root.getNumber());
		rootNode.put("name", root.getName());
		rootNode.put("state", root.getLifeCycleState().getDisplay());
		rootNode.put("version", root.getVersionIdentifier().getSeries().getValue() + "."
				+ root.getIterationIdentifier().getSeries().getValue());
		rootNode.put("modifier", root.getModifierFullName());
		rootNode.put("qty", 1);
		//
		rootNode.put("model", IBAUtils.getStringValue(root, "MODEL"));
		rootNode.put("remarks", IBAUtils.getStringValue(root, "REMARKS"));
		rootNode.put("specification", IBAUtils.getStringValue(root, "SPECIFICATION"));
		rootNode.put("deptcode", IBAUtils.getStringValue(root, "DEPTCODE"));
		rootNode.put("productmethod", IBAUtils.getStringValue(root, "PRODUCTMETHOD"));
		rootNode.put("ecoNo", IBAUtils.getStringValue(root, "CHANGENO"));
		rootNode.put("manufacture", IBAUtils.getStringValue(root, "MANUFACTURE"));

		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(root);
//		boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(root);
		if (isCheckOut) {
			rootNode.put("isCheckOut", isCheckOut);
		}
//		if (isWorkCopy) {
//			rootNode.put("isWorkCopy", isWorkCopy);
//		}

		Baseline baseLine = (Baseline) CommonUtil.getObject(baseline);
		JSONArray children = new JSONArray();
		WTPartBaselineConfigSpec configSpec = WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec(baseLine);
		QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);
		int level = 2;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}

			String[] childRtn = getDwgInfo(p);
			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb_3d", ThumbnailUtil.thumbnailSmall(p));
			node.put("thumb_2d", ThumbnailUtil.thumbnailSmall(childRtn[1]));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("dwg_no", childRtn[0]);
			node.put("dwg_oid", childRtn[1]);
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("modifier", p.getModifierFullName());
			node.put("qty", link.getQuantity().getAmount());

			node.put("model", IBAUtils.getStringValue(p, "MODEL"));
			node.put("remarks", IBAUtils.getStringValue(p, "REMARKS"));
			node.put("specification", IBAUtils.getStringValue(p, "SPECIFICATION"));
			node.put("deptcode", IBAUtils.getStringValue(p, "DEPTCODE"));
			node.put("productmethod", IBAUtils.getStringValue(p, "PRODUCTMETHOD"));
			node.put("ecoNo", IBAUtils.getStringValue(p, "CHANGENO"));
			node.put("manufacture", IBAUtils.getStringValue(p, "MANUFACTURE"));

			isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(p, baseLine, skip);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}

			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * BOM 뷰에서 레이지 로드
	 */
	public ArrayList<Map<String, Object>> lazyLoad(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		boolean desc = (boolean) params.get("desc"); // 정전개
		String baseline = (String) params.get("baseline");
		boolean skip = (boolean) params.get("skip"); // 더미 품목 제외
		String oid = (String) params.get("oid");
		int level = (int) params.get("level");
		if (desc) { // 정전개
			if (StringUtil.checkString(baseline)) {
				list = descendants(list, oid, skip, baseline, level);
			} else {
				list = descendants(list, oid, skip, level);
			}
		} else { // 역전개
			if (StringUtil.checkString(baseline)) {
				list = ancestor(list, oid, skip, baseline, level);
			} else {
				list = ancestor(list, oid, skip, level);
			}
		}
		return list;
	}

	/**
	 * 역전개 레이지 로드
	 */
	private ArrayList<Map<String, Object>> ancestor(ArrayList<Map<String, Object>> list, String oid, boolean skip,
			int level) throws Exception {
		WTPart end = (WTPart) CommonUtil.getObject(oid);
		View view = ViewHelper.service.getView(end.getViewName());
		String state = end.getLifeCycleState().toString();
		WTPartMaster master = (WTPartMaster) end.getMaster();
		QuerySpec query = new QuerySpec();
		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });
		query.appendAnd();
		query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { idx_part });

		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

//		if (state != null) {
//			query.appendAnd();
//			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
//		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);
		QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		++level;
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			String[] childRtn = getDwgInfo(p);
			Map<String, Object> node = new HashMap<>();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb_3d", ThumbnailUtil.thumbnailSmall(p));
			node.put("thumb_2d", ThumbnailUtil.thumbnailSmall(childRtn[1]));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("dwg_no", childRtn[0]);
			node.put("dwg_oid", childRtn[1]);
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("modifier", p.getModifierFullName());
			node.put("qty", link.getQuantity().getAmount());
			node.put("model", IBAUtils.getStringValue(p, "MODEL"));
			node.put("remarks", IBAUtils.getStringValue(p, "REMARKS"));
			node.put("specification", IBAUtils.getStringValue(p, "SPECIFICATION"));
			node.put("deptcode", IBAUtils.getStringValue(p, "DEPTCODE"));
			node.put("productmethod", IBAUtils.getStringValue(p, "PRODUCTMETHOD"));
			node.put("ecoNo", IBAUtils.getStringValue(p, "CHANGENO"));
			node.put("manufacture", IBAUtils.getStringValue(p, "MANUFACTURE"));
			boolean isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(master, view, state, skip);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}
			list.add(node);
		}
		return list;

	}

	/**
	 * 베이스라인 역전개 레이지로드
	 */
	private ArrayList<Map<String, Object>> ancestor(ArrayList<Map<String, Object>> list, String oid, boolean skip,
			String baseline, int level) throws Exception {
		WTPart end = (WTPart) CommonUtil.getObject(oid);
		String state = end.getLifeCycleState().toString();
		WTPartMaster master = (WTPartMaster) end.getMaster();
		QuerySpec query = new QuerySpec();
		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });

//		if (state != null) {
//			query.appendAnd();
//			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
//					new int[] { idx_part });
//		}

		if (baseline != null) {
			Baseline baseLine = (Baseline) CommonUtil.getObject(baseline);
			int idx_b = query.addClassList(BaselineMember.class, false);
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id",
					BaselineMember.class, "roleBObjectRef.key.id"), new int[] { idx_part, idx_b });
			query.appendAnd();
			query.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
					baseLine.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_b });
		}

		QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		++level;
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			String[] childRtn = getDwgInfo(p);
			Map<String, Object> node = new HashMap<>();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb_3d", ThumbnailUtil.thumbnailSmall(p));
			node.put("thumb_2d", ThumbnailUtil.thumbnailSmall(childRtn[1]));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("dwg_no", childRtn[0]);
			node.put("dwg_oid", childRtn[1]);
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("modifier", p.getModifierFullName());
			node.put("qty", link.getQuantity().getAmount());
			node.put("model", IBAUtils.getStringValue(p, "MODEL"));
			node.put("remarks", IBAUtils.getStringValue(p, "REMARKS"));
			node.put("specification", IBAUtils.getStringValue(p, "SPECIFICATION"));
			node.put("deptcode", IBAUtils.getStringValue(p, "DEPTCODE"));
			node.put("productmethod", IBAUtils.getStringValue(p, "PRODUCTMETHOD"));
			node.put("ecoNo", IBAUtils.getStringValue(p, "CHANGENO"));
			node.put("manufacture", IBAUtils.getStringValue(p, "MANUFACTURE"));
			boolean isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(master, baseline, state, skip);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}

			list.add(node);
		}
		return list;
	}

	/**
	 * 정전개 레이지 로드
	 */
	private ArrayList<Map<String, Object>> descendants(ArrayList<Map<String, Object>> list, String oid, boolean skip,
			int level) throws Exception {
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		View view = ViewHelper.service.getView(part.getViewName());
		State state = part.getLifeCycleState();
//		WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		++level;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			String[] childRtn = getDwgInfo(p);
			Map<String, Object> node = new HashMap<>();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb_3d", ThumbnailUtil.thumbnailSmall(p));
			node.put("thumb_2d", ThumbnailUtil.thumbnailSmall(childRtn[1]));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("dwg_no", childRtn[0]);
			node.put("dwg_oid", childRtn[1]);
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("modifier", p.getModifierFullName());
			node.put("qty", link.getQuantity().getAmount());
			node.put("model", IBAUtils.getStringValue(p, "MODEL"));
			node.put("remarks", IBAUtils.getStringValue(p, "REMARKS"));
			node.put("specification", IBAUtils.getStringValue(p, "SPECIFICATION"));
			node.put("deptcode", IBAUtils.getStringValue(p, "DEPTCODE"));
			node.put("productmethod", IBAUtils.getStringValue(p, "PRODUCTMETHOD"));
			node.put("ecoNo", IBAUtils.getStringValue(p, "CHANGENO"));
			node.put("manufacture", IBAUtils.getStringValue(p, "MANUFACTURE"));
			boolean isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(p, view, state, skip);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}
			list.add(node);
		}
		return list;
	}

	/**
	 * 베이스라인 정전개 레이지 로드
	 */
	private ArrayList<Map<String, Object>> descendants(ArrayList<Map<String, Object>> list, String oid, boolean skip,
			String baseline, int level) throws Exception {
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		Baseline baseLine = (Baseline) CommonUtil.getObject(baseline);
		WTPartBaselineConfigSpec configSpec = WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec(baseLine);
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		++level;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			String[] childRtn = getDwgInfo(p);
			Map<String, Object> node = new HashMap<>();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb_3d", ThumbnailUtil.thumbnailSmall(p));
			node.put("thumb_2d", ThumbnailUtil.thumbnailSmall(childRtn[1]));
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("dwg_no", childRtn[0]);
			node.put("dwg_oid", childRtn[1]);
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("modifier", p.getModifierFullName());
			node.put("qty", link.getQuantity().getAmount());
			node.put("model", IBAUtils.getStringValue(p, "MODEL"));
			node.put("remarks", IBAUtils.getStringValue(p, "REMARKS"));
			node.put("specification", IBAUtils.getStringValue(p, "SPECIFICATION"));
			node.put("deptcode", IBAUtils.getStringValue(p, "DEPTCODE"));
			node.put("productmethod", IBAUtils.getStringValue(p, "PRODUCTMETHOD"));
			node.put("ecoNo", IBAUtils.getStringValue(p, "CHANGENO"));
			node.put("manufacture", IBAUtils.getStringValue(p, "MANUFACTURE"));
			boolean isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("isCheckOut", isCheckOut);
			}
//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(p, baseLine, skip);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}

			list.add(node);
		}
		return list;
	}

	/**
	 * BOM 뷰에서 사용할 도면 여부체크
	 */
	private String[] getDwgInfo(WTPart part) throws Exception {
		String[] rtn = new String[2];
		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		boolean exist = false;
		if (epm != null) {
			EPMDocument epm_d = PartHelper.manager.getEPMDocument2D(epm);
			if (epm_d != null) {
				rtn[0] = epm_d.getNumber();
				rtn[1] = epm_d.getPersistInfo().getObjectIdentifier().getStringValue();
			} else {
				exist = true;
				// 무조건 잇음..
				if (epm.getAuthoringApplication().toString().equals("MANUAL")) {
					rtn[0] = epm.getNumber();
					rtn[1] = epm.getPersistInfo().getObjectIdentifier().getStringValue();
					exist = false;
				}
			}
		} else {
			exist = true;
		}

		// 3D 없을시 체크???
		if (exist) {
			WTDocument doc = hasAP(part, "$$APDocument");
			if (doc != null) {
				rtn[0] = "AP";
				rtn[1] = doc.getPersistInfo().getObjectIdentifier().getStringValue();
			} else {
				rtn[0] = "ND";
				rtn[1] = "";
			}
		}
		return rtn;
	}

	/**
	 * AP 문서..
	 */
	private WTDocument hasAP(WTPart part, String docType) throws Exception {
		QuerySpec query = new QuerySpec();

		int idx_p = query.addClassList(WTPart.class, false);
		int idx_d = query.addClassList(WTDocument.class, true);
		int idx_l = query.addClassList(WTPartDescribeLink.class, false);

		query.setAdvancedQueryEnabled(true);

		query.appendJoin(idx_l, "roleA", idx_p);
		query.appendJoin(idx_l, "roleB", idx_d);
		query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id",
				SearchCondition.EQUAL, CommonUtil.getOIDLongValue(part)), new int[] { idx_p });
		query.appendAnd();
		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, docType),
				new int[] { idx_d });

		QueryResult qr = PersistenceServerHelper.manager.query(query);
		if (qr.size() == 1) {
			Object obj[] = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) obj[0];
			return doc;
		}
		return null;
	}

	/**
	 * BOM 첨부파일, 도면 일괄 다운로드
	 */
	public File batch(Map<String, Object> params) throws Exception {
		ArrayList<String> arr = (ArrayList<String>) params.get("arr");
		String target = (String) params.get("target");
		String reason = (String) params.get("reason");
		String description = (String) params.get("description");

		for (String oid : arr) {
			WTPart part = (WTPart) CommonUtil.getObject(oid);
		}

		return null;
	}

	/**
	 * 정전개 하위가 잇는지 없는지 판단 - 베이스라인
	 */
	private boolean isLazy(WTPart parent, Baseline baseLine, boolean skip) throws Exception {
		WTPartBaselineConfigSpec configSpec = WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec(baseLine);
		QueryResult result = WTPartHelper.service.getUsesWTParts(parent, configSpec);
		boolean isLazy = false;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			isLazy = true;
			break;
		}
		return isLazy;
	}

	/**
	 * 정전개 하위가 잇는지 없는지 판단
	 */
	private boolean isLazy(WTPart parent, View view, State state, boolean skip) throws Exception {
//		WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(parent, configSpec);
		boolean isLazy = false;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			isLazy = true;
			break;
		}
		return isLazy;
	}

	/**
	 * 역전개 레이지 로드 여부 확인
	 */
	private boolean isLazy(WTPartMaster master, View view, String state, boolean skip) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });
		query.appendAnd();
		query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { idx_part });

		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

		if (state != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);
		QueryResult qr = PersistenceHelper.manager.find(query);
		boolean isLazy = false;
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			isLazy = true;
			break;
		}
		return isLazy;
	}

	/**
	 * 베이스 라인 역전개 레이즈 로드 여부 확인
	 */
	private boolean isLazy(WTPartMaster master, String baseline, String state, boolean skip) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });

		if (state != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
					new int[] { idx_part });
		}

		if (baseline != null) {
			Baseline baseLine = (Baseline) CommonUtil.getObject(baseline);
			int idx_b = query.addClassList(BaselineMember.class, false);
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id",
					BaselineMember.class, "roleBObjectRef.key.id"), new int[] { idx_part, idx_b });
			query.appendAnd();
			query.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
					baseLine.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_b });
		}

		QueryResult qr = PersistenceHelper.manager.find(query);
		boolean isLazy = false;
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			if (skip) {
				if (skip(p)) {
					continue;
				}
			}
			isLazy = true;
			break;
		}
		return isLazy;
	}

	/**
	 * BOM 에디터 레이지 로드
	 */
	public JSONArray editorLazyLoad(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		boolean skip = Boolean.parseBoolean((String) params.get("skip"));
		int level = Integer.parseInt((String) params.get("level"));
		WTPart parent = (WTPart) CommonUtil.getObject(oid);

		String viewName = parent.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);

		State state = parent.getLifeCycleState();
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(parent, configSpec);
		++level;
		JSONArray list = new JSONArray();
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart p = (WTPart) obj[1];

			if (skip) {
				if (skip(p)) {
					continue;
				}
			}

			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("level", level);
			node.put("poid", parent.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("thumb", ThumbnailUtil.thumbnailSmall(p));
			node.put("number", p.getNumber());
			node.put("title", p.getName());
			node.put("name", p.getName());
			node.put("state", p.getLifeCycleState().getDisplay());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("creator", p.getCreatorFullName());
			node.put("isRoot", false);
			node.put("link", link.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("qty", link.getQuantity().getAmount());
			node.put("expanded", false);
			boolean isCheckOut = WorkInProgressHelper.isCheckedOut(p);
//			boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(p);
			if (isCheckOut) {
				node.put("icon", "/Windchill/wtcore/images/part_checkout.png");
			} else {
				node.put("icon", "/Windchill/wtcore/images/part.gif");
			}
			node.put("isCheckOut", isCheckOut);

//			if (isWorkCopy) {
//				node.put("isWorkCopy", isWorkCopy);
//			}

			boolean isLazy = isLazy(p, view, state, skip);
			if (!isLazy) {
				node.put("lazy", false);
//				node.put("children", new JSONArray());
			} else {
				node.put("lazy", true);
			}
			list.add(node);
			Collections.sort(list, new BomComparator());
		}
		return list;
	}

	/**
	 * 변경된 노드 리턴
	 */
	public JSONObject getNode(WTPart part) throws Exception {
		JSONObject node = new JSONObject();
		node.put("oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
		node.put("version", part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
		boolean isCheckOut = WorkInProgressHelper.isCheckedOut(part);
		if (isCheckOut) {
			node.put("icon", "/Windchill/wtcore/images/part_checkout.png");
		} else {
			node.put("icon", "/Windchill/wtcore/images/part.gif");
		}
		node.put("isCheckOut", isCheckOut);
		return node;
	}
}
