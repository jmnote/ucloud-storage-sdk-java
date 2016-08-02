package com.olleh.ucloudbiz.ucloudstorage;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rackspacecloud.client.cloudfiles.IFilesTransferCallback; 

class SegmentUploader extends Thread {
	private static boolean isFinished = false;
	private final String segmentID;
	private final String containerName;
	private final String contentType;
	private byte[] segmentData;
	private static final AtomicInteger alive = new AtomicInteger();
	private static AtomicBoolean isSucceeded = new AtomicBoolean(true);
	private FilesClientExt fclient;
	private TicketPool tp;
	private String ticket;
	private IFilesTransferCallback callback;
	
	protected SegmentUploader(String containerName, String segmentID, 
						      byte[] segmentData, String contentType, FilesClientExt fclient, 
						      String ticket, IFilesTransferCallback callback) {
		this.containerName = containerName;
		this.segmentID   = segmentID;
		this.segmentData = segmentData;
		this.contentType = contentType;
		this.fclient = fclient;
		this.ticket  = ticket;
		this.callback = callback;
		tp = TicketPool.getInstance();
	}
	
	public void run() {
		alive.incrementAndGet();
		try {
			fclient.storeObject(containerName, segmentData, contentType, segmentID, 
			                    new HashMap<String, String>(), callback);
		}
		catch(Exception e1) {
			try {
				fclient.storeObject(containerName, segmentData, contentType, segmentID, 
				                    new HashMap<String, String>(), callback);
				tp.freeTicket(ticket);
				alive.decrementAndGet();
				if(alive.get() == 0) { isFinished = true; }
			}
			catch(Exception e2) {
				try {
					fclient.storeObject(containerName, segmentData, contentType, segmentID, 
					                    new HashMap<String, String>(), callback);
				    tp.freeTicket(ticket);                     
					alive.decrementAndGet();                   
					if(alive.get() == 0) { isFinished = true; }
				}
				catch(Exception e3) {
					e3.printStackTrace();
					isSucceeded.set(false);	
					tp.freeTicket(ticket);
					alive.decrementAndGet();
					isFinished = true;
				}
			}
		}
		tp.freeTicket(ticket);
		alive.decrementAndGet();
		if(alive.get() == 0) { isFinished = true; }
	}
	
	protected String getID() { return this.segmentID; }
	protected byte[] getData() { return this.segmentData; }
	protected long getSize() { return this.segmentData.length; }
	protected static boolean isFinished() { return isFinished; }
	protected static boolean isSucceeded() { return isSucceeded.get(); }
}