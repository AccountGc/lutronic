package com.e3ps.workspace.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.ECOChange;
import com.e3ps.common.mail.MailUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.MailUser;
import com.e3ps.org.MailWTobjectLink;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.workspace.AppPerLink;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.ApprovalUserLine;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.PersistMasterLink;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.WorkDataMailUserLink;
import com.e3ps.workspace.column.ApprovalLineColumn;
import com.e3ps.workspace.dto.ApprovalLineDTO;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class WorkspaceHelper {

	public static final WorkspaceService service = ServiceFactory.getService(WorkspaceService.class);
	public static final WorkspaceHelper manager = new WorkspaceHelper();

	// # ECR : 작업중-일괄결재-승인중-승인됨-반려됨-재작업-폐기

	/*
	 * 마스터 라인 상태값
	 */
	public static final String STATE_MASTER_APPROVAL_APPROVING = "승인중";
	public static final String STATE_MASTER_APPROVAL_REJECT = "반려됨";
	public static final String STATE_MASTER_APPROVAL_COMPLETE = "결재완료";

	/**
	 * 기안 라인 상태
	 */
	public static final String STATE_SUBMIT_READY = "기안전";
	public static final String STATE_SUBMIT_COMPLETE = "기안완료";

	/*
	 * 결재라인 상태값
	 */
	public static final String STATE_APPROVAL_READY = "대기중";
	public static final String STATE_APPROVAL_APPROVING = "승인중";
	public static final String STATE_APPROVAL_COMPLETE = "결재완료";
	public static final String STATE_APPROVAL_REJECT = "반려됨";

	/**
	 * 합의 라인 상태값 상수
	 */
	public static final String STATE_AGREE_READY = "대기중";
	public static final String STATE_AGREE_START = "합의중";
	public static final String STATE_AGREE_COMPLETE = "합의완료";
	public static final String STATE_AGREE_REJECT = "합의반려";

	/**
	 * 수신 라인 상태값 상수
	 */
	public static final String STATE_RECEIVE_READY = "수신대기중";
	public static final String STATE_RECEIVE_START = "수신확인중";
	public static final String STATE_RECEIVE_COMPLETE = "수신완료";
	public static final String STATE_RECEIVE_REJECT = "수신반려";

	/*
	 * 결재자 역할들
	 */
	public static final String WORKING_SUBMITTER = "기안자";
	public static final String WORKING_APPROVAL = "승인자";
	public static final String WORKING_AGREE = "합의자";
	public static final String WORKING_RECEIVE = "수신자";

	/*
	 * 결재라인 종류
	 */
	public static final String SUBMIT_LINE = "기안";
	public static final String AGREE_LINE = "합의";
	public static final String APPROVAL_LINE = "결재";
	public static final String RECEIVE_LINE = "수신";

	/**
	 * 완료함
	 */
	public Map<String, Object> complete(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineColumn> list = new ArrayList<>();
		String name = (String) params.get("name");
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalMaster.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, ApprovalMaster.STATE,
				STATE_MASTER_APPROVAL_COMPLETE);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalMaster.class, ApprovalMaster.CREATE_TIMESTAMP,
				receiveFrom, receiveTo);
		QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, name);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalMaster master = (ApprovalMaster) obj[0];
			Persistable per = master.getPersist();
			if (per == null) {
				System.out.println("삭제된(완료) 데이터");
				WorkspaceHelper.service.deleteAllLines(master);
			}
			ApprovalLineColumn column = new ApprovalLineColumn(master, "COMPLETE_COLUMN");
			column.setRowNum(rowNum++);
			list.add(column);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 결재함
	 */
	public Map<String, Object> approval(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineColumn> list = new ArrayList<>();
		String submiterOid = (String) params.get("submiterOid");
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");
		String approvalTitle = (String) params.get("approvalTitle");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		// 쿼리 수정할 예정
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_APPROVAL_APPROVING);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, APPROVAL_LINE);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveFrom,
				receiveTo);

		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalLine.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		QuerySpecUtils.toCreator(query, idx_m, ApprovalMaster.class, submiterOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveFrom,
				receiveTo);
		QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, approvalTitle);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine approvalLine = (ApprovalLine) obj[0];
			ApprovalMaster m = approvalLine.getMaster();
			Persistable per = m.getPersist();
			if (per == null) {
				System.out.println("삭제된(결재) 데이터");
				WorkspaceHelper.service.deleteAllLines(m);
			}
			ApprovalLineColumn column = new ApprovalLineColumn(approvalLine, "APPROVAL_COLUMN");
			column.setRowNum(rowNum++);
			list.add(column);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 수신함
	 */
	public Map<String, Object> receive(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineColumn> list = new ArrayList<>();
		String name = (String) params.get("name");
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");
		String submiterOid = (String) params.get("submiterOid");
		String state = (String) params.get("state");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_master = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_master);
		if ("ALL".equals(state)) {
			query.appendAnd();
			query.appendOpenParen();
			QuerySpecUtils.toEquals(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_RECEIVE_START);
			QuerySpecUtils.toEqualsOr(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_RECEIVE_COMPLETE);
			QuerySpecUtils.toEqualsOr(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_RECEIVE_REJECT);
			query.appendCloseParen();
		} else if ("START".equals(state)) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_RECEIVE_START);
		} else if ("COMPLETE".equals(state)) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_RECEIVE_COMPLETE);
		} else if ("REJECT".equals(state)) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_RECEIVE_REJECT);
		}

		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, RECEIVE_LINE);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveFrom,
				receiveTo);
		QuerySpecUtils.toCreator(query, idx_master, ApprovalMaster.class, submiterOid);

		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalLine.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, name);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			ApprovalMaster m = line.getMaster();
			Persistable per = m.getPersist();
			if (per == null) {
				System.out.println("삭제된(합의) 데이터");
				WorkspaceHelper.service.deleteAllLines(m);
			}
			ApprovalLineColumn column = new ApprovalLineColumn(line, "RECEIVE_COLUMN");
			column.setRowNum(rowNum++);
			list.add(column);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 진행함
	 */
	public Map<String, Object> progress(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineColumn> list = new ArrayList<>();
		String name = (String) params.get("name");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalMaster.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		query.appendOpenParen();
		QuerySpecUtils.toEquals(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_APPROVAL_APPROVING);
		QuerySpecUtils.toEqualsOr(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_AGREE_READY);
		query.appendCloseParen();

		QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, name);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalMaster master = (ApprovalMaster) obj[0];
			Persistable per = master.getPersist();
			if (per == null) {
				System.out.println("삭제된(진행) 데이터");
				WorkspaceHelper.service.deleteAllLines(master);
			}
			ApprovalLineColumn column = new ApprovalLineColumn(master, "PROGRESS_COLUMN");
			column.setRowNum(rowNum++);
			list.add(column);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 반려함
	 */
	public Map<String, Object> reject(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineColumn> list = new ArrayList<>();
		String name = (String) params.get("name");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalMaster.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		// 합의 어떻게 처리 할건지?
		query.appendOpenParen();
//		QuerySpecUtils.toEquals(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_MASTER_AGREE_REJECT);
		QuerySpecUtils.toEquals(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_MASTER_APPROVAL_REJECT);
		query.appendCloseParen();

		QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, name);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalMaster master = (ApprovalMaster) obj[0];
			Persistable per = master.getPersist();
			if (per == null) {
				System.out.println("삭제된(반려) 데이터");
				WorkspaceHelper.service.deleteAllLines(master);
			}
			ApprovalLineColumn column = new ApprovalLineColumn(master, "REJECT_COLUMN");
			column.setRowNum(rowNum++);
			list.add(column);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
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

	/**
	 * 결재명 만들기
	 */
	public String getName(Persistable per) throws Exception {

		String name = "";

		if (!(per instanceof LifeCycleManaged)) {
			throw new Exception("객체가 라이프사이클을 구현하지 않았습니다.");
		}

		if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			name = doc.getNumber() + " [" + doc.getName() + "]";
		} else if (per instanceof ECOChange) {
			ECOChange e = (ECOChange) per;
			name = e.getEoNumber() + " [" + e.getEoName() + "]";
		} else if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			name = part.getNumber() + " [" + part.getName() + "]";
		} else if (per instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) per;
			name = epm.getNumber() + " [" + epm.getName() + "]";
		} else if (per instanceof AsmApproval) {
			AsmApproval asm = (AsmApproval) per;
			name = asm.getNumber() + " [" + asm.getName() + "]";
		}

		return name;
	}

	/**
	 * 모든 결재라인을 가져오는 함수, 두번재 변수로 기안라인을 가져오냐마쟈 여부 TRUE 안가져옴, FALSE 가져옴
	 */
	public ArrayList<ApprovalLine> getAllLines(ApprovalMaster master) throws Exception {
		return getAllLines(master, false);
	}

	/**
	 * 모든 결재라인을 가져오는 함수
	 */
	public ArrayList<ApprovalLine> getAllLines(ApprovalMaster master, boolean exclude) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<ApprovalLine>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		QuerySpecUtils.toEquals(query, idx, ApprovalLine.class, "masterReference.key.id", master);

		if (exclude) {
			QuerySpecUtils.toNotEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, SUBMIT_LINE);
		}
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, false);

		// 정렬???
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;
	}

	/**
	 * 결재 개수
	 */
	public Map<String, Integer> count() throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Integer> count = new HashMap<>();
		count.put("approval", (int) approval(params).get("total"));
		count.put("agree", (int) agree(params).get("total"));
		count.put("receive", (int) receive(params).get("total"));
		count.put("complete", (int) complete(params).get("total"));
		count.put("reject", (int) reject(params).get("total"));
		count.put("progress", (int) progress(params).get("total"));
		return count;
	}

	/**
	 * 결재라인 가져오기
	 */
	public ArrayList<ApprovalLine> getApprovalLines(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_APPROVAL);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, APPROVAL_LINE);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.SORT, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;
	}

	/**
	 * 합의 라인 가져오기
	 */
	public ArrayList<ApprovalLine> getAgreeLines(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_AGREE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, AGREE_LINE);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;
	}

	/**
	 * 수신라인 가져오기
	 */
	public ArrayList<ApprovalLine> getReceiveLines(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_RECEIVE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, RECEIVE_LINE);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;
	}

	/**
	 * 기안라인 가져오기
	 */
	public ApprovalLine getSubmitLine(ApprovalMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_SUBMITTER);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, SUBMIT_LINE);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			return line;
		}
		return null;
	}

	/**
	 * 결재 마스터 객체 가져오기
	 */
	public ApprovalMaster getMaster(Persistable per) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(per, "lineMaster", PersistMasterLink.class);
		if (result.hasMoreElements()) {
			return (ApprovalMaster) result.nextElement();
		}

		// 일괄결재 일 경우
		if (result.size() == 0) {
			result.reset();
			AsmApproval asm = WorkspaceHelper.manager.getAsmApproval(per);
			if (asm != null) {
				result = PersistenceHelper.manager.navigate(asm, "lineMaster", PersistMasterLink.class);
				if (result.hasMoreElements()) {
					return (ApprovalMaster) result.nextElement();
				}
			}
		}
		return null;
	}

	/**
	 * 일괄결재 객체
	 */
	public AsmApproval getAsmApproval(Persistable per) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(per, "approval", AppPerLink.class);
		if (qr.hasMoreElements()) {
			AsmApproval asm = (AsmApproval) qr.nextElement();
			return asm;
		}
		return null;
	}

	/**
	 * 결재 이력
	 */
	public JSONArray history(String oid) throws Exception {
		Persistable per = CommonUtil.getObject(oid);
		ArrayList<Map<String, String>> list = new ArrayList<>();
		ApprovalMaster master = getMaster(per);

		if (master != null) {
			ApprovalLine submit = getSubmitLine(master);
			Map<String, String> data = new HashMap<>();
			data.put("type", submit.getType());
			data.put("role", submit.getRole());
			data.put("name", submit.getName());
			data.put("state", submit.getState());
			data.put("owner", submit.getOwnership().getOwner().getFullName());
			data.put("receiveDate_txt",
					submit.getStartTime() != null ? submit.getStartTime().toString().substring(0, 16) : "");
			data.put("completeDate_txt",
					submit.getCompleteTime() != null ? submit.getCompleteTime().toString().substring(0, 16) : "");
			data.put("description", submit.getDescription());
			list.add(data);

			ArrayList<ApprovalLine> agreeLines = getAgreeLines(master);
			for (ApprovalLine agreeLine : agreeLines) {
				Map<String, String> map = new HashMap<>();
				map.put("type", agreeLine.getType());
				map.put("role", agreeLine.getRole());
				map.put("name", agreeLine.getName());
				map.put("state", agreeLine.getState());
				map.put("owner", agreeLine.getOwnership().getOwner().getFullName());
				map.put("receiveDate_txt",
						agreeLine.getStartTime() != null ? agreeLine.getStartTime().toString().substring(0, 16) : "");
				map.put("completeDate_txt",
						agreeLine.getCompleteTime() != null ? agreeLine.getCompleteTime().toString().substring(0, 16)
								: "");
				map.put("description", agreeLine.getDescription());
				list.add(map);
			}

			ArrayList<ApprovalLine> approvalLines = getApprovalLines(master);
			for (ApprovalLine approvalLine : approvalLines) {
				Map<String, String> map = new HashMap<>();
				map.put("type", approvalLine.getType());
				map.put("role", approvalLine.getRole());
				map.put("name", approvalLine.getName());
				map.put("state", approvalLine.getState());
				map.put("owner", approvalLine.getOwnership().getOwner().getFullName());
				map.put("receiveDate_txt",
						approvalLine.getStartTime() != null ? approvalLine.getStartTime().toString().substring(0, 16)
								: "");
				map.put("completeDate_txt",
						approvalLine.getCompleteTime() != null
								? approvalLine.getCompleteTime().toString().substring(0, 16)
								: "");
				map.put("description", approvalLine.getDescription());
				list.add(map);
			}

			ArrayList<ApprovalLine> receiveLines = getReceiveLines(master);
			for (ApprovalLine receiveLine : receiveLines) {
				Map<String, String> map = new HashMap<>();
				map.put("type", receiveLine.getType());
				map.put("role", receiveLine.getRole());
				map.put("name", receiveLine.getName());
				map.put("state", receiveLine.getState());
				map.put("owner", receiveLine.getOwnership().getOwner().getFullName());
				map.put("receiveDate_txt",
						receiveLine.getStartTime() != null ? receiveLine.getStartTime().toString().substring(0, 16)
								: "");
				map.put("completeDate_txt",
						receiveLine.getCompleteTime() != null
								? receiveLine.getCompleteTime().toString().substring(0, 16)
								: "");
				map.put("description", receiveLine.getDescription());
				list.add(map);
			}
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 마지막 결재 라인인지 체크
	 */
	public boolean isEndApprovalLine(ApprovalMaster master, int sort) throws Exception {
		boolean isEndApprovalLine = true;
		ArrayList<ApprovalLine> list = getApprovalLines(master);
		for (ApprovalLine appLine : list) {
			int compare = appLine.getSort();
			System.out.println("=" + compare);
			if (sort <= compare) {
				isEndApprovalLine = false;
				break;
			}
		}
		return isEndApprovalLine;
	}

	/**
	 * 합의함 조회 함수
	 */
	public Map<String, Object> agree(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineColumn> list = new ArrayList<>();

		String name = (String) params.get("name"); // 결재 제목
		String submiterOid = (String) params.get("submiterOid"); // 작성자
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");
		String state = (String) params.get("state");
		state = state == null ? "합의중" : state;

		// 쿼리문 작성
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_master = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_master);

		if ("합의완료".equals(state)) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_COMPLETE);
		} else if ("합의중".equals(state)) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_START);
		} else if ("합의반려".equals(state)) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_REJECT);
		} else if ("전체".equals(state)) {

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}

			query.appendOpenParen();

			QuerySpecUtils.toEquals(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_COMPLETE);
			QuerySpecUtils.toEqualsOr(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_START);
			QuerySpecUtils.toEqualsOr(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_REJECT);

			query.appendCloseParen();
		}

		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, AGREE_LINE);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveFrom,
				receiveTo);
		QuerySpecUtils.toCreator(query, idx_master, ApprovalMaster.class, submiterOid);
		QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, name);
		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalLine.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine approvalLine = (ApprovalLine) obj[0];
			ApprovalMaster m = approvalLine.getMaster();
			Persistable per = m.getPersist();
			if (per == null) {
				System.out.println("삭제된(합의) 데이터");
				WorkspaceHelper.service.deleteAllLines(m);
			}
			ApprovalLineColumn column = new ApprovalLineColumn(approvalLine, "AGREE_COLUMN");
			column.setRowNum(rowNum++);
			list.add(column);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 외부 메일 가져오기 메서드
	 */
	public JSONArray getExternalMail(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();

		Persistable per = CommonUtil.getObject(oid);
		if (per instanceof WorkData) {
			WorkData data = (WorkData) per;
			QueryResult qr = PersistenceHelper.manager.navigate(data, "mailUser", WorkDataMailUserLink.class);
			while (qr.hasMoreElements()) {
				MailUser user = (MailUser) qr.nextElement();
				Map<String, String> map = new HashMap<String, String>();
				map.put("oid", user.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("name", user.getName());
				map.put("email", user.getEmail());
				list.add(map);
			}
		} else {
			ArrayList<MailWTobjectLink> data = MailUserHelper.manager.navigate(oid);
			for (MailWTobjectLink link : data) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("oid", link.getUser().getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("name", link.getUser().getName());
				map.put("email", link.getUser().getEmail());
				list.add(map);
			}
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 진행함 상세(합의중, 승인중, 수신확인중)
	 * 
	 * @throws Exception
	 */
	public ApprovalLineDTO ingLine(ApprovalMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalMaster.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		query.appendOpenParen();
		QuerySpecUtils.toEquals(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_AGREE_START);
		QuerySpecUtils.toEqualsOr(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_APPROVAL_APPROVING);
		query.appendCloseParen();

//		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
//		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		ApprovalLineDTO dto = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			dto = new ApprovalLineDTO((ApprovalMaster) obj[0]);
		}
		return dto;
	}

	/**
	 * 완료함 상세
	 */
	public ApprovalLineDTO completeLine(ApprovalMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		// 완료함은 자기가 기안한거..
		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalMaster.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		QuerySpecUtils.toEquals(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_APPROVAL_COMPLETE);
//		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
//		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);

		System.out.println("query=" + query);
		QueryResult result = PersistenceHelper.manager.find(query);
		ApprovalLineDTO dto = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			dto = new ApprovalLineDTO((ApprovalMaster) obj[0]);
		}
		return dto;
	}

	/**
	 * 반려함 상세
	 */
	public ApprovalLineDTO rejectLine(ApprovalMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		if (!CommonUtil.isAdmin()) {
			WTUser sessionUser = CommonUtil.sessionUser();
			QuerySpecUtils.toCreator(query, idx, ApprovalMaster.class,
					sessionUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		QuerySpecUtils.toEquals(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_APPROVAL_REJECT);
//		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, SUBMIT_LINE);
//		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
//		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		ApprovalLineDTO dto = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			dto = new ApprovalLineDTO((ApprovalMaster) obj[0]);
		}
		return dto;
	}

	/**
	 * 합의중이 있는지 체크
	 */
	public boolean isAgreeApprovalLine(ApprovalMaster master) throws Exception {
		boolean isAgreeApprovalLine = true;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);
		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_AGREE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, AGREE_LINE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_START);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.size() == 0) {
			isAgreeApprovalLine = false;
		}
		return isAgreeApprovalLine;
	}

	public ArrayList<ApprovalLine> getAgreeLine(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_AGREE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, AGREE_LINE);
