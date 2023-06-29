package com.e3ps.help.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.FileUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;

import wt.content.ApplicationData;
import wt.util.WTProperties;

@Controller
@RequestMapping("/help")
public class HelpController {

	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView();

		String menu = StringUtil.checkReplaceStr(request.getParameter("menu"), "menu1");
		String title = StringUtil.checkReplaceStr(request.getParameter("title"), "HelpDesk");
		String sWtHome = WTProperties.getLocalProperties().getProperty("wt.home", "");
		String viewName = "help:/help/index";
		String url = "";
		
		if("creoView".equals(menu)) {
			viewName = "help:/help/main";
			url = WebUtil.getHost() + "wtcore/jsp/wvs/download/download_cvmcad.jsp";
		}else if("wgm".equals(menu)) {
			viewName = "help:/help/main";
			url = WebUtil.getHost() + "install/jsp/UWGM.jsp";
		}else {
		
			File dir = new File(sWtHome + "\\codebase\\jsp\\help\\" + menu);
			
			if(!dir.exists()){
				dir.mkdirs();
			}
			
		    File file = null;
		    String filename = "";
		    ApplicationData data = null;
		    
		    if(dir.listFiles().length == 0){
		    }
	
		    List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		    
		    for(int i=0; i< dir.listFiles().length; i++){
		        file = dir.listFiles()[i];
		        if(file.isHidden())continue;
		        
		        filename = file.getName();
		        data = ApplicationData.newApplicationData(null);
		        data.setFileName(filename);
		        
		        Map<String,String> map = new HashMap<String,String>();
		        
		        map.put("icon", CommonUtil.getContentIconStr(data));
		        map.put("fileName", filename);
		        map.put("fileSize", FileUtil.getFileSizeStr(file.length()));
		        
		        list.add(map);
		    }
		    model.addObject("sFilePath", menu);
		    model.addObject("list", list);
		}
		
		model.setViewName(viewName);
		model.addObject("menu", menu);
		model.addObject("title", title);
		model.addObject("url", url);
		return model;
	}
}
