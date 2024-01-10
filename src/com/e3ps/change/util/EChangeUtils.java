package com.e3ps.change.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.PartGroupLink;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.ecn.service.EcnHelper;
import com.e3ps.change.eco.service.EcoHelper;
import com.e3ps.change.eo.service.EoHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.ContentUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.Department;
import com.e3ps.org.service.DepartmentHelper;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.service.SAPHelper;

import net.sf.json.JSONArray;
import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.session.SessionHelper;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;

/**
 * 설변관련 공통으로 사용된 함수 모음
 */
public class EChangeUtils {

	public static final EChangeUtils manager = new EChangeUtils();

	/**
	 * 설변 PDF 변환용
	 */
	private static final String processQueueName = "AttachPdfProcessQueue";
	private static final String className = "com.e3ps.common.aspose.AsposeUtils";
	private static final String methodName = "attachPdf";

	/**
	 * 중복 제거한 베이스 라인 가져오기??
	 */
	public ArrayList<Map<String, String>> getBaseline(String oid) throws Exception {
		ArrayList<Map<String, String>> list = arrayBaseLine(oid);
		ArrayList<Map<String, String>> baseLine = new ArrayList<Map<String, String>>();
		Map<String, String> tempMap = new HashMap<String, String>();
		for (Map<String, String> data : list) {
			Map<String, String> groupMap = new HashMap<String, String>();
			String baseLine_name = data.get("baseLine_name");
			String temp_name = baseLine_name.substring(0, baseLine_name.indexOf(":"));
			String baseLine_oid = data.get("baseLine_oid");
			String part_oid = data.get("part_oid");
			System.out.println(baseLine_name + " , " + temp_name + tempMap.containsKey(temp_name));
			if (tempMap.containsKey(temp_name)) {
				continue;
			} else {
				tempMap.put(temp_name, temp_name);
				groupMap.put("baseLine_name", temp_name);
				groupMap.put("baseLine_oid", baseLine_oid);
				groupMap.put("part_oid", part_oid);
			}
			baseLine.add(groupMap);
		}
		return baseLine;
	}

