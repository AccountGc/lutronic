package com.e3ps.sap;

import java.util.ArrayList;

import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.conn.SAPConnection;
import com.e3ps.sap.conn.SAPDevConnection;
import com.e3ps.sap.service.SAPHelper;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoTable;

import wt.part.WTPart;

public class SAPTest {

	public static void main(String[] args) throws Exception {

		SAPHelper.manager.ZPPIF_PDM_001_TEST(436906, "C2507014");
//
//		JCoDestination destination = JCoDestinationManager.getDestination(SAPDevConnection.DESTINATION_NAME);
//		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_001");
//		if (function == null) {
//			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
//		}
//
//		WTPart part1 = (WTPart) CommonUtil.getObject("wt.part.WTPart:441542");
//		WTPart part2 = (WTPart) CommonUtil.getObject("wt.part.WTPart:441553");
////		ArrayList<WTPart> list = PartHelper.manager.descendants(root);
//
//		ArrayList<WTPart> list = new ArrayList<WTPart>();
//		list.add(part1);
//		list.add(part2);
//		System.out.println("SAP PART INTERFACE START!");
//		// ET_MAT
//		JCoTable insertTable = function.getTableParameterList().getTable("ET_MAT");
//		JCoParameterList importTable = function.getImportParameterList();
//		importTable.setValue("IV_WERKS", "1000"); // 플랜트
//		int idx = 1;
//		for (WTPart part : list) {
//
////			String next = getNextSeq("PART", "00000000");
////			String ZIFNO = "PART-" + next;
//
//			insertTable.insertRow(idx);
//
//			// 플랜트
//
//			// 샘플로 넣기
//			insertTable.setValue("AENNR8", "C230700" + idx); // 변경번호 8자리
//			insertTable.setValue("MATNR", part.getNumber()); // 자재번호
//			insertTable.setValue("MAKTX", part.getName()); // 자재내역(자재명)
//			insertTable.setValue("MEINS", part.getDefaultUnit().toString().toUpperCase()); // 기본단위
//
//			String ZSPEC = IBAUtil.getStringValue(part, "SPECIFICATION");
//			insertTable.setValue("ZSPEC", ZSPEC); // 사양
//
//			String ZMODEL = IBAUtil.getStringValue(part, "MODEL");
//			insertTable.setValue("ZMODEL", ZMODEL); // Model:프로젝트
//
//			String ZPRODM = IBAUtil.getStringValue(part, "PRODUCTMETHOD");
//			insertTable.setValue("ZPRODM", ZPRODM); // 제작방법
//
//			String ZDEPT = IBAUtil.getStringValue(part, "DEPTCODE");
//			insertTable.setValue("ZDEPT", ZDEPT); // 설계부서
//
//			// 샘플링 실제는 2D여부 확인해서 전송
//			insertTable.setValue("ZDWGNO", part.getNumber() + ".DRW"); // 도면번호
//
//			String v = part.getVersionIdentifier().getSeries().getValue() + "."
//					+ part.getIterationIdentifier().getSeries().getValue();
//			insertTable.setValue("ZEIVR", v); // 버전
//			// 테스트 용으로 전송
//			insertTable.setValue("ZPREPO", "X"); // 선구매필요
//
//			System.out.println("number = " + part.getNumber() + ", version = " + v);
//			idx++;
//		}
//
//		function.execute(destination);
//		JCoParameterList result = function.getExportParameterList();
//
////		JCoParameterFieldIterator it = result.getParameterFieldIterator();
////		while (it.hasNextField()) {
////			JCoField field = (JCoField) it.nextField();
////			System.out.println("field=" + field.getName());
////		}
//
//		Object r_type = result.getValue("EV_STATUS");
//		Object r_msg = result.getValue("EV_MESSAGE");
//		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
//		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);
//
//		// ERP 전송 로그 작성
//		System.out.println("SAP PART INTERFACE END!");
		System.exit(0);
	}

}
