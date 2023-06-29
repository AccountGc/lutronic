package com.e3ps.test;

import java.util.HashMap;
import java.util.Hashtable;

import com.e3ps.common.mail.MailUtil;

public class MailTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Hashtable hash = new Hashtable();
		HashMap to = new HashMap();
		to.put("tsuam@e3ps.com", "엄태식");
		hash.put("SUBJECT", "SUBJECT 메일테스트111");
		hash.put("CONTENT", "CONTENT 메일테스트2222");
		hash.put("TO", to);
		try{
			MailUtil.manager.sendMail(hash);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
