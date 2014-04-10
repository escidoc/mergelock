package de.fiz.mergelock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

public class SocketServlet extends WebSocketServlet {

	private static List clients = new ArrayList();

	public static LinkedHashMap<String, MergeLockEntry> mergeLocks = new LinkedHashMap<String, MergeLockEntry>();

	public static void forceUnlock(String project){
		if (mergeLocks.containsKey(project)) {
			MergeLockEntry mergeLockEntry = mergeLocks.get(project);
			mergeLocks.remove(project);
			mergeLockEntry.getSocketServlet().broadcast("unlocked#" + project + "#" + mergeLockEntry.getUser());
		}
	}

	private void broadcast(String message) {
		try {
			StreamInbound someClient;
			ListIterator iter = clients.listIterator();
			while (iter.hasNext()) {
				someClient = (MessageInbound) iter.next();
				try {
					someClient.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// If something really goes wrong -> release all locks to avoid deadlock
			mergeLocks.clear();
			e.printStackTrace();
		}
	}

	@Override
	protected StreamInbound createWebSocketInbound(String string,
			HttpServletRequest hsr) {

		final String ipClient = hsr.getRemoteAddr();
		final SocketServlet servletReference = this;

		MessageInbound inbound = new MessageInbound() {


			@Override
			protected void onClose(int status) {
				try{
					clients.remove(this);
					Iterator<String> mergeLockKeys = mergeLocks.keySet().iterator();
					while (mergeLockKeys.hasNext()) {
						String key = mergeLockKeys.next();
						if (mergeLocks.get(key).getClient() == this) {
							broadcast("unlocked#" + mergeLocks.get(key).getProject() + "#" + mergeLocks.get(key).getUser());
							mergeLocks.remove(key);
							break;
						}
					}
				} catch (Exception e) {
					// If something really goes wrong -> release all locks to avoid deadlock
					mergeLocks.clear();
					e.printStackTrace();
				}

			}

			@Override
			protected void onTextMessage(CharBuffer cb) throws IOException {
				try {
					CharBuffer msg = CharBuffer.wrap(cb);
					String messageAsString = msg.toString();

					String[] messageParts = messageAsString.split("#");
					String task = messageParts[0];
					String project = messageParts[1];
					String user = messageParts[2];
					MergeLockEntry entry = new MergeLockEntry(project, user,
							this, ipClient, servletReference);
					if (task.equals("lock")) {
						if (!mergeLocks.containsKey(project)) {
							mergeLocks.put(project, entry);
							broadcast("locked#" + project + "#" + user);
						}
					} else if (task.equals("unlock")) {
						if (mergeLocks.containsKey(project)) {
							mergeLocks.remove(project);
							broadcast("unlocked#" + project + "#" + user);
						}
					} else if (task.equals("status")) {
						if (mergeLocks.containsKey(project)) {
							MergeLockEntry foundLock = mergeLocks.get(project);

							String answer = "locked#" + foundLock.getProject()
									+ "#" + foundLock.getUser();

							WsOutbound outbound = getWsOutbound();
							outbound.writeTextMessage(CharBuffer.wrap(answer));
						}
					}
				} catch (Exception e) {
					// If something really goes wrong -> release all locks to avoid deadlock
					mergeLocks.clear();
					e.printStackTrace();
				}
			}

			@Override
			protected void onOpen(WsOutbound outbound) {
				int connSize = clients.size();
			}

			@Override
			protected void onBinaryMessage(ByteBuffer message)
					throws IOException {
			}
		};

		// Collect clients connected
		clients.add(inbound);

		return inbound;
	}
}
