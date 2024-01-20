/*
 * @(#) PartData.java  Create on 2004. 12. 9.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.part.dto;

import java.beans.PropertyDescriptor;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.service.RohsUtil;

import lombok.Getter;
import lombok.Setter;
import wt.enterprise.BasicTemplateProcessor;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContained;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartBaselineConfigSpec;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
//import com.e3ps.common.web.CommonWebHelper;
import wt.vc.views.View;

/**
 * Part에 관련해서 상세한 정보를 포함하는 Data 클래스
 *
 * @author yhjang@e3ps.com
 * @version 1.00, 2004. 12. 9.
 * @since 1.4
 * @see e3ps.util.attr.ObjectData
 */
@Getter
@Setter
public class PartData {
//	public WTPart part;
	private String number;
	private String icon;
	private String baseline;
	private String unit;
	private EPMDocument epm;
	private String epmOid;
	private String name;
	private String state;
	private String stateKey;
	private String oid;
	private String vrOid;

	private String dwgOid;
	private String model;
	private String productmethod;
	private String deptcode;
	private String manufacture;

	private String manufacture_name;
	private String model_name;

	private String lineImg = "joinbottom";
	private String mat;
	private String finish;
	private String remark;
	private String weight;
	private String specification;
	private ArrayList<Object[]> descPartlist = new ArrayList<Object[]>();
	private ArrayList<Object[]> ascPartlist = new ArrayList<Object[]>();

	private String ecoNo;
	private String creator;
	private String createDate;
	private String modifyDate;

//	private boolean isDelete;
	private boolean isApproved;
	private String version;
	private String baselineOid;
	private String iteration;
	private String PDMLinkProductOid;
	private boolean last;
	private String location;
	private double rohsState;
	private String partName1;
	private String partName2;
	private String partName3;
	private String partName4;

	public PartData(WTPart part) throws Exception {
//    	super(part);
//    	setPart(part);
		setOid(part.getPersistInfo().getObjectIdentifier().toString());
		setVrOid(CommonUtil.getVROID(part));
//		setIcon(BasicTemplateProcessor.getObjectIconImgTag(part));
		setNumber(part.getNumber());
		setUnit(part.getDefaultUnit().toString());

		setLast(CommonUtil.isLatestVersion(part));

		String ecoNumber = "";
		HashMap map = IBAUtil.getAttributes(part);

		String model_code = (String) map.get(AttributeKey.IBAKey.IBA_MODEL); // 프로젝트 코드
		NumberCode model = NumberCodeHelper.manager.getNumberCode(model_code, "MODEL");
		if (model != null) {
			setModel(model_code);
			setModel_name(model.getName());
		}
		setProductmethod((String) map.get(AttributeKey.IBAKey.IBA_PRODUCTMETHOD));// 제작방법
		setDeptcode((String) map.get(AttributeKey.IBAKey.IBA_DEPTCODE)); // 부서 코드

		String manufaturer_code = (String) map.get(AttributeKey.IBAKey.IBA_MANUFACTURE);
		NumberCode manufaturer = NumberCodeHelper.manager.getNumberCode(manufaturer_code, "MANUFACTURE");
		if (manufaturer != null) {
			setManufacture(manufaturer_code);// MANUFATURER
			setManufacture_name(manufaturer.getName());
		}
		setMat((String) map.get(AttributeKey.IBAKey.IBA_MAT));// 재질
		setFinish((String) map.get(AttributeKey.IBAKey.IBA_FINISH));// 재질
		setRemark((String) map.get(AttributeKey.IBAKey.IBA_REMARKS));// 비고
		setWeight((String) map.get(AttributeKey.IBAKey.IBA_WEIGHT)); // 무게
		setSpecification((String) map.get(AttributeKey.IBAKey.IBA_SPECIFICATION));// 사양
		ecoNumber = (String) map.get(AttributeKey.IBAKey.IBA_CHANGENO); // ECO NO
		if (ecoNumber != null && ecoNumber.length() > 0) {
			setEcoNo(ecoNumber);
		} else {
			setEcoNo(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_ECONO));
		}
		setName(part.getName());
//    	setState(part.getLifeCycleState().toString());
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorFullName());
		setCreateDate(DateUtil.getDateString(part.getCreateTimestamp(), "a"));
		setModifyDate(DateUtil.getDateString(part.getModifyTimestamp(), "a"));

