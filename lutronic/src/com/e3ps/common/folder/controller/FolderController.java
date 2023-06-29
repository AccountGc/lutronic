package com.e3ps.common.folder.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.util.WTException;

import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;

@Controller
@RequestMapping("/folder")
public class FolderController {
	
	/**	메뉴 하단에 보이는 폴더 트리구조
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/treeFolder")
	public ModelAndView treeFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/common/treeFolder");
		
		String folder = request.getParameter("folder");
		
		if(folder == null) {
			folder = "/Default";
		}
		
		Folder cadd = FolderHelper.service.getFolder(folder, WCUtil.getWTContainerRef());
		
		try {
			JSONObject json = CommonFolderHelper.service.newGetTrees(cadd);
			model.addObject("treeString", json.toJSONString());
		} catch(WTException e) {
			e.printStackTrace();
		}
		
		return model;
	}
	
	/**	수정시 보이는 폴더 트리구조
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/include_FolderSelect")
	public ModelAndView include_FolderSelect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("include:/common/include_FolderSelect");
		
		String folder = request.getParameter("root");
		String paramName = StringUtil.checkReplaceStr(request.getParameter("paramName"), "fid");
		
		if(folder == null) {
			folder = "/Default";
		}
		
		Folder cadd = FolderHelper.service.getFolder(folder, WCUtil.getWTContainerRef());
		
		HashMap hash = new HashMap();
		hash.put(0,"0");

		ArrayList flist = CommonFolderHelper.service.getFolderDTree(cadd);
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("id", cadd.getPersistInfo().getObjectIdentifier().getId());
		map.put("level", -1);
		map.put("name", cadd.getName());
		map.put("oid", cadd.getPersistInfo().getObjectIdentifier().toString());
		map.put("location", FolderHelper.getFolderPath(cadd));
		map.put("isLast", false);
		map.put("s1", "");
		
		list.add(map);
		ReferenceFactory rf = new ReferenceFactory();
		for(int i=0; i< flist.size(); i++){
			String[] s = (String[])flist.get(i);
			int level = Integer.parseInt(s[4]);
			
			SubFolder subFolder = (SubFolder)rf.getReference(s[2]).getObject();
			String sublocation = subFolder.getLocation() + "/" + subFolder.getName();
			String parent = (String)hash.get((level-1));
			hash.put(level, Integer.toString(i+1));
			
			boolean isLast = true;
			/*
			if(flist.size()<=i+1){
				isLast = true;
			}else{
				String[] s2 = (String[])flist.get(i+1);
				int level2 = Integer.parseInt(s2[0]);

				if(level2<=level){
					isLast = true;
				}
			}
			if(isLast){
				name = "<font color=darkred>"+s[3]+"</font>";
			}
			*/
			String name = s[3];
			
			map = new HashMap<String,Object>();
			map.put("id", subFolder.getPersistInfo().getObjectIdentifier().getId());
			map.put("level", subFolder.getParentFolder().getObjectId().getId());
			map.put("name", name);
			map.put("oid", s[2]);
			map.put("location", sublocation);
			map.put("isLast", isLast);
			map.put("s1", s[1]);
			
			list.add(map);
		}
		model.addObject("list", list);
		
		String location = request.getParameter("folder");
		Folder nowLoc = null;
		if(location == null) {
			nowLoc = cadd;
		}else {
			nowLoc = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
		}
		
		model.addObject("location", FolderHelper.getFolderPath(nowLoc));
		model.addObject("oid", nowLoc.getPersistInfo().getObjectIdentifier().toString());
		model.addObject("paramName", paramName);
		
		
		return model;
	}
	
	@ResponseBody
	@RequestMapping("/getNextTree")
	public List<Map<String,Object>> getNextTree(HttpServletRequest request, HttpServletResponse response){
		String oid = request.getParameter("oid");
		//System.out.println("oid ");
		SubFolder folder = (SubFolder)CommonUtil.getObject(oid);
		List<Map<String,Object>> result = null;
		try {
			result =  CommonFolderHelper.service.getNextTree(folder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
