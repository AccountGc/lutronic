package com.e3ps.change.service;

import java.io.StringWriter;
import java.util.Date;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOEul;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.WCUtil;

import wt.clients.folder.FolderTaskLogic;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.xml.jaxp.JAXPFactories;
import wt.vc.baseline.ManagedBaseline;

@SuppressWarnings("serial")
public class StandardEulService extends StandardManager implements EulService {
	
	public static StandardEulService newStandardEulService() throws WTException {
		final StandardEulService instance = new StandardEulService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public void completeEBOM(EChangeOrder eco) throws Exception{
		
		Vector<EOEul> eulList =getEoEul(eco);
		//System.out.println(" eulList " + eulList.size());
		for(int i = 0 ; i<eulList.size() ; i++){
			//BEContext.saveAction(eulList.get(i));
			EulHelper.service.createBaseline(eulList.get(i));
		}
		
	}
	
	private String document2String(Document document) {
		TransformerFactory transformerfactory = new JAXPFactories()
				.getTransformerFactory();
		Transformer transformer = null;
		try {
			transformer = transformerfactory.newTransformer();
		} catch (TransformerConfigurationException tcEx) {
			//System.out.println("TransformerConfigurationException: " + tcEx.getMessage());
			tcEx.printStackTrace();
		}

		try {
			DOMSource domsource = new DOMSource(document);
			StreamResult streamresult = new StreamResult(new StringWriter());
			transformer.transform(domsource, streamresult);
			StringBuffer stringbuffer = new StringBuffer(streamresult
					.getWriter().toString());
			return stringbuffer.toString();
		} catch (TransformerException tEx) {
			//System.out.println("TransformerException: " + tEx.getMessage());
			tEx.printStackTrace();
		}

		return null;
	}
	
	@Override
	public Vector<EOEul> getEoEul(EChangeOrder eco){
		Vector<EOEul> vec = new Vector();
		if( eco == null) return vec;
		try{
			QuerySpec qs  = new QuerySpec();
			int eulInt = qs.addClassList(EOEul.class, true);
			
			qs.appendWhere(new SearchCondition(EOEul.class,"ecoReference.key.id",SearchCondition.EQUAL,CommonUtil.getOIDLongValue(eco)),new int[]{eulInt});
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			//System.out.println(qs);
			//System.out.println( "qr = " +qr.size());
			while(qr.hasMoreElements()){
				
				Object[] oo = (Object[])qr.nextElement();
				EOEul eul = (EOEul) oo[0];
				vec.add(eul);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vec;
	}
	
	@Override
	public Vector<String> getEoEulTopTopAssy(EChangeOrder eco){
		Vector<String> vec = new Vector();
		if( eco == null) return vec;
		try{
			QuerySpec qs  = new QuerySpec();
			int eulInt = qs.addClassList(EOEul.class, true);
			
			qs.appendWhere(new SearchCondition(EOEul.class,"ecoReference.key.id",SearchCondition.EQUAL,CommonUtil.getOIDLongValue(eco)),new int[]{eulInt});
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			//System.out.println(qs);
			//System.out.println( "qr = " +qr.size());
			while(qr.hasMoreElements()){
				
				Object[] oo = (Object[])qr.nextElement();
				EOEul eul = (EOEul) oo[0];
				WTPart part = (WTPart)CommonUtil.getObject(eul.getTopAssyOid());
				vec.add(part.getNumber());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return vec;
	}

	@Override
	public ManagedBaseline createBaseline(EOEul eulb) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ManagedBaseline createBaseline(WTPart wtpart, EOEul eulb) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
