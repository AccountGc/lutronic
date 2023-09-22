package com.e3ps.common.comments.service;

import java.util.ArrayList;

import com.e3ps.common.comments.Comments;
import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.common.util.QuerySpecUtils;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class CommentsHelper {

	public static final CommentsService service = ServiceFactory.getService(CommentsService.class);
	public static final CommentsHelper manager = new CommentsHelper();

	/**
	 * 댓글 목록
	 */
	public ArrayList<CommentsDTO> list(Persistable per) throws Exception {
		ArrayList<CommentsDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Comments.class, true);
		QuerySpecUtils.toEquals(query, idx, Comments.class, "", per);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Comments comments = (Comments) obj[0];
			CommentsDTO dto = new CommentsDTO(comments);
			list.add(dto);
		}
		return list;
	}
}
