package com.e3ps.erp.beans;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.BOMERP;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
		
		setOid(CommonUtil.getOIDString(bom));
		setZifno(StringUtil.checkNull(bom.getZifno()));
		setZitmsta(StringUtil.checkNull(bom.getZitmsta()));//설계 변경 CUD
		setMatnr(StringUtil.checkNull(bom.getMatnr()));  //모품목
		setPosnr(bom.getPosnr());
		setIdnrk(StringUtil.checkNull(bom.getIdnrk()));  //자품목
		setMenge(StringUtil.checkNull(bom.getMenge()));  //수량
		setMeins(StringUtil.checkNull(bom.getMeins())); //단위
		setAennr(StringUtil.checkNull(bom.getAennr()));
		setZifsta(StringUtil.checkNull(bom.getZifsta()));
		setZifmsg(StringUtil.checkNull(bom.getZifmsg()));
		setCreateDate(DateUtil.getDateString(bom.getPersistInfo().getCreateStamp(), "all"));
		setReturnZifsta(StringUtil.checkNull(bom.getReturnZifsta()));
		setReturnZifmsg(StringUtil.checkNull(bom.getReturnZifmsg()));
		setPVer(StringUtil.checkNull(bom.getPver()));
		setCVer(StringUtil.checkNull(bom.getCver()));
		
	}
	
}
