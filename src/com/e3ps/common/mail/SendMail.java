package com.e3ps.common.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import com.e3ps.common.jdf.config.ConfigEx;
import com.e3ps.common.jdf.config.ConfigExImpl;

public class SendMail {

	Message msg = null;
	Multipart mp = new MimeMultipart();

	private void setDefaultFromAddress() throws Exception {
		setFromMailAddress("pdm-admin@lutronic.com", "PDM ADMIN");
	}

	private void setMimeMessage(Session session) throws Exception {
		msg = new MimeMessage(session);
		msg.setSentDate(new Date());
	}

	private Session getSession(boolean debug) throws Exception {
		Properties props = new Properties();
		Session session = null;
//		props.put("mail.smtp.host", "smtp.office365.com");
//		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.auth", true);

		props.put("mail.smtp.host", "smtp.mailplug.co.kr");
		props.put("mail.smtp.port", 465);

		MailAuthenticator authenticator = new MailAuthenticator("kcplm@pro-packer.com", "creator007");
//		MailAuthenticator authenticator = new MailAuthenticator("pdm-admin@lutronic.com", "anrndghkqorentksdl@2021");
		session = Session.getDefaultInstance(props, authenticator);
		session.setDebug(debug);
		return session;
	}

	public SendMail() throws SendMailException {
		try {
			setMimeMessage(getSession(SendMailConfig.SESSION_DEBUG_MESSAGE_FLAG));
			setDefaultFromAddress();
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][constructor]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][constructor]" + e.getMessage(), e);
		}
	}

	public void setFromMailAddress(String mailAddress, String name) throws SendMailException {
		try {
			InternetAddress sender = new InternetAddress(mailAddress, name, "utf-8");
			msg.setFrom(sender);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setFromMailAddress]" + e.getMessage(), e);
		}
	}

	public void setFromMailAddress(String mailAddress) throws SendMailException {
		try {
			InternetAddress sender = new InternetAddress(mailAddress);
			msg.setFrom(sender);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setFromMailAddress]" + e.getMessage(), e);
		}
	}

	public void setToMailAddress(String mailAddress, String name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.TO);
	}

	public void setCcMailAddress(String mailAddress, String name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.CC);
	}

	public void setBccMailAddress(String mailAddress, String name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.BCC);
	}

	private void setMailAddress(String mailAddress, String name, Message.RecipientType type) throws SendMailException {
		try {
			InternetAddress[] recipients = { new InternetAddress(mailAddress, name, "utf-8") };
			msg.setRecipients(type, recipients);
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		}
	}

	public void setToMailAddress(String[] mailAddress, String[] name) throws Exception {
		setMailAddress(mailAddress, name, Message.RecipientType.TO);
	}

	public void setCcMailAddress(String[] mailAddress, String[] name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.CC);
	}

	public void setBccMailAddress(String[] mailAddress, String[] name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.BCC);
	}

	private void setMailAddress(String[] mailAddress, String[] name, Message.RecipientType type)
			throws SendMailException {
		try {

			InternetAddress[] recipients = new InternetAddress[mailAddress.length];
			for (int i = 0; i < mailAddress.length; i++) {
				recipients[i] = new InternetAddress(mailAddress[i], name[i]);
			}

			msg.setRecipients(type, recipients);

		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		}
	}

	public void setToMailAddress(String mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.TO);
	}

	public void setCcMailAddress(String mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.CC);
	}

	public void setBccMailAddress(String mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.BCC);
	}

	private void setMailAddress(String mailAddress, Message.RecipientType type) throws SendMailException {
		try {
			InternetAddress[] recipients = { new InternetAddress(mailAddress) };
			msg.setRecipients(type, recipients);
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		}
	}

	public void setToMailAddress(String[] mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.TO);
	}

	public void setCcMailAddress(String[] mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.CC);
	}

	public void setBccMailAddress(String[] mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.BCC);
	}

	private void setMailAddress(String[] mailAddress, Message.RecipientType type) throws SendMailException {
		try {

			InternetAddress[] recipients = new InternetAddress[mailAddress.length];
			for (int i = 0; i < mailAddress.length; i++) {
				recipients[i] = new InternetAddress(mailAddress[i]);
			}
			msg.setRecipients(type, recipients);

		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		}
	}

	public void send() throws Exception {
		Transport.send(msg);
	}

	public void setSubject(String subject) throws Exception {
		((MimeMessage) msg).setSubject(subject, "euc-kr");
	}

	public void setText(String textMessage) throws SendMailException {
		try {
			msg.setContent(textMessage, SendMailConfig.PLAIN_CONTENT_TYPE);
			msg.setHeader(SendMailConfig.CONTENT_TRANSFER_ENCODING, SendMailConfig.CONTENT_TYPE);
			msg.saveChanges();
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setText]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setText]" + e.getMessage(), e);
		}
	}

	public void setHtml(String htmlMessage) throws SendMailException {
		try {
			msg.setDataHandler(
					new DataHandler(new ByteArrayDataSource2(htmlMessage, SendMailConfig.HTML_CONTENT_TYPE)));
			msg.setHeader(SendMailConfig.CONTENT_TRANSFER_ENCODING, SendMailConfig.CONTENT_TYPE);
			msg.saveChanges();
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setHtml]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setHtml]" + e.getMessage(), e);
		}
	}

	private void attachFileSourceArray(MimeMultipart multiPart, String[] fileNames) throws Exception {
		MimeBodyPart[] fileBodyPartArray = null;

		if (fileNames == null) {
			fileBodyPartArray = new MimeBodyPart[0];
		} else {
			fileBodyPartArray = new MimeBodyPart[fileNames.length];
		}

		for (int i = 0; i < fileBodyPartArray.length; i++) {

			fileBodyPartArray[i] = new MimeBodyPart();
			FileDataSource fileSource = new FileDataSource(fileNames[i]);
			fileBodyPartArray[i].setDataHandler(new DataHandler(fileSource));
			fileBodyPartArray[i].setFileName(fileSource.getName());
			multiPart.addBodyPart(fileBodyPartArray[i]);
		}

	}

	public void setTextAndFile(String textMessage, String fileName) throws Exception {
		setTextAndFile(textMessage, new String[] { fileName });
	}

	public void setTextAndFile(String textMessage, String[] fileNames) throws SendMailException {
		try {

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(textMessage, "utf-8");

			MimeMultipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(textPart);

			attachFileSourceArray(multiPart, fileNames);

			multiPart.setSubType("mixed");
			msg.setContent(multiPart);
			msg.saveChanges();

		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setTextAndFile]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setTextAndFile]" + e.getMessage(), e);
		}
	}

	public void setHtmlAndFile(String htmlMessage, String fileName) throws Exception {
		setHtmlAndFile(htmlMessage, new String[] { fileName });
	}

	public void setHtmlAndFile(String htmlMessage, String[] fileNames) throws Exception {
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlMessage, "text/html;charset=itf-8");
//		htmlPart.setDataHandler(new DataHandler(new ByteArrayDataSource2(htmlMessage, "text/html;charset=euc-kr")));
//		htmlPart.setHeader("Content-Transfer-Encoding", "7bit");
//		htmlPart.setHeader("Content-Type", "multipart/mixed; charset=UTF-8");

		MimeMultipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(htmlPart);

