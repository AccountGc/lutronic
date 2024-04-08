package com.e3ps.rohs.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aspose.cells.Cell;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.DocLocation;
import com.e3ps.part.dto.ObjectComarator;
import com.e3ps.part.dto.PartDTO;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.RepresentToLink;
import com.e3ps.rohs.dto.RoHSHolderData;
import com.e3ps.rohs.dto.RohsData;
import com.e3ps.rohs.util.RohsPartComparator;
import com.e3ps.workspace.ApprovalLine;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

public class RohsHelper {
	public static final RohsService service = ServiceFactory.getService(RohsService.class);
	public static final RohsHelper manager = new RohsHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<RohsData> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(ROHSMaterial.class, true);

		try {
			String islastversion = StringUtil.checkNull((String) params.get("islastversion"));
			String rohsNumber = StringUtil.checkNull((String) params.get("rohsNumber"));
			String rohsName = StringUtil.checkNull((String) params.get("rohsName"));
			String description = StringUtil.checkNull((String) params.get("description"));
			String predate = StringUtil.checkNull((String) params.get("createdFrom"));
			String postdate = StringUtil.checkNull((String) params.get("createdTo"));
			String predate_modify = StringUtil.checkNull((String) params.get("modifiedFrom"));
			String postdate_modify = StringUtil.checkNull((String) params.get("modifiedTo"));
			String creator = StringUtil.checkNull((String) params.get("creatorOid"));
			String state = StringUtil.checkNull((String) params.get("state"));
			String manufacture = StringUtil.checkNull((String) params.get("manufacture"));

			// 정렬
			String sortKey = (String) params.get("sortKey");
			String sortType = (String) params.get("sortType");

			if (!StringUtil.checkString(islastversion)) {
				islastversion = "true";
			}

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[] { idx });

			if ("true".equals(islastversion)) {
				QuerySpecUtils.toLatest(query, idx, ROHSMaterial.class);
			}

