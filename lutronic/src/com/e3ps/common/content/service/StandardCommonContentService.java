package com.e3ps.common.content.service;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.content.CacheUploadUtil;
import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.beans.EpmUtil;
import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.content.StreamData;
import wt.content.Streamed;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.LobLocator;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fv.FileFolder;
import wt.fv.FvHelper;
import wt.fv.FvMount;
import wt.fv.FvTransaction;
import wt.fv.StandardFvService;
import wt.fv.StoreStreamListener;
import wt.fv.StoredItem;
import wt.fv.Vault;
import wt.fv.uploadtocache.BackupedFile;
import wt.fv.uploadtocache.CacheDescriptor;
import wt.fv.uploadtocache.CachedContentDescriptor;
import wt.objectstorage.ContentFileWriter;
import wt.objectstorage.ContentManagerFactory;
import wt.objectstorage.ContentStorageManager;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.representation.Representation;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.EncodingConverter;
import wt.util.WTException;
import wt.util.WTProperties;

public class StandardCommonContentService extends StandardManager implements CommonContentService {

	public static StandardCommonContentService newStandardCommonContentService() throws Exception {
		final StandardCommonContentService instance = new StandardCommonContentService();
		instance.initialize();
		return instance;
	}

	@Override
	public ContentHolder attachPrimary(ContentHolder holder, String loc) throws Exception {
		return attach(holder, loc, null, ContentRoleType.PRIMARY);
	}

	@Override
	public ContentHolder attach(ContentHolder holder, String loc) throws Exception {
		return attach(holder, loc, null, ContentRoleType.SECONDARY);
	}

	@Override
	public ContentHolder attach(ContentHolder holder, String loc, String desc) throws Exception {
		return attach(holder, loc, desc, ContentRoleType.SECONDARY);
	}
	
//	public ContentHolder attach(ContentHolder holder, String loc, String desc, ContentRoleType contentRoleType) throws Exception {
//		
//        String cacheId = loc.split("/")[0];
//        String fileName = loc.split("/")[1];
//
//        CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);
//
//        holder = CommonContentHelper.service.attach(holder, cacheDs, fileName, desc, contentRoleType);
//		
//		return holder;
//	}

