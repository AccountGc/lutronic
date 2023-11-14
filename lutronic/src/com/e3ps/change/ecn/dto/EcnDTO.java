package com.e3ps.change.ecn.dto;

import java.util.ArrayList;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;

@Getter
@Setter
public class EcnDTO {

	private String oid;
	private String name;
	private String number;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;
	private String eoCommentD;
	private String eoCommentE;

//	private JSONArray list = new JSONArray();
	private ArrayList<EChangeRequest> list = new ArrayList<EChangeRequest>();

	public EcnDTO() {

	}

	public EcnDTO(String oid) throws Exception {
		this((EChangeNotice) CommonUtil.getObject(oid));
	}

	public EcnDTO(EChangeNotice ecn) throws Exception {
		setOid(ecn.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(ecn.getEoName());
		setNumber(ecn.getEoNumber());
		setEoCommentA(ecn.getEoCommentA());
		setEoCommentB(ecn.getEoCommentB());
		setEoCommentC(ecn.getEoCommentC());
		setEoCommentD(ecn.getEoCommentD());
		setEoCommentE(ecn.getEoCommentE());
//		setList(EcnHelper.manager.getEcnGroupPart(ecn));
		setCr(ecn);
	}

	private void setCr(EChangeNotice ecn) throws Exception {
		ArrayList<EChangeRequest> crList = new ArrayList<EChangeRequest>();
		EChangeOrder eco = ecn.getEco();
		QueryResult result = PersistenceHelper.manager.navigate(eco, "ecr", RequestOrderLink.class);
		while (result.hasMoreElements()) {
			EChangeRequest ecr = (EChangeRequest) result.nextElement();
			crList.add(ecr);
		}
		setList(crList);
	}
}
