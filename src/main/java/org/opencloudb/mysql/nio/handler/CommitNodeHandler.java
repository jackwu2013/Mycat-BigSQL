/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.opencloudb.mysql.nio.handler;

import java.util.List;

import org.apache.log4j.Logger;
import org.opencloudb.backend.BackendConnection;
import org.opencloudb.net.FrontSession;
import org.opencloudb.net.mysql.ErrorPacket;

/**
 * @author mycat
 */
public class CommitNodeHandler implements ResponseHandler {
	private static final Logger LOGGER = Logger
			.getLogger(CommitNodeHandler.class);
	private final FrontSession session;

	public CommitNodeHandler(FrontSession session) {
		this.session = session;
	}

	public void commit(BackendConnection conn) {
		conn.setResponseHandler(CommitNodeHandler.this);
		conn.commit();
	}

	@Override
	public void connectionAcquired(BackendConnection conn) {
		LOGGER.error("unexpected invocation: connectionAcquired from commit");

	}

	@Override
	public void okResponse(byte[] ok, BackendConnection conn) {
		session.clearResources(false);
		session.writeBytes(ok);
	}

	@Override
	public void errorResponse(byte[] err, BackendConnection conn) {
		ErrorPacket errPkg = new ErrorPacket();
		errPkg.read(err);
		String errInfo = new String(errPkg.message);
		session.setTxInterrupt(errInfo);
		session.writeErrorPkg(errPkg);
	}

	@Override
	public void rowEofResponse(byte[] eof, BackendConnection conn) {
		LOGGER.error(new StringBuilder().append("unexpected packet for ")
				.append(conn).append(" bound by ").append(session.getConInfo())
				.append(": field's eof").toString());
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, BackendConnection conn) {
		LOGGER.error(new StringBuilder().append("unexpected packet for ")
				.append(conn).append(" bound by ").append(session.getConInfo())
				.append(": field's eof").toString());
	}

	@Override
	public void rowResponse(byte[] row, BackendConnection conn) {
		LOGGER.warn(new StringBuilder().append("unexpected packet for ")
				.append(conn).append(" bound by ").append(session.getConInfo())
				.append(": row data packet").toString());
	}

	@Override
	public void writeQueueAvailable() {

	}

	@Override
	public void connectionError(Throwable e, BackendConnection conn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionClose(BackendConnection conn, String reason) {
		// TODO Auto-generated method stub

	}

}