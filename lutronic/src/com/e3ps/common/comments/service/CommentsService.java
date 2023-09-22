package com.e3ps.common.comments.service;

import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.doc.dto.DocumentDTO;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import wt.method.RemoteInterface;

@RemoteInterface
public interface CommentsService {

	public abstract void create(CommentsDTO dto) throws Exception;

	public abstract void modify(CommentsDTO dto) throws Exception;

	public abstract void delete(String oid) throws Exception;

}
