package com.e3ps.doc.dto;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.common.comments.service.CommentsHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.StringDefinitionReference;
import wt.iba.value.IBAHolderReference;
import wt.iba.value.StringValue;
import wt.query.QuerySpec;

@Getter
@Setter
public class DocumentDTO {

	private String oid;
	private WTDocument doc;
	private String number;
	private String name;
	private String description;
	private String content;
	private String location;
	private String documentType_name;
	private String documentType_code;
	private boolean latest;
	private String state;
	private String version;
	private String iteration;
	private String creator;
	private String createdDate;
	private String modifier;
	private String modifiedDate;

	// IBA
//	private String writer_name;
//	private String writer_oid;
	private String writer;
	private String model_name;
	private String model_code;
	private String preseration_name;
	private String preseration_code;
	private String interalnumber;
	private String deptcode_name;
	private String deptcode_code;
	private String approvaltype_name;
	private String approvaltype_code;

	// 댓글
	private ArrayList<CommentsDTO> comments = new ArrayList<CommentsDTO>();

	// auth
	private boolean _delete = false;
	private boolean _modify = false;
	private boolean _revise = false;
	private boolean _withdraw = false;

	// 변수용
	private String iterationNote;
	private String documentName;
	private String lifecycle;
	private String primary;
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows90 = new ArrayList<>(); // 관련 문서
	private ArrayList<Map<String, String>> rows91 = new ArrayList<>(); // 관련 품목
	private ArrayList<Map<String, String>> rows100 = new ArrayList<>(); // 관련 EO
	private ArrayList<Map<String, String>> rows105 = new ArrayList<>(); // 관련 ECO
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows103 = new ArrayList<>(); // 관련 ECPR

	private boolean temprary;

	public DocumentDTO() {

	}

	public DocumentDTO(String oid) throws Exception {
		this((WTDocument) CommonUtil.getObject(oid));
	}

	/**
	 * 문서 정보
	 */
	public DocumentDTO(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setDoc(doc);
		setName(doc.getName());
		setNumber(doc.getNumber());
		setDescription(doc.getDescription());
		setContent(doc.getTypeInfoWTDocument().getPtc_rht_1());
		setLocation(doc.getLocation());
		setDocumentType_name(doc.getDocType().getDisplay());
		setDocumentType_code(doc.getDocType().toString());
		setLatest(CommonUtil.isLatestVersion(doc));
		setState(doc.getLifeCycleState().getDisplay());
		setVersion(doc.getVersionIdentifier().getSeries().getValue());
		setIteration(doc.getIterationIdentifier().getSeries().getValue());
		setCreator(doc.getCreatorFullName());
		setCreatedDate(doc.getCreateTimestamp().toString().substring(0, 10));
		setModifier(doc.getModifierFullName());
		setModifiedDate(doc.getModifyTimestamp().toString().substring(0, 10));
		setIBAAttributes(doc);
		setAuth(doc);
		setComments(CommentsHelper.manager.comments(doc));
	}

	/**
	 * IBA 값 세팅
	 */
	private void setIBAAttributes(WTDocument doc) throws Exception {
		// 작성자
		String writer = IBAUtil.getStringValue(doc, "DSGN");
		setWriter(writer);
//		String writer_name = CommonUtil.getUserNameFromOid(writer_oid);
//		setWriter_oid(writer_oid);
//		setWriter_name(writer_name);
		// 프로젝트 코드
		String model_code = IBAUtil.getStringValue(doc, "MODEL");
		String model_name = keyToValue(model_code, "MODEL");
		setModel_code(model_code);
		setModel_name(model_name);
		// 내부문서번호
		setInteralnumber(IBAUtil.getStringValue(doc, "INTERALNUMBER"));
		// 결재타입
		String approvalType_code = IBAUtil.getStringValue(doc, "APPROVALTYPE");
		String approvalType_name = "BATCH".equals(approvalType_code) ? "일괄결재" : "기본결재";
		setApprovaltype_code(approvalType_code);
		setApprovaltype_name(approvalType_name);
		// 보존기간
		String preseration_code = IBAUtil.getStringValue(doc, "PRESERATION");
		String preseration_name = keyToValue(preseration_code, "PRESERATION");
		setPreseration_code(preseration_code);
		setPreseration_name(preseration_name);
		// 부서코드
		String deptcode_code = IBAUtil.getStringValue(doc, "DEPTCODE");
		String deptcode_name = keyToValue(deptcode_code, "DEPTCODE");
		setDeptcode_code(deptcode_code);
		setDeptcode_name(deptcode_name);
	}

	/**
	 * IBA 값 디스플레이 값으로 변경
	 */
	private String keyToValue(String code, String codeType) throws Exception {
		return NumberCodeHelper.manager.getNumberCodeName(code, codeType);
	}

	/**
	 * 권한 설정
	 */
	private void setAuth(WTDocument doc) throws Exception {
		// 개정 권한 - (최신버전 && 승인됨)
		if (!CommonUtil.isAdmin()) {
			if (check("APPROVED") && isLatest()) {
				set_revise(true);
			}
			// 삭제, 수정 권한 - (최신버전 && ( 임시저장 || 작업중 || 일괄결재중 || 재작업))
			if (isLatest() && (check("INWORK") || check("TEMPRARY") || check("BATCHAPPROVAL") || check("REWORK"))) {
				set_delete(true);
				set_modify(true);
			}

			if (check("APPROVING") && isLatest()) {
				set_withdraw(true);
			}
			// 관리자는 일단 모든 권한 오픈
		} else {
			set_delete(true);
			set_modify(true);
			set_withdraw(true);
			set_revise(true);
		}
	}

	/**
	 * 상태값 여부 체크
	 */
	private boolean check(String state) throws Exception {
		boolean check = false;
		String compare = getDoc().getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}

	/**
	 * IBA 값 가져오기 함수
	 */
	public StringDefinition getSd(String name) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(StringDefinition.class, true);
		QuerySpecUtils.toEquals(query, idx, StringDefinition.class, StringDefinition.NAME, name);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			StringDefinition sd = (StringDefinition) obj[0];
			return sd;
		}
		return null;
	}

	/**
	 * IBA 속성값 설정
	 */
	public void setIBAValue(WTDocument doc, String value, String name) throws Exception {
		StringDefinition sd = getSd(name);
		StringValue sv = new StringValue();
		if (sd == null) {
			return;
		}

		// 생성전 삭제 처리..
		deleteIBAValue(doc, sd);
		sv.setValue(value);
		sv.setDefinitionReference((StringDefinitionReference) sd.getAttributeDefinitionReference());
		sv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(doc)));
		PersistenceHelper.manager.save(sv);
	}

	/**
	 * IBA 값 삭제
	 */
	private void deleteIBAValue(WTDocument doc, StringDefinition sd) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(StringValue.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, StringValue.class, "definitionReference.key.id", sd);
		QuerySpecUtils.toEqualsAnd(query, idx, StringValue.class, "theIBAHolderReference.key.id", doc);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			StringValue sv = (StringValue) obj[0];
			PersistenceHelper.manager.delete(sv);
		}
	}
}