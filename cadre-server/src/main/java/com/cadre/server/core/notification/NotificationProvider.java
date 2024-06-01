package com.cadre.server.core.notification;

import javax.activation.DataSource;

import com.cadre.server.core.boundary.ServiceProvider;

public interface NotificationProvider  extends ServiceProvider {

	/**
	 * Send Message To
	 * @param from
	 * @param to
	 * @param message
	 */
	public void sendMessageTo(String email, String subj, String message,  String [] filesName, DataSource [] attachFilesContent);

	
}