//    	if(epm == null){
//			epm = DrawingHelper.service.getEPMDocument(part);
//			setEpmOid("");
//		}else {
//			setEpmOid(epm.getPersistInfo().getObjectIdentifier().toString());
//		}
//    	setDelete(false);
		setApproved(false);
		setVersion(part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
		setBaselineOid("");
		setIteration(part.getIterationIdentifier().getSeries().getValue());
		WTContained wc = (WTContained) part;
		setPDMLinkProductOid(CommonUtil.getOIDString(wc.getContainer()));
		setStateKey(part.getLifeCycleState().toString());
		setLocation(part.getLocation());
		Map<String, Object> dataMap = RohsUtil.getProductRoHsState(getOid());
		setRohsState((double) dataMap.get("totalState"));
		setIBAPartName(part);
	}

	private ArrayList<Object[]> descentLastPart(WTPart part, Baseline baseline, boolean isCheckDummy, State state)
			throws Exception {
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec(baseline));
		QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (re.hasMoreElements()) {
			Object oo[] = (Object[]) re.nextElement();

			if (!(oo[1] instanceof WTPart)) {
				continue;
			}

			if (isCheckDummy) {
				WTPart p = (WTPart) oo[1];
				boolean isChange = PartUtil.isChange(p.getNumber());
				if (isChange) {
					continue;
				}
			}

			this.descPartlist.add(oo);
		}
		return this.descPartlist;
	}

	private ArrayList<Object[]> descentLastPart(WTPart part, View view, boolean isCheckDummy, State state)
			throws WTException {
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, state));
		QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (re.hasMoreElements()) {
			Object oo[] = (Object[]) re.nextElement();

			if (!(oo[1] instanceof WTPart)) {
				continue;
			}
			if (isCheckDummy) {
				WTPart p = (WTPart) oo[1];
				boolean isChange = PartUtil.isChange(p.getNumber());
				if (isChange) {
					continue;
				}
			}
			this.descPartlist.add(oo);
		}
		return this.descPartlist;
	}

	public ArrayList<Object[]> ancestorPart(WTPart part, View view, boolean isCheckDummy, State state)
			throws WTException {
		ArrayList<Object[]> v = new ArrayList<Object[]>();
		try {
			WTPartMaster master = (WTPartMaster) part.getMaster();
			QuerySpec qs = new QuerySpec();
			int index1 = qs.addClassList(WTPartUsageLink.class, true);
			int index2 = qs.addClassList(WTPart.class, true);
			qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=",
					master.getPersistInfo().getObjectIdentifier().getId()), new int[] { index1 });
			SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
					"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { index1, index2 }, 0);
			sc.setOuterJoin(0);
			qs.appendAnd();
			qs.appendWhere(sc, new int[] { index1, index2 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
					new int[] { index2 });
			if (view != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
						view.getPersistInfo().getObjectIdentifier().getId()), new int[] { index2 });
			}
			if (state != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
						new int[] { index2 });
			}

			SearchUtil.addLastVersionCondition(qs, WTPart.class, index2);

			ClassInfo classinfo = WTIntrospector.getClassInfo(WTPart.class);
			PropertyDescriptor dd = classinfo.getPropertyDescriptor("number");

			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "master>number"), true),
					new int[] { index2 });

			QueryResult re = PersistenceHelper.manager.find(qs);
			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();
				if (oo[1] instanceof WTPart && isCheckDummy) {
					WTPart p = (WTPart) oo[1];
					boolean isChange = PartUtil.isChange(p.getNumber());
					if (isChange) {
						continue;
					}
				}
				v.add(oo);
			}
			Comparator comparator = new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String o1Number = (String) ((WTPart) o1[1]).getNumber();
					String o2Number = (String) ((WTPart) o2[1]).getNumber();
					return o1Number.compareTo(o2Number);
				}
			};
			Collections.sort(v, comparator);
		} catch (Exception ex) {
			throw new WTException();
		}
		return v;
	}

	public ArrayList<Object[]> ancestorPart(WTPart part, Baseline baseline, boolean isCheckDummy, State state)
			throws WTException {
		ArrayList<Object[]> v = new ArrayList<Object[]>();
		try {
			WTPartMaster master = (WTPartMaster) part.getMaster();
			QuerySpec qs = new QuerySpec();
			int index1 = qs.addClassList(WTPartUsageLink.class, true);
			int index2 = qs.addClassList(WTPart.class, true);
			qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=",
					master.getPersistInfo().getObjectIdentifier().getId()), new int[] { index1 });
			SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
					"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { index1, index2 }, 0);
			sc.setOuterJoin(0);
			qs.appendAnd();
			qs.appendWhere(sc, new int[] { index1, index2 });

			if (state != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
						new int[] { index2 });
			}

			if (baseline != null) {
				int index3 = qs.addClassList(BaselineMember.class, false);
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id",
						BaselineMember.class, "roleBObjectRef.key.id"), new int[] { index2, index3 });
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
						baseline.getPersistInfo().getObjectIdentifier().getId()), new int[] { index3 });
			}
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "master>number"), true),
					new int[] { index2 });
			// System.out.println("### 22222222 == "+qs);
			QueryResult re = PersistenceHelper.manager.find(qs);

			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();
				if (oo[1] instanceof WTPart && isCheckDummy) {
					WTPart p = (WTPart) oo[1];
					boolean isChange = PartUtil.isChange(p.getNumber());
					if (isChange) {
						continue;
					}
				}
				v.add(oo);
			}
			Comparator comparator = new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String o1Number = (String) ((WTPart) o1[1]).getNumber();
					String o2Number = (String) ((WTPart) o2[1]).getNumber();
					return o1Number.compareTo(o2Number);
				}
			};
			Collections.sort(v, comparator);
		} catch (Exception ex) {
			throw new WTException();
		}
		return v;
	}

