package com.e3ps.change.ecpr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.ecpr.column.EcprColumn;
import com.e3ps.change.ecpr.dto.EcprDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.org.People;

import wt.fc.PagingQueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class EcprHelper {

	public static final EcprService service = ServiceFactory.getService(EcprService.class);
	public static final EcprHelper manager = new EcprHelper();
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception{
		Map<String, Object> map = new HashMap<>();
		ArrayList<EcprColumn> list = new ArrayList<>();
		
		QuerySpec qs = null; 
		try{
			String name = StringUtil.checkNull((String)params.get("name"));
			String number = StringUtil.checkNull((String)params.get("number"));
			
			String createdFrom = StringUtil.checkNull((String)params.get("createdFrom"));
			String createdTo = StringUtil.checkNull((String)params.get("createdTo"));
			String creator = StringUtil.checkNull((String)params.get("creator"));
			String state = StringUtil.checkNull((String)params.get("state"));
			
			//ECR 속성
			String writedFrom = StringUtil.checkNull((String)params.get("writedFrom"));
			String writedTo = StringUtil.checkNull((String)params.get("writedTo"));
			String approveFrom = StringUtil.checkNull((String)params.get("approveFrom"));
			String approveTo = StringUtil.checkNull((String)params.get("approveTo"));
			
			String createDepart = StringUtil.checkNull((String)params.get("createDepart"));
			String writer = StringUtil.checkNull((String)params.get("writer"));
			String proposer = StringUtil.checkNull((String)params.get("proposer"));
			String model = StringUtil.checkNull((String)params.get("model"));
			
			qs = new QuerySpec(); 
			Class ecrClass = ECPRRequest.class;
			int ecoIdx = qs.appendClassList(ecrClass, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
			}
			
			//등록일
			if(createdFrom.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(createdFrom)), new int[] {ecoIdx});
			}
			
			//등록일
			if(createdTo.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(createdTo)), new int[] {ecoIdx});
			}
			
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
			}
			
			//등록자//creator.key.id
			if(creator.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(creator);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(ecrClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
			}
			
			//작성일
			if(writedFrom.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.CREATE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL , writedFrom), new int[] {ecoIdx});
			}
			
		
			if(writedTo.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.CREATE_DATE, SearchCondition.LESS_THAN_OR_EQUAL , writedTo), new int[] {ecoIdx});
			}
			
			//승인일
			if(approveFrom.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.APPROVE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL , approveFrom), new int[] {ecoIdx});
			}
			
		
			if(approveTo.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.APPROVE_DATE, SearchCondition.LESS_THAN_OR_EQUAL , approveTo), new int[] {ecoIdx});
			}
			
			//작성부서
			if(createDepart.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.CREATE_DEPART, SearchCondition.LIKE , "%"+createDepart+"%", false), new int[] {ecoIdx});
			}
			
			//작성자
			if(writer.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.WRITER, SearchCondition.LIKE , "%"+writer+"%", false), new int[] {ecoIdx});
			}
			
			//제안자
			if(proposer.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.PROPOSER, SearchCondition.LIKE , "%"+proposer+"%", false), new int[] {ecoIdx});
			}
			
			
			//제품명
			if(model.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.MODEL, SearchCondition.EQUAL, model, false), new int[] {ecoIdx});
			}
			
			//CR/ECPR 구분
			if( qs.getConditionCount() > 0 ) {
				qs.appendAnd();
			}
			qs.appendWhere(new SearchCondition(ecrClass, ECPRRequest.EO_TYPE, SearchCondition.EQUAL, "ECPR", false), new int[] {ecoIdx});
			
			qs.appendOrderBy(new OrderBy(new ClassAttribute(ecrClass, "thePersistInfo.modifyStamp"), true), new int[] { ecoIdx }); 
			PageQueryUtils pager = new PageQueryUtils(params, qs);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EcprColumn dto = new EcprColumn(obj);
				list.add(dto);
			}
			map.put("list", list);
			map.put("topListCount", pager.getTotal());
			map.put("pageSize", pager.getPsize());
			map.put("total", pager.getTotalSize());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * 모델명 복수개로 인해서 처리 하는 함수
	 */
	public String displayToModel(String model) throws Exception {
		String display = "";
		if(model != null) {
			String[] ss = model.split(",");
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				if (ss.length - 1 == i) {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "MODEL");
				} else {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "MODEL") + ",";
				}
			}			
		}
		return display;
	}
	
	/**
	 * 변경구분 복수개로 인해서 처리 하는 함수
	 */
	public String displayToSection(String section) throws Exception {
		String display = "";
		if(section != null) {
			String[] ss = section.split(",");
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				if (ss.length - 1 == i) {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "CHANGESECTION");
				} else {
					display += NumberCodeHelper.manager.getNumberCodeName(s, "CHANGESECTION") + ",";
				}
			}
		}
		return display;
	}
	
	/**
	 * 작성부서 코드 -> 값
	 */
	public String displayToDept(String dept) throws Exception {
		return NumberCodeHelper.manager.getNumberCodeName(dept, "DEPTCODE");
	}
}
