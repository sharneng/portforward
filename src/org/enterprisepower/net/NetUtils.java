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

package org.enterprisepower.net;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Networking related static utilties methods are defined in this class.
 * 
 * @author Kenneth Xu
 * 
 */
public final class NetUtils {
	private static final Log log = LogFactory.getLog(NetUtils.class);

	private NetUtils() {
	}

	/**
	 * Parse an {@link InetSocketAddress} object the given string. The
	 * <code>endPoint</code> is in format of <code>[hostname:]port</code>. It
	 * can be either a port number, or a hostname and port number separated by
	 * the character ':'.
	 * <p>
	 * 
	 * For example, '<code>www.company.com:80</code>', '<code>1234</code>' and '
	 * <code>localhost:3344</code>' are all valid end points.
	 * 
	 * @param endPoint
	 *            the string to parse.
	 * @return the {@linkplain InetSocketAddress} parsed from string.
	 * @exception NumberFormatException
	 *                if the port number is not a integer.
	 */
	public static InetSocketAddress parseInetSocketAddress(String endPoint) {
		String hostname = null;
		String portString;
		int port;

		int index = endPoint.indexOf(":");
		if (index >= 0) {
			hostname = endPoint.substring(0, index);
			portString = endPoint.substring(index + 1);
		} else {
			portString = endPoint;
		}
		port = Integer.parseInt(portString);
		return hostname == null ? new InetSocketAddress(port)
				: new InetSocketAddress(hostname, port);
	}

	/**
	 * Quietly close a {@linkplain Socket} object. It tries to shutdown the
	 * input and output of the socket before close. Exceptions are ignored.
	 * 
	 * @param socket
	 *            the Socket object to close
	 * @see #shutdown(Socket)
	 */
	public static void close(Socket socket) {
		if (socket == null)
			return;

		if (!socket.isClosed()) {
			shutdown(socket);
			try {
				socket.close();
			} catch (Throwable e) {
				log.debug(e.getMessage(), e);
			}
		}
	}

	/**
	 * Quietly shutdown the input of a {@linkplain Socket}. Exception are
	 * ignored.
	 * 
	 * @param socket
	 *            the socket to shutdown the input
	 */
	public static void shutdownInput(Socket socket) {
		if (socket == null)
			return;
		try {
			if (!socket.isInputShutdown())
				socket.shutdownInput();
		} catch (Throwable e) {
			log.debug(e.getMessage(), e);
		}
	}

	/**
	 * Quietly shutdown the output of a {@linkplain Socket}. Exceptions are
	 * ignored.
	 * 
	 * @param socket
	 *            the socket to shutdown the output
	 * @see Socket#shutdownOutput()
	 */
	public static void shutdownOutput(Socket socket) {
		if (socket == null)
			return;
		try {
			if (!socket.isOutputShutdown())
				socket.shutdownOutput();
		} catch (Throwable e) {
			log.debug(e.getMessage(), e);
		}
	}

	/**
	 * Quietly shutdown both input and output of a {@linkplan Socket}.
	 * Exceptions are ignored.
	 * 
	 * @param socket
	 *            the socket to shutdown
	 * @see #shutdownInput(Socket)
	 * @see #shutdownOutput(Socket)
	 */
	public static void shutdown(Socket socket) {
		shutdownInput(socket);
		shutdownOutput(socket);
	}

}