			// 상태 임시저장 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.LIFE_CYCLE_STATE,
					SearchCondition.NOT_EQUAL, "TEMPRARY"), new int[] { idx });

			// 문서번호
			if (rohsNumber.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.NUMBER, SearchCondition.LIKE,
						"%" + rohsNumber + "%", false), new int[] { idx });
			}

			// 문서명
			if (rohsName.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.NAME, SearchCondition.LIKE,
						"%" + rohsName + "%", false), new int[] { idx });
			}

			// 설명
			if (description.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.DESCRIPTION,
						SearchCondition.LIKE, "%" + description + "%", false), new int[] { idx });
			}

			// 등록일
			if (predate.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.CREATE_TIMESTAMP,
						SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[] { idx });
			}

			if (postdate.length() > 0) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.CREATE_TIMESTAMP,
						SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[] { idx });
			}

			// 수정일
			if (predate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(
						new SearchCondition(ROHSMaterial.class, ROHSMaterial.MODIFY_TIMESTAMP,
								SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)),
						new int[] { idx });
			}

			if (postdate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(
						new SearchCondition(ROHSMaterial.class, ROHSMaterial.MODIFY_TIMESTAMP,
								SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)),
						new int[] { idx });
			}

			// 등록자
			if (creator.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ROHSMaterial.class, "iterationInfo.creator.key.id",
						SearchCondition.EQUAL, CommonUtil.getOIDLongValue(creator)), new int[] { idx });
			}

			// 상태
			if (state.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(ROHSMaterial.class, ROHSMaterial.LIFE_CYCLE_STATE,
						SearchCondition.EQUAL, state), new int[] { idx });
			}

			// 협력 업체
			if (manufacture.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							ROHSMaterial.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + manufacture + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				manufacture = "";
			}

			boolean sort = QuerySpecUtils.toSort(sortType);
			QuerySpecUtils.toOrderBy(query, idx, ROHSMaterial.class, toSortKey(sortKey), sort);

			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				RohsData data = new RohsData((ROHSMaterial) obj[0]);
				data.setRowNum(rowNum++);
				list.add(data);
			}

			map.put("list", list);
			map.put("topListCount", pager.getTotal());
			map.put("pageSize", pager.getPsize());
			map.put("total", pager.getTotalSize());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	private String toSortKey(String sortKey) throws Exception {
		if ("number".equals(sortKey)) {
			return ROHSMaterial.NUMBER;
		} else if ("name".equals(sortKey)) {
			return ROHSMaterial.NAME;
		} else if ("stateDisplay".equals(sortKey)) {
			return ROHSMaterial.LIFE_CYCLE_STATE;
		} else if ("creator".equals(sortKey)) {
			return ROHSMaterial.CREATOR_FULL_NAME;
		} else if ("createDate".equals(sortKey)) {
			return ROHSMaterial.CREATE_TIMESTAMP;
		} else if ("modifyDate".equals(sortKey)) {
			return ROHSMaterial.MODIFY_TIMESTAMP;
		}
		return ROHSMaterial.CREATE_TIMESTAMP;
	}

	public JSONArray include_RohsView(String oid, String module, String roleType) throws Exception {
		List<RohsData> list = null;
		try {
			if (oid.length() > 0) {
				if ("rohs".equals(module)) {
					ROHSMaterial rohs = (ROHSMaterial) CommonUtil.getObject(oid);
					list = RohsHelper.manager.getRepresentToLinkList(rohs, roleType);
				} else if ("part".equals(module)) {
					WTPart part = (WTPart) CommonUtil.getObject(oid);
					list = RohsHelper.manager.getPartToROHSList(part);
				} else {
					list = new ArrayList<RohsData>();
				}
			} else {
				list = new ArrayList<RohsData>();
			}
			// System.out.println("include_RohsView ObjectComarator START =" + list.size());
			Collections.sort(list, new ObjectComarator());
			// System.out.println("include_RohsView ObjectComarator end");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONArray.fromObject(list);
	}

	public Map<String, Object> file(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<RohsData> list = new ArrayList<>();

		String fileType = StringUtil.checkNull((String) params.get("fileType"));
		String publicationFrom = StringUtil.checkNull((String) params.get("publicationFrom"));
		String publicationTo = StringUtil.checkNull((String) params.get("publicationTo"));
		String fileName = StringUtil.checkNull((String) params.get("fileName"));
		String sortKey = (String) params.get("sortKey");
		String sortType = (String) params.get("sortType");

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(ROHSContHolder.class, true);
		int idx2 = query.addClassList(ROHSMaterial.class, false);

		QuerySpecUtils.toInnerJoin(query, ROHSContHolder.class, ROHSMaterial.class, "rohsReference.key.id",
				"thePersistInfo.theObjectIdentifier.id", idx, idx2);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[] { idx2 });

		QuerySpecUtils.toLatest(query, idx2, ROHSMaterial.class);

		QuerySpecUtils.toEqualsAnd(query, idx, ROHSContHolder.class, ROHSContHolder.FILE_TYPE, fileType);
		QuerySpecUtils.toLikeAnd(query, idx, ROHSContHolder.class, ROHSContHolder.FILE_NAME, fileName.toUpperCase());
		if (publicationFrom.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(ROHSContHolder.class, ROHSContHolder.PUBLICATION_DATE,
					SearchCondition.GREATER_THAN_OR_EQUAL, publicationFrom), new int[] { idx });
		}
		if (publicationTo.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(ROHSContHolder.class, ROHSContHolder.PUBLICATION_DATE,
					SearchCondition.LESS_THAN_OR_EQUAL, publicationTo), new int[] { idx });
		}
		QuerySpecUtils.toOrderBy(query, idx, ROHSContHolder.class, ROHSContHolder.ROHS_REFERENCE + ".key.id", false);

		boolean sort = QuerySpecUtils.toSort(sortType);
		QuerySpecUtils.toOrderBy(query, idx, ROHSContHolder.class, toSortKey(sortKey), sort);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ROHSContHolder holder = (ROHSContHolder) obj[0];
			RoHSHolderData data = new RoHSHolderData(holder);
			RohsData rData = new RohsData(data.getRohs());
			rData.setFileName(data.getFileName());
			list.add(rData);
			rData.setRowNum(rowNum++);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());

		return map;
	}

	public Map<String, Object> part(Map<String, Object> params) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String, Object>> partRohslist = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> partlist = new ArrayList<Map<String, Object>>();

		String partOid = StringUtil.checkNull((String) params.get("partOid"));
		if (partOid == "") {
			returnMap.put("partRohslist", partRohslist);
			return returnMap;
		}
		WTPart part = (WTPart) CommonUtil.getObject(partOid);
		if (part == null) {
			returnMap.put("partRohslist", partRohslist);
			return returnMap;
		}

		Map<String, Object> productStateMap = RohsUtil.getProductRoHsState(partOid);
		returnMap.put("totalState", productStateMap.get("totalState"));
		returnMap.put("passCount", productStateMap.get("passCount"));
		returnMap.put("totalCount", productStateMap.get("totalCount"));
		returnMap.put("greenCount", productStateMap.get("greenCount"));
		returnMap.put("blackCount", productStateMap.get("blackCount"));
		returnMap.put("dumyCount", productStateMap.get("dumyCount"));
		returnMap.put("continueCount", productStateMap.get("continueCount"));
		returnMap.put("redCount", productStateMap.get("redCount"));
		returnMap.put("orangeCount", productStateMap.get("orangeCount"));
		returnMap.put("listCount", productStateMap.get("listCount"));
		returnMap.put("isDumy_SonPartsCount", productStateMap.get("isDumy_SonPartsCount"));

		partlist = childPartPutMap(part, partlist, 0);
		HashMap<String, Integer> stateMap = new HashMap<String, Integer>();

		int totalLevl = 0;
		int green = 0;
		int red = 0;
		for (int i = 0; i < partlist.size(); i++) {
			Map<String, Object> partMap = partlist.get(i);

			String oid = (String) partMap.get("partOid");
			int level = (Integer) partMap.get("level");

			if (totalLevl < level) {
				totalLevl = level;
			}
			WTPart supart = (WTPart) CommonUtil.getObject(oid);

			List<RohsData> rohslist = getPartROHSList(supart);

			stateMap = RohsUtil.getRohsState(stateMap, oid);
			int rohsState = stateMap.get(oid);
			String state = "검은색";
			if (rohsState == RohsUtil.STATE_NOT_APPROVED) {
				state = "빨강색";
				red++;
			} else if (rohsState == RohsUtil.STATE_NONE_ROHS) {
				state = "주황색";
			} else if (rohsState == RohsUtil.STATE_ALL_APPROVED) {
				state = "녹색";
				green++;
			} else if (rohsState == RohsUtil.STATE_NOT_ROHS) {
				state = "검은색";
			}
			if (rohslist.size() > 0) {
				for (RohsData rohsData : rohslist) {
					ROHSMaterial material = (ROHSMaterial) CommonUtil.getObject(rohsData.getOid());
					List<ROHSContHolder> holderList = getROHSContHolder(material);

					if (holderList.size() > 0) {
						for (ROHSContHolder rohsHolder : holderList) {
							Map<String, Object> partRohsMap = new HashMap<String, Object>();
							partRohsMap.put("rohsNumber", rohsData.getNumber());
							partRohsMap.put("rohsName", rohsData.getName());
							partRohsMap.put("rohsState", rohsState);
							partRohsMap.put("rohsStateName", state);
							partRohsMap.put("rohslifeState", rohsData.getStateDisplay());
							partRohsMap.put("fileName", rohsHolder.getFileName());
							partRohsMap.put("docType", RohsUtil.getRohsDocTypeName(rohsHolder.getFileType()));
							partRohsMap = setPartROHMap(partRohsMap, partMap);
							partRohslist.add(partRohsMap);
						}
					} else {
						Map<String, Object> partRohsMap = new HashMap<String, Object>();
						partRohsMap.put("rohsNumber", rohsData.getNumber());
						partRohsMap.put("rohsName", rohsData.getName());
						partRohsMap.put("rohsState", rohsState);
						partRohsMap.put("rohslifeState", rohsData.getStateDisplay());
						partRohsMap.put("rohsStateName", state);
						partRohsMap = setPartROHMap(partRohsMap, partMap);
						partRohslist.add(partRohsMap);
					}
				}
			} else {
				Map<String, Object> partRohsMap = new HashMap<String, Object>();
				partRohsMap.put("rohsState", rohsState);
				partRohsMap.put("rohsStateName", state);
				partRohsMap = setPartROHMap(partRohsMap, partMap);
				partRohslist.add(partRohsMap);
			}

		}

		// 정렬
