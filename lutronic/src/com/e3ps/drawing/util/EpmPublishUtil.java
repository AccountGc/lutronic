package com.e3ps.drawing.util;

import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.beans.EpmUtil;
import com.ptc.wvs.common.util.WVSProperties;
import com.ptc.wvs.server.publish.Publish;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigSpec;

public class EpmPublishUtil {

	private static Method filterEPMDocumentPublishMethod;

	static {
		try {
			String s2 = WVSProperties.getPropertyValue("publish.service.filterepmdocumentpublishmethod");
			if (s2 != null) {
				StringTokenizer stringtokenizer = new StringTokenizer(s2.trim(), "/");
				if (stringtokenizer.countTokens() == 2) {
					Class class1 = Class.forName(stringtokenizer.nextToken());
					Class aclass[] = { wt.epm.EPMDocument.class };
					filterEPMDocumentPublishMethod = class1.getMethod(stringtokenizer.nextToken(), aclass);

				}
			}
		} catch (Exception ex) {

		}
	}

	public static void publish(EPMDocument epm) throws Exception {

		boolean flag1 = true;

		if (filterEPMDocumentPublishMethod != null)
			try {
				Object aobj[] = { epm };
				flag1 = ((Boolean) filterEPMDocumentPublishMethod.invoke(null, aobj)).booleanValue();
			} catch (Exception exception) {
				exception.printStackTrace();
				flag1 = true;
			}

		String fileName = "";
		QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem primaryFile = (ContentItem) result.nextElement();
			fileName = ((ApplicationData) primaryFile).getFileName();
		}

		String authoringType = EpmUtil.getExtension(fileName).toUpperCase();

		if (authoringType.equals("DSN") || authoringType.equals("BRD") || authoringType.equals("ZIP")
				|| authoringType.equals("PDF")) {
			flag1 = false;
		}
		// System.out.println("Publish Check ::: flag1"+ flag1+"\tfileName="+fileName);
		if (flag1) {
			// System.out.println("Publish Check222222222222222222 ::: flag1"+
			// flag1+"\tfileName="+fileName);
			ConfigSpec configspec = null;
			ConfigSpec configspec1 = null;
			Publish.doPublish(false, true, epm, configspec, null, false, null, null, 1, null, 2, null);
		}

	}

	/**
	 * 입력한 기간에 등록된 도면의 Publishing (변환) 요청
	 * 
	 * @param predate
	 * @param postdate
	 * @param islastversion
	 */
	public static void multiPublish(String predate, String postdate, String islastversion) {
		try {
			QuerySpec qs = new QuerySpec();
			int idx = qs.appendClassList(EPMDocument.class, true);

			if (predate.length() > 0) {
				if (qs.getConditionCount() > 0) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[] { idx });
			}
			if (postdate.length() > 0) {
				if (qs.getConditionCount() > 0) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[] { idx });
			}

			// 최신 이터레이션
			if (qs.getConditionCount() > 0) {
				qs.appendAnd();
			}
			qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idx });
			// 버전검색
			if (!StringUtil.checkString(islastversion))
				islastversion = "true";
			if ("true".equals(islastversion)) {
				QuerySpecUtils.toLatest(qs, idx, EPMDocument.class);
			}

			QueryResult qr = PersistenceHelper.manager.find(qs);

			if (qr != null) {
				while (qr.hasMoreElements()) {
					Object[] obj = (Object[]) qr.nextElement();
					EPMDocument epm = (EPMDocument) obj[0];
					EpmPublishUtil.publish(epm);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
