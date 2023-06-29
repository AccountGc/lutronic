package com.e3ps.org.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import wt.method.MethodContext;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.services.StandardManager;
import wt.util.WTProperties;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.Department;

public class StandardOrgService extends StandardManager implements OrgService {
	
	public static StandardOrgService newStandardOrgService() throws Exception {
		final StandardOrgService instance = new StandardOrgService();
		instance.initialize();
		return instance;
	}
	
	static String dataStore = "Oracle"; //SQLServer ....
	static {
		try{
			dataStore = WTProperties.getLocalProperties().getProperty("wt.db.dataStore");
		}catch(Exception ex){
			dataStore = "Oracle";
		}
	}
	
	@Override
	public JSONObject getDepartmentTree(Department root) throws Exception{

		MethodContext methodcontext = null;
		WTConnection wtconnection = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        JSONObject deptList = new JSONObject();
        
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

			StringBuffer sb = null;

			sb = new StringBuffer();
			
			sb.append("			SELECT LEVEL, 																		");
			sb.append("				NAME, 																			");
			sb.append("				classnameA2A2 ||':' ||idA2A2 oid, 												");
			sb.append("				CODE, 																			");
			sb.append("				SORT, 																			");
			sb.append("				ida2a2 id,																		");
			sb.append("				idA3parentReference pid, 														");
			sb.append("  			CONNECT_BY_ISLEAF AS isleaf 													");
			sb.append("			FROM Department 																	");
			sb.append("  			START WITH idA3parentReference = ? 												");
			sb.append("  			CONNECT BY prior idA2A2        = idA3parentReference 							");
			sb.append("			ORDER SIBLINGS BY SORT	 															");

			st = con.prepareStatement(sb.toString());
			st.setLong(1, root.getPersistInfo().getObjectIdentifier().getId());

			rs = st.executeQuery();
			
			String rootName = root.getName();
			String mainID = "1";
			int mainLevel = 0;

			JSONObject topList = new JSONObject();
			// String rootCode = "1"; // 대성 (1) : Tree구성을 위해 임의로 code 지정함
			topList.put("id", mainID);
			topList.put("text", rootName);
			topList.put("oid", root.getPersistInfo().getObjectIdentifier().toString());
			topList.put("isLeaf", "0");
			topList.put("parentCode", "0");
			topList.put("levelCnt", mainLevel);
			topList.put("code", root.getCode());
			topList.put("pcode", "0");
			topList.put("sort", root.getSort());
			topList.put("isLeaf", 0);
			topList.put("item", new JSONArray());

			// deptList.put("text" , rootName );

			// Tree에 등록될 순서별로 데이터를 담을 Map
			Map treeMap = new HashMap();

			JSONObject jsonParentData = null;
			JSONArray jsonListData = new JSONArray();
			jsonListData.put(topList);

			// Tree구성을 위해 추가
			deptList.put("id", "0");
			deptList.put("item", jsonListData);

			treeMap.put("0", deptList);
			treeMap.put(mainID, topList);

			while (rs.next()) {
				String id = rs.getString("ID");
				String name = rs.getString("NAME");
				String oid = rs.getString("OID");
				String parentOid = rs.getString("PID");
				String code = rs.getString("CODE");
				String sort = rs.getString("SORT");
				int level = rs.getInt("LEVEL");
				int isLeaf = rs.getInt("ISLEAF");
				
				JSONObject jsonData = new JSONObject();
				jsonData.put("id", id);
				jsonData.put("text", name);
				jsonData.put("oid", oid);
				jsonData.put("level", level);
				jsonData.put("parentOid", parentOid);
				jsonData.put("code", code);
				jsonData.put("pcode", ((Department)CommonUtil.getObject("com.e3ps.org.Department:"+parentOid)).getCode());
				jsonData.put("sort", sort);
				jsonData.put("isLeaf", isLeaf);

				// 하위를 가지는 정보를 Map에 넣어준다.
				if (isLeaf == 0) {
					JSONArray jsonSubListData = new JSONArray();
					jsonData.put("item", jsonSubListData);
					treeMap.put(id, jsonData);
				}

				if (level == 1) {
					parentOid = mainID;
				}

				// 해당 데이터의 Parent 정보를 가져와 데이터를 추가한다.
				jsonParentData = (JSONObject) treeMap.get(parentOid);
				jsonListData = jsonParentData.getJSONArray("item");
				if (jsonListData == null) {
					jsonListData = new JSONArray();
				}

				// 해당 데이터를 추가한다.
				jsonListData.put(jsonData);
				jsonParentData.put("item", jsonListData);

				// 수정된 정보를 Map에 다시 넣어준다.
				treeMap.put(parentOid, jsonParentData);

			}
			
			
		} catch (Exception e) {
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
		
		return deptList;
	}

}