//	/**
//     * @return 주도면이 있으면 true, 없으면 false
//     */
//    public boolean isMainEPM() {
//    	try {
//    		if(epm == null){
//    			epm = DrawingHelper.service.getEPMDocument(this.part);
//    		}
//	    	
//	    	return (epm != null);
//    	}catch(Exception e) {
//    		e.printStackTrace();
//    		return false;
//    	}
//    }
//    
//	public String getViewName() {
//		View view = ViewHelper.getView(part);
//		return view == null ? "" : view.getName();
//	}
//	
//	public String getEpmOid() throws Exception {
//		if(epm == null){
//			epm = DrawingHelper.service.getEPMDocument(part);
//		}
//		
//		if(epm != null) {
//			return epm.getPersistInfo().getObjectIdentifier().toString();
//		}
//		return "";
//	}

	private void setIBAPartName(WTPart part) throws Exception {
		String partName1 = StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME1"));
		setPartName1(partName1);
		String partName2 = StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME2"));
		setPartName2(partName2);
		String partName3 = StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME3"));
		setPartName3(partName3);
		String partName4 = StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME4"));
		setPartName4(partName4);
	}

//	public String getPartName(int index) {
//		try {
//			
//			if(index == 4) {
//				String partName1 = StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME1"));
//				String partName2 = StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME2"));
//				String partName3 = StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME3"));
//				String partName4 = StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME4"));
//				if(partName1.length() == 0 && partName2.length() == 0 && partName3.length() == 0 && partName4.length() == 0) {
//					return this.name;
//				}else {
//					return partName4;
//				}
//			}else {
//				return StringUtil.checkNull(IBAUtil.getAttrValue(part, "PARTNAME"+index));
//			}
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//		
//	}

	public String getDwgOid() {
		return dwgOid;
	}

	public void setDwgOid(String dwgOid) {
		this.dwgOid = dwgOid;
	}

