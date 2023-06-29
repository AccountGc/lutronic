package com.e3ps.common.content;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.content.StreamData;
import wt.content.Streamed;
import wt.fc.LobLocator;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.org.WTPrincipalReference;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.util.WTException;

public class CopyOfCommonContentHelper implements wt.method.RemoteAccess, java.io.Serializable {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static CopyOfCommonContentHelper service = new CopyOfCommonContentHelper();

	public ContentHolder attach( ContentHolder holder, ArrayList files)throws Exception{
		return attach(holder,files,null);
	}
   
	public ContentHolder attach( ContentHolder holder, ArrayList files, ArrayList description )
            throws Exception {

	   if(!SERVER) {
			Class argTypes[] = new Class[]{ContentHolder.class,ArrayList.class,ArrayList.class };
			Object args[] = new Object[]{holder, files, description};
			try {
				return (ContentHolder)wt.method.RemoteMethodServer.getDefault().invoke(
						"attach",
						null,
						this,
						argTypes,
						args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}

       Transaction trx = new Transaction();
       try {
           trx.start();

           WTPrincipalReference principalReference=SessionHelper.manager.getPrincipalReference();

           for ( int i = 0 ; files != null && i < files.size() ; i++ ) {
               String loc = (String)files.get(i);
			   String desc = null;
			   if(description!=null && description.size()>i){
					desc = (String)description.get(i);
			   }
				
				attach(holder, loc, desc, ContentRoleType.SECONDARY);
           }

           trx.commit();
		   trx = null;
       } catch(Exception e) {
           throw e;
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
       return holder;
	}

	public ContentHolder attachPrimary( ContentHolder holder, String loc)throws Exception
	{
		return attach(holder, loc, null, ContentRoleType.PRIMARY);
	}

	public ContentHolder attach( ContentHolder holder, String loc)throws Exception
	{
		return attach(holder, loc, null, ContentRoleType.SECONDARY);
	}

	public ContentHolder attach( ContentHolder holder, String loc, String desc)throws Exception
	{
		return attach(holder, loc, desc, ContentRoleType.SECONDARY);
	}


   public ContentHolder attach( ContentHolder holder, String loc, String desc ,ContentRoleType contentRoleType)
            throws Exception {
		if(!SERVER) {
			Class argTypes[] = new Class[]{ContentHolder.class,String.class,String.class,ContentRoleType.class };
			Object args[] = new Object[]{holder, loc,desc,contentRoleType};
			try {
				return (ContentHolder)wt.method.RemoteMethodServer.getDefault().invoke(
						"attach",
						null,
						this,
						argTypes,
						args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
		Transaction trx=new Transaction();
		try {
           trx.start();

           File file = new File(loc);
           String downloadFile = "";
//           System.out.println("loc	==	"+loc);
           
//           SecureStreamCreator.Init("100167", "scdk2013", null);

//           SecureStreamCreator ssc = new SecureStreamCreator();

//           System.out.println("###	File	==	" + file.getPath() + ":" + file.getName());

//           SecureStreamReader ssr = ssc.CreateDecryptReader(file.getPath());
           String newfile = "";
//           if ( ssr != null ){
//        	   System.out.println("aaaaaaaaa");
//        	   if ( ssr.IsSecureSource() != false ){
//					System.out.println("��ȣȭ");
//					newfile = "C:/DRMTEMP/"+file.getName();
//					ssr.ReadAllToFile(newfile, null);
//					ssr.Close();
//					System.out.println("��ȣȭ �Ϸ�");
        	   
//	        	   System.out.println("bbbbbb");
//	        	   file = new File(newfile);
//	        	   System.out.println("###	File3	==	" + file.getPath() + ":" + file.getName());
//        	   }else{
//    	           System.out.println("loc22	==	"+loc);
//    	           file = new File(loc);
//        	   }
//			}
//           
//		   System.out.println("###	File	==	" + file.getPath() + ":" + file.getName());
           ApplicationData applicationdata = ApplicationData.newApplicationData(holder);
           applicationdata.setFileName(file.getName());
           applicationdata.setUploadedFromPath(file.getPath());
           applicationdata.setRole(contentRoleType);
           applicationdata.setCreatedBy(SessionHelper.manager.getPrincipalReference());

			if(desc!=null ){
				applicationdata.setDescription(desc);
			}
               
			ContentServerHelper.service.updateContent(holder, applicationdata, file.getPath());
               
			if (holder instanceof FormatContentHolder){
               holder = ContentServerHelper.service.updateHolderFormat((FormatContentHolder) holder);
			}
           
           trx.commit();
		   trx = null;
       } catch(Exception e) {
           throw e;
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
       return holder;
   }

   public ContentHolder attach( ContentHolder holder, ApplicationData data, boolean isPrimary )
            throws Exception {
            if (isPrimary)
				return attach(holder, data, ContentRoleType.PRIMARY);
            else
				return attach(holder, data, ContentRoleType.SECONDARY);
   }

   public ContentHolder attach( ContentHolder holder, ApplicationData data, ContentRoleType contentRoleType )
            throws Exception {

		if(!SERVER) {
			Class argTypes[] = new Class[]{ContentHolder.class,ApplicationData.class,ContentRoleType.class };
			Object args[] = new Object[]{holder, data, contentRoleType};
			try {
				return (ContentHolder)wt.method.RemoteMethodServer.getDefault().invoke(
						"attach",
						null,
						this,
						argTypes,
						args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}

      Transaction transaction = new Transaction();
        try
        {
            transaction.start();

            LobLocator loblocator = null;
            data = (ApplicationData) PersistenceHelper.manager.refresh(data);
            Streamed streamed = (Streamed) PersistenceHelper.manager.refresh(data.getStreamData().getObjectId());
            try
            {
                loblocator.setObjectIdentifier(((ObjectReference) streamed).getObjectId());
                ((StreamData) streamed).setLobLoc(loblocator);
            }
            catch (Exception exception)
            {}
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
        }
        catch (Exception e)
        {
            transaction.rollback();
            e.printStackTrace();
        }
        finally
        {
            transaction = null;
        }
        return holder;
      //##end attach%40B6D04600B0.body
   }

   public ContentHolder delete( ContentHolder holder, ContentItem deleteItem )
            throws Exception {
       
	   
	   if(!SERVER) {
			Class argTypes[] = new Class[]{ContentHolder.class,ContentItem.class};
			Object args[] = new Object[]{holder, deleteItem};
			try {
				return (ContentHolder)wt.method.RemoteMethodServer.getDefault().invoke(
						"delete",
						null,
						this,
						argTypes,
						args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}


       Transaction trx = new Transaction();
		try {
			trx.start();
			
			if ( holder == null || deleteItem == null ) return holder;
			holder = ContentHelper.service.getContents(holder);
			ContentServerHelper.service.deleteContent(holder,deleteItem);
		  	
			trx.commit();
		   trx = null;
       } catch(Exception e) {
           throw e;
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
       return holder;
   }

   public ContentHolder delete( ContentHolder holder )
            throws Exception {

		if(!SERVER) {
			Class argTypes[] = new Class[]{ContentHolder.class};
			Object args[] = new Object[]{holder};
			try {
				return (ContentHolder)wt.method.RemoteMethodServer.getDefault().invoke(
						"delete",
						null,
						this,
						argTypes,
						args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}


		Transaction trx = new Transaction();
		try {

		   trx.start();

           holder = ContentHelper.service.getContents(holder);
      		Vector files = ContentHelper.getApplicationData(holder);
      		if ( files != null ) {
      		    for ( int i = 0 ; i < files.size() ; i++ ) {
      		      holder = delete(holder,(ApplicationData)files.get(i));
      		    }
      		}
      		trx.commit();
		   trx = null;
		} catch(Exception e) {
           throw e;
		} finally {
           if(trx!=null){
				trx.rollback();
		   }
		}
   		return holder;
   }
   
   public ContentHolder delete( ContentHolder holder,String pdf )
   throws Exception {

	if(!SERVER) {
		Class argTypes[] = new Class[]{ContentHolder.class};
		Object args[] = new Object[]{holder};
		try {
			return (ContentHolder)wt.method.RemoteMethodServer.getDefault().invoke(
					"delete",
					null,
					this,
					argTypes,
					args);
		}
		catch(RemoteException e) {
			e.printStackTrace();
			throw new WTException(e);
		}
		catch(InvocationTargetException e) {
			e.printStackTrace();
			throw new WTException(e);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	
	Transaction trx = new Transaction();
	try {
	
	  trx.start();
	
	  holder = ContentHelper.service.getContents(holder);
			Vector files = ContentHelper.getApplicationData(holder);
			if ( files != null ) {
			    for ( int i = 0 ; i < files.size() ; i++ ) {
			    	ApplicationData app= (ApplicationData)files.get(i);
			    	if(pdf.equals(app.getDescription())) holder = delete(holder,(ApplicationData)files.get(i));
			    }
			}
			trx.commit();
	  trx = null;
	} catch(Exception e) {
	  throw e;
	} finally {
	  if(trx!=null){
			trx.rollback();
	  }
	}
		return holder;
	}

};