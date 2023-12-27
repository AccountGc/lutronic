package com.e3ps.drawing.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import wt.epm.E3PSRENameObject;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PersistenceHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.drawing.util.EpmPublishUtil;

@SuppressWarnings("serial")
public class StandardEpmUtilService extends StandardManager implements EpmUtilService {

	public static StandardEpmUtilService newStandardEpmUtilService() throws Exception {
		final StandardEpmUtilService instance = new StandardEpmUtilService();
		instance.initialize();
		return instance;
	}
	
	/**
	 * oid : Part WTPart 의 속성을  EPMDocument 로 Copy 
	 * @param oid
	 */
	@Override
	public void copyIBA(String oid){
		
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		
		try{
			EPMDocument epm=DrawingHelper.service.getEPMDocument(part);
			
			if(epm != null){
				/*
				String approvedBy = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_APPROVEDBY);
				String designBy = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_DESIGNBY);
				String drawnDate = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_DRAWNDATE);
				String surface = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SURFACE);
				String heat = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_HEAT);
				String maker = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_MAKER);
				
				if(approvedBy.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_APPROVEDBY, approvedBy, "string");
				}
				if(designBy.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_DESIGNBY, designBy, "string");
				}
				if(drawnDate.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_DRAWNDATE, drawnDate, "string");				
				}
				if(surface.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SURFACE, surface, "string");
				}
				if(heat.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_HEAT, heat, "string");
				}
				if(maker.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_MAKER, maker, "string");
				}
				*/
				
				String meterial = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_MATERIAL);
                String spec = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SPEC);
                String surface = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SURFACE);
                String subcontract = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SUBCONTRACT);
                String unit = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_UNIT);
                String supplier = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_SUPPLIER);
                String vendor = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_VENDOR);
                String weight = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_WEIGHT);
                String comment = IBAUtil.getAttrValue(part, AttributeKey.EPMKey.IBA_COMMENT);
                
				if (meterial.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_MATERIAL, meterial, "string");
				}
				if (spec.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SPEC, spec, "string");
				}
				if (surface.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SURFACE, surface, "string");
				}
				if (subcontract.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SUBCONTRACT, subcontract, "string");
				}
				if (unit.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_UNIT, unit, "string");
				}
				if (supplier.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_SUPPLIER, supplier, "string");
				}
				if (vendor.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_VENDOR, vendor, "string");
				}
				if (weight.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_WEIGHT, weight, "float");
				}
				if (comment.length() > 0) {
					IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_COMMENT, comment, "string");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 3D와 2D의 이름 변경
	 * 3D의 속성값에 변경된 이름을 저장한다. (iba : Description)
	 * @param epm
	 * @param changeName
	 */
	@Override
	public void changeEPMName(EPMDocument epm, String changeName) throws Exception {
			
		//E3PSRENameObject.manager.EPMReName(epm, "", changeName, "", false); 
		epmReName(epm, changeName);
		IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_DES, changeName, "string");
		Vector<EPMReferenceLink> vec = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
		boolean isNonCheckout = !(epm.getFamilyTableStatus()==1 || epm.getFamilyTableStatus()==2 || epm.getFamilyTableStatus()==3);
		for(int i=0; i<vec.size(); i++) {
			EPMReferenceLink link = (EPMReferenceLink)vec.get(i);
			EPMDocument epm2D = link.getReferencedBy();
			
			if(isNonCheckout){
				epm2D = (EPMDocument) ObjectUtil.getWorkingCopy(epm2D);
				
				// 체크인
				if (WorkInProgressHelper.isCheckedOut(epm2D)) {
					epm2D = (EPMDocument) WorkInProgressHelper.service.checkin(epm2D, Message.get("PDM에서 체크인 되었습니다"));
				}
			}
			
			epm2D = (EPMDocument) PersistenceHelper.manager.refresh(epm2D);
			
			//System.out.println("changeEPMName   2D =" +epm2D.getNumber());
			//E3PSRENameObject.manager.EPMReName(epm2D, "", changeName, "", false);
			epmReName(epm2D, changeName);
			EpmPublishUtil.publish(epm2D);
		}
	}
	
	@Override
	public void changeEPMState(EPMDocument epm, String state) throws Exception {
		LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm, State.toState(state), true);
		Vector<EPMReferenceLink> vec = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
		for(int i=0; i<vec.size(); i++) {
			EPMReferenceLink link = (EPMReferenceLink)vec.get(i);
			EPMDocument epm2D = link.getReferencedBy();
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm2D, State.toState(state), true);
		}
	}
	@Override
	public void epmReName(EPMDocument epm, String changeName) throws Exception{
		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			if(!epm.getName().equals(changeName)){
				methodcontext = MethodContext.getContext();
				wtconnection = (WTConnection) methodcontext.getConnection();
				Connection con = wtconnection.getConnection();
				
				EPMDocumentMaster master = (EPMDocumentMaster)epm.getMaster();
				long longOid = CommonUtil.getOIDLongValue(master);
				
				StringBuffer sql = new StringBuffer();
				
				sql.append("UPDATE EPMDocumentMaster set name= ? where ida2a2 = ? ");
				//System.out.println(sql.toString());
				//System.out.println(changeName + ","+longOid);
				st = con.prepareStatement(sql.toString());
				st.setString(1, changeName);
				st.setLong(2, longOid);
				st.executeQuery();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				throw new WTException(e);
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}
}
