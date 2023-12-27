/*
 * @(#) EventManager.java  Create on 2005. 10. 19.
 * Copyright (c) e3ps. All rights reserverd
 */
package com.e3ps.common.util;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.views.View;
import wt.vc.views.ViewReference;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.e3ps.common.iba.IBAUtil;

public class EventVersionManager {
    public static EventVersionManager manager = new EventVersionManager();

    public void eventListener(Object _obj, String _event){ 
    	
    	if (!_event.equals("PRE_CHECKOUT")) {
    		
			if (_event.equals("POST_CHECKIN")) {
				if ((_obj instanceof EPMDocument)) {
					//System.out.println(":::::::::::::: EPMDocument	POST_CHECKIN ::::::::::::::::: " + _event);
				}else if ((_obj instanceof WTPart)) {
					//System.out.println(":::::::::::::: WTPart	POST_CHECKIN ::::::::::::::::: " + _event);
				}
			}
    	}else{
    		
    		if (_event.equals("POST_CHECKIN")) {
				if ((_obj instanceof EPMDocument)) {
					//System.out.println(":::::::::::::: EPMDocument	POST_CHECKIN ::::::::::::::::: " + _event);
				}else if ((_obj instanceof WTPart)) {
					//System.out.println(":::::::::::::: WTPart	POST_CHECKIN ::::::::::::::::: " + _event);
				}
			}
    	}
    	
    	if (_obj instanceof WTPart) {
            if (_event.equals("POST_STORE")) {	
            	//System.out.println(":::::::::::::: wtpart	POST_STORE ::::::::::::::::: " + _event);
                newVersionEvent((WTPart) _obj);
            } else if (_event.equals("PRE_DELETE")) {
                preDeleteEvent((WTPart) _obj);
            }
        } else if (_obj instanceof EPMDocument) {
        	if (_event.equals("POST_STORE")) {	
            	newVersionEvent((EPMDocument) _obj);
            	//System.out.println(":::::::::::::: EPMDocument	POST_STORE ::::::::::::::::: " + _event);
            }else if (_event.equals("PRE_DELETE")){
            	preDeleteEvent((EPMDocument) _obj); 
            }else if (_event.equals("PUBLISH_SUCCESSFUL")){
            	
            
            }
        	
        } else if (_obj instanceof WTDocument) {
            if (_event.equals("PRE_DELETE")) {
                preDeleteEvent((WTDocument) _obj);
            } else if (_event.equals("POST_STORE")) {
                newVersionEvent((WTDocument) _obj);
            }
        } 
    }


    /**
     * 버전을 정렬하기 위한 메소드... Engineering일경우는 그냥 Manufacturing일경우는 . 뒷자리로 정렬
     * 
     * @param version
     * @return
     */
    private String parseVersion(String version) {
        String temp = version;
        if (version.lastIndexOf(".") < 0) {
            if (version.length() != 2)
                temp = " " + version;
        }
        else {
            int i_ver = version.lastIndexOf(".");
            temp = version.substring(i_ver + 1, version.length());
            if (temp.length() != 2)
                temp = version.substring(0, i_ver + 1) + " " + temp;
            else
                temp = version;
        }
        return temp;
    }

