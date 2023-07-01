<%@page import="com.e3ps.common.folder.beans.CommonFolderHelper"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="wt.fc.*,wt.folder.*"%>
<%@page import="wt.pdmlink.PDMLinkProduct"%>
<%@page import="wt.inf.container.WTContainerRef"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.folder.Cabinet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.folder.SubFolder"%>
<%
ReferenceFactory rf = new ReferenceFactory();
String folder = request.getParameter("folder");
String location = request.getParameter("location"); 

if(folder != null){ //재검색시 안되서 주석처리함.문제시 wslee
	location = null;
}

Folder root_folder = null;
Folder sfolder = null;
String root_location = null;

if( location == null ){ location = "/Default/Document"; }

if( location != null || location.length() > 0){
	root_folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
	sfolder = root_folder;
	root_location = root_folder.getLocation() + "/" + sfolder.getName();
}

if(folder!=null && folder.length()>0){
	sfolder = (Folder)rf.getReference(folder).getObject();
	location = sfolder.getLocation()+"/"+sfolder.getName();
}
%>
<link rel="stylesheet" href="/Windchill/jsp/css/e3ps.css" type="text/css">
<script language="javascript" src="/Windchill/jsp/js/common.js"></script>
<script type="text/javascript" src="/Windchill/jsp/js/dtree.js"></script>
<script type="text/javascript">

function selectDocumentTreeItem( oid , plocation ,isLast) {
	//alert(oid +","+ plocation +","+ isLast);
	var id_folder = document.getElementById("id_folder");
	id_folder.value = oid;

	var id_location = document.getElementById("id_location");
	id_location.value = plocation;

	var treedocumentdiv = document.getElementById("treedocument");
	treedocumentdiv.style.display = "none";

	var las = document.getElementById("isLast");
	if(las!=null)las.value = isLast;
}

function showhideDocumentTree() {
	var treedocumentdiv = document.getElementById("treedocument");
	if (treedocumentdiv.style.display == "none") treedocumentdiv.style.display = "";
	else treedocumentdiv.style.display = "none";
}

function clearDocumentTextInclude() {
	var id_folder = document.getElementById("id_folder");
	id_folder.value = "";

	var id_location = document.getElementById("id_location");
	id_location.value = "";
}
</script>
<link rel="StyleSheet" href="/Windchill/jsp/css/dtree.css" type="text/css" />
<input id="id_location" name="location" type="text" class="txt_field" size="35" style="width:63%" value="<%=location%>" readonly>
<input id="id_folder" name="folder" type="hidden"  value="<%=sfolder.getPersistInfo().getObjectIdentifier().toString()%>" readonly>
<a href="javascript:showhideDocumentTree();"><img src="/Windchill/jsp/portal/images/s_search.gif" border=0></a>
<div id="treedocument" style="position:absolute; width:230; height:335;background-color:#FFFFFF;border :1px solid Silver; overflow:auto;display:none;">

<%
ArrayList flist = CommonFolderHelper.service.getFolderDTree(root_folder);

if (flist!=null) {
%>
	
	<script type="text/javascript" src="/Windchill/jsp/js/dtree.js"></script>
	<script type="text/javascript">
		<!--
		var d = new dTree('d','/Windchill/jsp/part/images/tree');
		d.add(<%=root_folder.getPersistInfo().getObjectIdentifier().getId()%>,-1,'<%=root_folder.getName()%>',"JavaScript:selectDocumentTreeItem('<%=root_folder.getPersistInfo().getObjectIdentifier().toString()%>','<%=root_location%>',false)");
		<%
		HashMap hash = new HashMap();
		hash.put(new Integer(0),"0");

		QuerySpec qs = null;
		QueryResult qr = null;
		
		for(int i=0; i< flist.size(); i++){
			String[] s = (String[])flist.get(i);
			int level = Integer.parseInt(s[4]);
			//System.out.println("리스트["+i+"] : "+s[0] +","+s[1]+","+s[2]);
			SubFolder subFolder = (SubFolder)rf.getReference(s[2]).getObject();
			String sublocation = subFolder.getLocation() + "/" + subFolder.getName();
			String parent = (String)hash.get(new Integer(level-1));
			hash.put(new Integer(level), Integer.toString(i+1));
			
			boolean isLast = true;
			/* if(flist.size()<=i+1){
				isLast = true;
			}else{
				String[] s2 = (String[])flist.get(i+1);
				int level2 = Integer.parseInt(s2[0]);
				
				if(level2<=level){
					isLast = true;
				}
			} */
			String name = s[3];
			
			if(isLast){
				name = "<font color=darkred>"+s[3]+"</font>";
			}
			%>
				d.add(<%=subFolder.getPersistInfo().getObjectIdentifier().getId()%>,<%=subFolder.getParentFolder().getObjectId().getId()%>,'<%=name%>',"JavaScript:selectDocumentTreeItem('<%=s[2]%>','<%=sublocation%>',<%=isLast%>)",'<%=s[3]%>');
			<%
		}
		%>
		document.write(d);

		//-->
	</script>
<%}%>
</div>
<!-- <a href="javascript:clearDocumentTextInclude();"><img src="/Windchill/jsp/portal/images/x.gif" border=0></a> -->
