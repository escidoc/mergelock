package de.fiz.mergelock;

import org.apache.catalina.websocket.MessageInbound;

public class MergeLockEntry {

	private String project;

	private String user;

	private MessageInbound client;

	private String ipClient;

	private SocketServlet socketServlet;

	public MergeLockEntry(String project, String user, MessageInbound client, String ipClient, SocketServlet socketServlet) {
		this.project = project;
		this.user = user;
		this.client = client;
		this.ipClient = ipClient;
		this.socketServlet = socketServlet;
	}

	public String getProject() {
		return project;
	}

	public String getUser() {
		return user;
	}

	public MessageInbound getClient() {
		return client;
	}

	public String toString() {
		return "Mergelock: locked project '"+project+"' for user '"+user+"' from '"+ipClient+"'";
	}

	public String getIpClient() {
		return ipClient;
	}

	public SocketServlet getSocketServlet() {
		return this.socketServlet;
	}

}
