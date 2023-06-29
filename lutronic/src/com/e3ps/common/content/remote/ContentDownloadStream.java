package com.e3ps.common.content.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTIOException;

public class ContentDownloadStream implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7201426360269959935L;
	/**
	 * 
	 */
	private static final int BUFSIZ = 8192;
	private transient Object applicationData;
	private transient boolean download;
    private transient FilterInputStream stream;
	
	public ContentDownloadStream(Object applicationData)
	{
	    download = false;
	    this.applicationData = applicationData;
	}
	
	public InputStream getInputStream()
	{
		return stream;
	}
	
	private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException
	{
	    boolean flag = objectinputstream.readBoolean();

	    if(flag)
	    {
	        ContentDownloadThread contentdownloadthread = (ContentDownloadThread)Thread.currentThread();
	        synchronized(contentdownloadthread)
	        {  
	        	
	            while(!contentdownloadthread.isReady())
	            {
	                try
	                {   
	                	
	                    contentdownloadthread.wait();
	                }
	                catch(InterruptedException interruptedexception) { }
	            }
	            
	            FilterInputStream filterinputstream = new FilterInputStream(objectinputstream)
	            {
	                public void close()
	                {
	                }
	            };
	            
	            contentdownloadthread.setInputStream(filterinputstream);
	            contentdownloadthread.notifyAll();
	            
	            while(!contentdownloadthread.isReady())
	            { 
	                try
	                {
	                    contentdownloadthread.wait();
	                }
	                catch(InterruptedException interruptedexception1) { }
	            }
	        }
	    }
	    else
	    {
	        applicationData = (Object)objectinputstream.readObject();
	        download = true;
	    }
	}
	
	private void writeObject(ObjectOutputStream objectoutputstream) throws IOException
	{
	    objectoutputstream.writeBoolean(download);
	    
	    if(download)
	    {
	        InputStream inputstream = null;
	        
	        try
	        {
	            inputstream = findContentStream(applicationData);
	           
	            byte abyte0[] = new byte[BUFSIZ];
	            int i;
	            
	            while((i = inputstream.read(abyte0, 0, BUFSIZ)) > 0)
	            {
	                objectoutputstream.write(abyte0, 0, i);
	            }
	        }
	        catch(WTException wtexception)
	        {
	            throw new WTIOException((String)null, wtexception);
	        }
	        catch(Exception exception)
	        {
	            throw new WTIOException((String)null, exception);
	        }
	        finally
	        {
	            if(inputstream != null)
	                inputstream.close();
	        }
	    }
	    else
	    {
	        objectoutputstream.writeObject(applicationData);
	    }
	}
	
	private InputStream findContentStream(Object applicationdata)throws WTException
	{ 
		SessionContext sessioncontext = SessionContext.newContext();
      
        try
        {
            SessionHelper.manager.setAdministrator();
            
            if(applicationdata instanceof ApplicationData)
            {
            	return ContentServerHelper.service.findContentStream((ApplicationData)applicationdata);
            }
            else
            {
                try
                {
					return	new FileInputStream(new File((String)applicationdata));
				}
                catch (FileNotFoundException e)
                {
					e.printStackTrace();
					throw new WTException(e);
				}
            }
        }
        finally
        {
        	SessionContext.setContext(sessioncontext);
        }
	}
}