//	public String getDwgNo() {
//	    	String dwgNo = "";
//	    	boolean isDwg = false;
//	    	try {
//	    		EPMBuildRule buildRule = PartSearchHelper.service.getBuildRule(this.part);
//	    		if(buildRule != null) {
//	    			EPMDocument epm = (EPMDocument) buildRule.getBuildSource();
//	    			EPMDocument epm2D = EpmSearchHelper.service.getEPM2D((EPMDocumentMaster)epm.getMaster());
//	    			//System.out.println("check 1"+(null != epm2D));
//	    			if(epm2D != null) {
//	    				dwgNo = epm2D.getNumber();
//	    				dwgOid = CommonUtil.getOIDString(epm2D);
//	    			}else {
//	    				isDwg = true;
//	    				if(epm != null){
//	    					
//	    					EpmData epmData = new EpmData(epm);
//	    					String appType = epmData.getApplicationType();
//	    					if("MANUAL".equals(appType)){
//	    						//System.out.println("check 2");
//		        				dwgNo = epm.getNumber();
//		        				dwgOid = CommonUtil.getOIDString(epm);
//		        				isDwg = false;
//	    					}
//	        			}
//	    			}
//	    			
//	    			
//	    			
//	    		}else {
//	    			isDwg = true;
//	    		}
//	    		
//	    		if(isDwg) {
//	    			boolean isAP = PartSearchHelper.service.isHasDocumentType(part, "$$APDocument");
//	    			if(isAP){
//	    				dwgNo = "AP";
//	    				dwgOid = PartSearchHelper.service.getHasDocumentOid(part, "$$APDocument");
//	    			}else {
//	    				dwgNo = "ND";
//	    			}
//	    		}
//	    		
//	    	}catch(Exception e) {
//	    		e.printStackTrace();
//	    		dwgNo = "";
//	    	}
//	    	
//	    	return dwgNo;
//	    }
	/**
	 * 설계 변경 유무 체크
	 * 
	 * @return
	 */
//	public boolean isSelectEO(){
//    	
//		return PartSearchHelper.service.isSelectEO(this.part);
//    	
//    }

	/**
	 * 진채번 유무 체크
	 * 
	 * @return
	 */
	public boolean isChange() {
		return PartUtil.isChange(this.number);
	}

	// 순중량
//	public String getWeight(){
//		String weight ="";
//		try {
//			weight = StringUtil.checkNull(IBAUtil.getAttrfloatValue(part, IBAKey.IBA_WEIGHT)) ;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return weight;
//			
//	}

	// 재질(기본자재)
//	public String getMat(){
//		String mat ="";
//		try {
//			mat = StringUtil.checkNull(IBAUtil.getAttrfloatValue(part, IBAKey.IBA_MAT)) ;
//			if(mat.length()>0){
//				NumberCode code =NumberCodeHelper.service.getNumberCode("MAT", mat);
//				
//				if(code != null){
//					mat =mat+":"+code.getName();
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return mat;
//	}

	// 사양(SPECIFICATION)
//	public String getSpecification(){
//		String specification ="";
//		try {
//			//specification = StringUtil.checkNull(IBAUtil.getAttrfloatValue(part, IBAKey.IBA_SPECIFICATION)) ;
//			specification = StringUtil.checkNull(IBAUtil.getAttrValue2(part, IBAKey.IBA_SPECIFICATION)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return specification;
//	}

	// 후처리
//	public String getFINISH(){
//		String finish ="";
//		try {
//			finish = StringUtil.checkNull(IBAUtil.getAttrfloatValue(part, IBAKey.IBA_FINISH)) ;
//			if(finish.length()>0){
//				NumberCode code =NumberCodeHelper.service.getNumberCode("FINISH", finish);
//				
//				if(code != null){
//					finish =finish+":"+code.getName();
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return finish;
//	}

	// 후처리
