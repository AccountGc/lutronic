package com.e3ps.groupware.workprocess.service;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.fc.EnumeratedType;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.introspection.WTIntrospectionException;
import wt.org.OrganizationServicesMgr;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.ActorRole;
import wt.project.Role;
import wt.query.DateHelper;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamTemplate;
import wt.util.WTException;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfVariableInfo;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.obj.ReflectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.e3ps.rohs.ROHSMaterial;

@SuppressWarnings("serial")
public class StandardWorklistService extends StandardManager implements WorklistService {

	public static StandardWorklistService newStandardWorklistService() throws Exception {
		final StandardWorklistService instance = new StandardWorklistService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public String[] getWorkItemName(WTObject pbo) {
        String[] result = new String[3];
        String type = null;;
        try {
            type = pbo.getClassInfo().getDisplayName();
        }
        catch (WTIntrospectionException e)
        {
            e.printStackTrace();
        } 

        if (pbo instanceof WTPart)
        {
            result[0] = "부품";
            //result[1] = new PartData((wt.part.WTPart)pbo).getNumber();
            result[2] = ((WTPart)pbo).getName();
            return result;
        }else if(pbo instanceof EChangeActivity){
        	EChangeActivity eca = (EChangeActivity)pbo;
        	 result[0] = eca.getName();//getDefinition().getName();
             result[1] = eca.getEo().getEoNumber();
             result[2] = eca.getEo().getEoName();
             return result;
        }else if(pbo instanceof EChangeOrder){
        	EChangeOrder eco = (EChangeOrder)pbo;
        	result[0] = eco.getEoType().equals("CHANGE") ? "ECO" : "EO";
            result[1] = eco.getEoNumber();
            result[2] = eco.getEoName();
        }else if(pbo instanceof EChangeRequest){
        	EChangeRequest ecr = (EChangeRequest)pbo;
        	result[0] = "ECR";
            result[1] = ecr.getEoNumber();
            result[2] = ecr.getEoName();
        }else if(pbo instanceof ROHSMaterial){
        	result[0] = "RoHS";
            result[1] = ReflectUtil.callGetMethod(pbo, "Number");
            result[2] = ReflectUtil.callGetMethod(pbo, "Name");
        }else if(pbo instanceof AsmApproval){
        	result[0] = "일괄결재";
            result[1] = ReflectUtil.callGetMethod(pbo, "Number");
            result[2] = ReflectUtil.callGetMethod(pbo, "Name");
        }else{
        	result[0] = type;
            result[1] = ReflectUtil.callGetMethod(pbo, "Number");
            result[2] = ReflectUtil.callGetMethod(pbo, "Name");
        }

        return result;
    }
	
	@Override
	public String getCreatorName(WTObject pbo) {
    	String result = "";
    	String type = "";
        try {
        	type = pbo.getClassInfo().getDisplayName();
        }catch (WTIntrospectionException e) {
            e.printStackTrace();
        } 

        if(pbo instanceof EChangeActivity){
        	EChangeActivity eca = (EChangeActivity)pbo;
        	result = eca.getCreatorFullName(); 
        }else if(pbo instanceof EChangeOrder){
        	EChangeOrder eco = (EChangeOrder)pbo;
        	result = eco.getCreatorFullName();
        }else if(pbo instanceof EChangeRequest){
        	EChangeRequest ecr = (EChangeRequest)pbo;
        	result = ecr.getCreatorFullName();
        }else if(pbo instanceof WTDocument){
        	WTDocument doc = (WTDocument)pbo;
        	result = doc.getCreatorFullName();
        }
    	return result;
    }
	
	@Override
	public Persistable getPBO(WorkItem workItem) {
        Persistable pbo = null;
        try {
            pbo = workItem.getPrimaryBusinessObject().getObject();
        }
        catch (Exception e) {
        	e.printStackTrace();
            //System.out.println("사용가능하지 않은 WorkItem = " + workItem);
        }

        return pbo;
    }
	
	@Override
	public Vector completeWfActivity(WorkItem workItem, Hashtable params) throws WTException {
		WfActivity activity = (WfActivity) workItem.getSource().getObject();
		WfVariableInfo varInfo[] = ((WfAssignedActivityTemplate) activity.getTemplateReference().getObject()).getContextSignature().getVariableList();
		ProcessData processData = activity.getContext();

		for (int j = 0; j < varInfo.length; j++) {
			if (varInfo[j].isVisible() && !varInfo[j].isReadOnly()
					&& !varInfo[j].getName().equals("instructions")
					&& !varInfo[j].getName().equals("primaryBusinessObject")) {
				String s1 = null;
				Object paramObj = null;
				if (params.size() != 0) {
					paramObj = params.get(varInfo[j].getName());
					if (paramObj instanceof String)
						s1 = (String) paramObj;
					else if (paramObj instanceof Vector)
						s1 = (String) ((Vector) paramObj).get(0);
					else if (paramObj instanceof String[]) {
						if (((String[]) paramObj).length > 0)
							s1 = ((String[]) paramObj)[0];
					}// end if
				}// end if
				if (s1 == null
						&& (varInfo[j].getTypeName()
								.equals("java.lang.Boolean") || varInfo[j]
								.getTypeName().equals("boolean")))
					s1 = "false";

				if (s1 != null)
					processData = updateContext(processData, varInfo[j].getName(), s1);
			}
		}

		String key = null;
		Vector vec = null;
		Enumeration enm = params.keys();
		while (enm.hasMoreElements()) {
			key = (String) enm.nextElement();
			if (key.startsWith("WfUserEvent")) {
				if (vec == null)
					vec = new Vector();
				vec.addElement((String) params.get(key));
			}
		}

		if (processData != null) {
			activity.setContext(processData);
			PersistenceHelper.manager.save(activity);
		}

		return vec;
	}
	
	@Override
	public ProcessData updateContext(ProcessData processdata, String variableName, String val) throws WTException {
		Class class1 = processdata.getVariableClass(variableName);
		Locale locale = Locale.KOREA;
		ReferenceFactory rf = new ReferenceFactory();

		try {
			if (class1 == Class.forName("java.lang.String")) {
				processdata.setValue(variableName, val);
			} else if ((wt.org.WTPrincipal.class).isAssignableFrom(class1)) {
				// val 에 계정이 들어간다.
				WTUser user = (WTUser) CommonUtil.getObject(val);
				processdata.setValue(variableName, val.equals("") ? null : ((Object) (OrganizationServicesMgr.getPrincipal(user.getName()))));
			} else if (class1 != Class.forName("wt.team.Team"))
				if (class1 == Class.forName("wt.team.TeamTemplate")) {
					if (val == null || val.equals(""))
						processdata.setValue(variableName, null);
					else
						processdata.setValue(variableName, (TeamTemplate) rf.getReference(val).getObject());
				} else if (Team.class.isAssignableFrom(class1)) {
					if (val == null || val.equals(""))
						processdata.setValue(variableName, null);
					else
						processdata.setValue(variableName, (WTObject) rf.getReference(val).getObject());
				} else if (EnumeratedType.class.isAssignableFrom(class1)) {
					Object obj = null;
					if (wt.project.ActorRole.class.isAssignableFrom(class1))
						obj = val != "" ? ((Object) (ActorRole.toActorRole(val))) : null;
					else if (wt.project.Role.class.isAssignableFrom(class1))
						obj = val != "" ? ((Object) (Role.toRole(val))) : null;
					else
						obj = val != "" ? ((Object) (EnumeratedType.toEnumeratedType(class1.getName(), val))) : null;
					processdata.setValue(variableName, obj);
				} else if (WTObject.class.isAssignableFrom(class1))
					if (class1 == Class.forName("java.lang.Integer") || class1 == Integer.TYPE)
						processdata.setValue(variableName, val != "" ? ((Object) (new Integer(val))) : null);
					else if (class1 == Class.forName("java.lang.Character") || class1 == Character.TYPE)
						processdata.setValue(variableName, val != "" ? ((Object) (new Character(val.charAt(0)))) : null);
					else if (class1 == Class.forName("java.lang.Short") || class1 == Short.TYPE)
						processdata.setValue(variableName, val != "" ? ((Object) (Short.valueOf(val))) : null);
					else if (class1 == Class.forName("java.lang.Long") || class1 == Long.TYPE)
						processdata.setValue(variableName, val != "" ? ((Object) (Long.valueOf(val))) : null);
					else if (class1 == Class.forName("java.lang.Float") || class1 == Float.TYPE)
						processdata.setValue(variableName, val != "" ? ((Object) (Float.valueOf(val))) : null);
					else if (class1 == Double.TYPE || class1 == Class.forName("java.lang.Double"))
						processdata.setValue(variableName, val != "" ? ((Object) (Double.valueOf(val))) : null);
					else if (class1 == Class.forName("java.util.Date"))
						processdata.setValue(variableName, val != "" ? ((Object) (new Timestamp((new DateHelper(val, "day", locale)).getDate().getTime()))) : null);
					else if (class1 == Class.forName("java.net.URL"))
						processdata.setValue(variableName, val != "" ? ((Object) (new URL(val))) : null);
					else if (class1 == Class.forName("java.lang.Boolean") || class1 == Boolean.TYPE)
						processdata.setValue(variableName, val.equals("true") ? ((Object) (new Boolean(true))) : ((Object) (new Boolean(false))));
					else
						processdata.setValue(variableName, val);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return processdata;
	}
	
	@Override
	public void completeWorkItem(WorkItem workItem, Vector vec) {
		try {
			WTPrincipalReference prinRef = WTPrincipalReference.newWTPrincipalReference(SessionHelper.manager.getPrincipal());
			WorkflowHelper.service.workComplete(workItem, prinRef, vec);
		} catch (WTException e) {
			e.printStackTrace();	
		}
	}
	
	@Override
	public Object getWfProcessVariableValue(WfProcess process, String varName)
    {
        wt.workflow.engine.WfVariable[] wfv = process.getContext().getVariableList();
        for (int i = 0; i < wfv.length; i++)
        {
            //    	    System.out.println(" wfv[i].getName() " + wfv[i].getName());
            if (wfv[i].getName().equals(varName))
                return wfv[i].getValue();
        }
        return null;
    }
}
