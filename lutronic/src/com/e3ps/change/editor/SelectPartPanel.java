package com.e3ps.change.editor;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class SelectPartPanel extends JPanel implements QueryNotifier, DragGestureListener
{
    public JTable m_table;
    protected PData m_data;
    protected JTextField t_number;
    JRadioButton inworkButton;
    JRadioButton releasedButton;
    JRadioButton rd_Lastest_Version;
    JRadioButton rd_All_Version;
    JTextField t_name;
    JButton search;
    JCheckBox box;

    JComboBox viewBox;
    Progress progress;
    public Hashtable shash = new Hashtable();
    public PagingComponent pc;

    public SelectPartPanel(Progress progress, View view)
    {
        setLayout(new BorderLayout());
        this.progress = progress;
        GriddedPanel panel = new GriddedPanel();
        JPanel pp = new JPanel();
        pp.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
        JLabel n_l = new JLabel("Number: ");
        t_number = new JTextField();
        t_name = new JTextField();

        panel.addComponent(n_l, 1, 1);
        panel.addFilledComponent(t_number, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL);
        n_l = new JLabel("Name: ");
        panel.addComponent(n_l, 2, 1);

        panel.addFilledComponent(t_name, 1, 3, 3, 1, GridBagConstraints.HORIZONTAL);

        panel.addFilledComponent(t_name, 2, 2, 3, 1, GridBagConstraints.HORIZONTAL);
        pp.add(panel, BorderLayout.CENTER);
        JPanel pp1 = new JPanel();
        pp1.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
        search = new JButton("Search");
        SearchHandler ss = new SearchHandler();
        search.addActionListener(ss);
        t_number.addActionListener(ss);
        t_name.addActionListener(ss);
        pp1.setLayout(new BorderLayout());
        pp1.add(search, BorderLayout.SOUTH);
        pp.add(pp1, BorderLayout.EAST);

        box = new JCheckBox("직접 생성한 부품 검색");
//        box.setSelected(true);
        JPanel pp3 = new JPanel();
        pp3.setLayout(new BorderLayout());
        JPanel pp4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pp4.add(box);

        inworkButton = new JRadioButton("작업중 포함");
        releasedButton = new JRadioButton("승인됨");
        ButtonGroup group = new ButtonGroup();
        group.add(inworkButton);
        group.add(releasedButton);
        inworkButton.setSelected(true);
        //pp4.add(inworkButton);
        //pp4.add(releasedButton);

        // 검색시 최신, 모든 버전 옵션
        rd_Lastest_Version = new JRadioButton("최신버전");
        rd_All_Version = new JRadioButton("모든버전");
        ButtonGroup group1 = new ButtonGroup();
        group1.add(rd_Lastest_Version);
        group1.add(rd_All_Version);
        rd_Lastest_Version.setSelected(true);
        //pp4.add(rd_Lastest_Version);
        //pp4.add(rd_All_Version);

        viewBox = new JComboBox();
        if (BEContext.isUseView)
        {
            try
            {
                View[] vlist = ViewHelper.service.getAllViews();
                for (int i = 0; i < vlist.length; i++)
                {
                    viewBox.addItem(vlist[i].getName());
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            viewBox.setSelectedItem(view.getName());
        }
        else
        {
            viewBox.addItem("선택");
            viewBox.addItem("시작");
            viewBox.addItem("양산");
            viewBox.setSelectedItem("시작");
        }
        //pp4.add(viewBox);

        pp3.add(BorderLayout.NORTH, pp4);

        m_data = new PData();
        m_table = new JTable();
        m_table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        m_table.setAutoCreateColumnsFromModel(false);
        m_table.setModel(m_data);

        for (int k = 0; k < PData.m_columns.length; k++)
        {
            DefaultTableCellRenderer renderer = new ColoredTableCellRenderer();
            renderer.setHorizontalAlignment(PData.m_columns[k].m_alignment);
            javax.swing.table.TableColumn column = new javax.swing.table.TableColumn(k, PData.m_columns[k].m_width, renderer, null);
            m_table.addColumn(column);
        }
        JScrollPane spane = new JScrollPane(m_table);
        JTableHeader header = m_table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.setReorderingAllowed(true);

        pc = new PagingComponent(5, 30);
        pc.setPreferredSize(new Dimension(600, 20));
        pc.addListener(this);

        pp3.add(BorderLayout.CENTER, spane);

        add(BorderLayout.CENTER, pp3);
        add(BorderLayout.NORTH, pp);
        add(BorderLayout.SOUTH, pc);

        DragSource dragsource = DragSource.getDefaultDragSource();
        dragsource.createDefaultDragGestureRecognizer(m_table, 3, this);

    }

    public WTPart getEPM(int row)
    {
        return m_data.getEPM(row);
    }

    public class SearchHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            Thread thread = new Thread() {
                public void run()
                {
                    String number = t_number.getText().trim();
                    String name = t_name.getText().trim();
                    boolean isLastest = true;//rd_Lastest_Version.isSelected();
                    boolean isMine = box.isSelected();
                    boolean isInWork = inworkButton.isSelected();
                    String views = (String) viewBox.getSelectedItem();

                    QuerySpec sp = EulPartHelper.manager.getQuerySpec(number, name, isLastest, isMine, isInWork, views);
                    pc.setQuerySpec(sp);
                    progress.stop();
                    search.setEnabled(true);
                }
            };
            thread.start();
        }
    }

    public void actionPerformed(QueryResult qr)
    {
        m_data.removeAll();
        while (qr.hasMoreElements())
        {
            Object jj[] = (Object[]) qr.nextElement();
            WTPart part = (WTPart) jj[0];
            m_data.addData(part);
        }
        m_table.tableChanged(new TableModelEvent(m_data));
        m_table.repaint();
    }

    public String getQueryString(String str)
    {
        StringBuffer buffer = new StringBuffer();
        for (int j = 0; j < 10; j++)
        {
            if (j < str.length())
            {
                char c = str.charAt(j);
                if (c == ' ')
                {
                    buffer.append('_');
                }
                else buffer.append(c);

            }
            else
            {
                buffer.append('_');
            }
        }
        buffer.insert(5, '-');
        return buffer.toString();
    }

    public void dragGestureRecognized(DragGestureEvent draggestureevent)
    {
        Point point = draggestureevent.getDragOrigin();
        int h = m_table.getRowHeight();
        m_table.setRowSelectionInterval(point.y / h, point.y / h);
        int row = m_table.getSelectedRow();
        if (row < 0) return;
        Rectangle rectangle = m_table.getCellRect(row, 0, true);
        rectangle.setSize(170, (int) rectangle.getHeight());
        Point _ptOffset = new Point();
        _ptOffset.setLocation(point.x - rectangle.x, point.y - rectangle.y);
        WTPartData wdata = m_data.getData(row);
        JLabel jlabel = new JLabel(wdata.number.m_data + "(" + wdata.name + ")", wdata.number.m_icon, 0);
        jlabel.setSize((int) rectangle.getWidth(), (int) rectangle.getHeight());
        BufferedImage _imgGhost = new BufferedImage((int) rectangle.getWidth(), (int) rectangle.getHeight(), 3);
        Graphics2D graphics2d = _imgGhost.createGraphics();
        graphics2d.setComposite(AlphaComposite.getInstance(2, 0.5F));
        jlabel.paint(graphics2d);
        Icon icon = jlabel.getIcon();
        int i = icon != null ? icon.getIconWidth() + jlabel.getIconTextGap() : 0;
        graphics2d.setComposite(AlphaComposite.getInstance(4, 0.5F));
        graphics2d.setPaint(new GradientPaint(i, 0.0F, SystemColor.controlShadow, getWidth(), 0.0F, new Color(255, 255, 255, 0)));
        graphics2d.fillRect(i, 0, getWidth(), _imgGhost.getHeight());
        graphics2d.dispose();
        BEContext.editor.tree.setImage(_imgGhost);

        CTransferableTreePath ctransferabletreepath = new CTransferableTreePath(m_data.getEPM(row).getPersistInfo().getObjectIdentifier().toString());

        draggestureevent.startDrag(null, _imgGhost, new Point(5, 5), ctransferabletreepath, BEContext.editor.tree);
    }

    public static void main(String args[])
    {
        JFrame frame = new JFrame();
        // frame.getContentPane().add(new PartSearchTablePanel());
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    class ComboData
    {
        public String display;
        public String value;

        public ComboData(String display, String value)
        {
            this.display = display;
            this.value = value;
        }

        public String toString()
        {
            return display;
        }
    };

    class SearchFieldDocument extends javax.swing.text.PlainDocument
    {
        int size = 0;
        Component nextFocus = null;

        public SearchFieldDocument(int size, Component nextFocus)
        {
            this.size = size;
            this.nextFocus = nextFocus;
        }

        public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException
        {
            if (str == null || str.trim().length() == 0) { return; }
            if (size > 0 && offs >= size) {

            return; }
            if (offs == size - 1 && nextFocus != null)
            {
                nextFocus.requestFocus();
                if (nextFocus instanceof JTextField)
                {
                    ((JTextField) nextFocus).selectAll();
                }
            }
            super.insertString(offs, str, a);
        }
    }
};