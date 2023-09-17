package com.e3ps.doc.dto;

import java.util.Vector;

import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.beans.VersionData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.service.VersionHelper;

import lombok.Getter;
import lombok.Setter;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.enterprise.BasicTemplateProcessor;
import wt.fc.QueryResult;
import wt.session.SessionHelper;

@Getter
@Setter
public class DocumentDTO {

	private String oid;
	private String number;
	private String name;
	private String state;
	private String icon;
	private String linkOid;
	private String icon2;
	private String creator;
	private String modifier;
	private String createDate;
	private String modifyDate;
	private String description;
	private String documentType;
	private String location;
	private boolean isLatest;
	private String approvalType;
	private String model;
	private String preseration;
	private String interalnumber;
	private String deptcode;
	private String manufacture;
	private String moldtype;
	private String moldnumber;
	private String moldcost;
	private String productmethod;
	private String unit;
	private String weight;
	private String mat;
	private String finish;
	private String remarks;
	private String specification;
	private String ecoNo;
	private String ecoDate;
	private String chk;
	private String apr;
	private String rev;
	private String des;
	private String changeNo;
	private String changeDate;
	private String version;

	public DocumentDTO(WTDocument doc) throws Exception {
		setOid(CommonUtil.getOIDString(doc));
		setNumber(doc.getNumber());
		setName(doc.getName());
		setState(doc.getLifeCycleState().getDisplay());
		setCreator(doc.getCreatorFullName());
		setModifier(doc.getModifierFullName());
		setCreateDate(DateUtil.getDateString(doc.getCreateTimestamp(), "d"));
		setModifyDate(DateUtil.getDateString(doc.getModifyTimestamp(), "d"));
		setDescription(doc.getDescription());
		setDocumentType(doc.getDocType().getDisplay(Message.getLocale()));

		ContentItem item = null;
		QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) doc, ContentRoleType.PRIMARY);
		while (result.hasMoreElements()) {
			item = (ContentItem) result.nextElement();
		}

		if (item != null) {
			this.icon = CommonUtil.getContentIconStr(item);
		}

		this.icon2 = BasicTemplateProcessor.getObjectIconImgTag(doc);
		setLatest(VersionHelper.service.isLastVersion(doc));
		setLocation(StringUtil.checkNull(doc.getLocation()).replaceAll("/Default", ""));

		String appType = IBAUtil.getAttrValue(doc, IBAKey.IBA_APPROVALTYPE);
		NumberCode code = NumberCodeHelper.service.getNumberCode("APPROVALTYPE", appType);
		if (code != null) {
			setApprovalType(code.getCode());
		}
		setModel(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_MODEL)));
		setPreseration(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_PRESERATION)));
		setInteralnumber(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_INTERALNUMBER)));
		setDeptcode(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_DEPTCODE)));
		setManufacture(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_MANUFACTURE)));
		setMoldtype(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_MOLDTYPE)));
		setMoldnumber(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_MOLDNUMBER)));
		setMoldcost(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_MOLDCOST)));
		setProductmethod(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_PRODUCTMETHOD)));
		setUnit(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.EPMKey.IBA_UNIT)));
		setWeight(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_WEIGHT)));
		setMat(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_MAT)));
		setFinish(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_FINISH)));
		setRemarks(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_REMARKS)));
		setSpecification(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_SPECIFICATION)));
		setEcoNo(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_ECONO)));
		setEcoDate(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_ECODATE)));
		setChk(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_CHK)));
		setApr(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_APR)));
		setRev(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_REV)));
		setDes(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_DES)));
		setChangeNo(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_CHANGENO)));
		setChangeDate(StringUtil.checkNull(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_CHANGEDATE)));
		setVersion(doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
	}

	/**
	 * 회수 권한 승인중 && (소유자 || 관리자 ) && 기본 결재
	 * 
	 * @return
	 */
	public boolean isWithDraw() {
		try {
			return (state.equals("APPROVING") && (isOwner() || CommonUtil.isAdmin()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * Owner 유무 체크
	 * 
	 * @return
	 */
	public boolean isOwner() {

		try {
			return SessionHelper.getPrincipal().getName().equals(getCreator());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

//	public String getDocumentName(int index) {
//		try {
//			
//			if(index == 2) {
//				String docName1 = StringUtil.checkNull(IBAUtil.getAttrValue2(doc, "DOCUMENTNAME1"));
//				String docName2 = StringUtil.checkNull(IBAUtil.getAttrValue2(doc, "DOCUMENTNAME2"));
//				if(docName1.length() == 0 && docName2.length() == 0) {
//					return this.name;
//				}else {
//					return docName2;
//				}
//			}else {
//				return StringUtil.checkNull(IBAUtil.getAttrValue2(doc, "DOCUMENTNAME"+index));
//			}
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//	}
//	
//	public String getDescription(boolean isView) {
//		String description = StringUtil.checkNull(this.doc.getDescription());
//		if(isView) {
//			description = WebUtil.getHtml(description);
//		}
//		return description;
//	}
//	
//	public String getDocumentType() {
//		String type = this.doc.getDocType().getDisplay(Message.getLocale());
//		return type;
//	}
//	
//	public String getIBAValue(String iba) {
//		try {
//			return IBAUtil.getAttrValue(this.doc, iba);
//		} catch(Exception e) {
//			return "";
//		}
//	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 문서와 링크 eca,개발마스터 Activity
	 * 
	 * @return
	 */
	public String getLinkOid() {
		return linkOid;
	}

	public void setLinkOid(String linkOid) {
		this.linkOid = linkOid;
	}

	public String getIcon2() {
		return icon2;
	}

	public void setIcon2(String icon2) {
		this.icon2 = icon2;
	}
}
