package com.e3ps.charts.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;

import net.sf.json.JSONArray;

public class ChartsHelper {
	public static final ChartsHelper manager = new ChartsHelper();
	
	public JSONArray crPie() throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			con = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/wind","wcadmin","wcadmin1");
			st = con.createStatement();

			StringBuffer sql = new StringBuffer()
			.append("SELECT CREATEDEPART, COUNT(*), ")
			.append("(SELECT COUNT(*) FROM ECHANGEREQUEST) AS TOTAL ")
			.append("FROM ECHANGEREQUEST ")
			.append("GROUP BY CREATEDEPART");
			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
				String createDepart = rs.getString(1);
				double count = rs.getInt(2);
				double total = rs.getInt(3);
				double totalCnt = total / count;
				double departCnt = Math.round(totalCnt * 100) / 100.0; 
				Map<String, Object> map = new HashMap<>();
				ArrayList<NumberCode> deptcodeList = NumberCodeHelper.manager.getArrayCodeList("DEPTCODE");
				for (NumberCode deptcode : deptcodeList) {
					if(deptcode.getCode().equals(createDepart)) {
						map.put("name", deptcode.getName());
					}
				}
				map.put("y", departCnt);
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.close();
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return JSONArray.fromObject(list);
	}
}
