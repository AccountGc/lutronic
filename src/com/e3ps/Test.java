package com.e3ps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.org.Department;
import com.e3ps.org.People;
import com.e3ps.part.service.PartHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTKeyedHashMap;
import wt.load.LoadUser;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		ArrayList<String> list = new ArrayList<>();

		// 위 부분은 엑셀 행을 읽어서 작업한다고 가정한다.
		// 2D, 3D모두 포함하여 작성한다.
		for (String s : list) {
			// 엑셀 각 행에 대한 열을 읽어서 작업한다고 가정한다.
			// 캐드 명은 서버에 유일하다

			String cadName = "test.part";
			EPMDocumentMaster m = get(cadName);
			int breakCount = 0; // 품목 이름, 번호 변경 한번을 위한 체크 변수
			if (m != null) {
				// 도면의 모든 버전에 대해서 속성값 생성 작업을 한다
				QueryResult rs = VersionControlHelper.service.allIterationsOf(m);
				while (rs.hasMoreElements()) {
					EPMDocument e = (EPMDocument) rs.nextElement();

					// 제우스에 있는 IBAUtil 사용하여 생성할 변수를 생성한다.
					// 생성전 값을 찾아서 있는지 없는지 체크해서 없을 경우만 생서한다.
					// ex
					String part_name = IBAUtil.getStringValue(e, "PART_NAME");
					if (part_name != null && part_name.equals("")) {
						IBAUtil.createIba(e, "string", "PART_NAME", "");
					}

					// 도면으로 부품을 찾는다 제우스에 있는 함수 사용한다.
					WTPart part = PartHelper.manager.getPart(e);
					if (part != null) {
						// 위와 마찬가지 작업을 진행한다.
						String wt_part_name = IBAUtil.getStringValue(part, "PART_NAME");
						if (wt_part_name != null && wt_part_name.equals("")) {
							IBAUtil.createIba(part, "string", "PART_NAME", "");
						}

						if (breakCount == 0) {
							WTPartMaster master = (WTPartMaster) part.getMaster();
							WTPartMasterIdentity identity = (WTPartMasterIdentity) master.getIdentificationObject();
							master.setName(""); // 변경할 이름
							master.setNumber(""); // 변경할 번호
							master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, identity);
							breakCount++;
						}
					}
				}

				// 도면 번호, 이름, 캐드명 변경
				m = (EPMDocumentMaster) PersistenceHelper.manager.refresh(m);
				EPMDocumentMasterIdentity identity = (EPMDocumentMasterIdentity) m.getIdentificationObject();
				m.setName(""); // 변경할 이름
				m.setNumber(""); // 변경할 번호
				m = (EPMDocumentMaster) IdentityHelper.service.changeIdentity(m, identity);

				WTKeyedHashMap map = new WTKeyedHashMap();
				map.put(m, "변경이름"); // ?? 채번꺼 참조
				EPMDocumentHelper.service.changeCADName(map);

			}
		}

		System.exit(0);
	}

	private static EPMDocumentMaster get(String cadName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocumentMaster.class, true);
		SearchCondition sc = new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.CADNAME, "=", cadName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			EPMDocumentMaster m = (EPMDocumentMaster) obj[0];
			return m;
		}
		return null;
	}
}