//	public String getModel(){
//		String model ="";
//		try {
//			model = StringUtil.checkNull(IBAUtil.getAttrfloatValue(part, IBAKey.IBA_MODEL)) ;
//			if(model.length()>0){
//				NumberCode code =NumberCodeHelper.service.getNumberCode("MODEL", model);
//				
//				if(code != null){
//					model =model+":"+code.getName();
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return model;
//	}

	// 제작 방법
//	public String getPRODUCTMETHOD(){
//		String method ="";
//		try {
//			method = StringUtil.checkNull(IBAUtil.getAttrfloatValue(part, IBAKey.IBA_PRODUCTMETHOD)) ;
//			if(method.length()>0){
//				NumberCode code =NumberCodeHelper.service.getNumberCode("PRODUCTMETHOD", method);
//				
//				if(code != null){
//					method =method+":"+code.getName();
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return method;
//	}

	// 부서 코드
//	public String getDEPTCODE(){
//		String departcode ="";
//		try {
//			departcode = StringUtil.checkNull(IBAUtil.getAttrfloatValue(part, IBAKey.IBA_DEPTCODE)) ;
//			if(departcode.length()>0){
//				NumberCode code =NumberCodeHelper.service.getNumberCode("DEPTCODE", departcode);
//				
//				if(code != null){
//					departcode =departcode+":"+code.getName();
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return departcode;
//	}
	/**
	 * isGENERIC 유무 체크 epm.getFamilyTableStatus() : 0 = Normal
	 * epm.getFamilyTableStatus() : 1 = INSTANCE epm.getFamilyTableStatus() : 2 =
	 * GENERIC epm.getFamilyTableStatus() : 3 = GENERIC and INSTANCE
	 * 
	 * @return
	 */
	public boolean isGENERIC(WTPart part) {

		boolean isGENERIC = false;
		int status;
		try {
			if (epm != null) {

				status = epm.getFamilyTableStatus();
				// System.out.println("getFamilyTableStatus1 =" +status );
				if (status == 2 || status == 3) {
					isGENERIC = true;
				}
			} else {
				epm = DrawingHelper.service.getEPMDocument(part);
				if (epm != null) {
					status = epm.getFamilyTableStatus();
					// System.out.println("getFamilyTableStatus2 =" +status );
					if (status == 2 || status == 3) {
						isGENERIC = true;
					}
				}

			}
			// System.out.println("======= isGENERIC ============" + epm + ":" + isGENERIC);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isGENERIC;
	}

	public boolean isINSTANCE(WTPart part) {

		boolean isINSTANCE = false;
		int status;
		try {
			if (epm != null) {

				status = epm.getFamilyTableStatus();

				if (status == 1) {
					isINSTANCE = true;
				}
			} else {
				epm = DrawingHelper.service.getEPMDocument(part);
				if (epm != null) {
					status = epm.getFamilyTableStatus();

					if (status == 1) {
						isINSTANCE = true;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isINSTANCE;
	}

	/**
	 * GENERIC 이고 최신 버전 이고 작업중인 경우 수정
	 * 
	 * @return
	 */
	public boolean isFamliyModify(WTPart part) {
		// System.out.println("======= isFamliyModify ============");
		return (isGENERIC(part) && this.isLast() && isWorking());
	}

	public boolean isWorking() {
		return ("INWORK").equals(this.getState());

	}

//	
//	/**
//	 * 속성 clearing 
//	 */
	public boolean isClearing() {
		String ver = this.version;

		boolean isClearing = ver.equals("A") && "INWORK".equals(this.getState());

		return isClearing;

	}

	public ArrayList<Object[]> getDescPartlist() {
		Comparator comparator = new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String o1Number = (String) ((WTPart) o1[1]).getNumber();
				String o2Number = (String) ((WTPart) o2[1]).getNumber();
				return o1Number.compareTo(o2Number);
			}
		};
		Collections.sort(descPartlist, comparator);
		return descPartlist;
	}

}
