package com.e3ps.drawing.column;

import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;

@Getter
@Setter
public class DrawingColumn {

	private String oid;

	public DrawingColumn() {

	}

	public DrawingColumn(EPMDocument epm) throws Exception {
		setOid(epm.getPersistInfo().getObjectIdentifier().getStringValue());
	}
}