	/**
	 * 부품과 관련된 베이스 라인 가져오는 함수
	 */
	private ArrayList<Map<String, String>> arrayBaseLine(String oid) throws Exception {
		WTPart part = (WTPart) CommonUtil.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx_l = query.appendClassList(ManagedBaseline.class, true);
		int idx_m = query.appendClassList(BaselineMember.class, false);
		int idx_p = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toInnerJoin(query, ManagedBaseline.class, BaselineMember.class,
				"thePersistInfo.theObjectIdentifier.id", "roleAObjectRef.key.id", idx_l, idx_m);
		QuerySpecUtils.toInnerJoin(query, BaselineMember.class, WTPart.class, "roleBObjectRef.key.id",
				"thePersistInfo.theObjectIdentifier.id", idx_m, idx_p);
		QuerySpecUtils.toEqualsAnd(query, idx_p, WTPart.class, "masterReference.key.id", part.getMaster());
		QuerySpecUtils.toOrderBy(query, idx_l, ManagedBaseline.class, ManagedBaseline.CREATE_TIMESTAMP, true);

		QueryResult result = PersistenceHelper.manager.find(query);

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ManagedBaseline baseLine = (ManagedBaseline) obj[0];
			WTPart p = (WTPart) obj[1];
			Map<String, String> map = new HashMap<String, String>();
			map.put("part_oid", p.getPersistInfo().getObjectIdentifier().toString());
			map.put("baseLine_oid", baseLine.getPersistInfo().getObjectIdentifier().toString());
			map.put("baseLine_name", baseLine.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 베이스라인에 처리된 부품 목록
	 */
	public void collectBaseLineParts(WTPart part, Vector v) throws Exception {
		ArrayList<WTPart> list = PartHelper.manager.descentsPart(part, (View) part.getView().getObject());

		for (WTPart p : list) {
			String state = p.getLifeCycleState().toString();
			boolean isApproved = state.equals("APPROVED");
			// 승인된거 패스
			if (isApproved) {
				continue;
			}

			WTPart prev = null;
			prev = (WTPart) VersionControlHelper.service.predecessorOf(p);

			if (prev == null) {
				prev = p;
			}
			v.add(prev);
			collectBaseLineParts(prev, v);
		}
	}

	/**
	 * 설변활동 산출물 요약
	 */
	public ArrayList<Map<String, Object>> summary(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		EChangeOrder e = (EChangeOrder) CommonUtil.getObject(oid);

		QuerySpec qs = new QuerySpec(EChangeActivity.class);
		QuerySpecUtils.toEqualsAnd(qs, 0, EChangeActivity.class, "eoReference.key.id", e);
		QuerySpecUtils.toEqualsAnd(qs, 0, EChangeActivity.class, EChangeActivity.ACTIVE_TYPE, "DOCUMENT");
		QueryResult result = PersistenceHelper.manager.find(qs);
		while (result.hasMoreElements()) {
			EChangeActivity eca = (EChangeActivity) result.nextElement();

			Map<String, Object> map = new HashMap<>();
			// 담당부서 담당자 상태 요청완료일 완료일 첨부파일 의견 산추룸ㄹ
			Department dept = DepartmentHelper.manager.getDepartment(eca.getActiveUser());
			map.put("oid", eca.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("department_name", dept != null ? dept.getName() : "지정안됨");
			map.put("activity_user", eca.getActiveUser().getFullName());
			map.put("state", eca.getLifeCycleState().getDisplay());
			map.put("finishDate", eca.getFinishDate() != null ? eca.getFinishDate().toString().substring(0, 10) : "");
			map.put("completeDate", eca.getModifyTimestamp().toString().substring(0, 10));
			map.put("description", eca.getDescription());
			map.put("secondary", ContentUtils.getSecondary(eca));
			JSONArray data = ActivityHelper.manager.docList(eca);
			map.put("data", data);
			list.add(map);
		}
		return list;
	}

	/**
	 * EO 결재후 발생할 내용들 큐로 전환
	 */
	public static void afterEoAction(Hashtable<String, String> hash) throws Exception {
		System.out.println("EO 승인후 호출 !!!");
		try {
			String oid = hash.get("oid");
			EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(oid);

			ArrayList<EOCompletePartLink> completeParts = EoHelper.manager.completeParts(eo);
			System.out.println("완제품 개수 = " + completeParts.size());

			// 모든 부품 대상 수집..
			ArrayList<WTPart> list = EoHelper.manager.getter(eo, completeParts);

			System.out.println("EO 대상 품목 개수 =  " + list.size());

			System.out.println("EO 대상품목 상태값 변경 시작");
			EoHelper.service.eoPartApproved(eo, list);
			System.out.println("EO 대상품목 상태값 변경 완료");

			// 개발이 없어진다.
			SAPHelper.service.sendSapToEo(eo, completeParts, list);

			EoHelper.service.saveBaseline(eo, completeParts);

			// IBA 값 세팅

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * ECO 결재후 발생할 내용들 큐로 전환
	 */
	public static void afterEcoAction(Hashtable<String, String> hash) throws Exception {
		System.out.println("ECO 승인후 호출 !!!");
		String oid = hash.get("oid");
		EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);

		// 완제품 재수집
		ArrayList<EOCompletePartLink> completeParts = EcoHelper.manager.completeParts(eco);
		ArrayList<EcoPartLink> ecoParts = EcoHelper.manager.ecoParts(eco);
		System.out.println("ECO 완제품 개수 = " + completeParts.size());
		System.out.println("ECO 대상품목 개수 = " + ecoParts.size());

		// ECO 정보로 ECN 자동 생성
		EcnHelper.service.create(eco, ecoParts, completeParts);

		SAPHelper.service.sendSapToEco(eco);

		// 부품 도면 상태 변경 - 전송 후 상태값을 변경해야 할듯
		EcoHelper.manager.setIBAAndState(ecoParts, completeParts);

		// 베이스 라인 생성
		EcoHelper.service.saveBaseline(eco, completeParts);

	}

	/**
	 * 더미 부품 인지 체크
	 */
	public static boolean isDummy(String number) throws Exception {
		boolean isDummy = true;

		if (StringUtil.checkString(number)) {
			if (number.length() == 10) {
				if (Pattern.matches("^[0-9]+$", number)) {
					// 숫자임
					isDummy = false;
				} else {
					// 숫자아님
					isDummy = true;
				}
			} else {
				isDummy = true;
			}
		} else {
			// 입력값 없음.
			isDummy = true;
		}
		return isDummy;
	}

	/**
	 * 현재 버전의 다음 버전 객체
	 */
	public RevisionControlled getNext(RevisionControlled rc) throws Exception {
		byte[] b = rc.getVersionIdentifier().getValue().getBytes();
		b[b.length - 1] += 1;
		QueryResult qr = VersionControlHelper.service.allVersionsOf(rc.getMaster());
		RevisionControlled next = null;
		while (qr.hasMoreElements()) {
			RevisionControlled obj = ((RevisionControlled) qr.nextElement());
			if (obj.getVersionIdentifier().getSeries().getValue().equals(new String(b))) {
				next = obj;
			}
		}
		if (next != null) {
			return (RevisionControlled) VersionControlHelper.getLatestIteration(next, false);
		}
		return next;
	}

	/**
	 * 현재 버전의 이전 버전 객체
	 */
	public static RevisionControlled getPrev(RevisionControlled rc) throws Exception {
		byte[] b = rc.getVersionIdentifier().getValue().getBytes();
		b[b.length - 1] -= 1;
		QueryResult qr = VersionControlHelper.service.allVersionsOf(rc.getMaster());
		RevisionControlled prev = null;
		while (qr.hasMoreElements()) {
			RevisionControlled obj = ((RevisionControlled) qr.nextElement());
			if (obj.getVersionIdentifier().getSeries().getValue().equals(new String(b))) {
				prev = obj;
			}
		}
		if (prev != null) {
			return (RevisionControlled) VersionControlHelper.getLatestIteration(prev, false);
		}
		return prev;
	}

	/**
	 * ECO와 연관 시켜 이전품목 이력 품목
	 */
	public WTPart getEcoPrePart(EChangeOrder eco, WTPart after) throws Exception {
		WTPart pre_part = null;
		WTPartMaster master = after.getMaster();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartToPartLink.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, PartToPartLink.class, "ecoReference.key.id", eco);
		QuerySpecUtils.toEqualsAnd(query, idx, PartToPartLink.class, "roleBObjectRef.key.id", master);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartToPartLink link = (PartToPartLink) obj[0];
			WTPartMaster m = link.getPrev();
			String version = link.getPreVersion();
			pre_part = PartHelper.manager.getPart(m.getNumber(), version);
		}
		return pre_part;
	}

	/**
	 * ECO 관련 목품 그룹핑 정보
	 */
	public String getPartGroup(WTPart next_part, EChangeOrder eco) throws Exception {
		String group = "";

		int idx = 0;

		WTPartMaster m = (WTPartMaster) next_part.getMaster();

		QuerySpec query = new QuerySpec();
		int i = query.appendClassList(PartGroupLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, PartGroupLink.class, "roleAObjectRef.key.id", m);
		QuerySpecUtils.toEqualsAnd(query, idx, PartGroupLink.class, "ecoReference.key.id", eco);
		QueryResult result = PersistenceHelper.manager.find(query);
//		QueryResult result = PersistenceHelper.manager.navigate(next_part, "ecr", PartGroupLink.class);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartGroupLink link = (PartGroupLink) obj[0];
			EChangeRequest ecr = link.getEcr();

			// 2
			if (result.size() - 1 == idx) {
				group += ecr.getPersistInfo().getObjectIdentifier().getStringValue();
			} else {
				group += ecr.getPersistInfo().getObjectIdentifier().getStringValue() + ", ";
			}
			idx++;
		}

		return group;
	}

	/**
	 * ECO 진행시 변경된 품목 리스트 - 개정 혹은 이전품에 대한 내용
	 */
	public ArrayList<WTPart> getEcoParts(EChangeOrder eco) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		QueryResult result = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		while (result.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) result.nextElement();
			WTPartMaster master = link.getPart();
			String version = link.getVersion();
			WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
			boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");

			// 개정 케이스 - 이전품목을 가여와야한다.
			if (isApproved) {
				// 진짜 다음 버전으로 올리는 케이스 인데..
				WTPart next_part = (WTPart) getNext(part);
				list.add(next_part);
//				list.add(part);// 이전것도 일단 보낸다???
				// 애시당초 변경된건을 넣은 케이스
			} else {
				list.add(part);
				WTPart pre_part = SAPHelper.manager.getPre(part, eco);
				if (pre_part != null) {
//					list.add(pre_part);
				}
			}
		}

		return list;
	}

	/**
	 * 설변 PDF 변환용
	 */
	public void attachPdfMethod(String oid) throws Exception {
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", oid);

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, methodName, className, argClasses, argObjects);
	}
}
