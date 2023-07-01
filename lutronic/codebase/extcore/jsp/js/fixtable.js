function fixCell(str, align)
{
	document.write("<table style='table-layout:fixed' width=100% cellpadding=0 cellspacing=0 border=0>");
	document.write("<tr><td nowrap "+(align==null?"":"align="+align)+">"+str+"</td></tr>");
	document.write("</table>");
}