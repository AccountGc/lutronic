package com.e3ps.test;

import java.util.ArrayList;
import java.util.List;

import wt.part.WTPart;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;

public class EndItemTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			String oid = "wt.part.WTPart:8709497";
			WTPart part = (WTPart)CommonUtil.getObject(oid);
			List<WTPart> partList = new ArrayList<WTPart>();
			partList = PartSearchHelper.service.getPartEndItem(part, partList);
			for(WTPart pPart : partList){
				///System.out.println("pPart =" + pPart.getNumber());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static List<WTPart> getPartEndItem(WTPart part,List<WTPart> partList) throws Exception{
		BomBroker broker = new BomBroker();
		ArrayList list = new ArrayList();
		
		PartTreeData root = broker.getTree(part, false, null);
		broker.setHtmlForm(root, list);
		
		//System.out.println(part.getNumber()+" : list.size() =" + list.size());
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				PartTreeData data = (PartTreeData) list.get(i);
				/*
				if (data.children.size() > 0) {
					continue;
				}
				*/
				WTPart endPart = data.part;
				if(endPart.getNumber().equals(part.getNumber())){
					continue;
				}
				
				String partNumter = endPart.getNumber();
				
				if(PartUtil.isChange(partNumter)){
					continue;
				}
				//System.out.println("partNumter =" + partNumter);
				if(PartUtil.completeProductCheck(partNumter)){
					if(!partList.contains(endPart)){
						partList.add(endPart);
					}
				}
			}
		}
		
		return partList;
		
		
	}

}
