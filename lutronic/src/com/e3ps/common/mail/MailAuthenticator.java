package com.e3ps.common.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
	private String id;
	private String pw;

	public MailAuthenticator(String id, String pw) {
		this.id = id;
		this.pw = pw;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(id, pw);
	}
}