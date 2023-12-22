package com.e3ps.part.bom.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;

import net.sf.json.JSONObject;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardBomService extends StandardManager implements BomService {

	public static StandardBomService newStandardBomService() throws WTException {
		StandardBomService instance = new StandardBomService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> removeLink(Map<String, String> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid"); // 부모
		String oid = (String) params.get("oid"); // 자식
//		String link = (String) params.get("link");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart parent = (WTPart) CommonUtil.getObject(poid);
			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				parent = (WTPart) clink.getWorkingCopy();
			}

			WTPart part = (WTPart) CommonUtil.getObject(oid);
			WTPartMaster child = part.getMaster();

			WTPartUsageLink usageLink = BomHelper.manager.getUsageLink(parent, child);
			if (usageLink != null) {
				PersistenceHelper.manager.delete(usageLink);
			}

			JSONObject node = BomHelper.manager.getNode(parent);
			map.put("resNode", node);

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
		return map;
	}

	@Override
	public Map<String, Object> undocheckout(String oid) throws Exception {
		// 화면에서 막기에 체크 안한다..
		Map<String, Object> map = new HashMap<>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart part = (WTPart) CommonUtil.getObject(oid);
			WTPart origin = (WTPart) WorkInProgressHelper.service.undoCheckout(part);

			JSONObject node = BomHelper.manager.getNode(origin);
			map.put("resNode", node);

			trs.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> checkout(String oid) throws Exception {
		Map<String, Object> map = new HashMap<>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart part = (WTPart) CommonUtil.getObject(oid);

			WTPart workingCopy = null;
			if (!WorkInProgressHelper.isCheckedOut(part)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(part, cFolder, "");
				workingCopy = (WTPart) clink.getWorkingCopy();
			}

			if (workingCopy != null) {
				JSONObject node = BomHelper.manager.getNode(workingCopy);
				map.put("resNode", node);
			} else {
				JSONObject node = BomHelper.manager.getNode(part);
				map.put("resNode", node);
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}

		return map;
	}

	@Override
	public Map<String, Object> checkin(String oid) throws Exception {
		Map<String, Object> map = new HashMap<>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart part = (WTPart) CommonUtil.getObject(oid);
			if (WorkInProgressHelper.isCheckedOut(part)) {
				WTPart newPart = (WTPart) WorkInProgressHelper.service.checkin(part, "");
				JSONObject node = BomHelper.manager.getNode(newPart);
				map.put("resNode", node);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> paste(Map<String, String> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid");
		String oid = (String) params.get("oid"); // 복붙되는놈
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart parent = (WTPart) CommonUtil.getObject(poid);
			WTPart workingCopy = null;
			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				workingCopy = (WTPart) clink.getWorkingCopy();
			} else {
				workingCopy = parent;
			}

			// 복사본에... 생성
			WTPart child = (WTPart) CommonUtil.getObject(oid);
			WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(workingCopy, child.getMaster());
			usageLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			PersistenceHelper.manager.save(usageLink);

			JSONObject node = BomHelper.manager.getNode(workingCopy);
			map.put("resNode", node);

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
		return map;
	}

	@Override
	public Map<String, Object> exist(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid");
//		String oid = (String) params.get("oid");
		ArrayList<String> list = (ArrayList<String>) params.get("list");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart parent = (WTPart) CommonUtil.getObject(poid);
			for (String oid : list) {
				if (poid.trim().equals(oid.trim())) {
					throw new Exception("추가 하하려는 품목중에 상위 품목과 동일한 품목이 존재합니다.");
				}
			}

			WTPart workingCopy = null;
			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				workingCopy = (WTPart) clink.getWorkingCopy();
			} else {
				workingCopy = parent;
			}

			// 복사본에... 생성

			for (String oid : list) {
				WTPart child = (WTPart) CommonUtil.getObject(oid);
				WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(workingCopy, child.getMaster());
				usageLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
				PersistenceHelper.manager.save(usageLink);
			}

			JSONObject node = BomHelper.manager.getNode(workingCopy);
			map.put("resNode", node);

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
		return map;
	}

	@Override
	public Map<String, Object> drop(Map<String, String> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid");
		String oid = (String) params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart parent = (WTPart) CommonUtil.getObject(poid);
			WTPart workingCopy = null;
			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				workingCopy = (WTPart) clink.getWorkingCopy();
			} else {
				workingCopy = parent;
			}

			// 복사본에... 생성
			WTPart child = (WTPart) CommonUtil.getObject(oid);
			WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(workingCopy, child.getMaster());
			usageLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			PersistenceHelper.manager.save(usageLink);

			JSONObject node = BomHelper.manager.getNode(workingCopy);
			map.put("resNode", node);

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
		return map;
	}

	@Override
	public Map<String, Object> append(Map<String, String> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid");
		String oid = (String) params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart parent = (WTPart) CommonUtil.getObject(poid);
			WTPart workingCopy = null;
			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				workingCopy = (WTPart) clink.getWorkingCopy();
			} else {
				workingCopy = parent;
			}

			// 복사본에... 생성
			WTPart child = (WTPart) CommonUtil.getObject(oid);
			WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(workingCopy, child.getMaster());
			usageLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			PersistenceHelper.manager.save(usageLink);

			JSONObject node = BomHelper.manager.getNode(workingCopy);
			map.put("resNode", node);

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
		return map;
	}

	@Override
	public Map<String, Object> replace_exist(Map<String, String> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String roid = (String) params.get("roid"); // 교체되는 대상
		String poid = (String) params.get("poid"); // 교체되는 대상의 부모 체크아웃 처리
		String oid = (String) params.get("oid"); // 교체하려는 대상
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart child = (WTPart) CommonUtil.getObject(roid);
			WTPart parent = (WTPart) CommonUtil.getObject(poid);
			WTPart workingCopy = null;
			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				workingCopy = (WTPart) clink.getWorkingCopy();
			} else {
				workingCopy = parent;
			}

			WTPartUsageLink usageLink = BomHelper.manager.getUsageLink(workingCopy, child.getMaster());
			if (usageLink != null) {
				// 자식을 교체한다?
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				usageLink.setRoleBObject(part.getMaster());
				PersistenceHelper.manager.modify(usageLink);
			}

			// 복사본에... 생성

			JSONObject node = BomHelper.manager.getNode(workingCopy);
			map.put("resNode", node);

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
		return map;
	}
}
