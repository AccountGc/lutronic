package com.e3ps.workspace.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.workspace.ApprovalUserLine;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class WorkspaceHelper {

	public static final WorkspaceService service = ServiceFactory.getService(WorkspaceService.class);
	public static final WorkspaceHelper manager = new WorkspaceHelper();

	/*
	 * 라이프사이클 관련 상태값
	 */
	public static final String STATE_APPROVAL_APPROVING = "승인중";
	public static final String STATE_APPROVAL_REJECT = "반려됨";
	public static final String STATE_APPROVAL_COMPLETE = "결재완료";

	public Map<String, Object> complete(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> approval(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> receive(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> progress(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> reject(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 개인결재선 로드 함수
	 */
	public Map<String, Object> loadLine(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		Map<String, Object> result = new HashMap<>();
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		WTUser sessionUser = CommonUtil.sessionUser();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalUserLine.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalUserLine.class, "ownership.owner.key.id", sessionUser);
		QuerySpecUtils.toLikeAnd(query, idx, ApprovalUserLine.class, ApprovalUserLine.NAME, name);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalUserLine.class, ApprovalUserLine.NAME, false);
		QueryResult rs = PersistenceHelper.manager.find(query);
		while (rs.hasMoreElements()) {
			Object[] obj = (Object[]) rs.nextElement();
			ApprovalUserLine line = (ApprovalUserLine) obj[0];
			Map<String, Object> map = new HashMap<>();
			map.put("oid", line.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", line.getName());
			map.put("favorite", line.getFavorite());
			list.add(map);
		}
		result.put("list", list);
		return result;
	}

	/**
	 * 개인결재선 저장시 검증 함수
	 */
	public Map<String, Object> validate(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		Map<String, Object> result = new HashMap<>();
		WTUser sessionUser = CommonUtil.sessionUser();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalUserLine.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalUserLine.class, ApprovalUserLine.NAME, name);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalUserLine.class, "ownership.owner.key.id", sessionUser);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.size() > 0) {
			result.put("validate", true);
			result.put("msg", "중복된 개인결재선 이름입니다.");
			return result;
		}
		result.put("validate", false);
		return result;
	}

	/**
	 * 개인결재선 즐겨찾기 불러오는 함수
	 */
	public Map<String, Object> loadFavorite() throws Exception {
		Map<String, Object> map = new HashMap<>();

		WTUser sessionUser = CommonUtil.sessionUser();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalUserLine.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalUserLine.class, "ownership.owner.key.id", sessionUser);
		QuerySpecUtils.toBooleanAnd(query, idx, ApprovalUserLine.class, ApprovalUserLine.FAVORITE, true);
		QueryResult rs = PersistenceHelper.manager.find(query);

		ArrayList<PeopleDTO> approval = new ArrayList<>();
		ArrayList<PeopleDTO> agree = new ArrayList<>();
		ArrayList<PeopleDTO> receive = new ArrayList<>();

		if (rs.hasMoreElements()) {
			Object[] obj = (Object[]) rs.nextElement();
			ApprovalUserLine line = (ApprovalUserLine) obj[0];
			ArrayList<String> approvalList = (ArrayList<String>) line.getApprovalList();
			for (String oid : approvalList) {
				People p = (People) CommonUtil.getObject(oid);
				PeopleDTO dto = new PeopleDTO(p);
				approval.add(dto);
			}
			ArrayList<String> agreeList = (ArrayList<String>) line.getAgreeList();
			for (String oid : agreeList) {
				People p = (People) CommonUtil.getObject(oid);
				PeopleDTO dto = new PeopleDTO(p);
				agree.add(dto);
			}
			ArrayList<String> receiveList = (ArrayList<String>) line.getReceiveList();
			for (String oid : receiveList) {
				People p = (People) CommonUtil.getObject(oid);
				PeopleDTO dto = new PeopleDTO(p);
				receive.add(dto);
			}
		}

		map.put("approval", approval);
		map.put("agree", agree);
		map.put("receive", receive);
		return map;
	}

	/**
	 * 개인결재선 불러오는 함수
	 */
	public Map<String, Object> loadFavorite(String _oid) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<PeopleDTO> approval = new ArrayList<>();
		ArrayList<PeopleDTO> agree = new ArrayList<>();
		ArrayList<PeopleDTO> receive = new ArrayList<>();
		ApprovalUserLine line = (ApprovalUserLine) CommonUtil.getObject(_oid);
		ArrayList<String> approvalList = (ArrayList<String>) line.getApprovalList();
		for (String oid : approvalList) {
			People p = (People) CommonUtil.getObject(oid);
			PeopleDTO dto = new PeopleDTO(p);
			approval.add(dto);
		}
		ArrayList<String> agreeList = (ArrayList<String>) line.getAgreeList();
		for (String oid : agreeList) {
			People p = (People) CommonUtil.getObject(oid);
			PeopleDTO dto = new PeopleDTO(p);
			agree.add(dto);
		}
		ArrayList<String> receiveList = (ArrayList<String>) line.getReceiveList();
		for (String oid : receiveList) {
			People p = (People) CommonUtil.getObject(oid);
			PeopleDTO dto = new PeopleDTO(p);
			receive.add(dto);
		}
		map.put("approval", approval);
		map.put("agree", agree);
		map.put("receive", receive);
		return map;
	}
}
