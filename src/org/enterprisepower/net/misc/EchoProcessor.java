package org.enterprisepower.net.misc;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.enterprisepower.io.IOUtils;
import org.enterprisepower.net.NetUtils;

public class EchoProcessor implements Runnable {
	private static final Log log = LogFactory.getLog(EchoProcessor.class);

	boolean isCompleted = false;

	private Socket socket;

	public EchoProcessor(Socket source) {
		this.socket = source;
	}

	public void process() {
		Thread t = new Thread(this);
		t.start();

		if (log.isTraceEnabled()) {
			log.trace("started new process threads: " + this);
		}
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void close() {
		if (log.isTraceEnabled())
			log.trace("close() called on " + this);
		if (socket.isClosed()) {
			if (log.isTraceEnabled())
				log.trace("socket already closed: " + socket);
		} else {
			if (log.isTraceEnabled())
				log.trace("closing socket: " + socket);
			NetUtils.close(socket);
		}
	}

	public String toString() {
		return this.getClass().getName() + "(" + socket + ")";
	}

	public void run() {
		try {
			IOUtils.copyStream(socket.getInputStream(), socket
					.getOutputStream(), false);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			close();
		}
	}

}
