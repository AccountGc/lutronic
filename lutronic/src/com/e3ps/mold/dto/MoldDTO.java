package com.e3ps.mold.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.common.comments.service.CommentsHelper;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.iba.AttributeKey.IBAKey;
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
public class MoldDTO {
	
	private String oid;
	private String name;
	private String number;
	private String state;
	private String creator;
	private String modifier;
	private String createDate;
	private String modifyDate;
	private String version;
	private String description;
	private String location;
	private String documentType;
	
	// IBA
	private String manufacture_name;
	private String manufacture_code;
	private String moldtype_name;
	private String moldtype_code;
	private String moldnumber_name;
	private String moldnumber_code;
	private String moldcost_name;
	private String moldcost_code;
	private String interalnumber;
	private String deptcode_name;
	private String deptcode_code;
	private String approvaltype_name;
	private String approvaltype_code;
	
	// 변수용
	private String lifecycle;
	private String primary;
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> partList = new ArrayList<>(); // 관련 품목
	private ArrayList<Map<String, String>> docList = new ArrayList<>(); // 관련 문서

	public MoldDTO() {
		
	}

	public MoldDTO(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(doc.getName());
		setNumber(doc.getNumber());
		setState(doc.getLifeCycleState().getDisplay());
		setCreator(doc.getCreatorFullName());
		setModifier(doc.getModifierFullName());
		setCreateDate(doc.getCreateTimestamp().toString().substring(0, 10));
		setModifyDate(doc.getModifyTimestamp().toString().substring(0, 10));
		setInteralnumber(doc.getIterationIdentifier().getSeries().getValue());
		setVersion(doc.getVersionIdentifier().getSeries().getValue());
		setDescription(doc.getDescription());
		setLocation(doc.getLocation());
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