package com.e3ps.common.comments.beans;

import java.util.ArrayList;

import com.e3ps.common.comments.Comments;
import com.e3ps.common.comments.service.CommentsHelper;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsDTO {

	private String oid;
	private String comment;
	private int depth;
	private String creator;
	private String createdDate;
	private ArrayList<CommentsDTO> reply = new ArrayList<CommentsDTO>();

	public CommentsDTO() {

	}

	public CommentsDTO(String oid) throws Exception {
		this((Comments) CommonUtil.getObject(oid));
	}

	public CommentsDTO(Comments comments) throws Exception {
		setOid(comments.getPersistInfo().getObjectIdentifier().getStringValue());
		setComment(comments.getComments());
		setDepth(comments.getDepth());
		setCreator(comments.getOwnership().getOwner().getFullName());
		setCreatedDate(comments.getCreateTimestamp().toString().substring(0, 16));
		setReply(CommentsHelper.manager.reply(comments));
	}
}
