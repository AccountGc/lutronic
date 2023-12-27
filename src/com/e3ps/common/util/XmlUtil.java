package com.e3ps.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import wt.util.WTProperties;

import com.e3ps.common.jdf.config.ConfigEx;
import com.e3ps.common.jdf.config.ConfigExImpl;

public class XmlUtil {

	
	
	public static Document getxmlPaseing(String filePath) throws Exception {
		
		
		File xmlFile = new File(filePath);
		
		if(!xmlFile.isFile()){
			 throw new Exception(filePath + " is not Exist");
		}
		Document document =getxmlPaseing(xmlFile);
		return document;
	}
	
	public static Document getxmlPaseing(File file) throws Exception{
		Document document = null;
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentbuilder = factory.newDocumentBuilder();
			document = documentbuilder.parse(file);
		}catch(Exception e){
			throw new Exception( file +"[e3ps.add.mrm.util.XMLHepler] xml Paseing Error :" +e.getMessage());
		}
		
		return document;
	}
	
	
	
	/**
	 * xml file path 
	 * @param file
	 * @return
	 */
	public static String getXMLFile(String fileType){
		String xmlFile ="";
		try{
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String wtHome = wtproperties.getProperty("wt.home");
			xmlFile = wtHome.concat(File.separator).concat("codebase").concat(File.separator)
						.concat("e3ps").concat(File.separator).concat("xmlquery").concat(File.separator).concat(fileType).concat(".xml");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return xmlFile;
		
	}
	
	public static void main(String[] args) {
		
		String filePath ="D:\\SpecCode.xml";
		
		//System.out.println("XML TEST");
		try {
			//XmlUtil.manager.getXmlQuery(filePath, "");
			File file = new File(filePath);
			Document document = getxmlPaseing(file);
			Element root = (Element) document.getFirstChild();
			NodeList lis = root.getChildNodes();
			//System.out.println("document = " + lis.getLength());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