	@Override
	public ContentHolder attach(ContentHolder holder, String loc, String desc, ContentRoleType contentRoleType) throws Exception {
		Transaction trx = new Transaction();
		try {
			trx.start();
			
			File file = CommonContentHelper.manager.getFileFromCacheId(loc);
			String downloadFile = "";
			String newfile = "";
			ApplicationData applicationdata = ApplicationData.newApplicationData(holder);
			applicationdata.setFileName(file.getName());
			applicationdata.setUploadedFromPath(file.getPath());
			applicationdata.setRole(contentRoleType);
			applicationdata.setCreatedBy(SessionHelper.manager.getPrincipalReference());
			
			if (desc != null) {
				applicationdata.setDescription(desc);
			}
			
			System.out.println("===================>" + loc);
			System.out.println("===================>" + holder);
			System.out.println("===================>" + applicationdata);
			System.out.println("===================>" + file);
			System.out.println("===================>" + file.getPath());
			
			ContentServerHelper.service.updateContent(holder, applicationdata, file.getPath());
			
			if (holder instanceof FormatContentHolder) {
				holder = ContentServerHelper.service.updateHolderFormat((FormatContentHolder) holder);
			}
			EpmUtil.deleteFile(file.getPath());
			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return holder;
	}

	@Override
	public ContentHolder attach(ContentHolder holder, ApplicationData data, boolean isPrimary) throws Exception {
		if (isPrimary)
			return attach(holder, data, ContentRoleType.PRIMARY);
		else
			return attach(holder, data, ContentRoleType.SECONDARY);
	}
	
	@Override
	public ContentHolder attach(ContentHolder holder, ApplicationData data, ContentRoleType contentRoleType) throws Exception {

		Transaction transaction = new Transaction();
		try {
			transaction.start();

			LobLocator loblocator = null;
			data = (ApplicationData) PersistenceHelper.manager.refresh(data);
			Streamed streamed = (Streamed) PersistenceHelper.manager.refresh(data.getStreamData().getObjectId());
			try {
				loblocator.setObjectIdentifier(((ObjectReference) streamed).getObjectId());
				((StreamData) streamed).setLobLoc(loblocator);
			} catch (Exception exception) {
			}
			InputStream is = streamed.retrieveStream();

			ApplicationData saveData = ApplicationData.newApplicationData(holder);
			saveData.setIntendedForHttpOp(true);
			saveData.setFileName(data.getFileName());
			saveData.setFileSize(data.getFileSize());
			saveData.setCreatedBy(data.getCreatedBy());
			saveData.setDescription(data.getDescription() == null ? "" : data.getDescription());
			saveData.setRole(contentRoleType);

			ContentServerHelper.service.updateContent(holder, saveData, is);

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			transaction = null;
		}
		return holder;
		// ##end attach%40B6D04600B0.body
	}
	
	
	@Override
	public ContentHolder delete(ContentHolder holder, ContentItem deleteItem) throws Exception {

		Transaction trx = new Transaction();
		try {
			trx.start();

			if (holder == null || deleteItem == null)
				return holder;
			holder = ContentHelper.service.getContents(holder);
			ContentServerHelper.service.deleteContent(holder, deleteItem);

			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return holder;
	}

	@Override
	public ContentHolder delete(ContentHolder holder) throws Exception {

		Transaction trx = new Transaction();
		try {

			trx.start();

			holder = ContentHelper.service.getContents(holder);
			Vector files = ContentHelper.getApplicationData(holder);
			if (files != null) {
				for (int i = 0; i < files.size(); i++) {
					holder = delete(holder, (ApplicationData) files.get(i));
				}
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return holder;
	}

	
	/**
	 * <pre>
	 * @description 업로드 컴포넌트로 파일 추가
	 * @author dhkim
	 * @date 2016. 3. 10. 오후 4:05:06
	 * @method attach
	 * @param holder
	 * @param casheDes
	 * @param fileName
	 * @param desc
	 * @param contentRoleType
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	@Override
	public ContentHolder attach(ContentHolder holder, CachedContentDescriptor casheDes, String fileName, String desc, ContentRoleType contentRoleType) throws Exception {
		
		Transaction trx=new Transaction();
		try {
		      trx.start();
		      ApplicationData applicationdata = ApplicationData.newApplicationData(holder);
		      String uploadPath = casheDes.getContentIdentity();
		      applicationdata.setRole(contentRoleType);
		      applicationdata.setFileName(fileName);
		      applicationdata.setContentIdentity(uploadPath);
		      applicationdata.setUploadedFromPath(uploadPath);
		
		      applicationdata.setCreatedBy(SessionHelper.manager.getPrincipalReference());
		
		      if(desc!=null ){
		       applicationdata.setDescription(desc);
		      }
		
		      ContentServerHelper.service.updateContent(holder, applicationdata, casheDes);
		
		      if (holder instanceof FormatContentHolder){
		       holder = ContentServerHelper.service.updateHolderFormat((FormatContentHolder) holder);
		      }
		
		      trx.commit();
		      trx = null;
		  } catch(PropertyVetoException e) {
		     throw new WTException(e);
		  } finally {
		      if(trx!=null){
		        trx.rollback();
		        trx = null;
		     }
		  }
		  return holder;
	}
	
	@Override
	public ContentHolder attach(ContentHolder holder, String primary, String[] secondary) throws Exception  {
		return attach(holder, primary, secondary, null);
	}
	
	@Override
	public ContentHolder attach(ContentHolder holder, String primary, String[] secondary, String[] orgSecondary) throws Exception {
		return attach(holder, primary, secondary, orgSecondary, true);
	}
	
	@Override
	public ContentHolder attach(ContentHolder holder, String primary, String[] secondary, String[] delSecondary, boolean isWorkingCopy) throws Exception {
		return attach(holder, primary, secondary, delSecondary, null, isWorkingCopy);
	}
	
	@Override
	public ContentHolder attach(ContentHolder holder, String primary, String[] secondary, String[] delSecondary, String description, boolean isWorkingCopy) throws Exception {
		//System.out.println("StringUtil.checkString(primary)="+(StringUtil.checkString(primary)));
		//20161109 PJT EDIT START
		boolean isParse = true;
		try{
			if(StringUtil.checkString(primary)){
				String tmp = primary.split("/")[0];
		 	 	EncodingConverter localEncodingConverter = new EncodingConverter();
		 	 	String str = localEncodingConverter.decode(tmp);
			 	String[] arrayOfString = str.split(":");
			 	Long.parseLong(arrayOfString[0]);
			}
		}catch(NumberFormatException e){
			isParse = false;
		}
		
		if(StringUtil.checkString(primary) && isParse) {
			//20161109 PJT EDIT END &&isParse ADD
            ContentItem item = null;
            QueryResult result = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
            if (result.hasMoreElements ()) {
                item = (ContentItem) result.nextElement ();
                CommonContentHelper.service.delete(holder, item);
            }
            
            String cacheId = primary.split("/")[0];
	        String fileName = primary.split("/")[1];
	        //System.out.println("cacheId="+cacheId);
	        CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);
	        CommonContentHelper.service.attach(holder, cacheDs, fileName, description, ContentRoleType.PRIMARY);
	        
        }
		//System.out.println("isWorkingCopy="+(isWorkingCopy));
		if(isWorkingCopy) {
			
	        CommonContentHelper.service.delete(holder);
	        
	        if(delSecondary != null){
	        	for(String appOid : delSecondary) {
	        		ApplicationData app = (ApplicationData)CommonUtil.getObject(appOid);
	        		
	        		fileDown(appOid);
	        		
	        		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
	    	        String userName = user.getName();
	    	        
	    	        Config conf = ConfigImpl.getInstance();
	    	        String masterHost =  conf.getString("HTTP.HOST.URL")+ "/Windchill/";
	    	        
	    	        CacheDescriptor cd = CacheUploadUtil.getCacheDescriptor(1, true, userName, masterHost);
	        		
	        		ResourceBundle bundle =ResourceBundle.getBundle("wt");
	
	        		String reqHost = bundle.getString("wt.rmi.server.hostname");
	        	   	boolean isMain = masterHost.indexOf(reqHost) > 0;
	        		
	        	   	String savePath = WTProperties.getServerProperties().getProperty("wt.temp");
	
	        		String m_fileFullPath = savePath + "/" + app.getFileName();
	
	        		File file = new File(m_fileFullPath);
	        	   	
	        		CachedContentDescriptor descriptor =  CacheUploadUtil.doUploadToCache(cd, file, isMain);
	        		
	        		CachedContentDescriptor cacheDs = new CachedContentDescriptor(descriptor.getEncodedCCD());
	        		
	        		String desc = app.getDescription();
	        		
	        		attach(holder, cacheDs, app.getFileName(), desc, app.getRole());
					
	        	}
	        	
	        }
		} else {
			ContentHolder holder2 = ContentHelper.service.getContents(holder);
			Vector ofiles = ContentHelper.getApplicationData(holder2);
			if (ofiles != null) {
				for (int i = 0; i < ofiles.size(); i++) {
					ApplicationData oad = (ApplicationData) ofiles.get(i);
					//System.out.println("-----------------------    대상 파일 >>>>>>>>>>>>>>>>>  " + oad.getFileName());
					boolean flag = false;
					//System.out.println("null!=delSecondary =?" +(null!=delSecondary)+"\tdelSecondary.length="+delSecondary.length);
					if (null!= delSecondary) {
						for (int j = 0; j < delSecondary.length; j++) {
							String noid = delSecondary[j];
							if (noid.equals(oad.getPersistInfo().getObjectIdentifier().toString())) {
								//System.out.println("대상 파일 삭제 >>>  " + oad.getFileName()+"X");
								flag = true;
      							break;
      						}
      					}
  		    		}
					//System.out.println("-----------------------    대상 파일 flag >>>>>>>>>>>>>>>>>  " + flag);
  		    		if(!flag){
  		    			//System.out.println("-----------------------    대상 파일 oad.getRole().toString().equals() >>>>>>>>>>>>>>>>>  " + (oad.getRole().toString().equals("SECONDARY")));
  		    			//System.out.println("-----------------------    description == null && oad.getDescription() == null >>>>>>>>>>>>>>>>>  " + (description == null && oad.getDescription() == null));
  		    			//System.out.println("-----------------------    description != null && description.equals(oad.getDescription())l >>>>>>>>>>>>>>>>>  " + (description != null && description.equals(oad.getDescription())));
  		    			//System.out.println("-----------------------    description !oad.getRole().toString()="+oad.getRole().toString());
  		    			if(oad.getRole().toString().equals("SECONDARY")){
  		    				if(description == null && oad.getDescription() == null) {
  		    					System.out.println("#####################  삭제 목록 >>>>>  " + oad.getFileName());
  		    					holder = CommonContentHelper.service.delete(holder,oad);
  		    				}else if(description != null && description.equals(oad.getDescription())){
  		    					holder = CommonContentHelper.service.delete(holder,oad);
  		    					System.out.println("#####################  삭제 목록 >>>>>  " + oad.getFileName());
  		    				}
  		    			}
  		    			
  		    		}
				}
			}
		}
		//System.out.println("-----------------------    대상 파일 secondary >>>>>>>>>>>>>>>>>  " + (secondary != null));
		if(secondary != null){
			//System.out.println("ssss\t"+(secondary.length));
			for(int i=0; i< secondary.length; i++){
				//System.out.println("ssss\t"+(secondary[i]));
				String cacheId = secondary[i].split("/")[0];
		        String fileName = secondary[i].split("/")[1];
		        
		        CachedContentDescriptor cacheDs = new CachedContentDescriptor(cacheId);
		        CommonContentHelper.service.attach(holder, cacheDs, fileName, description, ContentRoleType.SECONDARY);
			}
		}
		
		return holder;
	}
	
	@Override
	public ApplicationData attachADDRole(ContentHolder holder, String roleType,String cachFile, boolean isWorkingCopy) throws Exception {
		ApplicationData app = null;
		ContentItem item = null;
		ContentRoleType contentroleType = ContentRoleType.toContentRoleType(roleType);
		
		
		if(StringUtil.checkString(roleType) && StringUtil.checkString(cachFile)) {
			 
			 
			 QueryResult result = ContentHelper.service.getContentsByRole (holder ,contentroleType);
			 String roleContent = cachFile;
			 String cacheId = roleContent.split("/")[0];
		     String fileName = roleContent.split("/")[1];
		     EncodingConverter localEncodingConverter = new EncodingConverter();
			String str = localEncodingConverter.decode(cacheId);
		     String[] arrayOfString = str.split(":");
			boolean isParse = true;	
		     try{
					long streamId = Long.parseLong(arrayOfString[0]);
					long fileSize = Long.parseLong(arrayOfString[1]);
					long folderId = Long.parseLong(arrayOfString[2]);
				}catch(NumberFormatException nfe){
					isParse = false;
				}
		     //System.out.println("isParse="+isParse);
			 if (result.hasMoreElements ()) {
	              item = (ContentItem) result.nextElement ();
	              ApplicationData appData = (ApplicationData)item;
				  String apFileName = appData.getFileName();	
	              //System.out.println("fileName="+fileName+"\tapFileName="+apFileName);
	             if(!"ECO".equals(fileName) && !"ECR".equals(fileName) )
	               CommonContentHelper.service.delete(holder, item);
	         }
		     CachedContentDescriptor cacheDs = null;
		     if(isParse){
		    	 cacheDs = new CachedContentDescriptor(cacheId);
		    	 app =  CommonContentHelper.service.attachROHS(holder, cacheDs, fileName, null, ContentRoleType.toContentRoleType(roleType));
		     }
		}else{
			QueryResult result = ContentHelper.service.getContentsByRole (holder ,contentroleType);
			 
			 if (result.hasMoreElements ()) {
	              item = (ContentItem) result.nextElement ();
	              CommonContentHelper.service.delete(holder, item);
	         }
		}
		
		
		return app;
	}
	
	@Override
	public ApplicationData attachROHS(ContentHolder holder, CachedContentDescriptor casheDes, String fileName, String desc, ContentRoleType contentRoleType) throws Exception {
		ApplicationData applicationdata = null;
		Transaction trx=new Transaction();
		try {
		      trx.start();
		      applicationdata = ApplicationData.newApplicationData(holder);
		      String uploadPath = casheDes.getContentIdentity();
		      applicationdata.setRole(contentRoleType);
		      applicationdata.setFileName(fileName);
		      applicationdata.setContentIdentity(uploadPath);
		      applicationdata.setUploadedFromPath(uploadPath);
		
		      applicationdata.setCreatedBy(SessionHelper.manager.getPrincipalReference());
		
		      if(desc!=null ){
		       applicationdata.setDescription(desc);
		      }
		
		      ContentServerHelper.service.updateContent(holder, applicationdata, casheDes);
		
		      if (holder instanceof FormatContentHolder){
		       holder = ContentServerHelper.service.updateHolderFormat((FormatContentHolder) holder);
		      }
		
		      trx.commit();
		      trx = null;
		  } catch(PropertyVetoException e) {
		     throw new WTException(e);
		  } finally {
		      if(trx!=null){
		        trx.rollback();
		        trx = null;
		     }
		  }
		  return applicationdata;
	}
	
	@Override
	public String copyApplicationData(String appOid) throws Exception {
		ApplicationData app = (ApplicationData)CommonUtil.getObject(appOid);
		
		fileDown(appOid);
		
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
        String userName = user.getName();
        
        Config conf = ConfigImpl.getInstance();
        String masterHost =  conf.getString("HTTP.HOST.URL")+ "/Windchill/";
        
        CacheDescriptor cd = CacheUploadUtil.getCacheDescriptor(1, true, userName, masterHost);
		
		ResourceBundle bundle =ResourceBundle.getBundle("wt");

		String reqHost = bundle.getString("wt.rmi.server.hostname");
	   	boolean isMain = masterHost.indexOf(reqHost) > 0;
		
	   	String savePath = WTProperties.getServerProperties().getProperty("wt.temp");

		String m_fileFullPath = savePath + "/" + app.getFileName();

		File file = new File(m_fileFullPath);
	   	
		CachedContentDescriptor descriptor =  CacheUploadUtil.doUploadToCache(cd, file, isMain);
		
		CachedContentDescriptor cacheDs = new CachedContentDescriptor(descriptor.getEncodedCCD());
		
		String cId = descriptor.getEncodedCCD();
		String name = app.getFileName();
		
		return cId + "/" + name;
		
		//attach(holder, cacheDs, app.getFileName(), null, app.getRole());
	}
	
	@Override
	public void fileDown(String appOid)throws Exception{
		FileOutputStream fos = null;
		InputStream is = null;
		try{
			String savePath = WTProperties.getServerProperties().getProperty("wt.temp");
			ApplicationData appData = (ApplicationData)CommonUtil.getObject(appOid);
			
			byte[] buffer = new byte[1024];
			
			File tempfile = new File(savePath + File.separator + appData.getFileName());
			fos = new FileOutputStream(tempfile);
		
			is = ContentServerHelper.service.findLocalContentStream(appData);
			
			int j = 0;
	        while ((j = is.read(buffer, 0, 1024)) > 0){
	        	
	        	fos.write(buffer, 0, j);
	        }
	            
	        fos.flush();
	        fos.close();
	        is.close();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(fos != null){
				fos.close();
			}
			
			if(is != null){
				is.close();
			}
		}
		
		
	}
	
	/**
	 * Holder 의  첨부 파일 리스트
	 * @param holder
	 * @param contentRoleType
	 * @param list
	 * @return
	 */
	@Override
	public List<ApplicationData> getAttachFileList(ContentHolder holder,String contentRoleType,List<ApplicationData> list) throws Exception{
		QueryResult contentFiles =null;
		
		if(ContentRoleType.PRIMARY.toString().equals(contentRoleType)){
			contentFiles = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
		}else if(ContentRoleType.SECONDARY.toString().equals(contentRoleType)){
			contentFiles = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.SECONDARY );
		}
		//System.out.println("contentFiles =" + contentFiles);
		while( contentFiles!= null && contentFiles.hasMoreElements()) {
			ContentItem item = (ContentItem) contentFiles.nextElement ();
			
			if (item instanceof ApplicationData) {
				
				ApplicationData ap = (ApplicationData) item;
				//System.out.println("ApplicationData =" + ap.getFileName());
				list.add(ap);
			}
		}
		
		return list;
	}
	/**
	 * Holder 의  첨부 파일 리스트
	 * @param holder
	 * @param contentRoleType
	 * @param list
	 * @return
	 */
	@Override
	public List<BatchDownData> getAttachFileList(ContentHolder holder,List<BatchDownData> list,String describe) throws Exception{
		List<ApplicationData> applist = new ArrayList<ApplicationData>();
		if( holder instanceof WTDocument){
			
			applist = getAttachFileList(holder, ContentRoleType.PRIMARY.toString(), applist);
			applist = getAttachFileList(holder, ContentRoleType.SECONDARY.toString(), applist);
		
		}else if( holder instanceof WTPart){
			
			applist = getAttachFileList(holder, ContentRoleType.PRIMARY.toString(), applist);
			applist = getAttachFileList(holder, ContentRoleType.SECONDARY.toString(), applist);
		
		}else if( holder instanceof EPMDocument){
			EPMDocument epm = (EPMDocument)holder;
			if("PROE".equals(epm.getAuthoringApplication().toString())){
				
				Representation representation = PublishUtils.getRepresentation(epm); 
				if(representation != null){
					representation = (Representation) ContentHelper.service.getContents(representation);
			        Vector contentList = ContentHelper.getContentList(representation);
			        for (int l = 0; l < contentList.size(); l++) {
			            ContentItem contentitem = (ContentItem) contentList.elementAt(l);
			            if( contentitem instanceof ApplicationData){
			            	ApplicationData drawAppData = (ApplicationData) contentitem;
			            	if(EpmUtil.isDwrainPublishFile(drawAppData)){
			            		//System.out.println("ApplicationData =" + drawAppData.getFileName());
			            		applist.add(drawAppData);
			            	}
			            }
			        }
				}
				
			}else{
				applist = getAttachFileList(holder, ContentRoleType.PRIMARY.toString(), applist);
			}
			
		}else if(holder instanceof EChangeActivity){
			
			applist = getAttachFileList(holder, ContentRoleType.SECONDARY.toString(), applist);
		
		}else if(holder instanceof EChangeOrder){
			
			applist = getAttachFileList(holder, ContentRoleType.SECONDARY.toString(), applist);
		}
		
		
		String oid = CommonUtil.getOIDString(holder);
		String objectType ="";
		//System.out.println(holder +":"+applist.size());
		if(applist.size()==0){
			BatchDownData data = new BatchDownData();
			
			data = setBatchDownData(holder, data);
			data.setAppData(null);
			data.setDescribe(describe);
			data.setFileName("");
			data.setAttachType("");
			list.add(data);
		}else{
			for(ApplicationData appData : applist){
				
				BatchDownData data = new BatchDownData();
				
				data = setBatchDownData(holder, data);
				data.setAppData(appData);
				data.setDescribe(describe);
				data.setFileName(appData.getFileName());
				data.setAttachType(appData.getRole().getDisplay());
				
				list.add(data);
				//System.out.println(appData.getFileName()  +"," +appData.getRole().getDisplay()+"," );
			}
		}
		
		
		return list;
	}
	
	@Override
	public BatchDownData setBatchDownData(ContentHolder holder, BatchDownData data){
		
		if( holder instanceof WTDocument){
			WTDocument doc = (WTDocument)holder;
			data.setPrimaryObject(doc);
			data.setNumber(doc.getNumber());
			data.setName(doc.getName());
			data.setObjectType("문서");
			data.setOid(CommonUtil.getOIDString(doc));
			data.setCreator(doc.getCreatorFullName());
			data.setModifier(doc.getModifierFullName());
			data.setRev(doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue());
			
		}else if( holder instanceof WTPart){
			WTPart part = (WTPart)holder;
			data.setPrimaryObject(part);
			data.setNumber(part.getNumber());
			data.setName(part.getName());
			data.setObjectType("품목");
			data.setOid(CommonUtil.getOIDString(part));
			data.setCreator(part.getCreatorFullName());
			data.setModifier(part.getModifierFullName());
			data.setRev(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
			
		}else if( holder instanceof EPMDocument){
			EPMDocument epm = (EPMDocument)holder;
			data.setPrimaryObject(epm);
			data.setNumber(epm.getNumber());
			data.setName(epm.getName());
			data.setObjectType("도면");
			data.setOid(CommonUtil.getOIDString(epm));
			data.setCreator(epm.getCreatorFullName());
			data.setModifier(epm.getModifierFullName());
			data.setRev(epm.getVersionIdentifier().getValue()+"."+epm.getIterationIdentifier().getValue());
		}else if(holder instanceof EChangeActivity){
			EChangeActivity eca = (EChangeActivity)holder;
			data.setPrimaryObject(eca);
			data.setNumber(eca.getEo().getEoNumber());
			data.setName(eca.getEo().getEoName());
			data.setObjectType("ECO");
			data.setCreator(eca.getCreatorFullName());
			data.setModifier(eca.getCreatorFullName());
			data.setOid(CommonUtil.getOIDString(eca.getEo()));
		}else if(holder instanceof EChangeOrder){
			EChangeOrder eo = (EChangeOrder)holder;
			data.setPrimaryObject(eo);
			data.setNumber(eo.getEoNumber());
			data.setName(eo.getEoName());
			data.setObjectType("ECO");
			data.setCreator(eo.getCreatorFullName());
			data.setModifier(eo.getCreatorFullName());
			data.setOid(CommonUtil.getOIDString(eo));
		}
		
		return data;
	}

	@Override
	public ApplicationData attachADDRole(ContentHolder holder, String roleType,String cachFile, String delFileName, boolean b) throws Exception {
		ApplicationData app = null;
		ContentItem item = null;
		ContentRoleType contentroleType = ContentRoleType.toContentRoleType(roleType);
		System.out.println("roleType->" + roleType);
		System.out.println("cachFile->" + cachFile);
		System.out.println("delFileName->" + delFileName);
		if(StringUtil.checkString(roleType) && StringUtil.checkString(cachFile)) {
			 
			System.out.println("attachADDRole-> attachADDRole if" );
			 QueryResult result = ContentHelper.service.getContentsByRole (holder ,contentroleType);
			 String roleContent = cachFile;
			 String cacheId = roleContent.split("/")[0];
		     String fileName = roleContent.split("/")[1];
		     EncodingConverter localEncodingConverter = new EncodingConverter();
			String str = localEncodingConverter.decode(cacheId);
		     String[] arrayOfString = str.split(":");
			boolean isParse = true;	
		     try{
					long streamId = Long.parseLong(arrayOfString[0]);
					long fileSize = Long.parseLong(arrayOfString[1]);
					long folderId = Long.parseLong(arrayOfString[2]);
				}catch(NumberFormatException nfe){
					isParse = false;
				}
		     //System.out.println("isParse="+isParse);
			 if (result.hasMoreElements ()) {
	              item = (ContentItem) result.nextElement ();
	              ApplicationData appData = (ApplicationData)item;
				  String apFileName = appData.getFileName();	
	              //System.out.println("delFileName="+delFileName+"\tapFileName="+apFileName);
	            if(apFileName.indexOf(delFileName)>-1)
	               CommonContentHelper.service.delete(holder, item);
	         }
		     CachedContentDescriptor cacheDs = null;
		     if(isParse){
		    	 cacheDs = new CachedContentDescriptor(cacheId);
		    	 app =  CommonContentHelper.service.attachROHS(holder, cacheDs, fileName, null, ContentRoleType.toContentRoleType(roleType));
		     }
		}else{
			System.out.println("attachADDRole-> attachADDRole else" );
			//shjeong 20220218 수정시 삭제되는 문제로 인해 수정
			boolean isJustLikeBefore = delFileName.length() > 0 && delFileName.equals("delocIds_ECO");
			System.out.println("isJustLikeBefore->" + isJustLikeBefore);
			if(!isJustLikeBefore){
				QueryResult result = ContentHelper.service.getContentsByRole (holder ,contentroleType);
				
				if (result.hasMoreElements ()) {
					item = (ContentItem) result.nextElement ();
					CommonContentHelper.service.delete(holder, item);
				}
			}
		}
		
		
		return app;
	}

	@Override
	public CachedContentDescriptor doUpload(CacheDescriptor localCacheDescriptor, File file) throws Exception {
		CachedContentDescriptor ccd = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			long folderId = localCacheDescriptor.getFolderId();
			long fileName = localCacheDescriptor.getFileNames()[0];
			long vaultId = localCacheDescriptor.getVaultId();
			long streamId = localCacheDescriptor.getStreamIds()[0];

			InputStream[] streams = new InputStream[1];
			streams[0] = new FileInputStream(file);
			long[] fileSize = new long[1];
			fileSize[0] = file.length();

			Vault vault = CommonContentHelper.manager.getLocalVault(vaultId);
			saveVault(vault, folderId, fileName, streams[0], fileSize);
			ccd = new CachedContentDescriptor(streamId, folderId, fileSize[0], 0, file.getPath());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return ccd;
	}
	
	private void saveVault(Vault vault, long folderId, long fileName, InputStream inputStream, long[] fileSize)
			throws Exception {
		FileFolder folder = null;
		StoreStreamListener listener = new StoreStreamListener();
		FvTransaction trs = new FvTransaction();
		try {
			trs.start();
			trs.addTransactionListener(listener);
			listener.prepareToGetFolder(vault);

			folder = StandardFvService.getActiveFolder(vault);

			listener.informGotFolderOk();
			FvMount mount = StandardFvService.getLocalMount(folder);

			String path = mount.getPath();
			String fn = StoredItem.buildFileName(fileName);
			String mountType = FvHelper.service.getMountType(mount);
			BackupedFile backupFile = new BackupedFile(mountType, path, fn);

			listener.prepareToUpload(folder, backupFile, path, fn);

			ContentStorageManager manager = ContentManagerFactory.getContentManager(mountType);
			ContentFileWriter cfWriter = manager.getContentFileWriter(backupFile.getFirstContentFile());

			cfWriter.storeStream(inputStream, backupFile, fileSize[0], false);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null) {
				trs.rollback();
			}
		}
	}

	@Override
	public void clear(ContentHolder holder) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}
		
		result.reset();
		result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.THUMBNAIL);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}
		
		result.reset();
		result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.ADDITIONAL_FILES);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}
	}
	
}
