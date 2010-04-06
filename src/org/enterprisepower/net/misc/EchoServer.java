package org.enterprisepower.net.misc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.enterprisepower.net.NetUtils;

import java.net.*;
import java.io.*;

class EchoServer implements Runnable {
	private static Log log = LogFactory.getLog(EchoServer.class);
	private ServerSocket serverSocket;
	private InetSocketAddress from;
	private Throwable exception;

	// private Cleaner cleaner = new Cleaner();

	public EchoServer(InetSocketAddress from) throws IOException {
		this.from = from;
		serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(from);
		String hostname = from.getHostName();
		if (hostname == null)
			hostname = "*";
		log.info("Ready to accept client connection on " + hostname + ":"
				+ from.getPort());
	}

	public void run() {
		Socket source = null;
		// new Thread(cleaner).start();
		while (true) {
			try {
				source = serverSocket.accept();
				log.trace("accepted client connection");
				new EchoProcessor(source).process();
			} catch (IOException e) {
				String msg = "Failed to accept client connection on port "
						+ from.getPort();
				log.error(msg, e);
				exception = e;
				return;
			}
		}
	}

	public void close() {
		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public static void main(String[] args) throws Throwable {
		EchoServer server = new EchoServer(NetUtils
				.parseInetSocketAddress(args[0]));
		server.run();
		if (server.exception != null)
			throw server.exception;
	}
}