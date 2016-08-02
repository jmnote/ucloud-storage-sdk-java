package com.olleh.ucloudbiz.ucloudstorage;

import java.util.ArrayList;

class TicketPool {
	static private TicketPool instance = null;  // The single instance
	private int used    = 0;
	private int counter = 0;
	private int addIdx  = 0;
	private int maxTicket = 0;
	private ArrayList<String> tickets = null;
	
	static synchronized public TicketPool getInstance() {
		if(instance == null) { instance = new TicketPool(5); }
		return instance;
	}
	
	static synchronized public TicketPool getInstance(int concurrent) {
		if(instance == null) { instance = new TicketPool(concurrent); }
		return instance;
	}
			
	public synchronized String getTicket() {
		String ticket = null;
		if(tickets.size() > 0) {
			ticket = tickets.get(0);			
			tickets.remove(0);
			++used;
			++counter;
		}
	    return ticket;
    }
    
    public synchronized void freeTicket (String ticket) {
		tickets.add(ticket);
		--used;
		notifyAll();
	}
	
	public synchronized void refreshPool() {
		if((tickets.size() == 0) && (used < maxTicket)) {
			int added = maxTicket - used;
			for(int i = 0; i < added; i++) {
				String ticket = addIdx + "ticketAdded";						
				tickets.add(ticket);
				++addIdx;
			}
			notifyAll();		
		}		
	}
	
	public void getPoolInfo() {
		System.out.println("used ticket : " + (new Integer(used)).toString());
		System.out.println("used ticket sum : " + (new Integer(counter)).toString());
		System.out.println("pool size : " + (new Integer(tickets.size()).toString()));
	}
		
	private TicketPool(int size) { maxTicket = size; createTickets(); }
			
	private void createTickets() {
		tickets = new ArrayList<String>();
		for(int i = 0; i < maxTicket; ++i) {
			String ticket = i + "ticket";						
			tickets.add(ticket);
		}
	}
}		    