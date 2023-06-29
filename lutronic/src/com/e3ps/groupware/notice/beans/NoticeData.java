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
import com.e3ps.common.web.WebUtil;
import com.e3ps.groupware.notice.Notice;

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
		this.oid = notice.getPersistInfo().getObjectIdentifier().toString();
		this.title = notice.getTitle();
		this.creator = notice.getOwner().getFullName();
		this.createDate = notice.getPersistInfo().getCreateStamp().toString().substring(0, 16);
		this.contents = notice.getContents();
		this.vContents = WebUtil.getHtml(notice.getContents());
		this.count = notice.getCount();
		this.isPopup = notice.isIsPopup();
		if(this.isPopup){
			this.checked="checked";
		}
		
		
		this.auth = (E3PSAuthHelper.service.isAuth("WS", "공지사항", E3PSAuthFlag.CREATE) || com.e3ps.common.util.CommonUtil.isAdmin());
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getvContents() {
		return vContents;
	}

	public void setvContents(String vContents) {
		this.vContents = vContents;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isPopup() {
		return isPopup;
	}

	public void setPopup(boolean isPopup) {
		this.isPopup = isPopup;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}
	
	
}
