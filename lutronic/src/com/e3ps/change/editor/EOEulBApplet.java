package com.e3ps.change.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

//import wt.clients.lifecycle.administrator.LifeCycleAdminApplet;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTContext;
import com.e3ps.change.EOEul;

public class EOEulBApplet extends JApplet implements ActionListener
{
    public String s;
    private static Object initSync = new Object();
    
    public void init()
    {
       // WTContext.init(this);
        this.setBackground(new Color(255, 255, 255));
        JButton button = null;
        try
        {
            button = new JButton(new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/button.gif")));
        }
        catch (Exception ex)
        {
            button = new JButton("작 성");
        }
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBackground(new Color(255, 255, 255));
        button.addActionListener(this);

        Container con = getContentPane();
        con.setLayout(new BorderLayout());
        con.add(button);
        con.setCursor(Cursor.getPredefinedCursor(12));
    }
    


    public void stop()
    {
        synchronized(initSync)
        {
            WTContext.getContext(this).stop();
            super.stop();
        }
    }

    public void destroy()
    {
        synchronized(initSync)
        {
            WTContext.getContext(this).destroy(this);
            super.destroy();
        }
    }

    public void start()
    {
        
        synchronized(initSync)
        {
            WTContext.getContext(this).start();
            //System.out.println("start....");
            super.start();
        }
        //System.out.println("start");
        BEContext.clear();

        String eulOid = getParameter("eulOid");
        String oid = getParameter("oid");
        s = eulOid;
        
        openEditor(oid);

        if (eulOid != null && eulOid.length() > 0)
        {
            
            Thread thread = new Thread() {
                public void run()
                {
                    try
                    {
                        ReferenceFactory rf = new ReferenceFactory();
                        EOEul eulb = (EOEul) rf.getReference(s).getObject();
                        WTPart part = (WTPart) rf.getReference(eulb.getTopAssyOid()).getObject();
                        BEContext.runProgress("등록중인 BOM(" + part.getNumber() + ")을 읽어오고 있습니다.<br>잠시만 기다려 주십시오");
                        BEContext.loadEul(eulb);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    BEContext.stopProgress();
                }
            };
            thread.start();
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        String oid = getParameter("oid");
        openEditor(oid);
    }

    public static void openEditor(String oid)
    {
        try
        {
            JFrame frame = null;
            if (BEContext.top != null)
            {
                frame = BEContext.top;
            }
            else
            {
                frame = new JFrame();
                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent arg0)
                    {
                        if (JOptionPane.showConfirmDialog(BEContext.getTop(),
                                                          "종료하기전에 반드시 저장버튼을 눌러 작업 내용을 저장해 주세요.\n이대로 종료 하시겠습니까?",
                                                          "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                        {
                            BEContext.getTop().setVisible(false);
                        }
                    }
                });
            }
            if (oid != null && oid.length()>0 )
            {
                BEContext.setEChangeOrder(oid);
            }
            Container con = frame.getContentPane();
            BOMManager ep = new BOMManager(frame);
            con.add(ep);
            frame.setSize(800, 700);
            frame.setVisible(true);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception
    {
        //System.out.println("start");
        wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
        if (methodServer.getUserName() == null)
        {
            methodServer.setUserName("wcadmin");
            methodServer.setPassword("lutadmin12#");
        }
        BEContext.clear();
        openEditor("com.e3ps.change.EChangeOrder:130105");
    }
}