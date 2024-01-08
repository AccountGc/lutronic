package com.e3ps.part.dto;

import java.util.ArrayList;

import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.common.comments.service.CommentsHelper;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;
import wt.part.WTPart;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

@Getter
@Setter
public class PartDTO {

	private String oid;
	private String number;
	private String name;
	private String location;
	private String version;
	private String remarks;
	private String state;
	private String creator;
	private String createDate;
	private String modifier;
	private String modifyDate;
	private String ecoNo;
	private boolean isLatest;

	private String epmOid;
	private String viewName;

	// 댓글
	private ArrayList<CommentsDTO> comments = new ArrayList<CommentsDTO>();

	public PartDTO(String oid) throws Exception {
		this((WTPart) CommonUtil.getObject(oid));
	}

	public PartDTO(WTPart part) throws Exception {
		setOid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(part.getNumber());
		setName(part.getName());
		setLocation(part.getLocation());
		setVersion(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
//		setRemark((String) map.get(AttributeKey.IBAKey.IBA_REMARKS));//
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorFullName());
		setCreateDate(part.getCreateTimestamp().toString().substring(0, 10));
		setModifier(part.getModifierFullName());
		setModifyDate(part.getModifyTimestamp().toString().substring(0, 10));
		setComments(CommentsHelper.manager.comments(part));
		setLatest(CommonUtil.isLatestVersion(part));
		View view = ViewHelper.getView(part);
		setViewName(view == null ? "" : view.getName());
	}
}