//		Collections.sort(partRohslist, new RohsPartComparator());

		returnMap.put("total", partRohslist.size());
		returnMap.put("green", green);
		returnMap.put("red", red);

		returnMap.put("totalLevel", totalLevl);
		returnMap.put("partRohslist", partRohslist);
		return returnMap;
	}

	private Map<String, Object> setPartROHMap(Map<String, Object> partRohsMap, Map<String, Object> partMap) {
		partRohsMap.put("partOid", partMap.get("partOid"));
		partRohsMap.put("partNumber", partMap.get("partNumber"));
		partRohsMap.put("partName", partMap.get("partName"));
		partRohsMap.put("partCreator", partMap.get("partCreateDate"));
		partRohsMap.put("partState", partMap.get("partState"));
		partRohsMap.put("level", partMap.get("level"));
		int level = (Integer) partMap.get("level");
		partRohsMap.put("L" + level, level);
		return partRohsMap;
	}

	public Map<String, Object> product(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> partList = (ArrayList<Map<String, Object>>) params.get("partList");
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		if (partList != null) {
			for (Map<String, Object> part : partList) {
				String partOid = (String) part.get("part_oid");
				WTPart wtpart = (WTPart) CommonUtil.getObject(partOid);
				PartDTO dto = new PartDTO(wtpart);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("number", dto.getNumber());
				map.put("name", dto.getName());
				map.put("creator", dto.getCreator());
				map.put("createDate", dto.getCreateDate());
				map.put("state", dto.getState());
				Map<String, Object> dataMap = RohsUtil.getProductRoHsState(partOid);
				Double totalState = (Double) dataMap.get("totalState");
				Double totalCount = (Double) dataMap.get("totalCount");
				Double passCount = (Double) dataMap.get("passCount");
				String rohsState = totalState + " % (" + passCount + "/" + totalCount + ")";
				map.put("rohsState", rohsState);
				list.add(map);
			}
		}
		result.put("list", list);
		return result;
	}

	/**
	 * 물질 전체 리스트 가져오기
	 */
	public ArrayList<RohsData> totalList() throws Exception {
		ArrayList<RohsData> list = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(ROHSMaterial.class, true);
		ReferenceFactory rf = new ReferenceFactory();

		try {
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				RohsData data = new RohsData((ROHSMaterial) obj[0]);
				list.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ROHSMaterial getRohs(String name) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(ROHSMaterial.class, true);
		QuerySpecUtils.toEquals(query, idx, ROHSMaterial.class, ROHSMaterial.NAME, name);
		QueryResult result = PersistenceHelper.manager.find(query);
		ROHSMaterial rohs = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			rohs = (ROHSMaterial) obj[0];
		}
		return rohs;
	}

	public List<Map<String, Object>> getRohsContent(String oid) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ROHSMaterial rohs = (ROHSMaterial) CommonUtil.getObject(oid);
		List<ROHSContHolder> holderList = RohsHelper.manager.getROHSContHolder(rohs);
		for (ROHSContHolder holder : holderList) {
			ApplicationData data = holder.getApp();
			String url = "/Windchill/plm/content/download?oid=" + CommonUtil.getOIDString(data);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("fileDown", url);
			map.put("fileType", RohsUtil.getRohsDocTypeName(holder.getFileType()));
			map.put("fileDate", holder.getPublicationDate());
			map.put("fileAppOid", CommonUtil.getOIDString(data));
			map.put("fileOid", CommonUtil.getOIDString(holder));
			map.put("fileRole", data.getRole().toString());
			map.put("fileTypeCode", holder.getFileType());
			map.put("fileName", holder.getFileName());
			list.add(map);
		}
		return list;
	}

	public int rohsCheck(Map<String, Object> params) throws Exception {
		String rohsName = StringUtil.checkNull((String) params.get("rohsName"));
		String rohsNumber = StringUtil.checkNull((String) params.get("rohsNumber"));
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(ROHSMaterial.class, true);
		if (!"".equals(rohsName)) {
			QuerySpecUtils.toEquals(query, idx, ROHSMaterial.class, ROHSMaterial.NAME, rohsName);
		}
		if (!"".equals(rohsNumber)) {
			QuerySpecUtils.toEquals(query, idx, ROHSMaterial.class, ROHSMaterial.NUMBER, rohsNumber);
		}
		QueryResult result = PersistenceHelper.manager.find(query);
		int count = 0;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ROHSMaterial rohs = (ROHSMaterial) obj[0];
			count++;
		}
		return count;
	}

	public List<Map<String, String>> rohsFileType() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String[] fileCode = AttributeKey.RohsKey.ROHS_CODE;
		String[] fileName = AttributeKey.RohsKey.ROHS_NAME;

		for (int i = 0; i < fileCode.length; i++) {
			Map<String, String> map = new HashMap<String, String>();

			map.put("code", fileCode[i]);
			map.put("name", fileName[i]);

			list.add(map);
		}
		return list;
	}

	public String rohsNameCheck(Map<String, Object> params) throws Exception {
		ArrayList<String> list = (ArrayList<String>) params.get("list");
		String duplicate = "";
		for (String rohsName : list) {
			QuerySpec query = new QuerySpec();
			int idx = query.addClassList(ROHSMaterial.class, true);
			QuerySpecUtils.toEquals(query, idx, ROHSMaterial.class, ROHSMaterial.NAME, rohsName);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ROHSMaterial rohs = (ROHSMaterial) obj[0];
				return rohs.getName();
			}
		}

		return duplicate;
	}

	public List<PartDTO> getROHSToPartList(ROHSMaterial rohs) throws Exception {
		return getROHSToPartList(rohs, CommonUtil.isLatestVersion(CommonUtil.getOIDString(rohs)));
	}

	public List<PartDTO> getROHSToPartList(ROHSMaterial rohs, boolean islastversion) throws Exception {
		List<PartDTO> list = new ArrayList<PartDTO>();
		QueryResult result = PersistenceHelper.manager.navigate(rohs, "part", PartToRohsLink.class);
		while (result.hasMoreElements()) {
			WTPart part = (WTPart) result.nextElement();
			PartDTO dto = new PartDTO(part);
			list.add(dto);
		}
		return list;
	}

	public ROHSContHolder getRohsContHolder(ROHSMaterial rohs) throws Exception {
		ROHSContHolder ch = null;
		QuerySpec qs = new QuerySpec();
		int idx = qs.addClassList(ROHSContHolder.class, true);
		qs.appendWhere(new SearchCondition(ROHSContHolder.class, "rohsReference.key.id", SearchCondition.EQUAL,
				CommonUtil.getOIDLongValue(rohs)), new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(qs);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ch = (ROHSContHolder) obj[0];
			return ch;
		}
		return ch;
	}

	public List<ROHSContHolder> getROHSContHolder(ROHSMaterial rohs) throws Exception {
		List<ROHSContHolder> list = new ArrayList<ROHSContHolder>();

		QuerySpec qs = new QuerySpec();
		int idx = qs.addClassList(ROHSContHolder.class, true);

		qs.appendWhere(new SearchCondition(ROHSContHolder.class, "rohsReference.key.id", SearchCondition.EQUAL,
				CommonUtil.getOIDLongValue(rohs)), new int[] { idx });

		QueryResult rt = PersistenceHelper.manager.find(qs);

		while (rt.hasMoreElements()) {

			Object[] oo = (Object[]) rt.nextElement();
			ROHSContHolder rholder = (ROHSContHolder) oo[0];
			list.add(rholder);
		}

		return list;
	}

	public List<RohsData> getPartROHSList(WTPart part) throws Exception {
		List<RohsData> list = new ArrayList<RohsData>();

		QueryResult result = PersistenceHelper.manager.navigate(part, "rohs", PartToRohsLink.class);
		while (result.hasMoreElements()) {
			ROHSMaterial rohs = (ROHSMaterial) result.nextElement();
			RohsData data = new RohsData(rohs);
			list.add(data);
		}
		return list;
	}

	public List<RohsData> getPartROHSList(WTPart part, boolean islastversion) throws Exception {
		QuerySpec qs = new QuerySpec();
		int idx1 = qs.addClassList(ROHSMaterial.class, true);
//        int idx2 = qs.addClassList(PartToRohsLink.class, true);

		if (qs.getConditionCount() > 0) {
			qs.appendAnd();
		}
		qs.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[] { idx1 });

		if (islastversion) {
			QuerySpecUtils.toLatest(qs, idx1, ROHSMaterial.class);
		}
		QuerySpecUtils.toOrderBy(qs, idx1, ROHSMaterial.class, ROHSMaterial.MODIFY_TIMESTAMP, false);

		QueryResult rt = PersistenceHelper.manager.navigate(part, "rohs", qs, true);
		List<RohsData> list = new ArrayList<RohsData>();
		while (rt.hasMoreElements()) {
			ROHSMaterial rohs = (ROHSMaterial) rt.nextElement();
			RohsData data = new RohsData(rohs);

			list.add(data);
		}
		return list;
	}

	public List<PartToRohsLink> getPartToRohsLinkList(RevisionControlled rev) throws Exception {
		List<PartToRohsLink> list = new ArrayList<PartToRohsLink>();
		String vr = CommonUtil.getVROID(rev);
		rev = (RevisionControlled) CommonUtil.getObject(vr);
		String roleType = "rohs";
		if (rev instanceof ROHSMaterial) {
			roleType = "part";
		}

		QueryResult rt = PersistenceHelper.manager.navigate(rev, roleType, PartToRohsLink.class, false);

		while (rt.hasMoreElements()) {
			PartToRohsLink link = (PartToRohsLink) rt.nextElement();
			list.add(link);
		}
		return list;
	}

	public List<RepresentToLink> getRepresentLink(ROHSMaterial rohs) throws Exception {
		List<RepresentToLink> list = new ArrayList<RepresentToLink>();
		String vr = CommonUtil.getVROID(rohs);
		rohs = (ROHSMaterial) CommonUtil.getObject(vr);

		QueryResult rt = PersistenceHelper.manager.navigate(rohs, "composition", RepresentToLink.class, false);

		while (rt.hasMoreElements()) {
			RepresentToLink link = (RepresentToLink) rt.nextElement();
			list.add(link);
		}
		return list;
	}

	public boolean duplicateNumber(String partOid, String rohsNumber) throws Exception {
		boolean isDuble = false;
		QuerySpec qs = new QuerySpec();
		int idx = qs.addClassList(PartToRohsLink.class, true);
		qs.appendWhere(new SearchCondition(PartToRohsLink.class, "roleAObjectRef.key.branchId", SearchCondition.EQUAL,
				CommonUtil.getOIDLongValue(partOid)));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			PartToRohsLink link = (PartToRohsLink) obj[0];
			ROHSMaterial rohs = (ROHSMaterial) link.getRoleBObject();
			// System.out.println("rohs.getNumber() =" + rohs.getNumber()
			// +",rohsNumber="+rohsNumber + "===="+(rohs.getNumber().equals(rohsNumber)));
			if (rohs.getNumber().equals(rohsNumber)) {
				isDuble = true;
			}
		}
		return isDuble;
	}

	public List<RohsData> getRepresentToLinkList(ROHSMaterial rohs, String roleType) throws Exception {
		// composition 구성,represent 대표
		List<RohsData> list = new ArrayList<RohsData>();
		String vr = CommonUtil.getVROID(rohs);
		rohs = (ROHSMaterial) CommonUtil.getObject(vr);

		QueryResult rt = PersistenceHelper.manager.navigate(rohs, roleType, RepresentToLink.class, true);
		while (rt.hasMoreElements()) {
			ROHSMaterial rohsMaterial = (ROHSMaterial) rt.nextElement();
			RohsData data = new RohsData(rohsMaterial);
			list.add(data);
		}
		return list;
	}

	public List<RohsData> getPartToROHSList(WTPart part) throws Exception {
		boolean islastversion = CommonUtil.isLatestVersion(part);

		QuerySpec qs = new QuerySpec();
		int idx1 = qs.addClassList(ROHSMaterial.class, true);
		int idx2 = qs.addClassList(PartToRohsLink.class, true);

		if (qs.getConditionCount() > 0) {
			qs.appendAnd();
		}
		qs.appendWhere(VersionControlHelper.getSearchCondition(ROHSMaterial.class, true), new int[] { idx1 });

		if (islastversion) {
			QuerySpecUtils.toLatest(qs, idx1, ROHSMaterial.class);
		}
		QuerySpecUtils.toOrderBy(qs, idx1, ROHSMaterial.class, ROHSMaterial.NUMBER, false);

		QueryResult rt = PersistenceHelper.manager.navigate(part, "rohs", qs, true);
		List<RohsData> list = new ArrayList<RohsData>();
		while (rt.hasMoreElements()) {
			ROHSMaterial rohs = (ROHSMaterial) rt.nextElement();
			RohsData data = new RohsData(rohs);

			list.add(data);
		}
		return list;
	}

	public List<Map<String, Object>> childPartPutMap(WTPart part, List<Map<String, Object>> list, int level)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// boolean isChildCheck = isChildPartCheck(part);
		if (PartUtil.isChange(part.getNumber())) {
			return list;
		}

		map.put("partOid", part.getPersistInfo().getObjectIdentifier().toString());
		map.put("partNumber", part.getNumber());
		map.put("partName", part.getName());
		map.put("partCreator", part.getCreatorFullName());
		map.put("partCreateDate",
				DateUtil.subString(DateUtil.getDateString(part.getPersistInfo().getCreateStamp(), "a"), 0, 10));
		map.put("partState", part.getLifeCycleState().getDisplay(Message.getLocale()));
		map.put("level", level);
		map.put("Level" + level, level);

		list.add(map);

		QueryResult result = isChildPart(part, true);
		List<WTPart> tempList = new ArrayList<WTPart>();
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			WTPartMaster childPartMaster = (WTPartMaster) o[0];
			WTPart childPart = PartHelper.service.getPart(childPartMaster.getNumber());// getWTPartFormWTPartMaster(childPartMaster);
			if (PartUtil.isChange(childPart.getNumber())) {
				continue;
			}
			tempList.add(childPart);
			/*
			 * if(childPart != null){ childPartPutMap(childPart, list,level+1); }
			 */
		}

		Collections.sort(tempList, new ObjectComarator());
		for (WTPart childPart : tempList) {
			if (childPart != null) {
				childPartPutMap(childPart, list, level + 1);
			}
		}
		return list;
	}

	private QueryResult isChildPart(WTPart part, boolean isData) throws Exception {
		QuerySpec spec = new QuerySpec();

		int idx_master = spec.addClassList(WTPartMaster.class, isData);
		int idx = spec.addClassList(WTPartUsageLink.class, false);
		int idx_part = spec.addClassList(WTPart.class, false);
		spec.setAdvancedQueryEnabled(true);

		ClassAttribute idx_l = new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id");
		ClassAttribute idx_p = new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id");

		spec.appendWhere(new SearchCondition(idx_l, SearchCondition.EQUAL, idx_p), new int[] { idx, idx_part });

		spec.appendAnd();
		ClassAttribute idx_l2 = new ClassAttribute(WTPartUsageLink.class, "roleBObjectRef.key.id");
		ClassAttribute idx_m = new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id");

		spec.appendWhere(new SearchCondition(idx_l2, SearchCondition.EQUAL, idx_m), new int[] { idx, idx_master });

		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { idx_part });

		spec.appendJoin(idx, "roleA", part);

		if (!isData) {
			spec.appendGroupBy(new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"), idx_part,
					true);
		}

		spec.appendOrderBy(new OrderBy(new ClassAttribute(WTPartMaster.class, WTPartMaster.NUMBER), true),
				new int[] { idx_master });

		// System.out.println(spec.toString());
		QueryResult result = PersistenceServerHelper.manager.query(spec);

		return result;
	}

	/**
	 * 관련 객체 불러오기 메서드
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		ROHSMaterial rohs = (ROHSMaterial) CommonUtil.getObject(oid);
		if ("rohs".equalsIgnoreCase(type)) {
			// 물질
			return JSONArray.fromObject(referenceRohs(rohs, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 ROHS
	 */
	private Object referenceRohs(ROHSMaterial rohs, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(rohs, "composition", RepresentToLink.class);
		while (result.hasMoreElements()) {
			ROHSMaterial ref = (ROHSMaterial) result.nextElement();
			RohsData data = new RohsData(ref);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return list;
	}

	/**
	 * rohs 이력
	 */
	public JSONArray allIterationsOf(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		ROHSMaterial rohs = (ROHSMaterial) CommonUtil.getObject(oid);
		QueryResult result = VersionControlHelper.service.allIterationsOf(rohs.getMaster());
		while (result.hasMoreElements()) {
			ROHSMaterial r = (ROHSMaterial) result.nextElement();
			Map<String, String> map = new HashMap<>();
			RohsData dto = new RohsData(r);
			map.put("oid", dto.getOid());
			map.put("name", dto.getName());
			map.put("number", dto.getNumber());
			map.put("version", dto.getVersion());
			map.put("creator", dto.getCreator());
			map.put("createdDate", dto.getCreateDate());
			map.put("modifier", dto.getModifier());
			map.put("modifiedDate", dto.getModifyDate());
			map.put("note", r.getIterationNote());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 최신버전
	 */
	public ROHSMaterial latest(String oid) throws Exception {
		ROHSMaterial rohs = (ROHSMaterial) CommonUtil.getObject(oid);
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(rohs.getMaster(), config);
		if (result.hasMoreElements()) {
			ROHSMaterial latest = (ROHSMaterial) result.nextElement();
			return latest;
		}
		return null;
	}

	/**
	 * ROHS 번호 검증
	 */
	public ROHSMaterial validateRohsNumber(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ROHSMaterial.class, true);
		QuerySpecUtils.toEquals(query, idx, ROHSMaterial.class, ROHSMaterial.NUMBER, number);
		QuerySpecUtils.toLatest(query, idx, ROHSMaterial.class);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			ROHSMaterial rohs = (ROHSMaterial) obj[0];
			return rohs;
		}
		return null;
	}

	/**
	 * 품목번호 검증
	 */
	public WTPart validatePartNumber(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toEquals(query, idx, WTPart.class, WTPart.NUMBER, number);
		QuerySpecUtils.toLatest(query, idx, WTPart.class);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			WTPart part = (WTPart) obj[0];
			return part;
		}
		return null;
	}

	/**
	 * 링크 등록 중복 체크
	 */
	public Map<String, Object> checker(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) params.get("data");
		for (Map<String, String> map : data) {
			String partNumber = map.get("partNumber");
			String rohsNumber = map.get("rohsNumber");
			// 화면에서 체크 하고 오니간..
			WTPart part = validatePartNumber(partNumber);
			QueryResult qr = PersistenceHelper.manager.navigate(part, "rohs", PartToRohsLink.class);
			while (qr.hasMoreElements()) {
				ROHSMaterial rohs = (ROHSMaterial) qr.nextElement();
				if (rohs.getNumber().equals(rohsNumber)) {
					String msg = partNumber + "와 " + rohsNumber + "는 이미 연관관계가 되어 있습니다.";
					result.put("msg", msg);
					result.put("checker", true);
					return result;
				}
			}
		}
		result.put("checker", false);
		return result;
	}

	public Map<String, Object> excelList() throws Exception {
		Map<String, Object> result = new HashMap<>();

		String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
		String path = WTProperties.getServerProperties().getProperty("wt.temp");

		File orgFile = new File(wtHome + "/codebase/com/e3ps/rohs/dto/rohs-list.xlsx");

		File newFile = CommonUtil.copyFile(orgFile, new File(path + "/ROHS 리스트.xlsx"));

		Workbook workbook = new Workbook(newFile.getPath());
		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.setName("ROHS 리스트"); // 시트 이름

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ROHSMaterial.class, true);

		QuerySpecUtils.toLatest(query, idx, ROHSMaterial.class);
		QuerySpecUtils.toOrderBy(query, idx, ROHSMaterial.class, ROHSMaterial.CREATE_TIMESTAMP, false);
		QueryResult qr = PersistenceHelper.manager.find(query);

		int rowIndex = 1;

		Style center = workbook.createStyle();
		center.setHorizontalAlignment(TextAlignmentType.CENTER);

		Style left = workbook.createStyle();
		left.setHorizontalAlignment(TextAlignmentType.LEFT);

		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			ROHSMaterial rohs = (ROHSMaterial) obj[0];
			RohsData dd = new RohsData(rohs);

			Cell rowCell = worksheet.getCells().get(rowIndex, 0);
			rowCell.setStyle(center);
			rowCell.putValue(rowIndex);

			Cell numberCell = worksheet.getCells().get(rowIndex, 1);
			numberCell.setStyle(center);
			numberCell.putValue(dd.getNumber());

			Cell manufactureCell = worksheet.getCells().get(rowIndex, 2);
			manufactureCell.setStyle(center);
			manufactureCell.putValue(dd.getManufactureDisplay());

			Cell nameCell = worksheet.getCells().get(rowIndex, 3);
			nameCell.setStyle(left);
			nameCell.putValue(dd.getName());

			Cell revCell = worksheet.getCells().get(rowIndex, 4);
			revCell.setStyle(center);
			revCell.putValue(dd.getVersion());

			Cell stateCell = worksheet.getCells().get(rowIndex, 5);
			stateCell.setStyle(center);
			stateCell.putValue(dd.getStateDisplay());

			Cell creatorCell = worksheet.getCells().get(rowIndex, 6);
			creatorCell.setStyle(center);
			creatorCell.putValue(dd.getCreator());

			Cell createdDateCell = worksheet.getCells().get(rowIndex, 7);
			createdDateCell.setStyle(center);
			createdDateCell.putValue(dd.getCreateDate());

			Cell modifyCell = worksheet.getCells().get(rowIndex, 8);
			modifyCell.setStyle(center);
			modifyCell.putValue(dd.getModifyDate());
			rowIndex++;
		}

		String fullPath = path + "/ROHS 리스트.xlsx";
		workbook.save(fullPath);
		result.put("name", newFile.getName());
		return result;
	}
}
