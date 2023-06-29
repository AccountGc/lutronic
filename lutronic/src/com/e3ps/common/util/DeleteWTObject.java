/*
 * @(#) DeleteWTObject.java  Create on 2005. 3. 24.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.common.util;

import java.util.Locale;

import wt.access.WTAclEntry;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTException;

import com.e3ps.common.jdf.log.Logger;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.groupware.workprocess.service.WFItemHelper;

/**
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2005. 3. 24.
 * @since 1.4
 */
public class DeleteWTObject implements RemoteAccess
{
    /** 객체 삭제
     * @param persistable
     * @return
     */
    public static String delete(Persistable persistable) {

        String msg = "Delete successful";
        if (persistable instanceof LifeCycleManaged)
            E3PSWorkflowHelper.service.deleteWfProcess((LifeCycleManaged) persistable);
        try
        {
            String temp = persistable.getIdentity();
            WFItemHelper.service.deleteWFItem(persistable);
            if(persistable instanceof WTObject)
                deleteAclObject((WTObject) persistable);
            PersistenceHelper.manager.delete(persistable);
            
            WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
            Logger.info.println(temp + "- by " + user.getFullName() +", "+ user.getName() );
        }
        catch (WTException e)
        {
            msg = e.getLocalizedMessage(Locale.KOREA);
            Logger.info.println(msg);
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return msg;
    }

    /**객체 삭제시 
     * @param obj
     */
    public static void deleteAclObject(WTObject obj)
    {
        try
        {
            QuerySpec query = new QuerySpec(WTAclEntry.class);
            if (obj instanceof TeamManaged)
            {
                Team team = TeamHelper.service.getTeam((TeamManaged) obj);
                if(team == null )return;
                query.appendWhere(new SearchCondition(WTAclEntry.class, "aclReference.key.id", "=", CommonUtil
                        .getOIDLongValue(team)), new int[] { 0 });
                query.appendOr();
            }
            query.appendWhere(new SearchCondition(WTAclEntry.class, "aclReference.key.id", "=", CommonUtil
                    .getOIDLongValue(obj)), new int[] { 0 });

            QueryResult qr = PersistenceHelper.manager.find(query);

            while (qr.hasMoreElements())
            {
                WTAclEntry element = (WTAclEntry) qr.nextElement();
                PersistenceHelper.manager.delete(element);
            }
            // PersistenceHelper.manager.delete(obj);
        }
        catch (QueryException e)
        {
            e.printStackTrace();
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