package com.e3ps.auth.service;

import java.util.Enumeration;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pds.ClassJoinCondition;
import wt.pds.QuerySpecStatementBuilder;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.e3ps.auth.AuthGroupLink;
import com.e3ps.auth.AuthUserLink;
import com.e3ps.auth.E3PSAuth;

public class StandardE3PSService extends StandardManager implements E3PSService {

	public static StandardE3PSService newStandardE3PSService() throws Exception {
		final StandardE3PSService instance = new StandardE3PSService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public boolean isAuth(final String _codeA, final String _codeB, final String _codeC) {
        //return true;
        return isMember(_codeA, _codeB, _codeC) || isGroupMember(_codeA, _codeB, _codeC);
    }
	
	/**
     * 현재 유저가 _codeA / _codeB / _codeC 권한을 갖는지 체크
     *
     * @param _codeA
     * @param _codeB
     * @param _codeC
     * @return
     */
	@Override
    public boolean isMember(final String _codeA, final String _codeB, final String _codeC) {
        boolean isMember = false;
        try {
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

            Class target = E3PSAuth.class;
            QuerySpec query = new QuerySpec();
            int idx = query.addClassList(target, true);
            int idx_link = query.addClassList(AuthUserLink.class, false);
            int idx_user = query.addClassList(WTUser.class, false);

            query.appendSelectAttribute("name", idx, false);

            QuerySpecStatementBuilder builder = (QuerySpecStatementBuilder) query.getStatementBuilder();
            ClassJoinCondition classjoincondition = new ClassJoinCondition(idx_link, "auth", idx);
            classjoincondition.setLinkOuterJoin(false);
            classjoincondition.setTargetOuterJoin(false);
            builder.appendJoin(classjoincondition);
            query = builder.getQuerySpec();
            //            SearchUtil.appendLinkRoleJoin(query, idx, idx_link, "auth");

            QuerySpecStatementBuilder builder2 = (QuerySpecStatementBuilder) query.getStatementBuilder();
            ClassJoinCondition classjoincondition2 = new ClassJoinCondition(idx_link, "user", idx_user);
            classjoincondition2.setLinkOuterJoin(false);
            classjoincondition2.setTargetOuterJoin(false);
            builder2.appendJoin(classjoincondition2);
            query = builder2.getQuerySpec();
            //            SearchUtil.appendLinkRoleJoin(query, idx_user, idx_link, "user");

            setWhereCode(query, target, idx, _codeA, _codeB, _codeC);

            if ( query.getConditionCount() > 0 ) {
                query.appendAnd();
            }
            query.appendWhere(new SearchCondition(WTUser.class, "thePersistInfo.theObjectIdentifier.id", "=", user
                    .getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_user });

            //            SearchUtil.appendEQUAL(query, WTUser.class, "thePersistInfo.theObjectIdentifier.id", user.getPersistInfo().getObjectIdentifier().getId(),
            //                                   idx_user);

            //            System.out.println("> 사용자쿼리 = " + query);
            QueryResult qr = PersistenceHelper.manager.find(query);
            if ( qr.size() > 0 ) {
                isMember = true;
            }
        }
        catch ( WTException e ) {
            e.printStackTrace();
        }
        return isMember;
    }
    
	@Override
    public boolean isGroupMember(final String _codeA, final String _codeB, final String _codeC) {
        boolean isMember = false;
        try {
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

            // 현 사용자의 객체와 그룹
            long[] arr_Group = null;
            try {
                arr_Group = new long[0];
                Enumeration enm = user.parentGroupObjects();
                while ( enm.hasMoreElements() ) {
                    WTGroup group = (WTGroup) enm.nextElement();

                    long[] temp = new long[arr_Group.length + 1];
                    System.arraycopy(arr_Group, 0, temp, 0, arr_Group.length);
                    temp[temp.length - 1] = group.getPersistInfo().getObjectIdentifier().getId();
                    arr_Group = temp;
                }
            }
            catch ( WTException e ) {
                e.printStackTrace();
            }

            // 속하는 그룹이 없는 경우
            if ( arr_Group.length == 0 ) {
                return false;
            }

            Class target = E3PSAuth.class;
            QuerySpec query = new QuerySpec();
            int idx = query.addClassList(target, true);
            int idx_link = query.addClassList(AuthGroupLink.class, false);
            int idx_group = query.addClassList(WTGroup.class, false);

            query.appendSelectAttribute("name", idx, false);

            QuerySpecStatementBuilder builder = (QuerySpecStatementBuilder) query.getStatementBuilder();
            ClassJoinCondition classjoincondition = new ClassJoinCondition(idx_link, "auth", idx);
            classjoincondition.setLinkOuterJoin(false);
            classjoincondition.setTargetOuterJoin(false);
            builder.appendJoin(classjoincondition);
            query = builder.getQuerySpec();
            //            SearchUtil.appendLinkRoleJoin(query, idx, idx_link, "auth");

            QuerySpecStatementBuilder builder2 = (QuerySpecStatementBuilder) query.getStatementBuilder();
            ClassJoinCondition classjoincondition2 = new ClassJoinCondition(idx_link, "group", idx_group);
            classjoincondition2.setLinkOuterJoin(false);
            classjoincondition2.setTargetOuterJoin(false);
            builder2.appendJoin(classjoincondition2);
            query = builder2.getQuerySpec();
            //            SearchUtil.appendLinkRoleJoin(query, idx_group, idx_link, "group");

            setWhereCode(query, target, idx, _codeA, _codeB, _codeC);

            query.appendAnd();
            SearchCondition sc = new SearchCondition(WTGroup.class, "thePersistInfo.theObjectIdentifier.id", arr_Group);
            query.appendWhere(sc, new int[] { idx_group });

            //            System.out.println("> 그룹쿼리 = " + query);
            QueryResult qr = PersistenceHelper.manager.find(query);
            if ( qr.size() > 0 ) {
                isMember = true;
            }
        }
        catch ( WTException e ) {
            e.printStackTrace();
        }
        return isMember;
    }
    
	private void setWhereCode(final QuerySpec _query, final Class _target, final int _idx, final String _codeA, final String _codeB, final String _codeC) throws QueryException {
		if (_query.getConditionCount() > 0) {
			_query.appendAnd();
		}
		_query.appendWhere(new SearchCondition(_target, "codeA", "=", _codeA),
				new int[] { _idx });
		// SearchUtil.appendEQUAL(_query, _target, "codeA", _codeA, _idx);

		_query.appendAnd();
		// codeB 조건
		_query.appendOpenParen();
		_query.appendWhere(new SearchCondition(_target, "codeB", "=", _codeB),
				new int[] { _idx });
		if (!"A".equals(_codeB)) {
			_query.appendOr();
			_query.appendWhere(new SearchCondition(_target, "codeB", "=", "A"),
					new int[] { _idx });
		}
		_query.appendCloseParen();
		_query.appendAnd();

		// codeC 조건
		_query.appendOpenParen();
		_query.appendWhere(new SearchCondition(_target, "codeC", "=", _codeC),
				new int[] { _idx });
		if (!"A".equals(_codeC)) {
			_query.appendOr();
			_query.appendWhere(new SearchCondition(_target, "codeC", "=", "A"),
					new int[] { _idx });
		}
		_query.appendCloseParen();
	}
}
