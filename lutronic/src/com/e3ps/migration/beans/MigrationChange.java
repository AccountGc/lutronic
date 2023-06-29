package com.e3ps.migration.beans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.drawing.beans.EpmUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.drawing.util.EpmPublishUtil;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;

import wt.epm.E3PSRENameObject;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.pom.DBProperties;
import wt.pom.Transaction;
import wt.pom.WTConnection;
import wt.series.MultilevelSeries;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.VersionInfo;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class MigrationChange implements wt.method.RemoteAccess, java.io.Serializable{
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
	
	public static MigrationChange manager = new MigrationChange();
	
	public  boolean isChange;
	public static String changeSFile = "changeSFile";
	public static String changeFFile = "changeFFile";
	public static String changeTotalFile = "changeTotalFile";
	public  String TopNumber ="";
	
	public static String versionSFile = "versionSFile";
	public static String versionNotFile = "versionNotFile";
	public static String versionTotalFile = "versionTotalFile";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String number = args[0];
		String change = args[1];
		String mode = args[2]; //ver,number;
		
		//windchill com.e3ps.migration.beans.MigrationChange number true ver
		MigrationChange.manager.isChange = change.equals("true") ? true : false;
		MigrationChange.manager.TopNumber = number;
		
		//System.out.println("TopNumber =" + MigrationChange.manager.TopNumber);
		if(mode.equals("number")){
			MigrationChange.manager.getEPMNumberChange(number,change);
			//3D, 2D를 부품기준의 번호와 이름으로 맞춤
		}else if(mode.equals("ver")){
			MigrationChange.manager.getEPMVesrionChange(number, change);
			//품목 기준으로 버전을 맞춤
		}else if(mode.equals("direct")){
			String part_oid = "wt.part.WTPart:"+args[3];
			String epm_oid = "wt.epm.EPMDocument:"+args[4];
			WTPart part = (WTPart)CommonUtil.getObject(part_oid);
			EPMDocument epm = (EPMDocument)CommonUtil.getObject(epm_oid);
			
			MigrationChange.manager.manualRevision(part, epm);
			//ver mode 가  정상 수행되지 않을 때, oid 입력. 3D 용도
		}else if(mode.equals("PDFVer")){
			String part_oid = "wt.part.WTPart:"+args[3];
			String epm_oid = "wt.epm.EPMDocument:"+args[4];
			WTPart part = (WTPart)CommonUtil.getObject(part_oid);
			EPMDocument epm = (EPMDocument)CommonUtil.getObject(epm_oid);
			
			MigrationChange.manager.changePDFVer(part, epm);
			//ver mode 가  정상 수행되지 않을 때, oid 입력. PDF 용도
		}
		
	}
	
	public void manualRevision(WTPart part, EPMDocument epm){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{WTPart.class,EPMDocument.class};
            Object args[] = new Object[]{part,epm};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "manualRevision",
                        null,
                        this,
                        argTypes,
                        args);
               return;
            } catch(RemoteException e) {
                e.printStackTrace();
               
            } catch(InvocationTargetException e) {
                e.printStackTrace();
               
            } catch(Exception e){
                
                
            }
        }
		Transaction trx = new Transaction();
		try{
			trx.start();
			
			revision(part, epm);
			
			trx.commit();
			trx = null;
	    } catch(Exception e) {
	 	   e.printStackTrace();
	     
	    } finally {
	        if(trx!=null){
					trx.rollback();
			  }
	    }
	}
	
	public void changePDFVer(WTPart part, EPMDocument epm){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{WTPart.class,EPMDocument.class};
            Object args[] = new Object[]{part,epm};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "changePDFVer",
                        null,
                        this,
                        argTypes,
                        args);
               return;
            } catch(RemoteException e) {
                e.printStackTrace();
               
            } catch(InvocationTargetException e) {
                e.printStackTrace();
               
            } catch(Exception e){
                
                
            }
        }
		Transaction trx = new Transaction();
		try{
			trx.start();
			
			changeVer(part, epm);
			
			trx.commit();
			trx = null;
	    } catch(Exception e) {
	 	   e.printStackTrace();
	     
	    } finally {
	        if(trx!=null){
					trx.rollback();
			  }
	    }
	}
	
	public  void changeVer(WTPart part,EPMDocument oldEpm){
		
		try{
			String partVer = part.getVersionIdentifier().getValue();
			String epmVer = oldEpm.getVersionIdentifier().getValue();	
			
			boolean isChangeVer = partVer.equals(epmVer)? false : true;
			String log = part.getNumber()+","+partVer+","+oldEpm.getNumber()+","+epmVer+",changeVer";
			
			if(isChangeVer){
				
				//System.out.println(log+", isChange="+ isChange+", isChangeVer "+isChangeVer);
				if(isChange){
					EPMDocument newEpm = (EPMDocument)revise(oldEpm, partVer, null);
					EPMDescribeLink newEdl = getDescribeLink(part);
					newEdl.setDescribedBy(newEpm);
					PersistenceServerHelper.manager.update(newEdl);
				}
				
				createLog(log, versionSFile);
			}else{
				//System.out.println(log+", isChange="+ isChange+", isChangeVer "+isChangeVer);
				createLog(log, versionNotFile);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public  EPMDescribeLink getDescribeLink(Object obj) throws WTException {
		QueryResult qr = null;
		if(obj instanceof WTPart){
			WTPart part =(WTPart)obj;
			qr = PersistenceHelper.manager.navigate(part, "describedBy", EPMDescribeLink.class, false);
		}/*else{
			EPMDocument epm =(EPMDocument)obj;
			qr = PersistenceHelper.manager.navigate(epm, "buildTarget", EPMBuildRule.class, false);
		}*/
      
        while (qr.hasMoreElements())
        {
        	EPMDescribeLink edl = (EPMDescribeLink) qr.nextElement();
            if (!WorkInProgressHelper.isWorkingCopy((Workable) edl.getDescribedBy()))
                return edl;
        }

        return null;
    }
	
	public void getEPMVesrionChange(String number,String change){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{String.class,String.class};
            Object args[] = new Object[]{number,change};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "getEPMVesrionChange",
                        null,
                        this,
                        argTypes,
                        args);
               return;
            } catch(RemoteException e) {
                e.printStackTrace();
               
            } catch(InvocationTargetException e) {
                e.printStackTrace();
               
            } catch(Exception e){
                
                
            }
        }
		Transaction trx = new Transaction();
		try{
			trx.start();
			WTPart topPart = PartHelper.service.getPart(number);
			EPMDocument topEPM = DrawingHelper.service.getEPMDocument(topPart);
			Vector<EPMDocument> vec = new Vector<EPMDocument>();  // 대상 리스트;
			
			
			
			//EPMDocument topEPM = DrawingHelper.service.getLastEPMDocument(number);
			
			vec = getEPMTree(topEPM, vec);
			
			StringBuffer sb = new StringBuffer();
			int totalcount =0;
			for(EPMDocument epm :vec){
				
				
				totalcount++;
				WTPart part = DrawingHelper.service.getWTPart(epm);
				if(epm == null){
					String errorlog = totalcount+",Revision EPM is null =" + epm;
					//System.out.println(errorlog);
					createLog(errorlog,"ERROR");
					continue;
				}
				
				
				if(part == null){
					String errorlog = totalcount+",Revision PART is null =" + epm.getNumber();
					//System.out.println(errorlog);
					createLog(errorlog,"ERROR");
					
					continue;
				}else{
					revision(part, epm);
				}
				
			}
			trx.commit();
			trx = null;
	    } catch(Exception e) {
	 	   e.printStackTrace();
	     
	    } finally {
	        if(trx!=null){
					trx.rollback();
			  }
	    }
	}
	
	public void getEPMNumberChange(String number,String change){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{String.class,String.class};
            Object args[] = new Object[]{number,change};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "getEPMNumberChange",
                        null,
                        this,
                        argTypes,
                        args);
               return;
            } catch(RemoteException e) {
                e.printStackTrace();
               
            } catch(InvocationTargetException e) {
                e.printStackTrace();
               
            } catch(Exception e){
                
                
            }
        }
		 //A .Vesrion 싱크(부품 , 3D , 2D)  - 부품 기준으로 Version 동일
		 //B.3D 도번 변경 - 부품 번호+확장자 로 변경
		 //C.2D 도번 변경 - 부품 번호+확장자,도명 - 부품명으로 변경
		 //PDF,DWG 업로드 

		try{
			WTPart topPart = PartHelper.service.getPart(number);
			EPMDocument topEPM = DrawingHelper.service.getEPMDocument(topPart);
			Vector<EPMDocument> vec = new Vector<EPMDocument>();  // 대상 리스트;
			
			//EPMDocument topEPM = DrawingHelper.service.getLastEPMDocument(number);
			
			vec = getEPMTree(topEPM, vec);
			int totalcount = 0;
			for(EPMDocument epm :vec){
				totalcount++;
				if(epm == null){
					String errorlog = totalcount+",NumberChange EPM is null =" + epm;
					//System.out.println(errorlog);
					createLog(errorlog,"ERROR");
					continue;
				}
				WTPart part = DrawingHelper.service.getWTPart(epm);
				
				if(part == null){
					String errorlog = totalcount+",NumberChange PART is null =" + epm.getNumber();
					//System.out.println(errorlog);
					createLog(errorlog,"ERROR");
					
					continue;
				}
				
				String totalLog = totalcount+","+part.getNumber()+","+epm.getNumber();
				createLog(totalLog, changeTotalFile);
				
				if(part != null){
					
					Vector<EPMReferenceLink> vec2D = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
					
					changeNumber(part, epm, false);
					
					
					
					HashMap map2D = new HashMap();
					for(int h = 0 ; h < vec2D.size() ; h++){
						EPMReferenceLink epmlink = vec2D.get(h);
						EPMDocument epm2d = epmlink.getReferencedBy();
						if(map2D.containsValue(epm2d.getNumber())){
							continue;
						}
						map2D.put(epm2d.getNumber(),epm2d.getNumber());
						if(epm2d.getDocType().toString().equals("CADDRAWING")){
							
							changeNumber(part, epm2d, true);
							//E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) epm2d, "APPROVED");
							
						}
						
					}
				}
				
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean changeNumber(WTPart part,EPMDocument epm,boolean is2D){
		
		boolean isResult = false;
		
		try{
			
			String partNumber = part.getNumber();
			
			String epmNumber = epm.getNumber();
			epmNumber = epmNumber.substring(0,epmNumber.lastIndexOf("."));
			
			String log=partNumber +","+epm.getNumber() +"," + epmNumber + ","+part.getName() +","+epm.getName() +","+is2D;
			if(partNumber.equals(epmNumber)){
				
				createLog(log, changeFFile);
				return true;
			}
			
			String newNumbeer = partNumber +"."+ EpmUtil.getExtension(epm.getCADName());
			String partName = "";
			log = partNumber +","+epm.getNumber() +"," + newNumbeer + ","+part.getName() +","+epm.getName() +","+is2D;
			if(is2D){
				partName = part.getName();
			}
			createLog(log, changeSFile);
			if(!isChange) return false;
			E3PSRENameObject.manager.EPMReName(epm, newNumbeer, partName, "", false);
			
			E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) epm, "APPROVED");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isResult;
	}
	
	public Vector<EPMDocument> getEPMTree(EPMDocument epm , Vector<EPMDocument> vec){
		
		
		try{
			if(!vec.contains(epm)){
				vec.add(epm);
			}
			
			LatestConfigSpec latest = new LatestConfigSpec();
			QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(epm, null,true ,latest); //최신
			while(qr.hasMoreElements()){
				
				EPMDocument subEPM =  (EPMDocument)qr.nextElement();
				if(!vec.contains(subEPM)){
					vec.add(subEPM);
				}
				
				vec=getEPMTree(subEPM,vec);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vec;
	}
	
	public  void revision(WTPart part,EPMDocument oldEpm){
		
		try{
			EPMBuildRule ebr = PartSearchHelper.service.getBuildRule(part);
			String ver = part.getVersionIdentifier().getValue();
					
			String partVer = part.getVersionIdentifier().getValue();
			String epmVer = oldEpm.getVersionIdentifier().getValue();	
			boolean isChangeVer = partVer.equals(epmVer)? false : true;
			EPMDocument newEpm = null;
			if (ebr != null) {
				String log = part.getNumber()+","+partVer+","+oldEpm.getNumber()+","+epmVer+",3D";
				
				if(isChangeVer){
					
					//System.out.println(log+", isChange="+ isChange+", isChangeVer "+isChangeVer);
					if(isChange){
						newEpm = (EPMDocument) revise(oldEpm, ver, "APPROVED");//ObjectUtil.reviseNote(oldEpm,reviseNote);
						EPMBuildRule newEbr = PartSearchHelper.service.getBuildRule(part);
						newEbr.setBuildSource(newEpm);
						PersistenceServerHelper.manager.update(newEbr);

						EPMBuildHistory ebh = getBuildHistory(part, oldEpm);
						if (ebh != null) {
							ebh.setBuiltBy(newEpm);
							PersistenceServerHelper.manager.update(ebh);
						}
						
						IBAUtil.changeIBAValue(newEpm, AttributeKey.EPMKey.IBA_VERSION, partVer , "string");
						IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_VERSION, partVer , "string");
						
						EpmPublishUtil.publish(newEpm);
						
					}
					
					createLog(log, versionSFile);
				}else{
					//System.out.println(log+", isChange="+ isChange+", isChangeVer "+isChangeVer);
					
					if(isChange){
						IBAUtil.changeIBAValue(oldEpm, AttributeKey.EPMKey.IBA_VERSION, partVer , "string");
						IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_VERSION, partVer , "string");
					}
					
					createLog(log, versionNotFile);
					
				}
				if(newEpm  != null){
					
					Vector<EPMReferenceLink> vec2D = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)newEpm.getMaster());
					for(int h = 0 ; h < vec2D.size() ; h++){
						EPMReferenceLink epmlink = vec2D.get(h);
						EPMDocument epm2d = epmlink.getReferencedBy();
						
						if(epm2d.getDocType().toString().equals("CADDRAWING")){
							String epm2dVer = epm2d.getVersionIdentifier().getValue();
							isChangeVer = partVer.equals(epm2dVer)? false : true;
							log = part.getNumber()+","+partVer+","+epm2d.getNumber()+","+epm2dVer  +",2D";
							//System.out.println(log+", isChange="+ isChange+", isChangeVer "+isChangeVer);
							if(isChangeVer){
								if(isChange){
									EPMDocument newEpm2d = (EPMDocument)revise(epm2d, ver, "APPROVED");
									EpmPublishUtil.publish(newEpm2d);
								}
								
								createLog(log, versionSFile);
							}else{
								createLog(log, versionNotFile);
							}
							
						}
						
					}
					
				}else{
					if(oldEpm !=null){
						Vector<EPMReferenceLink> vec2D = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)oldEpm.getMaster());
						for(int h = 0 ; h < vec2D.size() ; h++){
							EPMReferenceLink epmlink = vec2D.get(h);
							EPMDocument epm2d = epmlink.getReferencedBy();
							if(epm2d.getDocType().toString().equals("CADDRAWING")){
								String epm2dVer = epm2d.getVersionIdentifier().getValue();
								isChangeVer = partVer.equals(epm2dVer)? false : true;
								log = part.getNumber()+","+partVer+","+epm2d.getNumber()+","+epm2dVer  +",2D";
								//System.out.println(log+", isChange="+ isChange+", isChangeVer "+isChangeVer);
								if(isChangeVer){
									if(isChange){
										EPMDocument newEpm2d = (EPMDocument)revise(epm2d, ver, "APPROVED");
									}
									
									createLog(log, versionSFile);
								}else{
									createLog(log, versionNotFile);
								}
							}
							
						}
					}
					
					
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static EPMBuildHistory getBuildHistory(WTPart wtpart, EPMDocument epmdocument) throws WTException {
		QueryResult qr = PersistenceHelper.manager.find(EPMBuildHistory.class, wtpart, "built", epmdocument);
		return qr.hasMoreElements() ? (EPMBuildHistory) qr.nextElement() : null;
	}
	
	public  Versioned revise(Versioned obj, String ver, String state) throws VersionControlException,
    WTPropertyVetoException, WTException
	{
		////MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", partVersion);
		//part.setVersionInfo( VersionInfo.newVersionInfo(multilevelseries));
		
		
		
		String lifecycle = ((LifeCycleManaged) obj).getLifeCycleName();
		Folder folder = FolderHelper.service.getFolder((FolderEntry) obj);
		MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.series.HarvardSeries.intellian", ver);
		VersionIdentifier vi = VersionIdentifier.newVersionIdentifier(multilevelseries);
		
		obj = VersionControlHelper.service.newVersion(obj, vi, VersionControlHelper.firstIterationId(obj));
		FolderHelper.assignLocation((FolderEntry) obj, folder);
		LifeCycleHelper.setLifeCycle((LifeCycleManaged) obj, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle));
		obj = (Versioned) PersistenceHelper.manager.save((Persistable) obj);
		
		if (state != null)
		{
		    LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) obj, State.toState(state), false);
		}
		return obj;
	}
	
	public void  changeVesionLog(WTPart part, EPMDocument epm,boolean is2D) throws Exception{
		
		String partVer = part.getVersionIdentifier().getValue();
		String epmVer = epm.getVersionIdentifier().getValue();
		EPMDocumentMaster master = (EPMDocumentMaster)epm.getMaster();
		//long masterLong = CommonUtil.getOIDLongValue(master);
		String log = part.getNumber()+","+partVer+","+epm.getNumber()+","+epmVer;
		if(partVer.equals(epmVer)){
			
			createLog(log, versionNotFile);
			return ;
		}
		
		if(!is2D){
			IBAUtil.changeIBAValue(epm, AttributeKey.EPMKey.IBA_VERSION, partVer , "string");
			IBAUtil.changeIBAValue(part, AttributeKey.EPMKey.IBA_VERSION, partVer , "string");
		}
		log = log+","+is2D;
		createLog(log, versionSFile);
		
		///sb.append("Update EPMDocument set versionida2versioninfo='"+partVer+"' where ida3masterreference =" +masterLong+";\n");
		
		//update epmdocument set versionida2versioninfo ='A' where ida3masterreference ='996618';
		
	}
	
	
	
	public void changeVesion2(StringBuffer sql) throws SQLException{
		
		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();
			
			st = con.prepareStatement(sql.toString());
			st.execute();
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
            if ( rs != null ) {
                rs.close();
            }
            if ( st != null ) {
                st.close();
            }
			if (DBProperties.FREE_CONNECTION_IMMEDIATE
					&& !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}
	
	/*
	public  Vector<EPMDocument> getEPMDocument(){
		
		Vector<EPMDocument> vec = new Vector<EPMDocument>();
		try{
			String cadDivision ="SOLIDWORKS";
			String cadType = "";// ";
			QuerySpec query = new QuerySpec();
			
			int idx = query.addClassList(EPMDocument.class, true);
			
			//최신 iteration
			if(query.getConditionCount() > 0) { query.appendAnd(); }
			query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[]{idx});
			
			//최신 버전
			SearchUtil.addLastVersionCondition(query, EPMDocument.class, idx);
			
			//CAD 구분
			query.appendAnd();
			query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , "SOLIDWORKS", false), new int[]{idx});
			
			//CAD 타입
			query.appendAnd();
			query.appendOpenParen();
			query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , "CADCOMPONENT", false), new int[]{idx});
			query.appendOr();
			query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , "CADASSEMBLY", false), new int[]{idx});
			query.appendCloseParen();
			
			QueryResult rt =PersistenceHelper.manager.find(query);
			
			while(rt.hasMoreElements()){
				EPMDocument epm = (EPMDocument)rt.nextElement();
				
				vec.add(epm);
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vec;
	}
	*/
	public void createLog(String log,String fileName) {
		//System.out.println("========== "+fileName+" ===========");
		String filePath = "D:\\e3ps\\";
		
		File folder = new File(filePath);
		
		if(!folder.isDirectory()){
			
			folder.mkdirs();
		}
		fileName = fileName.replace(",", "||");
		fileName  = TopNumber+"_"+fileName;
		//System.out.println("fileName= " + fileName +",isChange =" + isChange);
		String toDay = com.e3ps.common.util.DateUtil.getCurrentDateString("date");
		toDay = com.e3ps.common.util.StringUtil.changeString(toDay, "/", "-");
		String logFileName = fileName+"_" + toDay.concat(".log");
		String logFilePath = filePath.concat(File.separator).concat(logFileName);
		File file = new File(logFilePath);
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PrintWriter out = new PrintWriter(new BufferedWriter(fw), true);
		out.write(log);
		//System.out.println(log);
		out.write("\n");
		out.close();
	}

}
