package com.e3ps;

import com.e3ps.common.util.CommonUtil;

import wt.epm.EPMDocument;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.vc.config.LatestConfigSpec;

public class Test {

	public static void main(String[] args) throws Exception {
//
//		WTPart p = (WTPart) CommonUtil.getObject("wt.part.WTPart:	");
//
//		QueryResult qr = PersistenceHelper.manager.navigate(p, EPMBuildRule.BUILD_SOURCE_ROLE, EPMBuildRule.class);
//		System.out.println(qr.size());
//		while (qr.hasMoreElements()) {
//			EPMDocument e = (EPMDocument) qr.nextElement();
//			System.out.println("e=" + e.getNumber());
//		}

//		EPMDocument seed = (EPMDocument)CommonUtil.getObject("wt.epm.EPMDocument:690123");
//		            
//		final int DRAWING_MODEL_DEP_TYPE = 4;
//		LatestConfigSpec latest_config = new LatestConfigSpec();
//		QuerySpec ref_filter_qs = new QuerySpec();
//		Class qc = EPMReferenceLink.class;
//		int idx = ref_filter_qs.addClassList(qc, true);
//		ref_filter_qs.appendWhere(new SearchCondition(qc, EPMReferenceLink.REQUIRED, SearchCondition.IS_TRUE), new int[]{idx});
//		ref_filter_qs.appendAnd();
//		ref_filter_qs.appendWhere(new SearchCondition(qc, EPMReferenceLink.DEP_TYPE, SearchCondition.EQUAL, DRAWING_MODEL_DEP_TYPE ), new int[]{idx});
//		System.out.println(ref_filter_qs);
//		
//		
//		QueryResult qr = EPMStructureHelper.service.navigateReferencesToIteration(seed, ref_filter_qs, true, latest_config);
//		System.out.println(qr.size());
//		while(qr.hasMoreElements()) {
//			Object[] obj = (Object[])qr.nextElement();
//			EPMReferenceLink link = (EPMReferenceLink)obj[0];
//			System.out.println("l="+link.getReferencedBy().getCADName());
//		}
		EPMDocument d = (EPMDocument) CommonUtil.getObject("wt.epm.EPMDocument:680784");
		wt.epm.EPMDocumentMaster dm = (wt.epm.EPMDocumentMaster) d.getMaster();
		wt.fc.QueryResult qr = wt.epm.structure.EPMStructureHelper.service.navigateReferencedBy(dm, null, false);
		System.out.println("qr=" + qr.size());
		while (qr.hasMoreElements()) {
			wt.epm.structure.EPMReferenceLink rl = (wt.epm.structure.EPMReferenceLink) qr.nextElement();
			if (rl.getDepType() == com.ptc.wpcfg.pdmabstr.PROEDependency.DEP_T_DRAW) {
				wt.epm.EPMDocument dd = rl.getReferencedBy();
				System.out.println("Found a drawing reference: " + dd.getCADName());
			}
		}
		System.exit(0);
	}
}