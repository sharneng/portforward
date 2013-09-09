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

import com.sharneng.io.IOUtils;
import com.sharneng.net.NetUtils;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class EchoProcessor implements Runnable {
    private static final Log log = LogFactory.getLog(EchoProcessor.class);

    private final boolean isCompleted = false;

    private final Socket socket;

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
        if (log.isTraceEnabled()) log.trace("close() called on " + this);
        if (socket.isClosed()) {
            if (log.isTraceEnabled()) log.trace("socket already closed: " + socket);
        } else {
            if (log.isTraceEnabled()) log.trace("closing socket: " + socket);
            NetUtils.close(socket);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + socket + ")";
    }

    @Override
    public void run() {
        try {
            IOUtils.copyStream(socket.getInputStream(), socket.getOutputStream(), false);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            close();
        }
    }

}
