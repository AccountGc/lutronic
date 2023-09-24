package com.e3ps.common.comments.service;

import java.util.ArrayList;

import com.e3ps.common.comments.Comments;
import com.e3ps.common.comments.ReplyCommentsLink;
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
	 * 댓글 목록 - 객체와 연관된
	 */
	public ArrayList<CommentsDTO> comments(Persistable per) throws Exception {
		ArrayList<CommentsDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Comments.class, true);
		QuerySpecUtils.toEquals(query, idx, Comments.class, "persistReference.key.id", per);
		QuerySpecUtils.toEqualsAnd(query, idx, Comments.class, Comments.DEPTH, 0);
		QuerySpecUtils.toOrderBy(query, idx, Comments.class, Comments.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Comments comments = (Comments) obj[0];
			CommentsDTO dto = new CommentsDTO(comments);
			list.add(dto);
		}
		return list;
	}

	/**
	 * 답글 리스트
	 */
	public ArrayList<CommentsDTO> reply(Comments comments) throws Exception {
		ArrayList<CommentsDTO> list = new ArrayList<CommentsDTO>();
		QueryResult result = PersistenceHelper.manager.navigate(comments, "reply", ReplyCommentsLink.class);
		while (result.hasMoreElements()) {
			Comments reply = (Comments) result.nextElement();
			CommentsDTO dto = new CommentsDTO(reply);
			list.add(dto);
			reply(list, reply);
		}
		return list;
	}

	/**
	 * 댓글 재귀함수
	 */
	private void reply(ArrayList<CommentsDTO> list, Comments comments) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(comments, "reply", ReplyCommentsLink.class);
		while (result.hasMoreElements()) {
			Comments reply = (Comments) result.nextElement();
			CommentsDTO dto = new CommentsDTO(reply);
			list.add(dto);
			reply(list, reply);
		}
	}
}
