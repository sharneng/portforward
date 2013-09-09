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

import com.sharneng.net.NetUtils;

/**
 * The command line entry for TCP/IP port forwarding.
 * 
 * @author Kenneth Xu
 * 
 */
public final class Forwarder {

    private Forwarder() {
    }

    /**
     * The command line entry.
     * 
     * @param args
     *            command line arguments
     * @throws Exception
     *             when error occurs
     */
    public static void main(String[] args) throws Exception {
        Listener listener = new Listener(NetUtils.parseInetSocketAddress(args[0]),
                NetUtils.parseInetSocketAddress(args[1]));
        listener.run();
        listener.close();
    }

}
