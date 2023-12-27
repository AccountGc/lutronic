/**
 * @(#)	ByteArrayDataSource.java
 * Copyright (c) whois. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok
 *	@desc	
 */

package com.e3ps.common.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
*	메일 발송시 다양한 형태의 메일 Contents를 설정하기 위해서 사용하는 Class<br>
*	javax.actiovation.Datasource 인터페이스는 다양한 데이타를 처리하기 위해 추상화 시킨 Interface 임<br>
*	DataSource는 데이타에 대해서 Stream 형태로 접근할수 있다.
*/
public class ByteArrayDataSource implements javax.activation.DataSource {
	private byte[] data; // data
	private String type; // content-type

	/**
	*	InputStream 과 데이타 Type을 이용한 생성자 
	*/
	public ByteArrayDataSource(InputStream is, String type) {
		this.type = type;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int ch;

			while ((ch = is.read()) != -1) os.write(ch);
			data = os.toByteArray();
		} catch (IOException e) {
		}
	}

	/**
	*	byte[] 과 데이타 Type을 이용한 생성자 
	*/
	public ByteArrayDataSource(byte[] data, String type) {
		this.data = data;
		this.type = type;
	}

	/**
	*	String 과 데이타 Type을 이용한 생성자 
	*/
	public ByteArrayDataSource(String data, String type) {
		try {
			this.data = data.getBytes("KSC5601");
		} catch (UnsupportedEncodingException e) {}
		this.type = type;
	}

	/**
	*	String 과 그 String의 charSet, 데이타 Type을 이용한 생성자 
	*/
	public ByteArrayDataSource(String data, String charSet, String type) {
		try {
			this.data = data.getBytes(charSet);
		} catch (UnsupportedEncodingException e) {}
		this.type = type;
	}

	/**
	*	세팅된 data를 InputStream 형태로 리턴한다.
	*/
	public InputStream getInputStream() throws IOException {
		if (data == null) throw new IOException("no data");
		return new ByteArrayInputStream(data);
	}

	/**
	*	OutputStrema은 제공하지 않는다.<br> 이 메소드를 호출하면 IOException을 발생 시킨다.
	*/
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("cannot do this");
	}

	/**
	*	세팅된 type을 리턴한다.
	*/
	public String getContentType() {
		return type;
	}

	/**
	 * 데이타의 이름은 제공하지 않는다.<br> 이 메소드를 호출하면 "dummy" String 객체를 리턴한다. 
	 */
	public String getName() {
		return "dummy";
	}
}
