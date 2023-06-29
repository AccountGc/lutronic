package com.e3ps.groupware.workprocess.beans;

import java.io.Serializable;

import wt.enterprise.Managed;
import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.Versioned;

import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.OwnPersistable;
import com.e3ps.common.util.StringUtil;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.service.WFItemHelper;


public class WorkStateCheck  implements Serializable,RemoteAccess
{
	public static final String MASTER_WORKING="MASTER_WORKING";    //"작업중";
	public static final String INWORK="INWORK";     //"작업중";
	public static final String PLANNING="PLANNING";     //"계획진행";
	public static final String ACTIVITYEO="ACTIVITYEO";     //"EO활동중";
	public static final String APPROVECOMPELETE="APPROVECOMPELETE";     //"승인완료";
	public static final String APPROVEDEO="APPROVEDEO";     //"EO승인됨";
	public static final String APPROVED="APPROVED";     //"승인됨";
	public static final String APPROVEDREPORT="APPROVEDREPORT";     //"승인통보";
	public static final String APPROVEREQUEST="APPROVEREQUEST";     //"승인요청";
	public static final String APPROVING="APPROVING";     //"승인중";
	public static final String CONFIRM="CONFIRM";     //"변경결과확인중";
	public static final String CONFIRMED="CONFIRMED";     //"확인됨";
	public static final String CONFIRMING="CONFIRMING";     //"확인중";
	public static final String CREATINGEUL="CREATINGEUL";     //"을지등록중"; 
	public static final String DEATH="DEATH";     //"폐기";
	public static final String DESIGNREVIEW="DESIGNREVIEW";     //"설계검토중";
	public static final String DESIGNREVIEWCOMP="DESIGNREVIEWCOMP";     //"설계검토완료";
	public static final String DEVCANCEL="DEVCANCEL";     //"개발취소";
	public static final String DISTRIBUTED="DISTRIBUTED";     //"배포완료";
	public static final String DISTRIBUTING="DISTRIBUTING";     //"배포중";
	public static final String ECNAPPROVING="ECNAPPROVING";     //"변경통보서승인중";
	public static final String ECNDISTRIBUTE="ECNDISTRIBUTE";     //"통보서배포중";
	public static final String ECRAPPROVED="ECRAPPROVED";     //"ECR 승인됨";
	public static final String ECRAPPROVING="ECRAPPROVING";     //"ECR 승인중";
	public static final String ECRTECHREVIEW="ECRTECHREVIEW";     //"기술검토중";
	public static final String EPI="EPI";     //"EPI진행중";
	public static final String EULAPPROVING="EULAPPROVING";     //"을지승인중";
	public static final String IMPLEMENT="IMPLEMENT";     //"변경활동중";
	public static final String IMPLEMENTDISTRIBUTE="IMPLEMENTDISTRIBUTE";     //"배포및변경적용중";
	public static final String ISIRAPPROVED="ISIRAPPROVED";     //"ISIR승인됨";
	public static final String ISIRAPPROVING="ISIRAPPROVING";     //"ISIR승인중";
	public static final String ONBOARD="ONBOARD";     //"변경대책회의중";
	public static final String ONREQUEST="ONREQUEST";     //"변경요청중";
	public static final String OPENFORDISTRIBUTION="OPEN FORDISTRIBUTION";     //"배포대기중";
	public static final String PROJECTCREATE="PROJECTCREATE";     //"프로젝트생성";
	public static final String PROJECTING="PROJECTING";     //"프로젝트중"; 
	public static final String PROJECTINWORK="PROJECTINWORK";     //"프로젝트중";
	public static final String REPORTINREWORK="REPORTINREWORK";     //"계획서개정";
	public static final String REPORTINWORK="REPORTINWORK";     //"계획서등록중";
	public static final String REQUESTAPPROVING="REQUESTAPPROVING";     //"변경요청서승인중";
	public static final String REQUESTCANCELED="REQUESTCANCELED";     //"변경요청서취소됨";
	public static final String REQUESTCOMPLETE="REQUESTCOMPLETE";     //"변경요청서완료됨";
	public static final String REQUESTESTART="REQUESTESTART";     //"변경요청서재의뢰";
	public static final String REQUESTREVIEW="REQUESTREVIEW";     //"타당성검토중";
	public static final String REQUESTREVIEWAGAIN="REQUESTREVIEWAGAIN";     //"변경요청서재검토";
	public static final String RETURN="RETURN";     //"반려됨";
	public static final String STOP="STOP";     //"중단됨";
	public static final String WAITPROCESS="WAITPROCESS";     //"결재 대기중";

    static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
    public static WorkStateCheck manager = new WorkStateCheck();
    private static final long serialVersionUID = 1L;

    /**
     * 수정 가능 여부. 상태가 작업중인지 확인.
     * @param per
     * @return
     * @throws Exception
     */
    public boolean isAccessModify(Persistable per)throws Exception{

            String creatorName = getCreatorName(per);

            return isAccessModify(per, creatorName);
    }
    public boolean isAccessModify(Persistable per, String creatorName)throws Exception{

        if(!CommonUtil.isAdmin() ){

            String suser = SessionHelper.manager.getPrincipal().getName();

            if(creatorName==null){
                throw new WTException(Message.get("등록자를 찾을 수 없습니다."));
            }

            if(!suser.equals(creatorName)){
                return false;
            }
        }

        return isModify(per);
    }
    public boolean isModify(Persistable per)throws Exception{
    	WTObject wtobj = (WTObject) per;
    	WFItem wfItem = WFItemHelper.service.getWFItem(wtobj);
        if (wfItem == null) return true;
        String state = wfItem.getObjectState();
        return WorkStateCheck.INWORK.equals(state);
    }

    public String getCreatorName(Object pbo) throws Exception{
        String creatorName = "";
        
        if(pbo instanceof OwnPersistable){
            creatorName = ((OwnPersistable)pbo).getOwner().getName();
        }else if(pbo instanceof Versioned ){
            creatorName = ((Versioned)pbo).getCreator().getName();
        }else if(pbo instanceof Managed){
            creatorName = ((Managed)pbo).getCreator().getName();
        }
        return creatorName;
    }
    
    /**
     * @param per
     * @return
     * @throws Exception
     */
    public boolean isAccessRevise(Persistable per)throws Exception{

        if(!CommonUtil.isAdmin() ){

            String suser = SessionHelper.manager.getPrincipal().getName();

            String creatorName = getCreatorName(per);

            if(creatorName==null){
                throw new WTException(Message.get("등록자를 찾을 수 없습니다."));
            }

            if(!suser.equals(creatorName)){
                return false;
            }
        }

        WTObject wtobj = (WTObject) per;
    	WFItem wfItem = WFItemHelper.service.getWFItem(wtobj);
        if (wfItem == null) return true;
        String state = wfItem.getObjectState();

        return WorkStateCheck.APPROVED.equals(state);
    }
    
    /**
     * 상태가 승인됨 상태인지 확인
     * @param per
     * @return
     * @throws Exception
     */
    public boolean isApproved(Persistable per)throws Exception{

        WTObject wtobj = (WTObject) per;
    	WFItem wfItem = WFItemHelper.service.getWFItem(wtobj);
        if (wfItem == null) return false;
        String state = wfItem.getObjectState();
        	return WorkStateCheck.APPROVED.equals(state);
    }
}
