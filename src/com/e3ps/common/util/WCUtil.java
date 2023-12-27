package com.e3ps.common.util;

import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.FormatContentHolder;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.ExchangeContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipalReference;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;

public class WCUtil
{
    private static ReferenceFactory rf;

    /**
     * @since 2007/05/27
     * @author kang ho chul
     * @see
     * @return Vector - description: Lifecycle state �� ��ȯ�Ѵ�
     * 
     */
    public static Vector getState(String lifeCycle) throws WTException
    {

        Vector stateVec = null;

        WTContainerRef wtContainerRef = null;
        try
        {
            wtContainerRef = getWTContainerRef();
        }
        catch (Exception e)
        {

            e.printStackTrace();
            throw new WTException(e);
        }

        if (wtContainerRef != null)
        {
            try
            {
                LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(lifeCycle, wtContainerRef);
                stateVec = LifeCycleHelper.service.findStates(lct);

            }
            catch (LifeCycleException e)
            {
                e.printStackTrace();
                throw new WTException(e);
            }
        }

        return stateVec;
    }

    /**
     * @since 2007/05/27
     * @author kang ho chul
     * @see
     * @return WTPrincipalReference - description: WTPrincipalReference �� ��ȯ�Ѵ�
     * 
     */
    public static WTPrincipalReference getWTPrincipalReference(String id)
    {

        WTPrincipalReference wtpr = null;
        try
        {
            wtpr = OrganizationServicesHelper.manager.getPrincipalReference(id, OrganizationServicesHelper.manager.getDefaultDirectoryService());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return wtpr;
    }

    /**
     * @since 2007/05/27
     * @author kang ho chul
     * @see
     * @return WTContainerRef - description: WTContainerRef �� ��ȯ�Ѵ�
     * 
     */
    public static WTContainerRef getWTContainerRef() throws Exception
    {

        PDMLinkProduct wtProduct = getPDMLinkProduct();
        WTContainerRef wtContainerRef = null;
        if (wtProduct != null)
        {
            wtContainerRef = WTContainerRef.newWTContainerRef(wtProduct);
        }

        return wtContainerRef;
    }
    
    public static WTContainerRef getWTContainerRef(String productName) throws Exception
    {
    	
        PDMLinkProduct wtProduct = getPDMLinkProduct(productName);
        WTContainerRef wtContainerRef = null;
        if (wtProduct != null)
        {
            wtContainerRef = WTContainerRef.newWTContainerRef(wtProduct);
        }

        return wtContainerRef;
    }

    /**
     * @since 2007/05/27
     * @author kang ho chul
     * @see
     * @return PDMLinkProduct - description: PDMLinkProduct �� ��ȯ�Ѵ�
     * 
     */
    
    public static PDMLinkProduct getPDMLinkProduct() throws Exception
    {
		Config conf = ConfigImpl.getInstance();
		String productName = conf.getString("product.context.name");
        QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
        SearchCondition sc1 = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, productName);
        qs.appendSearchCondition(sc1);
        QueryResult results = (QueryResult) PersistenceHelper.manager.find(qs);
        PDMLinkProduct wtProduct = null;
        if (results.hasMoreElements())
        {
            wtProduct = (PDMLinkProduct) results.nextElement();
        }

        return wtProduct;
    }
    
    public static PDMLinkProduct getPDMLinkProduct(String productName) throws Exception
    {
		
        QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
        SearchCondition sc1 = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, productName);
        qs.appendSearchCondition(sc1);
        QueryResult results = (QueryResult) PersistenceHelper.manager.find(qs);
        PDMLinkProduct wtProduct = null;
        if (results.hasMoreElements())
        {
            wtProduct = (PDMLinkProduct) results.nextElement();
        }

