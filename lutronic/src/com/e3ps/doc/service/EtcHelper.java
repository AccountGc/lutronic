package com.e3ps.doc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.FolderUtils;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.column.DocumentColumn;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class EtcHelper {

	public static final EtcService service = ServiceFactory.getService(EtcService.class);
	public static final EtcHelper manager = new EtcHelper();

	public static final String RA = "/Default/기타문서관리/RA팀";
	public static final String PRODUCTION = "/Default/기타문서관리/생산본부";
	public static final String PATHOLOGICAL = "/Default/기타문서관리/병리연구";
	public static final String CLINICAL = "/Default/기타문서관리/임상개발";
	public static final String COSMETIC = "/Default/기타문서관리/화장품";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<DocumentColumn> list = new ArrayList<>();

		boolean latest = (boolean) params.get("latest");
		String location = (String) params.get("location");
		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String creatorOid = (String) params.get("creatorOid");
		String state = (String) params.get("state");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String modifiedFrom = (String) params.get("modifiedFrom");
		String modifiedTo = (String) params.get("modifiedTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NUMBER, number);
//		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.DESCRIPTION, description);
		QuerySpecUtils.toState(query, idx, WTDocument.class, state);
		QuerySpecUtils.creatorQuery(query, idx, WTDocument.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, modifiedFrom,
				modifiedTo);

		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
		if (folder != null) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
			ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
			SearchCondition fsc = new SearchCondition(fca, "=",
					new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
			fsc.setOuterJoin(0);
			query.appendWhere(fsc, new int[] { f_idx, idx });
			query.appendAnd();

			query.appendOpenParen();
			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
					new int[] { f_idx });

			ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
			for (int i = 0; i < folders.size(); i++) {
				Folder sub = (Folder) folders.get(i);
				query.appendOr();
				long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
						new int[] { f_idx });
			}
			query.appendCloseParen();
		}

		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument document = (WTDocument) obj[0];
			DocumentColumn data = new DocumentColumn(document);
			list.add(data);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
