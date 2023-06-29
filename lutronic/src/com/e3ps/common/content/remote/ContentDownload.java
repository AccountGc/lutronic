package com.e3ps.common.content.remote;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Vector;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;



public class ContentDownload implements RemoteAccess, Serializable, Runnable
{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3478805821606945578L;
	/**
	 * 
	 */

	private transient Vector workers;
	private transient ContentDownloadThread downloadThread;
	private transient Throwable downloadException;
	private transient RemoteMethodServer remoteMethodServer;
	private transient URL url;
	private transient Vector reVector;
	
	public ContentDownload()
	{  
		//remoteMethodServer = RemoteMethodServer.getDefault();
	}
	
	public ContentDownload(URL url) {
		this.url = url;
	}	

	public void addContentStream(Object applicationData)
	{
	    addWorker(new ContentDownloadStream(applicationData));
	}

	protected void addWorker(Object obj)
	{
	    if(workers == null){
	    	workers = new Vector();
	    }
	    workers.add(obj);
	}

	public void execute()
	{
	    downloadThread = new ContentDownloadThread(this);
	    downloadThread.start();
	}

	public void run()
	{
	    try
	    {
	        Class aclass[] = {
	            Vector.class
	        };
	        Object aobj[] = {
	            workers
	        };
	        if(url != null) {
	        	RemoteMethodServer rs = RemoteMethodServer.getInstance(url);
	            Object obj = rs.invoke("execute", null, this, aclass, aobj);
	        }else{
	        	RemoteMethodServer.getDefault().invoke("execute", null, this, aclass, aobj);
	        }
	        
	    }
	    catch(InvocationTargetException invocationtargetexception)
	    {   
	    	invocationtargetexception.printStackTrace();
	        downloadException = invocationtargetexception.getTargetException();
	    }
	    catch(Throwable throwable)
	    {   
	    	throwable.printStackTrace();
	        downloadException = throwable;
	    }finally{
	    	
	    }
	}

	public Vector execute(Vector vector)
	    throws WTException
	{   
		
	    return vector;
	}

	public InputStream getInputStream()
	{
		return downloadThread.getInputStream();
	}

	public void done()
	{
	    if(downloadThread != null){
	        downloadThread.done();
	    }
	}

	public void checkStatus()
	    throws WTException
	{
	    if(downloadException != null)
	    {
	        if(downloadException instanceof WTException)
	            throw (WTException)downloadException;
	        else
	            throw new WTException(downloadException);
	    } else
	    {
	        return;
	    }
	}
	
	
}