        return wtProduct;
    }
    
    /**
     * @since 2007/05/27
     * @author kang ho chul
     * @see
     * @return PDMLinkProduct - description: PDMLinkProduct �� ��ȯ�Ѵ�
     * 
     */
    
    public static ExchangeContainer getSiteContainer() throws Exception
    {
        QuerySpec qs = new QuerySpec(ExchangeContainer.class);
        QueryResult results = (QueryResult) PersistenceHelper.manager.find(qs);
        ExchangeContainer ec = null;
        if (results.hasMoreElements())
        {
            ec = (ExchangeContainer) results.nextElement();
        }

        return ec;
    }
    /**
     * @since 2007/05/27
     * @author kang ho chul
     * @see
     * @return String - description: OID �� ��ȯ�Ѵ�
     * 
     */
    public static String getOIDString(Persistable per)
    {
        try
        {
            /*
             * ReferenceFactory rf = new ReferenceFactory(); return rf.getQueryString(rf.getReference(per));
             */
            if (per == null) { return ""; }
            return PersistenceHelper.getObjectIdentifier(per).getStringValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * 
     * @author Choi Seunghwan, swchoi@e3ps.com
     * @createDate 2007. 08. 06
     * @param per
     * @param isFullOid
     * @return
     */
    public static String getOIDString(Persistable persistable, boolean isFullOid)
    {
        if (persistable == null) return null;
        try
        {
            if (rf == null) rf = new ReferenceFactory();
            
            if(isFullOid)
                return rf.getReferenceString(persistable);
            else
                return PersistenceHelper.getObjectIdentifier(persistable).getStringValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @since 2007/05/27
     * @author kang ho chul
     * @see
     * @return String - description: WorkProcess OID �� ��ȯ�Ѵ�
     * 
     */
    public static String getWfProcessOIDString(Persistable per)
    {
        try
        {
            String workProcessOid = "";
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem item = (WorkItem) per;
            WfAssignedActivity source = (WfAssignedActivity) item.getSource().getObject();
            WfProcess process = source.getParentProcess();
            workProcessOid = rf.getReferenceString(process);
            return workProcessOid;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @since 2007/05/27
     * @author kang ho chul oid�� �ش��ϴ� Persistable��ü�� ��ȯ�Ѵ�.
     * @param oid
     * @return Persistable
     */
    public static Persistable getPersistable(String oid) throws Exception
    {
        return getObject(oid);
    }

    /**
     * @since 2007/05/27
     * @author kang ho chul ��ü�� ÷�εǾ� �ִ� ���� ������ ���´�.
     * @param fch
     * @return
     */
    public static Vector getAttacheFiles(FormatContentHolder fch)
    {
        Vector secVec = null;
        try
        {
            ContentHolder ch = (ContentHolder) fch;
            FormatContentHolder formatCHolder = (FormatContentHolder) ContentHelper.service.getContents(ch);

            Vector sfv = ContentHelper.getContentList(formatCHolder);

            if (sfv != null && sfv.size() > 0)
            {
                secVec = new Vector();
                for (int ii = 0; ii < sfv.size(); ii++)
                {
                    ContentItem sci = (ContentItem) sfv.get(ii);
                    if (sci instanceof ApplicationData)
                    {
                        ApplicationData secAppData = (ApplicationData) sci;
                        URL secUrl = ContentHelper.getDownloadURL(ch, secAppData);
                        String secFileName = secAppData.getFileName();
                        String secSize = (new Long(secAppData.getFileSize() / 1024L)).toString() + " KB";
                        String secFileOid = secAppData.getPersistInfo().getObjectIdentifier().getStringValue();
                        String[] secStr = new String[] { secUrl.toString(), secFileName, secSize, secFileOid };
                        secVec.add(secStr);
                    }
                }
            }
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        catch (PropertyVetoException e)
        {
            e.printStackTrace();
        }
        return secVec;
    }

    /**
     * @since 2007/05/28
     * @author kang ho chul
     * @param persist
     * @return
     * @throws Exception
     */
    public static long getLongPersistable(Persistable persist) throws Exception
    {
        return PersistenceHelper.getObjectIdentifier(persist).getId();
    }

    /**
     * @since 2006/07/25
     * @author sunny
     * @see
     * @return String - description: VR OID �� ��ȯ�Ѵ�
     * 
     */
    public static String getVROID(Persistable persistable)
    {
        if (persistable == null) return null;
        try
        {
            if (rf == null) rf = new ReferenceFactory();
            return rf.getReferenceString(persistable);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @since 2006/07/25
     * @author sunny
     * @see
     * @return String - description: VR OID �� ��ȯ�Ѵ�
     * 
     */
    public static String getVROID(String oid) throws WTException
    {
        try
        {
            Persistable obj = (Persistable) getPersistable(oid);
            if (obj == null)
                return null;
            else
                return getVROID(obj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new WTException(e);
        }
    }

    /**
     * oid���� ��ü�� �����Ѵ�.
     * 
     * @author Choi Seunghwan, swchoi@e3ps.com
     * @createDate 2007. 07. 24
     * @param oid
     * @return
     */
    public static Persistable getObject(String oid)
    {
        if (oid == null || "null".equals(oid)) return null;
        try
        {
            if (rf == null) rf = new ReferenceFactory();
            return rf.getReference(oid).getObject();
        }
        catch (WTException e)
        {
        	e.printStackTrace();
            //System.out.println("Error Message = "+ e.getMessage());
        }
        return null;
    }
}