//		attachFileSourceArray(multiPart, fileNames);

//		multiPart.setSubType("mixed");
		msg.setContent(multiPart);
		msg.saveChanges();
	}
}

class ByteArrayDataSource2 implements DataSource {

	private byte[] data; // data
	private String type; // content-type

	/* Create a DataSource from an input stream */
	public ByteArrayDataSource2(InputStream is, String type) {
		this.type = type;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int ch;

			while ((ch = is.read()) != -1)
				// XXX - must be made more efficient by
				// doing buffered reads, rather than one byte reads
				os.write(ch);
			data = os.toByteArray();

		} catch (IOException ioex) {
		}
	}

	/* Create a DataSource from a byte array */
	public ByteArrayDataSource2(byte[] data, String type) {
		this.data = data;
		this.type = type;
	}

	/* Create a DataSource from a String */
	public ByteArrayDataSource2(String data, String type) {
		try {
			// Assumption that the string contains only ASCII
			// characters! Otherwise just pass a charset into this
			// constructor and use it in getBytes()
			// this.data = data.getBytes("iso-8859-1");

			this.data = data.getBytes("utf-8"); // 한글로 Encoding

		} catch (UnsupportedEncodingException uex) {
		}
		this.type = type;
	}

	/**
	 * Return an InputStream for the data. Note - a new stream must be returned each
	 * time.
	 */
	public InputStream getInputStream() throws IOException {
		if (data == null)
			throw new IOException("no data");
		return new ByteArrayInputStream(data);
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("cannot do this");
	}

	public String getContentType() {
		return type;
	}

	public String getName() {
		return "dummy";
	}

}