//		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_START);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;
	}

	/**
	 * 결재선에서 지정한 기존 결재라인 가져오기
	 */
	public JSONArray loadLines(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList();
		WorkData data = (WorkData) CommonUtil.getObject(oid);
		ApprovalMaster m = data.getAppMaster();

		if (m != null) {
			// 기안라인 제거..
			// 합의 결재 수신 순서
			ArrayList<ApprovalLine> agreeLines = getAgreeLine(m);
			for (ApprovalLine line : agreeLines) {
				Map<String, Object> map = new HashMap<>();
				WTPrincipalReference ref = line.getOwnership().getOwner();
				if (ref != null) {
					WTUser user = (WTUser) ref.getObject();
					PeopleDTO dto = new PeopleDTO(user);
					map.put("name", line.getName());
					map.put("type", line.getType());
					map.put("id", dto.getId());
					map.put("duty", dto.getDuty());
					map.put("email", dto.getEmail());
					map.put("department_name", dto.getDepartment_name());
				}
				list.add(map);
			}

			ArrayList<ApprovalLine> approvalLines = getApprovalLines(m);
			int sort = 1;
			for (ApprovalLine line : approvalLines) {
				Map<String, Object> map = new HashMap<>();
				WTPrincipalReference ref = line.getOwnership().getOwner();
				if (ref != null) {
					WTUser user = (WTUser) ref.getObject();
					PeopleDTO dto = new PeopleDTO(user);
					map.put("sort", sort);
					map.put("name", line.getName());
					map.put("type", line.getType());
					map.put("id", dto.getId());
					map.put("duty", dto.getDuty());
					map.put("email", dto.getEmail());
					map.put("department_name", dto.getDepartment_name());
				}
				list.add(map);
				sort++;
			}

			ArrayList<ApprovalLine> receiveLines = getReceiveLines(m);
			for (ApprovalLine line : receiveLines) {
				Map<String, Object> map = new HashMap<>();
				WTPrincipalReference ref = line.getOwnership().getOwner();
				if (ref != null) {
					WTUser user = (WTUser) ref.getObject();
					PeopleDTO dto = new PeopleDTO(user);
					map.put("name", line.getName());
					map.put("type", line.getType());
					map.put("id", dto.getId());
					map.put("duty", dto.getDuty());
					map.put("email", dto.getEmail());
					map.put("department_name", dto.getDepartment_name());
				}
				list.add(map);
			}
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 최종 결재 종료 후 외부메일 유저에게 메일 전송
	 */
	public void sendExternalMail(Persistable per) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(per, "user", MailWTobjectLink.class);
		while (qr.hasMoreElements()) {
			MailUser user = (MailUser) qr.nextElement();
			String email = user.getEmail();
			String name = user.getName();
			if (!StringUtil.checkString(email)) {
				throw new Exception(name + " 사용자의 이메일 주소가 없습ㄴ디ㅏ.");
			}
			// 반복적 메일 전송..
			MailUtils.manager.sendExternalMail((LifeCycleManaged) per, user);
		}
	}

}
