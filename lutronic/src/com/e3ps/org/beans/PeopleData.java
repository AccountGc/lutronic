/**
 * @(#)	PeopleData.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @since jdk 1.4.2
 * @author Cho Sung Ok, jerred@e3ps.com
 */

package com.e3ps.org.beans;

import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.Department;
import com.e3ps.org.DepartmentPeopleLink;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeopleData{
	private String woid;

    private String oid = "";
    private String id = "";
    private String name = "";
    private String name_en = "";
    private String email = "";
    private String duty = "";
    private String dutycode = "";
    private String gradeName = "";
    private String gradeCode = "";
    private String cccCode = "";
    private String cellTel = "";
    private String homeTel = "";
    private String officeTel = "";
    private String address = "";
    private String departmentName = "";
    private String password = "";
    private String imgUrl = CompanyState.defautlURL.toString();
    
    public boolean isDiable = false;
    
    private ArrayList<String> primarys = new ArrayList<String>();
    
    public PeopleData() throws Exception{
    	
    }
    
    public PeopleData(People people) throws Exception{
    	setOid(people.getPersistInfo().getObjectIdentifier().getStringValue());
    	setId(people.getUser().getName());
    	setName(people.getName());
    	setName_en(people.getNameEn());
    	setEmail(people.getUser().getEMail());
    	setDuty(people.getDuty());
    	setDutycode(people.getDutyCode());
    	setCellTel(people.getCellTel());
    	setPassword(people.getPassword());
    	setDepartmentName(people.getDepartment().getName());
    	QueryResult qr = PersistenceHelper.manager.navigate(people, "user", WTUserPeopleLink.class);
		if (qr.hasMoreElements()) {
			WTUser user = (WTUser) qr.nextElement();
			setWoid(user.getPersistInfo().getObjectIdentifier().getStringValue());
		}
    }
   
//    public PeopleData() throws Exception{
//        this(SessionHelper.manager.getPrincipal());
//    }
//
//    public PeopleData(Object obj) throws Exception{
//    	
//    	if(obj==null)return;
//    	
//    	if(obj instanceof WTPrincipalReference){
//    		obj = (WTUser)((WTPrincipalReference)obj).getObject();
//    	}
//    	
//    	if(obj instanceof Object[]){
//    		Object[] o = (Object[])obj;
//    		if(o[0] instanceof WTUser){
//    			this.wtuser = (WTUser)o[0];
//    			this.people = (People)o[1];
//    		}else{
//    			this.people = (People)o[0];
//    			this.wtuser = (WTUser)o[1];
//    		}
//    	}else if (obj instanceof WTUser){
//            this.wtuser = (WTUser) obj;
//
//            QueryResult qr = PersistenceHelper.manager.navigate(this.wtuser, "people", WTUserPeopleLink.class);
//            if (qr.hasMoreElements()){
//                this.people = (People) qr.nextElement();
//            }else{
//                try{
//                    this.people = People.newPeople();
//                    this.people.setUser(this.wtuser);
//                    this.people.setName(this.wtuser.getFullName());
//                    this.people.setNameEn(this.wtuser.getFullName());
//                    this.people = (People) PersistenceHelper.manager.save(this.people);
//                    this.people.setIsDisable( (this.people.isIsDisable()== false)? false : this.people.isIsDisable());
//                    
//                }catch (WTPropertyVetoException e){
//                    e.printStackTrace();
//                }catch (WTException e){
//                    e.printStackTrace();
//                }
//            }
//
//        }else if (obj instanceof People){
//            this.people = (People) obj;
//            this.wtuser = this.people.getUser();
//        }
//        this.wtuserOID = PersistenceHelper.getObjectIdentifier ( this.wtuser ).getStringValue ();
//        this.peopleOID = PersistenceHelper.getObjectIdentifier ( this.people ).getStringValue ();
//        setWTUserData();
//        setPeopleData();
//        setDepartmentData();
//    }
//
//    public PeopleData(String oid) throws Exception
//    {
//        this (rf.getReference(oid).getObject());
//    }
//    private void setWTUserData() throws Exception
//    {
//        if (this.wtuser == null) return;
//
//        this.id = this.wtuser.getName();
//        this.name = this.wtuser.getFullName();
//        this.name_en = this.wtuser.getFullName();
//        this.email = this.wtuser.getEMail();
//        if (this.email == null) this.email = "";
//    }
//
//    private void setPeopleData() throws Exception
//    {
//        if (this.people == null) return;
//        
//        this.name = this.people.getName();
//        
//        if(people.isIsDisable()){
//        	this.name = name + "(퇴사)";
//        }
//        this.name_en = this.people.getNameEn();
//
//        this.duty = this.people.getDuty();
//        if (this.duty == null) this.duty = "";
//        this.dutycode = this.people.getDutyCode();
//        if (this.dutycode == null) this.dutycode = "";
//        this.cellTel = this.people.getCellTel();
//        if (this.cellTel == null) this.cellTel = "";
//        this.homeTel = this.people.getHomeTel();
//        if (this.homeTel == null) this.homeTel = "";
//        this.officeTel = this.people.getOfficeTel();
//        if (this.officeTel == null) this.officeTel = "";
//        this.address = this.people.getAddress();
//        if (this.address == null) this.address = "";
//        this.isDiable = this.people.isIsDisable();
//        this.password = this.people.getPassword();       
//        this.imgUrl = getImageURL();
//        
//        if(CommonUtil.isMember("PM_Group", this.wtuser)) {
//        	this.gradeCode = "G001";
//        }else if(CommonUtil.isMember("Executive_Group", this.wtuser)) {
//        	this.gradeCode = "G002";
//        }else {
//        	this.gradeCode = "G003";
//        }
//        
//        this.gradeName = NumberCodeHelper.service.getValue("DOCGRADE", this.gradeCode);
//	}
//
//    private void setDepartmentData() throws Exception
//    {	if(people != null){
//    	
//	    	QueryResult qr = PersistenceHelper.manager.navigate(this.people, "department", DepartmentPeopleLink.class);
//	        if (qr.hasMoreElements())
//	        {
//	            this.department = (Department) qr.nextElement();
//	            this.departmentName = this.department.getName();
//	            
//	            String[] prefix = {"중대형전지.","Battery연구소."};
//	            
//	            for(int i=0; i< prefix.length; i++){
//	            	if(departmentName.startsWith(prefix[i])){
//	            		this.departmentName = departmentName.substring(prefix[i].length());
//	            		break;
//	            	}
//	            }
//	        }
//	        else
//	        {
//	            this.departmentName = "지정안됨";
//	        }
//    	}else{
//    		this.departmentName = "지정안됨";
//    	}
//    }
//
//    public String getImageURL() throws Exception{
//        this.people = (People) ContentHelper.service.getContents(this.people);
//        Vector appList = ContentHelper.getApplicationData(this.people);
//        URL picURL = CompanyState.defautlURL;
//        if (appList.size() > 0){
//            ApplicationData pic = (ApplicationData) appList.get(0);
//            picURL = ContentHelper.getDownloadURL(this.people, pic);
//        }
//        return picURL.toString();
//    }
//    
//    public boolean getChief(){
//    	
//    	if( this.people.getChief() == null){
//    		
//    		return false;
//    	}
//    	
//    	return this.people.getChief();
//    	
//    }


}