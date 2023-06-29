package com.e3ps.erp.beans;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.BOMERP;


public class BOMERPData {
	
	public String oid;
	public String zifno;
	public String zitmsta;  //설계변경 CUD
	public String matnr;    //모품번
	public int posnr;
	public String idnrk;	//자품번
	public String menge;	//수량
	public String meins;	//단위
	public String aennr;
	public String zifsta;
	public String zifmsg;
	public String createDate;
	public String returnZifsta;
	public String returnZifmsg;
	
	public String pName;
	public String pOid;
	public String cName;
	public String cOid;
	public String pVer;
	public String cVer;
	
	public BOMERPData(final BOMERP bom) throws Exception {
		
		oid = CommonUtil.getOIDString(bom);
		zifno = StringUtil.checkNull(bom.getZifno());
		zitmsta = StringUtil.checkNull(bom.getZitmsta());//설계 변경 CUD
		matnr = StringUtil.checkNull(bom.getMatnr());  //모품목
		posnr = bom.getPosnr();
		idnrk = StringUtil.checkNull(bom.getIdnrk());  //자품목
		menge = StringUtil.checkNull(bom.getMenge());  //수량
		meins =  StringUtil.checkNull(bom.getMeins()); //단위
		aennr = StringUtil.checkNull(bom.getAennr());
		zifsta = StringUtil.checkNull(bom.getZifsta());
		zifmsg = StringUtil.checkNull(bom.getZifmsg());
		createDate = DateUtil.getDateString(bom.getPersistInfo().getCreateStamp(), "all");
		returnZifsta = StringUtil.checkNull(bom.getReturnZifsta());
		returnZifmsg = StringUtil.checkNull(bom.getReturnZifmsg());
		pVer = StringUtil.checkNull(bom.getPver());
		cVer = StringUtil.checkNull(bom.getCver());
		
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

	public String getZitmsta() {
		return zitmsta;
	}

	public void setZitmsta(String zitmsta) {
		this.zitmsta = zitmsta;
	}

	public String getMatnr() {
		return matnr;
	}

	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	public String getIdnrk() {
		return idnrk;
	}

	public void setIdnrk(String idnrk) {
		this.idnrk = idnrk;
	}

	public String getMenge() {
		return menge;
	}

	public void setMenge(String menge) {
		this.menge = menge;
	}

	public String getMeins() {
		return meins;
	}

	public void setMeins(String meins) {
		this.meins = meins;
	}

	public String getAennr() {
		return aennr;
	}

	public void setAennr(String aennr) {
		this.aennr = aennr;
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

	public int getPosnr() {
		return posnr;
	}

	public void setPosnr(int posnr) {
		this.posnr = posnr;
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

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public String getpOid() {
		return pOid;
	}

	public void setpOid(String pOid) {
		this.pOid = pOid;
	}

	public String getcOid() {
		return cOid;
	}

	public void setcOid(String cOid) {
		this.cOid = cOid;
	}

	public String getpVer() {
		return pVer;
	}

	public void setpVer(String pVer) {
		this.pVer = pVer;
	}

	public String getcVer() {
		return cVer;
	}

	public void setcVer(String cVer) {
		this.cVer = cVer;
	}
	
	
}
