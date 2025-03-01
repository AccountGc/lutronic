package com.e3ps.drawing.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import com.aspose.cells.Cell;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.drawing.EpmLocation;
import com.e3ps.drawing.beans.EpmData;
import com.e3ps.drawing.column.EpmColumn;
import com.e3ps.part.PartLocation;
import com.e3ps.part.service.PartHelper;
import com.ptc.wvs.server.util.PublishUtils;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.HolderToContent;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.FloatValue;
import wt.iba.value.StringValue;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.services.ServiceFactory;
import wt.util.FileUtil;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.viewmarkup.DerivedImage;

public class DrawingHelper {
	public static final DrawingService service = ServiceFactory.getService(DrawingService.class);
	public static final DrawingHelper manager = new DrawingHelper();
	public static final String PART_ROOT = "/Default/PART_Drawing";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		long start = System.currentTimeMillis() / 1000;
		System.out.println("도면 쿼리 시작 = " + start);
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EPMDocument.class, false);

		query.appendSelect(new ClassAttribute(EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { idx }, false);

		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<EpmColumn> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();

		boolean latest = (boolean) params.get("latest");
		String foid = StringUtil.checkNull((String) params.get("foid"));
		String location = StringUtil.checkNull((String) params.get("location"));
		String cadDivision = StringUtil.checkNull((String) params.get("cadDivision"));
		String cadType = StringUtil.checkNull((String) params.get("cadType"));
		String number = StringUtil.checkNull((String) params.get("number"));
		String name = StringUtil.checkNull((String) params.get("name"));
		String predate = StringUtil.checkNull((String) params.get("predate"));
		String postdate = StringUtil.checkNull((String) params.get("postdate"));
		String predate_modify = StringUtil.checkNull((String) params.get("predate_modify"));
		String postdate_modify = StringUtil.checkNull((String) params.get("postdate_modify"));
		String creator = StringUtil.checkNull((String) params.get("creatorOid"));
		String state = StringUtil.checkNull((String) params.get("state"));
		String autoCadLink = StringUtil.checkNull((String) params.get("autoCadLink"));
		String unit = StringUtil.checkNull((String) params.get("unit"));
		String model = StringUtil.checkNull((String) params.get("modelcode"));
		String productmethod = StringUtil.checkNull((String) params.get("productmethod"));
		String deptcode = StringUtil.checkNull((String) params.get("deptcode"));
		String weight1 = StringUtil.checkNull((String) params.get("weight1"));
		String weight2 = StringUtil.checkNull((String) params.get("weight2"));
		String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
		String mat = StringUtil.checkNull((String) params.get("mat"));
		String finish = StringUtil.checkNull((String) params.get("finish"));
		String remarks = StringUtil.checkNull((String) params.get("remarks"));
		String specification = StringUtil.checkNull((String) params.get("specification"));
		String createdFrom = StringUtil.checkNull((String) params.get("createdFrom"));
		String createdTo = StringUtil.checkNull((String) params.get("createdTo"));
		String modifiedFrom = StringUtil.checkNull((String) params.get("modifiedFrom"));
		String modifiedTo = StringUtil.checkNull((String) params.get("modifiedTo"));
		boolean checkout = (boolean) params.get("checkout");
		// 정렬
		String sortKey = (String) params.get("sortKey");
		String sortType = (String) params.get("sortType");

		if (!StringUtil.checkString(location)) {
			location = PART_ROOT;
		}
		if (StringUtil.checkString(location)) {
			int l = location.indexOf(PART_ROOT);

			if (l >= 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				location = location.substring((l + PART_ROOT.length()));
				// Folder Search
				int folder_idx = query.addClassList(EpmLocation.class, false);
				query.appendWhere(new SearchCondition(EpmLocation.class, EpmLocation.EPM, EPMDocument.class,
						"thePersistInfo.theObjectIdentifier.id"), new int[] { folder_idx, idx });
				query.appendAnd();

				query.appendWhere(new SearchCondition(EpmLocation.class, "loc", SearchCondition.LIKE, location + "%"),
						new int[] { folder_idx });
			}
		}

//		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
////		int isQuery = DOCUMENT_ROOT.indexOf(location);
//		if (query.getConditionCount() > 0) {
//			query.appendAnd();
//		}

//		if (isQuery < 0) {
//		int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
//		ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
//		SearchCondition fsc = new SearchCondition(fca, "=",
//				new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
//		fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
//		fsc.setOuterJoin(0);
//		query.appendWhere(fsc, new int[] { f_idx, idx });
//		query.appendAnd();

//		long fid = folder.getPersistInfo().getObjectIdentifier().getId();
//		query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
//				new int[] { f_idx });
//		}

//		query.appendOpenParen();
//		long fid = folder.getPersistInfo().getObjectIdentifier().getId();
//		query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
//				new int[] { f_idx });
//
//		ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
//		for (int i = 0; i < folders.size(); i++) {
//			Folder sub = (Folder) folders.get(i);
//			query.appendOr();
//			long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
//			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
//					new int[] { f_idx });
//		}
//		query.appendCloseParen();

		// Working Copy 제외
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(
				new SearchCondition(EPMDocument.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false),
				new int[] { idx });

		QuerySpecUtils.toLikeAnd(query, idx, EPMDocument.class, EPMDocument.NUMBER, number);
		QuerySpecUtils.toLikeAnd(query, idx, EPMDocument.class, EPMDocument.NAME, name);
		QuerySpecUtils.creatorQuery(query, idx, EPMDocument.class, creator);
		QuerySpecUtils.toState(query, idx, EPMDocument.class, state);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EPMDocument.class, EPMDocument.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EPMDocument.class, EPMDocument.MODIFY_TIMESTAMP, modifiedFrom,
				modifiedTo);

		// 프로젝트 코드
		if (model.length() > 0) {

			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MODEL);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + model + "%").toUpperCase()), new int[] { _idx });
			}

		} else {
			model = "";
		}

		// 제작방법
		if (productmethod.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_PRODUCTMETHOD);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + productmethod + "%").toUpperCase()), new int[] { _idx });
			}
		} else {
			productmethod = "";
		}

		// 부서
		if (deptcode.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_DEPTCODE);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + deptcode + "%").toUpperCase()), new int[] { _idx });
			}
		} else {
			deptcode = "";
		}

		// 무게
		boolean searchWeight = weight1.length() > 0 || weight2.length() > 0;
		// System.out.println("weight1 =" + weight1 +",weight2=" + weight2);
		if (searchWeight) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_WEIGHT);
			if (aview != null) {

				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendOpenParen();

				// System.out.println("aview.getHierarchyID() =" + aview.getHierarchyID());
				int _idx = query.appendClassList(FloatValue.class, false);
				query.appendWhere(new SearchCondition(FloatValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(FloatValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });

				if (weight1.length() > 0) {
					query.appendAnd();
					query.appendWhere(new SearchCondition(FloatValue.class, "value",
							SearchCondition.GREATER_THAN_OR_EQUAL, Double.parseDouble(weight1)), new int[] { _idx });
				}

				if (weight2.length() > 0) {
					query.appendAnd();
					query.appendWhere(new SearchCondition(FloatValue.class, "value", SearchCondition.LESS_THAN_OR_EQUAL,
							Double.parseDouble(weight2)), new int[] { _idx });
				}
				query.appendCloseParen();
			}
		}

		// MANUFACTURE
		if (manufacture.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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

		// 재질
		if (mat.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MAT);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + mat + "%").toUpperCase()), new int[] { _idx });
			}
		} else {
			mat = "";
		}

		// 후처리
		if (finish.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_FINISH);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + finish + "%").toUpperCase()), new int[] { _idx });
			}
		} else {
			finish = "";
		}

		// 비고
		if (remarks.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_REMARKS);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + remarks + "%").toUpperCase()), new int[] { _idx });
			}
		} else {
			remarks = "";
		}

		// 사양
		if (specification.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_SPECIFICATION);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + specification + "%").toUpperCase()), new int[] { _idx });
			}
		} else {
			specification = "";
		}

		if (cadDivision.length() > 0) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(EPMDocument.class, "master>authoringApplication",
					SearchCondition.EQUAL, cadDivision, false), new int[] { idx });
		}

		// CAD Ÿ��
		if (cadType.length() > 0) {
			query.appendAnd();
			query.appendWhere(
					new SearchCondition(EPMDocument.class, "master>docType", SearchCondition.EQUAL, cadType, false),
					new int[] { idx });
		}

		// autoCadLink
		if (autoCadLink.equals("true")) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			query.appendWhere(new SearchCondition(EPMDocument.class, "master>authoringApplication",
					SearchCondition.EQUAL, "SOLIDWORKS", false), new int[] { idx });
			query.appendAnd();
			query.appendOpenParen();
			query.appendWhere(new SearchCondition(EPMDocument.class, "master>docType", SearchCondition.EQUAL,
					"CADCOMPONENT", false), new int[] { idx });
			query.appendOr();
			query.appendWhere(new SearchCondition(EPMDocument.class, "master>docType", SearchCondition.EQUAL,
					"CADASSEMBLY", false), new int[] { idx });
			query.appendCloseParen();

		}
		if (checkout) {
			QuerySpecUtils.toEqualsAnd(query, idx, EPMDocument.class, "checkoutInfo.state", "c/o");
		}
		// 최신 이터레이션
		if (latest && !checkout) {
			QuerySpecUtils.toLatest(query, idx, EPMDocument.class);
		}

