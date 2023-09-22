package com.e3ps.common.comments.service;

import com.e3ps.common.comments.Comments;
import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.doc.dto.DocumentDTO;

import wt.fc.PersistenceHelper;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardCommentsService extends StandardManager implements CommentsService {

	public static StandardCommentsService newStandardCommentsService() throws WTException {
		StandardCommentsService instance = new StandardCommentsService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(CommentsDTO dto) throws Exception {
		String comment = dto.getComment();
		int depth = dto.getDepth();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			Comments comments = Comments.newComments();
			comments.setComments(comment);
			comments.setOwnership(ownership);
			comments.setDepth(depth);

			PersistenceHelper.manager.save(comments);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void modify(CommentsDTO dto) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Comments comments = (Comments) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(comments);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
