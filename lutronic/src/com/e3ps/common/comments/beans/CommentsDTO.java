package com.e3ps.common.comments.beans;

import com.e3ps.common.comments.Comments;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsDTO {

	private String oid;
	private String comment;
	private int depth;

	public CommentsDTO() {

	}

	public CommentsDTO(String oid) throws Exception {
		this((Comments) CommonUtil.getObject(oid));
	}

	public CommentsDTO(Comments comments) throws Exception {
		setOid(comments.getPersistInfo().getObjectIdentifier().getStringValue());
		setComment(comments.getComments());
		setDepth(comments.getDepth());
	}
}
