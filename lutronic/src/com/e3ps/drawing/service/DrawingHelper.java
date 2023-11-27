package com.e3ps.drawing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devOutPutLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.drawing.beans.EpmData;
import com.e3ps.drawing.column.EpmColumn;
import com.e3ps.part.service.PartHelper;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.FloatValue;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

public class DrawingHelper {
	public static final DrawingService service = ServiceFactory.getService(DrawingService.class);
	public static final DrawingHelper manager = new DrawingHelper();
	public static final String PART_ROOT = "/Default/PART_Drawing";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EPMDocument.class, true);
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<EpmColumn> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();

		try {
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
			String islastversion = StringUtil.checkNull((String) params.get("islastversion"));
			String autoCadLink = StringUtil.checkNull((String) params.get("autoCadLink"));
			String unit = StringUtil.checkNull((String) params.get("unit"));
			String model = StringUtil.checkNull((String) params.get("model"));
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

			Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}

			int folder_idx = query.addClassList(IteratedFolderMemberLink.class, false);
			SearchCondition sc1 = new SearchCondition(
					new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId"),
					SearchCondition.EQUAL, new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
			sc1.setFromIndicies(new int[] { folder_idx, idx }, 0);
			sc1.setOuterJoin(0);
			query.appendWhere(sc1, new int[] { folder_idx, idx });

			query.appendAnd();
			ArrayList folders = CommonFolderHelper.service.getFolderTree(folder);
			query.appendOpenParen();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
					SearchCondition.EQUAL, folder.getPersistInfo().getObjectIdentifier().getId()),
					new int[] { folder_idx });

			for (int fi = 0; fi < folders.size(); fi++) {
				String[] s = (String[]) folders.get(fi);
				Folder sf = (Folder) rf.getReference(s[2]).getObject();
				query.appendOr();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
								SearchCondition.EQUAL, sf.getPersistInfo().getObjectIdentifier().getId()),
						new int[] { folder_idx });
			}
			query.appendCloseParen();

//			String temp = "";
//			Folder folder = null;
//			if (location == null || location.length() == 0) {
//				location = "/Default/PART_Drawing";
//			}
//			if(foid!=null && foid.length() > 0){
//				folder = (Folder)rf.getReference(foid).getObject();
//				location = FolderHelper.getFolderPath( folder );
//				temp = folder.getName();
//			}else{
//				folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
//				foid="";
//			}

			// 상태 임시저장 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.LIFE_CYCLE_STATE,
					SearchCondition.NOT_EQUAL, "TEMPRARY"), new int[] { idx });

			// Working Copy 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EPMDocument.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL,
					"wrk", false), new int[] { idx });

			// �����ȣ
			if (number.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(EPMDocument.class, "master>number", SearchCondition.LIKE,
						"%" + number + "%", false), new int[] { idx });
			} else
				number = "";

			// �����
			if (name.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(EPMDocument.class, "master>name", SearchCondition.LIKE,
						"%" + name + "%", false), new int[] { idx });
			} else
				name = "";

			// �ۼ���
			if (creator.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(EPMDocument.class, "iterationInfo.creator.key.id", "=",
						CommonUtil.getOIDLongValue(creator)), new int[] { idx });
			} else
				creator = "";

			// ���°˻�
			if (StringUtil.checkString(state)) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(EPMDocument.class, "state.state", SearchCondition.EQUAL, state),
						new int[] { idx });
			}

			// �ۼ����� (predate)
			if (StringUtil.checkString(predate)) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[] { idx });
			}
			// �ۼ����� (postdate)
			if (StringUtil.checkString(postdate)) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[] { idx });
			}

			// ��������
			if (predate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(
						new SearchCondition(EPMDocument.class, "thePersistInfo.modifyStamp",
								SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)),
						new int[] { idx });
			}
			if (postdate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(EPMDocument.class, "thePersistInfo.modifyStamp",
						SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)), new int[] { idx });
			}

			QuerySpecUtils.toTimeGreaterAndLess(query, idx, EPMDocument.class, EPMDocument.CREATE_TIMESTAMP,
					createdFrom, createdTo);
			QuerySpecUtils.toTimeGreaterAndLess(query, idx, EPMDocument.class, EPMDocument.MODIFY_TIMESTAMP,
					modifiedFrom, modifiedTo);

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
								SearchCondition.GREATER_THAN_OR_EQUAL, Double.parseDouble(weight1)),
								new int[] { _idx });
					}

					if (weight2.length() > 0) {
						query.appendAnd();
						query.appendWhere(new SearchCondition(FloatValue.class, "value",
								SearchCondition.LESS_THAN_OR_EQUAL, Double.parseDouble(weight2)), new int[] { _idx });
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

			// Folder Search
//			if (location.length() > 0) {
//				int l = location.indexOf(ROOTLOCATION);
//
//				if (l >= 0) {
//					if (query.getConditionCount() > 0) {
//						query.appendAnd();
//					}
//					location = location
//							.substring((l + ROOTLOCATION.length()));
//					// Folder Search
//					int folder_idx = query.addClassList(EpmLocation.class,
//							false);
//					query.appendWhere(new SearchCondition(EpmLocation.class,
//							EpmLocation.EPM, EPMDocument.class,
//							"thePersistInfo.theObjectIdentifier.id"),
//							new int[] { folder_idx, idx });
//					query.appendAnd();
//
//					query.appendWhere(new SearchCondition(EpmLocation.class,
//							"loc", SearchCondition.LIKE, location + "%"),
//							new int[] { folder_idx });
//				}
//
//			}
			// CAD ����
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
			// 최신 이터레이션
			if ("true".equals(islastversion)) {
				QuerySpecUtils.toLatest(query, idx, EPMDocument.class);
			}

//			QueryResult result = PersistenceHelper.manager.find(query);
			QuerySpecUtils.toOrderBy(query, idx, EPMDocument.class, EPMDocument.MODIFY_TIMESTAMP, true);

			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EpmColumn data = new EpmColumn(obj);
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
				devActive m = (devActive) CommonUtil.getObject(oid);
				QueryResult qr = PersistenceHelper.manager.navigate(m, "output", devOutPutLink.class);

				while (qr.hasMoreElements()) {
					Object p = (Object) qr.nextElement();
					if (p instanceof EPMDocument) {
						EpmData data = new EpmData((EPMDocument) p);

						map.put("number", data.getNumber());
						map.put("name", data.getName());
						map.put("state", data.getState());
//						map.put("version", data.getVersion());
						map.put("creator", data.getCreator());
//						map.put("description", data.getDescription());
					}
				}
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
}
