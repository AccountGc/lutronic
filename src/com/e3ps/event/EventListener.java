package com.e3ps.event;

import java.sql.Timestamp;
import java.util.Date;

import com.e3ps.common.iba.IBAUtil;
import com.e3ps.org.People;
import com.e3ps.org.WTUserPeopleLink;
import com.ibm.icu.text.DecimalFormat;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.events.KeyedEvent;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceManagerEvent;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceEventListenerAdapter;
import wt.session.SessionHelper;
import wt.vc.VersionControlServiceEvent;
import wt.vc.wip.WorkInProgressServiceEvent;

public class EventListener extends ServiceEventListenerAdapter {

	private static final String POST_STORE = PersistenceManagerEvent.POST_STORE;
	private static final String POST_MODIFY = PersistenceManagerEvent.POST_MODIFY;
	private static final String STATE_CHANGE = LifeCycleServiceEvent.STATE_CHANGE;
	private static final String POST_CHECKIN = WorkInProgressServiceEvent.POST_CHECKIN;
	private static final String NEW_VERSION = VersionControlServiceEvent.NEW_VERSION;

	public EventListener(String s) {
		super(s);
	}

	public void notifyVetoableEvent(Object obj) throws Exception {
		if (!(obj instanceof KeyedEvent)) {
			return;
		}

		KeyedEvent keyedEvent = (KeyedEvent) obj;
		Object target = keyedEvent.getEventTarget();
		String type = keyedEvent.getEventType();

		System.out.println("type=" + type + ", target=" + target);

		if (target instanceof EPMDocument) {
			EPMDocument e = (EPMDocument) target;
			System.out.println("첫번째 체크인 일경우 머가 나오는지type=" + type);

			if (type.equals(POST_MODIFY)) {
				System.out.println("loc=" + e.getLocation());
			}

			if (type.equals(NEW_VERSION)) {
				System.out.println("loc2=" + e.getLocation());
			}

			if (type.equals(POST_STORE)) {
				System.out.println("loc3=" + e.getLocation());
			}

		}
//			if (type.equals(POST_CHECKIN)) {
//
//				EPMDocument epm = (EPMDocument) target;
//				String autoNumber = IBAUtil.getStringValue(epm, "AUTONUMBER"); // N=
//				boolean isAuto = !"N".equalsIgnoreCase(autoNumber);
//				// 채번
//				if (isAuto) {
//
//					// wt.pdmlink.PDMLinkProduct
//					// wt.library.WTLibrary;
//					String containerRef = epm.getContainer().toString();
//					System.out.println("containerRef=" + containerRef);
//					boolean isProduct = containerRef.indexOf("PDMLinkProduct") > -1;
//
//					Timestamp t = new Timestamp(new Date().getTime());
//					String year = t.toString().substring(0, 2); // 년도
//
//					String changeNumber = "";
//
//					DecimalFormat df = new DecimalFormat("######");
//
//					// 제품일경우
//					if (isProduct) {
//
//						// 접속한 사용자
//						WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
//
//						QueryResult qr = PersistenceHelper.manager.navigate(sessionUser, "people",
//								WTUserPeopleLink.class);
//						People p = null;
//						String groupCode = "";
//						if (qr.hasMoreElements()) {
//							p = (People) qr.nextElement();
////					groupCode = p.getGroupCode();
//						}
//
//						String suffix = "";
//						String nextNumber = "";
//						
//						changeNumber = groupCode + year; // EH24
//
//						QuerySpec query = new QuerySpec();
//						int idx = query.appendClassList(EPMDocumentMaster.class, true);
//						SearchCondition sc = new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER,
//								"LIKE", changeNumber + "%");
//						query.appendWhere(sc, new int[] { idx });
//						ClassAttribute ca = new ClassAttribute(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER);
//						OrderBy by = new OrderBy(ca, false);
//						QueryResult rs = PersistenceHelper.manager.find(query);
//
//						// 값이 없다면
//						if (rs.size() == 0) {
//							suffix = "000001";
//						}
//
//						if (rs.hasMoreElements()) {
//							Object[] oo = (Object[]) rs.nextElement();
//							EPMDocumentMaster m = (EPMDocumentMaster) oo[0];
//
//							// EH24000001H
//							String number = m.getNumber();
//							String ss = number.substring(4, 10);
//							System.out.println("ss=" + ss); // 0000001
//							// 000001 +1 = 000002
//							// 000001 +1 = 0000011
//
//							int next = Integer.parseInt(ss) + 1; // 000002 X, 2
//							nextNumber = df.format(next); // 000002
//						}
//						
//						changeNumber
//
//						// EH + 년도 + 000001 + H = 제품
//
//					} else {
//						// 라이브러리
//					}
//
//					// L + A + 년도 + 000001 + H = 라이브러리
//
//					// 채번 관련
//
//				}
//
//			}
//		}

		// 사용자 싱크
		if (target instanceof WTUser) {
			WTUser wtUser = (WTUser) target;
			if (type.equals(POST_STORE)) {
				EventHelper.service.create(wtUser);
			} else if (type.equals(POST_MODIFY)) {
				EventHelper.service.modify(wtUser);
			}
		}
	}
}
