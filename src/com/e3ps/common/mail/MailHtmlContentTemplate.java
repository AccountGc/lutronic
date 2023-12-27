package com.e3ps.common.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

public class MailHtmlContentTemplate {
	protected Hashtable args = new java.util.Hashtable();
	private String htmlTemplateSource = null;

	private static MailHtmlContentTemplate instance = null;

	public static MailHtmlContentTemplate getInstance() {
		if (instance == null) {
			instance = new MailHtmlContentTemplate();
		}
		return instance;
	}

	public String htmlContent(Hashtable hash, String template) {
		String htmlContent = "";
		try {
			args = hash;
			if (template == null || template.equals("")) {
				template = "mail_notice.html";
			}
			setHtmlTemplate(template);
			if (htmlTemplateSource != null) {
				htmlContent = parseTemplate(htmlTemplateSource);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlContent;
	}

	public void setHtmlTemplate(String template) {
		BufferedReader in = null;
		try {
			File file = new File("D:" + File.separator + "ptc" + File.separator + "Windchill_11.1" + File.separator
					+ "Windchill" + File.separator + "codebase" + File.separator + "mailTemplate" + File.separator
					+ template);

			in = new BufferedReader(new FileReader(file));
			StringBuffer buf = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				buf.append(line + "\n");
			}
			htmlTemplateSource = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String parseTemplate(String s) {
		StringBuffer content = new StringBuffer();
		try {
			while (s.length() > 0) {
				int position = s.indexOf("<@");
				if (position == -1) {
					content.append(s);
					break;
				}

				if (position != 0)
					content.append(s.substring(0, position));

				if (s.length() == position + 2)
					break;
				String remainder = s.substring(position + 2);

				int markEndPos = remainder.indexOf(">");
				if (markEndPos == -1)
					break;

				String argname = remainder.substring(0, markEndPos).trim();
				String value = (String) args.get(argname);
				if (value != null)
					content.append(value);
				if (remainder.length() == markEndPos + 1)
					break;
				s = remainder.substring(markEndPos + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}

	public void setArg(String name, String value) {
		args.put(name, value);
	}
}