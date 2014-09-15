package edu.umflint.events.automata;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NFAOperator implements Runnable {
	
		ConcurrentLinkedQueue<String> inputQ;
		ConcurrentLinkedQueue<String> outputQ;
		Automaton aut;
		HashSet<State> currState;
		
		public NFAOperator(ConcurrentLinkedQueue<String> i, ConcurrentLinkedQueue<String> o, Automaton a) {
			currState = new HashSet<State>();
			inputQ = i;
			outputQ = o;
			aut = a;
			currState.add(aut.startSt);
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
				HashSet<State> nextState = new HashSet<State>();
				// for each state in currState, get the set of transitions.
				Iterator<State> currIt = currState.iterator();
				while (currIt.hasNext()) {
					Iterator<State> stIt = aut.transitions.get(currIt.next()).get(event).iterator();
					while (stIt.hasNext()) {
						State st = stIt.next();
						if (!nextState.contains(st)) nextState.add(st);
					}
				}
				if (nextState.size() == 0) { // no transitions defined.. should not happen
					System.err.println ("no transitions defined for state on seeing symbol " + event);
					outputQ.add("EOF");
					// notify();
					return;
				}
				
				currState = nextState;
				currIt = currState.iterator();
				while (currIt.hasNext()) {
					if (aut.finalSt.contains(currIt.next())) {
						outputQ.add(next);
						// notify();
						break;
					}
				}
			}
		}
	}
	