package com.e3ps.erp.beans;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.ECOERP;

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
	
		oid = CommonUtil.getOIDString(eco);
		zifno = StringUtil.checkNull(eco.getZifno());
		aennr = StringUtil.checkNull(eco.getAennr());
		aetxt = StringUtil.checkNull(eco.getAetxt());
		datuv = StringUtil.checkNull(eco.getDatuv());
		aegru =StringUtil.checkNull(eco.getAegru());
		zecmid = StringUtil.checkNull(eco.getZecmid());
		zifsta = StringUtil.checkNull(eco.getZifsta());
		zifmsg = StringUtil.checkNull(eco.getZifmsg());
		createDate = DateUtil.getDateString(eco.getPersistInfo().getCreateStamp(), "all");
		returnZifsta = StringUtil.checkNull(eco.getReturnZifsta());
		returnZifmsg = StringUtil.checkNull(eco.getReturnZifmsg());
	}


	public String getOid() {
		return oid;
	}


	public void setOid(String oid) {
		this.oid = oid;
	}


	public String getZifno() {
		return zifno;
	}


	public void setZifno(String zifno) {
		this.zifno = zifno;
	}


	public String getAennr() {
		return aennr;
	}


	public void setAennr(String aennr) {
		this.aennr = aennr;
	}


	public String getAetxt() {
		return aetxt;
	}


	public void setAetxt(String aetxt) {
		this.aetxt = aetxt;
	}


	public String getDatuv() {
		return datuv;
	}


	public void setDatuv(String datuv) {
		this.datuv = datuv;
	}


	public String getAegru() {
		return aegru;
	}


	public void setAegru(String aegru) {
		this.aegru = aegru;
	}


	public String getZecmid() {
		return zecmid;
	}


	public void setZecmid(String zecmid) {
		this.zecmid = zecmid;
	}


	public String getZifsta() {
		return zifsta;
	}


	public void setZifsta(String zifsta) {
		this.zifsta = zifsta;
	}


	public String getZifmsg() {
		return zifmsg;
	}


	public void setZifmsg(String zifmsg) {
		this.zifmsg = zifmsg;
	}


	public String getCreateDate() {
		return createDate;
	}


	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public String getReturnZifsta() {
		return returnZifsta;
	}


	public void setReturnZifsta(String returnZifsta) {
		this.returnZifsta = returnZifsta;
	}


	public String getReturnZifmsg() {
		return returnZifmsg;
	}


	public void setReturnZifmsg(String returnZifmsg) {
		this.returnZifmsg = returnZifmsg;
	}
	
	

}
