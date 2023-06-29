package com.e3ps;

import java.util.HashMap;
import java.util.Vector;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.work.WorkItem;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.jdf.config.Config;
import com.e3ps.common.jdf.config.ConfigImpl;
import com.e3ps.org.Department;
import com.e3ps.org.service.MailUserHelper;

public class SJTest {
	public static void main(String[] args)  {
		
		setUser("wcadmin", "lutadmin12#");
		
		try {
			
			
			//createDept();
			//test();
			//getWorkItem();
			//createDOCGRADE();
			//linkDept();
			//AAA();
			//createGroup();
			//createMailUser();
			createPartGroup();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createPartGroup() throws Exception {
		
		NumberCodeType codeType = NumberCodeType.toNumberCodeType("PARTGROUP");
		
		NumberCode nc = new NumberCode();
		nc.setCode("P");
		nc.setCodeType(codeType);
		nc.setName("PCB 외주");
		nc.setDescription("PCB 외주");
		nc.setEngName("PCB outsource");
		
		PersistenceHelper.manager.save(nc);
	}
	
	public static void createMailUser() {
		
		String[] name = new String[]{"엄태식","김현민","임두빈","박혜민","이승진"};
		String[] email = new String[]{"tsuam@e3ps.com","hmkim@e3ps.com","dblim@e3ps.com","hmpark@e3ps.com","sjlee@e3ps.com"};
		String[] enable = new String[]{"true","true","true","true","false"};
		
		for(int i=0; i<name.length; i++) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("name", name[i]);
			map.put("email", email[i]);
			map.put("enable", enable[i]);
			
			MailUserHelper.service.createMailUser(map);
		}
	}
	
	public static void createGroup() {
		String a[] = new String[]{"Executive_Group","PM_Group","Intellian_Group"};
		String b[] = new String[]{"임원그룹","팀장/PM","intellian 내부 사용자"};
		for(int i=0; i<a.length; i++) {
			createGroup(a[i],b[i]);
		}
	}
	
	private static WTGroup createGroup(String name,String desc)
    {
        try
        {
			//System.out.println("create Group : " + name + "("+desc+")");

            WTGroup group = WTGroup.newWTGroup(name);
            group.setDescription(desc);
            return (WTGroup) OrganizationServicesHelper.manager.savePrincipal(group);

        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        catch (WTPropertyVetoException e)
        {
            e.printStackTrace();
        }
        return null;
    }
	
	public static void AAA() {
		EChangeActivity ea = new EChangeActivity();
		Vector<EChangeActivity> nextAct=ECAHelper.service.getNextECA(ea,"STEP5");
		if(nextAct.size()>0){
			ECAHelper.service.assignECA(nextAct);
		}
	}
	
	public static void setUser(final String id, final String pw) {
        RemoteMethodServer.getDefault().setUserName(id);
        RemoteMethodServer.getDefault().setPassword(pw);
    }
	
	public static void linkDept() throws Exception {
		
		QuerySpec rootQs = new QuerySpec(Department.class);
		rootQs.appendWhere(new SearchCondition(Department.class, Department.CODE, SearchCondition.EQUAL, "ROOT"), new int[] {0});
		
		QueryResult rootQr = PersistenceHelper.manager.find(rootQs);

		Department root = null;
		
		if(rootQr.hasMoreElements()) {
			//System.out.println("ROOT 존재");
		}else {
			root = new Department();
			root.setCode("ROOT");
			root.setName("intellian");
			root.setSort(0);
			
			root = (Department)PersistenceHelper.manager.save(root);
		}
		
		QuerySpec qs = new QuerySpec(Department.class);
		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		while(qr.hasMoreElements()) {
			
			Department dept = (Department)qr.nextElement();
			
			if("ROOT".equals(dept.getCode())) {
			}else {
			
				Department pdept = (Department)dept.getParentReference().getObject();
				
				if(pdept != null) {
					//System.out.println(dept.getName() + " +++++++  " + pdept.getName());
				}else {
					dept.setParent(root);
					
					PersistenceHelper.manager.modify(dept);
					//System.out.println(dept.getName() + " -------   ");
				}
			}
		}
		
	}
	
	
	public static void createDOCGRADE() throws Exception {
		
		NumberCodeType type = NumberCodeType.toNumberCodeType("DOCGRADE");
		
		NumberCode num1 = new NumberCode();
		num1.setCode("G001");
		num1.setCodeType(type);
		num1.setName("임원");
		num1.setSort("001");
		num1.setDisabled(false);
		
		PersistenceHelper.manager.save(num1);
		
		NumberCode num2 = new NumberCode();
		num2.setCode("G002");
		num2.setCodeType(type);
		num2.setName("팀장/PM");
		num2.setSort("002");
		num2.setDisabled(false);
		
		PersistenceHelper.manager.save(num2);
		
		NumberCode num3 = new NumberCode();
		num3.setCode("G003");
		num3.setCodeType(type);
		num3.setName("모두");
		num3.setSort("003");
		num3.setDisabled(false);
		
		PersistenceHelper.manager.save(num3);
		
	}
	
	public static void getWorkItem() throws Exception {
		String a = "com.e3ps.project.ProjectRegistApproval:208655";
		String b = "";
		String key = "CLASSNAMEKEYB4.KEY.ID";
		QuerySpec qs = new QuerySpec();
		
		int idx = qs.addClassList(WorkItem.class, true);
		
		qs.appendWhere(new SearchCondition(WorkItem.class, key , SearchCondition.EQUAL, a), new int[] {idx});
		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		if(qr.hasMoreElements()){
			//System.out.println("있다......................................");
		}
	}
	
	
	public static void test() throws Exception {
		Config conf = ConfigImpl.getInstance();
        String activity = conf.getString("process.route.type.0");
        //System.out.println(  activity + "   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>    ");
	}
	
	public static void createDept() throws Exception {
		Department dept = new Department();
		dept.setCode("root");
		dept.setName("intellian");
		dept.setSort(0);
		
		Department root = (Department)PersistenceHelper.manager.save(dept);
		
		Department test = Department.newDepartment();
		test.setCode("TEST1");
		test.setName("Test1");
		test.setSort(1);
		test.setParent(root);
		
		test = (Department)PersistenceHelper.manager.save(test);
		
		Department ds1 = Department.newDepartment();
		ds1.setCode("DS1");
		ds1.setName("영업 1팀");
		ds1.setSort(1);
		ds1.setParent(test);
		
		PersistenceHelper.manager.save(ds1);
		
		Department ds2 = Department.newDepartment();
		ds2.setCode("DS2");
		ds2.setName("영업 2팀");
		ds2.setSort(2);
		ds2.setParent(test);
		
		PersistenceHelper.manager.save(ds2);
		
		Department test2 = Department.newDepartment();
		test2.setCode("TEST2");
		test2.setName("Test2");
		test2.setSort(2);
		test2.setParent(root);
		
		test2 = (Department)PersistenceHelper.manager.save(test2);
		
		Department ed1 = Department.newDepartment();
		ed1.setCode("ED1");
		ed1.setName("설계 1팀");
		ed1.setSort(1);
		ed1.setParent(test2);
		
		PersistenceHelper.manager.save(ed1);
		
		Department ed2 = Department.newDepartment();
		ed2.setCode("ED2");
		ed2.setName("설계 2팀");
		ed2.setSort(2);
		ed2.setParent(test2);
		
		PersistenceHelper.manager.save(ed2);
		
		Department test3 = Department.newDepartment();
		test3.setCode("TEST3");
		test3.setName("Test3");
		test3.setSort(3);
		test3.setParent(root);
		
		test3 = (Department)PersistenceHelper.manager.save(test3);
		
		Department de1 = Department.newDepartment();
		de1.setName("개발 1팀");
		de1.setCode("DE1");
		de1.setSort(1);
		de1.setParent(test3);
		
		PersistenceHelper.manager.save(de1);
		
		Department test4 = Department.newDepartment();
		test4.setCode("TEST4");
		test4.setName("유지보수");
		test4.setSort(4);
		test4.setParent(root);
		
		PersistenceHelper.manager.save(test4);
	}
}
