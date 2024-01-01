package com.e3ps.part.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.comments.beans.CommentsDTO;
import com.e3ps.common.comments.service.CommentsHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.service.PartHelper;
import com.ptc.wvs.server.util.PublishUtils;

import lombok.Getter;
import lombok.Setter;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.representation.Representation;
import wt.util.FileUtil;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

@Getter
@Setter
public class PartDTO {

	private String oid;
	private String number;
	private String name;
	private String location;
	private String version;
	private String remarks;
	private String state;
	private String creator;
	private String createDate;
	private String modifier;
	private String modifyDate;
	private String ecoNo;
	private boolean isLatest;

	private String epmOid;
	private String viewName;

	private Map<String, String> pdf = new HashMap<>();
	private Map<String, String> dxf = new HashMap<>();
	private Map<String, String> step = new HashMap<>();

	// 댓글
	private ArrayList<CommentsDTO> comments = new ArrayList<CommentsDTO>();

	public PartDTO(String oid) throws Exception {
		this((WTPart) CommonUtil.getObject(oid));
	}

	public PartDTO(WTPart part) throws Exception {
		setOid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(part.getNumber());
		setName(part.getName());
		setLocation(part.getLocation());
		setVersion(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
//		setRemark((String) map.get(AttributeKey.IBAKey.IBA_REMARKS));//
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorFullName());
		setCreateDate(part.getCreateTimestamp().toString().substring(0, 10));
		setModifier(part.getModifierFullName());
		setModifyDate(part.getModifyTimestamp().toString().substring(0, 10));
		setComments(CommentsHelper.manager.comments(part));
		setLatest(CommonUtil.isLatestVersion(part));
		View view = ViewHelper.getView(part);
		setViewName(view == null ? "" : view.getName());
		setRepresentable(part);
	}

	private void setRepresentable(WTPart part) throws Exception {
		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		if (epm != null) {

			Representation _representation = PublishUtils.getRepresentation(epm);
			if (_representation != null) {
				// step
				QueryResult qr = ContentHelper.service.getContentsByRole(_representation, ContentRoleType.SECONDARY);
				while (qr.hasMoreElements()) {
					ApplicationData data = (ApplicationData) qr.nextElement();
					String ext = FileUtil.getExtension(data.getFileName());
					if ("stp".equalsIgnoreCase(ext)) {
						this.step.put("name", data.getFileName());
						this.step.put("fileSizeKB", data.getFileSizeKB() + "KB");
						this.step.put("url", ContentHelper
								.getDownloadURL(_representation, data, false, data.getFileName()).toString());
					}
				}
			}

			EPMDocument epm2d = PartHelper.manager.getEPMDocument2D(epm);
			if (epm2d != null) {
				Representation representation = PublishUtils.getRepresentation(epm2d);
				if (representation != null) {
					QueryResult result = ContentHelper.service.getContentsByRole(representation,
							ContentRoleType.SECONDARY);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						String ext = FileUtil.getExtension(data.getFileName());
						if ("dxf".equalsIgnoreCase(ext)) {
							this.dxf.put("name", data.getFileName());
							this.dxf.put("fileSizeKB", data.getFileSizeKB() + "KB");
							this.dxf.put("url", ContentHelper
									.getDownloadURL(representation, data, false, data.getFileName()).toString());
						}
					}

					result.reset();
					result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.ADDITIONAL_FILES);
					if (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						String ext = FileUtil.getExtension(data.getFileName());
						if ("pdf".equalsIgnoreCase(ext)) {
							this.pdf.put("name", data.getFileName());
							this.pdf.put("fileSizeKB", data.getFileSizeKB() + "KB");
							this.pdf.put("url", ContentHelper
									.getDownloadURL(representation, data, false, data.getFileName()).toString());
						}
					}
				}
			}
		}
	}
}
