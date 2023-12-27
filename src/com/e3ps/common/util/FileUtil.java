/**
 * @(#) FileUtil.java Copyright (c) jerred. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2004. 3. 22.
 * @author Cho Sung Ok, jerred@bcline.com
 * @desc
 */

package com.e3ps.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;

import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.content.Streamed;
import wt.fc.PersistenceHelper;
import wt.method.RemoteMethodServer;
import wt.util.WTException;


/**
 * 
 */
public class FileUtil implements wt.method.RemoteAccess, java.io.Serializable {
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
	public static FileUtil manager = new FileUtil();
	
	
	public static void checkDir(String dir) {
		File createDir = new File ( dir );
		if (!createDir.exists ()) createDir.mkdir ();
		if (!createDir.exists ()) createDir.mkdirs ();
	}

	public static void checkDir(File dir) {
		if (!dir.exists ()) dir.mkdir ();
		if (!dir.exists ()) dir.mkdirs ();
	}

	public static String getFileSizeStr(long filesize) {
		DecimalFormat df = new DecimalFormat ( ".#" );
		String fSize = "";
		if ( ( filesize > 1024 ) && ( filesize < 1024 * 1024 )) {
			fSize = df.format ( (float) filesize / 1024 ).toString () + " KB";
		} else if (filesize >= 1024 * 1024) {
			fSize = df.format ( (float) filesize / ( 1024 * 1024 ) ).toString ()	+ " MB";
		} else if (filesize < 1024 && filesize > 1) {
			fSize = "1 KB";
		} else {
			fSize = "0 Bytes";
		}
		return fSize;
	}
	
	   public void copyFiles(ApplicationData appData, String moveTo, String fileName) throws Exception {
		   
		   if (!SERVER) {
				Class argTypes[] = new Class[]{ApplicationData.class,String.class,String.class};
				Object args[] = new Object[]{appData, moveTo, fileName};
				try {
					RemoteMethodServer.getDefault().invoke("copyFiles", null, this, argTypes, args);
					return;
				} catch (RemoteException e) {
					e.printStackTrace();
					throw new WTException(e);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					throw new WTException(e);
				}
			}
	   
			int fileSize    = 0;
			int result      = 0;
			BufferedOutputStream bo = null;
			
			try {
				//System.out.println("1appData : "+appData);
				//System.out.println("2moveTo :"+moveTo);
				//System.out.println("3fileName :"+fileName);
				//System.out.println("4appData Size : "+appData.getFileSize());
				
				InputStream inStream = ContentServerHelper.service.findContentStream((ApplicationData)appData);
				
				//FileInputStream  inStream = (FileInputStream)FileUtil.findContentStream(appData);
				
				//System.out.println("5inStream : "+inStream);
				
				BufferedInputStream bi = new BufferedInputStream(inStream);
				
				File toFile = new File(moveTo);
		
				if(!toFile.exists())
					toFile.mkdirs();
		
				FileOutputStream outStream = new FileOutputStream(toFile + "\\" + fileName);
				bo = new BufferedOutputStream(outStream);
				fileSize = bi.available();
				
				while( (result = bi.read())  != -1  ){
					bo.write(result);
				}		
			} catch(Exception e) {
				e.printStackTrace();
				throw new WTException(e);
			} finally {
				if(bo != null)
					bo.close();
			}
		}  

	   
	   
		public static InputStream findContentStream( ApplicationData appData ) throws WTException {
			
			Streamed sd = null;
			
			try {
				sd = ( Streamed )PersistenceHelper.manager.refresh( appData.getStreamData( ).getObjectId( ) );
				//System.out.println("findContentStream : "+sd);
	        } catch (wt.fc.ObjectNoLongerExistsException e){
	        	appData = (ApplicationData)PersistenceHelper.manager.refresh( appData );
	            sd = ( Streamed )PersistenceHelper.manager.refresh( appData.getStreamData( ).getObjectId( ) );
				//System.out.println("catch findContentStream : "+sd);
	        }
	        
	        InputStream is = sd.retrieveStream();
	        
	        return is;
	        
	    }
	   
	   
}
