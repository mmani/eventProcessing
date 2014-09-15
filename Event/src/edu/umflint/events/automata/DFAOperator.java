package edu.umflint.events.automata;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DFAOperator implements Runnable {
	
	ConcurrentLinkedQueue<String> inputQ;
	ConcurrentLinkedQueue<String> outputQ;
	Automaton aut;
	State currState;
	
	public DFAOperator(ConcurrentLinkedQueue<String> i, ConcurrentLinkedQueue<String> o, Automaton a) {
		inputQ = i;
		outputQ = o;
		aut = a;
		currState = aut.startSt;
	}
	
	public String extendSym(String s) {
		String retVal = "";
		
		/* Iterator<String> it = aut.sortedAlph.iterator();
		System.out.print("sorted Alph : ");
		while (it.hasNext()) {
			System.out.print (it.next() + " ");
		}
		System.out.print("\n"); */

		
		Iterator<String> it = aut.sortedAlph.iterator();
		{
			String t = it.next();
			if (s.equals(t)) retVal = retVal + t;
			else retVal = retVal + "!" + t;
		}
		
		while (it.hasNext()) {
			String t = it.next();
			if (s.equals(t)) retVal = retVal + " & " + t;
			else retVal = retVal + " & !" + t;
		}
		// System.out.println ("extended " + s + " to " + retVal);
		return retVal;
	}
	
	// assume that the input first has the symbol and then space and then attributes
	public void run() {
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
				outputQ.add("EOF");
				// notify();
				return;
			}
			String event1 = next.substring(0, next.indexOf(' '));
			String event = extendSym(event1);
			
			Iterator<State> stIt = aut.transitions.get(currState).get(event).iterator();
			if (!stIt.hasNext()) { // no transitions defined.. should not happen
				System.err.println ("no transitions defined for state " + currState.num + " on seeing symbol " + event);
				outputQ.add("EOF");
				// notify();
				return;
			}
			// System.out.print("Transitioning from " + currState.num + " on " + event + " to ");
			currState = stIt.next();
			// System.out.println(currState.num);
			if (aut.finalSt.contains(currState)) {
				outputQ.add(next);
				// notify();
			}
		}
	}
}
