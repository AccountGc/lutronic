package com.e3ps.change.eo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eo.dto.EoDTO;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.WCUtil;
import com.e3ps.part.service.PartSearchHelper;

import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEoService extends StandardManager implements EoService {

	public static StandardEoService newStandardEoService() throws WTException {
		StandardEoService instance = new StandardEoService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EoDTO dto) throws Exception {
		ArrayList<Map<String, String>> rows104 = dto.getRows104();
		Transaction trs = new Transaction();
		try {
			trs.start();

			String type = "E";
			if (dto.getEoType().equals("DEV")) {
				type = "D";
			}
			String number = type + DateUtil.getCurrentDateString("ym");
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "EChangeOrder", EChangeOrder.EO_NUMBER);

			number = number + seqNo;

			EChangeOrder eo = EChangeOrder.newEChangeOrder();

			eo.setEoName(dto.getName());
			eo.setEoNumber(number);
			eo.setEoType(dto.getEoType());
			eo.setEoCommentA(dto.getEoCommentA());
			eo.setEoCommentB(dto.getEoCommentB());
			eo.setEoCommentC(dto.getEoCommentC());
//			eo.setOwner(SessionHelper.manager.getPrincipalReference()); // 필요 없는 라인..

			String location = "/Default/설계변경/EO";
			String lifecycle = "LC_ECO";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) eo, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(eo,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			eo = (EChangeOrder) PersistenceHelper.manager.save(eo);

			// 완제품 링크 및 검증
			validateAndCompleteSave(eo, rows104);

			// 완제품 생성
//	    	createCompleteLink(eco, completeOids);

			// 첨부파일
//			CommonContentHelper.service.attach(eco, null, secondarys);

			// 활동 생성
//	    	boolean isActivity = ECAHelper.service.createActivity(req, eco);

			// eco 상태 설정
			/*
			 * if(isActivity){
			 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
			 * State.toState("ACTIVITY")); }else{
			 * LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco,
			 * State.toState("APPROVE_REQUEST")); }
			 */

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}

	}

	private void validateAndCompleteSave(EChangeOrder eo, ArrayList<Map<String, String>> rows104) throws Exception {
		// 완제품 연결
		for (Map<String, String> map : rows104) {
			String oid = map.get("oid");
			WTPart part = (WTPart) CommonUtil.getObject(oid);

		}
	}

	List<WTPart> list = new ArrayList<WTPart>();

	boolean isEO = !eco.getEoType().equals(ECOKey.ECO_CHANGE);for(
	int i = 0;partOids!=null&&i<partOids.length;i++)
	{

		WTPart part = (WTPart) CommonUtil.getObject(partOids[i]);

		if (isEO) {
			boolean isSelect = PartSearchHelper.service.isSelectEO(part, eco.getEoType());
			if (!isSelect) {
				throw new Exception(Message.get(part.getNumber() + "은 EO,ECO가 진행중입니다."));
			}
		}
		list.add(part);
	}

	createCompleteLink(eco, list);
	}
}
