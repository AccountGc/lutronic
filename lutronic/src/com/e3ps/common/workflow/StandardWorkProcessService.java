package com.e3ps.common.workflow;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.lifecycle.LifeCycleManaged;
import wt.org.OrganizationServicesHelper;
import wt.org.OrganizationServicesMgr;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.util.WTException;


@SuppressWarnings("serial")
public class StandardWorkProcessService extends StandardManager implements WorkProcessService {
	
	public static StandardWorkProcessService newStandardWorkProcessService() throws Exception {
		final StandardWorkProcessService instance = new StandardWorkProcessService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public void setDefaultAssignee(WTObject obj) throws WTException {
    	WTPrincipal assignee = SessionHelper.manager.getPrincipal();
    	setAssignee(obj, assignee);
    	//wt.team.TeamHelper.service.addRolePrincipalMap(Role.toRole("ASSIGNEE"), assignee, roleHolder);
    }
	
	@Override
	public void setAssignee(WTObject obj, WTPrincipal assignee) throws WTException {
    	addPrincialToRole(obj, "ASSIGNEE", assignee);
    	//wt.team.TeamHelper.service.addRolePrincipalMap(Role.toRole("ASSIGNEE"), assignee, roleHolder);
    }
	
	/**
     * @param obj
     * @param roleName
     * @param id
     */
	@Override
    public void addParticipant(WTObject obj, String roleName, String id)
    {
        if (obj instanceof TeamManaged)
        {
            try
            {
                Team team = TeamHelper.service.getTeam((TeamManaged) obj);
                Role role = Role.toRole(roleName);
                team.addPrincipal(role, OrganizationServicesMgr.getPrincipal(id));
            }
            catch (TeamException e)
            {
                e.printStackTrace();
            }
            catch (WTException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param obj
     * @param roleName
     * @param assignedId[]
     */
	@Override
    public void addPrincialToRole(WTObject obj, String roleName, String[] assignedId)
    {
    	if (obj instanceof TeamManaged)
        {
	        try
	        {
	        	//Team team = (Team)((TeamManaged)obj).getTeamId().getObject();
	        	Team team = TeamHelper.service.getTeam((TeamManaged) obj);
				Enumeration userList = team.getPrincipalTarget(Role.toRole(roleName));
				while (userList.hasMoreElements()) {
				   TeamHelper.service.deleteRolePrincipalMap(Role.toRole(roleName),
						   								(wt.org.WTPrincipal)((wt.org.WTPrincipalReference)userList.nextElement()).getObject(),
						   								(wt.team.WTRoleHolder2) team);
				}
				
				if (assignedId != null) {
	
					for (int i=0; i<assignedId.length; i++) {		            
						WTPrincipal lUser = OrganizationServicesHelper.manager.getPrincipal(assignedId[i], OrganizationServicesHelper.manager.getDefaultDirectoryService());
			            //WTPrincipal lUser = OrganizationServicesHelper.manager.getAuthenticatedUser(assignedId[i]);	
			            if (lUser != null) {
				            /*
							    if(lUser instanceof WTGroup) {
							    	System.out.println("group.....�Դϴ�.");
							    }
						    */
						    wt.team.TeamHelper.service.addRolePrincipalMap(Role.toRole(roleName)
						                                                     , lUser
						                                                     , (wt.team.WTRoleHolder2) team);
						    //wt.lifecycle.LifeCycleHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged)obj);			        
						}
			        }
				}
	        }
	        catch (TeamException e)
	        {
	            e.printStackTrace();
	        }
	        catch (WTException e)
	        {
	            e.printStackTrace();
	        }
        }
    }
    
    /**
     * 
     * @param obj
     * @param roleName
     * @param assignedId
     */
	@Override
    public void addPrincialToRole(WTObject obj, String roleName, Vector users)
    {
    	if (obj instanceof TeamManaged)
        {
	        try
	        {
	        	//Team team = (Team)((TeamManaged)obj).getTeamId().getObject();
	        	Team team = TeamHelper.service.getTeam((TeamManaged) obj);
				Enumeration userList = team.getPrincipalTarget(Role.toRole(roleName));
				while (userList.hasMoreElements()) {
				   TeamHelper.service.deleteRolePrincipalMap(Role.toRole(roleName),
						   								(wt.org.WTPrincipal)((wt.org.WTPrincipalReference)userList.nextElement()).getObject(),
						   								(wt.team.WTRoleHolder2) team);
				}
				
				if (users != null) {					
					WTPrincipal lUser = null;
						
					WTPrincipal principal = null;					
					for (int i=0; i<users.size(); i++) {
						principal = (WTPrincipal)users.get(i);
						lUser = OrganizationServicesHelper.manager.getPrincipal(principal.getName(), OrganizationServicesHelper.manager.getDefaultDirectoryService());
			            if (lUser != null) {
				            /*
							    if(lUser instanceof WTGroup) {
							    	System.out.println("group.....�Դϴ�.");
							    }
						    */
						    wt.team.TeamHelper.service.addRolePrincipalMap(Role.toRole(roleName)
						                                                     , lUser
						                                                     , (wt.team.WTRoleHolder2) team);
						    //wt.lifecycle.LifeCycleHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged)obj);	
						}
			        }
				}
	        }
	        catch (TeamException e)
	        {
	            e.printStackTrace();
	        }
	        catch (WTException e)
	        {
	            e.printStackTrace();
	        }
        }
    }
    
    /**
     * @param obj
     * @param roleName
     * @param assignedId
     */
	@Override
    public void addPrincialToRole(WTObject obj, String roleName, String assignedId)
    {	
		//강제로 ACL 해제
		boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
    	if (obj instanceof TeamManaged)
        {
	        try
	        {	
	        	//Team team = (Team)((TeamManaged)obj).getTeamId().getObject();
	        	Team team = TeamHelper.service.getTeam((TeamManaged) obj);
				Enumeration userList = team.getPrincipalTarget(Role.toRole(roleName));
				while (userList.hasMoreElements()) {
				   TeamHelper.service.deleteRolePrincipalMap(Role.toRole(roleName),
						   								(wt.org.WTPrincipal)((wt.org.WTPrincipalReference)userList.nextElement()).getObject(),
						   								(wt.team.WTRoleHolder2) team);
				}
				
				if (assignedId != null) {
					
		            WTPrincipal lUser = OrganizationServicesHelper.manager.getPrincipal(assignedId, OrganizationServicesHelper.manager.getDefaultDirectoryService());
		            //WTPrincipal lUser = OrganizationServicesHelper.manager.getAuthenticatedUser(assignedId[i]);	
		            if (lUser != null) {
			            /*
						    if(lUser instanceof WTGroup) {
						    	System.out.println("group.....�Դϴ�.");
						    }
					    */
					    wt.team.TeamHelper.service.addRolePrincipalMap(Role.toRole(roleName)
					                                                     , lUser
					                                                     , (wt.team.WTRoleHolder2) team);
					    //wt.lifecycle.LifeCycleHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged)obj);			        
					
		            }			        
				}
	        }
	        catch (TeamException e)
	        {
	            e.printStackTrace();
	        }
	        catch (WTException e)
	        {
	            e.printStackTrace();
	        }finally{
	        	//강제로 ACL 적용
	        	SessionServerHelper.manager.setAccessEnforced(enforced);
	        }
        }
    }
    
    /**
     * @param obj
     * @param roleName
     * @param lUser
     */
	@Override
    public void addPrincialToRole(WTObject obj, String roleName, WTPrincipal lUser)
    {
    	if (obj instanceof TeamManaged)
        {
	        try
	        {
	        	//Team team = (Team)((TeamManaged)obj).getTeamId().getObject();
	        	Team team = TeamHelper.service.getTeam((TeamManaged) obj);
				Enumeration userList = team.getPrincipalTarget(Role.toRole(roleName));
				while (userList.hasMoreElements()) {
				   TeamHelper.service.deleteRolePrincipalMap(Role.toRole(roleName),
						   								(wt.org.WTPrincipal)((wt.org.WTPrincipalReference)userList.nextElement()).getObject(),
						   								(wt.team.WTRoleHolder2) team);
				}
				
				if (lUser != null) {
					/*		            
			            if(lUser instanceof WTGroup) {
			            	System.out.println("group.....�Դϴ�.");
			            }
			        */
		            
		            wt.team.TeamHelper.service.addRolePrincipalMap(Role.toRole(roleName)
		                                                             , lUser
		                                                             , (wt.team.WTRoleHolder2) team);
		            
		            wt.team.TeamHelper.service.augmentRoles(getLCMObject(team), wt.team.TeamReference.newTeamReference(team));
		            //wt.lifecycle.LifeCycleHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged)obj);			        
				}
	        }
	        catch (TeamException e)
	        {
	            e.printStackTrace();
	        }
	        catch (WTException e)
	        {
	            e.printStackTrace();
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
        }
    }
        
    /**
     * @param obj
     */
	@Override
    public void deleteAllRoles(WTObject obj) {
  
        if (obj instanceof TeamManaged)
        {
            try
            {
                Team team = TeamHelper.service.getTeam((TeamManaged) obj);
                Vector vecRole = team.getRoles();
                for (int i = vecRole.size() - 1; i > -1; i--)
                {
                    team.deleteRole((Role) vecRole.get(i));
                }
            }
            catch (TeamException e)
            {
                e.printStackTrace();
            }
            catch (WTException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * @param obj
     * @param roleName
     * @param id
     */
	@Override
    public void deleteParticipant(WTObject obj, String roleName, String id)
    {
        
        if (obj instanceof TeamManaged)
        {
            try
            {
                Team team = TeamHelper.service.getTeam((TeamManaged) obj);
                Role role = Role.toRole(roleName);

                Vector vecRole = team.getRoles();
                for (int i = vecRole.size() - 1; i > -1; i--)
                {
                    if (role.equals((Role) vecRole.get(i)))
                    {
                        team.deletePrincipalTarget(role, OrganizationServicesMgr.getPrincipal(id));
                        break;
                    }
                }
            }
            catch (TeamException e)
            {
                e.printStackTrace();
            }
            catch (WTException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param obj
     * @param roleName
     */
	@Override
    public void deleteRole(WTObject obj, String roleName)
    {
        
        if (obj instanceof TeamManaged)
        {
            try
            {
                Team team = TeamHelper.service.getTeam((TeamManaged) obj);
                Role role = Role.toRole(roleName);

                Vector vecRole = team.getRoles();
                for (int i = vecRole.size() - 1; i > -1; i--)
                {
                    if (role.equals((Role) vecRole.get(i)))
                    {
                        team.deleteRole((Role) vecRole.get(i));
                        break;
                    }
                }
            }
            catch (TeamException e)
            {
                e.printStackTrace();
            }
            catch (WTException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param obj
     * @param roleName
     * @return boolean
     */
	@Override
    public boolean includeRole(WTObject obj, String roleName)
    {

        boolean flag = false;
        if (obj instanceof TeamManaged)
        {
            try
            {
                Team team = TeamHelper.service.getTeam((TeamManaged) obj);
                Role role = Role.toRole(roleName);

                Vector vecRole = team.getRoles();
                for (int i = vecRole.size() - 1; i > -1; i--)
                {
                    if (role.equals((Role) vecRole.get(i)))
                    {/*
                        Enumeration enum = team.getPrincipalTarget(role);
                        if (enum.hasMoreElements())
                        {
                            flag = true;
                            break;
                        }
                        */
                    }
                }
            }
            catch (TeamException e)
            {
                e.printStackTrace();
            }
            catch (WTException e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }
    
    /**
     * TeamManaged ��ü���� Ư��Role�� ����ڵ��� ��� �����Ѵ�.
     * 
     * @param obj
     * @param roleName
     * @return WTUser�� ���� Vector
     */
	@Override
    public Vector getRoleParticipant(TeamManaged obj, String roleName)
    {
        Vector list = new Vector();        
        try
        {
            Team team = TeamHelper.service.getTeam((TeamManaged) obj);
            Role role = Role.toRole(roleName);

            Enumeration r = team.getPrincipalTarget(role);
            while (r.hasMoreElements())
            {
                WTPrincipalReference ref = (WTPrincipalReference) r.nextElement();
                list.addElement(ref.getPrincipal());
            }
        }
        catch (TeamException e)
        {
            e.printStackTrace();
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        
        return list;
    }
    
	@Override
    public String getTaskName(String key, Locale locale) {
    	String taskName = "";
    	try {
    		if( (key == null) || (key.trim().length() == 0)) {
    			return "";
    		}
    		
    		if(locale == null)
    			locale = Locale.getDefault();
    		
    		if (key != null) {    		
	    		ResourceBundle resourcebundle = ResourceBundle.getBundle("wt.clients.workflow.tasks.TasksRB", locale);
	    		if(resourcebundle.getString(key) == null){
	    			taskName = key;
	    		} else {
	    			taskName = resourcebundle.getString(key);
	    		}	    	
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}    	
    	return taskName;
    }
    
	@Override
    public LifeCycleManaged getLCMObject(String paramString) throws Exception
    {
		LifeCycleManaged localLifeCycleManaged = null;
		 
		ReferenceFactory localReferenceFactory = new ReferenceFactory();
		WTReference localWTReference = localReferenceFactory.getReference(paramString);
		WTObject localWTObject = (WTObject)localWTReference.getObject();
		 
		if (localWTObject instanceof LifeCycleManaged) {
			localLifeCycleManaged = (LifeCycleManaged)localWTObject;
		}
		else if (localWTObject instanceof Team)
		{
			Vector localVector = TeamHelper.service.whereUsed(TeamReference.newTeamReference((Team)localWTObject));
			if (localVector.size() > 0)
			{
				localWTReference = (WTReference)localVector.elementAt(0);
				if (localWTReference.getObject() instanceof LifeCycleManaged)
				{
					localLifeCycleManaged = (LifeCycleManaged)localWTReference.getObject();
				}
			}
		}
		return localLifeCycleManaged;
	}
	@Override
    public LifeCycleManaged getLCMObject(Team team) throws Exception
    {
		LifeCycleManaged localLifeCycleManaged = null;
		
		WTReference localWTReference = null;
		Vector localVector = TeamHelper.service.whereUsed(TeamReference.newTeamReference(team));
		if (localVector.size() > 0)
		{
			localWTReference = (WTReference)localVector.elementAt(0);
			if (localWTReference.getObject() instanceof LifeCycleManaged)
			{
				localLifeCycleManaged = (LifeCycleManaged)localWTReference.getObject();
			}
		}
		
		return localLifeCycleManaged;
	}
}