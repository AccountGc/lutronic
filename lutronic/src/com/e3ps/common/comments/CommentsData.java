package com.e3ps.common.comments;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsData {
	private String oid;
	private String id;
	private String comments;
	private int cNum;
	private int cStep;
	private String oPerson;
	private String deleteYN;
	
	private String creator;
	private String createDate;
	private String modifyDate;
	
	public CommentsData() {
		
	}
	
	public CommentsData(Comments com) {
		setOid(CommonUtil.getOIDString(com));
		setId(com.getOwner().getName());
		setComments(com.getComments());
		setCNum(com.getCNum());
		setCStep(com.getCStep());
		setDeleteYN(com.getDeleteYN());
		setOPerson(com.getOPerson());
		setCreator(com.getOwner().getFullName());
		setCreateDate(DateUtil.getDateString(com.getCreateTimestamp(),"a"));
		setModifyDate(DateUtil.getDateString(com.getModifyTimestamp(),"a"));
	}
}
