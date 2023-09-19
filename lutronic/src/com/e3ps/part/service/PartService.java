package com.e3ps.part.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.query.QuerySpec;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.beans.BatchDownData;
import com.e3ps.common.beans.ResultData;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;

@RemoteInterface
public interface PartService {
	
	/**
	 * 
	 * 
	 * 
	 */
	
//	Map<String, Object> requestPartMapping(Map<String, Object> params);
	
	void create(Map<String,Object> map) throws Exception;
	
	Map<String, Object> updatePartAction(Map<String, Object> params);
	
	String createPackagePartAction(HttpServletRequest request, HttpServletResponse response);
	
	ResultData changeNumber(HttpServletRequest req);
	
	List<Map<String,Object>> partBomListGrid(String oid) throws Exception ;
	
	ResultData partStateChange(HttpServletRequest request, HttpServletResponse response);
	
	ResultData updatePackagePartAction(HttpServletRequest request, HttpServletResponse response);
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	

	Map<String, Object> listPartAction(HttpServletRequest request, HttpServletResponse response) throws Exception;


	Vector<String> getQuantityUnit();

	List<PartData> include_PartList(String oid, String moduleType) throws Exception;

	Map<String, String> getAttributes(String oid) throws Exception;

	Map<String, Object> delete(Map<String, Object> params) throws Exception;

	Hashtable modify(Map hash, String[] loc, String[] deloc) throws Exception;

	Map<String, Object> getPartTreeAction(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	Map<String, Object> getBaseLineCompare(HttpServletRequest request, HttpServletResponse response) throws Exception;


	WTPart getPart(String number, String version) throws Exception;

	QuerySpec getEPMNumber(String number) throws Exception;

	WTPart getPart(String number) throws Exception;

	String excelBomLoadAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	WTPart include_ChangePartList(EChangeOrder eco);

	List<Map<String, Object>> partChange(String partOid) throws Exception;

	List<Map<String, Object>> partExpandAction(String partOid, String moduleType, String desc) throws Exception;

	Map<String, Object> selectEOPartAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	List<Map<String,Object>> viewPartBomAction(HttpServletRequest request, HttpServletResponse response) throws Exception;

	Map<String, Object> searchSeqAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	List<PartData> include_partLink(String module, String oid);

	ResultData linkPartAction(HttpServletRequest request, HttpServletResponse response);

	ResultData deletePartLinkAction(HttpServletRequest request, HttpServletResponse response);

	List<Map<String, Object>> partInstanceGrid(String oid) throws Exception;

	List<Map<String, Object>> listAUIPartAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	//Map<String, String> createProduct(Map<String, Object> hash) throws Exception;
	//boolean isDubleCheck(String number);
	//Map<String, Object> getBaselineinfo(String oid, String oid2, String baseline, String baseline2) throws Exception;

	void partTreeSelectAttachDown(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> param)
			throws Exception;

	void partReName(WTPart part, String changeName) throws Exception;

	ResultData getBaseLineCompareExcelDown(HttpServletRequest request,
			HttpServletResponse response);

	List<BatchDownData> getEPMBatchDownList(List<BatchDownData> list,
			List<WTPart> partList, String describe) throws Exception;

	ResultData batchBomDrawingDownAction(String oid, String describe,
			String ecoOid);

	ResultData attributeCleaning(Map<String, Object> param) throws Exception;

	ResultData batchBomSelectDownAction(String oid,
			List<Map<String, Object>> itemList, String describe,
			String downType, String describeType);

	ResultData updateAUIPackagePartAction(Map<String, Object> param);

	ResultData createAUIPackagePartAction(HttpServletRequest request,
			HttpServletResponse response);

	ResultData updateAUIPartChangeAction(Map<String, Object> param);

	Map<String, Object> listPagingAUIPartAction(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public void createComments(Map<String, Object> params) throws Exception;
	
	public void updateComments(Map<String, Object> params) throws Exception;
	
	public void deleteComments(String oid) throws Exception;

	void batch(Map<String, Object> params) throws Exception;
	
	public  Map<String, Object> partCheckIn(Map<String, Object> params) throws Exception;
	
	public  Map<String, Object> partCheckOut(Map<String, Object> params) throws Exception;
	
	
}
