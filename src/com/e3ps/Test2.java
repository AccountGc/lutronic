package com.e3ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.ptc.windchill.cadx.remove.removeResource;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.FileUtil;

public class Test2 {

	public static void main(String[] args) throws Exception {

		String s = "ASDASD";
		String name = "pdf_dafadf_drw.dxf";
		String ext = FileUtil.getExtension(name);
		name = name.replace("." + ext, "").replace("step_", "").replace("_prt", "").replace("_asm", "")
				.replace("pdf_", "").replace("_drw", "") + "_" + s + "." + ext;
		System.out.println(name);
	}
}