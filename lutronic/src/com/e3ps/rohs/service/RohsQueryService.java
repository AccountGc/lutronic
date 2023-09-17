package com.e3ps.rohs.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.part.dto.PartData;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSContHolder;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.RepresentToLink;
import com.e3ps.rohs.dto.RohsData;

import wt.enterprise.RevisionControlled;
import wt.part.WTPart;
import wt.query.QuerySpec;

public interface RohsQueryService {
	QuerySpec getListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	/**
	 * 물질에서 ROHSContHOLder 가져오기
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	List<ROHSContHolder> getROHSContHolder(String oid) throws Exception;
	
	/**
	 * 물질과 물질의 관계 링크 
	 * @param represent 대표 물질
	 * @return
	 * @throws Exception
	 */
	List<RepresentToLink> getRepresentLink(ROHSMaterial represent)
			throws Exception;
	
	/**
	 * 물질과 물질의 관계 리스트
	 * @param rohs
	 * @param roleType  구성물질 composition,대표물질 represent
	 * @return
	 * @throws Exception
	 */
	List<RohsData> getRepresentToLinkList(ROHSMaterial rohs, String roleType)
			throws Exception;

	/**
	 * ROHS의 연관 부품
	 * @param rohs
	 * @return
	 * @throws Exception
	 */
	List<PartData> getROHSToPartList(ROHSMaterial rohs) throws Exception;
	
	/**
	 * Part에서 연관 ROHS
	 * @param part
	 * @return
	 * @throws Exception
	 */
	List<RohsData> getPartToROHSList(WTPart part) throws Exception;
	
	/**
	 * ROHS에서 연관 PART  PartToRohsLink 
	 * @param rev
	 * @return
	 * @throws Exception
	 */
	List<PartToRohsLink> getPartToRohsLinkList(RevisionControlled rev)
			throws Exception;
	
	QuerySpec listRoHSDataAction(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	List<Map<String,Object>> childPartPutMap(WTPart part, List<Map<String,Object>> list,int level) throws Exception;

	int duplicateName(String rohsName) throws Exception;

	List<RohsData> getPartROHSList(WTPart part, boolean islastversion)
			throws Exception;

	List<PartData> getROHSToPartList(ROHSMaterial rohs, boolean islastversion)
			throws Exception;

	boolean duplicateLink(String partOid, String rohsOid) throws Exception;

	boolean duplicateNumber(String partOid, String rohsNumber) throws Exception;

	List<RohsData> getRepresentStructure(ROHSMaterial rohs,
			List<RohsData> representList) throws Exception;

	
}
