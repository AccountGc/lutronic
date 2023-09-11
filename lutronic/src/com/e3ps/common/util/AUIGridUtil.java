package com.e3ps.common.util;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.fc.QueryResult;
import wt.util.FileUtil;

public class AUIGridUtil {

	private AUIGridUtil() {

	}

	public static String primary(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
//			String icon = getAUIGridFileIcon(ext);
			String icon = "";
			String url = "/Windchill/eSolution/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue();
			template += "<a href=" + url + "><img src=" + icon + " style='position: relative; top: 2px;'></a>";
		}
		return template;
	}
}
