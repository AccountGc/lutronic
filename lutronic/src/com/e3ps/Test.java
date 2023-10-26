package com.e3ps;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.etc.service.EtcHelper;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.rohs.ROHSAttr;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.workspace.ApprovalLine;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {
		
		String type = "pathological";

		ArrayList<NumberCode> preserationList = NumberCodeHelper.manager.getArrayCodeList("PRESERATION");
		ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
		ArrayList<NumberCode> modelList = NumberCodeHelper.manager.getArrayCodeList("MODEL");
		JSONArray docTypeList = DocumentHelper.manager.toJson();
		String location = EtcHelper.manager.toLocation(type);
		ModelAndView model = new ModelAndView();
		model.addObject("location", location);
		model.addObject("preserationList", preserationList);
		model.addObject("deptcodeList", deptcodeList);
		model.addObject("modelList", modelList);
		model.addObject("docTypeList", docTypeList);
		model.setViewName("/extcore/jsp/document/etc/etc-list.jsp");

	}
}