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
package com.sharneng.net.misc;

import com.sharneng.net.NetUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.annotation.CheckForNull;

class EchoServer implements Runnable {
    private static Log log = LogFactory.getLog(EchoServer.class);
    private final ServerSocket serverSocket;
    private final InetSocketAddress from;
    @CheckForNull
    private Throwable exception;

    // private Cleaner cleaner = new Cleaner();

    public EchoServer(InetSocketAddress from) throws IOException {
        this.from = from;
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(from);
        String hostname = from.getHostName();
        if (hostname == null) hostname = "*";
        log.info("Ready to accept client connection on " + hostname + ":" + from.getPort());
    }

    @Override
    public void run() {
        Socket source = null;
        // new Thread(cleaner).start();
        while (true) {
            try {
                source = serverSocket.accept();
                log.trace("accepted client connection");
                new EchoProcessor(source).process();
            } catch (IOException e) {
                String msg = "Failed to accept client connection on port " + from.getPort();
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
        EchoServer server = new EchoServer(NetUtils.parseInetSocketAddress(args[0]));
        server.run();
        if (server.exception != null) throw server.exception;
    }
}