//			QueryResult result = PersistenceHelper.manager.find(query);
		boolean sort = QuerySpecUtils.toSort(sortType);
		QuerySpecUtils.toOrderBy(query, idx, EPMDocument.class, toSortKey(sortKey), sort);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			BigDecimal bd = (BigDecimal) obj[0];
			String oid = "wt.epm.EPMDocument:" + bd.longValue();
			EpmColumn column = new EpmColumn(oid);
			column.setRowNum(rowNum++);
			list.add(column);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		long end = System.currentTimeMillis() / 1000;
		System.out.println("도면 쿼리 종료 = " + end + ", 걸린 시간 = " + (end - start));
		return map;
	}

	private String toSortKey(String sortKey) throws Exception {
		if ("name".equals(sortKey)) {
			return EPMDocument.NAME;
		} else if ("cadType".equals(sortKey)) {
			return EPMDocument.DOC_TYPE;
		} else if ("number".equals(sortKey)) {
			return EPMDocument.NUMBER;
		} else if ("state".equals(sortKey)) {
			return EPMDocument.LIFE_CYCLE_STATE;
		} else if ("creator".equals(sortKey)) {
			return (EPMDocument.CREATOR + "." + WTAttributeNameIfc.REF_OBJECT_ID);
		} else if ("createdDate".equals(sortKey)) {
			return EPMDocument.CREATE_TIMESTAMP;
		} else if ("modifiedDate".equals(sortKey)) {
			return EPMDocument.MODIFY_TIMESTAMP;
		}
		return EPMDocument.CREATE_TIMESTAMP;
	}

	public JSONArray include_Reference(String oid, String moduleType) throws Exception {
		List<EpmData> list = new ArrayList<EpmData>();
		try {
			if (StringUtil.checkString(oid)) {
				if ("drawing".equals(moduleType)) {
					EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
					List<EPMReferenceLink> vecRef = EpmSearchHelper.service.getReferenceDependency(epm, "references");
					// System.out.println("StandardDrawingService ::: include_Refence.jsp :::
					// vecRef.size() = "+vecRef.size());
					for (EPMReferenceLink link : vecRef) {
						EPMDocumentMaster master = (EPMDocumentMaster) link.getReferences();
						EPMDocument epmdoc = EpmSearchHelper.service.getLastEPMDocument(master);
						EpmData ref = new EpmData(epmdoc);
						ref.setLinkRefernceType(link.getReferenceType().getDisplay(Message.getLocale()));
						list.add(ref);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONArray.fromObject(list);
	}

	public JSONArray include_ReferenceBy(String oid) throws Exception {
		List<EpmData> list = new ArrayList<EpmData>();
		try {
			if (StringUtil.checkString(oid)) {
				EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
				List<EPMReferenceLink> refList = EpmSearchHelper.service
						.getEPMReferenceList((EPMDocumentMaster) epm.getMaster());
				for (EPMReferenceLink link : refList) {
					EPMDocument epmdoc = link.getReferencedBy();
					EpmData data = new EpmData(epmdoc);
					data.setLinkRefernceType(link.getReferenceType().getDisplay(Message.getLocale()));

					list.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 상세보기 주도면 불러오기
	 */
	public JSONArray include_DrawingList(Map<String, Object> params) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String oid = (String) params.get("oid");
		String moduleType = (String) params.get("moduleType");
		String title = (String) params.get("title");
		String paramName = (String) params.get("paramName");
		String epmType = StringUtil.checkReplaceStr((String) params.get("epmType"), "");
		String distribute = StringUtil.checkNull((String) params.get("distribute"));

		try {
			Map<String, String> map = new HashMap<String, String>();

			map.put("title", title);
			map.put("paramName", paramName);
			map.put("distribute", distribute);
			if ("part".equals(moduleType)) {
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				boolean lastVer = CommonUtil.isLatestVersion(part);
				if ("main".equals(epmType)) {
					EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
					if (epm != null) {
						EpmData data = new EpmData(epm);

						map.put("number", data.getNumber());
						map.put("name", data.getName());
						map.put("state", data.getState());
//						map.put("version", data.getVersion());
						map.put("creator", data.getCreator());
//						map.put("description", data.getDescription());
					}
				} else {
					Vector<EPMDescribeLink> vecDesc = EpmSearchHelper.service.getEPMDescribeLink(part, lastVer);
					for (EPMDescribeLink link : vecDesc) {
						EPMDocument epmLink = (EPMDocument) link.getRoleBObject();
						EpmData data = new EpmData(epmLink);

						map.put("number", data.getNumber());
						map.put("name", data.getName());
						map.put("state", data.getState());
//						map.put("version", data.getVersion());
						map.put("creator", data.getCreator());
//						map.put("description", data.getDescription());
					}

				}
			} else if ("active".equals(moduleType)) {
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return JSONArray.fromObject(list);
	}

	public List<Map<String, String>> cadTypeList() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		EPMDocumentType[] appType = EPMDocumentType.getEPMDocumentTypeSet();

		for (EPMDocumentType type : appType) {
			if (!type.isSelectable())
				continue;
			Map<String, String> map = new HashMap<String, String>();
			map.put("code", type.toString());
			map.put("name", type.getDisplay(Message.getLocale()));
			list.add(map);
		}

		return list;
	}

	/**
	 * 문서 이력
	 */
	public JSONArray allIterationsOf(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
		QueryResult result = VersionControlHelper.service.allIterationsOf(epm.getMaster());
		while (result.hasMoreElements()) {
			EPMDocument d = (EPMDocument) result.nextElement();
			Map<String, String> map = new HashMap<>();
			EpmData data = new EpmData(d);
			map.put("oid", data.getOid());
			map.put("name", data.getName());
			map.put("number", data.getNumber());
			map.put("version", data.getVersion());
			map.put("creator", data.getCreator());
			map.put("state", data.getState());
			map.put("createdDate", data.getCreateDate());
			map.put("modifier", data.getModifier());
			map.put("modifiedDate", data.getModifyDate());
//			map.put("primary", data.getPrimary());
//			map.put("secondary", data.getSecondary());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 주도면 존재여부 (true : 존재, false : 존재x)
	 */
	public boolean isExist(ArrayList<Map<String, Object>> rows91) throws Exception {
		boolean isConnect = false;
		for (Map<String, Object> map : rows91) {
			String partNumber = (String) map.get("number");
			WTPart part = PartHelper.service.getPart(partNumber);
			EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
			if (epm != null) {
				isConnect = true;
			}
		}
		return isConnect;
	}

	/**
	 * 최신버전 도면
	 */
	public EPMDocument latest(EPMDocumentMaster master) throws Exception {
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(master, config);
		if (result.hasMoreElements()) {
			EPMDocument latest = (EPMDocument) result.nextElement();
			return latest;
		}
		return null;
	}

	/**
	 * 최신버전 도면
	 */
	public EPMDocument latest(String oid) throws Exception {
		EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(epm.getMaster(), config);
		if (result.hasMoreElements()) {
			EPMDocument latest = (EPMDocument) result.nextElement();
			return latest;
		}
		return null;
	}

	/**
	 * 도면과 연결된 품목 가져오는 함수
	 */
	public WTPart getWTPart(EPMDocument epm) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(epm, EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class);
//		System.out.println("부품과 연결된 도면 = " + qr.size());
		if (qr.hasMoreElements()) {
			WTPart p = (WTPart) qr.nextElement();
			return p;
		}
		return null;
	}

	/**
	 * 도면일괄 다운로드
	 */
	public void progress(Map<String, Object> params) throws Exception {
		String type = (String) params.get("type");
		ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) params.get("data");

		WTUser user = CommonUtil.sessionUser();

		String savePath = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "batch"
				+ File.separator + user.getName();
		File save = new File(savePath);
		if (!save.exists()) {
			save.mkdirs();
		}

		for (Map<String, String> map : list) {
			String oid = map.get("epm_oid");
			EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);

			Representation representation = PublishUtils.getRepresentation(epm);
			if (representation != null) {
				if ("DXF".equals(type)) {
					QueryResult result = ContentHelper.service.getContentsByRole(representation,
							ContentRoleType.SECONDARY);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						String ext = FileUtil.getExtension(data.getFileName());
						if ("dxf".equalsIgnoreCase(ext)) {
							continue;
						}
						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
						File file = new File(savePath + File.separator + data.getFileName());
						FileOutputStream fos = new FileOutputStream(file);
						int j = 0;
						while ((j = is.read(buffer, 0, 10240)) > 0) {
							fos.write(buffer, 0, j);
						}
						fos.close();
						is.close();
					}
				} else if ("PDF".equals(type)) {
					QueryResult result = ContentHelper.service.getContentsByRole(representation,
							ContentRoleType.ADDITIONAL_FILES);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						String ext = FileUtil.getExtension(data.getFileName());
						if ("pdf".equalsIgnoreCase(ext)) {
							continue;
						}
						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
						File file = new File(savePath + File.separator + data.getFileName());
						FileOutputStream fos = new FileOutputStream(file);
						int j = 0;
						while ((j = is.read(buffer, 0, 10240)) > 0) {
							fos.write(buffer, 0, j);
						}
						fos.close();
						is.close();
					}
				}
			} // end for

			String zipFileName = user.getName() + ".zip";
			zipDirectory(savePath, zipFileName);
		}
	}

	private void zipDirectory(String savePath, String zipFileName) throws Exception {
		FileOutputStream fos = new FileOutputStream(zipFileName);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File fileToZip = new File(savePath);

		zipFile(fileToZip, fileToZip.getName(), zipOut);
		zipOut.close();
		fos.close();
		System.out.println("압축 파일 생성 완료: " + zipFileName);
	}

	private void zipFile(File fileToZip, String name, ZipOutputStream zipOut) throws Exception {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (name.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(name));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(name + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, name + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(name);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	/**
	 * 최신버전 도면인지 확인
	 */
	public boolean isLatest(EPMDocument epm) throws Exception {
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(epm.getMaster(), config);
		if (result.hasMoreElements()) {
			EPMDocument latest = (EPMDocument) result.nextElement();
			if (epm.getPersistInfo().getObjectIdentifier().getId() == latest.getPersistInfo().getObjectIdentifier()
					.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * CREO VIEW 접속 URL 만들기
	 */
	public String getCreoViewUrl(HttpServletRequest request, String oid) throws Exception {

		Persistable per = (Persistable) CommonUtil.getObject(oid);
		EPMDocument epm = null;
		if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			epm = PartHelper.manager.getEPMDocument(part);
		} else {
			epm = (EPMDocument) per;
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);
		int idx_d = query.appendClassList(DerivedImage.class, true);
		int idx_h = query.appendClassList(HolderToContent.class, false);
		int idx_a = query.appendClassList(ApplicationData.class, true);

		SearchCondition sc = new SearchCondition(EPMDocument.class, "thePersistInfo.theObjectIdentifier.id",
				DerivedImage.class, "derivedFromReference.key.id");
		query.appendWhere(sc, new int[] { idx, idx_d });
		query.appendAnd();

		sc = new SearchCondition(DerivedImage.class, "thePersistInfo.theObjectIdentifier.id", HolderToContent.class,
				"roleAObjectRef.key.id");
		query.appendWhere(sc, new int[] { idx_d, idx_h });
		query.appendAnd();

		sc = new SearchCondition(ApplicationData.class, "thePersistInfo.theObjectIdentifier.id", HolderToContent.class,
				"roleBObjectRef.key.id");
		query.appendWhere(sc, new int[] { idx_a, idx_h });
		query.appendAnd();

		sc = new SearchCondition(EPMDocument.class, "thePersistInfo.theObjectIdentifier.id", "=",
				epm.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(ApplicationData.class, ApplicationData.FILE_NAME, "LIKE", "%.pvs");
		query.appendWhere(sc, new int[] { idx_a });

		QueryResult qr = PersistenceHelper.manager.find(query);
		String doid = "";
		String aoid = "";
		String fileName = "";
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			DerivedImage image = (DerivedImage) obj[1];
			ApplicationData dd = (ApplicationData) obj[2];
			doid = image.getPersistInfo().getObjectIdentifier().getStringValue();
			aoid = dd.getPersistInfo().getObjectIdentifier().getStringValue();
			fileName = dd.getFileName();
		}

		PDMLinkProduct product = WCUtil.getPDMLinkProduct();

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.indexOf(request.getContextPath()));

		StringBuilder sb = new StringBuilder(url);
		sb.append(request.getContextPath());
		sb.append("/servlet/WindchillAuthGW/com.ptc.wvs.server.util.WVSContentHelper/redirectDownload/");
		sb.append(fileName);
		sb.append("?ContentHolder=").append(doid);
		sb.append("&HttpOperationItem=").append(aoid);
		sb.append("&u8=1&objref=").append(doid);
		sb.append("&ContainerOid=").append(product.toString());
		return sb.toString();
	}

	public Map<String, Object> zip(Map<String, Object> params) throws Exception {
		System.out.println(params);
		Map<String, Object> result = new HashMap<>();
		ArrayList<Map<String, Object>> gridData = (ArrayList<Map<String, Object>>) params.get("gridData");
		boolean dxf = (boolean) params.get("dxf");
		boolean pdf = (boolean) params.get("pdf");
		boolean step = (boolean) params.get("step");

		WTUser user = CommonUtil.sessionUser();
		Timestamp t = new Timestamp(new Date().getTime());
		String to = t.toString().substring(0, 10);
		String path = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + user.getName() + to;

		for (Map<String, Object> data : gridData) {
			String part_oid = (String) data.get("part_oid");
			WTPart root = (WTPart) CommonUtil.getObject(part_oid); // 최상위..
			ArrayList<WTPart> list = PartHelper.manager.descendants(root);
			System.out.println("list=" + list.size());

			for (WTPart part : list) {
				EPMDocument epm = PartHelper.manager.getEPMDocument(part);

				if (epm != null) {

					if (step) {
						Representation representation = PublishUtils.getRepresentation(epm);
						if (representation != null) {
							QueryResult qr = ContentHelper.service.getContentsByRole(representation,
									ContentRoleType.ADDITIONAL_FILES);
							if (qr.hasMoreElements()) {
								ApplicationData dd = (ApplicationData) qr.nextElement();
								String fname = dd.getFileName();
								String ext = FileUtil.getExtension(fname);
								if ("stp".equalsIgnoreCase(ext) || "step".equalsIgnoreCase(ext)) {
									byte[] buffer = new byte[10240];
									InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
									File file = new File(path + File.separator + fname);
									FileOutputStream fos = new FileOutputStream(file);
									int j = 0;
									while ((j = is.read(buffer, 0, 10240)) > 0) {
										fos.write(buffer, 0, j);
									}
									fos.close();
									is.close();
								}
							}
						}
					}

					EPMDocument epm2d = PartHelper.manager.getEPMDocument2D(epm);

					if (epm2d != null) {
						// pdf
						Representation representation = PublishUtils.getRepresentation(epm2d);
						if (pdf) {
							if (representation != null) {
								QueryResult qr = ContentHelper.service.getContentsByRole(representation,
										ContentRoleType.ADDITIONAL_FILES);
								if (qr.hasMoreElements()) {
									ApplicationData dd = (ApplicationData) qr.nextElement();
									String fname = dd.getFileName();
									String ext = FileUtil.getExtension(fname);
									if ("pdf".equalsIgnoreCase(ext)) {
										byte[] buffer = new byte[10240];
										InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
										File file = new File(path + File.separator + fname);
										FileOutputStream fos = new FileOutputStream(file);
										int j = 0;
										while ((j = is.read(buffer, 0, 10240)) > 0) {
											fos.write(buffer, 0, j);
										}
										fos.close();
										is.close();
									}
								}
							}
						}

						if (dxf) {
							if (representation != null) {
								QueryResult qr = ContentHelper.service.getContentsByRole(representation,
										ContentRoleType.SECONDARY);
								ApplicationData dd = (ApplicationData) qr.nextElement();
								String fname = dd.getFileName();
								String ext = FileUtil.getExtension(fname);
								if ("dxf".equalsIgnoreCase(ext)) {
									byte[] buffer = new byte[10240];
									InputStream is = ContentServerHelper.service.findLocalContentStream(dd);
									File file = new File(path + File.separator + fname);
									FileOutputStream fos = new FileOutputStream(file);
									int j = 0;
									while ((j = is.read(buffer, 0, 10240)) > 0) {
										fos.write(buffer, 0, j);
									}
									fos.close();
									is.close();
								}
							}
						}

					}
				}
			}
		}

		String savePath = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "zip";
		File ff = new File(savePath);
		if (!ff.exists()) {
			ff.mkdirs();
		}
		String zipFileName = user.getName() + ".zip";
		// zipDirectory(savePath, zipFileName);

		return result;
	}

	public Map<String, Object> excelList() throws Exception {
		Map<String, Object> result = new HashMap<>();

		String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
		String path = WTProperties.getServerProperties().getProperty("wt.temp");

		File orgFile = new File(wtHome + "/codebase/com/e3ps/drawing/beans/epm_list.xlsx");

		File newFile = CommonUtil.copyFile(orgFile, new File(path + "/도면 리스트.xlsx"));

		Workbook workbook = new Workbook(newFile.getPath());
		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.setName("도면 리스트"); // 시트 이름

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);

		String location = "/Default/PART_Drawing";

		if (StringUtil.checkString(location)) {
			int l = location.indexOf(PART_ROOT);

			if (l >= 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				location = location.substring((l + PART_ROOT.length()));
				// Folder Search
				int folder_idx = query.addClassList(EpmLocation.class, false);
				query.appendWhere(new SearchCondition(EpmLocation.class, EpmLocation.EPM, EPMDocument.class,
						"thePersistInfo.theObjectIdentifier.id"), new int[] { folder_idx, idx });
				query.appendAnd();

				query.appendWhere(new SearchCondition(EpmLocation.class, "loc", SearchCondition.LIKE, location + "%"),
						new int[] { folder_idx });
			}
		}

		QuerySpecUtils.toLatest(query, idx, EPMDocument.class);
		QuerySpecUtils.toOrderBy(query, idx, EPMDocument.class, EPMDocument.CREATE_TIMESTAMP, false);
		QueryResult qr = PersistenceHelper.manager.find(query);

		int rowIndex = 1;

		Style center = workbook.createStyle();
		center.setHorizontalAlignment(TextAlignmentType.CENTER);

		Style left = workbook.createStyle();
		left.setHorizontalAlignment(TextAlignmentType.LEFT);

		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			EPMDocument epm = (EPMDocument) obj[0];

			Cell rowCell = worksheet.getCells().get(rowIndex, 0);
			rowCell.setStyle(center);
			rowCell.putValue(rowIndex);

			Cell cadTypeCell = worksheet.getCells().get(rowIndex, 1);
			cadTypeCell.setStyle(center);
			cadTypeCell.putValue(epm.getDocType().getDisplay());

			Cell numberCell = worksheet.getCells().get(rowIndex, 2);
			numberCell.setStyle(center);
			numberCell.putValue(epm.getNumber());

			Cell nameCell = worksheet.getCells().get(rowIndex, 3);
			nameCell.setStyle(left);
			nameCell.putValue(epm.getName());

			Cell locationCell = worksheet.getCells().get(rowIndex, 4);
			locationCell.setStyle(center);
			locationCell.putValue(epm.getLocation());

			Cell revCell = worksheet.getCells().get(rowIndex, 5);
			revCell.setStyle(center);
			revCell.putValue(epm.getVersionIdentifier().getSeries().getValue() + "."
					+ epm.getIterationIdentifier().getSeries().getValue());

			Cell stateCell = worksheet.getCells().get(rowIndex, 6);
			stateCell.setStyle(center);
			stateCell.putValue(epm.getLifeCycleState().getDisplay());

			Cell creatorCell = worksheet.getCells().get(rowIndex, 7);
			creatorCell.setStyle(center);
			creatorCell.putValue(epm.getCreatorFullName());

			Cell createdDateCell = worksheet.getCells().get(rowIndex, 8);
			createdDateCell.setStyle(center);
			createdDateCell.putValue(epm.getCreateTimestamp().toString().substring(0, 10));

			Cell modifyCell = worksheet.getCells().get(rowIndex, 9);
			modifyCell.setStyle(center);
			modifyCell.putValue(epm.getModifyTimestamp().toString().substring(0, 10));
			rowIndex++;
		}

		String fullPath = path + "/도면 리스트.xlsx";
		workbook.save(fullPath);
		result.put("name", newFile.getName());
		return result;
	}

}
