package com.e3ps.org.service;

import javax.servlet.http.HttpServletRequest;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;
import com.infoengine.util.Base64;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

@SuppressWarnings("serial")
public class StandardPeopleService extends StandardManager implements PeopleService {

	public static StandardPeopleService newStandardPeopleService() throws Exception {
		final StandardPeopleService instance = new StandardPeopleService();
		instance.initialize();
		return instance;
	}
	
	/**워크플로 상태변경 로봇 이벤트 실행시 
     * @param _obj
     * @param _event
     */
	@Override
    public void eventListener(Object _obj, String _event){ 
    	
    	if(_obj instanceof WTUser){
			if (_event.equals("POST_DISABLE"))
            {
                syncDelete((WTUser) _obj);
            }
            else if (_event.equals("POST_ENABLE"))
            {
                // 처리 상태 모호...
            }
            else if (_event.equals("POST_DELETE"))
            {
                syncDelete((WTUser) _obj);
            }
            else if (_event.equals("POST_MODIFY"))
            {
                syncModify((WTUser) _obj);
            }
            else if (_event.equals("POST_STORE"))
            {
                syncStore((WTUser) _obj);
            }
		}
    }
    
    @Override
    public void syncStore(WTUser _user)
    {
        try
        {
            People people = People.newPeople();
            people.setUser(_user);
            //people.setId(_user.getName());
            people.setName(_user.getFullName());
            
            //20140117 유저 싱크시 부서 추가.
            people.setDepartment(DepartmentHelper.service.getDepartment("NULL"));
            
           // people.setEmail((_user.getEMail() == null) ? "" : _user.getEMail());
           // people.setPwChangeDate(new Timestamp(new Date().getTime()));
            //System.out.println("[PeopleHelper][Create]"+_user.getName()+"|"+_user.getFullName());
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

    @Override
    public void syncModify(WTUser _user)
    {
        try
        {
            QueryResult qr = PersistenceHelper.manager.navigate(_user, "people", WTUserPeopleLink.class);
            if (qr.hasMoreElements())
            {
                People people = (People) qr.nextElement();
                //people.setId(_user.getName());
                people.setName(_user.getFullName());
                //people.setEmail((_user.getEMail() == null) ? "" : _user.getEMail());
                PersistenceHelper.manager.modify(people);
            }
            else
            {
                syncStore(_user);
            }
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

    @Override
    public void syncDelete(WTUser _user)
    {/*
        try
        {	
            QueryResult qr = PersistenceHelper.manager.navigate(_user, "people", WTUserPeopleLink.class);
            if (qr.hasMoreElements())
            {
                People people = (People) qr.nextElement();
                PersistenceHelper.manager.delete(people);
            }
            
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        */
    }

    @Override
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
                    syncModify(wtuser);
            }
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public People getPeople(String name)
    {
        try
        {
            QuerySpec spec = new QuerySpec();
            Class mainClass = People.class;
            int mainClassPos = spec.addClassList(mainClass, true);
            spec.appendWhere(new SearchCondition(mainClass, People.NAME, "=", name), new int[] { mainClassPos });
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
    
    @Override
    public void savePassword(HttpServletRequest req)
    {
        String auth = req.getHeader("authorization");
        auth = auth.substring("Basic ".length());
        String authDecoded = Base64.decode(auth);
        String userPassword = authDecoded.substring(authDecoded.indexOf(":") + 1);

        try
        {
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            People people = UserHelper.service.getPeople(user);//getPeople(user.getName());
            people.setPassword(userPassword);
            PersistenceHelper.manager.modify(people);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public WTUser getChiefUser(Department depart) {
		try {
			QuerySpec spec = new QuerySpec(People.class);
			spec.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", "=", CommonUtil.getOIDLongValue(depart)), new int[] { 0 });
			spec.appendAnd();
			spec.appendWhere(new SearchCondition(People.class, People.CHIEF, SearchCondition.NOT_NULL, true), new int[] {0 });
	        QueryResult qr = PersistenceHelper.manager.find(spec);

	        //System.out.println("getChiefUser = " +spec );
	        while (qr.hasMoreElements()){
	        	return ((People)qr.nextElement()).getUser();
	        }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return null;
    }
    
    @Override
    public QuerySpec getUserSearch(String name) throws QueryException{
    	QuerySpec spec = new QuerySpec();
		try {
			int idx = spec.addClassList(People.class, true);
			
			spec.appendWhere(new SearchCondition(People.class, "name", SearchCondition.EQUAL, name), new int[] { idx } );
		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return spec;
    }
}