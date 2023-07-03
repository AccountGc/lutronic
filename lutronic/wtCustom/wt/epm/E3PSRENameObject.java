package wt.epm;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.poi.ss.formula.functions.Count;

import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMasterIdentity;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;

public class E3PSRENameObject implements wt.method.RemoteAccess, java.io.Serializable {
	
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static E3PSRENameObject manager= new E3PSRENameObject();
	
	/**
	 * EPMDocumetn number,name,CadName 변경
	 * @param epm
	 * @param changeNumber
	 * @param changeName
	 * @param changeCadName
	 * @return
	 */
	public void EPMReName(EPMDocument epm,String changeNumber,String changeName, String changeCadName,boolean isDisuse) throws Exception{
		
		
			//System.out.println("[E3PSRENameObject] EPMReName = " + epm.getNumber() +":" + isDisuse);
			
			changeNumber = StringUtil.checkNull(changeNumber);
			changeName = StringUtil.checkNull(changeName);
			changeCadName = StringUtil.checkNull(changeCadName);
            
			if(changeNumber.length()==0 && changeName.length()==0 && changeCadName.length()==0) return;
			
			if(isDisuse){
				changeNumber = this.getDelteNumber(changeNumber, epm);
				changeCadName = this.getDelteCadName(changeCadName);
			}
			
			Identified identified = (Identified) epm.getMaster();
            EPMDocumentMasterIdentity emid = (EPMDocumentMasterIdentity) identified.getIdentificationObject();

            if(changeNumber.length()>0) emid.setNumber(changeNumber.trim());
            if(changeName.length()>0) emid.setName(changeName.trim());
            if(changeCadName.length()>0) emid.setCADName(changeCadName.trim());
            
            IdentityHelper.service.changeIdentity(identified, emid);
            PersistenceHelper.manager.refresh(epm);
            
            if(changeCadName.length()>0) this.cadNameSpaceChange(epm);
            
        
	}
	
	/**
	 * WTPart number,name 변경
	 * @param part
	 * @param changeNumber
	 * @param changeName
	 * @return
	 */
	public void PartReName(WTPart part,String changeNumber,String changeName,boolean isDisuse)throws Exception{
		
		//System.out.println("[E3PSRENameObject] PartReName = " + part.getNumber() +":" + isDisuse);
        Identified identified = (Identified) part.getMaster();
        WTPartMasterIdentity partid = (WTPartMasterIdentity)identified.getIdentificationObject();
        if(isDisuse){
        	changeNumber = this.getDelteNumber(changeNumber, part);
        }
        
        partid.setNumber(changeNumber.trim());
        partid.setName(changeName.trim());
      
        IdentityHelper.service.changeIdentity(identified, partid);
        PersistenceHelper.manager.refresh(part);
		
	}
	
	/**
	 * EPMCADNamespace CAD Change
	 * @param epm
	 */
	private  void cadNameSpaceChange(EPMDocument epm){
		
		 EPMDocumentMaster master = (EPMDocumentMaster)epm.getMaster();
		 long masterlong = CommonUtil.getOIDLongValue(master); 
		 try {
			QuerySpec spec = new QuerySpec(EPMCADNamespace.class);
			spec.appendWhere(new SearchCondition(EPMCADNamespace.class,"masterReference.key.id",SearchCondition.EQUAL,masterlong));
			
			QueryResult rt =PersistenceHelper.manager.find(spec);
			
			while(rt.hasMoreElements()){
				EPMCADNamespace space =(EPMCADNamespace)rt.nextElement();
				space.setCADName(epm.getCADName());
				space=(EPMCADNamespace)PersistenceHelper.manager.modify(space);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	 }
	
	/**
	 * DEL 중복 가능성 때문에 Number-DEL-001
	 * @param changeNumber
	 * @param obj
	 * @return
	 */
	private String getDelteNumber(String changeNumber,WTObject obj) throws Exception{

		 String seq ="";
		 if(obj instanceof WTPart){ 
			 seq = getMaxNO(changeNumber,"WTPartMaster", "WTPartNumber");
			 changeNumber = "DEL-"+seq+"-"+changeNumber;
		 }else if(obj instanceof EPMDocument){
			 
			 //String tempName = getCadName(changeNumber);
			 //String prefix = getPrefix(changeNumber);
			 seq = getMaxNO(changeNumber,"EPMDocumentMaster", "DocumentNumber");
			 
			 changeNumber = "DEL-"+seq+"-"+changeNumber;
		 }else{
			 return "";
		 }
		 //System.out.println("1.[E3PSRENameObject] getDelteNumber = " + changeNumber +":" + seq);
		 
		 //System.out.println("2.[E3PSRENameObject] getDelteNumber = " + changeNumber );
		 
		 return changeNumber;
		 
	}
	
	public String getMaxNO(String searchName,String tabName,String colName) throws Exception{
		
		if (!SERVER) {

			try {
				Class argTypes[] = new Class[]{String.class,String.class,String.class};
				Object args[] = new Object[]{searchName,tabName,colName};
				return (String)RemoteMethodServer.getDefault().invoke("getMaxNO", null, this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}


		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        String maxCount = "";
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();
			
			 StringBuffer sb = new StringBuffer()
			.append("SELECT COUNT("+colName+") from ")
			.append(tabName)
			.append(" WHERE  ")
			.append(""+colName+"")
			.append(" LIKE ")
			.append("'%"+searchName+"%'");
			//System.out.println(sb.toString());
			st = con.prepareStatement(sb.toString());
			rs = st.executeQuery();
			
			String seqNum = null;
			BigDecimal count = null;
			while (rs.next()) {
				 count = rs.getBigDecimal(1);
			}
			maxCount =String.valueOf(count.intValue() + 1);
			
			if(maxCount.length() == 1){
				maxCount ="00"+maxCount;
			}else if(maxCount.length() == 2){
				maxCount ="0"+maxCount;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
            if ( rs != null ) {
                rs.close();
            }
            if ( st != null ) {
                st.close();
            }
			if (DBProperties.FREE_CONNECTION_IMMEDIATE
					&& !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
		
		return maxCount;
	}
	
	/**
	 * DEL 중복 가능성 때문에 cadName-DEL-000.SOLDDRAW
	 * @param changeCadName
	 * @return
	 */
	private String getDelteCadName(String changeCadName){
		
		try{
			String tempName = getCadName(changeCadName);
			String prefix = getPrefix(changeCadName);
			String seq = getMaxNO(changeCadName, "EPMDocumentMaster", "CADName");
			
			changeCadName = "-DEL-" + seq +"-"+changeCadName;
			
			return changeCadName;
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * 확장자 빠진 cad명
	 * @param changeName
	 * @return
	 */
	private String getCadName(String changeName){
		return changeName.substring(0,changeName.indexOf("."));
		
	}
	
	/**
	 * cad명에서 확장자
	 * @param changeName
	 * @return
	 */
	private String getPrefix(String changeName){
		
		return changeName.substring(changeName.indexOf(".")+1);
	}
	
	public String getExtentionName(String changeName) {
		String extName = this.getPrefix(changeName);
		return extName;
	}
	
	/**
	 *  
	 * @param changeNumber
	 * @param obj
	 * @return
	 */
	private boolean numberCheck(String changeNumber,WTObject obj){
		 
		 
		 try{
			 
		 }catch(Exception e){
			 
		 }
		 
		 return false;
	 }
}
