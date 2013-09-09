/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.enterprisepower.net.portforward;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.net.*;
import java.io.*;

/**
 * 
 * @author Kenneth Xu
 * 
 */
public class Listener implements Runnable {
	private static Log log = LogFactory.getLog(Listener.class);
	private ServerSocket serverSocket;
	private InetSocketAddress from, to;
	private Throwable exception;
	private Cleaner cleaner = new Cleaner();

	public Throwable getException() {
		return exception;
	}

	public Listener(InetSocketAddress from, InetSocketAddress to)
			throws IOException {
		this.from = from;
		this.to = to;
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
		new Thread(cleaner).start();
		while (true) {
			try {
				TargetConnector connector = new TargetConnector(to);
				source = serverSocket.accept();
				log.trace("accepted client connection");
				Socket target = connector.openSocket();
				new Processor(source, target, cleaner).process();
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
}