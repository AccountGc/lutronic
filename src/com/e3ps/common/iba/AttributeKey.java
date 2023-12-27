package com.e3ps.common.iba;

public class AttributeKey {
	
	public static String[] ibas = new String[]{"part","cad","document","mold"};
	
	public class IBAKey {

		public static final String IBA_SPECIFICATION = "SPECIFICATION";
		public static final String IBA_WEIGHT = "WEIGHT";
		public static final String IBA_MANUFACTURE = "MANUFACTURE";
		public static final String IBA_MAT = "MAT";
		public static final String IBA_FINISH = "FINISH";
		public static final String IBA_REMARKS = "REMARKS";
		public static final String IBA_DEPTCODE = "DEPTCODE";
		public static final String IBA_MODEL = "MODEL";
		public static final String IBA_PRODUCTMETHOD = "PRODUCTMETHOD";
		public static final String IBA_MOLDTYPE = "MOLDTYPE";
		public static final String IBA_MOLDNUMBER = "MOLDNUMBER";
		public static final String IBA_MOLDCOST = "MOLDCOST";
		
		public static final String IBA_PARTNAME1 = "PARTNAME1";
		public static final String IBA_PARTNAME2 = "PARTNAME2";
		public static final String IBA_PARTNAME3 = "PARTNAME3";
		public static final String IBA_PARTNAME4 = "PARTNAME4";
		
		public static final String IBA_DOCUMENTNAME1 = "DOCUMENTNAME1";
		public static final String IBA_DOCUMENTNAME2 = "DOCUMENTNAME2";
		
		public static final String IBA_ECONO = "ECONO";
		public static final String IBA_ECODATE = "ECODATE";
		public static final String IBA_DSGN	= "DSGN";
		public static final String IBA_CHK = "CHK";
		public static final String IBA_APR = "APR";
		public static final String IBA_REV = "REV";
		public static final String IBA_DES = "DES";
		public static final String IBA_SCALE = "scale";
		
		public static final String IBA_CHANGEDATE 	= "CHANGEDATE";
		public static final String IBA_CHANGENO 	= "CHANGENO";
		
		public static final String IBA_INTERALNUMBER = "INTERALNUMBER";
		public static final String IBA_PRESERATION = "PRESERATION";
		public static final String IBA_APPROVALTYPE ="APPROVALTYPE";
	}
	
	public class CommonKey{
		public static final String COMMON_DEFAULT = "DEFAULT";
		public static final String COMMON_BATCH= "BATCH";
		
	}
	
	public class ECOKey{
		public static final String ECO_DEV = "DEV";      	//EO 개발
		public static final String ECO_PRODUCT= "PRODUCT"; 	//EO 양산
		public static final String ECO_CHANGE= "CHANGE";	//ECO 설계변경
	}
	
	public static class RohsKey{
		public static final String[] ROHS_CODE = {"TR", "DOC", "MSDS", "XRF"};
		public static final String[] ROHS_NAME = {"정밀분석 성적서", "보증서", "MSDS 성분 분석표", "XRF분석 성적서"};
	}
	/**
	 * 글로벌 속성 Part
	 * @author tusm
	 *
	 */
	public class PartKey{
		public static final String IBA_PARTTYPE 	= "PartType";
		public static final String IBA_PRODUCT 		= "Product";
		public static final String IBA_PROCESSTYPE 	= "ProcessType";
		
		public static final String IBA_PRODUCTTYPE = "ProductType";
		public static final String IBA_PRODUCTSUBTYPE = "ProductSubtype";
		public static final String IBA_PRODUCTDIMENSIONGROUP = "ProductDimensionGroup";
		
		public static final String IBA_PARTCHECK	= "PartCheck";
	}
	
	/**
	 * 글로벌 속성 Drawing
	 * @author tusm
	 *
	 */
	public class EPMKey{
		public static final String IBA_PARTNO 		= "PartNo";
		public static final String IBA_DWGNO 		= "DwgNo";
		public static final String IBA_DESCRIPTION 	= "Description";
		public static final String IBA_VERSION 		= "Version";
		public static final String IBA_DATE 		= "Date";
		public static final String IBA_APPROVAL 	= "Approval";
		public static final String IBA_CHECK		= "Check";
		public static final String IBA_DRAWING 		= "Drawing";
		
		public static final String IBA_MATERIAL 	= "Material";
		public static final String IBA_SPEC         = "SPEC";
		public static final String IBA_SURFACE 		= "Surface";
		public static final String IBA_SUBCONTRACT 	= "Subcontract";
		public static final String IBA_UNIT		    = "Unit";
		public static final String IBA_SUPPLIER 	= "Supplier";
		public static final String IBA_VENDOR 	    = "Vendor";
		public static final String IBA_WEIGHT 		= "Weight";
		public static final String IBA_COMMENT 		= "Comment";
		
		public static final String IBA_SEQCHECK 	= "SeqCheck";
		public static final String IBA_ERPCHECK 	= "ERPCheck";
		public static final String IBA_PARTCHECK	= "PartCheck";
	}
	
	public class DocKey{
		public static final String IBA_GRADE		= "Grade";
		public static final String IBA_PROJECTCODE  = "ProjectCode";
	}
	
	/**
	 * 글로벌 속성 Version
	 * @author tusm
	 *
	 */
	public class VersionKey{
		public static final String IBA_LATESTVERSIONFLAG = "LatestVersionFlag";
	}
	
	/**
	 * NumberCode RB
	 * @author tusm
	 *
	 */
	public class NumberCodeKey{
		
		public static final String NC_ISSUETYPE 		= "ISSUETYPE";
		public static final String NC_PARTGROUP 		= "PARTGROUP";
		public static final String NC_MODELGROUP 		= "MODELGROUP";
		public static final String NC_SUPPLIER 			= "SUPPLIER";
		public static final String NC_ECOTYPE 			= "ECOTYPE";
		public static final String NC_PRODUCTGROUP 		= "PRODUCTGROUP";
		public static final String NC_PRODUCTSERIES	 	= "PRODUCTSERIES";
		public static final String NC_ECRRESULT 		= "ECRRESULT";
		public static final String NC_PRIORITY 			= "PRIORITY";
		public static final String NC_APPLY 			= "APPLY";
		public static final String NC_GRADE 			= "GRADE";
		public static final String NC_VENDORGROUP 		= "VENDORGROUP";
		public static final String NC_DRAWINGTYPE		= "DRAWINGTYPE";
		public static final String NC_DISTRIBUTEMETHOD	= "DISTRIBUTEMETHOD";
		public static final String NC_GATE 				= "GATE";
		public static final String NC_PROJECTTYPE 		= "PROJECTTYPE";
		public static final String NC_PROJECTROLE 		= "PROJECTROLE";
		public static final String NC_DOCGRADE 			= "DOCGRADE";
		public static final String NC_PRODUCTTYPE 		= "PRODUCTTYPE";
		
	}
}


