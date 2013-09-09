/*
 * Copyright 2002-2013 the original author or authors.
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
package com.sharneng.net.portforward;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.annotation.CheckForNull;

/**
 * Listens on the given port to accept connection and forward it to target.
 * 
 * @author Kenneth Xu
 * 
 */
public class Listener implements Runnable, Closeable {
    private static Log log = LogFactory.getLog(Listener.class);
    private final ServerSocket serverSocket;
    private final InetSocketAddress from, to;
    @CheckForNull
    private Throwable exception;
    private final Cleaner cleaner = new Cleaner();

    /**
     * Gets the exception occurred if any.
     * 
     * @return the exception if any error occurred
     */
    @CheckForNull
    public Throwable getException() {
        return exception;
    }

    /**
     * Constructs a new instance of Listener.
     * 
     * @param from
     *            the address to listen for connections
     * @param to
     *            the address to forward connections to
     * @throws IOException
     *             when something is not going write when network operation
     */
    public Listener(InetSocketAddress from, InetSocketAddress to) throws IOException {
        this.from = from;
        this.to = to;
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(from);
        String hostname = from.getHostName();
        if (hostname == null) hostname = "*";
        log.info("Ready to accept client connection on " + hostname + ":" + from.getPort());
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
                String msg = "Failed to accept client connection on port " + from.getPort();
                log.error(msg, e);
                exception = e;
                return;
            }
        }
    }

    /**
     * Closes this listener and associated resources.
     */
    @Override
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
