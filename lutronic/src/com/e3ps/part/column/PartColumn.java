package com.e3ps.part.column;

import java.sql.Timestamp;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.ThumbnailUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.part.service.PartHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;

@Getter
@Setter
public class PartColumn {

	private String part_oid; // 부품
	private String epm_oid; // 3D
	private String drawing_oid; // 2D
	private String _3d;
	private String _2d;
	private String number;
	private String name;
	private String location;
	private String version;
	private String remarks;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String ecoNo;
	private String pdf;
	private String step;
	private String dxf;

	public PartColumn() {

	}

	public PartColumn(WTPart part) throws Exception {
		setPart_oid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		set_3d(ThumbnailUtil.thumbnailSmall(part));
		setNumber(part.getNumber());
		setName(part.getName());
		setLocation(part.getLocation());
		setVersion(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
		setRemarks(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_REMARKS));
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorName());
		setCreateDate(part.getCreateTimestamp());
		setModifiedDate(part.getModifyTimestamp());
		setAttach(part);
	}

	private void setAttach(WTPart part) throws Exception {
		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		// 3D 캐드 세팅..
		if (epm != null) {
			setStep(AUIGridUtil.step(epm));
			EPMDocument epm2D = PartHelper.manager.getEPMDocument2D(epm);
			if (epm2D != null) {
				set_2d(ThumbnailUtil.thumbnailSmall(epm2D));
				setPdf(AUIGridUtil.pdf(epm2D));
				setDxf(AUIGridUtil.dxf(epm2D));
			}
		}
	}
}
