package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MMailConfig.TABLE_NAME)
public class MMailConfig extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_MailConfig";
	public static final int TABLE_ID = 11;
	
	  /** Column name AD_MailConfig_ID */
    public static final String COLUMNNAME_AD_Message_ID = "AD_MailConfig_ID";
    /** Column name AD_MailConfig_UU */
    public static final String COLUMNNAME_AD_Message_UU = "AD_MailConfig_UU";
	/** Column name RequestEMail */
	public static final String COLUMNNAME_RequestEMail = "RequestEMail";
	/** Column name RequestFolder */
	public static final String COLUMNNAME_RequestFolder = "RequestFolder";
	/** Column name RequestUser */
	public static final String COLUMNNAME_RequestUser = "RequestUser";
	/** Column name RequestUserPW */
	public static final String COLUMNNAME_RequestUserPW = "RequestUserPW";
	/** Column name SMTPHost */
	public static final String COLUMNNAME_SMTPHost = "SMTPHost";
    /** Column name SMTPPort */
    public static final String COLUMNNAME_SMTPPort = "SMTPPort";

	/**
	 * Set Request EMail.
	 * 
	 * @param RequestEMail EMail address to send automated mails from or receive
	 *                     mails for automated processing (fully qualified)
	 */
	public void setRequestEMail(String RequestEMail) {
		setValueNoCheck(COLUMNNAME_RequestEMail, RequestEMail);
	}

	/**
	 * Get Request EMail.
	 * 
	 * @return EMail address to send automated mails from or receive mails for
	 *         automated processing (fully qualified)
	 */
	public String getRequestEMail() {
		return (String) getValueNoCheck(COLUMNNAME_RequestEMail);
	}

	/**
	 * Set Request Folder.
	 * 
	 * @param RequestFolder EMail folder to process incoming emails; if empty INBOX
	 *                      is used
	 */
	public void setRequestFolder(String RequestFolder) {
		setValueNoCheck(COLUMNNAME_RequestFolder, RequestFolder);
	}

	/**
	 * Get Request Folder.
	 * 
	 * @return EMail folder to process incoming emails; if empty INBOX is used
	 */
	public String getRequestFolder() {
		return (String) getValueNoCheck(COLUMNNAME_RequestFolder);
	}

	/**
	 * Set Request User.
	 * 
	 * @param RequestUser User Name (ID) of the email owner
	 */
	public void setRequestUser(String RequestUser) {
		setValueNoCheck(COLUMNNAME_RequestUser, RequestUser);
	}

	/**
	 * Get Request User.
	 * 
	 * @return User Name (ID) of the email owner
	 */
	public String getRequestUser() {
		return (String) getValueNoCheck(COLUMNNAME_RequestUser);
	}

	/**
	 * Set Request User Password.
	 * 
	 * @param RequestUserPW Password of the user name (ID) for mail processing
	 */
	public void setRequestUserPW(String RequestUserPW) {
		setValueNoCheck(COLUMNNAME_RequestUserPW, RequestUserPW);
	}

	/**
	 * Get Request User Password.
	 * 
	 * @return Password of the user name (ID) for mail processing
	 */
	public String getRequestUserPW() {
		return (String) getValueNoCheck(COLUMNNAME_RequestUserPW);
	}

	/**
	 * Set Mail Host.
	 * 
	 * @param SMTPHost Hostname of Mail Server for SMTP and IMAP
	 */
	public void setSMTPHost(String SMTPHost) {
		setValueNoCheck(COLUMNNAME_SMTPHost, SMTPHost);
	}

	/**
	 * Get Mail Host.
	 * 
	 * @return Hostname of Mail Server for SMTP and IMAP
	 */
	public String getSMTPHost() {
		return (String) getValueNoCheck(COLUMNNAME_SMTPHost);
	}

	/**
	 * Set SMTP Port.
	 * 
	 * @param SMTPPort SMTP Port Number
	 */
	public void setSMTPPort(int SMTPPort) {
		setValueNoCheck(COLUMNNAME_SMTPPort, Integer.valueOf(SMTPPort));
	}

	/**
	 * Get SMTP Port.
	 * 
	 * @return SMTP Port Number
	 */
	public int getSMTPPort() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_SMTPPort);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
}
