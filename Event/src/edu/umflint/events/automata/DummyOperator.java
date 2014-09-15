package edu.umflint.events.automata;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DummyOperator implements Runnable {
	
	ConcurrentLinkedQueue<String> inputQ;
	ConcurrentLinkedQueue<String> outputQ;
	
	public DummyOperator(ConcurrentLinkedQueue<String> i, ConcurrentLinkedQueue<String> o) {
		inputQ = i;
		outputQ = o;
	}
	
	public void run() {
		outputQ.add("EOF"); // add EOF to output Q and then just consume the input..
		while (true) {
			if (inputQ.isEmpty()) {
				/* try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} */
				continue;
			}
			String next = inputQ.remove();
			if (next.equals("EOF")) { // EOF indicates end of input
				// outputQ.add("EOF");
				// notify();
				return;
			}
		}
	}
}
