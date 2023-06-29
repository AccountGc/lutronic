package com.e3ps.test;

import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.EnumeratedType;
import wt.sandbox.InteropInfo;
import wt.sandbox.InteropState;

public class WTDocTEST {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			WTDocument doc = WTDocument.newWTDocument();
			//toInteropState("iop_c/i")
			//DocumentType docType = DocumentType.toDocumentType(documentType);
			InteropInfo inState = InteropInfo.newInteropInfo();
			InteropState iState = InteropState.toInteropState("iop_c/i");
			inState.setState(iState);
			doc.setInteropInfo(inState);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
