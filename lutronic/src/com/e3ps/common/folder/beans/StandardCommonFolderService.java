package com.e3ps.common.folder.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.method.MethodContext;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTProperties;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;

public class StandardCommonFolderService extends StandardManager implements CommonFolderService {

	public static StandardCommonFolderService newStandardCommonFolderService() throws Exception {
		final StandardCommonFolderService instance = new StandardCommonFolderService();
		instance.initialize();
		return instance;
	}

	@Override
	public JSONObject dhtmlxTree(final Folder obj) throws WTException {
		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

		PreparedStatement st = null;
		ResultSet rs = null;

		JSONObject deptList = new JSONObject();

		try {

			ArrayList list = new ArrayList();

			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

			StringBuffer sql = null;

			sql = new StringBuffer();
			// sql.append(" SELECT T.RN ID, NVL(S.RN, 0) PID, T.OID, T.NAME, T.L  ");
			// sql.append("   FROM (                                       ");
			sql.append("          SELECT ROWNUM RN,                     ");
			sql.append("                 LEVEL L,                       ");
			sql.append("                 TB.NAME NAME,                  ");
			sql.append("                 TB.CLASSNAMEA2A2||':'||TB.IDA2A2 OID, ");
			sql.append("                 TB.IDA2A2 ID,                  ");
			sql.append("                 TA.IDA3A5 PID,                 ");
			sql.append("                 CONNECT_BY_ISLEAF as isleaf    ");
			sql.append("            FROM SUBFOLDERLINK TA, SUBFOLDER TB ");
			sql.append("           WHERE TA.IDA3B5 = TB.IDA2A2          ");
			sql.append("           START WITH TA.IDA3A5 = ?             ");
			sql.append("         CONNECT BY PRIOR TA.IDA3B5 = TA.IDA3A5 ");
			sql.append("           ORDER SIBLINGS BY TB.NAME ASC        ");
			// sql.append("        ) T,                                    ");
			// sql.append("        (                                       ");
			// sql.append("          SELECT ROWNUM RN,                     ");
			// sql.append("                 SB.IDA2A2 ID                   ");
			// sql.append("            FROM SUBFOLDERLINK SA, SUBFOLDER SB ");
			// sql.append("           WHERE SA.IDA3B5 = SB.IDA2A2          ");
			// sql.append("           START WITH SA.IDA3A5 = ?             ");
			// sql.append("         CONNECT BY PRIOR SA.IDA3B5=SA.IDA3A5   ");
			// sql.append("           ORDER SIBLINGS BY SB.NAME ASC        ");
			// sql.append("        ) S                                     ");
			// sql.append("  WHERE T.PID = S.ID(+)                         ");
			// sql.append("  ORDER BY T.RN                                 ");

			st = con.prepareStatement(sql.toString());
			st.setLong(1, obj.getPersistInfo().getObjectIdentifier().getId());
			// System.out.println(sql.toString());
			rs = st.executeQuery();

			String rootName = obj.getName();
			String mainID = "1";
			int mainLevel = 0;

			JSONObject topList = new JSONObject();
			// String rootCode = "1"; // 대성 (1) : Tree구성을 위해 임의로 code 지정함
			topList.put("id", mainID);
			topList.put("text", rootName);
			topList.put("oid", "");
			topList.put("isLeaf", "0");
			topList.put("parentCode", "0");
			topList.put("levelCnt", mainLevel);
			topList.put("item", new JSONArray());
			topList.put("location", FolderHelper.getFolderPath(obj));

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
				int level = rs.getInt("L");
				int isLeaf = rs.getInt("ISLEAF");

				String loc = FolderHelper.getFolderPath((SubFolder) CommonUtil.getObject(oid));

				JSONObject jsonData = new JSONObject();
				jsonData.put("id", id);
				jsonData.put("text", name);
				jsonData.put("oid", oid);
				jsonData.put("level", level);
				jsonData.put("parenOid", parentOid);
				jsonData.put("location", loc);

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
			throw new WTException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				throw new WTException(e);
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}

		return deptList;
	}
	
