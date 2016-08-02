package com.olleh.ucloudbiz.ucloudstorage;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

class SegmentRemover extends Thread {
	private static boolean isFinished = false;
	private final String segmentID;
	private final String containerName;
	private static final AtomicInteger alive = new AtomicInteger();
	private static AtomicBoolean isSucceeded = new AtomicBoolean(true);
	private FilesClientExt fclient;
	private TicketPool tp;
	private String ticket;
	
	protected SegmentRemover(String containerName, 
						     String segmentID, 
						     FilesClientExt fclient,
						     String ticket) {
		this.containerName = containerName;
		this.segmentID = segmentID;
		this.fclient = fclient;
		this.ticket = ticket;
		tp = TicketPool.getInstance();
	}
	
	public void run() {
		alive.incrementAndGet();
		try {
			fclient.deleteObject(containerName, segmentID);
		}
		catch(Exception e1) {
			try {
				fclient.deleteObject(containerName, segmentID);
				tp.freeTicket(ticket);                     
				alive.decrementAndGet();                   
				if(alive.get() == 0) { isFinished = true; }
			}
			catch(Exception e2) {
				try {
					fclient.deleteObject(containerName, segmentID);
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
	protected static boolean isFinished() { return isFinished; }
	protected static boolean isSucceeded() { return isSucceeded.get(); }
	protected static void setFinished(boolean v) { isFinished = v; }
}