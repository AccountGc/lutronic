package com.e3ps.common.util;

import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ContentRoleType;
import wt.representation.Representable;

public class ThumbnailUtil {

	private ThumbnailUtil() {

	}

	/**
	 * 큰 썸네일
	 */
	public static String thumbnail(String oid) throws Exception {
		Representable representable = (Representable) CommonUtil.getObject(oid);
		return thumbnail(representable);
	}

	/**
	 * 큰 썸네일
	 */
	public static String thumbnail(Representable per) throws Exception {
		Representable thum = (Representable) per;
		String thumnail = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(thum),
				ContentRoleType.THUMBNAIL);
		return thumnail;
	}

	/**
	 * 작은 썸네일
	 */
	public static String thumbnailSmall(String oid) throws Exception {
		Representable representable = (Representable) CommonUtil.getObject(oid);
		return thumbnailSmall(representable);
	}

	/**
	 * 작은 썸네일
	 */
	public static String thumbnailSmall(Representable per) throws Exception {
		Representable thum = (Representable) per;
		String thumnail = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(thum),
				ContentRoleType.THUMBNAIL_SMALL);
		return thumnail;
	}
}
