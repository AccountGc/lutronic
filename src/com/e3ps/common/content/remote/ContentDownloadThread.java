package com.e3ps.common.content.remote;

import java.io.InputStream;
import java.util.Vector;

import wt.util.WTThread;

public class ContentDownloadThread extends WTThread
{
	private boolean ready;
    private boolean done;
    private InputStream inputStream;
    private String fileName;
    
    public ContentDownloadThread(Runnable runnable)
    {
        super(runnable);
        ready = false;
        done = false;
    }

    public boolean isReady()
    {
        return ready || done;
    }
    
    

    public void setInputStream(InputStream inputstream)
    {   
    	inputStream = inputstream;
        ready = false;
    }

    public synchronized InputStream getInputStream()
    {
        ready = true;
        notifyAll();
        while(ready) 
            try
            {
                wait();
            }
            catch(InterruptedException interruptedexception) { }
        Vector v = new Vector();
        return inputStream;
    }

    public synchronized void done()
    {
        done = true;
        notifyAll();
    }

}