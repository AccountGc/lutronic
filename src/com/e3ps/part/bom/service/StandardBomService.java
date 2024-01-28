package com.e3ps.part.bom.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.WCUtil;

import net.sf.json.JSONObject;
import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.enterprise.CopyObjectInfo;
import wt.enterprise.EnterpriseHelper;
import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
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
	public Map<String, Object> paste(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid");
		ArrayList<String> arr = (ArrayList<String>) params.get("arr");
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
			for (String oid : arr) {
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
				if (poid.equals(oid)) {
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

			ArrayList<String> refList = new ArrayList<String>();
			for (String oid : list) {
				WTPart child = (WTPart) CommonUtil.getObject(oid);
				WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(workingCopy, child.getMaster());
				usageLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
				PersistenceHelper.manager.save(usageLink);
				refList.add("_" + usageLink.getPersistInfo().getObjectIdentifier().getId());
			}

			JSONObject node = BomHelper.manager.getNode(workingCopy);
			map.put("refList", refList);
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
	public Map<String, Object> drop(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid");
		ArrayList<String> arr = (ArrayList<String>) params.get("arr");
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
			ArrayList<String> refList = new ArrayList<String>();
			for (String oid : arr) {

				if (poid.equals(oid)) {
					throw new Exception("교체하려는 품목과 교체되는 대상 품목이 일치합니다.");
				}

				WTPart child = (WTPart) CommonUtil.getObject(oid);
				WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(workingCopy, child.getMaster());
				usageLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
				PersistenceHelper.manager.save(usageLink);
				refList.add("_" + usageLink.getPersistInfo().getObjectIdentifier().getId());
			}

			JSONObject node = BomHelper.manager.getNode(workingCopy);
			map.put("refList", refList);
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
			ArrayList<String> refList = new ArrayList<String>();
			WTPart child = (WTPart) CommonUtil.getObject(oid);
			WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(workingCopy, child.getMaster());
			usageLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			PersistenceHelper.manager.save(usageLink);
			refList.add("_" + usageLink.getPersistInfo().getObjectIdentifier().getId());

			JSONObject node = BomHelper.manager.getNode(workingCopy);
			map.put("refList", refList);
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
	public Map<String, Object> replace(Map<String, String> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String roid = (String) params.get("roid"); // 교체되는 대상
		String poid = (String) params.get("poid"); // 교체되는 대상의 부모 체크아웃 처리
		String oid = (String) params.get("oid"); // 교체하려는 대상
		Transaction trs = new Transaction();
		try {
			trs.start();

			if (roid.equals(oid)) {
				throw new Exception("교체하려는 품목과 교체되는 대상 품목이 일치합니다.");
			}

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
			// 링크를 제거하고 신규 링크를 생성한다.
			if (usageLink != null) {
				PersistenceHelper.manager.delete(usageLink);
			}

			WTPart replacePart = (WTPart) CommonUtil.getObject(oid);
			WTPartUsageLink newLink = WTPartUsageLink.newWTPartUsageLink(workingCopy, replacePart.getMaster());
			newLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			PersistenceHelper.manager.save(newLink);

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

	@Override
	public Map<String, Object> removeMultiLink(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid"); // 부모
		ArrayList<String> arr = (ArrayList<String>) params.get("arr"); // 자식
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart parent = (WTPart) CommonUtil.getObject(poid);
			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				parent = (WTPart) clink.getWorkingCopy();
			}

			for (String oid : arr) {
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				WTPartMaster child = part.getMaster();
				WTPartUsageLink usageLink = BomHelper.manager.getUsageLink(parent, child);
				if (usageLink != null) {
					PersistenceHelper.manager.delete(usageLink);
				}
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
	public Map<String, Object> update(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid"); // 부모
		String link = (String) params.get("link");
		String oid = (String) params.get("oid"); // 본인
		int value = (int) params.get("value");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart child = (WTPart) CommonUtil.getObject(oid);
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
			System.out.print("usageLink=" + usageLink);
			usageLink.setQuantity(Quantity.newQuantity(value, usageLink.getQuantity().getUnit()));
			PersistenceHelper.manager.modify(usageLink);

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
	public Map<String, Object> saveAs(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String location = (String) params.get("location");
		String oid = (String) params.get("oid"); // 본인
		String saveAsNum = (String) params.get("saveAsNum");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart part = (WTPart) CommonUtil.getObject(oid);
			RevisionControlled[] originals = new RevisionControlled[1];
			CopyObjectInfo[] copyInfoArray = null;

			originals[0] = part;

			// 체크
//			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(WTPartMaster.class, true);
//			QuerySpecUtils.toEquals(query, idx, WTPartMaster.class, WTPartMaster.NUMBER, part.getNumber());
//			QueryResult qr = PersistenceHelper.manager.find(query);
//			if(qr.size() > 0) {
//				map.put("exist", true);
//				return map;
//			}

			copyInfoArray = EnterpriseHelper.service.newMultiObjectCopy(originals);
			WTPart copy = (WTPart) copyInfoArray[0].getCopy();
			copy.setName(part.getName());
			copy.setNumber(saveAsNum);
			copy.setContainer(part.getContainer());

			copyInfoArray = EnterpriseHelper.service.saveMultiObjectCopy(copyInfoArray);

			Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.service.changeFolder((FolderEntry) copy, folder);

			map.put("copy", copy.getPersistInfo().getObjectIdentifier().getStringValue());

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
		map.put("exist", false);
		return map;
	}
}