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
import com.e3ps.download.DownloadHistory;

public class DownloadData {

	private  String userName ="";
	private  String userID ="";
	private  String moduleInfo ="" ;
	private  int downCount =0;
	private  String describe;
	private Timestamp downTime = null;
	
	public DownloadData(DownloadHistory history){
		WTUser user = history.getUser();
		this.userName = user.getFullName();
		this.userID = user.getName();
		this.describe = history.getDescribe();
		
		this.downCount = history.getDCount();
		this.downTime = history.getPersistInfo().getModifyStamp();
		this.moduleInfo = this.getModuleInfo( history.getDOid());
		
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
	
	public String getUserName(){
		return userName;
	}
	
	public String getUserID(){
		return userID;
	}
	
	public String getModuleInfo(){
		
		return moduleInfo;
	}
	
	public int getDownCount(){
		return downCount;
	}
	
	public Timestamp getDownTime(){
		return downTime;
	}

	public String getDescribe() {
		return describe;
	}
	
	
}
