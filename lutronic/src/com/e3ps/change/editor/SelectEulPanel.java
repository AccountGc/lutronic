package com.e3ps.change.editor;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import java.net.*;
import java.awt.image.*;
import java.awt.dnd.*;
import wt.query.*;
import wt.fc.*;
import wt.part.*;
import javax.swing.border.*;
import wt.session.*;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.VersionControlHelper;
import wt.vc.views.*;
import wt.iba.value.service.*;
import wt.iba.value.*;
import wt.iba.definition.litedefinition.*;
import wt.iba.value.litevalue.*;
import wt.iba.definition.*;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.definition.service.IBADefinitionService;
import wt.clients.iba.container.*;
import wt.query.specification.*;
import wt.csm.navigation.litenavigation.*;
import wt.pds.*;
import wt.introspection.*;
import java.beans.*;


import com.e3ps.change.*;


public class SelectEulPanel extends JPanel implements QueryNotifier
{
	public JTable m_table;
	protected EulTableData m_data;
    Progress progress;
	public Hashtable shash=new Hashtable();
	public PagingComponent pc;

	public SelectEulPanel(Progress progress,View view){
		setLayout(new BorderLayout());
		this.progress=progress;

		JPanel pp3=new JPanel();
		pp3.setLayout(new BorderLayout());
		pp3.setBorder( new EmptyBorder( new Insets( 5, 5, 5, 5 )));
		
		m_data=new EulTableData();
		m_table=new JTable();
		m_table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    m_table.setAutoCreateColumnsFromModel(false);
		m_table.setModel(m_data);

		for (int k = 0; k < EulTableData.m_columns.length; k++) {
           DefaultTableCellRenderer renderer = new ColoredTableCellRenderer();
           renderer.setHorizontalAlignment(EulTableData.m_columns[k].m_alignment);
           javax.swing.table.TableColumn column = new javax.swing.table.TableColumn(k,EulTableData.m_columns[k].m_width, renderer, null);
           m_table.addColumn(column);   
        }
		JScrollPane spane=new JScrollPane(m_table);
		JTableHeader header = m_table.getTableHeader();
        header.setUpdateTableInRealTime(true);
		header.setReorderingAllowed(true);

		pc=new PagingComponent(5,30);
		pc.setPreferredSize(new Dimension(600,20));
		pc.addListener(this);

		pp3.add(BorderLayout.CENTER,spane);

		add(BorderLayout.CENTER,pp3);
		add(BorderLayout.SOUTH,pc);
		search();

	}
    public EOEul getEul(int row){
		return m_data.getEul(row);
	}
	public void search(){
		  
		  Thread thread=new Thread(){
			  public void run(){
				  try{
					progress.run("search");

					 QuerySpec qs = new QuerySpec(EOEul.class);
					 qs.appendSearchCondition(new SearchCondition(EOEul.class,"ecoReference.key.id","=",BEContext.eo.getPersistInfo().getObjectIdentifier().getId()));
					 pc.setQuerySpec(qs);

				  }catch(Exception ex){
					 ex.printStackTrace();
				  }
			  progress.stop();
			 }
		  };
		  thread.start();
	}
	public void actionPerformed(QueryResult qr){
		m_data.removeAll();
		while(qr.hasMoreElements()){
			Object[] o = (Object[])qr.nextElement(); 
			EOEul eul=(EOEul)o[0];
			m_data.addData(eul);
		}
		m_table.tableChanged(new TableModelEvent(m_data)); 
        m_table.repaint();
	}
	public String getQueryString(String str){
	  StringBuffer buffer=new StringBuffer();
	  for(int j=0;j<10;j++){
			    if(j<str.length()){
					char c=str.charAt(j);
					if(c==' '){
						buffer.append('_');
					}else
						buffer.append(c);

				}
				else{
					buffer.append('_');
				}
			 }
			 buffer.insert(5,'-');
			 return buffer.toString();
	}
};

class EulTableData  extends AbstractTableModel{
    protected Vector m_vector;
	protected boolean m_sortAsc;
	protected int  m_sortCol;
	
    static final public ColumnData m_columns[]={
	new ColumnData( "    View", 160, JLabel.LEFT ),
    new ColumnData( "    Ass'y Number", 160, JLabel.LEFT ),
    new ColumnData( "    Ass'y Name", 220, JLabel.LEFT ),
	new ColumnData( "    Creator", 80, JLabel.CENTER ),
    new ColumnData( "    LastUpdated", 160, JLabel.CENTER ),
    new ColumnData( "    State", 100, JLabel.CENTER )
  };
  protected int m_columnsCount = m_columns.length;
  public EulTableData(){
	  m_vector=new Vector();
  }
  public int getRowCount(){
    return m_vector==null ? 0 : m_vector.size(); 
  }
  public void removeAll(){
	  m_vector.removeAllElements();
  }
  public String getColumnName(int column) {
    String str = m_columns[column].m_title;
    return str;
  }
  public int getColumnCount() { 
    return m_columns.length; 
  }
  public void addData(EOEul data){
     m_vector.add(new EOEulData(data));
  }
  public boolean isCellEditable(int nRow, int nCol) {
    return false;
  }
  public EOEul getEul(int nRow){
	  EOEulData row = (EOEulData)m_vector.elementAt(nRow);
	  return row.eul;
  }
   public EOEulData getData(int nRow){
	 return (EOEulData)m_vector.elementAt(nRow);
  }
   public Object getValueAt(int nRow, int nCol) {
    if (nRow < 0 || nRow>=getRowCount())
      return "";
    EOEulData row = (EOEulData)m_vector.elementAt(nRow);
    switch (nCol) {
	  case 0: return row.viewName;
	  case 1: return row.number;
      case 2: return row.name;
	  case 3: return row.creator;
      case 4: return row.lastupdate;
      case 5: return row.state;
    }
    return "";
  }
}
class EOEulData
{    
	 EOEul eul;
     public IconData number;
	 String viewName;
	 String name;
	 String version;
	 String type;
	 String lastupdate;
	 String creator;
	 String state;
	 ImageIcon icon;
	 public static ImageIcon icon1;
	 public static ImageIcon icon2;
     public static ImageIcon icon3;
     static{
	 try{
		icon1=new ImageIcon(new URL(BEContext.host+"com/e3ps/change/editor/images/part.gif"));
		icon2=new ImageIcon(new URL(BEContext.host+"com/e3ps/change/editor/images/partcheckoutcopy.gif"));
		icon3=new ImageIcon(new URL(BEContext.host+"com/e3ps/change/editor/images/partcheckout.gif"));
	 }catch(Exception ex){
	 }

	}
     public EOEulData(EOEul eul){
       this.eul=eul;
	   icon = icon1;
	   
		try{
			ReferenceFactory rf = new ReferenceFactory();
		   WTPart part = (WTPart)rf.getReference(eul.getTopAssyOid()).getObject();
		   viewName = part.getView().getName();
		   this.number= new IconData(icon,part.getNumber());
		   this.name= part.getName();
		   //this.state = ApprovalHelper.manager.getState(eul);
		   
           if(WorkInProgressHelper.isCheckedOut(part)){
			  if(WorkInProgressHelper.isWorkingCopy(part)){
			      icon=icon2;
			  }else{
			      icon=icon3;
			  }
		     
		 } 
		}catch(Exception ex){}
		this.lastupdate=eul.getPersistInfo().getModifyStamp().toString();
		if(eul.getOwner()!=null){
			this.creator = eul.getOwner().getFullName();
		}else{
			this.creator = "";
		}
		
	  }
};