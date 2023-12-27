package com.e3ps.erp.beans;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.ECOERP;

import lombok.Getter;

import lombok.Getter;
import lombok.Setter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ECOERPData {
	
	public String oid;
	public String zifno;
	public String aennr;
	public String aetxt;
	public String datuv;
	public String aegru;
	public String zecmid;
	public String zifsta;
	public String zifmsg;
	public String createDate;
	public String returnZifsta;
	public String returnZifmsg;
	
	
	public ECOERPData(final ECOERP eco) throws Exception {
	
		setOid(CommonUtil.getOIDString(eco));
		setZifno(StringUtil.checkNull(eco.getZifno()));
		setAennr(StringUtil.checkNull(eco.getAennr()));
		setAetxt(StringUtil.checkNull(eco.getAetxt()));
		setDatuv(StringUtil.checkNull(eco.getDatuv()));
		setAegru(StringUtil.checkNull(eco.getAegru()));
		setZecmid(StringUtil.checkNull(eco.getZecmid()));
		setZifsta(StringUtil.checkNull(eco.getZifsta()));
		setZifmsg(StringUtil.checkNull(eco.getZifmsg()));
		setCreateDate(DateUtil.getDateString(eco.getPersistInfo().getCreateStamp(), "all"));
		setReturnZifsta(StringUtil.checkNull(eco.getReturnZifsta()));
		setReturnZifmsg(StringUtil.checkNull(eco.getReturnZifmsg()));
	}

}
