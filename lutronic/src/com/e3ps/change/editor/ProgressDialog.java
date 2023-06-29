package com.e3ps.change.editor;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog{
	
	static ProgressDialog dialog;
	Progress progress;
	Frame parent;
	JLabel label;
	

	public ProgressDialog(Frame parent,String name){
		super(parent,name,false);
		this.parent = parent;
		getContentPane().setLayout(new BorderLayout());
		progress=new Progress();

		JPanel mPanel = new JPanel();
		mPanel.setLayout(new BorderLayout());
		label = new JLabel();
		mPanel.setBorder(BorderFactory.createEmptyBorder(10,50,10,50));
		mPanel.add(label,BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().add(BorderLayout.CENTER,mPanel);
		getContentPane().add(BorderLayout.SOUTH,progress);
	}
	public JDialog createDialog(){
         setSize(400,200);
		 setLocation();
		 setVisible(true);
		 setResizable(true);
		 return this;
	}
	public void setLocation(){
		Point p=parent.getLocation();
		Dimension dd=parent.getSize();
		int width=(int)dd.getWidth()/2;
		int height=(int)dd.getHeight()/2;
		int centerx=(int)p.getX()+width;
		int centery=(int)p.getY()+height;
		Dimension d=getSize();
		int x=centerx-(int)d.getWidth()/2;
		int y=centery-(int)d.getHeight()/2;
		super.setLocation(x,y);
    }
	public Progress getProgress(){
		return progress;
	}
	public void setMessage(String message){
		message = "<html><p align=center><font size=3>"+message+"</font></p></html>";
		label.setText(message);
	}
	public static void run(String message,String barTitle){
		dialog = new ProgressDialog(BEContext.getTop(),"Progress Dialog");
		dialog.setMessage(message);
		dialog.createDialog();
		dialog.getProgress().run(barTitle);
	}
	public static void stop(){
		if(dialog==null)return;
		dialog.getProgress().stop();
		dialog.dispose();
	}
};