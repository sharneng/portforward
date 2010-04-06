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

import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.enterprisepower.io.IOUtils;
import org.enterprisepower.net.NetUtils;

/**
 * 
 * @author Kenneth Xu
 * 
 */
public class Processor implements Cleanable {
	private static final Log log = LogFactory.getLog(Processor.class);

	private Socket source;
	private Socket target;
	private Cleaner cleaner;

	Copier req;
	Copier res;

	public Processor(Socket source, Socket target, Cleaner cleaner) {
		this.source = source;
		this.target = target;
		this.cleaner = cleaner;
		cleaner.add(this);
	}

	public void process() {

		req = new Copier(source, target);
		res = new Copier(target, source);

		new Thread(req).start();
		new Thread(res).start();
		if (log.isTraceEnabled()) {
			log.trace("started new request stream copier threads: " + req);
			log.trace("started new response stream copier threads: " + res);
		}
	}

	public boolean isCompleted() {
		return req.isCompleted && res.isCompleted;
	}

	public void close() {
		if (log.isTraceEnabled())
			log.trace("close() called on " + this);
		NetUtils.close(source);
		NetUtils.close(target);
	}

	public String toString() {
		return this.getClass().getName() + "(from " + source + " to " + target
				+ ")";
	}

	private class Copier implements Runnable {
		Socket in;
		Socket out;
		boolean isCompleted = false;

		Copier(Socket in, Socket out) {
			this.in = in;
			this.out = out;
		}

		public void run() {
			try {
				IOUtils.copyStream(in.getInputStream(), out.getOutputStream(),
						false);
			} catch (java.net.SocketException e) {
				NetUtils.shutdown(source);
				NetUtils.shutdown(target);
				log.debug(this, e);
			} catch (Exception e) {
				log.error(this, e);
			} finally {
				this.isCompleted = true;
				if (cleaner != null)
					synchronized (cleaner) {
						cleaner.notify();
					}
			}
		}

		public String toString() {
			return this.getClass().getName() + "(from " + in + " to " + out
					+ ")";
		}

	}
}
