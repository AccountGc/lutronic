package com.e3ps.download.beans;

import java.sql.Timestamp;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.query.parser.ast.IsNullNode;
import wt.fc.ObjectNoLongerExistsException;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.download.DownloadHistory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadData {

	private String oid;
	private String id;
	private String userName ="";
	private String userID ="";
	private String moduleInfo ="" ;
	private int downCount =0;
	private String describe;
	private Timestamp downTime = null;
	
	public DownloadData() {
		
	}
	
	public DownloadData(DownloadHistory history){
		setOid(CommonUtil.getOIDString(history));
		setId(history.getPersistInfo().getObjectIdentifier().toString());
		setUserName(history.getUser().getFullName());
		setUserID(history.getUser().getName());
		setDescribe(StringUtil.checkReplaceStr(history.getDescribe(), "-"));
		setModuleInfo(getModuleInfo(history.getDOid()));
		setDownCount(history.getDCount());
		setDownTime(history.getPersistInfo().getModifyStamp());
	}
	
	private String getModuleInfo(String oid){
		ReferenceFactory rf = new ReferenceFactory();
		String targetInfo = "";
		try{
			Object obj  = (Object)rf.getReference(oid).getObject();
			
			if(obj instanceof WTPart){
				WTPart part = (WTPart)obj;
				targetInfo ="[품목] " + part.getNumber()+"("+part.getName()+")";
			}else if(obj instanceof EPMDocument){
				EPMDocument epm = (EPMDocument)obj;
				targetInfo ="[도면] " + epm.getNumber()+"("+epm.getName()+")";
			}else if(obj instanceof WTDocument){
				WTDocument doc = (WTDocument)obj;
				targetInfo ="[문서] " + doc.getNumber()+"("+doc.getName()+")";
			}else if(obj instanceof EChangeOrder){
				EChangeOrder eco = (EChangeOrder)obj;
				targetInfo ="[ECO] " + eco.getEoNumber()+"("+eco.getEoName()+")";
			}else if(obj instanceof EChangeRequest){
				EChangeRequest ecr = (EChangeRequest)obj;
				targetInfo ="[ECR] " + ecr.getEoNumber()+"("+ecr.getEoName()+")";
			}else{
				targetInfo = Message.get("다운로드 이력 정보를 추가 하여 주시기 바랍니다.");
			}
		}catch(Exception e){
			targetInfo = Message.get("다운로드 이력 정보가[삭제] 정상적이지 않습니다.");
		}
		
		return targetInfo;
	}
}