	@Override
	public ArrayList getFolderTree(final Folder obj) throws WTException {

        MethodContext methodcontext = null;
        WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        
        String dataStore = "Oracle";
        
        try {

            ArrayList list = new ArrayList();

            methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            Connection con = wtconnection.getConnection();

            StringBuffer sql = null;

            try {
                dataStore = WTProperties.getLocalProperties().getProperty("wt.db.dataStore");
            }
            catch ( Exception ex ) {
                dataStore = "Oracle";
            }
            
            if ( "Oracle".equals(dataStore) ) {

                /*
                 * ���Ʈ�� select t.l,s.name,s.CLASSNAMEA2A2||':'||s.ida2a2 from
                 * subfolder s,( select level l,ida3b5 id from subfolderlink
                 * start with ida3a5=7843 connect by prior ida3b5=ida3a5 ) t
                 * where t.id=s.ida2a2
                 */

                sql = new StringBuffer().append("SELECT T.L,S.NAME,S.CLASSNAMEA2A2||':'||S.IDA2A2 FROM SUBFOLDER S,( ")
                        .append("SELECT LEVEL L,IDA3B5 ID FROM SUBFOLDERLINK ").append("START WITH IDA3A5=?  ")
                        .append("connect by prior ida3b5=ida3a5 ").append(") t where  t.id=s.ida2a2 ");

            } else {

                /*
                 * ���Ʈ��-mssql with cte (idA3B5, level) as ( select idA3B5, 1 as
                 * level from SubFolderLink where idA3A5=7843 union all select
                 * a.idA3B5, level+1 from SubFolderLink a, cte b where
                 * a.idA3A5=b.idA3B5 ) select c.level, s.name,
                 * s.classnameA2A2+':'+str(s.idA2A2) from SubFolder s, cte c
                 * where c.idA3B5=s.idA2A2
                 */

                sql = new StringBuffer()
                        .append("with cte (idA3B5, level) as ( ")
                        .append("select idA3B5, 1 as level ")
                        .append("from SubFolderLink ")
                        .append("where idA3A5=? ")
                        .append("union all ")
                        .append("select a.idA3B5, level+1 ")
                        .append("from SubFolderLink a, cte b ")
                        .append("where a.idA3A5=b.idA3B5 ) ")
                        .append("select c.level, s.name, s.classnameA2A2+':'+convert(varchar, s.idA2A2) from SubFolder s, cte c ")
                        .append("where c.idA3B5=s.idA2A2");

            }
            st = con.prepareStatement(sql.toString());
            st.setLong(1, obj.getPersistInfo().getObjectIdentifier().getId());

            rs = st.executeQuery();

            while ( rs.next() ) {
                String level = rs.getString(1);
                String name = rs.getString(2);
                String oid = rs.getString(3);
                oid = oid.trim();
                
                list.add(new String[] { level, name, oid });

            }
           
           Comparator<String[]> comparator = new Comparator<String[]>(){
                public int compare(String[] o1, String[] o2) {
                     return o2[2].compareTo(o1[2]);
                }
           };
       
           Collections.sort(list, comparator);
            return list;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            throw new WTException(e);
        }
        finally {
            try {
                if ( rs != null ) {
                    rs.close();
                }
                if ( st != null ) {
                    st.close();
                }
            } catch(Exception e) {
                throw new WTException(e);
            }
            if ( DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive() ) {
                MethodContext.getContext().freeConnection();
            }
        }
    }
	
	/****
     * ȭ����� DTree�� ���������� �׷����� �ʾƼ� �߰���. (ORACLE�� ����)
     * @param obj
     * @return
     * @throws WTException
     */
	@Override
    public ArrayList getFolderDTree(final Folder obj) throws WTException {

        MethodContext methodcontext = null;
        WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {

            ArrayList list = new ArrayList();

            methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            Connection con = wtconnection.getConnection();

            StringBuffer sql = null;
            long sqlstart = System.currentTimeMillis();
            sql = new StringBuffer();
            sql.append(" SELECT T.RN ID, NVL(S.RN, 0) PID, T.OID, T.NAME, T.L  ");
            sql.append("   FROM (                                       ");
            sql.append("          SELECT ROWNUM RN,                     ");
            sql.append("                 LEVEL L,                       ");
            sql.append("                 TB.NAME NAME,                  ");
            sql.append("                 TB.CLASSNAMEA2A2||':'||TB.IDA2A2 OID, ");
            sql.append("                 TB.IDA2A2 ID,                  ");
            sql.append("                 TA.IDA3A5 PID                  ");
            sql.append("            FROM SUBFOLDERLINK TA, SUBFOLDER TB ");
            sql.append("           WHERE TA.IDA3B5 = TB.IDA2A2          ");
            sql.append("           START WITH TA.IDA3A5 = ?             ");
            sql.append("         CONNECT BY PRIOR TA.IDA3B5 = TA.IDA3A5 ");
            sql.append("           ORDER SIBLINGS BY TB.NAME ASC        ");
            sql.append("        ) T,                                    ");
            sql.append("        (                                       ");
            sql.append("          SELECT ROWNUM RN,                     ");
            sql.append("                 SB.IDA2A2 ID                   ");
            sql.append("            FROM SUBFOLDERLINK SA, SUBFOLDER SB ");
            sql.append("           WHERE SA.IDA3B5 = SB.IDA2A2          ");
            sql.append("           START WITH SA.IDA3A5 = ?             ");
            sql.append("         CONNECT BY PRIOR SA.IDA3B5=SA.IDA3A5   ");
            sql.append("           ORDER SIBLINGS BY SB.NAME ASC        ");
            sql.append("        ) S                                     ");
            sql.append("  WHERE T.PID = S.ID(+)                         ");
            sql.append("  ORDER BY T.RN                                 ");

            st = con.prepareStatement(sql.toString());
            st.setLong(1, obj.getPersistInfo().getObjectIdentifier().getId());
            st.setLong(2, obj.getPersistInfo().getObjectIdentifier().getId());
            rs = st.executeQuery();
            long sqlend = System.currentTimeMillis();
            //System.out.println("sql " + (sqlend-sqlstart));
            while ( rs.next() ) {
                list.add(new String[] { rs.getString(1), rs.getString(2),   // DTree�� ID, DTree�� PID
                                        StringUtil.checkNull(rs.getString(3)).trim(),   //OID
                                        rs.getString(4), rs.getString(5) }); // �����, Level

            }

            return list;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            throw new WTException(e);
        }
        finally {
            try {
                if ( rs != null ) {
                    rs.close();
                }
                if ( st != null ) {
                    st.close();
                }
            } catch(Exception e) {
                throw new WTException(e);
            }
            if ( DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive() ) {
                MethodContext.getContext().freeConnection();
            }
        }

    }
	
