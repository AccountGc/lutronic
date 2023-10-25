package com.e3ps.change.eo.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eo.service.EoHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.service.MailUserHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EoDTO {

	private String oid;
	private String number;
	private String name;
	private String eoType;
	private String model_name;
	private String state;
	private String creator;
	private String createdDate;
	private String modifiedDate;
	private String eoCommentA = "";
	private String eoCommentB = "";
	private String eoCommentC = "";

	// 변수용
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows104 = new ArrayList<>(); // 완제품
	private ArrayList<Map<String, String>> rows90 = new ArrayList<>(); // 관련문서
	private ArrayList<Map<String, String>> rows200 = new ArrayList<>(); // ECA
	private ArrayList<Map<String, String>> rows300 = new ArrayList<>(); // 제품코드
	private ArrayList<Map<String, String>> modelInfo = new ArrayList<>();

	// 결재 변수
	private ArrayList<Map<String, String>> agreeRows = new ArrayList<>(); // 검토
	private ArrayList<Map<String, String>> approvalRows = new ArrayList<>(); // 결재
	private ArrayList<Map<String, String>> receiveRows = new ArrayList<>(); // 수신

	// 외부 메일 변수
	private ArrayList<Map<String, String>> external = new ArrayList<Map<String, String>>();

	public EoDTO() {

	}

	public EoDTO(String oid) throws Exception {
		this((EChangeOrder) CommonUtil.getObject(oid));
	}

	public EoDTO(EChangeOrder eo) throws Exception {
		setOid(eo.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(eo.getEoNumber());
		setName(eo.getEoName());
		setEoType(eo.getEoType());
//		modelInfo
		// 모델 코드 처리??
		if (eo.getModel() != null) {
			setModel_name(EoHelper.manager.displayToModel(eo.getModel()));
		}

		setModelInfo(getModel(eo.getModel()));
		setState(eo.getLifeCycleState().getDisplay());
		setCreator(eo.getCreatorFullName());
		setCreatedDate(eo.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(eo.getModifyTimestamp().toString().substring(0, 10));
		setEoCommentA(eo.getEoCommentA());
		setEoCommentB(eo.getEoCommentB());
		setEoCommentC(eo.getEoCommentC());
	}

	private ArrayList<Map<String, String>> getModel(String model) throws Exception {
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		if(model != null) {
			String[] ss = model.split(",");
			for (int i = 0; i < ss.length; i++) {
				Map<String, String> data = new HashMap<String, String>();
				String s = ss[i];
				NumberCode code = NumberCodeHelper.manager.getNumberCode(s, "MODEL");
				data.put("name", code.getName());
				data.put("code", code.getCode());
				data.put("sort", code.getSort());
				data.put("description", code.getDescription());
				data.put("enabled", code.getEngName());
				data.put("oid", code.getPersistInfo().getObjectIdentifier().getStringValue());
				result.add(data);
			}			
		}
		return result;
	}

}
