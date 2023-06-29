package com.e3ps.erp.beans;


import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.PARTERP;

public class PARTERPData {
	
	public String oid;
	public String zifno;
	
	public String matnr;	//자재번호
	public String maktx;	//자재명
	public String meins;
	public String matkl;
	public String ntgew;
	public String gewei;
	public String wrkst;
	public String zspec;	//사양(SPECIFICATION)
	public String zfinsh;
	public String zmodel;	//프로젝트 코드 코드:명칭
	public String zprodm;	//제작방법  코드:명칭
	public String zdept;	//ZDEPT
	public String zmat1;
	public String zmat2;
	public String zmat3;
	public String aennr;
	public String zeivr;   //버전
	
	public String zifsta;
	public String zifmsg;
	public String createDate;
	public String returnZifsta;
	public String returnZifmsg;
	
	public String partOid;
	public String manufacture;
	public String revise;
	
	public PARTERPData(final PARTERP part) throws Exception {
		
		oid = CommonUtil.getOIDString(part);
		zifno = StringUtil.checkNull(part.getZifno());
		matnr = StringUtil.checkNull(part.getMatnr());
		maktx = StringUtil.checkNull(part.getMaktx());
		meins = StringUtil.checkNull(part.getMeins());
		matkl = StringUtil.checkNull(part.getMatkl());
		ntgew = StringUtil.checkNull(part.getNtgew());
		gewei = StringUtil.checkNull(part.getGewei());
		wrkst = StringUtil.checkNull(part.getWrkst());
		zspec = StringUtil.checkNull(part.getZspec());
		zfinsh = StringUtil.checkNull(part.getZfinsh());
		zmodel = StringUtil.checkNull(part.getZmodel());
		zprodm = StringUtil.checkNull(part.getZprodm());
		zdept = StringUtil.checkNull(part.getZdept());
		zmat1 = StringUtil.checkNull(part.getZmat1());
		zmat2 = StringUtil.checkNull(part.getZmat2());
		zmat3 = StringUtil.checkNull(part.getZmat3());
		aennr = StringUtil.checkNull(part.getAennr());
		zeivr = StringUtil.checkNull(part.getZeivr());
		zifsta = StringUtil.checkNull(part.getZifsta());
		zifmsg = StringUtil.checkNull(part.getZifmsg());
		createDate = DateUtil.getDateString(part.getPersistInfo().getCreateStamp(), "all");
		returnZifsta = StringUtil.checkNull(part.getReturnZifsta());
		returnZifmsg = StringUtil.checkNull(part.getReturnZifmsg());
		
		
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

	public String getMatnr() {
		return matnr;
	}

	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	public String getMaktx() {
		return maktx;
	}

	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}

	public String getMeins() {
		return meins;
	}

	public void setMeins(String meins) {
		this.meins = meins;
	}

	public String getMatkl() {
		return matkl;
	}

	public void setMatkl(String matkl) {
		this.matkl = matkl;
	}

	public String getNtgew() {
		return ntgew;
	}

	public void setNtgew(String ntgew) {
		this.ntgew = ntgew;
	}

	public String getGewei() {
		return gewei;
	}

	public void setGewei(String gewei) {
		this.gewei = gewei;
	}

	public String getWrkst() {
		return wrkst;
	}

	public void setWrkst(String wrkst) {
		this.wrkst = wrkst;
	}

	public String getZspec() {
		return zspec;
	}

	public void setZspec(String zspec) {
		this.zspec = zspec;
	}

	public String getZfinsh() {
		return zfinsh;
	}

	public void setZfinsh(String zfinsh) {
		this.zfinsh = zfinsh;
	}

	public String getZmodel() {
		return zmodel;
	}

	public void setZmodel(String zmodel) {
		this.zmodel = zmodel;
	}

	public String getZprodm() {
		return zprodm;
	}

	public void setZprodm(String zprodm) {
		this.zprodm = zprodm;
	}

	public String getZdept() {
		return zdept;
	}

	public void setZdept(String zdept) {
		this.zdept = zdept;
	}

	public String getZmat1() {
		return zmat1;
	}

	public void setZmat1(String zmat1) {
		this.zmat1 = zmat1;
	}

	public String getZmat2() {
		return zmat2;
	}

	public void setZmat2(String zmat2) {
		this.zmat2 = zmat2;
	}

	public String getZmat3() {
		return zmat3;
	}

	public void setZmat3(String zmat3) {
		this.zmat3 = zmat3;
	}

	public String getAennr() {
		return aennr;
	}

	public void setAennr(String aennr) {
		this.aennr = aennr;
	}

	public String getZeivr() {
		return zeivr;
	}

	public void setZeivr(String zeivr) {
		this.zeivr = zeivr;
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

	public String getPartOid() {
		return partOid;
	}

	public void setPartOid(String partOid) {
		this.partOid = partOid;
	}

	public String getManufacture() {
		return manufacture;
	}

	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}

	public String getRevise() {
		return revise;
	}

	public void setRevise(String revise) {
		this.revise = revise;
	}

	
	
	
}
