package e3ps.part.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.ParameterList;
import com.sap.mw.jco.JCO.Table;

import e3ps.approval.service.ApprovalHelper;
import e3ps.common.dto.BOMCompareDTO;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.HyoSungCommonUtil;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.MessageUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.PartCommonUtil;
import e3ps.common.util.SapConnectionManager;
import e3ps.common.util.SapRfcService;
import e3ps.common.util.StandardBOMSapRfcService;
import e3ps.common.util.StringUtils;
import e3ps.echange.EBOMObj;
import e3ps.echange.EBOMObjWTPartLink;
import e3ps.epm.EPMApproval;
import e3ps.epm.service.EpmHelper;
import e3ps.org.User;
import e3ps.part.beans.BomTreeViewDataCompare;
import e3ps.part.beans.BomTreeViewDataLvFindNumberCompare;
import e3ps.part.beans.PartSimpleViewData;
import e3ps.part.beans.PartTreeDataBOM;
import e3ps.part.beans.WTPartUsageLinkFindNumberCompare;
import e3ps.part.bom.BomExclusionCode;
import e3ps.part.column.EBOMOBJColumnData;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;

@Service
public class StandardBOMService {
	
	private static Logger LOG = LoggerFactory.getLogger(StandardBOMService.class);
	
	public static int serverType = getServerType();
	
	
	public static int getServerType() {
		
		/*
		 * 0 = 운영서버
		 * 1 = 개발서버
		 * 2 = 개발자	
		 */
		
		int reValue = 0;
		InetAddress local;
		try {
			local = InetAddress.getLocalHost();
			String ip = local.getHostAddress();
			if ("150.30.5.51".equals(ip)) {
				reValue = 0;
			} else if("150.30.5.151".equals(ip)) {
				reValue = 1;
			} else {
				reValue = 2;
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return reValue;
	}
	
	public void test() {
		System.out.println(" ---- StandardBOMService TETST ");
	}
	
	
	
	
	// 표준BOM 추가 - 111
	//public Map<String, Object> addEBOMSelectPDMAction_ReNEW2(Map<String, Object> param) throws Exception {
	public Map<String, Object> addStandardBOM(Map<String, Object> param) throws Exception {
		
		//LOG.debug("@@@@ addStandardBOM :: " + param);
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		String bomType = (String) param.get("bomType");
		String menuType = (String) param.get("menuType");
		String plant = (String) param.get("plant");
		
		ReferenceFactory rf = new ReferenceFactory();
		long start = System.currentTimeMillis();
		
		String errorMsg = "";
		
		//ArrayList<String[]> data = new ArrayList<String[]>();
		ArrayList<BOMCompareDTO> result = new ArrayList<BOMCompareDTO>();
		
		System.out.println("param -" + param);
		
		
		try {
			
			for (String rootOid : list) {
				WTPart part = (WTPart) rf.getReference(rootOid).getObject();
				if (part != null) {
					View view = ViewHelper.service.getView(part.getViewName());
					WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
					
					// 1.중복제거
					HashMap<String, WTPart> duplicatedMapInfo = new HashMap<String, WTPart>();
					duplicatedMapInfo = getDuplicateCheck(part, duplicatedMapInfo, configSpec);
					
					//2. 하위 조회
					//ArrayList<String[]> result = new ArrayList<String[]>();
					getStandardBomDataList(part, plant, configSpec, 0, result, rootOid, duplicatedMapInfo);
				}
			}
			
			
			//결과물 출력 테스트
			//for(BOMCompareDTO d : result) {
				//System.out.println(d.getParentPartNo() + " --- " + d.getChildPartNo());
			//}
			
			System.out.println("------------------- " + result.size());
			System.out.println("------------------- " + (System.currentTimeMillis() - start) / 1000.0 + "초");
			
			//System.out.println("<br>addEBOMSelectPDMAction_ReNEW : " + (System.currentTimeMillis() - start) / 1000.0 + "초");
			
			map.put("bomType", bomType);
			map.put("menuType", menuType);
			map.put("result", "SUCCESS");
			map.put("list", result);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("url", "/Windchill/plm/part/approvalEBOM");
			map.put("result", "FAIL");
			map.put("msg", errorMsg);
			if(e.getMessage() != null) {
				map.put("msg", e.getMessage());
				throw new Exception(e.getMessage());
			}
			throw new Exception(errorMsg);
		}
		return map;
	}

	
	/**
	 * 중복제거해서 Map에 담기
	 * @param part
	 * @param rMap
	 * @return
	 */
	public HashMap<String, WTPart> getDuplicateCheck(WTPart part, HashMap<String, WTPart> rMap, WTPartStandardConfigSpec configSpec) {
		
		try {
			
			String p_PART_NO = IBAUtils.getStringValue(part, "PART_NO");
			
			if(p_PART_NO != null && !"".equals(p_PART_NO)) p_PART_NO = p_PART_NO.trim(); // 공백제거
			
			if( !rMap.containsKey(p_PART_NO) ) {
				if( !"".equals(p_PART_NO.trim())) {
					rMap.put(p_PART_NO.trim(), part);
					//System.out.println("p_PART_NO ==" + p_PART_NO);
				}
			}
			
			QueryResult results = WTPartHelper.service.getUsesWTParts(part, configSpec);
			
			while (results.hasMoreElements()) {
				Object[] obj = (Object[]) results.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];

				if (!(obj[1] instanceof WTPart)) {
					continue;
				}
				
				WTPart childPart = (WTPart) obj[1];
				String c_PART_NO = IBAUtils.getStringValue(childPart, "PART_NO");
				
				if(c_PART_NO != null && !"".equals(c_PART_NO)) c_PART_NO = c_PART_NO.trim(); // 공백제거
				
				if( !rMap.containsKey(c_PART_NO) ) { // 중복제거
					if( !"".equals(c_PART_NO)) {
						rMap.put(c_PART_NO, childPart);
						//System.out.println("c_PART_NO ==" + c_PART_NO);
					}
				}
				
				rMap = getDuplicateCheck(childPart, rMap, configSpec) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rMap;
	}
	
	
	//추가한 BOM하위와 PLM비교- 222222
	public static void getStandardBomDataList(WTPart part, String plant, WTPartStandardConfigSpec configSpec, int level, 
								ArrayList<BOMCompareDTO> result, String rootOid, HashMap<String, WTPart> duplicatedMapInfo) {
		
		ArrayList<PartTreeDataBOM> firstList  = new ArrayList<>();
		ArrayList<WTPartUsageLink> secondList = new ArrayList<>();
		ArrayList<PartTreeDataBOM> sortedList = new ArrayList<>();
		
		try {
			
			WTPart rootPart = (WTPart) CommonUtils.getObject(rootOid);
			
			QueryResult results = WTPartHelper.service.getUsesWTParts(part, configSpec);
			
			int startNumber = 1;
			level = level + 1;
			
			HashMap<String, String> dataMap = new HashMap<String, String>();
			
			
			while (results.hasMoreElements()) {
				Object[] obj = (Object[]) results.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];

				if (!(obj[1] instanceof WTPart)) {
					continue;
				}
				
				WTPart childPart = (WTPart) obj[1];

				WTPart pPart = part;
				WTPart cPart = childPart;
				String p_PART_NO = IBAUtils.getStringValue(pPart, "PART_NO");
				String c_PART_NO = IBAUtils.getStringValue(cPart, "PART_NO");
				
				if(p_PART_NO != null && !"".equals(p_PART_NO)) {
					p_PART_NO = p_PART_NO.trim();
					dataMap.put(p_PART_NO.trim(), pPart.getPersistInfo().getObjectIdentifier().getStringValue());
				}
				
				if(c_PART_NO != null && !"".equals(c_PART_NO)) {
					c_PART_NO = c_PART_NO.trim();
					dataMap.put(c_PART_NO.trim(), cPart.getPersistInfo().getObjectIdentifier().getStringValue());
				}
				
				// 1차 Validation 시작
				// 모품 - CABLE, PCBA 제외
				if (!"".equals(p_PART_NO) && p_PART_NO.length() >= 4) {
					String num = p_PART_NO.substring(0, 4);
					String[] cable = BomExclusionCode.getCable(); // CABLE
					String[] pcba = BomExclusionCode.getPcba(); // PCBA
					String[] arrayCodes = ArrayUtils.addAll(cable, pcba);
					List<String> codes = Arrays.asList(arrayCodes);
					if(codes.contains(num)) {
						continue;
					}
				}
				
				// 모품 - DUMMY 제외 (IBA_PART_NAME, IBA_TITLE, NAME)
				if (PartHelper.manager.isOnlyDummyPart(pPart)) {
					continue;
				}

				// 자품 - DUMMY 제외 (IBA_PART_NAME, IBA_TITLE, NAME)
				if (PartHelper.manager.isOnlyDummyPart(cPart)) {
					continue;
				}
				
				// 모품 - ENDITEM 제외
				boolean isEndItem = IBAUtils.getBooleanValue(pPart, "ENDITEM");
				if (isEndItem) {
					continue;
				}
				
				
				EPMDocument parentEPM = PartHelper.manager.getEPMDocument(part);
				EPMDocument childEPM  = PartHelper.manager.getEPMDocument(childPart);
				
				//LOG
				LOG.debug("111 -- " + p_PART_NO + " > " + c_PART_NO + " == " + parentEPM + " , " + childEPM);
				
				if (parentEPM != null && childEPM != null) {
					EPMDocumentMaster m = (EPMDocumentMaster) childEPM.getMaster();
					int compNumber = PartHelper.getCompNumber(parentEPM, m);
					if (compNumber > 0) {
						DecimalFormat format = new DecimalFormat("0000");
						link.setFindNumber(format.format(compNumber));
						PersistenceServerHelper.manager.update(link);
						link = (WTPartUsageLink) PersistenceHelper.manager.refresh(link);
						PartTreeDataBOM dd = new PartTreeDataBOM(childPart, link, level, part);
						firstList.add(dd);
					} else {
						secondList.add(link);
					}
				}else{
					secondList.add(link);
				}
			} // end while
			
			Collections.sort(firstList, new BomTreeViewDataCompare());
			
			// 1. 양수인거 findNumber 재정의
			for(PartTreeDataBOM dd : firstList) {
				WTPartUsageLink link = dd.link;
				DecimalFormat format = new DecimalFormat("0000");
				link.setFindNumber(format.format(startNumber));
				PersistenceServerHelper.manager.update(link);
				
				link = (WTPartUsageLink) PersistenceHelper.manager.refresh(link);
				PartTreeDataBOM redd = new PartTreeDataBOM(dd.part, link, dd.level, dd.parentPart);
				
				startNumber++;
				//lastNum = fineNo;
				//fineNo++;
				sortedList.add(redd);
			}
			
			
			//2. 음수 or 순번없는거 findNumber 재정의
			Collections.sort(secondList, new WTPartUsageLinkFindNumberCompare());
			
			if (null != secondList && secondList.size() > 0) {
				for (WTPartUsageLink link : secondList) {
					DecimalFormat format = new DecimalFormat("0000");
					
					link.setFindNumber(format.format(startNumber));
					PersistenceServerHelper.manager.update(link);
					link = (WTPartUsageLink) PersistenceHelper.manager.refresh(link);

					WTPart noEpmchildPart = PartHelper.manager.getLatestPart(link.getUses().getNumber());
					PartTreeDataBOM dd = new PartTreeDataBOM(noEpmchildPart, link, level, part);
					sortedList.add(dd);
					
					startNumber++;
				}
			}
			
			Collections.sort(sortedList, new BomTreeViewDataLvFindNumberCompare());
			
			
			
			ArrayList<WTPart> nextStepList = new ArrayList<WTPart>();
			HashMap<String, BOMCompareDTO> pdmData = new LinkedHashMap<String, BOMCompareDTO>(); // PLM과 비교할 데이터 셋팅
			
			
			// 3. 하위 검사 여부 체크
			for(PartTreeDataBOM sortPart : sortedList){
				WTPart ddPart = sortPart.part;
				//tdata.parentPart
				
				String parentPartNo = "";
				String childPartNo  = "";
				
				parentPartNo = sortPart.parent_iba_number;
				childPartNo  = sortPart.iba_number;
				
				if(parentPartNo != null && !"".equals(parentPartNo)) parentPartNo = parentPartNo.trim();
				if(childPartNo != null && !"".equals(childPartNo)) childPartNo = childPartNo.trim();
				
				//비교를 위해 미리 PDM데이터 셋팅 (모-자)
				BOMCompareDTO dto = new BOMCompareDTO();
				
				//모품
				dto.setParentName(sortPart.parentPart.getName());
				dto.setParentVersion(VersionControlHelper.getIterationDisplayIdentifier(sortPart.parentPart) + " (" + sortPart.parentPart.getViewName() + " )");
				dto.setParentOid(sortPart.parentPart.getPersistInfo().getObjectIdentifier().getStringValue());
				dto.setParentPartNo(parentPartNo);
				dto.setParentState(sortPart.parentPart.getLifeCycleState().getDisplay(MessageUtils.getUserSelectLocale()));
				
				//자품
				dto.setChildOid(ddPart.getPersistInfo().getObjectIdentifier().getStringValue());
				dto.setChildName(ddPart.getName());
				dto.setChildVersion(VersionControlHelper.getIterationDisplayIdentifier(ddPart) + " (" + ddPart.getViewName() + " )");
				dto.setChildPartNo(childPartNo);
				dto.setChildQty(String.valueOf( sortPart.quantity));				//수량
				dto.setChildunit(ddPart.getDefaultUnit().toString().toUpperCase()); //단위
				dto.setChildFindNumber(sortPart.link.getFindNumber());				//findNumber(자품)
				dto.setChildState(ddPart.getLifeCycleState().getDisplay(MessageUtils.getUserSelectLocale()));           //상태
				
				String key = parentPartNo + "↔" + childPartNo;
				pdmData.put(key, dto);
				
				String ccPartNo = IBAUtils.getStringValue(sortPart.part, "PART_NO");
				if(ccPartNo != null && !"".equals(ccPartNo)) ccPartNo = ccPartNo.trim();
				
				//하위추가 여부 체크 - CABLE, KASSY, PCB는 단품처리하므로 자체는 추가되고 그 하위는 추가하지 않는다.
				// 1) PCB, CABLE, KASSY 아닐 경우 하위 추가
				// 2) PCM, CABLE, KASSY 하위 Hidden
				// 3) ASM이면서 결재중 and 릴리즈된거는 제외
				if( !HyoSungCommonUtil.isKCP_Check(ccPartNo) ) {
					if (!"component".equals(ddPart.getPartType().toString())) {
						if ( !"UNDERAPPROVAL".equals(ddPart.getState().toString()) && !"RELEASED".equals(ddPart.getState().toString()) 
											&& !"PROTOTYPE".equals(ddPart.getState().toString()) ) {
							nextStepList.add(ddPart);
						}
					}
				}
				
			} // END sortedList
			
			
			
			// 4. PLM과 비교 후, 결과값 반환
			ArrayList<BOMCompareDTO> resultData = comparePDMPLMData(plant, part, pdmData);
			
			
			// 5. 출력 전 2차 Validation 시작
			for(int i=0; i < resultData.size(); i++) {
				
				StringBuffer message = new StringBuffer();
				
				BOMCompareDTO dto = resultData.get(i);
				
				dto.setRootOid(rootOid); // 선택한 최상위 BOM OID
				dto.setRootPartNo(IBAUtils.getStringValue(rootPart, "PART_NO"));
				String type = dto.getType(); // D일 경우는 PLM에만 있는거라 체크해줘야됨
				
				LOG.trace(type + " --- " + dto.getParentPartNo() + " > " + dto.getChildPartNo());
				
				String checkType = "S";
				
				WTPart parentPart     = null;
				String parentPartNo   = "";
				String parentPartName = "";
				String parentTitle    = "";
				
				if(dto.getParentOid() == null) {
					parentPart = duplicatedMapInfo.get(dto.getParentPartNo().trim());
					if(parentPart != null) {
						dto.setParentOid(parentPart.getPersistInfo().getObjectIdentifier().getStringValue());
						parentTitle = IBAUtils.getStringValue(parentPart, "TITLE");
						parentPartNo = IBAUtils.getStringValue(parentPart, "PART_NO");
						parentPartName = IBAUtils.getStringValue(parentPart, "PART_NAME"); 
						
						dto.setParentPartNo(parentPartNo.trim());
						dto.setParentName(parentPart.getName());
						dto.setParentVersion(VersionControlHelper.getIterationDisplayIdentifier(parentPart) + " (" + parentPart.getViewName() + " )");
						dto.setParentState(parentPart.getLifeCycleState().getDisplay(MessageUtils.getUserSelectLocale()));
					} else {
						dto.setParentName("해당 부품은 PDM에 없습니다.");
						dto.setParentOid("");
					}
				} else {
					parentPart =  (WTPart) CommonUtils.getObject(dto.getParentOid());  //dto.getParentPart();
					parentTitle = IBAUtils.getStringValue(parentPart, "TITLE");
					parentPartNo = IBAUtils.getStringValue(parentPart, "PART_NO"); // dto.getParentPartNo();
					parentPartName = IBAUtils.getStringValue(parentPart, "PART_NAME"); //dto.getParentName();
				}
				
				if(parentPartNo != null && !"".equals(parentPartNo)) parentPartNo = parentPartNo.trim();
				
				//System.out.println( (i+1) + "-" + parentPartNo + "->>> " + dto.getParentOid() + " -- " +  dto.getChildOid() + " ," + dto.getChildPartNo() + "--- " + dto.getType());
				//System.out.println("childPart -- " + childPart + " , " + dto.getChildOid());
				
				WTPart childPart 	 = null;
				String childPartNo   = ""; //dto.getChildPartNo();
				String childPartName = ""; // dto.getChildName();
				String childState    = ""; //childPart.getState().toString();
				String childTitle    = ""; // IBAUtils.getStringValue(childPart, "TITLE");
				
				if(dto.getChildOid() == null) {
					childPart = duplicatedMapInfo.get(dto.getChildPartNo().trim());
					
					if(childPart == null) {
						
						//품명과 파일명으로 조회
						//5728002423
						//childPart = PartHelper.manager.getWTPartFromPARTNO(dto.getChildPartNo().trim(), CommonUtils.getView(plant));
						//LIKE '품번%'
						childPart = PartHelper.manager.getWTPartByPartNoAndFileNameAsLikeV2(dto.getChildPartNo().trim());
						
						if(childPart == null) {
							//LIKE '%품번%'
							childPart = PartHelper.manager.getWTPartByPartNoAndFileNameAsLike(dto.getChildPartNo().trim(), CommonUtils.getView(plant));
						}
						
						if( childPart == null) {
							//LIKE
							childPart = PartHelper.manager.getWTPartFromPARTNOAsLike(dto.getChildPartNo().trim(), CommonUtils.getView(plant));
						}
					}
					
					if(childPart != null) {
						dto.setChildOid(childPart.getPersistInfo().getObjectIdentifier().getStringValue());
						childState = childPart.getState().toString();
						childTitle  = IBAUtils.getStringValue(childPart, "TITLE");
						childPartNo = IBAUtils.getStringValue(childPart, "PART_NO");
						if(childPartNo != null && !"".equals(childPartNo)) childPartNo = childPartNo.trim(); // 공백제거
						
						childPartName = IBAUtils.getStringValue(childPart, "PART_NAME");
						dto.setChildPartNo(childPartNo);
						dto.setChildName(childPart.getName());
						dto.setChildVersion(VersionControlHelper.getIterationDisplayIdentifier(childPart) + " (" + childPart.getViewName() + " )");
						dto.setChildState(childPart.getLifeCycleState().getDisplay(MessageUtils.getUserSelectLocale()));
					} else {
						childState  = "-";
						childTitle  = "-";
						dto.setChildName("해당 부품은 PDM에 없습니다.");
					}
					
				} else {
					childPart   = (WTPart) CommonUtils.getObject(dto.getChildOid());
					childTitle  = IBAUtils.getStringValue(childPart, "TITLE");
					childPartNo = IBAUtils.getStringValue(childPart, "PART_NO");
					childPartName = IBAUtils.getStringValue(childPart, "PART_NAME");
				}
				
				if(childPartNo != null && !"".equals(childPartNo)) childPartNo = childPartNo.trim();
				
				String childFindNumber = dto.getChildFindNumber();
				
				//System.out.println( (i+1) + "  " + parentPartNo + " >>> " + childPartNo + " --- " + childFindNumber + "_" + dto.getType());
				

				if(parentPart == null || childPart == null) {
					//continue;
				}
				
				if(parentPart != null) {
					if (!parentPart.isLatestIteration()) {
						checkType = "E";
						message.append("상위 최신버전 아님" + " \n");
					}
					
					if (parentPartNo.length() == 0) {
						checkType = "E";
						message.append("상위 PART_NO 없음");
					}
					
					if (parentTitle.length() == 0) {
						checkType = "E";
						message.append("상위 TITLE 없음");
					}
					
					if (parentPartName.length() == 0) {
						checkType = "E";
						message.append("상위 PART_NAME 없음");
					}
					
					// 체크아웃 체크
					if (WorkInProgressHelper.isCheckedOut(parentPart)) {
						message.append("상위 Check-Out 된 객체");
						checkType = "E";
					}
					
					// wrk 체크
					if (WorkInProgressHelper.isWorkingCopy(parentPart)) {
						message.append("상위 Working-Copy 된 객체");
						checkType = "E";
					}
				}
				
				if (childPartNo.length() == 0) {
					checkType = "E";
					message.append("하위 PART_NO 없음" + " \n");
				}
				
				
				if(childPart != null) {
					
					String cVersion = childPart.getVersionIdentifier().getSeries().getValue();
					char cv = cVersion.charAt(0);
					
					// D가 아닐 경우만 검사한다
					// 즉, D일 경우 자품은 검사할 필요가 없다
					if(!"D".contentEquals(type)) {
						if (!childPart.isLatestIteration()) {
							checkType = "E";
							message.append("하위 최신버전 아님" + " \n");
						}
						
						if (childTitle.length() == 0) {
							checkType = "E";
							message.append("하위 TITLE 없음" + " \n");
						}

						if (childPartName.length() == 0) {
							checkType = "E";
							message.append("하위 PART_NAME 없음" + " \n");
						}
						
						// 체크아웃 체크
						if (WorkInProgressHelper.isCheckedOut(childPart)) {
							message.append("하위 Check-Out 된 객체" + " \n");
							checkType = "E";
						}
						
						// wrk 체크
						if (WorkInProgressHelper.isWorkingCopy(childPart)) {
							message.append("하위 Working-Copy 된 객체");
							checkType = "E";
						}
						
						
						//자품 검사 - 00 or 알파벳 > "검증"할때 검사하기 때문에 임시로 뺌.
						/*if ("component".equals(childPart.getPartType().toString())) {
							if("00".equals(cVersion) || Character.isAlphabetic(cv) ) {
								if ("INWORK".equals(childState) || "RETURN".equals(childState)) {
									type = "E";
									message.append("해당 EndItem 은 도면승인이 필요합니다.");
								}
							}
						}*/
					}
					
					//5728 5721
				}
				
				dto.setCheckType(checkType);
				dto.setCheckMsg(message.toString());
				
				LOG.info( (i+1) + "  " + parentPartNo + " >>> " + childPartNo + " --- " + childFindNumber + " > " + dto.getType());
				
				//결과값 넘길 DTO만들어서 LIST에 담아서 넘겨준다.
				result.add(dto);
				
				
			} // end resultData
			
			
			// 6. 하위 순환 검사
			for(WTPart nextPart : nextStepList) {
				//getStandardBomDataList(WTPart part, String plant, WTPartStandardConfigSpec configSpec, int level)
				getStandardBomDataList(nextPart, plant, configSpec, level, result, "", duplicatedMapInfo);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//PDM-PLM데이터와 비교 - 3333333
	public static ArrayList<BOMCompareDTO> comparePDMPLMData(String plant, WTPart part, HashMap<String, BOMCompareDTO> PDMData) {
		
		//LOG.info("## comparePDMPLMData ##");
		
		
		ArrayList<BOMCompareDTO> resultList = new ArrayList<BOMCompareDTO>();
		
		try {
			
			String parent_PartNo = IBAUtils.getStringValue(part, "PART_NO");
			if(parent_PartNo != null && !"".equals(parent_PartNo)) parent_PartNo = parent_PartNo.trim();
			
			// ######  PLM Data  #######
			HashMap<String, BOMCompareDTO> PLMData = new HashMap<String, BOMCompareDTO>();
			PLMData = getSapPLMDataList(plant, parent_PartNo); // PLM DATA 셋팅
			
			
			// ######  PDM-PLM 비교 ######
			//1.PDM 데이터가 기준이다.
			for(String key : PDMData.keySet()) {
				
				BOMCompareDTO pdmDto = PDMData.get(key);
				
				//PLM-PDM 비교
				if(PLMData != null && PLMData.containsKey(key)) {
					
					
					BOMCompareDTO plmDto = PLMData.get(key);
					
					String pdmQty = pdmDto.getChildQty();
					String plmQty = plmDto.getChildQty();

					LOG.info("비교 ========= " + plmDto.getChildPartNo() + " , " + pdmDto.getChildPartNo());
					
					boolean flag = false;
					
					
					String pdmFindNumber = pdmDto.getChildFindNumber();
					String plmFindNumber = plmDto.getChildFindNumber();
					
					
					//findNumber비교
					if( !pdmFindNumber.equals(plmFindNumber)) {
						//C
						//resultData
						pdmDto.setType("C");
						resultList.add(pdmDto);
						flag = true;
					}
					
					//수량비교
					if( !pdmQty.equals(plmQty) ) {
						//Q
						if(flag == false) {
							pdmDto.setType("Q");
							resultList.add(pdmDto);
						}
						//flag = true;
					}
					
					
					//비교 후 PLM데이터에서 제외한다. > 남은거 "D"로 보여주기 위해서.
					PLMData.remove(key);
					
				} else {
				//PDM에만 있으면 'A'로 추가로 표시
					pdmDto.setType("A");
					resultList.add(pdmDto);
				}
				
			} // end for
			

			
			// ## PLM에만 있는 부품은 "D"로 표시해준다.
			if(PLMData != null && PLMData.size() > 0) {
				for(String key : PLMData.keySet()) {
					BOMCompareDTO dd = PLMData.get(key);
					dd.setType("D");
					LOG.info("D ---> " + key);
					resultList.add(PLMData.get(key));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	
	
	
	// 표준BOM비교를 위해 PLM정보 조회
	/**
	 * @category 표준BOM비교를 위해 PLM정보 조회
	 * @param plant
	 * @param parent_PartNo
	 * @return
	 */
	public static HashMap<String, BOMCompareDTO> getSapPLMDataList(String plant, String parent_PartNo) {
		
		HashMap<String, BOMCompareDTO> result = new LinkedHashMap<String, BOMCompareDTO>();
		
		ArrayList<String> MATNOLIST = new ArrayList<String>();
		//SapConnectionManager sapConnectionManager = new SapConnectionManager();
		
		IRepository repository = null;
		JCO.Client client = null;
		String SID = "ABAP_AS";
		String sFuncName = "ZPLM_PDMLINK_BOM_LIST";
		String tableName = "IT_MATNR";
		String tableName2 = "ET_BOM";
		
		try {
			MATNOLIST.add(parent_PartNo);
			
			String I_WERKS = plant;
			String I_DATUV = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // (String) sapMap.get("I_DATUV");
			
			if (serverType == 0) {
				//운영
				//repository = sapConnectionManager.getConnection100(SID);  // 운영
				client = JCO.createClient(
	                    "100",
	                    "RFCCAD",
	                    "hyosung00",
	                    "EN",
	                    "nherpdb.hyosung.com",
	                    "40"
	                    //"NEP",
	                    //"nherp"
	            );
			} else if(serverType == 1){
				//개발
				//repository = sapConnectionManager.getConnectionTest(SID); // 개발
				client = JCO.createClient(
	                    "100",
	                    "RFCCAD",
	                    "hyosung00",
	                    "EN",
	                    "tnsdeccsvr01.hyosung.com",
	                    "40"
	            );
			}
			
			//client = JCO.getClient(SID);
			client.connect();
			repository = new JCO.Repository("ABAP_AS", client);
			
			IFunctionTemplate ftemplate = repository.getFunctionTemplate(sFuncName);
			JCO.Function function = ftemplate.getFunction();
			
			ParameterList pList = function.getImportParameterList();
			pList.getField("I_WERKS").setValue(plant);
			pList.getField("I_DATUV").setValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			pList.getField("I_SINGLE").setValue("X");

			JCO.ParameterList table = function.getTableParameterList();
			Table iTable = table.getTable(tableName);

			/*for (int i = 0; i < MATNOList.size(); i++) {
				String MATNO = (String) MATNOList.get(i);
				iTable.appendRow();
				iTable.setValue(MATNO, "MATNO");
			}*/
			
			
			//모품 PART_NO 셋팅
			//String MATNO = (String) MATNOList.get(i);
			iTable.appendRow();
			iTable.setValue(parent_PartNo, "MATNO");
			
			client.execute(function); // 실행
			
			JCO.ParameterList table2 = function.getTableParameterList();
			JCO.Table iTable2 = table2.getTable(tableName2);
			
			//조회
			for (int i = 0; i < iTable2.getNumRows(); i++) {
				String MATNO = (String) iTable2.getValue("MATNO"); // INPUT DATA
				String STUFE = (String) iTable2.getValue("STUFE"); // LEVEL
				String MATNR = (String) iTable2.getValue("MATNR"); // 상위 품번
				String IDNRK = (String) iTable2.getValue("IDNRK"); // 하위 품번
				String MENGE = (String) iTable2.getValue("MENGE"); // 수량
				String MEINS = (String) iTable2.getValue("MEINS"); // 단위
				String TYPE = (String) iTable2.getValue("TYPE"); // TYPE
				String MESSAGE = (String) iTable2.getValue("MESSAGE"); // MESSAGE
				String POSNR = (String) iTable2.getValue("POSNR"); // 순번
				iTable2.nextRow();
				
				if ("".equals(TYPE)) {
					MATNR = rmZero(MATNR);
					IDNRK = rmZero(IDNRK);
					DecimalFormat format = new DecimalFormat("0");
					
					String key = MATNR + "↔" + IDNRK;
					
					BOMCompareDTO dto = new BOMCompareDTO();
					dto.setParentPartNo(MATNR);
					dto.setChildPartNo(IDNRK);
					
					dto.setChildQty(String.valueOf(Double.parseDouble((MENGE))));  // 수량
					dto.setChildunit(MEINS); // 단위
					dto.setChildFindNumber(POSNR); // 순번
					
					LOG.debug("SAP SUCCESS == " + MATNO + " , " + IDNRK + " , " + MENGE + " :: " + POSNR + " ==> "+TYPE+"//"+ MESSAGE);
					result.put(key, dto);
				} else {
					LOG.debug(" SAP ERROR == " +   MATNO + " , " + IDNRK + " , " + MENGE + " :: " + POSNR + " ==> "+TYPE+"//"+ MESSAGE);
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//JCO.releaseClient(client);
			//sapConnectionManager.cleanUp();
			client.disconnect();
		}
		return result;
	}
	
	
	
	private static String rmZero(String matnr) {
		int len = matnr.length();
		for (int d = 0; d < len; d++) {
			if ("0".equals(matnr.substring(0, 1))) {
				matnr = matnr.substring(1);
			}
		}
		return matnr;
	}
	
	
	
	
	//표준BOM > '체크'클릭 > 1차 검증
	public Map<String, Object> standardBomCheck(Map<String, Object> param) {
		
		//System.out.println(" ###### standardBomCheck :: param = " + param);
		LOG.debug(" ###### standardBomCheck ###### ");
		//LOG.debug(" ###### param = " + param);
		
		List<String> partOids = (List<String>) param.get("partOids");
		List<String> pTypes = (List<String>) param.get("pTypes");
		List<String> rootOids = (List<String>) param.get("rootOids");
		String plant = (String) param.get("plant"); 
		String appEcoNo = (String) param.get("appEcoNo"); 
		String totalECO = (String) param.get("totalECO");
		List<String> cOids = (List<String>) param.get("cOids");
		
		String totalEcoFlag = (String) param.get("totalEcoFlag");

		/*System.out.println(" ###### partOids = " + partOids);
		System.out.println(" ###### rootOids = " + rootOids);
		System.out.println(" ###### pTypes = " + pTypes);
		System.out.println(" ###### plant = " + plant);
		System.out.println(" ###### appEcoNo = " + appEcoNo);
		System.out.println(" ###### totalECO = " + totalECO);
		System.out.println(" ###### cOids = " + cOids);*/
		
		LOG.info(" ###### totalEcoFlag = {} ", totalEcoFlag);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		HashMap<String, String> checkMap = new LinkedHashMap<>();
		HashMap<String, String> basicMap = new LinkedHashMap<>();
		HashMap<String, String> viewKey = new LinkedHashMap<>();
		Map<String, Object> PLM_CheckMap = new HashMap<>();
		
		String pdmErrorFlag = "SUCCESS";
		
		try {
			
			//1차 PDM 체크
			for (int i = 0; i < partOids.size(); i++) {
				
				String type = "S";
				StringBuffer msg = new StringBuffer();
				
				String parentOid = partOids.get(i);
				String childOid  = cOids.get(i);
				
				WTPart pPart = (WTPart) CommonUtils.getObject(parentOid);
				WTPart cPart = (WTPart) CommonUtils.getObject(childOid);
				
				
				//LOG.info("parentOid={}, childOid={} ::  ", totalEcoFlag);
				
				String addType = pTypes.get(i).trim();
				
				String p_PART_NO = IBAUtils.getStringValue(pPart, "PART_NO");
				String c_PART_NO = IBAUtils.getStringValue(cPart, "PART_NO");
				String c_state = "";
				
				if(p_PART_NO != null && !"".equals(p_PART_NO)) p_PART_NO = p_PART_NO.trim();
				if(c_PART_NO != null && !"".equals(c_PART_NO)) c_PART_NO = c_PART_NO.trim();
				
				LOG.debug("parentOid={}, childOid={} ::::: p_PART_NO={}, c_PART_NO={} ", parentOid, childOid, p_PART_NO, c_PART_NO);
				
				if(cPart != null) {
					c_state = cPart.getState().toString();
				}
				
				
				if (!pPart.isLatestIteration()) {
					msg.append("상위 최신 버전이 아닙니다.");
					type = "E";
				}	
				
				
				// 라이프사이클 체크
				if (!"DEFAULT_LIFECYCLE".equals(pPart.getLifeCycleName()) ) {
					msg.append("상위 라이프사이클 불일치" + System.lineSeparator());
					type = "E";
				}
				
				if ("".equals(IBAUtils.getStringValue(pPart, "TITLE"))) {
					msg.append("상위 TITLE 없음");
					type = "E";
				}
				
				if ("".equals(IBAUtils.getStringValue(pPart, "PART_NAME"))) {
					msg.append("상위 PART_NAME 없음");
					type = "E";
				}
				
				
				// 품번 체크 - 모품
				if ("".equals(IBAUtils.getStringValue(pPart, "PART_NO"))) {
					msg.append("상위 PART_NO 없음" + System.lineSeparator());
					type = "E";
				}
				
				// wrk 체크
				if (WorkInProgressHelper.isWorkingCopy(pPart)) {
					
					if(WorkInProgressHelper.isCheckedOut(pPart)) {
						WorkInProgressHelper.service.undoCheckout(pPart);
					}
					//msg.append("상위 Working-Copy 된 객체");
					//type = "E";
				}
				

				// WPART-체크아웃 체크
				if (WorkInProgressHelper.isCheckedOut(pPart)) {
					msg.append("상위 WTPART : Check-Out 된 객체" + System.lineSeparator());
					type = "E";
				}

				
				//WTPART에 연결된 3D정보 조회
				HashMap<String, Object> eMap = PartCommonUtil.relatedEPMLifecycleInfo(pPart); 
				String parent_EPM3D_OID = (String)eMap.get("EPM3D_OID");
				//String EPM2D_OID = (String)eMap.get("EPM2D_OID");
				
				EPMDocument parent_epm3d = null;
				if(parent_EPM3D_OID != null && !"".equals(parent_EPM3D_OID)) {
					parent_epm3d = (EPMDocument) CommonUtils.getObject(parent_EPM3D_OID);
					if(parent_epm3d != null) {
						// 3D-체크아웃 체크
						if (WorkInProgressHelper.isCheckedOut(parent_epm3d)) {
							msg.append("상위 3D : Check-Out 된 객체" + "\n");
							type = "E";
						}
					}
					
					//모품번 3D의 LIFE_CYCLE 체크
					if(parent_epm3d != null) {
						if (!"DEFAULT_LIFECYCLE".equals(parent_epm3d.getLifeCycleName()) ) {
							msg.append("모품번 3D의 라이프사이클 불일치 > 마이그레이션 진행해야 합니다." + System.lineSeparator());
							type = "E";
						}
					}
				}
				
				
				
				//
				String pPart_FileName = pPart.getNumber(); // 상위 파일이름
				String cPart_FileName = ""; //cPart.getNumber() == null ? "" : cPart.getNumber(); // 하위 파일이름 (name -> number로 변경함. Library때문에)
				
				if(cPart != null) {
					cPart_FileName = cPart.getNumber() == null ? "" : cPart.getNumber(); // 하위 파일이름 (name -> number로 변경함. Library때문에)
				}
				
				
				
				if(pPart_FileName != null && !"".equals(pPart_FileName) && p_PART_NO != null && !"".equals(p_PART_NO)) {
					pPart_FileName = pPart_FileName.trim();
					if( !pPart_FileName.contains(p_PART_NO) ) {
						LOG.info("모 품번이 다름=" + pPart_FileName + " - " + p_PART_NO);
						msg.append("모품에 입력한 품번과 파일이름의 품번이 다릅니다." + System.lineSeparator());
						msg.append("차이점: " + pPart_FileName + " vs " + p_PART_NO + " 다름.");
						type = "E";
					}
				}
				
				
				if(cPart_FileName != null && !"".equals(cPart_FileName) && c_PART_NO != null && !"".equals(c_PART_NO)) {
					cPart_FileName = cPart_FileName.trim();
					if( !cPart_FileName.contains(c_PART_NO) ) {
						LOG.info("자 품번이 다름=" + cPart_FileName + " - " + c_PART_NO);
						
						msg.append("자품에 입력한 품번과 파일명의 품번이 다릅니다." + System.lineSeparator());
						msg.append("차이점: " + cPart_FileName + " vs " + c_PART_NO + " 다름.");
						type = "E";
					}
				}
				
				
				// 자품 체크
				// "D"가 아닌 경우만 검사
				if(!"D".equals(addType)) {
					
					// 품번 체크 - 자품
					String cVersion = cPart.getVersionIdentifier().getSeries().getValue();
					char cv = cVersion.charAt(0);
					
					
					if (!cPart.isLatestIteration()) {
						msg.append("하위 최신 버전이 아닙니다.");
						type = "E";
					}
					
					// 라이프사이클 체크
					if (!"DEFAULT_LIFECYCLE".equals(cPart.getLifeCycleName()) ) {
						msg.append("하위 라이프사이클 불일치");
						type = "E";
					}
					
					
					if ("".equals(IBAUtils.getStringValue(cPart, "TITLE"))) {
						msg.append("하위 TITLE 없음");
						type = "E";
					}
					
					if ("".equals(IBAUtils.getStringValue(cPart, "PART_NAME"))) {
						msg.append("하위 PART_NAME 없음");
						type = "E";
					}
					
					// 모품-WTPART wrk 체크
					if (WorkInProgressHelper.isWorkingCopy(cPart)) {
						if(WorkInProgressHelper.isCheckedOut(cPart)) {
							WorkInProgressHelper.service.undoCheckout(cPart);
						}
						//msg.append("하위 Working-Copy 된 객체");
						//type = "E";
					}
					
					// 모품-WTPAT 체크아웃 체크
					if (WorkInProgressHelper.isCheckedOut(cPart)) {
						msg.append("하위 Check-Out 된 객체" + System.lineSeparator());
						type = "E";
					}
					
					
					
					//Question) 7430은 알파벳에 작업중이여도 자품쪽으로해서 넘어가도되나?
					//자품에 대해서 검사
					//isKCP_Check
					//KAASY, ENDITEM -> 도면승인
					//PCBA CABLE -> 마이그레이션
					if(HyoSungCommonUtil.isKCP_Check(c_PART_NO)) {
						List<String> PCB = Arrays.asList("7650", "7750", "7760", "7791", "7792");
						List<String> CABLE = Arrays.asList("3200", "3300", "3326", "3328", "3400", "3321", "3325");
						List<String> KASSY = Arrays.asList("4140", "4370", "4371", "4372", "4375", "4520", "4931");
						
						String firstNum = c_PART_NO.substring(0, 4);
						
						//PCBA CABLE -> 마이그레이션
						if(PCB.contains(firstNum) || CABLE.contains(firstNum)) {
							if ("00".equals(cVersion) || Character.isAlphabetic(cv) ) {
								if ("INWORK".equals(c_state) || "RETURN".equals(c_state)) {
									msg.append(c_PART_NO + "-해당 품번은 마이그레이션을 진행해야 합니다.");
									type = "E";
								}
							}
						}
						
						//KAASY -> 도면승인 (ENDITEM도 포함??)
						if(KASSY.contains(firstNum)) {
							if ("00".equals(cVersion) || Character.isAlphabetic(cv) ) {
								if ("INWORK".equals(c_state) || "RETURN".equals(c_state)) {
									msg.append(c_PART_NO + "-해당 품번은 도면승인을 진행해야 합니다.");
									type = "E";
								}
							}
						}
					}
					
					
					
					//단품이면서 버전이 00 or 알파벳일 경우
					if ("component".equals(cPart.getPartType().toString())) {
						if ("00".equals(cVersion) || Character.isAlphabetic(cv) ) {
							if ("INWORK".equals(c_state) || "RETURN".equals(c_state)) {
								type = "E";
								msg.append("해당 EndItem 은 도면승인이 필요합니다.");
							}
						}
					}
					
				} // end if "D"가 아닌 경우만 검사
				
				
				if ("".equals(IBAUtils.getStringValue(cPart, "PART_NO"))) {
					msg.append("하위 PART_NO 없음");
					type = "E";
				}
				
				//ASM인데 하위가 없는 경우??
				
				
				// 상태 체크
				if ("WITHDRAWN".equals(pPart.getState().toString()) || "PROTOTYPE".equals(pPart.getState().toString()) ) {
					msg.append("상위 부품이 등록 가능한 상태가 아닙니다.");
					type = "E";
				}
				
				
				//00이상의 버전일 경우 iteration이 '1'인 경우는 전송하면 안된다. 변경된게 없는것이기 때문에.
				if(pPart != null) {
					if(!"00".equals(pPart.getVersionIdentifier().getValue()) && !"A".equals(pPart.getVersionIdentifier().getValue())  ) {
						if("1".equals(pPart.getIterationIdentifier().getSeries().getValue())) {
							msg.append("WTPART 버전의 첫번째 Iteration은 추가 할 수 없습니다.");
							type = "E";
						}
					}
				}
				
				
				
				String endMsg = msg.toString();
				p_PART_NO = p_PART_NO.trim();
				c_PART_NO = c_PART_NO.trim();
				String errKey = p_PART_NO + "-" + c_PART_NO;
				//checkMap.put(c_PART_NO, endMsg);
				
				
				basicMap.put(errKey, "");
				viewKey.put(errKey, "");
				
				if("E".equals(type)) {
					checkMap.put(errKey, endMsg);
					pdmErrorFlag = "FAIL";
				} else {
					checkMap.put(errKey, "");
				}
				
				//p_PART_NO
			}
			
			
			//System.out.println("1111111 checkMap === " + checkMap); // PDM검증
			
			
			//2차 PLM검증
			PLM_CheckMap = standardBomPLMCheck(param, "X"); // 2차 PLM검증 - SAP
			
			String plmErrorFlag = (String) PLM_CheckMap.get("result");
			
			if(plmErrorFlag.equals("FAIL")) {
				pdmErrorFlag = "FAIL";
				PLM_CheckMap.put("msg", "검증에 실패하였습니다.");
			}
			
			
			ArrayList<String[]> plmErrorList = (ArrayList<String[]>) PLM_CheckMap.get("errorList");
			ArrayList<String[]> ONE_MAT_MODIFY_ERROR = (ArrayList<String[]>) PLM_CheckMap.get("ONE_MAT_MODIFY_ERROR");
			
			
			//1단계에서 발생한 오류 정리
			//모-자가 되지 않아 먼저 셋팅함
			if(ONE_MAT_MODIFY_ERROR != null && ONE_MAT_MODIFY_ERROR.size() > 0) {
				pdmErrorFlag = "FAIL";
				for(int i=0; i < ONE_MAT_MODIFY_ERROR.size(); i++) {
					
					String errOid = ONE_MAT_MODIFY_ERROR.get(i)[0]; // { partOid, AETXT, MESSAGE, MATNR2 };
					String errTxt = ONE_MAT_MODIFY_ERROR.get(i)[1];
					String errMsg = ONE_MAT_MODIFY_ERROR.get(i)[2];
					String errPartNo = ONE_MAT_MODIFY_ERROR.get(i)[3];
					
					if(errPartNo != null && !"".equals(errPartNo)) errPartNo = errPartNo.trim();
					
					for(String key : checkMap.keySet()) {
						
						if(key.contains(errPartNo)) {
							String emsg = basicMap.get(key);
							//emsg = errMsg;
							//basicMap.put(key, emsg);
							
							String oMsg = checkMap.get(key);
							
							if(!"".equals(oMsg)) {
								oMsg += "\n " +  errMsg;
							} else {
								oMsg += errMsg;
							}
							
							checkMap.put(key, oMsg);
						}
					}
					
					System.out.println("ONE_MAT_MODIFY_ERROR :: " + errOid + " > " + errTxt + " > " + errMsg + " > " + errPartNo);
				}
			}
			
			
			//PDM에서 체크한 MAP으로
			//PLM나머지 단계 에러와 합치기
			if(plmErrorList != null &&  plmErrorList.size() > 0) {
				
				for (int i = 0; i < plmErrorList.size(); i++) {
					String plmError = plmErrorList.get(i)[2];
					String ppNo = plmErrorList.get(i)[3]; // pNO
					String ccNo = plmErrorList.get(i)[4]; // cNo
					ppNo = ppNo.trim();
					ccNo = ccNo.trim();
					
					String errKey = ppNo + "-" + ccNo;
					//LOG.info(ppNo + " --- " + ccNo + " >>> " + plmError);
					
					if(checkMap.containsKey(errKey)) {
						String msg = checkMap.get(errKey);
						if(!"".equals(msg)) {
							msg += "\n " +  plmError;
						} else {
							msg += plmError;
						}
						
						checkMap.put(errKey, msg);
					}
				}
			}
			
			
			
			PLM_CheckMap.put("checkedList", checkMap); // PDM + PLM 오류 통합
			PLM_CheckMap.put("ONE_MAT_MODIFY_ERROR", basicMap);
			PLM_CheckMap.put("basicMap", basicMap);
			PLM_CheckMap.put("viewKey", viewKey);
			
			LOG.trace("222222222222222222222222 최종출력 부분 ALL checkMap ");
			
			if(checkMap != null && checkMap.size() > 0) {
				for(String key : checkMap.keySet()) {
					LOG.trace(key + " --- " + checkMap.get(key));
				}
			}
			PLM_CheckMap.put("result", pdmErrorFlag); // PDM + PLM 오류 체크 > FAIL, SUCCESS
			//PLM_CheckMap.put("result", "SUCCESS");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", "FAIL");
			map.put("msg", MessageUtils.getMessage("epm.message.action12")+"\n"+e.getMessage());
		}
		
		//System.out.println("checkMap === " + checkMap);
		 
		System.out.println("### standardBomCheck END !! ###");
		return PLM_CheckMap;
	}
	
	
	
	//표준BOM > '체크'클릭 > 2차 검증(PLM) 및 전송
	public static Map<String, Object> standardBomPLMCheck(Map<String, Object> param, String valid) {
		LOG.debug("---------- standardBomPLMCheck START --------param = ");
		LOG.trace("param ={}", param);
		
		Map<String, Object> result = new HashMap<>();
		
		ArrayList<String[]> ttList = new ArrayList<String[]>();
		
		ArrayList<String[]> sendPartList = new ArrayList<String[]>();
		ArrayList<String[]> chkPartList = new ArrayList<String[]>();
		
		List<String> partOids = (List<String>) param.get("partOids");
		List<String> pTypes = (List<String>) param.get("pTypes");
		List<String> rootOids = (List<String>) param.get("rootOids");
		String plant = (String) param.get("plant"); 
		String appEcoNo = (String) param.get("appEcoNo"); 
		String totalECO = (String) param.get("totalECO");
		List<String> cOids = (List<String>) param.get("cOids");
		List<String> qtys = (List<String>) param.get("cQTYs");
		List<String> units = (List<String>) param.get("units");
		List<String> partSeqs = (List<String>) param.get("partSeqs");
		String totalEcoFlag = (String) param.get("totalEcoFlag");
		
		
		String I_AENNR = appEcoNo;
		String I_WERKS = plant; // PLANT
		//String I_ZVALID = "X"; // 검증 O
		String I_ZVALID = valid; // 검증 O
		
		//System.out.println("units == " + units);
		
		
		try {
			
			HashMap<String, String> duplicateInfo = new HashMap<>();
			
			for (int i = 0; i < partOids.size(); i++) {
				String rootOid = (String) partOids.get(0);
				String parentOid = partOids.get(i);
				String childOid  = cOids.get(i);
				
				String types = (String) pTypes.get(i);
				String pOid = (String) partOids.get(i);
				String cOid = (String) cOids.get(i);
				String cqty = (String) qtys.get(i);
				String unit = (String) units.get(i);
				
				WTPart pPart = (WTPart) CommonUtils.getObject(parentOid);
				//WTPart cPart = (WTPart) CommonUtils.getObject(childOid);
				
				String partSeq = (String) partSeqs.get(i);
				String[] partList = new String[] { types, pOid, cOid, cqty, unit, rootOid, partSeq };
				String p_PartNo = IBAUtils.getAttrValue(pPart, "PART_NO");
				
				if(p_PartNo != null && !"".equals(p_PartNo)) p_PartNo = p_PartNo.trim();
				
				
				// @@@ 1. ZPLM_PDMLINK_MAT_BASIC 검사 > "CR, RE"
				// CR > TRUE , RE > FALSE
				//boolean sendChk = SapRfcService.matModifyMBOM_Check(p_PartNo, plant);
				boolean sendChk = SapRfcService.matModifyMBOM_Check_V1(p_PartNo, plant, duplicateInfo);
				
				if(sendChk ) { 
					sendPartList.add(partList);
				}
				chkPartList.add(partList);
			}
			
			LOG.debug("sendPartList.size ==={} " + sendPartList.size());
			LOG.debug("chkPartList.size ==={} " + chkPartList.size());
			
			Map<String, Object> sapMap = new HashMap<String, Object>();
			//sapMap.put("ROOTMATNR", ROOTMATNR); // 사용안함
			
			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String I_ERNAM = EpmHelper.manager.getSAPConnectUser(sessionUser);
			
			
			
			sapMap.put("I_WERKS", I_WERKS);  // PLANT
			sapMap.put("I_AENNR", I_AENNR); // ECONO
			sapMap.put("I_ERNAM", I_ERNAM); // 사용자
			sapMap.put("I_ZVALID", I_ZVALID);
			
			if( totalEcoFlag != null && !"".equals(totalEcoFlag) && "TRUE".equals(totalEcoFlag.toUpperCase()) ) {
				sapMap.put("I_INTEGRATED", "X");
			}
			
			Map<String, Object> returnMap = null;
			ArrayList<String[]> relist = null;
			ArrayList<String> ONE_MAT_MODIFY_ERROR = null; // 1단계
			ArrayList<String[]> errorList00 = new ArrayList<String[]>();
			String type = "";
			String message = "";
			
			LOG.debug("## standardBomPLMCheck :: sapMap === {}" , sapMap);
			
			
			// CR 전송
			if( sendPartList.size() > 0 ) {
				sapMap.put("BOMList", sendPartList);
				// 1. ZPLM_PDMLINK_ECO_MAT_MODIFY :: 부품 승인 즉, 설계 검도 부품 List를 지정된 ECO의 Object - Material List에 반영하는 Interface
				
				
				// SapRfcService.matModifyMBOM(sapMap);
				returnMap = SapRfcService.matModifyMBOM(sapMap);
				//System.out.println("matModifyMBOM --> " + returnMap);
				
				relist = (ArrayList<String[]>) returnMap.get("list"); // 전체 리스트 { partOid, AETXT, MESSAGE };
				errorList00 = (ArrayList<String[]>) returnMap.get("errorList01"); //오류 리스트
				ONE_MAT_MODIFY_ERROR = (ArrayList<String>) returnMap.get("1-STEP-MAT_MODIFY-ERROR");
				
				//ECO_MAT_MODIFY_ERROR_LIST
				
				//1-STEP-MAT_MODIFY-ERROR
				type = (String) returnMap.get("type"); // 전체 결과 TYPE
				message = (String) returnMap.get("message"); // 전체 결과의 MESSAGE
			}
			
			
			Map<String, Object> returnMap2 = null;
			String type2 = "";
			String message2 = "";
			ArrayList<String[]> relist2 = null;
			
			ArrayList<String[]> list01 = new ArrayList<String[]>();
			ArrayList<String[]> list02 = new ArrayList<String[]>();
			
			ArrayList<String[]> errorList = new ArrayList<String[]>();
			ArrayList<String[]> createErrorList = new ArrayList<String[]>();
			ArrayList<String[]> modifyErrorList = new ArrayList<String[]>();
			
			//RE이 부품들
			if (chkPartList.size() > 0) {
				// SapRfcService.bomModifySeqMBOM(sapMap); 
				sapMap.put("BOMList", chkPartList);
				//returnMap2 = SapRfcService.bomModifySeqMBOM(sapMap); //
				returnMap2 = StandardBOMSapRfcService.bomModifySeqMBOM_V1(sapMap); // MODIFY : RE일 경우
				
				LOG.debug("bomModifySeqMBOM_V1 = {} ", returnMap2);
				
				relist2 = (ArrayList<String[]>) returnMap2.get("list"); // 에러 리스트
				
				
				errorList = (ArrayList<String[]>) returnMap2.get("errorList");
				createErrorList = (ArrayList<String[]>) returnMap2.get("errorList01"); // CREATE 에러 errorList ,  { partOid, AETXT, MESSAGE, MATNR2, IDNRK2 };
				modifyErrorList = (ArrayList<String[]>) returnMap2.get("errorList02"); // MODIFY 에러 리스트  ,  { partOid, AETXT, MESSAGE, MATNR2, IDNRK2 };
				type2 = (String) returnMap2.get("type");
				message2 = (String) returnMap2.get("message");
			}
			
			
			//에러 리스트 합치기
			if(createErrorList != null && createErrorList.size() > 0) {
				for (int i = 0; i < createErrorList.size(); i++) {
					String oo = createErrorList.get(i)[2];
					String ee = createErrorList.get(i)[3]; // pNO
					String mm = createErrorList.get(i)[4]; // cNo
					System.out.println(oo + " - " + ee + " - " + mm );
				}
			}
			
			if(relist2 != null && relist2.size() > 0) {
				for (int i = 0; i < relist2.size(); i++) {
					String oo = relist2.get(i)[0];
					String ee = relist2.get(i)[3]; // pNO
					String mm = relist2.get(i)[4]; // cNo
					System.out.println(oo + " - " + ee + " - " + mm );
				}
			}
			
			
			//type2 = "E"; // 일부러 에러
			
			//타입검사
			if ("E".equals(type) || "E".equals(type2)) {
				result.put("result", "FAIL");
				result.put("msg", MessageUtils.getMessage("epm.message.action12")+"\n"+message+","+message2);
				
				//오류 리스트 정리
				ttList = PartHelper.manager.getMessageList(relist, relist2);
				result.put("list", ttList);
				result.put("list01", list01);
				

				//1단계 에러
				result.put("ONE_MAT_MODIFY_ERROR", ONE_MAT_MODIFY_ERROR);
				result.put("errorList00", errorList00);
				
				//2단계 에러
				result.put("errorList", errorList); // CREATE + MODIFY 에러 리스트
				result.put("errorList01", createErrorList); // CREATE 에러 리스트
				result.put("errorList02", modifyErrorList); // MODIFY 에러 리스트
				
				result.put("relist2", relist2); // 전체 오류 리스트(CREATE + MODIFY)
			} else {
				result.put("result", "SUCCESS");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.debug("---------- result = {}" , result);
		LOG.debug("---------- standardBomPLMCheck END ----------  ");
		return result;   // --> StandardBOMService로 Go~!
	}
	
	
	//tempoStandardBOM
	//표준BOM - 임시저장
	public static Map<String, Object> tempoStandardBOM(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<>();
		
		
		List<String> appList = (List<String>) param.get("appList");
		List<String> epmAppOids = (List<String>) param.get("epmAppOids");
		List<String> rootOids = (List<String>) param.get("rootOids");
		List<String> partOids = (List<String>) param.get("partOids");
		List<String> cOids = (List<String>) param.get("cOids");
		List<String> pTypes = (List<String>) param.get("pTypes");
		List<String> qtys = (List<String>) param.get("qtys");
		List<String> units = (List<String>) param.get("units");
		List<String> partSeqs = (List<String>) param.get("partSeqs");
		
		String appEcoNo = (String) param.get("appEcoNo"); // ECONO
		String ebomName = (String) param.get("appName");
		String ebomType = (String) param.get("ebomType"); // 초도여부
		String ebomplant = (String) param.get("plant");
		String totalEcoFlag = (String) param.get("totalEcoFlag"); // 통합여부
		
		
		LOG.debug("rootOids -- " + rootOids.size());
		LOG.debug("partOids -- " + partOids.size());
		LOG.debug("cOids -- " + cOids.size());
		LOG.debug("pTypes -- " + pTypes.size());
		LOG.debug("qtys -- " + qtys.size());
		LOG.debug("units -- " + units.size());
		LOG.debug("epartSeq -- " + partSeqs.size());
		
		String desc = "";
		if (!"".equals((String) param.get("ebomDesc"))) {
			desc = (String) param.get("ebomDesc");
		}
		
		EBOMObj ebomObj = null;
		
		Transaction trs = new Transaction();
		try {
			
			if( totalEcoFlag != null && !"".equals(totalEcoFlag) && "TRUE".equals(totalEcoFlag.toUpperCase()) ) {
				totalEcoFlag = "true";
			} else {
				String returnEcono = HyoSungCommonUtil.findEPM_totalECO(appEcoNo.trim());
				
				if(returnEcono != null && "true".equals(returnEcono)) {
					totalEcoFlag = "true";
				} else {
					totalEcoFlag = "";
				}
			}
			
			
			//EBOM번호 생성
			String number = PartHelper.manager.getNextNumberEBM("EBM");
			
			
			//초도여부 체크?
			
			
			
			// 1) EBOMObj 객체 저장
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);
			ebomObj = EBOMObj.newEBOMObj();
			ebomObj.setNumber(number);
			ebomObj.setName(ebomName);
			ebomObj.setPlant(ebomplant);
			ebomObj.setEbomType("standard"); // 표준BOM   //초도여부
			ebomObj.setEtc02("standard");
			ebomObj.setDescription(desc);
			ebomObj.setEcoNo(appEcoNo);    // ECONO - 표준BOM
			//ebomObj.setEcoName(ecoName); // ECONO NAME - 표준
			ebomObj.setOwnership(ownership);
			ebomObj.setState("임시저장");
			ebomObj.setInsideState("임시저장");
			ebomObj.setTotalECO(totalEcoFlag);
			
			ebomObj = (EBOMObj) PersistenceHelper.manager.save(ebomObj);
			
			// 2) EBOMLink 연결 
			DecimalFormat format = new DecimalFormat("0000");
			int bomseq = 1;

			if (pTypes.size() > 0) {
				for (int i = 0; i < pTypes.size(); i++) {
					String rootOid = (String) rootOids.get(i);
					String type = (String) pTypes.get(i);
					String pOid = (String) partOids.get(i);
					String cOid = (String) cOids.get(i);
					String qty = (String) qtys.get(i);
					String unit = (String) units.get(i);
					String bomseqStr = format.format(bomseq);
					String seq = (String) partSeqs.get(i);
					
					PartHelper.manager.EBOMLinkHelper(
							ebomObj, type, pOid, cOid, ebomplant, 
							qty, unit, rootOid, bomseqStr, seq
							);
					bomseq++;
				}
			}
			
			
			// 3) 도면승인 연결
			if (null != epmAppOids) {
				if (epmAppOids.size() > 0) {
					for (int i = 0; i < epmAppOids.size(); i++) {
						String epmAppOid = (String) epmAppOids.get(i);
						PartHelper.manager.EBOMEPMAppLinkHelper(ebomObj, epmAppOid);
					}
				}
			}
			
			// 4) 결재자 세팅
			if (appList.size() > 0) {
				ApprovalHelper.service.imsiSubmitApp(ebomObj, param);
			}
			
			//파일첨부
			ContentUtils.updateSecondary(param, ebomObj);
			
			
			result.put("result", "SUCCESS");
			result.put("url", "/Windchill/plm/approval/listImsi");
			trs.commit();
		} catch (Exception e) {
			result.put("result", "FAIL");
			result.put("msg", MessageUtils.getMessage("common.message.tempCreateFail")+"\n"+e.toString());
			result.put("url", "/Windchill/plm/part/approvalEBOM");
			e.printStackTrace();
		} finally {
			if (trs != null) {
				trs.rollback();				
			}
		}
		
		return result;
	}
	
	
	
	//임시저장에서 수정(임시저장)
	public static Map<String, Object> tempoModifyStandardBOM(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<>();
		
		
		List<String> appList = (List<String>) param.get("appList");
		List<String> epmAppOids = (List<String>) param.get("epmAppOids");
		List<String> rootOids = (List<String>) param.get("rootOids");
		List<String> partOids = (List<String>) param.get("partOids");
		List<String> cOids = (List<String>) param.get("cOids");
		List<String> pTypes = (List<String>) param.get("pTypes");
		List<String> qtys = (List<String>) param.get("qtys");
		List<String> units = (List<String>) param.get("units");
		List<String> partSeqs = (List<String>) param.get("partSeqs");
		
		String soid = (String) param.get("soid");
		String appEcoNo = (String) param.get("appEcoNo"); // ECONO
		String ebomName = (String) param.get("appName");
		String ebomType = (String) param.get("ebomType"); // 초도여부
		String ebomplant = (String) param.get("plant");
		String totalEcoFlag = (String) param.get("totalEcoFlag"); // 통합여부
		
		if( totalEcoFlag != null && !"".equals(totalEcoFlag) && "TRUE".equals(totalEcoFlag.toUpperCase()) ) {
			totalEcoFlag = "true";
		} else {
			String returnEcono = HyoSungCommonUtil.findEPM_totalECO(appEcoNo.trim());
			
			if(returnEcono != null && "true".equals(returnEcono)) {
				totalEcoFlag = "true";
			} else {
				totalEcoFlag = "";
			}
		}
		
		String desc = "";
		if (!"".equals((String) param.get("ebomDesc"))) {
			desc = (String) param.get("ebomDesc");
		}
		
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		
		try {
			trs.start();
			
			EBOMObj ebomObj = (EBOMObj) rf.getReference(soid).getObject(); //EBOM 생성
			
			// 1) EBOMObj 객체 저장
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);
			//ebomObj = EBOMObj.newEBOMObj();
			//ebomObj.setNumber(number);
			ebomObj.setName(ebomName);
			ebomObj.setPlant(ebomplant);
			ebomObj.setEbomType("standard"); // 표준BOM   //초도여부
			ebomObj.setEtc02("standard");
			ebomObj.setDescription(desc);
			ebomObj.setEcoNo(appEcoNo);    // ECONO - 표준BOM
			//ebomObj.setEcoName(ecoName); // ECONO NAME - 표준
			ebomObj.setOwnership(ownership);
			ebomObj.setState("임시저장");
			ebomObj.setInsideState("임시저장");
			ebomObj.setTotalECO(totalEcoFlag);
			
			ebomObj = (EBOMObj) PersistenceHelper.manager.modify(ebomObj);
			
			
			PartHelper.manager.deleteEBOMLink(ebomObj); //BOM정보 링크삭제
			DecimalFormat format = new DecimalFormat("0000");
			int bomseq = 1;
			if (pTypes.size() > 0) {
				for (int i = 0; pTypes != null && i < pTypes.size(); i++) {
					String rootOid = (String) rootOids.get(i);
					String type = (String) pTypes.get(i);
					String pOid = (String) partOids.get(i);
					String cOid = (String) cOids.get(i);
					String qty = (String) qtys.get(i);
					String unit = (String) units.get(i);
					String bomseqStr = format.format(bomseq);
					
					String seq = (String)partSeqs.get(i);
					
					PartHelper.manager.EBOMLinkHelper(ebomObj, type, pOid, cOid, ebomplant, qty, unit, rootOid,
							bomseqStr, seq);
					bomseq++;
				}
			}
			
			PartHelper.manager.deleteEBOMEPMAppLink(ebomObj); // 도면승인 연결정보 삭제
			if (null != epmAppOids) {
				if (epmAppOids.size() > 0) {
					for (int i = 0; i < epmAppOids.size(); i++) {
						String epmAppOid = (String) epmAppOids.get(i);
						PartHelper.manager.EBOMEPMAppLinkHelper(ebomObj, epmAppOid);
					}
				}
			}
			
			
			if (appList.size() > 0) {
				//결재라인 삭제 및 새로 셋팅
				ApprovalHelper.service.imsiSubmitApp(ebomObj, param); 
			}
			
			ContentUtils.updateSecondary(param, ebomObj);
			result.put("result", "SUCCESS");
			result.put("url", "/Windchill/plm/approval/listImsi"); //임시저장 상세화면
			trs.commit();
			trs = null;
		} catch (Exception e) {
			result.put("result", "FAIL");
			result.put("msg", MessageUtils.getMessage("common.message.tempCreateFail")+"\n"+e.getMessage());
			//result.put("url", "/Windchill/plm/part/modifyEBOM");
			result.put("url", "/Windchill/plm/standard/modifyStandardEBOM"); // 표준BOM 상세화면
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		
		
		return result;
	}
	
	
	//createStandardBOM
	//등록화면에서 상신
	/**
	 * @category 
	 * @param param
	 * @return
	 */
	public static Map<String, Object> createStandardBOM(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<>();
		
		List<String> appList = (List<String>) param.get("appList");
		List<String> epmAppOids = (List<String>) param.get("epmAppOids");
		List<String> rootOids = (List<String>) param.get("rootOids");
		List<String> partOids = (List<String>) param.get("partOids");
		List<String> cOids = (List<String>) param.get("cOids");
		List<String> pTypes = (List<String>) param.get("pTypes");
		List<String> qtys = (List<String>) param.get("qtys");
		List<String> units = (List<String>) param.get("units");
		List<String> partSeqs = (List<String>) param.get("partSeqs");
		
		String appEcoNo = (String) param.get("appEcoNo"); // ECONO
		String ebomName = (String) param.get("appName");
		String ebomType = (String) param.get("ebomType"); // 초도여부
		String ebomplant = (String) param.get("plant");
		String totalEcoFlag = (String) param.get("totalEcoFlag"); // 통합여부
		
		LOG.debug("rootOids -- " + rootOids.size());
		LOG.debug("partOids -- " + partOids.size());
		LOG.debug("cOids -- " + cOids.size());
		LOG.debug("pTypes -- " + pTypes.size());
		LOG.debug("qtys -- " + qtys.size());
		LOG.debug("units -- " + units.size());
		LOG.debug("epartSeq -- " + partSeqs.size());
		
		String desc = "";
		if (!"".equals((String) param.get("ebomDesc"))) {
			desc = (String) param.get("ebomDesc");
		}
		
		EBOMObj ebomObj = null;
		
		Transaction trs = new Transaction();
		try {
			
			if( totalEcoFlag != null && !"".equals(totalEcoFlag) && "TRUE".equals(totalEcoFlag.toUpperCase()) ) {
				totalEcoFlag = "true";
			} else {
				
				//한번더검사
				String returnEcono = HyoSungCommonUtil.findEPM_totalECO(appEcoNo.trim());
				
				if(returnEcono != null && "true".equals(returnEcono)) {
					totalEcoFlag = "true";
				} else {
					totalEcoFlag = "";
				}
				
			}
			
			
			//EBOM번호 생성
			String number = PartHelper.manager.getNextNumberEBM("EBM");
			
			
			//초도여부 체크?
			
			
			
			// 1) EBOMObj 객체 저장
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);
			ebomObj = EBOMObj.newEBOMObj();
			ebomObj.setNumber(number);
			ebomObj.setName(ebomName);
			ebomObj.setPlant(ebomplant);
			ebomObj.setEbomType("standard"); // 표준BOM에서 구분값   //초도여부
			ebomObj.setEtc02("standard");
			ebomObj.setDescription(desc);
			ebomObj.setEcoNo(appEcoNo);    // ECONO - 표준BOM
			//ebomObj.setEcoName(ecoName); // ECONO NAME - 표준
			ebomObj.setOwnership(ownership);
			ebomObj.setState("결재 중");
			ebomObj.setInsideState("결재 중");
			ebomObj.setTotalECO(totalEcoFlag);
			
			ebomObj = (EBOMObj) PersistenceHelper.manager.save(ebomObj);
			
			// 2) EBOMLink 연결 
			DecimalFormat format = new DecimalFormat("0000");
			int bomseq = 1;

			if (pTypes.size() > 0) {
				for (int i = 0; i < pTypes.size(); i++) {
					String rootOid = (String) rootOids.get(i);
					String type = (String) pTypes.get(i);
					String pOid = (String) partOids.get(i);
					String cOid = (String) cOids.get(i);
					String qty = (String) qtys.get(i);
					String unit = (String) units.get(i);
					String bomseqStr = format.format(bomseq);
					String seq = (String) partSeqs.get(i);
					
					PartHelper.manager.EBOMLinkHelper(
							ebomObj, type, pOid, cOid, ebomplant, 
							qty, unit, rootOid, bomseqStr, seq
							);
					bomseq++;
				}
			}
			
			
			// 3) 도면승인 연결
			if (null != epmAppOids) {
				if (epmAppOids.size() > 0) {
					for (int i = 0; i < epmAppOids.size(); i++) {
						String epmAppOid = (String) epmAppOids.get(i);
						PartHelper.manager.EBOMEPMAppLinkHelper(ebomObj, epmAppOid);
					}
				}
			}
			
			// 4) 결재자 세팅
			if (appList.size() > 0) {
				ApprovalHelper.service.submitApp(ebomObj, param); // submitApp: 상신
			}
			
			//파일첨부
			ContentUtils.updateSecondary(param, ebomObj);
			
			
			result.put("result", "SUCCESS");
			result.put("url", "/Windchill/plm/part/listEBOM");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			result.put("result", "FAIL");
			result.put("msg", MessageUtils.getMessage("common.message.tempCreateFail")+"\n"+e.toString());
			result.put("url", "/Windchill/plm/part/approvalEBOM");
			trs.rollback();
			e.printStackTrace();
		} finally {
			if (trs != null) {
				trs.rollback();				
			}
		}
		
		return result;
	}
	
	
	//임시저장에서 상신
	public static Map<String, Object> tempoCreateStandardBOM(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<>();
		
		List<String> appList = (List<String>) param.get("appList");
		List<String> epmAppOids = (List<String>) param.get("epmAppOids");
		List<String> rootOids = (List<String>) param.get("rootOids");
		List<String> partOids = (List<String>) param.get("partOids");
		List<String> cOids = (List<String>) param.get("cOids");
		List<String> pTypes = (List<String>) param.get("pTypes");
		List<String> qtys = (List<String>) param.get("qtys");
		List<String> units = (List<String>) param.get("units");
		List<String> partSeqs = (List<String>) param.get("partSeqs");
		
		String soid = (String) param.get("soid");
		String appEcoNo = (String) param.get("appEcoNo"); // ECONO
		String ebomName = (String) param.get("appName");
		String ebomType = (String) param.get("ebomType"); // 초도여부
		String ebomplant = (String) param.get("plant");
		String totalEcoFlag = (String) param.get("totalEcoFlag"); // 통합여부
		
		LOG.debug("rootOids -- " + rootOids.size());
		LOG.debug("partOids -- " + partOids.size());
		LOG.debug("cOids -- " + cOids.size());
		LOG.debug("pTypes -- " + pTypes.size());
		LOG.debug("qtys -- " + qtys.size());
		LOG.debug("units -- " + units.size());
		LOG.debug("epartSeq -- " + partSeqs.size());
		
		String desc = "";
		if (!"".equals((String) param.get("ebomDesc"))) {
			desc = (String) param.get("ebomDesc");
		}
		
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		
		try {
			trs.start();
			
			if( totalEcoFlag != null && !"".equals(totalEcoFlag) && "TRUE".equals(totalEcoFlag.toUpperCase()) ) {
				totalEcoFlag = "true";
			} else {
				String returnEcono = HyoSungCommonUtil.findEPM_totalECO(appEcoNo.trim());
				
				if(returnEcono != null && "true".equals(returnEcono)) {
					totalEcoFlag = "true";
				} else {
					totalEcoFlag = "";
				}
			}

			EBOMObj ebomObj = (EBOMObj) rf.getReference(soid).getObject(); //EBOM 생성
			
			// 1) EBOMObj 객체 저장
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);
			ebomObj.setNumber(ebomObj.getNumber());
			ebomObj.setName(ebomName);
			ebomObj.setPlant(ebomplant);
			ebomObj.setEbomType("standard"); // 표준BOM   //초도여부
			ebomObj.setEtc02("standard");
			ebomObj.setDescription(desc);
			ebomObj.setEcoNo(appEcoNo);    // ECONO - 표준BOM
			//ebomObj.setEcoName(ecoName); // ECONO NAME - 표준
			ebomObj.setOwnership(ownership);
			ebomObj.setState("결재 중");
			ebomObj.setInsideState("결재 중");
			ebomObj.setTotalECO(totalEcoFlag);
			
			ebomObj = (EBOMObj) PersistenceHelper.manager.modify(ebomObj);
		
			
			PartHelper.manager.deleteEBOMLink(ebomObj); //BOM정보 삭제
			
			DecimalFormat format = new DecimalFormat("0000");
			int bomseq = 1;
			if (pTypes.size() > 0) {
				for (int i = 0; pTypes != null && i < pTypes.size(); i++) {
					String rootOid = (String) rootOids.get(i);
					String type = (String) pTypes.get(i);
					String pOid = (String) partOids.get(i);
					String cOid = (String) cOids.get(i);
					String qty = (String) qtys.get(i);
					String unit = (String) units.get(i);
					String bomseqStr = format.format(bomseq);
					
					String seq = (String)partSeqs.get(i);
					
					PartHelper.manager.EBOMLinkHelper(ebomObj, type, pOid, cOid, ebomplant, qty, unit, rootOid,
							bomseqStr, seq);
					bomseq++;
				}
			}
			
			
			PartHelper.manager.deleteEBOMEPMAppLink(ebomObj); // 도면승인 연결정보 삭제
			if (null != epmAppOids) {
				if (epmAppOids.size() > 0) {
					for (int i = 0; i < epmAppOids.size(); i++) {
						String epmAppOid = (String) epmAppOids.get(i);
						PartHelper.manager.EBOMEPMAppLinkHelper(ebomObj, epmAppOid);
					}
				}
			}
			
			// 4) 결재자 세팅
			if (appList.size() > 0) {
				ApprovalHelper.service.submitApp(ebomObj, param); 
			}
			
						
			// 파일첨부
			ContentUtils.updateSecondary(param, ebomObj);
			
			result.put("result", "SUCCESS");
			result.put("url", "/Windchill/plm/part/listEBOM");
			trs.commit();
		} catch (Exception e) {
			result.put("result", "FAIL");
			result.put("msg", MessageUtils.getMessage("common.message.tempCreateFail")+"\n"+e.toString());
			result.put("url", "/Windchill/plm/part/approvalEBOM");
			e.printStackTrace();
		} finally {
			if (trs != null) {
				trs.rollback();				
			}
		}
		
		return result;
	}
	
	//결재자 최종 승인 후, PLM I/F
	public static String sendStandardBOMtoPLM(EBOMObj ebom) {
	
		LOG.debug(" -------- sendStandardBOMtoPLM ------");
		Map<String, Object> returnMap = new HashMap<>();
		
		List<String> partOids = new ArrayList<String>();
		List<String> pTypes = new ArrayList<String>();   // (List<String>) param.get("pTypes");
		List<String> rootOids = new ArrayList<String>(); //(List<String>) param.get("rootOids");
		String plant    = ebom.getPlant(); // (String) param.get("plant"); 
		String appEcoNo = ebom.getEcoNo(); // (String) param.get("appEcoNo"); 
		//String totalECO = ebom.gete (String) param.get("totalECO");
		List<String> cOids    = new ArrayList<String>(); //(List<String>) param.get("cOids");
		List<String> qtys     = new ArrayList<String>(); //(List<String>) param.get("cQTYs");
		List<String> units    = new ArrayList<String>(); // (List<String>) param.get("units");
		List<String> partSeqs = new ArrayList<String>(); // (List<String>) param.get("partSeqs");
		String totalEcoFlag   = ebom.getTotalECO();      //(String) param.get("totalEcoFlag");
		
		//Map<String, Object> param = new HashMap<String, Object>();
		LinkedHashMap<String, Object> param = new LinkedHashMap<>();
		
		String plmErrorFlag = "";
		
		try {
			
			long ebomlid = CommonUtils.getOIDLongValue(ebom);
			
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(EBOMObjWTPartLink.class, true);
			SearchCondition sc = new SearchCondition(EBOMObjWTPartLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, ebomlid);
			query.appendWhere(sc, new int[] {idx});
			
			ClassAttribute ca = new ClassAttribute(EBOMObjWTPartLink.class, EBOMObjWTPartLink.BOM_SEQ);
			OrderBy by = new OrderBy(ca, false);
			query.appendOrderBy(by, new int[] { idx });
			
			QueryResult result = PersistenceHelper.manager.find(query);
			
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EBOMObjWTPartLink link = (EBOMObjWTPartLink) obj[0];
				WTPart pPart = (WTPart)CommonUtils.getObject(link.getParentOid());
				WTPart cPart = (WTPart)CommonUtils.getObject(link.getPartOid());
				
				String rootOid   = link.getRootOid();
				String parentOid = link.getParentOid();
				String childOid = link.getPartOid();
				
				String ebomType = link.getEbomType();
				String ebomQty  = link.getEbomQty();
				String ebomUnit = link.getEbomUnit();
				String partSeq  = link.getBomLinkSeq();
				
				rootOids.add(rootOid);
				partOids.add(parentOid);
				cOids.add(childOid);
				
				pTypes.add(ebomType);
				qtys.add(ebomQty);
				units.add(ebomUnit);
				partSeqs.add(partSeq);
			}
			
			
			param.put("rootOids", partOids);
			param.put("partOids", partOids);
			param.put("cOids", cOids);
			
			param.put("pTypes", pTypes);
			param.put("cQTYs", qtys);
			param.put("units", units);
			param.put("partSeqs", partSeqs);
			
			param.put("plant", plant);
			param.put("appEcoNo", ebom.getEcoNo());
			//param.put("totalECO", plant);
			param.put("totalEcoFlag", totalEcoFlag);
			param.put("process", ebom.getNumber());
			
			LOG.info("@@ sendStandardBOMtoPLM Send ---> " + ebom + " , " + totalEcoFlag);
			Map<String, Object> PLM_CheckMap = standardBomPLMCheck(param, ""); // PLM 전송
			plmErrorFlag = (String) PLM_CheckMap.get("result");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return plmErrorFlag;
	}
		
		
	//표준BOM 조회화면에서 조회
	public Map<String, Object> findStandardBOM(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		QuerySpec query = null;

		// search param
		String name = (String) param.get("name");
		String number = (String) param.get("number");

		String plant = (String) param.get("plant");
		String description = (String) param.get("description");

		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String econoVal = (String) param.get("econoVal");
		
		
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		String states = (String) param.get("states");
		String module = (String) param.get("module");
		String bomSendType = (String) param.get("bomSendType");
		ReferenceFactory rf = new ReferenceFactory();

		try {
			query = new QuerySpec();

			int ebomidx = query.appendClassList(EBOMObj.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

		/*	//표준BOM 구분값
			if (true)
			{
				sc = new SearchCondition(EBOMObj.class, EBOMObj.EBOM_TYPE, SearchCondition.EQUAL, "standard");
				ca = new ClassAttribute(EBOMObj.class, EBOMObj.EBOM_TYPE);
				ColumnExpression ce = StringUtils.getColumnExpression("standard");
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { ebomidx });
			}*/
			
			//표준BOM 구분값
			sc = new SearchCondition(EBOMObj.class, EBOMObj.EBOM_TYPE, SearchCondition.EQUAL, "standard");
			query.appendWhere(sc, new int[] { ebomidx });
			
			
			// EBOM 제목
			if (!StringUtils.isEmpty(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(EBOMObj.class, EBOMObj.NAME);
				ColumnExpression ce = StringUtils.getColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			// EBOM POPUP 상태값 제한
			if (!StringUtils.isEmpty(module) && "list_ebomobj".equals(module)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				String[] stateCol = { "결재 중", "릴리즈됨" };
				ca = new ClassAttribute(EBOMObj.class, EBOMObj.STATE);

				sc = new SearchCondition(EBOMObj.class, EBOMObj.STATE, stateCol, false);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			// 대소문자 구분 EBOM 번호
			if (!StringUtils.isEmpty(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(EBOMObj.class, EBOMObj.NUMBER);
				ColumnExpression ce = StringUtils.getColumnExpression(number);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			
			// ECONO
			if(econoVal != null && !"".equals(econoVal)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				
				sc = new SearchCondition(EBOMObj.class, EBOMObj.ECO_NO, SearchCondition.EQUAL, econoVal);
				query.appendWhere(sc, new int[] { ebomidx });
			}
			
			
			
			// plant
			if (!StringUtils.isEmpty(plant)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(EBOMObj.class, EBOMObj.PLANT);
				ColumnExpression ce = StringUtils.getColumnExpression(plant);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { ebomidx });
			}
			
			

			// description
			if (!StringUtils.isEmpty(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(EBOMObj.class, EBOMObj.DESCRIPTION);
				ColumnExpression ce = StringUtils.getColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			// 생성자
			if (!StringUtils.isEmpty(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				User user = (User) rf.getReference(creatorsOid).getObject();
				long ids = user.getWtuser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(EBOMObj.class, "ownership.owner.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			// 수정자
			if (!StringUtils.isEmpty(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				User user = (User) rf.getReference(modifierOid).getObject();
				long ids = user.getWtuser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(EBOMObj.class, "ownership.owner.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			// 생성일
			if (!StringUtils.isEmpty(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(EBOMObj.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			if (!StringUtils.isEmpty(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(EBOMObj.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			// 수정일
			if (!StringUtils.isEmpty(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(EBOMObj.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			if (!StringUtils.isEmpty(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(EBOMObj.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			// plant
			if (!StringUtils.isEmpty(states)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(EBOMObj.class, EBOMObj.STATE);
				ColumnExpression ce = StringUtils.getColumnExpression(states);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { ebomidx });
			}

			if (StringUtils.isEmpty(sort)) {
				sort = "true";
			}

			if (StringUtils.isEmpty(sortKey)) {
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(EBOMObj.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { ebomidx });
			
			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);
			//LOG.debug("###query = {} "+query.toString());
			
			
			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EBOMObj ebomObj = (EBOMObj) obj[0];
				EBOMOBJColumnData data = new EBOMOBJColumnData(ebomObj);
				list.add(data);
			}
			
			map.put("list", list);
			map.put("lastPage", pager.getLastPage());
			map.put("topListCount", pager.getTotal());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			map.put("total", pager.getTotalSize());
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}








	// BOM 변경 구조 추가 건
	public Map<String, Object> addStandardBOM2(Map<String, Object> param) throws Exception {
		
		//LOG.debug("@@@@ addStandardBOM :: " + param);
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		String bomType = (String) param.get("bomType");
		String menuType = (String) param.get("menuType");
		String plant = (String) param.get("plant");
		
		ReferenceFactory rf = new ReferenceFactory();
		long start = System.currentTimeMillis();
		
		String errorMsg = "";
		
		//ArrayList<String[]> data = new ArrayList<String[]>();
		ArrayList<BOMCompareDTO> result = new ArrayList<BOMCompareDTO>();
		
		System.out.println("param -" + param);
		
		
		try {
			
			for (String rootOid : list) {
				WTPart part = (WTPart) rf.getReference(rootOid).getObject();
				if (part != null) {
					View view = ViewHelper.service.getView(part.getViewName());
					WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
					
					// 1.중복제거
					// 중복 제거 삭제
					//HashMap<String, WTPart> duplicatedMapInfo = new HashMap<String, WTPart>();
					//duplicatedMapInfo = getDuplicateCheck(part, duplicatedMapInfo, configSpec);
					
					//2. 하위 조회
					//ArrayList<String[]> result = new ArrayList<String[]>();
					getStandardBomDataList2(part, plant, configSpec, 0, result, rootOid);
				}
			}
			
			
			//결과물 출력 테스트
			//for(BOMCompareDTO d : result) {
				//System.out.println(d.getParentPartNo() + " --- " + d.getChildPartNo());
			//}
			
			System.out.println("------------------- " + result.size());
			System.out.println("------------------- " + (System.currentTimeMillis() - start) / 1000.0 + "초");
			
			//System.out.println("<br>addEBOMSelectPDMAction_ReNEW : " + (System.currentTimeMillis() - start) / 1000.0 + "초");
			
			map.put("bomType", bomType);
			map.put("menuType", menuType);
			map.put("result", "SUCCESS");
			map.put("list", result);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("url", "/Windchill/plm/part/approvalEBOM");
			map.put("result", "FAIL");
			map.put("msg", errorMsg);
			if(e.getMessage() != null) {
				map.put("msg", e.getMessage());
				throw new Exception(e.getMessage());
			}
			throw new Exception(errorMsg);
		}
		return map;
	}


		public static void getStandardBomDataList2(WTPart part, String plant, WTPartStandardConfigSpec configSpec, int level, 
								ArrayList<BOMCompareDTO> result, String rootOid) {
		
		ArrayList<PartTreeDataBOM> firstList  = new ArrayList<>();
		ArrayList<WTPartUsageLink> secondList = new ArrayList<>();
		ArrayList<PartTreeDataBOM> sortedList = new ArrayList<>();
		
		try {
			
			WTPart rootPart = (WTPart) CommonUtils.getObject(rootOid);
			
			QueryResult results = WTPartHelper.service.getUsesWTParts(part, configSpec);
			
			int startNumber = 1;
			level = level + 1;
			
			HashMap<String, String> dataMap = new HashMap<String, String>();
			
			
			while (results.hasMoreElements()) {
				Object[] obj = (Object[]) results.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];

				if (!(obj[1] instanceof WTPart)) {
					continue;
				}
				
				WTPart childPart = (WTPart) obj[1];

				WTPart pPart = part;
				WTPart cPart = childPart;
				String p_PART_NO = IBAUtils.getStringValue(pPart, "PART_NO");
				String c_PART_NO = IBAUtils.getStringValue(cPart, "PART_NO");
				
				if(p_PART_NO != null && !"".equals(p_PART_NO)) {
					p_PART_NO = p_PART_NO.trim();
					dataMap.put(p_PART_NO.trim(), pPart.getPersistInfo().getObjectIdentifier().getStringValue());
				}
				
				if(c_PART_NO != null && !"".equals(c_PART_NO)) {
					c_PART_NO = c_PART_NO.trim();
					dataMap.put(c_PART_NO.trim(), cPart.getPersistInfo().getObjectIdentifier().getStringValue());
				}
				
				// 1차 Validation 시작
				// 모품 - CABLE, PCBA 제외
				if (!"".equals(p_PART_NO) && p_PART_NO.length() >= 4) {
					String num = p_PART_NO.substring(0, 4);
					String[] cable = BomExclusionCode.getCable(); // CABLE
					String[] pcba = BomExclusionCode.getPcba(); // PCBA
					String[] arrayCodes = ArrayUtils.addAll(cable, pcba);
					List<String> codes = Arrays.asList(arrayCodes);
					if(codes.contains(num)) {
						continue;
					}
				}
				
				// 모품 - DUMMY 제외 (IBA_PART_NAME, IBA_TITLE, NAME)
				if (PartHelper.manager.isOnlyDummyPart(pPart)) {
					continue;
				}

				// 자품 - DUMMY 제외 (IBA_PART_NAME, IBA_TITLE, NAME)
				if (PartHelper.manager.isOnlyDummyPart(cPart)) {
					continue;
				}
				
				// 모품 - ENDITEM 제외
				boolean isEndItem = IBAUtils.getBooleanValue(pPart, "ENDITEM");
				if (isEndItem) {
					continue;
				}
				
				
				PartTreeDataBOM dd = new PartTreeDataBOM(childPart, link, level, part);
				firstList.add(dd);
				
				EPMDocument parentEPM = PartHelper.manager.getEPMDocument(part);
				EPMDocument childEPM  = PartHelper.manager.getEPMDocument(childPart);
				
				//LOG
				LOG.debug("111 -- " + p_PART_NO + " > " + c_PART_NO + " == " + parentEPM + " , " + childEPM);
				// FIND NUMBER 줄 필요 없어 보임
				if (parentEPM != null && childEPM != null) {
					EPMDocumentMaster m = (EPMDocumentMaster) childEPM.getMaster();
					int compNumber = PartHelper.getCompNumber(parentEPM, m);
					if (compNumber > 0) {
						// FIND NUMBER 세팅 필요 없어보
//						DecimalFormat format = new DecimalFormat("0000");
//						link.setFindNumber(format.format(compNumber));
//						PersistenceServerHelper.manager.update(link);
//						link = (WTPartUsageLink) PersistenceHelper.manager.refresh(link);
//						PartTreeDataBOM dd = new PartTreeDataBOM(childPart, link, level, part);
//						firstList.add(dd);
					} else {
						secondList.add(link);
					}
				}else{
					secondList.add(link);
				}
			} // end while
			
			Collections.sort(firstList, new BomTreeViewDataCompare());
			
			// 1. 양수인거 findNumber 재정의
			for(PartTreeDataBOM dd : firstList) {
				WTPartUsageLink link = dd.link;
				DecimalFormat format = new DecimalFormat("0000");
				link.setFindNumber(format.format(startNumber));
				PersistenceServerHelper.manager.update(link);
				
				link = (WTPartUsageLink) PersistenceHelper.manager.refresh(link);
				PartTreeDataBOM redd = new PartTreeDataBOM(dd.part, link, dd.level, dd.parentPart);
				
				startNumber++;
				//lastNum = fineNo;
				//fineNo++;
				sortedList.add(redd);
			}
			
			
			//2. 음수 or 순번없는거 findNumber 재정의
			Collections.sort(secondList, new WTPartUsageLinkFindNumberCompare());
			
			if (null != secondList && secondList.size() > 0) {
				for (WTPartUsageLink link : secondList) {
					DecimalFormat format = new DecimalFormat("0000");
					
					link.setFindNumber(format.format(startNumber));
					PersistenceServerHelper.manager.update(link);
					link = (WTPartUsageLink) PersistenceHelper.manager.refresh(link);

					WTPart noEpmchildPart = PartHelper.manager.getLatestPart(link.getUses().getNumber());
					PartTreeDataBOM dd = new PartTreeDataBOM(noEpmchildPart, link, level, part);
					sortedList.add(dd);
					
					startNumber++;
				}
			}
			
			Collections.sort(sortedList, new BomTreeViewDataLvFindNumberCompare());
			
			
			
			ArrayList<WTPart> nextStepList = new ArrayList<WTPart>();
			HashMap<String, BOMCompareDTO> pdmData = new LinkedHashMap<String, BOMCompareDTO>(); // PLM과 비교할 데이터 셋팅
			
			
			// 3. 하위 검사 여부 체크
			for(PartTreeDataBOM sortPart : sortedList){
				WTPart ddPart = sortPart.part;
				//tdata.parentPart
				
				String parentPartNo = "";
				String childPartNo  = "";
				
				parentPartNo = sortPart.parent_iba_number;
				childPartNo  = sortPart.iba_number;
				
				if(parentPartNo != null && !"".equals(parentPartNo)) parentPartNo = parentPartNo.trim();
				if(childPartNo != null && !"".equals(childPartNo)) childPartNo = childPartNo.trim();
				
				//비교를 위해 미리 PDM데이터 셋팅 (모-자)
				BOMCompareDTO dto = new BOMCompareDTO();
				
				//모품
				dto.setParentName(sortPart.parentPart.getName());
				dto.setParentVersion(VersionControlHelper.getIterationDisplayIdentifier(sortPart.parentPart) + " (" + sortPart.parentPart.getViewName() + " )");
				dto.setParentOid(sortPart.parentPart.getPersistInfo().getObjectIdentifier().getStringValue());
				dto.setParentPartNo(parentPartNo);
				dto.setParentState(sortPart.parentPart.getLifeCycleState().getDisplay(MessageUtils.getUserSelectLocale()));
				
				//자품
				dto.setChildOid(ddPart.getPersistInfo().getObjectIdentifier().getStringValue());
				dto.setChildName(ddPart.getName());
				dto.setChildVersion(VersionControlHelper.getIterationDisplayIdentifier(ddPart) + " (" + ddPart.getViewName() + " )");
				dto.setChildPartNo(childPartNo);
				dto.setChildQty(String.valueOf( sortPart.quantity));				//수량
				dto.setChildunit(ddPart.getDefaultUnit().toString().toUpperCase()); //단위
				dto.setChildFindNumber(sortPart.link.getFindNumber());				//findNumber(자품)
				dto.setChildState(ddPart.getLifeCycleState().getDisplay(MessageUtils.getUserSelectLocale()));           //상태
				
				String key = parentPartNo + "↔" + childPartNo;
				pdmData.put(key, dto);
				
				String ccPartNo = IBAUtils.getStringValue(sortPart.part, "PART_NO");
				if(ccPartNo != null && !"".equals(ccPartNo)) ccPartNo = ccPartNo.trim();
				
				//하위추가 여부 체크 - CABLE, KASSY, PCB는 단품처리하므로 자체는 추가되고 그 하위는 추가하지 않는다.
				// 1) PCB, CABLE, KASSY 아닐 경우 하위 추가
				// 2) PCM, CABLE, KASSY 하위 Hidden
				// 3) ASM이면서 결재중 and 릴리즈된거는 제외
				if( !HyoSungCommonUtil.isKCP_Check(ccPartNo) ) {
					if (!"component".equals(ddPart.getPartType().toString())) {
						if ( !"UNDERAPPROVAL".equals(ddPart.getState().toString()) && !"RELEASED".equals(ddPart.getState().toString()) 
											&& !"PROTOTYPE".equals(ddPart.getState().toString()) ) {
							nextStepList.add(ddPart);
						}
					}
				}
				
			} // END sortedList
			
			
			
			// 4. PLM과 비교 후, 결과값 반환
			ArrayList<BOMCompareDTO> resultData = comparePDMPLMData(plant, part, pdmData);
			
			
			// 5. 출력 전 2차 Validation 시작
			for(int i=0; i < resultData.size(); i++) {
				
				StringBuffer message = new StringBuffer();
				
				BOMCompareDTO dto = resultData.get(i);
				
				dto.setRootOid(rootOid); // 선택한 최상위 BOM OID
				dto.setRootPartNo(IBAUtils.getStringValue(rootPart, "PART_NO"));
				String type = dto.getType(); // D일 경우는 PLM에만 있는거라 체크해줘야됨
				
				LOG.trace(type + " --- " + dto.getParentPartNo() + " > " + dto.getChildPartNo());
				
				String checkType = "S";
				
				WTPart parentPart     = null;
				String parentPartNo   = "";
				String parentPartName = "";
				String parentTitle    = "";
				
				if(dto.getParentOid() == null) {
					parentPart = duplicatedMapInfo.get(dto.getParentPartNo().trim());
					if(parentPart != null) {
						dto.setParentOid(parentPart.getPersistInfo().getObjectIdentifier().getStringValue());
						parentTitle = IBAUtils.getStringValue(parentPart, "TITLE");
						parentPartNo = IBAUtils.getStringValue(parentPart, "PART_NO");
						parentPartName = IBAUtils.getStringValue(parentPart, "PART_NAME"); 
						
						dto.setParentPartNo(parentPartNo.trim());
						dto.setParentName(parentPart.getName());
						dto.setParentVersion(VersionControlHelper.getIterationDisplayIdentifier(parentPart) + " (" + parentPart.getViewName() + " )");
						dto.setParentState(parentPart.getLifeCycleState().getDisplay(MessageUtils.getUserSelectLocale()));
					} else {
						dto.setParentName("해당 부품은 PDM에 없습니다.");
						dto.setParentOid("");
					}
				} else {
					parentPart =  (WTPart) CommonUtils.getObject(dto.getParentOid());  //dto.getParentPart();
					parentTitle = IBAUtils.getStringValue(parentPart, "TITLE");
					parentPartNo = IBAUtils.getStringValue(parentPart, "PART_NO"); // dto.getParentPartNo();
					parentPartName = IBAUtils.getStringValue(parentPart, "PART_NAME"); //dto.getParentName();
				}
				
				if(parentPartNo != null && !"".equals(parentPartNo)) parentPartNo = parentPartNo.trim();
				
				//System.out.println( (i+1) + "-" + parentPartNo + "->>> " + dto.getParentOid() + " -- " +  dto.getChildOid() + " ," + dto.getChildPartNo() + "--- " + dto.getType());
				//System.out.println("childPart -- " + childPart + " , " + dto.getChildOid());
				
				WTPart childPart 	 = null;
				String childPartNo   = ""; //dto.getChildPartNo();
				String childPartName = ""; // dto.getChildName();
				String childState    = ""; //childPart.getState().toString();
				String childTitle    = ""; // IBAUtils.getStringValue(childPart, "TITLE");
				
				if(dto.getChildOid() == null) {
					childPart = duplicatedMapInfo.get(dto.getChildPartNo().trim());
					
					if(childPart == null) {
						
						//품명과 파일명으로 조회
						//5728002423
						//childPart = PartHelper.manager.getWTPartFromPARTNO(dto.getChildPartNo().trim(), CommonUtils.getView(plant));
						//LIKE '품번%'
						childPart = PartHelper.manager.getWTPartByPartNoAndFileNameAsLikeV2(dto.getChildPartNo().trim());
						
						if(childPart == null) {
							//LIKE '%품번%'
							childPart = PartHelper.manager.getWTPartByPartNoAndFileNameAsLike(dto.getChildPartNo().trim(), CommonUtils.getView(plant));
						}
						
						if( childPart == null) {
							//LIKE
							childPart = PartHelper.manager.getWTPartFromPARTNOAsLike(dto.getChildPartNo().trim(), CommonUtils.getView(plant));
						}
					}
					
					if(childPart != null) {
						dto.setChildOid(childPart.getPersistInfo().getObjectIdentifier().getStringValue());
						childState = childPart.getState().toString();
						childTitle  = IBAUtils.getStringValue(childPart, "TITLE");
						childPartNo = IBAUtils.getStringValue(childPart, "PART_NO");
						if(childPartNo != null && !"".equals(childPartNo)) childPartNo = childPartNo.trim(); // 공백제거
						
						childPartName = IBAUtils.getStringValue(childPart, "PART_NAME");
						dto.setChildPartNo(childPartNo);
						dto.setChildName(childPart.getName());
						dto.setChildVersion(VersionControlHelper.getIterationDisplayIdentifier(childPart) + " (" + childPart.getViewName() + " )");
						dto.setChildState(childPart.getLifeCycleState().getDisplay(MessageUtils.getUserSelectLocale()));
					} else {
						childState  = "-";
						childTitle  = "-";
						dto.setChildName("해당 부품은 PDM에 없습니다.");
					}
					
				} else {
					childPart   = (WTPart) CommonUtils.getObject(dto.getChildOid());
					childTitle  = IBAUtils.getStringValue(childPart, "TITLE");
					childPartNo = IBAUtils.getStringValue(childPart, "PART_NO");
					childPartName = IBAUtils.getStringValue(childPart, "PART_NAME");
				}
				
				if(childPartNo != null && !"".equals(childPartNo)) childPartNo = childPartNo.trim();
				
				String childFindNumber = dto.getChildFindNumber();
				
				//System.out.println( (i+1) + "  " + parentPartNo + " >>> " + childPartNo + " --- " + childFindNumber + "_" + dto.getType());
				

				if(parentPart == null || childPart == null) {
					//continue;
				}
				
				if(parentPart != null) {
					if (!parentPart.isLatestIteration()) {
						checkType = "E";
						message.append("상위 최신버전 아님" + " \n");
					}
					
					if (parentPartNo.length() == 0) {
						checkType = "E";
						message.append("상위 PART_NO 없음");
					}
					
					if (parentTitle.length() == 0) {
						checkType = "E";
						message.append("상위 TITLE 없음");
					}
					
					if (parentPartName.length() == 0) {
						checkType = "E";
						message.append("상위 PART_NAME 없음");
					}
					
					// 체크아웃 체크
					if (WorkInProgressHelper.isCheckedOut(parentPart)) {
						message.append("상위 Check-Out 된 객체");
						checkType = "E";
					}
					
					// wrk 체크
					if (WorkInProgressHelper.isWorkingCopy(parentPart)) {
						message.append("상위 Working-Copy 된 객체");
						checkType = "E";
					}
				}
				
				if (childPartNo.length() == 0) {
					checkType = "E";
					message.append("하위 PART_NO 없음" + " \n");
				}
				
				
				if(childPart != null) {
					
					String cVersion = childPart.getVersionIdentifier().getSeries().getValue();
					char cv = cVersion.charAt(0);
					
					// D가 아닐 경우만 검사한다
					// 즉, D일 경우 자품은 검사할 필요가 없다
					if(!"D".contentEquals(type)) {
						if (!childPart.isLatestIteration()) {
							checkType = "E";
							message.append("하위 최신버전 아님" + " \n");
						}
						
						if (childTitle.length() == 0) {
							checkType = "E";
							message.append("하위 TITLE 없음" + " \n");
						}

						if (childPartName.length() == 0) {
							checkType = "E";
							message.append("하위 PART_NAME 없음" + " \n");
						}
						
						// 체크아웃 체크
						if (WorkInProgressHelper.isCheckedOut(childPart)) {
							message.append("하위 Check-Out 된 객체" + " \n");
							checkType = "E";
						}
						
						// wrk 체크
						if (WorkInProgressHelper.isWorkingCopy(childPart)) {
							message.append("하위 Working-Copy 된 객체");
							checkType = "E";
						}
						
						
						//자품 검사 - 00 or 알파벳 > "검증"할때 검사하기 때문에 임시로 뺌.
						/*if ("component".equals(childPart.getPartType().toString())) {
							if("00".equals(cVersion) || Character.isAlphabetic(cv) ) {
								if ("INWORK".equals(childState) || "RETURN".equals(childState)) {
									type = "E";
									message.append("해당 EndItem 은 도면승인이 필요합니다.");
								}
							}
						}*/
					}
					
					//5728 5721
				}
				
				dto.setCheckType(checkType);
				dto.setCheckMsg(message.toString());
				
				LOG.info( (i+1) + "  " + parentPartNo + " >>> " + childPartNo + " --- " + childFindNumber + " > " + dto.getType());
				
				//결과값 넘길 DTO만들어서 LIST에 담아서 넘겨준다.
				result.add(dto);
				
				
			} // end resultData
			
			
			// 6. 하위 순환 검사
			for(WTPart nextPart : nextStepList) {
				//getStandardBomDataList(WTPart part, String plant, WTPartStandardConfigSpec configSpec, int level)
				getStandardBomDataList2(nextPart, plant, configSpec, level, result, "");
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
