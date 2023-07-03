package com.e3ps.change.beans;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.editor.BEContext;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.org.People;
import com.e3ps.org.beans.PeopleData;
import com.e3ps.org.service.UserHelper;

import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;
import wt.vc.wip.WorkInProgressHelper;

@Getter
@Setter
public class ECAData{
	private ImageIcon icon;
    private String oid;
    private String step;
    private String stepName;
    private String stepSort;
    private String name;
    private String activityType;
    private String activityName;
    private String activeUserOid;
    private String activeUserName;
    private String finishDate;
    private String state;
    private String stateName;
    private String completeDate;
    private String description;
    private String comments;
    private String departName;
    private String departmentName;
    private boolean isAutoCreate=false;
    private boolean isModify = false;
    private List<DocumentData> docList;
    
	public ECAData(final EChangeActivity eca) {
		setOid(CommonUtil.getOIDString(eca));
		setStep(eca.getStep());
		NumberCode stepCode = NumberCodeHelper.service.getNumberCode("EOSTEP", getStep());
		if (stepCode != null) {
			setStepName(stepCode.getName());
		}
		setName(eca.getName());
		setActivityType(eca.getActiveType());
		setActivityName(ChangeUtil.getActivityName(eca.getActiveType()));
		if(eca.getActiveUser() != null){
			setActiveUserOid(CommonUtil.getOIDString(eca.getActiveUser()));
			setActiveUserName(eca.getActiveUser().getFullName());
		}
		setFinishDate(DateUtil.getDateString(eca.getFinishDate(), "d"));
		setState(eca.getLifeCycleState().toString());
		setStateName(eca.getLifeCycleState().getDisplay());
		if(state.equals("COMPLETED")){
			setCompleteDate(DateUtil.getDateString(eca.getModifyTimestamp(),"d"));
    	}
		setDescription(WebUtil.getHtml(eca.getDescription()));
		setComments(WebUtil.getHtml(eca.getComments()));
		String eoNumber = eca.getEo().getEoNumber();
		setAutoCreate(getName().startsWith(eoNumber));
		setModify(!state.equals("COMPLETED"));
        if(eca.getActiveUser() != null){
            WTUser user = eca.getActiveUser();
            People pp = UserHelper.service.getPeople(user);
            if(pp != null && pp.getDepartment() != null){
                setDepartmentName(pp.getDepartment().getName());
            }
        }
		setStepSort(getStepSort());
		setIcon(getIcon());
		setDocList(getDocList());
		setDepartName(getDepartName());
	}
}	
