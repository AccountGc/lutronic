package com.e3ps.common.comments.service;

import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.doc.dto.DocumentDTO;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import wt.method.RemoteInterface;

@RemoteInterface
public interface CommentsService {

	/**
	 * 댓글 등록
	 */
	public abstract void create(CommentsDTO dto) throws Exception;

	/**
	 * 댓글 수정
	 */
	public abstract void modify(CommentsDTO dto) throws Exception;

	/**
	 * 댓글 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * 답글 등록
	 */
	public abstract void reply(CommentsDTO dto) throws Exception;

}
