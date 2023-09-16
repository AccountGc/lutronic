package com.e3ps.groupware.notice.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.util.WTProperties;

import com.e3ps.auth.beans.E3PSAuthFlag;
import com.e3ps.auth.service.E3PSAuthHelper;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.groupware.notice.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeData {

	public String oid;
	public String title;
	public String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createDate;
	public String contents;
	public int count;
	private String secondary;

	public NoticeData() {

	}

	public NoticeData(Notice notice) throws Exception {
		setOid(notice.getPersistInfo().getObjectIdentifier().toString());
		setTitle(notice.getTitle());
		setCreator(notice.getOwner().getFullName());
		setCreateDate(notice.getCreateTimestamp());
		setContents(notice.getContents());
		setCount(notice.getCount());
		setSecondary(AUIGridUtil.secondary(notice));
	}
}