	@Override
	public JSONObject newGetTrees(final Folder obj) throws WTException {

		JSONObject deptList = new JSONObject();
		
		try{
			QueryResult childFolders = FolderHelper.service.findSubFolders(obj);
			
			String rootName = obj.getName();
			String mainID = "1";
			int mainLevel = 0;

			JSONObject topList = new JSONObject();
			// String rootCode = "1"; // 대성 (1) : Tree구성을 위해 임의로 code 지정함
			topList.put("id", mainID);
			topList.put("text", rootName);
			topList.put("oid", "");
			topList.put("isLeaf", "0");
			topList.put("parentCode", "0");
			topList.put("levelCnt", mainLevel);
			topList.put("item", new JSONArray());  //여기에 하위 폴더 넣는것
			topList.put("location", FolderHelper.getFolderPath(obj));
			topList.put("childSize", childFolders.size());
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

			
			
			
			
			
			while (childFolders.hasMoreElements()) {
			     SubFolder folder = (SubFolder)childFolders.nextElement();
			     String folderName = folder.getName();
			     SubFolder parentFolder =(SubFolder)obj;
			     QueryResult childs = FolderHelper.service.findSubFolders(folder);
			     String loc = FolderHelper.getFolderPath(folder);
			     String id = String.valueOf(folder.getPersistInfo().getObjectIdentifier().getId());
			     String parentOid = parentFolder.getPersistInfo().getObjectIdentifier().getStringValue();
			     String[] locArr = loc.split("/");
			     int level = locArr.length -3;
			     JSONObject jsonData = new JSONObject();
					jsonData.put("id", id);
					jsonData.put("text", folderName);
					jsonData.put("oid", folder.getPersistInfo().getObjectIdentifier().getStringValue());
					jsonData.put("level", mainID);
					jsonData.put("parenOid", parentFolder.getPersistInfo().getObjectIdentifier().getStringValue());
					jsonData.put("location", loc);
					jsonData.put("childSize", String.valueOf(childs.size()));
					// 하위를 가지는 정보를 Map에 넣어준다.
					JSONArray jsonSubListData = new JSONArray();
					jsonData.put("item", jsonSubListData);
					treeMap.put(id, jsonData);
					
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
		}catch(Exception e){
			e.printStackTrace();
		}



		return deptList;
	}
	
	
	@Override
	public  List<Map<String,Object>> getNextTree(Folder obj) throws WTException {
		List<Map<String,Object>> listResult = null;
		Map<String,Object> result = null;
		try{
			listResult = new ArrayList<Map<String,Object>>();
			QueryResult childFolders = FolderHelper.service.findSubFolders(obj);
			while (childFolders.hasMoreElements()) {
				 result = new HashMap<String, Object>();
			     SubFolder folder = (SubFolder)childFolders.nextElement();
			     QueryResult childs = FolderHelper.service.findSubFolders(folder);
			     String id =  String.valueOf(folder.getPersistInfo().getObjectIdentifier().getId());
			     String folderName = folder.getName();
			     SubFolder parentFolder =(SubFolder)obj;
			     String oid =  folder.getPersistInfo().getObjectIdentifier().getStringValue();
			     String parenOid =  parentFolder.getPersistInfo().getObjectIdentifier().getStringValue();
			     String loc = FolderHelper.getFolderPath(folder);
			     String[] locArr = loc.split("/");
			     int level = locArr.length -3;
			     result.put("id", id);
			     result.put("oid",oid);
			     result.put("parenOid",parenOid);
			     result.put("text",folderName);
			     result.put("location",loc);
			     result.put("level",level);
			     result.put("childSize", String.valueOf(childs.size()));
			     listResult.add(result);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return listResult;
	}
	
	
	
}
