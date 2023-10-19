package com.e3ps;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.rohs.ROHSAttr;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.workspace.ApprovalLine;

import wt.content.ApplicationData;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {

		ApprovalLine line = (ApprovalLine) CommonUtil.getObject("com.e3ps.workspace.ApprovalLine:311110");
		System.out.println(line.getType());

	}
}