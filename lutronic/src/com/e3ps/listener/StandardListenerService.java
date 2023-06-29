package com.e3ps.listener;

import java.io.Serializable;

import wt.epm.listeners.EPMEventServiceEvent;
import wt.epm.workspaces.EPMWorkspaceManagerEvent;
import wt.fc.PersistenceManagerEvent;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.DebugProperties;
import wt.util.DebugWriter;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressServiceEvent;
import wt.workflow.engine.WfEngineServiceEvent;
import wt.workflow.engine.WfEventAuditType;

import com.ptc.wvs.server.publish.PublishServiceEvent;

public class StandardListenerService extends StandardManager
  implements ListenerService, Serializable
{
  private static final String CLASSNAME = StandardListenerService.class.getName();

  private static final boolean DEBUG = DebugProperties.isDebugOn(CLASSNAME);
  private static final DebugWriter LOG = DEBUG ? DebugProperties.getWriter(CLASSNAME) : null;

  public String getConceptualClassname()
  {
    return CLASSNAME;
  }

  public static StandardListenerService newStandardListenerService()
    throws WTException
  {
    StandardListenerService instance = new StandardListenerService();
    instance.initialize();
    return instance;
  }

  protected synchronized void performStartupProcess()
    throws ManagerException
  {
    if ((DEBUG) && (DebugProperties.isTrace(this))) {
      LOG.enter(CLASSNAME, "performStartupProcess");
    }

    SessionContext prev = SessionContext.newContext();
    try {
      SessionHelper.manager.setAdministrator();
    }
    catch (WTException wte) {
      System.err.println("StandardListenerService: failed to set Administrator (ok if installation)");
      return;
    }
    finally {
      SessionContext.setContext(prev);
    }

    super.performStartupProcess();

    EventListener listener = new EventListener(getConceptualClassname());
    MailNotyListener maillistener = new MailNotyListener(this.getConceptualClassname());
    
    getManagerService().addEventListener(listener, LifeCycleServiceEvent.generateEventKey("STATE_CHANGE"));
    
    getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey("PRE_CHECKOUT"));
    getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey("POST_CHECKIN"));
    
    getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("PRE_DELETE"));
    getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("PRE_REMOVE"));
    getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("POST_DELETE"));
    getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("POST_STORE"));
    getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("POST_MODIFY"));

    getManagerService().addEventListener(listener, EPMWorkspaceManagerEvent.generateEventKey("NEW_TO_WORKSPACE"));
    getManagerService().addEventListener(listener, EPMWorkspaceManagerEvent.generateEventKey("PRE_WORKSPACE_CHECKIN"));
    getManagerService().addEventListener(listener, EPMWorkspaceManagerEvent.generateEventKey("POST_WORKSPACE_CHECKIN"));
    getManagerService().addEventListener(listener, EPMWorkspaceManagerEvent.generateEventKey("CHECKOUT_TO_WORKSPACE"));

    getManagerService().addEventListener(listener, EPMEventServiceEvent.generateEventKey("CHECKIN_COMPLETE"));
    getManagerService().addEventListener(listener, PublishServiceEvent.generateEventKey("PUBLISH_SUCCESSFUL"));
    
    getManagerService().addEventListener(maillistener, WfEngineServiceEvent.generateEventKey(WfEventAuditType.ACTIVITY_STATE_CHANGED));

    if ((DEBUG) && (DebugProperties.isTrace(this)))
      LOG.exit(CLASSNAME, "performStartupProcess");
  }
}