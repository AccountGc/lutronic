package com.e3ps.groupware.notice.beans;

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
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.groupware.notice.Notice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeData {
	
	public String oid;
	public String title;
	public String creator;
	public String createDate;
	public String contents;
	public String vContents;
	public String checked;
	public int count;
	public boolean isPopup;
	
	public boolean auth = false;
	
	public NoticeData(Notice notice) throws Exception {
		setOid(notice.getPersistInfo().getObjectIdentifier().toString());
		setTitle(notice.getTitle());
		setCreator(notice.getOwner().getFullName());
		setCreateDate(DateUtil.getDateString(notice.getCreateTimestamp(),"a"));
		setContents(notice.getContents());
//		setvContents(WebUtil.getHtml(notice.getContents()));
		setCount(notice.getCount());
		setPopup(notice.isIsPopup());
		
		if(notice.isIsPopup()){
			this.checked="checked";
		}
		
		setAuth((E3PSAuthHelper.service.isAuth("WS", "공지사항", E3PSAuthFlag.CREATE) || com.e3ps.common.util.CommonUtil.isAdmin()));
		
	}
}
