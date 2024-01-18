<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.sap.service.SAPHelper"%>
<%@page import="com.e3ps.change.util.EChangeUtils"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="java.util.Date"%>
<%@page import="com.sap.conn.jco.JCoTable"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.e3ps.sap.conn.SAPDev600Connection"%>
<%@page import="com.sap.conn.jco.JCoDestinationManager"%>
<%@page import="com.sap.conn.jco.JCoFunction"%>
<%@page import="com.sap.conn.jco.JCoDestination"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.e3ps.sap.dto.SAPSendBomDTO"%>
<%@page import="com.sap.conn.jco.JCoParameterList"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "com.e3ps.change.EChangeOrder:208998227";
EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
send(eco);
%>


<%!private void send(EChangeOrder eco) throws Exception {
		JCoDestination destination = JCoDestinationManager.getDestination(SAPDev600Connection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_002");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트
		importTable.setValue("IV_TEST", "X"); // 플랜트

		int idx = 1;

		DecimalFormat df = new DecimalFormat("0000");
		Timestamp t = new Timestamp(new Date().getTime());
		String today = t.toString().substring(0, 10).replaceAll("-", "");

		JCoTable ecoTable = function.getTableParameterList().getTable("ET_ECM");
		ecoTable.appendRow();
		ecoTable.setValue("AENNR8", eco.getEoNumber()); // 변경번호 12자리??
		ecoTable.setValue("ZECMID", "ECO"); // EO/ECO 구분
		ecoTable.setValue("DATUV", today); // 보내는 날짜
		ecoTable.setValue("AEGRU", eco.getEoName()); // 변경사유 테스트 일단 한줄
		ecoTable.setValue("AETXT", eco.getEoName()); // 변경 내역 첫줄만 일단 테스트

		if (StringUtil.checkString(eco.getEoCommentA())) {
			String AETXT_L = eco.getEoCommentA() != null ? eco.getEoCommentA() : "";
			ecoTable.setValue("AETXT_L", AETXT_L.replaceAll("\n", "<br>"));
		} else {
			ecoTable.setValue("AETXT_L", ""); // 변경 내역 전체 내용
		}
		JCoTable bomTable = function.getTableParameterList().getTable("ET_BOM");
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		while (qr.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) qr.nextElement();
			WTPartMaster master = link.getPart();
			String version = link.getVersion();
			WTPart target = PartHelper.manager.getPart(master.getNumber(), version);
			boolean isPast = link.getPast();

			WTPart next_part = null;
			WTPart pre_part = null;

			boolean isRight = link.getRightPart();
			boolean isLeft = link.getLeftPart();
			// 오른쪽이면 다음 버전 품목을 전송해야한다.. 이게 맞는듯
			if (isLeft) {
				// 왼쪽이면 승인됨 데이터..그니깐 개정후 데이터를 보낸다 근데 변경점이 없지만 PDM상에서 버전은 올라간 상태
				next_part = (WTPart) EChangeUtils.manager.getNext(target);
				pre_part = target;
			} else if (isRight) {
				// 오른쪽 데이터면 애시당초 바귄 대상 품번 그대로 넣어준다..
				next_part = target;
				pre_part = SAPHelper.manager.getPre(target, eco);
			}

			ArrayList<SAPSendBomDTO> sendList = new ArrayList<SAPSendBomDTO>();
			// 둘다 있을 경우
			if (pre_part != null && next_part != null) {
				ArrayList<Object[]> rights = SAPHelper.manager.sendList(next_part);
				ArrayList<Object[]> lefts = SAPHelper.manager.sendList(pre_part);

				ArrayList<Map<String, Object>> addList = SAPHelper.manager.addList(lefts, rights);
				ArrayList<Map<String, Object>> removeList = SAPHelper.manager.removeList(lefts, rights);

				ArrayList<String> addKey = new ArrayList<String>();
				// 추가 항목 넣음
				for (Map<String, Object> add : addList) {
					String addOid = (String) add.get("oid");
					WTPart addPart = (WTPart) CommonUtil.getObject(addOid);
					// 부모_자식
					String key = next_part.getNumber() + "_" + addPart.getNumber();

					if (next_part.getNumber().startsWith("8")) {
						continue;
					}

					if (addPart.getNumber().startsWith("8")) {
						continue;
					}

					SAPSendBomDTO addDto = new SAPSendBomDTO();
					addDto.setParentPartNumber(null);
					addDto.setChildPartNumber(null);
					addDto.setNewParentPartNumber(next_part.getNumber());
					addDto.setNewChildPartNumber(addPart.getNumber());
					addDto.setQty((int) add.get("qty"));
					addDto.setUnit((String) add.get("unit"));
					addDto.setSendType("추가품목");
					addDto.setKey(key);
					if (addKey.contains(key)) {
						addKey.add(key);
						sendList.add(addDto);
					}
				}

				ArrayList<String> removeKey = new ArrayList<String>();
				// 삭제 항목 넣음
				for (Map<String, Object> remove : removeList) {
					String removeOid = (String) remove.get("oid");
					WTPart removePart = (WTPart) CommonUtil.getObject(removeOid);
					String key = pre_part.getNumber() + "_" + removePart.getNumber();

					if (pre_part.getNumber().startsWith("8")) {
						continue;
					}

					if (removePart.getNumber().startsWith("8")) {
						continue;
					}

					SAPSendBomDTO removeDto = new SAPSendBomDTO();
					removeDto.setParentPartNumber(pre_part.getNumber());
					removeDto.setChildPartNumber(removePart.getNumber());
					removeDto.setNewParentPartNumber(null);
					removeDto.setNewChildPartNumber(null);
					removeDto.setQty((int) remove.get("qty"));
					removeDto.setUnit((String) remove.get("unit"));
					removeDto.setSendType("삭제품");
					removeDto.setKey(key);

					if (removeKey.contains(key)) {
						removeKey.add(key);
						sendList.add(removeDto);
					}
				}

				// 변경 대상 리스트..
				ArrayList<SAPSendBomDTO> changeList = SAPHelper.manager.getOneLevel(next_part, eco);
				Iterator<SAPSendBomDTO> iterator = changeList.iterator();
				List<SAPSendBomDTO> itemsToRemove = new ArrayList<>();
				//				ArrayList<String> changeKey = new ArrayList<String>();
				while (iterator.hasNext()) {
					SAPSendBomDTO dto = iterator.next();
					dto.setSendType("변경품");
					String compNum = dto.getNewChildPartNumber();
					
// 					System.out.println("compNum="+compNum);
// 					if (compNum.startsWith("8")) {
// 						continue;
// 					}

					// addList에서 같은 newChildPartNumber를 찾으면 itemsToRemove에 추가
					for (Map<String, Object> addMap : addList) {
						String addOid = (String) addMap.get("oid");
						WTPart addPart = (WTPart) CommonUtil.getObject(addOid);
						if (addPart.getNumber().equals(compNum)) {
							itemsToRemove.add(dto);
							break; // 이미 찾았으니 더 이상 검색할 필요가 없음
						}
					}
				}

				// itemsToRemove에 해당하는 모든 아이템을 changeList에서 제거
				changeList.removeAll(itemsToRemove);

				// 위의 반복문에서 변경된 changeList를 sendList에 추가
				sendList.addAll(changeList);
			}

			for (SAPSendBomDTO dto : sendList) {

				System.out.println("전송타입 = " + dto.getSendType() + " || 이전부모품번 = " + dto.getParentPartNumber() + ", "
						+ " 이전자식품번 =  " + dto.getChildPartNumber() + ", 신규부모품번 = " + dto.getNewParentPartNumber()
						+ ", 신규자식품번 = " + dto.getNewChildPartNumber());

				bomTable.insertRow(idx);
				bomTable.setValue("AENNR8", eco.getEoNumber()); // 변경번호 12자리?
				bomTable.setValue("SEQNO", df.format(idx)); // 항목번호 ?? 고정인지.. 애매한데
				bomTable.setValue("MATNR_OLD", dto.getParentPartNumber()); // 이전 모품번
				bomTable.setValue("IDNRK_OLD", dto.getChildPartNumber()); // 이전 자품번
				bomTable.setValue("MATNR_NEW", dto.getNewParentPartNumber()); // 기존 모품번
				bomTable.setValue("IDNRK_NEW", dto.getNewChildPartNumber()); // 기존 자품번
				bomTable.setValue("MENGE", dto.getQty()); // 수량
				bomTable.setValue("MEINS", dto.getUnit()); // 단위
				bomTable.setValue("AENNR12", eco.getEoNumber() + df.format(idx)); // 변경번호 12자리
				idx++;
			}
		}
		function.execute(destination);

		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");

		JCoTable rtnTable = function.getTableParameterList().getTable("ET_BOM");
		rtnTable.firstRow();
		for (int i = 0; i < rtnTable.getNumRows(); i++, rtnTable.nextRow()) {
			Object ZIFSTA = rtnTable.getValue("ZIFSTA");
			Object ZIFMSG = rtnTable.getValue("ZIFMSG");
			Object MATNR_OLD = rtnTable.getValue("MATNR_OLD");
			Object IDNRK_OLD = rtnTable.getValue("IDNRK_OLD");
			Object MATNR_NEW = rtnTable.getValue("MATNR_NEW");
			Object IDNRK_NEW = rtnTable.getValue("IDNRK_NEW");
			System.out.println("이전부모 =  " + MATNR_OLD + ", 이전자식 = " + IDNRK_OLD + ", 신규부모 = " + MATNR_NEW + ", 신규자식 = "
					+ IDNRK_NEW + ", ZIFSTA=" + ZIFSTA + ", ZIFMSG=" + ZIFMSG);
		}
		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);

	}%>