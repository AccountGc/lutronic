package com.e3ps.org.beans;

import java.util.*;
import java.io.*;
import java.sql.*;
import wt.method.*;
import wt.pom.*;
import wt.pds.*;
import wt.introspection.*;
import wt.session.*;
import wt.util.*;
import wt.fc.*;
import wt.org.*;
import wt.query.*;

import com.e3ps.common.util.StringUtil;
import com.e3ps.org.*;

public class UserHelper  implements Serializable,RemoteAccess 
{

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static UserHelper service = new UserHelper();
	
	public static boolean nameChange = false;
	/**
	 *  Windchill 10.0 이후 이름 처리
	 *  
	 */
	public void setAllUserName()throws Exception{
		
		if(!nameChange){
			QuerySpec query = new QuerySpec();
	        QueryResult qr = null;
	        int idx = query.addClassList(WTUser.class, true);
	        SearchCondition sc = new SearchCondition(WTUser.class, "first", "<>", "");
	        query.appendWhere(sc, new int[]{idx});
	        qr = PersistenceHelper.manager.find(query);
	        String fullName = "";
	        
	        while(qr.hasMoreElements()){
	     	   Object[] o = (Object[])qr.nextElement();
	            WTUser wtuser = (WTUser)o[0];
	            fullName = wtuser.getFullName();
	            int i = fullName.indexOf(", ");
	            if(i > 0){
	            	fullName = fullName.substring(0,i)+fullName.substring(i+2);
	            	wtuser.setFullName(fullName);
	            	PersistenceServerHelper.manager.update(wtuser);
	            }
	        }
	        nameChange = true;
	        //System.out.println("유저 변경완료!!!");
		}
	}
	
	/**
	* 접속한 계정이 Admin 그룹에 포함 되어 있는지를 알아낸다 <br>
	*/
	public static boolean isAdmin() throws Exception {
		return isMember ( "Administrators" );
	}

	/**
	* 접속한 계정이 Parameter로 넘어온 group 명의 그룹에 포함 되어 있는지를 알아낸다 <br>
	*/
	public static boolean isMember(String group) throws Exception {
		WTUser user = (wt.org.WTUser) SessionHelper.manager.getPrincipal ();
		Enumeration en = user.parentGroupNames ();
		while (en.hasMoreElements ()) {
			String st = (String) en.nextElement ();
			if (st.equals ( group )) return true;
		}
		return false;
	}

