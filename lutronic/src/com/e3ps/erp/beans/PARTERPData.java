package com.e3ps.erp.beans;


import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.PARTERP;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
		
		setOid(CommonUtil.getOIDString(part));
		setZifno(StringUtil.checkNull(part.getZifno()));
		setMatnr(StringUtil.checkNull(part.getMatnr()));
		setMaktx(StringUtil.checkNull(part.getMaktx()));
		setMeins(StringUtil.checkNull(part.getMeins()));
		setMatkl(StringUtil.checkNull(part.getMatkl()));
		setNtgew(StringUtil.checkNull(part.getNtgew()));
		setGewei(StringUtil.checkNull(part.getGewei()));
		setWrkst(StringUtil.checkNull(part.getWrkst()));
		setZspec(StringUtil.checkNull(part.getZspec()));
		setZfinsh(StringUtil.checkNull(part.getZfinsh()));
		setZmodel(StringUtil.checkNull(part.getZmodel()));
		setZprodm(StringUtil.checkNull(part.getZprodm()));
		setZdept(StringUtil.checkNull(part.getZdept()));
		setZmat1(StringUtil.checkNull(part.getZmat1()));
		setZmat2(StringUtil.checkNull(part.getZmat2()));
		setZmat3(StringUtil.checkNull(part.getZmat3()));
		setAennr(StringUtil.checkNull(part.getAennr()));
		setZeivr(StringUtil.checkNull(part.getZeivr()));
		setZifsta(StringUtil.checkNull(part.getZifsta()));
		setZifmsg(StringUtil.checkNull(part.getZifmsg()));
		setCreateDate(DateUtil.getDateString(part.getPersistInfo().getCreateStamp(), "all"));
		setReturnZifsta(StringUtil.checkNull(part.getReturnZifsta()));
		setReturnZifmsg(StringUtil.checkNull(part.getReturnZifmsg()));
	}
	
}
