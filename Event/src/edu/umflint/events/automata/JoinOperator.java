package edu.umflint.events.automata;

import java.util.concurrent.*;

public class JoinOperator implements Runnable {

	ConcurrentLinkedQueue<String> inQ1;
	ConcurrentLinkedQueue<String> inQ2;
	ConcurrentLinkedQueue<String> inQ3;
	ConcurrentLinkedQueue<String> outputQ;
	
	public JoinOperator(ConcurrentLinkedQueue<String> i1, ConcurrentLinkedQueue<String> i2, ConcurrentLinkedQueue<String> i3, ConcurrentLinkedQueue<String> o) {
		inQ1 = i1;
		inQ2 = i2;
		inQ3 = i3;
		outputQ = o;
	}
	
	public void run() {
		String inEl1 = null;
		String inEl2 = null;
		String inEl3 = null;
		
		String aEl = null;
		String bEl = null;
		
		int currSm;
		
		try {
			while (true) {
				if (inEl1 == null) {
					if (inQ1.isEmpty()) continue;
					else inEl1 = inQ1.remove();
				}
				if (inEl2 == null) {
					if (inQ2.isEmpty()) continue;
					else inEl2 = inQ2.remove();
				}
				if (inEl3 == null) {
					if (inQ3.isEmpty()) continue;
					else inEl3 = inQ3.remove();
				}
				currSm = determineSmallest(inEl1, inEl2, inEl3);
				switch (currSm) {
				case 1:
					bEl = null;
					aEl = inEl1;
					inEl1 = null;
					break;
				case 2:
					aEl = null;
					bEl = inEl2;
					inEl2 = null;
					break;
				case 3:
					if (aEl != null) outputQ.add(inEl3);
					inEl3 = null;
					break;
				case 0: // all Qs are completely processed
					outputQ.add("EOF");
				}				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int determineSmallest(String el1, String el2, String el3) {
		int currSm = 0;
		int currID = 0;
		if (!el1.equals("EOF")) {
			int id = Integer.parseInt(el1.substring(el1.lastIndexOf('=') + 3));
			currID = id;
			currSm = 1;
		}
		if (!el2.equals("EOF")) {
			int id = Integer.parseInt(el2.substring(el2.lastIndexOf('=') + 3));
			if (currID == 0) {
				currID = id;
				currSm = 2;
			}
			else {
				if (id < currID) {
					currID = id;
					currSm = 2;
				}
			}
		}
		if (!el3.equals("EOF")) {
			int id = Integer.parseInt(el3.substring(el3.lastIndexOf('=') + 3));
			if (currID == 0) {
				currID = id;
				currSm = 3;
			}
			else {
				if (id < currID) {
					currID = id;
					currSm = 3;
				}
			}
		}
		return currSm;
	}
	
	public static void main (String argv[]) {
		String el3 = "a id = 4 id1 = a4";
		System.out.println (el3.substring(el3.lastIndexOf('=') + 3));
	}
}