	public void syncSave(WTUser _user)
    {
		//System.out.println("syncSave");
        try
        {
			if(_user.isDisabled()){
				syncDelete(_user);
				return;
			}
			
			// Windchill 10 이후 이름 변경
			String fullName = _user.getFullName();
			int i = fullName.indexOf(", ");
			if(i>0){
				fullName = fullName.substring(0,i)+fullName.substring(i+2);
				_user.setFullName(fullName);
				PersistenceServerHelper.manager.update(_user);
			}
			//
			
			People people = getPeople(_user);

			if(people==null){
				people = People.newPeople();
				people.setNameEn(_user.getFullName());
			}
            
            people.setUser(_user);
            people.setName(_user.getFullName());

            PersistenceHelper.manager.save(people);
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        catch (WTPropertyVetoException e)
        {
            e.printStackTrace();
        }
    }

    public void syncDelete(WTUser _user)
    {
		//System.out.println("syncDelete");
        try
        {
            QueryResult qr = PersistenceHelper.manager.navigate(_user, "people", WTUserPeopleLink.class);
            if (qr.hasMoreElements())
            {
                People people = (People) qr.nextElement();
                people.setIsDisable(true);
                PersistenceHelper.manager.modify(people);
            }
        }
        catch (WTException e)
        {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void syncWTUser()
    {
        try
        {
            QuerySpec query = new QuerySpec(WTUser.class);
            QueryResult result = PersistenceHelper.manager.find(query);
            WTUser wtuser = null;
            while (result.hasMoreElements())
            {
                wtuser = (WTUser) result.nextElement();
                if (!wtuser.isDisabled())
                    syncSave(wtuser);
            }
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
    }

	public People getPeople(WTUser user)
    {
		return getPeople(user.getPersistInfo().getObjectIdentifier().getId());
	}

    public People getPeople(long userid)
    {
        try
        {
            QuerySpec spec = new QuerySpec();
            Class mainClass = People.class;
            int mainClassPos = spec.addClassList(mainClass, true);
            spec.appendWhere(new SearchCondition(mainClass, "userReference.key.id", "=", userid), new int[] { mainClassPos });
            QueryResult qr = PersistenceHelper.manager.find(spec);
            if (qr.hasMoreElements())
            {
                Object[] obj = (Object[]) qr.nextElement();
                return (People) obj[0];
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
    
    public WTUser getUser(String id)
    {
        try
        {
            QuerySpec spec = new QuerySpec();
            Class mainClass = WTUser.class;
            int mainClassPos = spec.addClassList(mainClass, true);
            spec.appendWhere(new SearchCondition(mainClass, "name", "=", id), new int[] {
                mainClassPos
            });
            QueryResult qr = PersistenceHelper.manager.find(spec);
            if(qr.hasMoreElements())
            {
                Object obj[] = (Object[])qr.nextElement();
                return (WTUser)obj[0];
            }
        }
        catch(QueryException e)
        {
            e.printStackTrace();
        }
        catch(WTException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getUserInfo(WTUser user) throws Exception{

		People people = UserHelper.service.getPeople(user);
		Department dept1 = people.getDepartment();
		
		String[] infos = new String[4];
		
		String useroid = StringUtil.checkNull( PersistenceHelper.getObjectIdentifier ( user ).getStringValue());
		String peopleoid = StringUtil.checkNull(PersistenceHelper.getObjectIdentifier ( people ).getStringValue());
		
		String deptoid = "";
		if(dept1 != null){
			deptoid = StringUtil.checkNull(PersistenceHelper.getObjectIdentifier ( dept1 ).getStringValue()); 
		}
		
		String id = StringUtil.checkNull(user.getName());
		String name = StringUtil.checkNull(user.getFullName());

		String departmentname = dept1==null?"":StringUtil.checkNull(dept1.getName());
		String duty = StringUtil.checkNull(people.getDuty());
		String dutycode = StringUtil.checkNull(people.getDutyCode());
		String email = StringUtil.checkNull(user.getEMail());
		String temp = StringUtil.checkNull(people.getName());
		
		String values = useroid + "," + peopleoid + "," + deptoid + "," + id + "," + name + "," + 
		departmentname + "," + duty + "," + dutycode + "," + email + "," + temp; 
		
		infos[0] = departmentname;
		infos[1] = id;
		infos[2] = duty;
		infos[3] = values;
		
    	return infos;
    }
    
    public Department getDepartment(String code)throws Exception{
    	QuerySpec qs = new QuerySpec(Department.class);
    	qs.appendWhere(new SearchCondition(Department.class,Department.CODE,"=",code),new int[]{0});
    	QueryResult qr = PersistenceHelper.manager.find(qs);
    	if(qr.hasMoreElements()){
    		Department dept = (Department)qr.nextElement();
    		return dept;
    	}
    	return null;
    }
    
    public WTGroup getWTGroup(String code)throws Exception{
    	QuerySpec qs = new QuerySpec(WTGroup.class);
    	qs.appendWhere(new SearchCondition(WTGroup.class,WTGroup.NAME,"=",code),new int[]{0});
    	QueryResult qr = PersistenceHelper.manager.find(qs);
    	if(qr.hasMoreElements()){
    		WTGroup dept = (WTGroup)qr.nextElement();
    		return dept;
    	}
    	return null;
    }
    
    public static WTUser getWTUser(String name)
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
    
    /**진영산업 = JY
     * 진영산업(울산) = UJ
     * 마성산업 = MS
     * 북경산업 = BJ
     * 금주진영 = CJ 
     * 부서별 로그 
     * @return
     * @throws WTException 
     */
    public String getDepartmentImg() throws WTException{
    	
    	WTUser user = (WTUser)SessionHelper.getPrincipal();
    	return getDepartmentImg(user);
    }
    
    public String getDepartmentImg(WTUser user){
    	String imgURL = "\\Windchill\\jsp\\portal\\images\\img_menu\\";
    	try{
    		//WTUser user = (WTUser)SessionHelper.getPrincipal();
    		Department dp= DepartmentHelper.manager.getDepartment(user);
    		
    		String img = "JY_LOGIN.gif";
    		if(dp != null){
    			String departCode = dp.getCode();
    			if(departCode.toUpperCase().startsWith("JY") || departCode.toUpperCase().startsWith("UJ")){
    				img = "JY_LOGIN.gif";
    			}else if(departCode.toUpperCase().startsWith("MS")){
    				img = "MS_LOGIN.gif";
    			}else if(departCode.toUpperCase().startsWith("BJ")){
    				img = "BJ_LOGIN.gif";
    			}else if(departCode.toUpperCase().startsWith("CJ")){
    				img = "CJ_LOGIN.gif";
    			}
    		}
    		imgURL =imgURL+img;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return imgURL;
    }
    
    public String getDepartmentImg(Department dp){
    	
    	String imgURL = "\\Windchill\\jsp\\portal\\images\\img_menu\\";
    	try{
    		
    		String img = "JY_LOGIN.gif";
    		if(dp != null){
    			String departCode = dp.getCode();
    			if(departCode.toUpperCase().startsWith("JY") || departCode.toUpperCase().startsWith("UJ")){
    				img = "topMenu_left2.png";
    			}else if(departCode.toUpperCase().startsWith("MS")){
    				img = "MS_LOGIN.gif";
    			}else if(departCode.toUpperCase().startsWith("BJ")){
    				img = "BJ_LOGIN.gif";
    			}else if(departCode.toUpperCase().startsWith("CJ")){
    				img = "CJ_LOGIN.gif";
    			}
    		}
    		imgURL =imgURL+img;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return imgURL;
    }
    
}