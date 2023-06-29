/*
 * @(#) MakeZIPHelper.java  Create on 2005. 7. 29.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.distribute.util;

import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;















import jxl.write.WritableWorkbook;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.HolderToContent;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.URLFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.query.template.URLGenerator;
import wt.representation.Representation;
import wt.servlet.WcUtils;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;
import wt.viewmarkup.WTMarkUp;

import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.content.FileDown;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.excelDown.service.ExcelDownHelper;
import com.e3ps.common.mail.MailUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.download.service.DownloadHistoryHelper;
import com.e3ps.drawing.beans.EpmUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.ptc.wvs.server.util.ETB;
import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.Util;


/**
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2005. 7. 29.
 * @since 1.4
 */


public class MakeZIPUtil implements RemoteAccess
{
	
	/*public static File distribteSaveZip(Distribute dis) throws Exception{
		
		try{
			
			//1.대상 파일 Search
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	*/
	public static File drawingSaveZip(Vector<ApplicationData> vecApp,String fileName,ArrayList<String> list,HttpServletResponse response) throws Exception{
	  	  response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + ".zip\"");
	      response.setContentType("application/zip");
		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		  ZipOutputStream zos = new ZipOutputStream(baos);
          byte buffer[] = new byte[1024];
          //System.out.println("vecApp = "+ vecApp.size());
		  for(ApplicationData appData : vecApp){
			 //System.out.println("appData File = " +appData.getFileName());
			
			 InputStream inputstream =  ContentServerHelper.service.findContentStream(appData);
			 for (int j = 0; j < list.size(); j++) {
				 if(String.valueOf(list.get(j)).equals(appData.getFileName())){
					 list.remove(j);
					 //System.out.println("List Dell Appdata = " +appData.getFileName());
					 break;
				 }
			}
			 //System.out.println("!list.contains(appData.getFileName() = " +(!list.contains(appData.getFileName())));
			 if(list.contains(appData.getFileName())) continue;
				 zos.putNextEntry(new ZipEntry(appData.getFileName()));
             int h;
             while ((h = inputstream.read(buffer, 0, 1024)) > 0){
            	  zos.write(buffer, 0, h);
             }
             zos.closeEntry();
             inputstream.close();
		  }
		   zos.close();
		    try{            
		        response.getOutputStream().write(baos.toByteArray());
		        response.flushBuffer();
		    }
		    catch (IOException e){
		        e.printStackTrace();        
		    }
		    finally{
		        baos.close();
		    }
		return null;
	}
	
	
	
	
	public static File download(InputStream in, String fileName)
	    throws Exception
	  {
	    BufferedOutputStream outs = null;
	    BufferedInputStream fin = null;

	    byte[] b = new byte[4096];

	    int read = 0;
	    File temFile = null;
	    String orgFileTempFolder ="D:\\"+fileName;
	    try {
	      temFile = new File(orgFileTempFolder);
	      fin = new BufferedInputStream(in);
	      outs = new BufferedOutputStream(new FileOutputStream(temFile));
	      while ((read = fin.read(b)) != -1)
	      {
	        outs.write(b, 0, read);
	        outs.flush();
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	      throw new WTException(e);
	    } finally {
	      try {
	        fin.close();
	      } catch (IOException localIOException1) {
	        localIOException1.printStackTrace();
	        throw new WTException(localIOException1);
	      }
	    }

	    return temFile;
	  }	
	
    public static File saveAsZIP(EPMDocument epm)
    {
        if (!RemoteMethodServer.ServerFlag)
        {
            try
            {
                RemoteMethodServer rms = RemoteMethodServer.getDefault();
                Class[] argTypes = { EPMDocument.class};
                Object[] argValues = { epm };
                return (File) rms.invoke("saveAsZIP", "com.e3ps.distribute.util.MakeZIPUtil", null, argTypes, argValues);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        Representation representation = PublishUtils.getRepresentation(epm);
        
//        QueryResult rs = null;
//        DistributeProcess process = null;
//        
//		try {
//			rs = PersistenceHelper.manager.navigate(epm, "distributeProcess", DistributeProcessObjLink.class);	
//	        
//	        if(rs.hasMoreElements()){
//	        	process = (DistributeProcess)rs.nextElement();
//	        }
//	        
//		} catch (WTException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
        
        
        File zipfile = null;
        FileOutputStream fos = null;
        try
        {
            String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
            zipfile = new File(tempDir + File.separator + epm.getNumber() + ".zip");
            fos = new FileOutputStream(zipfile);
            saveZIP(representation, false, fos);
            //saveZIP(representation, false, fos, process);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        catch (PropertyVetoException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fos != null) fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return zipfile;
    }

    private static void saveZIP(Representation representation, boolean isAnnotation, OutputStream os)//, DistributeProcess process)// ,
            // boolean
            // flag2)
            throws WTException, PropertyVetoException, IOException
    {
        boolean flag2 = false;
        if (representation == null || os == null) return;

        ZipOutputStream zos = new ZipOutputStream(os);
        byte buffer[] = new byte[1024];
        Vector vector = null;
        String edFileName = null;
        String fileCharset = null;

        representation = (Representation) ContentHelper.service.getContents(representation);
        Vector contentList = ContentHelper.getContentList(representation);
        for (int l = 0; l < contentList.size(); l++)
        {
            ContentItem contentitem = (ContentItem) contentList.elementAt(l);
            if (contentitem instanceof ApplicationData)
            {
                ApplicationData applicationdata = (ApplicationData) contentitem;
                String fileName = applicationdata.getFileName();

                String extension = Util.getExtension(fileName).toUpperCase();
                
                if (applicationdata.getRole() == ContentRoleType.PRODUCT_VIEW_ED)
                {
                   
                    
                    InputStream inputstream = ContentServerHelper.service.findContentStream(applicationdata);
                    
                    int i;
                    while ((i = inputstream.read(buffer, 0, 1024)) > 0)
                        zos.write(buffer, 0, i);
                    //inputstream.close();
                    zos.closeEntry();
                  
                }
                else if (applicationdata.getRole() != ContentRoleType.PRODUCT_VIEW_EDZ)
                {
                    if (extension.equals("ETB")) fileName = fileName + ".save";
                    InputStream inputstream = ContentServerHelper.service.findContentStream(applicationdata);
                   
                    
                    try
                    {
                        zos.putNextEntry(new ZipEntry(fileName));
                        int i;
                        while ((i = inputstream.read(buffer, 0, 1024)) > 0)
                            zos.write(buffer, 0, i);

                        zos.closeEntry();
                    }
                    catch (Exception ze)
                    {
                        ze.printStackTrace();
                    }
                }
            }
        }
      
        
        if (!isAnnotation || edFileName == null || !(representation instanceof Viewable))
        {
        	zos.close();
            return;
        }

        QueryResult qr = ViewMarkUpHelper.service.getMarkUps((Viewable) representation);
        
        if (qr == null || qr.size() == 0)
        {
            zos.close();
            return;
        }

        StringBuffer stringbuffer = new StringBuffer(300);
        while (qr.hasMoreElements())
        {
            WTMarkUp wtmarkup = (WTMarkUp) qr.nextElement();
            String s5 = wtmarkup.getAdditionalInfo();
            ETB etb = new ETB(s5);
            String s6 = etb.getWcFile();
            String s7 = s6;
            int i1 = s6.indexOf("!>");
            if (i1 >= 0 && i1 < s6.length() - 3)
            {
                s7 = s6.substring(i1 + 2);
                s5 = Util.SandR(s5, s6, s7);
            }
            String s8 = etb.getTargetWcFile();
            i1 = s8.indexOf("!>");
            if (i1 >= 0 && i1 < s8.length() - 3) s5 = Util.SandR(s5, s8, s8.substring(i1 + 2));
            stringbuffer.append(s5).append("\n");
            wtmarkup = (WTMarkUp) ContentHelper.service.getContents(wtmarkup);
            Vector vector2 = ContentHelper.getContentListAll(wtmarkup);
            for (int j1 = 0; j1 < vector2.size(); j1++)
            {
                ContentItem contentitem1 = (ContentItem) vector2.elementAt(j1);
                if (contentitem1 instanceof ApplicationData)
                {
                    ApplicationData applicationdata2 = (ApplicationData) contentitem1;
                    String s1;
                    if (applicationdata2.getRole() == ContentRoleType.THUMBNAIL) s1 = Util.setExtension(s7, "gif");
                    else s1 = s7;
                    InputStream inputstream2 = ContentServerHelper.service.findContentStream(applicationdata2);
                    
                    try
                    {
                        zos.putNextEntry(new ZipEntry(s1));
                        int k;
                        while ((k = inputstream2.read(buffer, 0, 1024)) > 0)
                            zos.write(buffer, 0, k);

                        zos.closeEntry();
                    }
                    catch (Exception ze)
                    {
                        ze.printStackTrace();
                    }
                }
            }

        }

        if (stringbuffer != null)
        {
            zos.putNextEntry(new ZipEntry(Util.setExtension(edFileName, "etb")));
            if (fileCharset != null && fileCharset.length() > 0) zos.write(stringbuffer.toString().getBytes(fileCharset));
            else zos.write(stringbuffer.toString().getBytes());
            zos.closeEntry();
        }
        zos.close();
        
       
    }

    public static File saveAsZIP(String _fileName, Vector _contentHolders, String _distirubeType)
    {
        if (!RemoteMethodServer.ServerFlag)
        {
            try
            {
                RemoteMethodServer rms = RemoteMethodServer.getDefault();
                Class[] argTypes = { String.class, Vector.class};
                Object[] argValues = { _fileName, _contentHolders, _distirubeType};
                return (File) rms.invoke("saveAsZIP", "e3ps.distribute.util.MakeZIPUtil", null, argTypes, argValues);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            if (_fileName == null) _fileName = getTempFileName();
            String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
            File zipfile = new File(tempDir + File.separator + _fileName + ".zip");
            FileOutputStream fos = new FileOutputStream(zipfile);
            saveZIP(_contentHolders, _distirubeType, fos);
            fos.close();

            return zipfile;
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        catch (PropertyVetoException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * �ӽ� ���ϸ� ��
     * 
     * @return
     */
    public static String getTempFileName()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new java.util.Date());
    }

    /**
     * EPMDocument �� ������ ��ü�� ������ ��� ���
     * 
     * @param _holder
     * @param _distirubeType
     * @param _fileName
     * @return
     */
    public static File saveAsZIP(ContentHolder _holder, String _distirubeType, String _fileName)
    {
        if (!RemoteMethodServer.ServerFlag)
        {
            try
            {
                RemoteMethodServer rms = RemoteMethodServer.getDefault();
                Class[] argTypes = { ContentHolder.class, String.class};
                Object[] argValues = { _holder, _distirubeType};
                return (File) rms.invoke("saveAsZIP", "e3ps.distribute.util.MakeZIPUtil", null, argTypes, argValues);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        File zipfile = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try
        {
            if (_fileName == null) _fileName = getTempFileName();
            String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
            zipfile = new File(tempDir + File.separator + _fileName + ".zip");
            fos = new FileOutputStream(zipfile);
            zos = new ZipOutputStream(fos);
            addZIP(zos, _holder, _distirubeType);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (zos != null) zos.close();
                if (fos != null) fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return zipfile;
    }

    private static void addZipEntry(ZipOutputStream zos, ApplicationData adata) throws WTException, IOException
    {
        byte buffer[] = new byte[1024];
        InputStream is = ContentServerHelper.service.findContentStream(adata);
        
        zos.putNextEntry(new ZipEntry(adata.getFileName()));
        int i = 0;
        
        
        while ((i = is.read(buffer, 0, 1024)) > 0)
            zos.write(buffer, 0, i);

        zos.closeEntry();
        is.close();
    }

    private static void addZipEntry(ZipOutputStream zos, File _file) throws WTException, IOException
    {
        byte buffer[] = new byte[1024];
        InputStream fin = new FileInputStream(_file);
        
        zos.putNextEntry(new ZipEntry(_file.getName()));
        int i = 0;
        while ((i = fin.read(buffer, 0, 1024)) > 0)
            zos.write(buffer, 0, i);

        zos.closeEntry();
        fin.close();
    }

    private static void addZIP(ZipOutputStream _zos, ContentHolder _holder, String _distType) throws WTException, IOException
    {
    	if (_holder instanceof EPMDocument)
        {	
        	
        	
            if("conversion".equals(_distType))
            {
                // ��ȯ�� �ȵ� ������ ���� ���� �ڵ� ���
                Representation representation = PublishUtils.getRepresentation((EPMDocument)_holder);
                if(representation == null)  _distType = "original";
            }
            if ("original".equals(_distType))
            {
                QueryResult qr_Content = ContentHelper.service.getContentsByRole((EPMDocument) _holder, ContentRoleType.SECONDARY);
                while (qr_Content.hasMoreElements())
                {
                    ContentItem item = (ContentItem) qr_Content.nextElement();
                    if (item instanceof ApplicationData)
                    {
                        addZipEntry(_zos, (ApplicationData) item);
                        break;
                    }
                }
            }
            else
            {
                File zip_ProductView = saveAsZIP((EPMDocument) _holder);
                addZipEntry(_zos, zip_ProductView);
            }
        }
        else
        {
            Vector vec = ContentHelper.getContentListAll(_holder);
            if (vec.size() == 1)
            {
                addZipEntry(_zos, (ApplicationData) vec.get(0));
            }
            else
            {
                for (int i = vec.size() - 1; i > -1; i--)
                    addZipEntry(_zos, (ApplicationData) vec.get(i));
            }
        }
    }

    private static void saveZIP(Vector _list, String _distributeType, OutputStream os) throws WTException, PropertyVetoException, IOException
    {
        ZipOutputStream zos = new ZipOutputStream(os);

        for (int i = _list.size() - 1; i > -1; i--)
        {
            ContentHolder holder = (ContentHolder) _list.get(i);
            addZIP(zos, holder, _distributeType);
        }

        if (zos != null) zos.close();
    }
    
    public static File attchFileZip(List<ApplicationData> list,String fileName,HttpServletResponse response) throws Exception{
		  response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + ".zip\"");
		  response.setContentType("application/zip");
		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		  ZipOutputStream zos = new ZipOutputStream(baos);
		  byte buffer[] = new byte[1024];
		  //System.out.println("attchFileZip vecApp = "+ list.size());
		  for(ApplicationData appData : list){
			 //System.out.println("attchFileZip appData File = " +appData.getFileName());
			 InputStream inputstream =  ContentServerHelper.service.findContentStream(appData);
			 
			 //System.out.println("!list.contains(appData.getFileName() = " +(!list.contains(appData.getFileName())));
			 if(list.contains(appData)) {
				 continue;
			 }
			 zos.putNextEntry(new ZipEntry(appData.getFileName()));
		    int h;
		    while ((h = inputstream.read(buffer, 0, 1024)) > 0){
		  	  zos.write(buffer, 0, h);
		    }
		    zos.closeEntry();
		    inputstream.close();
		  }
		  zos.close();
		 try{            
		        response.getOutputStream().write(baos.toByteArray());
		        response.flushBuffer();
		 } catch (IOException e){
		
		    e.printStackTrace();        
		 } finally{
		
		    baos.close();
		}
		return null;
    }
    
    /**
     * 일괄 다운로드 Zip,파일 생성,엑셀,history,메일
     * @param targetlist
     * @param fileName
     * @return
     * @throws Exception
     */
    public static File batcchAttchFileZip(List<BatchDownData> targetlist,String fileName) throws Exception{
    	String orgFileName = fileName;
    	fileName = fileName+"_"+System.currentTimeMillis();
    	File zipfile = null;
        FileOutputStream fos = null;
        
    	String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
        zipfile = new File(tempDir + File.separator + fileName + ".zip");
        fos = new FileOutputStream(zipfile);
        
		ZipOutputStream zos = new ZipOutputStream(fos);
		byte buffer[] = new byte[1024];
		List<ApplicationData> list = new ArrayList<ApplicationData>();
		String describe = "";
		for(BatchDownData data : targetlist){
			 describe = data.getDescribe();
    		 ApplicationData appData = data.getAppData();
    		 if(appData == null){
    			 data.setMsg("파일이 존재 하지 않습니다.");
    			 data.setResult(false);
    			 continue;
    		 }
    		
    		 InputStream inputstream = null;
    		 //System.out.println("appData =" + appData.getFileName() +","+appData.getType());
    		 try{
    			 
    			 //2D 변환 파일은 권한 체크 하지 않음
    			 if(!EpmUtil.isDwrainPublishFile(appData)){
    				 URL durl = ContentHelper.service.getDownloadURL((ContentHolder)data.getPrimaryObject(), appData);
    			 }
    			 
    			
    			//  System.out.println("durl = " +durl +"=======" +durl.toString());
    		 }catch(Exception e){
    			 e.printStackTrace();
    			 data.setMsg(e.getLocalizedMessage());
    			 data.setResult(false);
    			 continue;
    		 }
    		 //System.out.println("appData File = " +appData.getFileName());
    		 inputstream =  ContentServerHelper.service.findContentStream(appData);
			 
    		 //System.out.println("!list.contains(appData.getFileName() = " +(!list.contains(appData.getFileName())));
			 if(list.contains(appData)) {
				 continue;
			 }else{
				 list.add(appData);
			 }
			
			String zipOrgFileName = appData.getFileName();
			if(data.getPrimaryObject() instanceof EPMDocument){
				EPMDocument epm = (EPMDocument)data.getPrimaryObject();
				if( EpmUtil.isCreoDrawing(epm)){
					zipOrgFileName= EpmUtil.getPublishFile(epm, zipOrgFileName);
				}
			}
		
			data.setMsg("다운로드");
			data.setResult(true);
			zos.putNextEntry(new ZipEntry(zipOrgFileName));
		    int h;
		    while ((h = inputstream.read(buffer, 0, 1024)) > 0){
		  	  zos.write(buffer, 0, h);
		    }
		    zos.closeEntry();
		    inputstream.close();
    	}
		//엑셀 파일 생성
		File excelFile = ExcelDownHelper.service.batchDownloadexcelDown(orgFileName, targetlist);
		zos.putNextEntry(new ZipEntry(excelFile.getName()));
		FileInputStream outExel = new FileInputStream(excelFile);
		//InputStream inputstreamExcel = outExel
	    int h;
	    while ((h = outExel.read(buffer, 0, 1024)) > 0){
	  	  zos.write(buffer, 0, h);
	    }
	    zos.closeEntry();
		zos.close();
		
		//Histor 새성
		DownloadHistoryHelper.service.createBatchDownloadHistory(targetlist);
		
		
		//메일 발송
		WTUser downUser = (WTUser)SessionHelper.getPrincipal();
		String subject = "[일괄다운로드]"+downUser.getFullName() +"["+orgFileName+"]";
		String content = subject+"<br>";
	    content = content +"[다운로드 사유] " + "<br>"+describe;
		Hashtable hash = new Hashtable();
		Vector attache = new Vector<>();
		attache.add(excelFile.getAbsolutePath());
		HashMap to = new HashMap();
		//to.put("tsuam@e3ps.com", "엄태식");//
		to.put("yjjeong@lutronic.com", "정윤지");
		hash.put("SUBJECT", subject);
		hash.put("CONTENT", content);
		hash.put("TO", to);
		hash.put("ATTACHE", attache);
		try{
			MailUtil.manager.sendMail(hash);
			
		}catch(Exception e){
			e.printStackTrace();
		}
    	return zipfile;
    	
    	
    }
    
    public static File attachFileSaveZip(Vector<BatchDownData> vecApp,String fileName) throws Exception{
	  	  
		//response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + ".zip\"");
		//response.setContentType("application/zip");
		
		String orgFileName = fileName;
    	fileName = fileName+"_"+System.currentTimeMillis();
    	File zipfile = null;
        FileOutputStream fos = null;
        
    	String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
        zipfile = new File(tempDir + File.separator + fileName + ".zip");
        fos = new FileOutputStream(zipfile);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
        ZipOutputStream zos = new ZipOutputStream(fos);

		HashMap<String, String> doubleCheckmap = new HashMap<String, String>();
	  
		for(BatchDownData batchData : vecApp){
			
			ApplicationData appData = batchData.getAppData();
			
			if(doubleCheckmap.containsKey(appData.getFileName())) {
				continue;
			}
				doubleCheckmap.put(appData.getFileName(), appData.getFileName());
			
			
			try{
				URL durl = ContentHelper.service.getDownloadURL((ContentHolder)batchData.getPrimaryObject(), appData);
			}catch(Exception e){
				throw new Exception(e.getLocalizedMessage());
			}
			
			InputStream inputstream =  ContentServerHelper.service.findContentStream(appData);
		
			
			zos.putNextEntry(new ZipEntry(appData.getFileName()));
			int h;
			while ((h = inputstream.read(buffer, 0, 1024)) > 0){
				zos.write(buffer, 0, h);
			}
			zos.closeEntry();
			inputstream.close();
		}
	   zos.close();
	  
	   
	   return zipfile;
}
    
    
    

    
}