package com.e3ps.org.beans;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.*;
import wt.util.*;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.query.*;

import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.common.util.*;

import java.text.*;

import jxl.*;

import java.io.*;


public class PeopleLoader  implements wt.method.RemoteAccess, java.io.Serializable {
	
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static void main(String[] args)throws Exception{
		
		String sWtHome = wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
        String sFilePath = sWtHome + "\\loadFiles\\lgchem\\People.xls" ;
		
        if(args!=null && args.length>0 && args[0].trim().length()>0){
        	sFilePath = args[0];
        }
		new PeopleLoader().loadPeople(sFilePath);
	}
	
	public void loadPeople(String sFilePath )throws Exception{
		
		File newfile =  new File(sFilePath);

		Workbook wb = JExcelUtil.getWorkbook(newfile);

		Sheet[] sheets = wb.getSheets();
		WTGroup excutiveGroup = null;
		WTGroup pmGroup = null;
		WTGroup intellianGroup = null;
		WTGroup vendorGroup = null;
		//그룹 설정
       
    	excutiveGroup = getGroup("Executive_Group");
    	if(excutiveGroup == null){
    		createGroup("Executive_Group", "임원그룹");
    	}
   
    	pmGroup = getGroup("PM_Group");
    	if(pmGroup ==  null){
    		createGroup("PM_Group", "팀장/PM");
    	}
    	
    	intellianGroup = getGroup("Intellian_Group");
    	if(pmGroup ==  null){
    		createGroup("Intellian_Group", "intellian 내부 사용자");
    	}
    	
    	vendorGroup = getGroup("Vendor_Group");
    	if(pmGroup ==  null){
    		createGroup("Vendor_Group", "intellian 협력 업체");
    	}
       
        
        //System.out.println("USER GROUP : " +excutiveGroup.getName() +","+ pmGroup.getName());
        for (int i = 0; i < sheets.length; i++)
        {
            int rows = sheets[i].getRows();
            for (int j = 1; j < rows; j++)
            {
                if (JExcelUtil.checkLine(sheets[i].getRow(j),0))
                {
                    Cell[] cell = sheets[i].getRow(j);
                    
                    String name = JExcelUtil.getContent(cell, 0).trim();
                    String fullName = JExcelUtil.getContent(cell, 1).trim();
                    String dutyName = JExcelUtil.getContent(cell, 2).trim();
                    String department = JExcelUtil.getContent(cell, 3).trim();
                    String chief = JExcelUtil.getContent(cell, 4).trim();
                    String grade = StringUtil.checkNull(JExcelUtil.getContent(cell, 5).trim());
                    String vendor = StringUtil.checkNull(JExcelUtil.getContent(cell, 6).trim());
                    boolean isVendor = vendor.toUpperCase().equals("Y") ? true : false;
                    chief = StringUtil.checkNull(chief);
                    
                   
                   
                    WTUser user = getWTUser(name);
                    
        			People people = UserHelper.service.getPeople(user);

        			if(people==null){
        				people = People.newPeople();
        			}
        			
        			
                    people.setDuty(dutyName);
                    
                    if(dutyName != null && dutyName.length() > 0){
                    	Hashtable nameHash = CompanyState.nameTable;
                    	String dutyCode = (String)nameHash.get(dutyName);
                    	people.setDutyCode(dutyCode);
                    }
                    people.setUser(user);
                    people.setName(fullName);
                    
                    if(department!=null){
                    	people.setDepartment(getDept(department.trim()));
                    }
                    
                    if(chief.length()>0){
                    	people.setChief(chief.equals("O")?true:false);
                    }
                    
                    PersistenceHelper.manager.save(people);
                    
                    
                    
                    
                    if(isVendor){
                    	
                    	addUser(vendorGroup, user);
                    
                    }else{
                    	addUser(intellianGroup, user);
                    	
                    	if(grade.equals("A")){
                        	addUser(excutiveGroup, user);
                        	
                        }else if(grade.equals("B")){
                        	addUser(pmGroup, user);	//팀장/PM
                        }
                    }
                    
                    //System.out.println(name + ":" + fullName + " , " +grade);
                    
                }
            }
        }// for
        
        
        
	}
	
	
    public WTUser getWTUser(String name)
    {
        try
        {
            QuerySpec spec = new QuerySpec();
            int userPos = spec.addClassList(WTUser.class,true);
            spec.appendWhere(new SearchCondition(WTUser.class,WTUser.NAME,"=",name),new int[]{userPos});
            QueryResult qr = PersistenceHelper.manager.find(spec);
            if (qr.hasMoreElements())
            {
                Object[] obj = (Object[]) qr.nextElement();
                return (WTUser) obj[0];
            }
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
	public Department getDept(String code)throws Exception{
		QuerySpec qs = new QuerySpec(Department.class);
		qs.appendWhere(new SearchCondition(Department.class,Department.CODE,"=",code),new int[]{0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if(qr.size()==1){
			return (Department)qr.nextElement();
		}
		return null;
	}
	

	public Department getDeptByName(String name)throws Exception{
		QuerySpec qs = new QuerySpec(Department.class);
		qs.appendWhere(new SearchCondition(Department.class,"name","=",name),new int[]{0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if(qr.size()==1){
			return (Department)qr.nextElement();
		}
		return null;
	}
	
	private WTGroup createGroup(String name,String desc)
    {
        try
        {
			//System.out.println("create Group : " + name + "("+desc+")");
            if (getGroup(name)!=null) return null;

            WTGroup group = WTGroup.newWTGroup(name);
            group.setDescription(desc);
            return (WTGroup) OrganizationServicesHelper.manager.savePrincipal(group);

        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        catch (WTPropertyVetoException e)
        {
            e.printStackTrace();
        }
        return null;
    }

	private void addUser(String group, Vector users){
		try
        {
			
			WTGroup wtgroup = getGroup(group);
			for (int i = 0; i<users.size() ;i++)
			{
					//System.out.println("add User : " + group + "("+(String)users.get(i)+")");
					OrganizationServicesHelper.manager.addMember(wtgroup, (WTPrincipal)getUser( (String)users.get(i)));
			}
		}catch (WTException e)
        {
            e.printStackTrace();
        }
	}
	
	private void addUser(WTGroup wtgroup,WTUser user){
		try{
			//System.out.println("add User : " + wtgroup.getName() + "("+user.getFullName()+")");
			OrganizationServicesHelper.manager.addMember(wtgroup, (WTPrincipal)user);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private WTGroup getGroup(String name){
		try{
			WTGroup group = wt.org.OrganizationServicesHelper.manager.getGroup(name);
			return group;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private WTUser getUser(String name){
		try{
			WTUser user = wt.org.OrganizationServicesHelper.manager.getUser(name);
			return user;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}