    private TreeMap sortByVersion(Versioned part) {
        TreeMap listAll = new TreeMap();
        try {
            QueryResult qr = VersionControlHelper.service.allVersionsOf(part.getMaster());

            String tempViewName = null;
            if (part instanceof WTPart) {// 부품인 경우
                String viewName = ((WTPart) part).getViewName();
                if (viewName == null)
                    viewName = "not";

                while (qr.hasMoreElements()) {
                    WTPart tempPart = (WTPart) qr.nextElement();
                    tempViewName = tempPart.getViewName();
                    if (tempViewName == null)
                        tempViewName = "not";

                    if (viewName.equals(tempViewName))
                        listAll.put(parseVersion(tempPart.getVersionIdentifier().getValue()), tempPart);
                }
            }
            else {
                while (qr.hasMoreElements()) {
                    Versioned temp = (Versioned) qr.nextElement();
                    listAll.put(parseVersion(temp.getVersionIdentifier().getValue()), temp);
                }
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return listAll;
    }

    private void newVersionEvent(WTPart part) {
        try {	
            if (WorkInProgressHelper.isCheckedOut(part)) { return; }
            if (!VersionControlHelper.isLatestIteration(part)) { return; }
            String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();
            String oldVersion = "";
            String oview = "";
            String nview = "";
            //System.out.println(":::::::::: POST_STORE : " + part.getNumber());
            if (part.getView() != null) {
                nview = part.getView().getName();
            }
            
            TreeMap listAll = sortByVersion(part);

            WTPart oldpart = null;
            String partVer = parseVersion(version);

            if (listAll.size() > 0) {
                Iterator it = listAll.keySet().iterator();
                while (it.hasNext()) {
                    String _ver = (String) it.next();
                    if (partVer.equals(_ver))
                        break;
                    else
                        oldpart = (WTPart) listAll.get(_ver);
                }

                if (oldpart == null) {
                    IBAUtil.changeIBAValue(part, "LatestVersionFlag", "true", "string");
                    //System.out.println("#############		AAAAAAAAA");
                    return;
                }

                oldVersion = VersionControlHelper.getVersionIdentifier(oldpart).getSeries().getValue();
                if (oldpart.getView() != null) {
                    oview = oldpart.getView().getName();
                }
                if (oview.equals(nview) && (!version.equals(oldVersion))) { 
                    IBAUtil.changeIBAValue(part, "LatestVersionFlag", "true", "string");
                    //System.out.println("#############		bbbbbbbb");
                   
                    Vector list = getOldPartList(part);

                    for (int i = 0; i < list.size(); i++) {
                        WTPart pp = (WTPart) list.get(i);
                        IBAUtil.deleteIBA(pp, "LatestVersionFlag", "string");
                        //System.out.println("#############		ccccccccc");
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newVersionEvent(Versioned _versioned) {
        try {
            if (WorkInProgressHelper.isCheckedOut((Workable) _versioned)) { return; }
            if (!VersionControlHelper.isLatestIteration(_versioned)) { return; }

            String version = _versioned.getVersionIdentifier().getValue();
            //System.out.println("target Versioned : " + _versioned.getIdentity());
           
            String oldVersion = "";
            TreeMap listAll = sortByVersion(_versioned);

            Versioned oldVersionObj = null;
            String str_ver = parseVersion(version);
            if (listAll.size() > 0) {
                Iterator it = listAll.keySet().iterator();
                while (it.hasNext()) {
                    String _ver = (String) it.next();
                    if (str_ver.equals(_ver))
                        break;
                    else
                        oldVersionObj = (Versioned) listAll.get(_ver);
                }

                if (oldVersionObj == null) {
                    IBAUtil.changeIBAValue((IBAHolder) _versioned, "LatestVersionFlag", "true","string");
                    //System.out.println("#############		dddddddd");
                    return;
                }

                oldVersion = oldVersionObj.getVersionIdentifier().getValue();

                if (!version.equals(oldVersion)) { // 버전이 변경되었을 경우 New Version 일경우

                    IBAUtil.changeIBAValue((IBAHolder) _versioned, "LatestVersionFlag", "true","string");
                    //System.out.println("#############		eeeeeeee");

                    Vector list = getOldVersionList(_versioned);
                    for (int i = 0; i < list.size(); i++)
                    {
                        IBAUtil.deleteIBA((IBAHolder)list.get(i), "LatestVersionFlag","string");
                        //System.out.println("#############		ffffffffff");
                    }
                    return;
                }
            }
        } catch (Exception e)  {
            e.printStackTrace();
        }
    }

    private void preDeleteEvent(Versioned _versioned) {
        try {
            if (WorkInProgressHelper.isCheckedOut((Workable) _versioned)
                    && WorkInProgressHelper.isWorkingCopy((Workable) _versioned)) { return; }

            String version = parseVersion(_versioned.getVersionIdentifier().getValue());

            Versioned preVersion = null;

            TreeMap list = sortByVersion(_versioned);
            Iterator it = list.keySet().iterator();
            while (it.hasNext()) {
                String _ver = (String) it.next();
                if (version.equals(_ver))
                    break;
                else
                    preVersion = (Versioned) list.get(_ver);
            }

            if (preVersion != null)
                IBAUtil.changeIBAValue((IBAHolder) preVersion, "LatestVersionFlag", "true","string");
            //System.out.println("#############		preDeleteEvent");
        } catch (Exception e) {
            e.printStackTrace();
            new WTException(e.getMessage());
        }
    }

    private Vector getOldVersionList(Versioned _versioned) throws Exception {
        Class target = EPMDocument.class;
        String no = null;
        if (_versioned instanceof EPMDocument)
            no = ((EPMDocument) _versioned).getNumber();
        else {
            no = ((WTDocument) _versioned).getNumber();
            target = WTDocument.class;
        }
        String v = VersionControlHelper.getVersionIdentifier(_versioned).getSeries().getValue();

        QuerySpec spec = new QuerySpec();
        int idx = spec.addClassList(target, true);
        spec.appendWhere(new SearchCondition(target, "master>number", "=", no), new int[] { idx });
        spec.appendAnd();
        spec.appendWhere(new SearchCondition(target, "versionInfo.identifier.versionId", "<>", v), new int[] { idx });

        AttributeDefDefaultView aview = IBADefinitionHelper.service
                .getAttributeDefDefaultViewByPath("LatestVersionFlag");
        if (aview != null) {
            spec.appendAnd();
            int k = spec.appendClassList(StringValue.class, false);
            SearchCondition sc = new SearchCondition(new ClassAttribute(StringValue.class,
                    "theIBAHolderReference.key.id"), "=", new ClassAttribute(target,
                    "thePersistInfo.theObjectIdentifier.id"));
            sc.setFromIndicies(new int[] { k, idx }, 0);
            sc.setOuterJoin(0);
            spec.appendWhere(sc, new int[] { k, idx });
            spec.appendAnd();
            spec.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=", aview
                    .getHierarchyID()), new int[] { k });
            spec.appendAnd();
            spec.appendWhere(new SearchCondition(StringValue.class, "value", "=", "TRUE"), new int[] { k });
        }

        Vector list = new Vector();
        QueryResult rr = PersistenceHelper.manager.find(spec);
        while (rr.hasMoreElements()) {
            Object[] o = (Object[]) rr.nextElement();
            list.add(o[0]);
        }
        return list;
    }

    private Vector getOldPartList(WTPart part) throws Exception {
        String v = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();

        QuerySpec spec = new QuerySpec();
        int idx = spec.addClassList(WTPart.class, true);
        spec.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", part.getNumber()), new int[] { idx });
        spec.appendAnd();

        spec.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", "<>", v),
                         new int[] { idx });

        ViewReference vr = part.getView();
        if (vr != null) {
            View view = (View) vr.getObject();
            spec.appendAnd();
            spec.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx });
        }

        AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("LatestVersionFlag");
        if (aview != null) {
            spec.appendAnd();
            int k = spec.appendClassList(StringValue.class, false);
            SearchCondition sc = new SearchCondition(new ClassAttribute(StringValue.class,
                    "theIBAHolderReference.key.id"), "=", new ClassAttribute(WTPart.class,
                    "thePersistInfo.theObjectIdentifier.id"));
            sc.setFromIndicies(new int[] { k, idx }, 0);
            sc.setOuterJoin(0);
            spec.appendWhere(sc, new int[] { k, idx });
            spec.appendAnd();
            spec.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=", aview
                    .getHierarchyID()), new int[] { k });
            spec.appendAnd();
            spec.appendWhere(new SearchCondition(StringValue.class, "value", "=", "TRUE"), new int[] { k });
        }

        Vector list = new Vector();
        QueryResult rr = PersistenceHelper.manager.find(spec);
        while (rr.hasMoreElements()) {
            Object[] o = (Object[]) rr.nextElement();
            list.add(o[0]);
        }
        return list;
    }
    
    private void changePartNumber(WTPart part, String number){
        try{	
        	//System.out.println("###############				changePartNumber		######");
        	
        	String[] noArray = number.split("[.]");
        	
        	number = noArray[0];
        	
        	if(noArray.length > 2 ){
        		number = "";
        		for(int i=0; i< noArray.length; i++){
        			number += noArray[i];
        		}
        	}
        	WTPartMaster wtpartmaster = (WTPartMaster) part.getMaster();
        	wtpartmaster = WTPartHelper.service.changeWTPartMasterIdentity(wtpartmaster, null, number, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
