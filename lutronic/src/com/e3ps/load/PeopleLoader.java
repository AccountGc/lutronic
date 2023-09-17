package com.e3ps.load;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.e3ps.common.util.JExcelUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.dto.CompanyState;
import com.e3ps.org.service.UserHelper;


@SuppressWarnings("serial")
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
		HashMap groupMap = new HashMap();
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
                    String groupList = JExcelUtil.getContent(cell, 4).trim();
                    //String chief = JExcelUtil.getContent(cell, 4).trim();
                    //String grade = StringUtil.checkNull(JExcelUtil.getContent(cell, 5).trim());
                    //String vendor = StringUtil.checkNull(JExcelUtil.getContent(cell, 6).trim());
                    //boolean isVendor = vendor.toUpperCase().equals("Y") ? true : false;
                    //boolean chief = StringUtil.checkNull(chief);
                    
                   
                   
                    WTUser user = getWTUser(name);
                    
                    if(user == null){
                    	//System.out.println("j . WTUser :"+name+ " is not Exist");
                    	break;
                    }
                    
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
                   
                    /* 
                    if(chief.length()>0){
                    	people.setChief(chief.equals("O")?true:false);
                    }
                    */
                    PersistenceHelper.manager.save(people);
                    
                    //System.out.println(name + ":" + groupList);
                    WTGroup lutronicGroup = null;
                    if(groupList.length()>0){
                    	String[] groups= groupList.split(",");
                    	for(int idx = 0 ; idx < groups.length ; idx++){
                    		
                    		String groupName = groups[idx];
                    		if(groupMap.containsKey(groupName)){
                    			lutronicGroup = (WTGroup)groupMap.get(groupName);
                    		}else{
                    			lutronicGroup = getGroup(groupName);
                    			
                    			if(lutronicGroup != null){
                    				deleteGroupUser(lutronicGroup);
                    				groupMap.put(groupName, lutronicGroup);
                    			}
                    			
                    		}
                    		
                    		if(lutronicGroup != null){
                    			addGroupUser(lutronicGroup, user);
                    		}else{
                    			//System.out.println(name +" = "+j+"["+idx+"]" +". GrOUP : "+groupName + " is not Exist");
                    			//break;
                    			
                    		}
                    	}
                    }
                    
                    
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

	private void addGroupUser(String group, Vector users){
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
	
	private void addGroupUser(WTGroup wtgroup,WTUser user){
		try{
			//System.out.println("add User : " + wtgroup.getName() + "("+user.getFullName()+")");
			OrganizationServicesHelper.manager.addMember(wtgroup, (WTPrincipal)user);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void deleteGroupUser(WTGroup wtgroup){
		
		try{
			Enumeration<WTPrincipal> enm = OrganizationServicesHelper.manager.members(wtgroup);
			while (enm.hasMoreElements()){
				OrganizationServicesHelper.manager.removeMember(wtgroup, (WTPrincipal)enm.nextElement());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	private WTGroup getGroup(String name){
		try{
			//OrganizationServicesHelper.manager.getGroup
			//name = OrganizationServicesHelper.manager.getGroup(arg0, arg1);//wt.org.OrganizationServicesHelper.manager.getGroup(name);
			WTGroup group = UserHelper.service.getWTGroup(name);
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

