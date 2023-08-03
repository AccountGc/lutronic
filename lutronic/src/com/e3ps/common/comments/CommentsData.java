package com.e3ps.common.comments;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsData {
	private String oid;
	private int cDepth;
	private String groupId;
	private String content;
	private String creator;
	private String createDate;
	private String modifyDate;
	
	public CommentsData() {
		
	}
	
	public CommentsData(Comments com) {
		setOid(CommonUtil.getOIDString(com));
		setCDepth(com.getCDepth());
		setGroupId(com.getGroupId());
		setContent(com.getContent());
		setCreator(com.getOwner().getFullName());
		setCreateDate(DateUtil.getDateString(com.getCreateTimestamp(),"a"));
		setModifyDate(DateUtil.getDateString(com.getModifyTimestamp(),"a"));
	}
	
}
