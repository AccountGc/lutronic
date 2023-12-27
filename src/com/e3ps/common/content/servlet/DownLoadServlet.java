package com.e3ps.common.content.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.HolderToContent;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.httpgw.WTContextBean;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.EncodingConverter;

import com.e3ps.common.content.remote.ContentDownload;
import com.e3ps.common.jdf.log.DownloadLoggerWriter;
import com.e3ps.download.service.DownloadHistoryHelper;

//import com.fasoo.adk.packager.WorkPackager;


public class DownLoadServlet extends HttpServlet{

    public static final String HOLDEROID = "holderOid";
    public static final String ADOID = "appOid";

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {
        WTContextBean wtcontextbean = new WTContextBean();
        wtcontextbean.setRequest(req);

        doService(req, res);
    }

    /**
    * POST����� ó���Ѵ�.
    */
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {
        doGet(req,res);
    }

    protected void doService(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{

        String contentOid = request.getParameter(HOLDEROID);
        String addOid = request.getParameter(ADOID);
        String sRtn = "";

        try{

        	
            ReferenceFactory rf = new ReferenceFactory();

            ContentHolder contentHolder = (ContentHolder)rf.getReference(contentOid).getObject();
            ApplicationData ad = (ApplicationData)rf.getReference(addOid).getObject();

            String strClient=request.getHeader("User-Agent");
            String fileName = ad.getFileName();
            
            if(fileName.equals("{$CAD_NAME}")){
            	EPMDocument epm = (EPMDocument)contentHolder;
            	fileName = epm.getCADName();
            }
            
            boolean msie = strClient.indexOf("MSIE")>-1;
            if(msie)
            {
                boolean msie7 = strClient.indexOf("MSIE 7") >= 0;
                if(msie7)
                fileName = EncodingConverter.escape(fileName);
                            // Try to guess user agent's encoding from request headers if not known
               String encoding = "UTF-8";
               fileName = ContentHelper.encode(fileName,encoding);
            }

            if(strClient.indexOf("MSIE 5.5")>-1) {
                response.setHeader("Content-Disposition", "filename=" + fileName + ";");
            } else {
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";");
            }  // end if
            response.setContentType("application/unknown");

            ContentDownload down = new ContentDownload();
            
            ad.setHolderLink(getHolderToContent(contentHolder, ad));
            down.addContentStream(ad);
            down.execute();
            
            InputStream in = down.getInputStream();
            BufferedInputStream fin = new BufferedInputStream(in);
            BufferedOutputStream outs = new BufferedOutputStream(response.getOutputStream());

            int read = 0;
            byte b[] = new byte[4096];
            try {
                while ((read = fin.read(b)) != -1){
                    outs.write(b,0,read);
                }
                outs.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(outs != null)
                        outs.close();
                    if(in != null)
                        fin.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
             
            WTUser user = (WTUser)SessionHelper.getPrincipal();
            Hashtable hash = new Hashtable();
            hash.put("dOid", contentOid);
            hash.put("userId", user.getFullName());
            DownloadHistoryHelper.service.createDownloadHistory(hash);
            
            
            DownloadLoggerWriter log = new DownloadLoggerWriter();
            //String type, WTUser user, ContentHolder holder , ApplicationData data)
            String type = contentHolder.getClass().getName();
            log.println( type , (WTUser)wt.session.SessionHelper.manager.getPrincipal(), contentHolder, ad);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    private HolderToContent getHolderToContent(ContentHolder contentHolder, ApplicationData ad)throws Exception{

        QuerySpec spec = new QuerySpec(ApplicationData.class, wt.content.HolderToContent.class);

        spec.appendWhere(new SearchCondition(ApplicationData.class, "thePersistInfo.theObjectIdentifier.id", "=",  ad.getPersistInfo().getObjectIdentifier().getId()));


        QueryResult queryresult = PersistenceHelper.manager.navigate(contentHolder, "theContentItem" , spec, false);//(pp, "theContentItem", wt.content.HolderToContent.class, false);

        HolderToContent holdertocontent = (HolderToContent)queryresult.nextElement();

        return holdertocontent;
    }

}
